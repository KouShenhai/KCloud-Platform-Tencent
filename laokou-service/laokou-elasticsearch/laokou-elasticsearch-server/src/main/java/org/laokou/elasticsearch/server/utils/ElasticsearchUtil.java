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
package org.laokou.elasticsearch.server.utils;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.*;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.laokou.elasticsearch.client.constant.EsConstant;
import org.laokou.elasticsearch.client.dto.AggregationDTO;
import org.laokou.elasticsearch.client.dto.SearchDTO;
import org.laokou.elasticsearch.client.form.SearchForm;
import org.laokou.elasticsearch.client.vo.SearchVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import java.io.IOException;
import java.util.*;
/**
 * Elasticsearch工具类
 * @author Kou Shenhai
 * @version 1.0
 * @date 2021/1/24 0024 下午 5:42
 */
@Slf4j
@Component
public class ElasticsearchUtil {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    private static final String PRIMARY_KEY_NAME = "id";

    private static final String HIGHLIGHT_PRE_TAGS = "<span style='color:red;'>";

    private static final String HIGHLIGHT_POST_TAGS = "</span>";

    /**
     * 批量同步数据到ES
     * @param indexName 索引名称
     * @param jsonDataList 数据列表
     * @return
     * @throws IOException
     */
    public boolean syncBatchIndex(String indexName,String jsonDataList) throws IOException {
        //判空
        if (StringUtils.isBlank(jsonDataList)) {
            return false;
        }
        //批量操作Request
        BulkRequest bulkRequest = packBulkIndexRequest(indexName, jsonDataList);
        if (bulkRequest.requests().isEmpty()) {
            return false;
        }
        final BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        if (bulk.hasFailures()) {
            for (BulkItemResponse item : bulk.getItems()) {
                log.error("索引[{}],主键[{}]更新操作失败，状态为:[{}],错误信息:{}",indexName,item.getId(),item.status(),item.getFailureMessage());
            }
            return false;
        }
        //记录索引新增与修改数量
        Integer createdCount = 0;
        Integer updatedCount = 0;
        for (BulkItemResponse item : bulk.getItems()) {
            if (DocWriteResponse.Result.CREATED.equals(item.getResponse().getResult())) {
                createdCount++;
            } else if (DocWriteResponse.Result.UPDATED.equals(item.getResponse().getResult())){
                updatedCount++;
            }
        }
        log.info("索引[{}]批量同步更新成功，共新增[{}]个，修改[{}]个",indexName,createdCount,updatedCount);
        return true;
    }

    /**
     * 批量修改ES
     * @param indexName 索引名称
     * @param indexAlias 别名
     * @param jsonDataList 数据列表
     * @param clazz 类型
     * @return
     * @throws IOException
     */
    public boolean updateBatchIndex(String indexName,String indexAlias, String jsonDataList,Class clazz) throws IOException {
        if (!syncIndex(indexName,indexAlias,clazz)) {
            return false;
        }
        return this.updateDataBatch(indexName,jsonDataList);
    }

    /**
     * 同步索引
     * @param indexName 索引名称
     * @param indexAlias 索引别名
     * @param clazz 类型
     * @return
     * @throws IOException
     */
    private boolean syncIndex(String indexName,String indexAlias,Class clazz) throws IOException {
        //创建索引
        if (!isIndexExists(indexName)) {
            if (!createIndex(indexName, indexAlias, clazz, false)) {
                return false;
            }
        }
        return true;
    }

