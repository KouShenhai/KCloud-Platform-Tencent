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
