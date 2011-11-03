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

package com.icesoft.faces.component.menupopup;

import com.icesoft.faces.application.D2DViewHandler;
import com.icesoft.faces.component.CSS_DEFAULT;
import com.icesoft.faces.component.DisplayEvent;
import com.icesoft.faces.component.PORTLET_CSS_DEFAULT;
import com.icesoft.faces.component.menubar.MenuBarRenderer;
import com.icesoft.faces.context.DOMContext;
import com.icesoft.faces.context.effects.JavascriptContext;
import com.icesoft.faces.renderkit.dom_html_basic.HTML;
import com.icesoft.faces.util.CoreUtils;

import org.w3c.dom.Element;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.util.Map;

import com.icesoft.util.pooling.ClientIdPool;


public class MenuPopupRenderer extends MenuBarRenderer {
    
    protected void trailingEncodeBegin(FacesContext facesContext, UIComponent uiComponent) {
        DOMContext domContext =
                DOMContext.getDOMContext(facesContext, uiComponent);
        Element menuDiv = (Element) domContext.getRootNode();
        if(menuDiv != null) {
        
            MenuPopup menuPopup = (MenuPopup) uiComponent;
            if (menuPopup.getHideOn() != null) {
                if (menuPopup.getHideOn().equals("mouseout")) {
                    menuDiv.setAttribute(HTML.ONMOUSEOUT_ATTR, "Ice.Menu.hideOnMouseOut('" + uiComponent.getClientId(facesContext) + "',event);");
                }
            }
            //String style = (String) uiComponent.getAttributes().get("style");
            //style = CurrentStyle.modifyStyleWithVisibility(style, false);
            //CurrentStyle.setStyleOnElement(style, menuDiv);
            
            if (menuPopup.getDisplayListener() != null) {
                Element progressListenerFld = domContext.createElement(HTML.INPUT_ELEM);
                progressListenerFld.setAttribute(HTML.ID_ATTR, uiComponent.getClientId(facesContext) + "_sub" + MenuPopup.DISPLAY_LISTENER_ID);
                progressListenerFld.setAttribute(HTML.NAME_ATTR, uiComponent.getClientId(facesContext) + "_sub" + MenuPopup.DISPLAY_LISTENER_ID);            
                progressListenerFld.setAttribute(HTML.TYPE_ATTR, "hidden");
                progressListenerFld.setAttribute(HTML.STYLE_ATTR, "display:none;");
                progressListenerFld.setAttribute(HTML.VALUE_ATTR, "");
                menuDiv.appendChild(progressListenerFld);
            }
            
            // Put the top level menu items into a separate child div
            Element submenuDiv = domContext.createElement(HTML.DIV_ELEM);
            submenuDiv.setAttribute(HTML.NAME_ATTR, "TOP_LEVEL_SUBMENU");
            String subMenuDivId = ClientIdPool.get(uiComponent.getClientId(facesContext) + "_sub");
            submenuDiv.setAttribute(HTML.ID_ATTR, subMenuDivId);
            
            //TODO Figure out what style class to use on the inner div
            MenuPopup menuComponent = (MenuPopup) uiComponent;
            String qualifiedName = menuComponent.getTopSubMenuStyleClass();
            submenuDiv.setAttribute(HTML.CLASS_ATTR, CoreUtils.
                addPortletStyleClassToQualifiedClass(qualifiedName,
                CSS_DEFAULT.MENU_BAR_TOP_SUB_MENU_STYLE , PORTLET_CSS_DEFAULT.PORTLET_MENU) );
            submenuDiv.setAttribute(HTML.STYLE_ATTR, "display:none");
            
            menuDiv.appendChild(submenuDiv);
            domContext.setCursorParent(submenuDiv);
        }
    }
}
