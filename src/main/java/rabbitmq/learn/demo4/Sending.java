package rabbitmq.learn.demo4;

import com.rabbitmq.client.*;

/**
 * 使用direct类型交换机,通过发送的RouterKey来判断发送到那个队列中
 * 目的将所有级别的日志信息发送的消费者那里
 */
public class Sending {
    private final static String TASK_QUEUE_NAME = "exchange-test-queue";
    private final static String EXCHANGE_NAME="exchange_direct";
    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("www.lizhiyu.xyz");
        factory.setUsername("admin");
        factory.setPassword("admin");
        factory.setVirtualHost("/dev");
        factory.setPort(5672);
        try(Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();){
            //声明交换机,
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
            String message="hello";
            //标记队列是持久化的 MessageProperties.PERSISTENT_TEXT_PLAIN
            String errorMessage="error msg";
            String infoMessage="info msg";
            String debugMessage="debug msg";
            channel.basicPublish(EXCHANGE_NAME, "errorRoutingKey", MessageProperties.PERSISTENT_TEXT_PLAIN, errorMessage.getBytes());
            channel.basicPublish(EXCHANGE_NAME, "infoRoutingKey", MessageProperties.PERSISTENT_TEXT_PLAIN, infoMessage.getBytes());
            channel.basicPublish(EXCHANGE_NAME, "debugRoutingKey", MessageProperties.PERSISTENT_TEXT_PLAIN, debugMessage.getBytes());
            System.out.println(" [x] Sent '" + message + "'");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
