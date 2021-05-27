package com.brielage.monitor;

import com.brielage.monitor.Consumer.Consumer;
import com.brielage.monitor.Consumer.ConsumerFactory;
import com.brielage.monitor.Consumer.ConsumerHeartbeat;
import com.brielage.monitor.Consumer.ConsumerUser;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@SuppressWarnings({"BusyWait", "ConstantConditions"})
@SpringBootApplication
public class MonitorApplication
        implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(MonitorApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Map<String, String> consumersStartData = new HashMap<>();

        //consumersStartData.put("event", "to-monitoring_event-queue");
        consumersStartData.put("heartbeat", "to-monitoring_heartbeat-queue");
        //consumersStartData.put("user", "to-monitoring_user-queue");

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

                Thread.sleep(500);

                ConsumerFactory consumerFactory = new ConsumerFactory();
                List<Consumer> consumers = new ArrayList<>();
                boolean autoAck = false;

                for (Map.Entry<String, String> e : consumersStartData.entrySet()) {
                    System.out.println("make " + e.getKey());
                    Consumer consumer = consumerFactory.get(e.getKey(), channel, e.getValue(), autoAck);
                    consumer.start();
                    consumers.add(consumer);
                }

                // don't keep making consumers when one is finished with the messages already in
                // queue
                //channel.basicCancel(consumerTag);
                while (channel.isOpen()) {
                }
            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
                Thread.sleep(10000);
            }
        }
    }
}
