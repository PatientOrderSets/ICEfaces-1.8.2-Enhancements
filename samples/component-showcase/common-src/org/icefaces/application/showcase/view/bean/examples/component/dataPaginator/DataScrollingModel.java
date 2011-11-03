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
package org.icefaces.application.showcase.view.bean.examples.component.dataPaginator;

import org.icefaces.application.showcase.view.bean.examples.component.dataTable.DataTableBase;

import javax.faces.event.ValueChangeEvent;
import java.util.HashMap;

import com.icesoft.faces.component.datapaginator.DataPaginator;

/**
 * <p>The DataScrollingModel class is used to show how a dataTable data
 * scrolling can be modfied.  There are three display modes for the table;
 * default or no scrolling, scrolling and paging.</p>
 * <p>Scrolling is handled by an attribute on the dataTable component along with
 * a scroll height specification of the scroll viewport</p>
 * <p>Paging is handle by the dataPaginator component. </p>
 *
 * @since 1.7
 */
public class DataScrollingModel extends DataTableBase {
    /**
     * dataTable will have no pagging or scrolling enabled.
     */
    public static final String NO_SCROLLING = "none";
    /**
     * dataTable will have scrolling enabled.
     */
    public static final String SCROLLING_SCROLLING = "scrolling";
    /**
     * dataTable will have paging enabled.
     */
    public static final String PAGINATOR_SCROLLING = "paging";

    // currently select scrolling state select by user.
    private String selectedDataScrollMode;
    private static HashMap selectedDataScrollModes;

    // Used in this example to reset the paginator when moving between
    // scrolling views, not needed in normal application development. 
    private DataPaginator dataPaginatorBinding;

    /**
     * Creates a new instance where the efault scrolling is none.
     */
    public DataScrollingModel() {

        selectedDataScrollMode = PAGINATOR_SCROLLING;

        selectedDataScrollModes = new HashMap();

        // default data table setting
        selectedDataScrollModes.put(NO_SCROLLING,
                new DataScrollMode(0, false, false));

        // scrolling data table settings
        selectedDataScrollModes.put(SCROLLING_SCROLLING,
                new DataScrollMode(0, true, false));

        // paging data table settings
        selectedDataScrollModes.put(PAGINATOR_SCROLLING,
                new DataScrollMode(9, false, true));
    }

    public void dataModelChangeListener(ValueChangeEvent event){
        String oldPagingValue = (String)event.getOldValue();

        if ( oldPagingValue != null && oldPagingValue.equals(PAGINATOR_SCROLLING) &&
                dataPaginatorBinding != null){
            dataPaginatorBinding.gotoFirstPage();
        }
    }

    public String getSelectedDataScrollMode() {
        return selectedDataScrollMode;
    }

    public void setSelectedDataScrollMode(String selectedDataScrollMode) {
        this.selectedDataScrollMode = selectedDataScrollMode;
    }

    public HashMap getSelectedDataScrollModes() {
        return selectedDataScrollModes;
    }

    /**
     * Get all possible records from our service layer.
     */
    protected void init() {
        // build employee list form employee service.
        employees = employeeService.getEmployees(50);
    }

    public DataPaginator getDataPaginatorBinding() {
        return dataPaginatorBinding;
    }

    public void setDataPaginatorBinding(DataPaginator dataPaginatorBinding) {
        this.dataPaginatorBinding = dataPaginatorBinding;
    }

    /**
     * Utility method for storing the states of the different scrolling modes.
     * This class is used alone with standard JSF Map notation to retreive
     * specific properties.
     */
    public class DataScrollMode {
        // number of rows to display when paging, default value (0) shows
        // all records.
        private int rows;
        // scrolling enabled
        private boolean scrollingEnabled;
        // paging enabled.
        private boolean pagingEnabled;

        public DataScrollMode(int rows, boolean scrollingEnabled,
                              boolean pagingEnabled) {
            this.rows = rows;
            this.scrollingEnabled = scrollingEnabled;
            this.pagingEnabled = pagingEnabled;
        }

        public int getRows() {
            return rows;
        }

        public void setRows(int rows) {
            this.rows = rows;
        }

        public boolean isScrollingEnabled() {
            return scrollingEnabled;
        }

        public void setScrollingEnabled(boolean scrollingEnabled) {
            this.scrollingEnabled = scrollingEnabled;
        }

        public boolean isPagingEnabled() {
            return pagingEnabled;
        }

        public void setPagingEnabled(boolean pagingEnabled) {
            this.pagingEnabled = pagingEnabled;
        }

    }
}
