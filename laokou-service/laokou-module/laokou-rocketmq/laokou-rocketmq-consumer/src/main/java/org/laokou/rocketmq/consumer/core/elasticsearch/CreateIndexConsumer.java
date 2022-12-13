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
 
package org.laokou.rocketmq.consumer.core.elasticsearch;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.laokou.common.core.utils.HttpResultUtil;
import org.laokou.common.core.utils.JacksonUtil;
import org.laokou.elasticsearch.client.dto.CreateIndexDTO;
import org.laokou.rocketmq.client.constant.RocketmqConstant;
import org.laokou.rocketmq.consumer.feign.elasticsearch.ElasticsearchApiFeignClient;
import org.laokou.rocketmq.consumer.filter.MessageFilter;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * @author Kou Shenhai
 */
@RocketMQMessageListener(consumerGroup = "laokou-consumer-group-7", topic = RocketmqConstant.LAOKOU_CREATE_INDEX_TOPIC)
@Component
@RequiredArgsConstructor
@Slf4j
public class CreateIndexConsumer implements MessageListenerConcurrently {

    private final ElasticsearchApiFeignClient elasticsearchApiFeignClient;

    private final MessageFilter messageFilter;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> messageExtList, ConsumeConcurrentlyContext context) {
        String messageBody = messageFilter.getBody(messageExtList);
        if (messageBody == null) {
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        try {
            CreateIndexDTO createIndexDTO = JacksonUtil.toBean(messageBody, CreateIndexDTO.class);
            HttpResultUtil<Boolean> result = elasticsearchApiFeignClient.create(createIndexDTO);
            if (!result.success()) {
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (FeignException e) {
            log.error("错误信息:{}",e.getMessage());
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
    }
}
