package org.laokou.common.constant;
/**
 * 队列常量值
 * @author Kou Shenhai
 * @version 1.0
 * @date 2020/9/18 0018 上午 7:07
 */
public interface RabbitMqConstant {

    /**
     * 操作日志消息
     */
    String LAOKOU_OPERATE_LOG_QUEUE = "laokou.operate.log.queue";

    /**
     * 登录日志消息
     */
    String LAOKOU_LOGIN_LOG_QUEUE = "laokou.login.log.queue";

    /**
     * 自定义延迟消息
     */
    String LAOKOU_CUSTOM_DELAY_QUEUE = "laokou.custom.delay.queue";

    /**
     * 自定义延迟交换机
     */
    String LAOKOU_CUSTOM_DELAY_EXCHANGE_NAME = "laokou.custom.delay.exchange";

    /**
     * 自定义延迟路由key
     */
    String LAOKOU_CUSTOM_DELAY_ROUTING_KEY = "laokou.custom.delay.routing";

    /**
     * 默认的交换机名称
     */
    String  DEFAULT_EXCHANGE_NAME = "default.exchange";

    /**
     * 默认的路由key
     */
    String  DEFAULT_ROUTING_KEY = "default.routing";

    /**
     * 默认的队列名称
     */
    String  DEFAULT_QUEUE = "default.queue";

    /**
     * 是否持久队列，消息队列丢数据
     */
    Boolean DURABLE = true;

    /**
     * 声明一个独立队列
     */
    Boolean EXCLUSIVE = false;

    /**
     * 服务器不再使用时是否自动删除交换器
     */
    Boolean AUTO_DELETE = false;

}
