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

package com.icesoft.faces.component.menubar;

import com.icesoft.faces.component.ext.taglib.Util;
import com.icesoft.faces.util.CoreUtils;


/**
 *
 */
public class MenuItemRadio extends MenuItem {

    private static final String DEFAULT_ICON_UNSELECTED =
            "/xmlhttp/css/xp/css-images/menu_radio.gif";
    private static final String DEFAULT_ICON_SELECTED =
            "/xmlhttp/css/xp/css-images/menu_radio_selected.gif";

    private boolean selected;

    /**
     * <p>Return the value of the <code>COMPONENT_FAMILY</code> of this
     * component.</p>
     */
    public String getFamily() {
        return "com.icesoft.faces.MenuNodeRadio";
    }

    /* (non-Javadoc)
     * @see javax.faces.component.UIComponent#getRendererType()
     */
    public String getRendererType() {
        return "com.icesoft.faces.View";
    }


    /**
     * <p>Return the value of the <code>selected</code> property.</p>
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * <p>Set the value of the <code>selected</code> property.</p>
     */
    public void setSelected(boolean newValue) {
        selected = newValue;
    }

    /**
     * <p>Return the value of the <code>unselectedIcon</code> property.</p>
     */
    public String getUnselectedIcon() {
        return CoreUtils.resolveResourceURL(getFacesContext(),
               DEFAULT_ICON_UNSELECTED);
    }

    /**
     * <p>Return the value of the <code>selectedIcon</code> property.</p>
     */
    public String getSelectedIcon() {
        return CoreUtils.resolveResourceURL(getFacesContext(),
               DEFAULT_ICON_SELECTED);
    }


}
