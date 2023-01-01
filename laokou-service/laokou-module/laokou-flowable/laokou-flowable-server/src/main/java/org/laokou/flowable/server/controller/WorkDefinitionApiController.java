/**
 * Copyright (c) 2022 KCloud-Platform-Tencent Authors. All Rights Reserved.
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
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.laokou.common.swagger.utils.HttpResult;
import org.laokou.flowable.client.dto.DefinitionDTO;
import org.laokou.flowable.client.vo.DefinitionVO;
import org.laokou.flowable.client.vo.PageVO;
import org.laokou.flowable.server.service.WorkDefinitionService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author laokou
 */
@RestController
@Api(value = "流程定义API",protocols = "http",tags = "流程定义API")
@RequestMapping("/work/definition/api")
@RequiredArgsConstructor
public class WorkDefinitionApiController {
    private final WorkDefinitionService workDefinitionService;

    @PostMapping(value = "/insert")
    @ApiOperation(value = "流程定义>新增流程")
    public HttpResult<Boolean> insert(@RequestParam("name")String name, @RequestPart("file") MultipartFile file) throws IOException {
        return new HttpResult<Boolean>().ok(workDefinitionService.insertDefinition(name,file.getInputStream()));
    }

    @PostMapping(value = "/query")
    @ApiOperation(value = "流程定义>查询流程")
    public HttpResult<PageVO<DefinitionVO>> query(@RequestBody DefinitionDTO dto) {
        return new HttpResult<PageVO<DefinitionVO>>().ok(workDefinitionService.queryDefinitionPage(dto));
    }

    @GetMapping(value = "/diagram")
    @ApiOperation(value = "流程定义>流程图")
    public void diagram(@RequestParam("definitionId")String definitionId, HttpServletResponse response) {
        workDefinitionService.diagramDefinition(definitionId,response);
    }

    @DeleteMapping(value = "/delete")
    @ApiOperation(value = "流程定义>删除流程")
    public HttpResult<Boolean> delete(@RequestParam("deploymentId")String deploymentId) {
        return new HttpResult<Boolean>().ok(workDefinitionService.deleteDefinition(deploymentId));
    }

    @PutMapping(value = "/suspend")
    @ApiOperation(value = "流程定义>挂起流程")
    public HttpResult<Boolean> suspend(@RequestParam("definitionId")String definitionId) {
        return new HttpResult<Boolean>().ok(workDefinitionService.suspendDefinition(definitionId));
    }

    @PutMapping(value = "/activate")
    @ApiOperation(value = "流程定义>激活流程")
    public HttpResult<Boolean> activate(@RequestParam("definitionId")String definitionId) {
        return new HttpResult<Boolean>().ok(workDefinitionService.activateDefinition(definitionId));
    }

}