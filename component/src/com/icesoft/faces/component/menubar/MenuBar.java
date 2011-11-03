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

package com.icesoft.faces.component.menubar;

import com.icesoft.faces.component.CSS_DEFAULT;
import com.icesoft.faces.component.ext.taglib.Util;
import com.icesoft.faces.component.menupopup.MenuPopup;
import com.icesoft.faces.context.effects.JavascriptContext;

import javax.faces.component.NamingContainer;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;
import javax.faces.event.ActionEvent;

import java.io.IOException;
import java.util.Iterator;


/**
 * MenuBar is a JSF component class representing the ICEfaces menu bar. <p>The
 * menuBar component provides a robust menu system that supports:
 * <p/>
 * 1. Nested child menuItem and menuItemSeparator components. Support for
 * menuItemCheckbox and menuItemRadio components are planned for a future
 * release.<br/> 2. Horizontal (default) and Vertical menu orientations. Defines
 * whether the submenus of the top-level menu items appear beside or below the
 * top-level menu items.<br/> 3. Definition of the heirarchy of menu items and
 * their submenus in one of two ways:<br/> - by using a binding to a bean method
 * that returns a (potentially) dynamic heirarchy of menu items.<br/> - by
 * statically defining the heirarchy in the JSPX page.<br/> 4. The action
 * attribute of the contained menuItem tags or instances can be defined to
 * indicate a string or a backing bean method that can be used in application
 * navigation.<br/> 5. The actionListener attribute of the contained menuItem
 * tags or instances can be defined to indicate an actionListener that resides
 * in a backing bean.<br/>
 * <p/>
 * This component extends the JSF UICommand component and implements the
 * NamingContainer interface.
 * <p/>
 * By default the MenuBar is rendered by the "com.icesoft.faces.View" renderer
 * type.
 *
 * @author Chris Brown
 * @author gmccleary
 * @version 1.1
 */
public class MenuBar extends UICommand implements NamingContainer {

    // default style classes
    private static final String DEFAULT_IMAGEDIR = "/xmlhttp/css/xp/css-images/";
    /**
     * String constant vertical orientation
     */
    public static final String ORIENTATION_VERTICAL = "vertical";
    /**
     * String contant horizontal orientation
     */
    public static final String ORIENTATION_HORIZONTAL = "horizontal";
    /**
     * String contant default orientation
     */
    public static final String DEFAULT_ORIENTATION = ORIENTATION_HORIZONTAL;
    /**
     * String constant menu id prefix
     */
    public static final String ID_PREFIX = "menu-";

    private String imageDir;
    private String orientation; // horizontal | vertical ; default = horizontal
    private String style;
    private String renderedOnUserRole = null;
    private String noIcons;
    private Boolean displayOnClick;
    private Boolean keyboardNavigationEnabled;    
    /**
     * default no args constructor
     */
    public MenuBar() {
        JavascriptContext.includeLib(JavascriptContext.ICE_EXTRAS,
                                     FacesContext.getCurrentInstance());
    }

    /**
     * <p>Return the value of the <code>COMPONENT_FAMILY</code> of this
     * component.</p>
     *
     * @return String component family
     */
    public String getFamily() {
        return "com.icesoft.faces.Menu";
    }

    /**
     * @return String component type
     */
    public String getComponentType() {
        return "com.icesoft.faces.Menu";
    }

    /* (non-Javadoc)
     * @see javax.faces.component.UIComponentBase#getRendererType()
     */
    public String getRendererType() {
        return "com.icesoft.faces.View";
    }

    /**
     * Return the value of the noIcons property. A return value of "true"
     * indicates that noIcons should be rendered on all of the MenuBar
     * subMenus.
     *
     * @return String value of Boolean noIcons
     */
    public String getNoIcons() {

        if (noIcons != null) {
            return noIcons;
        }
        ValueBinding vb = getValueBinding("noIcons");
        if (vb != null) {
            return vb.getValue(getFacesContext()).toString();
        }
        return String.valueOf(false);
    }

    /**
     * Set the value of the noIcons property. Setting this value to "true" will
     * cause all subMenus of this MenuBar to be rendered without icons and
     * spacers.
     *
     * @param b
     */
    public void setNoIcons(String b) {
        noIcons = b;
    }

    /**
     * <p>Set the value of the <code>style</code> property.</p>
     *
     * @param style
     */
    public void setStyle(String style) {
        this.style = style;
    }

