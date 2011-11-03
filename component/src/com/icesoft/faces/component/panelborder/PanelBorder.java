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

package com.icesoft.faces.component.panelborder;

import com.icesoft.faces.component.CSS_DEFAULT;
import com.icesoft.faces.component.ext.taglib.Util;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import java.util.ArrayList;
import java.util.List;

/**
 * PanelBorder is a JSF component class that represent an ICEfaces border layout
 * panel. The "north", "west", "east", "center" and "south" named facets
 * represent the components responsible for rendering the north, west, east,
 * center and south areas of the PanelBorder.
 * <p/>
 * This component extends the JSF HtmlPanelGroup component.
 * <p/>
 * By default this component is rendered by the "com.icesoft.faces.BorderLayout"
 * renderer type.
 *
 * @version beta 1.0
 */
public class PanelBorder
        extends HtmlPanelGroup {
    public static final String COMPONENT_TYPE =
        "com.icesoft.faces.BorderLayout";
    public static final String COMPONENT_FAMILY = "javax.faces.Panel";
    public static final String DEFAULT_RENDERER_TYPE =
        "com.icesoft.faces.BorderLayout";
    
    public static final String NORTH_LAYOUT = "north";
    public static final String WEST_LAYOUT = "west";
    public static final String CENTER_LAYOUT = "center";
    public static final String EAST_LAYOUT = "east";
    public static final String SOUTH_LAYOUT = "south";
    
    private String _layout = null;
    private String styleClass = null;
    private String style = null;
    private String align = null;
    private String border = null;
    private String bgcolor = null;
    private String cellpadding = null;
    private String cellspacing = null;
    private String frame = null;
    private String rules = null;
    private String summary = null;
    private String height = null;
    private String width = null;
    private String dir = null;
    private String lang = null;
    private String title = null;
    private String renderedOnUserRole = null;

    /**
     * <p>Return the value of the <code>north</code> property.</p>
     */
    public UIComponent getNorth() {
        return (UIComponent) getFacet(PanelBorder.NORTH_LAYOUT);
    }

    /**
     * <p>Return the value of the <code>west</code> property.</p>
     */
    public UIComponent getWest() {
        return (UIComponent) getFacet(PanelBorder.WEST_LAYOUT);
    }

    /**
     * <p>Return the value of the <code>east</code> property.</p>
     */
    public UIComponent getEast() {
        return (UIComponent) getFacet(PanelBorder.EAST_LAYOUT);
    }

    /**
     * <p>Return the value of the <code>center</code> property.</p>
     */
    public UIComponent getCenter() {
        return (UIComponent) getFacet(PanelBorder.CENTER_LAYOUT);
    }

    /**
     * <p>Return the value of the <code>south</code> property.</p>
     */
    public UIComponent getSouth() {
        return (UIComponent) getFacet(PanelBorder.SOUTH_LAYOUT);
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

    public PanelBorder() {
        setRendererType(DEFAULT_RENDERER_TYPE);
    }

    /**
     * <p>Return the value of the <code>COMPONENT_FAMILY</code> of this
     * component.</p>
     */
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    /**
     * <p>Set the value of the <code>layout</code> property.</p>
     */
    public void setLayout(String layout) {
        _layout = layout;
    }

    /**
     * @deprecated <p>Return the value of the <code>layout</code> property.
     *             </p>
     */
    public String getLayout() {
        if (_layout != null) {
            return _layout;
        }
        ValueBinding vb = getValueBinding("layout");
        return vb != null ? (String) vb.getValue(getFacesContext()) : "none";
    }

    /**
     * <p>Return the value of the <code>northClass</code> property.</p>
     */
    public String getNorthClass() {
        return Util.getQualifiedStyleClass(this,
                              CSS_DEFAULT.PANEL_BORDER_DEFAULT_NORTH_CLASS);
    }

    /**
     * <p>Return the value of the <code>westClass</code> property.</p>
     */
    public String getWestClass() {
        return Util.getQualifiedStyleClass(this,
                               CSS_DEFAULT.PANEL_BORDER_DEFAULT_WEST_CLASS);
    }

    /**
     * <p>Return the value of the <code>eastClass</code> property.</p>
     */
    public String getEastClass() {
        return Util.getQualifiedStyleClass(this,
                               CSS_DEFAULT.PANEL_BORDER_DEFAULT_EAST_CLASS);
    }

    /**
     * <p>Return the value of the <code>centerClass</code> property.</p>
     */
    public String getCenterClass() {
        return Util.getQualifiedStyleClass(this,
                               CSS_DEFAULT.PANEL_BORDER_DEFAULT_CENTER_CLASS);
    }


    /**
     * <p>Return the value of the <code>southClass</code> property.</p>
     */
    public String getSouthClass() {
        return Util.getQualifiedStyleClass(this,
                               CSS_DEFAULT.PANEL_BORDER_DEFAULT_SOUTH_CLASS);
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
                        CSS_DEFAULT.PANEL_BORDER_DEFAULT,
                        "styleClass");
        
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
     * <p>Set the value of the <code>style</code> property.</p>
     */
    public void setStyle(String style) {
        this.style = style;
    }

    private transient Object values[];
    /**
     * <p>Gets the state of the instance as a <code>Serializable</code>
     * Object.</p>
     */
    public Object saveState(FacesContext context) {
        if(values == null){
            values = new Object[28];
        }
        values[0] = super.saveState(context);
        values[1] = _layout;
        values[2] = styleClass;
        values[3] = renderedOnUserRole;
        values[4] = align;
        values[5] = border;
        values[6] = bgcolor;
        values[7] = cellpadding;
        values[8] = cellspacing;
        values[9] = frame;
        values[10] = rules;
        values[11] = summary;
        values[12] = height;
        values[13] = width;
        values[14] = dir;
        values[15] = lang;
        values[16] = title;
        values[17] = Boolean.valueOf(renderNorth);
        values[18] = Boolean.valueOf(renderNorthSet);
        values[19] = Boolean.valueOf(renderSouth);
        values[20] = Boolean.valueOf(renderSouthSet);
        values[21] = Boolean.valueOf(renderEast);
        values[22] = Boolean.valueOf(renderEastSet);
        values[23] = Boolean.valueOf(renderWest);
        values[24] = Boolean.valueOf(renderWestSet);
        values[25] = Boolean.valueOf(renderCenter);
        values[26] = Boolean.valueOf(renderCenterSet);
        values[27] = style;
        
        return ((Object) (values));
    }

    /**
     * <p>Perform any processing required to restore the state from the entries
     * in the state Object.</p>
     */
    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        _layout = (String) values[1];
        styleClass = (String) values[2];
        renderedOnUserRole = (String) values[3];
        align = (String) values[4];
        border = (String) values[5];
        bgcolor = (String) values[6];
        cellpadding = (String) values[7];
        cellspacing = (String) values[8];
        frame = (String) values[9];
        rules = (String) values[10];
        summary = (String) values[11];
        height = (String) values[12];
        width = (String) values[13];
        dir = (String) values[14];
        lang = (String) values[15];
        title = (String) values[16];
        renderNorth = Boolean.valueOf(values[17].toString()).booleanValue();
        renderNorthSet = Boolean.valueOf(values[18].toString()).booleanValue();
        renderSouth = Boolean.valueOf(values[19].toString()).booleanValue();
        renderSouthSet = Boolean.valueOf(values[20].toString()).booleanValue();
         renderEast = Boolean.valueOf(values[21].toString()).booleanValue();
         renderEastSet = Boolean.valueOf(values[22].toString()).booleanValue();
        renderWest = Boolean.valueOf(values[23].toString()).booleanValue();
         renderWestSet = Boolean.valueOf(values[24].toString()).booleanValue();
         renderCenter = Boolean.valueOf(values[25].toString()).booleanValue();
         renderCenterSet = Boolean.valueOf(values[26].toString()).booleanValue();
         style = (String)values[27];
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


    public void setAlign(String align) {
        this.align = align;
    }

    public void setBorder(String border) {
        this.border = border;
    }

    public void setBgcolor(String bgcolor) {
        this.bgcolor = bgcolor;
    }

    public void setCellpadding(String cellpadding) {
        this.cellpadding = cellpadding;
    }

    public void setCellspacing(String cellspacing) {
        this.cellspacing = cellspacing;
    }

    public void setFrame(String frame) {
        this.frame = frame;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the value of align property
     */
    public String getAlign() {
        if (align != null) {
            return align;
        }
        ValueBinding vb = getValueBinding("align");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * @return the value of border property
     */
    public String getBorder() {
        if (border != null) {
            return border;
        }
        ValueBinding vb = getValueBinding("border");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * @return the value of bgcolor property
     */
    public String getBgcolor() {
        if (bgcolor != null) {
            return bgcolor;
        }
        ValueBinding vb = getValueBinding("bgcolor");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * @return the value of cellpadding property
     */
    public String getCellpadding() {
        if (cellpadding != null) {
            return cellpadding;
        }
        ValueBinding vb = getValueBinding("cellpadding");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * @return the value of cellspacing property
     */
    public String getCellspacing() {
        if (cellspacing != null) {
            return cellspacing;
        }
        ValueBinding vb = getValueBinding("cellspacing");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * @return the value of frame property
     */
    public String getFrame() {
        if (frame != null) {
            return frame;
        }
        ValueBinding vb = getValueBinding("frame");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * @return the value of rules property
     */
    public String getRules() {
        if (rules != null) {
            return rules;
        }
        ValueBinding vb = getValueBinding("rules");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * @return the value of summary property
     */
    public String getSummary() {
        if (summary != null) {
            return summary;
        }
        ValueBinding vb = getValueBinding("summary");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * @return the value of height property
     */
    public String getHeight() {
        if (height != null) {
            return height;
        }
        ValueBinding vb = getValueBinding("height");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * @return the value of width property
     */
    public String getWidth() {
        if (width != null) {
            return width;
        }
        ValueBinding vb = getValueBinding("width");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
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


    private boolean renderCenter = true;
    private boolean renderCenterSet = false;

    public boolean isRenderCenter() {
        if (getLayout().endsWith(PanelBorder.CENTER_LAYOUT)) {
            return false;
        }
        if (getLayout().equalsIgnoreCase(PanelBorderRenderer.DEFAULT_LAYOUT)) {
            return true;
        }
        if (this.renderCenterSet) {
            return (this.renderCenter);
        }
        ValueBinding vb = getValueBinding("renderCenter");
        if (vb != null) {
            return (Boolean.TRUE.equals(vb.getValue(getFacesContext())));
        } else {
            return (this.renderCenter);
        }
    }

    public void setRenderCenter(boolean renderCenter) {
        if (renderCenter != this.renderCenter) {
            this.renderCenter = renderCenter;
        }
        this.renderCenterSet = true;
    }


    private boolean renderEast = true;
    private boolean renderEastSet = false;

    public boolean isRenderEast() {
        if (getLayout().endsWith(PanelBorder.EAST_LAYOUT) ||
            getLayout().equalsIgnoreCase(PanelBorderRenderer.CENTER_ONLY)) {
            return false;
        }
        if (getLayout().equalsIgnoreCase(PanelBorderRenderer.DEFAULT_LAYOUT)) {
            return true;
        }
        if (this.renderEastSet) {
            return (this.renderEast);
        }
        ValueBinding vb = getValueBinding("renderEast");
        if (vb != null) {
            return (Boolean.TRUE.equals(vb.getValue(getFacesContext())));
        } else {
            return (this.renderEast);
        }
    }

    public void setRenderEast(boolean renderEast) {
        if (renderEast != this.renderEast) {
            this.renderEast = renderEast;
        }
        this.renderEastSet = true;
    }

    private boolean renderNorth = true;
    private boolean renderNorthSet = false;

    public boolean isRenderNorth() {
        if (getLayout().endsWith(PanelBorder.NORTH_LAYOUT) ||
            getLayout().equalsIgnoreCase(PanelBorderRenderer.CENTER_ONLY)) {
            return false;
        }
        if (getLayout().equalsIgnoreCase(PanelBorderRenderer.DEFAULT_LAYOUT)) {
            return true;
        }
        if (this.renderNorthSet) {
            return (this.renderNorth);
        }
        ValueBinding vb = getValueBinding("renderNorth");
        if (vb != null) {
            return (Boolean.TRUE.equals(vb.getValue(getFacesContext())));
        } else {
            return (this.renderNorth);
        }
    }

    public void setRenderNorth(boolean renderNorth) {
        if (renderNorth != this.renderNorth) {
            this.renderNorth = renderNorth;
        }
        this.renderNorthSet = true;
    }

    private boolean renderSouth = true;
    private boolean renderSouthSet = false;

    public boolean isRenderSouth() {
        if (getLayout().endsWith(PanelBorder.SOUTH_LAYOUT) ||
            getLayout().equalsIgnoreCase(PanelBorderRenderer.CENTER_ONLY)) {
            return false;
        }
        if (getLayout().equalsIgnoreCase(PanelBorderRenderer.DEFAULT_LAYOUT)) {
            return true;
        }
        if (this.renderSouthSet) {
            return (this.renderSouth);
        }
        ValueBinding vb = getValueBinding("renderSouth");
        if (vb != null) {
            return (Boolean.TRUE.equals(vb.getValue(getFacesContext())));
        } else {
            return (this.renderSouth);
        }
    }

    public void setRenderSouth(boolean renderSouth) {
        if (renderSouth != this.renderSouth) {
            this.renderSouth = renderSouth;
        }
        this.renderSouthSet = true;
    }

    private boolean renderWest = true;
    private boolean renderWestSet = false;

    public boolean isRenderWest() {
        if (getLayout().endsWith(PanelBorder.WEST_LAYOUT) ||
            getLayout().equalsIgnoreCase(PanelBorderRenderer.CENTER_ONLY)) {
            return false;
        }
        if (getLayout().equalsIgnoreCase(PanelBorderRenderer.DEFAULT_LAYOUT)) {
            return true;
        }
        if (this.renderWestSet) {
            return (this.renderWest);
        }
        ValueBinding vb = getValueBinding("renderWest");
        if (vb != null) {
            return (Boolean.TRUE.equals(vb.getValue(getFacesContext())));
        } else {
            return (this.renderWest);
        }
    }

    public void setRenderWest(boolean renderWest) {
        if (renderWest != this.renderWest) {
            this.renderWest = renderWest;
        }
        this.renderWestSet = true;
    }

    public List getLayoutAsList() {
        List defaultLayout = new ArrayList(5);
        if (isRenderCenter()) {
            if (!defaultLayout.contains(PanelBorder.CENTER_LAYOUT)) {
                defaultLayout.add(PanelBorder.CENTER_LAYOUT);
            }
        } else {
            defaultLayout.remove(PanelBorder.CENTER_LAYOUT);
        }
        if (isRenderEast()) {
            if (!defaultLayout.contains(PanelBorder.EAST_LAYOUT)) {
                defaultLayout.add(PanelBorder.EAST_LAYOUT);
            }
        } else {
            defaultLayout.remove(PanelBorder.EAST_LAYOUT);
        }
        if (isRenderNorth()) {
            if (!defaultLayout.contains(PanelBorder.NORTH_LAYOUT)) {
                defaultLayout.add(PanelBorder.NORTH_LAYOUT);
            }
        } else {
            defaultLayout.remove(PanelBorder.NORTH_LAYOUT);
        }
        if (isRenderSouth()) {
            if (!defaultLayout.contains(PanelBorder.SOUTH_LAYOUT)) {
                defaultLayout.add(PanelBorder.SOUTH_LAYOUT);
            }
        } else {
            defaultLayout.remove(PanelBorder.SOUTH_LAYOUT);
        }
        if (isRenderWest()) {
            if (!defaultLayout.contains(PanelBorder.WEST_LAYOUT)) {
                defaultLayout.add(PanelBorder.WEST_LAYOUT);
            }
        } else {
            defaultLayout.remove(PanelBorder.WEST_LAYOUT);
        }

        return defaultLayout;
    }


}
