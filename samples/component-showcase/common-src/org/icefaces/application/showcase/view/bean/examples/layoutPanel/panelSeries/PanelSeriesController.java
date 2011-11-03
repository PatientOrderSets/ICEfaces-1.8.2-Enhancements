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
package org.icefaces.application.showcase.view.bean.examples.layoutPanel.panelSeries;

import org.icefaces.application.showcase.view.bean.examples.component.dataTable.DataTableBase;

import javax.faces.event.ActionEvent;
import java.io.Serializable;

/**
 * <p>The PanelSeriesController is a simple class that maintains an list of
 * employees. The class also has two methods which either add or removed
 * employees from the list. </p>
 * <p>The mock employe service layer is used to retreive employee records.</p>
 *
 * @see org.icefaces.application.showcase.view.bean.examples.component.dataTable.DataTableBase
 * @since 1.7
 */
public class PanelSeriesController extends DataTableBase implements Serializable {

    // start off with 3 employees.
    private int employeeCount = 3;

    /**
     * Adds a new employee to the list of employees.
     * @param event jsf action event
     */
    public void addEmployee(ActionEvent event){
        employeeCount++;
        employees = employeeService.getEmployees(employeeCount);
    }

    /**
     * Removes a new employee to the list of employees.
     * @param event jsf action event
     */
    public void removeEmployee(ActionEvent event){
        employeeCount--;
        employees = employeeService.getEmployees(employeeCount);
    }

    public int getEmployeeCount(){
        return employeeCount;
    }

    protected void init() {
        employees = employeeService.getEmployees(employeeCount); 
    }

}
