package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.webapp.command.Command;
import com.icesoft.faces.webapp.command.CommandQueue;
import com.icesoft.faces.webapp.command.NOOP;
import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.Server;
import com.icesoft.faces.webapp.http.common.standard.FixedXMLContentHandler;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SendUpdates implements Server {
    private static final Log LOG = LogFactory.getLog(SendUpdates.class);
    private static final Command NOOP = new NOOP();
    private Map commandQueues;
    private PageTest pageTest;
    private static boolean debugDOMUpdate;

    public SendUpdates(final Configuration configuration, final Map commandQueues, final PageTest pageTest) {
        this.commandQueues = commandQueues;
        this.pageTest = pageTest;
        debugDOMUpdate = configuration
            .getAttributeAsBoolean("debugDOMUpdate", false);
    }

    public void service(final Request request) throws Exception {
        if (!pageTest.isLoaded()) {
            request.respondWith(new ReloadResponse(""));
        } else {
            request.respondWith(new Handler(commandQueues, request));
        }
    }

    public void shutdown() {
    }

    public static class Handler extends FixedXMLContentHandler {
        private final Request request;
        private Map commandQueues;

        public Handler(Map commandQueues, Request request) {
            this.commandQueues = commandQueues;
            this.request = request;
        }

        public void writeTo(Writer writer) throws IOException {
            String viewIdentifier = request.getParameter("ice.view");
            if (commandQueues.containsKey(viewIdentifier)) {
                CommandQueue queue = (CommandQueue) commandQueues.get(viewIdentifier);
                Command command = queue.take();
                if (SendUpdates.debugDOMUpdate) {
                    //logging can be problematic in different server
                    //environments
                    System.out.println(command);
                }
                if (LOG.isTraceEnabled())  {
                    LOG.trace(command);
                }
                command.serializeTo(writer);
            } else {
                NOOP.serializeTo(writer);
            }
        }
    }
}
