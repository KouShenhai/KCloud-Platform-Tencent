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
import org.laokou.admin.server.application.service.SysLogApplicationService;
import org.laokou.admin.server.interfaces.qo.LoginLogQO;
import org.laokou.admin.server.interfaces.qo.SysOperateLogQO;
import org.laokou.admin.client.vo.SysLoginLogVO;
import org.laokou.admin.client.vo.SysOperateLogVO;
import org.laokou.common.dto.LoginLogDTO;
import org.laokou.common.dto.OperateLogDTO;
import org.laokou.common.utils.HttpResultUtil;
import org.laokou.security.annotation.PreAuthorize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping(value = "/operate/insert")
    @ApiOperation("系统日志>操作日志>新增")
    public HttpResultUtil<Boolean> insertOperateLog(@RequestBody OperateLogDTO dto) {
        return new HttpResultUtil<Boolean>().ok(sysLogApplicationService.insertOperateLog(dto));
    }

    @PostMapping(value = "/operate/query")
    @PreAuthorize("sys:log:operate:query")
    @ApiOperation("系统日志>操作日志>查询")
    public HttpResultUtil<IPage<SysOperateLogVO>> queryOperateLog(@RequestBody SysOperateLogQO qo) {
        return new HttpResultUtil<IPage<SysOperateLogVO>>().ok(sysLogApplicationService.queryOperateLogPage(qo));
    }

    @PostMapping(value = "/login/insert")
    @ApiOperation("系统日志>登录日志>新增")
    public HttpResultUtil<Boolean> insertLoginLog(@RequestBody LoginLogDTO dto) {
        return new HttpResultUtil<Boolean>().ok(sysLogApplicationService.insertLoginLog(dto));
    }

    @PostMapping(value = "/login/query")
    @ApiOperation("系统日志>登录日志>查询")
    @PreAuthorize("sys:log:login:query")
    public HttpResultUtil<IPage<SysLoginLogVO>> queryLoginLog(@RequestBody LoginLogQO qo) {
        return new HttpResultUtil<IPage<SysLoginLogVO>>().ok(sysLogApplicationService.queryLoginLogPage(qo));
    }

}
