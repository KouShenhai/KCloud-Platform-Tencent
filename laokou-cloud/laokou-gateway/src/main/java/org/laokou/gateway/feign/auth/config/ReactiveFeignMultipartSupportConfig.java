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
package org.laokou.gateway.feign.auth.config;
import org.laokou.common.constant.Constant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactivefeign.ReactiveOptions;
import reactivefeign.client.ReactiveHttpRequestInterceptor;
import reactivefeign.retry.BasicReactiveRetryPolicy;
import reactivefeign.spring.config.ReactiveRetryPolicies;
import reactivefeign.webclient.WebReactiveOptions;
import reactor.core.publisher.Mono;
/**
 * @author  Kou Shenhai
 */
@Configuration
public class ReactiveFeignMultipartSupportConfig {

    @Bean
    public feign.Logger.Level multipartLoggerLevel() {
        return feign.Logger.Level.FULL;
    }

    @Bean
    public ReactiveHttpRequestInterceptor reactiveHttpRequestInterceptor() {
        return reactiveHttpRequest -> Mono.subscriberContext().map(ctx -> {
            if (ctx.isEmpty()) {
                return reactiveHttpRequest;
            }
            reactiveHttpRequest.headers().put(Constant.AUTHORIZATION_HEAD,
                    ctx.get(Constant.AUTHORIZATION_HEAD));
            return reactiveHttpRequest;
        });
    }

    /**
     * 设置一些超时时间
     * @return
     */
    @Bean
    public ReactiveOptions reactiveOptions() {
        return new WebReactiveOptions.Builder()
                .setWriteTimeoutMillis(30000)
                .setReadTimeoutMillis(30000)
                .setConnectTimeoutMillis(30000)
                .build();
    }

    /**
     * 重试机制
     * @return
     */
    @Bean
    public ReactiveRetryPolicies retryOnNext() {
        //不进行重试，retryOnSame是控制对同一个实例的重试策略，retryOnNext是控制对不同实例的重试策略。
        return new ReactiveRetryPolicies.Builder()
                .retryOnSame(BasicReactiveRetryPolicy.retryWithBackoff(0, 10))
                .retryOnNext(BasicReactiveRetryPolicy.retryWithBackoff(0, 10))
                .build();
    }

}
