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
import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class OutputLinkRenderer extends DomBasicRenderer {
    
    //private static final String[] passThruAttributes = AttributeConstants.getAttributes(AttributeConstants.H_OUTPUTLINK);
    //handle style
    private static final String[] passThruAttributes = new String[]{ HTML.ACCESSKEY_ATTR,  HTML.CHARSET_ATTR,  HTML.COORDS_ATTR,  HTML.DIR_ATTR,  HTML.HREFLANG_ATTR,  HTML.LANG_ATTR,  HTML.ONBLUR_ATTR,  HTML.ONCLICK_ATTR,  HTML.ONDBLCLICK_ATTR,  HTML.ONFOCUS_ATTR,  HTML.ONKEYDOWN_ATTR,  HTML.ONKEYPRESS_ATTR,  HTML.ONKEYUP_ATTR,  HTML.ONMOUSEDOWN_ATTR,  HTML.ONMOUSEMOVE_ATTR,  HTML.ONMOUSEOUT_ATTR,  HTML.ONMOUSEOVER_ATTR,  HTML.ONMOUSEUP_ATTR,  HTML.REL_ATTR,  HTML.REV_ATTR,  HTML.SHAPE_ATTR,  HTML.STYLE_ATTR,  HTML.TABINDEX_ATTR,  HTML.TARGET_ATTR,  HTML.TITLE_ATTR,  HTML.TYPE_ATTR };                        
           
    /**
     * @return false as this component does not specially
     *         handle rendering its children.
     */
    public boolean getRendersChildren() {
        return false;
    }

    /**
     * Returns the value of this component as an Object.
     *
     * @param uiComponent the component to retrieve the value from.
     * @return value as an Object.
     */
    protected Object getValue(UIComponent uiComponent) {
        return ((UIOutput) uiComponent).getValue();
    }

    String clientId;

    /**
     * @param facesContext
     * @param uiComponent
     * @throws IOException
     */
    public void encodeBegin(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
        if (facesContext == null || uiComponent == null) {
            throw new NullPointerException(
                    "facesContext or uiComponent is null");
        }

        UIOutput output = (UIOutput) uiComponent;
        String linkVal = getValue(facesContext, uiComponent);


        if (!output.isRendered()) return;

        ResponseWriter writer = facesContext.getResponseWriter();

        if (writer == null) {
            throw new NullPointerException("ResponseWriter is null");
        }

        if (!checkDisabled(uiComponent)) {
            writer.startElement("a", uiComponent);
        } else {
            writer.startElement("span", uiComponent);
        }
        writer.writeAttribute("id", uiComponent.getClientId(facesContext),
                "id");

        if (null == linkVal || 0 == linkVal.length()) {
            linkVal = "";
        }

        clientId = output.getClientId(facesContext);

        linkVal = appendParameters(facesContext, uiComponent, linkVal);

        if (!checkDisabled(uiComponent)) {
            writer.writeURIAttribute("href",
                    facesContext
                            .getExternalContext().encodeResourceURL(
                            linkVal), "href");
        }

        // ICE-2169
        PassThruAttributeWriter.renderHtmlAttributes(writer, uiComponent, passThruAttributes);
        Boolean visibleAttribute = (Boolean) uiComponent.getAttributes().get("visible");
        boolean isVisible = visibleAttribute == null ? true : visibleAttribute.booleanValue();
        String style = (String) uiComponent.getAttributes().get(HTML.STYLE_ATTR);
        if (!isVisible) {
            if (style == null) {
                style = "";
            } else if (style.trim().length() == 0 || style.trim().endsWith(";")) {
                // nothing
            } else {
                style += ";";
            }
            style += "display:none;";
        }
        if (style != null) writer.writeAttribute(HTML.STYLE_ATTR, style, HTML.STYLE_ATTR);

        String styleClass = (String) output.getAttributes().get("styleClass");
        if (styleClass != null)
            writer.writeAttribute("class", styleClass, "styleClass");

        writer.flush();
    }

    /**
     * @param uiComponent
     * @return boolean
     */
    protected boolean checkDisabled(UIComponent uiComponent) {
        boolean disabled = false;
        try {
            return disabled = Boolean.valueOf(String.valueOf(
                    uiComponent.getAttributes().get("disabled"))).booleanValue();
        } catch (Exception e) {
        }
        return disabled;
    }

    /**
     * @param facesContext
     * @param uiComponent
     * @param hrefAttribute
     * @return String
     */
    private String appendParameters(FacesContext facesContext, UIComponent
            uiComponent, String hrefAttribute) {
        StringBuffer hrefBuffer = new StringBuffer(hrefAttribute);
        Map parameters = getParameterMap(uiComponent);
        int numberOfParameters = parameters.size();
        boolean parametersExist = numberOfParameters > 0;
        if (parametersExist) {
            hrefBuffer.append("?");
            Iterator parameterKeys = parameters.keySet().iterator();
            while (parameterKeys.hasNext()) {
                String nextKey = (String) parameterKeys.next();
                Object nextValue = parameters.get(nextKey);
                hrefBuffer.append(nextKey);
                hrefBuffer.append("=");
                hrefBuffer.append(
                        nextValue == null ? null : nextValue.toString());
                if (parameterKeys.hasNext()) {
                    hrefBuffer.append("&");
                }
            }
        }
        return hrefBuffer.toString();
    }

    /**
     * @param facesContext
     * @param uiComponent
     * @throws IOException
     */
    public void encodeEnd(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {

        if (facesContext == null || uiComponent == null) {
            throw new NullPointerException(
                    "facesContext or uiComponent is null");
        }

        UIOutput output = (UIOutput) uiComponent;

        if (!output.isRendered()) return;

        ResponseWriter writer = facesContext.getResponseWriter();

        if (writer == null) {
            throw new NullPointerException("ResponseWriter is null");
        }

        writer.endElement("a");

    }


}