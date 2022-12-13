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
import org.laokou.redis.utils.RedisKeyUtil;
import org.laokou.redis.utils.RedisUtil;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * 官方不再维护，过期类无法替换
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
    private final TokenStore tokenStore;
    private final RedisUtil redisUtil;
    private final LoginLogUtil loginLogUtil;
    private final ThreadPoolTaskExecutor authThreadPoolTaskExecutor;

    @SneakyThrows
    @Override
    public UserDetail login(String username, String password) {
        log.info("账号：{}，密码：{}",username,password);
        HttpServletRequest request = HttpContextUtil.getHttpServletRequest();
        UserDetail userDetail = sysUserService.getUserDetail(username);
        if (userDetail == null) {
            authThreadPoolTaskExecutor.execute(() -> {
                loginLogUtil.recordLogin(username, ResultStatusEnum.FAIL.ordinal(), MessageUtil.getMessage(ErrorCode.ACCOUNT_PASSWORD_ERROR),request);
            });
            throw new CustomOauth2Exception("" + ErrorCode.ACCOUNT_PASSWORD_ERROR,MessageUtil.getMessage(ErrorCode.ACCOUNT_PASSWORD_ERROR));
        }
        if(!PasswordUtil.matches(password, userDetail.getPassword())) {
            authThreadPoolTaskExecutor.execute(() -> {
                loginLogUtil.recordLogin(username, ResultStatusEnum.FAIL.ordinal(), MessageUtil.getMessage(ErrorCode.ACCOUNT_PASSWORD_ERROR),request);
            });
            throw new CustomOauth2Exception("" + ErrorCode.ACCOUNT_PASSWORD_ERROR,MessageUtil.getMessage(ErrorCode.ACCOUNT_PASSWORD_ERROR));
        }
        if (UserStatusEnum.DISABLE.ordinal() == userDetail.getStatus()) {
            authThreadPoolTaskExecutor.execute(() -> {
                loginLogUtil.recordLogin(username, ResultStatusEnum.FAIL.ordinal(), MessageUtil.getMessage(ErrorCode.ACCOUNT_DISABLE),request);
            });
            throw new CustomOauth2Exception("" + ErrorCode.ACCOUNT_DISABLE,MessageUtil.getMessage(ErrorCode.ACCOUNT_DISABLE));
        }
        CompletableFuture<UserDetail> c1 = CompletableFuture.supplyAsync(() -> sysDeptService.getDeptIds(userDetail))
                .thenApplyAsync(deptIds -> {
                    userDetail.setDeptIds(deptIds);
                    return userDetail;
                }, authThreadPoolTaskExecutor);
        CompletableFuture<UserDetail> c2 = CompletableFuture.supplyAsync(() -> sysMenuService.getPermissionsList(userDetail))
                .thenApplyAsync(permissionList -> {
                    userDetail.setPermissionList(permissionList);
                    return userDetail;
                }, authThreadPoolTaskExecutor);
        // 等待所有任务都完成
        CompletableFuture.allOf(c1,c2).join();
        if (CollectionUtils.isEmpty(userDetail.getPermissionList())) {
            loginLogUtil.recordLogin(username, ResultStatusEnum.FAIL.ordinal(), MessageUtil.getMessage(ErrorCode.NOT_PERMISSIONS),request);
            throw new CustomOauth2Exception("" + ErrorCode.NOT_PERMISSIONS,MessageUtil.getMessage(ErrorCode.NOT_PERMISSIONS));
        }
        authThreadPoolTaskExecutor.execute(() -> {
            // 登录成功
            loginLogUtil.recordLogin(userDetail.getUsername(), ResultStatusEnum.SUCCESS.ordinal(), AuthConstant.LOGIN_SUCCESS_MSG,request);
        });
        return userDetail;
    }

    @Override
    public void logout(HttpServletRequest request) {
        String token = getToken(request);
        OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(token);
        if (oAuth2AccessToken != null) {
            OAuth2Authentication oAuth2Authentication = tokenStore.readAuthentication(oAuth2AccessToken);
            UserDetail userDetail = (UserDetail) oAuth2Authentication.getPrincipal();
            Long userId = userDetail.getUserId();
            String resourceTreeKey = RedisKeyUtil.getResourceTreeKey(userId);
            redisUtil.delete(resourceTreeKey);
            tokenStore.removeAccessToken(oAuth2AccessToken);
            OAuth2RefreshToken refreshToken = oAuth2AccessToken.getRefreshToken();
            tokenStore.removeRefreshToken(refreshToken);
            tokenStore.removeAccessTokenUsingRefreshToken(refreshToken);
        }
    }

    @Override
    public void captcha(String uuid, HttpServletResponse response) throws IOException {
        if (StringUtil.isEmpty(uuid)) {
            throw new CustomOauth2Exception("" + ErrorCode.IDENTIFIER_NOT_NULL,MessageUtil.getMessage(ErrorCode.IDENTIFIER_NOT_NULL));
        }
        BufferedImage image = sysCaptchaService.createImage(uuid);
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(image,"jpg",out);
        out.close();
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
