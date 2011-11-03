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

package com.icesoft.faces.component.panelseries;

import com.icesoft.faces.component.CSS_DEFAULT;
import com.icesoft.faces.component.ext.taglib.Util;

import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

/**
 * PanelSeries is a JSF component class representing an ICEfaces panelSeries.
 * <p>The panelSeries component provides a mechanism for dynamically generating
 * a series of repeating child-components within a panel. This component renders
 * its child components in an iterative fashion similar to way the dataTable
 * component renders data rows. However, the panelSeries component is more
 * flexibile in that it can render a series of arbitrarily complex child
 * components. The dataset can be defined and used by implementing the value and
 * var attributes respectively.
 * <p/>
 * By default this component is rendered by the "com.icesoft.faces.PanelSeriesRenderer"
 * renderer type.
 *
 * @version beta 1.0
 */
public class PanelSeries extends UISeries {
    public static final String COMPONENT_TYPE = "com.icesoft.faces.PanelSeries";
    public static final String RENDERER_TYPE = "com.icesoft.faces.PanelSeriesRenderer";
    public static final String COMPONENT_FAMILY = "javax.faces.Panel";
    private String style = null;
    private String styleClass = null;


    public PanelSeries() {
        super();
        setRendererType(RENDERER_TYPE);
    }
    /*
     *  (non-Javadoc)
     * @see javax.faces.component.UIComponent#getFamily()
     */
    public String getFamily() {
        return (COMPONENT_FAMILY);
    }

    /*
     *  (non-Javadoc)
     * @see javax.faces.component.UIComponent#isRendered()
     */
    public boolean isRendered() {
        if (!Util.isRenderedOnUserRole(this)) {
            return false;
        }
        return super.isRendered();
    }


    /**
     * <p>Set the value of the <code>styleClass</code> property.</p>
     *
     * @return style class property value.
     */
    public String getStyleClass() {
        return Util.getQualifiedStyleClass(this, 
                styleClass, 
                CSS_DEFAULT.PANEL_SERIES_DEFAULT_CLASS, 
                "styleClass");
    }

    /**
     * <p>Set the value of the <code>styleClass</code> property.</p>
     */
    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    /**
     * <p>Set the value of the <code>style</code> property.</p>
     *
     * @return style property value.
     */
    public String getStyle() {
        if (style != null) {
            return style;
        }
        ValueBinding vb = getValueBinding("style");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>style</code> property.</p>
     */
    public void setStyle(String style) {
        this.style = style;
    }

    /*
     *  (non-Javadoc)
     * @see com.icesoft.faces.component.panelseries.UISeries#restoreChild(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
     */
    protected void restoreChild(FacesContext facesContext,
                                UIComponent uiComponent) {
        super.restoreChild(facesContext, uiComponent);
        if (uiComponent instanceof UIData) {
            String clientId = uiComponent.getClientId(facesContext);
            Object value = savedChildren.get(clientId);
            ((UIData) uiComponent).setValue(value);
        }
    }

    /*
     *  (non-Javadoc)
     * @see com.icesoft.faces.component.panelseries.UISeries#saveChild(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
     */
    protected void saveChild(FacesContext facesContext,
                             UIComponent uiComponent) {
        super.saveChild(facesContext, uiComponent);
        if (uiComponent instanceof UIData) {
            String clientId = uiComponent.getClientId(facesContext);
            savedChildren.put(clientId, ((UIData) uiComponent).getValue());
        }
    }

    private transient Object values[];
    public void restoreState(FacesContext context, Object state) {
        values = (Object[])state;
        super.restoreState(context, values[0]);
        style = (String)values[1];
        styleClass = (String)values[2];
    }

    public Object saveState(FacesContext context) {
        if(values == null){
            values = new Object[3];
        }
        values[0] = super.saveState(context);
        values[1] = style;
        values[2] = styleClass;
        return values;
    }
    
    
}
