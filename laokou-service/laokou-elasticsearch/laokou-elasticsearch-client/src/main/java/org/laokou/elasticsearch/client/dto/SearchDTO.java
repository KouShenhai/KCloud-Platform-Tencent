package org.laokou.elasticsearch.client.dto;

import lombok.Data;

/**
 * 搜索DTO
 * @author Kou Shenhai
 * @version 1.0
 * @date 2022/3/15 0015 上午 9:45
 */
@Data
public class SearchDTO {

    private String field;

    private String value;

    
}
