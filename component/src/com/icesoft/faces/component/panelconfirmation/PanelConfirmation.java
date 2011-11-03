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
 
package com.icesoft.faces.component.panelconfirmation;

import javax.faces.component.UIComponentBase;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.icesoft.faces.context.effects.JavascriptContext;
import com.icesoft.faces.component.ext.taglib.Util;
import com.icesoft.faces.component.CSS_DEFAULT;

public class PanelConfirmation extends UIComponentBase {

    public static final String COMPONENT_TYPE = "com.icesoft.faces.PanelConfirmation";
    
    public static final String COMPONENT_FAMILY = "com.icesoft.faces.PanelConfirmation";

    public static final String DEFAULT_RENDERER_TYPE = "com.icesoft.faces.PanelConfirmationRenderer";
    
    private String title = null;
    private String message = null;
    private String type = null;
    private String acceptLabel = null;
    private String cancelLabel = null;
    private Boolean autoCentre = null;
    private Boolean draggable = null;
    private Boolean displayAtMouse = null;
    
    private String style = null;
    private String styleClass = null;
    
    public PanelConfirmation() {
        setRendererType(DEFAULT_RENDERER_TYPE);
        JavascriptContext.includeLib(JavascriptContext.ICE_EXTRAS,
                                     FacesContext.getCurrentInstance());
    }
    
    public String getFamily() {
        return COMPONENT_FAMILY;
    }
    
    /**
     * <p>Set the value of the <code>title</code> property.</p>
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * <p>Return the value of the <code>title</code> property.</p>
     */
    public String getTitle() {
        if (title != null) {
            return title;
        }
        ValueBinding vb = getValueBinding("title");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }
    
    /**
     * <p>Set the value of the <code>message</code> property.</p>
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * <p>Return the value of the <code>message</code> property.</p>
     */
    public String getMessage() {
        if (message != null) {
            return message;
        }
        ValueBinding vb = getValueBinding("message");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>type</code> property.</p>
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * <p>Return the value of the <code>type</code> property.</p>
     */
    public String getType() {
        if (type != null) {
            return type;
        }
        ValueBinding vb = getValueBinding("type");
        return vb != null ? (String) vb.getValue(getFacesContext()) : "normal";
    }
    
    /**
     * <p>Set the value of the <code>acceptLabel</code> property.</p>
     */
    public void setAcceptLabel(String acceptLabel) {
        this.acceptLabel = acceptLabel;
    }

    /**
     * <p>Return the value of the <code>acceptLabel</code> property.</p>
     */
    public String getAcceptLabel() {
        if (acceptLabel != null) {
            return acceptLabel;
        }
        ValueBinding vb = getValueBinding("acceptLabel");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }
    
    /**
     * <p>Set the value of the <code>cancelLabel</code> property.</p>
     */
    public void setCancelLabel(String cancelLabel) {
        this.cancelLabel = cancelLabel;
    }

    /**
     * <p>Return the value of the <code>cancelLabel</code> property.</p>
     */
    public String getCancelLabel() {
        if (cancelLabel != null) {
            return cancelLabel;
        }
        ValueBinding vb = getValueBinding("cancelLabel");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }
    
    /**
     * <p>Set the value of the <code>style</code> property.</p>
     */
    public void setStyle(String style) {
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
    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    /**
     * <p>Return the value of the <code>styleClass</code> property.</p>
     */
    public String getStyleClass() {
        return Util.getQualifiedStyleClass(this, styleClass, CSS_DEFAULT.PANEL_CONFIRMATION_BASE, "styleClass");
    }
    
    /**
     * <p>Return the value of the <code>headerClass</code> property.</p>
     */
    public String getHeaderClass() {
        return Util.getQualifiedStyleClass(this, CSS_DEFAULT.PANEL_CONFIRMATION_HEADER);
    }
    
    /**
     * <p>Return the value of the <code>bodyClass</code> property.</p>
     */
    public String getBodyClass() {
        return Util.getQualifiedStyleClass(this, CSS_DEFAULT.PANEL_CONFIRMATION_BODY);
    }
    
    /**
     * <p>Return the value of the <code>buttonsClass</code> property.</p>
     */
    public String getButtonsClass() {
        return Util.getQualifiedStyleClass(this, CSS_DEFAULT.PANEL_CONFIRMATION_BUTTONS);
    }

    /**
     * <p>Set the value of the <code>autoCentre</code> property.</p>
     */    
	public void setAutoCentre(boolean autoCentre) {
		this.autoCentre = Boolean.valueOf(autoCentre);
	}
    
    /**
     * <p>Return the value of the <code>autoCentre</code> property.</p>
     */
	public boolean isAutoCentre() {
		if (autoCentre != null) {
			return autoCentre.booleanValue();
		}
		ValueBinding vb = getValueBinding("autoCentre");
		Boolean boolVal = vb != null ? (Boolean) vb.getValue(getFacesContext())
				: null;
		return boolVal != null ? boolVal.booleanValue() : false;
	}
	
    /**
     * <p>Set the value of the <code>draggable</code> property.</p>
     */    
	public void setDraggable(boolean draggable) {
		this.draggable = Boolean.valueOf(draggable);
	}
    
    /**
     * <p>Return the value of the <code>draggable</code> property.</p>
     */
	public boolean isDraggable() {
		if (draggable != null) {
			return draggable.booleanValue();
		}
		ValueBinding vb = getValueBinding("draggable");
		Boolean boolVal = vb != null ? (Boolean) vb.getValue(getFacesContext())
				: null;
		return boolVal != null ? boolVal.booleanValue() : false;
	}
	
    /**
     * <p>Set the value of the <code>displayAtMouse</code> property.</p>
     */    
	public void setDisplayAtMouse(boolean displayAtMouse) {
		this.displayAtMouse = Boolean.valueOf(displayAtMouse);
	}
    
    /**
     * <p>Return the value of the <code>displayAtMouse</code> property.</p>
     */
	public boolean isDisplayAtMouse() {
		if (displayAtMouse != null) {
			return displayAtMouse.booleanValue();
		}
		ValueBinding vb = getValueBinding("displayAtMouse");
		Boolean boolVal = vb != null ? (Boolean) vb.getValue(getFacesContext())
				: null;
		return boolVal != null ? boolVal.booleanValue() : false;
	}

    private transient Object states[];

    public Object saveState(FacesContext context) {
        if(states == null) {
            states = new Object[11];
        }
        states[0] = super.saveState(context);
        states[1] = title;
        states[2] = message;
        states[3] = type;
        states[4] = acceptLabel;
        states[5] = cancelLabel;
        states[6] = style;
        states[7] = styleClass;
        states[8] = autoCentre;
        states[9] = draggable;
        states[10] = displayAtMouse;
        return states;
    }
    
    public void restoreState(FacesContext context, Object state) {
        states = (Object[]) state;
        super.restoreState(context, states[0]);
        title = (String) states[1];
        message = (String) states[2];
        type = (String) states[3];
        acceptLabel = (String) states[4];
        cancelLabel = (String) states[5];
        style = (String) states[6];
        styleClass = (String) states[7];
        autoCentre = (Boolean) states[8];
        draggable = (Boolean) states[9];
        displayAtMouse = (Boolean) states[10];
    }
}
