package com.hhly.lottocore.rabbitmq.provider.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hhly.lottocore.rabbitmq.provider.MessageProvider;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.util.JsonUtil;

/**
 * 发送消息
 *
 * @author yuanshangbing
 * @create 2017/5/22 17:08
 */
@Service("orderFlowMessageProvider")
public class OrderFlowMessageProvider implements MessageProvider {

	/**
	 * 日志对象
	 */
	private static Logger logger = LoggerFactory.getLogger(OrderFlowMessageProvider.class);
	
    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     * 发送消息
     *
     * @param queueKey 队列名
     * @param message  消息
     */
    @Override
    public void sendMessage(String queueKey, Object message) {
        if (message == null) {
			logger.error("发送 订单/追号计划流程日志记录 到队列=>消息为空或消息模型不正确，不处理！");
			return;
		}
		try {
			amqpTemplate.convertAndSend(queueKey, message, new MessagePostProcessor() {
				@Override
				public Message postProcessMessage(Message message) throws AmqpException {
					message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
					message.getMessageProperties().setPriority(Constants.NUM_2);// 消息优先级
					return message;
				}
			});
			logger.info("发送 订单/追号计划流程日志记录 到队列成功！queueKey：" + queueKey + "，消息内容：" + JsonUtil.objectToJcakJson(message));
		} catch (Exception e) {
			logger.error("发送 订单/追号计划流程日志记录 到队列异常queueKey：" + queueKey + "，消息内容：" + JsonUtil.objectToJcakJson(message), e);
			// 记录告警 , 000000

			// ResultBO.getMsg("000000", target.getOrderType(),
			// target.getRefundType(), target.getOrderCode(),
			// target.getIssueCode(), target.getRefundAmount());
		}
    }
}