package org.laokou.redis.enums;

/**
 * @author Kou Shenhai
 */

public enum LockType {

    /**
     * 普通锁
     */
    LOCK,

    /**
     * 公平锁
     */
    FAIR,

    /**
     * 读锁
     */
    READ,

    /**
     * 写锁
     */
    WRITE

}
