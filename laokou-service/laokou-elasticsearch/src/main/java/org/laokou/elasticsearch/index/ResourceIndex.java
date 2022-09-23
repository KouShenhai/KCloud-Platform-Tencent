package org.laokou.elasticsearch.index;

import org.laokou.elasticsearch.annotation.ElasticsearchFieldInfo;
import lombok.Data;

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
