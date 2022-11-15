/**
 * Copyright (c) 2022 KCloud-Platform-Official Authors. All Rights Reserved.
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
package org.laokou.admin.server.infrastructure.component.feign.kafka;

import org.laokou.admin.server.infrastructure.component.feign.kafka.factory.KafkaApiFeignClientFallbackFactory;
import org.laokou.common.core.constant.ServiceConstant;
import org.laokou.kafka.client.dto.KafkaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author Kou Shenhai
 */
@FeignClient(name = ServiceConstant.LAOKOU_KAFKA,path = "/api", fallback = KafkaApiFeignClientFallbackFactory.class)
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
