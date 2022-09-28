package org.laokou.admin.server.application.service;

import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Mono;

/**
 * @author Kou Shenhai
 */
public interface SysRSocketApplicationService {

    /**
     * 双向流
     * @param rSocketRequester
     * @param userId
     * @return
     */
    Mono<Void> requestChannel(RSocketRequester rSocketRequester, Long userId);

    /**
     * 推送消息
     * @return
     * @param message 消息
     * @param userId  编号
     */
    Mono<Void> metadataPush(String message,Long userId);

}
