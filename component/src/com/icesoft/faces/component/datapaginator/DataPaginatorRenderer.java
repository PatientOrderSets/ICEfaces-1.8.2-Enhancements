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
/* Original Copyright
 * Copyright 2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.icesoft.faces.component.datapaginator;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;

import org.w3c.dom.Element;

import com.icesoft.faces.component.ext.HtmlCommandLink;
import com.icesoft.faces.component.panelseries.UISeries;
import com.icesoft.faces.component.util.CustomComponentUtils;
import com.icesoft.faces.component.ExtendedAttributeConstants;
import com.icesoft.faces.context.DOMContext;
import com.icesoft.faces.renderkit.dom_html_basic.DomBasicRenderer;
import com.icesoft.faces.renderkit.dom_html_basic.HTML;
import com.icesoft.faces.renderkit.dom_html_basic.PassThruAttributeRenderer;

import com.icesoft.util.pooling.ClientIdPool;

public class DataPaginatorRenderer extends DomBasicRenderer {
    public static final String RENDERER_TYPE = "com.icesoft.faces.DataScroller";

    protected static final String PAGE_NAVIGATION = "idx";

    private static final String[] PASSTHRU_EXCLUDE =
        new String[] { HTML.STYLE_ATTR };
    private static final String[] PASSTHRU =
        ExtendedAttributeConstants.getAttributes(
            ExtendedAttributeConstants.ICE_DATAPAGINATOR,
            PASSTHRU_EXCLUDE);
    
    public boolean getRendersChildren() {
        return true;
    }

    public void decode(FacesContext context, UIComponent component) {
        validateParameters(context, component, DataPaginator.class);
        Map parameter = context.getExternalContext().getRequestParameterMap();
        String param = (String) parameter.get(component.getClientId(context));
        if (param != null && param.length() >= PAGE_NAVIGATION.length()) {
            if (param.startsWith(PAGE_NAVIGATION)) {
                // queue a navigation event that results from the user pressing
                // on one of the page indexes
                component.queueEvent(new PaginatorActionEvent(component,
                                                              Integer.parseInt(
                                                                      param
                                                                              .substring(
                                                                              PAGE_NAVIGATION.length(),
                                                                              param.length()))));
            } else {
                // queue a navigation event that results from the user pressing
                // on the navigation buttons
                component
                        .queueEvent(new PaginatorActionEvent(component, param));
            }
        }
    }

    protected void setVariables(FacesContext facescontext,
                                DataPaginator scroller) throws IOException {
        Map requestMap = facescontext.getExternalContext().getRequestMap();

        String pageCountVar = scroller.getPageCountVar();
        if (pageCountVar != null) {
            int pageCount = scroller.getPageCount();
            requestMap.put(pageCountVar, new Integer(pageCount));
        }
        String pageIndexVar = scroller.getPageIndexVar();
        if (pageIndexVar != null) {
            int pageIndex = scroller.getPageIndex();
            if (pageIndex > scroller.getPageCount()) {
                pageIndex = scroller.getPageCount();
            }
            requestMap.put(pageIndexVar, new Integer(pageIndex));
        }
        String rowsCountVar = scroller.getRowsCountVar();
        if (rowsCountVar != null) {
            int rowsCount = scroller.getRowCount();
            requestMap.put(rowsCountVar, new Integer(rowsCount));
        }
        String displayedRowsCountVar = scroller.getDisplayedRowsCountVar();
        if (displayedRowsCountVar != null) {
            int displayedRowsCount = scroller.getRows();
            int max = scroller.getRowCount() - scroller.getFirstRow();
            if (displayedRowsCount > max) {
                displayedRowsCount = max;
            }
            requestMap.put(displayedRowsCountVar,
                           new Integer(displayedRowsCount));
        }
        String firstRowIndexVar = scroller.getFirstRowIndexVar();
        if (firstRowIndexVar != null) {
            int firstRowIndex = scroller.getFirstRow();
            if (scroller.getRowCount() > 0) {
                firstRowIndex += 1;
            } else { // ICE-2782
                firstRowIndex = 0;
            }
            requestMap.put(firstRowIndexVar, new Integer(firstRowIndex));
        }
        String lastRowIndexVar = scroller.getLastRowIndexVar();
        if (lastRowIndexVar != null) {
            int lastRowIndex = scroller.getFirstRow() + scroller.getRows();
            int count = scroller.getRowCount();
            if (lastRowIndex > count) {
                lastRowIndex = count;
            }
            requestMap.put(lastRowIndexVar, new Integer(lastRowIndex));
        }
    }

    public void removeVariables(FacesContext facescontext,
                                UIComponent uiComponent) throws IOException {
        DataPaginator scroller = (DataPaginator) uiComponent;
        Map requestMap = facescontext.getExternalContext().getRequestMap();

        String pageCountVar = scroller.getPageCountVar();
        if (pageCountVar != null) {
            requestMap.remove(pageCountVar);
        }
        String pageIndexVar = scroller.getPageIndexVar();
        if (pageIndexVar != null) {
            requestMap.remove(pageIndexVar);
        }
        String rowsCountVar = scroller.getRowsCountVar();
        if (rowsCountVar != null) {
            requestMap.remove(rowsCountVar);
        }
        String displayedRowsCountVar = scroller.getDisplayedRowsCountVar();
        if (displayedRowsCountVar != null) {
            requestMap.remove(displayedRowsCountVar);
        }
        String firstRowIndexVar = scroller.getFirstRowIndexVar();
        if (firstRowIndexVar != null) {
            requestMap.remove(firstRowIndexVar);
        }
        String lastRowIndexVar = scroller.getLastRowIndexVar();
        if (lastRowIndexVar != null) {
            requestMap.remove(lastRowIndexVar);
        }
    }

    public void encodeBegin(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
        validateParameters(facesContext, uiComponent, DataPaginator.class);
        DataPaginator scroller = (DataPaginator) uiComponent;
        if (!scroller.isModelResultSet()) {
            super.encodeBegin(facesContext, uiComponent);
                
            if (scroller.getChildCount() > 0) {
                // clear children before render
                List kids = scroller.getChildren();
                for (int i = 0; i < kids.size(); i++) {
                    UIComponent kid = (UIComponent) kids.get(i);
                    // do not remove the output text - bug #333
                    if (!kid.getFamily().equalsIgnoreCase("javax.faces.Output")) {
                        scroller.getChildren().remove(kids.get(i));
                    }
                }
            }
            //Reset the dataTable model before setting variables
            scroller.getUIData().setValue(null);
            scroller.getUIData().setRowIndex(-1);
            ((UISeries) scroller.getUIData()).ensureFirstRowInRange(); // ICE-2783
            setVariables(facesContext, scroller);
        }
    }

    public void encodeChildren(FacesContext facescontext,
                               UIComponent uicomponent) throws IOException {
        validateParameters(facescontext, uicomponent, DataPaginator.class);
        DataPaginator scroller = (DataPaginator) uicomponent;
        if (!scroller.isModelResultSet()) {
            boolean singlePageScroller = scroller.getPageCount() <= 1 &&
                                         scroller.getRowsCountVar() == null &&
                                         scroller.getDisplayedRowsCountVar() ==
                                         null;
            if (!singlePageScroller) {
                CustomComponentUtils.renderChildren(facescontext, uicomponent);
            }
        }
    }

    public void encodeEnd(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
        validateParameters(facesContext, uiComponent, DataPaginator.class);
        DataPaginator scroller = (DataPaginator) uiComponent;
        if (scroller.getUIData() == null) {
            return;
        }
        renderScroller(facesContext, uiComponent);
        if (!scroller.isModelResultSet()) {
            removeVariables(facesContext, uiComponent);
        }
    }


    protected void renderScroller(FacesContext facesContext,
                                  UIComponent uiComponent) throws IOException {
        DataPaginator scroller = (DataPaginator) uiComponent;
        if (!scroller.isModelResultSet()) {
            if (!scroller.isRenderFacetsIfSinglePage() &&
                scroller.getPageCount() <= 1) {
                return;
            }
        }
        if (scroller.getFacets().size() <= 0) {
            return;
        }

        DOMContext domContext =
                DOMContext.attachDOMContext(facesContext, scroller);
        if (!domContext.isInitialized()) {
            Element table = domContext.createRootElement(HTML.TABLE_ELEM);
            setRootElementId(facesContext, table, scroller);
            PassThruAttributeRenderer
                        .renderHtmlAttributes(facesContext, scroller, PASSTHRU);
        }
        Element table = (Element) domContext.getRootNode();
        DOMContext.removeChildren(table);

        Element tr = domContext.createElement(HTML.TR_ELEM);
        table.appendChild(tr);

        String styleClass = scroller.getStyleClass();
        table.setAttribute(HTML.CLASS_ATTR, styleClass);

        String style = scroller.getStyle();
        if(style != null && style.length() > 0)
            table.setAttribute(HTML.STYLE_ATTR, style);
        else
            table.removeAttribute(HTML.STYLE_ATTR);
        String scrollButtonCellClass = scroller.getscrollButtonCellClass();

        handleFacet(facesContext, scroller, domContext, table, tr,
                    scrollButtonCellClass, scroller.getFirst(),
                    DataPaginator.FACET_FIRST);
        
        // Horizontal shares a table row, but for vertical,
        // have them each auto-create their own table row
        if (scroller.isVertical()) {
            tr = null;
        }
        
        handleFacet(facesContext, scroller, domContext, table, tr,
                    scrollButtonCellClass, scroller.getFastRewind(),
                    DataPaginator.FACET_FAST_REWIND);
        
        handleFacet(facesContext, scroller, domContext, table, tr,
                    scrollButtonCellClass, scroller.getPrevious(),
                    DataPaginator.FACET_PREVIOUS);
        
        if (!scroller.isModelResultSet() && scroller.isPaginator()) {
            if (scroller.isVertical()) {
                tr = domContext.createElement(HTML.TR_ELEM);
                table.appendChild(tr);
            }
            Element td = domContext.createElement(HTML.TD_ELEM);
            tr.appendChild(td);
            Element paginatorTable = domContext.createElement(HTML.TABLE_ELEM);
            td.appendChild(paginatorTable);
            renderPaginator(facesContext, uiComponent, paginatorTable,
                            domContext);
        }
        
        handleFacet(facesContext, scroller, domContext, table, tr,
                    scrollButtonCellClass, scroller.getNext(),
                    DataPaginator.FACET_NEXT);
        
        handleFacet(facesContext, scroller, domContext, table, tr,
                    scrollButtonCellClass, scroller.getFastForward(),
                    DataPaginator.FACET_FAST_FORWARD);
        
        handleFacet(facesContext, scroller, domContext, table, tr,
                    scrollButtonCellClass, scroller.getLast(),
                    DataPaginator.FACET_LAST);
        
        domContext.stepOver();
    }
    
    protected void handleFacet(FacesContext facesContext,
                               DataPaginator scroller,
                               DOMContext domContext,
                               Element table,
                               Element tr,
                               String scrollButtonCellClass,
                               UIComponent facetComp,
                               String facetName)
            throws IOException {
        if (facetComp != null) {
            // tr is null when scroller.isVertical()
            if (tr == null) {
                tr = domContext.createElement(HTML.TR_ELEM);
                table.appendChild(tr);
            }
            Element td = domContext.createElement(HTML.TD_ELEM);
            td.setAttribute(HTML.CLASS_ATTR, scrollButtonCellClass);
            tr.appendChild(td);
            domContext.setCursorParent(td);
            renderFacet(facesContext, scroller, facetComp, facetName);
        }
    }
    
    protected void renderFacet(FacesContext facesContext,
                               DataPaginator scroller,
                               UIComponent facetComp, String facetName)
            throws IOException {
        HtmlCommandLink link =
                (HtmlCommandLink) getLink(facesContext, scroller, facetName);

        if (scroller.isDisabled() ||
            (!scroller.isModelResultSet() && scroller.getPageCount() <= 1) ||
            (scroller.getPageIndex() == 1 && (DataPaginator.FACET_FAST_REWIND.equals(facetName) ||
            DataPaginator.FACET_FIRST.equals(facetName) ||
            DataPaginator.FACET_PREVIOUS.equals(facetName) 
             )) ||
             (scroller.getPageIndex() == scroller.getPageCount() && (DataPaginator.FACET_FAST_FORWARD.equals(facetName) ||
             DataPaginator.FACET_LAST.equals(facetName) ||
             DataPaginator.FACET_NEXT.equals(facetName) 
             ))
            ) {
            link.setDisabled(true);
        } else {
            link.setDisabled(false);
        }

        link.encodeBegin(facesContext);
        encodeParentAndChildren(facesContext, facetComp);
        link.encodeEnd(facesContext);
    }

    protected void renderPaginator(FacesContext facesContext,
                                   UIComponent uiComponent,
                                   Element paginatorTable,
                                   DOMContext domContext) throws IOException {
        DataPaginator scroller = (DataPaginator) uiComponent;
        int maxPages = scroller.getPaginatorMaxPages();
        if (maxPages <= 1) {
            maxPages = 2;
        }
        int pageCount = scroller.getPageCount();
        if (pageCount <= 1) {
            return;
        }
        int pageIndex = scroller.getPageIndex();
        if (pageIndex > pageCount) {
            pageIndex = pageCount;
        }
        int delta = maxPages / 2;
        int pages;
        int start;
        if (pageCount > maxPages && pageIndex > delta) {
            pages = maxPages;
            start = pageIndex - pages / 2 - 1;
            if (start + pages > pageCount) {
                start = pageCount - pages;
            }
        } else {
            pages = pageCount < maxPages ? pageCount : maxPages;
            start = 0;
        }
        String styleClass = scroller.getPaginatorTableClass();
        paginatorTable.setAttribute(HTML.CLASS_ATTR, styleClass);

        Element tr = null;
        if (!scroller.isVertical()) {
            tr = domContext.createElement(HTML.TR_ELEM);
            paginatorTable.appendChild(tr);
        }
        Element td;
        UIComponent form = findForm(scroller);
        String formId = null;
        if (form == null) {
            throw new FacesException("Form tag is missing");
        } else {
            formId = form.getClientId(facesContext);
        }
        for (int i = start, size = start + pages; i < size; i++) {
            int idx = i + 1;
            if (scroller.isVertical()) {
                tr = domContext.createElement(HTML.TR_ELEM);
                paginatorTable.appendChild(tr);
            }
            td = domContext.createElement(HTML.TD_ELEM);
            tr.appendChild(td);
            domContext.setCursorParent(td);
            String cStyleClass;

            if (idx == pageIndex) {
                cStyleClass = scroller.getPaginatorActiveColumnClass();

            } else {
                cStyleClass = scroller.getPaginatorColumnClass();

            }
            if (cStyleClass != null) {
                td.setAttribute(HTML.CLASS_ATTR, cStyleClass);
            }

            Element link = getLink(facesContext, domContext, scroller,
                                   Integer.toString(idx), idx, formId);
            td.appendChild(link);
        }
    }

    protected Element getLink(FacesContext facesContext, DOMContext domContext,
                              DataPaginator scroller,
                              String text, int pageIndex, String formId) {

        Element link = domContext.createElement(HTML.ANCHOR_ELEM);
        if (text != null) {
            link.appendChild(domContext.createTextNode(text));
        }
        String linkid = ClientIdPool.get(scroller.getClientId(facesContext) +
                        DataPaginatorRenderer.PAGE_NAVIGATION +
                        Integer.toString(pageIndex));
        String onClick = /*"document.forms['"+ formId + "']" + "['"+ formId +":_idcl']" + ".value='" +  linkid  + "'"+ 
        		";*/"document.forms['" + formId + "']['" +
              scroller.getClientId(facesContext) + "'].value='" +
              DataPaginatorRenderer.PAGE_NAVIGATION + text + "'" +
              ";iceSubmit(" + " document.forms['" + formId + "']," +
              " this,event); " + "return false;";
        link.setAttribute(HTML.ID_ATTR, linkid);
        if (scroller.isDisabled()) {
            link.removeAttribute(HTML.ONCLICK_ATTR);
        } else {
            link.setAttribute(HTML.ONCLICK_ATTR, onClick);
        }
        link.setAttribute(HTML.HREF_ATTR, "javascript:;");
        PassThruAttributeRenderer.renderOnFocus(scroller, link);
        PassThruAttributeRenderer.renderOnBlur(link);
        return link;
    }

    protected HtmlCommandLink getLink(FacesContext facesContext,
                                      DataPaginator scroller,
                                      String facetName) {
        Application application = facesContext.getApplication();

        HtmlCommandLink link = (HtmlCommandLink) application
                .createComponent(HtmlCommandLink.COMPONENT_TYPE);
        String id = scroller.getId() + facetName;
        link.setId(id);
        link.setTransient(true);
        UIParameter parameter = (UIParameter) application
                .createComponent(UIParameter.COMPONENT_TYPE);
        parameter.setId(id + "_param");
        parameter.setTransient(true);
        parameter.setName(scroller.getClientId(facesContext));
        parameter.setValue(facetName);
        //getChildren doesn't need any check for the childCount
        List children = link.getChildren();
        children.add(parameter);
        
        // For some reason, these components being marked transient isn't 
        // resulting in them going away, so we'll explicitly remove old ones
        for(int i = 0; i < scroller.getChildCount(); i++) {
            UIComponent comp = (UIComponent) scroller.getChildren().get(i);
            if (comp.getId().equals(id)) {
                scroller.getChildren().remove(i);
                break;
            }
        }
        
        scroller.getChildren().add(link);
        return link;
    }
}
