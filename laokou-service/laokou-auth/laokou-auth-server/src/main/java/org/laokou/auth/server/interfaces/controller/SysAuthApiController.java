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
package org.laokou.auth.server.interfaces.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.laokou.auth.client.constant.AuthConstant;
import org.laokou.auth.server.application.service.SysAuthApplicationService;
import org.laokou.auth.server.infrastructure.token.AuthToken;
import org.laokou.common.core.utils.HttpResultUtil;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
/**
 * 系统认证控制器
 * @author laokou
 */
@RestController
@Tag(name = "Sys Auth API",description = "系统认证API")
@RequiredArgsConstructor
public class SysAuthApiController {

    private final SysAuthApplicationService sysAuthApplicationService;

    @GetMapping("/oauth2/captcha")
    @Operation(summary = "系统认证>验证码",description = "系统认证>验证码")
    @Parameters(value = {
              @Parameter(name = AuthConstant.UUID,description = "唯一标识",example = "1111")
            , @Parameter(name = OAuth2ParameterNames.GRANT_TYPE,description = "认证类型",example = "password")
    })
    public HttpResultUtil<String> captcha(HttpServletRequest request) {
        return new HttpResultUtil<String>().ok(sysAuthApplicationService.captcha(request));
    }

    @PostMapping("/oauth2/login")
    @Operation(summary = "系统认证>登录",description = "系统认证>登录")
    @Parameters(value = {
              @Parameter(name = OAuth2ParameterNames.USERNAME,description = "用户名",example = "admin")
            , @Parameter(name = OAuth2ParameterNames.PASSWORD,description = "密码",example = "admin123")
            , @Parameter(name = OAuth2ParameterNames.CLIENT_ID,description = "认证编号",example = "auth-client")
            , @Parameter(name = OAuth2ParameterNames.CLIENT_SECRET,description = "认证密钥",example = "secret")
            , @Parameter(name = AuthConstant.UUID,description = "唯一标识",example = "1111")
            , @Parameter(name = AuthConstant.CAPTCHA,description = "验证码",example = "Bb6v")
            , @Parameter(name = OAuth2ParameterNames.SCOPE,description = "认证范围",example = "auth")
            , @Parameter(name = OAuth2ParameterNames.GRANT_TYPE,description = "认证类型",example = "password")
    })
    public HttpResultUtil<AuthToken> login(HttpServletRequest request) throws IOException {
        return new HttpResultUtil<AuthToken>().ok(sysAuthApplicationService.login(request));
    }

    @GetMapping("/oauth2/logout")
    @Operation(summary = "系统认证>注销",description = "系统认证>注销")
    public HttpResultUtil<Boolean> logout(HttpServletRequest request) {
        return new HttpResultUtil<Boolean>().ok(sysAuthApplicationService.logout(request));
    }

}
