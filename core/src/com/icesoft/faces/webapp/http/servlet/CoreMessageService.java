package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.application.ProductInfo;
import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.faces.util.event.servlet.AnnouncementMessageHandler;
import com.icesoft.net.messaging.AbstractMessageHandler;
import com.icesoft.net.messaging.DefaultMessageService;
import com.icesoft.net.messaging.Message;
import com.icesoft.net.messaging.MessageHandler;
import com.icesoft.net.messaging.MessageSelector;
import com.icesoft.net.messaging.MessageServiceClient;
import com.icesoft.net.messaging.MessageServiceException;
import com.icesoft.net.messaging.TextMessage;
import com.icesoft.net.messaging.expression.Equal;
import com.icesoft.net.messaging.expression.Identifier;
import com.icesoft.net.messaging.expression.Or;
import com.icesoft.net.messaging.expression.StringLiteral;
import com.icesoft.util.Properties;

import edu.emory.mathcs.backport.java.util.concurrent.ScheduledThreadPoolExecutor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CoreMessageService
extends DefaultMessageService {
    private static final Log LOG = LogFactory.getLog(CoreMessageService.class);

    private final AcknowledgeMessageHandler acknowledgeMessageHandler = new AcknowledgeMessageHandler(this);
    private final AnnouncementMessageHandler announcementMessageHandler = new AnnouncementMessageHandler();
    private final DisposeViewsMessageHandler disposeViewsMessageHandler = new DisposeViewsMessageHandler();

    private final String blockingRequestHandlerContext;

    public CoreMessageService(
        final MessageServiceClient messageServiceClient, final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor,
        final String blockingRequestHandlerContext)
    throws IllegalArgumentException {
        // throws IllegalArgumentException
        super(messageServiceClient, scheduledThreadPoolExecutor);
        this.blockingRequestHandlerContext = blockingRequestHandlerContext;
    }

    public CoreMessageService(
        final MessageServiceClient messageServiceClient, final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor,
        final boolean retryOnFail, final String blockingRequestHandlerContext)
    throws IllegalArgumentException {
        // throws IllegalArgumentException
        super(messageServiceClient, scheduledThreadPoolExecutor, retryOnFail);
        this.blockingRequestHandlerContext = blockingRequestHandlerContext;
    }

    public CoreMessageService(
        final MessageServiceClient messageServiceClient, final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor,
        final Configuration configuration, final String blockingRequestHandlerContext)
    throws IllegalArgumentException {
        // throws IllegalArgumentException
        super(messageServiceClient, scheduledThreadPoolExecutor, configuration);
        this.blockingRequestHandlerContext = blockingRequestHandlerContext;
    }

    public CoreMessageService(
        final MessageServiceClient messageServiceClient, final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor,
        final Configuration configuration, final boolean retryOnFail, final String blockingRequestHandlerContext)
    throws IllegalArgumentException {
        // throws IllegalArgumentException
        super(messageServiceClient, scheduledThreadPoolExecutor, configuration, retryOnFail);
        this.blockingRequestHandlerContext = blockingRequestHandlerContext;
    }

    public AcknowledgeMessageHandler getAcknowledgeMessageHandler() {
        return acknowledgeMessageHandler;
    }

    public AnnouncementMessageHandler getAnnouncementMessageHandler() {
        return announcementMessageHandler;
    }

    public DisposeViewsMessageHandler getDisposeViewsMessageHandler() {
        return disposeViewsMessageHandler;
    }

    protected void setUpMessageServiceClient()
    throws MessageServiceException {
        LOG.debug("Setting up Message Service Client...");
        try {
            // throws MessageServiceException
            getMessageServiceClient().
                subscribe(MessageServiceClient.PUSH_TOPIC_NAME, acknowledgeMessageHandler.getMessageSelector());
            getMessageServiceClient().
                addMessageHandler(acknowledgeMessageHandler, MessageServiceClient.PUSH_TOPIC_NAME);
            // throws MessageServiceException
            getMessageServiceClient().start();
            Properties _messageProperties = new Properties();
            _messageProperties.setStringProperty(
                Message.DESTINATION_SERVLET_CONTEXT_PATH, blockingRequestHandlerContext);
            // throws MessageServiceException
            getMessageServiceClient().
                publishNow("Hello", _messageProperties, "Presence", MessageServiceClient.PUSH_TOPIC_NAME);
        } catch (MessageServiceException exception) {
            try {
                // throws MessageServiceException
                getMessageServiceClient().stop();
            } catch (MessageServiceException e) {
                // do nothing...
            }
            getMessageServiceClient().
                removeMessageHandler(acknowledgeMessageHandler, MessageServiceClient.PUSH_TOPIC_NAME);
            try {
                // throws MessageServiceException
                getMessageServiceClient().unsubscribe(MessageServiceClient.PUSH_TOPIC_NAME);
            } catch (MessageServiceException e) {
                // do nothing...
            }
            throw exception;
        }
        // throws MessageServiceException
        getMessageServiceClient().
            subscribe(
                MessageServiceClient.PUSH_TOPIC_NAME,
                new MessageSelector(
                    new Or(
                        announcementMessageHandler.getMessageSelector().getExpression(),
                        disposeViewsMessageHandler.getMessageSelector().getExpression())));
        getMessageServiceClient().
            addMessageHandler(announcementMessageHandler, MessageServiceClient.PUSH_TOPIC_NAME);
        getMessageServiceClient().
            addMessageHandler(disposeViewsMessageHandler, MessageServiceClient.PUSH_TOPIC_NAME);
    }

    protected void tearDownMessageServiceClient()
    throws MessageServiceException {
        LOG.debug("Tearing down Message Service Client...");
        getMessageServiceClient().
            removeMessageHandler(disposeViewsMessageHandler, MessageServiceClient.PUSH_TOPIC_NAME);
        getMessageServiceClient().
            removeMessageHandler(announcementMessageHandler, MessageServiceClient.PUSH_TOPIC_NAME);
        // throws MessageServiceException
        getMessageServiceClient().unsubscribe(MessageServiceClient.PUSH_TOPIC_NAME);
    }

    public static class AcknowledgeMessageHandler
    extends AbstractMessageHandler
    implements MessageHandler {
        private static MessageSelector messageSelector =
            new MessageSelector(new Equal(new Identifier(Message.MESSAGE_TYPE), new StringLiteral("Presence")));

        private final CoreMessageService coreMessageService;

        private AcknowledgeMessageHandler(final CoreMessageService coreMessageService) {
            super(messageSelector);
            this.coreMessageService = coreMessageService;
        }

        public void handle(final Message message) {
            if (message instanceof TextMessage) {
                String _text = ((TextMessage)message).getText();
                int _begin = 0;
                int _end = _text.indexOf(";");
                if (_end != -1 && _text.substring(_begin, _end).equals("Acknowledge")) {
                    String _product = _text.substring(_begin = _end + 1, _end = _text.indexOf(";", _begin));
                    String _primary = _text.substring(_begin = _end + 1, _end = _text.indexOf(";", _begin));
                    String _secondary = _text.substring(_begin = _end + 1, _end = _text.indexOf(";", _begin));
                    String _tertiary = _text.substring(_begin = _end + 1, _end = _text.indexOf(";", _begin));
                    String _releaseType = _text.substring(_begin = _end + 1, _end = _text.indexOf(";", _begin));
                    if (LOG.isInfoEnabled()) {
                        LOG.info(
                            "Push Server detected: \"" +
                                _product + " " +
                                _primary + "." + _secondary + "." + _tertiary +
                                    (_releaseType.length() == 0 ? _releaseType : " " + _releaseType) + "\"");
                    }
                    if (LOG.isWarnEnabled()) {
                        if (!_primary.equals("x") && !ProductInfo.PRIMARY.equals("x")) {
                            if (!_primary.equals(ProductInfo.PRIMARY) ||
                                !_secondary.equals(ProductInfo.SECONDARY) ||
                                !_tertiary.equals(ProductInfo.TERTIARY)) {

                                LOG.warn(
                                    "ICEfaces / Push Server version mismatch! - " +
                                        "Using \"" +
                                            ProductInfo.PRODUCT + " " +
                                            ProductInfo.PRIMARY + "." +
                                            ProductInfo.SECONDARY + "." +
                                            ProductInfo.TERTIARY + "\" " +
                                        "with \"" +
                                            _product + " " +
                                            _primary + "." +
                                            _secondary + "." +
                                            _tertiary + "\" " +
                                        "is not recommended.");
                            }
                        }
                    }
                    coreMessageService.getMessageServiceClient().
                        removeMessageHandler(this, MessageServiceClient.PUSH_TOPIC_NAME);
                    if (LOG.isInfoEnabled()) {
                        LOG.info("Using Push Server Blocking Request Handler");
                    }
                    MessageHandler.Callback[] _callbacks = getCallbacks(message);
                    for (int i = 0; i < _callbacks.length; i++) {
                        ((Callback)_callbacks[i]).acknowledge(_product, _primary, _secondary, _tertiary, _releaseType);
                    }
                }
            }
        }

        public static interface Callback
        extends MessageHandler.Callback {
            void acknowledge(String product, String primary, String secondary, String tertiary, String releaseType);
        }
    }
}
