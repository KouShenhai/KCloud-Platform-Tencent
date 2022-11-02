package org.laokou.oss.server;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.laokou.swagger.config.CorsConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

/**
 * @author Kou Shenhai
 */
@SpringBootApplication(scanBasePackages = {"org.laokou.common","org.laokou.oss","org.laokou.redis","org.laokou.mybatis"})
@EnableDiscoveryClient
@Import({CorsConfig.class})
@EnableEncryptableProperties
public class OssApplication {

    public static void main(String[] args) {
        SpringApplication.run(OssApplication.class, args);
    }

}
