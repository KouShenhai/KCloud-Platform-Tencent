///**
// * Copyright (c) 2022 KCloud-Platform-Official Authors. All Rights Reserved.
// * <p>
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// * <p>
// * http://www.apache.org/licenses/LICENSE-2.0
// * <p>
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package org.laokou.rocketmq.consumer.core.resource;
//
//import lombok.RequiredArgsConstructor;
//import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
//import org.apache.rocketmq.spring.core.RocketMQListener;
//import org.laokou.common.core.utils.JacksonUtil;
//import org.laokou.rocketmq.client.constant.RocketmqConstant;
//import org.laokou.rocketmq.client.dto.ResourceAuditLogDTO;
//import org.laokou.rocketmq.consumer.entity.SysResourceAuditLogDO;
//import org.laokou.rocketmq.consumer.entity.SysResourceDO;
//import org.laokou.rocketmq.consumer.service.SysResourceAuditLogService;
//import org.laokou.rocketmq.consumer.service.SysResourceService;
//import org.springframework.stereotype.Component;
//
///**
// * @author Kou Shenhai
// */
//@RocketMQMessageListener(consumerGroup = "laokou-consumer-group-3", topic = RocketmqConstant.LAOKOU_AUDIT_RESOURCE_TOPIC)
//@Component
//@RequiredArgsConstructor
//public class ResourceAuditConsumer implements RocketMQListener<String> {
//
//    private final SysResourceAuditLogService sysResourceAuditLogService;
//
//    private final SysResourceService sysResourceService;
//
//    @Override
//    public void onMessage(String message) {
//        final ResourceAuditLogDTO auditLogDTO = JacksonUtil.toBean(message, ResourceAuditLogDTO.class);
//        assert auditLogDTO != null;
//        SysResourceDO sysResourceDO = sysResourceService.getById(auditLogDTO.getResourceId());
//        sysResourceDO.setStatus(auditLogDTO.getStatus());
//        sysResourceService.updateById(sysResourceDO);
//        //插入审批日志
//        SysResourceAuditLogDO logDO = new SysResourceAuditLogDO();
//        logDO.setAuditDate(auditLogDTO.getAuditDate());
//        logDO.setAuditName(auditLogDTO.getAuditName());
//        logDO.setCreator(auditLogDTO.getCreator());
//        logDO.setComment(auditLogDTO.getComment());
//        logDO.setResourceId(auditLogDTO.getResourceId());
//        logDO.setAuditStatus(auditLogDTO.getAuditStatus());
//        sysResourceAuditLogService.save(logDO);
//    }
//
//}
