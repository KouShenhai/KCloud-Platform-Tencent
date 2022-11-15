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
package org.laokou.admin.server.interfaces.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.laokou.admin.server.application.service.SysLogApplicationService;
import org.laokou.admin.server.interfaces.qo.LoginLogQo;
import org.laokou.admin.server.interfaces.qo.SysOperateLogQo;
import org.laokou.admin.client.vo.SysLoginLogVO;
import org.laokou.admin.client.vo.SysOperateLogVO;
import org.laokou.common.core.utils.HttpResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
/**
 * 系统日志控制器
 * @author Kou Shenhai
 */
@RestController
@Api(value = "系统日志API",protocols = "http",tags = "系统日志API")
@RequestMapping("/sys/log/api")
public class SysLogApiController {

    @Autowired
    private SysLogApplicationService sysLogApplicationService;

    @PostMapping(value = "/operate/query")
    @ApiOperation("系统日志>操作日志>查询")
    @PreAuthorize("hasAuthority('sys:log:operate:query')")
    public HttpResultUtil<IPage<SysOperateLogVO>> queryOperateLog(@RequestBody SysOperateLogQo qo) {
        return new HttpResultUtil<IPage<SysOperateLogVO>>().ok(sysLogApplicationService.queryOperateLogPage(qo));
    }

    @PostMapping(value = "/login/query")
    @ApiOperation("系统日志>登录日志>查询")
    @PreAuthorize("hasAuthority('sys:log:login:query')")
    public HttpResultUtil<IPage<SysLoginLogVO>> queryLoginLog(@RequestBody LoginLogQo qo) {
        return new HttpResultUtil<IPage<SysLoginLogVO>>().ok(sysLogApplicationService.queryLoginLogPage(qo));
    }

}
