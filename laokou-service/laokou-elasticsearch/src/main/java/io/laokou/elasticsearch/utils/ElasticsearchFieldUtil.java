package io.laokou.elasticsearch.utils;
import io.laokou.elasticsearch.index.ResourceIndex;

import java.util.HashMap;
import java.util.Map;
/**
 * 索引管理
 * @author Kou Shenhai
 * @version 1.0
 * @date 2021/10/31 0031 上午 10:11
 */
public class ElasticsearchFieldUtil {

    public static final String RESOURCE_INDEX = "laokou_resource";

    private static final Map<String,Class<?>> classMap = new HashMap<>(16);

    static {
        classMap.put(RESOURCE_INDEX, ResourceIndex.class);
    }

    public static Class<?> getClazz(final String indexName) {
        return classMap.getOrDefault(indexName,Object.class);
    }
}
