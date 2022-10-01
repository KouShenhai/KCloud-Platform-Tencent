package org.laokou.rabbitmq.server.sender;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.laokou.common.constant.RabbitMqConstant;
import org.laokou.common.dto.MqDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelCallback;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Kou Shenhai
 * @version 1.0
 * @date 2020/9/18 0018 下午 11:19
 */
@RestController
@RequestMapping("/rabbitmq")
@Slf4j
public class RabbitMqSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ConnectionFactory connectionFactory;

    /**
     * RabbitMQ发送消息(使用默认的交换机)
     * @param routingKey
     * @param dto
     * @return
     */
    @PostMapping("/send/{routingKey}")
    public Boolean sendMsg(@PathVariable("routingKey") String routingKey, @RequestBody MqDTO dto) {
        try {
            rabbitTemplate.convertAndSend(routingKey, dto.getData());
        } catch (Exception e) {
            log.info("发送消息异常，消息KEY：{},消息体：{}",routingKey,dto.getData());
            log.info("异常信息：{}",e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * @param exchange:   交换机
     * @param routingKey: 路由键
     * @param dto:    消息内容
     * @Description: RabbitMQ发送消息(指定交换机)
     */
    @PostMapping("/send/{exchange}/{routingKey}")
    public void sendMsg(@PathVariable("exchange") String exchange, @PathVariable("routingKey") String routingKey, @RequestBody MqDTO dto) {
        rabbitTemplate.convertAndSend(exchange, routingKey, dto.getData());
    }

    /**
     * 功能描述:
     * 〈RabbitMQ发送延迟消息〉
     *
     * @param exchange:   交换机
     * @param routingKey: 路由键
     * @param dto     消息内容
     * @param delay       延迟时间
     */
    @PostMapping("/send/delay/{exchange}/{routingKey}")
    public void sendMsg(@PathVariable("exchange") String exchange, @PathVariable("routingKey") String routingKey, @RequestBody MqDTO dto, @RequestParam("delay") Long delay) {
        rabbitTemplate.convertAndSend(exchange, routingKey, dto.getData(), msg -> {
            msg.getMessageProperties().setHeader("x-delay", delay);
            return msg;
        });
    }

    /**
     * 创建交换机
     *
     * @param exchangeName 交换机名称
     * @param durable      是否长期有效
     */
    @PostMapping("/exchange")
    public void addExchange(@RequestParam("exchangeName") String exchangeName,
                            @RequestParam("durable") Boolean durable) {
        RabbitAdmin rabbitAdmin = getRabbitAdmin();
        TopicExchange topicExchange = new TopicExchange(exchangeName, durable, RabbitMqConstant.AUTO_DELETE);
        rabbitAdmin.declareExchange(topicExchange);
    }

    /**
     * 创建队列
     *
     * @param queueName 队列名称
     * @param durable   是否持久化
     * @param exclusive 声明一个独立队列
     */
    @PostMapping("/queue")
    public void addQueue(@RequestParam("queueName") String queueName,
                         @RequestParam("durable") Boolean durable,
                         @RequestParam("exclusive") Boolean exclusive) {
        RabbitAdmin rabbitAdmin = getRabbitAdmin();
        Queue queue = new Queue(queueName, durable, exclusive, RabbitMqConstant.AUTO_DELETE);
        rabbitAdmin.declareQueue(queue);
    }

    /**
     * 消息队列、交换机、路由绑定
     *
     * @param exchangeName 交换机名称
     * @param routingKey:  路由键
     * @param queueName    队列名称
     * @param durable      设置是否持久化
     */
    @PostMapping("/bind")
    public void declareTopicRoutingQueue(@RequestParam("exchangeName") String exchangeName,
                                         @RequestParam("routingKey") String routingKey,
                                         @RequestParam("queueName") String queueName,
                                         @RequestParam("durable") Boolean durable) {
        //对交换机名称校验 为null则设置为默认
        if (StringUtils.isBlank(exchangeName)) {
            exchangeName = RabbitMqConstant.DEFAULT_EXCHANGE_NAME;
        }

        //对路由键校验 为null则设置为默认
        if (StringUtils.isBlank(routingKey)) {
            routingKey = RabbitMqConstant.DEFAULT_ROUTING_KEY;
        }

        //对队列名称校验 为null则设置为默认
        if (StringUtils.isBlank(queueName)) {
            routingKey = RabbitMqConstant.DEFAULT_QUEUE;
        }

        //对持久化校验 为null则设置为默认
        if (durable == null) {
            durable = RabbitMqConstant.DURABLE;
        }

        declareTopicQueue(exchangeName, routingKey, queueName, durable,
                RabbitMqConstant.EXCLUSIVE, RabbitMqConstant.AUTO_DELETE);
    }

    /**
     * 根据队列名称查询队列的数量
     *
     * @param queueName 队列名称
     * @return
     */
    @GetMapping("/count/{queueName}")
    public Integer findMessageCount(@PathVariable("queueName") String queueName) {

        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        return rabbitAdmin.getRabbitTemplate().execute(new ChannelCallback<Integer>() {
            @Override
            public Integer doInRabbit(Channel channel) throws Exception {
                final AMQP.Queue.DeclareOk ok = channel.queueDeclare(queueName, true, false, false, null);
                return ok.getMessageCount();
            }
        });
    }

    /**
     * 删除消息队列
     *
     * @param queueName 队列名称
     */
    @DeleteMapping("/queue/{queueName}")
    public void deleteQuery(@PathVariable("queueName") String queueName) {
        RabbitAdmin rabbitAdmin = getRabbitAdmin();
        rabbitAdmin.deleteQueue(queueName);
    }

    /**
     * 删除交换机
     *
     * @param exchangeName 交换机名称
     */
    @DeleteMapping("/exchange/{exchangeName}")
    public void deleteExchangeName(@PathVariable("exchangeName") String exchangeName) {
        RabbitAdmin rabbitAdmin = getRabbitAdmin();
        rabbitAdmin.deleteExchange(exchangeName);
    }


    /**
     * 初始化rabbitAdmin
     *
     * @return
     */
    private RabbitAdmin getRabbitAdmin() {
        return new RabbitAdmin(connectionFactory);
    }

    /**
     * 消息队列、交换机、路由绑定
     *
     * @param exchangeName 交换机名称
     * @param routingKey:  路由键
     * @param queueName    队列名称
     * @param durable      设置是否持久化
     * @param exclusive    设置是否排他，如果为true，该队列进队首次声明它的连接可见，并在连接断开时自动删除（建议设置为false）
     * @param autoDelete   设置是否自动删除，true自动删除，（建议false）
     *                     前提：至少有一个消费者连接到这个队列，之后所有与这个队列连接的消费者都断开时才会自动删除
     */
    private void declareTopicQueue(String exchangeName, String routingKey,
                                   String queueName, Boolean durable,
                                   Boolean exclusive, Boolean autoDelete) {
        RabbitAdmin rabbitAdmin = getRabbitAdmin();
        TopicExchange topicExchange = new TopicExchange(exchangeName, durable, autoDelete);
        rabbitAdmin.declareExchange(topicExchange);
        Queue queue = new Queue(queueName, durable, exclusive, autoDelete);
        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(topicExchange).with(routingKey));
    }

}
