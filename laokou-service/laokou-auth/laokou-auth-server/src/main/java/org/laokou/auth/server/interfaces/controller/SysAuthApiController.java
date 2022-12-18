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
package org.laokou.auth.server.interfaces.controller;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.laokou.auth.server.application.service.SysAuthApplicationService;
import org.laokou.common.core.utils.HttpResultUtil;
import org.springframework.web.bind.annotation.*;
/**
 * 系统认证控制器
 * @author Kou Shenhai
 */
@RestController
@Api(value = "系统认证API",protocols = "http",tags = "系统认证API")
@RequiredArgsConstructor
public class SysAuthApiController {

    private final SysAuthApplicationService sysAuthApplicationService;

    @GetMapping("/oauth2/captcha")
    @ApiOperation("系统认证>验证码")
    @ApiImplicitParam(name = "uuid",value = "唯一标识",required = true,paramType = "query",dataType = "String")
    public HttpResultUtil<String> captcha(HttpServletRequest request) {
        return new HttpResultUtil<String>().ok(sysAuthApplicationService.captcha(request));
    }

    @PostMapping("/oauth2/login")
    @ApiOperation("系统认证>登录")
    public void login(HttpServletRequest request, HttpServletResponse response) {
        sysAuthApplicationService.login(request,response);
    }

}
