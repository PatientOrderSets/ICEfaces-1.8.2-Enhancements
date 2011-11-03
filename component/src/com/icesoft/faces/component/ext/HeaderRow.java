package com.icesoft.faces.component.ext;

import javax.faces.component.UIPanel;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.icesoft.faces.component.CSS_DEFAULT;
import com.icesoft.faces.component.ext.taglib.Util;

public class HeaderRow extends UIPanel {
    public static final String COMPONENT_TYPE = "com.icesoft.faces.HeaderRow";
    public static final String COMPONENT_FAMILY = "com.icesoft.faces.Header";
    private String style = null;
    private String styleClass = null;
    private String colspan = null;
    private String rowspan = null;
    private String renderedOnUserRole = null;

    public String getFamily() {
        return COMPONENT_FAMILY;
    }
    public String getComponentType() {
        return COMPONENT_TYPE;
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
        if (styleClass != null) {
            return styleClass;
        }
        ValueBinding vb = getValueBinding("styleClass");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    } 

    /**
     * <p>Set the value of the <code>colspan</code> property.</p>
     */
    public void setColspan(String colspan) {
        this.colspan = colspan;
    }

    /**
     * <p>Return the value of the <code>colspan</code> property.</p>
     */
    public String getColspan() {
        if (colspan != null) {
            return colspan;
        }
        ValueBinding vb = getValueBinding("colspan");
        return vb != null ? (String) vb.getValue(getFacesContext()) :null;
    }
    
    /**
     * <p>Set the value of the <code>rowspan</code> property.</p>
     */
    public void setRowspan(String rowspan) {
        this.rowspan = rowspan;
    }

    /**
     * <p>Return the value of the <code>rowspan</code> property.</p>
     */
    public String getRowspan() {
        if (rowspan != null) {
            return rowspan;
        }
        ValueBinding vb = getValueBinding("rowspan");
        return vb != null ? (String) vb.getValue(getFacesContext()) :null;
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

    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[])state;        
        super.restoreState(context, values[0]);
        renderedOnUserRole = (String)values[1];
        style = (String)values[2];
        styleClass = (String)values[3];
        colspan = (String) values[4];
        rowspan = (String) values[5];
    }

    public Object saveState(FacesContext context) {
        Object[] values = new Object[6];
        values[0] = super.saveState(context);
        values[1] = renderedOnUserRole;
        values[2] = style;
        values[3] = styleClass;
        values[4] = colspan;
        values[5] = rowspan;
        return values;
    }
    
    
}
