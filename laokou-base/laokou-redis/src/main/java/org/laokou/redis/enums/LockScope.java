package org.laokou.redis.enums;

/**
 * @author Kou Shenhai
 */

public enum  LockScope {

    /**
     * 本地锁
     */
    STANDALONE_LOCK,

    /**
     * 分布式锁
     */
    DISTRIBUTED_LOCK

}
