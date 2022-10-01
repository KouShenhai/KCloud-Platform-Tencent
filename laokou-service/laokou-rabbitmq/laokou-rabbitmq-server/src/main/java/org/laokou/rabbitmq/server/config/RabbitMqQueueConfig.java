package org.laokou.rabbitmq.server.config;
import org.laokou.common.constant.RabbitMqConstant;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;
/**
 * @author Kou Shenhai
 * @version 1.0
 * @date 2020/9/18 0018 下午 11:54
 */
@Configuration
public class RabbitMqQueueConfig {
    /**
     * 操作日志消息
     */
    @Bean("operateLogQueue")
    public Queue elasticsearchQueue() {
        return new Queue(RabbitMqConstant.LAOKOU_OPERATE_LOG_QUEUE);
    }

    /**
     * 登录日志消息
     */
    @Bean("loginLogQueue")
    public Queue mongodbQueue() {
        return new Queue(RabbitMqConstant.LAOKOU_LOGIN_LOG_QUEUE);
    }


    @Bean("customExchange")
    public CustomExchange customExchange() {
        Map<String,Object> args = new HashMap<>(1);
        args.put("x-delayed-type","direct");
        return new CustomExchange(RabbitMqConstant.LAOKOU_CUSTOM_DELAY_EXCHANGE_NAME,"x-delayed-message",true,false,args);
    }

    @Bean("immediateQueue")
    public Queue immediateQueue() {
        return new Queue(RabbitMqConstant.LAOKOU_CUSTOM_DELAY_QUEUE);
    }

    @Bean
    public Binding bindingNotify(@Qualifier("immediateQueue") Queue queue, @Qualifier("customExchange")CustomExchange customExchange) {
        return BindingBuilder.bind(queue).to(customExchange).with(RabbitMqConstant.LAOKOU_CUSTOM_DELAY_ROUTING_KEY).noargs();
    }

}
