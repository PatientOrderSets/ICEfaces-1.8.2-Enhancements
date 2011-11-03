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
import com.icesoft.net.messaging.Message;
import com.icesoft.net.messaging.MessageServiceClient;
import com.icesoft.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DisposeViewsHandler
extends AbstractHandler
implements Handler, Runnable {
    private static final int STATE_UNINITIALIZED = 0;
    private static final int STATE_PROCESSING_REQUEST = 1;
    private static final int STATE_DONE = 2;

    private static final ResponseHandler EMPTY_RESPONSE_HANDLER =
        new ResponseHandler() {
            public void respond(final Response response)
            throws Exception {
                response.setStatus(200);
                // general header fields
                response.setHeader("Pragma", "no-cache");
                response.setHeader("Cache-Control", "no-cache, no-store");
                // entity header fields
                response.setHeader("Content-Length", 0);
                response.setHeader("Content-Type", "text/xml");
            }
        };

    private static final String DISPOSE_VIEWS_MESSAGE_TYPE = "DisposeViews";

    private static final Log LOG = LogFactory.getLog(DisposeViewsHandler.class);

    private final SessionManager sessionManager;

    private int state = STATE_UNINITIALIZED;

    public DisposeViewsHandler(
        final Request request, final SessionManager sessionManager) {

        super(request);
        this.sessionManager = sessionManager;
    }

    public void run() {
        switch (state) {
            case STATE_UNINITIALIZED :
                if (LOG.isTraceEnabled()) {
                    LOG.trace("State: Uninitialized");
                }
                state = STATE_PROCESSING_REQUEST;
            case STATE_PROCESSING_REQUEST :
                if (LOG.isTraceEnabled()) {
                    LOG.trace("State: Processing Request");
                }
                // Parameter Name  : ice.views
                // Parameter Value : <ICEfaces ID>:<View Number>,
                //                   <ICEfaces ID>:<View Number>, etc.
                String[] _parameterNames = request.getParameterNames();
                for (int i = 0; i < _parameterNames.length; i++) {
                    if (!_parameterNames[i].equals("rand") &&
                        sessionManager.isValid(_parameterNames[i])) {

                        StringBuffer _message = new StringBuffer();
                        String[] _viewNumbers =
                            request.getParameterAsStrings(_parameterNames[i]);
                        for (int j = 0; j < _viewNumbers.length; j++) {
                            _message.
                                // ICEfaces ID
                                append(_parameterNames[i]).append(";").
                                // View Number
                                append(_viewNumbers[j]).append("\r\n");
                        }
                        Properties _messageProperties = new Properties();
                        _messageProperties.
                            setStringProperty(
                                Message.DESTINATION_SERVLET_CONTEXT_PATH,
                                sessionManager.
                                    getServletContextPath(_parameterNames[i]));
                        sessionManager.getPushServerMessageService().publish(
                            _message.toString(),
                            _messageProperties,
                            DISPOSE_VIEWS_MESSAGE_TYPE,
                            MessageServiceClient.PUSH_TOPIC_NAME);
                    }
                }
                try {
                    request.respondWith(EMPTY_RESPONSE_HANDLER);
                } catch (Exception exception) {
                    if (LOG.isErrorEnabled()) {
                        LOG.error(
                            "An error occurred while " +
                                "trying to response with: 200 OK!",
                            exception);
                    }
                }
                state = STATE_DONE;
            case STATE_DONE :
                if (LOG.isTraceEnabled()) {
                    LOG.trace("State: Done");
                }
                break;
            default :
                // this should never happen!
        }
    }
}
