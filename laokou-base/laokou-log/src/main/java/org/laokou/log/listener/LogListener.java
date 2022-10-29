/**
 * Copyright (c) 2022 KCloud-Platform Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.laokou.log.listener;
import feign.FeignException;
import org.laokou.kafka.client.dto.LoginLogDTO;
import org.laokou.kafka.client.dto.OperateLogDTO;
import org.laokou.common.exception.CustomException;
import org.laokou.common.exception.ErrorCode;
import org.laokou.common.utils.JacksonUtil;
import org.laokou.kafka.client.constant.KafkaConstant;
import org.laokou.kafka.client.dto.KafkaDTO;
import org.laokou.log.event.LoginLogEvent;
import org.laokou.log.event.OperateLogEvent;
import lombok.extern.slf4j.Slf4j;
import org.laokou.log.feign.kafka.KafkaApiFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
@Slf4j
@Component
@ConditionalOnWebApplication
public class LogListener {

    @Autowired
    private KafkaApiFeignClient kafkaApiFeignClient;

    @Order
    @EventListener(OperateLogEvent.class)
    public void listenOperateLog(OperateLogEvent event) {
        OperateLogDTO dto = (OperateLogDTO) event.getSource();
        KafkaDTO kafkaDTO = new KafkaDTO();
        kafkaDTO.setData(JacksonUtil.toJsonStr(dto));
        try {
            kafkaApiFeignClient.sendAsyncMessage(KafkaConstant.LAOKOU_OPERATE_LOG_TOPIC, kafkaDTO);
        } catch (FeignException e) {
            log.info("报错信息：{}",e.getMessage());
            throw new CustomException(ErrorCode.SERVICE_MAINTENANCE);
        }
    }

    @Order
    @EventListener(LoginLogEvent.class)
    public void listenLoginLog(LoginLogEvent event) {
        LoginLogDTO dto = (LoginLogDTO) event.getSource();
        KafkaDTO kafkaDTO = new KafkaDTO();
        kafkaDTO.setData(JacksonUtil.toJsonStr(dto));
        try {
            kafkaApiFeignClient.sendAsyncMessage(KafkaConstant.LAOKOU_LOGIN_LOG_TOPIC, kafkaDTO);
        } catch (FeignException e) {
            log.info("报错信息：{}",e.getMessage());
            throw new CustomException(ErrorCode.SERVICE_MAINTENANCE);
        }
    }

}
