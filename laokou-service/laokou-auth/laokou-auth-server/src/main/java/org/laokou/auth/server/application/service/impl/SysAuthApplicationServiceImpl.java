/**
 * Copyright (c) 2022 KCloud-Platform-Official Authors. All Rights Reserved.
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
package org.laokou.auth.server.application.service.impl;
import cn.hutool.core.thread.ThreadUtil;
import lombok.SneakyThrows;
import org.laokou.auth.client.enums.UserStatusEnum;
import org.laokou.auth.client.password.PasswordUtil;
import org.laokou.auth.client.password.RsaCoder;
import org.laokou.auth.client.user.UserDetail;
import org.laokou.auth.server.application.service.SysAuthApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.laokou.auth.server.domain.sys.repository.service.SysCaptchaService;
import org.laokou.auth.server.domain.sys.repository.service.SysDeptService;
import org.laokou.auth.server.domain.sys.repository.service.SysMenuService;
import org.laokou.auth.server.domain.sys.repository.service.impl.SysUserServiceImpl;
import org.laokou.auth.server.infrastructure.exception.CustomOAuth2Exception;
import org.laokou.common.constant.Constant;
import org.laokou.common.exception.ErrorCode;
import org.laokou.common.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import javax.crypto.BadPaddingException;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
/**
 * @author Kou Shenhai
 */
@Service
@Slf4j
public class SysAuthApplicationServiceImpl implements SysAuthApplicationService {

    @Autowired
    private SysUserServiceImpl sysUserService;

    @Autowired
    private SysMenuService sysMenuService;

    @Autowired
    private SysDeptService sysDeptService;

    @Autowired
    private SysCaptchaService sysCaptchaService;

    @Autowired
    private TokenStore tokenStore;

    private static final ThreadPoolExecutor executorService = new ThreadPoolExecutor(
            8,
            16,
            60,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(512),
            ThreadUtil.newNamedThreadFactory("laokou-auth-service",true),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    @SneakyThrows
    @Override
    public UserDetail login(String username, String password) {
        //SRA私钥解密
        try {
            username = RsaCoder.decryptByPrivateKey(username);
            password = RsaCoder.decryptByPrivateKey(password);
        } catch (BadPaddingException e) {
            throw new CustomOAuth2Exception(ErrorCode.DECRYPT_FAIL);
        }
        UserDetail userDetail = sysUserService.getUserDetail(username);
        if (userDetail == null) {
            throw new CustomOAuth2Exception(ErrorCode.ACCOUNT_PASSWORD_ERROR);
        }
        if(!PasswordUtil.matches(password, userDetail.getPassword())) {
            throw new CustomOAuth2Exception(ErrorCode.ACCOUNT_PASSWORD_ERROR);
        }
        CompletableFuture<UserDetail> c1 = CompletableFuture.supplyAsync(() -> sysDeptService.getDeptIds(userDetail))
                .thenApplyAsync(deptIds -> {
                    userDetail.setDeptIds(deptIds);
                    return userDetail;
                }, executorService);
        CompletableFuture<UserDetail> c2 = CompletableFuture.supplyAsync(() -> sysMenuService.getPermissionsList(userDetail))
                .thenApplyAsync(permissionList -> {
                    userDetail.setPermissionList(permissionList);
                    return userDetail;
                }, executorService);
        //等待所有任务都完成
        CompletableFuture.allOf(c1,c2).join();
        if (UserStatusEnum.DISABLE.ordinal() == userDetail.getStatus()) {
            throw new CustomOAuth2Exception(ErrorCode.ACCOUNT_DISABLE);
        }
        return userDetail;
    }

    @Override
    public void logout(HttpServletRequest request) {
        String token = getToken(request);
        OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(token);
        if (oAuth2AccessToken != null) {
            tokenStore.removeAccessToken(oAuth2AccessToken);
            OAuth2RefreshToken refreshToken = oAuth2AccessToken.getRefreshToken();
            tokenStore.removeRefreshToken(refreshToken);
            tokenStore.removeAccessTokenUsingRefreshToken(refreshToken);
        }
    }

    @Override
    public void captcha(String uuid, HttpServletResponse response) throws IOException {
        if (StringUtil.isEmpty(uuid)) {
            throw new CustomOAuth2Exception(ErrorCode.IDENTIFIER_NOT_NULL);
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
