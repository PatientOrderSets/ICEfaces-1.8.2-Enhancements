package org.icefaces.push.server;

import com.icesoft.faces.webapp.http.common.Configuration;
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

public class PushServerMessageService
extends DefaultMessageService {
    private static final Log LOG = LogFactory.getLog(PushServerMessageService.class);

    private final MessageHandler bufferedContextEventsMessageHandler = new BufferedContextEventsMessageHandler();
    private final MessageHandler contextEventMessageHandler = new ContextEventMessageHandler();
    private final MessageHandler helloMessageHandler =
        new AbstractMessageHandler(
            new MessageSelector(new Equal(new Identifier(Message.MESSAGE_TYPE), new StringLiteral("Presence")))) {

            public void handle(final Message message) {
                if (message instanceof TextMessage) {
                    if (((TextMessage)message).getText().equals("Hello")) {
                        Properties _messageProperties = new Properties();
                        _messageProperties.setStringProperty(
                            Message.DESTINATION_SERVLET_CONTEXT_PATH,
                            message.getStringProperty(Message.SOURCE_SERVLET_CONTEXT_PATH));
                        getMessageServiceClient().publish(
                            new StringBuffer().
                                append("Acknowledge").append(";").
                                append(ProductInfo.PRODUCT).append(";").
                                append(ProductInfo.PRIMARY).append(";").
                                append(ProductInfo.SECONDARY).append(";").
                                append(ProductInfo.TERTIARY).append(";").
                                append(ProductInfo.RELEASE_TYPE).append(";").
                                append(ProductInfo.BUILD_NO).append(";").
                                append(ProductInfo.REVISION).
                                    toString(),
                            _messageProperties,
                            "Presence",
                            MessageServiceClient.PUSH_TOPIC_NAME);
                    }
                }
            }
        };
    private final MessageHandler updatedViewsMessageHandler = new UpdatedViewsMessageHandler();

    public PushServerMessageService(
        final MessageServiceClient messageServiceClient, final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor)
    throws IllegalArgumentException {
        // throws IllegalArgumentException
        super(messageServiceClient, scheduledThreadPoolExecutor);
    }

    public PushServerMessageService(
        final MessageServiceClient messageServiceClient, final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor,
        final boolean retryOnFail)
    throws IllegalArgumentException {
        // throws IllegalArgumentException
        super(messageServiceClient, scheduledThreadPoolExecutor, retryOnFail);
    }

    public PushServerMessageService(
        final MessageServiceClient messageServiceClient, final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor,
        final Configuration configuration)
    throws IllegalArgumentException {
        // throws IllegalArgumentException
        super(messageServiceClient, scheduledThreadPoolExecutor, configuration);
    }

    public PushServerMessageService(
        final MessageServiceClient messageServiceClient, final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor,
        final Configuration configuration, final boolean retryOnFail)
    throws IllegalArgumentException {
        // throws IllegalArgumentException
        super(messageServiceClient, scheduledThreadPoolExecutor, configuration, retryOnFail);
    }

    public MessageHandler getBufferedContextEventsMessageHandler() {
        return bufferedContextEventsMessageHandler;
    }

    public MessageHandler getContextEventMessageHandler() {
        return contextEventMessageHandler;
    }

    public MessageHandler getUpdatedViewsMessageHandler() {
        return updatedViewsMessageHandler;
    }

    protected void setUpMessageServiceClient()
    throws MessageServiceException {
        // throws MessageServiceException
        getMessageServiceClient().subscribe(
            MessageServiceClient.PUSH_TOPIC_NAME,
            new MessageSelector(
                new Or(
                    helloMessageHandler.getMessageSelector().getExpression(),
                    new Or(
                        bufferedContextEventsMessageHandler.getMessageSelector().getExpression(),
                        new Or(
                            contextEventMessageHandler.getMessageSelector().getExpression(),
                            updatedViewsMessageHandler.getMessageSelector().getExpression())))));
        getMessageServiceClient().
            addMessageHandler(helloMessageHandler, MessageServiceClient.PUSH_TOPIC_NAME);
        getMessageServiceClient().
            addMessageHandler(bufferedContextEventsMessageHandler, MessageServiceClient.PUSH_TOPIC_NAME);
        getMessageServiceClient().
            addMessageHandler(contextEventMessageHandler, MessageServiceClient.PUSH_TOPIC_NAME);
        getMessageServiceClient().
            addMessageHandler(updatedViewsMessageHandler, MessageServiceClient.PUSH_TOPIC_NAME);
    }

    protected void tearDownMessageServiceClient()
    throws MessageServiceException {
        getMessageServiceClient().
            removeMessageHandler(updatedViewsMessageHandler, MessageServiceClient.PUSH_TOPIC_NAME);
        getMessageServiceClient().
            removeMessageHandler(contextEventMessageHandler, MessageServiceClient.PUSH_TOPIC_NAME);
        getMessageServiceClient().
            removeMessageHandler(bufferedContextEventsMessageHandler, MessageServiceClient.PUSH_TOPIC_NAME);
        getMessageServiceClient().
            removeMessageHandler(helloMessageHandler, MessageServiceClient.PUSH_TOPIC_NAME);
        // throws MessageServiceException
        getMessageServiceClient().unsubscribe(MessageServiceClient.PUSH_TOPIC_NAME);
    }
}
