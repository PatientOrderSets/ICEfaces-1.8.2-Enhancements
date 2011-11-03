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

package com.icesoft.faces.component.panelconfirmation;

import com.icesoft.faces.renderkit.dom_html_basic.DomBasicRenderer;
import com.icesoft.faces.component.ExtendedAttributeConstants;

import com.icesoft.faces.renderkit.dom_html_basic.HTML;
import com.icesoft.faces.context.DOMContext;
import com.icesoft.faces.application.D2DViewHandler;
import com.icesoft.faces.util.CoreUtils;

import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;

import org.w3c.dom.Element;
import org.w3c.dom.Text;

import java.io.IOException;

import com.icesoft.util.pooling.ClientIdPool;

public class PanelConfirmationRenderer extends DomBasicRenderer {

    // everything is excluded
    private static final String[] PASSTHRU_EXCLUDE = new String[]{ HTML.STYLE_ATTR, HTML.TITLE_ATTR };
    private static final String[] PASSTHRU = ExtendedAttributeConstants.getAttributes(
        ExtendedAttributeConstants.ICE_PANELCONFIRMATION, PASSTHRU_EXCLUDE);
    
    public void encodeBegin(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
    }
    
    public boolean getRendersChildren() {
        return false;
    }
    
    public void encodeChildren(FacesContext facescontext,
                               UIComponent uicomponent) throws IOException {
    }
    
    public void encodeEnd(FacesContext facesContext, UIComponent uiComponent) throws IOException {
    
        validateParameters(facesContext, uiComponent, null);
        
        PanelConfirmation panelConfirmation = (PanelConfirmation) uiComponent;
        
        DOMContext domContext = DOMContext.attachDOMContext(facesContext, uiComponent);
        
        Element rootDiv;
        if (domContext.isInitialized()) {
            rootDiv = (Element) domContext.getRootNode();
        } else {
            rootDiv = domContext.createRootElement(HTML.DIV_ELEM);
        }
        
        String id = panelConfirmation.getClientId(facesContext);
        
        rootDiv.setAttribute(HTML.ID_ATTR, id);
        String style = panelConfirmation.getStyle();
        if (style != null) {
            rootDiv.setAttribute(HTML.STYLE_ATTR, style + "display: none;");
        } else {
            rootDiv.setAttribute(HTML.STYLE_ATTR, "display: none;");
        }
        rootDiv.setAttribute(HTML.CLASS_ATTR, panelConfirmation.getStyleClass());
        
        Element table = domContext.createElement(HTML.TABLE_ELEM);
        table.setAttribute(HTML.CELLPADDING_ATTR, "0");
        table.setAttribute(HTML.CELLSPACING_ATTR, "0");
        table.setAttribute(HTML.WIDTH_ATTR, "100%");
        rootDiv.appendChild(table);
        
        // Header
        Element headerTr = domContext.createElement(HTML.TR_ELEM);
        table.appendChild(headerTr);
        Element headerTd = domContext.createElement(HTML.TD_ELEM);
        headerTd.setAttribute(HTML.ID_ATTR, ClientIdPool.get(id + "-handle"));
        headerTd.setAttribute(HTML.CLASS_ATTR, panelConfirmation.getHeaderClass());
        headerTr.appendChild(headerTd);
        
        String title = panelConfirmation.getTitle();
        if (title == null) {
            title = "Confirm";
        }
        if (title.equals("")) {
            title = "Confirm";
        }
        Element titleSpan = domContext.createElement(HTML.SPAN_ELEM);
        titleSpan.setAttribute(HTML.ID_ATTR, ClientIdPool.get(id + "-title"));
        headerTd.appendChild(titleSpan);
        Text titleText = domContext.createTextNode(title);
        titleSpan.appendChild(titleText);
        
        // Body
        Element bodyTr = domContext.createElement(HTML.TR_ELEM);
        table.appendChild(bodyTr);
        Element bodyTd = domContext.createElement(HTML.TD_ELEM);
        bodyTd.setAttribute(HTML.ID_ATTR, ClientIdPool.get(id + "-message"));
        bodyTd.setAttribute(HTML.CLASS_ATTR, panelConfirmation.getBodyClass());
        bodyTr.appendChild(bodyTd);
        
        String message = panelConfirmation.getMessage();
        if (message == null) {
            message = "";
        }
        Element messageSpan = domContext.createElement(HTML.SPAN_ELEM);
        bodyTd.appendChild(messageSpan);
        Text messageText = domContext.createTextNode(message);
        messageSpan.appendChild(messageText);
        
        // Buttons
        Element buttonsTr = domContext.createElement(HTML.TR_ELEM);
        table.appendChild(buttonsTr);
        Element buttonsTd = domContext.createElement(HTML.TD_ELEM);
        buttonsTd.setAttribute(HTML.CLASS_ATTR, panelConfirmation.getButtonsClass());
        buttonsTr.appendChild(buttonsTd);
        
        String type = panelConfirmation.getType();
        if (type != null) {
            if (type.equalsIgnoreCase("acceptOnly")) {
                renderAcceptButton(panelConfirmation, domContext, id, buttonsTd);
            } else if (type.equalsIgnoreCase("cancelOnly")) {
                renderCancelButton(panelConfirmation, domContext, id, buttonsTd);
            } else {
                renderAcceptButton(panelConfirmation, domContext, id, buttonsTd);
                renderCancelButton(panelConfirmation, domContext, id, buttonsTd);
            }
        } else {
            renderAcceptButton(panelConfirmation, domContext, id, buttonsTd);
            renderCancelButton(panelConfirmation, domContext, id, buttonsTd);
        }
        
        if (panelConfirmation.isDraggable()) {
            Element clientOnly = domContext.createElement(HTML.INPUT_ELEM);
	        clientOnly.setAttribute(HTML.TYPE_ATTR, "hidden");
	        clientOnly.setAttribute(HTML.ID_ATTR, id + "clientOnly");
	        rootDiv.appendChild(clientOnly);
        }
        
        domContext.stepOver();
    }
    
