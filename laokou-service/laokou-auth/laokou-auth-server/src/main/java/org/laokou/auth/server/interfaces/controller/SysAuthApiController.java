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
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.laokou.auth.server.application.service.SysAuthApplicationService;
import org.laokou.auth.server.infrastructure.token.AuthToken;
import org.laokou.common.core.utils.HttpResultUtil;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
/**
 * 系统认证控制器
 * @author laokou
 */
@RestController
//@Api(value = "系统认证API",protocols = "http",tags = "系统认证API")
@RequiredArgsConstructor
public class SysAuthApiController {

    private final SysAuthApplicationService sysAuthApplicationService;

    @GetMapping("/oauth2/captcha")
//    @ApiOperation("系统认证>验证码")
    public HttpResultUtil<String> captcha(HttpServletRequest request) {
        return new HttpResultUtil<String>().ok(sysAuthApplicationService.captcha(request));
    }

    @PostMapping("/oauth2/login")
//    @ApiOperation("系统认证>登录")
    public HttpResultUtil<AuthToken> login(HttpServletRequest request) throws IOException {
        return new HttpResultUtil<AuthToken>().ok(sysAuthApplicationService.login(request));
    }

}
