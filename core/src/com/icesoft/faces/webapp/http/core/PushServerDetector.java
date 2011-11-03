package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.async.common.PushServerAdaptingServlet;
import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.Server;
import com.icesoft.faces.webapp.http.servlet.CoreMessageService;
import com.icesoft.util.MonitorRunner;

import java.util.Collection;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PushServerDetector
implements Server {
    private static final Log LOG = LogFactory.getLog(PushServerDetector.class);
    private static final Object LOCK = new Object();

    private static ServerFactory factory;
    private static ServerFactory fallbackFactory;

    private Server server;

    public PushServerDetector(
        final HttpSession httpSession, final String icefacesID,
        final Collection synchronouslyUpdatedViews,
        final ViewQueue allUpdatedViews, final MonitorRunner monitorRunner,
        final Configuration configuration,
        final CoreMessageService coreMessageService,
        final PageTest pageTest) {

        if (factory == null) {
            synchronized (LOCK) {
                if (factory == null) {
                    if (coreMessageService != null) {
                        factory = new PushServerAdaptingServletFactory();
                        fallbackFactory = new SendUpdatedViewsFactory();
                    } else {
                        factory = new SendUpdatedViewsFactory();
                    }
                }
            }
        }
        server =
            factory.newServer(
                httpSession, icefacesID, synchronouslyUpdatedViews,
                allUpdatedViews, monitorRunner, configuration,
                coreMessageService, pageTest);
    }

    public void service(final Request request) throws Exception {
        server.service(request);
    }

    public void shutdown() {
        server.shutdown();
    }

    private static interface ServerFactory {
        public Server newServer(
            final HttpSession httpSession, final String icefacesID,
            final Collection synchronouslyUpdatedViews,
            final ViewQueue allUpdatedViews,
            final MonitorRunner monitorRunner,
            final Configuration configuration,
            final CoreMessageService coreMessageService,
            final PageTest pageTest);
    }

    private static class PushServerAdaptingServletFactory
    implements ServerFactory {
        public Server newServer(
            final HttpSession httpSession, final String icefacesID,
            final Collection synchronouslyUpdatedViews,
            final ViewQueue allUpdatedViews,
            final MonitorRunner monitorRunner,
            final Configuration configuration,
            final CoreMessageService coreMessageService,
            final PageTest pageTest) {

            try {
                return
                    new PushServerAdaptingServlet(
                        httpSession,
                        icefacesID,
                        synchronouslyUpdatedViews,
                        allUpdatedViews,
                        configuration,
                        coreMessageService);
            } catch (Exception exception) {
                // Possible exceptions: MessageServiceException
                LOG.warn(
                    "Failed to adapt to Push Server environment. Falling " +
                        "back to Push environment.",
                    exception);
                synchronized (LOCK) {
                    factory = fallbackFactory;
                    fallbackFactory = null;
                }
                return
                    factory.newServer(
                        httpSession, icefacesID, synchronouslyUpdatedViews,
                        allUpdatedViews, monitorRunner, configuration,
                        coreMessageService, pageTest);
            }
        }
    }

    private static class SendUpdatedViewsFactory
    implements ServerFactory {
        public Server newServer(
            final HttpSession httpSession, final String icefacesID,
            final Collection synchronouslyUpdatedViews,
            final ViewQueue allUpdatedViews,
            final MonitorRunner monitorRunner,
            final Configuration configuration,
            final CoreMessageService coreMessageService,
            final PageTest pageTest) {

            return
                new SendUpdatedViews(
                    icefacesID, synchronouslyUpdatedViews, allUpdatedViews,
                    monitorRunner, configuration, pageTest);
        }
    }
}
