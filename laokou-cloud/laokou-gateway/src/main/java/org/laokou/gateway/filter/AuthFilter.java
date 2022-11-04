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
package org.laokou.gateway.filter;
import lombok.Data;
import org.laokou.common.constant.Constant;
import org.laokou.common.exception.ErrorCode;
import org.laokou.common.utils.HttpResultUtil;
import org.laokou.common.utils.JacksonUtil;
import lombok.extern.slf4j.Slf4j;
import org.laokou.common.utils.StringUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
/**
 * 认证Filter
 * @author Kou Shenhai
 * @version 1.0
 * @date 2020/9/4 0004 下午 9:10
 */
@Component
@Slf4j
@Data
@ConfigurationProperties(prefix = "gateway")
public class AuthFilter implements GlobalFilter,Ordered {

    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    /**
     * 不拦截的urls
     */
    private List<String> uris;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String ip = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        // 记录日志

        // 放行uris
        // 获取request对象
        ServerHttpRequest request = exchange.getRequest();
        // 获取uri
        String requestUri = request.getPath().pathWithinApplication().value();
        log.info("uri：{}", requestUri);
        // 请求放行，无需验证权限
        if (pathMatcher(requestUri)){
            return chain.filter(exchange);
        }
        // 获取token
        String token = getToken(request);
        if (StringUtil.isEmpty(token)) {
            return response(exchange, new HttpResultUtil<>().error(ErrorCode.UNAUTHORIZED));
        }
        ServerHttpRequest build = exchange.getRequest().mutate()
                .header(Constant.AUTHORIZATION_HEAD, token).build();
        return chain.filter(exchange.mutate().request(build).build());
    }

    @Bean(value = "ipKeyResolver")
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            String ip = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
            return Mono.just(ip);
        };
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    /**
     * 前端响应
     * @param exchange
     * @param data
     * @return
     */
    private Mono<Void> response(ServerWebExchange exchange,Object data){
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(JacksonUtil.toJsonStr(data).getBytes(StandardCharsets.UTF_8));
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        exchange.getResponse().setStatusCode(HttpStatus.OK);
        return exchange.getResponse().writeWith(Flux.just(buffer));
    }

    /**
     * uri匹配
     * @param requestUri
     * @return
     */
    private boolean pathMatcher(String requestUri) {
        Iterator<String> iterator = uris.iterator();
        while (iterator.hasNext()) {
            String url = iterator.next();
            if (antPathMatcher.match(url,requestUri)){
                return true;
            }
        }
        return false;
    }

    /**
     * 获取token
     * @param request
     * @return
     */
    private String getToken(ServerHttpRequest request){
        //从header中获取token
        String token = request.getHeaders().getFirst(Constant.AUTHORIZATION_HEAD);
        //如果header中不存在Authorization，则从参数中获取Authorization
        if(StringUtil.isEmpty(token)){
            token = request.getQueryParams().getFirst(Constant.AUTHORIZATION_HEAD);
        }
        return token.trim();
    }

}
