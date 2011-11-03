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
/* Original Copyright
 * Copyright 2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.icesoft.faces.component.paneltabset;

import com.icesoft.faces.component.CSS_DEFAULT;
import com.icesoft.faces.component.ext.taglib.Util;
import com.icesoft.faces.context.DOMContext;
import com.icesoft.faces.renderkit.dom_html_basic.HTML;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

/**
 * PanelTab is a JSF component class that represents an ICEfaces tab panel.
 * <p/>
 * The component extends the javax.faces.component.html.HtmlPanelGroup.
 */
public class PanelTab
        extends HtmlPanelGroup {
    /**
     * The component type.
     */
    public static final String COMPONENT_TYPE = "com.icesoft.faces.PanelTab";
    /**
     * The component family.
     */
    public static final String COMPONENT_FAMILY = "javax.faces.Panel";
    /**
     * The default renderer type.
     */
    private static final String DEFAULT_RENDERER_TYPE = "com.icesoft.faces.TabbedPaneTab";
    /**
     * The current enabledOnUserRole state.
     */
    private String enabledOnUserRole = null;
    /**
     * The current renderedOnUserRole state.
     */
    private String renderedOnUserRole = null;
    /**
     * The current style.
     */
    private String style = null;
    /**
     * The current style class name.
     */
    private String styleClass = null;
    /**
     * The current label.
     */
    private String label = null;
    /**
     * The current disabled state.
     */
    private Boolean disabled = null;
    /**
     * The current icon.
     */
    private String icon = null;
    /**
     * The current icon.
     */
    private Boolean iconAlignRight = Boolean.FALSE;
    /**
     * The current labelWidth value.
     */
    private String labelWidth = null;
    /**
     * The current labelWrap state.
     */
    private Boolean labelWrap = Boolean.FALSE;


    /* (non-Javadoc)
    * @see javax.faces.component.html.HtmlPanelGroup#setStyle(java.lang.String)
    */
    public void setStyle(String style) {
        this.style = style;
    }

    /* (non-Javadoc)
     * @see javax.faces.component.html.HtmlPanelGroup#getStyle()
     */
    public String getStyle() {
        if (style != null) {
            return style;
        }
        ValueBinding vb = getValueBinding("style");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /* (non-Javadoc)
     * @see javax.faces.component.html.HtmlPanelGroup#setStyleClass(java.lang.String)
     */
    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    /* (non-Javadoc)
     * @see javax.faces.component.html.HtmlPanelGroup#getStyleClass()
     */
    public String getStyleClass() {
        return Util.getQualifiedStyleClass(this, 
                styleClass,
                CSS_DEFAULT.PANEL_TAB_DEFAULT_STYLECLASS,
                "styleClass");
    }

    /**
     * @param enabledOnUserRole
     */
    public void setEnabledOnUserRole(String enabledOnUserRole) {
        this.enabledOnUserRole = enabledOnUserRole;
    }

    /**
     * @return the value of enabledOnUserRole
     */
    public String getEnabledOnUserRole() {
        if (enabledOnUserRole != null) {
            return enabledOnUserRole;
        }
        ValueBinding vb = getValueBinding("enabledOnUserRole");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * @param renderedOnUserRole
     */
    public void setRenderedOnUserRole(String renderedOnUserRole) {
        this.renderedOnUserRole = renderedOnUserRole;
    }

    /**
     * @return the value of renderedOnUserRole
     */
    public String getRenderedOnUserRole() {
        if (renderedOnUserRole != null) {
            return renderedOnUserRole;
        }
        ValueBinding vb = getValueBinding("renderedOnUserRole");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * Creates an instance and sets the default renderer type to
     * "javax.faces.Group".
     */
    public PanelTab() {
        setRendererType(DEFAULT_RENDERER_TYPE);
    }

    /* (non-Javadoc)
     * @see javax.faces.component.UIComponent#getFamily()
     */
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    /**
     * @param label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return the value of label
     */
    public String getLabel() {
        if (label != null) {
            return label;
        }
        ValueBinding vb = getValueBinding("label");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * @param disabled
     */
    public void setDisabled(boolean disabled) {
        this.disabled = new Boolean(disabled);
        ValueBinding vb = getValueBinding("disabled");
        if (vb != null) {
            vb.setValue(getFacesContext(), this.disabled);
            this.disabled = null;
        }
    }

    /**
     * @return the value of disabled
     */
    public boolean isDisabled() {
        if (!Util.isEnabledOnUserRole(this)) {
            return true;
        }

        if (disabled != null) {
            return disabled.booleanValue();
        }
        ValueBinding vb = getValueBinding("disabled");
        Boolean v =
                vb != null ? (Boolean) vb.getValue(getFacesContext()) : null;
        return v != null ? v.booleanValue() : false;
    }

    private transient Object values[];
    /* (non-Javadoc)
     * @see javax.faces.component.StateHolder#saveState(javax.faces.context.FacesContext)
     */
    public Object saveState(FacesContext context) {
        if(values == null){
            values = new Object[24];
        }
        values[0] = super.saveState(context);
        values[1] = label;
        values[2] = disabled;
        values[3] = enabledOnUserRole;
        values[4] = renderedOnUserRole;
        values[5] = style;
        values[6] = styleClass;
        values[7] = dir;
        values[8] = lang;
        values[9] = title;
        values[10] = onclick;
        values[11] = ondblclick;
        values[12] = onmousedown;
        values[13] = onmouseup;
        values[14] = onmouseover;
        values[15] = onmousemove;
        values[16] = onmouseout;
        values[17] = onkeypress;
        values[18] = onkeydown;
        values[19] = onkeyup;
        values[20] = icon;
        values[21] = iconAlignRight;
        values[22] = labelWidth;
        values[23] = labelWrap;
        
        return ((Object) (values));
    }

    /* (non-Javadoc)
     * @see javax.faces.component.StateHolder#restoreState(javax.faces.context.FacesContext, java.lang.Object)
     */
    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        label = (String) values[1];
        disabled = (Boolean) values[2];
        enabledOnUserRole = (String) values[3];
        renderedOnUserRole = (String) values[4];
        style = (String) values[5];
        styleClass = (String) values[6];
        dir = (String) values[7];
        lang = (String) values[8];
        title = (String) values[9];
        onclick = (String) values[10];
        ondblclick = (String) values[11];
        onmousedown = (String) values[12];
        onmouseup = (String) values[13];
        onmouseover = (String) values[14];
        onmousemove = (String) values[15];
        onmouseout = (String) values[16];
        onkeypress = (String) values[17];
        onkeydown = (String) values[18];
        onkeyup = (String) values[19];
        icon = (String)values[20];
        iconAlignRight = (Boolean)values[21];
        labelWidth = (String) values[22];
        labelWrap = (Boolean) values[23];
    }

    /* (non-Javadoc)
     * @see javax.faces.component.UIComponent#isRendered()
     */
    public boolean isRendered() {
        if (!Util.isRenderedOnUserRole(this)) {
            return false;
        }
        return super.isRendered();
    }

    private String dir = null;
    private String lang = null;
    private String title = null;
    private String onclick = null;
    private String ondblclick = null;
    private String onmousedown = null;
    private String onmouseup = null;
    private String onmouseover = null;
    private String onmousemove = null;
    private String onmouseout = null;
    private String onkeypress = null;
    private String onkeydown = null;
    private String onkeyup = null;


    /**
     * @param dir
     */
    public void setDir(String dir) {
        this.dir = dir;
    }

    /**
     * @param lang
     */
    public void setLang(String lang) {
        this.lang = lang;
    }

    /**
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @param onclick
     */
    public void setOnclick(String onclick) {
        this.onclick = onclick;
    }

    /**
     * @param ondblclick
     */
    public void setOndblclick(String ondblclick) {
        this.ondblclick = ondblclick;
    }

    /**
     * @param onkeydown
     */
    public void setOnkeydown(String onkeydown) {
        this.onkeydown = onkeydown;
    }

    /**
     * @param onkeypress
     */
    public void setOnkeypress(String onkeypress) {
        this.onkeypress = onkeypress;
    }

    /**
     * @param onkeyup
     */
    public void setOnkeyup(String onkeyup) {
        this.onkeyup = onkeyup;
    }

    /**
     * @param onmousedown
     */
    public void setOnmousedown(String onmousedown) {
        this.onmousedown = onmousedown;
    }

    /**
     * @param onmousemove
     */
    public void setOnmousemove(String onmousemove) {
        this.onmousemove = onmousemove;
    }

    /**
     * @param onmouseout
     */
    public void setOnmouseout(String onmouseout) {
        this.onmouseout = onmouseout;
    }

    /**
     * @param onmouseover
     */
    public void setOnmouseover(String onmouseover) {
        this.onmouseover = onmouseover;
    }

    /**
     * @param onmouseup
     */
    public void setOnmouseup(String onmouseup) {
        this.onmouseup = onmouseup;
    }

    /**
     * @return the value of dir property
     */
    public String getDir() {
        if (dir != null) {
            return dir;
        }
        ValueBinding vb = getValueBinding("dir");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * @return the value of lang property
     */
    public String getLang() {
        if (lang != null) {
            return lang;
        }
        ValueBinding vb = getValueBinding("lang");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * @return the value of title property
     */
    public String getTitle() {
        if (title != null) {
            return title;
        }
        ValueBinding vb = getValueBinding("title");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * @return the value of onclick property
     */
    public String getOnclick() {
        if (onclick != null) {
            return onclick;
        }
        ValueBinding vb = getValueBinding("onclick");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * @return the value of ondblclick property
     */
    public String getOndblclick() {
        if (ondblclick != null) {
            return ondblclick;
        }
        ValueBinding vb = getValueBinding("ondblclick");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * @return the value of onmousedown property
     */
    public String getOnmousedown() {
        if (onmousedown != null) {
            return onmousedown;
        }
        ValueBinding vb = getValueBinding("onmousedown");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * @return the value of onmouseup property
     */
    public String getOnmouseup() {
        if (onmouseup != null) {
            return onmouseup;
        }
        ValueBinding vb = getValueBinding("onmouseup");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * @return the value of onmouseover property
     */
    public String getOnmouseover() {
        if (onmouseover != null) {
            return onmouseover;
        }
        ValueBinding vb = getValueBinding("onmouseover");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * @return the value of onmousemove property
     */
    public String getOnmousemove() {
        if (onmousemove != null) {
            return onmousemove;
        }
        ValueBinding vb = getValueBinding("onmousemove");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * @return the value of onmouseout property
     */
    public String getOnmouseout() {
        if (onmouseout != null) {
            return onmouseout;
        }
        ValueBinding vb = getValueBinding("onmouseout");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * @return the value of onkeypress property
     */
    public String getOnkeypress() {
        if (onkeypress != null) {
            return onkeypress;
        }
        ValueBinding vb = getValueBinding("onkeypress");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * @return the value of onkeydown property
     */
    public String getOnkeydown() {
        if (onkeydown != null) {
            return onkeydown;
        }
        ValueBinding vb = getValueBinding("onkeydown");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * @return the value of onkeyup property
     */
    public String getOnkeyup() {
        if (onkeyup != null) {
            return onkeyup;
        }
        ValueBinding vb = getValueBinding("onkeyup");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * @param icon
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * @return icon
     */
    public String getIcon() {
        if (icon != null) {
            return icon;
        }
        ValueBinding vb = getValueBinding("icon");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * @param iconAlignRight
     */
    public void setIconAlignRight(boolean iconAlignRight) {
        this.iconAlignRight = new Boolean(iconAlignRight);
    }

    /**
     * @return iconAlignRight
     */
    public boolean isIconAlignRight() {
        if (iconAlignRight != null) {
            return iconAlignRight.booleanValue();
        }
        ValueBinding vb = getValueBinding("iconAlignRight");
        return vb != null ?
               ((Boolean) vb.getValue(getFacesContext())).booleanValue() :
               false;
    }

    /**
     * @param domContext
     * @param parent
     * @param child
     * @param tabSet
     */
    void addHeaderText(DOMContext domContext, Node parent, Node child,
                       PanelTabSet tabSet) {
        Element table = (Element) domContext.createElement(HTML.TABLE_ELEM);
        table.setAttribute(HTML.CELLPADDING_ATTR,"0");
        table.setAttribute(HTML.CELLSPACING_ATTR,"0");
        Element tr = (Element) domContext.createElement(HTML.TR_ELEM);
        Element labelTd = (Element) domContext.createElement(HTML.TD_ELEM);
        table.appendChild(tr);
        
        Element div = (Element) domContext.createElement(HTML.DIV_ELEM);
        parent.appendChild(div);
        div.appendChild(table);
        
        if (getLabelWidth() != null) {
            div.setAttribute(HTML.STYLE_ATTR, "width:"+getLabelWidth()+"px;overflow:hidden;");
            
            if (isLabelWrap()) {
                labelTd.setAttribute(HTML.STYLE_ATTR, "white-space: normal;width:"+getLabelWidth()+"px;");
                Element innerDiv = (Element) domContext.createElement(HTML.DIV_ELEM);
                innerDiv.setAttribute(HTML.STYLE_ATTR, "max-width:"+getLabelWidth()+"px;text-align:left;");
                labelTd.appendChild(innerDiv);
                innerDiv.appendChild(child);
            } else {
                labelTd.appendChild(child);
            }
        } else {
            labelTd.appendChild(child);
        }

        if (getIcon() == null) {
            tr.appendChild(labelTd);
            return;
        }

        Element iconTd = (Element) domContext.createElement(HTML.TD_ELEM);
        Element icon = (Element) domContext.createElement(HTML.IMG_ELEM);
        icon.setAttribute(HTML.SRC_ATTR, getIcon());
        icon.setAttribute(HTML.BORDER_ATTR, "border");
        iconTd.appendChild(icon);

        String iconClass = CSS_DEFAULT.PANEL_TAB_HEADER_ICON_DEFAULT_CLASS;

        if (isIconAlignRight()) {
            tr.appendChild(labelTd);
            tr.appendChild(iconTd);
            iconClass += CSS_DEFAULT.PANEL_TAB_SET_DEFAULT_RIGHT;
        } else {
            tr.appendChild(iconTd);
            tr.appendChild(labelTd);
            iconClass += CSS_DEFAULT.PANEL_TAB_SET_DEFAULT_LEFT;
        }
        iconClass = Util.getQualifiedStyleClass(tabSet,iconClass);
        icon.setAttribute(HTML.CLASS_ATTR, iconClass);
    }

    String getTabOnClass(String placement) {
        return Util.getQualifiedStyleClass(this, 
                CSS_DEFAULT.PANEL_TAB_SET_DEFAULT_TABONCLASS +
                placement);
    }
    
    String getTabOffClass(String placement) {
        return Util.getQualifiedStyleClass(this, 
        CSS_DEFAULT.PANEL_TAB_SET_DEFAULT_TABOFFCLASS +
        placement);
    }
    
    String getTabOverClass(String placement) {
        return Util.getQualifiedStyleClass(this, 
                CSS_DEFAULT.PANEL_TAB_SET_DEFAULT_TABOVERCLASS +
                placement);    
    }
    
    /**
     * @param labelWidth
     */
    public void setLabelWidth(String labelWidth) {
        this.labelWidth = labelWidth;
    }

    /**
     * @return labelWidth
     */
    public String getLabelWidth() {
        if (labelWidth != null) {
            return labelWidth;
        }
        ValueBinding vb = getValueBinding("labelWidth");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }
    
    /**
     * @param labelWrap
     */
    public void setLabelWrap(boolean labelWrap) {
        this.labelWrap = new Boolean(labelWrap);
    }

    /**
     * @return labelWrap
     */
    public boolean isLabelWrap() {
        if (labelWrap != null) {
            return labelWrap.booleanValue();
        }
        ValueBinding vb = getValueBinding("labelWrap");
        return vb != null ?
               ((Boolean) vb.getValue(getFacesContext())).booleanValue() :
               false;
    }
    
    public UIComponent getLabelFacet() {
        return (UIComponent) getFacet("label");
    }    
}