    /**
     * <p>Return the value of the <code>style</code> property.</p>
     *
     * @return String style
     */
    public String getStyle() {
        if (style != null) {
            return style;
        }
        ValueBinding vb = getValueBinding("style");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    // GETTERS AND SETTERS

    /**
     * <p>Return the value of the <code>imageDir</code> property.</p>
     *
     * @return String imageDir
     */
    public String getImageDir() {
        if (imageDir != null) {
            return imageDir;
        }
        ValueBinding vb = getValueBinding("imageDir");
        if (vb != null) {
            return (String) vb.getValue(getFacesContext());
        }
        return DEFAULT_IMAGEDIR;
    }

    /**
     * <p>Set the value of the <code>imageDir</code> property.</p>
     *
     * @param imageDir
     */
    public void setImageDir(String imageDir) {
        this.imageDir = imageDir;
    }

    /**
     * <p>Return the value of the <code>orientation</code> property.</p>
     *
     * @return String orientation
     */
    public String getOrientation() {
        if (orientation != null) {
            return orientation;
        }
        ValueBinding vb = getValueBinding("orientation");
        if (vb != null) {
            return (String) vb.getValue(getFacesContext());
        }
        return DEFAULT_ORIENTATION;
    }

    /**
     * <p>Set the value of the <code>orientation</code> property.</p>
     *
     * @param orient
     */
    public void setOrientation(String orient) {
        orientation = orient;
    }

    /* (non-Javadoc)
     * @see javax.faces.component.UIComponent#processDecodes(javax.faces.context.FacesContext)
     */
    public void processDecodes(FacesContext context) {
        if (context == null) {
            throw new NullPointerException("context");
        }
        if (!isRendered()) {
            return;
        }

        Iterator kids = getFacetsAndChildren();
        while (kids.hasNext()) {
            UIComponent kid = (UIComponent) kids.next();
            kid.processDecodes(context);
        }
        
        try {
            decode(context);
        } catch (RuntimeException e) {
            context.renderResponse();
            throw e;
        }
    }

    /* (non-Javadoc)
     * @see javax.faces.component.UIComponent#queueEvent(javax.faces.event.FacesEvent)
     */
    public void queueEvent(FacesEvent e) {
        // ICE-1956 UICommand subclasses shouldn't call super.queueEvent
        //  on ActionEvents or else the immediate flag is ignored
        if( (e instanceof ActionEvent) && !this.equals(e.getComponent()) && getParent() != null) {
            getParent().queueEvent(e);
        }
        else {
            super.queueEvent(e);
        }
    }

    /* (non-Javadoc)
     * @see javax.faces.component.UIComponent#broadcast(javax.faces.event.FacesEvent)
     */
    public void broadcast(FacesEvent event) throws AbortProcessingException {
        super.broadcast(event);
        return;

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
    
    private String styleClass;
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
        String defaultStyle = getComponentRootStyle();
        String userDefinedClass = styleClass;
        if (MenuBar.ORIENTATION_VERTICAL.equalsIgnoreCase(
                getOrientation())){
            defaultStyle+=CSS_DEFAULT.MENU_BAR_VERTICAL_SUFFIX_STYLE;
            if (userDefinedClass != null) {        
                userDefinedClass+=CSS_DEFAULT.MENU_BAR_VERTICAL_SUFFIX_STYLE;
            }
        }
        return Util.getQualifiedStyleClass(this, 
                userDefinedClass, 
                defaultStyle, 
                "styleClass");
    }
    
    public String getItemStyleClass() {
        return Util.getQualifiedStyleClass(this, 
                                CSS_DEFAULT.MENU_BAR_ITEM_STYLE);
    }
    
    public String getItemLabelStyleClass() {
        return Util.getQualifiedStyleClass(this, 
                                CSS_DEFAULT.MENU_BAR_ITEM_LABEL_STYLE);
    }
    
    public String getItemImageStyleClass() {
        return Util.getQualifiedStyleClass(this, 
                                CSS_DEFAULT.MENU_BAR_ITEM_STYLE+
                                CSS_DEFAULT.MENU_ITEM_IMAGE_STYLE);
    }
    
    public String getSubMenuStyleClass() {
        return Util.getQualifiedStyleClass(this, 
                                CSS_DEFAULT.MENU_BAR_SUB_MENU_STYLE);
    }
    
    public String getSubMenuIndicatorStyleClass() {
        return Util.getQualifiedStyleClass(this, 
                                CSS_DEFAULT.MENU_BAR_SUB_MENU_INDICATOR_STYLE);
    }
    
    public String getComponentRootStyle() {
        return CSS_DEFAULT.MENU_BAR_STYLE;
    }
    
    public void setDisplayOnClick(boolean displayOnClick) {
        this.displayOnClick = Boolean.valueOf(displayOnClick);
    }
    
    public boolean isDisplayOnClick() {
        if (displayOnClick != null) {
            return displayOnClick.booleanValue();
        }
        ValueBinding vb = getValueBinding("displayOnClick");
        if (vb != null) {
            return ((Boolean) vb.getValue(getFacesContext())).booleanValue();
        }
        return false;
    }

    public void encodeBegin(FacesContext context) throws IOException {
        if (isKeyboardNavigationEnabled()) {
            String call = "new Ice.MenuBarKeyNavigator('" + 
            getClientId(context) +"', " +
            isDisplayOnClick() +");";
            JavascriptContext.addJavascriptCall(context, call);
        }
        super.encodeBegin(context);
    }

    public Object saveState(FacesContext context) {
        Object values[] = new Object[9];
        values[0] = super.saveState(context);
        values[1] = displayOnClick;
        values[2] = imageDir;
        values[3] = noIcons;
        values[4] = orientation;
        values[5] = renderedOnUserRole;
        values[6] = style;
        values[7] = styleClass;
        values[8] = keyboardNavigationEnabled;
        return values;
    }

    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        displayOnClick = (Boolean) values[1];
        imageDir = (String) values[2];
        noIcons = (String) values[3];
        orientation = (String) values[4];
        renderedOnUserRole = (String) values[5];
        style = (String) values[6];
        styleClass = (String) values[7];
        keyboardNavigationEnabled = (Boolean) values[8];        
    }
    
    public boolean isKeyboardNavigationEnabled() {
        if (keyboardNavigationEnabled != null) {
            return keyboardNavigationEnabled.booleanValue();
        }
        ValueBinding vb = getValueBinding("keyboardNavigationEnabled");
        Boolean boolVal = vb != null ?
                (Boolean) vb.getValue(getFacesContext()) : null;
        return boolVal != null ? boolVal.booleanValue() : true;
    }

    public void setKeyboardNavigationEnabled(boolean keyboardNavigationEnabled) {
        this.keyboardNavigationEnabled = new Boolean(keyboardNavigationEnabled);
    }    
}

