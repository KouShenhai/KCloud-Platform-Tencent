package org.laokou.kafka.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.laokou.kafka.client.dto.KafkaDTO;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * @author Kou Shenhai
 */
@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
@Api(value = "Kafka消息API",protocols = "http",tags = "Kafka消息API")
public class KafkaSender {

    private final KafkaTemplate kafkaTemplate;

    @PostMapping("/send/{topic}")
    @ApiOperation("Kafka消息>同步发送")
    public void sendMessage(@PathVariable("topic") String topic, @RequestBody KafkaDTO dto) throws InterruptedException, ExecutionException, TimeoutException {
        kafkaTemplate.send(topic,dto.getData()).get();
    }

    @PostMapping("/sendAsync/{topic}")
    @ApiOperation("Kafka消息>异步发送")
    public void sendAsyncMessage(@PathVariable("topic") String topic, @RequestBody KafkaDTO dto) {
        kafkaTemplate.send(topic,dto.getData()).addCallback(new ListenableFutureCallback() {
            @Override
            public void onFailure(Throwable throwable) {
                log.error("报错信息：{}",throwable.getMessage());
            }

            @Override
            public void onSuccess(Object o) {
                log.info("发送成功");
            }
        });
    }
}
