package org.laokou.redis.factory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.laokou.redis.RedisUtil;
import org.laokou.redis.enums.LockType;
import org.redisson.api.RLock;

/**
 * @author Kou Shenhai
 */
@RequiredArgsConstructor
@Slf4j
public class RedissonLock extends AbstractLock<RLock>{

    private final RedisUtil redisUtil;

    @Override
    public RLock getLock(LockType type,String key) {
        switch(type) {
            case LOCK: return redisUtil.getLock(key);
            case FAIR: return redisUtil.getFairLock(key);
            case READ: return redisUtil.getReadLock(key);
            case WRITE: return redisUtil.getWriteLock(key);
            default: return null;
        }
    }

    @Override
    public Boolean tryLock(RLock lock,long expire,long timeout) throws InterruptedException {
        //线程名称
        String threadName = Thread.currentThread().getName();
        if (redisUtil.tryLock(lock,expire, timeout)) {
            log.info("加锁成功...");
            return true;
        } else {
            log.info("线程{}获取锁失败",threadName);
            return false;
        }
    }

    @Override
    public void unlock(RLock lock) {
        //线程名称
        String threadName = Thread.currentThread().getName();
        if (redisUtil.isLocked(lock)) {
            log.info("{}对应的锁被持有，线程{}",lock,threadName);
            if (redisUtil.isHeldByCurrentThread(lock)) {
                log.info("当前线程{}持有锁",threadName);
                redisUtil.unlock(lock);
                log.info("解锁成功...");
            }
        } else {
            log.info("无线程持有，无需解锁...");
        }
    }
}
