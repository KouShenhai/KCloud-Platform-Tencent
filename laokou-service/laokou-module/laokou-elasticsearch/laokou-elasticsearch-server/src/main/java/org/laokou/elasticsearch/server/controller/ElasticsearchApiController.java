/**
 * Copyright (c) 2022 KCloud-Platform-Tencent Authors. All Rights Reserved.
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
package org.laokou.elasticsearch.server.controller;
import lombok.RequiredArgsConstructor;
import org.laokou.common.core.utils.HttpResultUtil;
import org.laokou.elasticsearch.client.form.SearchForm;
import org.laokou.elasticsearch.client.dto.CreateIndexDTO;
import org.laokou.elasticsearch.client.dto.ElasticsearchDTO;
import org.laokou.elasticsearch.client.vo.SearchVO;
import org.laokou.elasticsearch.server.utils.ElasticsearchFieldUtil;
import org.laokou.elasticsearch.server.utils.ElasticsearchUtil;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;
/**
 * @author laokou
 */
@RestController
@RequestMapping("/api")
//@Api(value = "索引管理API",protocols = "http",tags = "索引管理API")
@RequiredArgsConstructor
public class ElasticsearchApiController {

    private final ElasticsearchUtil elasticsearchUtil;

    @PostMapping("/create")
//    @ApiOperation("索引管理>创建")
    public HttpResultUtil<Boolean> create(@RequestBody final CreateIndexDTO model) throws IOException {
        String indexName = model.getIndexName();
        String indexAlias = model.getIndexAlias();
        Class<?> clazz = ElasticsearchFieldUtil.getClazz(indexAlias);
        return new HttpResultUtil<Boolean>().ok(elasticsearchUtil.createIndex(indexName,indexAlias,clazz));
    }

    @PostMapping("/createAsync")
//    @ApiOperation("索引管理>异步创建")
    public void createAsync(@RequestBody final CreateIndexDTO model) throws IOException {
        String indexName = model.getIndexName();
        String indexAlias = model.getIndexAlias();
        Class<?> clazz = ElasticsearchFieldUtil.getClazz(indexAlias);
        elasticsearchUtil.createAsyncIndex(indexName,indexAlias,clazz);
    }

    @PostMapping("/sync")
//    @ApiOperation("索引管理>同步")
    public HttpResultUtil<Boolean> sync(@RequestBody final ElasticsearchDTO model) throws IOException {
        String id = model.getId();
        String indexName = model.getIndexName();
        String indexAlias = model.getIndexAlias();
        String jsonData = model.getData();
        Class<?> clazz = ElasticsearchFieldUtil.getClazz(indexAlias);
        return new HttpResultUtil<Boolean>().ok(elasticsearchUtil.syncIndex(id,indexName,indexAlias,jsonData,clazz));
    }

    @PostMapping("/syncAsyncBatch")
//    @ApiOperation("索引管理>批量异步同步")
    public void syncAsyncBatch(@RequestBody final ElasticsearchDTO model) {
        String indexName = model.getIndexName();
        String jsonDataList = model.getData();
        elasticsearchUtil.syncAsyncBatchIndex(indexName,jsonDataList);
    }

    @PostMapping("/syncBatch")
//    @ApiOperation("索引管理>批量同步")
    public HttpResultUtil<Boolean> syncBatch(@RequestBody final ElasticsearchDTO model) throws IOException {
        String indexName = model.getIndexName();
        String jsonDataList = model.getData();
        return new HttpResultUtil<Boolean>().ok(elasticsearchUtil.syncBatchIndex(indexName,jsonDataList));
    }

    @GetMapping("/detail")
//    @ApiOperation("索引管理>详情")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "indexName",value = "索引名称",required = true,paramType = "query",dataType = "String"),
//            @ApiImplicitParam(name = "id",value = "主键",required = true,paramType = "query",dataType = "String")
//    })
    public HttpResultUtil<String> detail(@RequestParam("indexName")final String indexName, @RequestParam("id")final String id) {
        return new HttpResultUtil<String>().ok(elasticsearchUtil.getIndexById(indexName,id));
    }

    @PutMapping("/updateBatch")
//    @ApiOperation("索引管理>批量修改")
    public HttpResultUtil<Boolean> updateBatch(@RequestBody final ElasticsearchDTO model) throws IOException {
        String indexName = model.getIndexName();
        String indexAlias = model.getIndexAlias();
        String jsonDataList = model.getData();
        Class<?> clazz = ElasticsearchFieldUtil.getClazz(indexAlias);
        return new HttpResultUtil<Boolean>().ok(elasticsearchUtil.updateBatchIndex(indexName,indexAlias,jsonDataList,clazz));
    }

    @PutMapping("/update")
//    @ApiOperation("索引管理>修改")
    public HttpResultUtil<Boolean> update(@RequestBody final ElasticsearchDTO model) {
        String id = model.getId();
        String indexName = model.getIndexName();
        String paramJson = model.getData();
        return new HttpResultUtil<Boolean>().ok(elasticsearchUtil.updateIndex(indexName,id,paramJson));
    }

    @DeleteMapping("/deleteBatch")
//    @ApiOperation("索引管理>根据ids批量删除")
    public HttpResultUtil<Boolean> deleteBatch(@RequestParam("indexName")final String indexName,@RequestParam("ids")final List<String> ids) {
        return new HttpResultUtil<Boolean>().ok(elasticsearchUtil.deleteBatchIndex(indexName,ids));
    }

    @DeleteMapping("/deleteById")
//    @ApiOperation("索引管理>根据id删除")
    public HttpResultUtil<Boolean> deleteById(@RequestParam("indexName")final String indexName,@RequestParam("id")final String id) {
        return new HttpResultUtil<Boolean>().ok(elasticsearchUtil.deleteIndexById(indexName,id));
    }

    @DeleteMapping("/deleteAsync")
//    @ApiOperation("索引管理>异步删除")
    public void deleteAsync(@RequestParam("indexName")final String indexName) {
        elasticsearchUtil.deleteAsyncIndex(indexName);
    }

    @DeleteMapping("/deleteAll")
//    @ApiOperation("索引管理>清空")
    public HttpResultUtil<Boolean> deleteAll(@RequestParam("indexName")final String indexName) {
        return new HttpResultUtil<Boolean>().ok(elasticsearchUtil.deleteAllIndex(indexName));
    }

    @DeleteMapping("/delete")
//    @ApiOperation("索引管理>删除")
    public HttpResultUtil<Boolean> delete(@RequestParam("indexName")final String indexName) throws IOException {
        return new HttpResultUtil<Boolean>().ok(elasticsearchUtil.deleteIndex(indexName));
    }

    @PostMapping("/highlightSearch")
//    @ApiOperation("索引管理>高亮搜索")
    public HttpResultUtil<SearchVO<Map<String,Object>>> highlightSearch(@RequestBody final SearchForm searchForm) throws IOException {
        return new HttpResultUtil<SearchVO<Map<String,Object>>>().ok(elasticsearchUtil.highlightSearchIndex(searchForm));
    }

    @PostMapping("/aggregationSearch")
//    @ApiOperation("索引管理>聚合查询")
    public HttpResultUtil<SearchVO<Map<String,Long>>> aggregationSearch(@RequestBody final SearchForm searchForm) throws IOException {
        return new HttpResultUtil<SearchVO<Map<String,Long>>>().ok(elasticsearchUtil.aggregationSearchIndex(searchForm));
    }

}
