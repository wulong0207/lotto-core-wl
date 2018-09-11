package com.hhly.lottocore.remote.numorder.service.impl.cancel;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hhly.lottocore.rabbitmq.provider.MessageProvider;
import com.hhly.lottocore.remote.numorder.service.OrderAddService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.OrderFlowInfoEnum.StatusEnum;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.exception.ResultJsonException;
import com.hhly.skeleton.base.util.DateUtil;
import com.hhly.skeleton.lotto.base.order.bo.OrderAddBO;
import com.hhly.skeleton.lotto.base.order.bo.OrderFlowInfoBO;
import com.hhly.skeleton.lotto.base.order.vo.OrderAddVO;

/**
 * @desc 追号计划添加处理者(主要用于用户下单)
 * @author huangb
 * @date 2017年8月21日
 * @company 益彩网络
 * @version v1.0
 */
@Component("chaseAddHandler")
public class ChaseAddHandler {
	/**
	 * 日志对象
	 */
	private static Logger logger = Logger.getLogger(ChaseAddHandler.class);

	/**
	 * 追号服务（主要用到追号下单的服务）
	 */
	@Autowired
	private OrderAddService orderAddService;
	
	/**
	 * 订单/追号计划流程日志记录的消息队列
	 */
	@Resource(name="orderFlowMessageProvider")
	private MessageProvider orderFlowMessageProvider;

	/**
	 * @desc 用户追号下单
	 * @author huangb
	 * @date 2017年8月21日
	 * @param orderAdd
	 *            追号对象
	 * @return 用户追号下单
	 */
	public ResultBO<?> userChase(OrderAddVO orderAdd) {
		ResultBO<?> rs = null;
		try {
			rs = orderAddService.addChase(orderAdd);
			// 确保追号计划下单成功了在记录流程日志
			if (null != rs && rs.isOK() && rs.getData() != null) {
				OrderAddBO target = (OrderAddBO) rs.getData();
				// 发送队列消息:追号流程日志记录
				sendMsgOfChaseFlow(target);
			}
		} catch (ResultJsonException e) {
			logger.error("用户追号计划下单=>添加下单信息异常（ResultJsonException）", e);
			return e.getResult();
		} catch (Exception ex) {
			logger.error("用户追号计划下单=>添加下单信息异常（Exception）", ex);
			return ResultBO.err();
		}
		return rs;
	}
	
	/**
	 * 用户追号下单，不加验证
	 * @desc 
	 * @create 2018年1月11日
	 * @param orderAdd
	 * @return ResultBO<?>
	 */
	public ResultBO<?> userChaseWithOutVerify(OrderAddVO orderAdd) {
		ResultBO<?> rs = null;
		try {
			rs = orderAddService.addChaseWithOutVerify(orderAdd);
			// 确保追号计划下单成功了在记录流程日志
			if (null != rs && rs.isOK() && rs.getData() != null) {
				OrderAddBO target = (OrderAddBO) rs.getData();
				// 发送队列消息:追号流程日志记录
				sendMsgOfChaseFlow(target);
			}
		} catch (ResultJsonException e) {
			logger.error("用户追号计划下单=>添加下单信息异常（ResultJsonException）", e);
			return e.getResult();
		} catch (Exception ex) {
			logger.error("用户追号计划下单=>添加下单信息异常（Exception）", ex);
			return ResultBO.err();
		}
		return rs;
	}
	
	
	/**
	 * @desc 发送队列消息:追号流程日志记录
	 * @author huangb
	 * @date 2017年8月15日
	 * @param target
	 *            追号计划下单目标对象
	 */
	private void sendMsgOfChaseFlow(OrderAddBO target) {
		// 用户追号计划下单的流程日志记录
		OrderFlowInfoBO msgModel = new OrderFlowInfoBO(target.getOrderAddCode(), null, StatusEnum.SUBMIT_FLOW.getKey(), DateUtil.getNowDate());
		orderFlowMessageProvider.sendMessage(Constants.QUEUE_NAME_FOR_ORDER_FLOW, msgModel);
	}
}
