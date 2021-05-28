package com.brielage.monitor.Consumer;

import com.brielage.monitor.XML.Heartbeat;
import com.fasterxml.jackson.xml.XmlMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.xml.sax.SAXException;

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
                        // not needed atm
                        //String routingKey = envelope.getRoutingKey();
                        //String contentType = properties.getContentType();

                        long deliveryTag = envelope.getDeliveryTag();
                        //System.out.println("deliveryTag: " + deliveryTag);

                        String b = new String(body);
                        // remove crap otherwise it errors
                        b = b.replace("\uFEFF", "");
                                //"<?xml version=\"1.0\" encoding=\"utf-8\"?>", "");
                        boolean validated = validateXML(b);

                        System.out.println(validated);

                        if(validated) {
                            XmlMapper mapper = new XmlMapper();
                            Heartbeat hb = mapper.readValue(b, Heartbeat.class);
                            System.out.println(hb.toString());
                        }

                        // uncomment when we actually do things with the message
                        // so that it gets removed from queue
                        //channel.basicAck(deliveryTag, false);
                    }
                });
    }
}
