/**
 * Copyright (c) 2022 KCloud-Platform-Tencent Authors. All Rights Reserved.
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
package org.laokou.gateway.feign.kafka.fallback;
import org.laokou.gateway.feign.kafka.KafkaApiFeignClient;
import org.laokou.kafka.client.dto.KafkaDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 服务降级
 * @author laokou
 * @version 1.0
 * @date 2020/9/5 0005 上午 12:12
 */
@Slf4j
@AllArgsConstructor
public class KafkaApiFeignClientFallback implements KafkaApiFeignClient {

    private final Throwable throwable;

    @Override
    public void sendAsyncMessage(String topic, KafkaDTO dto) {
        log.error("服务调用失败，报错原因：{}",throwable.getMessage());
    }
}
