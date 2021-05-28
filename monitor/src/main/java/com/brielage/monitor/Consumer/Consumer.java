package com.brielage.monitor.Consumer;

import com.brielage.monitor.XML.XSDValidator;
import com.rabbitmq.client.Channel;
import org.xml.sax.SAXException;

import javax.xml.validation.Schema;
import java.io.IOException;

public abstract class Consumer extends Thread {
    Channel channel;
    String queueName;
    boolean autoAck;
    String consumerTag;
    Schema schema;

    protected Consumer(Channel channel,
                       String queueName,
                       boolean autoAck,
                       String consumerTag)
            throws SAXException {
        this.channel = channel;
        this.queueName = queueName;
        this.autoAck = autoAck;
        this.consumerTag = consumerTag;
        this.schema = XSDValidator.getSchema(consumerTag);
    }

    public abstract void consume()
            throws IOException;

    public void run() {
        try {
            this.consume();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    boolean validateXML(String xmlString) {
        return XSDValidator.validate(schema, xmlString);
    }

    abstract void process(String xmlString)
            throws IOException;

    abstract void log(String value, String source);

    @Override
    public String toString() {
        return "Consumer{" +
                "channel=" + channel +
                ", queueName='" + queueName + '\'' +
                ", autoAck=" + autoAck +
                ", consumerTag='" + consumerTag + '\'' +
                '}';
    }
}
