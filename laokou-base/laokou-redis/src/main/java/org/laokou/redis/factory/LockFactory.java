package org.laokou.redis.factory;

import org.laokou.redis.RedisUtil;
import org.laokou.redis.enums.LockScope;
import org.laokou.redis.enums.LockType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;

/**
 * @author Kou Shenhai
 */
@Component
public class LockFactory {

    @Autowired
    private RedisUtil redisUtil;

    public AbstractLock build(LockScope scope) {
        switch (scope) {
            case DISTRIBUTED_LOCK: return new RedissonLock(redisUtil);
            case STANDALONE_LOCK: return new LocalLock();
        }
        return null;
    }

}
