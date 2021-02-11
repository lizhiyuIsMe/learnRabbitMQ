package rabbitmq.learn.Demo5;

import com.rabbitmq.client.*;

/**
 * 使用topic类型交换机，通过某一类RouterKey进行匹配到队列中
 * 匹配时候 #代表一个或多个单词    *代表仅仅一个单词(相对于direct类型更加灵活)
 */
public class Sending {
    private final static String TASK_QUEUE_NAME = "exchange-queue";
    private final static String EXCHANGE_NAME="exchange_topic";
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
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
            String message="hello";
            //标记队列是持久化的 MessageProperties.PERSISTENT_TEXT_PLAIN
            String errorMessage="error msg";
            String infoMessage="info msg";
            String debugMessage="debug msg";
            channel.basicPublish(EXCHANGE_NAME, "error.test", MessageProperties.PERSISTENT_TEXT_PLAIN, errorMessage.getBytes());
            channel.basicPublish(EXCHANGE_NAME, "error.test.test", MessageProperties.PERSISTENT_TEXT_PLAIN, errorMessage.getBytes());
            channel.basicPublish(EXCHANGE_NAME, "info.test", MessageProperties.PERSISTENT_TEXT_PLAIN, infoMessage.getBytes());
            channel.basicPublish(EXCHANGE_NAME, "debug.test", MessageProperties.PERSISTENT_TEXT_PLAIN, debugMessage.getBytes());
            System.out.println(" [x] Sent '" + message + "'");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
