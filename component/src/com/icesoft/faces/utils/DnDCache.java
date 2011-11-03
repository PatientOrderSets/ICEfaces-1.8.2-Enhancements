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

package com.icesoft.faces.utils;

import com.icesoft.faces.component.ext.HtmlPanelGroup;
import com.icesoft.faces.component.panelpositioned.PanelPositionedValue;
import com.icesoft.faces.util.CoreUtils;

import javax.faces.context.FacesContext;
import javax.portlet.PortletSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * To get around the limitations of findCompoent in iterative data strutures
 * refrences to UIComponets involved in Drag and Drop are placed in this cache.
 */
public class DnDCache implements java.io.Serializable {

    private static final String SESSION_KEY = "Icesoft_DnDCache_Key";


    private Map dragValues = new HashMap();
    private Map dropValues = new HashMap();
    private Map positionedPanelValues = new HashMap();

    private DnDCache() {
    }


    public static DnDCache getInstance(FacesContext context, boolean encoding) {
        if (CoreUtils.isPortletEnvironment()) {
            PortletSession portletSession = (PortletSession) context.
                        getExternalContext().getSession(false);
            DnDCache cache = (DnDCache) portletSession.getAttribute
                              (SESSION_KEY, PortletSession.APPLICATION_SCOPE);
            if (cache == null) {
                cache = new DnDCache();
                portletSession.setAttribute(SESSION_KEY,cache, 
                        PortletSession.APPLICATION_SCOPE);
            }
            return cache;
        } else {
            String viewId = context.getViewRoot().getViewId();
            Map map = context.getExternalContext().getSessionMap();
            DnDCache cache = (DnDCache) map.get(SESSION_KEY);
            if (cache == null) {
                cache = new DnDCache();
                map.put(SESSION_KEY, cache);
            }
            return cache;
        }
    }


    public void put(String id, HtmlPanelGroup uiComponent,
                    FacesContext context) {
        dragValues.put(id, uiComponent.getDragValue());
        dropValues.put(id, uiComponent.getDropValue());
    }

    public Object getDropValue(String id) {
        return dropValues.get(id);
    }

    public Object getDragValue(String id) {
        return dragValues.get(id);
    }


    public void putPositionPanelValue(String id, List list, int index) {
        positionedPanelValues.put(id, new PanelPositionedValue(list, index));
    }

    public PanelPositionedValue getPositionedPanelValue(String id) {
        return (PanelPositionedValue) positionedPanelValues.get(id);
    }


}
