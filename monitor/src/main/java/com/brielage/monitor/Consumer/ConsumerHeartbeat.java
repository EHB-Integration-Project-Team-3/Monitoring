package com.brielage.monitor.Consumer;

import com.brielage.monitor.XML.Heartbeat;
import com.fasterxml.jackson.xml.XmlMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.xml.sax.SAXException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ConsumerHeartbeat extends Consumer {
    public ConsumerHeartbeat(Channel channel,
                             String queueName,
                             boolean autoAck,
                             String consumerTag)
            throws SAXException {
        super(channel, queueName, autoAck, consumerTag);
    }


    public void consume()
            throws IOException {
        channel.basicConsume(queueName, autoAck, consumerTag,
                new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(String consumerTag,
                                               Envelope envelope,
                                               AMQP.BasicProperties properties,
                                               byte[] body)
                            throws IOException {
                        // remove crap otherwise it errors
                        // "\uFEFF" is a byte order mark, we get it from the message
                        // (not a problem on the sender side)
                        String bodyString = new String(body).replace("\uFEFF", "");
                        boolean validated = validateXML(bodyString);

                        System.out.println(validated);

                        if (validated) {
                            process(bodyString);
                        }

                        channel.basicAck(envelope.getDeliveryTag(), false);
                    }
                });
    }

    @Override
    void process(String xmlString)
            throws IOException {
        System.out.println("processing");
        XmlMapper mapper = new XmlMapper();
        Heartbeat heartbeat = mapper.readValue(xmlString, Heartbeat.class);
        System.out.println(heartbeat.toString());

        log(heartbeat.toString());
    }

    @Override
    void log(String value) {
        System.out.println("logging");
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter("/data/heartbeat.txt", true))) {
            writer.append(value);
            writer.newLine();
            writer.close();
            System.out.println("written");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
