package com.lizhiyu.rabbitmq.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


/**
 * @author lizhiyu
 * 设置投递到队列中的消息10秒钟后执行,通过队列的ttl超过最大值,通过死信队列执行
 * 生产者--》EXCHANGE_NAME_SCHEULE(普通交换机)--》QUEUE_NAME_SCHEULE(普通队列，这个队列没有消费者,当到达ttl后)
 *      --》QUEUE_NAME_EXCUTOR(交给队列绑定的死信交换机) --》QUEUE_NAME_EXCUTOR(给对列处理)
 * 注意点:
 * 1、QUEUE_NAME_SCHEULE 重启后要先删除之前的队列，不然队列配置的ttl等参数重启不会生效
 * 2、当队列中信息发生以下3中情况会将信息发送给死信队列
 *    1）ttl最大时间达到
 *    2）消费者接收到消息,然后channel.basicNack方法拒绝接收消息 的同时不让消息重新入队列
 *    3）队列达到容纳不下消息了达到极限
 */
@Configuration
public class RabbitMQScheduleConfig implements  RabbitTemplate.ConfirmCallback ,RabbitTemplate.ReturnCallback {

    private static Logger logger = LoggerFactory.getLogger(RabbitMQScheduleConfig.class);

    /**
     * 交换机名称
     */
    public static final String EXCHANGE_NAME_SCHEULE = "shcheule_exchange";
    /**
     * 队列名称
     */
    public static final String QUEUE_NAME_SCHEULE = "shcheule_queue";

    /**
     * 死信交换机
     */
    public static final String EXCHANGE_NAME_EXCUTOR = "excutor_exchange";
    /**
     * 死信交换机对应的队列
     */
    public static final String QUEUE_NAME_EXCUTOR = "excutor_queue";

    /**
     * 死信交换机和队列绑定的RouterKey
     */
     public static final  String ROUTER_KEY_EXCUTOR="excutor.test";

    /**
     * 声明交换机
     * @return
     */
    @Bean
    public Exchange shcheuleExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE_NAME_SCHEULE).durable(true).build();
    }

    /**
     * 声明队列
     * @return
     */
    @Bean
    public Queue shcheuleQueue() {
        //在这里如果修改了这里的配置，再次启动会提示错误信息，因为这些参数随着项目启动不会覆盖原有队列中的参数
        //如果项目启动过需要先删除项目中的队列，再启动
        Map<String,Object> args = new HashMap<>(3);
        //消息过期后，进入到死信交换机
        args.put("x-dead-letter-exchange",EXCHANGE_NAME_EXCUTOR);
        //消息过期后，进入到死信交换机的路由key
        args.put("x-dead-letter-routing-key",ROUTER_KEY_EXCUTOR);
        //过期时间，单位毫秒   10秒钟后执行
        args.put("x-message-ttl",10000);
        return QueueBuilder.durable(QUEUE_NAME_SCHEULE).withArguments(args).build();
    }


    /**
     * 死信交换机
     * @return
     */
    @Bean
    public Exchange excutorExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE_NAME_EXCUTOR).durable(true).build();
    }

    /**
     * 死信队列
     * @return
     */
    @Bean
    public Queue excutorQueue() {
        return QueueBuilder.durable(QUEUE_NAME_EXCUTOR).build();
    }


    /**
     * 普通交换机和普通队列进行绑定
     */
    @Bean
    public Binding shcheuleBinding(@Qualifier(value = "shcheuleQueue") Queue queue, @Qualifier(value = "shcheuleExchange")Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("shcheule.#").noargs();
    }

    /**
     * 死信交换机和死信队列进行绑定
     */
    @Bean
    public Binding excutorBinding(@Qualifier(value = "excutorQueue") Queue queue, @Qualifier(value = "excutorExchange")Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("excutor.#").noargs();
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