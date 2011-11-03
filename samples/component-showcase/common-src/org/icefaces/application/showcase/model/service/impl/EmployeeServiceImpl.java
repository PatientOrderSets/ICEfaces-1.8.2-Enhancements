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
package org.icefaces.application.showcase.model.service.impl;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.icefaces.application.showcase.model.service.EmployeeService;
import org.icefaces.application.showcase.model.entity.Employee;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.lang.reflect.InvocationTargetException;

/**
 * <p>Mock Employee Service layer implementation.  The methods in this class
 * provide data that are used in several of the component showcase examples.</p>
 *
 * @since 1.7
 */
public class EmployeeServiceImpl implements EmployeeService {

    private static final Log logger =
            LogFactory.getLog(EmployeeServiceImpl.class);

    /**
     * Gets a list of Employee records that matches the supplied criteria.
     *
     * @param listSize     number of records that will be retreived from service layer, max
     *                     number of records is 50.
     * @param isDescending true indicates a descending order; otherwise, descending
     * @param sortColumn   column name set will be sorted on.
     * @return list of Employee objects.
     */
    public ArrayList getEmployees(int listSize, boolean isDescending, String sortColumn) {

        ArrayList employees = getEmployees(listSize);
        // sour our list
        Collections.sort(employees, new EmployeeComparator(isDescending, sortColumn));

        return employees;
    }

    /**
     * Gets a list of Employee records that matches the supplied criteria.
     *
     * @param listSize number of records that will be retreived from service layer, max
     *                 number of records is 50.
     * @return list of Employee objects.
     */
    public ArrayList getEmployees(int listSize) {
        ArrayList employees = new ArrayList(listSize);
        Employee destEmployee;
        try{
            int count = 0;
            Employee employee;
            for (int i = 0, max = EMPLOYEE_POOL.size(); i < max; i++){
                if (count >= listSize){
                    break;
                }
                employee = (Employee)EMPLOYEE_POOL.get(i);
                destEmployee = new Employee();
                BeanUtils.copyProperties(destEmployee, employee);
                employees.add(destEmployee);
                count++;
            }
        }catch(IllegalAccessException e){
            logger.error(e);
        }
        catch(InvocationTargetException e){
            logger.error(e);
        }
        return employees;
    }


    /**
     * Utility class to ease the sorting of our awsome employee data. 
     */
    private class EmployeeComparator implements Comparator{

        private boolean isDescending;
        private String sortColumn;

        private EmployeeComparator(boolean descending, String sortColumn) {
            isDescending = descending;
            this.sortColumn = sortColumn;
        }

        /**
         * Compares its two arguments for order.  Returns a negative integer,
         * zero, or a positive integer as the first argument is less than, equal
         * to, or greater than the second.
         *
         * @param o1 the first object to be compared.
         * @param o2 the second object to be compared.
         * @return a negative integer, zero, or a positive integer as the
         *         first argument is less than, equal to, or greater than the
         *         second.
         * @throws ClassCastException if the arguments' types prevent them from
         *                            being compared by this Comparator.
         */
        public int compare(Object o1, Object o2) {
            Employee employee1 = (Employee)o1;
            Employee employee2 = (Employee)o2;
            if (Employee.DEPARTMENT_NAME_COLUMN.equals(sortColumn)) {
                return isDescending ?
                        employee1.getDepartmentName().compareTo(employee2.getDepartmentName()):
                        employee2.getDepartmentName().compareTo(employee1.getDepartmentName());

            }else if (Employee.SUB_DEPARTMENT_NAME_COLUMN.equals(sortColumn)) {
                return isDescending ?
                        employee1.getSubDepartmentName().compareTo(employee2.getSubDepartmentName()):
                        employee2.getSubDepartmentName().compareTo(employee1.getSubDepartmentName());

            }else if (Employee.FIRST_NAME_COLUMN.equals(sortColumn)) {
                return isDescending ?
                        employee1.getFirstName().compareTo(employee2.getFirstName()):
                        employee2.getFirstName().compareTo(employee1.getFirstName());

            }else if (Employee.LAST_NAME_COLUMN.equals(sortColumn)) {
                return isDescending ?
                        employee1.getLastName().compareTo(employee2.getLastName()):
                        employee2.getLastName().compareTo(employee1.getLastName());

            }else if (Employee.PHONE_COLUMN.equals(sortColumn)) {
                return isDescending ?
                        employee1.getPhone().compareTo(employee2.getPhone()):
                        employee2.getPhone().compareTo(employee1.getPhone());

            }else if (Employee.ID_COLUMN.equals(sortColumn)){
                return isDescending ?
                        Integer.valueOf(employee1.getId()).compareTo(Integer.valueOf(employee2.getId())) :
                        Integer.valueOf(employee2.getId()).compareTo(Integer.valueOf(employee1.getId()));
            }else {
                return 0;
            }
        }
    }


