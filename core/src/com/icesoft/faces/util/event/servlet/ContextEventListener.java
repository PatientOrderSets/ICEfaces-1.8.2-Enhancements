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

package com.icesoft.faces.util.event.servlet;

import java.util.EventListener;

/**
 * The listener interface for receiving context events. </p>
 */
public interface ContextEventListener extends EventListener {
    /**
     * Indicates the context has been destroyed. </p>
     *
     * This is just a relay of the <code>ServletContextEvent</code> that could
     * normally be received through registering a
     * <code>ServletContextListener</code>. This event generally indicates that
     * the application is shutting down, giving the listener time to do proper
     * clean-up. </p>
     *
     * Please note that <code>SessionDestroyedEvent</code>s are fired for every
     * session in the context before the <code>ContextDestroyedEvent</code> is
     * fired. </p>
     *
     * @param event the <code>ContextDestroyedEvent<code>.
     * @see javax.servlet.ServletContextListener
     * @see javax.servlet.ServletContextEvent
     * @see #sessionDestroyed(SessionDestroyedEvent)
     */
    public void contextDestroyed(ContextDestroyedEvent event);

    public void iceFacesIdDisposed(ICEfacesIDDisposedEvent event);
    
    /**
     * Indicates an ICEfaces ID has been retrieved. </p>
     *
     * Each time a call results in an ICEfaces ID being retrieved, an
     * <code>ICEfacesIDRetrievedEvent</code> is fired to all registered
     * <code>ContextEventListener</code>s. An ICEfaces ID is retrieved on all
     * initial page requests, but this can also happen on browser reloads.
     * Because of this, it is the responsibility of the listener to track valid
     * ICEfaces ID values. </p>
     *
     * @param event the <code>ICEfacesIDRetrievedEvent</code> containing the
     *              ICEfaces ID.
     */
    public void iceFacesIdRetrieved(ICEfacesIDRetrievedEvent event);

    /**
     * Indicates that the <code>ContextEventListener</code> is interested in
     * receiving buffered events. </p>
     *
     * It is possible for <code>ICEfacesIDRetrievedEvent</code>s and
     * <code>ViewNumberRetrievedEvent</code>s to be fired before
     * <code>ContextEventListener</code>s get a chance to register themselves
     * with the <code>ContextEventRepeater</code>.
     * <code>ContextEventListener</code>s that are interested in these events
     * can return <code>true</code> and all of the events that were fired before
     * registering are "refired". </p>
     *
     * @return <code>true</code> if interested in receiving buffered
     *         <code>ICEfacesIDRetrievedEvents</code> and
     *         <code>ViewNumberRetrievedEvent</code>, <code>false</code> if not.
     * @see ContextEventRepeater
     */
    public boolean receiveBufferedEvents();

    /**
     * Indicates that a session has been destroyed. </p>
     *
     * This is just a relay of the <code>HttpSessionEvent</code> that could
     * normally be received through registering an
     * <code>HttpSessionListener</code>. This event generally indicates that a
     * session got expired, giving the listener time to do clean-up and/or other
     * appropriate actions. </p>
     *
     * @param event the <code>SessionDestroyedEvent</code>.
     * @see javax.servlet.http.HttpSessionEvent
     * @see javax.servlet.http.HttpSessionListener
     */
    public void sessionDestroyed(SessionDestroyedEvent event);

    public void viewNumberDisposed(ViewNumberDisposedEvent event);
    
    /**
     * Indicates a view number has been retrieved. </p>
     *
     * Each time a call results in a view number being retrieved, a
     * <code>ViewNumberRetrievedEvent</code> is fired to all registered
     * <code>ContextEventListener</code>s. View numbers are retrieved on all
     * initial page requests, but this can also happen on browser reloads.
     * Because of this, it is the responsibility of the listener to track valid
     * view numbers. </p>
     *
     * @param event the <code>ViewNumberRetrievedEvent</code> containing both
     *              the ICEfaces ID and the view number.
     * @see #iceFacesIdRetrieved(ICEfacesIDRetrievedEvent)
     */
    public void viewNumberRetrieved(ViewNumberRetrievedEvent event);
}
