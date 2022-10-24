package org.laokou.kafka.client.constant;
/**
 * 队列常量值
 * @author Kou Shenhai
 * @version 1.0
 * @date 2020/9/18 0018 上午 7:07
 */
public interface KafkaConstant {

    /**
     * 操作日志消息
     */
    String LAOKOU_OPERATE_LOG_TOPIC = "laokou-operate-log-topic";

    /**
     * 登录日志消息
     */
    String LAOKOU_LOGIN_LOG_TOPIC = "laokou-login-log-topic";

    /**
     * 资源审批消息
     */
    String LAOKOU_RESOURCE_AUDIT_TOPIC = "laokou-resource-audit-topic";
}
