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
