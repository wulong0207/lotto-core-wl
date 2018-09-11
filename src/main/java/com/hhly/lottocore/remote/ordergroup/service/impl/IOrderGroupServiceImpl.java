package com.hhly.lottocore.remote.ordergroup.service.impl;

import com.hhly.lottocore.cache.service.UserInfoCacheService;
import com.hhly.lottocore.persistence.group.dao.OrderGroupLotteryMapper;
import com.hhly.lottocore.persistence.group.dao.OrderGroupMapper;
import com.hhly.lottocore.persistence.group.dao.OrderGroupUserMapper;
import com.hhly.lottocore.persistence.group.po.OrderGroupPO;
import com.hhly.lottocore.persistence.lottery.dao.LotteryTypeDaoMapper;
import com.hhly.lottocore.persistence.order.dao.OrderInfoDaoMapper;
import com.hhly.lottocore.remote.ordergroup.service.IOrderGroupService;
import com.hhly.lottocore.remote.ordergroup.service.OrderGroupContentService;
import com.hhly.lottocore.remote.ordergroup.service.OrderGroupService;
import com.hhly.lottocore.remote.ordergroup.service.OrderGroupUserService;
import com.hhly.lottocore.remote.sportorder.service.IOrderSearchService;
import com.hhly.lottocore.remote.sportorder.service.IOrderService;
import com.hhly.skeleton.base.bo.PagingBO;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.OrderEnum;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.page.AbstractStatisticsPage;
import com.hhly.skeleton.base.page.IPageService;
import com.hhly.skeleton.base.util.*;
import com.hhly.skeleton.lotto.base.group.bo.*;
import com.hhly.skeleton.lotto.base.group.vo.OrderGroupContentVO;
import com.hhly.skeleton.lotto.base.group.vo.OrderGroupInfoVO;
import com.hhly.skeleton.lotto.base.group.vo.OrderGroupQueryVO;
import com.hhly.skeleton.lotto.base.lottery.bo.LotteryBO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;
import com.hhly.skeleton.lotto.base.order.bo.OrderBaseInfoBO;
import com.hhly.skeleton.lotto.base.order.bo.OrderInfoBO;
import com.hhly.skeleton.lotto.base.order.vo.OrderSingleQueryVo;
import com.hhly.skeleton.user.bo.UserInfoBO;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

@Service("iOrderGroupService")
public class IOrderGroupServiceImpl implements IOrderGroupService {

    /**
     * 订单下单接口
     */
    @Resource(name="orderService")
    private IOrderService orderService;

    /**
     * 合买订单 服务
     */
    @Autowired
    private OrderGroupService orderGroupService;

    /**
     * 合买订单申购 服务
     */
    @Autowired
    private OrderGroupContentService orderGroupContentService;

    /**
     * 合买用户相关信息服务查询
     */
    @Autowired
    private OrderGroupUserService orderGroupUserService;

    //格式化
    private static final DecimalFormat df = new DecimalFormat("##.00");

    private static final String ORDER_GROUP_TITIL = "合买标题";
    private static final String ORDER_GROUP_DESCRIPTION = "合买宣言";

    @Resource(name="userInfoCacheService")
    private UserInfoCacheService userInfoCacheService;

    @Autowired
    private OrderGroupMapper orderGroupMapper;

    @Autowired
    private OrderGroupUserMapper orderGroupUserMapper;

    @Autowired
    private OrderGroupLotteryMapper orderGroupLotteryMapper;

    @Autowired
    private LotteryTypeDaoMapper lotteryTypeDaoMapper;

    @Value("${before_file_url}")
    protected String beforeFileUrl;

    @Autowired
    private IPageService pageService;

    @Autowired
    private OrderInfoDaoMapper orderInfoDaoMapper;

    @Autowired
    private IOrderSearchService orderSearchService;

    /**
     * 网站保底比例
     */
    @Value("${site_guarantee_ratio}")
    private String siteGuaranteeRatio;

