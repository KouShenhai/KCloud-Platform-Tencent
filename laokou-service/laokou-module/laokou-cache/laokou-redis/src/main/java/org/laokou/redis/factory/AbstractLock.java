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
