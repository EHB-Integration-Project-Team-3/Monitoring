package com.brielage.monitor.Consumer;

import com.rabbitmq.client.Channel;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import static org.mockito.Mockito.mock;

public class ConsumerFactoryTest {
    private final Channel mockChannel = mock(Channel.class);

    @Test
    public void getConsumerHeartbeat()
            throws SAXException {
        Assert.assertTrue(ConsumerFactory.get("heartbeat", mockChannel, "heartbeat", false) instanceof ConsumerHeartbeat);
    }

    @Test
    public void getNoConsumerHeartbeat() {
        Assertions.assertThatIllegalArgumentException()
                .isThrownBy(() -> ConsumerFactory.get("test", mockChannel, "heartbeat", false));
    }
}
