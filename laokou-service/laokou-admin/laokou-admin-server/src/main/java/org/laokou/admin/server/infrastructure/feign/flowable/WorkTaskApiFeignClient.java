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
package org.laokou.admin.server.infrastructure.feign.flowable;

import org.laokou.admin.server.infrastructure.feign.flowable.dto.AuditDTO;
import org.laokou.admin.server.infrastructure.feign.flowable.dto.ProcessDTO;
import org.laokou.admin.server.infrastructure.feign.flowable.dto.TaskDTO;
import org.laokou.admin.server.infrastructure.feign.flowable.factory.WorkTaskApiFeignClientFallbackFactory;
import org.laokou.admin.server.infrastructure.feign.flowable.vo.AssigneeVO;
import org.laokou.admin.server.infrastructure.feign.flowable.vo.PageVO;
import org.laokou.admin.server.infrastructure.feign.flowable.vo.TaskVO;
import org.laokou.common.core.constant.ServiceConstant;
import org.laokou.common.core.utils.HttpResultUtil;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
/**
 * @author Kou Shenhai
 */
@FeignClient(contextId = "workTask",name = ServiceConstant.LAOKOU_FLOWABLE,path = "/work/task/api", fallback = WorkTaskApiFeignClientFallbackFactory.class)
@Service
public interface WorkTaskApiFeignClient {

    /**
     * 查询任务
     * @param dto
     * @return
     */
    @PostMapping(value = "/query")
    HttpResultUtil<PageVO<TaskVO>> query(@RequestBody TaskDTO dto);

    /**
     * 审批任务
     * @param dto
     * @return
     */
    @PostMapping(value = "/audit")
    HttpResultUtil<AssigneeVO> audit(@RequestBody AuditDTO dto);

    /**
     * 开始任务
     * @param dto
     * @return
     */
    @PostMapping(value = "/start")
    HttpResultUtil<AssigneeVO> start(@RequestBody ProcessDTO dto);

    /**
     * 流程图
     * @param processInstanceId
     */
    @GetMapping(value = "/diagram")
    void diagram(@RequestParam("processInstanceId")String processInstanceId);
}
