package org.laokou.kafka.consumer.component.log;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.laokou.kafka.client.dto.LoginLogDTO;
import org.laokou.kafka.client.dto.OperateLogDTO;
import org.laokou.common.utils.JacksonUtil;
import org.laokou.kafka.client.constant.KafkaConstant;
import org.laokou.kafka.consumer.service.SysLoginLogService;
import org.laokou.kafka.consumer.service.SysOperateLogService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * @author Kou Shenhai
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LogConsumer {

    private final SysLoginLogService sysLoginLogService;

    private final SysOperateLogService sysOperateLogService;

    /**
     * 登录日志消息
     */
    @KafkaListener(topics = {KafkaConstant.LAOKOU_LOGIN_LOG_TOPIC})
    public void loginLog(String message, Acknowledgment acknowledgment) {
        try {
            if (StringUtils.isNotBlank(message)) {
                final LoginLogDTO loginLogDTO = JacksonUtil.toBean(message, LoginLogDTO.class);
                sysLoginLogService.insertLoginLog(loginLogDTO);
            }
        } catch (Exception e) {
            log.error("消息消费失败", e);
        } finally {
            //手动签发，并回馈信息给MQ
            acknowledgment.acknowledge();
        }
    }

    /**
     * 操作日志消息
     */
    @KafkaListener(topics = {KafkaConstant.LAOKOU_OPERATE_LOG_TOPIC})
    public void operateLog(String message, Acknowledgment acknowledgment) {
        try {
            if (StringUtils.isNotBlank(message)) {
                final OperateLogDTO operateLogDTO = JacksonUtil.toBean(message, OperateLogDTO.class);
                sysOperateLogService.insertOperateLog(operateLogDTO);
            }
        } catch (Exception e) {
            log.error("消息消费失败", e);
        } finally {
            //手动签发，并回馈信息给MQ
            acknowledgment.acknowledge();
        }
    }

}
