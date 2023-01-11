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
package org.laokou.gateway.utils;
import org.laokou.common.core.constant.Constant;
import org.laokou.common.core.utils.JacksonUtil;
import org.laokou.common.core.utils.StringUtil;
import org.laokou.gateway.constant.GatewayConstant;
import org.laokou.gateway.exception.GatewayException;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
/**
 * @author laokou
 */
public class ResponseUtil {
    public static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    /**
     * 前端响应
     * @param exchange
     * @param data
     * @return
     */
    public static Mono<Void> response(ServerWebExchange exchange, Object data){
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(JacksonUtil.toJsonStr(data).getBytes(StandardCharsets.UTF_8));
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        exchange.getResponse().setStatusCode(HttpStatus.OK);
        return exchange.getResponse().writeWith(Flux.just(buffer));
    }

    public static Map<String,Object> response(int code,String msg) {
       Map<String,Object> dataMap = new HashMap<>(2);
       dataMap.put("code",code);
       dataMap.put("msg",msg);
       return dataMap;
    }

    public static Map<String,Object> error(GatewayException gatewayException) {
        Map<String,Object> errorMap = new HashMap<>(2);
        errorMap.put("code",gatewayException.getCode());
        errorMap.put("msg",gatewayException.getMsg());
        return errorMap;
    }

    /**
     * 获取token
     * @param request
     */
    public static String getToken(ServerHttpRequest request){
        //从header中获取token
        String token = request.getHeaders().getFirst(Constant.AUTHORIZATION_HEAD);
        //如果header中不存在Authorization，则从参数中获取Authorization
        if(StringUtil.isEmpty(token)){
            token = request.getQueryParams().getFirst(Constant.AUTHORIZATION_HEAD);
        }
        return token == null ? "" : token.trim();
    }

    /**
     * 获取userId
     * @param request
     */
    public static String getUserId(ServerHttpRequest request){
        //从header中获取userId
        String userId = request.getHeaders().getFirst(GatewayConstant.REQUEST_USER_ID);
        //如果header中不存在userId，则从参数中获取userId
        if(StringUtil.isEmpty(userId)){
            userId = request.getQueryParams().getFirst(GatewayConstant.REQUEST_USER_ID);
        }
        return userId == null ? "" : userId.trim();
    }


    /**
     * 获取username
     * @param request
     */
    public static String getUsername(ServerHttpRequest request){
        //从header中获取username
        String username = request.getHeaders().getFirst(GatewayConstant.REQUEST_USERNAME);
        //如果header中不存在username，则从参数中获取username
        if(StringUtil.isEmpty(username)){
            username = request.getQueryParams().getFirst(GatewayConstant.REQUEST_USERNAME);
        }
        return username == null ? "" : username.trim();
    }

}
