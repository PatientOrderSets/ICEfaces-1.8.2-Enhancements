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

import com.icesoft.faces.component.CSS_DEFAULT;
import com.icesoft.faces.component.ext.taglib.Util;
import com.icesoft.faces.application.D2DViewHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.component.ActionSource;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.event.FacesEvent;
import javax.faces.event.PhaseId;
import java.sql.ResultSet;


/**
 * 
 */
public class DataPaginator extends HtmlPanelGroup implements ActionSource {
    private final Log log = LogFactory.getLog(DataPaginator.class);

    private static final String FIRST_FACET_NAME = "first";
    private static final String LAST_FACET_NAME = "last";
    private static final String NEXT_FACET_NAME = "next";
    private static final String PREVIOUS_FACET_NAME = "previous";
    private static final String FAST_FORWARD_FACET_NAME = "fastforward";
    private static final String FAST_REWIND_FACET_NAME = "fastrewind";

    // just for caching the associated uidata
    private transient UIData _UIData;

    private MethodBinding _actionListener;
    private Boolean disabled = null;

    /**
     * @return COMPONENT_TYPE
     */
    public String getComponentType() {
        return COMPONENT_TYPE;
    }

    /**
     * @return DEFAULT_RENDERER_TYPE
     * @see javax.faces.component.UIComponent#getRendererType()
     */
    public String getRendererType() {
        return DEFAULT_RENDERER_TYPE;
    }

    /**
     * <p>Set the value of the <code>disabled</code> property.</p>
     */
    public void setDisabled(boolean disabled) {
        this.disabled = new Boolean(disabled);
        ValueBinding vb = getValueBinding("disabled");
        if (vb != null) {
            vb.setValue(getFacesContext(), this.disabled);
            this.disabled = null;
        }
    }

    /**
     * <p>Return the value of the <code>disabled</code> property.</p>
     */
    public boolean isDisabled() {
        if (!Util.isEnabledOnUserRole(this)) {
            return true;
        }
        if (disabled != null) {
            return disabled.booleanValue();
        }
        ValueBinding vb = getValueBinding("disabled");
        Boolean v =
                vb != null ? (Boolean) vb.getValue(getFacesContext()) : null;
        return v != null ? v.booleanValue() : false;
    }

