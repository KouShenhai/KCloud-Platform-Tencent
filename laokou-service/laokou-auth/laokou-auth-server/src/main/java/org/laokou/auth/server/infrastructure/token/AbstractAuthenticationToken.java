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
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.laokou.auth.client.user.UserDetail;
import org.laokou.auth.server.domain.sys.repository.service.SysDeptService;
import org.laokou.auth.server.domain.sys.repository.service.SysMenuService;
import org.laokou.auth.server.domain.sys.repository.service.impl.SysUserServiceImpl;
import org.laokou.auth.server.infrastructure.log.LoginLogUtil;
import org.laokou.common.core.exception.CustomException;
import org.laokou.common.core.exception.ErrorCode;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.concurrent.TimeUnit;

/**
 * 共享用户信息接口
 * 继承该类
 *  $ 密码登录
 *  @ 验证码登录
 *  # 邮件登录
 * @author Kou Shenhai
 */
public abstract class AbstractAuthenticationToken implements AuthenticationToken, UserDetailsService {

    protected final SysUserServiceImpl sysUserService;
    protected final SysMenuService sysMenuService;
    protected final SysDeptService sysDeptService;
    protected final LoginLogUtil loginLogUtil;
    protected static final Cache<String,UserDetail> caffeineCache;

    static {
        caffeineCache = Caffeine.newBuilder().initialCapacity(200)
                .expireAfterAccess(2, TimeUnit.MINUTES)
                .maximumSize(4028)
                .build();
    }

    public AbstractAuthenticationToken(
      SysUserServiceImpl sysUserService
    , SysMenuService sysMenuService
    , SysDeptService sysDeptService
    , LoginLogUtil loginLogUtil) {
        this.sysDeptService = sysDeptService;
        this.sysMenuService = sysMenuService;
        this.loginLogUtil = loginLogUtil;
        this.sysUserService = sysUserService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetail userDetail = caffeineCache.getIfPresent(username);
        if (null == userDetail) {
            throw new CustomException(ErrorCode.ACCOUNT_NOT_EXIST);
        }
        // 移除
        caffeineCache.invalidate(username);
        return userDetail;
    }

}
