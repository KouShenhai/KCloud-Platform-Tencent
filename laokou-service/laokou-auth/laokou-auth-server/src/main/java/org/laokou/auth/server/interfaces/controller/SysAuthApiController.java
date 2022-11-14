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
package org.laokou.auth.server.interfaces.controller;
import io.swagger.annotations.Api;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
/**
 * 系统认证控制器
 * @author Kou Shenhai
 */
@RestController
@Api(value = "系统认证API",protocols = "http",tags = "系统认证API")
public class SysAuthApiController {

    @GetMapping("index")
    public String index() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encodePW = bCryptPasswordEncoder.encode("123");
        System.out.println(encodePW);
        boolean matches = bCryptPasswordEncoder.matches("123", encodePW);
        System.out.println("比较结果：" + matches);
        return "success";
    }


//    @Autowired
//    private SysAuthApplicationService sysAuthApplicationService;
//
//    @GetMapping("/sys/auth/api/captcha")
//    @ApiOperation("系统认证>验证码")
//    @ApiImplicitParam(name = "uuid",value = "唯一标识",required = true,paramType = "query",dataType = "String")
//    public void captcha(@RequestParam(Constant.UUID)String uuid, HttpServletResponse response) throws IOException {
//        sysAuthApplicationService.captcha(uuid,response);
//    }
//
//    @GetMapping("/sys/auth/api/logout")
//    @ApiOperation("系统认证>退出登录")
//    public Mono<Void> logout(HttpServletRequest request) {
//        sysAuthApplicationService.logout(request);
//        return Mono.empty();
//    }

}
