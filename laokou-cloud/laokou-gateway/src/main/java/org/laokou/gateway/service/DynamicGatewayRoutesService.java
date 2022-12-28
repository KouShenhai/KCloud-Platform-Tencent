package org.laokou.gateway.service;

import org.springframework.cloud.gateway.route.RouteDefinition;
import reactor.core.publisher.Mono;

/**
 * @author laokou
 */
public interface DynamicGatewayRoutesService {

    /**
     * 新增路由
     * @param route
     * @return
     */
   Mono<Void> insert(Mono<RouteDefinition> route);

    /**
     * 修改路由
     * @param route
     * @return
     */
   Mono<Void> update(Mono<RouteDefinition> route);

    /**
     * 删除路由
     * @param routeId
     * @return
     */
   Mono<Void> delete(Mono<String> routeId);

}
