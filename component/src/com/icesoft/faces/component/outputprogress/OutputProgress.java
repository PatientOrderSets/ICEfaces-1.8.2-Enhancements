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
package com.icesoft.faces.component.outputprogress;

import com.icesoft.faces.component.CSS_DEFAULT;
import com.icesoft.faces.component.ext.taglib.Util;
import com.icesoft.faces.util.DOMUtils;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

/**
 * OutputProgress is a JSF component class repersenting the ICEfaces progress
 * bar. This component can be used to output progress information to users when
 * a long running server-side process is necessary. The component can be run in
 * 2 modes; "determinate" and "indeterminate".
 * <p/>
 * OutputProgress extends the JSF UIComponentBase class.
 * <p/>
 * By default this component is rendered by the "com.icesoft.faces.Bar" renderer
 * type.
 */
public class OutputProgress extends UIComponentBase {

    private final String DEFAULT_LABEL_POSITION = "embed";
    private String label = null;
    private String labelPosition = null;
    private String labelComplete = null;
    private String styleClass = null;
    private boolean labelPositionChanged = false;
    private String style = null;
    private Integer value = null;
    private Boolean indeterminate = null;
    private String renderedOnUserRole;

    /*
     * (non-Javadoc)
     *
     * @see javax.faces.component.UIComponent#getFamily()
     */
    public String getFamily() {
        return "com.icesoft.faces.Progress";
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.faces.component.UIComponent#getRendererType()
     */
    public String getRendererType() {
        return "com.icesoft.faces.Bar";
    }

    /**
     * <p>
     * Set the value of the <code>label</code> property. </p>
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * <p>
     * Set the value of the <code>labelPosition</code> property. </p>
     */
    public void setLabelPosition(String labelPosition) {
        this.labelPosition = labelPosition;
    }

    /**
     * <p>
     * Set the value of the <code>labelComplete</code> property. </p>
     */
    public void setLabelComplete(String labelComplete) {
        this.labelComplete = labelComplete;
    }

    public String getStyle() {
        if (style != null) {
            return style;
        }
        ValueBinding vb = getValueBinding("style");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>
     * Set the value of the <code>style</code> property. </p>
     */
    public void setStyle(String style) {
        this.style = style;
    }

    /**
     * <p>
     * Set the value of the <code>value</code> property. </p>
     */
    public void setValue(int value) {
        this.value = new Integer(value);
    }

    /**
     * <p>
     * Return the value of the <code>value</code> property. </p>
     */
    public int getValue() {
        if (value != null) {
            return value.intValue();
        }
        ValueBinding vb = getValueBinding("value");
        if (vb != null) {
            Integer val = (Integer) vb.getValue(getFacesContext());
            if (val != null) {
                return val.intValue();
            }
        }
        return 0;
    }

    /**
     * <p>
     * Return the value of the <code>label</code> property. </p>
     */
    public String getLabel() {
        if (label != null) {
            return label;
        }
        ValueBinding vb = getValueBinding("label");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    public boolean isIndeterminate() {
        if (indeterminate != null) {
            return indeterminate.booleanValue();
        }
        ValueBinding vb = getValueBinding("indeterminate");
        if (vb != null) {
            return ((Boolean) vb.getValue(getFacesContext())).booleanValue();
        }
        return false;
    }

    /**
     * <p>
     * Using isIndeterminate instead. </p>
     * @Deprecated
     */
    public boolean getIndeterminate() {
        return isIndeterminate();
    }

    public void setIndeterminate(boolean b) {
        indeterminate = new Boolean(b);
    }

    /**
     * <p>
     * Return the value of the <code>progressLabel</code> property. </p>
     */
    public String getProgressLabel() {
        if (getLabel() == null && getIndeterminate()) {
            return "in progress...";
        } else if (getLabel() != null) {
            return DOMUtils.escapeAnsi(getLabel());
        } else {
            return getLabel();
        }
    }

    /**
     * <p>
     * Return the value of the <code>labelPosition</code> property. </p>
     */
    public String getLabelPosition() {
        if (labelPosition != null) {
            return labelPosition;
        }
        ValueBinding vb = getValueBinding("labelPosition");
        return vb != null ? (String) vb.getValue(getFacesContext())
                : DEFAULT_LABEL_POSITION;
    }

    /**
     * <p>
     * Return the value of the <code>labelComplete</code> property. </p>
     */
    public String getLabelComplete() {
        if (labelComplete != null) {
            return labelComplete;
        }
        ValueBinding vb = getValueBinding("labelComplete");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>
     * Return the value of the <code>progressLabelComplete</code> property.
     * </p>
     */
    public String getProgressLabelComplete() {
        if (getLabelComplete() == null && getIndeterminate()) {
            return "&nbsp;";
        } else if (getLabelComplete() != null) {
            return DOMUtils.escapeAnsi(getLabelComplete());
        } else {
            return getLabelComplete();
        }
    }

    /**
     * <p>
     * Set the value of the <code>styleClass</code> property. </p>
     */
    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    /**
     * <p>
     * Return the value of the <code>styleClass</code> property. </p>
     */
    public String getStyleClass() {
        return Util.getQualifiedStyleClass(this,
                styleClass,
                CSS_DEFAULT.OUTPUT_PROGRESS_BASE_CLASS,
                "styleClass");
    }

    public String getTextClass() {
        return Util.getQualifiedStyleClass(this,
                CSS_DEFAULT.OUTPUT_PROGRESS_TEXT_STYLE_CLASS);
    }

    public String getBackgroundClass() {
        return Util.getQualifiedStyleClass(this,
                CSS_DEFAULT.OUTPUT_PROGRESS_BG_STYLE_CLASS);
    }

    public String getFillClass() {
        return Util.getQualifiedStyleClass(this,
                CSS_DEFAULT.OUTPUT_PROGRESS_FILL_STYLE_CLASS);
    }

    public String getIndeterminateActiveClass() {
        return Util.getQualifiedStyleClass(this,
                CSS_DEFAULT.OUTPUT_PROGRESS_INDETERMINATE_ACTIVE_CLASS);
    }

    public String getIndeterminateInactiveClass() {
        return Util.getQualifiedStyleClass(this,
                CSS_DEFAULT.OUTPUT_PROGRESS_INDETERMINATE_INACTIVE_CLASS);
    }

    /**
     * <p>
     * Return the value of the <code>rendered</code> property. </p>
     */
    public boolean isRendered() {
        if (!Util.isRenderedOnUserRole(this)) {
            return false;
        }
        return super.isRendered();
    }

    /**
     * <p>
     * Gets the state of the instance as a <code>Serializable</code> Object.
     * </p>
     */
    public Object saveState(FacesContext context) {
        Object values[] = new Object[10];
        values[0] = super.saveState(context);
        values[1] = label;
        values[2] = labelPosition;
        values[3] = labelComplete;
        values[4] = new Boolean(labelPositionChanged);
        values[5] = value;
        values[6] = style;
        values[7] = renderedOnUserRole;
        values[8] = styleClass;
        values[9] = indeterminate;
        return ((Object) (values));
    }

    /**
     * <p>
     * Perform any processing required to restore the state from the entries in
     * the state Object. </p>
     */
    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        label = (String) values[1];
        labelPosition = (String) values[2];
        labelComplete = (String) values[3];
        labelPositionChanged = ((Boolean) values[4]).booleanValue();
        value = (Integer) values[5];
        style = (String) values[6];
        renderedOnUserRole = (String) values[7];
        styleClass = (String) values[8];
        indeterminate = (Boolean) values[9];
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
}
