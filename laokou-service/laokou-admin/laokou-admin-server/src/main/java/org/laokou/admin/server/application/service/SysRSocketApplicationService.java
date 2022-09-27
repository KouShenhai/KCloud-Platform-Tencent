package org.laokou.admin.server.application.service;

import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Kou Shenhai
 */
public interface SysRSocketApplicationService {

    /**
     * 无响应
     * @param socketRequester 请求参数
     * @param userId 编号
     * @return
     */
    Mono<Void> fireAndForget(RSocketRequester socketRequester, Long userId);

    /**
     * 请求响应
     * @return
     */
    Mono<String> requestResponse();

    /**
     * 请求流
     * @return
     */
    Flux<String> requestStream();

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
