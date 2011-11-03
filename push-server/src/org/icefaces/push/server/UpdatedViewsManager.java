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
import com.icesoft.net.messaging.Message;
import com.icesoft.net.messaging.MessageServiceClient;
import com.icesoft.util.Properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UpdatedViewsManager {
    protected static final String UPDATED_VIEWS_QUEUE_EXCEEDED_MESSAGE_TYPE =
        "UpdatedViewsQueueExceeded";

    private static final Log LOG =
        LogFactory.getLog(UpdatedViewsManager.class);

    protected final PushServerMessageService pushServerMessageService;
    protected final SessionManager sessionManager;
    protected final Map updatedViewsQueueMap = new HashMap();

    protected int updatedViewsQueueSize;

    public UpdatedViewsManager(
        final Configuration configuration,
        final PushServerMessageService pushServerMessageService,
        final SessionManager sessionManager) {

        setUpdatedViewsQueueSize(
            configuration.getAttributeAsInteger(
                "updatedViewsQueueSize", 100));
        this.pushServerMessageService = pushServerMessageService;
        this.sessionManager = sessionManager;
    }

    public int getUpdatedViewsQueueSize() {
        return updatedViewsQueueSize;
    }

    public List pull(
        final Set iceFacesIdSet, final SequenceNumbers sequenceNumbers) {

        if (iceFacesIdSet == null || iceFacesIdSet.isEmpty()) {
            return null;
        }
        synchronized (updatedViewsQueueMap) {
            List _updatedViewsList = new ArrayList();
            Iterator _iceFacesIds = iceFacesIdSet.iterator();
            int _size = iceFacesIdSet.size();
            for (int i = 0; i < _size; i++) {
                String _iceFacesId = (String)_iceFacesIds.next();
                if (updatedViewsQueueMap.containsKey(_iceFacesId)) {
                    UpdatedViewsQueue _updatedViewsQueue =
                        (UpdatedViewsQueue)
                            updatedViewsQueueMap.get(_iceFacesId);
                    Long _sequenceNumber = sequenceNumbers.get(_iceFacesId);
                    if (_sequenceNumber != null) {
                        _updatedViewsQueue.purge(_sequenceNumber.longValue());
                    }
                    UpdatedViews _updatedViews;
                    if (!_updatedViewsQueue.isEmpty()) {
                        Iterator _updatedViewsQueueIterator =
                            _updatedViewsQueue.iterator();
                        _updatedViews =
                            (UpdatedViews)_updatedViewsQueueIterator.next();
                        while (_updatedViewsQueueIterator.hasNext()) {
                            _updatedViews =
                                UpdatedViews.merge(
                                    _updatedViews,
                                    (UpdatedViews)
                                        _updatedViewsQueueIterator.next());
                        }
                        _updatedViewsList.add(_updatedViews);
                    }
                }
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Pulled pending updated views: " + iceFacesIdSet);
            }
            return _updatedViewsList;
        }
    }

    public void push(final UpdatedViews updatedViews) {
        if (updatedViews != null) {
            String _iceFacesId = updatedViews.getICEfacesID();
            synchronized (updatedViewsQueueMap) {
                UpdatedViewsQueue _updatedViewsQueue;
                if (updatedViewsQueueMap.containsKey(_iceFacesId)) {
                    _updatedViewsQueue =
                        (UpdatedViewsQueue)
                            updatedViewsQueueMap.get(_iceFacesId);
                } else {
                    _updatedViewsQueue =
                        newUpdatedViewsQueue(_iceFacesId, this);
                    updatedViewsQueueMap.put(_iceFacesId, _updatedViewsQueue);
                }
                try {
                    _updatedViewsQueue.add(updatedViews);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(
                            "Pushed pending updated views: " +
                                _iceFacesId + " " +
                                    "[size: " +
                                        _updatedViewsQueue.getSize() + "]");
                    }
                } catch (UpdatedViewsQueueExceededException exception) {
                    if (LOG.isWarnEnabled()) {
                        LOG.warn(
                            "Updated views queue exceeded: " +
                                updatedViews.getICEfacesID());
                    }
                    _updatedViewsQueue.clear();
                    Properties _messageProperties = new Properties();
                    _messageProperties.setStringProperty(
                        Message.DESTINATION_SERVLET_CONTEXT_PATH,
                        sessionManager.getServletContextPath(
                            updatedViews.getICEfacesID()));
                    pushServerMessageService.publish(
                        updatedViews.getICEfacesID(),
                        _messageProperties,
                        UPDATED_VIEWS_QUEUE_EXCEEDED_MESSAGE_TYPE,
                        MessageServiceClient.PUSH_TOPIC_NAME);
                }
            }
        }
    }

    public void remove(final String iceFacesId) {
        if (iceFacesId != null && iceFacesId.trim().length() != 0) {
            synchronized (updatedViewsQueueMap) {
                if (updatedViewsQueueMap.containsKey(iceFacesId)) {
                    updatedViewsQueueMap.remove(iceFacesId);
                }
            }
        }
    }

    public void remove(final String iceFacesId, final String viewNumber) {
        if (iceFacesId != null && iceFacesId.trim().length() != 0) {
            synchronized (updatedViewsQueueMap) {
                if (updatedViewsQueueMap.containsKey(iceFacesId)) {
                    UpdatedViewsQueue _updatedViewsQueue =
                        (UpdatedViewsQueue)updatedViewsQueueMap.get(iceFacesId);
                    Iterator _iterator = _updatedViewsQueue.iterator();
                    while (_iterator.hasNext()) {
                        UpdatedViews _updatedViews =
                            (UpdatedViews)_iterator.next();
                        if (_updatedViews.contains(viewNumber)) {
                            _updatedViews.remove(viewNumber);
                            if (_updatedViews.size() == 0) {
                                _iterator.remove();
                            }
                        }
                    }
                }
            }
        }
    }

    public void setUpdatedViewsQueueSize(final int updatedViewsQueueSize)
    throws IllegalArgumentException {
        if (updatedViewsQueueSize <= 0) {
            throw
                new IllegalArgumentException(
                    "illegal updated views queue size: " +
                        updatedViewsQueueSize);
        }
        this.updatedViewsQueueSize = updatedViewsQueueSize;
    }

    protected UpdatedViewsQueue newUpdatedViewsQueue(
        final String iceFacesId,
        final UpdatedViewsManager updatedViewsManager) {
        
        return new UpdatedViewsQueue(iceFacesId, updatedViewsManager);
    }
}
