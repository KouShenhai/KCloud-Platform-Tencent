package org.laokou.ump.server.controller;

import lombok.RequiredArgsConstructor;
import org.laokou.auth.client.user.UserDetail;
import org.laokou.auth.client.vo.UserInfoVO;
import org.laokou.common.constant.Constant;
import org.laokou.common.exception.ErrorCode;
import org.laokou.common.utils.ConvertUtil;
import org.laokou.common.utils.HttpResultUtil;
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
public class OauthController {

    private final TokenStore tokenStore;

    @GetMapping("/oauth/userInfo")
    public HttpResultUtil<UserInfoVO> userInfo(HttpServletRequest request) {
        UserDetail userDetail = getUserDetail(request,false);
        if (userDetail != null) {
            UserInfoVO userInfoVO = ConvertUtil.sourceToTarget(userDetail, UserInfoVO.class);
            userInfoVO.setPermissionList(userDetail.getPermissionsList());
            userInfoVO.setUserId(userDetail.getId());
            return new HttpResultUtil<UserInfoVO>().ok(userInfoVO);
        }
        return new HttpResultUtil<UserInfoVO>().error(ErrorCode.AUTHORIZATION_INVALID);
    }

    @GetMapping("/oauth/logout")
    public void logout(HttpServletRequest request) {
        getUserDetail(request,true);
    }

    private UserDetail getUserDetail(HttpServletRequest request,boolean isOut) {
        String token = request.getHeader(Constant.AUTHORIZATION_HEAD).replace(Constant.BEARER,"");
        OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(token);
        if (oAuth2AccessToken != null) {
            if (isOut) {
                tokenStore.removeAccessToken(oAuth2AccessToken);
                OAuth2RefreshToken refreshToken = oAuth2AccessToken.getRefreshToken();
                tokenStore.removeRefreshToken(refreshToken);
                tokenStore.removeAccessTokenUsingRefreshToken(refreshToken);
                return null;
            }
            OAuth2Authentication oAuth2Authentication = tokenStore.readAuthentication(oAuth2AccessToken);
            return (UserDetail) oAuth2Authentication.getPrincipal();
        }
        return null;
    }

}
