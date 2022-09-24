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
package org.laokou.admin.client.feign.elasticsearch.fallback;
import org.laokou.admin.client.feign.elasticsearch.ElasticsearchApiFeignClient;
import org.laokou.common.utils.HttpResultUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.laokou.elasticsearch.client.form.SearchForm;
import org.laokou.elasticsearch.client.model.CreateIndexModel;
import org.laokou.elasticsearch.client.model.ElasticsearchModel;
import org.laokou.elasticsearch.client.vo.SearchVO;

import java.util.Map;
/**
 * 服务降级
 * @author Kou Shenhai
 * @version 1.0
 * @date 2020/9/5 0005 上午 12:12
 */
@Slf4j
@AllArgsConstructor
public class ElasticsearchApiFeignClientFallback implements ElasticsearchApiFeignClient {

    private final Throwable throwable;

    @Override
    public void create(CreateIndexModel model) {
        log.error("服务调用失败，报错原因：{}",throwable.getMessage());
    }

    @Override
    public void syncAsyncBatch(ElasticsearchModel model) {
        log.error("服务调用失败，报错原因：{}",throwable.getMessage());
    }

    @Override
    public HttpResultUtil<SearchVO<Map<String,Object>>> highlightSearch(SearchForm searchForm) {
        log.error("服务调用失败，报错原因：{}",throwable.getMessage());
        return new HttpResultUtil<SearchVO<Map<String,Object>>>().error("服务调用失败，请联系管理员");
    }

}