package org.laokou.redis.factory;

import org.laokou.redis.enums.LockType;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Kou Shenhai
 */
public class LocalLock extends AbstractLock<Lock> {

    @Override
    public Lock getLock(LockType type,String key) {
        switch (type) {
            case LOCK: return new ReentrantLock();
            case FAIR: return new ReentrantLock(true);
            case READ: return new ReentrantReadWriteLock().readLock();
            case WRITE: return new ReentrantReadWriteLock().writeLock();
            default: return null;
        }
    }

    @Override
    public Boolean tryLock(Lock lock, long expire, long timeout) throws InterruptedException {
        return lock.tryLock(timeout, TimeUnit.SECONDS);
    }

    @Override
    public void unlock(Lock lock) {
        if (lock != null) {
            lock.unlock();
        }
    }

}
