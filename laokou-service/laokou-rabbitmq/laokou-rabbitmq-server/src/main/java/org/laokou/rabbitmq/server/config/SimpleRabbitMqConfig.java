package org.laokou.rabbitmq.server.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author Kou Shenhai
 * @version 1.0
 * @date 2020/9/18 0018 下午 11:26
 */
@Component
@Data
@Slf4j
public class SimpleRabbitMqConfig {

    /**
     * 地址
     */
    @Value("${spring.rabbitmq.addresses}")
    private String address;

    /**
     * 用户名
     */
    @Value("${spring.rabbitmq.username}")
    private String username;

    /**
     * 密码
     */
    @Value("${spring.rabbitmq.password}")
    private String password;

    /**
     * 虚拟主机
     */
    @Value("${spring.rabbitmq.virtual-host}")
    private String virtualHost;

    /**
     * 开启发送确认
     */
    @Value("${spring.rabbitmq.publisher-confirm-type}")
    private String publisherConfirmType;

    /**
     * 开启发送失败返回
     */
    @Value("${spring.rabbitmq.publisher-returns}")
    private Boolean publisherReturns;

    /**
     * 开启ACK
     */
    @Value("${spring.rabbitmq.listener.direct.acknowledge-mode}")
    private String directAcknowledgeMode;

    /**
     * 开启ACK
     */
    @Value("${spring.rabbitmq.listener.simple.acknowledge-mode}")
    private String simpleAcknowledgeMode;

    /**
     * 连接超时时间
     */
    @Value("${spring.rabbitmq.connection-timeout}")
    private Integer connectionTimeout;

    /**
     * 通道超时时间
     */
    @Value("${spring.rabbitmq.cache.channel.checkout-timeout}")
    private Integer checkoutTimeout;

    /**
     * 设置通道数目的限制
     */
    @Value("${spring.rabbitmq.cache.channel.size}")
    private Integer channelSize;

    /**
     * 设置缓存connection数量
     */
    @Value("${spring.rabbitmq.cache.connection.size}")
    private Integer connectionCacheSize;

    /**
     * 与return机制结合配置次属性
     */
    private Boolean templateMandatory;

    /**
     * Spring启动容器时执行
     */
    @PostConstruct
    private void initialize() {
        log.info("[rabbitmq] address: {}, username: {}, password: {}, virtualHost: {}, publisherConfirmType: {}, publisherReturns, " +
                        "templateMandatory: {}, directAcknowledgeMode: {}, simpleAcknowledgeMode: {}, connectionTimeout: {}, " +
                        "checkoutTimeout: {}, channelSize: {}, connectionCacheSize：{}",
                address, username, password, virtualHost, publisherConfirmType, publisherReturns,
                templateMandatory, directAcknowledgeMode, simpleAcknowledgeMode, connectionTimeout,
                checkoutTimeout, connectionCacheSize);
    }

    @Override
    public String toString() {
        return String.format("[rabbitmq] addresse: %s, username: %s, password: %s, virtualHost: %s, publisherConfirmType: %s, publisherReturns: %s, " +
                        "templateMandatory: %s, directAcknowledgeMode: %s, simpleAcknowledgeMode: %s, connectionTimeout: %s, " +
                        "checkoutTimeout: %s, channelSize: %s, connectionCacheSize：%s",
                address, username, password, virtualHost, publisherConfirmType, publisherReturns,
                templateMandatory, directAcknowledgeMode, simpleAcknowledgeMode, connectionTimeout,
                checkoutTimeout, channelSize, connectionCacheSize);
    }
}
