package org.laokou.elasticsearch.client.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class CreateIndexModel implements Serializable {

    private String indexName;
    private String indexAlias;

}
