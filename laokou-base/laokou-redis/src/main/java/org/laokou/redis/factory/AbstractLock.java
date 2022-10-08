package org.laokou.redis.factory;

import org.laokou.redis.enums.LockType;

/**
 * @author Kou Shenhai
 */
public abstract class AbstractLock<T> {

    protected T lock;

    /**
     * 获取锁
     * @param type
     * @return
     */
    protected abstract T getLock(LockType type);

    /**
     * 获取锁
     */
    protected abstract void lock();

    /**
     * 释放锁
     */
    protected abstract void unlock();

}