    public void decode(FacesContext facesContext, UIComponent uiComponent) {
        
        
    }
    
    public void renderAcceptButton(PanelConfirmation panelConfirmation, DOMContext domContext, String id, Element td) {
    
        String acceptLabel = panelConfirmation.getAcceptLabel();
        if (acceptLabel == null) {
            acceptLabel = "Accept";
        }
        if (acceptLabel.equals("")) {
            acceptLabel = "Accept";
        }
        Element acceptButton = domContext.createElement(HTML.INPUT_ELEM);
        acceptButton.setAttribute(HTML.VALUE_ATTR, acceptLabel);
        acceptButton.setAttribute(HTML.TYPE_ATTR, HTML.INPUT_TYPE_SUBMIT);
        acceptButton.setAttribute(HTML.ID_ATTR, ClientIdPool.get(id + "-accept"));
        acceptButton.setAttribute(HTML.ONCLICK_ATTR, "Ice.PanelConfirmation.current.accept();return false;");
        td.appendChild(acceptButton);
    }
    
    public void renderCancelButton(PanelConfirmation panelConfirmation, DOMContext domContext, String id, Element td) {
    
        String cancelLabel = panelConfirmation.getCancelLabel();
        if (cancelLabel == null) {
            cancelLabel = "Cancel";
        }
        if (cancelLabel.equals("")) {
            cancelLabel = "Cancel";
        }
        Element cancelButton = domContext.createElement(HTML.INPUT_ELEM);
        cancelButton.setAttribute(HTML.VALUE_ATTR, cancelLabel);
        cancelButton.setAttribute(HTML.TYPE_ATTR, HTML.INPUT_TYPE_SUBMIT);
        cancelButton.setAttribute(HTML.ID_ATTR, ClientIdPool.get(id + "-cancel"));
        cancelButton.setAttribute(HTML.ONCLICK_ATTR, "Ice.PanelConfirmation.current.cancel();return false;");
        td.appendChild(cancelButton);
    }
    
    public static String renderOnClickString(UIComponent uiComponent, String originalOnClick) {
    
        String panelConfirmationId = String.valueOf(uiComponent.getAttributes().get("panelConfirmation"));
        PanelConfirmation panelConfirmation = (PanelConfirmation) D2DViewHandler.findComponent(panelConfirmationId, uiComponent);
        if (panelConfirmation != null) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            panelConfirmationId = panelConfirmation.getClientId(facesContext);
            String autoCentre = panelConfirmation.isAutoCentre() ? "true" : "false";
            String draggable = panelConfirmation.isDraggable() ? "true" : "false";
            String displayAtMouse = panelConfirmation.isDisplayAtMouse() ? "true" : "false";
            return "new Ice.PanelConfirmation(this,event,'" + panelConfirmationId + "',"
                + autoCentre + "," + draggable + "," + displayAtMouse + ","
                + "'" + CoreUtils.resolveResourceURL(facesContext,"/xmlhttp/blank") + "',"
                + "function(event){" + originalOnClick + "});return false;";
        } else {
            return originalOnClick;
        }
    }
}
