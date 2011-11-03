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
package org.icefaces.application.showcase.view.bean.examples.component.commandSortHeader;

import org.icefaces.application.showcase.view.bean.examples.component.dataTable.DataTableBase;
import org.icefaces.application.showcase.view.bean.BeanNames;
import org.icefaces.application.showcase.model.entity.Employee;
import org.icefaces.application.showcase.util.FacesUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.event.PhaseListener;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;

/**
 *
 */
public class SortHeaderModel extends DataTableBase implements PhaseListener {

    private static final Log logger =
            LogFactory.getLog(SortHeaderModel.class);

    private boolean descending = true;
    private boolean oldDescending = descending;
    private String columnName = Employee.DEPARTMENT_NAME_COLUMN;
    private String oldColumnName = columnName;


    protected void init() {
        // build employee list form employee service.
        employees = employeeService.getEmployees(50, descending,
                columnName);
    }

    /**
     * <p>Handle a notification that the processing for a particular
     * phase of the request processing lifecycle is about to begin.</p>
     * <p>Checks if data model flags are dirty and call service layer to
     * refresh data if necessary.</p>
     */
    public void beforePhase(PhaseEvent event) {

        SortHeaderModel sortHeaderModel =
                (SortHeaderModel)FacesUtils.getManagedBean(
                        BeanNames.SORT_HEADER_MODEL);

        if ((sortHeaderModel.descending != sortHeaderModel.oldDescending) ||
                (!sortHeaderModel.columnName.equals(sortHeaderModel.oldColumnName))){
            sortHeaderModel.init();
            logger.debug("SortHeaderModel - dataRefresh ");
            // reset dirty flags.
            sortHeaderModel.oldDescending = sortHeaderModel.descending;
            sortHeaderModel.oldColumnName = sortHeaderModel.columnName;
        }
    }

    /**
     * <p>Handle a notification that the processing for a particular
     * phase has just been completed.</p>
     */
    public void afterPhase(PhaseEvent event) {}

    /**
     * <p>Return the identifier of the request processing phase during
     * which this listener is interested in processing {@link javax.faces.event.PhaseEvent}
     * events.  Legal values are the singleton instances defined by the
     * {@link javax.faces.event.PhaseId} class, including <code>PhaseId.ANY_PHASE</code>
     * to indicate an interest in being notified for all standard phases.</p>
     */
    public PhaseId getPhaseId() {
        return PhaseId.RENDER_RESPONSE;
    }

    public boolean isDescending() {
        return descending;
    }

    public void setDescending(boolean descending) {
        oldDescending = this.descending;
        this.descending = descending;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        oldColumnName = this.columnName;
        this.columnName = columnName;
    }
}
