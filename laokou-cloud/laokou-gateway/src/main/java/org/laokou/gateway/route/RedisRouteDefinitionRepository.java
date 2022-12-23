/**
 * Copyright (c) 2022 KCloud-Platform-Tencent Authors. All Rights Reserved.
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
package org.laokou.gateway.route;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.commons.collections.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.laokou.common.core.exception.CustomException;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;
/**
 * 基于redis存储
 * @author laokou
 * @version 1.0
 * @date 2022/7/25 0025 下午 3:55
 */
@Slf4j
@Component
public class RedisRouteDefinitionRepository implements RouteDefinitionRepository {

    private static final String DYNAMIC_GATEWAY_ROUTES = "dynamic:gateway:routes";

    /**
     * 高性能缓存
     */
    private final Cache<String,RouteDefinition> caffeineCache;
    private final ReactiveHashOperations<String,String,RouteDefinition> reactiveHashOperations;

    public RedisRouteDefinitionRepository(ReactiveRedisTemplate reactiveRedisTemplate) {
        this.reactiveHashOperations = reactiveRedisTemplate.opsForHash();
        caffeineCache = Caffeine.newBuilder().initialCapacity(15)
                .expireAfterAccess(30, TimeUnit.MINUTES)
                .maximumSize(150)
                .build();
    }

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        Collection<RouteDefinition> routeDefinitions = caffeineCache.asMap().values();
        if (CollectionUtils.isEmpty(routeDefinitions)) {
            return reactiveHashOperations.entries(DYNAMIC_GATEWAY_ROUTES)
                    .map(Map.Entry::getValue)
                    .doOnNext(definition -> caffeineCache.put(definition.getId(),definition));
        }
        return Flux.fromIterable(routeDefinitions);
    }

    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        return route.flatMap(definition -> reactiveHashOperations.put(DYNAMIC_GATEWAY_ROUTES,definition.getId(), definition))
                .doOnNext(result -> caffeineCache.invalidateAll())
                .flatMap(result -> result ? Mono.empty()
                        : Mono.defer(() -> Mono.error(new CustomException("Route definition cannot be added"))));
    }

    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return routeId.flatMap(id -> reactiveHashOperations.remove(DYNAMIC_GATEWAY_ROUTES,id))
                .doOnNext(result -> caffeineCache.invalidateAll())
                .flatMap(result -> result != 0 ? Mono.empty()
                        : Mono.defer(() -> Mono.error(new CustomException(String.format("Route definition is not found,routeId:%s",routeId)))));
    }

}
