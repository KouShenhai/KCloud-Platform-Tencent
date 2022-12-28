package org.laokou.mongodb.server.utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.laokou.common.core.utils.JacksonUtil;
import org.laokou.mongodb.client.dto.SearchDTO;
import org.laokou.mongodb.client.vo.SearchVO;
import org.laokou.mongodb.server.form.QueryForm;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
/**
 * @author laokou
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class MongodbUtil {

    private final MongoTemplate mongoTemplate;

    /**
     * 保存数据
     * @param collectionName 集合名称
     * @param objData obj对象
     * @return
     */
    public void saveData(String collectionName,Object objData) {
        mongoTemplate.save(objData,collectionName);
    }

    public Object queryDataById(Class<?> clazz,String id) {
        long startTime = System.currentTimeMillis();
        final Object obj = mongoTemplate.findById(id, clazz);
        log.info("消耗时间：{}ms",(System.currentTimeMillis() - startTime));
        return obj;
    }

    public SearchVO queryData(QueryForm queryForm) {
        final long startTime = System.currentTimeMillis();
        final Query query = new Query();
        final String collectionName = queryForm.getCollectionName();
        final Criteria criteria = new Criteria();
        final List<SearchDTO> likeSearchList = queryForm.getLikeSearchList();
        Integer pageNum = queryForm.getPageNum();
        Integer pageSize = queryForm.getPageSize();
        final int size = likeSearchList.size();
        if (size > 0) {
            Criteria[] likeCriteria = new Criteria[size];
            for (int j = 0; j < size; j++) {
                final SearchDTO searchDTO = likeSearchList.get(j);
                final String machValue = searchDTO.getValue();
                final String machKey = searchDTO.getField();
                final Pattern pattern = Pattern.compile("^.*" + machValue + ".*$", Pattern.CASE_INSENSITIVE);
                likeCriteria[j] = Criteria.where(machKey).regex(pattern);
            }
            criteria.andOperator(likeCriteria);
        }
        query.addCriteria(criteria);
        //分页
        int start = 0;
        int end = 10000;
        if (queryForm.isNeedPage()) {
            start = (pageNum - 1) * pageSize;
            end = pageSize;
        }
        query.skip(start);
        query.limit(end);
        final List<Map> result = mongoTemplate.find(query, Map.class, collectionName);
        final long total = mongoTemplate.count(query, collectionName);
        final SearchVO<Map> searchVO = new SearchVO<>();
        searchVO.setRecords(result);
        searchVO.setPageNum(pageNum);
        searchVO.setPageSize(pageSize);
        searchVO.setTotal(total);
        log.info("返回结果：{}", JacksonUtil.toJsonStr(searchVO));
        log.info("消耗时间：{}ms",(System.currentTimeMillis() - startTime));
        return searchVO;
    }

    public void saveDataBatch(String collectionName,List<? extends Object> dataList) {
        mongoTemplate.insert(dataList,collectionName);
    }

    public void deleteAll(String collectionName) {
        mongoTemplate.dropCollection(collectionName);
    }

}
