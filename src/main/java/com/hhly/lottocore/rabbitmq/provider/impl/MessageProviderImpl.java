package com.hhly.lottocore.rabbitmq.provider.impl;

import org.apache.log4j.Logger;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hhly.lottocore.rabbitmq.provider.MessageProvider;

@Service
public class MessageProviderImpl implements MessageProvider {
	private static final Logger LOGGER = Logger.getLogger(MessageProviderImpl.class);
	
    @Autowired
	private AmqpTemplate amqpTemplate;
    
	@Override
	public void sendMessage(String queueKey, Object message) {
		LOGGER.info(new StringBuilder("发送mq信息：queue:").append(queueKey).append(",messge:").append(message).toString());
		byte [] body= message.toString().getBytes();
		MessageProperties properties = new MessageProperties();
		properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
		Message message2 = new Message(body,properties );
		amqpTemplate.send(queueKey,message2);
	}

	
}
