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

package com.icesoft.faces.component.outputconnectionstatus;

import com.icesoft.faces.component.CSS_DEFAULT;
import com.icesoft.faces.component.ext.taglib.Util;

import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

/**
 * OutputConnectionStatus is a JSF component class representing an ICEfaces
 * output connection status state indicator.
 * <p/>
 * This component extends the JSF HtmlPanelGroup component.
 * <p/>
 * By default the OutputConnectionStatus is rendered by the
 * "com.icesoft.faces.OutputConnectionStatusRenderer" renderer type.
 */

public class OutputConnectionStatus extends HtmlPanelGroup {
    public static final String COMPONENT_TYPE =
            "com.icesoft.faces.OutputConnectionStatus";
    public static final String RENDERER_TYPE =
            "com.icesoft.faces.OutputConnectionStatusRenderer";
    public static final String COMPONENT_FAMILY = "javax.faces.Panel";
    private static final String DEFAULT_LABEL = "";
    private java.lang.String style;
    private java.lang.String styleClass;
    private java.lang.String inactiveLabel;
    private java.lang.String activeLabel;
    private java.lang.String cautionLabel;
    private java.lang.String disconnectedLabel;
    private java.lang.String renderedOnUserRole;
    private Boolean showPopupOnDisconnect; // ICE-2621
    private Boolean displayHourglassWhenActive;

    /**
     * <p>Return the value of the <code>COMPONENT_FAMILY</code> of this
     * component.</p>
     */
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    /**
     * <p>Return the value of the <code>RENDERER_TYPE</code> of this
     * component.</p>
     */
    public String getRendererType() {
        return RENDERER_TYPE;
    }

    /**
     * <p>Return the value of the <code>COMPONENT_TYPE</code> of this
     * component.</p>
     */
    public String getComponentType() {
        return COMPONENT_TYPE;
    }

    /**
     * <p>Return the value of the <code>activeClass</code> property.</p>
     */
    public String getActiveClass() {
        return Util.getQualifiedStyleClass(this,
                CSS_DEFAULT.OUTPUT_CONNECTION_STATUS_DEFAULT_ACTIVE_CLASS);
    }

    /**
     * <p>Set the value of the <code>activeLabel</code> property.</p>
     */
    public void setActiveLabel(java.lang.String activeLabel) {
        this.activeLabel = activeLabel;
    }

    /**
     * <p>Return the value of the <code>activeLabel</code> property.</p>
     */
    public String getActiveLabel() {
        if (activeLabel != null) {
            return activeLabel;
        }
        ValueBinding vb = getValueBinding("activeLabel");
        return vb != null ? (String) vb.getValue(getFacesContext()) :
                DEFAULT_LABEL;
    }

    /**
     * <p>Return the value of the <code>cautionClass</code> property.</p>
     */
    public String getCautionClass() {
        return Util.getQualifiedStyleClass(this,
                CSS_DEFAULT.OUTPUT_CONNECTION_STATUS_DEFAULT_CAUTION_CLASS);
    }

    /**
     * <p>Set the value of the <code>cautionLabel</code> property.</p>
     */
    public void setCautionLabel(java.lang.String cautionLabel) {
        this.cautionLabel = cautionLabel;
    }

    /**
     * <p>Return the value of the <code>cautionLabel</code> property.</p>
     */
    public String getCautionLabel() {
        if (cautionLabel != null) {
            return cautionLabel;
        }
        ValueBinding vb = getValueBinding("cautionLabel");
        return vb != null ? (String) vb.getValue(getFacesContext()) :
                DEFAULT_LABEL;
    }

    /**
     * <p>Return the value of the <code>disconnectedClass</code> property.</p>
     */
    public String getDisconnectedClass() {
        return Util.getQualifiedStyleClass(this,
                CSS_DEFAULT.OUTPUT_CONNECTION_STATUS_DEFAULT_DISCONNECT_CLASS);
    }

    /**
     * <p>Set the value of the <code>disconnectedLabel</code> property.</p>
     */
    public void setDisconnectedLabel(java.lang.String disconnectedLabel) {
        this.disconnectedLabel = disconnectedLabel;
    }

    /**
     * <p>Return the value of the <code>disconnectedLabel</code> property.</p>
     */
    public String getDisconnectedLabel() {
        if (disconnectedLabel != null) {
            return disconnectedLabel;
        }
        ValueBinding vb = getValueBinding("disconnectedLabel");
        return vb != null ? (String) vb.getValue(getFacesContext()) :
                DEFAULT_LABEL;
    }

