package rabbitmq.learn.demo3;

import com.rabbitmq.client.*;

/**
 * fanout 类型交换机的使用，使用这种交换机类型，交换机会将接收的每条消息分别发送给此交换机绑定的队列
 * 因为发送给所有的绑定的队列，所以在这里RouterKey没有用了
 */
public class Sending {
    private final static String TASK_QUEUE_NAME = "exchange-test-queue";
    private final static String EXCHANGE_NAME="exchange_name";
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
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
            String message="hello";
            //标记队列是持久化的 MessageProperties.PERSISTENT_TEXT_PLAIN
            channel.basicPublish(EXCHANGE_NAME, "", MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
