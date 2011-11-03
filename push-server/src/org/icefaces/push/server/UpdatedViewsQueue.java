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

import java.util.Iterator;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UpdatedViewsQueue {
    private static final Log LOG = LogFactory.getLog(UpdatedViewsQueue.class);

    protected final String iceFacesId;
    protected final TreeSet updatedViewsQueue = new TreeSet();

    protected int size;
    protected UpdatedViewsManager updatedViewsManager;

    public UpdatedViewsQueue(
        final String iceFacesId,
        final UpdatedViewsManager updatedViewsManager) {

        this.iceFacesId = iceFacesId;
        if (updatedViewsManager != null) {
            this.updatedViewsManager = updatedViewsManager;
            this.size =
                this.updatedViewsManager.getUpdatedViewsQueueSize();
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Created " + this);
        }
    }

    public void add(final UpdatedViews updatedViews)
    throws UpdatedViewsQueueExceededException {
        if (updatedViews != null &&
            updatedViews.getICEfacesID().equals(iceFacesId)) {

            if (updatedViewsManager != null && getSize() == size) {
                throw new UpdatedViewsQueueExceededException();
            }
            updatedViewsQueue.add(updatedViews);
            if (LOG.isDebugEnabled()) {
                LOG.debug("UpdatedViews added to " + this);
            }
        }
    }

    public void addAll(final UpdatedViewsQueue updatedViewsQueue)
    throws UpdatedViewsQueueExceededException {
        if (updatedViewsQueue != null && !updatedViewsQueue.isEmpty() &&
            updatedViewsQueue.iceFacesId.equals(iceFacesId)) {

            final Iterator _updatedViews = updatedViewsQueue.iterator();
            while (_updatedViews.hasNext()) {
                add((UpdatedViews)_updatedViews.next());
            }
        }
    }

    public void clear() {
        updatedViewsQueue.clear();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Cleared " + this);
        }
    }

    public String getICEfacesID() {
        return iceFacesId;
    }

    public long getSequenceNumber() {
        if (updatedViewsQueue.size() != 0) {
            return ((UpdatedViews)updatedViewsQueue.last()).getSequenceNumber();
        } else {
            return 0;
        }
    }

    public int getSize() {
        return updatedViewsQueue.size();
    }

    public boolean isEmpty() {
        return updatedViewsQueue.isEmpty();
    }

    public Iterator iterator() {
        return updatedViewsQueue.iterator();
    }

    public void purge(final long sequenceNumber) {
        if (sequenceNumber > 0) {
            final Iterator _updatedViewsIterator = iterator();
            while (_updatedViewsIterator.hasNext()) {
                final UpdatedViews _updatedViews =
                    (UpdatedViews)_updatedViewsIterator.next();
                if (_updatedViews.getSequenceNumber() > sequenceNumber) {
                    break;
                }
                _updatedViewsIterator.remove();
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Purged up to " + sequenceNumber + " from " + this);
            }
        }
    }

    public String toString() {
        return
            "UpdatedViewsQueue [" + iceFacesId + "]:\r\n" +
            "        sequence number : " + getSequenceNumber() + "\r\n" +
            "        size            : " + getSize();
    }
}