    /**
     * 发起合买
     * @param orderGroupInfoVO
     * @return
     * @throws Exception
     */
    @Override
    public ResultBO<?> addOrderGroup(final OrderGroupInfoVO orderGroupInfoVO) throws Exception {
        ResultBO<?> resultBO = null;
        if(ObjectUtil.isBlank(orderGroupInfoVO.getApplyWay())){
            orderGroupInfoVO.setApplyWay(1);//所有人可认购
        }
        OrderGroupPO orderGroupPO = new OrderGroupPO(orderGroupInfoVO);
        //比较传参是否正确
        if(!ObjectUtil.isBlank(orderGroupInfoVO.getMinBuyRatio()) && MathUtil.compareTo(MathUtil.truncate(MathUtil.mulRoundDown(MathUtil.div(orderGroupInfoVO.getMinBuyAmount(),orderGroupInfoVO.getOrderAmount()),100)),orderGroupPO.getMinBuyRatio())!= 0){
            return ResultBO.err(MessageCodeConstants.PARAM_IS_FIELD);
        }
        if(!ObjectUtil.isBlank(orderGroupInfoVO.getGuaranteeRatio()) && MathUtil.compareTo(MathUtil.truncate(MathUtil.mulRoundDown(MathUtil.div(orderGroupInfoVO.getGuaranteeAmount(),orderGroupInfoVO.getOrderAmount()),100)),orderGroupPO.getGuaranteeRatio())!= 0){
            return ResultBO.err(MessageCodeConstants.PARAM_IS_FIELD);
        }
        orderGroupPO.setMinBuyRatio(MathUtil.truncate(MathUtil.mulRoundDown(MathUtil.div(orderGroupInfoVO.getMinBuyAmount(),orderGroupInfoVO.getOrderAmount()),100)));
        orderGroupPO.setGuaranteeRatio(MathUtil.truncate(MathUtil.mulRoundDown(MathUtil.div(orderGroupInfoVO.getGuaranteeAmount(),orderGroupInfoVO.getOrderAmount()),100)));
        if(ObjectUtil.isBlank(orderGroupInfoVO.getMinBuyRatio())){
            orderGroupInfoVO.setMinBuyRatio(orderGroupPO.getMinBuyRatio());
        }
        if(ObjectUtil.isBlank(orderGroupInfoVO.getGuaranteeRatio())){
            orderGroupInfoVO.setGuaranteeRatio(orderGroupPO.getGuaranteeRatio());
        }
        resultBO = validate(orderGroupInfoVO);
        if(resultBO.isError()){
            return resultBO;
        }
        //1、下订单
        orderGroupInfoVO.setBuyType((short)3);
        resultBO = orderService.addOrder(orderGroupInfoVO);
        if(resultBO.isError()){
            return resultBO;
        }
        OrderInfoBO orderInfoBO = (OrderInfoBO)resultBO.getData();
        String orderCode = orderInfoBO.getOrderCode();
        Integer userId = orderGroupInfoVO.getUserId();
        //2、新增一条记录到order_group表
//        orderGroupPO.setCommissionAmount(MathUtil.round(MathUtil.div(MathUtil.mul(orderGroupInfoVO.getOrderAmount(),orderGroupPO.getCommissionRatio()),100),2));
//        orderGroupPO.setProgress(MathUtil.truncate(MathUtil.mul(MathUtil.div(orderGroupInfoVO.getMinBuyAmount(),orderGroupInfoVO.getOrderAmount()),100)));
//        orderGroupPO.setProgressAmount(orderGroupInfoVO.getMinBuyAmount());
        orderGroupPO.setDescription(orderGroupInfoVO.getDescription());
        orderGroupPO.setMinBuyRatio(orderGroupInfoVO.getMinBuyRatio());
        orderGroupPO.setSiteGuaranteeRatio(Double.valueOf(siteGuaranteeRatio));
        orderGroupPO.setSiteGuaranteeAmount(MathUtil.truncate(MathUtil.div(MathUtil.mulRoundDown(orderGroupInfoVO.getOrderAmount(),Double.valueOf(siteGuaranteeRatio)),100)));
        orderGroupPO.setUserId(userId);
        orderGroupPO.setOrderCode(orderCode);
        orderGroupPO.setTitle(new String(Base64.encodeBase64(orderGroupPO.getTitle().getBytes(SysUtil.getSystemEncoding()))));//防止标题带表情
        if(!ObjectUtil.isBlank(orderGroupPO.getDescription())){
            orderGroupPO.setDescription(new String(Base64.encodeBase64(orderGroupPO.getDescription().getBytes(SysUtil.getSystemEncoding()))));//防止描述带表情
        }
        resultBO = orderGroupService.addOrderGroup(orderGroupPO);
        if(resultBO.isError()){
            return resultBO;
        }
        return ResultBO.ok(orderInfoBO);
    }

