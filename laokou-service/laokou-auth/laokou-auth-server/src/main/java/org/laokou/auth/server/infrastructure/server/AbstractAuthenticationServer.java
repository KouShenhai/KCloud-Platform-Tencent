/**
 * Copyright (c) 2022 KCloud-Platform-Tencent Authors. All Rights Reserved.
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
package org.laokou.auth.server.infrastructure.server;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.collections.CollectionUtils;
import org.laokou.auth.client.user.UserDetail;
import org.laokou.auth.server.domain.sys.repository.service.SysDeptService;
import org.laokou.auth.server.domain.sys.repository.service.SysMenuService;
import org.laokou.auth.server.domain.sys.repository.service.impl.SysUserServiceImpl;
import org.laokou.auth.server.infrastructure.log.LoginLogUtil;
import org.laokou.common.core.enums.ResultStatusEnum;
import org.laokou.common.swagger.exception.CustomException;
import org.laokou.common.swagger.exception.ErrorCode;
import org.laokou.common.core.utils.HttpContextUtil;
import org.laokou.common.core.utils.MessageUtil;
import org.laokou.redis.utils.RedisUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import java.util.List;

/**
 * 共享用户信息接口
 * 继承该类
 *  $ 密码登录
 *  @ 验证码登录
 *  # 邮件登录
 * @author laokou
 */
public abstract class AbstractAuthenticationServer implements AuthenticationServer {

    protected final SysUserServiceImpl sysUserService;
    protected final SysMenuService sysMenuService;
    protected final SysDeptService sysDeptService;
    protected final LoginLogUtil loginLogUtil;
    protected final RedisUtil redisUtil;
    protected final PasswordEncoder passwordEncoder;

    public AbstractAuthenticationServer(
      SysUserServiceImpl sysUserService
    , SysMenuService sysMenuService
    , SysDeptService sysDeptService
    , LoginLogUtil loginLogUtil
    , RedisUtil redisUtil
    , PasswordEncoder passwordEncoder) {
        this.sysDeptService = sysDeptService;
        this.sysMenuService = sysMenuService;
        this.loginLogUtil = loginLogUtil;
        this.sysUserService = sysUserService;
        this.redisUtil = redisUtil;
        this.passwordEncoder = passwordEncoder;
    }

}
