package rabbitmq.learn.demo3;

import com.rabbitmq.client.*;

public class Receiving2 {

    //队列名字相同，然而队列声明参数不同则声明时候就会抛异常
    private final static String TASK_QUEUE_NAME = "exchange-test-queue";
    private final static String EXCHANGE_NAME="exchange_name";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("www.lizhiyu.xyz");
        factory.setUsername("admin");
        factory.setPassword("admin");
        factory.setVirtualHost("/dev");
        factory.setPort(5672);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);

        //获得队列名字
        String queueName = channel.queueDeclare().getQueue();
        //因为这里交换机使用的广播形式，所以routingKey为空
        channel.queueBind(queueName, EXCHANGE_NAME, "");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");
            //手工确认，向服务端说明此任务已经被消费
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

            //代表没有被消费
            //channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, true);
        };

    }

}