    /**
     * 发起合买成功，并且支付成功后调用
     * @param orderCode
     * @return
     * @throws Exception
     */
    @Override
    public ResultBO<?> addOrderGroupContent(final String orderCode) throws Exception {
        OrderInfoBO orderInfoBO = orderService.getOrderInfo(orderCode);
        OrderGroupContentVO orderGroupContentVO = new OrderGroupContentVO();
        if(ObjectUtil.isBlank(orderInfoBO)){
            return ResultBO.err(MessageCodeConstants.ORDER_CODE_IS_NULL_FIELD);
        }
        orderGroupContentVO.setOrderCode(orderCode);
        orderGroupContentVO.setUserId(orderInfoBO.getUserId());
        orderGroupContentVO.setBuyAmount(orderInfoBO.getOrderAmount());
        return orderGroupContentService.insertOrderGroupContent(orderGroupContentVO);
    }

    /**
     * 验证 合买相关参数
     * @param orderGroupInfoVO
     * @return
     */
    private ResultBO<?> validate(OrderGroupInfoVO orderGroupInfoVO) {

        //验证保底比例 加 认购比例 是否小于等于 100 %
        if(MathUtil.compareTo(MathUtil.add(orderGroupInfoVO.getMinBuyRatio(),orderGroupInfoVO.getGuaranteeRatio()),100) == 1){
            return ResultBO.err(MessageCodeConstants.PARAM_IS_FIELD);
        }

        //验证合买认购比例（默认5%），认购进度0%-100%
        if(orderGroupInfoVO.getMinBuyRatio()==null || !(MathUtil.compareTo(orderGroupInfoVO.getMinBuyRatio(),0) > -1 &&
                MathUtil.compareTo(orderGroupInfoVO.getMinBuyRatio(), Constants.NUM_1) < 100)){
            return ResultBO.err(MessageCodeConstants.PARAM_IS_FIELD);
        }
        //验证保底进度默认无保底，设置保底进度0%-100%
        if(orderGroupInfoVO.getGuaranteeRatio()== null || !( MathUtil.compareTo(orderGroupInfoVO.getGuaranteeRatio(),0) > -1 &&
                MathUtil.compareTo(orderGroupInfoVO.getGuaranteeRatio(),100) < 1)){
            return ResultBO.err(MessageCodeConstants.PARAM_IS_FIELD);
        }
        //标题不允许为空
//        if(ObjectUtil.isBlank(orderGroupInfoVO.getTitle())){
//            return ResultBO.err(MessageCodeConstants.PARAM_IS_FIELD);
//        }
        //验证合买标题字符长度20
        if(!ObjectUtil.isBlank(orderGroupInfoVO.getTitle()) && orderGroupInfoVO.getTitle().length() > 20){
            return ResultBO.err(MessageCodeConstants.STRING_LENGTH_LIMIT,ORDER_GROUP_TITIL,20);
        }
        //验证合买宣言字符长度60
        if(!ObjectUtil.isBlank(orderGroupInfoVO.getDescription()) && orderGroupInfoVO.getDescription().length() > 60){
            return ResultBO.err(MessageCodeConstants.STRING_LENGTH_LIMIT,ORDER_GROUP_DESCRIPTION,60);
        }
        //验证佣金设置比例为1%-10%
        if(orderGroupInfoVO.getCommissionRatio() == null || !(MathUtil.compareTo(orderGroupInfoVO.getCommissionRatio(),0) > -1 &&
                MathUtil.compareTo(orderGroupInfoVO.getCommissionRatio(),10) < 1)){
            return ResultBO.err(MessageCodeConstants.PARAM_IS_FIELD);
        }
        //验证保密类型设置：1：公开、2：跟单公开:3：开奖后公开
        if(MathUtil.compareTo(orderGroupInfoVO.getVisibleType(), Constants.NUM_1) != Constants.NUM_0 &&
                MathUtil.compareTo(orderGroupInfoVO.getVisibleType(),Constants.NUM_2) != Constants.NUM_0 &&
                MathUtil.compareTo(orderGroupInfoVO.getVisibleType(),Constants.NUM_3) != Constants.NUM_0){
            return ResultBO.err(MessageCodeConstants.PARAM_IS_FIELD);
        }
        //验证购买对象值范围[1,2]
        if(ObjectUtil.isBlank(orderGroupInfoVO.getApplyWay()) || (MathUtil.compareTo(orderGroupInfoVO.getApplyWay(), Constants.NUM_2) != Constants.NUM_0 &&
                MathUtil.compareTo(orderGroupInfoVO.getApplyWay(),Constants.NUM_1) != Constants.NUM_0)){
            return ResultBO.err(MessageCodeConstants.PARAM_IS_FIELD);
        }
        //验证认购密码，只包 A、含数字、字母 B、字符长度为6
        if(MathUtil.compareTo(orderGroupInfoVO.getApplyWay(),Constants.NUM_1) != Constants.NUM_0 && (ObjectUtil.isBlank(orderGroupInfoVO.getApplyCode()) ||
                 orderGroupInfoVO.getApplyCode().length() > Constants.NUM_6 || !orderGroupInfoVO.getApplyCode().matches(RegularValidateUtil.REGULAR_APPLY_CODE))){
            return ResultBO.err(MessageCodeConstants.APPLY_PASSWORD_ILLEGAL_SERVICE);
        }
        return ResultBO.ok();
    }

