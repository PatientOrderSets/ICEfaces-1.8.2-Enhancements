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
import com.icesoft.faces.util.Debug;
import org.w3c.dom.Element;

import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public class TableRenderer extends DomBasicRenderer {

    private static final String[] passThruAttributes = AttributeConstants.getAttributes(AttributeConstants.H_DATATABLE);

    public boolean getRendersChildren() {
        return true;
    }

    public void encodeBegin(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {

        validateParameters(facesContext, uiComponent, null);
        ResponseWriter responseWriter = facesContext.getResponseWriter();
        Debug.assertTrue(responseWriter != null, "ResponseWriter is null");
        uiComponent.getAttributes().get("resizableColumnWidths");
        DOMContext domContext =
                DOMContext.attachDOMContext(facesContext, uiComponent);

        if (!domContext.isInitialized()) {
            Element root = null;
            root = domContext.createRootElement("table");
            setRootElementId(facesContext, root, uiComponent);
            PassThruAttributeRenderer.renderHtmlAttributes(
                    facesContext, uiComponent, passThruAttributes);
        }
        Element root = (Element) domContext.getRootNode();
        DOMContext.removeChildren(root);
        String styleClass = getComponentStyleClass(uiComponent);
        if (styleClass != null && styleClass.length() > 0) {
            root.setAttribute("class", styleClass);
        }
        Object cellspacing = uiComponent.getAttributes().get(HTML.CELLSPACING_ATTR);
        if (cellspacing != null) {
            root.setAttribute(HTML.CELLSPACING_ATTR, String.valueOf(cellspacing));
        } else {
            root.setAttribute(HTML.CELLSPACING_ATTR, "0");
        }

        if (isScrollable(uiComponent)) {
            Element tr = domContext.createElement("tr");
            root.appendChild(tr);
            Element td = domContext.createElement("td");
            tr.appendChild(td);
            Element mainDiv = domContext.createElement("div");
            td.appendChild(mainDiv);
            Element headerDiv = domContext.createElement("div");
            Element headerTable = domContext.createElement("table");
            headerTable.setAttribute("style","width:100%;");
            headerDiv.appendChild(headerTable);

            mainDiv.appendChild(headerDiv);
            Element bodyDiv = domContext.createElement("div");
            String height =
                    (String) uiComponent.getAttributes().get("scrollHeight");
            bodyDiv.setAttribute("style",
                               "overflow:auto;width:100%;"+ (height!=null&&height.length()>0?"height:" + height + ";":""));
            
            Element bodytable = domContext.createElement("table");
//            bodytable.setAttribute("style","width:100%;");
            bodyDiv.appendChild(bodytable);
            mainDiv.appendChild(bodyDiv);
            Object scollFooter = uiComponent.getAttributes().get("scrollFooter");
            if (!(scollFooter != null && ((Boolean)scollFooter).booleanValue())) {
                Element footerDiv = domContext.createElement("div");
                Element footerTable = domContext.createElement("table");
                footerDiv.appendChild(footerTable);
                mainDiv.appendChild(footerDiv);
            }
        }
        renderFacet(facesContext, uiComponent, domContext, true); //header facet
        renderFacet(facesContext, uiComponent, domContext,
                    false); //footer facet
    }

    // this method is overridden in the subclass
    public String getComponentStyleClass(UIComponent uiComponent) {
        String styleClass =
                (String) uiComponent.getAttributes().get("styleClass");
        return styleClass;
    }



    protected void renderFacet(FacesContext facesContext,
                               UIComponent uiComponent,
                               DOMContext domContext, boolean header)
            throws IOException {
        String facet, tag, element, facetClass;
        if (header) {
            facet = "header";
            tag = HTML.THEAD_ELEM;
            element = HTML.TH_ELEM;
            facetClass = getHeaderClass(uiComponent);
        } else {
            facet = "footer";
            tag = HTML.TFOOT_ELEM;
            element = HTML.TD_ELEM;
            facetClass = getFooterClass(uiComponent);
        }
        UIData uiData = (UIData) uiComponent;
        uiData.setRowIndex(-1);
        Element root = (Element) domContext.getRootNode();
        if (isScrollable(uiComponent)) {
            if (header) {
                Element headerDiv = domContext.createElement("div");
                Element headerTable = domContext.createElement("table");
                headerDiv.appendChild(headerTable);
                root.getFirstChild().getFirstChild().appendChild(headerDiv);
                root = headerTable;
            } else {
                // Get the table in the second div
                root = (Element) root.getChildNodes().item(1).getFirstChild();
            }
        }

        // detect whether a facet exists on the UIData component or any of the
        // child UIColumn components; if so, then render a thead element
        UIComponent headerFacet = getFacetByName(uiData, facet);
        boolean childHeaderFacetExists =
                childColumnHasFacetWithName(uiData, facet);
        Element thead = null;
        if (headerFacet != null || childHeaderFacetExists) {
            thead = domContext.createElement(tag);
            root.appendChild(thead);
        }
        // if the header is associated with the UIData component then encode the
        // header inside a tr and th element that span the whole table.
        if (headerFacet != null && headerFacet.isRendered()) {
            resetFacetChildId(headerFacet);
            Element tr = domContext.createElement("tr");
            thead.appendChild(tr);
            Element th = domContext.createElement(element);
            tr.appendChild(th);
            if (facetClass != null) {
                th.setAttribute("class", facetClass);
            }
            th.setAttribute("colspan",
                            String.valueOf(getNumberOfChildColumns(uiData)));
            th.setAttribute("scope", "colgroup");
            domContext.setCursorParent(th);
            encodeParentAndChildren(facesContext, headerFacet);
        }

        // if one or more of the child columns has a header facet then render a
        // row to accommodate the header(s); render an empty th for each column
        // that has no header facet
        if (childHeaderFacetExists) {
            Element tr = domContext.createElement("tr");
            thead.appendChild(tr);
            StringTokenizer columnWidths =
                    getColumnWidths(uiData);

            Iterator childColumns = getRenderedChildColumnsIterator(uiData);
            while (childColumns.hasNext()) {


                UIColumn nextColumn = (UIColumn) childColumns.next();
                Element th = domContext.createElement(element);
                tr.appendChild(th);
                if (facetClass != null) {
                    th.setAttribute("class", facetClass);
                }
                if (columnWidths != null && columnWidths.hasMoreTokens()) {
                    String width = columnWidths.nextToken();

                    th.setAttribute("style",
                                    "width:" + width + ";overflow:hidden;");
                }
                //th.setAttribute("colgroup", "col");
                UIComponent nextFacet = getFacetByName(nextColumn, facet);
                if (nextFacet != null) {
                    resetFacetChildId(nextFacet);
                    domContext.setCursorParent(th);
                    encodeParentAndChildren(facesContext, nextFacet);
                }
            }
            if (isScrollable(uiComponent)) {
                tr.appendChild(scrollBarSpacer(domContext, facesContext));
            }
        }
        domContext.setCursorParent(root);
    }

    protected void resetFacetChildId(UIComponent component) {
        component.setId(component.getId());
        if (component.getChildCount() == 0)return;
        Iterator facetChild = component.getChildren().iterator();
        while (facetChild.hasNext()) {
            UIComponent child = (UIComponent) facetChild.next();
            resetFacetChildId(child);
        }
    }

    // this method is overridden in the subclass
    public String getHeaderClass(UIComponent component) {
        return (String) component.getAttributes().get("headerClass");
    }


    public String getFooterClass(UIComponent component) {
        return (String) component.getAttributes().get("footerClass");
    }



    protected boolean childColumnHasFacetWithName(UIComponent component,
                                                  String facetName) {
        Iterator childColumns = getRenderedChildColumnsIterator(component);
        while (childColumns.hasNext()) {
            UIColumn nextChildColumn = (UIColumn) childColumns.next();
            if (getFacetByName(nextChildColumn, facetName) != null) {
                return true;
            }
        }
        return false;
    }

    public void encodeChildren(FacesContext facesContext,
                               UIComponent uiComponent)
            throws IOException {

        validateParameters(facesContext, uiComponent, null);

        DOMContext domContext =
                DOMContext.attachDOMContext(facesContext, uiComponent);
        Element root = (Element) domContext.getRootNode();

        Element tbody = domContext.createElement("tbody");
        root.appendChild(tbody);

        // render the appropriate styles for each row and column
        String columnStyles[] = getColumnStyleClasses(uiComponent);

        String rowStyles[] = getRowStyles(uiComponent);

        int columnStyleIndex = 0;
        int rowStyleIndex = 0;
        int columnStylesMaxIndex = columnStyles.length - 1;
        int rowStylesMaxIndex = rowStyles.length - 1;
        // keep track of row index on UIData component and how many rows we've displayed
        UIData uiData = (UIData) uiComponent;
        int rowIndex = uiData.getFirst();
        int numberOfRowsToDisplay = uiData.getRows();
        int countOfRowsDisplayed = 0;
        uiData.setRowIndex(rowIndex);

        while (uiData.isRowAvailable()) {
            // Have we finished the required number of rows ? Note that
            // numberOfRowsToDisplay == 0 means that we display all remaining rows
            // of the underlying model and in this case we rely on the
            // isRowAvailable method (above) to limit rendering work.
            if (numberOfRowsToDisplay > 0
                && countOfRowsDisplayed >= numberOfRowsToDisplay) {
                break;
            }
            // render another row
            Element tr = domContext.createElement("tr");
            tr.setAttribute("id", uiComponent.getClientId(facesContext));
            tbody.appendChild(tr);
            // if row styles exist, then render the appropriate one
            if (rowStylesMaxIndex >= 0) {
                tr.setAttribute("class", rowStyles[rowStyleIndex]);
                if (++rowStyleIndex > rowStylesMaxIndex) {
                    rowStyleIndex = 0;
                }
            }
            // render the child columns; each one in a td
            Iterator childColumns;
            childColumns = getRenderedChildColumnsIterator(uiData);
            StringTokenizer columnWidths =
                    getColumnWidths(uiComponent);

            int colNumber = 1;
            while (childColumns.hasNext()) {
                // render another td
                UIColumn nextColumn = (UIColumn) childColumns.next();
                Element td = domContext.createElement("td");
                if (columnWidths != null && columnWidths.hasMoreTokens()) {
                    td.setAttribute("style",
                                    "width:" + columnWidths.nextToken() + ";");
                }
                tr.appendChild(td);

                // if column styles exist, then apply the appropriate one
                writeColStyles(columnStyles, columnStylesMaxIndex,
                               columnStyleIndex, td, colNumber++,
                               uiComponent);
                if (++columnStyleIndex > columnStylesMaxIndex) {
                    columnStyleIndex = 0;
                }

                if (nextColumn.getChildCount() > 0) {
                    // recursively render the components contained within this td (column)
                    Iterator childrenOfThisColumn =
                            nextColumn.getChildren().iterator();
                    domContext.setCursorParent(td);
                    while (childrenOfThisColumn.hasNext()) {
                        UIComponent nextChild =
                                (UIComponent) childrenOfThisColumn.next();
                        if (nextChild.isRendered()) {
                            encodeParentAndChildren(facesContext, nextChild);
                        }
                    }
                }
            }
            // keep track of rows displayed
            countOfRowsDisplayed++;
            // maintain the row index property on the underlying UIData component
            rowIndex++;
            uiData.setRowIndex(rowIndex);
            // reset the column style index for the next row
            columnStyleIndex = 0;


        }
        // reset the underlying UIData component  
        uiData.setRowIndex(-1);
        domContext.stepOver();
    }

    // this method is overridden in the subclass
    public void writeColStyles(String[] columnStyles, int columnStylesMaxIndex,
                               int columnStyleIndex, Element td,
                               int colNumber,
                                UIComponent uiComponent) {
        if (columnStyles.length > 0) {
            if (columnStylesMaxIndex >= 0) {
                td.setAttribute("class", columnStyles[columnStyleIndex]);
                if (++columnStyleIndex > columnStylesMaxIndex) {
                    columnStyleIndex = 0;
                }
            }
        }
    }

    // this method is overridden in the subclass
    public String[] getRowStyles(UIComponent uiComponent) {
        return getRowStyleClasses(uiComponent);
    }

    public void encodeEnd(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
        validateParameters(facesContext, uiComponent, null);
        if (!uiComponent.isRendered()) {
            return;
        }
        if (isScrollable(uiComponent)) {
            JavascriptContext.addJavascriptCall(facesContext, "Ice.dataTable.onLoad('" +
                    uiComponent.getClientId(facesContext) + "');");
        }
    }

    protected int getNumberOfChildColumns(UIComponent component) {
        return getRenderedChildColumnsList(component).size();
    }

    protected Iterator getRenderedChildColumnsIterator(UIComponent component) {
        return getRenderedChildColumnsList(component).iterator();
    }

    protected List getRenderedChildColumnsList(UIComponent component) {
        List results = new ArrayList();
        if (component.getChildCount() > 0) {
            Iterator kids = component.getChildren().iterator();
            while (kids.hasNext()) {
                UIComponent kid = (UIComponent) kids.next();
                if ((kid instanceof UIColumn) && kid.isRendered()) {
                    results.add(kid);
                }
            }
        }
        return results;
    }

    protected boolean isScrollable(UIComponent uiComponent) {
        Object o = uiComponent.getAttributes().get("scrollable");
        if (o != null && o instanceof Boolean) {
            return ((Boolean) o).booleanValue();
        }
        return false;
    }

    protected Element scrollBarSpacer(DOMContext domContext, FacesContext facesContext) {
        Element spacer = domContext.createElement("th");
        spacer.setAttribute(HTML.STYLE_ATTR, "padding:0"); // ICE-2654
        spacer.appendChild(domContext.createElement(HTML.DIV_ELEM));
        return spacer;
    }

    protected StringTokenizer getColumnWidths(UIComponent uiComponent) {
        Object o = uiComponent.getAttributes().get("columnWidths");
        if (o != null && o instanceof String) {
            return new StringTokenizer(o.toString(), ",");
        }
        return null;
    }
    
    protected List getColumnWidthsAsList(UIComponent uiComponent) {
        StringTokenizer st = getColumnWidths(uiComponent);
        if (st == null) {
            return null;
        }
        int count = st.countTokens();
        if (count == 0) {
            return null;
        }
        List columnWidths = new ArrayList(count);
        while (st.hasMoreTokens()) {
            columnWidths.add(st.nextToken());
        }
        return columnWidths;
    }

    protected Element getScrollableHeaderTableElement(Element root) {
        // First table in first div path : table/tr/td/div/div0/table
        return (Element) root.getFirstChild().getFirstChild().getFirstChild().getFirstChild().getFirstChild();
    }

    protected Element getScrollableBodyTableElement(Element root) {
        // First table in second div path table/tr/td/div/div1/table
        return (Element) root.getFirstChild().getFirstChild().getFirstChild().getFirstChild().getNextSibling().getFirstChild();
    }

    protected Element getScrollableFooterTableElement(Element root) {
        // First table in second div path table/tr/td/div/div1/table
        return (Element) root.getFirstChild().getFirstChild().getFirstChild().getFirstChild().getNextSibling().getNextSibling().getFirstChild();
    }
}
