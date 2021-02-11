package rabbitmq.learn.demo2;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class Receiving {

    //队列名字相同，然而队列声明参数不同则声明时候就会抛异常
    private final static String TASK_QUEUE_NAME = "router";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("www.lizhiyu.xyz");
        factory.setUsername("admin");
        factory.setPassword("admin");
        factory.setVirtualHost("/dev");
        factory.setPort(5672);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        //声明为持久的,用于将消息持久化的服务器上
        boolean durable = true;
        channel.queueDeclare(TASK_QUEUE_NAME, durable, false, false, null);

        //当接收端存在1个没有确认的消息数时候,接收端不会再去获得新的任务,来实现任务的公平分配
        channel.basicQos(1);

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");
            try {
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //手工确认，向服务端说明此任务已经被消费
                //第二参数代表是否是多条数据
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }catch (Exception e){
                //第二参数代表是否是多条数据
                //第三个参数代表是否还要重新处理此条数据
                channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, true);
            }finally {
                System.out.println(" [x] Done");
            }
        };
    }

}
