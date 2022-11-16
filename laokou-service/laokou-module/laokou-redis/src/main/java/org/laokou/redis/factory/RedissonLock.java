/**
 * Copyright (c) 2022 KCloud-Platform-Official Authors. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *   http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.laokou.redis.factory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.laokou.redis.utils.RedisUtil;
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

    /**
     * 获取锁
     * @param lock 锁
     * @param expire 过期时间
     * @param timeout 超时时间
     * @return
     * @throws InterruptedException
     */
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

    /**
     * 释放锁
     * @param lock 锁
     */
    @Override
    public void unlock(RLock lock) {
        if (lock != null) {
            //线程名称
            String threadName = Thread.currentThread().getName();
            if (redisUtil.isLocked(lock)) {
                log.info("{}对应的锁被持有，线程{}", lock, threadName);
                if (redisUtil.isHeldByCurrentThread(lock)) {
                    log.info("当前线程{}持有锁", threadName);
                    redisUtil.unlock(lock);
                    log.info("解锁成功...");
                }
            } else {
                log.info("无线程持有，无需解锁...");
            }
        }
    }
}
