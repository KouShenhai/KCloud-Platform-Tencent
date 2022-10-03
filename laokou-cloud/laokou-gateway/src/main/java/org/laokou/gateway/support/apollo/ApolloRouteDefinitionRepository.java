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
package org.laokou.gateway.support.apollo;
import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfig;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Lists;
import org.laokou.common.utils.JacksonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Collection;
/**
 * @author Kou Shenhai
 * @version 1.0
 * @date 2022/7/25 0025 下午 3:55
 */
@Slf4j
@Configuration
public class ApolloRouteDefinitionRepository implements RouteDefinitionRepository, ApplicationEventPublisherAware {

    private static final String ROUTES = "gateway.routes";

    private ApplicationEventPublisher publisher;

    /**
     * 高性能缓存
     */
    private final Cache<String,RouteDefinition> caffeineCache;

    @ApolloConfig
    private Config config;

    public ApolloRouteDefinitionRepository() {
        caffeineCache = Caffeine.newBuilder().initialCapacity(128).maximumSize(1024).build();
    }

    @ApolloConfigChangeListener(value = "application")
    private void changeHandler(ConfigChangeEvent event) {
        log.info("apollo动态拉取配置...");
        if (event.isChanged(ROUTES)) {
            this.publisher.publishEvent(new RefreshRoutesEvent(this));
        }
    }

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        Collection<RouteDefinition> routeDefinitions = caffeineCache.asMap().values();
        if (routeDefinitions.isEmpty()) {
            final String property = config.getProperty(ROUTES, null);
            if (StringUtils.isBlank(property)) {
                return Flux.fromIterable(Lists.newArrayList());
            }
            routeDefinitions = JacksonUtil.toList(property, RouteDefinition.class);
        }
        return Flux.fromIterable(routeDefinitions);
    }

    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        return route.flatMap(item -> {
            this.caffeineCache.invalidateAll();
            return Mono.empty();
        });
    }

    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return routeId.flatMap(item -> {
            this.caffeineCache.invalidateAll();
            return Mono.empty();
        });
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }
}