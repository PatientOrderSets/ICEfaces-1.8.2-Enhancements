package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.net.messaging.AbstractMessageHandler;
import com.icesoft.net.messaging.Message;
import com.icesoft.net.messaging.MessageHandler;
import com.icesoft.net.messaging.MessageSelector;
import com.icesoft.net.messaging.TextMessage;
import com.icesoft.net.messaging.expression.Equal;
import com.icesoft.net.messaging.expression.Identifier;
import com.icesoft.net.messaging.expression.StringLiteral;

import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DisposeViewsMessageHandler
extends AbstractMessageHandler
implements MessageHandler {
    protected static final String MESSAGE_TYPE = "DisposeViews";

    private static final Log LOG = LogFactory.getLog(DisposeViewsMessageHandler.class);

    private static MessageSelector messageSelector =
        new MessageSelector(
            new Equal(
                new Identifier(Message.MESSAGE_TYPE),
                new StringLiteral(MESSAGE_TYPE)));

    protected DisposeViewsMessageHandler() {
        super(messageSelector);
    }

    public void handle(final Message message) {
        if (message == null) {
            return;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Handling:\r\n\r\n" + message);
        }
        if (message instanceof TextMessage) {
            StringTokenizer _iceFacesIdViewNumberPairs =
                new StringTokenizer(((TextMessage)message).getText());
            while (_iceFacesIdViewNumberPairs.hasMoreTokens()) {
                StringTokenizer _iceFacesIdViewNumberPair =
                    new StringTokenizer(
                        _iceFacesIdViewNumberPairs.nextToken(), ";");
                String _iceFacesId = _iceFacesIdViewNumberPair.nextToken();
                String _viewNumber = _iceFacesIdViewNumberPair.nextToken();
                MessageHandler.Callback[] _callbacks = getCallbacks(message);
                for (int i = 0; i < _callbacks.length; i++) {
                    ((Callback)_callbacks[i]).disposeView(_iceFacesId, _viewNumber);
                }
            }
        }
    }

    public String toString() {
        return getClass().getName();
    }

    public static interface Callback
    extends MessageHandler.Callback {
        public void disposeView(
            final String iceFacesId, final String viewNumber);
    }
}
