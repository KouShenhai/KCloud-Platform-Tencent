package org.laokou.admin.server.application.service.impl;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.laokou.admin.server.application.service.SysRSocketApplicationService;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Kou Shenhai
 */
@Service
@Data
@Slf4j
public class SysRSocketApplicationServiceImpl implements SysRSocketApplicationService {

    /**
     * 路由地址
     */
    private static final String ROUTE = "/send-message";

    /**
     * 接收userId
     */
    private Long userId;

    /**
     * concurrent包线程安全Set，用来存放每个客户对应的SysRSocketApplicationServiceImpl对象
     */
    private static final CopyOnWriteArraySet<SysRSocketApplicationServiceImpl> sysRSocketCopyOnWriteArraySet = new CopyOnWriteArraySet<>();

    /**
     * 静态变量，用来记录当前线程连接数，线程安全
     */
    private static int onlineCount = 0;

    /**
     * 与某些客户端的连接会话，需要通过它来给客户打发送数据
     */
    private RSocketRequester socketRequester;

    /**
     * 返回在线数
     * @return
     */
    private static synchronized int getOnlineCount(){
        return onlineCount;
    }

    /**
     * 连接人数增加时
     */
    private static synchronized void addOnlineCount(){
        SysRSocketApplicationServiceImpl.onlineCount++;
    }

    /**
     * 连接人数减少时
     */
    private static synchronized void subOnlineCount(){
        SysRSocketApplicationServiceImpl.onlineCount--;
    }

    @Override
    public Flux<String> requestChannel() {
        return Flux.just("");
    }

    @Override
    public Mono<Void> metadataPush(String message, Long userId) {
        // 推送数据
        for (SysRSocketApplicationServiceImpl server : sysRSocketCopyOnWriteArraySet) {
            log.info("推送消息给:{}" , server.userId + ",推送内容：{}" , message);
            if (null == userId) {
                socketRequester.route(ROUTE).data(message).send();
            } else if (userId.equals(server.userId)) {
                socketRequester.route(ROUTE).data(message).send();
            }
        }
        return Mono.empty();
    }
}
