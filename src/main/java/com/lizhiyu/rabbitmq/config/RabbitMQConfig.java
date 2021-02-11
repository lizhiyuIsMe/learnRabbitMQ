package com.lizhiyu.rabbitmq.config;


import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * topic简单使用的配置声明
 *
 * 身缠着--》EXCHANGE_NAME交换机--》通过RouterKey(order.#)-》QUEUE_NAME -->消费者
 */
@Configuration
public class RabbitMQConfig {

    /**
     * 交换机名称
     */
    public static final String EXCHANGE_NAME = "order_exchange";
    /**
     * 队列名称
     */
    public static final String QUEUE_NAME = "order_queue";

    /**
     * 声明交换机
     * @return
     */
    @Bean
    public Exchange orderExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE_NAME).durable(true).build();
        //return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    /**
     * 声明队列
     * @return
     */
    @Bean
    public Queue orderQueue() {
        return QueueBuilder.durable(QUEUE_NAME).build();
        //return new Queue(QUEUE_NAME, true, false, false, null);
    }
    /**
     * 交换机和队列绑定关系
     */
    @Bean
    public Binding orderBinding(@Qualifier(value = "orderQueue") Queue queue,@Qualifier(value = "orderExchange")  Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("order.#").noargs();
        //return BindingBuilder.bind(queue2).to(exchange).with("people.#").noargs();
        //return new Binding(QUEUE_NAME, Binding.DestinationType.QUEUE, EXCHANGE_NAME, "order.#", null);
    }
}