package org.laokou.cas.token;
import org.laokou.cas.user.BaseUserVO;
import io.laokou.common.utils.TokenUtil;
import org.joda.time.DateTime;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
/**
 * 获取授权码
 * @author Kou Shenhai
 * @version 1.0
 * @date 2021/5/28 0028 下午 5:13
 */
public class RenTokenEnhancer implements TokenEnhancer {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication auth) {
        if (accessToken instanceof DefaultOAuth2AccessToken) {
            DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) accessToken;
            //添加授权码
            BaseUserVO vo = (BaseUserVO) auth.getUserAuthentication().getPrincipal();
            token.setValue(TokenUtil.getToken(TokenUtil.getClaims(vo.getUserId(),vo.getUsername())));
            token.setExpiration(DateTime.now().plusSeconds(TokenUtil.getExpire().intValue()).toDate());
            return token;
        }
        return accessToken;
    }

}
