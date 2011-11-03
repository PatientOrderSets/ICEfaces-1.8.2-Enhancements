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
import com.icesoft.faces.component.PORTLET_CSS_DEFAULT;
import com.icesoft.faces.component.ExtendedAttributeConstants;
import com.icesoft.faces.context.DOMContext;
import com.icesoft.faces.context.effects.JavascriptContext;
import com.icesoft.faces.renderkit.dom_html_basic.DomBasicRenderer;
import com.icesoft.faces.renderkit.dom_html_basic.HTML;
import com.icesoft.faces.renderkit.dom_html_basic.PassThruAttributeRenderer;
import com.icesoft.faces.util.CoreUtils;
import com.icesoft.faces.component.menupopup.MenuPopup;

import org.w3c.dom.Element;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.io.IOException;

public class MenuBarRenderer extends DomBasicRenderer {
    private static final String[] passThruAttributes =
            ExtendedAttributeConstants.getAttributes(ExtendedAttributeConstants.ICE_MENUBAR);

    public static final String PATH_DELIMITER = "-";

    public boolean getRendersChildren() {
        return true;
    }

    public void encodeBegin(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
        validateParameters(facesContext, uiComponent, MenuBar.class);

        DOMContext domContext =
                DOMContext.attachDOMContext(facesContext, uiComponent);
        if (!domContext.isInitialized()) {
            domContext.createRootElement(HTML.DIV_ELEM);
        }
        Element menuDiv = (Element) domContext.getRootNode();
        menuDiv.setAttribute(HTML.ID_ATTR,
                             uiComponent.getClientId(facesContext));
        MenuBar menuComponent = (MenuBar) uiComponent;

        if (!(uiComponent instanceof MenuPopup) && (!((MenuBar)uiComponent).isDisplayOnClick())) {
            menuDiv.setAttribute(HTML.ONMOUSEOUT_ATTR, "Ice.Menu.hideOnMouseOut('" + uiComponent.getClientId(facesContext) + "',event);");
        }

        String defaultStyle = menuComponent.getComponentRootStyle();
        if (MenuBar.ORIENTATION_VERTICAL.equalsIgnoreCase(
                menuComponent.getOrientation())){
            defaultStyle+=CSS_DEFAULT.MENU_BAR_VERTICAL_SUFFIX_STYLE;
        }
        
        String styleClass = menuComponent.getStyleClass();
        menuDiv.setAttribute(HTML.CLASS_ATTR, CoreUtils.
        		addPortletStyleClassToQualifiedClass(styleClass, defaultStyle, PORTLET_CSS_DEFAULT.PORTLET_MENU));
        String style = menuComponent.getStyle();
        if(style != null && style.length() > 0)
            menuDiv.setAttribute(HTML.STYLE_ATTR, style);
        else
            menuDiv.removeAttribute(HTML.STYLE_ATTR);
        DOMContext.removeChildren(menuDiv);

        PassThruAttributeRenderer.renderHtmlAttributes(facesContext, uiComponent, passThruAttributes);

        domContext.stepInto(uiComponent);
        
        trailingEncodeBegin(facesContext, uiComponent);
    }

    /**
     * For subclasses to add in any required rendering, without having to
     * override the whole encodeBegin(-)
     * @param facesContext
     * @param uiComponent
     */
    protected void trailingEncodeBegin(FacesContext facesContext, UIComponent uiComponent) {
    }

    public void encodeChildren(FacesContext context, UIComponent component)
            throws IOException {
        for (int i = 0; i < component.getChildCount(); i++) {
            encodeParentAndChildren(context, (UIComponent) component
                    .getChildren().get(i));
        }
    }

    public void encodeEnd(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
        DOMContext domContext =
                DOMContext.getDOMContext(facesContext, uiComponent);
        super.encodeEnd(facesContext, uiComponent);
    }

}
