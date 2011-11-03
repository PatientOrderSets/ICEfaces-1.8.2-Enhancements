package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.env.Authorization;
import com.icesoft.util.ThreadLocalUtility;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class SessionDispatcher implements PseudoServlet {
    private final static Log Log = LogFactory.getLog(SessionDispatcher.class);
    //ICE-3073 - manage sessions with this structure
    private final static Map SessionMonitors = new HashMap();
    private final Map sessionBoundServers = new HashMap();
    private final Map activeRequests = new HashMap();
    private ServletContext context;

    public SessionDispatcher(ServletContext context) {
        associateSessionDispatcher(context);
        this.context = context;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(true);
        checkSession(session);

        //attach session bound server to the current thread -- this is a lock-free strategy
        String id = session.getId();
        try {
            //put the request in the pool of active request in case HttpServletRequest.isUserInRole need to be called
            addRequest(id, request);
            //lookup session bound server -- this is a lock-free strategy
            lookupServer(session).service(request, response);
        } finally {
            //remove the request from the active requests pool
            removeRequest(id, request);
        }
    }

    public void shutdown() {
        Iterator i = sessionBoundServers.values().iterator();
        while (i.hasNext()) {
            PseudoServlet pseudoServlet = (PseudoServlet) i.next();
            pseudoServlet.shutdown();
        }
    }

    protected abstract PseudoServlet newServer(HttpSession session, Monitor sessionMonitor, Authorization authorization) throws Exception;

    protected void checkSession(HttpSession session) throws Exception {
        final String id = session.getId();
        final Monitor monitor;
        synchronized (SessionMonitors) {
            if (!SessionMonitors.containsKey(id)) {
                monitor = new Monitor(session);
                SessionMonitors.put(id, monitor);
            } else {
                monitor = (Monitor) SessionMonitors.get(id);
            }
            //it is possible to have multiple web-app contexts associated with the same session ID
            monitor.addInSessionContext(context);
        }

        synchronized (sessionBoundServers) {
            if (!sessionBoundServers.containsKey(id)) {
                sessionBoundServers.put(id, this.newServer(session, monitor, new Authorization() {
                    public boolean isUserInRole(String role) {
                        return inRole(id, role);
                    }
                }));
            }
        }
    }

    protected PseudoServlet lookupServer(final HttpSession session) {
        return lookupServer(session.getId());
    }

    protected PseudoServlet lookupServer(final String sessionId) {
        return (PseudoServlet) sessionBoundServers.get(sessionId);
    }

    private void sessionShutdown(HttpSession session) {
        PseudoServlet servlet = (PseudoServlet) sessionBoundServers.get(session.getId());
        servlet.shutdown();
    }

    private void sessionDestroy(HttpSession session) {
        sessionBoundServers.remove(session.getId());
    }

    private void addRequest(String key, HttpServletRequest request) {
        synchronized (activeRequests) {
            if (activeRequests.containsKey(key)) {
                List requests = (List) activeRequests.get(key);
                requests.add(request);
            } else {
                List requests = new ArrayList();
                requests.add(request);
                activeRequests.put(key, requests);
            }
        }
    }

    private void removeRequest(String key, HttpServletRequest request) {
        synchronized (activeRequests) {
            List requests = (List) activeRequests.get(key);
            if (requests != null) {
                requests.remove(request);
                if (requests.isEmpty()) {
                    activeRequests.remove(key);
                }
            }
        }
    }

    private boolean inRole(String sessionID, String role) {
        Collection sessionRequests = (Collection) activeRequests.get(sessionID);
        if (sessionRequests != null && !sessionRequests.isEmpty()) {
            Iterator i = new ArrayList(sessionRequests).iterator();
            while (i.hasNext()) {
                try {
                    HttpServletRequest request = (HttpServletRequest) i.next();
                    if (request.isUserInRole(role)) {
                        return true;
                    }
                } catch (Throwable t) {
                    //ignore
                }
            }
        }
        return false;
    }

    /**
     * Perform the session shutdown tasks for a session that has either been invalidated via
     * the ICEfaces Session wrapper (internal) or via a sessionDestroyed event from a container
     * (external). #3164 If the Session has been externally invalidated this method doesn't need
     * to invalidate it again as that can cause infinite loops in some containers.
     *
     * @param session Session to invalidate
     */
    private static void notifySessionShutdown(final HttpSession session, final ServletContext context) {
        Log.debug("Shutting down session: " + session.getId());
        String sessionID = session.getId();
        // avoid executing this method twice
        if (!SessionMonitors.containsKey(sessionID)) {
            Log.debug("Session: " + sessionID + " already shutdown, skipping");
            return;
        }

        SessionDispatcher sessionDispatcher = lookupSessionDispatcher(context);
        //shutdown session bound server
        try {
            sessionDispatcher.sessionShutdown(session);
        } catch (Exception e) {
            Log.error(e);
        }

        synchronized (SessionMonitors) {
            try {
                sessionDispatcher.sessionDestroy(session);
            } catch (Exception e) {
                Log.error(e);
            }
            //ICE-3189 - do this before invalidating the session
            SessionMonitors.remove(sessionID);
        }
    }

    //Exposing MainSessionBoundServlet for Tomcat 6 Ajax Push
    public static PseudoServlet getSingletonSessionServer(final HttpSession session, ServletContext context) {
        return lookupSessionDispatcher(context).lookupServer(session);
    }

    public static PseudoServlet getSingletonSessionServer(final String sessionId, Map applicationMap) {
        return lookupSessionDispatcher(applicationMap).lookupServer(sessionId);
    }

    public static PseudoServlet getSingletonSessionServer(final String sessionId, final ServletContext servletContext) {
        return lookupSessionDispatcher(servletContext).lookupServer(sessionId);
    }

    public static class Listener implements ServletContextListener, HttpSessionListener {
        private boolean run = true;

        public void contextInitialized(ServletContextEvent servletContextEvent) {
            try {
                Thread monitor = new Thread("Session Monitor") {
                    public void run() {
                        while (run) {
                            try {
                                // Iterate over the session monitors using a copying iterator
                                Iterator iterator = new ArrayList(SessionMonitors.values()).iterator();
                                while (iterator.hasNext()) {
                                    final Monitor sessionMonitor = (Monitor) iterator.next();
                                    sessionMonitor.shutdownIfExpired();
                                    ThreadLocalUtility.checkThreadLocals(ThreadLocalUtility.EXITING_SESSION_MONITOR);
                                }

                                Thread.sleep(10000);
                            } catch (InterruptedException e) {
                                //ignore interrupts
                            }
                        }
                    }
                };
                monitor.setDaemon(true);
                monitor.start();
            } catch (Exception e) {
                Log.error("Unable to initialize Session Monitor ", e);
            }
        }

        public void contextDestroyed(ServletContextEvent servletContextEvent) {
            run = false;
        }

        public void sessionCreated(HttpSessionEvent event) {
        }

        public void sessionDestroyed(HttpSessionEvent event) {
            HttpSession session = event.getSession();
            notifySessionShutdown(session, session.getServletContext());
        }
    }

    private void associateSessionDispatcher(ServletContext context) {
        context.setAttribute(SessionDispatcher.class.getName(), this);
    }

    private static SessionDispatcher lookupSessionDispatcher(ServletContext context) {
        return (SessionDispatcher) context.getAttribute(SessionDispatcher.class.getName());
    }

    private static SessionDispatcher lookupSessionDispatcher(Map applicationMap) {
        return (SessionDispatcher) applicationMap.get(SessionDispatcher.class.getName());
    }

    public static class Monitor implements Externalizable {
        private final String POSITIVE_SESSION_TIMEOUT = "positive_session_timeout";
        private Set contexts = new HashSet();
        private HttpSession session;
        private long lastAccess;

        private Monitor(HttpSession session) {
            this.session = session;
            this.lastAccess = session.getLastAccessedTime();
            session.setAttribute(Monitor.class.getName(), this);
        }

        public static Monitor lookupSessionMonitor(HttpSession session) {
            return (Monitor) session.getAttribute(Monitor.class.getName());
        }

        public void touchSession() {
            lastAccess = System.currentTimeMillis();
        }

        public Date expiresBy() {
            return new Date(lastAccess + (session.getMaxInactiveInterval() * 1000));
        }

        public boolean isExpired() {

            long elapsedInterval = System.currentTimeMillis() - lastAccess;
            try {
                int maxInterval = session.getMaxInactiveInterval();
                // 4496 return true if session is already expired
                Object o = session.getAttribute(POSITIVE_SESSION_TIMEOUT);

                // Try to reset the max session timeout if it is -1 from a Failover on Tomcat...
                // But if it was originally negative, it should stay that way.
                if (maxInterval > 0) {
                    if (o == null) {
                        session.setAttribute(POSITIVE_SESSION_TIMEOUT, new Integer(maxInterval));
                    }
                } else {
                    if (o != null) {
                        maxInterval = ((Integer) o).intValue();
                        session.setMaxInactiveInterval(maxInterval);
                    }
                }

                //a negative time indicates the session should never timeout
                if (maxInterval < 0) {
                    return false;
                } else {
                    return elapsedInterval + 15000 > maxInterval * 1000;
                }
            } catch (Exception e) {
                return true;
            }
        }

        public void shutdown() {
            //notify all the contexts associated to this monitored session
            Iterator i = contexts.iterator();
            while (i.hasNext()) {
                ServletContext context = (ServletContext) i.next();
                notifySessionShutdown(session, context);
            }
            try {
                session.invalidate();
            } catch (IllegalStateException e) {
                Log.info("Session already invalidated.");
            }
        }

        public void shutdownIfExpired() {
            if (isExpired()) {
                shutdown();
            }
        }

        public void addInSessionContext(ServletContext context) {
            contexts.add(context);
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            //ignore
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            //ignore
        }

        public Monitor() {
            //ignore
        }
    }
}
