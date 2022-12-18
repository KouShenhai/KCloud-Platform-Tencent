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
package org.laokou.auth.server.infrastructure.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.laokou.auth.client.user.UserDetail;
import org.laokou.auth.server.application.service.SysAuthApplicationService;
import org.laokou.common.core.utils.JacksonUtil;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Kou Shenhai
 * @version 1.0
 * @date 2021/4/16 0016 上午 9:45
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class Oauth2PasswordAuthenticationProvider implements AuthenticationProvider {

    private final SysAuthApplicationService sysAuthApplicationService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getPrincipal().toString();
        String password = authentication.getCredentials().toString();
        UserDetail userDetail = sysAuthApplicationService.login(username,password);
        List<String> permissionList = userDetail.getPermissionList();
        Set<GrantedAuthority> authorities = new HashSet<>(permissionList.size());
        authorities.addAll(permissionList.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet()));
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetail, authentication.getName(), authorities);
        // 获取当前认证
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("获取信息：{}", JacksonUtil.toJsonStr(principal));
        return usernamePasswordAuthenticationToken;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
