package com.hhly.lottocore.rabbitmq.provider.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hhly.lottocore.rabbitmq.provider.MessageProvider;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.mq.OrderCancelMsgModel;

/**
 * @desc 用户撤单退款的消息队列
 * @author huangb
 * @date 2017年5月29日
 * @company 益彩网络
 * @version v1.0
 */
@Component("orderCancelMessageProvider")
public class OrderCancelMessageProvider implements MessageProvider {

	/**
	 * 日志对象
	 */
	private static Logger logger = LoggerFactory.getLogger(OrderCancelMessageProvider.class);

	/**
	 * 消息模板(可提供消息的转换)
	 */
	@Autowired
	private AmqpTemplate amqpTemplate;

	@Override
	public void sendMessage(String queueKey, Object message) {

		if (message == null || !(message instanceof OrderCancelMsgModel)) {
			logger.error("发送 用户撤单退款消息 到队列=>消息为空或消息模型不正确，不处理！");
			return;
		}
		OrderCancelMsgModel target = (OrderCancelMsgModel) message;
		try {
			amqpTemplate.convertAndSend(queueKey, target, new MessagePostProcessor() {
				@Override
				public Message postProcessMessage(Message message) throws AmqpException {
					message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
					message.getMessageProperties().setPriority(Constants.NUM_2);// 消息优先级
					return message;
				}
			});
		} catch (Exception e) {
			logger.error(
					"发送 用户撤单退款消息 到队列异常，orderType=" + target.getOrderType() + ",refundType=" + target.getRefundType()
							+ ",orderCode=" + target.getOrderCode() + ",refundAmount=" + target.getRefundAmount(), e);
			// 记录告警 , 30300

			// ResultBO.getMsg("30300", target.getOrderType(),
			// target.getRefundType(), target.getOrderCode(),
			// target.getIssueCode(), target.getRefundAmount());

		}
	}
}
