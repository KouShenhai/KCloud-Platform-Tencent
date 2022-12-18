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
import lombok.RequiredArgsConstructor;
import org.laokou.auth.client.user.UserDetail;
import org.laokou.auth.server.domain.sys.repository.service.SysDeptService;
import org.laokou.auth.server.domain.sys.repository.service.SysMenuService;
import org.laokou.auth.server.domain.sys.repository.service.impl.SysUserServiceImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
/**
 * 共享用户信息接口
 * 继承该类
 *  $ 密码登录
 *  @ 手机登录
 *  # 邮件登录
 * @author Kou Shenhai
 */
@RequiredArgsConstructor
public abstract class AbstractAuthenticationToken implements AuthenticationToken, UserDetailsService {

    final SysUserServiceImpl sysUserService;
    final SysMenuService sysMenuService;
    final SysDeptService sysDeptService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetail userDetail = sysUserService.getUserDetail(username);
        Long userId = userDetail.getUserId();
        Integer superAdmin = userDetail.getSuperAdmin();
        userDetail.setDeptIds(sysDeptService.getDeptIds(superAdmin,userId));
        userDetail.setPermissionList(sysMenuService.getPermissionsList(superAdmin,userId));
        return userDetail;
    }

}
