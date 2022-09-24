package org.laokou.elasticsearch.server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author Kou Shenhai
 */
@EnableDiscoveryClient
@EnableConfigurationProperties
@SpringBootApplication(scanBasePackages = {"org.laokou.elasticsearch"})
public class ElasticsearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElasticsearchApplication.class, args);
    }

}
