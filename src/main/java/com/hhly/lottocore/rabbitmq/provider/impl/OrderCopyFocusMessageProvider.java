package com.hhly.lottocore.rabbitmq.provider.impl;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hhly.lottocore.rabbitmq.provider.MessageProvider;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.mq.msg.MessageModel;
/**
 * 抄单关注，发消息
 * @author longguoyou
 * @date 2017年11月14日
 * @compay 益彩网络科技有限公司
 */

@Service("orderCopyFocusMessageProvider")
public class OrderCopyFocusMessageProvider implements MessageProvider {

	@Autowired
	private AmqpTemplate amqpTemplate;
	
	@Override
	public void sendMessage(String queueKey, Object message) {
		MessageModel target = (MessageModel) message;
        try {
			amqpTemplate.convertAndSend(queueKey, target, new MessagePostProcessor() {
				@Override
				public Message postProcessMessage(Message message) throws AmqpException {
					message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
					message.getMessageProperties().setPriority(Constants.NUM_2);// 消息优先级
					return message;
				}
			});
		} catch (AmqpException e) {
			e.printStackTrace();
		}    
	}

}
