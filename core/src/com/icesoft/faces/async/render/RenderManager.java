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

import com.icesoft.faces.util.event.servlet.ContextEventRepeater;
import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.faces.webapp.http.servlet.ServletContextConfiguration;
import edu.emory.mathcs.backport.java.util.concurrent.ScheduledThreadPoolExecutor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * The RenderManager is the central class for developers wanting to do
 * server-initiated rendering.  The recommended use is to have a single
 * RenderManager for your application, typically configured as a
 * application-scope, managed bean.  Any class that needs to request server side
 * render calls can do so using the RenderManager.
 * <p/>
 * Server-initiated renders can be requested directly via the {@link
 * RenderManager#requestRender requestRender} method or you get a named {@link
 * GroupAsyncRenderer} and add a Renderable implementation to request and
 * receive render calls as part of a group.
 * <p/>
 * The RenderManager contains a reference to a single {@link RenderHub} that
 * uses a special queue and thread pool to optimize render calls for reliability
 * and scalability.
 */
public class RenderManager implements Disposable {
    static final int MIN = 1;
    public static final int ON_DEMAND = 2;
    public static final int INTERVAL = 3;
    public static final int DELAY = 4;
    static final int MAX = 4;

    private static RenderManager internalRenderManager;

    /**
     * No argument constructor suitable for using as a managed bean.
     */
    public RenderManager() {
        // do nothing.
    }

    /**
     * This is typically called when the application is shutting down and we
     * need to dispose of all the RenderManager's resources.  It iterates
     * through all of the named {@link GroupAsyncRenderer}s, calling dispose on
     * each of them in turn.  It then calls dispose on the {@link RenderHub}.
     * Once the RenderManager has been disposed, it can no longer be used.
     *
     * @deprecated
     */
    public void dispose() {
        // do nothing.
    }

    public BroadcastRenderer getBroadcastRenderer() {
        return
            internalRenderManager != null ?
                internalRenderManager.getBroadcastRenderer() : null;
    }

    /**
     * The DelayRenderer is a {@link GroupAsyncRenderer} that requests a
     * server-initiated render of all the Renderables in its collection.  The
     * request to render is made once at a set point in the future.
     * <p/>
     * This method returns the appropriate GroupAsyncRenderer based on the
     * renderer name. If the name is new, a new instance is created and stored
     * in the RenderManager's collection.  If the named GroupAsyncRenderer
     * already exists, and it's of the proper type, then the existing instance
     * is returned.
     *
     * @param rendererName the name of the GroupAsyncRenderer
     * @return a new or existing GroupAsyncRenderer, based on the renderer name
     */
    public DelayRenderer getDelayRenderer(final String rendererName) {
        return
            internalRenderManager != null ?
                internalRenderManager.getDelayRenderer(rendererName) : null;
    }

    public static RenderManager getInstance() {
        return internalRenderManager;
    }

    /**
     * The IntervalRenderer is a {@link GroupAsyncRenderer} that requests a
     * server-initiated render of all the Renderables in its collection.  The
     * request to render is made repeatedly at the set interval.
     * <p/>
     * This method returns the appropriate GroupAsyncRenderer based on the
     * renderer name. If the name is new, a new instance is created and stored
     * in the RenderManager's collection.  If the named GroupAsyncRenderer
     * already exists, and it's of the proper type, then the existing instance
     * is returned.
     *
     * @param rendererName the name of the GroupAsyncRenderer
     * @return a new or existing GroupAsyncRenderer, based on the renderer name
     */
    public IntervalRenderer getIntervalRenderer(final String rendererName) {
        return
            internalRenderManager != null ?
                internalRenderManager.getIntervalRenderer(rendererName) : null;
    }

    /**
     * The OnDemandRenderer is a {@link GroupAsyncRenderer} that requests a
     * server-initiated render of all the Renderables in its collection.  The
     * request is made immediately upon request.
     * <p/>
     * This method returns the appropriate GroupAsyncRenderer based on the
     * renderer name. If the name is new, a new instance is created and stored
     * in the RenderManager's collection.  If the named GroupAsyncRenderer
     * already exists, and it's of the proper type, then the existing instance
     * is returned.
     *
     * @param rendererName the name of the GroupAsyncRenderer
     * @return a new or existing GroupAsyncRenderer, based on the renderer name
     */
    public OnDemandRenderer getOnDemandRenderer(final String rendererName) {
        return
            internalRenderManager != null ?
                internalRenderManager.getOnDemandRenderer(rendererName) : null;
    }

