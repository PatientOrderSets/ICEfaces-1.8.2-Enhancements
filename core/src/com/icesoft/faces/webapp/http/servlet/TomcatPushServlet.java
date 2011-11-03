package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.util.event.servlet.ContextDestroyedEvent;
import com.icesoft.faces.util.event.servlet.ContextEventListener;
import com.icesoft.faces.util.event.servlet.ContextEventRepeater;
import com.icesoft.faces.util.event.servlet.ICEfacesIDDisposedEvent;
import com.icesoft.faces.util.event.servlet.ICEfacesIDRetrievedEvent;
import com.icesoft.faces.util.event.servlet.SessionDestroyedEvent;
import com.icesoft.faces.util.event.servlet.ViewNumberDisposedEvent;
import com.icesoft.faces.util.event.servlet.ViewNumberRetrievedEvent;
import com.icesoft.faces.webapp.http.core.ViewQueue;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.catalina.CometEvent;
import org.apache.catalina.CometProcessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TomcatPushServlet
extends HttpServlet
implements CometProcessor, ContextEventListener {
    private static final Log LOG = LogFactory.getLog(TomcatPushServlet.class);

    private final Map eventResponderMap = new HashMap();

    public void contextDestroyed(final ContextDestroyedEvent event) {
        // do nothing.
    }

    public void destroy() {
        // do nothing.
    }

    /**
     * Process the given Comet event.
     *
     * @param event The Comet event that will be processed
     * @throws IOException
     * @throws ServletException
     */
    public void event(final CometEvent event)
    throws IOException, ServletException {
        CometEvent.EventType eventType = event.getEventType();
        if (eventType == CometEvent.EventType.BEGIN) {
            begin(event);
        } else if (eventType == CometEvent.EventType.READ) {
            read(event);
        } else if (eventType == CometEvent.EventType.END) {
            end(event);
        } else if (eventType == CometEvent.EventType.ERROR) {
            error(event);
        }
    }

    public void iceFacesIdDisposed(final ICEfacesIDDisposedEvent event) {
        // The ICEfacesIDDisposedEvent happens on shutdown sequence.
        try {
            Thread.sleep(3000);
        } catch (InterruptedException exception) {
            // ignoring interrupts.
        }
        Object session = event.getSession();
        synchronized (eventResponderMap) {
            if (eventResponderMap.containsKey(session)) {
                ((EventResponder)eventResponderMap.get(session)).dispose();
            }
        }
        // do nothing.
    }

    public void iceFacesIdRetrieved(final ICEfacesIDRetrievedEvent event) {
        // do nothing.
    }

    public void init() throws ServletException {
        ContextEventRepeater.addListener(this);
    }

    public boolean receiveBufferedEvents() {
        return true;
    }

    public void sessionDestroyed(final SessionDestroyedEvent event) {
        Object session = event.getSession();
        synchronized (eventResponderMap) {
            if (eventResponderMap.containsKey(session)) {
                eventResponderMap.remove(session);
            }
        }
    }

    public void viewNumberDisposed(final ViewNumberDisposedEvent event) {
        // do nothing.
    }

    public void viewNumberRetrieved(final ViewNumberRetrievedEvent event) {
        // do nothing.
    }

    protected void begin(final CometEvent event)
    throws IOException, ServletException {
        /*
         * EventType.BEGIN : BEGIN will be called at the beginning of the
         *                   processing of the connection. It can be used to
         *                   initialize any relevant fields using the request
         *                   and response objects. Between the end of the
         *                   processing of this event, and the beginning of the
         *                   processing of the END or ERROR events, it is
         *                   possible to use the response object to write data
         *                   on the open connection. Note that the response
         *                   object and depedent OutputStream and Writer are
         *                   still not synchronized, so when they are accessed
         *                   by multiple threads, synchronization is mandatory.
         *                   After processing the initial event, the request is
         *                   considered to be committed.
         */
        HttpSession session = event.getHttpServletRequest().getSession(false);
        synchronized (eventResponderMap) {
            if (!eventResponderMap.containsKey(session)) {
                MainSessionBoundServlet server =
                    (MainSessionBoundServlet)
                        SessionDispatcher.getSingletonSessionServer(
                            session, getServletContext());
                eventResponderMap.put(
                    session,
                    new EventResponder(
                        server.getSessionID(),
                        server.getAllUpdatedViews(),
                        server.getSynchronouslyUpdatedViews()));
            }
        }
    }

    protected void end(final CometEvent event)
    throws IOException, ServletException {
        /*
         * EventType.END   : END may be called to end the processing of the
         *                   request. Fields that have been initialized in the
         *                   BEGIN method should be reset. After this event has
         *                   been processed, the request and response objects,
         *                   as well as all their dependent objects will be
         *                   recycled and used to process other requests. END
         *                   will also be called when data is available and the
         *                   end of file is reached on the request input (this
         *                   usually indicates the client has pipelined a
         *                   request).
         */
        event.close();
    }

    protected void error(final CometEvent event)
    throws IOException, ServletException {
        /*
         * EventType.ERROR : ERROR will be called by the container in the case
         *                   where an IOException or a similar unrecoverable
         *                   error occurs on the connection. Fields that have
         *                   been initialized in the BEGIN method should be
         *                   reset. After this event has been processed, the
         *                   request and response objects, as well as all their
         *                   dependent objects will be recycled and used to
         *                   process other requests.
         */
        event.close();
    }

    protected void read(final CometEvent event)
    throws IOException, ServletException {
        /*
         * EventType.READ  : READ indicates that input data is available, and
         *                   that one read can be made without blocking. The
         *                   available and ready methods of the InputStream or
         *                   Reader may be used to determine if there is a risk
         *                   of blocking: the servlet should read while data is
         *                   reported available, and can make one additional
         *                   read should read while data is reported available.
         *                   When encountering a read error, the servlet should
         *                   report it by propagating the exception properly.
         *                   Throwing an exception will cause the ERROR event to
         *                   be invoked, and the connection will be closed.
         *                   Alternately, it is also possible to catch any
         *                   exception, perform clean up on any data structure
         *                   the servlet may be using, and using the close
         *                   method of the event. It is not allowed to attempt
         *                   reading data from the request object outside of the
         *                   execution of this method.
         *
         *                   On some platforms, like Windows, a client
         *                   disconnect is indicated by a READ event. Reading
         *                   from the stream may result in -1, an IOException or
         *                   an EOFException. Make sure you properly handle all
         *                   these three cases. If you don't catch the
         *                   IOException, Tomcat will instantly invoke your
         *                   event chain with an ERROR as it catches the error
         *                   for you, and you will be notified of the error at
         *                   that time.
         */
        HttpServletRequest request = event.getHttpServletRequest();
        InputStream in = request.getInputStream();
        byte[] buffer = new byte[4096];
        int n;
        try {
            do {
                n = in.read(buffer);
                if (n > 0) {
//                    LOG.info("POST: [" + new String(buffer, 0, n) + "]");
                } else if (n == -1) {
                    error(event);
                    return;
                }
            } while (in.available() > 0);
        } catch (EOFException exception) {
            if (LOG.isErrorEnabled()) {
                LOG.error(
                    "An EOF exception occurred " +
                        "while trying to read the request.",
                    exception);
            }
            error(event);
            return;
        } catch (IOException exception) {
            if (LOG.isErrorEnabled()) {
                LOG.error(
                    "An I/O error occurred while trying to read the request.",
                    exception);
            }
            error(event);
            return;
        }
        EventResponder eventResponder;
        HttpSession session = request.getSession(false);
        synchronized (eventResponderMap) {
            if (eventResponderMap.containsKey(session)) {
                eventResponder = (EventResponder)eventResponderMap.get(session);
            } else {
                eventResponder = null;
            }
        }
        if (eventResponder != null) {
            synchronized (eventResponder.allUpdatedViews) {
                if (eventResponder.sendResponse(
                        event.getHttpServletResponse())) {

                    event.close();
                } else {
                    eventResponder.pendingRequest = event;
                }
            }
        } else {
            error(event);
        }
    }

    protected void service(
        final HttpServletRequest request, final HttpServletResponse response)
    throws IOException, ServletException {
        // Not used by Tomcat6
        throw
            new ServletException(
                "service() not supported by TomcatPushServlet. Configure the " +
                    "connector, replacing protocol=\"HTTP/1.1\" with " +
                    "protocol=\"org.apache.coyote.http11.Http11NioProtocol\"");
    }

    private static class EventResponder implements Runnable {
        private static final Log LOG = LogFactory.getLog(EventResponder.class);

        private final String sessionID;
        private final ViewQueue allUpdatedViews;
        private final Collection synchronouslyUpdatedViews;

        private CometEvent pendingRequest;

        private EventResponder(
            final String sessionID, final ViewQueue allUpdatedViews,
            final Collection synchronouslyUpdatedViews) {

            this.sessionID = sessionID;
            this.allUpdatedViews = allUpdatedViews;
            this.allUpdatedViews.onPut(this);
            this.synchronouslyUpdatedViews = synchronouslyUpdatedViews;
        }

        public void run() {
            synchronized (allUpdatedViews) {
                if (pendingRequest != null) {
                    try {
                        if (sendResponse(
                                pendingRequest.getHttpServletResponse())) {

                            pendingRequest.close();
                            pendingRequest = null;
                        }
                    } catch (IOException exception) {
                        if (LOG.isErrorEnabled()) {
                            LOG.error(
                                "An I/O error occurred " +
                                    "while trying to send a response.",
                                exception);
                        }
                    }
                }
            }
        }

        private void dispose() {
            responseSender = LastUpdatedViewsSender;
        }

        private Set<String> getUpdatedViews() {
            Set<String> viewIdentifierSet;
            synchronized (allUpdatedViews) {
                allUpdatedViews.removeAll(synchronouslyUpdatedViews);
                synchronouslyUpdatedViews.clear();
                viewIdentifierSet = new HashSet<String>(allUpdatedViews);
                allUpdatedViews.clear();
            }
            return viewIdentifierSet;
        }

        private boolean sendResponse(final HttpServletResponse response)
        throws IOException {
            return responseSender.send(response);
        }

        private final ResponseSender UpdatedViewsSender =
            new ResponseSender() {
                public boolean send(final HttpServletResponse response)
                throws IOException {
                    synchronized (allUpdatedViews) {
                        Set updatedViewSet = getUpdatedViews();
                        if (!updatedViewSet.isEmpty()) {
                            response.setContentType("text/xml; charset=UTF-8");
                            response.addHeader(
                                "X-Powered-By", "Tomcat Push Servlet");
                            String[] viewIdentifiers =
                                (String[])
                                    updatedViewSet.toArray(
                                        new String[updatedViewSet.size()]);
                            PrintWriter writer = response.getWriter();
                            writer.write("<updated-views>");
                            for (int i = 0; i < viewIdentifiers.length; i++) {
                                if (i != 0) {
                                    writer.write(' ');
                                }
                                writer.write(
                                    sessionID + ":" + viewIdentifiers[i]);
                            }
                            writer.write("</updated-views>");
                            writer.flush();
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            };
        private final ResponseSender LastUpdatedViewsSender =
            new ResponseSender() {
                public boolean send(final HttpServletResponse response)
                throws IOException {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException exception) {
                        // ignoring interrupts.
                    }
                    synchronized (allUpdatedViews) {
                        Set updatedViewSet = getUpdatedViews();
                        if (!updatedViewSet.isEmpty()) {
                            response.setContentType("text/xml; charset=UTF-8");
                            response.addHeader(
                                "X-Powered-By", "Tomcat Push Servlet");
                            String[] viewIdentifiers =
                                (String[])
                                    updatedViewSet.toArray(
                                        new String[updatedViewSet.size()]);
                            PrintWriter writer = response.getWriter();
                            writer.write("<updated-views>");
                            for (int i = 0; i < viewIdentifiers.length; i++) {
                                if (i != 0) {
                                    writer.write(' ');
                                }
                                writer.write(
                                    sessionID + ":" + viewIdentifiers[i]);
                            }
                            writer.write("</updated-views>");
                            writer.flush();
                            responseSender = ConnectionCloseSender;
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            };
        private final ResponseSender ConnectionCloseSender =
            new ResponseSender() {
                public boolean send(final HttpServletResponse response)
                throws IOException {
                    /*
                     * let the bridge know that this blocking connection should
                     * not be re-initialized...
                     */
                    // entity header fields
                    response.setHeader("Content-Length", "0");
                    // extension header fields
                    response.setHeader("X-Connection", "close");
                    response.addHeader(
                        "X-Powered-By", "Tomcat Push Servlet");
                    return true;
                }
            };
        private ResponseSender responseSender = UpdatedViewsSender;

        private static interface ResponseSender {
            public boolean send(final HttpServletResponse response)
            throws IOException;
        }
    }
}
