package org.laokou.elasticsearch.client.vo;

import lombok.Data;
import java.util.List;

/**
 * @author Kou Shenhai
 */
@Data
public class SearchVO<T> {

    private Integer pageNum;

    private Integer pageSize;

    private List<T> records;

    /**
     * 数据总条数
     */
    private Long total;

}