    public  ResultBO<?> queryOrderGroupList(OrderGroupQueryVO orderGroupQueryVO)throws Exception{
        ResultBO<?> result = orderGroupService.queryOrderGroup(orderGroupQueryVO);
        return result;
    }

    /**
     * 合买大厅列表，合买二期
     * @param orderGroupQueryVO
     * @return
     * @throws Exception
     */
    @Override
    public ResultBO<?> queryOrderGroupListV11(OrderGroupQueryVO orderGroupQueryVO) throws Exception {
        ResultBO<?> result = null;
        if(orderGroupQueryVO.getType() == null || (orderGroupQueryVO.getType() != Constants.NUM_0 && orderGroupQueryVO.getType() != Constants.NUM_1)){
            return ResultBO.err(MessageCodeConstants.PARAM_IS_FIELD);
        }
        if(orderGroupQueryVO.getType() == Constants.NUM_0){
            orderGroupQueryVO.setUserId(null);
        }
        if(!ObjectUtil.isBlank(orderGroupQueryVO.getToken())){
            result = userInfoCacheService.checkToken(orderGroupQueryVO.getToken());
            if(result.isError())
                return result;
            UserInfoBO userInfoBO = (UserInfoBO) result.getData();
            orderGroupQueryVO.setCheckUserId(userInfoBO.getId());//用于查询是否已经参与合买
        }
        result = orderGroupService.queryOrderGroupV11(orderGroupQueryVO);
        return result;
    }


    @Override
    public ResultBO<?> queryOrderGroupContentList(OrderGroupContentVO orderGroupContentVO) throws Exception {
        return orderGroupContentService.queryOrderGroupContentList(orderGroupContentVO);
    }

    @Override
    public ResultBO<?> validOrderGroupContent(OrderSingleQueryVo orderSingleQueryVo) throws Exception {
        ResultBO resultBO =  orderSearchService.queryOrderInfo(orderSingleQueryVo.getOrderCode(),orderSingleQueryVo.getToken());
        if(resultBO.isError()){
            return resultBO;
        }
        OrderBaseInfoBO orderBaseInfoBO = (OrderBaseInfoBO)resultBO.getData();
        OrderDetailGroupInfoBO orderDetailGroupInfoBO = orderBaseInfoBO.getOrderDetailGroupInfoBO();
        //1.验证是否支付成功
        if(orderBaseInfoBO.getPayStatus().shortValue() != OrderEnum.PayStatus.SUCCESS_PAY.getValue()){
            return ResultBO.err(MessageCodeConstants.CAN_NOT_FLOW);
        }
        //2.验证是否是招募中
        if(orderDetailGroupInfoBO.getGrpbuyStatus().shortValue() != OrderEnum.GRPBuyStatus.RECRUIT.getValue()){
            return ResultBO.err(MessageCodeConstants.CAN_NOT_FLOW);
        }
        //3.验证是否过订单截止时间
        if(DateUtil.compare(orderBaseInfoBO.getEndSaleTime(),new Date())<0){
            return ResultBO.err(MessageCodeConstants.CAN_NOT_FLOW);
        }
        //4.出票失败、已撤单
        if(OrderEnum.OrderStatus.FAILING_TICKET.getValue() == orderBaseInfoBO.getOrderStatus().shortValue()
                || OrderEnum.OrderStatus.WITHDRAW.getValue() == orderBaseInfoBO.getOrderStatus().shortValue()
                ||  OrderEnum.OrderStatus.WITHDRAWING.getValue() == orderBaseInfoBO.getOrderStatus().shortValue()){
            return ResultBO.err(MessageCodeConstants.CAN_NOT_FLOW);
        }
        //5.验证密码是否正确
        OrderGroupBO orderGroupBO = orderGroupMapper.queryOrderGroupByOrderCode(orderSingleQueryVo.getOrderCode());
        if(orderGroupBO.getApplyWay().intValue() == Constants.NUM_2){
            if(ObjectUtil.isBlank(orderSingleQueryVo.getApplyCode())){
               return ResultBO.err(MessageCodeConstants.ORDER_GROUP_WRONG_APPLY_CODE);
            }
            if(!orderGroupBO.getApplyCode().equals(orderSingleQueryVo.getApplyCode())){
                return ResultBO.err(MessageCodeConstants.ORDER_GROUP_WRONG_APPLY_CODE);
            }
        }
        return ResultBO.ok();
    }

