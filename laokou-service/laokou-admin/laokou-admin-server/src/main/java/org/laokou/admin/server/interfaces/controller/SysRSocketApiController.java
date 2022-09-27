package org.laokou.admin.server.interfaces.controller;

import org.laokou.admin.server.application.service.SysRSocketApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Kou Shenhai
 */
@RestController
@RequestMapping("/sys/rsocket/api")
public class SysRSocketApiController {

    @Autowired
    private SysRSocketApplicationService sysRSocketApplicationService;

    @MessageMapping("fire-and-forget")
    public Mono<Void> fireAndForget(RSocketRequester socketRequester, Long userId) {
        return sysRSocketApplicationService.fireAndForget(socketRequester,userId);
    }

    @MessageMapping("request-response")
    public Mono<String> requestResponse() {
        return sysRSocketApplicationService.requestResponse();
    }

    @MessageMapping("request-stream")
    public Flux<String> requestStream() {
        return sysRSocketApplicationService.requestStream();
    }

    @MessageMapping("request-channel")
    public Flux<String> requestChannel() {
        return sysRSocketApplicationService.requestChannel();
    }

}
