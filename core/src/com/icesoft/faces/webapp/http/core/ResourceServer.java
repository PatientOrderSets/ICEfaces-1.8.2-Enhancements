package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.faces.webapp.http.common.FileLocator;
import com.icesoft.faces.webapp.http.common.MimeTypeMatcher;
import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.Server;
import com.icesoft.faces.webapp.http.common.standard.CacheControlledServer;
import com.icesoft.faces.webapp.http.common.standard.CompressingServer;
import com.icesoft.faces.webapp.http.common.standard.PathDispatcherServer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

public class ResourceServer implements Server {
    private static final Log log = LogFactory.getLog(ResourceServer.class);
    private Server dispatcher;

    public ResourceServer(Configuration configuration, MimeTypeMatcher mimeTypeMatcher, FileLocator fileLocator) {
        PathDispatcherServer pathDispatcher = new PathDispatcherServer();
        pathDispatcher.dispatchOn(".*xmlhttp\\/javascript-blocked$", new RedirectOnJSBlocked(configuration));
        pathDispatcher.dispatchOn(".*xmlhttp\\/.*\\/.*\\.js$", new CacheControlledServer(new ServeJSCode()));
        pathDispatcher.dispatchOn(".*xmlhttp\\/css\\/.*", new CacheControlledServer(new ServeCSSResource(mimeTypeMatcher)));
        pathDispatcher.dispatchOn(".*xmlhttp\\/blank$", new CacheControlledServer(new ServeBlankPage()));
        pathDispatcher.dispatchOn(".*xmlhttp\\/wait\\-cursor$", new CacheControlledServer(new WaitCursorPage()));
        //match any path that does not point to WEB-INF directory
        pathDispatcher.dispatchOn("^(?!.*WEB\\-INF.*).*$", new FileServer(fileLocator, mimeTypeMatcher));
        dispatcher = new CompressingServer(pathDispatcher, mimeTypeMatcher, configuration);
    }

    public void service(Request request) throws Exception {
        try {
            dispatcher.service(request);
        } catch (IOException e) {
            //capture & log Tomcat specific exception            
            if (e.getClass().getName().endsWith("ClientAbortException")) {
                log.debug("Browser closed the connection prematurely for " + request.getURI());
            } else {
                throw e;
            }
        }
    }

    public void shutdown() {
        dispatcher.shutdown();
    }
}
