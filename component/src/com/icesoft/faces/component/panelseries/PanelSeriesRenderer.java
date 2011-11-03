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

package com.icesoft.faces.component.panelseries;

import com.icesoft.faces.context.DOMContext;
import com.icesoft.faces.renderkit.dom_html_basic.DomBasicRenderer;
import com.icesoft.faces.renderkit.dom_html_basic.HTML;
import org.w3c.dom.Element;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.Iterator;


public class PanelSeriesRenderer extends DomBasicRenderer {
    public boolean getRendersChildren() {
        return true;
    }

    public void encodeBegin(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
        DOMContext domContext =
                DOMContext.attachDOMContext(facesContext, uiComponent);

        if (!domContext.isInitialized()) {
            Element rootSpan = domContext.createElement(HTML.DIV_ELEM);
            domContext.setRootNode(rootSpan);
            setRootElementId(facesContext, rootSpan, uiComponent);
        }
        Element root = (Element) domContext.getRootNode();
        String style = ((PanelSeries) uiComponent).getStyle();
        if(style != null && style.length() > 0)
            root.setAttribute(HTML.STYLE_ATTR, style);
        else
            root.removeAttribute(HTML.STYLE_ATTR);
        root.setAttribute(HTML.CLASS_ATTR,
                          ((PanelSeries) uiComponent).getStyleClass());
        domContext.stepInto(uiComponent);
    }

    public void encodeChildren(FacesContext facesContext,
                               UIComponent uiComponent) throws IOException {
        validateParameters(facesContext, uiComponent, null);
        DOMContext domContext =
                DOMContext.getDOMContext(facesContext, uiComponent);
        Element root = (Element) domContext.getRootNode();
        DOMContext.removeChildren(root);
        PanelSeries list = (PanelSeries) uiComponent;
        UISeries uiList = (UISeries) uiComponent;
        int rowIndex = uiList.getFirst();
        int numberOfRowsToDisplay = uiList.getRows();
        int countOfRowsDisplayed = 0;
        while (  ( numberOfRowsToDisplay == 0 ) ||
                 ( (numberOfRowsToDisplay > 0) &&
                   (countOfRowsDisplayed < numberOfRowsToDisplay) )  )
        {
             uiList.setRowIndex(rowIndex);
             if(!uiList.isRowAvailable()){
                break;
            }
            Iterator childs;
            if (list.getChildCount() > 0) {
                childs = list.getChildren().iterator();
                while (childs.hasNext()) {
                    UIComponent nextChild = (UIComponent) childs.next();
                    if (nextChild.isRendered()) {
                        domContext.setCursorParent(root);
                        encodeParentAndChildren(facesContext, nextChild);
                    }
                }
            }
            rowIndex++;
            countOfRowsDisplayed++;
        }
        uiList.setRowIndex(-1);

    }
}
