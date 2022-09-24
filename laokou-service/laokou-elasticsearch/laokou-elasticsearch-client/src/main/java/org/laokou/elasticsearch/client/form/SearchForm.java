package org.laokou.elasticsearch.client.form;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.laokou.elasticsearch.client.dto.AggregationDTO;
import org.laokou.elasticsearch.client.dto.SearchDTO;

import java.io.Serializable;
import java.util.List;
/**
 * 搜索
 * @author Kou Shenhai
 * @version 1.0
 * @date 2020/8/9 0009 下午 5:16
 */
@Data
@ApiModel(description = "搜索实体类")
public class SearchForm implements Serializable {

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 条数
     */
    private Integer pageSize = 10000;

    /**
     * 是否分页
     */
    private boolean needPage = true;

    /**
     * 查询索引名称
     */
    private String[] indexNames;

    /**
     * 分词搜索
     */
    private List<SearchDTO> queryStringList;

    /**
     * 排序
     */
    private List<SearchDTO> sortFieldList;

    /**
     * 高亮搜索字段
     */
    private List<String> highlightFieldList;

    /**
     * or搜索-精准匹配
     */
    private List<SearchDTO> orSearchList;

    /**
     * 聚合字段
     */
    private AggregationDTO aggregationKey;

}
