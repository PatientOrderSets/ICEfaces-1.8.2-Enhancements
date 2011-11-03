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
import com.icesoft.faces.webapp.http.common.Response;
import com.icesoft.faces.webapp.http.common.ResponseHandler;

import java.util.List;
import java.util.Set;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UpdatedViewsResponseHandler
implements ResponseHandler {
    private static final Log LOG =
        LogFactory.getLog(UpdatedViewsResponseHandler.class);

    private final Request request;
    private final List updatedViewsList;
    
    public UpdatedViewsResponseHandler(
        final Request request, final List updatedViewsList) {

        this.request = request;
        this.updatedViewsList = updatedViewsList;
    }

    public void respond(final Response response)
    throws Exception {
        // preparation
        StringBuffer _sequenceNumbers = new StringBuffer();
        StringBuffer _entityBody = new StringBuffer();
        _entityBody.append("<updated-views>");
        for (int i = 0, _iMax = updatedViewsList.size(); i < _iMax; i++) {
            UpdatedViews _updatedViews = (UpdatedViews)updatedViewsList.get(i);
            if (i != 0) {
                _sequenceNumbers.append(",");
                _entityBody.append(" ");
            }
            _sequenceNumbers.
                append(_updatedViews.getICEfacesID()).append(":").
                append(_updatedViews.getSequenceNumber());
            Set _updatedViewsSet = _updatedViews.getUpdatedViewsSet();
            Iterator _updatedViewsIterator = _updatedViewsSet.iterator();
            for (int j = 0, _jMax = _updatedViewsSet.size() ; j < _jMax; j++) {
                if (j != 0) {
                    _entityBody.append(" ");
                }
                _entityBody.
                    append(_updatedViews.getICEfacesID()).append(":").
                    append(_updatedViewsIterator.next());
            }
        }
        _entityBody.append("</updated-views>\r\n\r\n");
        // general header fields
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache, no-store");
        // entity header fields
        response.setHeader("Content-Length", _entityBody.length());
        response.setHeader("Content-Type", "text/xml");
        // extension header fields
        response.setHeader(
            "X-Set-Window-Cookie",
            "Sequence_Numbers=\"" + _sequenceNumbers.toString() + "\"");
        // entity-body
        response.writeBody().write(_entityBody.toString().getBytes("UTF-8"));
    }
}
