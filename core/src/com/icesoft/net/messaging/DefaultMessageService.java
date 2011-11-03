package com.icesoft.net.messaging;

import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.util.Properties;

import edu.emory.mathcs.backport.java.util.concurrent.ScheduledFuture;
import edu.emory.mathcs.backport.java.util.concurrent.ScheduledThreadPoolExecutor;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DefaultMessageService
implements MessageServiceClient.Administrator {
    private static final Log LOG = LogFactory.getLog(DefaultMessageService.class);

    private static final int STATE_UNINITIALIZED = 0;
    private static final int STATE_SET_UP = 1;
    private static final int STATE_SET_UP_DONE = 2;
    private static final int STATE_STARTED = 3;
    private static final int STATE_STOPPED = 4;
    private static final int STATE_TEAR_DOWN = 5;
    private static final int STATE_TEAR_DOWN_DONE = 6;
    private static final int STATE_CLOSED = 7;

    private static final int DEFAULT_INTERVAL = 10000;
    private static final int DEFAULT_MAX_RETRIES = 30;
    private static final int DEFAULT_INTERVAL_ON_RECONNECT = 5000;
    private static final int DEFAULT_MAX_RETRIES_ON_RECONNECT = 60;

    private final Object reconnectLock = new Object();
    private final Object stateLock = new Object();

    private final Configuration configuration;
    private final MessageServiceClient messageServiceClient;
    private final boolean retryOnFail;
    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    private MessagePublisher currentMessagePublisher;
    private long successTimestamp;

    private int currentState = STATE_UNINITIALIZED;
    private int requestedState = STATE_UNINITIALIZED;

    public DefaultMessageService(
        final MessageServiceClient messageServiceClient, final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor)
    throws IllegalArgumentException {
        // throws IllegalArgumentException 
        this(messageServiceClient, scheduledThreadPoolExecutor, null, false);
    }

    public DefaultMessageService(
        final MessageServiceClient messageServiceClient, final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor,
        final boolean retryOnFail)
    throws IllegalArgumentException {
        // throws IllegalArgumentException
        this(messageServiceClient, scheduledThreadPoolExecutor, null, retryOnFail);
    }

    public DefaultMessageService(
        final MessageServiceClient messageServiceClient, final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor,
        final Configuration configuration)
    throws IllegalArgumentException {
        // throws IllegalArgumentException
        this(messageServiceClient, scheduledThreadPoolExecutor, configuration, false);
    }

    public DefaultMessageService(
        final MessageServiceClient messageServiceClient, final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor,
        final Configuration configuration, final boolean retryOnFail)
    throws IllegalArgumentException {
        if (messageServiceClient == null) {
            throw new IllegalArgumentException("messageServiceClient is null");
        }
        if (scheduledThreadPoolExecutor == null) {
            throw new IllegalArgumentException("scheduledThreadPoolExecutor is null");
        }
        this.messageServiceClient = messageServiceClient;
        this.messageServiceClient.setAdministrator(this);
        this.scheduledThreadPoolExecutor = scheduledThreadPoolExecutor;
        this.configuration = configuration;
        this.retryOnFail = retryOnFail;
        this.currentMessagePublisher =
            new QueueMessagePublisher(this, messageServiceClient, scheduledThreadPoolExecutor);
    }

    public final void close() {
        synchronized (stateLock) {
            requestedState = STATE_CLOSED;
            if (LOG.isDebugEnabled()) {
                LOG.debug("Requested State: CLOSED");
            }
            if (currentState == STATE_TEAR_DOWN_DONE) {
                try {
                    // throws MessageServiceException
                    messageServiceClient.close();
                    currentState = STATE_CLOSED;
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Current State: CLOSED");
                    }
                } catch (MessageServiceException exception) {
                    if (LOG.isErrorEnabled()) {
                        LOG.error("Failed to close the message service client.", exception);
                    }
                }
            }
        }
    }

    public MessageServiceClient getMessageServiceClient() {
        return messageServiceClient;
    }

    public final void publish(
        final Serializable object, final Properties messageProperties, final String topicName) {

        currentMessagePublisher.publish(object, messageProperties, topicName);
    }

    public final void publish(
        final Serializable object, final Properties messageProperties, final String messageType,
        final String topicName) {

        currentMessagePublisher.publish(object, messageProperties, messageType, topicName);
    }

    public final void publish(
        final Serializable object, final String topicName) {

        currentMessagePublisher.publish(object, topicName);
    }

    public final void publish(
        final Serializable object, final String messageType, final String topicName) {

        currentMessagePublisher.publish(object, messageType, topicName);
    }

    public final void publish(
        final String text, final Properties messageProperties, final String topicName) {

        currentMessagePublisher.publish(text, messageProperties, topicName);
    }

    public final void publish(
        final String text, final Properties messageProperties, final String messageType, final String topicName) {

        currentMessagePublisher.publish(text, messageProperties, messageType, topicName);
    }

    public final void publish(
        final String text, final String topicName) {

        currentMessagePublisher.publish(text, topicName);
    }

    public final void publish(
        final String text, final String messageType, final String topicName) {

        currentMessagePublisher.publish(text, messageType, topicName);
    }

    public final void publishNow(
        final Serializable object, final Properties messageProperties, final String topicName) {

        currentMessagePublisher.publishNow(object, messageProperties, topicName);
    }

    public final void publishNow(
        final Serializable object, final Properties messageProperties, final String messageType,
        final String topicName) {

        currentMessagePublisher.publishNow(object, messageProperties, messageType, topicName);
    }

    public final void publishNow(
        final Serializable object, final String topicName) {

        currentMessagePublisher.publishNow(object, topicName);
    }

    public final void publishNow(
        final Serializable object, final String messageType, final String topicName) {

        currentMessagePublisher.publishNow(object, messageType, topicName);
    }

    public final void publishNow(
        final String text, final Properties messageProperties, final String topicName) {

        currentMessagePublisher.publishNow(text, messageProperties, topicName);
    }

    public final void publishNow(
        final String text, final Properties messageProperties, final String messageType, final String topicName) {

        currentMessagePublisher.publishNow(text, messageProperties, messageType, topicName);
    }

    public final void publishNow(
        final String text, final String topicName) {

        currentMessagePublisher.publishNow(text, topicName);
    }

    public final void publishNow(
        final String text, final String messageType, final String topicName) {

        currentMessagePublisher.publishNow(text, messageType, topicName);
    }

    public final void reconnect() {
        LOG.debug("Reconnecting...");
        if (scheduledThreadPoolExecutor == null) {
            // blocking reconnect
            reconnectNow();
        } else {
            // non-blocking reconnect
            new ReconnectTask().execute();
        }
    }

    public final boolean reconnectNow() {
        LOG.debug("Reconnecting now...");
        // blocking reconnect
        return new ReconnectTask().executeNow();
    }

    public final void setUp() {
        LOG.debug("Setting up...");
        setUp(
            !retryOnFail ? 0 : configuration.getAttributeAsInteger("interval", DEFAULT_INTERVAL),
            !retryOnFail ? 0 : configuration.getAttributeAsInteger("maxRetries", DEFAULT_MAX_RETRIES));
    }

    public final boolean setUpNow() {
        LOG.debug("Setting up now...");
        return
            setUpNow(
                !retryOnFail ? 0 : configuration.getAttributeAsInteger("interval", DEFAULT_INTERVAL),
                !retryOnFail ? 0 : configuration.getAttributeAsInteger("maxRetries", DEFAULT_MAX_RETRIES));
    }

    public final void start() {
        synchronized (stateLock) {
            requestedState = STATE_STARTED;
            if (LOG.isDebugEnabled()) {
                LOG.debug("Requested State: STARTED");
            }
            if (currentState == STATE_SET_UP_DONE) {
                try {
                    // throws MessageServiceException
                    messageServiceClient.start();
                    currentState = STATE_STARTED;
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Current State: STARTED");
                    }
                } catch (MessageServiceException exception) {
                    if (LOG.isFatalEnabled()) {
                        LOG.fatal("Failed to start message delivery!", exception);
                    }
                }
            }
        }
    }

    public final void stop() {
        synchronized (stateLock) {
            requestedState = STATE_STOPPED;
            if (LOG.isDebugEnabled()) {
                LOG.debug("Requested State: STOPPED");
            }
            if (currentState == STATE_STARTED) {
                try {
                    // throws MessageServiceException
                    messageServiceClient.stop();
                    currentState = STATE_STOPPED;
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Current State: STOPPED");
                    }
                } catch (MessageServiceException exception) {
                    if (LOG.isFatalEnabled()) {
                        LOG.fatal("Failed to stop message delivery!", exception);
                    }
                }
            }
        }
    }

    public final void tearDown() {
        LOG.debug("Tearing down...");
        if (scheduledThreadPoolExecutor == null) {
            // blocking tear down
            tearDownNow();
        } else {
            // non-blocking tear down
            new TearDownTask(scheduledThreadPoolExecutor).execute();
        }
    }

    public final boolean tearDownNow() {
        LOG.debug("Tearing down now...");
        // blocking tear down
        return new TearDownTask().executeNow();
    }

    protected void setUpMessageServiceClient()
    throws MessageServiceException {

    }

    protected void tearDownMessageServiceClient()
    throws MessageServiceException {

    }

    private void setUp(final int interval, final int maxRetries) {
        LOG.debug("Setting up... (interval: [" + interval + "], maxRetries: [" + maxRetries + "])");
        if (scheduledThreadPoolExecutor == null) {
            // blocking set up
            setUpNow(interval, maxRetries);
        } else {
            // non-blocking set up
            new SetUpTask(interval, maxRetries, scheduledThreadPoolExecutor).execute();
        }
    }

    private boolean setUpNow(final int interval, final int maxRetries) {
        LOG.debug("Setting up now... (interval: [" + interval + "], maxRetries: [" + maxRetries + "])");
        // blocking set up
        return new SetUpTask(interval, maxRetries).executeNow();
    }

    private class ReconnectTask
    implements Runnable {
        private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

        private ScheduledFuture scheduledFuture;

        private boolean succeeded = false;

        private ReconnectTask() {
            this(null);
        }

        private ReconnectTask(final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor) {
            this.scheduledThreadPoolExecutor = scheduledThreadPoolExecutor;
        }

        public void run() {
            LOG.debug("Executing Reconnect task...");
            long _failTimestamp = System.currentTimeMillis();
            synchronized (reconnectLock) {
                if (_failTimestamp > successTimestamp + 5000) {
                    tearDownNow();
                    if (setUpNow(
                            !retryOnFail ?
                                0 :
                                configuration.getAttributeAsInteger(
                                    "intervalOnReconnect", DEFAULT_INTERVAL_ON_RECONNECT),
                            !retryOnFail ?
                                0 :
                                configuration.getAttributeAsInteger(
                                    "maxRetriesOnReconnect", DEFAULT_MAX_RETRIES_ON_RECONNECT))) {

                        successTimestamp = System.currentTimeMillis();
                        succeeded = true;
                    }
                } else {
                    succeeded = true;
                }
            }
            if (scheduledFuture != null) {
                scheduledFuture.cancel(false);
                scheduledFuture = null;
            }
            LOG.debug("Executing Reconnect task... (succeeded: [" + succeeded + "])");
        }

        private void execute() {
            if (scheduledThreadPoolExecutor != null) {
                scheduledFuture = scheduledThreadPoolExecutor.schedule(this, 0, TimeUnit.MILLISECONDS);
            } else {
                executeNow();
            }
        }

        private boolean executeNow() {
            run();
            return succeeded;
        }
    }

    private class SetUpTask
    implements Runnable {
        private final int interval;
        private final int maxRetries;
        private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

        private ScheduledFuture scheduledFuture;

        private boolean cancelled = false;
        private int retries = 0;
        private boolean succeeded = false;

        private SetUpTask(final int interval, final int maxRetries) {
            this(interval, maxRetries, null);
        }

        private SetUpTask(
            final int interval, final int maxRetries, final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor) {

            this.interval = interval;
            this.maxRetries = maxRetries;
            this.scheduledThreadPoolExecutor = scheduledThreadPoolExecutor;
        }

        public void run() {
            LOG.debug("Executing Set Up task...");
            try {
                // throws InvalidDestinationException, JMSException, NamingException
                setUpMessageServiceClient();
                if (scheduledFuture != null) {
                    scheduledFuture.cancel(false);
                    scheduledFuture = null;
                }
                cancelled = true;
                succeeded = true;
                synchronized (stateLock) {
                    currentState = STATE_SET_UP_DONE;
                    if (LOG.isInfoEnabled()) {
                        LOG.debug("Current State: SET UP DONE");
                    }
                    if (requestedState == STATE_STARTED) {
                        start();
                    }
                }
            } catch (Exception exception) {
                LOG.debug("Exception: " + exception.getClass().getName() + ": " + exception.getMessage());
                tearDownNow();
                if (retries++ == maxRetries) {
                    if (scheduledFuture != null) {
                        scheduledFuture.cancel(false);
                        scheduledFuture = null;
                    }
                    cancelled = true;
                    currentMessagePublisher.stop();
                    // switch from a queue-based message publisher to a noop message publisher.
                    currentMessagePublisher = new NoopMessagePublisher();
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Set up of the message service client failed: " + exception.getMessage());
                    }
                }
            }
            LOG.debug("Executing Set Up task... (succeeded: [" + succeeded + "])");
        }

        private void execute() {
            synchronized (stateLock) {
                currentState = STATE_SET_UP;
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Current State: SET UP");
                }
            }
            if (scheduledThreadPoolExecutor != null) {
                scheduledFuture =
                    scheduledThreadPoolExecutor.scheduleAtFixedRate(this, 0, interval, TimeUnit.MILLISECONDS);
            } else {
                executeNow();
            }
        }

        private boolean executeNow() {
            synchronized (stateLock) {
                currentState = STATE_SET_UP;
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Current State: SET UP");
                }
            }
            while (!cancelled) {
                run();
                if (!cancelled) {
                    try {
                        // throws InterruptedException
                        Thread.sleep(interval);
                    } catch (InterruptedException exception) {
                        // do nothing
                    }
                }
            }
            return succeeded;
        }
    }

    private class TearDownTask
    implements Runnable {
        private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

        private ScheduledFuture scheduledFuture;

        private boolean succeeded = false;

        private TearDownTask() {
            this(null);
        }

        private TearDownTask(final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor) {
            this.scheduledThreadPoolExecutor = scheduledThreadPoolExecutor;
        }

        public void run() {
            LOG.debug("Executing Tear Down task...");
            try {
                tearDownMessageServiceClient();
                if (scheduledFuture != null) {
                    scheduledFuture.cancel(false);
                    scheduledFuture = null;
                }
                succeeded = true;
                synchronized (stateLock) {
                    currentState = STATE_TEAR_DOWN_DONE;
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Current State: TEAR DOWN DONE");
                    }
                    if (requestedState == STATE_CLOSED) {
                        close();
                    }
                }
            } catch (Exception exception) {
                if (scheduledFuture != null) {
                    scheduledFuture.cancel(false);
                    scheduledFuture = null;
                }
            }
            LOG.debug("Executing Tear Down task... (succeeded: [" + succeeded + "])");
        }

        private void execute() {
            synchronized (stateLock) {
                currentState = STATE_TEAR_DOWN;
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Current State: TEAR DOWN");
                }
            }
            if (scheduledThreadPoolExecutor != null) {
                scheduledFuture = scheduledThreadPoolExecutor.schedule(this, 0, TimeUnit.MILLISECONDS);
            } else {
                executeNow();
            }
        }

        private boolean executeNow() {
            synchronized (stateLock) {
                currentState = STATE_TEAR_DOWN;
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Current State: TEAR DOWN");
                }
            }
            run();
            return succeeded;
        }
    }
}
