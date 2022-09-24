package org.laokou.elasticsearch.client.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AggregationDTO implements Serializable {

    private String groupKey;
    private String field;
    private String script;

}
