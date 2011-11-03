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
package com.icesoft.applications.faces.address;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * FormElementContainer references the FormElement objects. It maintains an
 * ArrayList of all of the elements to use for iteration in the FormProcessor.
 *
 * @see AddressFormProcessor
 */
public class FormElementContainer {

    //form text elements
    private FormElement title, firstName, lastName;
    private LinkedFormElement city, state, zip;


    //submit button
    private SubmitButton submit;

    private ArrayList titles, componentList;


    /**
     * Instantiates the FormElements and adds them to an ArrayList.
     */
    public FormElementContainer() {

        titles = new ArrayList();
        titles.add("");
        titles.add("Ms.");
        titles.add("Mrs.");
        titles.add("Mr.");
        titles.add("Dr.");

        //instantiate the form elements
        title = new FormElement();
        firstName = new FormElement();
        lastName = new FormElement();
        city = new LinkedFormElement();
        state = new LinkedFormElement();
        zip = new LinkedFormElement();
        submit = new SubmitButton();

        /* add the form elements to an array to make iteration 
        possible for reset() and submit.status() */
        componentList = new ArrayList();
        componentList.add(title);
        componentList.add(firstName);
        componentList.add(lastName);
        componentList.add(city);
        componentList.add(state);
        componentList.add(zip);
    }


    /**
     * Resets the FormElement's to their original state.
     */
    public void reset() {

        //diable submit button
        submit.setStatus(false);

        Iterator i = componentList.iterator();
        FormElement current;

        //loop through array of form elements and reset each one
        while (i.hasNext()) {

            current = (FormElement) i.next();
            current.reset();
        }
    }

    /**
     * List of titles (Mr., Ms., Dr., etc.).
     *
     * @return the the list of titles
     */
    public ArrayList getTitles() {
        return getListAsSelectItems(titles);
    }


    /**
     * Returns an ArrayList of SelectItem's suitable for a drop-down selection
     * component.
     *
     * @param list The list of choices
     * @return selectItems The list of SelectItem choices
     */
    public ArrayList getListAsSelectItems(ArrayList list) {

        //empty list
        if (list == null) {
            return new ArrayList();
        }
        //non-empty
        ArrayList selectItems = new ArrayList(list.size());
        String val;

        //each entry must be added appropriately for drop-down use
        for (int index = 0; index < list.size(); index++) {
            val = (String) list.get(index);            
            selectItems.add(new SelectItem(val, val, ""));
        }
        return selectItems;
    }

    /*
    *  Form element object getters and setters.
    *  The actual Strings containing the values are stored
    *  internally in each object.
    */

    public FormElement getTitle() {
        return title;
    }

    public void setTitle(FormElement title) {
        this.title = title;
    }

    public FormElement getFirstName() {
        return firstName;
    }

    public void setFirstName(FormElement firstName) {
        this.firstName = firstName;
    }

    public FormElement getLastName() {
        return lastName;
    }

    public void setLastName(FormElement lastName) {
        this.lastName = lastName;
    }

    public LinkedFormElement getCity() {
        return city;
    }

    public void setCity(LinkedFormElement city) {
        this.city = city;
    }

    public LinkedFormElement getState() {
        return state;
    }

    public void setState(LinkedFormElement state) {
        this.state = state;
    }

    public LinkedFormElement getZip() {
        return zip;
    }

    public void setZip(LinkedFormElement zip) {
        this.zip = zip;
    }

    public SubmitButton getSubmit() {
        return submit;
    }

    public void setSubmit(SubmitButton submit) {
        this.submit = submit;
    }

    /**
     * Get the list of form elements to use for iteration.
     *
     * @return the list of form element objects
     */
    public ArrayList getComponentList() {
        return componentList;
    }

    /**
     * Set the new list of form elements. Contains submit, first name, last
     * name, city, state, and zip objects.
     *
     * @param componentList the list of form element objects
     */
    public void setComponentList(ArrayList componentList) {
        this.componentList = componentList;
    }
}