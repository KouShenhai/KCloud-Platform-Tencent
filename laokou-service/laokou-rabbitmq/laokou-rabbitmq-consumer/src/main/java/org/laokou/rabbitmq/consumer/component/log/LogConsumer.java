package org.laokou.rabbitmq.consumer.component.log;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.laokou.common.constant.RabbitMqConstant;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
 @Component
public class LogConsumer {

    /**
     * es 消息
     */
    @RabbitHandler
    @RabbitListener(queues = RabbitMqConstant.LAOKOU_LOGIN_LOG_QUEUE)
    public void loginLog(Channel channel, Message message) throws IOException {
        try {
            String msgText = new String(message.getBody(), StandardCharsets.UTF_8);
            if (StringUtils.isNotBlank(msgText)) {

            }
        } catch (Exception e) {
            log.error("消息消费失败", e);
        } finally {
            //手动签发，并回馈信息给MQ
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }

}
