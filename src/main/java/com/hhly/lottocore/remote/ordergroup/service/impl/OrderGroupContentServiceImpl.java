package com.hhly.lottocore.remote.ordergroup.service.impl;

import com.hhly.lottocore.persistence.group.dao.OrderGroupContentMapper;
import com.hhly.lottocore.persistence.group.dao.OrderGroupMapper;
import com.hhly.lottocore.persistence.group.po.OrderGroupContentPO;
import com.hhly.lottocore.persistence.group.po.OrderGroupPO;
import com.hhly.lottocore.rabbitmq.provider.MessageProvider;
import com.hhly.lottocore.remote.ordergroup.service.OrderGroupContentService;
import com.hhly.lottocore.remote.sportorder.service.IOrderSearchService;
import com.hhly.skeleton.base.bo.PagingBO;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.OrderEnum;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.page.AbstractStatisticsPage;
import com.hhly.skeleton.base.page.IPageService;
import com.hhly.skeleton.base.util.NumberUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.base.util.OrderNoUtil;
import com.hhly.skeleton.cms.ordermgr.bo.OrderGroupBO;
import com.hhly.skeleton.lotto.base.group.bo.OrderDetailGroupInfoBO;
import com.hhly.skeleton.lotto.base.group.bo.OrderMyGroupBO;
import com.hhly.skeleton.lotto.base.group.vo.OrderGroupContentVO;
import com.hhly.skeleton.lotto.base.order.bo.OrderBaseInfoBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description
 * @Author longguoyou
 * @Date  2018/4/28 15:17
 * @Since 1.8
 */
@Service("orderGroupContentService")
public class OrderGroupContentServiceImpl implements OrderGroupContentService {


    @Autowired
    private OrderGroupContentMapper orderGroupContentMapper;

    @Autowired
    private IOrderSearchService orderSearchService;

    @Autowired
    private OrderGroupMapper orderGroupMapper;

    @Autowired
    private IPageService pageService;

    /*抄单满员发起退款（保底）*/
    @Value("${order_group_full_queuename}")
    private String orderGroupFullQueuename;
    @Autowired
    @Qualifier("orderGroupMessageProvider")
    private MessageProvider messageProvider;


    @Override
    public ResultBO<?> addOrderGroupContent(final OrderGroupContentPO orderGroupContentPO) throws Exception {
        return ResultBO.ok(orderGroupContentMapper.insert(orderGroupContentPO));
    }

    @Override
    public ResultBO<?> insertOrderGroupContent(OrderGroupContentVO orderGroupContentVO) throws Exception {
        //手动创建一个token用于查询订单信息的校验
        String token = Constants.TOKEN_NO_LOGIN;
        ResultBO resultBO =  orderSearchService.queryOrderInfo(orderGroupContentVO.getOrderCode(),token);
        if(resultBO.isError()){
            return resultBO;
        }
        OrderBaseInfoBO orderBaseInfoBO = (OrderBaseInfoBO)resultBO.getData();
        OrderDetailGroupInfoBO orderDetailGroupInfoBO = orderBaseInfoBO.getOrderDetailGroupInfoBO();
        if(ObjectUtil.isBlank(orderGroupContentVO.getFlag())){
            //1.验证是否支付成功
            if(orderBaseInfoBO.getPayStatus().shortValue() != OrderEnum.PayStatus.SUCCESS_PAY.getValue()){
                return ResultBO.err(MessageCodeConstants.CAN_NOT_FLOW);
            }
            //2.验证是否是招募中
            if(orderDetailGroupInfoBO.getGrpbuyStatus().shortValue() != OrderEnum.GRPBuyStatus.RECRUIT.getValue()){
                return ResultBO.err(MessageCodeConstants.CAN_NOT_FLOW);
            }
            //3.出票失败、已撤单
            if(OrderEnum.OrderStatus.FAILING_TICKET.getValue() == orderBaseInfoBO.getOrderStatus().shortValue()
                    || OrderEnum.OrderStatus.WITHDRAW.getValue() == orderBaseInfoBO.getOrderStatus().shortValue()
                    ||  OrderEnum.OrderStatus.WITHDRAWING.getValue() == orderBaseInfoBO.getOrderStatus().shortValue()){
                return ResultBO.err(MessageCodeConstants.CAN_NOT_FLOW);
            }
        }
        //3.插入跟单表更新和买表
        resultBO = insertOrderGroupContent(orderBaseInfoBO,orderDetailGroupInfoBO,orderGroupContentVO);
        return resultBO;
    }

