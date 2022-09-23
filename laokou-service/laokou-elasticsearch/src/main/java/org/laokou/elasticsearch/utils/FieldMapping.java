package org.laokou.elasticsearch.utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 属性、类型、分词器
 * @author Kou Shenhai
 * @version 1.0
 * @date 2021/2/9 0009 上午 10:20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldMapping {

    private String field;

    private String type;

    private Integer participle;

}
