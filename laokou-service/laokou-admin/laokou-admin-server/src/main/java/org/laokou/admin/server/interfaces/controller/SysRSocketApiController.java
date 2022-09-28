package org.laokou.admin.server.interfaces.controller;

import org.laokou.admin.server.application.service.SysRSocketApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author Kou Shenhai
 */
@RestController
public class SysRSocketApiController {

    @Autowired
    private SysRSocketApplicationService sysRSocketApplicationService;

    @MessageMapping("request-channel")
    public Mono<Void> requestChannel(RSocketRequester rSocketRequester,@Payload Long userId) {
        return sysRSocketApplicationService.requestChannel(rSocketRequester,userId);
    }

    @ConnectMapping("send-message")
    public Mono<String> sendMessage(String message) {
        return Mono.just(message);
    }

}
