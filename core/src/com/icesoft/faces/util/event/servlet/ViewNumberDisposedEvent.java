package com.icesoft.faces.util.event.servlet;

/**
 * <p>
 *   The <code>ViewNumberDisposedEvent</code> class represents an event that
 *   should be fired whenever a View Number is disposed.
 * </p>
 */
public class ViewNumberDisposedEvent
extends AbstractSessionEvent
implements ContextEvent {
    private int viewNumber;

    /**
     * <p>
     *   Constructs a <code>ViewNumberDisposedEvent</code> with the specified
     *   <code>source</code>, <code>iceFacesId</code> and
     *   <code>viewNumber</code>.
     * </p>
     *
     * @param      source
     *                 the HTTP session.
     * @param      iceFacesId
     *                 the ICEfaces ID identifying the session.
     * @param      viewNumber
     *                 the view number that has been disposed.
     * @throws     IllegalArgumentException
     *                 if the specified <code>iceFacesId</code> is either
     *                 <code>null</code> or empty.
     */
    public ViewNumberDisposedEvent(
        final Object source, final String iceFacesId, final int viewNumber)
    throws IllegalArgumentException {
        super(source, iceFacesId);
        this.viewNumber = viewNumber;
    }

    /**
     * <p>
     *   Gets the view number of this <code>ViewNumberRetrievedEvent</code>.
     * </p>
     *
     * @return     the view number.
     * @see        #getICEfacesID()
     */
    public int getViewNumber() {
        return viewNumber;
    }
}
