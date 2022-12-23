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
package org.laokou.auth.server.infrastructure.feign.rocketmq;
import org.laokou.auth.server.infrastructure.feign.rocketmq.factory.RocketmqApiFeignClientFallbackFactory;
import org.laokou.common.core.constant.ServiceConstant;
import org.laokou.rocketmq.client.dto.RocketmqDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author laokou
 */
@FeignClient(name = ServiceConstant.LAOKOU_ROCKETMQ,path = "/api", fallback = RocketmqApiFeignClientFallbackFactory.class)
@Service
public interface RocketmqApiFeignClient {

    /**
     * 异步发送
     * @param topic: 主题
     * @param dto:   消息内容（Json格式）
     */
    @PostMapping("/sendOne/{topic}")
    void sendOneMessage(@PathVariable("topic") String topic, @RequestBody RocketmqDTO dto);

}
