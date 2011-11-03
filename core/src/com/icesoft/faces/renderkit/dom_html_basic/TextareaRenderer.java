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
import com.icesoft.faces.util.DOMUtils;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;

public class TextareaRenderer extends BaseInputRenderer {
    protected static final String ONMOUSEDOWN_FOCUS = "this.focus();";
    
    private static final String[] passThruExcludes = new String[] {
        HTML.ROWS_ATTR, HTML.COLS_ATTR, HTML.ONMOUSEDOWN_ATTR };
    private static final String[] passThruAttributes =
        AttributeConstants.getAttributes(
            AttributeConstants.H_INPUTTEXTAREA, passThruExcludes);

    public void encodeBegin(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
        super.encodeBegin(facesContext, uiComponent);
        DomBasicRenderer.validateParameters(facesContext, uiComponent, UIInput.class);
        
        ResponseWriter writer = facesContext.getResponseWriter();
        String clientId = uiComponent.getClientId(facesContext);
        writer.startElement(HTML.TEXTAREA_ELEM, uiComponent);
        writer.writeAttribute(HTML.ID_ATTR, clientId, null);
        writer.writeAttribute(HTML.NAME_ATTR, clientId, null);
        
        renderHtmlAttributes(facesContext, writer, uiComponent);
        PassThruAttributeWriter.renderBooleanAttributes(
                writer, 
                uiComponent, 
                PassThruAttributeWriter.EMPTY_STRING_ARRAY);
        
        Object styleClass = uiComponent.getAttributes().get("styleClass");
        if (styleClass != null) {
            writer.writeAttribute(HTML.CLASS_ATTR, styleClass, null);
        }
        
        renderNumericAttributeOrDefault(writer, uiComponent, HTML.ROWS_ATTR, "2");
        renderNumericAttributeOrDefault(writer, uiComponent, HTML.COLS_ATTR, "20");
    }

    public void encodeChildren(FacesContext facesContext, UIComponent uiComponent) 
            throws IOException {
        super.encodeChildren(facesContext, uiComponent);
        DomBasicRenderer.validateParameters(facesContext, uiComponent, UIInput.class);
    }
    
    public void encodeEnd(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
        //it must call the super.encode to support effects and facesMessage recovery
        super.encodeEnd(facesContext, uiComponent);
        ResponseWriter writer = facesContext.getResponseWriter();
        
        String currentValue = getValue(facesContext, uiComponent);
        if (currentValue != null && currentValue.length() > 0) {
            writer.write(DOMUtils.escapeAnsi(currentValue));
        }
        
        writer.endElement(HTML.TEXTAREA_ELEM);
    }
       
    protected void renderNumericAttributeOrDefault(
        ResponseWriter writer, UIComponent uiComponent,
        String attribName, String defaultValue)
            throws IOException {
        Object val = uiComponent.getAttributes().get(attribName);
        if (val == null || ((Integer)val).intValue() <= -1) {
            val = defaultValue;
        }
        writer.writeAttribute(attribName, val, attribName);
    }
    
    protected void renderHtmlAttributes(
        FacesContext facesContext, ResponseWriter writer, UIComponent uiComponent)
            throws IOException {
        PassThruAttributeWriter.renderHtmlAttributes(
            writer, uiComponent, passThruAttributes);
        
        //fix for ICE-2514
        String app = (String)uiComponent.getAttributes().get(HTML.ONMOUSEDOWN_ATTR);
        String rend = ONMOUSEDOWN_FOCUS;
        String combined = DomBasicRenderer.combinedPassThru(app, rend);
        writer.writeAttribute(HTML.ONMOUSEDOWN_ATTR, combined, HTML.ONMOUSEDOWN_ATTR);
    }
}
