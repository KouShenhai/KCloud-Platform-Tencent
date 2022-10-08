package org.laokou.redis.factory;

import org.laokou.redis.enums.LockType;

/**
 * @author Kou Shenhai
 */
public abstract class AbstractLock<T> {

    /**
     * 获取锁
     * @param type
     * @param key
     * @return
     */
    public abstract T getLock(LockType type,String key);

    /**
     * 获取锁
     * @param lock
     * @param expire
     * @param timeout
     * @return
     * @throws InterruptedException
     */
    public abstract Boolean tryLock(T lock,long expire,long timeout) throws InterruptedException;

    /**
     * 释放锁
     * @param lock
     */
    public abstract void unlock(T lock);

}
