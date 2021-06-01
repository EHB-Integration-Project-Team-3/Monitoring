package com.brielage.monitor;

import com.brielage.monitor.Consumer.Consumer;
import com.brielage.monitor.Consumer.ConsumerFactory;
import com.brielage.monitor.Heartbeats.HeartbeatTimer;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@SuppressWarnings({"BusyWait", "ConstantConditions", "MismatchedQueryAndUpdateOfCollection", "InfiniteLoopStatement"})
@SpringBootApplication
public class MonitorApplication
        implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(MonitorApplication.class, args);
    }

    @Override
    public void run(String... args)
            throws Exception {
        Map<String, String> consumersStartData = new HashMap<>();

        //consumersStartData.put("event", "to-monitoring_event-queue");
        consumersStartData.put("heartbeat", "to-monitoring_heartbeat-queue");
        //consumersStartData.put("user", "to-monitoring_user-queue");

        ConnectionFactory factory = new ConnectionFactory();

        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setHost("10.3.17.61");
        factory.setPort(5672);

        while (true) {
            try {
                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel();

                Thread.sleep(500);

                boolean autoAck = false;
                boolean timer = false;

                for (Map.Entry<String, String> e : consumersStartData.entrySet()) {
                    Consumer consumer = ConsumerFactory.get(e.getKey(), channel, e.getValue(),
                            autoAck);
                    consumer.start();

                    if (!timer) {
                        if (e.getKey().equals("heartbeat")) {
                            HeartbeatTimer hbtimer = new HeartbeatTimer();
                            hbtimer.start();
                            timer = true;
                        }
                    }
                }

                // don't keep making consumers when one is finished with the messages already in
                // queue
                //noinspection StatementWithEmptyBody
                while (channel.isOpen()) {
                }
            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
                Thread.sleep(10000);
            }
        }
    }
}
