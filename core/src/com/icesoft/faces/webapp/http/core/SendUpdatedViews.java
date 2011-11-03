package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.webapp.http.common.*;
import com.icesoft.faces.webapp.http.common.standard.FixedXMLContentHandler;
import com.icesoft.faces.webapp.http.common.standard.ResponseHandlerServer;
import com.icesoft.util.MonitorRunner;
import edu.emory.mathcs.backport.java.util.concurrent.BlockingQueue;
import edu.emory.mathcs.backport.java.util.concurrent.LinkedBlockingQueue;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class SendUpdatedViews implements Server, Runnable {
    private static final Runnable NOOP = new Runnable() {
        public void run() {
        }
    };
    private static final ResponseHandler CloseResponse = new ResponseHandler() {
        public void respond(Response response) throws Exception {
            //let the bridge know that this blocking connection should not be re-initialized
            response.setHeader("X-Connection", "close");
            response.setHeader("Content-Length", 0);
        }
    };
    //Define here to avoid classloading problems after application exit
    private static final ResponseHandler NoopHandler = NOOPResponse.Handler;
    private static final Server AfterShutdown = new ResponseHandlerServer(CloseResponse);

    private final BlockingQueue pendingRequest = new LinkedBlockingQueue(1);
    private final ViewQueue allUpdatedViews;
    private final Collection synchronouslyUpdatedViews;
    private final String sessionID;
    private final long timeoutInterval;
    private final MonitorRunner monitorRunner;
    private long responseTimeoutTime;
    private Server activeServer;

    public SendUpdatedViews(String sessionID, final Collection synchronouslyUpdatedViews, final ViewQueue allUpdatedViews, final MonitorRunner monitorRunner, Configuration configuration, final PageTest pageTest) {
        this.timeoutInterval = configuration.getAttributeAsLong("blockingConnectionTimeout", 90000);
        this.sessionID = sessionID;
        this.allUpdatedViews = allUpdatedViews;
        this.synchronouslyUpdatedViews = synchronouslyUpdatedViews;
        this.monitorRunner = monitorRunner;

        allUpdatedViews.onPut(new Runnable() {
            public void run() {
                respondIfViewsAvailable();
            }
        });
        //add monitor
        monitorRunner.registerMonitor(this);

        //define blocking server
        final Server blockingServer = new Server() {
            public void service(final Request request) throws Exception {
                responseTimeoutTime = System.currentTimeMillis() + timeoutInterval;
                respondIfPendingRequest(CloseResponse);
                pendingRequest.put(request);
                respondIfViewsAvailable();
            }

            public void shutdown() {
                //avoid creating new blocking connections after shutdown
                activeServer = AfterShutdown;
                respondIfPendingRequest(CloseResponse);
            }
        };

        //setup server that switches to blocking server after first request
        activeServer = new ServerProxy(blockingServer) {
            public void service(Request request) throws Exception {
                //after first request switch to blocking server
                activeServer = blockingServer;

                //ICE-3493 -- test if there was a previous request made for loading the page,
                //if not assume that this request was meant for a failed cluster node
                if (pageTest.isLoaded()) {
                    //page was loaded from this node, so use the blocking server
                    super.service(request);
                } else {
                    //this is probably a failed-over request, send a Reload command together with the new session cookie
                    request.respondWith(new ReloadResponse("") );
                }
            }
        };
    }

    public void service(final Request request) throws Exception {
        activeServer.service(request);
    }

    public void shutdown() {
        //remove monitor
        monitorRunner.unregisterMonitor(this);
        allUpdatedViews.onPut(NOOP);
        activeServer.shutdown();
    }

    public void run() {
        if ((System.currentTimeMillis() > responseTimeoutTime) && (!pendingRequest.isEmpty())) {
            respondIfPendingRequest(NoopHandler);
        }
    }

    private void respondIfViewsAvailable() {
        try {
            allUpdatedViews.removeAll(synchronouslyUpdatedViews);
            synchronouslyUpdatedViews.clear();
            if (!allUpdatedViews.isEmpty()) {
                Request request = (Request) pendingRequest.poll();
                if (request != null) {
                    request.respondWith(new FixedXMLContentHandler() {
                        public void writeTo(Writer writer) throws IOException {
                            writer.write("<updated-views>");
                            Iterator i = new HashSet(allUpdatedViews).iterator();
                            while (i.hasNext()) {
                                writer.write(sessionID);
                                writer.write(":");
                                writer.write((String) i.next());
                                writer.write(' ');
                            }
                            writer.write("</updated-views>");
                            allUpdatedViews.clear();
                        }
                    });
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private void respondIfPendingRequest(ResponseHandler handler) {
        Request previousRequest = (Request) pendingRequest.poll();
        if (previousRequest != null) {
            try {
                previousRequest.respondWith(handler);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
