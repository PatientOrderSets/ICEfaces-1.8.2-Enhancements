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
import com.icesoft.faces.component.InvalidComponentTypeException;
import com.icesoft.faces.component.PORTLET_CSS_DEFAULT;
import com.icesoft.faces.component.ext.HtmlCommandLink;
import com.icesoft.faces.component.ext.HtmlGraphicImage;
import com.icesoft.faces.component.ext.HtmlOutputText;
import com.icesoft.faces.component.ext.HtmlPanelGroup;
import com.icesoft.faces.component.ext.taglib.Util;
import com.icesoft.faces.component.menupopup.MenuPopup;
import com.icesoft.faces.context.DOMContext;
import com.icesoft.faces.renderkit.dom_html_basic.HTML;
import com.icesoft.faces.util.CoreUtils;
import com.icesoft.faces.util.DOMUtils;
import com.icesoft.util.pooling.ClientIdPool;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import java.beans.Beans;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.List;


public class MenuItemRenderer extends MenuItemRendererBase {

    private static final String HIDDEN_FIELD_NAME = "cl";


    private static final String SUB = "_sub";
    private static final String KEYWORD_NULL = "null";
    private static final String KEYWORD_THIS = "this";
    private static final String DEFAULT_IMAGEDIR = "/xmlhttp/css/xp/css-images/";
    private static final String SUBMENU_IMAGE = "submenu.gif";
    private static final String LINK_SUFFIX = "link";
    private static final String GROUP_SUFFIX = "grp";
    private static final String INDICATOR_SUFFIX = "ind";
    private static final String ICON_SUFFIX = "icn";
    private static final String OUTPUT_SUFFIX = "out";

    public void decode(FacesContext facesContext, UIComponent uiComponent) {

        validateParameters(facesContext, uiComponent, null);
        if (isStatic(uiComponent)) {
            return;
        }
        String componentId = uiComponent.getClientId(facesContext);
        Map requestParameterMap =
                facesContext.getExternalContext().getRequestParameterMap();
        String hiddenFieldName = deriveCommonHiddenFieldName(facesContext,
                                                             (MenuItem) uiComponent);
        String hiddenFieldNameInRequestMap =
                (String) requestParameterMap.get(hiddenFieldName);

        if (hiddenFieldNameInRequestMap == null
            || hiddenFieldNameInRequestMap.equals("")) {
            // this command link did not invoke the submit
            return;
        }

        // debugging
        //examineRequest(facesContext, uiComponent, requestParameterMap, hiddenFieldName, hiddenFieldNameInRequestMap);
        String commandLinkClientId = componentId + ":" + LINK_SUFFIX;
        if (hiddenFieldNameInRequestMap.equals(commandLinkClientId)) {
            ActionEvent actionEvent = new ActionEvent(uiComponent);
            uiComponent.queueEvent(actionEvent);
        }
    }


