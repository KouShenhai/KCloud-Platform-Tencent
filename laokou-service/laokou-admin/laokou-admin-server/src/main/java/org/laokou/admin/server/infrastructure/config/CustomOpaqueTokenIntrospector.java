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
package org.laokou.admin.server.infrastructure.config;
import lombok.RequiredArgsConstructor;
import org.laokou.admin.server.infrastructure.constant.CacheConstant;
import org.laokou.auth.client.user.UserDetail;
import org.laokou.common.swagger.exception.ErrorCode;
import org.laokou.common.core.utils.MessageUtil;
import org.laokou.redis.utils.RedisKeyUtil;
import org.laokou.redis.utils.RedisUtil;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.stereotype.Component;
import java.security.Principal;
/**
 * @author laokou
 */
@Component
@RequiredArgsConstructor
public class CustomOpaqueTokenIntrospector implements OpaqueTokenIntrospector {

    private final OAuth2AuthorizationService oAuth2AuthorizationService;
    private final RedisUtil redisUtil;
    private final CacheManager caffeineCacheManager;

    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        Cache userInfoCache = caffeineCacheManager.getCache(CacheConstant.TOKEN);
        UserDetail userDetail = userInfoCache.get(token, UserDetail.class);
        if (userDetail != null) {
            return userDetail;
        }
        String userInfoKey = RedisKeyUtil.getUserInfoKey(token);
        Object obj = redisUtil.get(userInfoKey);
        if (obj != null) {
            userDetail = (UserDetail) obj;
            userInfoCache.put(token,userDetail);
            return userDetail;
        }
        OAuth2Authorization oAuth2Authorization = oAuth2AuthorizationService.findByToken(token, OAuth2TokenType.ACCESS_TOKEN);
        if (oAuth2Authorization == null) {
            throw new InvalidBearerTokenException(MessageUtil.getMessage(ErrorCode.UNAUTHORIZED));
        }
        if (!oAuth2Authorization.getAccessToken().isActive()) {
            throw new InvalidBearerTokenException(MessageUtil.getMessage(ErrorCode.AUTHORIZATION_INVALID));
        }
        userDetail = (UserDetail) ((UsernamePasswordAuthenticationToken) oAuth2Authorization.getAttribute(Principal.class.getName())).getPrincipal();
        redisUtil.set(userInfoKey,userDetail,RedisUtil.HOUR_ONE_EXPIRE);
        return userDetail;
    }
}
