package org.laokou.rabbitmq.consumer.component.log;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.laokou.common.constant.RabbitMqConstant;
import org.laokou.common.dto.LoginLogDTO;
import org.laokou.common.dto.OperateLogDTO;
import org.laokou.common.utils.JacksonUtil;
import org.laokou.rabbitmq.consumer.service.SysLoginLogService;
import org.laokou.rabbitmq.consumer.service.SysOperateLogService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
 @Component
public class LogConsumer {

    @Autowired
    private SysLoginLogService sysLoginLogService;

    @Autowired
    private SysOperateLogService sysOperateLogService;

    /**
     * 登录日志消息
     */
    @RabbitHandler
    @RabbitListener(queues = RabbitMqConstant.LAOKOU_LOGIN_LOG_QUEUE)
    public void loginLog(Channel channel, Message message) throws IOException {
        try {
            String msgText = new String(message.getBody(), StandardCharsets.UTF_8);
            if (StringUtils.isNotBlank(msgText)) {
                final LoginLogDTO loginLogDTO = JacksonUtil.toBean(msgText, LoginLogDTO.class);
                sysLoginLogService.insertLoginLog(loginLogDTO);
            }
        } catch (Exception e) {
            log.error("消息消费失败", e);
        } finally {
            //手动签发，并回馈信息给MQ
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }

    /**
     * 操作日志消息
     */
    @RabbitHandler
    @RabbitListener(queues = RabbitMqConstant.LAOKOU_OPERATE_LOG_QUEUE)
    public void operateLog(Channel channel, Message message) throws IOException {
        try {
            String msgText = new String(message.getBody(), StandardCharsets.UTF_8);
            if (StringUtils.isNotBlank(msgText)) {
                final OperateLogDTO operateLogDTO = JacksonUtil.toBean(msgText, OperateLogDTO.class);
                sysOperateLogService.insertOperateLog(operateLogDTO);
            }
        } catch (Exception e) {
            log.error("消息消费失败", e);
        } finally {
            //手动签发，并回馈信息给MQ
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }

}
