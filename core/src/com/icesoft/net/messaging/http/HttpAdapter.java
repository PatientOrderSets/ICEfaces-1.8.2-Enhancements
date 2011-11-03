package com.icesoft.net.messaging.http;

import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.Server;
import com.icesoft.faces.webapp.http.common.standard.OKHandler;
import com.icesoft.faces.webapp.http.common.standard.NotFoundHandler;
import com.icesoft.faces.webapp.http.servlet.EnvironmentAdaptingServlet;
import com.icesoft.faces.webapp.http.servlet.PseudoServlet;
import com.icesoft.faces.webapp.http.servlet.ServletContextConfiguration;
import com.icesoft.net.messaging.AbstractMessageServiceAdapter;
import com.icesoft.net.messaging.Message;
import com.icesoft.net.messaging.MessageHandler;
import com.icesoft.net.messaging.MessageSelector;
import com.icesoft.net.messaging.MessageServiceAdapter;
import com.icesoft.net.messaging.MessageServiceException;
import com.icesoft.net.messaging.TextMessage;
import com.icesoft.net.messaging.expression.Container;
import com.icesoft.net.messaging.expression.Expression;
import com.icesoft.net.messaging.expression.Or;
import com.icesoft.util.ServerUtility;

import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HttpAdapter
extends AbstractMessageServiceAdapter
implements MessageServiceAdapter {
    private static final Log LOG = LogFactory.getLog(HttpAdapter.class);

    private final Map messageHandlerMap = new HashMap();
    private final Map subscriberMap = new HashMap();

    private PseudoServlet httpMessagingDispatcher;
    private String localAddress;
    private int localPort;
    private boolean running = false;

    public HttpAdapter(final ServletContext servletContext) {
        this(null, -1, servletContext);
    }

    public HttpAdapter(
        final String localAddress, final int localPort,
        final ServletContext servletContext) {

        super(servletContext);
        this.localAddress = localAddress;
        this.localPort = localPort;
        httpMessagingDispatcher =
            new EnvironmentAdaptingServlet(
                new Server() {
                    public void dispatch(
                        final Message message, final String targetName) {

                        if (subscriberMap.containsKey(targetName) &&
                            ((Expression)subscriberMap.get(targetName)).
                                evaluate(message) &&
                            messageHandlerMap.containsKey(targetName)) {

                            if (LOG.isDebugEnabled()) {
                                LOG.debug(
                                    "[" +
                                        messageServiceClient.getName() +
                                    "] Incoming message:\r\n\r\n" + message);
                            }
                            Set _messageHandlerSet =
                                (Set)messageHandlerMap.get(targetName);
                            MessageHandler[] _messageHandlers =
                                (MessageHandler[])
                                    _messageHandlerSet.toArray(
                                        new MessageHandler[
                                            _messageHandlerSet.size()]);
                            for (int i = 0; i < _messageHandlers.length; i++) {
                                MessageSelector _messageSelector =
                                    _messageHandlers[i].getMessageSelector();
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug(
                                        "MessageHandler " +
                                            _messageHandlers[i] + ":\r\n" +
                                        _messageSelector);
                                }
                                if (_messageSelector == null ||
                                    _messageSelector.matches(message)) {

                                    if (LOG.isDebugEnabled()) {
                                        LOG.debug("Match!");
                                    }
                                    _messageHandlers[i].handle(message);
                                }
                            }
                        }
                    }

                    public void service(final Request request)
                    throws Exception {
                        if (LOG.isDebugEnabled()) {
                            StringBuffer _buffer = new StringBuffer();
                            _buffer.
                                append(request.getMethod()).append(" ").
                                    append(request.getURI()).append("\r\n");
                            String[] _headerNames = request.getHeaderNames();
                            for (int i = 0; i < _headerNames.length; i++) {
                                _buffer.
                                    append(_headerNames[i]).append(": ").
                                        append(
                                            request.getHeader(_headerNames[i])).
                                        append("\r\n");
                            }
                            LOG.debug("HTTP Request:\r\n\r\n" + _buffer);
                        }
                        if (request.getRemoteAddr().equals(
                                ServerUtility.getLocalAddr(
                                    request, servletContext))) {

                            Message _message =
                                new TextMessage(
                                    request.getParameter("message"));
                            headersToProperties(request, _message);
                            try {
                                request.respondWith(new OKHandler());
                            } catch (Exception exception) {
                                if (LOG.isErrorEnabled()) {
                                    LOG.error(
                                        "An error occurred " +
                                            "while trying to respond with: " +
                                                "200 OK",
                                        exception);
                                }
                            }
                            dispatch(
                                _message, request.getHeader("X-Target-Name"));
                        } else {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug(
                                    "404 Not Found " +
                                        "(Request-URI: " +
                                            request.getURI() +
                                        ")");
                            }
                            try {
                                request.respondWith(new NotFoundHandler(""));
                            } catch (Exception exception) {
                                if (LOG.isErrorEnabled()) {
                                    LOG.error(
                                        "An error occurred " +
                                            "while trying to respond with: " +
                                                "404 Not Found",
                                        exception);
                                }
                            }
                        }
                    }

                    public void shutdown() {
                        // do nothing.
                    }
                },
                new ServletContextConfiguration(
                    "com.icesoft.faces", servletContext),
                servletContext);
    }

    public void addMessageHandler(
        final MessageHandler messageHandler, final String targetName) {

        if (messageHandler != null &&
            targetName != null && targetName.trim().length() != 0) {

            Set _messageHandlerSet;
            if (messageHandlerMap.containsKey(targetName)) {
                _messageHandlerSet = (Set)messageHandlerMap.get(targetName);
            } else {
                _messageHandlerSet = new HashSet();
                messageHandlerMap.put(targetName, _messageHandlerSet);
            }
            if (!_messageHandlerSet.contains(messageHandler)) {
                _messageHandlerSet.add(messageHandler);
            }
        }
    }

    public void close()
    throws MessageServiceException {
        // do nothing.
    }

    public PseudoServlet getHttpMessagingDispatcher() {
        return httpMessagingDispatcher;
    }

    public void publish(final Message message, final String targetName)
    throws MessageServiceException {
        if (message != null &&
            targetName != null && targetName.trim().length() != 0 &&
            running) {

            HttpURLConnection _connection = null;
            OutputStreamWriter _writer = null;
            InputStreamReader _reader = null;
            try {
                String _destinationServletContextPath =
                    message.getStringProperty(
                        Message.DESTINATION_SERVLET_CONTEXT_PATH);
                URL _url =
                    new URL(
                        "http",
                        localAddress,
                        localPort,
                        (_destinationServletContextPath.startsWith("/") ?
                            _destinationServletContextPath :
                            "/" + _destinationServletContextPath
                        ) + "/block/message"
                    );
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Request-URI: [" + _url + "]");
                }
                _connection = (HttpURLConnection)_url.openConnection();
                _connection.setDoInput(true);
                _connection.setDoOutput(true);
                _connection.setRequestMethod("POST");
                propertiesToHeaders(message, _connection);
                _connection.setRequestProperty("X-Target-Name", targetName);
                _writer =
                    new OutputStreamWriter(_connection.getOutputStream());
                _writer.write(
                    new StringBuffer().
                        append(encode("message")).append('=').
                            append(encode(((TextMessage)message).getText())).
                        toString());
                _writer.flush();
                if (LOG.isDebugEnabled()) {
                    LOG.debug(
                        "[" +
                            messageServiceClient.getName() +
                        "] Outgoing message:\r\n\r\n" +
                            message);
                }
                _reader = new InputStreamReader(_connection.getInputStream());
                if (LOG.isDebugEnabled()) {
                    StringBuffer _buffer = new StringBuffer();
                    Iterator _headerFields =
                        _connection.getHeaderFields().entrySet().iterator();
                    while (_headerFields.hasNext()) {
                        Map.Entry _headerField =
                            (Map.Entry)_headerFields.next();
                        _buffer.
                            append(_headerField.getKey()).append(": ").
                                append(_headerField.getValue()).append("\r\n");
                    }
                    LOG.debug("HTTP Response:\r\n\r\n" + _buffer);
                }
            } catch (UnsupportedEncodingException exception) {
                throw new MessageServiceException(exception);
            } catch (IOException exception) {
                if (_connection != null) {
                    try {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(
                                "Request-URI: [" + _connection.getURL() + "], " +
                                "Status-Code: [" + _connection.getResponseCode() + "], " +
                                "Reason-Phrase: [" + _connection.getResponseMessage() + "]");
                        }
                        if (_connection.getResponseCode() == 503) {
                            // Service Unavailable
                            stop();
                        }
                    } catch (IOException e) {
                        // do nothing.
                    }
                    try {
                        _reader =
                            new InputStreamReader(_connection.getErrorStream());
                    } catch (Exception e) {
                        // do nothing.
                    }
                }
                throw new MessageServiceException(exception);
            } finally {
                if (_connection != null &&
                    _connection.getContentLength() != 0 &&
                    _reader != null) {

                    try {
                        for (
                            int i = 0,
                                _contentLength = _connection.getContentLength();
                            i < _contentLength;
                            i++) {

                            _reader.read();
                        }
                    } catch (IOException exception) {
                        // do nothing.
                    }
                }
                if (_writer != null) {
                    try {
                        _writer.close();
                    } catch (IOException exception) {
                        // do nothing.
                    }
                }
                if (_reader != null) {
                    try {
                        _reader.close();
                    } catch (IOException exception) {
                        // do nothing.
                    }
                }
            }
        }
    }

    public void removeMessageHandler(
        final MessageHandler messageHandler, final String targetName) {

        if (messageHandler != null &&
            targetName != null && targetName.trim().length() != 0) {

            if (messageHandlerMap.containsKey(targetName)) {
                Set _messageHandlerSet = (Set)messageHandlerMap.get(targetName);
                if (_messageHandlerSet.contains(messageHandler)) {
                    _messageHandlerSet.remove(messageHandler);
                }
            }
        }
    }

    public void setLocal(final String localAddress, final int localPort) {
        this.localAddress = localAddress;
        this.localPort = localPort;
    }

    public void shutdown() {
        // do nothing.
    }

    public void start() {
        running = true;
    }

    public void stop() {
        running = false;
    }

    public void subscribe(
        final String targetName, final MessageSelector messageSelector,
        final boolean noLocal)
    throws MessageServiceException {
        if (targetName != null && targetName.trim().length() != 0) {
            Container _container =
                new Container(messageSelector.getExpression());
            if (subscriberMap.containsKey(targetName)) {
                subscriberMap.put(
                    targetName,
                    new Or(
                        (Expression)subscriberMap.get(targetName),
                        _container));
            } else {
                subscriberMap.put(targetName, _container);
            }
        }
    }

    public void unsubscribe(final String targetName)
    throws MessageServiceException {
        if (targetName != null && targetName.trim().length() != 0) {
            if (subscriberMap.containsKey(targetName)) {
                subscriberMap.remove(targetName);
            }
        }
    }

    private static String encode(final String string)
    throws UnsupportedEncodingException {
        return URLEncoder.encode(string, "UTF-8");
    }

    private static void headersToProperties(
        final Request request, final Message message) {

        String _header;
        _header = request.getHeader("X-Source-Servlet-Context-Path");
        if (_header != null) {
            message.setStringProperty(
                Message.SOURCE_SERVLET_CONTEXT_PATH, _header);
        }
        _header = request.getHeader("X-Source-Node-Address");
        if (_header != null) {
            message.setStringProperty(
                Message.SOURCE_NODE_ADDRESS, _header);
        }
        _header = request.getHeader("X-Destination-Servlet-Context-Path");
        if (_header != null) {
            message.setStringProperty(
                Message.DESTINATION_SERVLET_CONTEXT_PATH, _header);
        }
        _header = request.getHeader("X-Destination-Node-Address");
        if (_header != null) {
            message.setStringProperty(
                Message.DESTINATION_NODE_ADDRESS, _header);
        }
        _header = request.getHeader("X-Message-Type");
        if (_header != null) {
            message.setStringProperty(
                Message.MESSAGE_TYPE, _header);
        }
        _header = request.getHeader("X-Message-Lengths");
        if (_header != null) {
            message.setStringProperty(
                Message.MESSAGE_LENGTHS, _header);
        }
    }

    private static void propertiesToHeaders(
        final Message message, final HttpURLConnection connection) {
        
        if (message.propertyExists(Message.SOURCE_SERVLET_CONTEXT_PATH)) {
            connection.setRequestProperty(
                "X-Source-Servlet-Context-Path",
                message.getStringProperty(
                    Message.SOURCE_SERVLET_CONTEXT_PATH));
        }
        if (message.propertyExists(Message.SOURCE_NODE_ADDRESS)) {
            connection.setRequestProperty(
                "X-Source-Node-Address",
                message.getStringProperty(
                    Message.SOURCE_NODE_ADDRESS));
        }
        if (message.propertyExists(Message.DESTINATION_SERVLET_CONTEXT_PATH)) {
            connection.setRequestProperty(
                "X-Destination-Servlet-Context-Path",
                message.getStringProperty(
                    Message.DESTINATION_SERVLET_CONTEXT_PATH));
        }
        if (message.propertyExists(Message.DESTINATION_NODE_ADDRESS)) {
            connection.setRequestProperty(
                "X-Destination-Node-Address",
                message.getStringProperty(
                    Message.DESTINATION_NODE_ADDRESS));
        }
        if (message.propertyExists(Message.MESSAGE_TYPE)) {
            connection.setRequestProperty(
                "X-Message-Type",
                message.getStringProperty(
                    Message.MESSAGE_TYPE));
        }
        if (message.propertyExists(Message.MESSAGE_LENGTHS)) {
            connection.setRequestProperty(
                "X-Message-Lengths",
                message.getStringProperty(
                    Message.MESSAGE_LENGTHS));
        }
    }
}
