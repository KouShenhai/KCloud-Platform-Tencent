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

package org.laokou.im.server.controller;

/**
 * @author Kou Shenhai
 */

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.laokou.im.client.PushMsgDTO;
import org.laokou.im.server.service.ImService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author Kou Shenhai
 */
@RestController
@AllArgsConstructor
@Api(value = "即时通讯API",protocols = "http",tags = "即时通讯API")
@RequestMapping("/api")
public class ImController {

    @Autowired
    private ImService imService;

    @PostMapping("/push")
    @ApiOperation("即时通讯API>推送")
    public void push(@RequestBody PushMsgDTO dto) throws IOException {
        imService.pusMessage(dto);
    }

}
