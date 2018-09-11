package com.hhly.lottocore.controller;

import com.hhly.lottocore.remote.sportorder.service.IOrderSearchService;
import com.hhly.lottocore.remote.sportorder.service.IOrderService;
import com.hhly.skeleton.base.bo.PagingBO;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.OrderEnum;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.constants.PayConstants;
import com.hhly.skeleton.base.util.DateUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.lotto.base.order.bo.OrderBaseInfoBO;
import com.hhly.skeleton.lotto.base.order.bo.OrderFullDetailInfoBO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderQueryVo;
import com.hhly.skeleton.lotto.base.order.vo.OrderSingleQueryVo;
import com.hhly.skeleton.pay.bo.OperateCouponBO;
import com.hhly.skeleton.pay.bo.TransUserBO;
import com.hhly.skeleton.pay.vo.TransUserVO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Description 下订单（仅供内部项目间调用，不对外）
 * @Author longguoyou
 * @Date  2018/3/9 11:14
 * @Since 1.8
 */
@RestController
@RequestMapping("/order")
public class OrderController {
    private static final Logger logger = Logger.getLogger(OrderController.class);

    @Resource(name="orderService")
    private IOrderService orderService;

    @Autowired
    private IOrderSearchService orderSearchService;

    /**
     *  订单入库
     * @param orderInfoVO
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResultBO<?> add(@RequestBody OrderInfoVO orderInfoVO){
        ResultBO<?> resultBO = null;
        try {
            resultBO = orderService.addOrder(orderInfoVO);
        } catch (Exception e) {
            logger.error("内部系统调用下单接口失败！",e);
            return ResultBO.err();
        }
        return resultBO;
    }


    //提供给外部lotto-api接口调用 start
    /**
     * 积分兑换 查询订单列表(没有红包)
     * @param orderQueryVo
     * @return
     */
    @RequestMapping(value = "/orderlist" ,method = RequestMethod.POST)
    public ResultBO<?> queryOrderInfoList(@RequestBody OrderQueryVo orderQueryVo){
        logger.debug("积分兑换-查询订单列表");
        if(ObjectUtil.isBlank(orderQueryVo) || ObjectUtil.isBlank(orderQueryVo.getUserId()) || ObjectUtil.isBlank(orderQueryVo.getChannelId())
                ||ObjectUtil.isNull(orderQueryVo.getPageIndex()) || ObjectUtil.isBlank(orderQueryVo.getPageSize())
                || ObjectUtil.isBlank(orderQueryVo.getBeginDate()) || ObjectUtil.isBlank(orderQueryVo.getEndDate())){
            return ResultBO.err(MessageCodeConstants.PARAM_IS_NULL_FIELD);
        }
        try {
            Date startDate = DateUtil.convertStrToDate(orderQueryVo.getBeginDate(),DateUtil.DATE_FORMAT);
            Date finishDate = DateUtil.convertStrToDate(orderQueryVo.getEndDate(),DateUtil.DATE_FORMAT);
            if(DateUtil.judgmentDate(startDate,finishDate)){//最大查询一年的数据
                return ResultBO.err(MessageCodeConstants.MAX_ORDER_LIST_NUM);
            }
            setDateForOrderList(orderQueryVo, startDate, finishDate);
            return orderSearchService.queryOrderListInfo(orderQueryVo);
        }catch (Exception e){
            logger.error("积分兑换-查询投注列表失败！",e);
            return ResultBO.err();
        }
    }

    private void setDateForOrderList(OrderQueryVo orderQueryVo, Date startDate, Date finishDate) {
        //结束时间取当前时间最大的。比如2016-04-27 23:59:59
        Date addHourDate = DateUtil.addHour(finishDate, Constants.NUM_23);
        Date addMinuteDate = DateUtil.addMinute(addHourDate,Constants.NUM_59);
        Date addSencodeDate = DateUtil.addSecond(addMinuteDate,Constants.NUM_59);
        orderQueryVo.setStartDate(startDate);
        orderQueryVo.setFinishDate(addSencodeDate);
    }


    /**
     * 积分兑换- 订单详情 只有代购
     * @param orderSingleQueryVo
     * @return
     */
    @RequestMapping(value = "/queryOrderDetailInfo",method = RequestMethod.POST)
    public ResultBO<?> queryOrderDetailInfo(@RequestBody OrderSingleQueryVo orderSingleQueryVo){
        logger.debug("积分兑换-订单详情");
        if(ObjectUtil.isBlank(orderSingleQueryVo) || ObjectUtil.isBlank(orderSingleQueryVo.getOrderCode()) || ObjectUtil.isBlank(orderSingleQueryVo.getUserId())  ){
            return ResultBO.err(MessageCodeConstants.PARAM_IS_NULL_FIELD);
        }
        try {
            return orderSearchService.queryOrderDetailInfo(orderSingleQueryVo.getOrderCode(), null,orderSingleQueryVo.getSource(),orderSingleQueryVo.getUserId(),orderSingleQueryVo.getOrderGroupContentId());
        }catch (Exception e){
            logger.error("积分兑换-查询订单详情失败！",e);
            return ResultBO.err();
        }
    }

    //提供给外部lotto-api接口调用 end
}
