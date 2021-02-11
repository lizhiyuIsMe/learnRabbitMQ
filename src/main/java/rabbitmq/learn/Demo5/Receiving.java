package rabbitmq.learn.Demo5;

import com.rabbitmq.client.*;

public class Receiving {

    //队列名字相同，然而队列声明参数不同则声明时候就会抛异常
    private final static String TASK_QUEUE_NAME = "exchange-queue";
    private final static String EXCHANGE_NAME="exchange_topic";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("www.lizhiyu.xyz");
        factory.setUsername("admin");
        factory.setPassword("admin");
        factory.setVirtualHost("/dev");
        factory.setPort(5672);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        //当接收端存在1个没有确认的消息数时候,接收端不会再去获得新的任务,来实现任务的公平分配
        channel.basicQos(1);
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        //获得队列名字
        String queueName = channel.queueDeclare().getQueue();
        //指定接收哪些routingkey对应的消息
        channel.queueBind(queueName, EXCHANGE_NAME, "error.*");
        channel.queueBind(queueName, EXCHANGE_NAME, "info.#");
        channel.queueBind(queueName, EXCHANGE_NAME, "debug.#");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");
            //手工确认，向服务端说明此任务已经被消费
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        };
        //如果使用自动确认，则这里是false
        //如果是true则代表消息被拿走就代表消息被处理了
        boolean autoAck = false;
        channel.basicConsume("", autoAck, deliverCallback, consumerTag -> { });
    }

}
