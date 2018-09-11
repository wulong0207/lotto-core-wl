package com.hhly.lottocore.remote.numorder.service.impl.cancel;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hhly.lottocore.rabbitmq.provider.MessageProvider;
import com.hhly.lottocore.remote.numorder.service.OrderAddService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.OrderFlowInfoEnum.StatusEnum;
import com.hhly.skeleton.base.constants.CancellationConstants.OrderTypeEnum;
import com.hhly.skeleton.base.constants.CancellationConstants.RefundTypeEnum;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.MQConstants;
import com.hhly.skeleton.base.exception.ResultJsonException;
import com.hhly.skeleton.base.mq.OrderCancelMsgModel;
import com.hhly.skeleton.base.util.DateUtil;
import com.hhly.skeleton.lotto.base.order.bo.OrderFlowInfoBO;
import com.hhly.skeleton.lotto.base.order.bo.UserChaseRefundBO;
import com.hhly.skeleton.lotto.base.order.vo.UserChaseDetailQueryVO;

/**
 * @desc 追号计划撤单处理者(主要用于用户撤单)
 * @author huangb
 * @date 2017年5月11日
 * @company 益彩网络
 * @version v1.0
 */
@Component("chaseCancelHandler")
public class ChaseCancelHandler {
	/**
	 * 日志对象
	 */
	private static Logger logger = Logger.getLogger(ChaseCancelHandler.class);

	/**
	 * 追号服务（主要用到撤单的服务）
	 */
	@Autowired
	private OrderAddService orderAddService;
	/**
	 * 用户撤单退款的消息队列
	 */
	@Resource(name = "orderCancelMessageProvider")
	private MessageProvider orderCancelMessageProvider;
	
	/**
	 * 订单/追号计划流程日志记录的消息队列
	 */
	@Resource(name="orderFlowMessageProvider")
	private MessageProvider orderFlowMessageProvider;

	/**
	 * @desc 用户撤单
	 * @author huangb
	 * @date 2017年7月3日
	 * @param queryVO
	 * @return 用户撤单
	 */
	public ResultBO<?> cancelChase(UserChaseDetailQueryVO queryVO) {
		try {
			ResultBO<?> rs = orderAddService.updChaseStatusAsUserCancel(queryVO);
			// 确保撤单成功了在发起退款动作
			if (null != rs && rs.isOK() && rs.getData() != null) {
				UserChaseRefundBO userChaseRefund = (UserChaseRefundBO) rs.getData();
				// 发送队列消息：用户撤单
				sendMsgOfOrderCancel(queryVO, userChaseRefund);
				
				// 20170816 add 发送队列消息:追号流程日志记录
				sendMsgOfChaseFlow(queryVO);
			}
		} catch (ResultJsonException e) {
			logger.error("用户撤单=>修改用户撤单信息异常（ResultJsonException）", e);
			return e.getResult();
		} catch (Exception ex) {
			logger.error("用户撤单=>修改用户撤单信息异常（Exception）", ex);
			return ResultBO.err();
		}
		return ResultBO.ok();
	}
	
	/**
	 * @desc 发送队列消息:用户撤单,执行退款
	 * @author huangb
	 * @date 2017年8月15日
	 * @param queryVO
	 *            撤单编号信息
	 * @param userChaseRefund
	 *            退款金额
	 */
	private void sendMsgOfOrderCancel(UserChaseDetailQueryVO queryVO, UserChaseRefundBO userChaseRefund) {
		// b>发送队列消息，执行退款
		orderCancelMessageProvider.sendMessage(MQConstants.ORDER_CANCEL_QUEUENAME,
				new OrderCancelMsgModel(OrderTypeEnum.ADDEDORDER.getKey(),
						RefundTypeEnum.USERCANCELREFUND.getKey(), queryVO.getOrderAddCode(), null,
						userChaseRefund.getRefundAmount()));
	}
	
	/**
	 * @desc 发送队列消息:追号流程日志记录
	 * @author huangb
	 * @date 2017年8月15日
	 * @param queryVO
	 *            撤单编号信息
	 */
	private void sendMsgOfChaseFlow(UserChaseDetailQueryVO queryVO) {
		// 用户/系统撤单的流程日志记录
		OrderFlowInfoBO msgModel = new OrderFlowInfoBO(queryVO.getOrderAddCode(), null, StatusEnum.AFTER_NUMBER.getKey(), DateUtil.getNowDate());
		orderFlowMessageProvider.sendMessage(Constants.QUEUE_NAME_FOR_ORDER_FLOW, msgModel);
	}
}