    /**
     * @see javax.faces.component.UIComponentBase#queueEvent(javax.faces.event.FacesEvent)
     */
    public void queueEvent(FacesEvent event) {
        if (event != null && event instanceof ActionEvent) {
            if (isImmediate()) {
                event.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);
            } else {
                event.setPhaseId(PhaseId.INVOKE_APPLICATION);
            }
        }
        super.queueEvent(event);
    }

    /**
     * @see javax.faces.component.UIComponentBase#broadcast(javax.faces.event.FacesEvent)
     */
    public void broadcast(FacesEvent event) throws AbortProcessingException {
        super.broadcast(event);
        if (event instanceof PaginatorActionEvent) {
            PaginatorActionEvent scrollerEvent = (PaginatorActionEvent) event;
            UIData uiData = getUIData();
            if (uiData == null) {
                return;
            }

            int pageindex = scrollerEvent.getPageIndex();
            if (pageindex == -1) {
                String facet = scrollerEvent.getScrollerfacet();
                if (FACET_FIRST.equals(facet)) {
                    gotoFirstPage();
                } else if (FACET_PREVIOUS.equals(facet)) {
                    gotoPreviousPage();
                } else if (FACET_NEXT.equals(facet)) {
                    gotoNextPage();
                } else if (FACET_FAST_FORWARD.equals(facet)) {
                    gotoFastForward();
                } else if (FACET_FAST_REWIND.equals(facet)) {
                    gotoFastRewind();
                } else if (FACET_LAST.equals(facet)) {
                    gotoLastPage();
                }
                scrollerEvent.setPageIndex(getPageIndex());
            } else {
                int pageCount = getPageCount();
                if (pageindex > pageCount) {
                    pageindex = pageCount;
                } else if (pageindex <= 0) {
                    pageindex = 1;
                }
                uiData.setFirst(uiData.getRows() * (pageindex - 1));
            }
            broadcastToActionListener(scrollerEvent);            
        }
    }

    protected void broadcastToActionListener(PaginatorActionEvent event) {
        FacesContext context = getFacesContext();

        try {
            MethodBinding actionListenerBinding = getActionListener();
            if (actionListenerBinding != null) {
                actionListenerBinding.invoke(context, new Object[]{event});
            }
            // super.broadcast(event) does this itself
            //ActionListener[] actionListeners = getActionListeners();
            //if(actionListeners != null) {
            //    for(int i = 0; i < actionListeners.length; i++) {
            //        actionListeners[i].processAction(event);
            //    }
            //}
        } catch (EvaluationException e) {
            Throwable cause = e.getCause();
            if (cause != null &&
                cause instanceof AbortProcessingException) {
                throw(AbortProcessingException) cause;
            }
            throw e;
        }
    }

    /**
     * <p>Return the instance of the <code>UIData</code> associated to this
     * component.</p>
     */
    public UIData getUIData() {
        if (_UIData == null) {
            _UIData = findUIData();
        }
        return _UIData;
    }

    public void setUIData(UIData uiData) {
        _UIData = uiData;
    }


    /**
     * <p>Return the value of the <code>pageIndex</code> property.</p>
     */
    public int getPageIndex() {
        UIData uiData = getUIData();
        int rows = uiData.getRows();
        int pageCount = getPageCount();
        int pageIndex = 0;
        if (rows > 0) {
            pageIndex = uiData.getFirst() / rows + 1;
            // if the page index > pageCount then some rows may have been removed
            // so we need to show the last page which may be a partial or full page
            // worth of data
            if (pageIndex > pageCount) {
                pageIndex = pageCount;
                // Code redone in HtmlDataTable.ensureFirstRowInRange() for ICE-2783
            }
        } else {
            log.warn("DataTable " +
                     uiData.getClientId(FacesContext.getCurrentInstance())
                     + " has invalid rows attribute.");
            pageIndex = 0;
        }

        if (rows == 0) {
            pageIndex = 1;
        } else if (uiData.getFirst() % rows > 0) {
            pageIndex++;
        }
        return pageIndex;
    }

    /**
     * <p>Return the value of the <code>pageCount</code> property.</p>
     */
    public int getPageCount() {
        UIData uiData = getUIData();
        int rows = uiData.getRows();

        int pageCount;
        if (rows > 0) {
            pageCount = rows <= 0 ? 1 : uiData.getRowCount() / rows;
            if (uiData.getRowCount() % rows > 0) {
                pageCount++;
            }
        } else {
            rows = 1;
            pageCount = 1;
        }
        return pageCount;
    }

    /**
     * <p>Return the value of the <code>rowCount</code> property.</p>
     */
    public int getRowCount() {
        return getUIData().getRowCount();
    }

    /**
     * <p>Return the value of the <code>rows</code> property.</p>
     */
    public int getRows() {
        return getUIData().getRows();
    }

    /**
     * <p>Return the value of the <code>first</code> property.</p>
     */
    public int getFirstRow() {
        return getUIData().getFirst();
    }

    /**
     * <p>Return the instance of <code>UIData</code> associated to this
     * component.</p>
     */
    protected UIData findUIData() {
        String forStr = getFor();
        UIComponent forComp;
        if (forStr == null) {
            // DataPaginator may be a child of uiData
            forComp = getParent();
        } else {

            //forComp = findComponent(forStr);

            forComp =  D2DViewHandler.findComponent(forStr, this);
        }
        if (forComp == null) {
            throw new IllegalArgumentException(
                    "could not find UIData referenced by attribute dataScroller@for = '"
                    + forStr + "'");
        } else if (!(forComp instanceof UIData)) {
            throw new IllegalArgumentException(
                    "uiComponent referenced by attribute dataScroller@for = '" +
                    forStr + "' must be of type "
                    + UIData.class.getName() + ", not type " +
                    forComp.getClass().getName());
        }
        return (UIData) forComp;
    }

    /**
     * <p>Set the value of the <code>first</code> facet for this component.</p>
     */
    public void setFirst(UIComponent first) {
        getFacets().put(FIRST_FACET_NAME, first);
    }

    /**
     * <p>Return the value of the <code>first</code> facet of this
     * component.</p>
     */
    public UIComponent getFirst() {
        return (UIComponent) getFacet(FIRST_FACET_NAME);
    }

    /**
     * <p>Set the value of the <code>last</code> facet for this component.</p>
     */
    public void setLast(UIComponent last) {
        getFacets().put(LAST_FACET_NAME, last);
    }

    /**
     * <p>Return the value of the <code>last</code> facet of this
     * component.</p>
     */
    public UIComponent getLast() {
        return (UIComponent) getFacet(LAST_FACET_NAME);
    }

    /**
     * <p>Set the value of the <code>next</code> facet for this component.</p>
     */
    public void setNext(UIComponent next) {
        getFacets().put(NEXT_FACET_NAME, next);
    }

    /**
     * <p>Return the value of the <code>next</code> facet of this
     * component.</p>
     */
    public UIComponent getNext() {
        return (UIComponent) getFacet(NEXT_FACET_NAME);
    }

    /**
     * <p>Set the value of the <code>fast forward</code> facet for this
     * component.</p>
     */
    public void setFastForward(UIComponent previous) {
        getFacets().put(FAST_FORWARD_FACET_NAME, previous);
    }

    /**
     * <p>Return the value of the <code>fast forward</code> facet of this
     * component.</p>
     */
    public UIComponent getFastForward() {
        return (UIComponent) getFacet(FAST_FORWARD_FACET_NAME);
    }

    /**
     * <p>Set the value of the <code>fast rewind</code> facet for this
     * component.</p>
     */
    public void setFastRewind(UIComponent previous) {
        getFacets().put(FAST_REWIND_FACET_NAME, previous);
    }

    /**
     * <p>Return the value of the <code>fast rewind</code> facet of this
     * component.</p>
     */
    public UIComponent getFastRewind() {
        return (UIComponent) getFacet(FAST_REWIND_FACET_NAME);
    }

    /**
     * <p>Set the value of the <code>previous</code> facet for this
     * component.</p>
     */
    public void setPrevious(UIComponent previous) {
        getFacets().put(PREVIOUS_FACET_NAME, previous);
    }

    /**
     * <p>Return the value of the <code>previous</code> facet of this
     * component.</p>
     */
    public UIComponent getPrevious() {
        return (UIComponent) getFacet(PREVIOUS_FACET_NAME);
    }

    /**
     * <p>Return a flag indicating whether this component is responsible for
     * rendering its child components.</p>
     */
    public boolean getRendersChildren() {
        return true;
    }

    /**
     * @see javax.faces.component.ActionSource#getAction()
     */
    public MethodBinding getAction() {
        // not used
        return null;
    }

    /**
     * @see javax.faces.component.ActionSource#setAction(javax.faces.el.MethodBinding)
     */
    public void setAction(MethodBinding action) {
        throw new UnsupportedOperationException(
                "Defining an action is not supported. Use an actionListener");
    }

    /**
     * @see javax.faces.component.ActionSource#setActionListener(javax.faces.el.MethodBinding)
     */
    public void setActionListener(MethodBinding actionListener) {
        _actionListener = actionListener;
    }

    /**
     * @see javax.faces.component.ActionSource#getActionListener()
     */
    public MethodBinding getActionListener() {
        return _actionListener;
    }

    /**
     * @see javax.faces.component.ActionSource#addActionListener(javax.faces.event.ActionListener)
     */
    public void addActionListener(ActionListener listener) {
        addFacesListener(listener);
    }

    /**
     * @see javax.faces.component.ActionSource#getActionListeners()
     */
    public ActionListener[] getActionListeners() {
        return (ActionListener[]) getFacesListeners(ActionListener.class);
    }

    /**
     * @see javax.faces.component.ActionSource#removeActionListener(javax.faces.event.ActionListener)
     */
    public void removeActionListener(ActionListener listener) {
        removeFacesListener(listener);
    }

    public static final String COMPONENT_TYPE =
            "com.icesoft.faces.DataScroller";
    public static final String COMPONENT_FAMILY = "javax.faces.Panel";
    private static final String DEFAULT_RENDERER_TYPE =
            "com.icesoft.faces.DataScroller";
    private static final boolean DEFAULT_IMMEDIATE = false;
    private static final boolean DEFAULT_VERTICAL = false;
    private String _for = null;
    private Integer _fastStep = null;
    private String _pageIndexVar = null;
    private String _pageCountVar = null;
    private String _rowsCountVar = null;
    private String _displayedRowsCountVar = null;
    private String _firstRowIndexVar = null;
    private String _lastRowIndexVar = null;
    private String _style = null;
    private String _styleClass = null;
    private String _columnClasses = null;
    private Boolean _paginator = null;
    private Integer _paginatorMaxPages = null;
    private Boolean _renderFacetsIfSinglePage = null;
    private Boolean _immediate;
    private Boolean _vertical;
    public static final String FACET_FIRST = "first".intern();
    public static final String FACET_PREVIOUS = "previous".intern();
    public static final String FACET_NEXT = "next".intern();
    public static final String FACET_LAST = "last".intern();
    public static final String FACET_FAST_FORWARD = "fastf".intern();
    public static final String FACET_FAST_REWIND = "fastr".intern();

    public DataPaginator() {
        setRendererType(DEFAULT_RENDERER_TYPE);
    }

    /* (non-Javadoc)
      * @see javax.faces.component.UIComponent#getFamily()
      */
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    /**
     * <p>Set the value of the <code>for</code> property.</p>
     */
    public void setFor(String forValue) {
        _for = forValue;
    }

    /**
     * <p>Return the value of the <code>for</code> property.</p>
     */
    public String getFor() {
        if (_for != null) {
            return _for;
        }
        ValueBinding vb = getValueBinding("for");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>fastStep</code> property.</p>
     */
    public void setFastStep(int fastStep) {
        _fastStep = new Integer(fastStep);
    }

    /**
     * <p>Return the value of the <code>fastStep</code> property.</p>
     */
    public int getFastStep() {
        if (_fastStep != null) {
            return _fastStep.intValue();
        }
        ValueBinding vb = getValueBinding("fastStep");
        Integer v =
                vb != null ? (Integer) vb.getValue(getFacesContext()) : null;
        return v != null ? v.intValue() : Integer.MIN_VALUE;
    }

    /**
     * <p>Set the value of the <code>pageIndexVar</code> property.</p>
     */
    public void setPageIndexVar(String pageIndexVar) {
        _pageIndexVar = pageIndexVar;
    }

    /**
     * <p>Return the value of the <code>pageIndexVar</code> property.</p>
     */
    public String getPageIndexVar() {
        if (_pageIndexVar != null) {
            return _pageIndexVar;
        }
        ValueBinding vb = getValueBinding("pageIndexVar");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>pageCountVar</code> property.</p>
     */
    public void setPageCountVar(String pageCountVar) {
        _pageCountVar = pageCountVar;
    }

    /**
     * <p>Return the value of the <code>pageCountVar</code> property.</p>
     */
    public String getPageCountVar() {
        if (_pageCountVar != null) {
            return _pageCountVar;
        }
        ValueBinding vb = getValueBinding("pageCountVar");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>rowsCountVar</code> property.</p>
     */
    public void setRowsCountVar(String rowsCountVar) {
        _rowsCountVar = rowsCountVar;
    }

    /**
     * <p>Return the value of the <code>rowsCountVar</code> property.</p>
     */
    public String getRowsCountVar() {
        if (_rowsCountVar != null) {
            return _rowsCountVar;
        }
        ValueBinding vb = getValueBinding("rowsCountVar");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>displayedRowsCountVar</code> property.</p>
     */
    public void setDisplayedRowsCountVar(String displayedRowsCountVar) {
        _displayedRowsCountVar = displayedRowsCountVar;
    }

    /**
     * <p>Return the value of the <code>displayedRowsCountVar</code>
     * property.</p>
     */
    public String getDisplayedRowsCountVar() {
        if (_displayedRowsCountVar != null) {
            return _displayedRowsCountVar;
        }
        ValueBinding vb = getValueBinding("displayedRowsCountVar");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>firstRowIndexVar</code> property.</p>
     */
    public void setFirstRowIndexVar(String firstRowIndexVar) {
        _firstRowIndexVar = firstRowIndexVar;
    }

    /**
     * <p>Return the value of the <code>firstRowIndexVar</code> property.</p>
     */
    public String getFirstRowIndexVar() {
        if (_firstRowIndexVar != null) {
            return _firstRowIndexVar;
        }
        ValueBinding vb = getValueBinding("firstRowIndexVar");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>lastRowIndexVar</code> property.</p>
     */
    public void setLastRowIndexVar(String lastRowIndexVar) {
        _lastRowIndexVar = lastRowIndexVar;
    }

    /**
     * <p>Return the value of the <code>lastRowIndexVar</code> property.</p>
     */
    public String getLastRowIndexVar() {
        if (_lastRowIndexVar != null) {
            return _lastRowIndexVar;
        }
        ValueBinding vb = getValueBinding("lastRowIndexVar");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>style</code> property.</p>
     */
    public void setStyle(String style) {
        _style = style;
    }


    /**
     * <p>Return the value of the <code>style</code> property.</p>
     */
    public String getStyle() {
        if (_style != null) {
            return _style;
        }
        ValueBinding vb = getValueBinding("style");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>styleClass</code> property.</p>
     */
    public void setStyleClass(String styleClass) {
        _styleClass = styleClass;
    }

    /**
     * <p>Return the value of the <code>styleClass</code> property.</p>
     */
    public String getStyleClass() {
        return Util.getQualifiedStyleClass(this, 
                                    _styleClass,
                                    CSS_DEFAULT.DATA_PAGINATOR_BASE, 
                                    "styleClass",
                                    isDisabled());
    }

    public String getBaseStyleClass() {
        return CSS_DEFAULT.DATA_PAGINATOR_BASE;
    }

    /**
     * <p>Set the value of the <code>paginator</code> property.</p>
     */
    public void setPaginator(boolean paginator) {
        _paginator = Boolean.valueOf(paginator);
    }

    /**
     * <p>Return the value of the <code>paginator</code> property.</p>
     */
    public boolean isPaginator() {
        if (_paginator != null) {
            return _paginator.booleanValue();
        }
        ValueBinding vb = getValueBinding("paginator");
        Boolean v =
                vb != null ? (Boolean) vb.getValue(getFacesContext()) : null;
        return v != null ? v.booleanValue() : false;
    }

    /**
     * <p>Set the value of the <code>paginatorMaxPages</code> property.</p>
     */
    public void setPaginatorMaxPages(int paginatorMaxPages) {
        _paginatorMaxPages = new Integer(paginatorMaxPages);
    }

    /**
     * <p>Return the value of the <code>paginatorMaxPages</code> property.</p>
     */
    public int getPaginatorMaxPages() {
        if (_paginatorMaxPages != null) {
            return _paginatorMaxPages.intValue();
        }
        ValueBinding vb = getValueBinding("paginatorMaxPages");
        Integer v =
                vb != null ? (Integer) vb.getValue(getFacesContext()) : null;
        return v != null ? v.intValue() : Integer.MIN_VALUE;
    }


    /**
     * <p>Return the value of the <code>paginatorTableClass</code>
     * property.</p>
     */
    public String getPaginatorTableClass() {
        return Util.getQualifiedStyleClass(this,CSS_DEFAULT.PAGINATOR_TABLE_CLASS, isDisabled());

    }

    /**
     * <p>Return the value of the <code>paginatorColumnClass</code>
     * property.</p>
     */
    public String getPaginatorColumnClass() {
        return Util.getQualifiedStyleClass(this,CSS_DEFAULT.PAGINATOR_COLUMN_CLASS, isDisabled());
    }

    /**
     * <p>Return the value of the <code>scrollButtonCellClass</code>
     * property.</p>
     */
    public String getscrollButtonCellClass() {
        return Util.getQualifiedStyleClass(this,CSS_DEFAULT.DATA_PAGINATOR_SCROLL_BUTTON_CELL_CLASS, isDisabled());
    }


    /**
     * <p>Return the value of the <code>paginatorActiveColumnClass</code>
     * property.</p>
     */
    public String getPaginatorActiveColumnClass() {
        return Util.getQualifiedStyleClass(this, CSS_DEFAULT.PAGINATOR_ACTIVE_COLUMN_CLASS, isDisabled());
    }

    /** 
     * <p>Set the value of the <code>renderFacetsIfSinglePage</code>
     * property.</p>
     */
    public void setRenderFacetsIfSinglePage(boolean renderFacetsIfSinglePage) {
        _renderFacetsIfSinglePage = Boolean.valueOf(renderFacetsIfSinglePage);
    }

    /**
     * <p>Return the value of the <code>renderFacetsIfSinglePage</code>
     * property.</p>
     */
    public boolean isRenderFacetsIfSinglePage() {
        if (_renderFacetsIfSinglePage != null) {
            return _renderFacetsIfSinglePage.booleanValue();
        }
        ValueBinding vb = getValueBinding("renderFacetsIfSinglePage");
        Boolean v =
                vb != null ? (Boolean) vb.getValue(getFacesContext()) : null;
        return v != null ? v.booleanValue() : true;
    }

    /**
     * <p>Set the value of the <code>immediate</code> property.</p>
     */
    public void setImmediate(boolean immediate) {
        _immediate = Boolean.valueOf(immediate);
    }

    /**
     * <p>Return the value of the <code>immediate</code> property.</p>
     */
    public boolean isImmediate() {
        if (_immediate != null) {
            return _immediate.booleanValue();
        }
        ValueBinding vb = getValueBinding("immediate");
        Boolean v =
                vb != null ? (Boolean) vb.getValue(getFacesContext()) : null;
        return v != null ? v.booleanValue() : DEFAULT_IMMEDIATE;
    }

    /**
     * <p>Set the value of the <code>vertical</code> property.</p>
     */
    public void setVertical(boolean vertical) {
        _vertical = Boolean.valueOf(vertical);
    }

    /**
     * <p>Return the value of the <code>vertical</code> property.</p>
     */
    public boolean isVertical() {
        if (_vertical != null) {
            return _vertical.booleanValue();
        }
        ValueBinding vb = getValueBinding("vertical");
        Boolean v =
                vb != null ? (Boolean) vb.getValue(getFacesContext()) : null;
        return v != null ? v.booleanValue() : DEFAULT_VERTICAL;
    }

    /**
     * <p>Return the value of the <code>rendered</code> property.</p>
     */
    public boolean isRendered() {
        if (!Util.isRenderedOnUserRole(this)) {
            return false;
        }
        return super.isRendered();
    }

    /**
     * <p>Gets the state of the instance as a <code>Serializable</code>
     * Object.</p>
     */
    public Object saveState(FacesContext context) {
        Object values[] = new Object[21];
        values[0] = super.saveState(context);
        values[1] = _for;
        values[2] = _fastStep;
        values[3] = _pageIndexVar;
        values[4] = _pageCountVar;
        values[5] = _rowsCountVar;
        values[6] = _displayedRowsCountVar;
        values[7] = _firstRowIndexVar;
        values[8] = _lastRowIndexVar;
        values[9] = _style;
        values[10] = _styleClass;
        values[11] = _columnClasses;
        values[12] = _paginator;
        values[13] = _paginatorMaxPages;
        values[14] = _renderFacetsIfSinglePage;
        values[15] = _immediate;
        values[16] = saveAttachedState(context, _actionListener);
        values[17] = _vertical;
        values[18] = renderedOnUserRole;
        values[19] = enabledOnUserRole;
        values[20] = disabled;
        return values;
    }

    /**
     * <p>Perform any processing required to restore the state from the entries
     * in the state Object.</p>
     */
    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        _for = (String) values[1];
        _fastStep = (Integer) values[2];
        _pageIndexVar = (String) values[3];
        _pageCountVar = (String) values[4];
        _rowsCountVar = (String) values[5];
        _displayedRowsCountVar = (String) values[6];
        _firstRowIndexVar = (String) values[7];
        _lastRowIndexVar = (String) values[8];
        _style = (String) values[9];
        _styleClass = (String) values[10];
        _columnClasses = (String) values[11];
        _paginator = (Boolean) values[12];
        _paginatorMaxPages = (Integer) values[13];
        _renderFacetsIfSinglePage = (Boolean) values[14];
        _immediate = (Boolean) values[15];
        _actionListener =
                (MethodBinding) restoreAttachedState(context, values[16]);
        _vertical = (Boolean) values[17];
        renderedOnUserRole = (String)values[18];
        enabledOnUserRole = (String)values[19];
        disabled = (Boolean)values[20];

    }

    /**
     * Sets the dataPaginator to the first page
     */
    public void gotoFirstPage() {
        getUIData().setFirst(0);
    }

    /**
     * Sets the dataPaginator to the previous page
     */
    public void gotoPreviousPage() {
        if (isModelResultSet()) {
            int first = getUIData().getFirst() - getUIData().getRows();
            if (first < 0) {
                first = 0;
            }
            getUIData().setFirst(first);
        } else {
            int previous = getUIData().getFirst() - getUIData().getRows();
            int rowCount = getRowCount();
            if (previous >= 0) {
                if (previous > rowCount) {
                    previous = rowCount;
                }
                getUIData().setFirst(previous);
            }
        }
    }

    /**
     * Sets the dataPaginator to the next page
     */
    public void gotoNextPage() {
        int next = getUIData().getFirst() + getUIData().getRows();
        if (isModelResultSet()) {
            getUIData().setRowIndex(next);
            if (getUIData().isRowAvailable()) {
                getUIData().setFirst(next);
            }
        } else {
            if (next < getUIData().getRowCount()) {
                getUIData().setFirst(next);
            }
        }
    }

    /**
     * Sets the dataPaginator to the (n) number of pages forward, defined by the
     * fastStep property
     */
    public void gotoFastForward() {
        int fastStep = getFastStep();
        if (fastStep <= 0) {
            fastStep = 1;
        }
        int next = getUIData().getFirst() + getUIData().getRows() * fastStep;
        int rowcount = getUIData().getRowCount();
        if (next >= rowcount) {
            next = (rowcount - 1) - ((rowcount - 1) % getUIData().getRows());
        }
        getUIData().setFirst(next);
    }

    /**
     * Sets the dataPaginator to the (n) number of pages back, defined by the
     * fastStep property
     */
    public void gotoFastRewind() {
        int fastStep = getFastStep();
        if (fastStep <= 0) {
            fastStep = 1;
        }
        int previous =
                getUIData().getFirst() - getUIData().getRows() * fastStep;
        if (previous < 0) {
            previous = 0;
        }
        getUIData().setFirst(previous);
    }

    /**
     * Sets the dataPaginator to the last page
     */
    public void gotoLastPage() {
        if (isModelResultSet()) {
            int first = getUIData().getFirst();
            while (true) {
                getUIData().setRowIndex(first + 1);
                if (getUIData().isRowAvailable()) {
                    first++;
                } else {
                    break;
                }
            }
            getUIData().setFirst(first - (first % getUIData().getRows()));
        } else {
            int rowcount = getUIData().getRowCount();
            int rows = getUIData().getRows();
            int delta = rowcount % rows;
            int first = delta > 0 && delta < rows ? rowcount - delta :
                        rowcount - rows;
            if (first >= 0) {
                getUIData().setFirst(first);
            } else {
                getUIData().setFirst(0);
            }
        }
    }

    /**
     * Return a boolean value, whether current page is the last page
     */
    public boolean isLastPage() {
        return (getPageIndex() >= getPageCount()) ? true : false;
    }

    private String enabledOnUserRole = null;
    private String renderedOnUserRole = null;

    /**
     * <p>Set the value of the <code>enabledOnUserRole</code> property.</p>
     */
    public void setEnabledOnUserRole(String enabledOnUserRole) {
        this.enabledOnUserRole = enabledOnUserRole;
    }

    /**
     * <p>Return the value of the <code>enabledOnUserRole</code> property.</p>
     */
    public String getEnabledOnUserRole() {
        if (enabledOnUserRole != null) {
            return enabledOnUserRole;
        }
        ValueBinding vb = getValueBinding("enabledOnUserRole");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>renderedOnUserRole</code> property.</p>
     */
    public void setRenderedOnUserRole(String renderedOnUserRole) {
        this.renderedOnUserRole = renderedOnUserRole;
    }

    /**
     * <p>Return the value of the <code>renderedOnUserRole</code> property.</p>
     */
    public String getRenderedOnUserRole() {
        if (renderedOnUserRole != null) {
            return renderedOnUserRole;
        }
        ValueBinding vb = getValueBinding("renderedOnUserRole");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    private transient Boolean modelResultSet = null;

    public boolean isModelResultSet() {

        if (modelResultSet == null) {
            if (getUIData().getValue() instanceof ResultSet) {
                modelResultSet = Boolean.TRUE;
            } else {
                if (getUIData()
                        .getValue() instanceof javax.faces.model.DataModel) {
                    javax.faces.model.DataModel dataModel =
                            (javax.faces.model.DataModel) getUIData()
                                    .getValue();
                    if (dataModel.getRowCount() == -1) {
                        modelResultSet = Boolean.TRUE;
                        return modelResultSet.booleanValue();
                    }
                }
                modelResultSet = Boolean.FALSE;
            }
        }
        return modelResultSet.booleanValue();
    }
}
