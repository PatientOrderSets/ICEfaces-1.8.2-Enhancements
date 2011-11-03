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

import com.icesoft.faces.context.View;
import com.icesoft.faces.webapp.http.servlet.MainSessionBoundServlet;
import com.icesoft.faces.webapp.http.servlet.SessionDispatcher;
import com.icesoft.faces.webapp.http.servlet.PseudoServlet;
import com.icesoft.faces.webapp.http.common.Server;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesState;
import com.icesoft.faces.webapp.xmlhttp.RenderingException;
import com.icesoft.util.StaticTimerUtility;
import edu.emory.mathcs.backport.java.util.concurrent.CopyOnWriteArraySet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.context.FacesContext;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Set;

/**
 * The GroupAsyncRenderer is the foundation class for other types of renderers
 * that are designed to operate on a group of {@link Renderable}s.  It
 * implements the {@link AsyncRenderer} interface and is mainly responsible for
 * smartly managing a group of Renderable instances.
 * <p/>
 * Groups of Renderables are stored as WeakReferences in special sets that are
 * copied before each render pass so that Renderables can be safely added and
 * removed from the group while a render pass is in progress.
 * <p/>
 * Although it is possible to create and use GroupRenderers directly, developers
 * are advised to use the {@link RenderManager} to create and use named render
 * groups.
 *
 * @author ICEsoft Technologies, Inc.
 * @see RenderManager, OnDemandRenderer, IntervalRenderer, DelayRenderer
 */
