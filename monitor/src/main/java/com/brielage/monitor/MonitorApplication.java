package com.brielage.monitor;

import com.fasterxml.jackson.xml.XmlMapper;
import com.rabbitmq.client.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@SuppressWarnings("BusyWait")
@SpringBootApplication
public class MonitorApplication
        implements CommandLineRunner {
    private final static String queueName = "to-monitoring_heartbeat-queue";

    public static void main(String[] args) {
        SpringApplication.run(MonitorApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();

        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setHost("10.3.17.61");
        factory.setPort(5672);

        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel();

                channel.queueDeclare(
                        queueName,
                        true,
                        false,
                        false,
                        null);

                boolean autoAck = false;
                String consumerTag = "myConsumerTag";

                //noinspection ConstantConditions
                channel.basicConsume(queueName, autoAck, consumerTag,
                        new DefaultConsumer(channel) {
                            @Override
                            public void handleDelivery(String consumerTag,
                                                       Envelope envelope,
                                                       AMQP.BasicProperties properties,
                                                       byte[] body)
                                    throws IOException {
                                // not needed atm
                                //String routingKey = envelope.getRoutingKey();
                                //String contentType = properties.getContentType();
                                long deliveryTag = envelope.getDeliveryTag();

                                System.out.println("deliveryTag: " + deliveryTag);
                                String b = new String(body);
                                // remove crap otherwise it errors
                                b = b.replace("\uFEFF<?xml version=\"1.0\" encoding=\"utf-8\"?>", "");
                                System.out.println(b);
                                System.out.println("\n\n");

                                XmlMapper mapper = new XmlMapper();
                                Heartbeat hb = mapper.readValue(b, Heartbeat.class);
                                System.out.println(hb.toString());

                                // uncomment when we actually do things with the message
                                // so that it gets removed from queue
                                //channel.basicAck(deliveryTag, false);
                            }
                        });

                //channel.basicCancel(consumerTag);
            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
                Thread.sleep(10000);
            }
        }
    }
}
