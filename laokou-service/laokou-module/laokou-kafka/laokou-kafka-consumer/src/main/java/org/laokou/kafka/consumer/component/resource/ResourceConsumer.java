/**
 * Copyright (c) 2022 KCloud-Platform-Official Authors. All Rights Reserved.
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
package org.laokou.kafka.consumer.component.resource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.laokou.common.core.utils.DateUtil;
import org.laokou.common.core.utils.JacksonUtil;
import org.laokou.common.core.utils.StringUtil;
import org.laokou.kafka.client.constant.KafkaConstant;
import org.laokou.kafka.client.dto.ResourceAuditLogDTO;
import org.laokou.kafka.consumer.entity.SysResourceAuditLogDO;
import org.laokou.kafka.consumer.entity.SysResourceDO;
import org.laokou.kafka.consumer.service.SysResourceAuditLogService;
import org.laokou.kafka.consumer.service.SysResourceService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
/**
 * @author Kou Shenhai
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ResourceConsumer {

    private final SysResourceAuditLogService sysResourceAuditLogService;

    private final SysResourceService sysResourceService;

    /**
     * 审核消息
     * @param message
     * @param acknowledgment
     */
    @KafkaListener(topics = {KafkaConstant.LAOKOU_RESOURCE_AUDIT_TOPIC})
    public void loginLog(String message, Acknowledgment acknowledgment) {
        try {
            if (StringUtil.isNotEmpty(message)) {
                final ResourceAuditLogDTO auditLogDTO = JacksonUtil.toBean(message, ResourceAuditLogDTO.class);
                SysResourceDO sysResourceDO = sysResourceService.getById(auditLogDTO.getResourceId());
                sysResourceDO.setStatus(auditLogDTO.getStatus());
                sysResourceService.updateById(sysResourceDO);
                //插入审批日志
                SysResourceAuditLogDO logDO = new SysResourceAuditLogDO();
                logDO.setAuditDate(DateUtil.parse(auditLogDTO.getAuditDate(),DateUtil.DATE_TIME_PATTERN));
                logDO.setAuditName(auditLogDTO.getAuditName());
                logDO.setCreator(auditLogDTO.getCreator());
                logDO.setComment(auditLogDTO.getComment());
                logDO.setResourceId(auditLogDTO.getResourceId());
                logDO.setAuditStatus(auditLogDTO.getAuditStatus());
                sysResourceAuditLogService.save(logDO);
            }
        } catch (Exception e) {
            log.error("消息消费失败", e);
        } finally {
            //手动签发，并回馈信息给MQ
            acknowledgment.acknowledge();
        }
    }

}
