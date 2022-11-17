/**
 * Copyright (c) 2022 KCloud-Platform-Official Authors. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *   http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.laokou.kafka.consumer.component.log;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.laokou.common.core.utils.JacksonUtil;
import org.laokou.common.core.utils.StringUtil;
import org.laokou.kafka.client.dto.LoginLogDTO;
import org.laokou.kafka.client.dto.OperateLogDTO;
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
            if (StringUtil.isNotEmpty(message)) {
                final LoginLogDTO loginLogDTO = JacksonUtil.toBean(message, LoginLogDTO.class);
                sysLoginLogService.insertLoginLog(loginLogDTO);
            }
        } catch (Exception e) {
            log.error("消息消费失败，错误信息：{}", e.getMessage());
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
            if (StringUtil.isNotEmpty(message)) {
                final OperateLogDTO operateLogDTO = JacksonUtil.toBean(message, OperateLogDTO.class);
                sysOperateLogService.insertOperateLog(operateLogDTO);
            }
        } catch (Exception e) {
            log.error("消息消费失败，错误信息：{}", e.getMessage());
        } finally {
            //手动签发，并回馈信息给MQ
            acknowledgment.acknowledge();
        }
    }

}
