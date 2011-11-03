package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.webapp.http.common.Server;
import com.icesoft.faces.webapp.http.common.ResponseHandler;
import com.sun.enterprise.web.connector.grizzly.comet.CometContext;
import com.sun.enterprise.web.connector.grizzly.comet.CometEngine;
import com.sun.enterprise.web.connector.grizzly.comet.CometHandler;
import com.sun.enterprise.web.connector.grizzly.comet.CometEvent;

import java.lang.reflect.InvocationTargetException;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GlassFishAdaptingServlet
implements PseudoServlet {
    private static final Log LOG =
        LogFactory.getLog(GlassFishAdaptingServlet.class);

    private final Server server;
    private final String contextPath;

    public GlassFishAdaptingServlet(
        final Server server, final ServletContext servletContext)
    throws ServletException {
        this.server = server;
        try {
            contextPath =
                ServletContext.class.
                    getMethod("getContextPath", new Class[] {}).
                        invoke(servletContext, (Object[])null) + "/";
        } catch (NoSuchMethodException exception) {
            throw
                new EnvironmentAdaptingException(
                    "No such method: ServletContext.getContextPath", exception);
        } catch (IllegalAccessException exception) {
            throw
                new EnvironmentAdaptingException(
                    "Illegal access: ServletContext.getContextPath", exception);
        } catch (InvocationTargetException exception) {
            throw
                new EnvironmentAdaptingException(
                    "Invocation target: ServletContext.getContextPath",
                    exception);
        }
        CometEngine.getEngine().register(contextPath).setExpirationDelay(-1);
    }

    public void service(
        final HttpServletRequest request, final HttpServletResponse response)
    throws Exception {
        GlassFishRequestResponse requestResponse =
            new GlassFishRequestResponse(request, response);
        server.service(requestResponse);
        synchronized (requestResponse) {
            if (!requestResponse.isDone()) {
                requestResponse.park();
                /*
                 * This will make sure that the HTTP request and response are
                 * being "parked" by Grizzly's Comet Engine.
                 *
                 * Please note that, the actual "parking" happens somewhere
                 * after the onInitialize(CometEvent) method of the CometHandler
                 * has been invoked.
                 */
                try {
                    /*
                     * CometContext.addCometHandler(CometHandler) throws an
                     * IllegalStateException when the cometSupport property is
                     * not set to true in the config/domain.xml file.
                     */
                    CometEngine.getEngine().register(contextPath).
                        addCometHandler(requestResponse);
                } catch (IllegalStateException exception) {
                    if (LOG.isErrorEnabled()) {
                        LOG.error(
                            "\r\n" +
                            "\r\n" +
                            "Failed to add Comet handler: \r\n" +
                            "    Exception message: " +
                                exception.getMessage() + "\r\n" +
                            "    Exception cause: " +
                                exception.getCause() + "\r\n\r\n" +
                            "To enable GlassFish ARP, please set the " +
                                "cometSupport property to true in the \r\n" +
                            "domain's config/domain.xml for the " +
                                "http-listener listening to port " +
                                    request.getServerPort() + ".\r\n");
                    }
                    throw new EnvironmentAdaptingException(exception);
                }
            }
        }
    }

    public void shutdown() {
        server.shutdown();
    }

    private class GlassFishRequestResponse
    extends ServletRequestResponse
    implements CometHandler {
        private boolean parked = false;
        private boolean done = false;

        public GlassFishRequestResponse(
            final HttpServletRequest request,
            final HttpServletResponse response)
        throws Exception {
            super(request, response);
        }

        public void attach(final Object object) {
            // do nothing.
        }

        public boolean isDone() {
            return done;
        }

        public boolean isParked() {
            return parked;
        }

        public void onEvent(final CometEvent event)
        throws IOException {
            // do nothing.
        }

        public void onInitialize(final CometEvent event)
        throws IOException {
            // do nothing.
        }

        public void onTerminate(final CometEvent event)
        throws IOException {
            // do nothing.
        }

        public void onInterrupt(final CometEvent event)
        throws IOException {
            // do nothing.
        }

        public void park() {
            parked = true;
        }

        public void respondWith(final ResponseHandler handler)
        throws Exception {
            synchronized (this) {
                if (!isParked()) {
                    handler.respond(this);
                    done = true;
                } else {
                    CometContext cometContext =
                        CometEngine.getEngine().register(contextPath);
                    int count = 0;
                    while (!cometContext.isActive(this)) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException exception) {
                            // ignoring interrupts...
                        }
                        if (count++ > 10)  {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("cometContext.isActive failed");
                            }
                            break;
                        }
                    }
                    if (cometContext.isActive(this))  {
                        handler.respond(this);
                        cometContext.resumeCometHandler(this);
                        unpark();
                    }
                }
            }
        }

        public void unpark() {
            parked = false;
        }
    }
}
