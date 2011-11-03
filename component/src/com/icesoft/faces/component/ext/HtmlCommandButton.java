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

package com.icesoft.faces.component.ext;

import com.icesoft.faces.component.CSS_DEFAULT;
import com.icesoft.faces.component.PORTLET_CSS_DEFAULT;
import com.icesoft.faces.component.ext.taglib.Util;
import com.icesoft.faces.context.BridgeFacesContext;
import com.icesoft.faces.context.effects.CurrentStyle;
import com.icesoft.faces.context.effects.Effect;
import com.icesoft.faces.context.effects.JavascriptContext;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;


/**
 * This is an extension of javax.faces.component.html.HtmlCommandButton, which
 * provides some additional behavior to this component such as: <ul> <li>default
 * classes for enabled and disabled state</li> <li>provides full and partial
 * submit mechanism</li> <li>changes the component's enabled and rendered state
 * based on the authentication</li> <li>adds effects to the component</li> <ul>
 */
public class HtmlCommandButton
        extends javax.faces.component.html.HtmlCommandButton {

    /**
     * String constant component type
     */
    public static final String COMPONENT_TYPE =
            "com.icesoft.faces.HtmlCommandButton";
    /**
     * String constant renderer type
     */
    public static final String RENDERER_TYPE = "com.icesoft.faces.Button";
    private String styleClass = null;
    private String enabledOnUserRole = null;
    private String renderedOnUserRole = null;
    private Effect effect = null;
    private Boolean visible = null;
    private static final boolean DEFAULT_VISIBLE = true;
    private Boolean partialSubmit = null;

    private Effect onclickeffect;
    private Effect ondblclickeffect;
    private Effect onmousedowneffect;
    private Effect onmouseupeffect;
    private Effect onmousemoveeffect;
    private Effect onmouseovereffect;
    private Effect onmouseouteffect;
    //private Effect onchangeeffect;
    //private Effect onreseteffect;
    //private Effect onsubmiteffect;
    private Effect onkeypresseffect;
    private Effect onkeydowneffect;
    private Effect onkeyupeffect;
    private CurrentStyle currentStyle;

    private String panelConfirmation = null;
    
    /**
     * default no args constructor
     */
    public HtmlCommandButton() {
        super();
        setRendererType(RENDERER_TYPE);
    }

    /* (non-Javadoc)
     * @see javax.faces.component.UIComponentBase#setValueBinding(java.lang.String, javax.faces.el.ValueBinding)
     */
    public void setValueBinding(String s, ValueBinding vb) {
        if (s != null && s.indexOf("effect") != -1) {
            // If this is an effect attribute make sure Ice Extras is included
            JavascriptContext.includeLib(JavascriptContext.ICE_EXTRAS,
                                         getFacesContext());
        }
        super.setValueBinding(s, vb);
    }

    /**
     * <p>Set the value of the <code>dir</code> property.</p>
     *
     * @param partialSubmit
     */
    public void setPartialSubmit(boolean partialSubmit) {
        this.partialSubmit = Boolean.valueOf(partialSubmit);
    }

    /**
     * <p>Return the value of the <code>partialSubmit</code> property.</p>
     *
     * @return boolean partialSubmit
     */
    public boolean getPartialSubmit() {
        if (partialSubmit != null) {
            return partialSubmit.booleanValue();
        }
        ValueBinding vb = getValueBinding("partialSubmit");
        Boolean boolVal =
                vb != null ? (Boolean) vb.getValue(getFacesContext()) : null;
        return boolVal != null ? boolVal.booleanValue() :
               Util.isParentPartialSubmit(this);
    }

    /**
     * <p>Set the value of the <code>effect</code> property.</p>
     *
     * @param effect
     */
    public void setEffect(Effect effect) {
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
        this.effect = effect;
    }

    /**
     * <p>Return the value of the <code>effect</code> property.</p>
     *
     * @return Effect effect
     */
    public Effect getEffect() {
        if (effect != null) {
            return effect;
        }
        ValueBinding vb = getValueBinding("effect");
        return vb != null ? (Effect) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>visible</code> property.</p>
     *
     * @param visible
     */
    public void setVisible(boolean visible) {
        this.visible = Boolean.valueOf(visible);
    }

    /**
     * <p>Return the value of the <code>visible</code> property.</p>
     *
     * @return boolean visible
     */
    public boolean isVisible() {
        if (visible != null) {
            return visible.booleanValue();
        }
        ValueBinding vb = getValueBinding("visible");
        Boolean boolVal =
                vb != null ? (Boolean) vb.getValue(getFacesContext()) : null;
        return boolVal != null ? boolVal.booleanValue() : DEFAULT_VISIBLE;
    }


    /**
     * <p>Return the value of the <code>visible</code> property.</p>
     *
     * @return boolean visible
     */
    public boolean getVisible() {
        if (visible != null) {
            return visible.booleanValue();
        }
        ValueBinding vb = getValueBinding("visible");
        Boolean boolVal =
                vb != null ? (Boolean) vb.getValue(getFacesContext()) : null;
        return boolVal != null ? boolVal.booleanValue() : DEFAULT_VISIBLE;
    }

    /**
     * <p>Return the value of the <code>disabled</code> property.</p>
     *
     * @return boolean disabled
     */
    public boolean isDisabled() {
        if (!Util.isEnabledOnUserRole(this)) {
            return true;
        } else {            
            if (this.disabled_set) {
                return this.disabled;
            }
    
            ValueBinding _vb = getValueBinding("disabled");
            if (_vb != null) {
                Object _result = _vb.getValue(getFacesContext());
                if (_result == null) {
                    return false;
                } else {
                    return ((Boolean) _result).booleanValue();
                }
            } else {
                return this.disabled;
            }
        }
    }

    private boolean disabled = false;
    private boolean disabled_set = false;


  /**
   * <p>Set the value of the <code>disabled</code> property.</p>
   */
  public void setDisabled(boolean disabled) {
    this.disabled = disabled;
    this.disabled_set = true;
  }
    
    

    /**
     * <p>Set the value of the <code>enabledOnUserRole</code> property.</p>
     *
     * @param enabledOnUserRole
     */
    public void setEnabledOnUserRole(String enabledOnUserRole) {
        this.enabledOnUserRole = enabledOnUserRole;
    }

    /**
     * <p>Return the value of the <code>enabledOnUserRole</code> property.</p>
     *
     * @return String enabledOnUserRole
     */
    public String getEnabledOnUserRole() {
        if (enabledOnUserRole != null) {
            return enabledOnUserRole;
        }
        ValueBinding vb = getValueBinding("enabledOnUserRole");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>renderedOnUserRole</code> property.</p>
     *
     * @param renderedOnUserRole
     */
    public void setRenderedOnUserRole(String renderedOnUserRole) {
        this.renderedOnUserRole = renderedOnUserRole;
    }

    /**
     * <p>Return the value of the <code>renderedOnUserRole</code> property.</p>
     *
     * @return String renderedOnUserRole
     */
    public String getRenderedOnUserRole() {
        if (renderedOnUserRole != null) {
            return renderedOnUserRole;
        }
        ValueBinding vb = getValueBinding("renderedOnUserRole");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>styleClass</code> property.</p>
     *
     * @param styleClass
     */
    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    /**
     * <p>Return the value of the <code>styleClass</code> property.</p>
     *
     * @return String styleClass
     */
    public String getStyleClass() {
        return Util.getQualifiedStyleClass(this, 
                                styleClass,
                                CSS_DEFAULT.COMMAND_BTN_DEFAULT_STYLE_CLASS,
                                "styleClass",
                                isDisabled(),
                                PORTLET_CSS_DEFAULT.PORTLET_FORM_BUTTON);
    }

    /**
     * <p>Return the value of the <code>rendered</code> property.</p>
     *
     * @return boolean rendered
     */
    public boolean isRendered() {
        if (!Util.isRenderedOnUserRole(this)) {
            return false;
        }
        return super.isRendered();
    }

    /**
     * <p>Return the value of the <code>onclickeffect</code> property.</p>
     *
     * @return Effect onclickEffect
     */
    public Effect getOnclickeffect() {
        if (onclickeffect != null) {
            return onclickeffect;
        }
        ValueBinding vb = getValueBinding("onclickeffect");

        return vb != null ? (Effect) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>onclickeffect</code> property.</p>
     *
     * @param onclickeffect
     */
    public void setOnclickeffect(Effect onclickeffect) {
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
        this.onclickeffect = onclickeffect;
    }

    /**
     * <p>Return the value of the <code>ondblclickeffect</code> property.</p>
     *
     * @return Effect
     */
    public Effect getOndblclickeffect() {
        if (ondblclickeffect != null) {
            return ondblclickeffect;
        }
        ValueBinding vb = getValueBinding("ondblclickeffect");

        return vb != null ? (Effect) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>ondblclickeffect</code> property.</p>
     *
     * @param ondblclickeffect
     */
    public void setOndblclickeffect(Effect ondblclickeffect) {
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
        this.ondblclickeffect = ondblclickeffect;
    }

    /**
     * <p>Return the value of the <code>onmousedowneffect</code> property.</p>
     *
     * @return Effect
     */
    public Effect getOnmousedowneffect() {
        if (onmousedowneffect != null) {
            return onmousedowneffect;
        }
        ValueBinding vb = getValueBinding("onmousedowneffect");

        return vb != null ? (Effect) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>onmousedowneffect</code> property.</p>
     *
     * @param onmousedowneffect
     */
    public void setOnmousedowneffect(Effect onmousedowneffect) {
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
        this.onmousedowneffect = onmousedowneffect;
    }

    /**
     * <p>Return the value of the <code>onmouseupeffect</code> property.</p>
     *
     * @return Effect
     */
    public Effect getOnmouseupeffect() {
        if (onmouseupeffect != null) {
            return onmouseupeffect;
        }
        ValueBinding vb = getValueBinding("onmouseupeffect");

        return vb != null ? (Effect) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>onmouseupeffect</code> property.</p>
     *
     * @param onmouseupeffect
     */
    public void setOnmouseupeffect(Effect onmouseupeffect) {
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
        this.onmouseupeffect = onmouseupeffect;
    }

    /**
     * <p>Return the value of the <code>onmousemoveeffect</code> property.</p>
     *
     * @return Effect
     */
    public Effect getOnmousemoveeffect() {
        if (onmousemoveeffect != null) {
            return onmousemoveeffect;
        }
        ValueBinding vb = getValueBinding("onmousemoveeffect");

        return vb != null ? (Effect) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>onmousemoveeffect</code> property.</p>
     *
     * @param onmousemoveeffect
     */
    public void setOnmousemoveeffect(Effect onmousemoveeffect) {
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
        this.onmousemoveeffect = onmousemoveeffect;
    }

    /**
     * <p>Return the value of the <code>onmouseovereffect</code> property.</p>
     *
     * @return Effect
     */
    public Effect getOnmouseovereffect() {
        if (onmouseovereffect != null) {
            return onmouseovereffect;
        }
        ValueBinding vb = getValueBinding("onmouseovereffect");

        return vb != null ? (Effect) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>onmouseovereffect</code> property.</p>
     *
     * @param onmouseovereffect
     */
    public void setOnmouseovereffect(Effect onmouseovereffect) {
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
        this.onmouseovereffect = onmouseovereffect;
    }

    /**
     * <p>Return the value of the <code>onmouseouteffect</code> property.</p>
     *
     * @return Effect
     */
    public Effect getOnmouseouteffect() {
        if (onmouseouteffect != null) {
            return onmouseouteffect;
        }
        ValueBinding vb = getValueBinding("onmouseouteffect");

        return vb != null ? (Effect) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>onmouseouteffect</code> property.</p>
     *
     * @param onmouseouteffect
     */
    public void setOnmouseouteffect(Effect onmouseouteffect) {
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
        this.onmouseouteffect = onmouseouteffect;
    }


    /**
     * <p>Return the value of the <code>onkeypresseffect</code> property.</p>
     *
     * @return Effect
     */
    public Effect getOnkeypresseffect() {
        if (onkeypresseffect != null) {
            return onkeypresseffect;
        }
        ValueBinding vb = getValueBinding("onkeypresseffect");

        return vb != null ? (Effect) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>onkeypresseffect</code> property.</p>
     *
     * @param onkeypresseffect
     */
    public void setOnkeypresseffect(Effect onkeypresseffect) {
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
        this.onkeypresseffect = onkeypresseffect;
    }

    /**
     * <p>Return the value of the <code>onkeydowneffect</code> property.</p>
     *
     * @return Effect
     */
    public Effect getOnkeydowneffect() {
        if (onkeydowneffect != null) {
            return onkeydowneffect;
        }
        ValueBinding vb = getValueBinding("onkeydowneffect");

        return vb != null ? (Effect) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>onkeydowneffect</code> property.</p>
     *
     * @param onkeydowneffect
     */
    public void setOnkeydowneffect(Effect onkeydowneffect) {
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
        this.onkeydowneffect = onkeydowneffect;
    }

    /**
     * <p>Return the value of the <code>onkeyupeffect</code> property.</p>
     *
     * @return Effect
     */
    public Effect getOnkeyupeffect() {
        if (onkeyupeffect != null) {
            return onkeyupeffect;
        }
        ValueBinding vb = getValueBinding("onkeyupeffect");

        return vb != null ? (Effect) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>onkeyupeffect</code> property.</p>
     *
     * @param onkeyupeffect
     */
    public void setOnkeyupeffect(Effect onkeyupeffect) {
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
        this.onkeyupeffect = onkeyupeffect;
    }

    /**
     * <p>Return the value of the <code>currentStyle</code> property.</p>
     *
     * @return CurrentStyle
     */
    public CurrentStyle getCurrentStyle() {
        return currentStyle;
    }

    /**
     * <p>Set the value of the <code>currentStyle</code> property.</p>
     *
     * @param currentStyle
     */
    public void setCurrentStyle(CurrentStyle currentStyle) {
        this.currentStyle = currentStyle;
    }
    
    /**
     * <p>Set the value of the <code>panelConfirmation</code> property.</p>
     *
     * @param panelConfirmation
     */
    public void setPanelConfirmation(String panelConfirmation) {
        this.panelConfirmation = panelConfirmation;
    }

    /**
     * <p>Return the value of the <code>panelConfirmation</code> property.</p>
     *
     * @return String panelConfirmation
     */
    public String getPanelConfirmation() {
        if (panelConfirmation != null) {
            return panelConfirmation;
        }
        ValueBinding vb = getValueBinding("panelConfirmation");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }


    /**
     * <p>Gets the state of the instance as a <code>Serializable</code>
     * Object.</p>
     *
     * @param context
     * @return Object values[]
     */
    public Object saveState(FacesContext context) {
        Object values[] = new Object[21];
        values[0] = super.saveState(context);
        values[1] = enabledOnUserRole;
        values[2] = renderedOnUserRole;
        values[3] = styleClass;
        values[4] = effect;
        values[5] = onclickeffect;
        values[6] = ondblclickeffect;
        values[7] = onmousedowneffect;
        values[8] = onmouseupeffect;
        values[9] = onmousemoveeffect;
        values[10] = onmouseovereffect;
        values[11] = onmouseouteffect;
        values[12] = onkeypresseffect;
        values[13] = onkeydowneffect;
        values[14] = onkeyupeffect;
        values[15] = currentStyle;
        values[16] = visible;
        values[17] = partialSubmit;
        values[18] = disabled ? Boolean.TRUE : Boolean.FALSE;
        values[19] = disabled_set ? Boolean.TRUE : Boolean.FALSE;
        values[20] = panelConfirmation;
        return ((Object) (values));
    }

    /**
     * <p>Perform any processing required to restore the state from the entries
     * in the state Object.</p>
     *
     * @param context
     * @param state
     */
    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        enabledOnUserRole = (String) values[1];
        renderedOnUserRole = (String) values[2];
        styleClass = (String) values[3];
        effect = (Effect) values[4];
        onclickeffect = (Effect) values[5];
        ondblclickeffect = (Effect) values[6];
        onmousedowneffect = (Effect) values[7];
        onmouseupeffect = (Effect) values[8];
        onmousemoveeffect = (Effect) values[9];
        onmouseovereffect = (Effect) values[10];
        onmouseouteffect = (Effect) values[11];
        onkeypresseffect = (Effect) values[12];
        onkeydowneffect = (Effect) values[13];
        onkeyupeffect = (Effect) values[14];
        currentStyle = (CurrentStyle) values[15];
        visible = (Boolean) values[16];
        partialSubmit = (Boolean) values[17];
        disabled = ((Boolean) values[18]).booleanValue();
        disabled_set = ((Boolean) values[19]).booleanValue();
        panelConfirmation = (String) values[20];
    }

    /**
     * This method is used to communicate a focus request from the application
     * to the client browser.
     */
    public void requestFocus() {
        ((BridgeFacesContext) FacesContext.getCurrentInstance())
                .setFocusId("null");
        JavascriptContext.focus(FacesContext.getCurrentInstance(),
                                this.getClientId(
                                        FacesContext.getCurrentInstance()));
    }

}
   

  