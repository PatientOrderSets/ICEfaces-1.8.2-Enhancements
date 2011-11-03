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

import edu.emory.mathcs.backport.java.util.concurrent.ScheduledExecutorService;
import edu.emory.mathcs.backport.java.util.concurrent.ScheduledFuture;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The DelayRenderer is type of {@link GroupAsyncRenderer} that is used to
 * request a single render pass on a group of Renderables.  The render pass is
 * executed after a specified time delay.  DelayRenderers can be created and
 * used directly but it is recommended to use the RenderManager to create and
 * managed named render groups.
 *
 * @author ICEsoft Technologies, Inc.
 * @see RenderManager, GroupAsyncRenderer
 */
public class DelayRenderer extends OnDemandRenderer implements Runnable {

    private static Log log = LogFactory.getLog(IntervalRenderer.class);
    private ScheduledFuture future;

    /**
     * The delay value to use before request a render pass on the group.
     */
    private long delay = 60000;

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    /**
     * Schedules a render pass on the group of Renderables using the delay value
     * specified using {@link #setDelay}.  If a delay value was not
     * explicitly set, then the default delay value (60000 ms) is used.
     */
    public void requestRender() {
        future =
            RenderManager.getInstance().getScheduledService().
                schedule(this, delay, TimeUnit.MILLISECONDS);
        if (log.isDebugEnabled()) {
            log.debug("delay render started: delay is " + delay + " ms");
        }
    }

    public void requestStop() {
        super.requestStop();
        if (future != null && !future.isDone()) {
            future.cancel(false);
        }
    }

    public void run() {
        super.requestRender();
    }

    public void dispose() {
        super.dispose();
        future = null;
    }
}
