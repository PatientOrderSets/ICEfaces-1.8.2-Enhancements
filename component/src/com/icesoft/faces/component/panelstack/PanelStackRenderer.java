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

package com.icesoft.faces.component.panelstack;

import com.icesoft.faces.component.util.CustomComponentUtils;
import com.icesoft.faces.component.ExtendedAttributeConstants;
import com.icesoft.faces.context.DOMContext;
import com.icesoft.faces.renderkit.dom_html_basic.DomBasicRenderer;
import com.icesoft.faces.renderkit.dom_html_basic.HTML;
import com.icesoft.faces.renderkit.dom_html_basic.PassThruAttributeRenderer;
import com.icesoft.faces.application.D2DViewHandler;
import org.w3c.dom.Element;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.io.IOException;

public class PanelStackRenderer extends DomBasicRenderer {
    // Basically, everything is excluded
    private static final String[] PASSTHRU_EXCLUDE =
        new String[] { HTML.STYLE_ATTR };
    private static final String[] PASSTHRU =
        ExtendedAttributeConstants.getAttributes(
            ExtendedAttributeConstants.ICE_PANELSTACK,
            PASSTHRU_EXCLUDE);
    
    public boolean getRendersChildren() {
        return true;
    }

    public void encodeChildren(FacesContext facesContext,
                               UIComponent uiComponent) throws IOException {
        validateParameters(facesContext, uiComponent, PanelStack.class);
        DOMContext domContext =
                DOMContext.attachDOMContext(facesContext, uiComponent);

        if (!domContext.isInitialized()) {
            Element panelStackTable =
                    domContext.createRootElement(HTML.TABLE_ELEM);
            setRootElementId(facesContext, panelStackTable, uiComponent);
            PassThruAttributeRenderer.renderHtmlAttributes(
                facesContext, uiComponent, PASSTHRU);
        }
        Element panelStackTable = (Element) domContext.getRootNode();
        DOMContext.removeChildren(panelStackTable);
        panelStackTable.setAttribute(HTML.CLASS_ATTR,
                                     ((PanelStack) uiComponent).getStyleClass());
        String style = ((PanelStack) uiComponent).getStyle();
        if(style != null && style.length() > 0)
            panelStackTable.setAttribute(HTML.STYLE_ATTR, style);
        else
            panelStackTable.removeAttribute(HTML.STYLE_ATTR);
        Element tr = domContext.createElement(HTML.TR_ELEM);
        panelStackTable.appendChild(tr);
        tr.setAttribute(HTML.CLASS_ATTR,
                        ((PanelStack) uiComponent).getRowClass());
        Element td = domContext.createElement(HTML.TD_ELEM);
        tr.appendChild(td);
        td.setAttribute(HTML.CLASS_ATTR,
                        ((PanelStack) uiComponent).getColumnClass());
        domContext.setCursorParent(td);
        PanelStack panelStack = (PanelStack) uiComponent;
        String selectedPanel = panelStack.getSelectedPanel();
        UIComponent childToRender = null;

        if (selectedPanel == null) {
            // render the first child
            if (panelStack.getChildCount() > 0) {
                childToRender = (UIComponent) panelStack.getChildren().get(0);
            }
        } else {
            // render the selected child
            //childToRender = panelStack.findComponent(selectedPanel);
            childToRender =  D2DViewHandler.findComponent(selectedPanel, panelStack);
            if (childToRender == null) {
                // if not found, render the first child
                if (panelStack.getChildCount() > 0) {
                    childToRender =
                            (UIComponent) panelStack.getChildren().get(0);
                }
            }
        }

        if (childToRender != null) {
            CustomComponentUtils.renderChild(facesContext, childToRender);
        }

        domContext.stepOver();
        facesContext.getExternalContext().getRequestMap().put(PanelStack.LAST_SELECTED_PANEL + uiComponent.getClientId(facesContext), selectedPanel);
    }


}
