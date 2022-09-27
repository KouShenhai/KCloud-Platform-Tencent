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
package org.laokou.admin.server.infrastructure.component.handler.message.impl;

import org.laokou.admin.client.enums.ChannelTypeEnum;
import org.laokou.admin.server.infrastructure.component.event.PushMessageEvent;
import org.laokou.admin.server.infrastructure.component.handler.message.BaseHandler;
import org.laokou.admin.client.dto.MessageDTO;
import org.laokou.common.utils.SpringContextUtil;
import org.springframework.stereotype.Service;

@Service
public class PlatformHandler extends BaseHandler {

    public PlatformHandler() {
        channelCode = ChannelTypeEnum.PLATFORM.ordinal();
    }

    @Override
    public boolean handler(MessageDTO dto) {
        SpringContextUtil.publishEvent(new PushMessageEvent(dto));
        return true;
    }
}