    // this component renders its children, so, this method will be called once
    // for each top-level menu node. From there, this component will manage
    // the rendering of all children components. The idea is to end up with
    // a fairly flat structure. There will exist a master div that contains the
    // entire menu. Inside that div there will exist a first-order child div for
    // each top level menu item and there will exist a first-order child div for
    // each submenu. Inside the submenu div there will exist a div to hold each
    // menu item in the submenu. Note that there is no nesting of submenu items
    public void encodeBegin(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {

        if (!uiComponent.isRendered()) {
            return;
        }

        if (!(uiComponent.getParent() instanceof MenuBar) &&
            !(uiComponent.getParent() instanceof MenuItems)) {
            throw new InvalidComponentTypeException(
                    "MenuBar expected as parent of top-level MenuItem");
        }
        // Set the clientId to null as a side effect, so that a unique
        // clientId will be generated when we are in a UIData component
        uiComponent.setId(uiComponent.getId()); // ICE-3064
        // If static model declaration (in the jsp) is employed then the
        // immediate parent will be the Menu component
        // Else if the model declaration is in the bean class then there
        // is a MenuItems between the MenuItem and the Menu component
        MenuBar menuComponent = null;
        if (uiComponent.getParent() instanceof MenuBar) {
            menuComponent = (MenuBar) uiComponent.getParent();
        } else if (uiComponent.getParent().getParent() instanceof MenuBar) {
            menuComponent = (MenuBar) uiComponent.getParent().getParent();
        } else {
            throw new InvalidComponentTypeException("Expecting MenuBar");
        }

        // is vertical ?
        boolean vertical = menuComponent.getOrientation().equalsIgnoreCase(
                MenuBar.ORIENTATION_VERTICAL);

        validateParameters(facesContext, uiComponent, MenuItemBase.class);

        // first render
        DOMContext domContext =
                DOMContext.attachDOMContext(facesContext, uiComponent);
        String clientId = uiComponent.getClientId(facesContext);
        if (!domContext.isInitialized()) {
            Element topLevelDiv = domContext.createRootElement(HTML.DIV_ELEM);
            topLevelDiv.setAttribute(HTML.ID_ATTR, clientId);
        }
        Element topLevelDiv = (Element) domContext.getRootNode();
      //  topLevelDiv.setAttribute(HTML.NAME_ATTR, "TOP_LEVEL");

        String rootItemSubClass = CSS_DEFAULT.MENU_BAR_ITEM_STYLE;
        if (vertical) {
            rootItemSubClass = CSS_DEFAULT.MENU_BAR_VERTICAL_SUFFIX_STYLE +
                                rootItemSubClass;
        }
        String qualifiedName = ((MenuItem) uiComponent).
        getUserDefinedStyleClass(menuComponent.getItemStyleClass(), 
                rootItemSubClass);
        String call = null;
        if (uiComponent.getChildCount() > 0) {
            topLevelDiv.setAttribute(HTML.CLASS_ATTR, CoreUtils.addPortletStyleClassToQualifiedClass(
                    qualifiedName, rootItemSubClass, PORTLET_CSS_DEFAULT.PORTLET_MENU_CASCADE_ITEM));
            String displayEvent = HTML.ONMOUSEOVER_ATTR;
            if (vertical) {
                String supermenu = menuComponent.getClientId(facesContext);
                Element parentNode = (Element) topLevelDiv.getParentNode();
                if (parentNode.getAttribute(HTML.NAME_ATTR).equals("TOP_LEVEL_SUBMENU")) {
                    supermenu += "_sub";
                }
                call = "Ice.Menu.hideOrphanedMenusNotRelatedTo(this);" +
                expand(supermenu, clientId + "_sub",
                        KEYWORD_THIS) + "";
            } else {
                call = "Ice.Menu.hideOrphanedMenusNotRelatedTo(this);" +
                    expand("this", clientId + "_sub",
                           KEYWORD_NULL) + "";
            }
            if (menuComponent instanceof MenuPopup) {
                topLevelDiv.setAttribute(displayEvent, call);
            }
        } else {
            topLevelDiv.setAttribute(HTML.CLASS_ATTR, CoreUtils.addPortletStyleClassToQualifiedClass(
                    qualifiedName, rootItemSubClass, PORTLET_CSS_DEFAULT.PORTLET_MENU_ITEM));
            if (menuComponent instanceof MenuPopup) {
                topLevelDiv.setAttribute(HTML.ONMOUSEOVER_ATTR,
                                       "Ice.Menu.hideOrphanedMenusNotRelatedTo(this);");
              }
        }
        
        if (menuComponent instanceof MenuPopup) {
            if (((MenuPopup)menuComponent).getHideOn() != null) {
                if (((MenuPopup)menuComponent).getHideOn().equals("mouseout")) {
                    topLevelDiv.setAttribute(HTML.ONMOUSEOUT_ATTR, "Ice.Menu.removeHoverClasses(this);Ice.Menu.hideOnMouseOut('" + menuComponent.getClientId(facesContext) + "',event);");
                } else {
                    topLevelDiv.setAttribute(HTML.ONMOUSEOUT_ATTR, "Ice.Menu.removeHoverClasses(this);");
                }
            } else {
                topLevelDiv.setAttribute(HTML.ONMOUSEOUT_ATTR, "Ice.Menu.removeHoverClasses(this);");
            }
        } else {
            if (!menuComponent.isDisplayOnClick()) {
                topLevelDiv.setAttribute(HTML.ONMOUSEOUT_ATTR, "Ice.Menu.hideOnMouseOut('" + menuComponent.getClientId(facesContext) + "',event);");
            }
        }
        
        String title = ((MenuItem) uiComponent).getTitle();
        if(title != null && title.length() > 0)
            topLevelDiv.setAttribute(HTML.TITLE_ATTR, title);

        DOMContext.removeChildren(topLevelDiv);
        Element masterDiv = topLevelDiv;
        String topLevelMenuId = menuComponent.getClientId(facesContext);
        while(masterDiv != null &&
              !masterDiv.getAttribute(HTML.ID_ATTR).equals(topLevelMenuId) )
        {
            masterDiv = (Element) masterDiv.getParentNode();
        }

        renderAnchor(facesContext, domContext, (MenuItem) uiComponent,
                     topLevelDiv, menuComponent, vertical);
        if (menuComponent.getStyleClass().startsWith("iceMnuPop")) {
            Element anch = (Element)topLevelDiv.getChildNodes().item(0);
            String onclick = anch.getAttribute(HTML.ONCLICK_ATTR);
            onclick = onclick.replaceAll("return false;", "Ice.Menu.hideAll(); return false;");
            anch.setAttribute(HTML.ONCLICK_ATTR, onclick);
            anch.setAttribute(HTML.ONFOCUS_ATTR, "this.parentNode.onmouseover();");              
        }
        if ((uiComponent.getChildCount() > 0) &&
            (((MenuItem) uiComponent).isChildrenMenuItem())) {
            renderChildrenRecursive(facesContext, menuComponent, uiComponent,
                                    vertical, masterDiv);

        }
    }

    private String expand(String supermenu, String submenu, String submenuDiv) {
        // delimit ids to force resolution from ids to elements
        if (!(supermenu.equalsIgnoreCase(KEYWORD_NULL)) &&
            !(supermenu.equalsIgnoreCase(KEYWORD_THIS))) {
            supermenu = "$('" + supermenu + "')";
        }
        if (!(submenu.equalsIgnoreCase(KEYWORD_NULL)) &&
            !(submenu.equalsIgnoreCase(KEYWORD_THIS))) {
            submenu = "$('" + submenu + "')";
        }
        if (!(submenuDiv.equalsIgnoreCase(KEYWORD_NULL)) &&
            !(submenuDiv.equalsIgnoreCase(KEYWORD_THIS))) {
            submenuDiv = "$('" + submenuDiv + "')";
        }
        return "Ice.Menu.show(" + supermenu + "," + submenu + "," + submenuDiv +
               ");";
    }

    protected static String deriveCommonHiddenFieldName(
            FacesContext facesContext,
            UIComponent uiComponent) {

        if (Beans.isDesignTime()){
            return "";
        }
        UIComponent parentNamingContainer = findForm(uiComponent);
        String parentClientId = parentNamingContainer.getClientId(facesContext);
        String hiddenFieldName = parentClientId
                                 + NamingContainer.SEPARATOR_CHAR
                                 + UIViewRoot.UNIQUE_ID_PREFIX
                                 + HIDDEN_FIELD_NAME;
        return hiddenFieldName;
    }
    
    /**
     * Used to add icon and label to the
     *  {Top Level, No Link, Horizontal} menu items
     * and add icon, label and indicator to the
     *  {Top Level, No Link, Vertical} menu items 
     * Doesn't render spacer, if no icon given
     */ 
    private Element makeTopLevelAnchor(FacesContext facesContext,
                                       MenuItem menuItem,
                                       MenuBar menuBar,
                                       boolean vertical) {
        DOMContext domContext =
                DOMContext.getDOMContext(facesContext, menuItem);
        Element anchor = domContext.createElement(HTML.ANCHOR_ELEM);
        if (!menuItem.isDisabled()) {
            String link = menuItem.getLink();
            if (link != null && link.length() > 0) {
                anchor.setAttribute(HTML.HREF_ATTR, link);
            }
            String target = menuItem.getTarget(); 
            if (target != null && target.length() > 0) {
                anchor.setAttribute(HTML.TARGET_ATTR, target);
            }
            String onclick = menuItem.getOnclick(); 
            if (onclick != null && onclick.length() > 0) {
                anchor.setAttribute(HTML.ONCLICK_ATTR, onclick);
            }
            if ( (!menuItem.isLinkSpecified()) &&
                 (onclick == null || onclick.length() == 0) ) {
                anchor.setAttribute(HTML.ONCLICK_ATTR, "return false;");
            }
        }
        
        if (vertical) {
            if (menuItem.getChildCount() > 0 && menuItem.isChildrenMenuItem()) {
                Element subImg = domContext.createElement(HTML.IMG_ELEM);
                subImg.setAttribute(HTML.SRC_ATTR,
                    CoreUtils.resolveResourceURL(facesContext, getSubMenuImage(menuBar)));
                subImg.setAttribute(HTML.STYLE_ATTR, "border:none;");
                subImg.setAttribute(HTML.CLASS_ATTR,
                    menuBar.getSubMenuIndicatorStyleClass());
                subImg.setAttribute(HTML.ALT_ATTR, "");
                anchor.appendChild(subImg);
            }
        }
        
        // only render icons if noIcons is false
        if (!menuBar.getNoIcons().equalsIgnoreCase("true")) {
            // do not render icon if it is the default blank image
            String icon = menuItem.getSpecifiedIcon();
            if (icon != null && icon.length() > 0) {
                Element iconImg = domContext.createElement(HTML.IMG_ELEM);
                iconImg.setAttribute(HTML.SRC_ATTR,
                    CoreUtils.resolveResourceURL(facesContext, icon));
                iconImg.setAttribute(HTML.STYLE_ATTR, "border:none;");
                iconImg.setAttribute(HTML.CLASS_ATTR, menuItem.
                    getUserDefinedStyleClass(menuBar.getItemImageStyleClass(),
                        (vertical?CSS_DEFAULT.MENU_BAR_VERTICAL_SUFFIX_STYLE:"")+
                        CSS_DEFAULT.MENU_BAR_ITEM_STYLE+
                        CSS_DEFAULT.MENU_ITEM_IMAGE_STYLE));
                String alt = menuItem.getAlt();
                if(alt != null && alt.length() > 0)
                    iconImg.setAttribute(HTML.ALT_ATTR, alt);
                anchor.appendChild(iconImg);
            }
        }

        // create a span for text
        Element span = domContext.createElement(HTML.SPAN_ELEM);
        if (!menuItem.isDisabled()) {
            anchor.setAttribute(HTML.CLASS_ATTR, "iceLink");
        } else {
            anchor.setAttribute(HTML.CLASS_ATTR, "iceLink-dis");
        }
        span.setAttribute(HTML.CLASS_ATTR, menuItem.
                getUserDefinedStyleClass(menuBar.getItemLabelStyleClass(), 
                        (vertical?CSS_DEFAULT.MENU_BAR_VERTICAL_SUFFIX_STYLE:"")+
                        CSS_DEFAULT.MENU_BAR_ITEM_LABEL_STYLE));
        anchor.appendChild(span);
        // create text
        Node text = domContext.createTextNode(DOMUtils.escapeAnsi(menuItem.getValue().toString()));
        span.appendChild(text);

        return anchor;
    }

    /**
     * Used to add icon, label and indicator to the
     * {Sub Level, No Link} menu items
     */ 
    private Element makeAnchor(FacesContext facesContext, DOMContext domContext,
                               MenuItem menuItem, MenuBar menuBar) {

        Element anchor = domContext.createElement(HTML.ANCHOR_ELEM);
        if (!menuItem.isDisabled()) {
            String link = menuItem.getLink();
            if (link != null && link.length() > 0) {
                anchor.setAttribute(HTML.HREF_ATTR, link);
            }
            String target = menuItem.getTarget(); 
            if (target != null && target.length() > 0) {
                anchor.setAttribute(HTML.TARGET_ATTR, target);
            }
            String onclick = menuItem.getOnclick(); 
            if (onclick != null && onclick.length() > 0) {
                anchor.setAttribute(HTML.ONCLICK_ATTR, onclick);
            }
            if ( (!menuItem.isLinkSpecified()) &&
                 (onclick == null || onclick.length() == 0) ) {
                anchor.setAttribute(HTML.ONCLICK_ATTR, "return false;");
            }
        }
        anchor.setAttribute(HTML.ID_ATTR, ClientIdPool.get(
                menuItem.getClientId(facesContext)+ ":"+LINK_SUFFIX));
        if (menuItem.getChildCount() > 0 && menuItem.isChildrenMenuItem()) {
            Element subImg = domContext.createElement(HTML.IMG_ELEM);
            subImg.setAttribute(HTML.SRC_ATTR,
                CoreUtils.resolveResourceURL(facesContext, getSubMenuImage(menuBar)));
            subImg.setAttribute(HTML.STYLE_ATTR, "border:none;");
            subImg.setAttribute(HTML.CLASS_ATTR,
                                menuBar.getSubMenuIndicatorStyleClass());
            subImg.setAttribute(HTML.ALT_ATTR, "");
            anchor.appendChild(subImg);
        }

        // only render icons if noIcons is false
        if (!menuBar.getNoIcons().equalsIgnoreCase("true")) {
            String icon = menuItem.getIcon(); 
            if (icon != null && icon.length() > 0) {
                Element iconImg = domContext.createElement(HTML.IMG_ELEM);
                iconImg.setAttribute(HTML.SRC_ATTR,
                    CoreUtils.resolveResourceURL(facesContext, icon));
                iconImg.setAttribute(HTML.STYLE_ATTR, "border:none;");
                iconImg.setAttribute(HTML.CLASS_ATTR,
                    menuItem.getImageStyleClass());
                String alt = menuItem.getAlt();
                if(alt != null && alt.length() > 0)
                    iconImg.setAttribute(HTML.ALT_ATTR, alt);
                anchor.appendChild(iconImg);
            }
        }

        // create a span for text
        Element span = domContext.createElement(HTML.SPAN_ELEM);
        if (!menuItem.isDisabled()) {
            anchor.setAttribute(HTML.CLASS_ATTR,"iceLink");
        } else {
            anchor.setAttribute(HTML.CLASS_ATTR,"iceLink-dis");
        }
        span.setAttribute(HTML.CLASS_ATTR, menuItem.getLabelStyleClass());

        anchor.appendChild(span);
        // create text
        Node text = domContext.createTextNode(DOMUtils.escapeAnsi(menuItem.getValue().toString()));
        span.appendChild(text);

        return anchor;
    }

    private void renderChildrenRecursive(FacesContext facesContext,
                                         MenuBar menuComponent,
                                         UIComponent uiComponent,
                                         boolean vertical, Element masterDiv) {
//StackTraceElement[] ste = Thread.currentThread().getStackTrace();
//System.out.println("renderChildrenRecursive()  "+ste.length+"  called in: " + this);
//System.out.println("renderChildrenRecursive()  "+ste.length+"    uiComponent: " + uiComponent);
//if(uiComponent instanceof MenuItem)
//  System.out.println("renderChildrenRecursive()  "+ste.length+"    Name: " + ((MenuItem)uiComponent).getValue());
        if (!uiComponent.isRendered()) {
            return;
        }
        
        DOMContext domContext =
                DOMContext.getDOMContext(facesContext, uiComponent);
        // create the div that will hold all the sub menu items
        Element submenuDiv = domContext.createElement(HTML.DIV_ELEM);
     //   submenuDiv.setAttribute(HTML.NAME_ATTR, "SUBMENU");
        String subMenuDivId = uiComponent.getClientId(facesContext) + SUB;
        submenuDiv.setAttribute(HTML.ID_ATTR, subMenuDivId);

        
        submenuDiv.setAttribute(HTML.CLASS_ATTR, menuComponent.getSubMenuStyleClass());
        submenuDiv.setAttribute(HTML.STYLE_ATTR, "display:none");
        masterDiv.appendChild(submenuDiv);
        // check if this menuItem is disabled, if it is lets disable the  children
        // render each menuItem in this submenu
        boolean disabled = false;
        Boolean disObj = (Boolean) uiComponent.getAttributes().get("disabled");
        if(disObj != null && disObj.booleanValue())
            disabled = true;
        for (int childIndex = 0; childIndex < uiComponent.getChildCount(); childIndex++) {
            UIComponent nextSubMenuItem =
                (UIComponent) uiComponent.getChildren().get(childIndex);
//System.out.println("renderChildrenRecursive()  "+ste.length+"      Render  childIndex: " + childIndex + "  child: " + nextSubMenuItem);
            if(nextSubMenuItem instanceof MenuItem) {
//System.out.println("renderChildrenRecursive()  "+ste.length+"              MenuItem  : " + ((MenuItem)nextSubMenuItem).getValue());
                renderSubMenuItem(
                    facesContext, domContext,
                    (MenuItem) nextSubMenuItem, menuComponent,
                    disabled, vertical,
                    submenuDiv, subMenuDivId);
            }
            else if(nextSubMenuItem instanceof MenuItems) {
//System.out.println("renderChildrenRecursive()  "+ste.length+"              MenuItems");
                renderSubMenuItems(
                    facesContext, domContext,
                    (MenuItems) nextSubMenuItem, menuComponent,
                    disabled, vertical,
                    submenuDiv, subMenuDivId);
            }
            else if(nextSubMenuItem instanceof MenuItemSeparator) {
//System.out.println("renderChildrenRecursive()  "+ste.length+"              MenuItemSeparator");
                renderSubMenuItemSeparator(
                    domContext, (MenuItemSeparator) nextSubMenuItem, submenuDiv);
            }
        }

        // recurse
        // check if parent is disabled , if it is the child items should also be disabled.
        // we should not render child MenuItems of a disabled menuItem

        for (int childIndex = 0; childIndex < uiComponent.getChildCount(); childIndex++) {
            UIComponent nextSubMenuItem =
                (UIComponent) uiComponent.getChildren().get(childIndex);
//System.out.println("renderChildrenRecursive()  "+ste.length+"      Recurse  childIndex: " + childIndex + "  child: " + nextSubMenuItem);
            if(nextSubMenuItem instanceof MenuItem) {
                MenuItem mi = (MenuItem) nextSubMenuItem;
//System.out.println("renderChildrenRecursive()  "+ste.length+"               MenuItem  : " + mi.getValue());
                if(mi.isChildrenMenuItem()) {
                    renderChildrenRecursive(
                        facesContext, menuComponent, mi,
                        vertical, masterDiv);
                }
            }
            else if(nextSubMenuItem instanceof MenuItems) {
//System.out.println("renderChildrenRecursive()  "+ste.length+"               MenuItems");
                MenuItems mis = (MenuItems) nextSubMenuItem;
                List kids = mis.prepareChildren();
                if(kids != null) {
                    for(int kidIndex = 0; kidIndex < kids.size(); kidIndex++) {
                        UIComponent nextKid = (UIComponent) kids.get(kidIndex);
//System.out.println("renderChildrenRecursive()  "+ste.length+"      Recurse  kidIndex: " + kidIndex + "  kid: " + nextKid);
                        if(nextKid instanceof MenuItem) {
                            MenuItem mi = (MenuItem) nextKid;
//System.out.println("renderChildrenRecursive()  "+ste.length+"               MenuItem  : " + mi.getValue());
                            if(mi.isChildrenMenuItem()) {
                                renderChildrenRecursive(
                                    facesContext, menuComponent, mi,
                                    vertical, masterDiv);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void renderSubMenuItemSeparator(DOMContext domContext, MenuItemSeparator nextSubMenuItem, Element submenuDiv) {
        if (!nextSubMenuItem.isRendered()) {
            return;
        }
        Element subMenuItemDiv = domContext.createElement(HTML.DIV_ELEM);
        submenuDiv.appendChild(subMenuItemDiv);
        renderSeparatorDiv(domContext, subMenuItemDiv, nextSubMenuItem);
    }
    
    private void renderSubMenuItems(
        FacesContext facesContext, DOMContext domContext,
        MenuItems nextSubMenuItems, MenuBar menuComponent,
        boolean disabled, boolean vertical,
        Element submenuDiv, String subMenuDivId)
    {
        List children = nextSubMenuItems.prepareChildren();
        if(children != null) {
            for(int i = 0; i < children.size(); i++) {
                MenuItemBase mib = (MenuItemBase) children.get(i);
                if(mib instanceof MenuItem) {
                    renderSubMenuItem(
                        facesContext, domContext,
                        (MenuItem) mib, menuComponent,
                        disabled, vertical,
                        submenuDiv, subMenuDivId);
                }
                else if(mib instanceof MenuItemSeparator) {
                    renderSubMenuItemSeparator(
                        domContext, (MenuItemSeparator) mib, submenuDiv);
                }
            }
        }
    }
    
    private void renderSubMenuItem(
        FacesContext facesContext, DOMContext domContext,
        MenuItem nextSubMenuItem, MenuBar menuComponent,
        boolean disabled, boolean vertical,
        Element submenuDiv, String subMenuDivId)
    {
        if (!nextSubMenuItem.isRendered()) {
            return;
        }
        // Set the clientId to null as a side effect, so that a unique
        // clientId will be generated when we are in a UIData component
        nextSubMenuItem.setId(nextSubMenuItem.getId()); // ICE-3064
        String call = null;
        Element subMenuItemDiv = domContext.createElement(HTML.DIV_ELEM);
        submenuDiv.appendChild(subMenuItemDiv);
        String qualifiedName = nextSubMenuItem.getStyleClass();
       // subMenuItemDiv.setAttribute(HTML.NAME_ATTR, "ITEM");
        String subMenuItemClientId = nextSubMenuItem.getClientId(facesContext);
        subMenuItemDiv.setAttribute(HTML.ID_ATTR, subMenuItemClientId);
        if (nextSubMenuItem.isChildrenMenuItem()) {
            call = "Ice.Menu.hideOrphanedMenusNotRelatedTo(this);" +
            expand(subMenuDivId, subMenuItemClientId + SUB, KEYWORD_THIS) +
            "";
            subMenuItemDiv.setAttribute(HTML.CLASS_ATTR,
                CoreUtils.addPortletStyleClassToQualifiedClass(
                    qualifiedName, qualifiedName,
                    PORTLET_CSS_DEFAULT.PORTLET_MENU_CASCADE_ITEM));
            subMenuItemDiv.setAttribute(HTML.ONMOUSEOVER_ATTR, call);
        } else {
            subMenuItemDiv.setAttribute(HTML.CLASS_ATTR,
                CoreUtils.addPortletStyleClassToQualifiedClass(
                    qualifiedName, qualifiedName,
                    PORTLET_CSS_DEFAULT.PORTLET_MENU_ITEM));
            subMenuItemDiv.setAttribute(HTML.ONMOUSEOVER_ATTR,
                "Ice.Menu.hideOrphanedMenusNotRelatedTo(this);");
        }
        if (menuComponent instanceof MenuPopup) {
            if (((MenuPopup)menuComponent).getHideOn() != null) {
                if (((MenuPopup)menuComponent).getHideOn().equals("mouseout")) {
                    subMenuItemDiv.setAttribute(HTML.ONMOUSEOUT_ATTR, "Ice.Menu.removeHoverClasses(this);Ice.Menu.hideOnMouseOut('" + menuComponent.getClientId(facesContext) + "',event);");
                } else {
                    subMenuItemDiv.setAttribute(HTML.ONMOUSEOUT_ATTR, "Ice.Menu.removeHoverClasses(this);");
                }
            } else {
                subMenuItemDiv.setAttribute(HTML.ONMOUSEOUT_ATTR, "Ice.Menu.removeHoverClasses(this);");
            }
        } else {
            if (!menuComponent.isDisplayOnClick()) {
                subMenuItemDiv.setAttribute(HTML.ONMOUSEOUT_ATTR, "Ice.Menu.hideOnMouseOut('" + menuComponent.getClientId(facesContext) + "',event);");
            }        
        }
        String title = nextSubMenuItem.getTitle();
        if(title != null && title.length() > 0)
            subMenuItemDiv.setAttribute(HTML.TITLE_ATTR, title);
        // if parent is disabled apply the disabled attribute value of the parent menuItem to this submenuItem
        if (disabled) {
            nextSubMenuItem.setDisabled(disabled);
        }
        // add a command link if we need one
        renderAnchor(facesContext, domContext,
            nextSubMenuItem, subMenuItemDiv,
            menuComponent, vertical);

        Element anch = (Element)subMenuItemDiv.getChildNodes().item(0);

        if (call != null) {
            anch.setAttribute(HTML.ONFOCUS_ATTR, "if( $('"+ subMenuItemDiv.getAttribute("id") +"_sub').style.display == 'none') { " + call + "}");
        }   
        if (menuComponent.getStyleClass().startsWith("iceMnuPop")) {
            String onclick = anch.getAttribute(HTML.ONCLICK_ATTR);
            onclick = onclick.replaceAll("return false;", "Ice.Menu.hideAll(); return false;");
            anch.setAttribute(HTML.ONCLICK_ATTR, onclick);   
        }        

//      Element anch = (Element)subMenuItemDiv.getChildNodes().item(0);
//      anch.setAttribute(HTML.HREF_ATTR, "javascript:void(0);");
         
    }
    
    /**
     * @param facesContext
     * @param domContext
     * @param nextSubMenuItem
     * @param subMenuItemDiv
     */
    private void renderAnchor(FacesContext facesContext, DOMContext domContext,
                              MenuItem nextSubMenuItem,
                              Element subMenuItemDiv,
                              MenuBar menuComponent, boolean vertical) {

        // check if the nextSubMenuItem isRendered
        if (!nextSubMenuItem.isRendered()) {
            return;
        }

        // check if this is a Top Level Menu or MenuItems
        if ((nextSubMenuItem.getParent() instanceof MenuBar) ||
            ((nextSubMenuItem.getParent() instanceof MenuItems)
             && (nextSubMenuItem.getParent().getParent() instanceof MenuBar))) {
            // handle action/actionListeners if attached to top level menuItems
            if (nextSubMenuItem.hasActionOrActionListener()) {
                HtmlCommandLink link = new HtmlCommandLink();
                if (nextSubMenuItem.isDisabled()) {
                    link.setDisabled(true);
                } else { // only add action and actionlisteners on enabled menuItems
                    MethodBinding action = nextSubMenuItem.getAction();
                    if (action != null) {
                        link.setAction(action);
                    }
                    MethodBinding actionListener = nextSubMenuItem.getActionListener();
                    if (actionListener != null) {
                        link.setActionListener(actionListener);
                    }
                    ActionListener[] actionListeners = nextSubMenuItem.getActionListeners();
                    if (actionListeners != null) {
                        for(int i = 0; i < actionListeners.length; i++) {
                            link.removeActionListener(actionListeners[i]);
                            link.addActionListener(actionListeners[i]);
                        }
                    }
                }
                link.setOnclick(nextSubMenuItem.getOnclick());
                link.setValue(nextSubMenuItem.getValue());
                link.setParent(nextSubMenuItem);
                link.setId(LINK_SUFFIX);
                //link.setStyleClass("");
                Node lastCursorParent = domContext.getCursorParent();
                domContext.setCursorParent(subMenuItemDiv);
                addChildrenToLink(
                    link, nextSubMenuItem, menuComponent, true, !vertical);                    
                ((MenuItem) nextSubMenuItem).addParameter(link);
                try {
                    encodeParentAndChildren(facesContext, link);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                domContext.setCursorParent(lastCursorParent);
            } else {
                // anchor
                Element anchor = makeTopLevelAnchor(
                    facesContext, nextSubMenuItem, menuComponent, vertical);
                subMenuItemDiv.appendChild(anchor);
            }
        } else if (nextSubMenuItem.hasActionOrActionListener()) {
            HtmlCommandLink link = new HtmlCommandLink();
            if (nextSubMenuItem.isDisabled()){
                link.setDisabled(true);
            } else { // only set action and actionListeners on enabled menuItems
                MethodBinding action = nextSubMenuItem.getAction();
                if (action != null) {
                    link.setAction(action);
                }
                MethodBinding actionListener = nextSubMenuItem.getActionListener();
                if (actionListener != null) {
                    link.setActionListener(actionListener);
                }
                ActionListener[] actionListeners = nextSubMenuItem.getActionListeners();
                if (actionListeners != null) {
                    for(int i = 0; i < actionListeners.length; i++) {
                        link.removeActionListener(actionListeners[i]);
                        link.addActionListener(actionListeners[i]);
                    }
                }
            }
            link.setOnclick(nextSubMenuItem.getOnclick());
            link.setValue(nextSubMenuItem.getValue());
            link.setParent(nextSubMenuItem);
            link.setId(LINK_SUFFIX);

            Node lastCursorParent = domContext.getCursorParent();
            domContext.setCursorParent(subMenuItemDiv);
            addChildrenToLink(
                link, nextSubMenuItem, menuComponent, false, !vertical);                    
            ((MenuItem) nextSubMenuItem).addParameter(link);
            try {
                encodeParentAndChildren(facesContext, link);

            } catch (IOException e) {
                e.printStackTrace();
            }
            domContext.setCursorParent(lastCursorParent);

        } else {
            // anchor
            Element anchor = makeAnchor(facesContext, domContext,
                                        nextSubMenuItem, menuComponent);
            subMenuItemDiv.appendChild(anchor);
        }
    }

    /**
     * Used to add icon and label to the
     *  {Top Level, Link, Horizontal} menu items
     * and add icon, label and indicator to the
     *  {Top Level, Link, Vertical} and the {Sub Level, Link} menu items
     */ 
    private void addChildrenToLink(HtmlCommandLink link,
                                   MenuItem nextSubMenuItem,
                                   MenuBar menuComponent,
                                   boolean topLevel,
                                   boolean horizontal) {
        if(!(topLevel && horizontal)) {
            if (nextSubMenuItem.getChildCount() > 0 &&
                nextSubMenuItem.isChildrenMenuItem()) {
                HtmlGraphicImage image = new HtmlGraphicImage();
                image.setId(INDICATOR_SUFFIX);
                image.setUrl(getSubMenuImage(menuComponent));
                image.setStyle("border:none;");
                image.setStyleClass(menuComponent.getSubMenuIndicatorStyleClass());
                link.getChildren().add(image);
            }
        }
        
        if( !menuComponent.getNoIcons().equalsIgnoreCase("true") ) {
            String icon = null;
            if(topLevel) {
                // do not render icon if it is the default blank image
                icon = nextSubMenuItem.getSpecifiedIcon();
            }
            else {
                icon = nextSubMenuItem.getIcon();
            }
            if(icon != null && icon.length() > 0) {
                HtmlGraphicImage image = new HtmlGraphicImage();
                image.setId(ICON_SUFFIX);
                image.setUrl(icon);
                image.setStyle("border:none;");
                image.setStyleClass(nextSubMenuItem.getImageStyleClass());
                String alt = nextSubMenuItem.getAlt();
                if(alt != null && alt.length() > 0)
                    image.setAlt(alt);
                link.getChildren().add(image);
            }
        }

        HtmlOutputText outputText = new HtmlOutputText();
        outputText.setId(OUTPUT_SUFFIX);
        outputText.setValue(link.getValue());
//        if (!nextSubMenuItem.isDisabled()) {
//            outputText.setStyleClass("iceSubMenuRowLabel");
//        } else {
//            outputText.setStyleClass("iceSubMenuRowLabel-dis");
//        }
        outputText.setStyleClass(nextSubMenuItem.getLabelStyleClass());
        link.setValue("");
        link.getChildren().add(outputText);
    }
    
    private void renderSeparatorDiv(DOMContext domContext, Element parent, 
            MenuItemSeparator menuItemSeparator) {
        Element hr = domContext.createElement("hr");
        parent.setAttribute(HTML.CLASS_ATTR, menuItemSeparator.getStyleClass());
        parent.appendChild(hr);
    }

    /**
     * @return SubMenuImage url
     */
    private String getSubMenuImage(MenuBar menuComponent) {
        String customPath = null;
        if ((customPath = menuComponent.getImageDir()) != null) {
            return customPath + SUBMENU_IMAGE;
        }
        return DEFAULT_IMAGEDIR + SUBMENU_IMAGE;
    }

    protected String getTextValue(UIComponent component) {
        if (component instanceof MenuItem) {
            return ((MenuItem) component).getValue().toString();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see com.icesoft.faces.component.menubar.MenuItemRendererBase
     * #encodeChildren(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
     */
    public void encodeChildren(FacesContext context, UIComponent component)
            throws IOException {
    }

    /**
     * This method is used for debugging.
     *
     * @param facesContext
     * @param uiComponent
     * @param requestParameterMap
     * @param hiddenFieldName
     */
    private void examineRequest(FacesContext facesContext,
                                UIComponent uiComponent,
                                Map requestParameterMap, String hiddenFieldName,
                                String hiddenValue) {
        Iterator entries = requestParameterMap.entrySet().iterator();
        System.out.println("decoding " + ((MenuItem) uiComponent).getValue());
        System.out.println("request map");
        while (entries.hasNext()) {
            Map.Entry next = (Map.Entry) entries.next();
            if (!next.getKey().toString().equals("rand")) {
                System.out.println("[" + next.getKey().toString() + "=" +
                                   next.getValue() + "]");
            }
        }
        System.out
                .println("looking for hidden field [" + hiddenFieldName + "]");
        System.out.println(
                "client id = [" + uiComponent.getClientId(facesContext));
        System.out.println(
                "################################################ QUEUEING for hidden field [" +
                hiddenValue + "]");
    }

    /* (non-Javadoc)
     * @see com.icesoft.faces.renderkit.dom_html_basic.DomBasicRenderer
     * #encodeEnd(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
     */
    public void encodeEnd(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
        DOMContext domContext =
                DOMContext.getDOMContext(facesContext, uiComponent);
        super.encodeEnd(facesContext, uiComponent);
    }


}
