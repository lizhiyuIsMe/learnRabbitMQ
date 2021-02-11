package com.lizhiyu.rabbitmq.test;

import com.lizhiyu.rabbitmq.config.AckSender;
import com.lizhiyu.rabbitmq.config.RabbitMQConfig;
import com.lizhiyu.rabbitmq.config.RabbitMQScheduleConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RabbitMQTest {
    @Autowired
    private AckSender ackSender;

    @Test
    void send() {
        //this.rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME,"order.new","新订单来啦1");
        //发送消息时候如果不书写交换机,则使用默认交换机,RouterKey就是队列的名称
        //this.rabbitTemplate.convertAndSend(routerKey,(Object)content,correlationData);
        ackSender.send(RabbitMQConfig.EXCHANGE_NAME,"order.new","新订单来啦");
        ackSender.send(RabbitMQConfig.EXCHANGE_NAME,"order.pay","新订单付款了");
        ackSender.send(RabbitMQConfig.EXCHANGE_NAME,"order.get","新订单收货了");
        ackSender.send(RabbitMQConfig.EXCHANGE_NAME,"order.receiver","新订单好评了");
        //可以通过书写发送的交交换机名称不存在，则可以测试到交换机没有收到消息的回调
        //可以通过书写发送的RouterKey找不到匹配的队列，则可以测试到队列没有收到消息的回调
    }


    @Test
    void send2() {
        //将消息发送到队列A中,A队列没有被消费然后10秒后再背执行
        ackSender.send(RabbitMQScheduleConfig.EXCHANGE_NAME_SCHEULE,"shcheule.new","新订单1来啦");
        ackSender.send(RabbitMQScheduleConfig.EXCHANGE_NAME_SCHEULE,"shcheule.new","新订单2来啦");
        ackSender.send(RabbitMQScheduleConfig.EXCHANGE_NAME_SCHEULE,"shcheule.new","新订单3来啦");
        ackSender.send(RabbitMQScheduleConfig.EXCHANGE_NAME_SCHEULE,"shcheule.new","新订单4来啦");
    }

}