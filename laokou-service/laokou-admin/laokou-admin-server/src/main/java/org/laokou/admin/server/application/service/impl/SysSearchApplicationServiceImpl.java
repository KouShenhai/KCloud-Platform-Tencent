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
package org.laokou.admin.server.application.service.impl;

import feign.FeignException;
import org.laokou.admin.server.application.service.SysSearchApplicationService;
import org.laokou.admin.server.infrastructure.component.feign.elasticsearch.ElasticsearchApiFeignClient;
import org.laokou.common.core.exception.CustomException;
import org.laokou.common.core.exception.ErrorCode;
import org.laokou.common.core.utils.HttpResultUtil;
import org.laokou.elasticsearch.client.form.SearchForm;
import org.laokou.elasticsearch.client.vo.SearchVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;

/**
 * @author Kou Shenhai
 */
@Service
public class SysSearchApplicationServiceImpl implements SysSearchApplicationService {

    @Autowired
    private ElasticsearchApiFeignClient elasticsearchApiFeignClient;

    @Override
    public SearchVO<Map<String,Object>> searchResource(SearchForm form) {
        HttpResultUtil<SearchVO<Map<String, Object>>> result;
        try {
             result = elasticsearchApiFeignClient.highlightSearch(form);
        } catch (FeignException ex) {
            throw new CustomException(ErrorCode.SERVICE_MAINTENANCE);
        }
        return result.getData();
    }
}
