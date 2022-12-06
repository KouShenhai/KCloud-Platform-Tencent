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
import org.laokou.admin.server.application.service.WorkflowTaskApplicationService;
import org.laokou.admin.client.enums.FlowCommentEnum;
import org.laokou.admin.client.dto.AuditDTO;
import org.laokou.admin.client.dto.ClaimDTO;
import org.laokou.admin.client.dto.UnClaimDTO;
import org.laokou.auth.client.utils.UserUtil;
import org.laokou.common.core.exception.CustomException;
import org.laokou.common.core.utils.FileUtil;

import org.apache.commons.collections.MapUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.image.ProcessDiagramGenerator;
import org.flowable.task.api.DelegationState;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Kou Shenhai
 */
@Service
public class WorkflowTaskApplicationServiceImpl implements WorkflowTaskApplicationService {

    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RepositoryService repositoryService;

    @Qualifier("processEngine")
    @Autowired
    private ProcessEngine processEngine;

    @Override
    public Boolean auditTask(AuditDTO dto) {
        Task task = taskService.createTaskQuery().taskId(dto.getTaskId()).singleResult();
        if (null == task) {
            throw new CustomException("任务不存在");
        }
        if (DelegationState.PENDING.equals(task.getDelegationState())) {
            taskService.addComment(dto.getTaskId(),dto.getInstanceId(), FlowCommentEnum.DELEGATE.getType(),dto.getComment());
            //委派
            taskService.resolveTask(dto.getTaskId(),dto.getValues());
        } else {
            taskService.addComment(dto.getTaskId(),dto.getInstanceId(), FlowCommentEnum.NORMAL.getType(),dto.getComment());
            if (MapUtils.isNotEmpty(dto.getValues())) {
                taskService.complete(dto.getTaskId(),dto.getValues());
            } else {
                taskService.complete(dto.getTaskId());
            }
        }
        return true;
    }

    @Override
    public Boolean claimTask(ClaimDTO dto) {
        final Task task = taskService.createTaskQuery()
                .taskId(dto.getTaskId())
                .singleResult();
        if (null == task) {
            throw new CustomException("任务不存在");
        }
        taskService.claim(dto.getTaskId(), UserUtil.getUserId().toString());
        return true;
    }

    @Override
    public Boolean unClaimTask(UnClaimDTO dto) {
        taskService.unclaim(dto.getTaskId());
        return true;
    }

    @Override
    public Boolean deleteTask(String taskId) {
        taskService.deleteTask(taskId);
        return true;
    }
}
