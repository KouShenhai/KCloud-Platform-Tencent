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
package org.laokou.gateway.filter;
import lombok.RequiredArgsConstructor;
import org.laokou.common.constant.Constant;
import org.laokou.common.user.UserDetail;
import org.laokou.common.utils.HttpResultUtil;
import org.laokou.common.utils.JacksonUtil;
import org.laokou.gateway.feign.auth.AuthApiFeignClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
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
@RequiredArgsConstructor
public class AuthFilter implements GlobalFilter,Ordered {

    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    /**
     * 不拦截的urls
     */
    private final List<String> uris;

    private final AuthApiFeignClient authApiFeignClient;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("加载 AuthFilter");
        //获取request对象
        ServerHttpRequest request = exchange.getRequest();
        //获取uri
        String requestUri = request.getPath().pathWithinApplication().value();
        String method = request.getMethod().toString();
        log.info("uri：{}", requestUri);
        //请求放行，无需验证权限
        if (pathMatcher(requestUri)){
            return chain.filter(exchange);
        }
        //获取用户token
        String Authorization = request.getHeaders().getFirst(Constant.AUTHORIZATION_HEAD);
        if (StringUtils.isBlank(Authorization)){
            Authorization = request.getQueryParams().getFirst(Constant.AUTHORIZATION_HEAD);
        }
        log.info("Authorization:{}",Authorization);
        //获取访问资源的权限
        //资源访问权限
        String language = request.getHeaders().getFirst(HttpHeaders.ACCEPT_LANGUAGE);
        HttpResultUtil<UserDetail> result = authApiFeignClient.resource(language,Authorization,requestUri,method);
        log.info("result:{}",result);
        if (!result.success()) {
            return response(exchange,result);
        }
        UserDetail userDetail = result.getData();
        final String userId = userDetail.getId().toString();
        final String username = userDetail.getUsername();
        ServerHttpRequest build = exchange.getRequest().mutate()
                .header(Constant.TICKET,Constant.TICKET)
                .header(Constant.USER_KEY_HEAD,userId )
                .header(Constant.USERNAME_HEAD,username).build();
        return chain.filter(exchange.mutate().request(build).build());
    }

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

    private Mono<Void> response(ServerWebExchange exchange,Object data){
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(JacksonUtil.toJsonStr(data).getBytes(StandardCharsets.UTF_8));
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        exchange.getResponse().setStatusCode(HttpStatus.OK);
        return exchange.getResponse().writeWith(Flux.just(buffer));
    }

    @Bean(value = "ipKeyResolver")
    KeyResolver ipKeyResolver() {
        return exchange -> {
            String ip = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
            log.info("获取到ip为{}的请求",ip);
            return Mono.just(ip);
        };
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

}
