package com.icesoft.net.messaging;

import com.icesoft.util.Properties;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NoopMessagePublisher
implements MessagePublisher {
    private static final Log LOG = LogFactory.getLog(NoopMessagePublisher.class);

    public NoopMessagePublisher() {
        // do nothing...
    }                                                     

    public void publish(
        final Serializable objectMessage, final Properties messageProperties, final String topicName) {}

    public void publish(
        final Serializable objectMessage, final Properties messageProperties, final String messageType,
        final String topicName) {}

    public void publish(
        final Serializable objectMessage, final String topicName) {}

    public void publish(
        final Serializable objectMessage, final String messageType, final String topicName) {}

    public void publish(
        final String textMessage, final Properties messageProperties, final String topicName) {}

    public void publish(
        final String textMessage, final Properties messageProperties, final String messageType,
        final String topicName) {}

    public void publish(
        final String textMessage, final String topicName) {}

    public void publish(
        final String textMessage, final String messageType, final String topicName) {}

    public void publishNow(
        final Serializable objectMessage, final Properties messageProperties, final String topicName) {}

    public void publishNow(
        final Serializable objectMessage, final Properties messageProperties, final String messageType,
        final String topicName) {}

    public void publishNow(
        final Serializable objectMessage, final String topicName) {}

    public void publishNow(
        final Serializable objectMessage, final String messageType, final String topicName) {}

    public void publishNow(
        final String textMessage, final Properties messageProperties, final String topicName) {}

    public void publishNow(
        final String textMessage, final Properties messageProperties, final String messageType,
        final String topicName) {}

    public void publishNow(
        final String textMessage, final String topicName) {}

    public void publishNow(
        final String textMessage, final String messageType, final String topicName) {}

    public void stop() {}
}
