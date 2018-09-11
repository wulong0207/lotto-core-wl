package com.hhly.lottocore.aspect;

import com.hhly.lottocore.persistence.order.dao.OrderInfoDaoMapper;
import com.hhly.lottocore.rabbitmq.provider.MessageProvider;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.cms.ordermgr.vo.OrderGroupLotteryBO;
import com.hhly.skeleton.lotto.base.order.bo.OrderBaseInfoBO;
import com.hhly.skeleton.lotto.base.order.bo.OrderInfoBO;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
/**
 * @Description 
 * @Author longguoyou
 * @Date  2018/7/25 11:47
 * @Since 1.8
 */
@Aspect
@Component
@Order(10)
public class OrderGroupAspect {
	
	public static  final ThreadPoolExecutor THREAD_POOL = new ThreadPoolExecutor(10, 10, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	
	private Logger logger = LoggerFactory.getLogger(OrderGroupAspect.class);

	/**
	 * 订单
	 */
	@Autowired
	private OrderInfoDaoMapper orderInfoDaoMapper;

	/**
	 * 发起合买消息队列
	 */
	@Resource(name="orderCountMessageProvider")
	private MessageProvider orderCountMessageProvider;

	@AfterReturning(pointcut = "execution(* com.hhly.lottocore.remote.ordergroup.service.IOrderGroupService.addOrderGroup(..))",returning = "returnValue")
	public void executeOrderCount(Object returnValue){
		ResultBO<?> result = (ResultBO)returnValue;
		if(result != null && result.isOK()){
			OrderInfoBO orderInfoBO = (OrderInfoBO)result.getData();
			THREAD_POOL.execute(new OrderCountMessageThread(orderInfoBO));
		}else{
			if(result == null){
				logger.error("============returnValue is null");
			}else{
				logger.error("============错误描述【" + result.getMessage() + "】");
			}
		}
	}

	class OrderCountMessageThread implements Runnable{
		OrderInfoBO orderInfoBO;
		OrderCountMessageThread(OrderInfoBO orderInfoBO){
			this.orderInfoBO = orderInfoBO;
		}
		@Override
		public void run() {
			OrderBaseInfoBO orderBaseInfoBO = orderInfoDaoMapper.queryOrderInfo(orderInfoBO.getOrderCode(), orderInfoBO.getUserId());
			OrderGroupLotteryBO mqBO = new OrderGroupLotteryBO();
			mqBO.setLotteryCode(orderBaseInfoBO.getLotteryCode());
			mqBO.setUserId(orderBaseInfoBO.getUserId());
			mqBO.setOrderAmount(orderBaseInfoBO.getOrderAmount());
			mqBO.setOrderCode(orderBaseInfoBO.getOrderCode());
			mqBO.setType(Constants.NUM_1);
			orderCountMessageProvider.sendMessage(Constants.ORDER_GROUP_RESULT_STATIS_QUEUE, mqBO);
			logger.debug("发起合买 Send Message 成功: [orderCode = " + orderBaseInfoBO.getOrderCode() + ",lotteryCode = " + orderBaseInfoBO.getLotteryCode() +",userId = "+ orderBaseInfoBO.getUserId() + ",orderAmount = " + orderBaseInfoBO.getOrderAmount() + ",type = 1]");
		}
	}

}