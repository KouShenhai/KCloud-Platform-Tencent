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
package org.laokou.auth.server.domain.sys.repository.service.impl;
import cn.hutool.core.thread.ThreadUtil;
import org.laokou.auth.client.enums.UserStatusEnum;
import org.laokou.auth.client.user.UserDetail;
import org.laokou.auth.server.domain.sys.repository.service.SysDeptService;
import org.laokou.auth.server.domain.sys.repository.service.SysMenuService;
import org.laokou.auth.server.infrastructure.exception.CustomOAuth2Exception;
import org.laokou.common.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
/**
 * @author Kou Shenhai
 */
@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private SysUserServiceImpl sysUserService;

    @Autowired
    private SysMenuService sysMenuService;

    @Autowired
    private SysDeptService sysDeptService;

    private static final ThreadPoolExecutor executorService = new ThreadPoolExecutor(
            8,
            16,
            60,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(512),
            ThreadUtil.newNamedThreadFactory("laokou-auth-service",true),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetail userDetail = sysUserService.getUserDetail(username);
        if (userDetail == null) {
            throw new CustomOAuth2Exception(ErrorCode.ACCOUNT_NOT_EXIST);
        }
        CompletableFuture<UserDetail> c1 = CompletableFuture.supplyAsync(() -> sysDeptService.getDeptIds(userDetail))
                .thenApplyAsync(deptIds -> {
                    userDetail.setDeptIds(deptIds);
                    return userDetail;
                }, executorService);
        CompletableFuture<UserDetail> c2 = CompletableFuture.supplyAsync(() -> sysMenuService.getPermissionsList(userDetail))
                .thenApplyAsync(permissionList -> {
                    userDetail.setPermissionList(permissionList);
                    Set<GrantedAuthority> authorities = new HashSet<>(permissionList.size());
                    authorities.addAll(permissionList.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet()));
                    return userDetail;
                }, executorService);
        //等待所有任务都完成
        CompletableFuture.allOf(c1,c2);
        if (UserStatusEnum.DISABLE.ordinal() == userDetail.getStatus()) {
            userDetail.setEnabled(false);
        }
        return userDetail;
    }
}
