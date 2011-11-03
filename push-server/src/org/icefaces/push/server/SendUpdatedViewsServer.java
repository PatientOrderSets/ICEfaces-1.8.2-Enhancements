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

import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.Server;
import com.icesoft.faces.webapp.http.servlet.SessionDispatcher;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class SendUpdatedViewsServer
implements Server {
    private static final Log LOG =
        LogFactory.getLog(SendUpdatedViewsServer.class);

    private final SessionManager sessionManager;
    private final SessionDispatcher.Monitor monitor;

    public SendUpdatedViewsServer(
        final SessionManager sessionManager,
        final SessionDispatcher.Monitor monitor) {

        this.sessionManager = sessionManager;
        this.monitor = monitor;
    }

    public void service(final Request request)
    throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Extracting ICEfaces ID(s)...");
        }
        Set _iceFacesIdSet = new HashSet();
        try {
            String[] _iceFacesIds =
                request.getParameterAsStrings("ice.session");
            for (int i = 0; i < _iceFacesIds.length; i++) {
                if (sessionManager.isValid(_iceFacesIds[i])) {
                    _iceFacesIdSet.add(_iceFacesIds[i]);
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(
                            "Invalid ICEfaces ID: " +
                                _iceFacesIds[i] + ")");
                    }
                }
            }
        } catch (Exception e) {
            //todo: remove this
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("ICEfaces ID(s): " + _iceFacesIdSet);
        }
        monitor.touchSession();
        handle(request, _iceFacesIdSet);
    }

    public abstract void handle(Request request, Set iceFacesIdSet);

    public void shutdown() {
        // do nothing.
    }
}
