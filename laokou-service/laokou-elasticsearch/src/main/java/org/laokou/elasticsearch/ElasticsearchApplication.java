package org.laokou.elasticsearch;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author Kou Shenhai
 */
@EnableDiscoveryClient
@EnableConfigurationProperties
@SpringBootApplication(scanBasePackages = {"io.laokou.elasticsearch"})
public class ElasticsearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElasticsearchApplication.class, args);
    }

}
