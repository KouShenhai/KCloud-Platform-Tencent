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
package org.laokou.ump.server.utils;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.JsonNode;
import org.laokou.auth.client.user.BaseUserVO;
import org.laokou.ump.server.exception.RenOAuth2Exception;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.laokou.common.constant.Constant;
import org.laokou.common.exception.ErrorCode;
import org.laokou.common.utils.HttpUtil;
import org.laokou.common.utils.JacksonUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
/**
 * 认证
 * @author Kou Shenhai
 * @version 1.0
 * @date 2021/4/11 0011 下午 5:16
 */
@Component
@Slf4j
public class AuthUtil {

    @Value("${auth.client_id}")
    private String CLIENT_ID;

    @Value("${auth.client_secret}")
    private String CLIENT_SECRET;

    @Value("${auth.redirect_uri}")
    private String REDIRECT_URI;

    @Value("${auth.grant_type}")
    private String GRANT_TYPE;

    private static final String POST_AUTHORIZE_URL = "http://192.168.62.1:9001/oauth/token";

    private static final String GET_USER_KEY_URL = "http://192.168.62.1:9001/oauth2/userInfo";

    public String getAccessToken(String code) throws IOException {
        //将code放入
        Map<String,String> tokenMap = new HashMap<>(5);
        tokenMap.put("code",code);
        tokenMap.put("client_id",CLIENT_ID);
        tokenMap.put("client_secret",CLIENT_SECRET);
        tokenMap.put("redirect_uri",REDIRECT_URI);
        tokenMap.put("grant_type",GRANT_TYPE);
        String resultJson = HttpUtil.doPost(POST_AUTHORIZE_URL,tokenMap,new HashMap<>(0));
        String accessToken = JSONUtil.parseObj(resultJson).getStr(Constant.ACCESS_TOKEN);
        if (StringUtils.isEmpty(accessToken)){
            throw new RenOAuth2Exception(ErrorCode.UNAUTHORIZED,"授权码已过期，请重新获取");
        }
        return accessToken;
    }

    public BaseUserVO getUerInfo(String accessToken) throws IOException {
        Map<String,String> userInfoMap = new HashMap<>(1);
        userInfoMap.put(Constant.ACCESS_TOKEN,accessToken);
        String json = HttpUtil.doGet(GET_USER_KEY_URL, userInfoMap,new HashMap<>(0));
        JsonNode data = JacksonUtil.readTree(json).get("data");
        return JacksonUtil.toBean(data.toString(),BaseUserVO.class);
    }

}
