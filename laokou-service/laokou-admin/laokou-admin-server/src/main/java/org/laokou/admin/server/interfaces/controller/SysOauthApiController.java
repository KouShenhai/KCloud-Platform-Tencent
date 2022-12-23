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
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.laokou.admin.server.application.service.SysOauthApplicationService;
import org.laokou.admin.client.dto.SysOauthDTO;
import org.laokou.admin.server.interfaces.qo.SysOauthQo;
import org.laokou.admin.client.vo.SysOauthVO;
import org.laokou.common.core.utils.HttpResultUtil;
import org.laokou.admin.server.infrastructure.annotation.OperateLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
/**
 * @author laokou
 */
@RestController
@Api(value = "系统认证API",protocols = "http",tags = "系统认证API")
@RequestMapping("/sys/oauth/api")
public class SysOauthApiController {

    @Autowired
    private SysOauthApplicationService sysOauthApplicationService;

    @PostMapping("/query")
    @ApiOperation("系统认证>查询")
    @PreAuthorize("hasAuthority('sys:oauth:query')")
    public HttpResultUtil<IPage<SysOauthVO>> query(@RequestBody SysOauthQo qo) {
        return new HttpResultUtil<IPage<SysOauthVO>>().ok(sysOauthApplicationService.queryOauthPage(qo));
    }

    @GetMapping(value = "/detail")
    @ApiOperation("系统认证>详情")
    public HttpResultUtil<SysOauthVO> detail(@RequestParam("id") Long id) {
        return new HttpResultUtil<SysOauthVO>().ok(sysOauthApplicationService.getOauthById(id));
    }

    @PostMapping(value = "/insert")
    @ApiOperation("系统认证>新增")
    @OperateLog(module = "系统认证",name = "认证新增")
    @PreAuthorize("hasAuthority('sys:oauth:insert')")
    public HttpResultUtil<Boolean> insert(@RequestBody SysOauthDTO dto) {
        return new HttpResultUtil<Boolean>().ok(sysOauthApplicationService.insertOauth(dto));
    }

    @PutMapping(value = "/update")
    @ApiOperation("系统认证>修改")
    @OperateLog(module = "系统认证",name = "认证修改")
    @PreAuthorize("hasAuthority('sys:oauth:update')")
    public HttpResultUtil<Boolean> update(@RequestBody SysOauthDTO dto) {
        return new HttpResultUtil<Boolean>().ok(sysOauthApplicationService.updateOauth(dto));
    }

    @DeleteMapping(value = "/delete")
    @ApiOperation("系统认证>删除")
    @OperateLog(module = "系统认证",name = "认证删除")
    @PreAuthorize("hasAuthority('sys:oauth:delete')")
    public HttpResultUtil<Boolean> delete(@RequestParam("id") Long id) {
        return new HttpResultUtil<Boolean>().ok(sysOauthApplicationService.deleteOauth(id));
    }

}
