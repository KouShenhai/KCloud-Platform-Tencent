package org.laokou.kafka.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.laokou.kafka.client.dto.KafkaDTO;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Kou Shenhai
 */
@RestController
@RequestMapping("/kafka")
@Slf4j
@RequiredArgsConstructor
public class KafkaSender {

    private final KafkaTemplate kafkaTemplate;

    @PostMapping("/send/{topic}")
    public void sendMessage(@PathVariable("topic") String topic, @RequestBody KafkaDTO dto) throws InterruptedException, ExecutionException, TimeoutException {
        kafkaTemplate.send(topic,dto.getData()).get(10, TimeUnit.SECONDS);
    }

    @PostMapping("/sendAsync/{topic}")
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
