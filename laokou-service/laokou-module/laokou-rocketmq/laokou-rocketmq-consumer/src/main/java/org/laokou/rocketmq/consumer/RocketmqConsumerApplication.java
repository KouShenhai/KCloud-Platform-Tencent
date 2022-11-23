package org.laokou.rocketmq.consumer;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author Kou Shenhai
 */
@SpringBootApplication(scanBasePackages = {"org.laokou.common.core","org.laokou.common.mybatisplus","org.laokou.rocketmq.consumer"})
@EnableEncryptableProperties
@EnableDiscoveryClient
@EnableFeignClients
public class RocketmqConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RocketmqConsumerApplication.class, args);
    }

}
