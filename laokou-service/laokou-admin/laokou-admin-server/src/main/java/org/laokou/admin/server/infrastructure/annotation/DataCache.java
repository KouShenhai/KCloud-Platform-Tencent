package org.laokou.admin.server.infrastructure.annotation;

import java.lang.annotation.*;

/**
 * @author laokou
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataCache {

    /**
     * 缓存名称
     */
    String name();

    /**
     * 缓存键
     */
    String key();

    /**
     * 过期时间
     * 单位秒
     * 默认10分钟
     * @return
     */
    int expire() default 600;

    /**
     * 操作类型
     */


}
