/**
 * Copyright (c) 2022 KCloud-Platform-Netflix Authors. All Rights Reserved.
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
package org.laokou.redis.aspect;
import org.laokou.redis.annotation.Lock4j;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.laokou.redis.enums.LockScope;
import org.laokou.redis.enums.LockType;
import org.laokou.redis.factory.AbstractLock;
import org.laokou.redis.factory.LockFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;

/**
 * @author Kou Shenhai
 */
@Component
@Aspect
@Slf4j
@AllArgsConstructor
public class LockAspect {

    private final LockFactory factory;

    /**
     * 配置切入点
     */
    @Pointcut("@annotation(org.laokou.redis.annotation.Lock4j)")
    public void lockPointCut() {}

    @Around(value = "lockPointCut()")
    public void around(ProceedingJoinPoint joinPoint) {
        //获取注解
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        if (null == method) {
            return;
        }
        Lock4j lock4j = method.getAnnotation(Lock4j.class);
        if (lock4j == null) {
            lock4j = AnnotationUtils.findAnnotation(method,Lock4j.class);
        }
        String key = lock4j.key();
        long expire = lock4j.expire();
        long timeout = lock4j.timeout();
        final LockType type = lock4j.type();
        final LockScope scope = lock4j.scope();
        final AbstractLock abstractLock = factory.build(scope);
        // 设置锁的自动过期时间，则执行业务的时间一定要小于锁的自动过期时间，否则就会报错
        final Object lock = abstractLock.getLock(type, key);
        try {
            if (abstractLock.tryLock(lock,expire,timeout)) {
                joinPoint.proceed();
            }
        } catch (Throwable throwable) {
            log.error("异常信息：{}",throwable.getMessage());
        } finally {
            //释放锁
            abstractLock.unlock(lock);
        }
    }

}
