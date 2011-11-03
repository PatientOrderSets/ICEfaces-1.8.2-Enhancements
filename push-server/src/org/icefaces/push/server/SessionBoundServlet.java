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
 */
package org.icefaces.push.server;

import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.servlet.BasicAdaptingServlet;
import com.icesoft.faces.webapp.http.servlet.EnvironmentAdaptingServlet;
import com.icesoft.faces.webapp.http.servlet.PathDispatcher;
import com.icesoft.faces.webapp.http.servlet.PseudoServlet;
import com.icesoft.faces.webapp.http.servlet.SessionDispatcher;

import edu.emory.mathcs.backport.java.util.concurrent.ScheduledThreadPoolExecutor;

import java.util.Set;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SessionBoundServlet
extends PathDispatcher
implements PseudoServlet {
    private static final Log LOG = LogFactory.getLog(SessionBoundServlet.class);

    public SessionBoundServlet(
        final ServletContext servletContext,
        final SessionManager sessionManager,
        final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor,
        final Configuration configuration,
        final SessionDispatcher.Monitor monitor) {

        dispatchOn(
            ".*block\\/receive\\-updated\\-views$",
            new EnvironmentAdaptingServlet(
                new SendUpdatedViewsServer(sessionManager, monitor) {
                    public void handle(
                        final Request request, final Set iceFacesIdSet) {

                        if (LOG.isDebugEnabled()) {
                            LOG.debug(
                                "Incoming receive-updated-views request: " +
                                    "ICEfaces IDs [" + iceFacesIdSet + "], " +
                                    "Sequence Numbers [" +
                                        new SequenceNumbers(
                                            request.
                                                getHeaderAsStrings(
                                                    "X-Window-Cookie")) +
                                    "]");
                        }
                        new IDVerifier(
                            iceFacesIdSet,
                            new ReceiveUpdatedViewsHandler(
                                request, iceFacesIdSet, sessionManager,
                                scheduledThreadPoolExecutor, configuration)
                        ).handle();
                    }
                },
                configuration,
                servletContext));
        dispatchOn(
            ".*block\\/dispose\\-views$",
            new BasicAdaptingServlet(
                new DisposeViewsHandlerServer(monitor) {
                    public void handle(final Request request) {
                        new DisposeViewsHandler(request, sessionManager).handle();
                    }
                }));
        dispatchOn(".*", new BasicAdaptingServlet(new NotFoundServer()));
    }
}
