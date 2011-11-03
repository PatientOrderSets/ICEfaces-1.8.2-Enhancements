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

package com.icesoft.faces.async.render;

import edu.emory.mathcs.backport.java.util.concurrent.LinkedBlockingQueue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The SingleEntryQueue is used by the {@link RenderHub} to queue up {@link
 * Renderable}s for render calls.  The queue is a specialized version of a
 * LinkedBlockingQueue that holds, at most, one instance of a Renderable.
 * <p/>
 * To clarify, let's say a Renderable is initially offered to the queue.  It is
 * the first one so there is no rendering calls being executed and the queue is
 * empty.  The Renderable is accepted into the queue and then is removed from
 * the queue and a render call is executed.
 * <p/>
 * While that render call is underway, the same Renderable is again offered to
 * the queue. Since it is not in the queue yet, it is again accepted.
 * <p/>
 * But now we suppose that it's waiting to be pulled off the queue (the thread
 * pools is busy) and the same Renderable is again offered (Renderable equality
 * is based on the equality of the internal {@link com.icesoft.faces.webapp.xmlhttp.PersistentFacesState
 * PersistentFacesState} it contains). This time it is not accepted because the
 * Renderable is already on the queue and any subsequent render calls should
 * take care of all changes to the underlying DOM.  This is how the render calls
 * for a given Renderable are efficiently coalesced.
 *
 * @author ICEsoft Technologies, Inc.
 */
class SingleEntryQueue extends LinkedBlockingQueue {

    private static Log log = LogFactory.getLog(SingleEntryQueue.class);

    public SingleEntryQueue(int capacity) {
        super(capacity);
    }

    public boolean offer(Object objectToOffer) {
        if (this.contains(objectToOffer)) {
            if (log.isTraceEnabled()) {
                log.trace("object is already in work queue: " + objectToOffer);
            }
            //It may seem counter-intuitive but if we discover that the
            //Renderable is already in the queue, we should not call offer()
            //on the parent but we should return true.  It should still be
            //considered a successful operation as far as the thread pool is
            //concerned - a successful coalescing of the render calls.
            return true;
        }
        return super.offer(objectToOffer);
    }
}
