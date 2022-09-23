package io.laokou.elasticsearch.controller;
import io.laokou.common.utils.HttpResultUtil;
import io.laokou.elasticsearch.form.SearchForm;
import io.laokou.elasticsearch.model.CreateIndexModel;
import io.laokou.elasticsearch.model.ElasticsearchModel;
import io.laokou.elasticsearch.utils.ElasticsearchFieldUtil;
import io.laokou.elasticsearch.utils.ElasticsearchUtil;
import io.laokou.elasticsearch.vo.SearchVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;
/**
 * elasticsearch是分布式搜索引擎
 * @author Kou Shenhai
 * @version 1.0
 * @date 2021/2/8 0008 下午 6:33
 */
@RestController
@RequestMapping("/api")
@Api(value = "索引管理API",protocols = "http",tags = "索引管理API")
public class ElasticsearchApiController {

    @Autowired
    private ElasticsearchUtil elasticsearchUtil;

    @PostMapping("/create")
    @ApiOperation("索引管理>创建")
    @CrossOrigin
    public void create(@RequestBody final CreateIndexModel model) throws IOException {
        String indexName = model.getIndexName();
        String indexAlias = model.getIndexAlias();
        Class<?> clazz = ElasticsearchFieldUtil.getClazz(indexAlias);
        elasticsearchUtil.createIndex(indexName,indexAlias,clazz,true);
    }

    @PostMapping("/sync")
    @ApiOperation("索引管理>同步")
    @CrossOrigin
    public void sync(@RequestBody final ElasticsearchModel model) throws IOException {
        String id = model.getId();
        String indexName = model.getIndexName();
        String indexAlias = model.getIndexAlias();
        String jsonData = model.getData();
        Class<?> clazz = ElasticsearchFieldUtil.getClazz(indexAlias);
        elasticsearchUtil.syncIndex(id,indexName,indexAlias,jsonData,clazz);
    }

    @PostMapping("/syncAsyncBatch")
    @ApiOperation("索引管理>批量异步同步")
    @CrossOrigin
    public void syncAsyncBatch(@RequestBody final ElasticsearchModel model) throws IOException {
        String indexName = model.getIndexName();
        String jsonDataList = model.getData();
        elasticsearchUtil.syncAsyncBatchIndex(indexName,jsonDataList);
    }

    @PostMapping("/syncBatch")
    @ApiOperation("索引管理>批量同步")
    @CrossOrigin
    public void syncBatch(@RequestBody final ElasticsearchModel model) throws IOException {
        String indexName = model.getIndexName();
        String jsonDataList = model.getData();
        elasticsearchUtil.syncBatchIndex(indexName,jsonDataList);
    }

    @GetMapping("/detail")
    @ApiOperation("索引管理>详情")
    @CrossOrigin
    @ApiImplicitParams({
            @ApiImplicitParam(name = "indexName",value = "索引名称",required = true,paramType = "query",dataType = "String"),
            @ApiImplicitParam(name = "id",value = "主键",required = true,paramType = "query",dataType = "String")
    })
    public HttpResultUtil<String> detail(@RequestParam("indexName")final String indexName,@RequestParam("id")final String id) {
        return new HttpResultUtil<String>().ok(elasticsearchUtil.getIndexById(indexName,id));
    }

    @PutMapping("/updateBatch")
    @ApiOperation("索引管理>批量修改")
    @CrossOrigin
    public void updateBatch(@RequestBody final ElasticsearchModel model) throws IOException {
        String indexName = model.getIndexName();
        String indexAlias = model.getIndexAlias();
        String jsonDataList = model.getData();
        Class<?> clazz = ElasticsearchFieldUtil.getClazz(indexAlias);
        elasticsearchUtil.updateBatchIndex(indexName,indexAlias,jsonDataList,clazz);
    }

    @PutMapping("/update")
    @ApiOperation("索引管理>修改")
    @CrossOrigin
    public void update(@RequestBody final ElasticsearchModel model) {
        String id = model.getId();
        String indexName = model.getIndexName();
        String paramJson = model.getData();
        elasticsearchUtil.updateIndex(indexName,id,paramJson);
    }

    @DeleteMapping("/deleteBatch")
    @ApiOperation("索引管理>批量删除")
    @CrossOrigin
    public void deleteBatch(@RequestParam("indexName")final String indexName,@RequestParam("ids")final List<String> ids) {
        elasticsearchUtil.deleteBatchIndex(indexName,ids);
    }

    @DeleteMapping("/delete")
    @ApiOperation("索引管理>删除")
    @CrossOrigin
    public void delete(@RequestParam("indexName")final String indexName,@RequestParam("id")final String id) {
        elasticsearchUtil.deleteIndex(indexName,id);
    }

    @DeleteMapping("/deleteAll")
    @ApiOperation("索引管理>清空")
    @CrossOrigin
    public void deleteAll(@RequestParam("indexName")final String indexName) {
        elasticsearchUtil.deleteAllIndex(indexName);
    }

    @PostMapping("/highlightSearch")
    @ApiOperation("索引管理>高亮搜索")
    @CrossOrigin
    public HttpResultUtil<SearchVO<Map<String,Object>>> highlightSearch(@RequestBody final SearchForm searchForm) throws IOException {
        return new HttpResultUtil<SearchVO<Map<String,Object>>>().ok(elasticsearchUtil.highlightSearchIndex(searchForm));
    }

    @PostMapping("/aggregationSearch")
    @ApiOperation("索引管理>聚合搜索")
    @CrossOrigin
    public HttpResultUtil<SearchVO<Map<String,Long>>> aggregationSearch(@RequestBody final SearchForm searchForm) throws IOException {
        return new HttpResultUtil<SearchVO<Map<String,Long>>>().ok(elasticsearchUtil.aggregationSearchIndex(searchForm));
    }

}
