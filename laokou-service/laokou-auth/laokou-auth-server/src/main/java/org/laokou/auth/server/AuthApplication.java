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
package org.laokou.auth.server;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.laokou.common.utils.JvmUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;

/**
 * 架构演变
 * 单机架构（两层架构）
 * 三层架构（集中式架构）
 * DDD分层架构(分布式微服务架构) > 表现层 应用层 领域层 基础层
 * @author Kou Shenhai
 */
@SpringBootApplication(scanBasePackages = {"org.laokou.common","org.laokou.auth","org.laokou.redis","org.laokou.log","org.laokou.datasource"})
@EnableDiscoveryClient
@EnableConfigurationProperties
@EnableApolloConfig
@EnableAspectJAutoProxy
@Slf4j
@EnableEncryptableProperties
@EnableFeignClients(basePackages = {"org.laokou.log"})
public class AuthApplication implements CommandLineRunner, WebServerFactoryCustomizer<WebServerFactory> {

    private static final String MODULE_NAME = "laokou-service\\laokou-auth";

    private static final String SERVICE_NAME = "laokou-auth-server";

    private static final String PACK_NAME = "org.laokou.auth.server".replaceAll("\\.", Matcher.quoteReplacement(File.separator));

    private static final String APPLICATION_NAME = "AuthApplication";

    public static void main(String[] args) throws IOException {
        final String baseDir = System.getProperty("user.dir");
        final String path = String.format("%s\\%s\\%s\\target\\classes\\%s\\%s.class",baseDir,MODULE_NAME,SERVICE_NAME,PACK_NAME,APPLICATION_NAME);
        JvmUtil.getJvmInfo(path);
        SpringApplication.run(AuthApplication.class, args);
    }

    @Override
    public void run(String... args){
        log.info("日志乱码 > -Dfile.encoding=UTF-8");
        log.info("开启APR模式 > -Djava.library.path=./lib");
    }

    /**
     * 监控服务
     * @param applicationName
     * @return
     */
    @Bean
    MeterRegistryCustomizer<MeterRegistry> configurer(
            @Value("${spring.application.name}") String applicationName) {
        return (registry) -> registry.config().commonTags("application", applicationName);
    }

    @Override
    public void customize(WebServerFactory factory) {
        TomcatServletWebServerFactory containerFactory = (TomcatServletWebServerFactory) factory;
        containerFactory.setProtocol("org.apache.coyote.http11.Http11AprProtocol");
    }

}
