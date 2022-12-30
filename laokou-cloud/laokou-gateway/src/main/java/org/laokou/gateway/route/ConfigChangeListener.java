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

package org.laokou.gateway.route;

import com.tencent.cloud.polaris.config.spring.event.ConfigChangeSpringEvent;
import lombok.RequiredArgsConstructor;
import org.laokou.gateway.service.DynamicGatewayRoutesService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author Kou Shenhai
 */
@Component
@RequiredArgsConstructor
public class ConfigChangeListener implements ApplicationListener<ConfigChangeSpringEvent> {

    private final DynamicGatewayRoutesService dynamicGatewayRoutesService;

    @Override
    public void onApplicationEvent(ConfigChangeSpringEvent event) {
        try {
            dynamicGatewayRoutesService.batch();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
