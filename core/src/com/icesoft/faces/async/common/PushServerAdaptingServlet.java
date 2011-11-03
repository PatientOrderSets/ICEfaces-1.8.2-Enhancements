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
package com.icesoft.faces.async.common;

import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.Server;
import com.icesoft.faces.webapp.http.common.standard.StreamingContentHandler;
import com.icesoft.faces.webapp.http.core.ViewQueue;
import com.icesoft.faces.webapp.http.servlet.CoreMessageService;
import com.icesoft.net.messaging.Message;
import com.icesoft.net.messaging.MessageServiceClient;
import com.icesoft.net.messaging.MessageServiceException;
import com.icesoft.util.Properties;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PushServerAdaptingServlet
implements Server {
    private static final Log LOG =
        LogFactory.getLog(PushServerAdaptingServlet.class);
    private static final String SEQUENCE_NUMBER_KEY =
        "com.icesoft.faces.sequenceNumber";
    private static final String UPDATED_VIEWS_MESSAGE_TYPE = "UpdatedViews";

    private final Object lock = new Object();

    private final String blockingRequestHandlerContext;

    public PushServerAdaptingServlet(
        final HttpSession httpSession, final String iceFacesId,
        final Collection synchronouslyUpdatedViews,
        final ViewQueue allUpdatedViews, final Configuration configuration,
        final CoreMessageService coreMessageService)
    throws MessageServiceException {
        blockingRequestHandlerContext =
            configuration.getAttribute(
                "blockingRequestHandlerContext", "push-server");
        allUpdatedViews.onPut(
            new Runnable() {
                public void run() {
                    allUpdatedViews.removeAll(synchronouslyUpdatedViews);
                    synchronouslyUpdatedViews.clear();
                    Set _viewIdentifierSet = new HashSet(allUpdatedViews);
                    if (!_viewIdentifierSet.isEmpty()) {
                        Long _sequenceNumber;
                        synchronized (lock) {
                            _sequenceNumber =
                                (Long)httpSession.getAttribute(SEQUENCE_NUMBER_KEY);
                            if (_sequenceNumber != null) {
                                _sequenceNumber =
                                    new Long(_sequenceNumber.longValue() + 1);
                            } else {
                                _sequenceNumber =
                                    new Long(1);
                            }
                            httpSession.setAttribute(
                                SEQUENCE_NUMBER_KEY, _sequenceNumber);
                        }
                        String[] _viewIdentifiers =
                            (String[])
                                _viewIdentifierSet.toArray(
                                    new String[_viewIdentifierSet.size()]);
                        StringWriter _stringWriter = new StringWriter();
                        for (int i = 0; i < _viewIdentifiers.length; i++) {
                            if (i != 0) {
                                _stringWriter.write(',');
                            }
                            _stringWriter.write(_viewIdentifiers[i]);
                        }
                        Properties _messageProperties = new Properties();
                        _messageProperties.
                            setStringProperty(
                                Message.DESTINATION_SERVLET_CONTEXT_PATH,
                                blockingRequestHandlerContext);
                        coreMessageService.publish(
                            iceFacesId + ";" +                    // ICEfaces ID
                                _sequenceNumber + ";" +       // Sequence Number
                                _stringWriter.toString(),        // Message Body
                            _messageProperties,
                            UPDATED_VIEWS_MESSAGE_TYPE,
                            MessageServiceClient.PUSH_TOPIC_NAME);
                    }
                }
            });
    }

    public void service(final Request request)
    throws Exception {
        request.respondWith(new StreamingContentHandler("text/plain", "UTF-8") {
            public void writeTo(Writer writer) throws IOException {
                writer.write("Push Server is enabled.\n");
                writer.write("Check your server configuration.\n");
            }
        });
    }

    public void shutdown() {
    }
}
