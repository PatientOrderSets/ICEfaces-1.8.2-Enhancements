package com.icesoft.faces.async.render;

/**
 * <p>
 *   The <code>SessionRenderer</code> class allows an application to initiate
 *   rendering asynchronously and independently of user interaction for a
 *   session or a group of sessions.  When a session is rendered, all windows or
 *   views that a particular user has open in their session will be updated via
 *   Ajax Push with the current application state.
 * </p>
 */
public class SessionRenderer {
    public static final String ALL_SESSIONS = "SessionRenderer.ALL_SESSIONS";

    /**
     * <p>
     *   Adds the current session to the group of sessions with the specified
     *   <code>groupName</code>.  The group is automatically garbage collected
     *   when all its member sessions have become invalid.
     * </p>
     * <p>
     *   For more fine-grained control use the {@link RenderManager} API.
     * </p>
     *
     * @param      groupName
     *                 the name of the group of sessions to add the current
     *                 session to.
     * @throws     IllegalStateException
     *                 if no current session is active.
     * @see        #removeCurrentSession(String)
     */
    public static void addCurrentSession(final String groupName)
    throws IllegalStateException {
        // Creates an OnDemandRenderer instance if not already done so.
        RenderManager.getInstance().getOnDemandRenderer(groupName).
            addCurrentSession();
    }

    /**
     * <p>
     *   Removes the current session from the group of sessions with the
     *   specified <code>groupName</code>.  The group is automatically garbage
     *   collected when all its member sessions have been removed.
     * </p>
     * <p>
     *   For more fine-grained control use the {@link RenderManager} API.
     * </p>
     *
     * @param      groupName
     *                 the name of the group of sessions to remove the current
     *                 session from.
     * @see        #addCurrentSession(String)
     */
    public static void removeCurrentSession(final String groupName) {
        // Does not create an OnDemandRenderer instance.
        OnDemandRenderer renderer = getRenderer(groupName);
        if (renderer != null) {
            renderer.removeCurrentSession();
            removeRendererIfEmpty(renderer);
        }
    }

    /**
     * <p>
     *   Renders the group of sessions with the specified <code>groupName</code>
     *   by performing the JavaServer Faces execute and render life cycle
     *   phases.  If a <code>FacesContext</code> is in the scope of the current
     *   thread scope, the current view will not be asynchronously rendered as
     *   it is already rendered as a result of the user event being processed.
     * </p>
     * <p>
     *   For more fine-grained control use the {@link RenderManager} API.
     * </p>
     *
     * @param      groupName
     *                 the name of the group of sessions to render.
     */
    public static void render(final String groupName) {
        // Does not create an OnDemandRenderer instance.
        OnDemandRenderer renderer = getRenderer(groupName);
        if (renderer != null) {
            renderer.requestRender();
            removeRendererIfEmpty(renderer);
        }
    }

    private static OnDemandRenderer getRenderer(final String groupName) {
        return
            (OnDemandRenderer)
                RenderManager.getInstance().getRenderer(groupName);
    }

    private static void removeRendererIfEmpty(final OnDemandRenderer renderer) {
        if (renderer.isEmpty()) {
            RenderManager.getInstance().removeRenderer(renderer);
        }
    }
}
