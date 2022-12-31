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
package org.laokou.gateway.route;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.laokou.common.core.utils.JacksonUtil;
import org.laokou.gateway.constant.GatewayConstant;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/**
 * @author laokou
 */
@Component
@Slf4j
public class CacheRouteDefinitionRepository implements RouteDefinitionRepository {

    private ReactiveRedisTemplate<String, RouteDefinition> reactiveRedisTemplate;
    private ReactiveValueOperations<String, RouteDefinition> routeDefinitionReactiveValueOperations;

    public CacheRouteDefinitionRepository(ReactiveRedisTemplate<String, RouteDefinition> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
        this.routeDefinitionReactiveValueOperations = reactiveRedisTemplate.opsForValue();
    }

    @PostConstruct
    public void initRouter() throws IOException {
        ClassPathResource resource = new ClassPathResource("router.json");
        String rule = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        List<RouteDefinition> routeDefinitionList = JacksonUtil.toList(rule, RouteDefinition.class);
        Map<String, RouteDefinition> routeDefinitionMap = routeDefinitionList.stream().collect(Collectors.toMap(k -> createKey(k.getId()), v -> v));
        routeDefinitionReactiveValueOperations.delete(this.createKey("*"))
                        .flatMap(success -> routeDefinitionReactiveValueOperations.multiSet(routeDefinitionMap)).subscribe();
    }

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        return this.reactiveRedisTemplate.keys(this.createKey("*")).flatMap((key) -> this.routeDefinitionReactiveValueOperations.get(key))
                .onErrorContinue((throwable, routeDefinition) -> {
            if (log.isErrorEnabled()) {
                log.error("get routes from redis error cause : {}", throwable.toString(), throwable);
            }
        });
    }

    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        return route.flatMap((routeDefinition) -> this.routeDefinitionReactiveValueOperations.set(this.createKey(routeDefinition.getId()), routeDefinition)
                .flatMap((success) -> success ? Mono.empty()
                        : Mono.defer(() -> Mono.error(new RuntimeException(String.format("Could not add route to redis repository: %s", routeDefinition))))));
    }

    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return routeId.flatMap((id) -> this.routeDefinitionReactiveValueOperations.delete(this.createKey(id))
                .flatMap((success) -> success ? Mono.empty()
                        : Mono.defer(() -> Mono.error(new NotFoundException(String.format("Could not remove route from redis repository with id: %s", routeId))))));
    }

    private String createKey(String routeId) {
        return GatewayConstant.REDIS_DYNAMIC_ROUTER_RULE_KEY + ":" + routeId;
    }

}
