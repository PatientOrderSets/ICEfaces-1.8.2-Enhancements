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

package com.icesoft.faces.renderkit.dom_html_basic;

import com.icesoft.faces.component.AttributeConstants;
import com.icesoft.faces.context.DOMContext;
import org.w3c.dom.Element;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import java.io.IOException;

public class SecretRenderer extends DomBasicInputRenderer {

    //private final static String[] passThruAttributes = AttributeConstants.getAttributes(AttributeConstants.H_INPUTSECRET);
    //handled onMouseDown
    private final static String[] passThruAttributes = 
               new String[]{ HTML.ACCESSKEY_ATTR,  HTML.ALT_ATTR,  HTML.DIR_ATTR,  HTML.LANG_ATTR,  HTML.MAXLENGTH_ATTR,  HTML.ONBLUR_ATTR,  HTML.ONCHANGE_ATTR,  HTML.ONCLICK_ATTR,  HTML.ONDBLCLICK_ATTR,  HTML.ONFOCUS_ATTR,  HTML.ONKEYDOWN_ATTR,  HTML.ONKEYPRESS_ATTR,  HTML.ONKEYUP_ATTR,  HTML.ONMOUSEMOVE_ATTR,  HTML.ONMOUSEOUT_ATTR,  HTML.ONMOUSEOVER_ATTR,  HTML.ONMOUSEUP_ATTR,  HTML.ONSELECT_ATTR,  HTML.SIZE_ATTR,  HTML.STYLE_ATTR,  HTML.TABINDEX_ATTR,  HTML.TITLE_ATTR }; 
    public void encodeBegin(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
        validateParameters(facesContext, uiComponent, UIInput.class);
    }

    public void encodeChildren(FacesContext facesContext,
                               UIComponent uiComponent)
            throws IOException {
        validateParameters(facesContext, uiComponent, UIInput.class);
    }

    protected void renderEnd(FacesContext facesContext, UIComponent uiComponent,
                             String currentValue) throws IOException {

        validateParameters(facesContext, uiComponent, UIInput.class);

        DOMContext domContext =
                DOMContext.attachDOMContext(facesContext, uiComponent);

        if (!domContext.isInitialized()) {
            Element root = domContext.createElement("input");
            domContext.setRootNode(root);
            setRootElementId(facesContext, root, uiComponent);
            root.setAttribute("type", "password");
            root.setAttribute("name", uiComponent.getClientId(facesContext));
        }

        Element root = (Element) domContext.getRootNode();

        String styleClass =
                (String) uiComponent.getAttributes().get("styleClass");
        if (styleClass != null) {
            root.setAttribute("class", styleClass);
        }
        PassThruAttributeRenderer.renderHtmlAttributes(
                facesContext, uiComponent, passThruAttributes);
        String[] attributes = new String[]{HTML.DISABLED_ATTR, HTML.READONLY_ATTR};
        Object attribute;
        for (int i = 0; i < attributes.length; i++) {
            attribute = uiComponent.getAttributes().get(attributes[i]);
            if (attribute instanceof Boolean && ((Boolean) attribute).booleanValue()) {
                root.setAttribute(attributes[i], attributes[i]);
            }
        }

        String autoComplete = (String)uiComponent.getAttributes().get(HTML.AUTOCOMPLETE_ATTR);
        if(autoComplete != null && "off".equalsIgnoreCase(autoComplete)){
            root.setAttribute(HTML.AUTOCOMPLETE_ATTR, "off");
        }
        // render the current value of the component as the value of the "value"
        // attribute  if and only if the value of the component attribute 
        // "redisplay" is the string "true"
        if (redisplayAttributeIsTrue(uiComponent) && currentValue != null) {
            root.setAttribute("value", currentValue);
        } else {
            root.setAttribute("value", "");
        }

        //fix for ICE-2514
        String mousedownScript = (String)uiComponent.getAttributes().get(HTML.ONMOUSEDOWN_ATTR);
        root.setAttribute(HTML.ONMOUSEDOWN_ATTR, combinedPassThru(mousedownScript, "this.focus();"));        
    }

    /**
     * @param uiComponent
     * @return boolean
     */
    private boolean redisplayAttributeIsTrue(UIComponent uiComponent) {
        Object redisplayAttribute =
                uiComponent.getAttributes().get("redisplay");
        return redisplayAttribute != null
               && redisplayAttribute.toString().toLowerCase().equals("true");
    }

}