    /**
     * ES修改数据
     * @param indexName 索引名称
     * @param id 主键
     * @param paramJson 参数JSON
     * @return
     */
    public boolean updateIndex(String indexName,String id,String paramJson) {
        UpdateRequest updateRequest = new UpdateRequest(indexName, id);
        //如果修改索引中不存在则进行新增
        updateRequest.docAsUpsert(true);
        //立即刷新数据
        updateRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        updateRequest.doc(paramJson,XContentType.JSON);
        try {
            UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
            log.info("索引[{}],主键：【{}】操作结果:[{}]",indexName,id,updateResponse.getResult());
            if (DocWriteResponse.Result.CREATED.equals(updateResponse.getResult())) {
                //新增
                log.info("索引：【{}】,主键：【{}】新增成功",indexName,id);
                return true;
            } else if (DocWriteResponse.Result.UPDATED.equals(updateResponse.getResult())) {
                //修改
                log.info("索引：【{}】，主键：【{}】修改成功",indexName, id);
                return true;
            } else if (DocWriteResponse.Result.NOOP.equals(updateResponse.getResult())) {
                //无变化
                log.info("索引:[{}],主键:[{}]无变化",indexName, id);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("索引：[{}],主键：【{}】，更新异常:[{}]",indexName, id,e);
            return false;
        }
        return false;
    }

    /**
     * 删除数据
     * @param indexName 索引名称
     * @param id 主键
     * @return
     */
    public boolean deleteIndex(String indexName,String id) {
        DeleteRequest deleteRequest = new DeleteRequest(indexName);
        deleteRequest.id(id);
        deleteRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        try {
            DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
            if (DocWriteResponse.Result.NOT_FOUND.equals(deleteResponse.getResult())) {
                log.error("索引：【{}】，主键：【{}】删除失败",indexName, id);
                return false;
            } else {
                log.info("索引【{}】主键【{}】删除成功",indexName, id);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("删除索引【{}】出现异常[{}]",indexName,e);
            return false;
        }
    }

    /**
     * 批量删除ES
     * @param indexName 索引名称
     * @param ids id列表
     * @return
     */
    public boolean deleteBatchIndex(String indexName,List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return false;
        }
        BulkRequest bulkRequest = packBulkDeleteRequest(indexName, ids);
        try {
            BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            if (bulk.hasFailures()) {
                for (BulkItemResponse item : bulk.getItems()) {
                    log.error("删除索引:[{}],主键：{}失败，信息：{}",indexName,item.getId(),item.getFailureMessage());
                }
                return false;
            }
            //记录索引新增与修改数量
            Integer deleteCount = 0;
            for (BulkItemResponse item : bulk.getItems()) {
                if (DocWriteResponse.Result.DELETED.equals(item.getResponse().getResult())) {
                    deleteCount++;
                }
            }
            log.info("批量删除索引[{}]成功，共删除[{}]个",indexName,deleteCount);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            log.error("删除索引：【{}】出现异常:{}",indexName,e);
            return false;
        }
    }

    /**
     * 组装删除操作
     * @param indexName 索引名称
     * @param ids id列表
     * @return
     */
    private BulkRequest packBulkDeleteRequest(String indexName, List<String> ids) {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        ids.forEach(id -> {
            DeleteRequest deleteRequest = new DeleteRequest(indexName);
            deleteRequest.id(id);
            bulkRequest.add(deleteRequest);
        });
        return bulkRequest;
    }

    /**
     * 批量修改ES
     * @param indexName 索引名称
     * @param jsonDataList json数据列表
     * @return
     */
    public boolean updateDataBatch(String indexName, String jsonDataList) {
        //判空
        if (StringUtils.isBlank(jsonDataList)) {
            return false;
        }
        BulkRequest bulkRequest = packBulkUpdateRequest(indexName, jsonDataList);
        if (bulkRequest.requests().isEmpty()) {
            return false;
        }
        try {
            //同步执行
            BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            if (bulk.hasFailures()) {
                for (BulkItemResponse item : bulk.getItems()) {
                    log.error("索引【{}】,主键[{}]修改操作失败，状态为【{}】,错误信息：{}",indexName,item.status(),item.getFailureMessage());
                }
                return false;
            }
            //记录索引新增与修改数量
            Integer createCount = 0;
            Integer updateCount = 0;
            for (BulkItemResponse item : bulk.getItems()) {
                if (DocWriteResponse.Result.CREATED.equals(item.getResponse().getResult())) {
                    createCount++;
                } else if (DocWriteResponse.Result.UPDATED.equals(item.getResponse().getResult())) {
                    updateCount++;
                }
            }
            log.info("索引【{}】批量修改更新成功，共新增[{}]个，修改[{}]个",indexName,createCount,updateCount);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("索引【{}】批量修改更新出现异常",indexName);
            return false;
        }
        return true;
    }

    /**
     * 组装bulkUpdate
     * @param indexName 索引名称
     * @param jsonDataList 数据列表
     * @return
     */
    private BulkRequest packBulkUpdateRequest(String indexName,String jsonDataList) {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        JSONArray jsonArray = JSONUtil.parseArray(jsonDataList);
        if (jsonArray.isEmpty()) {
            return bulkRequest;
        }
        //循环数据封装bulkRequest
        jsonArray.forEach(obj ->{
            final Map<String, Object> map = (Map<String, Object>) obj;
            UpdateRequest updateRequest = new UpdateRequest(indexName,map.get(PRIMARY_KEY_NAME).toString());
            // 修改索引中不存在就新增
            updateRequest.docAsUpsert(true);
            updateRequest.doc(map,XContentType.JSON);
            bulkRequest.add(updateRequest);
        });
        return bulkRequest;
    }

    /**
     * 根据主键查询ES
     * @param indexName 索引名称
     * @param id 主键
     * @return
     */
    public String getIndexById(String indexName,String id) {
        //判断索引是否存在
        boolean result = isIndexExists(indexName);
        if (!result) {
            return "";
        }
        GetRequest getRequest = new GetRequest(indexName, id);
        try {
            GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
            String resultJson = getResponse.getSourceAsString();
            log.info("索引【{}】主键【{}】，查询结果：【{}】",indexName,id,resultJson);
            return resultJson;
        } catch (IOException e) {
            e.printStackTrace();
            log.error("索引【{}】主键[{}]，查询异常：{}",indexName,id,e);
            return "";
        }
    }

    /**
     * 清空索引内容
     * @param indexName 索引名称
     * @return
     */
    public boolean deleteAllIndex(String indexName) {
        //判断索引是否存在
        boolean result = isIndexExists(indexName);
        if (!result) {
            log.error("索引【{}】不存在，删除失败",indexName);
            return false;
        }
        DeleteRequest deleteRequest = new DeleteRequest(indexName);
        deleteRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        try {
            DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
            if (DocWriteResponse.Result.NOT_FOUND.equals(deleteResponse.getResult())) {
                log.error("索引【{}】删除失败",indexName);
                return false;
            }
            log.info("索引【{}】删除成功",indexName);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            log.error("删除索引[{}]，出现异常[{}]",indexName,e);
            return false;
        }
    }

    /**
     * 批量数据保存到ES-异步
     * @param indexName 索引名称
     * @param jsonDataList 数据列表
     * @return
     * @throws IOException
     */
    public boolean syncAsyncBatchIndex(String indexName,String jsonDataList) throws IOException {
        //判空
        if (StringUtils.isBlank(jsonDataList)) {
            return false;
        }
        //批量操作Request
        BulkRequest bulkRequest = packBulkIndexRequest(indexName, jsonDataList);
        if (bulkRequest.requests().isEmpty()) {
            return false;
        }
        //异步执行
        ActionListener<BulkResponse> listener = new ActionListener<BulkResponse>() {
            @Override
            public void onResponse(BulkResponse bulkItemResponses) {
                if (bulkItemResponses.hasFailures()) {
                    for (BulkItemResponse item : bulkItemResponses.getItems()) {
                        log.error("索引【{}】,主键【{}】更新失败，状态【{}】，错误信息：{}",indexName,item.getId(),
                                item.status(),item.getFailureMessage());
                    }
                }
            }
            //失败操作
            @Override
            public void onFailure(Exception e) {
                log.error("索引【{}】批量异步更新出现异常:{}",indexName,e);
            }
        };
        restHighLevelClient.bulkAsync(bulkRequest,RequestOptions.DEFAULT,listener);
        log.info("索引批量更新索引【{}】中",indexName);
        return true;
    }

    /**
     * 删除索引
     * @param indexName 索引名称
     * @return
     * @throws IOException
     */
    public boolean deleteIndex(String indexName) throws IOException {
        //判断索引是否存在
        boolean result = isIndexExists(indexName);
        if (!result) {
            log.error("索引【{}】不存在，删除失败",indexName);
            return true;
        }
        //删除操作Request
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(indexName);
        deleteIndexRequest.indicesOptions(IndicesOptions.LENIENT_EXPAND_OPEN);
        AcknowledgedResponse acknowledgedResponse = restHighLevelClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
        if (!acknowledgedResponse.isAcknowledged()) {
            log.error("索引【{}】删除失败",indexName);
            return false;
        }
        log.info("索引【{}】删除成功",indexName);
        return true;
    }

    /**
     * 批量操作的Request
     * @param indexName 索引名称
     * @param jsonDataList json数据列表
     * @return
     */
    private BulkRequest packBulkIndexRequest(String indexName,String jsonDataList) {
        BulkRequest bulkRequest = new BulkRequest();
        //IMMEDIATE > 请求向es提交数据，立即进行数据刷新<实时性高，资源消耗高>
        //WAIT_UNTIL >  请求向es提交数据，等待数据完成刷新<实时性高，资源消耗低>
        //NONE > 默认策略<实时性低>
        bulkRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        JSONArray jsonArray = JSONUtil.parseArray(jsonDataList);
        if (jsonArray.isEmpty()) {
            return bulkRequest;
        }
        //循环数据封装bulkRequest
        jsonArray.forEach(obj ->{
            final Map<String, Object> map = (Map<String, Object>) obj;
            IndexRequest indexRequest = new IndexRequest(indexName);
            indexRequest.source(JSONUtil.toJsonStr(obj),XContentType.JSON);
            indexRequest.id(map.get(PRIMARY_KEY_NAME).toString());
            bulkRequest.add(indexRequest);
        });
        return bulkRequest;
    }

    /**
     * 创建ES索引
     * @param indexName 索引名称
     * @param indexAlias 别名
     * @param clazz 类型
     * @param isDel 是否删除索引 true删除 false不删除
     * @return
     * @throws IOException
     */
    public boolean createIndex(String indexName,String indexAlias,Class clazz,boolean isDel) throws IOException {
        //删除索引
        if (isDel) {
             if (!deleteIndex(indexName)) {
                 return false;
             }
        }
        //创建索引
        boolean createResult = createIndexAndCreateMapping(indexName,indexAlias, FieldMappingUtil.getFieldInfo(clazz));
        if (!createResult) {
            log.info("索引【{}】创建失败",indexName);
            return false;
        }
        log.info("索引：[{}]创建成功",indexName);
        return true;
    }

    /**
     * 数据同步到ES
     * @param id 主键
     * @param indexName 索引名称
     * @param jsonData json数据
     * @param clazz 类型
     * @return
     */
    public boolean syncIndex(String id,String indexName,String indexAlias,String jsonData,Class clazz) throws IOException {
        //创建索引
        if (!syncIndex(indexName,indexAlias,clazz)) {
            return false;
        }
        //创建操作Request
        IndexRequest indexRequest = new IndexRequest(indexName);
        //配置相关信息
        indexRequest.source(jsonData, XContentType.JSON);
        //IMMEDIATE > 立即刷新
        indexRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        indexRequest.id(id);
        IndexResponse response = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        //判断索引是新增还是修改
        if (IndexResponse.Result.CREATED.equals(response.getResult())) {
            log.info("索引【{}】保存成功",indexName);
            return true;
        } else if (IndexResponse.Result.UPDATED.equals(response.getResult())) {
            log.info("索引【{}】修改成功",indexName);
            return true;
        }
        return false;
    }

    /**
     * 判断索引是否存在
     * @param indexName 索引名称
     * @return
     */
    public boolean isIndexExists(String indexName) {
        try {
            GetIndexRequest getIndexRequest = new GetIndexRequest(indexName);
            return restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 创建索引设置相关配置信息
     * @param indexName 索引名称
     * @param indexAlias 索引别名
     * @param fieldMappingList 数据列表
     * @return
     * @throws IOException
     */
    private boolean createIndexAndCreateMapping(String indexName,String indexAlias, List<FieldMapping> fieldMappingList) throws IOException {
        //封装es索引的mapping
        XContentBuilder mapping = packEsMapping(fieldMappingList, null);
        mapping.endObject().endObject();
        mapping.close();
        //进行索引的创建
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
        //配置分词器
        XContentBuilder settings = packSettingMapping();
        XContentBuilder aliases = packEsAliases(indexAlias);
        createIndexRequest.settings(settings);
        createIndexRequest.mapping(mapping);
        createIndexRequest.aliases(aliases);
        //同步方式创建索引
        CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        boolean acknowledged = createIndexResponse.isAcknowledged();
        if (acknowledged) {
            log.info("索引:{}创建成功", indexName);
            return true;
        } else {
            log.error("索引:{}创建失败", indexName);
            return false;
        }
    }

    /**
     * 配置ES别名
     * @author Kou Shenhai
     * @param alias 别名
     * @return
     * @throws IOException
     */
    private XContentBuilder packEsAliases(String alias) throws IOException{
        XContentBuilder aliases = XContentFactory.jsonBuilder().startObject()
                .startObject(alias).endObject();
        aliases.endObject();
        aliases.close();
        return aliases;
    }

    /**
     * 配置Mapping
     * @param fieldMappingList 组装的实体类信息
     * @param mapping
     * @return
     * @throws IOException
     */
    private XContentBuilder packEsMapping(List<FieldMapping> fieldMappingList,XContentBuilder mapping) throws IOException {
        if (mapping == null) {
            //如果对象是空，首次进入，设置开始结点
            mapping = XContentFactory.jsonBuilder().startObject()
                    .field("dynamic",true)
                    .startObject("properties");
        }
        //循环实体对象的类型集合封装ES的Mapping
        for (FieldMapping fieldMapping : fieldMappingList) {
            String field = fieldMapping.getField();
            String dataType = fieldMapping.getType();
            Integer participle = fieldMapping.getParticiple();
            //设置分词规则
            if (EsConstant.NOT_ANALYZED.equals(participle)) {
                if ("date".equals(dataType)) {
                    mapping.startObject(field)
                            .field("type", dataType)
                            .field("format","yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis")
                            .endObject();
                } else {
                    mapping.startObject(field)
                            .field("type", dataType)
                            .endObject();
                }
            } else if (EsConstant.IK_INDEX.equals(participle)) {
                mapping.startObject(field)
                        .field("type",dataType)
                        .field("eager_global_ordinals",true)
                        .field("boost",10)
                        //fielddata=true 用来解决text字段不能进行聚合操作
                        .field("fielddata",true)
                        .field("analyzer","ik_pinyin")
                        .field("search_analyzer","ik_max_word")
                        .endObject();
            }
        }
        return mapping;
    }

    /**
     * 配置Settings
     * @return
     * @throws IOException
     */
    private XContentBuilder packSettingMapping() throws IOException {
        XContentBuilder setting = XContentFactory.jsonBuilder().startObject()
                .startObject("index")
                .field("number_of_shards",1)
                .field("number_of_replicas",1)
                .field("refresh_interval","30s")
                .startObject("analysis");
        //ik分词拼音
        setting.startObject("analyzer")
                .startObject("ik_pinyin")
                .field("tokenizer","ik_max_word")
                .field("filter", "laokou_pinyin")
                .endObject();
        setting.endObject();
        //设置拼音分词器分词
        setting.startObject("filter")
                .startObject("laokou_pinyin")
                .field("type", "pinyin")
                // 不会这样划分：刘德华 > [liu,de,hua]
                .field("keep_full_pinyin", false)
                // 这样划分：刘德华 > [liudehua]
                .field("keep_joined_full_pinyin",true)
                //保留原始中文
                .field("keep_original", true)
                .field("limit_first_letter_length", 16)
                .field("remove_duplicated_term", true)
                .field("none_chinese_pinyin_tokenize", false)
                .endObject()
                .endObject();
        setting.endObject().endObject().endObject();
        setting.close();
        return setting;
    }

    /**
     * 关键字高亮显示
     * @param searchForm 查询实体类
     * @return
     * @throws IOException
     */
    public SearchVO<Map<String,Object>> highlightSearchIndex(SearchForm searchForm) throws IOException {
        final String[] indexNames = searchForm.getIndexNames();
        //用于搜索文档，聚合，定制查询有关操作
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(indexNames);
        searchRequest.source(buildSearchSource(searchForm,true,null));
        SearchHits hits = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT).getHits();
        List<Map<String,Object>> data = new ArrayList<>();
        for (SearchHit hit : hits){
            Map<String,Object> sourceData = hit.getSourceAsMap();
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            for (String key : highlightFields.keySet()){
                sourceData.put(key,highlightFields.get(key).getFragments()[0].string());
            }
            data.add(sourceData);
        }
        SearchVO<Map<String,Object>> vo = new SearchVO();
        final long total = hits.getTotalHits().value;
        vo.setRecords(data);
        vo.setTotal(total);
        vo.setPageNum(searchForm.getPageNum());
        vo.setPageSize(searchForm.getPageSize());
        return vo;
    }

    /**
     * 构建query
     * @param searchForm
     * @return
     */
    private BoolQueryBuilder buildBoolQuery(SearchForm searchForm) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //分词查询
        final List<SearchDTO> queryStringList = searchForm.getQueryStringList();
        //or查询
        final List<SearchDTO> orSearchList = searchForm.getOrSearchList();
        if (CollectionUtil.isNotEmpty(orSearchList)) {
            //or查询
            BoolQueryBuilder orQuery = QueryBuilders.boolQuery();
            for (SearchDTO dto : orSearchList) {
                orQuery.should(QueryBuilders.termQuery(dto.getField(), dto.getValue()));
            }
            boolQueryBuilder.must(orQuery);
        }
        if (CollectionUtil.isNotEmpty(queryStringList)) {
            //分词查询
            BoolQueryBuilder analysisQuery = QueryBuilders.boolQuery();
            for (SearchDTO dto : queryStringList) {
                final String field = dto.getField();
                //清除左右空格并处理特殊字符
                final String keyword = QueryParser.escape(dto.getValue().trim());
                analysisQuery.should(QueryBuilders.queryStringQuery(keyword).field(field));
            }
            boolQueryBuilder.must(analysisQuery);
        }
        return boolQueryBuilder;
    }

    /**
     * 构建搜索
     * @param searchForm
     * @param isHighlightSearchFlag
     * @param aggregationBuilder
     * @return
     */
    private SearchSourceBuilder buildSearchSource(SearchForm searchForm, boolean isHighlightSearchFlag, TermsAggregationBuilder aggregationBuilder) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        final Integer pageNum = searchForm.getPageNum();
        final Integer pageSize = searchForm.getPageSize();
        final List<SearchDTO> sortFieldList = searchForm.getSortFieldList();
        if (isHighlightSearchFlag) {
            final List<String> highlightFieldList = searchForm.getHighlightFieldList();
            //高亮显示数据
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            //设置关键字显示颜色
            highlightBuilder.preTags(HIGHLIGHT_PRE_TAGS);
            highlightBuilder.postTags(HIGHLIGHT_POST_TAGS);
            //设置显示的关键字
            for (String field : highlightFieldList) {
                highlightBuilder.field(field, 0, 0);
            }
            //多个字段高亮,这项要为false
            highlightBuilder.requireFieldMatch(false);
            //设置高亮
            searchSourceBuilder.highlighter(highlightBuilder);
        }
        //分页
        if (searchForm.isNeedPage()) {
            final int pageIndex = (pageNum - 1) * pageSize;
            searchSourceBuilder.from(pageIndex);
            searchSourceBuilder.size(pageSize);
        }
        //追踪分数开启
        searchSourceBuilder.trackScores(true);
        //注解
        searchSourceBuilder.explain(true);
        //匹配度倒排，数值越大匹配度越高
        searchSourceBuilder.sort("_score",SortOrder.DESC);
        //排序
        if (CollectionUtil.isNotEmpty(sortFieldList)) {
            for (SearchDTO dto : sortFieldList) {
                SortOrder sortOrder;
                final String desc = "desc";
                final String value = dto.getValue();
                final String field = dto.getField();
                if (desc.equalsIgnoreCase(value)) {
                    sortOrder = SortOrder.DESC;
                } else {
                    sortOrder = SortOrder.ASC;
                }
                searchSourceBuilder.sort(field, sortOrder);
            }
        }
        searchSourceBuilder.query(buildBoolQuery(searchForm));
        //获取真实总数
        searchSourceBuilder.trackTotalHits(true);
        //聚合对象
        if (null != aggregationBuilder) {
            searchSourceBuilder.aggregation(aggregationBuilder);
        }
        return searchSourceBuilder;
    }

