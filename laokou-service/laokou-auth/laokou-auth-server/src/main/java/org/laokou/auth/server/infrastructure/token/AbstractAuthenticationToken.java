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
package org.laokou.auth.server.infrastructure.token;
import jakarta.servlet.http.HttpServletRequest;
import org.laokou.auth.client.user.UserDetail;
import org.laokou.auth.server.domain.sys.repository.service.SysDeptService;
import org.laokou.auth.server.domain.sys.repository.service.SysMenuService;
import org.laokou.auth.server.domain.sys.repository.service.impl.SysUserServiceImpl;
import org.laokou.auth.server.infrastructure.log.LoginLogUtil;
import org.laokou.common.core.enums.ResultStatusEnum;
import org.laokou.common.core.enums.SuperAdminEnum;
import org.laokou.common.core.exception.CustomException;
import org.laokou.common.core.exception.ErrorCode;
import org.laokou.common.core.utils.MessageUtil;
import org.laokou.redis.utils.RedisKeyUtil;
import org.laokou.redis.utils.RedisUtil;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
/**
 * 共享用户信息接口
 * 继承该类
 *  $ 密码登录
 *  @ 验证码登录
 *  # 邮件登录
 * @author laokou
 */
public abstract class AbstractAuthenticationToken implements AuthenticationToken, UserDetailsService {

    protected final SysUserServiceImpl sysUserService;
    protected final SysMenuService sysMenuService;
    protected final SysDeptService sysDeptService;
    protected final LoginLogUtil loginLogUtil;
    protected final RedisUtil redisUtil;
    protected final PasswordEncoder passwordEncoder;

    public AbstractAuthenticationToken(
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

    @Override
    public UserDetails loadUserByUsername(String loginName) throws UsernameNotFoundException {
        String userInfoKey = RedisKeyUtil.getUserInfoKey(loginName);
        Object obj = redisUtil.get(userInfoKey);
        if (obj != null) {
            return (UserDetail) obj;
        }
        UserDetail userDetail = sysUserService.getUserDetail(loginName);
        Long userId = userDetail.getUserId();
        Integer superAdmin = userDetail.getSuperAdmin();
        userDetail.setPermissionList(sysMenuService.getPermissionsList(superAdmin,userId));
        userDetail.setDeptIds(sysDeptService.getDeptIds(superAdmin,userId));
        redisUtil.set(userInfoKey,userDetail,RedisUtil.HOUR_ONE_EXPIRE);
        return userDetail;
    }

    protected void checkUserInfo(UserDetail userDetail, String loginName, String password, HttpServletRequest request) {
        AuthorizationGrantType grantType = getGrantType();
        String loginType = grantType.getValue();
        if (userDetail == null) {
            loginLogUtil.recordLogin(loginName,loginType, ResultStatusEnum.FAIL.ordinal(), MessageUtil.getMessage(ErrorCode.ACCOUNT_PASSWORD_ERROR),request);
            throw new CustomException(ErrorCode.ACCOUNT_PASSWORD_ERROR);
        }
        if (PasswordAuthenticationToken.GRANT_TYPE.equals(grantType)) {
            // 验证密码
            String clientPassword = userDetail.getPassword();
            if (!passwordEncoder.matches(password, clientPassword)) {
                loginLogUtil.recordLogin(loginName, loginType, ResultStatusEnum.FAIL.ordinal(), MessageUtil.getMessage(ErrorCode.ACCOUNT_PASSWORD_ERROR), request);
                throw new CustomException(ErrorCode.ACCOUNT_PASSWORD_ERROR);
            }
        }
        // 是否锁定
        if (!userDetail.isEnabled()) {
            loginLogUtil.recordLogin(loginName,loginType, ResultStatusEnum.FAIL.ordinal(), MessageUtil.getMessage(ErrorCode.ACCOUNT_DISABLE),request);
            throw new CustomException(ErrorCode.ACCOUNT_DISABLE);
        }
        Long userId = userDetail.getUserId();
        Integer superAdmin = userDetail.getSuperAdmin();
        // 权限标识列表
        if (SuperAdminEnum.YES.ordinal() != superAdmin && sysMenuService.getPermissionsCount(userId) < 1) {
            loginLogUtil.recordLogin(loginName,loginType, ResultStatusEnum.FAIL.ordinal(), MessageUtil.getMessage(ErrorCode.NOT_PERMISSIONS),request);
            throw new CustomException(ErrorCode.NOT_PERMISSIONS);
        }
    }

}
