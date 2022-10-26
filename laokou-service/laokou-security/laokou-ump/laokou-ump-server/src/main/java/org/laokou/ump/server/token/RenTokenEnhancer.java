/**
 * Copyright (c) 2022 KCloud-Platform Authors. All Rights Reserved.
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
package org.laokou.ump.server.token;
import org.joda.time.DateTime;
import org.laokou.auth.client.user.BaseUserVO;
import org.laokou.auth.client.utils.TokenUtil;
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
