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
package org.laokou.ump.server.provider;
import org.laokou.auth.client.user.BaseUserVO;
import org.laokou.auth.client.user.UserDetail;
import org.laokou.ump.server.exception.RenOAuth2Exception;
import org.laokou.ump.server.service.SysMenuService;
import org.laokou.ump.server.service.SysUserService;
import org.laokou.ump.server.utils.AuthUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.laokou.common.enums.SuperAdminEnum;
import org.laokou.common.exception.ErrorCode;
import org.laokou.common.utils.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;
/**
 * @author Kou Shenhai
 * @version 1.0
 * @date 2021/4/16 0016 上午 9:45
 */
@Component
@Slf4j
public class AuthAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private SysMenuService sysMenuService;

    @Autowired
    private SysUserService sysUserService;

    @SneakyThrows
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String code = authentication.getName();
        String password = (String)authentication.getCredentials();
        log.info("code：{}",code);
        String accessToken = authUtil.getAccessToken(code);
        BaseUserVO vo = authUtil.getUerInfo(accessToken);
        if (null == vo) {
            throw new RenOAuth2Exception(ErrorCode.ACCOUNT_NOT_EXIST, MessageUtil.getMessage(ErrorCode.ACCOUNT_NOT_EXIST));
        }
        Set<GrantedAuthority> authorities = new HashSet<>();
        UserDetail userDetail = sysUserService.getUserDetail(vo.getUserId(), vo.getUsername());
        List<String> permissionList;
        if(SuperAdminEnum.YES.ordinal() == userDetail.getSuperAdmin()) {
            permissionList = sysMenuService.getPermissionsList();
        } else {
            permissionList = sysMenuService.getPermissionsListByUserId(vo.getUserId());
        }
        authorities.addAll(permissionList.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet()));
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(authentication.getDetails(),password,authorities);
        return authenticationToken;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
