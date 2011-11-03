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

package com.icesoft.faces.async.render;

import com.icesoft.faces.webapp.http.common.Configuration;
import edu.emory.mathcs.backport.java.util.concurrent.ScheduledThreadPoolExecutor;
import edu.emory.mathcs.backport.java.util.concurrent.ThreadPoolExecutor;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The RenderHub is designed to handle all server-side rendering calls. Although
 * it can be created programmatically, it is recommended that the application
 * developer use a {@link RenderManager} instance for all rendering duties and
 * for creating named {@link GroupAsyncRenderer)s.  The RenderManager creates
 * and uses its own RenderHub for all rendering.
 * <p/>
 * A single RenderHub should handle all server-side rendering duties in a
 * thread-safe and efficient manner.  It uses a specialized queue to hold {@link
 * Renderable} instances and a thread pool to render each Renderable on the
 * queue.  The queue will only keep a single entry of any particular Renderable.
 * Subsequent Renderables that are offered to the queue for rendering are
 * discarded since any pending render call will update the client to the latest
 * state.  This coalescing of render calls makes it safer and more efficient for
 * the application developer to add rendering into their application.
 *
 * @author ICEsoft Technologies, Inc.
 * @see RenderManager
 */
public class RenderHub {

    private static Log log = LogFactory.getLog(RenderHub.class);

    /**
     * The specialized thread pool used to execute render calls on Renderable
     * instances.  It uses the supplied settings for defaults.
     */
    private ThreadPoolExecutor renderService;
    private int corePoolSize;
    private int maxPoolSize;
    private long keepAliveTime;
    private int renderQueueCapacity;

    /**
     * The specialized thread pool used to execute render calls at some future
     * time.  The RenderHub makes this available to Renderers that need this
     * ability.
     *
     * @see IntervalRenderer, DelayRenderer
     */
    private ScheduledThreadPoolExecutor scheduledService;
    private int schedulePoolSize = 5;

    /**
     * Used by the ThreadPoolExector as a callback for rejected Runnable
     * executions.
     */
    private RejectionHandler rejectionHandler;

    /**
     * Used by the ThreadPoolExecutor to create its thread pool.
     */
    private RenderThreadFactory threadFactory;

    /**
     * Public constructor.  Although it possible for developers to construct and
     * use their own RenderHub, it is highly recommended that a RenderManager be
     * used.  The RenderManager creates and uses it's own internal RenderHub.
     *
     * @param configuration
     */
    public RenderHub(Configuration configuration) {
        corePoolSize = configuration.getAttributeAsInteger("corePoolSize", 10);
        maxPoolSize = configuration.getAttributeAsInteger("maxPoolSize", 15);
        keepAliveTime = configuration.getAttributeAsLong("keepAliveTime", 300000);
        renderQueueCapacity = configuration.getAttributeAsInteger("renderQueueCapacity", 1000);

        rejectionHandler = new RejectionHandler();
        threadFactory = new RenderThreadFactory();
    }

    /**
     * Get the starting size of the core thread pool.
     *
     * @return The starting size of the core thread pool.
     */
    public int getCorePoolSize() {
        return corePoolSize;
    }

