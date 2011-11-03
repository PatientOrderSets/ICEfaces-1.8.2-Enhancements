package com.icesoft.faces.context;

import com.icesoft.faces.env.Authorization;
import com.icesoft.faces.util.event.servlet.ContextEventRepeater;
import com.icesoft.faces.webapp.command.Command;
import com.icesoft.faces.webapp.command.CommandQueue;
import com.icesoft.faces.webapp.command.NOOP;
import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.Response;
import com.icesoft.faces.webapp.http.common.ResponseHandler;
import com.icesoft.faces.webapp.http.common.standard.NoCacheContentHandler;
import com.icesoft.faces.webapp.http.core.LifecycleExecutor;
import com.icesoft.faces.webapp.http.core.ResourceDispatcher;
import com.icesoft.faces.webapp.http.core.ViewQueue;
import com.icesoft.faces.webapp.http.servlet.SessionDispatcher;
import com.icesoft.faces.webapp.parser.ImplementationUtil;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesState;
import com.icesoft.faces.facelets.FaceletsUIDebug;
import com.icesoft.util.SeamUtilities;
import edu.emory.mathcs.backport.java.util.concurrent.locks.ReentrantLock;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpSession;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class View implements CommandQueue {

    public static final String ICEFACES_STATE_MAPS = "icefaces.state.maps";
    private static final Log Log = LogFactory.getLog(View.class);
    private static final NOOP NOOP = new NOOP();
    private static final Runnable DoNothing = new Runnable() {
        public void run() {
        }
    };
    private final Page lifecycleExecutedPage = new Page() {
        private String lastPath;

        private final ResponseHandler lifecycleResponseHandler = new NoCacheContentHandler("text/html", "UTF-8") {
            public void respond(Response response) throws Exception {
                super.respond(response);
                facesContext.switchToNormalMode();
                LifecycleExecutor.getLifecycleExecutor(facesContext).apply(facesContext);
                facesContext.switchToPushMode();
            }
        };

        public void serve(Request request) throws Exception {
            if (FaceletsUIDebug.handleRequest(session, request)) {
                return;
            }
            String path = request.getURI().getPath();
            boolean reloded = path.equals(lastPath);
            lastPath = path;
            //reuse FacesContext on reload -- this preserves the ViewRoot in case forward navigation rules were executed           
            if (reloded && !SeamUtilities.isSeamEnvironment()) {
                facesContext.reload(request);
            } else {
                if (facesContext != null) {
                    facesContext.dispose();
                }
                if (ImplementationUtil.isJSF2()) {
                    Class bfc2Class = Class.forName("org.icefaces.x.context.BridgeFacesContext2");
                    Constructor bfc2Constructor = bfc2Class.getConstructors()[0];
                    facesContext = (BridgeFacesContext) bfc2Constructor.newInstance(new Object[]{request, viewIdentifier, sessionID, View.this, configuration, resourceDispatcher, sessionMonitor, blockingRequestHandlerContext, authorization});
                } else {
                    facesContext = new BridgeFacesContext(request, viewIdentifier, sessionID, View.this, configuration, resourceDispatcher, sessionMonitor, blockingRequestHandlerContext, authorization);
                }
            }
            makeCurrent();
            try {
                request.respondWith(lifecycleResponseHandler);
            } catch (Exception e)  {
                String viewID = "Unknown View"; 
                try {
                    viewID = facesContext.getViewRoot().getViewId();
                } catch (NullPointerException npe)  { }
                Log.error("Exception occured during rendering on " + 
                        request.getURI() + " [" + viewID + "]", e);
                throw e;
            }
        }
    };
    private Page page = lifecycleExecutedPage;
    private final ReentrantLock queueLock = new ReentrantLock();
    private final ReentrantLock lifecycleLock = new ReentrantLock();
    private BridgeFacesContext facesContext;
    private PersistentFacesState persistentFacesState;
    private Command currentCommand = NOOP;
    private final String viewIdentifier;
    private final Collection viewListeners = new ArrayList();
    private final String sessionID;
    private final HttpSession session;
    private final Configuration configuration;
    private final SessionDispatcher.Monitor sessionMonitor;
    private final ResourceDispatcher resourceDispatcher;
    private Runnable dispose;
    private final ViewQueue allServedViews;
    private String blockingRequestHandlerContext;
    private Authorization authorization;
    private final ArrayList onReleaseListeners = new ArrayList();

    public View(final String viewIdentifier, final String sessionID, final HttpSession session, final ViewQueue allServedViews, final Configuration configuration, final SessionDispatcher.Monitor sessionMonitor, final ResourceDispatcher resourceDispatcher, final String blockingRequestHandlerContext, final Authorization authorization) throws Exception {
        this.sessionID = sessionID;
        this.session = session;
        this.configuration = configuration;
        this.viewIdentifier = viewIdentifier;
        this.sessionMonitor = sessionMonitor;
        this.resourceDispatcher = resourceDispatcher;
        this.allServedViews = allServedViews;
        this.blockingRequestHandlerContext = blockingRequestHandlerContext;
        this.authorization = authorization;
        this.persistentFacesState = new PersistentFacesState(this, viewListeners, configuration);
        ContextEventRepeater.viewNumberRetrieved(session, sessionID, Integer.parseInt(viewIdentifier));
        //fail fast if environment cannot be detected
        dispose = new Runnable() {
            public void run() {
                //dispose view only once
                dispose = DoNothing;
                Log.debug("Disposing " + this);
                installThreadLocals();
                notifyViewDisposal();
                releaseAll();
                releaseLifecycleLockUnconditionally();
                persistentFacesState.dispose();
                facesContext.dispose();
                allServedViews.remove(viewIdentifier);
            }
        };
        Log.debug("Created " + this);
    }

    //this is the "postback" request
    public void processPostback(Request request) throws Exception {
        acquireLifecycleLock();
        facesContext.processPostback(request);
        makeCurrent();
    }

    //this is the page load request
    public void servePage(Request request) throws Exception {
        try {
            acquireLifecycleLock();
            page.serve(request);
        } catch (Throwable t)  {
            Log.error("Problem encountered during View.servePage ", t);
            throw new Exception(t);
        }
    }

    public void put(Command command) {
        queueLock.lock();
        try {
            currentCommand = currentCommand.coalesceWithNext(command);
        } finally {
            queueLock.unlock();
        }
        try {
            allServedViews.put(viewIdentifier);
        } catch (InterruptedException e) {
            Log.warn("Failed to queue updated view", e);
        }
    }

    public Command take() {
        Command command = null;
        queueLock.lock();
        try {
            command = currentCommand;
            currentCommand = NOOP;
        } finally {
            queueLock.unlock();
        }
        return command;
    }

    public void release() {
        try {
            releaseAll();
            notifyRelease();
        } finally {
            releaseLifecycleLock();
        }
    }

    private void releaseAll() {
        if (facesContext != null) {
            facesContext.release();
        }
        if (persistentFacesState != null) {
            persistentFacesState.release();
        }
        if (facesContext != null) {
            ((BridgeExternalContext) facesContext.getExternalContext()).release();
        }
    }

    public BridgeFacesContext getFacesContext() {
        return facesContext;
    }

    public PersistentFacesState getPersistentFacesState() {
        return persistentFacesState;
    }

    public void dispose() {
        try {
            acquireLifecycleLock();
            dispose.run();
            ContextEventRepeater.viewNumberDisposed(
                    facesContext.getExternalContext().getSession(false),
                    sessionID,
                    Integer.parseInt(viewIdentifier));
        } finally {
            releaseLifecycleLockUnconditionally();
        }
    }

    public void installThreadLocals() {
        persistentFacesState.setCurrentInstance();
        facesContext.setCurrentInstance();
    }

    public void acquireLifecycleLock() {
        if (!lifecycleLock.isHeldByCurrentThread()) {
            lifecycleLock.lock();
        }
    }

    public void releaseLifecycleLock() {
        lifecycleLock.lock();
        //release all locks corresponding to current thread!
        releaseLifecycleLockUnconditionally();
    }

    private void releaseLifecycleLockUnconditionally() {
        while (lifecycleLock.getHoldCount() > 0) {
            lifecycleLock.unlock();
        }
    }

    public String toString() {
        return "View[" + sessionID + ":" + viewIdentifier + "]";
    }

    void preparePage(final ResponseHandler handler) {
        page = new Page() {
            public void serve(Request request) throws Exception {
                request.respondWith(handler);
                page = lifecycleExecutedPage;
            }
        };
    }

    private void makeCurrent() throws Exception {
        acquireLifecycleLock();
        installThreadLocals();
        facesContext.injectBundles();
        facesContext.applyBrowserDOMChanges();
    }

    private void notifyViewDisposal() {
        Iterator i = viewListeners.iterator();
        while (i.hasNext()) {
            try {
                ViewListener listener = (ViewListener) i.next();
                listener.viewDisposed();
            } catch (Throwable t) {
                Log.warn("Failed to invoke view listener", t);
            }
        }
        // Clean up View state maps on View disposal
        Map sessionMap = facesContext.getExternalContext().getSessionMap();
        try {
            Map m = (Map) sessionMap.get(ICEFACES_STATE_MAPS);
            if (m != null) {
                m.remove(facesContext.getViewNumber());
            }
        } catch (Exception e) {
            Log.error("Exception cleaning up State Saving Map: " + e);
        }
    }

    private void notifyRelease() {
        Iterator i = onReleaseListeners.iterator();
        while (i.hasNext()) {
            Runnable runnable = (Runnable) i.next();
            runnable.run();
        }
    }

    public void onRelease(Runnable runnable) {
        onReleaseListeners.add(runnable);
    }

    private interface Page {
        void serve(Request request) throws Exception;
    }
}
