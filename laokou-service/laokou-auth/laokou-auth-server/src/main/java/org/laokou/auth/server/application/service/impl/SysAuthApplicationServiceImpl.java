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
package org.laokou.auth.server.application.service.impl;
import com.wf.captcha.GifCaptcha;
import com.wf.captcha.base.Captcha;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.collections.CollectionUtils;
import org.laokou.auth.client.enums.UserStatusEnum;
import org.laokou.auth.client.constant.AuthConstant;
import org.laokou.auth.server.infrastructure.log.LoginLogUtil;
import org.laokou.common.core.constant.Constant;
import org.laokou.common.core.enums.ResultStatusEnum;
import org.laokou.common.core.exception.ErrorCode;
import org.laokou.common.core.password.PasswordUtil;
import org.laokou.auth.client.user.UserDetail;
import org.laokou.auth.server.application.service.SysAuthApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.laokou.auth.server.domain.sys.repository.service.SysCaptchaService;
import org.laokou.auth.server.domain.sys.repository.service.SysDeptService;
import org.laokou.auth.server.domain.sys.repository.service.SysMenuService;
import org.laokou.auth.server.domain.sys.repository.service.impl.SysUserServiceImpl;
import org.laokou.auth.server.infrastructure.exception.CustomOauth2Exception;
import org.laokou.common.core.utils.*;
import org.springframework.stereotype.Service;
import java.awt.*;
import java.util.List;
/**
 * SpringSecurity最新版本更新
 * @author Kou Shenhai
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SysAuthApplicationServiceImpl implements SysAuthApplicationService {

    private final SysUserServiceImpl sysUserService;
    private final SysMenuService sysMenuService;
    private final SysDeptService sysDeptService;
    private final SysCaptchaService sysCaptchaService;
    private final LoginLogUtil loginLogUtil;

    @SneakyThrows
    @Override
    public UserDetail login(String username, String password) {
        log.info("账号：{}，密码：{}",username,password);
        HttpServletRequest request = HttpContextUtil.getHttpServletRequest();
        UserDetail userDetail = sysUserService.getUserDetail(username);
        if (userDetail == null) {
            loginLogUtil.recordLogin(username, ResultStatusEnum.FAIL.ordinal(), MessageUtil.getMessage(ErrorCode.ACCOUNT_PASSWORD_ERROR),request);
            throw new CustomOauth2Exception("" + ErrorCode.ACCOUNT_PASSWORD_ERROR,MessageUtil.getMessage(ErrorCode.ACCOUNT_PASSWORD_ERROR));
        }
        if(!PasswordUtil.matches(password, userDetail.getPassword())) {
            loginLogUtil.recordLogin(username, ResultStatusEnum.FAIL.ordinal(), MessageUtil.getMessage(ErrorCode.ACCOUNT_PASSWORD_ERROR),request);
            throw new CustomOauth2Exception("" + ErrorCode.ACCOUNT_PASSWORD_ERROR,MessageUtil.getMessage(ErrorCode.ACCOUNT_PASSWORD_ERROR));
        }
        if (UserStatusEnum.DISABLE.ordinal() == userDetail.getStatus()) {
            loginLogUtil.recordLogin(username, ResultStatusEnum.FAIL.ordinal(), MessageUtil.getMessage(ErrorCode.ACCOUNT_DISABLE),request);
            throw new CustomOauth2Exception("" + ErrorCode.ACCOUNT_DISABLE,MessageUtil.getMessage(ErrorCode.ACCOUNT_DISABLE));
        }
        List<String> permissionsList = sysMenuService.getPermissionsList(userDetail);
        if (CollectionUtils.isEmpty(permissionsList)) {
            loginLogUtil.recordLogin(username, ResultStatusEnum.FAIL.ordinal(), MessageUtil.getMessage(ErrorCode.NOT_PERMISSIONS),request);
            throw new CustomOauth2Exception("" + ErrorCode.NOT_PERMISSIONS,MessageUtil.getMessage(ErrorCode.NOT_PERMISSIONS));
        }
        List<Long> deptIds = sysDeptService.getDeptIds(userDetail);
        userDetail.setDeptIds(deptIds);
        userDetail.setPermissionList(permissionsList);
        // 登录成功
        loginLogUtil.recordLogin(userDetail.getUsername(), ResultStatusEnum.SUCCESS.ordinal(), AuthConstant.LOGIN_SUCCESS_MSG,request);
        return userDetail;
    }

    @Override
    public void logout(HttpServletRequest request) {
//        String token = getToken(request);
//        OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(token);
//        if (oAuth2AccessToken != null) {
//            OAuth2Authentication oAuth2Authentication = tokenStore.readAuthentication(oAuth2AccessToken);
//            UserDetail userDetail = (UserDetail) oAuth2Authentication.getPrincipal();
//            Long userId = userDetail.getUserId();
//            String resourceTreeKey = RedisKeyUtil.getResourceTreeKey(userId);
//            redisUtil.delete(resourceTreeKey);
//            tokenStore.removeAccessToken(oAuth2AccessToken);
//            OAuth2RefreshToken refreshToken = oAuth2AccessToken.getRefreshToken();
//            tokenStore.removeRefreshToken(refreshToken);
//            tokenStore.removeAccessTokenUsingRefreshToken(refreshToken);
//        }
    }

    @Override
    public String captcha(String uuid) {
        if (StringUtil.isEmpty(uuid)) {
            throw new CustomOauth2Exception("" + ErrorCode.IDENTIFIER_NOT_NULL,MessageUtil.getMessage(ErrorCode.IDENTIFIER_NOT_NULL));
        }
        // 三个参数分别为宽、高、位数
        Captcha captcha = new GifCaptcha(130, 48, 4);
        // 设置字体，有默认字体，可以不用设置
        captcha.setFont(new Font("Verdana", Font.PLAIN, 32));
        // 设置类型，纯数字、纯字母、字母数字混合
        captcha.setCharType(Captcha.TYPE_DEFAULT);
        sysCaptchaService.setCode(uuid,captcha.text());
        return captcha.toBase64();
    }

    private static String getToken(HttpServletRequest request){
        //从header中获取token
        String token = request.getHeader(Constant.AUTHORIZATION_HEAD);
        //如果header中不存在Authorization，则从参数中获取Authorization
        if(StringUtil.isEmpty(token)){
            token = request.getParameter(Constant.AUTHORIZATION_HEAD);
        }
        if (StringUtil.isEmpty(token)) {
            return token;
        }
        int index = token.indexOf(Constant.BEARER);
        if (index == -1) {
            return token.trim();
        }
        return token.substring(index + 7).trim();
    }

}
