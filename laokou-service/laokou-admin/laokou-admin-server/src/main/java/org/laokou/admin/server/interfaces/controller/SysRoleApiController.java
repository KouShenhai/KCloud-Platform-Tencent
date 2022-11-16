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
package org.laokou.admin.server.interfaces.controller;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.laokou.admin.server.application.service.SysRoleApplicationService;
import org.laokou.admin.client.dto.SysRoleDTO;
import org.laokou.admin.server.interfaces.qo.SysRoleQo;
import org.laokou.admin.client.vo.SysRoleVO;
import org.laokou.admin.server.infrastructure.component.annotation.OperateLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.laokou.common.core.utils.HttpResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
/**
 * 系统角色控制器
 * @author Kou Shenhai
 */
@RestController
@Api(value = "系统角色API",protocols = "http",tags = "系统角色API")
@RequestMapping("/sys/role/api")
public class SysRoleApiController {

    @Autowired
    private SysRoleApplicationService sysRoleApplicationService;

    @PostMapping("/query")
    @ApiOperation("系统角色>查询")
    @PreAuthorize("hasAuthority('sys:role:query')")
    public HttpResultUtil<IPage<SysRoleVO>> query(@RequestBody SysRoleQo qo) {
        return new HttpResultUtil<IPage<SysRoleVO>>().ok(sysRoleApplicationService.queryRolePage(qo));
    }

    @PostMapping("/list")
    @ApiOperation("系统角色>列表")
    public HttpResultUtil<List<SysRoleVO>> list(@RequestBody SysRoleQo qo) {
        return new HttpResultUtil<List<SysRoleVO>>().ok(sysRoleApplicationService.getRoleList(qo));
    }

    @GetMapping("/detail")
    @ApiOperation("系统角色>详情")
    public HttpResultUtil<SysRoleVO> detail(@RequestParam("id") Long id) {
        return new HttpResultUtil<SysRoleVO>().ok(sysRoleApplicationService.getRoleById(id));
    }

    @PostMapping("/insert")
    @ApiOperation("系统角色>新增")
    @OperateLog(module = "系统角色",name = "角色新增")
    @PreAuthorize("hasAuthority('sys:role:insert')")
    public HttpResultUtil<Boolean> insert(@RequestBody SysRoleDTO dto) {
        return new HttpResultUtil<Boolean>().ok(sysRoleApplicationService.insertRole(dto));
    }

    @PutMapping("/update")
    @ApiOperation("系统角色>修改")
    @OperateLog(module = "系统角色",name = "角色修改")
    @PreAuthorize("hasAuthority('sys:role:update')")
    public HttpResultUtil<Boolean> update(@RequestBody SysRoleDTO dto) {
        return new HttpResultUtil<Boolean>().ok(sysRoleApplicationService.updateRole(dto));
    }

    @DeleteMapping("/delete")
    @ApiOperation("系统角色>删除")
    @OperateLog(module = "系统角色",name = "角色删除")
    @PreAuthorize("hasAuthority('sys:role:delete')")
    public HttpResultUtil<Boolean> delete(@RequestParam("id") Long id) {
        return new HttpResultUtil<Boolean>().ok(sysRoleApplicationService.deleteRole(id));
    }

}