    /**
     * <p>Return the value of the <code>inactiveClass</code> property.</p>
     */
    public String getInactiveClass() {
        return Util.getQualifiedStyleClass(this,
                CSS_DEFAULT.OUTPUT_CONNECTION_STATUS_DEFAULT_INACTIVE_CLASS);
    }

    /**
     * <p>Set the value of the <code>inactiveLabel</code> property.</p>
     */
    public void setInactiveLabel(java.lang.String inactiveLabel) {
        this.inactiveLabel = inactiveLabel;
    }

    /**
     * <p>Return the value of the <code>inactiveLabel</code> property.</p>
     */
    public String getInactiveLabel() {
        if (inactiveLabel != null) {
            return inactiveLabel;
        }
        ValueBinding vb = getValueBinding("inactiveLabel");
        return vb != null ? (String) vb.getValue(getFacesContext()) :
                DEFAULT_LABEL;
    }

    /**
     * <p>Set the value of the <code>style</code> property.</p>
     */
    public void setStyle(java.lang.String style) {
        this.style = style;
    }

    /**
     * <p>Return the value of the <code>style</code> property.</p>
     */
    public String getStyle() {
        if (style != null) {
            return style;
        }
        ValueBinding vb = getValueBinding("style");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>styleClass</code> property.</p>
     */
    public void setStyleClass(java.lang.String styleClass) {
        this.styleClass = styleClass;
    }

    /**
     * <p>Return the value of the <code>styleClass</code> property.</p>
     */
    public String getStyleClass() {
        return Util.getQualifiedStyleClass(this,
                styleClass,
                CSS_DEFAULT.OUTPUT_CONNECTION_STATUS_DEFAULT_STYLE_CLASS,
                "styleClass");
    }


    /**
     * <p>Set the value of the <code>renderedOnUserRole</code> property.</p>
     */
    public void setRenderedOnUserRole(String renderedOnUserRole) {
        this.renderedOnUserRole = renderedOnUserRole;
    }

    /**
     * <p>Return the value of the <code>renderedOnUserRole</code> property.</p>
     */
    public String getRenderedOnUserRole() {
        if (renderedOnUserRole != null) {
            return renderedOnUserRole;
        }
        ValueBinding vb = getValueBinding("renderedOnUserRole");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Return the value of the <code>rendered</code> property.</p>
     */
    public boolean isRendered() {
        if (!Util.isRenderedOnUserRole(this)) {
            return false;
        }
        return super.isRendered();
    }

    /**
     * <p>Gets the state of the instance as a <code>Serializable</code>
     * Object.</p>
     */
    public Object saveState(FacesContext context) {
        Object values[] = new Object[10];
        values[0] = super.saveState(context);
        values[1] = style;
        values[2] = styleClass;
        values[3] = inactiveLabel;
        values[4] = activeLabel;
        values[5] = cautionLabel;
        values[6] = disconnectedLabel;
        values[7] = renderedOnUserRole;
        values[8] = showPopupOnDisconnect;
        values[9] = displayHourglassWhenActive;
        return ((Object) (values));
    }

    /**
     * <p>Perform any processing required to restore the state from the entries
     * in the state Object.</p>
     */
    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        style = (String) values[1];
        styleClass = (String) values[2];
        inactiveLabel = (String) values[3];
        activeLabel = (String) values[4];
        cautionLabel = (String) values[5];
        disconnectedLabel = (String) values[6];
        renderedOnUserRole = (String) values[7];
        showPopupOnDisconnect = (Boolean) values[8];
        displayHourglassWhenActive = (Boolean) values[9];
    }

    public boolean isShowPopupOnDisconnect() {
        if (showPopupOnDisconnect != null) return showPopupOnDisconnect.booleanValue();
        ValueBinding vb = getValueBinding("showPopupOnDisconnect");
        if (vb == null) return false;
        Object value = vb.getValue(getFacesContext());
        if (value == null) return false;
        return ((Boolean) value).booleanValue();
    }

    public void setShowPopupOnDisconnect(boolean showPopupOnDisconnect) {
        this.showPopupOnDisconnect = Boolean.valueOf(showPopupOnDisconnect);
    }

    public boolean isDisplayHourglassWhenActive() {
        if (displayHourglassWhenActive != null) return displayHourglassWhenActive.booleanValue();
        ValueBinding vb = getValueBinding("displayHourglassWhenActive");
        if (vb == null) return false;
        Object value = vb.getValue(getFacesContext());
        if (value == null) return false;
        return ((Boolean) value).booleanValue();
    }

    public void setDisplayHourglassWhenActive(boolean displayHourglassWhenActive) {
        this.displayHourglassWhenActive = Boolean.valueOf(displayHourglassWhenActive);
    }
}
