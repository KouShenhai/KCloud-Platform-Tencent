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
package org.laokou.cas.server;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import io.micrometer.core.instrument.MeterRegistry;
import org.laokou.common.utils.JvmUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;

/**
 * @author Kou Shenhai
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableApolloConfig
public class CasApplication {

    private static final String MODULE_NAME = "laokou-service\\laokou-cas";

    private static final String SERVICE_NAME = "laokou-cas-server";

    private static final String PACK_NAME = "org.laokou.cas.server".replaceAll("\\.", Matcher.quoteReplacement(File.separator));

    private static final String APPLICATION_NAME = "CasApplication";

    public static void main(String[] args) throws IOException {
        final String baseDir = System.getProperty("user.dir");
        final String path = String.format("%s\\%s\\%s\\target\\classes\\%s\\%s.class",baseDir,MODULE_NAME,SERVICE_NAME,PACK_NAME,APPLICATION_NAME);
        JvmUtil.getJvmInfo(path);
        SpringApplication.run(CasApplication.class, args);
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
}
