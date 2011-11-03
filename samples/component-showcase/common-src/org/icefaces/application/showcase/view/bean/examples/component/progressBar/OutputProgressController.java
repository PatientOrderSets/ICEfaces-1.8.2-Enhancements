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
package org.icefaces.application.showcase.view.bean.examples.component.progressBar;

import com.icesoft.faces.async.render.RenderManager;
import com.icesoft.faces.async.render.Renderable;
import com.icesoft.faces.context.DisposableBean;
import com.icesoft.faces.webapp.xmlhttp.FatalRenderingException;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesState;
import com.icesoft.faces.webapp.xmlhttp.RenderingException;
import com.icesoft.faces.webapp.xmlhttp.TransientRenderingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import edu.emory.mathcs.backport.java.util.concurrent.ThreadPoolExecutor;
import edu.emory.mathcs.backport.java.util.concurrent.LinkedBlockingQueue;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;

import java.io.Serializable;

/**
 * <p>The OutputProgressController is responsible for handling all user actions
 * for the OutputProgress demo.  This includes the the starting a long
 * running process to show how the progress bar can be used to monitor a
 * process on the server. </p>
 * <p>This class is especially interesting shows a usage scenario for the
 * Renderable and ServletContextListener interfaces.  The Renderable
 * interface is used for the server side pushes needed to update the
 * outputProgress state.  The ServletContextListener is used to showdown
 * the thread pool when the server shuts down. </p>
 *
 * @see com.icesoft.faces.async.render.RenderManager
 * @see javax.servlet.ServletContextListener
 * @since 1.7
 */
