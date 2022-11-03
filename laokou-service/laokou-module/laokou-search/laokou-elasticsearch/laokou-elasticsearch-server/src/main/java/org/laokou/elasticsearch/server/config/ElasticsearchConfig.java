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
package org.laokou.elasticsearch.server.config;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;
/**
 * es配置文件
 * @author Kou Shenhai
 * @version 1.0
 * @date 2020/8/9 0009 下午 2:01
 */
@Configuration
@Slf4j
public class ElasticsearchConfig {

    private static final String HTTP_SCHEME = "http";

    /**
     * 权限验证
     */
    final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    /**
     * es主机
     */
    @Value("${elasticsearch.host}")
    private String[] host;

    @Value("${elasticsearch.username}")
    private String username;

    @Value("${elasticsearch.password}")
    private String password;

    @Bean
    public RestClientBuilder restClientBuilder() {
        HttpHost[] hosts = Arrays.stream(host)
                .map(this::makeHttpHost)
                .filter(Objects::nonNull)
                .toArray(HttpHost[]::new);
        log.info("host:{}",Arrays.toString(hosts));
        //配置权限验证
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
        return RestClient.builder(hosts)
                .setHttpClientConfigCallback(httpClientBuilder ->
                httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                .setMaxConnPerRoute(100)
                .setKeepAliveStrategy((response, context) -> Duration.ofMinutes(5).toMillis())
                //最大连接数
                .setMaxConnTotal(100)
        ).setRequestConfigCallback(builder -> {
            builder.setConnectTimeout(-1);
            builder.setSocketTimeout(60000);
            builder.setConnectionRequestTimeout(-1);
            return builder;
        });
    }

    /**
     * 处理请求地址
     * @param address
     * @return
     */
    private HttpHost makeHttpHost(String address) {
        assert StringUtils.isNotEmpty(address);
        String[] hostAddress = address.split(":");
        if (hostAddress.length == 2) {
            String ip = hostAddress[0];
            Integer port = Integer.valueOf(hostAddress[1]);
            return new HttpHost(ip, port, HTTP_SCHEME);
        } else {
            return null;
        }
    }

    /**
     * 配置highLevelClient bean
     * @param restClientBuilder
     * @return
     */
    @Bean(name = "restHighLevelClient")
    public RestHighLevelClient restHighLevelClient(@Autowired RestClientBuilder restClientBuilder) {
        return new RestHighLevelClient(restClientBuilder);
    }
}
