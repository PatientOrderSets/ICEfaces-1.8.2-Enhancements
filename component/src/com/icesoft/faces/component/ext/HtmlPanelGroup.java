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
import com.icesoft.faces.component.dragdrop.DndEvent;
import com.icesoft.faces.component.dragdrop.DragEvent;
import com.icesoft.faces.component.dragdrop.DropEvent;
import com.icesoft.faces.component.ext.taglib.Util;
import com.icesoft.faces.component.paneltooltip.PanelTooltip;
import com.icesoft.faces.context.effects.CurrentStyle;
import com.icesoft.faces.context.effects.Effect;
import com.icesoft.faces.context.effects.JavascriptContext;
import com.icesoft.faces.utils.SeriesStateHolder;

import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;
import javax.faces.event.PhaseId;


/**
 * This is an extension of javax.faces.component.html.HtmlPanelGroup, which
 * provides some additional behavior to this component such as: <ul> <li>changes
 * the component's rendered state based on the authentication</li> <li>provides
 * drag & drop mechanism</li> <li>allows to render scrollable panel</li>
 * <li>adds effects to the component</li> <ul>
 */

public class HtmlPanelGroup extends javax.faces.component.html.HtmlPanelGroup
        implements SeriesStateHolder {
    public static final String COMPONENT_TYPE =
            "com.icesoft.faces.HtmlPanelGroup";
    public static final String RENDERER_TYPE = "com.icesoft.faces.Group";
    public static final String SCROLLABLE_STYLE = "overflow:auto;";
    private static final boolean DEFAULT_VISIBLE = true;
    private String renderedOnUserRole = null;
    private String style = null;
    private String scrollWidth = null;
    private String scrollHeight = null;

    private String draggable;
    private MethodBinding dragListener;
    private Object dragValue;

    private String dropTarget;
    private String dropTargetScrollerId;
    private MethodBinding dropListener;
    private Object dropValue;

    private String dropMask;
    private String dragMask;


    private CurrentStyle currentStyle;
    // This is needed to avoid unessisary dom updates.
    private transient String renderedStyle;

    private Effect effect;
    protected Boolean visible = null;
    private String dragOptions;

    private Effect onclickeffect;
    private Effect ondblclickeffect;
    private Effect onmousedowneffect;
    private Effect onmouseupeffect;
    private Effect onmousemoveeffect;
    private Effect onmouseovereffect;
    private Effect onmouseouteffect;

    private Effect onkeypresseffect;
    private Effect onkeydowneffect;
    private Effect onkeyupeffect;

    private String hoverclass;
    
    private String menuPopup;
    private String panelTooltip;
    
    private Object contextValue;
    private String onclick;
    private String ondblclick;
    private String onmousedown;
    private String onkeyup;
    private String onkeydown;
    private String onkeypress;
    private String onmouseout;
    private String onmouseover;
    private String onmousemove;
    private String onmouseup;
    private String title;    
    /**
     *
     */
    public HtmlPanelGroup() {//The following attributes should be overriden by ice:panelGroup
        super();
        setRendererType(RENDERER_TYPE);
    }

    public void decode(FacesContext context) {
        super.decode(context);
        if (getPanelTooltip() != null) {
            PanelTooltip.decodeTooltip(context, this);
        }
    }
    
    public void setValueBinding(String s, ValueBinding vb) {
        if (s != null && (s.indexOf("effect") != -1 ||
                          s.indexOf("drag") != -1 || s.indexOf("drop") != -1)) {
            // If this is an effect attribute make sure Ice Extras is included
            JavascriptContext.includeLib(JavascriptContext.ICE_EXTRAS,
                                         getFacesContext());
        }
        super.setValueBinding(s, vb);
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
            return getScrollableStyle(style);
        }

        ValueBinding vb = getValueBinding("style");
        return vb != null ?
               getScrollableStyle((String) vb.getValue(getFacesContext())) :
               getScrollableStyle("");
    }

    /**
     * <p>Set the value of the <code>dragOptions</code> property.</p>
     */
    public void setDragOptions(String s) {
        this.dragOptions = s;
    }

    /**
     * <p>Return the value of the <code>dragOptions</code> property.</p>
     */
    public String getDragOptions() {
        if (this.dragOptions != null) {
            return dragOptions;
        }
        ValueBinding vb = getValueBinding("dragOptions");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>scrollWidth</code> property.</p>
     */
    public void setScrollWidth(String scrollWidth) {
        this.scrollWidth = scrollWidth;
    }

    /**
     * <p>Return the value of the <code>scrollWidth</code> property.</p>
     */
    public String getScrollWidth() {
        if (scrollWidth != null) {
            return scrollWidth;
        }
        ValueBinding vb = getValueBinding("scrollWidth");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>scrollHeight</code> property.</p>
     */
    public void setScrollHeight(String scrollHeight) {
        this.scrollHeight = scrollHeight;
    }

    /**
     * <p>Return the value of the <code>scrollHeight</code> property.</p>
     */
    public String getScrollHeight() {
        if (scrollHeight != null) {
            return scrollHeight;
        }
        ValueBinding vb = getValueBinding("scrollHeight");
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
     * <p>Return the value of the <code>draggable</code> property.</p>
     */
    public String getDraggable() {
        if (draggable != null) {
            return draggable;
        }
        ValueBinding vb = getValueBinding("draggable");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>draggable</code> property.</p>
     */
    public void setDraggable(String draggable) {
        this.draggable = draggable;
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
    }

    /**
     * <p>Return the value of the <code>dragListener</code> property.</p>
     */
    public MethodBinding getDragListener() {
        return dragListener;
    }

    /**
     * <p>Set the value of the <code>dragListener</code> property.</p>
     */
    public void setDragListener(MethodBinding dragListener) {
        this.dragListener = dragListener;
    }

    /**
     * <p>Return the value of the <code>dragValue</code> property.</p>
     */
    public Object getDragValue() {
        if (dragValue != null) {
            return dragValue;
        }
        ValueBinding vb = getValueBinding("dragValue");
        return vb != null ? (Object) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>dragValue</code> property.</p>
     */
    public void setDragValue(Object dragValue) {
        this.dragValue = dragValue;
    }

    /**
     * <p>Return the value of the <code>dropTarget</code> property.</p>
     */
    public String getDropTarget() {
        if (dropTarget != null) {
            return dropTarget;
        }
        ValueBinding vb = getValueBinding("dropTarget");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>dropTarget</code> property.</p>
     */
    public void setDropTarget(String dropTarget) {
        this.dropTarget = dropTarget;
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
    }

    /**
     * <p>Return the value of the <code>dropListener</code> property.</p>
     */
    public MethodBinding getDropListener() {
        return dropListener;
    }

    /**
     * <p>Set the value of the <code>dropListener</code> property.</p>
     */
    public void setDropListener(MethodBinding dropListener) {
        this.dropListener = dropListener;
    }

    /**
     * <p>Return the value of the <code>dropValue</code> property.</p>
     */
    public Object getDropValue() {
        if (dropValue != null) {
            return dropValue;
        }
        ValueBinding vb = getValueBinding("dropValue");
        return vb != null ? (Object) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>dropValue</code> property.</p>
     */
    public void setDropValue(Object dropValue) {
        this.dropValue = dropValue;
    }

    /**
     * <p>Return the value of the <code>dropMask</code> property.</p>
     */
    public String getDropMask() {
        return dropMask;
    }

    /**
     * <p>Set the value of the <code>dropMask</code> property.</p>
     */
    public void setDropMask(String dropMask) {
        this.dropMask = dropMask;
    }

    /**
     * <p>Return the value of the <code>dragMask</code> property.</p>
     */
    public String getDragMask() {
        return dragMask;
    }

    /**
     * <p>Set the value of the <code>dragMask</code> property.</p>
     */
    public void setDragMask(String dragMask) {
        this.dragMask = dragMask;
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
     * <p>Set the value of the <code>effect</code> property.</p>
     */
    public void setEffect(Effect effect) {
        this.effect = effect;
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
    }


    /**
     * <p>Return the value of the <code>onclickeffect</code> property.</p>
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
     */
    public void setOnclickeffect(Effect onclickeffect) {
        this.onclickeffect = onclickeffect;
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
    }

    /**
     * <p>Return the value of the <code>ondblclickeffect</code> property.</p>
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
     */
    public void setOndblclickeffect(Effect ondblclickeffect) {
        this.ondblclickeffect = ondblclickeffect;
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
    }

    /**
     * <p>Return the value of the <code>onmousedowneffect</code> property.</p>
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
     */
    public void setOnmousedowneffect(Effect onmousedowneffect) {
        this.onmousedowneffect = onmousedowneffect;
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
    }

    /**
     * <p>Return the value of the <code>onmouseupeffect</code> property.</p>
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
     */
    public void setOnmouseupeffect(Effect onmouseupeffect) {
        this.onmouseupeffect = onmouseupeffect;
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
    }

    /**
     * <p>Return the value of the <code>onmousemoveeffect</code> property.</p>
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
     */
    public void setOnmousemoveeffect(Effect onmousemoveeffect) {
        this.onmousemoveeffect = onmousemoveeffect;
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
    }

    /**
     * <p>Return the value of the <code>onmouseovereffect</code> property.</p>
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
     */
    public void setOnmouseovereffect(Effect onmouseovereffect) {
        this.onmouseovereffect = onmouseovereffect;
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
    }

    /**
     * <p>Return the value of the <code>onmouseouteffect</code> property.</p>
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
     */
    public void setOnmouseouteffect(Effect onmouseouteffect) {
        this.onmouseouteffect = onmouseouteffect;
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
    }


    /**
     * <p>Return the value of the <code>onkeypresseffect</code> property.</p>
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
     */
    public void setOnkeypresseffect(Effect onkeypresseffect) {
        this.onkeypresseffect = onkeypresseffect;
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
    }

    /**
     * <p>Return the value of the <code>onkeydowneffect</code> property.</p>
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
     */
    public void setOnkeydowneffect(Effect onkeydowneffect) {
        this.onkeydowneffect = onkeydowneffect;
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
    }


    /**
     * <p>Return the value of the <code>onkeyupeffect</code> property.</p>
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
     */
    public void setOnkeyupeffect(Effect onkeyupeffect) {
        this.onkeyupeffect = onkeyupeffect;
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
    }
    
    /**
     * <p>Return the value of the <code>menuPopup</code> property,
     * which is the id of the ice:menuPopup to show when the user
     * context-clicks on this component</p>
     */
    public String getMenuPopup() {
        if(menuPopup != null) {
            return menuPopup;
        }
        ValueBinding vb = getValueBinding("menuPopup");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }
    
    /**
     * <p>Set the value of the <code>menuPopup</code> property,
     * which is the id of the ice:menuPopup to show when the user
     * context-clicks on this component</p>
     */
    public void setMenuPopup(String menuPopup) {
        this.menuPopup = menuPopup;
    }

    /**
     * <p>Gets the state of the instance as a <code>Serializable</code>
     * Object.</p>
     */
    public Object saveState(FacesContext context) {
        Object values[] = new Object[44];
        values[0] = super.saveState(context);
        values[1] = renderedOnUserRole;
        values[2] = style;
        values[3] = scrollWidth;
        values[4] = scrollHeight;
        values[5] = draggable;        
        values[6] = saveAttachedState(context, dragListener);
        values[7] = dragValue;
        values[8] = dropTarget;
        values[9] = saveAttachedState(context, dropListener);
        values[10] = dropValue;
        values[11] = dragMask;
        values[12] = dropMask;
        values[13] = effect;
        values[14] = onclickeffect;
        values[15] = ondblclickeffect;
        values[16] = onmousedowneffect;
        values[17] = onmouseupeffect;
        values[18] = onmousemoveeffect;
        values[19] = onmouseovereffect;
        values[20] = onmouseouteffect;
        values[21] = onkeypresseffect;
        values[22] = onkeydowneffect;
        values[23] = onkeyupeffect;
        values[24] = currentStyle;
        values[25] = visible;
        values[26] = menuPopup;
        values[27] = panelTooltip;
        values[28] = contextValue;
        values[29] = renderedStyle;
        values[30] = dragOptions;
        values[31] = hoverclass; 
        values[32] = onclick;       
        values[33] = ondblclick;
        values[34] = onmousedown;
        values[35] = onkeyup;
        values[36] = onkeydown;
        values[37] = onkeypress;
        values[38] = onmouseout;
        values[39] = onmouseover;
        values[40] = onmousemove;
        values[41] = onmouseup;        
        values[42] = dropTargetScrollerId;    
        values[43] = title;        
        return values;

    }

    /**
     * <p>Perform any processing required to restore the state from the entries
     * in the state Object.</p>
     */
    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        renderedOnUserRole = (String) values[1];
        style = (String) values[2];
        scrollWidth = (String) values[3];
        scrollHeight = (String) values[4];
        draggable = (String) values[5];
        dragListener = (MethodBinding)restoreAttachedState(context, values[6]);
        dragValue = values[7];
        dropTarget = (String) values[8];
        dropListener = (MethodBinding)restoreAttachedState(context, values[9]);
        dropValue = values[10];
        dragMask = (String) values[11];
        dropMask = (String) values[12];
        effect = (Effect) values[13];
        onclickeffect = (Effect) values[14];
        ondblclickeffect = (Effect) values[15];
        onmousedowneffect = (Effect) values[16];
        onmouseupeffect = (Effect) values[17];
        onmousemoveeffect = (Effect) values[18];
        onmouseovereffect = (Effect) values[19];
        onmouseouteffect = (Effect) values[20];
        onkeypresseffect = (Effect) values[21];
        onkeydowneffect = (Effect) values[22];
        onkeyupeffect = (Effect) values[23];
        currentStyle = (CurrentStyle) values[24];
        visible = (Boolean) values[25];
        menuPopup = (String) values[26];
        panelTooltip = (String) values[27];
        contextValue = values[28];
        renderedStyle = (String)values[29];
        dragOptions = (String)values[30];
        hoverclass = (String)values[31];    
        onclick = (String)values[32];  
        ondblclick = (String)values[33];
        onmousedown= (String)values[34];
        onkeyup= (String)values[35];
        onkeydown= (String)values[36];
        onkeypress= (String)values[37] ;
        onmouseout= (String)values[38];
        onmouseover= (String)values[39];
        onmousemove= (String)values[40];
        onmouseup= (String)values[41];
        dropTargetScrollerId = (String) values[42];
        title = (String) values[43];        
    }

    public Object saveSeriesState(FacesContext facesContext) {
        Object values[] = new Object[3];
        values[0] = style;
        values[1] = currentStyle;
        values[2] = renderedStyle;
        return values;
    }

    public void restoreSeriesState(FacesContext facesContext, Object state) {
        Object values[] = (Object[]) state;
        style = (String) values[0];
        currentStyle = (CurrentStyle) values[1];
        renderedStyle = (String) values[2];
    }
    
    /**
     * <p>Return the given _style String as the <code>scrollableStyle</code> property.</p>
     * @param _style 
     * @return String
     */
    private String getScrollableStyle(String _style) {
        return _style;
    }

    /* (non-Javadoc)
     * @see javax.faces.component.UIComponent#broadcast(javax.faces.event.FacesEvent)
     */
    public void broadcast(FacesEvent event)
            throws AbortProcessingException {
        super.broadcast(event);

        if (event instanceof DragEvent && dragListener != null) {
            Object[] oa = {(DragEvent) event};
            dragListener.invoke(getFacesContext(), oa);
        }
        if (event instanceof DropEvent && dropListener != null) {
            Object[] oa = {(DropEvent) event};
            dropListener.invoke(getFacesContext(), oa);
        }
    }

    /* (non-Javadoc)
     * @see javax.faces.component.UIComponent#queueEvent(javax.faces.event.FacesEvent)
     */
    public void queueEvent(FacesEvent event) {
        if (event instanceof DndEvent) {
            event.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);
        }
        super.queueEvent(event);
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
     * <p>Return the value of the <code>renderedStyle</code> property.</p>
     */
    public String getRenderedStyle() {
        return renderedStyle;
    }

    /**
     * <p>Set the value of the <code>renderedStyle</code> property.</p>
     */
    public void setRenderedStyle(String renderedStyle) {
        this.renderedStyle = renderedStyle;
    }

    public String getHoverclass() {
        return hoverclass;
    }

    public void setHoverclass(String hoverclass) {
        this.hoverclass = hoverclass;
    }

    /**
     * <p>Return the value of the <code>styleClass</code> property.</p>
     */
    public String getStyleClass() {
        return Util.getQualifiedStyleClass(this, 
                    super.getStyleClass(),
                    CSS_DEFAULT.PANEL_GROUP_DEFAULT_STYLE_CLASS,
                    "styleClass");
    }
    
    /**
     * <p>Set the value of the <code>panelTooltip</code> property.</p>
     */
    public void setPanelTooltip(String panelTooltip) {
        this.panelTooltip = panelTooltip;
    }

    /**
     * <p>Return the value of the <code>panelTooltip</code> property.</p>
     */
    public String getPanelTooltip() {
        if (panelTooltip != null) {
            return panelTooltip;
        }
        ValueBinding vb = getValueBinding("panelTooltip");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>contextValue</code> property,
     * which uniquely identifies the portion of the data model that
     * is being represented within this panelGroup.</p>
     */
    public void setContextValue(Object contextValue) {
        this.contextValue = contextValue;
    }
    
    /**
     * <p>Return the value of the <code>contextValue</code> property,
     * which uniquely identifies the portion of the data model that
     * is being represented within this panelGroup.</p>
     */
    public Object getContextValue() {
        if(contextValue != null) {
            return contextValue;
        }
        ValueBinding vb = getValueBinding("contextValue");
        return vb != null ? vb.getValue(getFacesContext()) : null;
    }
    
    /**
     * <p>Return the value of the <code>onclick</code> property.</p>
     */
    public String getOnclick() {
        if (onclick != null) {
            return onclick;
        }
        ValueBinding vb = getValueBinding("onclick");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>onclick</code> property.</p>
     */
    public void setOnclick(String onclick) {
        this.onclick = onclick;
    }
    
    /**
     * <p>Return the value of the <code>ondblclick</code> property.</p>
     */
    public String getOndblclick() {
        if (ondblclick != null) {
            return ondblclick;
        }
        ValueBinding vb = getValueBinding("ondblclick");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>ondblclick</code> property.</p>
     */
    public void setOndblclick(String ondblclick) {
        this.ondblclick = ondblclick;
    }
    
    /**
     * <p>Return the value of the <code>onmousedown</code> property.</p>
     */
    public String getOnmousedown() {
        if (onmousedown != null) {
            return onmousedown;
        }
        ValueBinding vb = getValueBinding("onmousedown");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>onmousedown</code> property.</p>
     */
    public void setOnmousedown(String onmousedown) {
        this.onmousedown = onmousedown;
    }
    
    /**
     * <p>Return the value of the <code>onmouseup</code> property.</p>
     */
    public String getOnmouseup() {
        if (onmouseup != null) {
            return onmouseup;
        }
        ValueBinding vb = getValueBinding("onmouseup");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>onmouseup</code> property.</p>
     */
    public void setOnmouseup(String onmouseup) {
        this.onmouseup = onmouseup;
    }
    
    /**
     * <p>Return the value of the <code>onmousemove</code> property.</p>
     */
    public String getOnmousemove() {
        if (onmousemove != null) {
            return onmousemove;
        }
        ValueBinding vb = getValueBinding("onmousemove");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>onmousemove</code> property.</p>
     */
    public void setOnmousemove(String onmousemove) {
        this.onmousemove = onmousemove;
    }
    
    /**
     * <p>Return the value of the <code>onmouseover</code> property.</p>
     */
    public String getOnmouseover() {
        if (onmouseover != null) {
            return onmouseover;
        }
        ValueBinding vb = getValueBinding("onmouseover");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>onmouseover</code> property.</p>
     */
    public void setOnmouseover(String onmouseover) {
        this.onmouseover = onmouseover;
    }
    
    /**
     * <p>Return the value of the <code>onmouseout</code> property.</p>
     */
    public String getOnmouseout() {
        if (onmouseout != null) {
            return onmouseout;
        }
        ValueBinding vb = getValueBinding("onmouseout");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>onmouseout</code> property.</p>
     */
    public void setOnmouseout(String onmouseout) {
        this.onmouseout = onmouseout;
    }
    
    /**
     * <p>Return the value of the <code>onkeypress</code> property.</p>
     */
    public String getOnkeypress() {
        if (onkeypress != null) {
            return onkeypress;
        }
        ValueBinding vb = getValueBinding("onkeypress");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>onkeypress</code> property.</p>
     */
    public void setOnkeypress(String onkeypress) {
        this.onkeypress = onkeypress;
    }
    
    /**
     * <p>Return the value of the <code>onkeydown</code> property.</p>
     */
    public String getOnkeydown() {
        if (onkeydown != null) {
            return onkeydown;
        }
        ValueBinding vb = getValueBinding("onkeydown");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>onkeydown</code> property.</p>
     */
    public void setOnkeydown(String onkeydown) {
        this.onkeydown = onkeydown;
    }
    
    /**
     * <p>Return the value of the <code>onkeyup</code> property.</p>
     */
    public String getOnkeyup() {
        if (onkeyup != null) {
            return onkeyup;
        }
        ValueBinding vb = getValueBinding("onkeyup");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>onkeyup</code> property.</p>
     */
    public void setOnkeyup(String onkeyup) {
        this.onkeyup = onkeyup;
    }

    public String getDropTargetScrollerId() {
        if (dropTargetScrollerId != null) return dropTargetScrollerId;
        ValueBinding vb = getValueBinding("dropTargetScrollerId");
        if (vb == null) return null;
        Object value = vb.getValue(getFacesContext());
        if (value == null) return null;
        return value.toString();
    }

    public void setDropTargetScrollerId(String dropTargetScrollerId) {
        this.dropTargetScrollerId = dropTargetScrollerId;
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
}

