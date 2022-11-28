/**
 * Copyright (c) 2022 KCloud-Platform-Official Authors. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *   http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.laokou.gateway.filter;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.http.HttpUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.laokou.common.core.constant.Constant;
import org.laokou.common.core.exception.ErrorCode;
import org.laokou.common.core.password.PasswordUtil;
import org.laokou.common.core.utils.HttpResultUtil;
import org.laokou.common.core.utils.StringUtil;
import org.laokou.gateway.constant.GatewayConstant;
import org.laokou.gateway.utils.ResponseUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * 认证Filter
 * @author Kou Shenhai
 * @version 1.0
 * @date 2020/9/4 0004 下午 9:10
 */
@Component
@Slf4j
@Setter
@Getter
@ConfigurationProperties(prefix = "gateway")
public class AuthFilter implements GlobalFilter,Ordered {

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    private static final String OAUTH_URI = "/oauth/token";

    private static final String USERNAME = "username";

    private static final String PASSWORD = "password";

    /**
     * 不拦截的urls
     */
    private List<String> uris;

    @Override
    public Mono filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取request对象
        ServerHttpRequest request = exchange.getRequest();
        // 获取uri
        String requestUri = request.getPath().pathWithinApplication().value();
        log.info("uri：{}", requestUri);
        // 请求放行，无需验证权限
        if (pathMatcher(requestUri)){
            return chain.filter(exchange);
        }
        // 表单提交
        MediaType mediaType = request.getHeaders().getContentType();
        if (ANT_PATH_MATCHER.match(OAUTH_URI,requestUri) && MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(mediaType)) {
            return authDecode(exchange,chain);
        }
        // 获取token
        String token = getToken(request);
        if (StringUtil.isEmpty(token)) {
            return ResponseUtil.response(exchange, new HttpResultUtil<>().error(ErrorCode.UNAUTHORIZED, GatewayConstant.UNAUTHORIZED_MSG));
        }
        ServerHttpRequest build = exchange.getRequest().mutate()
                .header(Constant.AUTHORIZATION_HEAD, token).build();
        return chain.filter(exchange.mutate().request(build).build());
    }

    @Bean(value = "ipKeyResolver")
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            String ip = Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).getAddress().getHostAddress();
            return Mono.just(ip);
        };
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    private Mono authDecode(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerRequest serverRequest = ServerRequest.create(exchange, HandlerStrategies.withDefaults().messageReaders());
        Mono modifiedBody = serverRequest.bodyToMono(String.class).flatMap(decrypt());
        BodyInserter<Mono, ReactiveHttpOutputMessage> bodyInserter = BodyInserters.fromPublisher(modifiedBody, String.class);
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(exchange.getRequest().getHeaders());
        headers.remove(HttpHeaders.CONTENT_LENGTH);
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, headers);
        return bodyInserter.insert(outputMessage, new BodyInserterContext()).then(Mono.defer(() -> {
            ServerHttpRequest decorator = decorate(exchange, headers, outputMessage);
            return chain.filter(exchange.mutate().request(decorator).build());
        }));
    }

    private Function decrypt() {
        return s -> {
            // 获取请求密码并解密
            Map<String, String> inParamsMap = HttpUtil.decodeParamMap((String) s, CharsetUtil.CHARSET_UTF_8);
            if (inParamsMap.containsKey(PASSWORD) && inParamsMap.containsKey(USERNAME)) {
                try {
                    String password = inParamsMap.get(PASSWORD);
                    String username = inParamsMap.get(USERNAME);
                    // 返回修改后报文字符
                    if (StringUtil.isNotEmpty(password)) {
                        inParamsMap.put(PASSWORD, PasswordUtil.decode(password));
                    }
                    if (StringUtil.isNotEmpty(username)) {
                        inParamsMap.put(USERNAME, PasswordUtil.decode(username));
                    }
                } catch (Exception e) {
                    log.error("错误信息：{}",e.getMessage());
                }
            }
            else {
                log.error("非法请求数据:{}", s);
            }
            return Mono.just(HttpUtil.toParams(inParamsMap, Charset.defaultCharset(), true));
        };
    }

    private ServerHttpRequestDecorator decorate(ServerWebExchange exchange, HttpHeaders headers, CachedBodyOutputMessage outputMessage) {
        return new ServerHttpRequestDecorator(exchange.getRequest()) {
            @Override
            public HttpHeaders getHeaders() {
                long contentLength = headers.getContentLength();
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.putAll(super.getHeaders());
                if (contentLength > 0) {
                    httpHeaders.setContentLength(contentLength);
                } else {
                    httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                }
                return httpHeaders;
            }

            @Override
            public Flux<DataBuffer> getBody() {
                return outputMessage.getBody();
            }
        };
    }

    /**
     * uri匹配
     * @param requestUri
     * @return
     */
    private boolean pathMatcher(String requestUri) {
        for (String url : uris) {
            if (ANT_PATH_MATCHER.match(url, requestUri)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取token
     * @param request
     */
    private String getToken(ServerHttpRequest request){
        //从header中获取token
        String token = request.getHeaders().getFirst(Constant.AUTHORIZATION_HEAD);
        //如果header中不存在Authorization，则从参数中获取Authorization
        if(StringUtil.isEmpty(token)){
            token = request.getQueryParams().getFirst(Constant.AUTHORIZATION_HEAD);
        }
        assert token != null;
        return token.trim();
    }

}
