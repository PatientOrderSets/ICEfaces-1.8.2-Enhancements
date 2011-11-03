package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.async.render.RenderManager;
import com.icesoft.faces.env.Authorization;
import com.icesoft.faces.util.event.servlet.ContextEventRepeater;
import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.faces.webapp.http.common.FileLocator;
import com.icesoft.faces.webapp.http.common.MimeTypeMatcher;
import com.icesoft.faces.webapp.http.common.standard.NotFoundHandler;
import com.icesoft.faces.webapp.http.common.standard.ResponseHandlerServer;
import com.icesoft.faces.webapp.http.core.DisposeBeans;
import com.icesoft.faces.webapp.http.core.ResourceServer;
import com.icesoft.net.messaging.MessageServiceClient;
import com.icesoft.net.messaging.http.HttpAdapter;
import com.icesoft.net.messaging.jms.JMSAdapter;
import com.icesoft.util.IdGenerator;
import com.icesoft.util.MonitorRunner;
import com.icesoft.util.SeamUtilities;
import com.icesoft.util.ServerUtility;

import edu.emory.mathcs.backport.java.util.concurrent.ScheduledThreadPoolExecutor;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.net.URI;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MainServlet extends HttpServlet {
    private static final Log LOG = LogFactory.getLog(MainServlet.class);
    private static final CurrentContextPath currentContextPath = new CurrentContextPath();
    private static final PseudoServlet NotFound = new BasicAdaptingServlet(new ResponseHandlerServer(new NotFoundHandler("")));

    static {
        final String headless = "java.awt.headless";
        if (null == System.getProperty(headless)) {
            System.setProperty(headless, "true");
        }
    }

    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(10);

    private PathDispatcher dispatcher = new PathDispatcher();
    private ServletContext context;
    private MonitorRunner monitorRunner;
    private CoreMessageService coreMessageService;
    private String localAddress;
    private int localPort;
    private String blockingRequestHandlerContext;
    private boolean detectionDone = false;

    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        context = servletConfig.getServletContext();
        try {
            final Configuration configuration = new ServletContextConfiguration("com.icesoft.faces", context);
            final IdGenerator idGenerator = new IdGenerator(context.getResource("/WEB-INF/web.xml").getPath());
            final MimeTypeMatcher mimeTypeMatcher = new MimeTypeMatcher() {
                public String mimeTypeFor(String extension) {
                    return context.getMimeType(extension);
                }
            };
            final FileLocator localFileLocator = new FileLocator() {
                public File locate(String path) {
                    URI contextURI = URI.create(currentContextPath.lookup());
                    URI pathURI = URI.create(path);
                    String result = contextURI.relativize(pathURI).getPath();
                    String fileLocation = context.getRealPath(result);
                    return new File(fileLocation);
                }
            };
            monitorRunner = new MonitorRunner(configuration.getAttributeAsLong("monitorRunnerInterval", 10000));
            RenderManager.setServletConfig(servletConfig);
            PseudoServlet resourceServer = new BasicAdaptingServlet(new ResourceServer(configuration, mimeTypeMatcher, localFileLocator));
            PseudoServlet sessionDispatcher = new SessionDispatcher(context) {
                protected PseudoServlet newServer(HttpSession session, Monitor sessionMonitor, Authorization authorization) {
                    return new MainSessionBoundServlet(session, sessionMonitor, idGenerator, mimeTypeMatcher, monitorRunner, configuration, getCoreMessageService(configuration), blockingRequestHandlerContext, authorization);
                }
            };
            if (SeamUtilities.isSpringEnvironment()) {
                //Need to dispatch to the Spring resource server
                dispatcher.dispatchOn("/spring/resources/", resourceServer);
            }
            //don't create new session for resources belonging to expired user sessions
            dispatcher.dispatchOn(".*(block\\/resource\\/)", new SessionVerifier(sessionDispatcher, false));
            if (!configuration.getAttributeAsBoolean("synchronousUpdate", false)) {
                dispatcher.dispatchOn(".*(block\\/message)",
                        new PseudoServlet() {
                            public void service(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
                                if (coreMessageService != null && coreMessageService.getMessageServiceClient().getMessageServiceAdapter() instanceof HttpAdapter) {
                                    ((HttpAdapter)coreMessageService.getMessageServiceClient().getMessageServiceAdapter()).getHttpMessagingDispatcher().service(request, response);
                                } else {
                                    NotFound.service(request, response);
                                }
                            }

                            public void shutdown() {
                                // do nothing.
                            }
                        });
            }
            //don't create new session for XMLHTTPRequests identified by "block/*" prefixed paths
            dispatcher.dispatchOn(".*(block\\/)", new SessionVerifier(sessionDispatcher, true));
            dispatcher.dispatchOn(".*(\\/$|\\.iface$|\\.jsf|\\.faces$|\\.jsp$|\\.jspx$|\\.html$|\\.xhtml$|\\.seam$|uploadHtml$|/spring/)", sessionDispatcher);
            dispatcher.dispatchOn(".*", resourceServer);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    public void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Servicing Request-URI: ["+ request.getRequestURI() + "]");
        }
        if (localAddress == null) {
            localAddress = ServerUtility.getLocalAddr(request, context);
            localPort = ServerUtility.getLocalPort(request, context);
        }
        try {
            currentContextPath.attach(request.getContextPath());
            dispatcher.service(request, response);
        } catch (SocketException e) {
            if ("Broken pipe".equals(e.getMessage())) {
                // client left the page
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Connection broken by client.", e);
                } else if (LOG.isDebugEnabled()) {
                    LOG.debug("Connection broken by client: " + e.getMessage());
                }
            } else {
                throw new ServletException(e);
            }
        } catch (RuntimeException e) {
            //ICE-4261: We cannot wrap RuntimeExceptions as ServletExceptions because of support for Jetty
            //Continuations.  However, if the message of a RuntimeException is null, Tomcat won't
            //properly redirect to the configured error-page.  So we need a new RuntimeException
            //that actually includes a message.
            if (e.getMessage() != null) {
                throw e;
            }
            // ICE-4507 let Jetty continuation messages get through untouched
            String errorClassname = e.getClass().getName();
            if (errorClassname.startsWith("org.mortbay.jetty")) {
                throw e;
            }
            throw new RuntimeException("wrapped Exception: " + errorClassname, e);
        } catch (Exception e) {
            throw new ServletException(e);
        } finally {
            currentContextPath.detach();
        }
    }

    public void destroy() {
        monitorRunner.stop();
        DisposeBeans.in(context);
        dispatcher.shutdown();
        if (coreMessageService != null) {
            coreMessageService.stop();
            coreMessageService.tearDownNow();
        }
        scheduledThreadPoolExecutor.shutdown();
    }

    private synchronized CoreMessageService getCoreMessageService(final Configuration configuration) {
        if (!detectionDone) {
            if (!configuration.getAttributeAsBoolean("synchronousUpdate", false)) {
                setUpCoreMessageService(configuration);
            }
            detectionDone = true;
        }
        return coreMessageService;
    }

    private boolean isJMSAvailable() {
        try {
            this.getClass().getClassLoader().
                    loadClass("javax.jms.TopicConnectionFactory");
            return true;
        } catch (ClassNotFoundException exception) {
            return false;
        }
    }

    private void setUpCoreMessageService(final Configuration configuration) {
        String blockingRequestHandler = configuration.getAttribute("blockingRequestHandler", "auto-detect");
        if (blockingRequestHandler.equalsIgnoreCase("icefaces")) {
            // Adapt to Push environment.
            if (LOG.isInfoEnabled()) {
                LOG.info("Blocking Request Handler: \"" + blockingRequestHandler + "\"");
            }
            if (LOG.isInfoEnabled()) {
                LOG.info("Adapting to Push environment.");
            }
        } else if (
            blockingRequestHandler.equalsIgnoreCase("push-server") ||
            blockingRequestHandler.equalsIgnoreCase("auto-detect")) {

            // Adapt to Server Push / auto-detect environment.
            if (LOG.isInfoEnabled()) {
                LOG.info("Blocking Request Handler: \"" + blockingRequestHandler + "\"");
            }
            String blockingRequestHandlerContext = configuration.getAttribute("blockingRequestHandlerContext", "push-server");
            coreMessageService =
                new CoreMessageService(
                    new MessageServiceClient("Core MSC", new HttpAdapter(localAddress, localPort, context), currentContextPath.lookup()),
                    scheduledThreadPoolExecutor,
                    new ServletContextConfiguration("com.icesoft.net.messaging", context),
                    blockingRequestHandlerContext);
            if (coreMessageService.setUpNow()) {
                this.blockingRequestHandlerContext = URI.create("/").resolve(blockingRequestHandlerContext + "/").toString();
            } else {
                coreMessageService = null;
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Push Server not found - the Push Server must be deployed to support multiple asynchronous applications.");
                }
                if (LOG.isInfoEnabled()) {
                    LOG.info("Adapting to Push environment.");
                }
            }
        } else {
            if (LOG.isInfoEnabled()) {
                LOG.info("Blocking Request Handler: \"" + blockingRequestHandler + "\"");
            }
            boolean isJMSAvailable = isJMSAvailable();
            if (LOG.isDebugEnabled()) {
                LOG.debug("JMS API Available: " + isJMSAvailable);
            }
            String blockingRequestHandlerContext = configuration.getAttribute("blockingRequestHandlerContext", "push-server");
            if (isJMSAvailable) {
                coreMessageService =
                    new CoreMessageService(
                        new MessageServiceClient("Core MSC", new JMSAdapter(context), currentContextPath.lookup()),
                        scheduledThreadPoolExecutor,
                        new ServletContextConfiguration("com.icesoft.net.messaging", context),
                        true,
                        configuration.getAttribute("blockingRequestHandlerContext", "push-server"));
                if (coreMessageService.setUpNow()) {
                    this.blockingRequestHandlerContext = URI.create("/").resolve(blockingRequestHandlerContext + "/").toString();
                } else {
                    coreMessageService = null;
                }
            }
            if (coreMessageService == null) {
                coreMessageService =
                    new CoreMessageService(
                        new MessageServiceClient("Core MSC", new HttpAdapter(localAddress, localPort, context), currentContextPath.lookup()),
                        scheduledThreadPoolExecutor,
                        new ServletContextConfiguration("com.icesoft.net.messaging", context),
                        configuration.getAttribute("blockingRequestHandlerContext", "push-server"));
                if (coreMessageService.setUpNow()) {
                    this.blockingRequestHandlerContext = URI.create("/").resolve(blockingRequestHandlerContext + "/").toString();
                } else {
                    coreMessageService = null;
                }
            }
            if (coreMessageService == null) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Push Server not found - the Push Server must be deployed to support multiple asynchronous applications.");
                }
                if (LOG.isInfoEnabled()) {
                    LOG.info("Adapting to Push environment.");
                }
            }
        }
        if (coreMessageService != null) {
            coreMessageService.start();
            ContextEventRepeater.setCoreMessageService(coreMessageService);
        }
    }

    //todo: factor out into a ServletContextDispatcher
    private static class CurrentContextPath extends ThreadLocal {
        public String lookup() {
            return (String) get();
        }

        public void attach(String path) {
            set(path);
        }

        public void detach() {
            set(null);
        }
    }
}