    public ResultBO<?> queryOrderGroupPersonInfo(OrderSingleQueryVo orderSingleQueryVo)throws Exception{
        Integer userId = orderSingleQueryVo.getUserId();
        if(orderSingleQueryVo.getSeeType().intValue()== Constants.NUM_2){//2我的个人主页，传token
            ResultBO<?> result = userInfoCacheService.checkToken(orderSingleQueryVo.getToken());
            if(result.isError()) {
                return result;
            }
            UserInfoBO userInfo = (UserInfoBO) result.getData();
            userId = userInfo.getId();
        }
        OrderGroupPersonInfoBO orderGroupPersonInfoBO = new OrderGroupPersonInfoBO();
        //1.查询用户统计信息
        OrderGroupUserBO orderGroupUserBO = orderGroupUserMapper.queryOrderGroupUserInfo(userId);
        if(orderGroupUserBO==null || ObjectUtil.isBlank(orderGroupUserBO.getId())){
            return ResultBO.err( MessageCodeConstants.ORDER_GROUP_USER_NOT_EXIST);
        }
        //设置用户统计信息
        setOrderGroupUserInfo(orderGroupPersonInfoBO, orderGroupUserBO);

        if(orderSingleQueryVo.getSource().intValue()==0){//PC端，查询最近三条中奖纪录和排行榜
           //2.最近三条中奖纪录
            List<OrderGroupPersonLatelyInfoBO>  orderGroupPersonLatelyInfoBOs = orderGroupMapper.queryLatelyThreeRecord(userId);
            if(!ObjectUtil.isBlank(orderGroupPersonLatelyInfoBOs)){
                for(OrderGroupPersonLatelyInfoBO orderGroupPersonLatelyInfoBO :orderGroupPersonLatelyInfoBOs){
                    orderGroupPersonLatelyInfoBO.setLotteryName(getLotteryName(orderGroupPersonLatelyInfoBO.getLotteryCode()));
                }
                orderGroupPersonInfoBO.setLatelyThreeOrderList(orderGroupPersonLatelyInfoBOs);
            }
            //3.排行榜
            List<OrderGroupPersonLatelyInfoBO> orderGroupRankingList = orderGroupUserMapper.queryGroupRankingList();
            orderGroupPersonInfoBO.setOrderGroupRankingList(orderGroupRankingList);
        }
        //4.分彩种历史战绩
        ResultBO resultBO = getOrderLoteryList(userId);
        if(resultBO.isError()){
            return resultBO;
        }
        orderGroupPersonInfoBO.setOrderGroupLotteryBOs((List<OrderGroupLotteryBO>)resultBO.getData());
        orderGroupPersonInfoBO.setUserId(orderSingleQueryVo.getUserId());
        return ResultBO.ok(orderGroupPersonInfoBO);
    }

