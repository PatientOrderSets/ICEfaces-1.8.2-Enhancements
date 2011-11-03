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

import java.util.Enumeration;

import javax.jms.JMSException;
import javax.jms.JMSSecurityException;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractJMSConnection
implements JMSConnection {
    private static final Log LOG =
        LogFactory.getLog(AbstractJMSConnection.class);

    protected final Object connectionLock = new Object();

    protected JMSAdapter jmsAdapter;

    protected Topic topic;
    protected TopicConnection topicConnection;
    protected TopicSession topicSession;
    protected int acknowledgeMode;

    protected boolean connected = false;

    protected AbstractJMSConnection(
        final Topic topic, final JMSAdapter jmsAdapter,
        final int acknowledgeMode)
    throws IllegalArgumentException {
        if (topic == null) {
            throw new IllegalArgumentException("topic is null");
        }
        if (jmsAdapter == null) {
            throw
                new IllegalArgumentException("jmsAdapter is null");
        }
        this.topic = topic;
        this.jmsAdapter = jmsAdapter;
        this.acknowledgeMode = acknowledgeMode;
    }

    public void close()
    throws JMSException {
        if (connected) {
            synchronized (connectionLock) {
                if (connected) {
                    JMSException _jmsException = null;
                    try {
                        if (topicSession != null) {
                            // throws JMSException.
                            topicSession.close();
                        }
                    } catch (JMSException exception) {
                        _jmsException = exception;
                    }
                    try {
                        if (topicConnection != null) {
                            // throws JMSException.
                            topicConnection.close();
                        }
                    } catch (JMSException exception) {
                        if (_jmsException == null) {
                            _jmsException = exception;
                        }
                    }
                    topicSession = null;
                    topicConnection = null;
                    connected = false;
                    if (_jmsException != null) {
                        throw _jmsException;
                    }
                }
            }
        }
    }

    public void open()
    throws JMSException, JMSSecurityException {
        if (!connected) {
            synchronized (connectionLock) {
                if (!connected) {
                    topicConnection =
                        jmsAdapter.getTopicConnectionFactory().
                            createTopicConnection();
                    topicSession =
                        topicConnection.createTopicSession(
                            false, acknowledgeMode);
                    connected = true;
                }
            }
        }
    }

    protected static String toString(final javax.jms.Message message) {
        StringBuffer _messageString = new StringBuffer();
        try {
            Enumeration _propertyNames = message.getPropertyNames();
            while (_propertyNames.hasMoreElements()) {
                String _propertyName = (String)_propertyNames.nextElement();
                _messageString.append(_propertyName);
                _messageString.append(": ");
                _messageString.append(message.getObjectProperty(_propertyName));
                _messageString.append("\r\n");
            }
            _messageString.append("\r\n");
            if (message instanceof javax.jms.ObjectMessage) {
                _messageString.append(
                    ((javax.jms.ObjectMessage)message).getObject());
            } else if (message instanceof javax.jms.TextMessage) {
                _messageString.append(
                    ((javax.jms.TextMessage)message).getText());
            }
            _messageString.append("\r\n");
        } catch (JMSException exception) {
            // do nothing (this is just a toString() method)
        }
        return _messageString.toString();
    }
}
