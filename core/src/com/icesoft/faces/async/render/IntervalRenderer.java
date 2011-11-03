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
 * The IntervalRenderer is type of {@link GroupAsyncRenderer} that is used to
 * request a single render pass on a group of Renderables.  The render pass is
 * executed repeatedly on a set interval.  The interval is measured from the
 * start of one render pass to the start of the next render pass so does not
 * take the time to complete the render pass into account.  If the time to
 * complete the render pass exceeds the interval then the next render pass is
 * done on a best effort basis.  IntervalRenderers can be created and used
 * directly but it is recommended to use the RenderManager to create and managed
 * named render groups.
 *
 * @author ICEsoft Technologies, Inc.
 * @see RenderManager, GroupAsyncRenderer
 */
public class IntervalRenderer extends OnDemandRenderer implements Runnable {

    private static Log log = LogFactory.getLog(IntervalRenderer.class);

    private long interval = 60000;
    private boolean isStarted = false;
    private ScheduledFuture future;

    /**
     * Get the currently specified interval.  If no interval has been explicitly
     * set, then the default interval value (60000 ms) is used.
     *
     * @return The current interval value in ms.
     */
    public long getInterval() {
        return interval;
    }

    /**
     * Set the interval to wait before executing the next render pass.  Once the
     * rendering has been started, setting the interval has no effect.
     *
     * @param interval The time in ms to wait before starting the next render
     *                 pass.
     */
    public void setInterval(long interval) {
        this.interval = interval;
    }

    /**
     * Schedules a render pass on the group of Renderables using the interval
     * value specified using {@link #setInterval}.
     */
    public void requestRender() {
        if (isStarted) {
            return;
        }
        isStarted = true;
        future =
            RenderManager.getInstance().getScheduledService().
                scheduleAtFixedRate(
                    this, interval, interval, TimeUnit.MILLISECONDS);
        if (log.isDebugEnabled()) {
            log.debug(
                    "interval render started: interval is " + interval + " ms");
        }
    }

    public void requestStop() {
        super.requestStop();
        if (future != null && !future.isDone()) {
            future.cancel(false);
        }
        isStarted = false;
    }

    public void run() {
        super.requestRender();
    }

    public void dispose() {
        super.dispose();
        future = null;
    }
}

