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
import com.icesoft.net.messaging.ObjectMessage;
import com.icesoft.net.messaging.TextMessage;

import javax.jms.DeliveryMode;
import javax.jms.InvalidDestinationException;
import javax.jms.JMSException;
import javax.jms.MessageFormatException;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicPublisher;

import java.util.Enumeration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JMSPublisherConnection
extends AbstractJMSConnection
implements JMSConnection {
    private static final Log LOG =
        LogFactory.getLog(JMSPublisherConnection.class);

    private TopicPublisher topicPublisher;

    public JMSPublisherConnection(
        final Topic topic, final JMSAdapter jmsAdapter)
    throws IllegalArgumentException {
        super(topic, jmsAdapter, Session.AUTO_ACKNOWLEDGE);
    }

    public void close()
    throws JMSException {
        if (connected) {
            synchronized (connectionLock) {
                if (connected) {
                    JMSException _jmsException = null;
                    if (topicPublisher != null) {
                        try {
                            // throws JMSException.
                            topicPublisher.close();
                        } catch (JMSException exception) {
                            _jmsException = exception;
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
                    topicPublisher = null;
                    if (_jmsException != null) {
                        throw _jmsException;
                    }
                }
            }
        }
    }

    public void publish(final Message message)
    throws
        InvalidDestinationException, JMSException, MessageFormatException,
        UnsupportedOperationException {

        if (message != null) {
            if (connected) {
                synchronized (connectionLock) {
                    if (connected) {
                        if (topicPublisher == null) {
                            // throws InvalidDestinationException, JMSException.
                            topicPublisher =
                                topicSession.createPublisher(topic);
                            // throws JMSException.
                            topicPublisher.setDeliveryMode(
                                DeliveryMode.NON_PERSISTENT);
                        }
                        javax.jms.Message _message = createMessage(message);
                        // throws
                        //     InvalidDestinationException, JMSException,
                        //     MessageFormatException,
                        //     UnsupportedOperationException
                        topicPublisher.publish(_message);
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(
                                "[" +
                                    jmsAdapter.getMessageServiceClient().getName() +
                                "] Outgoing message:\r\n\r\n" +
                                    toString(_message));
                        }
                    }
                }
            }
        }
    }

    private javax.jms.Message createMessage(final Message message)
    throws JMSException {
        if (message instanceof ObjectMessage) {
            // throws JMSException.
            return createObjectMessage((ObjectMessage)message);
        } else if (message instanceof TextMessage) {
            // throws JMSException.
            return createTextMessage((TextMessage)message);
        }
        return null;
    }

    private javax.jms.ObjectMessage createObjectMessage(
        final ObjectMessage objectMessage)
    throws JMSException {
        // throws JMSException.
        javax.jms.ObjectMessage _objectMessage =
            topicSession.createObjectMessage(objectMessage.getObject());
        Enumeration _propertyNames = objectMessage.getPropertyNames();
        while (_propertyNames.hasMoreElements()) {
            String _propertyName = (String)_propertyNames.nextElement();
            // throws
            //     IllegalArgumentException, JMSException,
            //     MessageFormatException, MessageNotWriteableException.
            _objectMessage.setObjectProperty(
                _propertyName, objectMessage.getObjectProperty(_propertyName));
        }
        return _objectMessage;
    }

    private javax.jms.TextMessage createTextMessage(
        final TextMessage textMessage)
    throws JMSException {
        // throws JMSException.
        javax.jms.TextMessage _textMessage =
            topicSession.createTextMessage(textMessage.getText());
        Enumeration _propertyNames = textMessage.getPropertyNames();
        while (_propertyNames.hasMoreElements()) {
            String _propertyName = (String)_propertyNames.nextElement();
            // throws
            //     IllegalArgumentException, JMSException,
            //     MessageFormatException, MessageNotWriteableException.
            _textMessage.setObjectProperty(
                _propertyName, textMessage.getObjectProperty(_propertyName));
        }
        return _textMessage;
    }
}
