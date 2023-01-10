/**
 * Copyright (c) 2022 KCloud-Platform-Tencent Authors. All Rights Reserved.
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
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.laokou.auth.client.constant.AuthConstant;
import org.laokou.auth.client.user.UserDetail;
import org.laokou.auth.server.infrastructure.log.LoginLogUtil;
import org.laokou.auth.server.infrastructure.server.AuthenticationServer;
import org.laokou.common.core.constant.Constant;
import org.laokou.common.swagger.exception.CustomException;
import org.laokou.common.swagger.exception.ErrorCode;
import org.laokou.auth.server.application.service.SysAuthApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.laokou.common.core.utils.*;
import org.laokou.redis.utils.RedisKeyUtil;
import org.laokou.redis.utils.RedisUtil;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.stereotype.Service;
import java.security.Principal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
/**
 * SpringSecurity最新版本更新
 * @author laokou
 */
@Service
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SysAuthApplicationServiceImpl implements SysAuthApplicationService {
    private final OAuth2AuthorizationService oAuth2AuthorizationService;
    private final RedisUtil redisUtil;

    private AuthenticationServer authenticationToken(HttpServletRequest request) {
        // 1.验证grantType
        String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
        if (StringUtil.isEmpty(grantType)) {
            throw new CustomException(ErrorCode.UNSUPPORTED_GRANT_TYPE);
        }
        try {
            String className = AuthenticationServer.class.getSimpleName();
            AuthenticationServer authenticationServer = SpringContextUtil.getBean(grantType + className, AuthenticationServer.class);
            // 2.验证账号/密码/验证码 或 手机号/验证码等等
            return authenticationServer;
        } catch (NoSuchBeanDefinitionException e ) {
            throw new CustomException(ErrorCode.UNSUPPORTED_GRANT_TYPE);
        }
    }

    @Override
    public String captcha(HttpServletRequest request) {
        return authenticationToken(request).captcha(request);
    }

    @Override
    public Boolean logout(HttpServletRequest request) {
        String token = request.getHeader(Constant.AUTHORIZATION_HEAD);
        if (StringUtil.isEmpty(token)) {
            return true;
        }
        token = token.substring(7);
        OAuth2Authorization oAuth2Authorization = oAuth2AuthorizationService.findByToken(token, OAuth2TokenType.ACCESS_TOKEN);
        if (oAuth2Authorization == null) {
            return true;
        }
        UserDetail userDetail = (UserDetail) ((UsernamePasswordAuthenticationToken) oAuth2Authorization.getAttribute(Principal.class.getName())).getPrincipal();
        // 清空用户信息
        oAuth2AuthorizationService.remove(oAuth2Authorization);
        // 清空用户key
        String userInfoKey = RedisKeyUtil.getUserInfoKey(token);
        redisUtil.delete(userInfoKey);
        Long userId = userDetail.getUserId();
        String resourceTreeKey = RedisKeyUtil.getResourceTreeKey(userId);
        redisUtil.delete(resourceTreeKey);
        return true;
    }

}