    /**
     * Set the thread pool size of the core render service.  The default is 10.
     * This number will need to be adjusted based on the characteristics of the
     * application.  Note that increasing the number of threads past a certain
     * number (based on OS, JVM, etc) can actually decrease performance as
     * thread context switching becomes a burden.
     *
     * @param corePoolSize The number of threads to dedicate to the scheduled
     *                     service thread pool.
     */
    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
        //todo: do we need to dynamically re-configure services
        resetCoreService();
        resetScheduledService();
    }

    /**
     * Get the maximum size of the core thread pool.
     *
     * @return The maximum size of the core thread pool.
     */
    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    /**
     * Set the maximum thread pool size of the core render service.  The default
     * is 15. This number will need to be adjusted based on the characteristics
     * of the application.  Note that increasing the number of threads past a
     * certain number (based on OS, JVM, etc) can actually decrease performance
     * as thread context switching becomes a burden.
     *
     * @param maxPoolSize The maximum number of threads to dedicate to the core
     *                    service thread pool.
     */
    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
        //todo: do we need to dynamicaly re-configure service
        resetCoreService();
    }

    /**
     * Get the keep alive time of threads above the core number.  The number of
     * threads that are created past the core number (up to the maximum number)
     * are kept alive until they are idle for the keep alive time.
     *
     * @return The keep alive time for threads created past the core number.
     */
    public long getKeepAliveTime() {
        return keepAliveTime;
    }

    /**
     * Set the amount of idle time to keep threads created above the core size.
     * The default is 300000 ms.
     *
     * @param keepAliveTime The idle time in ms to keep additional threads
     *                      alive.
     */
    public void setKeepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
        //todo: do we need to dynamically re-configure service
        resetCoreService();
    }

    /**
     * Get the capacity of the core render service queue.
     *
     * @return The capacity of the core render service queue.
     */
    public int getRenderQueueCapacity() {
        return renderQueueCapacity;
    }

    public void setRenderQueueCapacity(int renderQueueCapacity) {
        this.renderQueueCapacity = renderQueueCapacity;
        //todo: do we need to dynamically re-configure service
        resetCoreService();
    }

    /**
     * The prime responsibility of the RenderHub is to perform a render call on
     * the submitted Renderable.  Each call to this method is submitted and, if
     * not already in the queue, placed on the queue to be rendered.  The thread
     * pool is created here if does not yet exist using whatever configuration
     * values have been set.
     *
     * @param renderable The Renderable instance to add to the rendering queue.
     */
    public void requestRender(Renderable renderable) {
        if (renderService == null) {
            createCoreService();
        }
        renderService.execute(new RunnableRender(renderable));
    }

    /**
     * Method for creating the thread pool/queue service.  Currently we use a
     * JDK 1.4.x backport of the JDK 1.5.x concurrency utilities.
     */
    private synchronized void createCoreService() {

        SingleEntryQueue queue = new SingleEntryQueue(renderQueueCapacity);

        renderService = new ThreadPoolExecutor(corePoolSize,
                maxPoolSize,
                keepAliveTime,
                TimeUnit.MILLISECONDS,
                queue,
                threadFactory,
                rejectionHandler
        );

        if (log.isInfoEnabled()) {
            log.info("core render service created:" +
                    "\n  core pool size : " + corePoolSize +
                    "\n  max pool size  : " + maxPoolSize +
                    "\n  keep alive time: " + keepAliveTime);
        }
    }

    /**
     * As a secondary responsibility, the RenderHub also maintains a thread pool
     * for render calls that should occur in the future.  This service is used
     * by renderers such as the {@link IntervalRenderer} and {@link
     * DelayRenderer} to delay the eventual render calls.  All the rendering is
     * still handled by the RenderHubs core thread pool/queue.  The scheduled
     * service is used to delay when that occurs. Having this supplied by the
     * RenderHub ensures that there is only one.
     *
     * @return A scheduling service.
     */
    public ScheduledThreadPoolExecutor getScheduledService() {
        if (scheduledService == null) {
            createScheduledService();
        }
        return scheduledService;
    }

    protected synchronized void createScheduledService() {

        scheduledService = new ScheduledThreadPoolExecutor(corePoolSize,
                threadFactory,
                rejectionHandler
        );
        if (log.isInfoEnabled()) {
            log.info("scheduled render service created:" +
                    "\n  core pool size : " + schedulePoolSize);
        }
    }

    /**
     * Get the thread pool size of the scheduled render service.
     *
     * @return the size of the schedule service thread pool
     */
    public int getSchedulePoolSize() {
        return schedulePoolSize;
    }

    /**
     * Set the thread pool size of the scheduled render service.  The default is
     * 5 and unless you have a lot of renderers that use the scheduling service,
     * it should really be more than enough.
     *
     * @param schedulePoolSize The number of threads to dedicate to the
     *                         scheduled service thread pool.
     */
    public void setSchedulePoolSize(int schedulePoolSize) {
        this.schedulePoolSize = schedulePoolSize;
    }

    /**
     * Cleanly disposes of the RenderHub's resources.  Used by the RenderManager
     * when the application shuts down.
     */
    public void dispose() {
        if (renderService != null) {
            renderService.shutdown();
            renderService = null;
        }

        if (scheduledService != null) {
            scheduledService.shutdown();
            scheduledService = null;
        }
    }

    private synchronized void resetCoreService() {
        if (renderService != null) {
            renderService.shutdown();
            createCoreService();
        }
    }

    private synchronized void resetScheduledService() {
        if (scheduledService != null) {
            scheduledService.shutdown();
            createScheduledService();
        }
    }
}
