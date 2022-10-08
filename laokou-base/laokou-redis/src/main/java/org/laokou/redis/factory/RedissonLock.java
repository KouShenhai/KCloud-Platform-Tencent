package org.laokou.redis.factory;

import org.laokou.redis.enums.LockType;
import org.redisson.api.RLock;

/**
 * @author Kou Shenhai
 */
public class RedissonLock extends AbstractLock<RLock>{



    @Override
    protected RLock getLock(LockType type) {
        return null;
    }

    @Override
    protected void lock() {

    }

    @Override
    protected void unlock() {

    }
}
