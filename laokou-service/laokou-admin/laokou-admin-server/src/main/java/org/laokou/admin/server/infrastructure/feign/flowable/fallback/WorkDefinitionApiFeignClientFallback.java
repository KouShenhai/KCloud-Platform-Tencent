/**
 * Copyright (c) 2022 KCloud-Platform-Official Authors. All Rights Reserved.
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
package org.laokou.admin.server.infrastructure.feign.flowable.fallback;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.laokou.admin.server.infrastructure.feign.flowable.WorkDefinitionApiFeignClient;
import org.laokou.common.core.utils.HttpResultUtil;
import org.laokou.flowable.client.dto.DefinitionDTO;
import org.laokou.flowable.client.vo.DefinitionVO;
import org.laokou.flowable.client.vo.PageVO;
import org.springframework.web.multipart.MultipartFile;
/**
 * 服务降级
 * @author Kou Shenhai
 * @version 1.0
 * @date 2020/9/5 0005 上午 12:12
 */
@Slf4j
@AllArgsConstructor
public class WorkDefinitionApiFeignClientFallback implements WorkDefinitionApiFeignClient {

    private final Throwable throwable;


    @Override
    public HttpResultUtil<Boolean> insert(String name, MultipartFile file) {
        log.error("服务调用失败，报错原因：{}",throwable.getMessage());
        return new HttpResultUtil<>();
    }

    @Override
    public HttpResultUtil<PageVO<DefinitionVO>> query(DefinitionDTO dto) {
        log.error("服务调用失败，报错原因：{}",throwable.getMessage());
        return new HttpResultUtil<>();
    }

    @Override
    public void diagram(String definitionId) {
        log.error("服务调用失败，报错原因：{}",throwable.getMessage());
    }

    @Override
    public HttpResultUtil<Boolean> delete(String deploymentId) {
        log.error("服务调用失败，报错原因：{}",throwable.getMessage());
        return new HttpResultUtil<>();
    }

    @Override
    public HttpResultUtil<Boolean> suspend(String definitionId) {
        log.error("服务调用失败，报错原因：{}",throwable.getMessage());
        return new HttpResultUtil<>();
    }

    @Override
    public HttpResultUtil<Boolean> activate(String definitionId) {
        log.error("服务调用失败，报错原因：{}",throwable.getMessage());
        return new HttpResultUtil<>();
    }
}