    /**
     * Returns the named instance of an AsyncRenderer if it already exists
     * otherwise it returns null.
     *
     * @param rendererName The name of the AsycRender to retrieve.
     * @return An instance of an AsyncRenderer that is associated with the
     *         provided name.
     */
    public AsyncRenderer getRenderer(final String rendererName) {
        return
            internalRenderManager != null ?
                internalRenderManager.getRenderer(rendererName) : null;
    }

    public ServletContext getServletContext() {
        return
            internalRenderManager != null ?
                internalRenderManager.getServletContext() : null;
    }

    /**
     * @deprecated Replaced by {@link #getBroadcastRenderer()}. 
     */
    public boolean isBroadcasted() {
        return
            internalRenderManager != null &&
                internalRenderManager.isBroadcasted();
    }

    /**
     * When an AsyncRenderer disposes itself, it also needs to remove itself
     * from the RenderManager's collection.
     *
     * @param renderer The Renderer to remove
     */
    public void removeRenderer(final AsyncRenderer renderer) {
        if (internalRenderManager != null) {
            internalRenderManager.removeRenderer(renderer);
        }
    }

    /**
     * Submits the supplied Renderable instance to the RenderHub for
     * server-initiated render.
     *
     * @param renderable The {@link Renderable} instance to render.
     */
    public void requestRender(final Renderable renderable) {
        if (internalRenderManager != null) {
            internalRenderManager.requestRender(renderable);
        }
    }

    /**
     * @deprecated Replaced by {@link #setBroadcastRenderer(BroadcastRenderer)}.
     */
    public void setBroadcasted(final boolean broadcasted) {
        if (internalRenderManager != null) {
            internalRenderManager.setBroadcasted(broadcasted);
        }
    }

    public void setBroadcastRenderer(
        final BroadcastRenderer broadcastRenderer) {

        if (internalRenderManager != null) {
            internalRenderManager.setBroadcastRenderer(broadcastRenderer);
        }
    }

    public static synchronized void setServletConfig(
        final ServletConfig servletConfig) {

        if (internalRenderManager == null) {
            internalRenderManager =
                new InternalRenderManager(servletConfig.getServletContext());
        }
    }

    public void setCorePoolSize(int corePoolSize) {
        internalRenderManager.setCorePoolSize(corePoolSize);
    }

    public void setMaxPoolSize(int maxPoolSize) {
        internalRenderManager.setMaxPoolSize(maxPoolSize);
    }

    public void setKeepAliveTime(long keepAliveTime) {
        internalRenderManager.setKeepAliveTime(keepAliveTime);
    }

    public void setRenderQueueCapacity(int renderQueueCapacity) {
        internalRenderManager.setRenderQueueCapacity(renderQueueCapacity);
    }

    /**
     * This method is used by {@link GroupAsyncRenderer}s that need to request
     * render calls based on some sort of schedule.  It uses a separate,
     * configurable thread pool and queue than the core rendering service.
     *
     * @return the scheduled executor for this RenderManager
     */
    ScheduledThreadPoolExecutor getScheduledService() {
        return
            internalRenderManager != null ?
                internalRenderManager.getScheduledService() : null;
    }

    void requestRender(final AsyncRenderer renderer) {
        if (internalRenderManager != null) {
            internalRenderManager.requestRender(renderer);
        }
    }

