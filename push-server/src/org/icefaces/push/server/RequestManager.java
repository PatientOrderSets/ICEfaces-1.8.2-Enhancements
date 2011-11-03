package org.icefaces.push.server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RequestManager {
    private static final Log LOG = LogFactory.getLog(RequestManager.class);

    private final Map pendingRequestMap = new HashMap();

    {
        if (LOG.isDebugEnabled()) {
            new Thread(
                new Runnable() {
                    public void run() {
                        while (true) {
                            StringBuffer _pendingRequests = new StringBuffer();
                            synchronized (pendingRequestMap) {
                                Iterator _entries =
                                    pendingRequestMap.entrySet().iterator();
                                while (_entries.hasNext()) {
                                    Map.Entry _entry =
                                        (Map.Entry)_entries.next();
                                    _pendingRequests.
                                        append(_entry.getKey()).append(" - ").
                                            append(_entry.getValue()).
                                            append(" [").
                                            append(
                                                ((Handler)_entry.getValue()).
                                                    getRequest().
                                                    getRemoteAddr()).
                                            append("]\r\n");
                                }
                            }
                            LOG.debug(
                                "Pending requests:\r\n\r\n" + _pendingRequests);
                            try {
                                Thread.sleep(60000);
                            } catch (InterruptedException exception) {
                                // do nothing.
                            }
                        }
                    }
                }
            ).start();
        }
    }

    /**
     * <p>
     *   Pulls the pending request, represented as a handler, for the specified
     *   <code>iceFacesId</code> off the queue.
     * </p>
     * <p>
     *   If <code>iceFacesId</code> is <code>null</code> or empty, or if there
     *   is no request in the queue for the <code>iceFacesId</code>,
     *   <code>null</code> is returned.
     * </p>
     *
     * @param      iceFacesIdSet
     *                 the ICEfaces ID set that identifies the session of the
     *                 requester.
     * @return     the request or <code>null</code>.
     * @see        #push(Set, Handler)
     */
    public Handler pull(final Set iceFacesIdSet) {
        if (iceFacesIdSet == null || iceFacesIdSet.isEmpty()) {
            return null;
        }
        synchronized (pendingRequestMap) {
            Iterator _iceFacesIds = iceFacesIdSet.iterator();
            while (_iceFacesIds.hasNext()) {
                Handler _handler = pull((String)_iceFacesIds.next());
                if (_handler != null) {
                    return _handler;
                }
            }
            return null;
        }
    }

    public Handler pull(final String iceFacesId) {
        if (iceFacesId == null || iceFacesId.trim().length() == 0) {
            return null;
        }
        synchronized (pendingRequestMap) {
            Iterator _entries = pendingRequestMap.entrySet().iterator();
            while (_entries.hasNext()) {
                Map.Entry _entry = (Map.Entry)_entries.next();
                if (((Set)_entry.getKey()).contains(iceFacesId)) {
                    _entries.remove();
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(
                            "Unparked pending request: " +
                                "ICEfaces ID set [" + _entry.getKey() + "]");
                    }
                    return (Handler)_entry.getValue();
                }
            }
            return null;
        }
    }

    /**
     * <p>
     *   Pushes the specified <code>handler</code>, representing the request,
     *   for the specified <code>iceFacesId</code> on the queue.
     * </p>
     * <p>
     *   If <code>iceFacesId</code> is <code>null</code> or empty, or
     *   <code>handler</code> is <code>null</code>, nothing is pushed on the
     *   queue.
     * </p>
     *
     * @param      iceFacesIdSet
     *                 the ICEfaces ID set that identifies the session of the
     *                 requester.
     * @param      handler
     *                 the handler that represents the pending request.
     * @see        #pull(Set)
     */
    public void push(final Set iceFacesIdSet, final Handler handler) {
        if (iceFacesIdSet == null || iceFacesIdSet.isEmpty() ||
            handler == null) {

            return;
        }
        synchronized (pendingRequestMap) {
            pendingRequestMap.put(iceFacesIdSet, handler);
            if (LOG.isDebugEnabled()) {
                LOG.debug(
                    "Parked pending request: " +
                        "ICEfaces ID set [" + iceFacesIdSet + "]");
            }
        }
    }
}
