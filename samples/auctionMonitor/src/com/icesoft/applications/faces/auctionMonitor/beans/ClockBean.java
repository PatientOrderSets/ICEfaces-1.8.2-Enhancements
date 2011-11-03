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

package com.icesoft.applications.faces.auctionMonitor.beans;

import com.icesoft.faces.async.render.IntervalRenderer;
import com.icesoft.faces.async.render.RenderManager;
import com.icesoft.faces.async.render.Renderable;
import com.icesoft.faces.context.DisposableBean;
import com.icesoft.faces.webapp.xmlhttp.FatalRenderingException;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesState;
import com.icesoft.faces.webapp.xmlhttp.RenderingException;
import com.icesoft.faces.webapp.xmlhttp.TransientRenderingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class used to control the background clock of the entire auction monitor By
 * queuing a render call every tickInterval (default 1000) milliseconds, this
 * class allows the auction monitor UI to have ticking clocks In addition this
 * class will help AuctionBean maintain a list of the number of users online
 * through incrementUsers and decrementUsers
 */
public class ClockBean implements Renderable, DisposableBean {
    private static Log log = LogFactory.getLog(ClockBean.class);
    private IntervalRenderer clock;
    private static int tickInterval = 1000;
    private String autoLoad = " ";
    private PersistentFacesState state = null;

    private static final String AUTO_LOAD = "ClockBean-Loaded";
    private static final String INTERVAL_RENDERER_GROUP = "clock";

    public ClockBean() {
        state = PersistentFacesState.getInstance();
        AuctionBean.incrementUsers();
    }

    public String getAutoLoad() {
        if (" ".equals(autoLoad)) {
            autoLoad = AUTO_LOAD;
        }
        return autoLoad;
    }

    public void setTickInterval(int interval) {
        tickInterval = interval;
    }

    public int getTickInterval() {
        return tickInterval;
    }

    public String updateInterval()  {
        if (clock.getInterval() != tickInterval) {
            clock.requestStop();
            if (tickInterval > 500)  {
                clock.setInterval(tickInterval);
                clock.requestRender();
            }
        }
        return "success";
    }

    public void setRenderManager(RenderManager manager) {
        if (manager != null) {
            clock = manager.getIntervalRenderer(INTERVAL_RENDERER_GROUP);
            clock.add(this);
            updateInterval();
        }
    }

    /**
     * Method to get the render manager, just return null to satisfy WAS
     */
    public RenderManager getRenderManager() {
        return null;
    }

    public PersistentFacesState getState() {
        return state;
    }

    public void renderingException(RenderingException renderingException) {
        if (log.isDebugEnabled() &&
                renderingException instanceof TransientRenderingException) {
            log.debug("ClockBean Transient Rendering exception:", renderingException);
        } else if (renderingException instanceof FatalRenderingException) {
            if (log.isDebugEnabled()) {
                log.debug("ClockBean Fatal rendering exception: ", renderingException);
            }
            performCleanup();
        }
    }

    protected boolean performCleanup() {
        try {
            if (clock != null && clock.contains(this)) {
                clock.remove(this);
                AuctionBean.decrementUsers();
            }
            return true;
        } catch (Exception failedCleanup) {
            if (log.isErrorEnabled()) {
                log.error("Failed to cleanup a clock bean", failedCleanup);
            }
        }
        return false;
    }

    public void dispose() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("ClockBean Dispose called - cleaning up");
        }
        performCleanup();
    }
}
