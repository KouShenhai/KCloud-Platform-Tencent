package org.laokou.redis.factory;

import org.laokou.redis.enums.LockScope;
import org.laokou.redis.enums.LockType;
import org.springframework.stereotype.Component;

@Component
public class LockFactory {

    public AbstractLock build(LockType type, LockScope scope) {
        return null;
    }

}
