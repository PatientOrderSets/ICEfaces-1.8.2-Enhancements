package com.icesoft.faces.component.panellayout;

import com.icesoft.faces.component.ext.taglib.Util;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

/*
 * PanelLayout is a container used for displaying a group of components.
 *               
 * AbsoluteLayout allow placement of components in absolute positions.
 * A flow layout arranges components in relative alignment.
 */
public class PanelLayout extends javax.faces.component.UIComponentBase {

    public final static String FLOWLAYOUT = "flow";
    public final static String ABSOLUATELAYOUT = "absolute";

    public PanelLayout() {
        super();
        setRendererType("com.icesoft.faces.PanelLayout");
    }

    /**
     * <p>Return the family for this component.</p>
     */
    public String getFamily() {
        return "com.icesoft.faces.Layout";
    }
    private java.lang.String _layout;

    public java.lang.String getLayout() {

        if (null != this._layout) {
            return this._layout;
        } else {
            return PanelLayout.ABSOLUATELAYOUT;
        }

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
    private java.lang.String _renderedOnUserRole;

    public java.lang.String getRenderedOnUserRole() {

        if (null != this._renderedOnUserRole) {
            return this._renderedOnUserRole;
        }

        ValueBinding _ve = getValueBinding("renderedOnUserRole");
        if (_ve != null) {
            return (java.lang.String) _ve.getValue(getFacesContext());
        } else {
            return null;
        }
    }
    private java.lang.String _style;

    public java.lang.String getStyle() {

        if (null != this._style) {
            return this._style;
        }

        ValueBinding _ve = getValueBinding("style");
        if (_ve != null) {
            return (java.lang.String) _ve.getValue(getFacesContext());
        } else {
            return null;
        }
    }
    private java.lang.String _styleClass;

    public java.lang.String getStyleClass() {

        if (null != this._styleClass) {
            return this._styleClass;
        }

        ValueBinding _ve = getValueBinding("styleClass");
        if (_ve != null) {
            return (java.lang.String) _ve.getValue(getFacesContext());
        } else {
            return null;
        }
    }
    private Boolean visible;
    private static final boolean DEFAULT_VISIBLE = false;

    /**
     * <p>Set the value of the <code>visible</code> property.</p>
     */
    public void setVisible(boolean visible) {
        this.visible = Boolean.valueOf(visible);
    }

    /**
     * <p>Return the value of the <code>visible</code> property.</p>
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

    public void setLayout(java.lang.String _layout) {

        this._layout = _layout;
    }

    public void setRenderedOnUserRole(java.lang.String _renderedOnUserRole) {

        this._renderedOnUserRole = _renderedOnUserRole;
    }

    public void setStyle(java.lang.String _style) {

        this._style = _style;
    }

    public void setStyleClass(java.lang.String _styleClass) {

        this._styleClass = _styleClass;
    }
    private transient Object[] _values;

    public Object saveState(FacesContext _context) {
        if (_values == null) {
            _values = new Object[6];
        }
        _values[0] = super.saveState(_context);
        _values[1] = _layout;
        _values[2] = _renderedOnUserRole;
        _values[3] = _style;
        _values[4] = _styleClass;
        _values[5] = visible;
        return _values;
    }

    public void restoreState(FacesContext _context, Object _state) {
        _values = (Object[]) _state;
        super.restoreState(_context, _values[0]);
        this._layout = (java.lang.String) _values[1];
        this._renderedOnUserRole = (java.lang.String) _values[2];
        this._style = (java.lang.String) _values[3];
        this._styleClass = (java.lang.String) _values[4];
        this.visible = (java.lang.Boolean) _values[5];
    }
}
