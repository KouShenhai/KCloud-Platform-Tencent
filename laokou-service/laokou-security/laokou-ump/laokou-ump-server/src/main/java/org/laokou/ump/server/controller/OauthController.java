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
package org.laokou.ump.server.controller;
import com.github.benmanes.caffeine.cache.Cache;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.laokou.auth.client.user.SecurityUser;
import org.laokou.auth.client.user.UserDetail;
import org.laokou.auth.client.utils.TokenUtil;
import org.laokou.auth.client.vo.UserInfoVO;
import org.laokou.common.constant.Constant;
import org.laokou.common.exception.ErrorCode;
import org.laokou.common.utils.HttpResultUtil;
import org.laokou.common.utils.StringUtil;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;

/**
     * @author Kou Shenhai
 */
@RestController
@RequiredArgsConstructor
@Api(value = "认证授权API",protocols = "http",tags = "认证授权API")
public class OauthController {

    private final TokenStore tokenStore;

    private final Cache<String,UserInfoVO> caffeineCache;

    @GetMapping("/oauth/userInfo")
    @ApiOperation("认证授权>用户信息")
    public HttpResultUtil<UserInfoVO> userInfo(HttpServletRequest request) {
        String token = SecurityUser.getToken(request);
        if (StringUtil.isEmpty(token)) {
            return new HttpResultUtil<UserInfoVO>().error(ErrorCode.UNAUTHORIZED);
        }
        // token是否过期
        if (TokenUtil.isExpiration(token)) {
            return new HttpResultUtil<UserInfoVO>().error(ErrorCode.AUTHORIZATION_INVALID);
        }
        // 拿出用户信息
        UserInfoVO vo = caffeineCache.getIfPresent(token);
        if (!StringUtil.isNull(vo)) {
            return new HttpResultUtil<UserInfoVO>().ok(vo);
        }
        OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(token);
        if (oAuth2AccessToken != null) {
            OAuth2Authentication oAuth2Authentication = tokenStore.readAuthentication(oAuth2AccessToken);
            UserDetail userDetail = (UserDetail) oAuth2Authentication.getPrincipal();
            vo = UserInfoVO.builder().userId(userDetail.getUserId())
                    .username(userDetail.getUsername())
                    .permissionList(userDetail.getPermissionsList())
                    .email(userDetail.getEmail())
                    .mobile(userDetail.getMobile())
                    .imgUrl(userDetail.getImgUrl()).build();
            caffeineCache.put(token, vo);
            return new HttpResultUtil<UserInfoVO>().ok(vo);
        }
        return new HttpResultUtil<UserInfoVO>().error(ErrorCode.AUTHORIZATION_INVALID);
    }

    @GetMapping("/oauth/logout")
    @ApiOperation("认证授权>退出登录")
    public void logout(HttpServletRequest request) {
        String token = request.getHeader(Constant.AUTHORIZATION_HEAD);
        if (StringUtil.isEmpty(token)) {
            return;
        }
        caffeineCache.invalidate(token);
        OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(token);
        if (oAuth2AccessToken != null) {
            tokenStore.removeAccessToken(oAuth2AccessToken);
            OAuth2RefreshToken refreshToken = oAuth2AccessToken.getRefreshToken();
            tokenStore.removeRefreshToken(refreshToken);
            tokenStore.removeAccessTokenUsingRefreshToken(refreshToken);
        }
    }

}
