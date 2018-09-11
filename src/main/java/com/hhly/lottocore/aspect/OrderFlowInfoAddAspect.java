package com.hhly.lottocore.aspect;

import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import com.hhly.skeleton.base.constants.PayConstants;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.hhly.lottocore.persistence.order.dao.OrderInfoDaoMapper;
import com.hhly.lottocore.rabbitmq.provider.MessageProvider;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.util.DateUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.lotto.base.order.bo.OrderBaseInfoBO;
import com.hhly.skeleton.lotto.base.order.bo.OrderInfoBO;

/**
 * 订单流程信息入库切面类
 * @author longguoyou
 * @date 2017年5月6日
 * @compay 益彩网络科技有限公司
 */
@Aspect
@Component
@Order(10)
public class OrderFlowInfoAddAspect {
	
	public static  final ThreadPoolExecutor THREAD_POOL = new ThreadPoolExecutor(10, 10, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	
	private Logger logger = LoggerFactory.getLogger(OrderFlowInfoAddAspect.class);
	/**
	 * 订单
	 */
	@Autowired
	private OrderInfoDaoMapper orderInfoDaoMapper;
	
	/**
	 * 下单流程消息队列
	 */
	@Resource(name="orderFlowMessageProvider")
	private MessageProvider orderFlowMessageProvider;
	
	@AfterReturning(pointcut = "execution(* com.hhly.lottocore.remote.sportorder.service.IOrderService.addOrder(..))",returning = "returnValue")
	public void executeAddOrderFlowInfo(Object returnValue){
		ResultBO<?> result = (ResultBO)returnValue;
		if(result != null && result.isOK()){
			OrderInfoBO orderInfoBO = (OrderInfoBO)result.getData();
			if(orderInfoBO.getBuyType().intValue() != PayConstants.BuyTypeEnum.JOINT_PURCHASE.getKey()){//非合买才发消息
				THREAD_POOL.execute(new OrderFlowMessageThread(orderInfoBO));
			}
		}else{
			if(result == null){
				logger.error("============returnValue is null 不进行订单流程信息入库");
			}else{
				logger.error("============错误描述【" + result.getMessage() + "】不进行订单流程信息入库");
			}
		}
	}
	
    class OrderFlowMessageThread implements Runnable{
    	OrderInfoBO orderInfoBO;
    	OrderFlowMessageThread(OrderInfoBO orderInfoBO){
    		this.orderInfoBO = orderInfoBO;
    	}
		@Override
		public void run() {
			OrderBaseInfoBO orderBaseInfoBO = orderInfoDaoMapper.queryOrderInfo(orderInfoBO.getOrderCode(), orderInfoBO.getUserId());
			JSONObject jsonObject = new JSONObject();
            jsonObject.put("orderCode", orderInfoBO.getOrderCode());
            jsonObject.put("createTime", DateUtil.convertDateToStr(ObjectUtil.isBlank(orderBaseInfoBO)?new Date():orderBaseInfoBO.getShowDate()));
            jsonObject.put("status", 1);
            jsonObject.put("buyType", 1);
            orderFlowMessageProvider.sendMessage(Constants.QUEUE_NAME_FOR_ORDER_FLOW, jsonObject);
            logger.debug("OrderFlowMesssage Send Message: " + jsonObject.toString());
		}
    }
	
}