    /**
     * 聚合查询
     * @return
     */
    public SearchVO<Map<String,Long>> aggregationSearchIndex(SearchForm searchForm) throws IOException {
        SearchVO vo = new SearchVO();
        List<Map<String,Long>> list = Lists.newArrayList();
        String[] indexNames = searchForm.getIndexNames();
        AggregationDTO aggregationKey = searchForm.getAggregationKey();
        String field = aggregationKey.getField();
        String groupKey = aggregationKey.getGroupKey();
        String script = aggregationKey.getScript();
        TermsAggregationBuilder aggregationBuilder;
        if (StringUtils.isNotBlank(field)) {
            aggregationBuilder = AggregationBuilders.terms(groupKey).field(field).size(100000);
        } else {
            aggregationBuilder = AggregationBuilders.terms(groupKey).script(new Script(script)).size(100000);
        }
        //用于搜索文档，聚合，定制查询有关操作
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(indexNames);
        searchRequest.source(buildSearchSource(searchForm, false, aggregationBuilder));
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        Aggregations aggregations = searchResponse.getAggregations();
        Terms aggregation = aggregations.get(groupKey);
        List<? extends Terms.Bucket> buckets = aggregation.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            Map<String,Long> dataMap = new HashMap<>(1);
            dataMap.put(bucket.getKeyAsString(),bucket.getDocCount());
            list.add(dataMap);
        }
        vo.setRecords(list);
        vo.setPageNum(searchForm.getPageNum());
        vo.setPageSize(searchForm.getPageSize());
        vo.setTotal((long) list.size());
        return vo;
    }

}
