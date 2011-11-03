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

package com.icesoft.faces.component.panelpositioned;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Used to track changes to positioned panel instances
 */
public class PanelPositionedModel implements Serializable {

    private List column;
    private Map idIndex = new HashMap();
    private int max = 0;

    public static PanelPositionedModel build() {

        PanelPositionedModel result = new PanelPositionedModel();

        return result;
    }


    public static PanelPositionedModel resetInstance(FacesContext context,
                                                     UIComponent component) {
        Map sessionMap = context.getExternalContext().getSessionMap();

        PanelPositionedModel result = build();
        sessionMap.put(getName(context, component), result);

        return result;
    }

    private static String getName(FacesContext context, UIComponent component) {
        return PanelPositionedModel.class.getName() + ":" +
                component.getClientId(context);
    }

    public static PanelPositionedModel getInstance(FacesContext context,
                                                   UIComponent component) {
        Map sessionMap = context.getExternalContext().getSessionMap();
        PanelPositionedModel result = (PanelPositionedModel) sessionMap
                .get(getName(context, component));
        return result;
    }


    private PanelPositionedModel() {
        this.column = new ArrayList();
    }

    public void setIndex(String id, int index) {
        idIndex.put(id, new Integer(index));
    }

    public int getIndex(String id) {
        Integer I = (Integer) idIndex.get(id);
        if (I != null) {
            return I.intValue();
        }
        return -1;

    }

    public int size() {
        return this.column.size();
    }

}
