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
import com.icesoft.faces.component.AttributeConstants;
import org.w3c.dom.Element;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.Iterator;

import com.icesoft.util.pooling.ClientIdPool;

public class GridRenderer extends DomBasicRenderer {
    private static final String[] PASSTHRU =
        AttributeConstants.getAttributes(
            AttributeConstants.H_PANELGRID);

    public boolean getRendersChildren() {
        return true;
    }

    public void encodeBegin(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
        validateParameters(facesContext, uiComponent, null);
        DOMContext domContext =
                DOMContext.attachDOMContext(facesContext, uiComponent);
        if (!domContext.isInitialized()) {
            Element root = domContext.createElement("table");
            domContext.setRootNode(root);
            setRootElementId(facesContext, root, uiComponent);
            doPassThru(facesContext, uiComponent);
        }
        Element root = (Element) domContext.getRootNode();
        String styleClass = ((HtmlPanelGrid) uiComponent).getStyleClass();
        if (styleClass != null) {
            root.setAttribute("class", styleClass);
        }
    }
    
    protected void doPassThru(FacesContext facesContext, UIComponent uiComponent) {
        PassThruAttributeRenderer.renderHtmlAttributes(
            facesContext, uiComponent, PASSTHRU);
    }
    
    private void renderHeaderFacet(FacesContext facesContext,
                                   UIComponent uiComponent,
                                   DOMContext domContext) throws IOException {
        Element root = (Element) domContext.getRootNode();
        DOMContext.removeChildrenByTagName(root, "thead");
        UIComponent headerFacet = getFacetByName(uiComponent, "header");
        if (headerFacet != null && headerFacet.isRendered()) {
            Element thead = domContext.createElement("thead");
            Element tr = domContext.createElement("tr");
            Element th = domContext.createElement("th");
            root.appendChild(thead);
            thead.appendChild(tr);
            tr.appendChild(th);
            String headerClassAttribute =
                    ((HtmlPanelGrid) uiComponent).getHeaderClass();
            if (headerClassAttribute != null) {
                th.setAttribute("class", headerClassAttribute);
            }
            th.setAttribute("scope", "colgroup");
            th.setAttribute("colspan",
                            String.valueOf(
                                    getConvertedColumnAttribute(uiComponent)));
            domContext.setCursorParent(th);
            encodeParentAndChildren(facesContext, headerFacet);
        }
    }

    private void renderFooterFacet(FacesContext facesContext,
                                   UIComponent uiComponent,
                                   DOMContext domContext) throws IOException {
        Element root = (Element) domContext.getRootNode();
        DOMContext.removeChildrenByTagName(root, "tfoot");
        UIComponent footerFacet = getFacetByName(uiComponent, "footer");
        if (footerFacet != null && footerFacet.isRendered()) {
            Element tfoot = domContext.createElement("tfoot");
            Element tr = domContext.createElement("tr");
            Element td = domContext.createElement("td");
            root.appendChild(tfoot);
            tfoot.appendChild(tr);
            tr.appendChild(td);
            String footerClassAttribute =
                    ((HtmlPanelGrid) uiComponent).getFooterClass();
            if (footerClassAttribute != null) {
                td.setAttribute("class", footerClassAttribute);
            }
            td.setAttribute("colspan",
                            String.valueOf(
                                    getConvertedColumnAttribute(uiComponent)));
            domContext.setCursorParent(td);
            encodeParentAndChildren(facesContext, footerFacet);
        }
    }

