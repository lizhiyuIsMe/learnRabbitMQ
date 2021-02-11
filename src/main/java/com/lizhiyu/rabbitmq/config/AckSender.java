package com.lizhiyu.rabbitmq.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

/**
 * @author lizhiyu
 * 
 */
@Component
public class AckSender implements  RabbitTemplate.ConfirmCallback ,RabbitTemplate.ReturnCallback {

    private static Logger logger = LoggerFactory.getLogger(AckSender.class);
    @Autowired
    private RabbitTemplate rabbitTemplate;


    public void send(String exchangeName,String routerKey,String content) {
        //template.convertAndSend(RabbitMQConfig.EXCHANGE_NAME,"order.new","新订单来啦1");
        //设置消息从发送者到交换机的回调
        this.rabbitTemplate.setConfirmCallback(this);
        //设置消息从交换机到队列的回调
        this.rabbitTemplate.setReturnCallback(this);
        //构建回调返回的数据
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        /**
         * 第一个参数是:交换机名称
         * 第二个参数是:routerKey
         * 第三个参数是:内容
         * 第四个参数是：用于出现错误时候能返回这个对象中存储的内容
         */
        this.rabbitTemplate.convertAndSend(exchangeName,routerKey,content,correlationData);
        //发送消息时候如果不书写交换机,则使用默认交换机,RouterKey就是队列的名称
        //this.rabbitTemplate.convertAndSend(routerKey,(Object)content,correlationData);
        logger.info("Confirm Send ok,"+new Date()+","+content);
    }

    /**
     * 消息回调确认方法
     * 如果消息没有到exchange,则confirm回调,ack=false
     * 如果消息到达exchange,则confirm回调,ack=true
     * @param
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean isSendSuccess, String s) {
        //这是一个错误 clean channel shutdown; protocol method: #method<channel.close>(reply-code=200, reply-text=OK, class-id=0, method-id=0)
        //如果提示错误是没有关系的,是测试时候发送端关闭的一个提示
        System.out.println("confirm--message:回调消息ID为: " + correlationData.getId());
        if (isSendSuccess) {
            logger.info("confirm--message:消息发送成功");
        } else {
            logger.error("confirm--message:消息发送失败" + s);
        }
    }

    /**
     * exchange到queue成功,则不回调return
     * exchange到queue失败,则回调return(需设置mandatory=true,否则不回回调,消息就丢了)
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        //当发送的routerKey没有找到对应的队列就会触发这个方法
        logger.error("return--message:" + new String(message.getBody()) + ",replyCode:" + replyCode
                + ",replyText:" + replyText + ",exchange:" + exchange + ",routingKey:" + routingKey);
    }

}