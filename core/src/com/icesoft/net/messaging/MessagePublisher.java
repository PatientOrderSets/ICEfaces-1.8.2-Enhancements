package com.icesoft.net.messaging;

import com.icesoft.util.Properties;

import java.io.Serializable;

public interface MessagePublisher {
    void publish(Serializable objectMessage, Properties messageProperties, String topicName);

    void publish(Serializable objectMessage, Properties messageProperties, String messageType, String topicName);

    void publish(Serializable objectMessage, String topicName);

    void publish(Serializable objectMessage, String messageType, String topicName);

    void publish(String textMessage, Properties messageProperties, String topicName);

    void publish(String textMessage, Properties messageProperties, String messageType, String topicName);

    void publish(String textMessage, String topicName);

    void publish(String textMessage, String messageType, String topicName);

    void publishNow(Serializable objectMessage, Properties messageProperties, String topicName);

    void publishNow(Serializable objectMessage, Properties messageProperties, String messageType, String topicName);

    void publishNow(Serializable objectMessage, String topicName);

    void publishNow(Serializable objectMessage, String messageType, String topicName);

    void publishNow(String textMessage, Properties messageProperties, String topicName);

    void publishNow(String textMessage, Properties messageProperties, String messageType, String topicName);

    void publishNow(String textMessage, String topicName);

    void publishNow(String textMessage, String messageType, String topicName);
    
    void stop();
}
