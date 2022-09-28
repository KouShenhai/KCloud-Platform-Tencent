package org.laokou.admin.server.application.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Kou Shenhai
 */
public interface SysRSocketApplicationService {

    /**
     * 双向流
     * @return
     */
    Flux<String> requestChannel();

    /**
     * 推送消息
     * @return
     * @param message 消息
     * @param userId  编号
     */
    Mono<Void> metadataPush(String message,Long userId);

}