    /**
     * 构建跟单对象
     * @param orderBaseInfoBO
     * @param orderDetailGroupInfoBO
     * @return
     */
    private ResultBO insertOrderGroupContent(OrderBaseInfoBO orderBaseInfoBO,OrderDetailGroupInfoBO orderDetailGroupInfoBO,OrderGroupContentVO orderGroupContentVO){
        //1.判断跟单金额+认购金额》=订单金额没。大于等于：进度满员,更新合买表，订单表
        Double totalBuyAmount = NumberUtil.sum(orderGroupContentVO.getBuyAmount(),orderDetailGroupInfoBO.getProgressAmount());
        boolean isFull = false;
        Double amount = 0d;
        Double ratio = 0d;

        if(NumberUtil.compareTo(totalBuyAmount,orderBaseInfoBO.getOrderAmount())>=0) {//满员
           //满员，直接用订单金额减去进度金额
            isFull = true;
            amount = NumberUtil.sub(orderBaseInfoBO.getOrderAmount(),orderDetailGroupInfoBO.getProgressAmount());
            ratio = NumberUtil.sub(100d,orderDetailGroupInfoBO.getProgress());
        }else{//没有满员
            amount = orderGroupContentVO.getBuyAmount();
            ratio = NumberUtil.div(amount,orderBaseInfoBO.getOrderAmount(),5);
            ratio = NumberUtil.mul(ratio,Constants.NUM_100);
            ratio = Double.valueOf(calculateProfit(ratio));
        }
        OrderGroupContentPO orderGroupContentPO = new OrderGroupContentPO();
        orderGroupContentPO.setOrderCode(orderBaseInfoBO.getOrderCode());
        orderGroupContentPO.setUserId(orderGroupContentVO.getUserId());
        orderGroupContentPO.setBuyRatio(ratio);
        orderGroupContentPO.setBuyAmount(amount);
        orderGroupContentPO.setBuyCode(OrderNoUtil.getOrderNo(OrderEnum.NumberCode.ORDER_GROUP_BUYCODE));
        if(!ObjectUtil.isBlank(orderGroupContentVO.getFlag())){
            orderGroupContentPO.setBuyFlag(1);//发起人认购
        }else{
            orderGroupContentPO.setBuyFlag(2);//跟单人认购
        }
        //1.插入跟单表
        orderGroupContentMapper.insert(orderGroupContentPO);

        OrderGroupPO orderGroupPO = new OrderGroupPO();
        orderGroupPO.setOrderCode(orderBaseInfoBO.getOrderCode());
        orderGroupPO.setBuyCount(orderDetailGroupInfoBO.getBuyCount()+1);
        if(isFull){//满员
            orderGroupPO.setProgress(100d);
            orderGroupPO.setProgressAmount(orderBaseInfoBO.getOrderAmount());
            orderGroupPO.setGrpbuyStatus(Integer.valueOf(OrderEnum.GRPBuyStatus.FULL_PERSON.getValue()));//满员
        }else{
            Double process = NumberUtil.div(totalBuyAmount,orderBaseInfoBO.getOrderAmount(),5);
            process = NumberUtil.mul(process,Constants.NUM_100);
            process = Double.valueOf(calculateProfit(process));
            orderGroupPO.setProgress(process);
            orderGroupPO.setProgressAmount(totalBuyAmount);
        }
        //2.更新合买表
        orderGroupMapper.updateByPrimaryKeySelective(orderGroupPO);
        //3.合买满员退款发送消息
        if(isFull) {//满员
            OrderGroupBO orderGroupBO = new OrderGroupBO();
            orderGroupBO.setOrderCode(orderBaseInfoBO.getOrderCode());
            messageProvider.sendMessage(orderGroupFullQueuename,orderGroupBO);
        }
        return ResultBO.ok(orderGroupContentPO);
    }

    /**
     * 保留double类型小数后两位，不四舍五入，直接取小数后两位 比如：10.1269 返回：10.12
     *
     * @param doubleValue
     * @return
     */
    public static String calculateProfit(double doubleValue) {
        // 保留4位小数
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.0000");
        String result = df.format(doubleValue);
        // 截取第一位
        String index = result.substring(0, 1);
        if (".".equals(index)) {
            result = "0" + result;
        }
        // 获取小数 . 号第一次出现的位置
        int inde = firstIndexOf(result, ".");
        // 字符串截断
        return result.substring(0, inde + 3);
    }

    /**
     * 查找字符串pattern在str中第一次出现的位置
     *
     * @param str
     * @param pattern
     * @return
     */
    public static int firstIndexOf(String str, String pattern) {
        for (int i = 0; i < (str.length() - pattern.length()); i++) {
            int j = 0;
            while (j < pattern.length()) {
                if (str.charAt(i + j) != pattern.charAt(j))
                    break;
                j++;
            }
            if (j == pattern.length())
                return i;
        }
        return -1;
    }

    @Override
    public ResultBO<?> queryOrderGroupContentList(OrderGroupContentVO orderGroupContentVO) throws Exception {
        PagingBO<OrderMyGroupBO> pageData = pageService.getPageData(orderGroupContentVO,
                new AbstractStatisticsPage<OrderMyGroupBO>() {
                    @Override
                    public int getTotal() {
                        int total = orderGroupContentMapper.queryOrderGroupContentCount(orderGroupContentVO);
                        return total;
                    }

                    @Override
                    public List<OrderMyGroupBO> getData() {
                        List<OrderMyGroupBO> result = orderGroupContentMapper.queryOrderGroupContentList(orderGroupContentVO);
                        return result;
                    }
                    @Override
                    public Object getOther() {
                        return null;
                    }
                });

        return ResultBO.ok(pageData);
    }
}
