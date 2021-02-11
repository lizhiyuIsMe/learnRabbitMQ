package rabbitmq.learn.demo1;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 使用direct默认的交换机(调用时候不指定交换机),所输入的RouterKey就是队列的名称
 */
public class Sending {
    private final static String QUEUE_NAME = "hello";
    /**
     * 仅仅是一个发送一个接收
     * @param argv
     * @throws Exception
     */
    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("www.lizhiyu.xyz");
        factory.setUsername("admin");
        factory.setPassword("admin");
        factory.setVirtualHost("/dev");
        factory.setPort(5672);
        try(Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();)
        {
            //声明一个队列
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            String message = "Hello World!";
            //将消息发送到队列里
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");
        }
    }

}
