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
package org.laokou.elasticsearch.server.utils;
import lombok.extern.slf4j.Slf4j;
import org.laokou.elasticsearch.client.annotation.ElasticsearchFieldInfo;

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
