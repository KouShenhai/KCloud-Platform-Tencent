package org.laokou.rabbitmq.consumer;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Kou Shenhai
 */
@EnableEncryptableProperties
@SpringBootApplication(scanBasePackages = {"org.laokou.common","org.laokou.datasource","org.laokou.rabbitmq.consumer"})
public class RabbitMqConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RabbitMqConsumerApplication.class, args);
    }

}
