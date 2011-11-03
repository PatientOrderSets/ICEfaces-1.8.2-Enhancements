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

import com.icesoft.faces.context.DOMContext;
import com.icesoft.faces.util.CoreUtils;
import com.icesoft.faces.util.Debug;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIMessage;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.Iterator;

public class MessageRenderer extends DomBasicRenderer {

    public void encodeBegin(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {

        validateParameters(facesContext, uiComponent, UIMessage.class);

        DOMContext domContext =
                DOMContext.attachDOMContext(facesContext, uiComponent);
        
        FacesMessage facesMessage =
                getSingleMessage(facesContext, (UIMessage) uiComponent);

        if ("javax.faces.Message".equals(uiComponent.getRendererType())) {
            if (facesMessage == null) {
                domContext.stepOver();
                return;
            }
        }
        if (!domContext.isInitialized()) {
            Element span = domContext.createElement(HTML.SPAN_ELEM);
            domContext.setRootNode(span);
            setRootElementId(facesContext, span, uiComponent);
        }
        Element root = (Element) domContext.getRootNode();

        if (!"javax.faces.Message".equals(uiComponent.getRendererType())) {
            if (facesMessage == null) {
                domContext.stepOver();
                return;
            }
        }


        // Remove the previous message 
        DOMContext.removeChildren(root);

        String[] styleAndStyleClass =
                getStyleAndStyleClass(uiComponent, facesMessage);
        String style = styleAndStyleClass[0];
        String styleClass = styleAndStyleClass[1];

        if (styleClass != null) {
            root.setAttribute("class", styleClass);
        }
        Boolean visible = (Boolean) uiComponent.getAttributes().get("visible");
        boolean isVisible = visible == null || visible.booleanValue();
        if (isVisible) {
            if (style != null && style.length() > 0) {
                root.setAttribute("style", style);
            } else {
                root.removeAttribute("style");
            }
        } else { // ICE-2174
            if (style == null) {
                style = "";
            } else if (style.trim().length() != 0 && !style.trim().endsWith(";")) {
                style += ";";
            }
            style += "display:none;";
            root.setAttribute(HTML.STYLE_ATTR, style);
        }

        // tooltip
        boolean tooltip = getToolTipAttribute(uiComponent);

        String[] summaryAndDetail = getSummaryAndDetail(facesMessage);
        String summary = summaryAndDetail[0];
        String detail = summaryAndDetail[1];

        // showSummary
        boolean showSummary = ((UIMessage) uiComponent).isShowSummary();
        boolean showDetail = ((UIMessage) uiComponent).isShowDetail();

        //dir lang
        String dir = (String)uiComponent.getAttributes().get("dir");
        if(dir != null){
            root.setAttribute(HTML.DIR_ATTR, dir);
        }
        String lang = (String)uiComponent.getAttributes().get("lang");
        if(lang != null){
            root.setAttribute(HTML.LANG_ATTR, lang);
        }
        
        // ICE-2174
        String title = (String) uiComponent.getAttributes().get("title");
        if (title == null && tooltip && showSummary) title = summary;
        if (title == null && tooltip && showDetail) title = detail;
        if (title !=null) root.setAttribute(HTML.TITLE_ATTR, title);

        if (tooltip && showSummary && showDetail) {
            Text textNode = domContext.getDocument().createTextNode(detail);
            root.appendChild(textNode);

        } else {
            if (showSummary) {
                Text textNode =
                        domContext.getDocument().createTextNode(summary);
                root.appendChild(textNode);
            }
            if (showDetail) {
                Text textNode = domContext.getDocument().createTextNode(detail);
                root.appendChild(textNode);
            }
        }

        domContext.stepOver();
    }


    private void writeStream(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
        DOMContext domContext =
                DOMContext.getDOMContext(facesContext, uiComponent);
        Element root = domContext.createRootElement(HTML.SPAN_ELEM);
        Text text = domContext.createTextNode("Message goes here");
        Object style = uiComponent.getAttributes().get("style");
        String sstyle = ( (style == null) ? null : style.toString() );
        if (sstyle != null && sstyle.length() > 0) {
            root.setAttribute(HTML.STYLE_ATTR, sstyle);
        }
        else {
            root.removeAttribute(HTML.STYLE_ATTR);
        }
        root.appendChild(text);
        domContext.stepOver();
    }

    /**
     * @param facesContext
     * @param uiComponent
     * @param uiMessage
     * @param domContext
     * @return
     */
    private FacesMessage getSingleMessage(FacesContext facesContext,
                                          UIMessage uiMessage) {
        String forComponentId = uiMessage.getFor();
        Debug.assertTrue(forComponentId != null,
                         "For component must not be null");
        Iterator messages = null;
        if (forComponentId.length() == 0) {
            // get the global messages
            messages = facesContext.getMessages(null);
        } else {
            UIComponent forComponent =
                    findForComponent(facesContext, uiMessage);
            if (forComponent != null) {
                if (messages == null || !messages.hasNext()) {
                    CoreUtils.recoverFacesMessages(facesContext, forComponent);
                }
                messages = facesContext
                        .getMessages(forComponent.getClientId(facesContext));

            }
        }
        if (messages == null || !messages.hasNext()) {
            // there are no messages to render
            return null;
        }
        FacesMessage firstMessage = (FacesMessage) messages.next();
        return firstMessage;
    }
}

