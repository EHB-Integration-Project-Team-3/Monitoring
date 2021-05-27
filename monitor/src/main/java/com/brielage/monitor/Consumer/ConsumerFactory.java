package com.brielage.monitor.Consumer;

import com.rabbitmq.client.Channel;
import org.xml.sax.SAXException;

public class ConsumerFactory {
    public Consumer get(String what,
                        Channel channel,
                        String queueName,
                        boolean autoAck)
            throws SAXException {

        switch (what) {
            case "user":
                return new ConsumerUser(channel, queueName, autoAck, what);
            case "heartbeat":
                return new ConsumerHeartbeat(channel, queueName, autoAck, what);
            case "event":
                return new ConsumerEvent(channel, queueName, autoAck, what);
            default:
                throw new IllegalArgumentException();
        }
    }
}
