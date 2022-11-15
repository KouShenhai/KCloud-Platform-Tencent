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
package org.laokou.admin.server.application.service;
import org.laokou.admin.client.dto.AuditDTO;
import org.laokou.admin.client.dto.ClaimDTO;
import org.laokou.admin.client.dto.UnClaimDTO;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
/**
 * @author Kou Shenhai
 */
public interface WorkflowTaskApplicationService {

    /**
     * 审批任务
     * @param dto
     * @return
     */
    Boolean auditTask(AuditDTO dto);

    /**
     * 签收任务
     * @param dto
     * @return
     */
    Boolean claimTask(ClaimDTO dto);

    /**
     * 取消签收任务
     * @param dto
     * @return
     */
    Boolean unClaimTask(UnClaimDTO dto);

    /**
     * 删除任务
     * @param taskId
     * @return
     */
    Boolean deleteTask(String taskId);

    /**
     * 任务流程图
     * @param processInstanceId
     * @param response
     * @throws IOException
     */
    void diagramProcess(String processInstanceId, HttpServletResponse response) throws IOException;
}
