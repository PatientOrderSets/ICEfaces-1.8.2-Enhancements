package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.webapp.command.CommandQueue;
import com.icesoft.faces.webapp.command.Pong;
import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.Server;
import com.icesoft.faces.webapp.http.common.ResponseHandler;
import com.icesoft.faces.webapp.http.common.Response;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ReceivePing implements Server {
    private static final Log LOG = LogFactory.getLog(ReceivePing.class);
    private static final Pong PONG = new Pong();
    private static final ResponseHandler CLOSE_RESPONSE = new ResponseHandler() {
        public void respond(Response response) throws Exception {
            //let the bridge know that this blocking connection should not be re-initialized
            response.setHeader("X-Connection", "close");
            response.setHeader("Content-Length", 0);
        }
    };
    private Map commandQueues;
    private PageTest pageTest;

    public ReceivePing(final Map commandQueues, final PageTest pageTest) {
        this.commandQueues = commandQueues;
        this.pageTest = pageTest;
    }

    public void service(final Request request) throws Exception {
        if (!pageTest.isLoaded()) {
            request.respondWith(new ReloadResponse(""));
        } else {
            String viewIdentifier = request.getParameter("ice.view");
            CommandQueue queue = (CommandQueue) commandQueues.get(viewIdentifier);
            if (queue != null) {
                queue.put(PONG);
            } else {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("could not get a valid queue for " + viewIdentifier);
                }
            }
            request.respondWith(NOOPResponse.Handler);
        }
    }

    public void shutdown() {
    }
}
