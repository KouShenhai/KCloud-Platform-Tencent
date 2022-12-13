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

package org.laokou.rocketmq.consumer.core.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.laokou.common.core.utils.JacksonUtil;
import org.laokou.rocketmq.client.constant.RocketmqConstant;
import org.laokou.rocketmq.client.dto.MsgDTO;
import org.laokou.rocketmq.client.enums.ChannelTypeEnum;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author Kou Shenhai
 */
@RocketMQMessageListener(consumerGroup = "laokou-consumer-group-6", topic = RocketmqConstant.LAOKOU_MESSAGE_NOTICE_TOPIC)
@Component
@RequiredArgsConstructor
@Slf4j
public class EmailNoticeConsumer implements MessageListenerConcurrently {

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> messageExtList, ConsumeConcurrentlyContext context) {
        if (CollectionUtils.isEmpty(messageExtList)) {
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        MessageExt messageExt = messageExtList.stream().findFirst().get();
        // 重试三次不成功则不进行重试
        if (messageExt.getReconsumeTimes() == RocketmqConstant.RECONSUME_TIMES) {
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        String messageBody = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        final MsgDTO dto = JacksonUtil.toBean(messageBody, MsgDTO.class);
        if (ChannelTypeEnum.EMAIL.ordinal() == dto.getSendChannel()) {
            log.info("邮件");
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
