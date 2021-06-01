package com.brielage.monitor.Consumer;

import com.brielage.monitor.Elastic.ElasticRequest;
import com.brielage.monitor.Heartbeats.HeartbeatCollector;
import com.brielage.monitor.Heartbeats.HeartbeatLogger;
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
                        // remove crap otherwise it errors
                        // "\uFEFF" is a byte order mark, we get it from the message
                        // (not a problem on the sender side)
                        String bodyString = new String(body).replace("\uFEFF", "");

                        if (validateXML(bodyString)) process(bodyString);
                        channel.basicAck(envelope.getDeliveryTag(), false);
                    }
                });
    }

    @Override
    void process(String xmlString)
            throws IOException {
        XmlMapper mapper = new XmlMapper();
        Heartbeat heartbeat = mapper.readValue(xmlString, Heartbeat.class);

        HeartbeatCollector.addHeartbeat(heartbeat);
        log(heartbeat);
    }

    @Override
    void log(Object o)
            throws IOException {
        Heartbeat heartbeat = (Heartbeat) o;
        ElasticRequest.sendToElastic("heartbeat", heartbeat);
        HeartbeatLogger.logAppend(heartbeat.getHeader().getSource().toLowerCase(), heartbeat.toString());
    }
}
