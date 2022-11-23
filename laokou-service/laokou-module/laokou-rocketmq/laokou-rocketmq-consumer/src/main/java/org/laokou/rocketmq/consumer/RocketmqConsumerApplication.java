package org.laokou.rocketmq.consumer;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Kou Shenhai
 */
@SpringBootApplication(scanBasePackages = {"org.laokou.common.core","org.laokou.common.mybatisplus","org.laokou.rocketmq.consumer"})
@EnableEncryptableProperties
public class RocketmqConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RocketmqConsumerApplication.class, args);
    }

}
