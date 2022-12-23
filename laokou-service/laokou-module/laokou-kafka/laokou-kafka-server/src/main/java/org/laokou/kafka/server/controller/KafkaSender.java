/**
 * Copyright (c) 2022 KCloud-Platform-Official Authors. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *   http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.laokou.kafka.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.laokou.kafka.client.dto.KafkaDTO;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

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
    public void sendMessage(@PathVariable("topic") String topic, @RequestBody KafkaDTO dto) throws InterruptedException, ExecutionException {
        kafkaTemplate.send(topic,dto).get();
    }

    @PostMapping("/sendAsync/{topic}")
    @ApiOperation("Kafka消息>异步发送")
    public void sendAsyncMessage(@PathVariable("topic") String topic, @RequestBody KafkaDTO dto) {
//        kafkaTemplate.send(topic,dto.getData()).addCallback(new ListenableFutureCallback() {
//            @Override
//            public void onFailure(Throwable throwable) {
//                log.error("报错信息：{}",throwable.getMessage());
//            }
//
//            @Override
//            public void onSuccess(Object o) {
//                log.info("发送成功");
//            }
//        });
    }
}