    public void encodeChildren(FacesContext facesContext,
                               UIComponent uiComponent)
            throws IOException {
        validateParameters(facesContext, uiComponent, null);
        DOMContext domContext =
                DOMContext.getDOMContext(facesContext, uiComponent);
        renderHeaderFacet(facesContext, uiComponent, domContext);
        // remove previous children
        Element root = (Element) domContext.getRootNode();
        DOMContext.removeChildrenByTagName(root, "tbody");
        Element tbody = domContext.createElement("tbody");
        root.appendChild(tbody);
        Element tr = null;

        // Render children inside the tbody element.
        // Based on the value of the "columns" attribute, create a new row every 
        // time a columns-worth of children has been rendered. 
        // Children with attribute "rendered" == false are not rendered and do
        // not occupy a table cell.
        if (uiComponent.getChildCount() > 0) {
            Iterator children = uiComponent.getChildren().iterator();
            if (children != null) {
                if (!children.hasNext()) {
                    tr = (Element) domContext.createElement(HTML.TR_ELEM);
                    tr.setAttribute(HTML.STYLE_ATTR , "display: none;");
                    Element td = (Element) domContext.createElement(HTML.TD_ELEM);
                    tr.appendChild(td);
                    tbody.appendChild(tr);
                }
                int numberOfColumns = getConvertedColumnAttribute(uiComponent);
                int rowIndex =
                        -1;// this initial value ensures zero-based indexing in ids
                int columnIndex =
                        numberOfColumns; // this initial value invoked initialization of first row
                String columnStyleClasses[] = getColumnStyleClasses(uiComponent);
                String rowStyleClasses[] = getRowStyles(uiComponent);
                int columnStyleIndex = 0;
                int rowStyleIndex = 0;
                int numberOfColumnStyles = columnStyleClasses.length - 1;
                int numberOfRowStyles = rowStyleClasses.length;
                UIComponent facet = null;
                while (children.hasNext()) {
                    UIComponent nextChild = (UIComponent) children.next();
                    if (!nextChild.isRendered()) {
                        continue;
                    }
                    // detect whether a new row is needed
                    if (columnIndex >= numberOfColumns) {
                        tr = domContext.createElement("tr");
                        tbody.appendChild(tr);
                        if (numberOfRowStyles > 0) {
                            tr.setAttribute("class",rowStyleClasses[rowStyleIndex++]);
                            if (rowStyleIndex >= numberOfRowStyles) {
                                rowStyleIndex = 0;
                            }
                        }
                        columnStyleIndex = 0;
                        columnIndex = 0;
                        rowIndex++;
                    }
                    // create the td for this child
                    Element td = domContext.createElement("td");
                    tr.appendChild(td);
                    td.setAttribute("id", getIndexedClientId(facesContext,
                                                             uiComponent,
                                                             columnIndex,
                                                             rowIndex));
               
                    writeColStyles(columnStyleClasses, numberOfColumnStyles,
                                   columnStyleIndex, td, columnIndex + 1, uiComponent);
                    if (++columnStyleIndex > numberOfColumnStyles) {
                        columnStyleIndex = 0;
                    }
    
                    domContext.setCursorParent(td);
                    encodeParentAndChildren(facesContext, nextChild);
                    columnIndex++;
                }
            } 
        }
        renderFooterFacet(facesContext, uiComponent, domContext);
        domContext.stepOver();
    }

    // this method is overridden in the subclass
    public String[] getRowStyles(UIComponent uiComponent) {
        return getRowStyleClasses(uiComponent);
    }

    private String getIndexedClientId(FacesContext facesContext,
                                      UIComponent uiComponent,
                                      int columnIndex, int rowIndex) {
        return ClientIdPool.get(uiComponent.getClientId(facesContext)
               + "-"
               + rowIndex
               + "-"
               + columnIndex);
    }

    private int getConvertedColumnAttribute(UIComponent uiComponent) {
        int convertedColumnAttribute = 1; // default
        Object columnAttribute = uiComponent.getAttributes().get("columns");
        int value;
        if (columnAttribute != null
            && columnAttribute instanceof Integer
            && (value = ((Integer) columnAttribute).intValue()) > 0) {
            convertedColumnAttribute = value;
        }
        return (convertedColumnAttribute);
    }


    public void encodeEnd(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
        validateParameters(facesContext, uiComponent, null);
    }

    // this method is overridden in the subclass
    public void writeColStyles(String[] columnStyles, int columnStylesMaxIndex,
                               int columnStyleIndex, Element td,
                               int colNumber, UIComponent uiComponent) {
        if (columnStyles.length > 0) {
            if (columnStylesMaxIndex >= 0) {
                td.setAttribute("class",
                                columnStyles[columnStyleIndex]);
                if (++columnStyleIndex > columnStylesMaxIndex) {
                    columnStyleIndex = 0;
                }
            }
        }
    }
    
    protected String getRowStyle(UIComponent uiComponent, String style) {
        return style;
    }
   

}