    protected static ArrayList EMPLOYEE_POOL = new ArrayList(50);

    // 50 people record based on popular names.
    static {
        EMPLOYEE_POOL.add(new Employee(10, "Western", "Calgary", "Ethan", "Smith", "555-4562"));
        EMPLOYEE_POOL.add(new Employee(15, "Western", "Calgary", "Jacob", "Smith", "555-4563"));
        EMPLOYEE_POOL.add(new Employee(20, "Western", "Calgary", "Logan", "Smith", "555-4564"));
        EMPLOYEE_POOL.add(new Employee(25, "Western", "Calgary", "Benjamin", "Smith", "555-4565"));
        EMPLOYEE_POOL.add(new Employee(30, "Western", "Calgary", "Jack", "Smith", "555-4566"));
        EMPLOYEE_POOL.add(new Employee(35, "Western", "Calgary", "Noah", "Johnson", "555-4567"));
        EMPLOYEE_POOL.add(new Employee(40, "Western", "Calgary", "William", "Johnson", "555-4568"));
        EMPLOYEE_POOL.add(new Employee(45, "Western", "Calgary", "Andrew", "Johnson", "555-4569"));
        EMPLOYEE_POOL.add(new Employee(46, "Western", "Calgary", "Samuel", "Johnson", "555-4570"));
        EMPLOYEE_POOL.add(new Employee(47, "Western", "Victoria", "Joseph", "Johnson", "555-4571"));
        EMPLOYEE_POOL.add(new Employee(50, "Western", "Victoria", "Daniel", "Williams", "555-4572"));
        EMPLOYEE_POOL.add(new Employee(16, "Western", "Victoria", "Anthony", "Williams", "555-4573"));
        EMPLOYEE_POOL.add(new Employee(17, "Western", "Victoria", "Angel", "Williams", "555-4574"));
        EMPLOYEE_POOL.add(new Employee(18, "Western", "Victoria", "Jacob", "Williams", "555-4575"));
        EMPLOYEE_POOL.add(new Employee(100, "Western", "Victoria", "David", "Williams", "555-4576"));
        EMPLOYEE_POOL.add(new Employee(120, "Western", "Victoria", "Andrew", "Brown", "555-4577"));
        EMPLOYEE_POOL.add(new Employee(130, "Western", "Victoria", "Jose", "Brown", "555-4578"));
        EMPLOYEE_POOL.add(new Employee(140, "Central", "Kitchener", "Joshua", "Brown", "555-4579"));
        EMPLOYEE_POOL.add(new Employee(150, "Central", "Kitchener", "Christopher", "Brown", "555-4580"));
        EMPLOYEE_POOL.add(new Employee(160, "Central", "Kitchener", "Matthew", "Brown", "555-4581"));
        EMPLOYEE_POOL.add(new Employee(170, "Central", "Kitchener", "Ryan", "Jones", "555-4582"));
        EMPLOYEE_POOL.add(new Employee(180, "Central", "Kitchener", "Jason", "Jones", "555-4583"));
        EMPLOYEE_POOL.add(new Employee(200, "Central", "Kitchener", "Kevin", "Jones", "555-4584"));
        EMPLOYEE_POOL.add(new Employee(220, "Central", "Kitchener", "Daniel", "Jones", "555-4585"));
        EMPLOYEE_POOL.add(new Employee(210, "Central", "Kitchener", "Matthew", "Jones", "555-4586"));
        EMPLOYEE_POOL.add(new Employee(220, "Central", "Kitchener", "Justin", "Miller", "555-4587"));
        EMPLOYEE_POOL.add(new Employee(230, "Central", "Toronto", "Ethan", "Miller", "555-4588"));
        EMPLOYEE_POOL.add(new Employee(240, "Central", "Toronto", "Eric", "Miller", "555-4589"));
        EMPLOYEE_POOL.add(new Employee(250, "Central", "Toronto", "Andrew", "Miller", "555-4590"));
        EMPLOYEE_POOL.add(new Employee(260, "Central", "Toronto", "Vincent", "Miller", "555-4591"));
        EMPLOYEE_POOL.add(new Employee(270, "Central", "Toronto", "Michael", "Miller", "555-4592"));
        EMPLOYEE_POOL.add(new Employee(280, "Central", "Toronto", "Joseph", "Davis", "555-4593"));
        EMPLOYEE_POOL.add(new Employee(290, "Central", "Toronto", "Daniel", "Davis", "555-4594"));
        EMPLOYEE_POOL.add(new Employee(300, "Central", "Waterloo", "Matthew", "Davis", "555-4595"));
        EMPLOYEE_POOL.add(new Employee(310, "Central", "Waterloo", "David", "Davis", "555-4596"));
        EMPLOYEE_POOL.add(new Employee(320, "Central", "Waterloo", "Nicholas", "Davis", "555-4597"));
        EMPLOYEE_POOL.add(new Employee(330, "Central", "Waterloo", "Jack", "Garcia", "555-4598"));
        EMPLOYEE_POOL.add(new Employee(340, "Eastern", "Ottawa", "Alexander", "Garcia", "555-4590"));
        EMPLOYEE_POOL.add(new Employee(350, "Eastern", "Ottawa", "Benjamin", "Garcia", "555-4591"));
        EMPLOYEE_POOL.add(new Employee(360, "Eastern", "Ottawa", "Jacob", "Garcia", "555-4592"));
        EMPLOYEE_POOL.add(new Employee(370, "Eastern", "Ottawa", "Angel", "Garcia", "555-4593"));
        EMPLOYEE_POOL.add(new Employee(380, "Eastern", "Ottawa", "Christopher", "Rodriguez", "555-4594"));
        EMPLOYEE_POOL.add(new Employee(390, "Eastern", "Ottawa", "Justin", "Rodriguez", "555-4595"));
        EMPLOYEE_POOL.add(new Employee(400, "Eastern", "Ottawa", "Jayden", "Rodriguez", "555-4596"));
        EMPLOYEE_POOL.add(new Employee(410, "Eastern", "Ottawa", "Anthony", "Rodriguez", "555-4597"));
        EMPLOYEE_POOL.add(new Employee(420, "Eastern", "Halifax", "Joshua", "Rodriguez", "555-4598"));
        EMPLOYEE_POOL.add(new Employee(430, "Eastern", "Halifax", "Kevin", "Watson", "555-4599"));
        EMPLOYEE_POOL.add(new Employee(440, "Eastern", "Halifax", "David", "Wilson", "555-4600"));
        EMPLOYEE_POOL.add(new Employee(450, "Eastern", "Halifax", "Daniel", "Wilson", "555-4601"));
        EMPLOYEE_POOL.add(new Employee(460, "Eastern", "Montreal", "Brandon", "Wilson", "555-4602"));

    }
}
