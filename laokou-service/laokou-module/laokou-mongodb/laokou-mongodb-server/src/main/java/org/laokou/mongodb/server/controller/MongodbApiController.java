package org.laokou.mongodb.server.controller;

import lombok.RequiredArgsConstructor;
import org.laokou.common.swagger.utils.HttpResult;
import org.laokou.mongodb.client.model.MongodbModel;
import org.laokou.mongodb.client.vo.SearchVO;
import org.laokou.mongodb.server.form.QueryForm;
import org.laokou.mongodb.server.utils.MongodbFieldUtil;
import org.laokou.mongodb.server.utils.MongodbUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * @author laokou
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
//@Api(tags = "Mongodb API 服务")
public class MongodbApiController {

    private final MongodbUtil mongodbUtil;

    @PostMapping("/save")
//    @ApiOperation("保存数据到Mongodb Collection")
    public void saveData(@RequestBody MongodbModel mongodbModel) {
        final String collectionName = mongodbModel.getCollectionName();
        final String jsonData = mongodbModel.getData();
        final Object obj = MongodbFieldUtil.getObj(collectionName,jsonData);
        mongodbUtil.saveData(collectionName,obj);
    }

    @PostMapping("/query")
//    @ApiOperation("多条件查询Mongodb Collection")
    @CrossOrigin
    public HttpResult<SearchVO> queryCollection(@RequestBody final QueryForm queryForm) {
        HttpResult<SearchVO> result = new HttpResult<>();
        result.setData(mongodbUtil.queryData(queryForm));
        return result;
    }

    @PostMapping("/get")
//    @ApiOperation("根据主键查询Mongodb Collection")
    @CrossOrigin
    public HttpResult<Object> getCollectionById(@RequestParam("collectionName")final String collectionName, @RequestParam("id")final String id) {
        HttpResult<Object> result = new HttpResult<>();
        final Class<?> clazz = MongodbFieldUtil.getClazz(collectionName);
        result.setData(mongodbUtil.queryDataById(clazz,id));
        return result;
    }

    @PostMapping("/saveBatch")
//    @ApiOperation("批量保存数据到Mongodb Collection")
    @CrossOrigin
    public void saveDataBatch(@RequestBody MongodbModel mongodbModel) {
        final String collectionName = mongodbModel.getCollectionName();
        final String jsonData = mongodbModel.getData();
        final List<? extends Object> objList = MongodbFieldUtil.getObjList(collectionName,jsonData);
        mongodbUtil.saveDataBatch(collectionName, objList);
    }

    @DeleteMapping("/all")
//    @ApiOperation("清空Mongodb Collection")
    @CrossOrigin
    public void deleteAll(@RequestParam("collectionName")final String collectionName ) {
        mongodbUtil.deleteAll(collectionName);
    }

}
