/**
 * Copyright (c) 2022 KCloud-Platform-Official Authors. All Rights Reserved.
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

package org.laokou.auth.server.infrastructure.token;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.laokou.auth.server.domain.sys.repository.service.SysDeptService;
import org.laokou.auth.server.domain.sys.repository.service.SysMenuService;
import org.laokou.auth.server.domain.sys.repository.service.impl.SysUserServiceImpl;
import org.laokou.auth.server.infrastructure.log.LoginLogUtil;
import org.laokou.redis.utils.RedisUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.stereotype.Component;

/**
 * 邮箱登录
 * @author Kou Shenhai
 */
@Component
@Slf4j
public class EmailAuthenticationToken extends AbstractAuthenticationToken{

    public static final String GRANT_TYPE = "email";

    public EmailAuthenticationToken(SysUserServiceImpl sysUserService
            , SysMenuService sysMenuService
            , SysDeptService sysDeptService
            , LoginLogUtil loginLogUtil
            , RedisUtil redisUtil
            , PasswordEncoder passwordEncoder) {
        super(sysUserService, sysMenuService, sysDeptService,loginLogUtil,redisUtil,passwordEncoder);
    }

    @Override
    public AuthorizationGrantType getGrantType() {
        return new AuthorizationGrantType(GRANT_TYPE);
    }

    @Override
    public UsernamePasswordAuthenticationToken login(HttpServletRequest request) {
        // 验证用户信息
        // 必须调用该方法
        // super.checkUserInfo(userDetail,username,password,request);
        return null;
    }

    @Override
    public String captcha(HttpServletRequest request) {
        return null;
    }
}
