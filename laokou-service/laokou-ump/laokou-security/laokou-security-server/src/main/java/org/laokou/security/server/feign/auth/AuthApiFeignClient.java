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
package org.laokou.security.server.feign.auth;
import org.laokou.auth.client.dto.LoginDTO;
import org.laokou.auth.client.user.UserDetail;
import org.laokou.common.constant.ServiceConstant;
import org.laokou.common.utils.HttpResultUtil;
import org.laokou.security.server.feign.auth.factory.AuthApiFeignClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
/**
 * @author Kou Shenhai
 */
@FeignClient(name = ServiceConstant.LAOKOU_AUTH, fallbackFactory = AuthApiFeignClientFallbackFactory.class)
@Service
public interface AuthApiFeignClient {

    /**
     * 用户详情
     * @param loginDTO
     * @return
     */
    @PostMapping(value = "/sys/auth/api/userDetail")
    HttpResultUtil<UserDetail> userDetail(@RequestBody LoginDTO loginDTO);

}
