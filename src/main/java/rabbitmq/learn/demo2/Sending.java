package rabbitmq.learn.demo2;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

/**
 * 创建队列的时候队列持久化到硬盘中
 * 通过消费者正在处理的任务数，达到公平消费  hannel.basicQos(正在处理的最大消息数量);
 * 手动确认机制的使用，如果消费者接收到消息，没有返回给队列是否消费此消息(只要消费者没挂队列就一直等待消费者处理)
 */
public class Sending {
    private final static String TASK_QUEUE_NAME = "router";
    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("www.lizhiyu.xyz");
        factory.setUsername("admin");
        factory.setPassword("admin");
        factory.setVirtualHost("/dev");
        factory.setPort(5672);
        try(Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();){
            //声明为持久的,用于将消息持久化的服务器上
            boolean durable = true;
            channel.queueDeclare(TASK_QUEUE_NAME, durable, false, false, null);
            String message="";
            for(int i=0;i<20;i++){
                message=String.valueOf(i);
                //标记队列是持久化的 MessageProperties.PERSISTENT_TEXT_PLAIN
                channel.basicPublish("", TASK_QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
                System.out.println(" [x] Sent '" + message + "'");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
