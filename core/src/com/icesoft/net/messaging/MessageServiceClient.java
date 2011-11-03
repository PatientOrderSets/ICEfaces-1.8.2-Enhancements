/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * "The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations under
 * the License.
 *
 * The Original Code is ICEfaces 1.5 open source software code, released
 * November 5, 2006. The Initial Developer of the Original Code is ICEsoft
 * Technologies Canada, Corp. Portions created by ICEsoft are Copyright (C)
 * 2004-2006 ICEsoft Technologies Canada, Corp. All Rights Reserved.
 *
 * Contributor(s): _____________________.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"
 * License), in which case the provisions of the LGPL License are
 * applicable instead of those above. If you wish to allow use of your
 * version of this file only under the terms of the LGPL License and not to
 * allow others to use your version of this file under the MPL, indicate
 * your decision by deleting the provisions above and replace them with
 * the notice and other provisions required by the LGPL License. If you do
 * not delete the provisions above, a recipient may use your version of
 * this file under either the MPL or the LGPL License."
 */
package com.icesoft.net.messaging;

import com.icesoft.util.ServerUtility;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MessageServiceClient {
    public static final String PUSH_TOPIC_NAME = "icefacesPush";

    private static final Log LOG =
        LogFactory.getLog(MessageServiceClient.class);

    private final Map messageHandlerMap = new HashMap();
    private final Map messagePipelineMap = new HashMap();

    private Administrator administrator;
    private MessageServiceConfiguration messageServiceConfiguration;
    private MessageServiceAdapter messageServiceAdapter;
    private Properties baseMessageProperties = new Properties();
    private String name;

    private Timer timer = new Timer();

    public MessageServiceClient(
        final MessageServiceConfiguration messageServiceConfiguration,
        final MessageServiceAdapter messageServiceAdapter,
        final ServletContext servletContext)
    throws IllegalArgumentException {
        this(
            null,
            messageServiceConfiguration,
            messageServiceAdapter,
            servletContext);
    }

    public MessageServiceClient(
        final MessageServiceConfiguration messageServiceConfiguration,
        final MessageServiceAdapter messageServiceAdapter,
        final String servletContextPath)
    throws IllegalArgumentException {
        this(
            null,
            messageServiceConfiguration,
            messageServiceAdapter,
            servletContextPath);
    }

    public MessageServiceClient(
        final MessageServiceAdapter messageServiceAdapter,
        final ServletContext servletContext)
    throws IllegalArgumentException {
        this(null, null, messageServiceAdapter, servletContext);
    }

    public MessageServiceClient(
        final MessageServiceAdapter messageServiceAdapter,
        final String servletContextPath)
    throws IllegalArgumentException {
        this(null, null, messageServiceAdapter, servletContextPath);
    }

    public MessageServiceClient(
        final String name,
        final MessageServiceConfiguration messageServiceConfiguration,
        final MessageServiceAdapter messageServiceAdapter,
        final ServletContext servletContext)
    throws IllegalArgumentException {
        this(name, messageServiceConfiguration, messageServiceAdapter);
        setBaseMessageProperties(servletContext);
    }

    public MessageServiceClient(
        final String name,
        final MessageServiceConfiguration messageServiceConfiguration,
        final MessageServiceAdapter messageServiceAdapter,
        final String servletContextPath)
    throws IllegalArgumentException {
        this(name, messageServiceConfiguration, messageServiceAdapter);
        setBaseMessageProperties(servletContextPath);
    }

    public MessageServiceClient(
        final String name, final MessageServiceAdapter messageServiceAdapter,
        final ServletContext servletContext)
    throws IllegalArgumentException {
        this(name, null, messageServiceAdapter, servletContext);
    }

    public MessageServiceClient(
        final String name, final MessageServiceAdapter messageServiceAdapter,
        final String servletContextPath)
    throws IllegalArgumentException {
        this(name, null, messageServiceAdapter, servletContextPath);
    }

    private MessageServiceClient(
        final String name,
        final MessageServiceConfiguration messageServiceConfiguration,
        final MessageServiceAdapter messageServiceAdapter)
    throws IllegalArgumentException {
        if (messageServiceAdapter == null) {
            throw new IllegalArgumentException("messageServiceAdapter is null");
        }
        this.name = name;
        if (messageServiceConfiguration != null) {
            this.messageServiceConfiguration = messageServiceConfiguration;
        } else {
            this.messageServiceConfiguration =
                new MessageServiceConfigurationProperties();
            this.messageServiceConfiguration.setMessageMaxDelay(100);
            this.messageServiceConfiguration.setMessageMaxLength(10 * 1024);
        }
        this.messageServiceAdapter = messageServiceAdapter;
        this.messageServiceAdapter.setMessageServiceClient(this);
    }

    /**
     * <p>
     *   Adds the specified <code>messageHandler</code> to this
     *   MessageServiceClient for the topic with the specified
     *   <code>topicName</code>.
     * </p>
     *
     * @param      messageHandler
     *                 the MessageHandler to be added.
     * @param      topicName
     *                 the topic name of the topic to be associated with the
     *                 MessageHandler.
     * @see        MessageServiceAdapter#addMessageHandler(MessageHandler, String)
     * @see        #removeMessageHandler(MessageHandler, String)
     */
    public void addMessageHandler(
        final MessageHandler messageHandler, final String topicName) {

        if (messageHandler != null &&
            topicName != null && topicName.trim().length() != 0) {

            Map _messageHandlerWrapperMap;
            if (messageHandlerMap.containsKey(topicName)) {
                _messageHandlerWrapperMap =
                    (Map)messageHandlerMap.get(topicName);
                if (_messageHandlerWrapperMap.containsKey(messageHandler)) {
                    return;
                }
            } else {
                _messageHandlerWrapperMap = new HashMap();
                messageHandlerMap.put(topicName, _messageHandlerWrapperMap);
            }
            MessageSeparator _messageSeparator;
            if (!(messageHandler instanceof MessageSeparator)) {
                _messageSeparator = new MessageSeparator(messageHandler);
            } else {
                _messageSeparator = (MessageSeparator)messageHandler;
            }
            _messageHandlerWrapperMap.put(messageHandler, _messageSeparator);
            messageServiceAdapter.addMessageHandler(
                _messageSeparator, topicName);
        }
    }

    /**
     * <p>
     *   Closes this MessageServiceClient's connection to the message service
     *   and disposes of all relevant resources.
     * </p>
     *
     * @throws     MessageServiceException
     *                 if the message service fails to close the connection due
     *                 to some internal error.
     * @see        MessageServiceAdapter#close()
     * @see        #stop()
     */
    public void close()
    throws MessageServiceException {
        try {
            messageServiceAdapter.close();
        } finally {
            messageHandlerMap.clear();
        }
    }

    /**
     * <p>
     *   Gets the MessageServiceAdapter of this MessageServiceClient.
     * </p>
     *
     * @return     the MessageServiceAdapter.
     */
    public MessageServiceAdapter getMessageServiceAdapter() {
        return messageServiceAdapter;
    }

    /**
     * <p>
     *   Gets the MessageServiceConfiguration of this MessageServiceClient.
     * </p>
     *
     * @return     the MessageServiceConfiguration.
     */
    public MessageServiceConfiguration getMessageServiceConfiguration() {
        return messageServiceConfiguration;
    }

    public Administrator getAdministrator() {
        return administrator;
    }

    /**
     * <p>
     *   Gets the name of this MessageServiceClient.
     * </p>
     *
     * @return     the name.
     */
    public String getName() {
        return name;
    }

    /**
     * <p>
     *   Gets the topic names of the topics on which this MessageServiceClient
     *   is a publisher.
     * </p>
     *
     * @return     the topic names.
     * @see        MessageServiceAdapter#getPublisherTopicNames()
     * @see        #getSubscriberTopicNames()
     */
    public String[] getPublisherTopicNames() {
        return messageServiceAdapter.getPublisherTopicNames();
    }

    /**
     * <p>
     *   Gets the topic names of the topics to which this MessageServiceClient
     *   is a subscriber.
     * </p>
     *
     * @return     the topic names.
     * @see        MessageServiceAdapter#getSubscriberTopicNames()
     * @see        #getPublisherTopicNames()
     */
    public String[] getSubscriberTopicNames() {
        return messageServiceAdapter.getSubscriberTopicNames();
    }

    /**
     * <p>
     *   Checks to see if this MessageServiceClient is publishing on the topic
     *   with the specified <code>topicName</code>.
     * </p>
     *
     * @param      topicName
     *                 the topic name to be checked.
     * @return     <code>true</code> if publishing on the topic with the
     *             specified <code>topicName</code>, <code>false</code> if not.
     * @see        MessageServiceAdapter#isPublishingOn(String)
     * @see        #isSubscribedTo(String)
     */
    public boolean isPublishingOn(final String topicName) {
        return
            topicName != null && topicName.trim().length() != 0 &&
            messageServiceAdapter.isPublishingOn(topicName);
    }

    /**
     * <p>
     *   Checks to see if this MessageServiceClient is subscribed to the topic
     *   with the specified <code>topicName</code>.
     * </p>
     *
     * @param      topicName
     *                 the topic name to be checked.
     * @return     <code>true</code> if subscribed to the topic with the
     *             specified <code>topicName</code>, <code>false</code> if not.
     * @see        MessageServiceAdapter#isSubscribedTo(String)
     * @see        #isPublishingOn(String)
     */
    public boolean isSubscribedTo(final String topicName) {
        return
            topicName != null && topicName.trim().length() != 0 &&
            messageServiceAdapter.isSubscribedTo(topicName);
    }

    /**
     * <p>
     *   Publishes the specified <code>object</code> to the topic with the
     *   specified <code>topicName</code>. That is, the <code>object</code> will
     *   be put into a MessagePipeline before actually being published as an
     *   ObjectMessage, with the specified <code>messageProperties</code>, to
     *   the message service.
     * </p>
     * <p>
     *   If this MessageServiceClient is not a publisher on the topic with the
     *   <code>topicName</code> yet, it will automatically add itself as a
     *   publisher.
     * </p>
     *
     * @param      object
     *                 the object to be published.
     * @param      messageProperties
     *                 the message properties for the ObjectMessage.
     * @param      topicName
     *                 the topic name of the topic to publish on.
     * @see        #publish(Serializable, Properties, String, String)
     * @see        #publish(Serializable, String, String)
     * @see        #publish(Serializable, String)
     * @see        ObjectMessage
     * @see        MessagePipeline
     */
    public void publish(
        final Serializable object, final Properties messageProperties,
        final String topicName) {

        if (object != null &&
            topicName != null && topicName.trim().length() != 0) {

            publish(
                createMessage(object, baseMessageProperties, messageProperties),
                topicName);
        }
    }

    /**
     * <p>
     *   Publishes the specified <code>object</code> to the topic with the
     *   specified <code>topicName</code>. That is, the <code>object</code> will
     *   be put into a MessagePipeline before actually being published as an
     *   ObjectMessage, with the specified <code>messageProperties</code> and
     *   <code>messageType</code>, to the message service.
     * </p>
     * <p>
     *   If this MessageServiceClient is not a publisher on the topic with the
     *   <code>topicName</code> yet, it will automatically add itself as a
     *   publisher.
     * </p>
     *
     * @param      object
     *                 the object to be published.
     * @param      messageProperties
     *                 the message properties for the ObjectMessage.
     * @param      messageType
     *                 the message type for the ObjectMessage.
     * @param      topicName
     *                 the topic name of the topic to publish on.
     * @see        #publish(Serializable, Properties, String)
     * @see        #publish(Serializable, String, String)
     * @see        #publish(Serializable, String)
     * @see        ObjectMessage
     * @see        MessagePipeline
     */
    public void publish(
        final Serializable object, final Properties messageProperties,
        final String messageType, final String topicName) {

        if (object != null &&
            topicName != null && topicName.trim().length() != 0) {

            publish(
                object,
                getMessageProperties(messageProperties, messageType),
                topicName);
        }
    }

    /**
     * <p>
     *   Publishes the specified <code>object</code> to the topic with the
     *   specified <code>topicName</code>. That is, the <code>object</code> will
     *   be put into a MessagePipeline before actually being published as an
     *   ObjectMessage to the message service.
     * </p>
     * <p>
     *   If this MessageServiceClient is not a publisher on the topic with the
     *   <code>topicName</code> yet, it will automatically add itself as a
     *   publisher.
     * </p>
     *
     * @param      object
     *                 the object to be published.
     * @param      topicName
     *                 the topic name of the topic to publish on.
     * @see        #publish(Serializable, Properties, String)
     * @see        #publish(Serializable, Properties, String, String)
     * @see        #publish(Serializable, String, String)
     * @see        ObjectMessage
     * @see        MessagePipeline
     */
    public void publish(final Serializable object, final String topicName) {
        if (object != null &&
            topicName != null && topicName.trim().length() != 0) {

            publish(object, (Properties)null, topicName);
        }
    }

    /**
     * <p>
     *   Publishes the specified <code>object</code> to the topic with the
     *   specified <code>topicName</code>. That is, the <code>object</code> will
     *   be put into a MessagePipeline before actually being published as an
     *   ObjectMessage, with the specified <code>messageType</code>, to the
     *   message service.
     * </p>
     * <p>
     *   If this MessageServiceClient is not a publisher on the topic with the
     *   <code>topicName</code> yet, it will automatically add itself as a
     *   publisher.
     * </p>
     *
     * @param      object
     *                 the object to be published.
     * @param      messageType
     *                 the message type for the ObjectMessage.
     * @param      topicName
     *                 the topic name of the topic to publish on.
     * @see        #publish(Serializable, Properties, String)
     * @see        #publish(Serializable, Properties, String, String)
     * @see        #publish(Serializable, String)
     * @see        ObjectMessage
     * @see        MessagePipeline
     */
    public void publish(
        final Serializable object, final String messageType,
        final String topicName) {

        if (object != null &&
            topicName != null && topicName.trim().length() != 0) {

            publish(object, getMessageProperties(null, messageType), topicName);
        }
    }

    /**
     * <p>
     *   Publishes the specified <code>text</code> to the topic with the
     *   specified <code>topicName</code>. That is, the <code>text</code> will
     *   be put into a MessagePipeline before actually being published as a
     *   TextMessage, with the specified <code>messageProperties</code>, to
     *   the message service.
     * </p>
     * <p>
     *   If this MessageServiceClient is not a publisher on the topic with the
     *   <code>topicName</code> yet, it will automatically add itself as a
     *   publisher.
     * </p>
     *
     * @param      text
     *                 the text to be published.
     * @param      messageProperties
     *                 the message properties for the TextMessage.
     * @param      topicName
     *                 the topic name of the topic to publish on.
     * @see        #publish(Serializable, Properties, String, String)
     * @see        #publish(Serializable, String, String)
     * @see        #publish(Serializable, String)
     * @see        TextMessage
     * @see        MessagePipeline
     */
    public void publish(
        final String text, final Properties messageProperties,
        final String topicName) {

        if (text != null && text.trim().length() != 0 &&
            topicName != null && topicName.trim().length() != 0) {

            publish(
                createMessage(text, baseMessageProperties, messageProperties),
                topicName);
        }
    }

    /**
     * <p>
     *   Publishes the specified <code>text</code> to the topic with the
     *   specified <code>topicName</code>. That is, the <code>text</code> will
     *   be put into a MessagePipeline before actually being published as a
     *   TextMessage, with the specified <code>messageProperties</code> and
     *   <code>messageType</code>, to the message service.
     * </p>
     * <p>
     *   If this MessageServiceClient is not a publisher on the topic with the
     *   <code>topicName</code> yet, it will automatically add itself as a
     *   publisher.
     * </p>
     *
     * @param      text
     *                 the text to be published.
     * @param      messageProperties
     *                 the message properties for the TextMessage.
     * @param      messageType
     *                 the message type for the TextMessage.
     * @param      topicName
     *                 the topic name of the topic to publish on.
     * @see        #publish(Serializable, Properties, String)
     * @see        #publish(Serializable, String, String)
     * @see        #publish(Serializable, String)
     * @see        TextMessage
     * @see        MessagePipeline
     */
    public void publish(
        final String text, final Properties messageProperties,
        final String messageType, final String topicName) {

        if (text != null && text.trim().length() != 0 &&
            topicName != null && topicName.trim().length() != 0) {

            publish(
                text,
                getMessageProperties(messageProperties, messageType),
                topicName);
        }
    }

    /**
     * <p>
     *   Publishes the specified <code>text</code> to the topic with the
     *   specified <code>topicName</code>. That is, the <code>text</code> will
     *   be put into a MessagePipeline before actually being published as a
     *   TextMessage to the message service.
     * </p>
     * <p>
     *   If this MessageServiceClient is not a publisher on the topic with the
     *   <code>topicName</code> yet, it will automatically add itself as a
     *   publisher.
     * </p>
     *
     * @param      text
     *                 the text to be published.
     * @param      topicName
     *                 the topic name of the topic to publish on.
     * @see        #publish(Serializable, Properties, String)
     * @see        #publish(Serializable, Properties, String, String)
     * @see        #publish(Serializable, String, String)
     * @see        TextMessage
     * @see        MessagePipeline
     */
    public void publish(final String text, final String topicName) {
        if (text != null && text.trim().length() != 0 &&
            topicName != null && topicName.trim().length() != 0) {

            publish(text, (Properties)null, topicName);
        }
    }

    /**
     * <p>
     *   Publishes the specified <code>text</code> to the topic with the
     *   specified <code>topicName</code>. That is, the <code>text</code> will
     *   be put into a MessagePipeline before actually being published as a
     *   TextMessage, with the specified <code>messageType</code>, to the
     *   message service.
     * </p>
     * <p>
     *   If this MessageServiceClient is not a publisher on the topic with the
     *   <code>topicName</code> yet, it will automatically add itself as a
     *   publisher.
     * </p>
     *
     * @param      text
     *                 the text to be published.
     * @param      messageType
     *                 the message type for the TextMessage.
     * @param      topicName
     *                 the topic name of the topic to publish on.
     * @see        #publish(Serializable, Properties, String)
     * @see        #publish(Serializable, Properties, String, String)
     * @see        #publish(Serializable, String)
     * @see        TextMessage
     * @see        MessagePipeline
     */
    public void publish(
        final String text, final String messageType, final String topicName) {

        if (text != null && text.trim().length() != 0 &&
            topicName != null && topicName.trim().length() != 0) {

            publish(text, getMessageProperties(null, messageType), topicName);
        }
    }

    /**
     * <p>
     *   Publishes the specified <code>object</code> to the topic with the
     *   specified <code>topicName</code>. That is, the <code>object</code> will
     *   be published immediately as an ObjectMessage, with the specified
     *   <code>messageProperties</code>, to the message service.
     * </p>
     * <p>
     *   If this MessageServiceClient is not a publisher on the topic with the
     *   <code>topicName</code> yet, it will automatically add itself as a
     *   publisher.
     * </p>
     *
     * @param      object
     *                 the object to be published.
     * @param      messageProperties
     *                 the message properties for the ObjectMessage.
     * @param      topicName
     *                 the topic name of the topic to publish on.
     * @throws     MessageServiceException
     *                 if the publishing of the message failed.
     * @see        #publish(Serializable, Properties, String, String)
     * @see        #publish(Serializable, String, String)
     * @see        #publish(Serializable, String)
     * @see        ObjectMessage
     * @see        MessagePipeline
     */
    public void publishNow(
        final Serializable object, final Properties messageProperties,
        final String topicName)
    throws MessageServiceException {
        if (object != null &&
            topicName != null && topicName.trim().length() != 0) {

            publishNow(
                createMessage(object, baseMessageProperties, messageProperties),
                topicName);
        }
    }

    /**
     * <p>
     *   Publishes the specified <code>object</code> to the topic with the
     *   specified <code>topicName</code>. That is, the <code>object</code> will
     *   be published immediately as an ObjectMessage, with the specified
     *   <code>messageProperties</code> and <code>messageType</code>, to the
     *   message service.
     * </p>
     * <p>
     *   If this MessageServiceClient is not a publisher on the topic with the
     *   <code>topicName</code> yet, it will automatically add itself as a
     *   publisher.
     * </p>
     *
     * @param      object
     *                 the object to be published.
     * @param      messageProperties
     *                 the message properties for the ObjectMessage.
     * @param      messageType
     *                 the message type for the ObjectMessage.
     * @param      topicName
     *                 the topic name of the topic to publish on.
     * @throws     MessageServiceException
     *                 if the publishing of the message failed.
     * @see        #publish(Serializable, Properties, String)
     * @see        #publish(Serializable, String, String)
     * @see        #publish(Serializable, String)
     * @see        ObjectMessage
     * @see        MessagePipeline
     */
    public void publishNow(
        final Serializable object, final Properties messageProperties,
        final String messageType, final String topicName)
    throws MessageServiceException {
        if (object != null &&
            topicName != null && topicName.trim().length() != 0) {

            publishNow(
                object,
                getMessageProperties(messageProperties, messageType),
                topicName);
        }
    }

    /**
     * <p>
     *   Publishes the specified <code>object</code> to the topic with the
     *   specified <code>topicName</code>. That is, the <code>object</code> will
     *   be published immediately as an ObjectMessage to the message service.
     * </p>
     * <p>
     *   If this MessageServiceClient is not a publisher on the topic with the
     *   <code>topicName</code> yet, it will automatically add itself as a
     *   publisher.
     * </p>
     *
     * @param      object
     *                 the object to be published.
     * @param      topicName
     *                 the topic name of the topic to publish on.
     * @throws     MessageServiceException
     *                 if the publishing of the message failed.
     * @see        #publish(Serializable, Properties, String)
     * @see        #publish(Serializable, Properties, String, String)
     * @see        #publish(Serializable, String, String)
     * @see        ObjectMessage
     * @see        MessagePipeline
     */
    public void publishNow(final Serializable object, final String topicName)
    throws MessageServiceException {
        if (object != null &&
            topicName != null && topicName.trim().length() != 0) {

            publishNow(object, (Properties)null, topicName);
        }
    }

    /**
     * <p>
     *   Publishes the specified <code>object</code> to the topic with the
     *   specified <code>topicName</code>. That is, the <code>object</code> will
     *   be published immediately as an ObjectMessage, with the specified
     *   <code>messageType</code>, to the message service.
     * </p>
     * <p>
     *   If this MessageServiceClient is not a publisher on the topic with the
     *   <code>topicName</code> yet, it will automatically add itself as a
     *   publisher.
     * </p>
     *
     * @param      object
     *                 the object to be published.
     * @param      messageType
     *                 the message type for the ObjectMessage.
     * @param      topicName
     *                 the topic name of the topic to publish on.
     * @throws     MessageServiceException
     *                 if the publishing of the message failed.
     * @see        #publish(Serializable, Properties, String)
     * @see        #publish(Serializable, Properties, String, String)
     * @see        #publish(Serializable, String)
     * @see        ObjectMessage
     * @see        MessagePipeline
     */
    public void publishNow(
        final Serializable object, final String messageType,
        final String topicName)
    throws MessageServiceException {
        if (object != null &&
            topicName != null && topicName.trim().length() != 0) {

            publishNow(
                object, getMessageProperties(null, messageType), topicName);
        }
    }

    /**
     * <p>
     *   Publishes the specified <code>text</code> to the topic with the
     *   specified <code>topicName</code>. That is, the <code>text</code> will
     *   be published immediately as a TextMessage, with the specified
     *   <code>messageProperties</code>, to the message service.
     * </p>
     * <p>
     *   If this MessageServiceClient is not a publisher on the topic with the
     *   <code>topicName</code> yet, it will automatically add itself as a
     *   publisher.
     * </p>
     *
     * @param      text
     *                 the text to be published.
     * @param      messageProperties
     *                 the message properties for the TextMessage.
     * @param      topicName
     *                 the topic name of the topic to publish on.
     * @throws     MessageServiceException
     *                 if the publishing of the message failed.
     * @see        #publish(Serializable, Properties, String, String)
     * @see        #publish(Serializable, String, String)
     * @see        #publish(Serializable, String)
     * @see        TextMessage
     * @see        MessagePipeline
     */
    public void publishNow(
        final String text, final Properties messageProperties,
        final String topicName)
    throws MessageServiceException {

        if (text != null && text.trim().length() != 0 &&
            topicName != null && topicName.trim().length() != 0) {

            publishNow(
                createMessage(text, baseMessageProperties, messageProperties),
                topicName);
        }
    }

    /**
     * <p>
     *   Publishes the specified <code>text</code> to the topic with the
     *   specified <code>topicName</code>. That is, the <code>text</code> will
     *   be published immediately as a TextMessage, with the specified
     *   <code>messageProperties</code> and <code>messageType</code>, to the
     *   message service.
     * </p>
     * <p>
     *   If this MessageServiceClient is not a publisher on the topic with the
     *   <code>topicName</code> yet, it will automatically add itself as a
     *   publisher.
     * </p>
     *
     * @param      text
     *                 the text to be published.
     * @param      messageProperties
     *                 the message properties for the TextMessage.
     * @param      messageType
     *                 the message type for the TextMessage.
     * @param      topicName
     *                 the topic name of the topic to publish on.
     * @throws     MessageServiceException
     *                 if the publishing of the message failed.
     * @see        #publish(Serializable, Properties, String)
     * @see        #publish(Serializable, String, String)
     * @see        #publish(Serializable, String)
     * @see        TextMessage
     * @see        MessagePipeline
     */
    public void publishNow(
        final String text, final Properties messageProperties,
        final String messageType, final String topicName)
    throws MessageServiceException {
        if (text != null && text.trim().length() != 0 &&
            topicName != null && topicName.trim().length() != 0) {

            publishNow(
                text,
                getMessageProperties(messageProperties, messageType),
                topicName);
        }
    }

    /**
     * <p>
     *   Publishes the specified <code>text</code> to the topic with the
     *   specified <code>topicName</code>. That is, the <code>text</code> will
     *   be published immediately as a TextMessage to the message service.
     * </p>
     * <p>
     *   If this MessageServiceClient is not a publisher on the topic with the
     *   <code>topicName</code> yet, it will automatically add itself as a
     *   publisher.
     * </p>
     *
     * @param      text
     *                 the text to be published.
     * @param      topicName
     *                 the topic name of the topic to publish on.
     * @throws     MessageServiceException
     *                 if the publishing of the message failed.
     * @see        #publish(Serializable, Properties, String)
     * @see        #publish(Serializable, Properties, String, String)
     * @see        #publish(Serializable, String, String)
     * @see        TextMessage
     * @see        MessagePipeline
     */
    public void publishNow(final String text, final String topicName)
    throws MessageServiceException {
        if (text != null && text.trim().length() != 0 &&
            topicName != null && topicName.trim().length() != 0) {

            publishNow(text, (Properties)null, topicName);
        }
    }

    /**
     * <p>
     *   Publishes the specified <code>text</code> to the topic with the
     *   specified <code>topicName</code>. That is, the <code>text</code> will
     *   be published immediately as a TextMessage, with the specified
     *   <code>messageType</code>, to the message service.
     * </p>
     * <p>
     *   If this MessageServiceClient is not a publisher on the topic with the
     *   <code>topicName</code> yet, it will automatically add itself as a
     *   publisher.
     * </p>
     *
     * @param      text
     *                 the text to be published.
     * @param      messageType
     *                 the message type for the TextMessage.
     * @param      topicName
     *                 the topic name of the topic to publish on.
     * @throws     MessageServiceException
     *                 if the publishing of the message failed.
     * @see        #publish(Serializable, Properties, String)
     * @see        #publish(Serializable, Properties, String, String)
     * @see        #publish(Serializable, String)
     * @see        TextMessage
     * @see        MessagePipeline
     */
    public void publishNow(
        final String text, final String messageType, final String topicName)
    throws MessageServiceException {
        if (text != null && text.trim().length() != 0 &&
            topicName != null && topicName.trim().length() != 0) {

            publishNow(
                text, getMessageProperties(null, messageType), topicName);
        }
    }

    /**
     * <p>
     *   Removes the specified <code>MessageHandler</code> from this
     *   MessageServiceClient for the topic with the specified
     *   <code>topicName</code>.
     * </p>
     *
     * @param      messageHandler
     *                 the MessageHandler to be removed.
     * @param      topicName
     *                 the topic name of the topic associated with the
     *                 MessageHandler.
     * @see        MessageServiceAdapter#removeMessageHandler(MessageHandler, String)
     * @see        #addMessageHandler(MessageHandler, String)
     */
    public void removeMessageHandler(
        final MessageHandler messageHandler, final String topicName) {

        if (messageHandler != null &&
            topicName != null && topicName.trim().length() != 0) {

            if (messageHandlerMap.containsKey(topicName)) {
                Map _messageHandlerWrapperMap =
                    (Map)messageHandlerMap.get(topicName);
                if (_messageHandlerWrapperMap.containsKey(messageHandler)) {
                    messageServiceAdapter.removeMessageHandler(
                        (MessageSeparator)
                            _messageHandlerWrapperMap.remove(messageHandler),
                        topicName);
                    if (_messageHandlerWrapperMap.isEmpty()) {
                        messageHandlerMap.remove(topicName);
                    }
                }
            }
        }
    }

    public void setAdministrator(final Administrator administrator) {
        this.administrator = administrator;
    }

    /**
     * <p>
     *   Starts this MessageServiceClient's delivery of incoming messages.
     * </p>
     *
     * @throws     MessageServiceException
     *                 if the message service fails to start message delivery
     *                 due to some internal error.
     * @see        MessageServiceAdapter#start()
     * @see        #stop()
     */
    public void start()
    throws MessageServiceException {
        messageServiceAdapter.start();
    }

    /**
     * <p>
     *   Stops this MessageServiceClient's delivery of incoming messages. The
     *   delivery can be restarted using the <code>start()</code> method.
     * </p>
     * <p>
     *   Stopping this MessageServiceClient's message delivery has no effect on
     *   its ability to send messages.
     * </p>
     *
     * @throws     MessageServiceException
     *                 if the message service fails to stop message delivery due
     *                 to some internal error.
     * @see        MessageServiceAdapter#stop()
     * @see        #start()
     * @see        #close()
     */
    public void stop()
    throws MessageServiceException {
        messageServiceAdapter.stop();
    }

    /**
     * <p>
     *   Subscribes this MessageServiceClient to the topic with the specified
     *   <code>topicName</code>. This type of subscription has no message
     *   selector and automatically inhibits the delivery of incoming messages
     *   published by this MessageServiceClient's own connection.
     * </p>
     *
     * @param      topicName
     *                 the topic name of the topic to subscribe to.
     * @throws     MessageServiceException
     *                 if the message service fails to subscribe due to some
     *                 internal error.
     * @see        MessageServiceAdapter#subscribe(String, MessageSelector, boolean)
     * @see        #subscribe(String, MessageSelector, boolean)
     * @see        #subscribe(String, boolean)
     * @see        #subscribe(String, MessageSelector)
     * @see        #unsubscribe(String)
     */
    public void subscribe(final String topicName)
    throws MessageServiceException {
        subscribe(topicName, null, true);
    }

    /**
     * <p>
     *   Subscribes this MessageServiceClient to the topic with the specified
     *   <code>topicName</code>. This type of subscription has no message
     *   selector.
     * </p>
     *
     * @param      topicName
     *                 the topic name of the topic to subscribe to.
     * @param      noLocal
     *                 if <code>true</code> inhibits the delivery of incoming
     *                 messages published by this MessageServiceClient's own
     *                 connection.
     * @throws     MessageServiceException
     *                 if the message service fails to subscribe due to some
     *                 internal error.
     * @see        MessageServiceAdapter#subscribe(String, MessageSelector, boolean)
     * @see        #subscribe(String, MessageSelector, boolean)
     * @see        #subscribe(String)
     * @see        #subscribe(String, MessageSelector)
     * @see        #unsubscribe(String)
     */
    public void subscribe(final String topicName, final boolean noLocal)
    throws MessageServiceException {
        subscribe(topicName, null, noLocal);
    }

    /**
     * <p>
     *   Subscribes this MessageServiceClient to the topic with the specified
     *   <code>topicName</code>. This type of subscription automatically
     *   inhibits the delivery of incoming messages published by this
     *   MessageServiceClient's own connection.
     * </p>
     *
     * @param      topicName
     *                 the topic name of the topic to subscribe to.
     * @param      messageSelector
     *                 only incoming messages with properties matching the
     *                 MessageSelector's expression are delivered.
     * @throws     MessageServiceException
     *                 if the message service fails to subscribe due to some
     *                 internal error.
     * @see        MessageServiceAdapter#subscribe(String, MessageSelector, boolean)
     * @see        #subscribe(String, MessageSelector, boolean)
     * @see        #subscribe(String)
     * @see        #subscribe(String, boolean)
     * @see        #unsubscribe(String)
     */
    public void subscribe(
        final String topicName, final MessageSelector messageSelector)
    throws MessageServiceException {
        subscribe(topicName, messageSelector, true);
    }

    /**
     * <p>
     *   Subscribes this MessageServiceClient to the topic with the specified
     *   <code>topicName</code>..
     * </p>
     *
     * @param      topicName
     *                 the topic name of the topic to subscribe to.
     * @param      messageSelector
     *                 only incoming messages with properties matching the
     *                 MessageSelector's expression are delivered.
     * @param      noLocal
     *                 if <code>true</code> inhibits the delivery of incoming
     *                 messages published by this MessageServiceClient's own
     *                 connection.
     * @throws     MessageServiceException
     *                 if the message service fails to subscribe due to some
     *                 internal error.
     * @see        MessageServiceAdapter#subscribe(String, MessageSelector, boolean)
     * @see        #subscribe(String)
     * @see        #subscribe(String, boolean)
     * @see        #subscribe(String, MessageSelector)
     * @see        #unsubscribe(String)
     */
    public void subscribe(
        final String topicName, final MessageSelector messageSelector,
        final boolean noLocal)
    throws MessageServiceException {
        if (topicName != null && topicName.trim().length() != 0) {
            messageServiceAdapter.subscribe(
                topicName, messageSelector, noLocal);
        }
    }

    /**
     * <p>
     *   Unsubscribes this MessageServiceClient from the topic with the
     *   specified <code>topicName</code>.
     * </p>
     *
     * @param      topicName
     *                 the topic name of the topic to unsubscribe from.
     * @throws     MessageServiceException
     *                 if the message service fails to unsubscribe due to some
     *                 internal error.
     * @see        MessageServiceAdapter#unsubscribe(String)
     * @see        #subscribe(String)
     * @see        #subscribe(String, boolean)
     * @see        #subscribe(String, MessageSelector)
     * @see        #subscribe(String, MessageSelector, boolean)
     */
    public void unsubscribe(final String topicName)
    throws MessageServiceException {
        if (topicName != null && topicName.trim().length() != 0) {
            try {
                messageServiceAdapter.unsubscribe(topicName);
            } finally {
                messageHandlerMap.remove(topicName);
            }
        }
    }

    void schedule(final PublishTask publishTask, final long delay) {
        try {
            timer.schedule(publishTask, delay);
        } catch (IllegalStateException exception) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(
                    "Task already scheduled or cancelled, " +
                        "timer was cancelled, or timer thread terminated.",
                    exception);
            }
        }
    }

    private static void addMessageProperties(
        final Properties messageProperties, final Message message) {

        if (messageProperties == null) {
            return;
        }
        Iterator _messageProperties = messageProperties.entrySet().iterator();
        while (_messageProperties.hasNext()) {
            Map.Entry _messageProperty = (Map.Entry)_messageProperties.next();
            Object _value = _messageProperty.getValue();
            if (_value instanceof Boolean) {
                message.setBooleanProperty(
                    (String)_messageProperty.getKey(),
                    ((Boolean)_value).booleanValue());
            } else if (_value instanceof Byte) {
                message.setByteProperty(
                    (String)_messageProperty.getKey(),
                    ((Byte)_value).byteValue());
            } else if (_value instanceof Double) {
                message.setDoubleProperty(
                    (String)_messageProperty.getKey(),
                    ((Double)_value).doubleValue());
            } else if (_value instanceof Float) {
                message.setFloatProperty(
                    (String)_messageProperty.getKey(),
                    ((Float)_value).floatValue());
            } else if (_value instanceof Integer) {
                message.setIntProperty(
                    (String)_messageProperty.getKey(),
                    ((Integer)_value).intValue());
            } else if (_value instanceof Long) {
                message.setLongProperty(
                    (String)_messageProperty.getKey(),
                    ((Long)_value).longValue());
            } else if (_value instanceof Short) {
                message.setShortProperty(
                    (String)_messageProperty.getKey(),
                    ((Short)_value).shortValue());
            } else if (_value instanceof String) {
                message.setStringProperty(
                    (String)_messageProperty.getKey(),
                    (String)_value);
            } else {
                message.setObjectProperty(
                    (String)_messageProperty.getKey(),
                    _value);
            }
        }
    }

    private static Message createMessage(
        final Serializable object, final Properties baseMessageProperties,
        final Properties messageProperties) {

        Message _message = new ObjectMessage(object);
        addMessageProperties(baseMessageProperties, _message);
        addMessageProperties(messageProperties, _message);
        return _message;
    }

    private static Message createMessage(
        final String text, final Properties baseMessageProperties,
        final Properties messageProperties) {

        Message _message = new TextMessage(text);
        addMessageProperties(baseMessageProperties, _message);
        addMessageProperties(messageProperties, _message);
        return _message;
    }

    private static Properties getMessageProperties(
        final Properties messageProperties, final String messageType) {

        Properties _messageProperties;
        if (messageProperties != null) {
            _messageProperties = messageProperties;
        } else {
            _messageProperties = new Properties();
        }
        _messageProperties.setProperty(Message.MESSAGE_TYPE, messageType);
        return _messageProperties;
    }

    private void publish(final Message message, final String topicName) {
        String _messagePipelineId =
            topicName + "/" + message.getStringProperty(Message.MESSAGE_TYPE);
        MessagePipeline _messagePipeline;
        if (messagePipelineMap.containsKey(_messagePipelineId)) {
            _messagePipeline =
                (MessagePipeline)messagePipelineMap.get(_messagePipelineId);
        } else {
            _messagePipeline = new MessagePipeline(this, topicName);
            messagePipelineMap.put(_messagePipelineId,  _messagePipeline);
        }
        _messagePipeline.enqueue(message);
    }

    private void publishNow(final Message message, final String topicName)
    throws MessageServiceException {
        messageServiceAdapter.publish(message, topicName);
    }

    private void setBaseMessageProperties() {
        try {
            baseMessageProperties.setProperty(
                Message.SOURCE_NODE_ADDRESS,
                InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException exception) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Failed to get IP address for localhost.", exception);
            }
        }
    }
    
    private void setBaseMessageProperties(final ServletContext servletContext) {
        if (servletContext != null) {
            String _servletContextPath =
                ServerUtility.getServletContextPath(servletContext);
            if (_servletContextPath != null) {
                baseMessageProperties.setProperty(
                    Message.SOURCE_SERVLET_CONTEXT_PATH, _servletContextPath);
            }
        }
        setBaseMessageProperties();
    }

    private void setBaseMessageProperties(final String servletContextPath) {
        if (servletContextPath != null) {
            baseMessageProperties.setProperty(
                Message.SOURCE_SERVLET_CONTEXT_PATH, servletContextPath);
        }
        setBaseMessageProperties();
    }
    
    public static interface Administrator {
        void reconnect();
        
        boolean reconnectNow();
    }
}
