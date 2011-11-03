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
package com.icesoft.faces.component.outputmedia;

import com.icesoft.faces.context.DOMContext;
import com.icesoft.faces.renderkit.dom_html_basic.DomBasicRenderer;
import com.icesoft.faces.renderkit.dom_html_basic.HTML;
import com.icesoft.faces.renderkit.dom_html_basic.PassThruAttributeRenderer;
import com.icesoft.faces.util.CoreUtils;
import com.icesoft.faces.component.ExtendedAttributeConstants;
import org.w3c.dom.Element;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class OutputMediaRenderer extends DomBasicRenderer {
    private static final String[] passThruAttributes =
            ExtendedAttributeConstants.getAttributes(ExtendedAttributeConstants.ICE_OUTPUTMEDIA);
    private static Map players = new HashMap();

    {
        Properties props = new Properties();
        props.setProperty("classid", "clsid:22D6f312-B0F6-11D0-94AB-0080C74C7E95");
        props.setProperty("codebase", "http://activex.microsoft.com/activex/controls/mplayer/en/nsmp2inf.cab#Version=6,4,7,1112");
        props.setProperty("pluginspage", "http://www.microsoft.com/windows/windowsmedia/download/AllDownloads.aspx");
        props.setProperty("sourceParamName", "filename");
        players.put("windows", props);

        props = new Properties();
        props.setProperty("classid", "clsid:D27CDB6E-AE6D-11cf-96B8-444553540000");
        props.setProperty("codebase", "http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,40,0");
        props.setProperty("pluginspage", "http://www.macromedia.com/go/getflashplayer");
        props.setProperty("sourceParamName", "movie");
        players.put("flash", props);

        props = new Properties();
        props.setProperty("classid", "clsid:02BF25D5-8C17-4B23-BC80-D3488ABDDC6B");
        props.setProperty("codebase", "http://www.apple.com/qtactivex/qtplugin.cab");
        props.setProperty("pluginspage", "http://www.apple.com/quicktime/download");
        props.setProperty("sourceParamName", "src");
        players.put("quicktime", props);

        props = new Properties();
        props.setProperty("classid", "clsid:CFCDAA03-8BE4-11cf-B84B-0020AFBBCCFA");
        props.setProperty("codebase", "http://www.real.com");
        props.setProperty("pluginspage", "http://www.real.com");
        props.setProperty("sourceParamName", "src");
        players.put("real", props);
    }

    public void encodeEnd(FacesContext facesContext, UIComponent uiComponent) throws IOException {
        validateParameters(facesContext, uiComponent, null);
        OutputMedia mediaComponent = (OutputMedia) uiComponent;

        DOMContext domContext = DOMContext.attachDOMContext(facesContext, uiComponent);
        Element object;
        if (domContext.isInitialized()) {
            DOMContext.removeChildren(domContext.getRootNode());
            object = (Element) domContext.getRootNode();
        } else {
            object = domContext.createRootElement("object");
        }

        String idAndName = mediaComponent.getClientId(facesContext);
        String sourceURL = mediaComponent.getSource();
        if (sourceURL != null) {
            sourceURL = CoreUtils.resolveResourceURL(facesContext, sourceURL);
        }

        String classid = null;
        String codebase = mediaComponent.getCodebase();
        String pluginspage = null;
        String sourceParamName = null;
        Properties playerProps = (Properties) players.get(mediaComponent.getPlayer());
        if (playerProps != null) {
            classid = playerProps.getProperty("classid");
            if (codebase == null) {
                codebase = playerProps.getProperty("codebase");
            }
            pluginspage = playerProps.getProperty("pluginspage");
            sourceParamName = playerProps.getProperty("sourceParamName");
        }

        setElementAttr(object, "classid", classid);
        setElementAttr(object, "codebase", codebase);
        setElementAttr(object, "standby", mediaComponent, "standbyText");
        setElementAttr(object, HTML.ID_ATTR, idAndName);
        setElementAttr(object, HTML.NAME_ATTR, idAndName);
        PassThruAttributeRenderer.renderHtmlAttributes(facesContext, uiComponent, passThruAttributes);
        setElementAttr(object, HTML.CLASS_ATTR, mediaComponent, HTML.STYLE_CLASS_ATTR);
        setElementAttr(object, HTML.TYPE_ATTR, mediaComponent, "mimeType");

        Element embed = domContext.createElement("embed");
        setElementAttr(embed, "pluginspage", pluginspage);
        setElementAttr(embed, HTML.SRC_ATTR, sourceURL);
        setElementAttr(embed, HTML.ID_ATTR, idAndName);
        setElementAttr(embed, HTML.NAME_ATTR, idAndName);
        PassThruAttributeRenderer.renderHtmlAttributes(facesContext, uiComponent, embed, embed, passThruAttributes);
        setElementAttr(embed, HTML.CLASS_ATTR, mediaComponent, HTML.STYLE_CLASS_ATTR);
        setElementAttr(embed, HTML.TYPE_ATTR, mediaComponent, "mimeType");

        appendParamElement(sourceParamName, sourceURL, object, domContext);
        if (mediaComponent.getChildCount() > 0 ){
            List children = mediaComponent.getChildren();
            Object component;
            UIParameter parameter;
            String paramName;
            for (int i = 0; i < children.size(); i++) {
                component = children.get(i);
                if (!(component instanceof UIParameter)) {
                    continue;
                }
                parameter = (UIParameter) component;
                paramName = parameter.getName();
                appendParamElement(paramName, parameter.getValue().toString(), object, domContext);
                if (paramName == null || embed.hasAttribute(paramName)) {
                    continue;
                }
                setElementAttr(embed, paramName, parameter, HTML.VALUE_ATTR);
            }
        }
        object.appendChild(embed);

        domContext.stepOver();
    }

    private void setElementAttr(Element element, String elementAttrName, UIComponent component, String componentAttrName) {
        Object attrValue = component.getAttributes().get(componentAttrName);
        if (attrValue != null) {
            element.setAttribute(elementAttrName, attrValue.toString());
        }
    }

    private void setElementAttr(Element element, String elementAttrName, String attrValue) {
        if (attrValue != null) {
            element.setAttribute(elementAttrName, attrValue);
        }
    }

    private void appendParamElement(String paramName, String paramValue, Element element, DOMContext domContext) {
        if (paramName == null || paramValue == null) return;
        Element param = domContext.createElement("param");
        param.setAttribute(HTML.NAME_ATTR, paramName);
        param.setAttribute(HTML.VALUE_ATTR, paramValue);
        element.appendChild(param);
    }
}