    private static class InternalRenderManager
    extends RenderManager
    implements Disposable {
        private static final Log LOG =
            LogFactory.getLog(InternalRenderManager.class);

        private final ServletContext servletContext;
        private final RenderHub renderHub;
        private final Map rendererGroupMap =
            Collections.synchronizedMap(new HashMap());
        private final ContextDestroyedListener shutdownListener =
            new ContextDestroyedListener(this);
        private BroadcastRenderer broadcastRenderer;

        private InternalRenderManager(final ServletContext servletContext) {
            this.servletContext = servletContext;
            Configuration configuration =
                new ServletContextConfiguration(
                    "com.icesoft.faces.async.render", this.servletContext);
            renderHub = new RenderHub(configuration);
            setBroadcasted(
                configuration.getAttributeAsBoolean("broadcasted", false));
            ContextEventRepeater.addListener(shutdownListener);
        }

        public void dispose() {
            synchronized (rendererGroupMap) {
                /*
                 * Bug 944
                 * We make a copy of the list of renderers to remove simply to
                 * iterate through them.  This avoids a concurrent modification
                 * issue when each individual renderers dispose method attempts
                 * to remove itself from the official groupMap.
                 */
                Iterator renderers =
                    new ArrayList(rendererGroupMap.values()).iterator();
                while (renderers.hasNext()) {
                    AsyncRenderer renderer = (AsyncRenderer)renderers.next();
                    renderer.dispose();
                    if (LOG.isTraceEnabled()) {
                        LOG.trace("Renderer disposed: " + renderer);
                    }
                }
                rendererGroupMap.clear();
            }
            renderHub.dispose();
            LOG.debug("All renderers and render hub have been disposed.");
            ContextEventRepeater.removeListener(shutdownListener);
        }

        public BroadcastRenderer getBroadcastRenderer() {
            return broadcastRenderer;
        }

        public DelayRenderer getDelayRenderer(final String rendererName) {
            return (DelayRenderer)getRenderer(rendererName, DELAY);
        }

        public IntervalRenderer getIntervalRenderer(final String rendererName) {
            return (IntervalRenderer)getRenderer(rendererName, INTERVAL);
        }

        public OnDemandRenderer getOnDemandRenderer(final String rendererName) {
            return (OnDemandRenderer)getRenderer(rendererName, ON_DEMAND);
        }

        public AsyncRenderer getRenderer(final String rendererName) {
            if (rendererName == null) {
                return null;
            }
            return (AsyncRenderer)rendererGroupMap.get(rendererName);
        }

        public ServletContext getServletContext() {
            return servletContext;
        }

        public boolean isBroadcasted() {
            return broadcastRenderer != null;
        }

        public void removeRenderer(final AsyncRenderer renderer) {
            if (renderer == null) {
                if (LOG.isInfoEnabled()) {
                    LOG.info("Renderer is null");
                }
            } else if (rendererGroupMap.remove(renderer.getName()) == null) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Renderer " + renderer.getName() + " not found");
                }
            } else {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Renderer " + renderer.getName() + " removed");
                }
            }
        }

        public void requestRender(final Renderable renderable) {
            renderHub.requestRender(renderable);
        }

        public void setBroadcasted(final boolean broadcasted) {
            // do nothing.
        }

        public void setCorePoolSize(int corePoolSize) {
            renderHub.setCorePoolSize(corePoolSize);
        }

        public void setMaxPoolSize(int maxPoolSize) {
            renderHub.setMaxPoolSize(maxPoolSize);
        }

        public void setKeepAliveTime(long keepAliveTime) {
            renderHub.setKeepAliveTime(keepAliveTime);
        }

        public void setBroadcastRenderer(
            final BroadcastRenderer broadcastRenderer) {

            this.broadcastRenderer = broadcastRenderer;
        }

        public void setRenderQueueCapacity(int renderQueueCapacity) {
            renderHub.setRenderQueueCapacity(renderQueueCapacity);
        }

        ScheduledThreadPoolExecutor getScheduledService() {
            return renderHub.getScheduledService();
        }

        void requestRender(final AsyncRenderer renderer) {
            if (renderer == null) {
                return;
            }
            if (broadcastRenderer != null &&
                isBroadcasted() && renderer.isBroadcasted()) {

                broadcastRenderer.requestRender(renderer);
            }
        }

        /**
         * Internal utility method that returns the proper type of {@link
         * GroupAsyncRenderer} and ensures that is added to the managed
         * collection, indexed by name.
         *
         * @param name the unique name of the GroupAsyncRenderer
         * @param type the type of GroupAsyncRenderer
         * @return the requested type of GroupAsyncRenderer
         * @throws IllegalArgumentException if the <code>name</code> is <code>null</code> or
         *                                  empty, or if the <code>type</code> is illegal.
         */
        private synchronized AsyncRenderer getRenderer(
            final String name, final int type)
        throws IllegalArgumentException {

            if (name == null || name.trim().length() == 0) {
                throw
                    new IllegalArgumentException(
                        "Illegal renderer name: " + name);
            }
            if (type < MIN || type > MAX) {
                throw
                    new IllegalArgumentException(
                        "Illegal renderer type: " + type);
            }
            if (rendererGroupMap.containsKey(name)) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Existing renderer retrieved: " + name);
                }
                return (AsyncRenderer)rendererGroupMap.get(name);
            }
            AsyncRenderer renderer;
            switch (type) {
                case ON_DEMAND:
                    renderer = new OnDemandRenderer();
                    break;
                case INTERVAL:
                    renderer = new IntervalRenderer();
                    break;
                case DELAY:
                    renderer = new DelayRenderer();
                    break;
                default:
                    // this shouldn't happen
                    renderer = null;
            }
            if (renderer != null) {
                renderer.setBroadcasted(broadcastRenderer != null);
                renderer.setName(name);
                rendererGroupMap.put(name, renderer);
            }
            if (LOG.isTraceEnabled()) {
                LOG.trace("New renderer retrieved: " + name);
            }
            return renderer;
        }
    }
}
