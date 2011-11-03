/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * "The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations under
 * the License.
 *
 * The Original Code is ICEfaces 1.5 open source software code, released
 * November 5, 2006. The Initial Developer of the Original Code is ICEsoft
 * Technologies Canada, Corp. Portions created by ICEsoft are Copyright (C)
 * 2004-2006 ICEsoft Technologies Canada, Corp. All Rights Reserved.
 *
 * Contributor(s): _____________________.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"
 * License), in which case the provisions of the LGPL License are
 * applicable instead of those above. If you wish to allow use of your
 * version of this file only under the terms of the LGPL License and not to
 * allow others to use your version of this file under the MPL, indicate
 * your decision by deleting the provisions above and replace them with
 * the notice and other provisions required by the LGPL License. If you do
 * not delete the provisions above, a recipient may use your version of
 * this file under either the MPL or the LGPL License."
 *
 */

package com.icesoft.faces.webapp.xmlhttp;

import com.icesoft.faces.context.BridgeExternalContext;
import com.icesoft.faces.context.BridgeFacesContext;
import com.icesoft.faces.context.View;
import com.icesoft.faces.context.ViewListener;
import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.faces.webapp.http.core.SessionExpiredException;
import com.icesoft.faces.webapp.parser.ImplementationUtil;
import com.icesoft.util.SeamUtilities;
import com.icesoft.faces.util.CoreUtils;
import edu.emory.mathcs.backport.java.util.concurrent.ExecutorService;
import edu.emory.mathcs.backport.java.util.concurrent.Executors;
import edu.emory.mathcs.backport.java.util.concurrent.ThreadFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.portlet.PortletSession;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * The {@link PersistentFacesState} class allows an application to initiate
 * rendering asynchronously and independently of user interaction.
 * <p/>
 * Typical use is to obtain a {@link PersistentFacesState} instance in a managed
 * bean constructor and then use that instance for any relevant rendering
 * requests.
 * <p/>
 * Applications should obtain the <code>PersistentFacesState</code> instance
 * using the public static getInstance() method.  The recommended approach is to
 * call this method from a mangaged-bean constructor and use the instance
 * obtained for any {@link #render} requests.
 * <p/>
 * Application writers that make calls to the <code>execute</code> and
 * <code>render</code> methods should be aware of the potential for deadlock
 * conditions if this code is coupled with a third party framework
 * (eg. Spring Framework) that does its own resource locking.
 */
public class PersistentFacesState implements Serializable {
    private static final Log log = LogFactory.getLog(PersistentFacesState.class);
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor(new DaemonThreadFactory());
    private static final InheritableThreadLocal localInstance = new InheritableThreadLocal();

    private final ClassLoader renderableClassLoader;
    private final Lifecycle lifecycle;
    private final boolean synchronousMode;
    private final Collection viewListeners;
    private View view;
    private boolean disposed;
    // For server push, the JSF state saving key generated after each render must
    // be preserved for the next artificial lifecycle so we can restore state
    private String stateRestorationId;

    public PersistentFacesState(View view, Collection viewListeners, Configuration configuration) {
        //JIRA case ICE-1365
        //Save a reference to the web app classloader so that server-side
        //render requests work regardless of how they are originated.
        this.renderableClassLoader = Thread.currentThread().getContextClassLoader();
        LifecycleFactory factory = (LifecycleFactory) FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
        this.lifecycle = factory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
        this.view = view;
        this.viewListeners = viewListeners;
        this.synchronousMode = configuration.getAttributeAsBoolean("synchronousUpdate", false);
        this.setCurrentInstance();
    }

    public void dispose() {
        disposed = true;
    }

    public void setCurrentInstance() {
        localInstance.set(this);
    }

    public static boolean isThreadLocalNull() {
        return localInstance.get() == null;
    }

    /**
     * Obtain the <code>PersistentFacesState</code> instance appropriate for the
     * current context.  This is managed through InheritableThreadLocal
     * variables.  The recommended approach is to call this method from a
     * mangaged-bean constructor and use the instance obtained for any {@link
     * #render} requests.
     *
     * @return the PersistentFacesState appropriate for the calling Thread
     */
    public static PersistentFacesState getInstance() {
        return (PersistentFacesState) localInstance.get();
    }

    /**
     * Obtain the {@link PersistentFacesState} instance keyed by viewNumber from
     * the specified sessionMap. This API is not intended for application use.
     *
     * @param sessionMap session-scope parameters
     * @return the PersistentFacesState
     * @deprecated
     */
    public static PersistentFacesState getInstance(Map sessionMap) {
        return getInstance();
    }

    /**
     * Return the FacesContext associated with this instance.
     *
     * @return the FacesContext for this instance
     */
    public FacesContext getFacesContext() {
        return view.getFacesContext();
    }

    /**
     * Returns whether the current view is in synchronous mode, which means
     * that server push is not available.
     *
     * @return If in synchronous mode
     */
    public boolean isSynchronousMode() {
        return synchronousMode;
    }

    /**
     * Render the view associated with this <code>PersistentFacesState</code>.
     * The user's browser will be immediately updated with any changes.
     */
    public void render() throws RenderingException {
        failIfDisposed();
        warnIfSynchronous();
        BridgeFacesContext facesContext = view.getFacesContext();
        try {
            view.acquireLifecycleLock();
            view.installThreadLocals();
            //todo: why is this necessary? is this the best place to have this call?
            facesContext.setFocusId("");
            lifecycle.render(facesContext);
        } catch (Exception e) {
            String viewID = "Unknown View"; 
            try {
                viewID = facesContext.getViewRoot().getViewId();
            } catch (NullPointerException npe)  { }
            log.error("Exception occured during execute push on " + viewID, e);
            throwRenderingException(e);
        } finally {
            facesContext.release();
            release();
            view.releaseLifecycleLock();
        }
    }

    /**
     * Render the view associated with this <code>PersistentFacesState</code>.
     * This takes place on a separate thread to guard against potential deadlock
     * from calling {@link #render} during view rendering.
     */
    public void renderLater() {
        warnIfSynchronous();
        executorService.execute(new RenderRunner());
    }

    public void renderLater(long miliseconds) {
        warnIfSynchronous();
        executorService.execute(new RenderRunner(miliseconds));
    }

    /**
     * @param setup    Runnable to run, in the proper thread context, before
     *                 doing the JSF lifecycle
     * @param warnSync Whether warn if in synchronous mode
     */
    public void renderLater(Runnable setup, boolean warnSync) {
        if (warnSync) {
            warnIfSynchronous();
        }
        executorService.execute(new RenderRunner(setup));
    }

    /**
     * Execute  the view associated with this <code>PersistentFacesState</code>.
     * This is typically followed immediatly by a call to
     * {@link PersistentFacesState#render}.
     * <p/>
     * This method obtains and releases the monitor on the FacesContext object.
     * If starting a JSF lifecycle causes 3rd party frameworks to perform locking
     * of their resources, releasing this monitor between the call to this method
     * and the call to {@link PersistentFacesState#render} can allow deadlocks
     * to occur. Use {@link PersistentFacesState#executeAndRender} instead
     *
     * @deprecated this method should not be exposed
     */
    public void execute() throws RenderingException {
        failIfDisposed();
        BridgeFacesContext facesContext = null;
        try {
            view.acquireLifecycleLock();
            view.installThreadLocals();
            facesContext = view.getFacesContext();

            // For JSF 1.1, with the inputFile, we need the execute phases to 
            // actually happen, which wasn't the case when the following code 
            // only ran for JSF 1.2. These are the options we have for JSF 1.1:
            // A. facesContext.renderResponse() skips the phases, so the 
            //    FileUploadPhaseListener can't work. We used to do this.
            // B. Doing nothing means that the old values that 
            //    FileUploadPhaseListener puts in the RequestParameterMap stick 
            //    around for subsequent file upload lifecycles (no problem) and 
            //    server pushes (might cause duplicate inputFile.actionListener 
            //    calls). As well as the values from the last user interaction, 
            //    which might cause problems too.
            // C. Clearing the RequestParameterMap leads to skipping the execute,
            //    so that's not sufficient.
            // D. Just doing the same thing for JSF 1.1 as JSF 1.2 seems to work

            if (true) { // if (ImplementationUtil.isJSF12()) {
                //facesContext.renderResponse() skips phase listeners
                //in JSF 1.2, so do a full execute with no stale input
                //instead
                Map requestParameterMap = facesContext.getExternalContext().getRequestParameterMap();
                requestParameterMap.clear();
                if (SeamUtilities.isSeamEnvironment()) {
                    //ICE-2990/JBSEAM-3426 must have empty requestAttributes for push to work with Seam
                    ((BridgeExternalContext) facesContext.getExternalContext()).removeSeamAttributes();
                }
                //Seam appears to need ViewState set during push
                // see below
//                requestParameterMap.put("javax.faces.ViewState", "ajaxpush");

                // If state saving is turned on, we need to insert the saved state
                // restoration key for this user into the request map for JSF.
                // Even if no state saving, inserting this key will at least
                // cause the JSF lifecycle to run, which we want to do for
                // consistency.
                String postback;
                if (ImplementationUtil.isJSFStateSaving() && stateRestorationId != null) {
                    postback = stateRestorationId;
                } else {
                    postback = "not reload";
                }

                facesContext.getExternalContext().
                        getRequestParameterMap().
                        put(BridgeExternalContext.PostBackKey, postback);

            }
            lifecycle.execute(facesContext);

        } catch (Exception e) {
            release();
            view.releaseLifecycleLock();
            String viewID = "Unknown View"; 
            try {
                viewID = facesContext.getViewRoot().getViewId();
            } catch (NullPointerException npe)  { }
            log.error("Exception occured during execute push on " + viewID, e);
            throwRenderingException(e);
        }
    }

    /**
     * Execute the JSF lifecycle (essentially calling <code> execute()</code> and
     * <code>render()</code> ) without releasing the FacesContext monitor
     * at any point between.
     *
     * @throws RenderingException if there is an exception rendering the View
     * @since 1.7
     */
    public void executeAndRender() throws RenderingException {
        view.acquireLifecycleLock();
        view.getFacesContext().injectBundles();
        CoreUtils.addAuxiliaryContexts(view.getFacesContext());
        execute();
        render();
    }

    public void setupAndExecuteAndRender() throws RenderingException {
        view.acquireLifecycleLock();
        installContextClassLoader();
        if (SeamUtilities.isSeamEnvironment()) {
            testSession();
        }
        // JIRA #1377 Call execute before render.
        // #2459 use fully synchronized version internally.
        executeAndRender();
    }

    /**
     * Redirect browser to a different URI. The user's browser will be
     * immediately redirected without any user interaction required. This appears
     * to require the page extension in the defining technology (.jxpx, .xhtml, etc)
     * rather than in the suffix replacement way ( .iface, .faces, etc ) 
     *
     * @param uri the relative or absolute URI.
     */
    public void redirectTo(final String uri) {
        warnIfSynchronous();
        executorService.execute(new Runnable()  {
            public void run()  {
                try {
                    view.acquireLifecycleLock();
                    view.installThreadLocals();
                    view.getFacesContext().getExternalContext().redirect(uri);
                } catch (Exception e) {
                    log.error("Exception during redirectTo ", e);
                    throw new RuntimeException(e);
                } finally {
                    release();
                    view.releaseLifecycleLock();
                }
            }
        });
    }

    /**
     * Navigate browser to a different page using an outcome argument to
     * select the navigation rule. The mechanism for navigation depends on
     * the navigation rule. <p>
     *
     * If there is no JSF lifecycle in progress when this method is called,
     * this method will manage one. Otherwise, it is the callers responsibility
     * to call the <code>execute</code> method before, and <code>render</code> method 
     * after this navigation method.
     *
     * @param outcome the 'from-outcome' field in the navigation rule.
     */
    public void navigateTo(String outcome) {
        warnIfSynchronous();
        try {
            BridgeFacesContext facesContext = view.getFacesContext();
            FacesContext fc = FacesContext.getCurrentInstance();
            // #4251 start a lifecycle to re-establish the ViewRoot before
            // trying navigation if a lifecycle is not already running.
            boolean manageLifecycle = ((fc == null) || (fc.getViewRoot() == null));
            if (manageLifecycle) {
                execute();
            }
            facesContext.getApplication().getNavigationHandler().
                         handleNavigation(facesContext,
                                          facesContext.getViewRoot().getViewId(),
                                          outcome);
            if (manageLifecycle) {
                render();
            } 
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Threads that used to server request/response cycles in an application
     * server are generally pulled from a pool.  Just before the thread is done
     * completing the cycle, we should clear any local instance variables to
     * ensure that they are not hanging on to any session references, otherwise
     * the session and their resources are not released.
     */
    public void release() {
        localInstance.set(null);
    }

    public void installContextClassLoader() {
        try {
            Thread.currentThread().setContextClassLoader(renderableClassLoader);
        } catch (SecurityException se) {
            log.debug("setting context class loader is not permitted", se);
        }
    }

    /**
     * @deprecated use {@link com.icesoft.faces.context.DisposableBean} interface instead
     */
    public void addViewListener(ViewListener listener) {
        if (!viewListeners.contains(listener)) {
            viewListeners.add(listener);
        }
    }

    private class RenderRunner implements Runnable {
        private final long delay;
        private Runnable setup;

        public RenderRunner() {
            delay = 0;
        }

        public RenderRunner(long miliseconds) {
            delay = miliseconds;
        }

        /**
         * @param setup Runnable to run, in the proper thread context, before
         *              doing the JSF lifecycle
         */
        public RenderRunner(Runnable setup) {
            delay = 0;
            this.setup = setup;
        }

        /**
         * <p>Not for application use. Entry point for {@link
         * PersistentFacesState#renderLater}.</p>
         */
        public void run() {
            try {
                Thread.sleep(delay);
                if (setup != null) {
                    setup.run();
                }
                setupAndExecuteAndRender();
            } catch (RenderingException e) {
                log.debug("renderLater failed ", e);
            } catch (InterruptedException e) {
                //ignore
            } catch (IllegalStateException e) {
                log.debug("renderLater failed ", e);
            }
        }
    }

    private static class DaemonThreadFactory implements ThreadFactory {
        private ThreadFactory defaultThreadFactory;

        private DaemonThreadFactory() {
            defaultThreadFactory = Executors.defaultThreadFactory();
        }

        public Thread newThread(Runnable runnable) {
            Thread thread = defaultThreadFactory.newThread(runnable);
            thread.setDaemon(true);
            return thread;
        }
    }

    /**
     * This is not a public API, but is intended for temporary use
     * by UploaderServer only.
     *
     * @deprecated
     */
    public void acquireUploadLifecycleLock() {
        view.acquireLifecycleLock();
    }

    /**
     * This is not a public API, but is intended for temporary use
     * by UploaderServer only.
     *
     * @deprecated
     */
    public void releaseUploadLifecycleLock() {
        view.releaseLifecycleLock();
    }

    /**
     * This is not a public API, but is intended for temporary use
     * by UploaderServer only.
     *
     * @deprecated
     */
    public void setAllCurrentInstances() {
        view.installThreadLocals();
    }

    private void warnIfSynchronous() {
        if (synchronousMode) {
            log.warn("Running in 'synchronous mode'. The page updates were queued but not sent.");
        }
    }

    private void testSession() throws IllegalStateException {
        Object o = view.getFacesContext().getExternalContext().getSession(false);
        if (o != null) {
            if (o instanceof HttpSession) {
                HttpSession session = (HttpSession) o;
                session.getAttributeNames();
            } else if (o instanceof PortletSession) {
                PortletSession ps = (PortletSession) o;
                ps.getAttributeNames();
            }
        }
    }

    private void fatalRenderingException() throws FatalRenderingException {
        final String message = "fatal render failure for " + view;
        log.debug(message);
        throw new FatalRenderingException(message);
    }

    private void fatalRenderingException(Exception e) throws FatalRenderingException {
        final String message = "fatal render failure for " + view;
        log.debug(message, e);
        throw new FatalRenderingException(message, e);
    }

    private void transientRenderingException(Exception e) throws TransientRenderingException {
        final String message = "transient render failure for " + view;
        log.debug(message, e);
        throw new TransientRenderingException(message, e);
    }

    private void throwRenderingException(Exception e) throws FatalRenderingException, TransientRenderingException {
        Throwable throwable = e;
        while (throwable != null) {
            if (throwable instanceof IllegalStateException || throwable instanceof SessionExpiredException) {
                fatalRenderingException(e);
            } else {
                throwable = throwable.getCause();
            }
        }
        transientRenderingException(e);
    }

    private void failIfDisposed() throws FatalRenderingException {
        if (disposed) {
            //ICE-3073 Clear threadLocal in all paths
            release();
            view.releaseLifecycleLock();
            fatalRenderingException();
        }
    }

    public String getStateRestorationId() {
        return stateRestorationId;
    }

    /**
     * JSF state saving requires a state id token written into the contents of
     * the forms in the client. For server push, however, this field has to be updated each
     * time state is serialized so that the subsequent push operation can artificially
     * insert the id into the request map for JSF code to find.
     *
     * @param stateRestorationId The state id
     */
    public void setStateRestorationId(String stateRestorationId) {
        this.stateRestorationId = stateRestorationId;
    }
}
