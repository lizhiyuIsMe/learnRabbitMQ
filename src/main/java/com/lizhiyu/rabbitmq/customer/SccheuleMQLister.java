package com.lizhiyu.rabbitmq.customer;


import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RabbitListener(queues = "excutor_queue")
public class SccheuleMQLister {

    /**
     * RabbitHandler 会自动匹配 消息类型（消息自动确认）
     * @param msg
     * @param message
     * @throws IOException
     */
    @RabbitHandler
    public void releaseCouponRecord(String msg, Message message, Channel channel) throws IOException {
        long msgTag = message.getMessageProperties().getDeliveryTag();
        System.out.println("message="+message.toString());
        //消息确认已经消费的回调、第二个参数是是否一次处理多条消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        /**
         * 代表拒绝消息
         * 第二个参数代表是否一次处理多条消息
         * 第三个参数代表处理完任务是否要重新入队(如果这个参数是true,则会无限次的重试、可以通过redis计数来判断是否要放回队列中)
         * 如果消息已经被确认消费，则再调用此方法则没有作用、
         * 如果消费者接收到消息没有回复RabbitMQ server 则RabbitMQ会一直等待消费者回复,知道消费者挂掉
         */
        //channel.basicNack(message.getMessageProperties().getDeliveryTag(), false,true);
    }
}
