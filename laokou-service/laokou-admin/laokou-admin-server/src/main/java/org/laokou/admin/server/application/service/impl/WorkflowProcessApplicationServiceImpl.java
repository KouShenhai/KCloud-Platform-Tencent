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
package org.laokou.admin.server.application.service.impl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.laokou.admin.server.application.service.WorkflowProcessApplicationService;
import org.laokou.admin.server.application.service.WorkflowTaskApplicationService;
import org.laokou.admin.client.enums.ChannelTypeEnum;
import org.laokou.admin.client.enums.MessageTypeEnum;
import org.laokou.admin.server.infrastructure.feign.kafka.RocketmqApiFeignClient;
import org.laokou.admin.server.infrastructure.utils.WorkFlowUtil;
import org.laokou.admin.client.dto.AuditDTO;
import org.laokou.admin.client.vo.StartProcessVO;
import org.laokou.admin.server.interfaces.qo.TaskQo;
import org.laokou.admin.client.vo.TaskVO;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.laokou.auth.client.utils.UserUtil;
import org.laokou.common.core.exception.CustomException;
import org.laokou.common.core.utils.JacksonUtil;
import org.laokou.common.core.utils.RedisKeyUtil;
import org.laokou.common.core.utils.StringUtil;
import org.laokou.common.core.utils.ThreadUtil;
import org.laokou.redis.utils.RedisUtil;
import org.laokou.rocketmq.client.constant.RocketmqConstant;
import org.laokou.rocketmq.client.dto.ResourceAuditLogDTO;
import org.laokou.rocketmq.client.dto.RocketmqDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
/**
 * @author Kou Shenhai
 */
@Service
@Slf4j
public class WorkflowProcessApplicationServiceImpl implements WorkflowProcessApplicationService {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    private static final String PROCESS_KEY = "Process_88888888";

    @Autowired
    private WorkflowTaskApplicationService workflowTaskApplicationService;

    @Autowired
    private RocketmqApiFeignClient rocketmqApiFeignClient;

    @Autowired
    private WorkFlowUtil workFlowUtil;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public StartProcessVO startResourceProcess(String processKey, String businessKey, String instanceName) {
        StartProcessVO vo = new StartProcessVO();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processKey)
                .latestVersion()
                .singleResult();
        if (null != processDefinition && processDefinition.isSuspended()) {
            throw new CustomException("流程已被挂起，请先激活流程");
        }
        final ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processKey,businessKey);
        runtimeService.setProcessInstanceName(processInstance.getId(),instanceName);
        assert processDefinition != null;
        vo.setDefinitionId(processDefinition.getId());
        vo.setInstanceId(processInstance.getId());
        return vo;
    }

    @Override
    public IPage<TaskVO> queryResourceTaskPage(TaskQo qo) {
        final Integer pageNum = qo.getPageNum();
        final Integer pageSize = qo.getPageSize();
        String processName = qo.getProcessName();
        final Long userId = UserUtil.getUserId();
        final String username = UserUtil.getUsername();
        TaskQuery taskQuery = taskService.createTaskQuery()
                .active()
                .includeProcessVariables()
                .processDefinitionKey(PROCESS_KEY)
                .taskCandidateOrAssigned(userId.toString())
                .orderByTaskCreateTime().desc();
        if (StringUtil.isNotEmpty(processName)) {
            taskQuery = taskQuery.processDefinitionNameLike("%" + processName + "%");
        }
        final long pageTotal = taskQuery.count();
        IPage<TaskVO> page = new Page<>(pageNum,pageSize,pageTotal);
        int  pageIndex = pageSize * (pageNum - 1);
        final List<Task> taskList = taskQuery.listPage(pageIndex, pageSize);
        List<TaskVO> voList = new ArrayList<>(taskList.size());
        for (Task task : taskList) {
            TaskVO vo = new TaskVO();
            vo.setTaskId(task.getId());
            vo.setTaskDefinitionKey(task.getTaskDefinitionKey());
            vo.setProcessInstanceId(task.getProcessInstanceId());
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .latestVersion()
                    .processDefinitionId(task.getProcessDefinitionId())
                    .singleResult();
            vo.setTaskName(task.getName());
            vo.setDefinitionId(processDefinition.getId());
            vo.setProcessName(processDefinition.getName());
            vo.setAssigneeName(username);
            vo.setCreateTime(task.getCreateTime());
            vo.setProcessInstanceName(processInstance.getName());
            vo.setBusinessKey(processInstance.getBusinessKey());
            voList.add(vo);
        }
        page.setRecords(voList);
        return page;
    }

    @Override
    public Boolean auditResourceTask(AuditDTO dto) {
        Map<String, Object> values = dto.getValues();
        String comment = dto.getComment();
        Boolean auditFlag = workflowTaskApplicationService.auditTask(dto);
        String auditUser = workFlowUtil.getAuditUser(dto.getDefinitionId(), dto.getInstanceId());
        int auditStatus = Integer.parseInt(values.get("auditStatus").toString());
        int status;
        //1 审核中 2 审批拒绝 3审核通过
        if (null != auditUser) {
            //审批中
            status = 1;
            workFlowUtil.sendAuditMsg(auditUser, MessageTypeEnum.REMIND.ordinal(), ChannelTypeEnum.PLATFORM.ordinal(),Long.valueOf(dto.getBusinessKey()),dto.getInstanceName());
        } else {
            //0拒绝 1同意
            if (0 == auditStatus) {
                //审批拒绝
                status = 2;
            } else {
                //审批通过
                status = 3;
            }
        }
        Long resourceId = Long.valueOf(dto.getBusinessKey());
        String resourceAuditKey = RedisKeyUtil.getResourceAuditKey(resourceId);
        String taskId = dto.getTaskId();
        Object obj = redisUtil.hGet(resourceAuditKey, taskId);
        if (obj != null) {
            return false;
        } else {
            redisUtil.hSet(resourceAuditKey,taskId,taskId,RedisUtil.HOUR_ONE_EXPIRE);
        }
        String username = UserUtil.getUsername();
        Long userId = UserUtil.getUserId();
        ThreadUtil.executorService.execute(() -> saveAuditLog(resourceId,status,auditStatus,comment, username,userId));
        return auditFlag;
    }

    private void saveAuditLog(Long resourceId,int status,int auditStatus,String comment,String username,Long userId) {
        try {
            ResourceAuditLogDTO auditLogDTO = new ResourceAuditLogDTO();
            auditLogDTO.setResourceId(resourceId);
            auditLogDTO.setStatus(status);
            auditLogDTO.setAuditStatus(auditStatus);
            auditLogDTO.setAuditDate(new Date());
            auditLogDTO.setAuditName(username);
            auditLogDTO.setCreator(userId);
            auditLogDTO.setComment(comment);
            RocketmqDTO rocketmqDTO = new RocketmqDTO();
            rocketmqDTO.setData(JacksonUtil.toJsonStr(auditLogDTO));
            rocketmqApiFeignClient.sendAsyncMessage(RocketmqConstant.LAOKOU_RESOURCE_AUDIT_TOPIC, rocketmqDTO);
        } catch (FeignException e) {
            log.error("错误信息：{}",e.getMessage());
        }
    }

}
