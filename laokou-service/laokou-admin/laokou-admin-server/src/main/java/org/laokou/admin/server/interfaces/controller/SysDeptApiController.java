/**
 * Copyright (c) 2022 KCloud-Platform-Tencent Authors. All Rights Reserved.
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
package org.laokou.admin.server.interfaces.controller;
import org.laokou.admin.server.application.service.SysDeptApplicationService;
import org.laokou.admin.client.dto.SysDeptDTO;
import org.laokou.admin.server.interfaces.qo.SysDeptQo;
import org.laokou.common.core.utils.HttpResultUtil;
import org.laokou.admin.client.vo.SysDeptVO;
import org.laokou.admin.server.infrastructure.annotation.OperateLog;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;
/**
 * 系统部门
 * @author laokou
 * @version 1.0
 * @date 2022/7/26 0026 下午 4:28
 */
@RestController
//@Api(value = "系统部门API",protocols = "http",tags = "系统部门API")
@RequestMapping("/sys/dept/api")
public class SysDeptApiController {

    @Autowired
    private SysDeptApplicationService sysDeptApplicationService;

    @GetMapping("/list")
//    @ApiOperation("系统部门>列表")
    public HttpResultUtil<SysDeptVO> list() {
        return new HttpResultUtil<SysDeptVO>().ok(sysDeptApplicationService.getDeptList());
    }

    @PostMapping("/query")
//    @ApiOperation("系统部门>查询")
    @PreAuthorize("hasAuthority('sys:dept:query')")
    public HttpResultUtil<List<SysDeptVO>> query(@RequestBody SysDeptQo qo) {
        return new HttpResultUtil<List<SysDeptVO>>().ok(sysDeptApplicationService.queryDeptList(qo));
    }

    @PostMapping("/insert")
//    @ApiOperation("系统部门>新增")
    @OperateLog(module = "系统部门",name = "部门新增")
    @PreAuthorize("hasAuthority('sys:dept:insert')")
    public HttpResultUtil<Boolean> insert(@RequestBody SysDeptDTO dto) {
        return new HttpResultUtil<Boolean>().ok(sysDeptApplicationService.insertDept(dto));
    }

    @PutMapping("/update")
//    @ApiOperation("系统部门>修改")
    @OperateLog(module = "系统部门",name = "部门修改")
    @PreAuthorize("hasAuthority('sys:dept:update')")
    public HttpResultUtil<Boolean> update(@RequestBody SysDeptDTO dto) {
        return new HttpResultUtil<Boolean>().ok(sysDeptApplicationService.updateDept(dto));
    }

    @GetMapping("/detail")
//    @ApiOperation("系统部门>详情")
    public HttpResultUtil<SysDeptVO> detail(@RequestParam("id")Long id) {
        return new HttpResultUtil<SysDeptVO>().ok(sysDeptApplicationService.getDept(id));
    }

    @DeleteMapping("/delete")
//    @ApiOperation("系统部门>删除")
    @OperateLog(module = "系统部门",name = "部门删除")
    @PreAuthorize("hasAuthority('sys:dept:delete')")
    public HttpResultUtil<Boolean> delete(@RequestParam("id")Long id) {
        return new HttpResultUtil<Boolean>().ok(sysDeptApplicationService.deleteDept(id));
    }

    @GetMapping("/get")
//    @ApiOperation("系统部门>部门树ids")
    public HttpResultUtil<List<Long>> get(@RequestParam(value = "roleId")Long roleId) {
        return new HttpResultUtil<List<Long>>().ok(sysDeptApplicationService.getDeptIdsByRoleId(roleId));
    }

}
