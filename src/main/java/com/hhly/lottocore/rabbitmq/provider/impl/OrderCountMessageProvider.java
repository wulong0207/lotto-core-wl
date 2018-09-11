package com.hhly.lottocore.rabbitmq.provider.impl;

import com.hhly.lottocore.rabbitmq.provider.MessageProvider;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.cms.ordermgr.vo.OrderGroupLotteryBO;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description 发起合买，成功后发消息
 * @Author longguoyou
 * @Date  2018/7/25 14:37
 * @Since 1.8
 */
@Service("orderCountMessageProvider")
public class OrderCountMessageProvider implements MessageProvider {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Override
    public void sendMessage(String queueKey, Object message) {
        OrderGroupLotteryBO target = (OrderGroupLotteryBO) message;
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
