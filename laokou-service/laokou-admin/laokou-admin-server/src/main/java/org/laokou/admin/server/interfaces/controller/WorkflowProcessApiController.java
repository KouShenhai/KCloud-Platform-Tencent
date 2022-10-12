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
package org.laokou.admin.server.interfaces.controller;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.laokou.admin.server.application.service.WorkflowProcessApplicationService;
import org.laokou.admin.client.dto.AuditDTO;
import org.laokou.admin.server.interfaces.qo.TaskQO;
import org.laokou.admin.client.vo.TaskVO;
import org.laokou.common.utils.HttpResultUtil;
import org.laokou.log.annotation.OperateLog;
import org.laokou.redis.annotation.Lock4j;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.laokou.redis.enums.LockScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
@RestController
@Api(value = "流程处理API",protocols = "http",tags = "流程处理API")
@RequestMapping("/workflow/process/api")
public class WorkflowProcessApiController {

    @Autowired
    private WorkflowProcessApplicationService workflowProcessApplicationService;

    @PostMapping("/resource/query")
    @ApiOperation("流程处理>资源查询")
    public HttpResultUtil<IPage<TaskVO>> queryResource(@RequestBody TaskQO qo, HttpServletRequest request) {
        return new HttpResultUtil<IPage<TaskVO>>().ok(workflowProcessApplicationService.queryResourceTaskPage(qo,request));
    }

    @PostMapping(value = "/resource/audit")
    @ApiOperation(value = "流程处理>资源审批")
    @OperateLog(module = "流程处理",name = "资源审批")
    @Lock4j(scope = LockScope.DISTRIBUTED_LOCK)
    public HttpResultUtil<Boolean> auditResource(@RequestBody AuditDTO dto, HttpServletRequest request) {
        return new HttpResultUtil<Boolean>().ok(workflowProcessApplicationService.auditResourceTask(dto,request));
    }

}
