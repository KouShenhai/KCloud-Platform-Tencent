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
package org.laokou.redis.config;
import org.redisson.Redisson;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisClient;
import org.redisson.client.codec.Codec;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import java.time.Duration;
/**
 * @EnableConfigurationProperties -> ConfigurationProperties的类进行一次注入
 * AutoConfiguration -> 给插件使用
 * Configuration -> 直接使用
 * @ConditionalOnBean -> spring容器中存在指定的class实例对象，对应的配置才生效
 * @ConditionalOnMissingBean -> ConditionalOnMissingBean 保证只有一个bean被注入
 * 某个class位于类路径上，才会实例化一个bean
 * @author Kou Shenhai
 */
@ConditionalOnClass(Redisson.class)
@AutoConfiguration
@EnableConfigurationProperties(RedisProperties.class)
public class RedisSessionConfig {

    private static final String REDIS_PROTOCOL_PREFIX = "redis://";

    private static final String REDISS_PROTOCOL_PREFIX = "rediss://";

    @Bean
    /**
     * ConditionalOnMissingBean -> 相同类型的bean被注入，保证bean只有一个
     */
    @ConditionalOnMissingBean(RedisClient.class)
    public RedissonClient redisClient(RedisProperties properties) {
        Config config = new Config();
        final Duration duration = properties.getTimeout();
        int timeout = duration == null ? 0 : (int) duration.toMillis();
        String protocolPrefix = REDIS_PROTOCOL_PREFIX;
        if (properties.isSsl()) {
            protocolPrefix = REDISS_PROTOCOL_PREFIX;
        }
        config.useSingleServer()
                .setAddress(protocolPrefix + properties.getHost() + ":" + properties.getPort())
                .setDatabase(properties.getDatabase())
                .setPassword(properties.getPassword())
                .setTimeout(timeout);
        //使用json序列化方式
        Codec codec = new CustomJsonJacksonCodec();
        config.setCodec(codec);
        return Redisson.create(config);
    }

    @Bean
    public RBloomFilter<String> bloomFilter(RedissonClient redisson) {
        RBloomFilter<String> bloomFilter = redisson.getBloomFilter("bloomFilter");
        bloomFilter.tryInit(100,0.001);
        return bloomFilter;
    }

}