    public ResultBO<?> queryOrderStandingList(OrderSingleQueryVo orderSingleQueryVo) throws Exception {
        Integer userId = orderSingleQueryVo.getUserId();
        if(orderSingleQueryVo.getSeeType().intValue()== Constants.NUM_2){//2我的个人主页，传token
            ResultBO<?> result = userInfoCacheService.checkToken(orderSingleQueryVo.getToken());
            if(result.isError()) {
                return result;
            }
            UserInfoBO userInfo = (UserInfoBO) result.getData();
            userId = userInfo.getId();
        }
        orderSingleQueryVo.setUserId(userId);
        PagingBO<OrderBaseInfoBO> pageData = pageService.getPageData(orderSingleQueryVo,
                new AbstractStatisticsPage<OrderBaseInfoBO>() {
                    @Override
                    public int getTotal() {
                       return  orderInfoDaoMapper.queryOrderStandingListCount(orderSingleQueryVo);
                    }

                    @Override
                    public List<OrderBaseInfoBO> getData() {
                        List<OrderBaseInfoBO> result;
                        result = orderInfoDaoMapper.queryOrderStandingList(orderSingleQueryVo);
                        if (!ObjectUtil.isBlank(result)) {
                            for (OrderBaseInfoBO orderListInfoBO : result) {
                                orderListInfoBO.setLotteryName(getLotteryName(orderListInfoBO.getLotteryCode()));
                                orderSearchService.buildAllOrderInfo(orderListInfoBO);
                            }
                        }
                        return result;
                    }

                    @Override
                    public Object getOther() {
                        return null;
                    }
         });
        return ResultBO.ok(pageData);
    }

    @Override
    public ResultBO<?> queryGroupRankingList() throws Exception {
        return orderGroupUserService.queryGroupRankingList();
    }

    @Override
    public ResultBO<?> queryGroupFamousList() throws Exception {
        return orderGroupUserService.queryGroupFamousList();
    }


    private ResultBO<?> getOrderLoteryList(Integer userId) {
        List<OrderGroupLotteryBO> orderGroupLotteryBOs= orderGroupLotteryMapper.queryOrderGroupLotteryInfo(userId);
        if(ObjectUtil.isBlank(orderGroupLotteryBOs)){
            return ResultBO.err( MessageCodeConstants.ORDER_GROUP_USER_NOT_EXIST);
        }
        for(OrderGroupLotteryBO orderGroupLotteryBO : orderGroupLotteryBOs){
            orderGroupLotteryBO.setLotteryName(getLotteryName(orderGroupLotteryBO.getLotteryCode()));
            orderGroupLotteryBO.setLotteryType(Constants.getLotteryType1(orderGroupLotteryBO.getLotteryCode()));
        }
        return ResultBO.ok(orderGroupLotteryBOs);
    }

    /**
     * 设置用户统计信息
     * @param orderGroupPersonInfoBO
     * @param orderGroupUserBO
     */
    private void setOrderGroupUserInfo(OrderGroupPersonInfoBO orderGroupPersonInfoBO, OrderGroupUserBO orderGroupUserBO) {
        orderGroupPersonInfoBO.setUserName(orderGroupUserBO.getUserName());
        orderGroupPersonInfoBO.setHeadPic(getUrl(beforeFileUrl,orderGroupUserBO.getHeadPic()));
        //获奖记录
        orderGroupPersonInfoBO.setWinBwCount(orderGroupUserBO.getWinBwCount());
        orderGroupPersonInfoBO.setWinSwCount(orderGroupUserBO.getWinSwCount());
        orderGroupPersonInfoBO.setWinWCount(orderGroupUserBO.getWinWCount());
        orderGroupPersonInfoBO.setWinQCount(orderGroupUserBO.getWinQCount());
        orderGroupPersonInfoBO.setWinOtherCount(orderGroupUserBO.getWinOtherCount());

        orderGroupPersonInfoBO.setWinCount(orderGroupUserBO.getWinCount());
        orderGroupPersonInfoBO.setWinAmount(orderGroupUserBO.getWinAmount());
        orderGroupPersonInfoBO.setOrderCount(orderGroupUserBO.getOrderCount());
        orderGroupPersonInfoBO.setOrderSucRate(orderGroupUserBO.getOrderSucRate());
        orderGroupPersonInfoBO.setCustomizationCount(orderGroupUserBO.getCustomizationCount());
    }

    /**
     * 获取彩种信息
     *
     * @param lotteryCode
     * @return
     */
    private String getLotteryName(Integer lotteryCode) {
        LotteryBO lotteryBO = lotteryTypeDaoMapper.findSingleFront(new LotteryVO(Integer.valueOf(lotteryCode)));
        return lotteryBO.getLotteryName();

    }

    /**
     * 获取球队赛事url
     *
     * @param beforeFileUrl
     * @param url
     * @return
     */
    private String getUrl(String beforeFileUrl, String url) {
        if (!StringUtil.isBlank(url)) {
            if (url.contains("http")) {
                return url;
            } else {
                return beforeFileUrl + url;
            }
        }

        return null;
    }





}
