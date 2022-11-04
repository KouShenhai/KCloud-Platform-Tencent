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
package org.laokou.security.server.controller;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.laokou.auth.client.user.SecurityUser;
import org.laokou.auth.client.user.UserDetail;
import org.laokou.auth.client.vo.UserInfoVO;
import org.laokou.captcha.service.SysCaptchaService;
import org.laokou.common.constant.Constant;
import org.laokou.common.exception.CustomException;
import org.laokou.common.exception.ErrorCode;
import org.laokou.common.utils.HttpResultUtil;
import org.laokou.common.utils.StringUtil;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
     * @author Kou Shenhai
 */
@RestController
@RequiredArgsConstructor
@Api(value = "认证授权API",protocols = "http",tags = "认证授权API")
public class OauthController {

    private final TokenStore tokenStore;

    private final SysCaptchaService sysCaptchaService;

    @GetMapping("/oauth/userInfo")
    @ApiOperation("认证授权>用户信息")
    public HttpResultUtil<UserInfoVO> userInfo(HttpServletRequest request) {
        String token = SecurityUser.getToken(request);
        if (StringUtil.isEmpty(token)) {
            return new HttpResultUtil<UserInfoVO>().error(ErrorCode.UNAUTHORIZED);
        }
        OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(token);
        if (oAuth2AccessToken != null) {
            OAuth2Authentication oAuth2Authentication = tokenStore.readAuthentication(oAuth2AccessToken);
            UserDetail userDetail = (UserDetail) oAuth2Authentication.getPrincipal();
            UserInfoVO vo = UserInfoVO.builder().userId(userDetail.getUserId())
                    .username(userDetail.getUsername())
                    .permissionList(userDetail.getPermissionsList())
                    .email(userDetail.getEmail())
                    .mobile(userDetail.getMobile())
                    .imgUrl(userDetail.getImgUrl()).build();
            return new HttpResultUtil<UserInfoVO>().ok(vo);
        }
        return new HttpResultUtil<UserInfoVO>().error(ErrorCode.AUTHORIZATION_INVALID);
    }

    @GetMapping("/oauth/logout")
    @ApiOperation("认证授权>退出登录")
    public void logout(HttpServletRequest request) {
        String token = request.getHeader(Constant.AUTHORIZATION_HEAD);
        OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(token);
        if (oAuth2AccessToken != null) {
            tokenStore.removeAccessToken(oAuth2AccessToken);
            OAuth2RefreshToken refreshToken = oAuth2AccessToken.getRefreshToken();
            tokenStore.removeRefreshToken(refreshToken);
            tokenStore.removeAccessTokenUsingRefreshToken(refreshToken);
        }
    }

    @GetMapping("/oauth/captcha")
    @ApiOperation("认证授权>验证码")
    public void captcha(@RequestParam(Constant.UUID)String uuid, HttpServletResponse response) throws IOException {
        //生成图片验证码
        if (StringUtil.isEmpty(uuid)) {
            throw new CustomException(ErrorCode.IDENTIFIER_NOT_NULL);
        }
        BufferedImage image = sysCaptchaService.createImage(uuid);
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(image,"jpg",out);
        out.close();
    }

}
