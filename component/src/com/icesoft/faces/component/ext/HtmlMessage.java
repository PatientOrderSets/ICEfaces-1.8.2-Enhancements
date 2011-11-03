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
import com.icesoft.faces.component.ext.taglib.Util;
import com.icesoft.faces.context.effects.CurrentStyle;
import com.icesoft.faces.context.effects.Effect;
import com.icesoft.faces.context.effects.JavascriptContext;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

/**
 * This is an extension of javax.faces.component.html.HtmlMessage, which
 * provides some additional behavior to this component such as: <ul> <li>changes
 * the component's rendered state based on the authentication</li> <li>adds
 * effects to the component</li> <ul>
 */
public class HtmlMessage extends javax.faces.component.html.HtmlMessage {
    public static final String COMPONENT_TYPE = "com.icesoft.faces.HtmlMessage";
    public static final String RENDERER_TYPE = "com.icesoft.faces.Message";
    private static final boolean DEFAULT_VISIBLE = true;
    private String renderedOnUserRole = null;
    private Effect effect;
    private Boolean visible = null;
    private String errorClass = null;
    private String fatalClass = null;
    private String infoClass = null;
    private String warnClass = null;
    private String styleClass = null;
    private CurrentStyle currentStyle;

    public HtmlMessage() {
        super();
        setRendererType(RENDERER_TYPE);
    }

    public void setValueBinding(String s, ValueBinding vb) {
        if (s != null && s.indexOf("effect") != -1) {
            // If this is an effect attribute make sure Ice Extras is included
            JavascriptContext.includeLib(JavascriptContext.ICE_EXTRAS,
                                         getFacesContext());
        }
        super.setValueBinding(s, vb);
    }

    /**
     * <p>Set the value of the <code>effect</code> property.</p>
     */
    public void setEffect(Effect effect) {
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
        this.effect = effect;
    }

    /**
     * <p>Return the value of the <code>effect</code> property.</p>
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
     */
    public void setVisible(boolean visible) {
        this.visible = Boolean.valueOf(visible);
    }

    /**
     * <p>Return the value of the <code>visible</code> property.</p>
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
     * <p>Return the value of the <code>currentStyle</code> property.</p>
     */
    public CurrentStyle getCurrentStyle() {
        return currentStyle;
    }

    /**
     * <p>Set the value of the <code>currentStyle</code> property.</p>
     */
    public void setCurrentStyle(CurrentStyle currentStyle) {
        this.currentStyle = currentStyle;
    }

    /**
     * <p>Gets the state of the instance as a <code>Serializable</code>
     * Object.</p>
     */
    public Object saveState(FacesContext context) {
        Object values[] = new Object[12];
        values[0] = super.saveState(context);
        values[1] = renderedOnUserRole;
        values[2] = effect;
        values[3] = currentStyle;
        values[4] = visible;
        values[5] = dir;
        values[6] = lang;
        values[7] = errorClass;
        values[8] = fatalClass;
        values[9] = infoClass;
        values[10] = warnClass;
        values[11] = styleClass;        
        return ((Object) (values));
    }

    /**
     * <p>Perform any processing required to restore the state from the entries
     * in the state Object.</p>
     */
    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        renderedOnUserRole = (String) values[1];
        effect = (Effect) values[2];
        currentStyle = (CurrentStyle) values[3];
        visible = (Boolean) values[4];
        dir = (String) values[5];
        lang = (String) values[6];
        errorClass = (String)values[7];
        fatalClass = (String)values[8];
        infoClass = (String)values[9];
        warnClass = (String)values[10];
        styleClass = (String)values[11];         
    }


    /**
     * <p>Set the value of the <code>styleClass</code> property.</p>
     */
    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    /**
     * <p>Return the value of the <code>styleClass</code> property.</p>
     */
    public String getStyleClass() {
        return Util.getQualifiedStyleClass(this, 
                styleClass,
                CSS_DEFAULT.MESSAGE_STYLE_CLASS,
                "styleClass");    
    }


    /**
     * <p>Set the value of the <code>errorClass</code> property.</p>
     */
    public void setErrorClass(String errorClass) {
        this.errorClass = errorClass;
    }

    /**
     * <p>Return the value of the <code>errorClass</code> property.</p>
     */
    public String getErrorClass() {
        return Util.getQualifiedStyleClass(this, 
                errorClass,
                CSS_DEFAULT.ERROR_STYLE_CLASS,
                "errorClass");  
    }


    /**
     * <p>Set the value of the <code>fataClass</code> property.</p>
     */
    public void setFatalClass(String fatalClass) {
        this.fatalClass = fatalClass;
    }

    /**
     * <p>Return the value of the <code>fataClass</code> property.</p>
     */
    public String getFatalClass() {
        return Util.getQualifiedStyleClass(this, 
                fatalClass,
                CSS_DEFAULT.FATAL_STYLE_CLASS,
                "fatalClass");  
    }


    /**
     * <p>Set the value of the <code>infoClass</code> property.</p>
     */
    public void setInfoClass(String infoClass) {
        this.infoClass = infoClass;
    }

    /**
     * <p>Return the value of the <code>infoClass</code> property.</p>
     */
    public String getInfoClass() {
        return Util.getQualifiedStyleClass(this, 
                infoClass,
                CSS_DEFAULT.INFO_STYLE_CLASS,
                "infoClass");  
    }

    /**
     * <p>Set the value of the <code>warnClass</code> property.</p>
     */
    public void setWarnClass(String warnClass) {
        this.warnClass = warnClass;
    }

    /**
     * <p>Return the value of the <code>warnClass</code> property.</p>
     */
    public String getWarnClass() {
        return Util.getQualifiedStyleClass(this, 
                warnClass,
                CSS_DEFAULT.WARN_STYLE_CLASS,
                "warnClass");  
    }
    
      private java.lang.String dir;

  /**
   * <p>Return the value of the <code>dir</code> property.  Contents:</p><p>
   * Direction indication for text that does not inherit directionality.
   *           Valid values are "LTR" (left-to-right) and "RTL" (right-to-left).
   * </p>
   */
  public java.lang.String getDir() {
    if (null != this.dir) {
      return this.dir;
    }
    ValueBinding _vb = getValueBinding("dir");
    if (_vb != null) {
      return (java.lang.String) _vb.getValue(getFacesContext());
    } else {
      return null;
    }
  }

  /**
   * <p>Set the value of the <code>dir</code> property.</p>
   */
  public void setDir(java.lang.String dir) {
    this.dir = dir;
  }
  
    private java.lang.String lang;

  /**
   * <p>Return the value of the <code>lang</code> property.  Contents:</p><p>
   * Code describing the language used in the generated markup
   *           for this component.
   * </p>
   */
  public java.lang.String getLang() {
    if (null != this.lang) {
      return this.lang;
    }
    ValueBinding _vb = getValueBinding("lang");
    if (_vb != null) {
      return (java.lang.String) _vb.getValue(getFacesContext());
    } else {
      return null;
    }
  }

  /**
   * <p>Set the value of the <code>lang</code> property.</p>
   */
  public void setLang(java.lang.String lang) {
    this.lang = lang;
  }

}
