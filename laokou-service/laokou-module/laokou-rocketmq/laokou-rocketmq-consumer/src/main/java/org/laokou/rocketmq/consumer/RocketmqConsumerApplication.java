package org.laokou.rocketmq.consumer;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author Kou Shenhai
 */
@SpringBootApplication(scanBasePackages = {"org.laokou.common.core","org.laokou.common.mybatisplus","org.laokou.rocketmq.consumer"})
@EnableEncryptableProperties
@EnableDiscoveryClient
public class RocketmqConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RocketmqConsumerApplication.class, args);
    }

}