public class GroupAsyncRenderer
implements AsyncRenderer {
    private static final Log LOG = LogFactory.getLog(GroupAsyncRenderer.class);

    protected final Set group = new CopyOnWriteArraySet();

    protected boolean broadcasted = false;
    protected String name;

    protected boolean stopRequested = false;

    public GroupAsyncRenderer() {
    }

    /**
     * <p>
     *   Adds the specified <code>renderable</code>, via a
     *   <code>WeakReference</code>, to the set of <code>Renderable</code>s of
     *   this group.  If it is already in this set, it is not added again.
     * </p>
     *
     * @param      renderable
     *                 the Renderable instance to add to the group.
     * @throws     IllegalArgumentException
     *                 if the specified <code>renderable</code> is
     *                 <code>null</code>.
     */
    public void add(final Renderable renderable)
    throws IllegalArgumentException {
        if (renderable != null) {
            add((Object)renderable);
        } else {
            throw new IllegalArgumentException("renderable is null");
        }
    }

    /**
     * <p>
     *   Adds the current session, via a <code>WeakReference</code>, to this
     *   <code>GroupAsyncRenderer</code>.  If this already contains the current
     *   session, it is not added again.
     * </p>
     *
     * @throws     IllegalStateException
     *                 if no current session is active.
     */
    public void addCurrentSession()
    throws IllegalStateException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null) {
            Object session =
                facesContext.getExternalContext().getSession(false);
            if (session != null) {
                add( getSessionID(session));
            } else {
                LOG.warn(
                    "Unable to add current session: " +
                        "no current session active");
                throw
                    new IllegalStateException(
                        "Unable to add current session: " +
                            "no current session active");
            }
        } else {
            if (LOG.isWarnEnabled()) {
                LOG.warn(
                    "Add current session cannot be done from non-JSF thread. " +
                        "Failed to add session to group '" + name + "'.");
            }
        }
    }

    /**
     * Removes all Renderables from the group.
     */
    public void clear() {
        // todo: remove synchronized block as CopyOnWriteArraySet is used?
        synchronized (group) {
            group.clear();
        }
    }

    public boolean contains(final Renderable renderable) {
        return contains((Object)renderable);
    }

    public boolean containsCurrentSession() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if( facesContext == null ){
            return false;
        }

        return contains( getSessionID(facesContext.getExternalContext().getSession(false)) );
    }

    private static String getSessionID(Object session){
        if(session == null){
            return null;
        }

        try {
            Method getIdMethod = session.getClass().getMethod("getId", null );
            Object id = getIdMethod.invoke(session,null);
            return id.toString();
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * Remove all Renderables from the group and removes the reference to the
     * RenderHub.  Once disposed, a GroupAsyncRenderer cannot be re-used.  This
     * method is typically used by the RenderManager to cleanly dispose of all
     * managed Renderers when the application is shutting down.
     */
    public void dispose() {
        requestStop();
        RenderManager.getInstance().removeRenderer(this);
        clear();
        name = null;
    }

    public String getName() {
        return name;
    }

    public boolean isBroadcasted() {
        return broadcasted;
    }

    /**
     * Used to determine if the Renderer has any Renderables left in its
     * collection.
     *
     * @return false if there are 1 or more Renderables left in the Renderer's
     *         collection, true otherwise
     */
    public boolean isEmpty() {
        return group.isEmpty();
    }

    /**
     * Removes a Renderable, via a WeakReference, from the set of Renderables of
     * this group.
     *
     * @param renderable the Renderable instance to remove
     */
    public void remove(final Renderable renderable) {
        remove((Object)renderable);
    }

    /**
     * <p>
     *   Removes the current session, via a <code>WeakReference</code>, from
     *   this <code>GroupAsyncRenderer</code>.
     * </p>
     */
    public void removeCurrentSession() {
        LOG.info("GroupAsyncRenderer.removeCurrentSession()");
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null) {
            Object session =
                facesContext.getExternalContext().getSession(false);
            if (session != null) {
                remove(getSessionID(session));
            } else {
                LOG.warn(
                    "Unable to remove current session: " +
                        "no current session active");
            }
        }
    }

    String lastRenderInfo = "";

    public String getLastRenderInfo()  {
        return lastRenderInfo;
    }

    /**
     * Request a render pass on all the Renderables in the group.  Render calls
     * that generate exceptions are passed back to the Renderable.renderException
     *
     * @throws IllegalStateException If a reference to a {@link RenderHub} has
     *                               not yet been set.
     */
    public void requestRender() {
        requestRender(true);
    }

    public void requestRender(final boolean allowBroadcasting) {
        if (LOG.isTraceEnabled()) {
            LOG.trace(name + " preparing to render " + group.size());
        }
        lastRenderInfo = "groupSize=" + group.size() + " startTime=" + System.currentTimeMillis();
        if (allowBroadcasting && isBroadcasted()) {
            // allow for potential broadcasting
            RenderManager.getInstance().requestRender(this);
        }
        stopRequested = false;
        /*
         * Note that the Iterator returned by the CopyOnWriteArraySet relies on
         * an unchanging snapshot of the array at the time the Iterator was
         * constructed and does not support the mutative remove operation!
         */
        if (StaticTimerUtility.Log.isTraceEnabled()) {
            StaticTimerUtility.newJob(group.size());
            StaticTimerUtility.startJobTmer();
        }
        /*
         * Invocation from a non-JSF thread currently cannot invoke a render
         * on sessions.
         */
        for (Iterator i = group.iterator(); !stopRequested && i.hasNext(); ) {
            /*
             * From the CopyOnWriteArraySet:
             *
             *     "The returned iterator provides a snapshot of the state of
             *     the set when the iterator was constructed.  No
             *     synchronization is needed while traversing the iterator.  The
             *     iterator does NOT support the remove method."
             */
            WeakReference reference = (WeakReference)i.next();
            Object object = reference.get();
            if (object == null) {
                group.remove(reference);
            } else if (object instanceof Renderable) {
                requestRender((Renderable)object);
            } else if (object instanceof String) {
                String sessionId = (String)object;
                try {
                    requestRender(sessionId);
                } catch (Exception exception) {
                    /*
                     * Remove from the CopyOnWriteArraySet is allowed here as
                     * the Iterator in requestRender(boolean) relies on an
                     * unchanging snapshot of the array at the time the Iterator
                     * was constructed.
                     */
                    remove(sessionId);
                }
            }
        }
    }

    /**
     * The method called by dispose to halt a render pass at the current {@link
     * Renderable}s.
     */
    public void requestStop() {
        stopRequested = true;
    }

    public void setBroadcasted(final boolean broadcasted) {
        this.broadcasted = broadcasted;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setRenderManager(final RenderManager renderManager) {
        // do nothing.
    }

    private void add(final Object object) {
        // todo: remove synchronized block as CopyOnWriteArraySet is used?
        synchronized (group) {
            if (!contains(object)) {
                if (group.add(new WeakReference(object))) {
                    if (LOG.isTraceEnabled()) {
                        LOG.trace(name + " added " + object);
                    }
                } else {
                    if (LOG.isWarnEnabled()) {
                        LOG.warn(name + " already contains " + object);
                    }
                }
            }
        }
    }

    private boolean contains(final Object object) {
        for (Iterator i = group.iterator(); i.hasNext(); ) {
            if (object == ((WeakReference)i.next()).get()) {
                return true;
            }
        }
        return false;
    }

    private void remove(final Object object) {
        // todo: remove synchronized block as CopyOnWriteArraySet is used?
        synchronized (group) {
            for (Iterator i = group.iterator(); i.hasNext(); ) {
                WeakReference reference = (WeakReference)i.next();
                if (object == reference.get()) {
                    group.remove(reference);
                    if (LOG.isTraceEnabled()) {
                        LOG.trace(name + " removing " + object);
                    }
                    return;
                }
            }
            if (LOG.isWarnEnabled()) {
                LOG.warn(name + " does not contain " + object);
            }
        }
    }

    private void requestRender(final String sessionId) {
        PersistentFacesState suppressedViewState;
        if (FacesContext.getCurrentInstance() != null) {
            suppressedViewState = PersistentFacesState.getInstance();
        } else {
            /*
             * Invocation from a non-JSF thread should not suppress the
             * current view.
             */
            suppressedViewState = null;
        }

        PseudoServlet serv = SessionDispatcher.getSingletonSessionServer(sessionId,
                RenderManager.getInstance().getServletContext());
        if (serv == null) {
            group.remove(sessionId);
            return;
        }

        for (Iterator i = ((MainSessionBoundServlet) serv).getViews().values().iterator();
             i.hasNext();) {

            final PersistentFacesState viewState =
                ((View) i.next()).getPersistentFacesState();
            if (viewState != suppressedViewState) {
                requestRender(
                    new Renderable() {
                        public PersistentFacesState getState() {
                            return viewState;
                        }

                        public void renderingException(
                            final RenderingException renderingException) {

                            /*
                             * It's up to our View infrastructure to remove
                             * dead views.
                             */
                        }
                    }
                );
            }
        }
    }

    private void requestRender(final Renderable renderable) {
        RenderManager.getInstance().requestRender(renderable);
    }
}