public class OutputProgressController
        implements Renderable, ServletContextListener, DisposableBean, Serializable {

    public static final Log log = LogFactory.getLog(OutputProgressController.class);

    // long running thread will sleep 10 times for this duration.
    public static final long PROCCESS_SLEEP_LENGTH = 300;

    // A thread pool is used to make this demo a little more scalable then
    // just creating a new thread for each user.
    protected static ThreadPoolExecutor longRunningTaskThreadPool =
            new ThreadPoolExecutor(5, 15, 30, TimeUnit.SECONDS,
                    new LinkedBlockingQueue(20));

    // render manager for the application, uses session id for on demand
    // render group.
    private RenderManager renderManager;
    private PersistentFacesState persistentFacesState;
    private String sessionId;

    // Model where we store the dynamic properties associated with outputProgress
    private OutputProgressModel outputProgressModel;

    /**
     * Default constructor where a reference to the PersistentFacesState is made
     * as well as the creation of the OutputProgressModel.  A reference to
     * PersistentFacesState is needed when implementing the Renderable
     * interface.
     */
    public OutputProgressController() {
        persistentFacesState = PersistentFacesState.getInstance();
        outputProgressModel = new OutputProgressModel();
    }

    /**
     * A long running task is started when this method is called.  Actually not
     * that long around 10 seconds.   This long process {@link LongOperationRunner}
     * is responsible for updating the percent complete in the model class.
     *
     * @param event
     */
    public void startLongProcress(ActionEvent event) {

        longRunningTaskThreadPool.execute(
                    new LongOperationRunner(outputProgressModel,
                            persistentFacesState));
    }

    /**
     * Callback method that is called if any exception occurs during an attempt
     * to render this Renderable.
     * <p/>
     * It is up to the application developer to implement appropriate policy
     * when a RenderingException occurs.  Different policies might be
     * appropriate based on the severity of the exception.  For example, if the
     * exception is fatal (the session has expired), no further attempts should
     * be made to render this Renderable and the application may want to remove
     * the Renderable from some or all of the
     * {@link com.icesoft.faces.async.render.GroupAsyncRenderer}s it
     * belongs to. If it is a transient exception (like a client's connection is
     * temporarily unavailable) then the application has the option of removing
     * the Renderable from GroupRenderers or leaving them and allowing another
     * render call to be attempted.
     *
     * @param renderingException The exception that occurred when attempting to
     *                           render this Renderable.
     */
    public void renderingException(RenderingException renderingException) {
        if (log.isTraceEnabled() &&
                renderingException instanceof TransientRenderingException) {
            log.trace("OutputProgressController Transient Rendering exception:", renderingException);
        } else if (renderingException instanceof FatalRenderingException) {
            if (log.isTraceEnabled()) {
                log.trace("OutputProgressController Fatal rendering exception: ", renderingException);
            }
            renderManager.getOnDemandRenderer(sessionId).remove(this);
            renderManager.getOnDemandRenderer(sessionId).dispose();
        }
    }

    /**
     * Return the reference to the
     * {@link com.icesoft.faces.webapp.xmlhttp.PersistentFacesState
     * PersistentFacesState} associated with this Renderable.
     * <p/>
     * The typical (and recommended usage) is to get and hold a reference to the
     * PersistentFacesState in the constructor of your managed bean and return
     * that reference from this method.
     *
     * @return the PersistentFacesState associated with this Renderable
     */
    public PersistentFacesState getState() {
        return persistentFacesState;
    }

    /**
     * Sets the application render manager reference and creates a new on
     * demand render for this session id.
     *
     * @param renderManager RenderManager reference for this application.
     *                      Usually called via the faces-config.xml using
     *                      chaining.
     */
    public void setRenderManager(RenderManager renderManager) {
        this.renderManager = renderManager;

        // Casting to HttpSession ruins it for portlets, in this case we only
        // need a unique reference so we use the object hash
        sessionId = FacesContext.getCurrentInstance().getExternalContext()
                .getSession(false).toString();
        renderManager.getOnDemandRenderer(sessionId).add(this);
    }

    /**
     * Gets the outputProgressModel for this instance.
     *
     * @return OutputProgressModel which contains the state of various
     *         dynamic properties that are manipulated by this example.
     */
    public OutputProgressModel getOutputProgressModel() {
        return outputProgressModel;
    }

    /**
     * Called when the Servlet Context is created.
     * @param event servlet context event.
     */
    public void contextInitialized(ServletContextEvent event) {
    }

    /**
     * Called when the Servlet Context is about to be destroyed.  This method
     * calls shutdownNow on the thread pool.
     * @param event servlet context event.
     */
    public void contextDestroyed(ServletContextEvent event) {
        if (longRunningTaskThreadPool != null) {
            longRunningTaskThreadPool.shutdownNow();
            if (log.isDebugEnabled()) {
                log.debug("Shutting down thread pool...");
            }
        }
    }

    /**
     * Utility class to represent some server process that we want to monitor
     * using ouputProgress and server push.
     */
    protected class LongOperationRunner implements Runnable {
        PersistentFacesState state = null;
        private OutputProgressModel ouputProgressModel;

        public LongOperationRunner(OutputProgressModel ouputProgressModel,
                                   PersistentFacesState state) {
            this.state = state;
            this.ouputProgressModel = ouputProgressModel;
        }

        /**
         * Routine that takes time and updates percentage as it runs.
         */
        public void run() {

            ouputProgressModel.setPogressStarted(true);
            try {
                for (int i = 0; i <= 100; i += 10) {
                    // pause the thread
                    Thread.sleep(PROCCESS_SLEEP_LENGTH);
                    // update the percent value
                    ouputProgressModel.setPercentComplete(i);
                    // call a render to update the component state
                    try {
                        renderManager.getOnDemandRenderer(sessionId).requestRender();
                    } catch (IllegalStateException e) {
                        log.error("Error running progress thread.", e);
                    }
                }
            }
            catch (InterruptedException e) {
                log.error("Error running progress thread.", e);
            }
            ouputProgressModel.setPogressStarted(false);
            renderManager.getOnDemandRenderer(sessionId).requestRender();
        }
    }

    /**
     * Dispose callback called due to a view closing or session
     * invalidation/timeout
     */
	public void dispose() throws Exception {
        if (log.isTraceEnabled()) {
            log.trace("OutputProgressController dispose OnDemandRenderer for session: " + sessionId);
        }
        renderManager.getOnDemandRenderer(sessionId).remove(this);
		renderManager.getOnDemandRenderer(sessionId).dispose();
	}


}
