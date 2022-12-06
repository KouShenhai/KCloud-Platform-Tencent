/**
 * Copyright (c) 2022 KCloud-Platform-Official Authors. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.laokou.flowable.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.laokou.common.core.utils.HttpResultUtil;
import org.laokou.flowable.server.dto.AuditDTO;
import org.laokou.flowable.server.dto.ProcessDTO;
import org.laokou.flowable.server.dto.TaskDTO;
import org.laokou.flowable.server.service.WorkTaskService;
import org.laokou.flowable.server.vo.AssigneeVO;
import org.laokou.flowable.server.vo.PageVO;
import org.laokou.flowable.server.vo.TaskVO;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Kou Shenhai
 */
@RestController
@Api(value = "流程任务API",protocols = "http",tags = "流程任务API")
@RequestMapping("/work/task/api")
@RequiredArgsConstructor
public class WorkTaskApiController {

    private final WorkTaskService workTaskService;

    @PostMapping(value = "/query")
    @ApiOperation(value = "流程任务>查询任务")
    public HttpResultUtil<PageVO<TaskVO>> query(@RequestBody TaskDTO dto) {
        return new HttpResultUtil<PageVO<TaskVO>>().ok(workTaskService.queryTaskPage(dto));
    }

    @PostMapping(value = "/audit")
    @ApiOperation(value = "流程任务>审批任务")
    public HttpResultUtil<AssigneeVO> audit(@RequestBody AuditDTO dto) {
        return new HttpResultUtil<AssigneeVO>().ok(workTaskService.auditTask(dto));
    }

    @PostMapping(value = "/start")
    @ApiOperation(value = "流程任务>开始任务")
    public HttpResultUtil<AssigneeVO> start(@RequestBody ProcessDTO dto) {
        return new HttpResultUtil<AssigneeVO>().ok(workTaskService.startTask(dto));
    }

    @GetMapping(value = "/diagram")
    @ApiOperation(value = "流程任务>任务流程")
    public void diagram(@RequestParam("processInstanceId")String processInstanceId, HttpServletResponse response) throws IOException {
        workTaskService.diagramTask(processInstanceId, response);
    }

}