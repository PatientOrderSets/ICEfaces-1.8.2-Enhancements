package org.icefaces.push.server;

import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.Server;
import com.icesoft.faces.webapp.http.servlet.SessionDispatcher;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class DisposeViewsHandlerServer
implements Server {
    private static final Log LOG =
        LogFactory.getLog(DisposeViewsHandlerServer.class);

    private final SessionDispatcher.Monitor monitor;

    public DisposeViewsHandlerServer(final SessionDispatcher.Monitor monitor) {
        this.monitor = monitor;
    }

    public void service(final Request request)
    throws Exception {
        monitor.touchSession();
        handle(request);
    }

    public abstract void handle(Request request);

    public void shutdown() {
        // do nothing.
    }
}
