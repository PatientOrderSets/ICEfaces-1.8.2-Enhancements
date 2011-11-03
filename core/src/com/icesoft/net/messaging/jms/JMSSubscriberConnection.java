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
package com.icesoft.net.messaging.jms;

import com.icesoft.net.messaging.Message;
import com.icesoft.net.messaging.MessageHandler;
import com.icesoft.net.messaging.MessageSelector;
import com.icesoft.net.messaging.ObjectMessage;
import com.icesoft.net.messaging.TextMessage;
import com.icesoft.net.messaging.expression.Container;
import com.icesoft.net.messaging.expression.Expression;
import com.icesoft.net.messaging.expression.Or;

import edu.emory.mathcs.backport.java.util.concurrent.RejectedExecutionException;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.jms.InvalidDestinationException;
import javax.jms.InvalidSelectorException;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JMSSubscriberConnection
extends AbstractJMSConnection
implements JMSConnection {
    private static final Log LOG =
        LogFactory.getLog(JMSSubscriberConnection.class);

    private final Map subscriberMap = new HashMap();

    private TopicSubscriber topicSubscriber;
    private MessageReceiver messageReceiver;

    private boolean started = false;

    public JMSSubscriberConnection(
        final Topic topic, final JMSAdapter jmsAdapter)
    throws IllegalArgumentException {
        super(topic, jmsAdapter, Session.CLIENT_ACKNOWLEDGE);
    }

    public void addMessageHandler(final MessageHandler messageHandler) {
        if (messageHandler != null) {
            if (connected && messageReceiver != null) {
                synchronized (connectionLock) {
                    if (connected && messageReceiver != null) {
                        messageReceiver.getMessageListener().
                            addMessageHandler(messageHandler);
                    }
                }
            }
        }
    }

    public void close()
    throws JMSException {
        if (connected) {
            synchronized (connectionLock) {
                if (connected) {
                    JMSException _jmsException = null;
                    if (started) {
                        try {
                            // throws JMSException.
                            stop();
                        } catch (JMSException exception) {
                            _jmsException = exception;
                        }
                    }
                    if (messageReceiver != null) {
                        messageReceiver.requestStop();
                        messageReceiver.getMessageListener().
                            clearMessageHandlers();
                    }
                    if (topicSubscriber != null) {
                        try {
                            // throws JMSException.
                            topicSubscriber.close();
                        } catch (JMSException exception) {
                            if (_jmsException == null) {
                                _jmsException = exception;
                            }
                        }
                    }
                    try {
                        // throws JMSException.
                        super.close();
                    } catch (JMSException exception) {
                        if (_jmsException == null) {
                            _jmsException = exception;
                        }
                    }
                    messageReceiver = null;
                    topicSubscriber = null;
                    if (_jmsException != null) {
                        throw _jmsException;
                    }
                }
            }
        }
    }

    public void removeMessageHandler(final MessageHandler messageHandler) {
        if (messageHandler != null) {
            if (connected && messageReceiver != null) {
                synchronized (connectionLock) {
                    if (connected && messageReceiver != null) {
                        messageReceiver.getMessageListener().
                            removeMessageHandler(messageHandler);
                    }
                }
            }
        }
    }

    public void start()
    throws JMSException {
        if (connected && !started) {
            synchronized (connectionLock) {
                if (connected && !started) {
                    topicConnection.start();
                    started = true;
                }
            }
        }
    }

    public void stop()
    throws JMSException {
        /*
         * Use of the stop() method on javax.jms.Connection
         * objects by applications in web and EJB containers may
         * interfere with the connection management functions of
         * the container and must not be used.
         */
        // do nothing.
        if (started) {
            synchronized (connectionLock) {
                if (started) {
//                    try {
//                        /*
//                         * IBM Websphere throws an IllegalArgumentException when
//                         * this stop() method is invoked.
//                         */
//                        // throws JMSException.
//                        topicConnection.stop();
//                    } catch (IllegalStateException exception) {
//                        // IBM Websphere: eat the exception.
////                        throw exception;
//                    } finally {
                        started = false;
//                    }
                }
            }
        }
    }

    public void subscribe(
        final MessageSelector messageSelector, final boolean noLocal)
    throws InvalidDestinationException, InvalidSelectorException, JMSException {
        if (connected) {
            synchronized (connectionLock) {
                if (connected) {
                    Container _container =
                        new Container(messageSelector.getExpression());
                    if (subscriberMap.containsKey(topic)) {
                        subscriberMap.put(
                            topic,
                            new Or(
                                (Expression)subscriberMap.get(topic),
                                _container));
                    } else {
                        subscriberMap.put(topic, _container);
                    }
                    if (topicSubscriber == null) {
                        // throws
                        //     InvalidDestinationException,
                        //     InvalidSelectorException, JMSException
                        topicSubscriber =
                            topicSession.createSubscriber(topic, null, noLocal);
                        messageReceiver =
                            new MessageReceiver(
                                new MessageListener(this), topicSubscriber);
                        try {
                            // throws RejectedExecutionException
                            jmsAdapter.getExecutorService().
                                execute(messageReceiver);
                        } catch (RejectedExecutionException exception) {
                            if (LOG.isFatalEnabled()) {
                                LOG.fatal(
                                    "messageReceiver could not be accepted " +
                                        "for execution!",
                                    exception);
                            }
                        }
                    }
                }
            }
        }
    }

    public void unsubscribe()
    throws JMSException {
        if (connected) {
            synchronized (connectionLock) {
                if (connected) {
                    if (messageReceiver != null) {
                        messageReceiver.requestStop();
                        messageReceiver.getMessageListener().
                            clearMessageHandlers();
                    }
                    if (subscriberMap.containsKey(topic)) {
                        subscriberMap.remove(topic);
                    }
                    try {
                        if (topicSubscriber != null) {
                            // throws JMSException.
                            topicSubscriber.close();
                        }
                    } finally {
                        topicSubscriber = null;
                    }
                }
            }
        }
    }

    private boolean accept(final Message message) {
        return
            subscriberMap.containsKey(topic) &&
            ((Expression)subscriberMap.get(topic)).evaluate(message);
    }

    private static Message convert(final javax.jms.Message message)
    throws JMSException {
        if (message instanceof javax.jms.TextMessage) {
            return convert((javax.jms.TextMessage)message);
        } else if (message instanceof javax.jms.ObjectMessage) {
            return convert((javax.jms.ObjectMessage)message);
        } else {
            return null;
        }
    }

    private static ObjectMessage convert(
        final javax.jms.ObjectMessage objectMessage)
    throws JMSException {
        ObjectMessage _objectMessage =
            new ObjectMessage(objectMessage.getObject());
        copyProperties(objectMessage, _objectMessage);
        return _objectMessage;
    }

    private static TextMessage convert(final javax.jms.TextMessage textMessage)
    throws JMSException {
        TextMessage _textMessage = new TextMessage(textMessage.getText());
        copyProperties(textMessage, _textMessage);
        return _textMessage;
    }

    private static void copyProperties(
        final javax.jms.Message source, final Message destination)
    throws JMSException {
        Enumeration _propertyNames = source.getPropertyNames();
        while (_propertyNames.hasMoreElements()) {
            String _propertyName = (String)_propertyNames.nextElement();
            destination.setObjectProperty(
                _propertyName, source.getObjectProperty(_propertyName));
        }
    }

    private class MessageListener
    implements javax.jms.MessageListener {
        private final Set messageHandlerSet = new HashSet();
        private final JMSSubscriberConnection jmsSubscriberConnection;

        private MessageListener(
            final JMSSubscriberConnection jmsSubscriberConnection) {

            this.jmsSubscriberConnection = jmsSubscriberConnection;
        }

        public void onMessage(final javax.jms.Message message) {
            try {
                Message _message = convert(message);
                if (LOG.isDebugEnabled()) {
                    LOG.debug(
                        "[" +
                            jmsAdapter.getMessageServiceClient().getName() +
                        "] Incoming message:\r\n\r\n" + _message);
                }
                if (jmsSubscriberConnection.accept(_message)) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(
                            "[" +
                                jmsAdapter.getMessageServiceClient().getName() +
                            "] Accepted message:\r\n\r\n" + _message);
                    }
                    MessageHandler[] _messageHandlers =
                        (MessageHandler[])
                            messageHandlerSet.toArray(
                                new MessageHandler[
                                    messageHandlerSet.size()]);
                    for (int i = 0; i < _messageHandlers.length; i++) {
                        MessageSelector _messageSelector =
                            _messageHandlers[i].getMessageSelector();
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(
                                "MessageHandler " + _messageHandlers[i] + ":\r\n" +
                                _messageSelector);
                        }
                        if (_messageSelector == null ||
                            _messageSelector.matches(_message)) {

                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Match!");
                            }
                            _messageHandlers[i].handle(_message);
                        }
                    }
                }
                message.acknowledge();
            } catch (JMSException exception) {
                if (LOG.isErrorEnabled()) {
                    LOG.error(
                        "Failed to convert message due to some internal error!",
                        exception);
                }
            }
        }

        private void addMessageHandler(final MessageHandler messageHandler) {
            if (messageHandler != null &&
                !messageHandlerSet.contains(messageHandler)) {

                messageHandlerSet.add(messageHandler);
            }
        }

        private void clearMessageHandlers() {
            messageHandlerSet.clear();
        }

        private void removeMessageHandler(final MessageHandler messageHandler) {
            if (messageHandler != null &&
                messageHandlerSet.contains(messageHandler)) {

                messageHandlerSet.remove(messageHandler);
            }
        }
    }

    private class MessageReceiver
    implements Runnable {
        private MessageListener messageListener;
        private TopicSubscriber topicSubscriber;
        private boolean stopRequested;

        private MessageReceiver(
            final MessageListener messageListener,
            final TopicSubscriber topicSubscriber) {

            this.messageListener = messageListener;
            this.topicSubscriber = topicSubscriber;
        }

        public void run() {
            if (LOG.isDebugEnabled()) {
                LOG.debug(this + " started.");
            }
            try {
                while (!stopRequested) {
                    javax.jms.Message _message = topicSubscriber.receive(1000);
                    if (_message != null) {
                        messageListener.onMessage(_message);
                    }
                }
            } catch (JMSException exception) {
                // reconnect will create a new instance of
                // JMSSubscriberConnection.
                jmsAdapter.
                    getMessageServiceClient().getAdministrator().reconnectNow();
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(this + " stopped.");
            }
        }

        public String toString() {
            StringBuffer _string = new StringBuffer();
            _string.append("MessageReceiver [");
            try {
                _string.append(topicSubscriber.getMessageSelector());
            } catch (JMSException exception) {
                // do nothing.
            }
            _string.append("]");
            return _string.toString();
        }

        private void requestStop() {
            stopRequested = true;
        }

        private MessageListener getMessageListener() {
            return messageListener;
        }

        private boolean isStopRequested() {
            return stopRequested;
        }
    }
}
