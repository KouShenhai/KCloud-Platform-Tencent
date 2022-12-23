/**
 * Copyright (c) 2022 KCloud-Platform-Tencent Authors. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.laokou.gateway.route;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
/**
 * @author laokou
 */
@Configuration
public class RedisConfig {

    @Bean
    public ReactiveRedisTemplate<String,Object> reactiveRedisTemplate(LettuceConnectionFactory factory) {
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // Json序列化配置
        ObjectMapper objectMapper = getObjectMapper();
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(objectMapper,Object.class);
        RedisSerializationContext.SerializationPair keySerializationPair =
                RedisSerializationContext.SerializationPair.fromSerializer(stringRedisSerializer);
        RedisSerializationContext.SerializationPair valueSerializationPair =
                RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer);
        RedisSerializationContext.SerializationPair hashValueSerializationPair =
                RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer);
        RedisSerializationContext<String, Object> redisSerializationContext = new RedisSerializationContext<>() {

            @Override
            public SerializationPair<String> getKeySerializationPair() {
                return keySerializationPair;
            }

            @Override
            public SerializationPair<Object> getValueSerializationPair() {
                return valueSerializationPair;
            }

            @Override
            public <HK> SerializationPair<HK> getHashKeySerializationPair() {
                return keySerializationPair;
            }

            @Override
            public <HV> SerializationPair<HV> getHashValueSerializationPair() {
                return hashValueSerializationPair;
            }

            @Override
            public SerializationPair<String> getStringSerializationPair() {
                return keySerializationPair;
            }
        };
        return new ReactiveRedisTemplate<>(factory,redisSerializationContext);
    }

    private ObjectMapper getObjectMapper() {
        //解决查询缓存转换异常的问题
        ObjectMapper objectMapper = new ObjectMapper();
        //Long类型转String类型
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(Long.class, ToStringSerializer.instance);
        javaTimeModule.addSerializer(Long.TYPE,ToStringSerializer.instance);
        objectMapper.registerModule(javaTimeModule);
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance , ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        return objectMapper;
    }
}
