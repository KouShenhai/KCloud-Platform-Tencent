package org.laokou.elasticsearch.utils;
import org.laokou.elasticsearch.annotation.ElasticsearchFieldInfo;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
/**
 * 每个属性对应的类型及分词器
 * @author Kou Shenhai
 * @version 1.0
 * @date 2021/1/24 0024 下午 7:51
 */
@Slf4j
public class FieldMappingUtil {

    public static List<FieldMapping> getFieldInfo(Class clazz) {
        //返回class中的所有字段（包括私有字段）
        Field[] fields = clazz.getDeclaredFields();
        //创建FieldMapping集合
        List<FieldMapping> fieldMappingList = new ArrayList<>();
        for (Field field : fields) {
            //获取字段上的FieldInfo对象
            boolean annotationPresent = field.isAnnotationPresent(ElasticsearchFieldInfo.class);
            if (annotationPresent) {
                ElasticsearchFieldInfo elasticsearchFieldInfo = field.getAnnotation(ElasticsearchFieldInfo.class);
                //获取字段名称
                String name = field.getName();
                fieldMappingList.add(new FieldMapping(name, elasticsearchFieldInfo.type(), elasticsearchFieldInfo.participle()));
            } else {
                continue;
            }
        }
        return fieldMappingList;
    }
}
