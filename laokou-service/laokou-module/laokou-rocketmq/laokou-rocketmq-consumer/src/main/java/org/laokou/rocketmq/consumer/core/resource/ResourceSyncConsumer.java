/**
 * Copyright (c) 2022 KCloud-Platform-Official Authors. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.laokou.rocketmq.consumer.core.resource;

import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.laokou.common.core.utils.ConvertUtil;
import org.laokou.common.core.utils.JacksonUtil;
import org.laokou.elasticsearch.client.model.ElasticsearchModel;
import org.laokou.rocketmq.client.constant.RocketmqConstant;
import org.laokou.rocketmq.client.dto.ResourceSyncDTO;
import org.laokou.rocketmq.consumer.feign.elasticsearch.ElasticsearchApiFeignClient;
import org.springframework.stereotype.Component;

/**
 * @author Kou Shenhai
 */
@RocketMQMessageListener(consumerGroup = "laokou-consumer-group-5", topic = RocketmqConstant.LAOKOU_SYNC_RESOURCE_TOPIC)
@Component
@RequiredArgsConstructor
public class ResourceSyncConsumer implements RocketMQListener<String> {

    private final ElasticsearchApiFeignClient elasticsearchApiFeignClient;

    @Override
    public void onMessage(String message) {
        ResourceSyncDTO resourceSyncDTO = JacksonUtil.toBean(message, ResourceSyncDTO.class);
        ElasticsearchModel model = ConvertUtil.sourceToTarget(resourceSyncDTO, ElasticsearchModel.class);
        elasticsearchApiFeignClient.syncAsyncBatch(model);
    }
}
