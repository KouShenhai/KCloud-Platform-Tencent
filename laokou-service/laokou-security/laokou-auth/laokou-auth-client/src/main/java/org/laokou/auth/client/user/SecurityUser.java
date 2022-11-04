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
package org.laokou.auth.client.user;
import org.laokou.auth.client.utils.TokenUtil;
import org.laokou.common.constant.Constant;
import org.laokou.common.exception.CustomException;
import org.laokou.common.exception.ErrorCode;
import org.apache.commons.lang.StringUtils;
import javax.servlet.http.HttpServletRequest;
/**
 * @author Kou Shenhai
 */
public class SecurityUser {

    public static Long getUserId(HttpServletRequest request) {
        String userId = request.getHeader(Constant.USER_KEY_HEAD);
        if (StringUtils.isBlank(userId)) {
            String authHeader = getToken(request);
            if (StringUtils.isBlank(authHeader)) {
                throw new CustomException(ErrorCode.UNAUTHORIZED);
            }
            if (TokenUtil.isExpiration(authHeader)) {
                throw new CustomException(ErrorCode.AUTHORIZATION_INVALID);
            }
            return TokenUtil.getUserId(authHeader);
        }
        return Long.valueOf(userId);
    }

    /**
     * 获取请求的token
     */
    public static String getToken(HttpServletRequest request){
        //从header中获取token
        String token = request.getHeader(Constant.AUTHORIZATION_HEAD);
        //如果header中不存在Authorization，则从参数中获取Authorization
        if(StringUtils.isBlank(token)){
            token = request.getParameter(Constant.AUTHORIZATION_HEAD);
        }
        return token;
    }

}
