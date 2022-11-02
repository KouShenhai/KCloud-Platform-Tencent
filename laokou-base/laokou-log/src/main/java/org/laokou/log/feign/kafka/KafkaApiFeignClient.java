/**
 * Copyright (c) 2022 KCloud-Platform Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.laokou.log.feign.kafka;
import org.laokou.common.constant.ServiceConstant;
import org.laokou.kafka.client.dto.KafkaDTO;
import org.laokou.log.config.FeignMultipartSupportConfig;
import org.laokou.log.feign.kafka.factory.KafkaApiFeignClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

/**
 * @author Kou Shenhai
 */
@FeignClient(name = ServiceConstant.LAOKOU_KAFKA,path = "/api",configuration = FeignMultipartSupportConfig.class, fallback = KafkaApiFeignClientFallbackFactory.class)
@Service
public interface KafkaApiFeignClient {

    /**
     * 异步发送
     * @param topic: 主题
     * @param dto:   消息内容（Json格式）
     */
    @PostMapping("/sendAsync/{topic}")
    void sendAsyncMessage(@PathVariable("topic") String topic, @RequestBody KafkaDTO dto);

}
