package org.laokou.rabbitmq.server.config;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
/**
 * @author Kou Shenhai
 * @version 1.0
 * @date 2020/9/18 0018 下午 11:24
 */
@Configuration
public class RabbitMqConfig {

    private final static Logger LOGGER = LoggerFactory.getLogger(RabbitMqConfig.class);

    @Autowired
    private SimpleRabbitMqConfig simpleRabbitMqConfig;

    /**
     * 配置连接工厂
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        return getConnectionFactory(simpleRabbitMqConfig);
    }

    /**
     * @Description 因为要设置回调类，所以应是prototype类型，如果是singleton类型，则回调类为最后一次设置
     *ConfirmCallback接口用于实现消息发送到RabbitMQ交换器后接收ack回调   即消息发送到exchange  ack
     *ReturnCallback接口用于实现消息发送到RabbitMQ 交换器，但无相应队列与交换器绑定时的回调  即消息发送不到任何一个队列中  ack
     */
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        // 消息发送失败返回到队列中,配置上文件需要配置 publisher-returns = ture
        template.setMandatory(true);

        // 消息返回,配置上文件需要配置 publisher-returns = ture
        // ReturnCallBack回调方法会被调用
        template.setReturnCallback((message, replyCode, replyTest, exchange, routingKey) -> {
            String correlationId = message.getMessageProperties().getCorrelationId();
            LOGGER.info("id【{}】",correlationId);
            LOGGER.info("exchange({}),route({}),replyCode({}),replyText({}),message:{}",
                    exchange, routingKey, replyCode, replyTest, message);
        });

        // 消息确认,配置上文件需要配置 publisher-confirms = ture
        template.setConfirmCallback((correlationData, ack, cause) -> {
            LOGGER.info("消息发送到exchange成功,correlationData:{},ack:{},cause:{}",
                    correlationData, ack, cause);
        });

        return template;
    }

    /**
     * @Description: RabbitTemplate使用CachingConnectionFactory作为连接工厂
     * 注意：在一个应用里面同时存在消费者和生产者时，建议使用一个具有相同选项的单独CachingConnectionFactory实例，
     * 一个用于生产者，一个用于消费者。这是为了避免消费者由于生产者阻塞而阻塞
     */
    private CachingConnectionFactory getConnectionFactory(SimpleRabbitMqConfig simpleRabbitMqConfig) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses(simpleRabbitMqConfig.getAddress());
        connectionFactory.setUsername(simpleRabbitMqConfig.getUsername());
        connectionFactory.setPassword(simpleRabbitMqConfig.getPassword());
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        //设置虚拟主机
        connectionFactory.setVirtualHost(simpleRabbitMqConfig.getVirtualHost());
        // 如果要进行消息回调，则这里必须要设置为true
        connectionFactory.setPublisherConfirms(true);
        // 开启发送失败退回
        connectionFactory.setPublisherReturns(simpleRabbitMqConfig.getPublisherReturns());
        // 设置连接超时时间
        connectionFactory.setConnectionTimeout(simpleRabbitMqConfig.getConnectionTimeout());

        //创建一个线程工程指定名称为rabbitmq-pool-0
        ThreadFactory rabbitMQthreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("rabbitmq-pool-%d").build();
        //创建线程池
        ThreadPoolExecutor executorService = new ThreadPoolExecutor(5, 200,
                0L, TimeUnit.SECONDS, new SynchronousQueue<>(),
                rabbitMQthreadFactory);
        connectionFactory.setExecutor(executorService);

        //设置RabbitMQ缓存模式，共有两个缓存模式如下：
        //1、CONNECTION模式，这个模式下允许创建多个Connection，会缓存一定数量的Connection，每个Connection中同样会缓存一些Channel，
        // 除了可以有多个Connection，其它都跟CHANNEL模式一样。
        //2、CHANNEL模式，程序运行期间ConnectionFactory会维护着一个Connection，
        // 所有的操作都会使用这个Connection，但一个Connection中可以有多个Channel，
        // 操作rabbitmq之前都必须先获取到一个Channel，
        // 否则就会阻塞（可以通过setChannelCheckoutTimeout()设置等待时间），
        // 这些Channel会被缓存（缓存的数量可以通过setChannelCacheSize()设置）；
        // connectionFactory.setCacheMode(CachingConnectionFactory.CacheMode.CONNECTION);

        //设置每个Connection中缓存Channel的数量，不是最大的。操作rabbitmq之前（send/receive message等）
        // 要先获取到一个Channel.获取Channel时会先从缓存中找闲置的Channel，如果没有则创建新的Channel，
        // 当Channel数量大于缓存数量时，多出来没法放进缓存的会被关闭。
        connectionFactory.setChannelCacheSize(simpleRabbitMqConfig.getChannelSize());

        //单位：毫秒；
        //从缓存获取不到可用的Channel时，不会创建新的Channel，会等待这个值设置的毫秒数
        //同时，在CONNECTION模式，这个值也会影响获取Connection的等待时间，
        //超时获取不到Connection也会抛出AmqpTimeoutException异常。
        connectionFactory.setChannelCheckoutTimeout(simpleRabbitMqConfig.getCheckoutTimeout());

        //setConnectionLimit：仅在CONNECTION模式使用，设置Connection的数量上限。
        connectionFactory.setConnectionLimit(100);
        //仅在CONNECTION模式使用，设置Connection的缓存数量。
        // connectionFactory.setConnectionCacheSize(simpleRabbitMqConfig.getConnectionCacheSize());

        return connectionFactory;
    }

}
