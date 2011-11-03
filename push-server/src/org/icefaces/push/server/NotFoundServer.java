package org.icefaces.push.server;

import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.Server;
import com.icesoft.faces.webapp.http.common.standard.NotFoundHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NotFoundServer
implements Server {
    private static final Log LOG = LogFactory.getLog(NotFoundServer.class);

    public NotFoundServer() {
        // do nothing.
    }

    public void service(final Request request)
    throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug(
                "404 Not Found (Request-URI: " + request.getURI() + ")");
        }
        request.respondWith(new NotFoundHandler(""));
    }

    public void shutdown() {
        // do nothing.
    }
}
