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
import com.icesoft.faces.context.effects.JavascriptContext;
import com.icesoft.faces.context.effects.LocalEffectEncoder;
import com.icesoft.faces.context.effects.CurrentStyle;
import org.w3c.dom.Element;

import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class CheckboxRenderer extends DomBasicInputRenderer {
    private static final String[] booleanCheckboxPassThruAttributes = AttributeConstants.getAttributes(AttributeConstants.H_SELECTBOOLEANCHECKBOX);
    public boolean getRendersChildren() {
        return true;
    }

    public void decode(FacesContext facesContext, UIComponent uiComponent) {
        validateParameters(facesContext, uiComponent, null);
        if (isStatic(uiComponent)) {
            return;
        }

        Map requestParameterMap =
                facesContext.getExternalContext().getRequestParameterMap();
        String componentClientId = uiComponent.getClientId(facesContext);
        String decodedValue =
                (String) requestParameterMap.get(componentClientId);
        if (decodedValue == null) {
            decodedValue = "false";
        } else if (decodedValue.equalsIgnoreCase("on") ||
                   decodedValue.equalsIgnoreCase("yes") ||
                   decodedValue.equalsIgnoreCase("true")) {
            decodedValue = "true";
        }
        ((EditableValueHolder) uiComponent).setSubmittedValue(decodedValue);
    }

    public void encodeBegin(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
        validateParameters(facesContext, uiComponent, null);
        DOMContext domContext =
                DOMContext.attachDOMContext(facesContext, uiComponent);
        String clientId = uiComponent.getClientId(facesContext);

        Element input = null;
        if (!domContext.isInitialized()) {
            if (uiComponent.getChildCount() > 0) {
                Element root = domContext.createRootElement(HTML.SPAN_ELEM);
                root.setAttribute(HTML.ID_ATTR, clientId + "span");
                root.setAttribute(HTML.STYLE_ATTR, "float:left");
                input = domContext.createElement(HTML.INPUT_ELEM);
                root.appendChild(input);
            } else {
                input = (Element) domContext.createRootElement(HTML.INPUT_ELEM);
            }
            input.setAttribute("type", "checkbox");
            input.setAttribute("id", clientId);
            input.setAttribute("name", clientId);
        }

        if (uiComponent.getChildCount() > 0)
            input = (Element) domContext.getRootNode().getFirstChild();
        else
            input = (Element) domContext.getRootNode();

        String currentValue = getValue(facesContext, uiComponent);
        if (currentValue != null && currentValue.equalsIgnoreCase("true")) {
            input.setAttribute("checked", "checked");
        } else {
            input.removeAttribute("checked");
        }

        String styleClass =
                (String) uiComponent.getAttributes().get("styleClass");
        if (styleClass != null) {
            input.setAttribute("class", styleClass);
        }
        JavascriptContext.fireEffect(uiComponent, facesContext);
        LocalEffectEncoder.encodeLocalEffects(uiComponent, input, facesContext);
        renderPassThruAttributes(facesContext, uiComponent, input);
        CurrentStyle.apply(facesContext, uiComponent, input, null);
        HashSet excludes = new HashSet();
        addJavaScript(facesContext, uiComponent, input, excludes);
    }


    public void renderPassThruAttributes(FacesContext facesContext, 
                                         UIComponent uiComponent,
                                         Element input) {
        PassThruAttributeRenderer.renderHtmlAttributes(facesContext, uiComponent, booleanCheckboxPassThruAttributes);
        //only "disabled" boolean attribute applies on a checkbox 
        PassThruAttributeRenderer.renderBooleanAttributes(
                facesContext,
                uiComponent,
                input,
                PassThruAttributeRenderer.EMPTY_STRING_ARRAY) ;
        // onfocus
        String original = (String) uiComponent.getAttributes().get("onfocus");
        String onfocus = "setFocus(this.id);";
        if (original == null) original = "";
        input.setAttribute(HTML.ONFOCUS_ATTR, onfocus + original);
        // onblur
        original = (String) uiComponent.getAttributes().get("onblur");
        String onblur = "setFocus('');";
        if (original == null) original = "";
        input.setAttribute(HTML.ONBLUR_ATTR, onblur + original);

        input.setAttribute("onkeypress", combinedPassThru((String) uiComponent.getAttributes().get("onkeypress"),
                "Ice.util.radioCheckboxEnter(form,this,event);"));
    }


    public void encodeChildren(FacesContext facesContext,
                               UIComponent uiComponent)
            throws IOException {
        validateParameters(facesContext, uiComponent, null);
        DOMContext domContext =
                DOMContext.getDOMContext(facesContext, uiComponent);
        if (uiComponent.getChildCount() > 0) {
            Iterator children = uiComponent.getChildren().iterator();
            domContext.setCursorParent(domContext.getRootNode());
            while (children.hasNext()) {
                UIComponent nextChild = (UIComponent) children.next();
                if (nextChild.isRendered()) {
                    encodeParentAndChildren(facesContext, nextChild);
                }
            }
        }
        domContext.stepOver();
    }

    public void encodeEnd(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
    }

    public Object getConvertedValue(FacesContext facesContext, UIComponent
            uiComponent, Object submittedValue) throws ConverterException {
        if (!(submittedValue instanceof String)) {
            throw new ConverterException(
                    "Expecting submittedValue to be String");
        }
        return Boolean.valueOf((String) submittedValue);
    }

    protected void addJavaScript(FacesContext facesContext,
                                 UIComponent uiComponent, Element root,
                                 Set excludes) {
    }
}
