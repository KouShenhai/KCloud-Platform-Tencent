package org.laokou.rabbitmq.consumer;
import org.laokou.common.utils.JvmUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;

/**
 * @author Kou Shenhai
 */
@SpringBootApplication(scanBasePackages = {"org.laokou.common","org.laokou.datasource","org.laokou.rabbitmq.consumer"})
public class RabbitMqConsumerApplication {

    private static final String MODULE_NAME = "laokou-service\\laokou-rabbitmq";

    private static final String SERVICE_NAME = "laokou-rabbitmq-consumer";

    private static final String PACK_NAME = "org.laokou.rabbitmq.consumer".replaceAll("\\.", Matcher.quoteReplacement(File.separator));

    private static final String APPLICATION_NAME = "RabbitMqConsumerApplication";

    public static void main(String[] args) throws IOException {
        final String baseDir = System.getProperty("user.dir");
        final String path = String.format("%s\\%s\\%s\\target\\classes\\%s\\%s.class",baseDir,MODULE_NAME,SERVICE_NAME,PACK_NAME,APPLICATION_NAME);
        JvmUtil.getJvmInfo(path);
        SpringApplication.run(RabbitMqConsumerApplication.class, args);
    }

}
