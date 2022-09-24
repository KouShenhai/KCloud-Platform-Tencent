package org.laokou.elasticsearch.client.index;

import lombok.Data;
import org.laokou.elasticsearch.client.annotation.ElasticsearchFieldInfo;

import java.io.Serializable;

@Data
public class ResourceIndex implements Serializable {

    @ElasticsearchFieldInfo(type = "long")
    private Long id;

    @ElasticsearchFieldInfo(type = "text",participle = 3)
    private String title;

    @ElasticsearchFieldInfo
    private String code;

    @ElasticsearchFieldInfo(type = "text",participle = 3)
    private String remark;

    @ElasticsearchFieldInfo
    private String ym;

}
