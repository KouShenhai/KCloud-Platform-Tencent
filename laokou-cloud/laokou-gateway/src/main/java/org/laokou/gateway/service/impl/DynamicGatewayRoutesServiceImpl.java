/**
 * Copyright (c) 2022 KCloud-Platform-Official Authors. All Rights Reserved.
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

package org.laokou.gateway.service.impl;

import lombok.RequiredArgsConstructor;
import org.laokou.gateway.route.RedisRouteDefinitionRepository;
import org.laokou.gateway.service.DynamicGatewayRoutesService;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * @author laokou
 */
@Service
@RequiredArgsConstructor
public class DynamicGatewayRoutesServiceImpl implements DynamicGatewayRoutesService, ApplicationEventPublisherAware {

    private ApplicationEventPublisher applicationEventPublisher;
    private final RedisRouteDefinitionRepository redisRouteDefinitionRepository;

    @Override
    public Mono<Void> insert(Mono<RouteDefinition> route) {
        redisRouteDefinitionRepository.save(route).subscribe();
        applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
        return Mono.empty();
    }

    @Override
    public Mono<Void> update(Mono<RouteDefinition> route) {
        redisRouteDefinitionRepository.delete(route.map(r -> r.getId())).subscribe();
        redisRouteDefinitionRepository.save(route).subscribe();
        applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
        return Mono.empty();
    }

    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        redisRouteDefinitionRepository.delete(routeId).subscribe();
        applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
        return Mono.empty();
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
