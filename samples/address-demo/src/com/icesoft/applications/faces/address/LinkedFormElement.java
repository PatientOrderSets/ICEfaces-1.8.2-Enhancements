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

import javax.faces.component.UISelectOne;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Specialized FormElement used for the city, state, and zip fields. Depending
 * on the outcomes of the various city, state, and zip ValueChangeEvents,
 * LinkedFormElements may display as an inputText box or a selectOneMenu (the
 * latter is only shown temporarily).
 */
public class LinkedFormElement extends FormElement {

    //component display control - toggle between textInput and selectOneMenu
    protected boolean selectRendered = false;

    //hold the values for drop-down list
    private ArrayList select;

    //reference to select element
    private UISelectOne selectChoice = null;

    /**
     * Resets the value to "" and disables the drop-down list.
     */
    public void reset() {
        //stored String value
        setValue("");

        //drop-down list choice
        if (selectChoice != null)
            selectChoice.setValue("");

        //drop-down list
        selectRendered = false;
        setSelect(new ArrayList());
    }

    /**
     * Sets the element value. If the value is non-null its whitespace is
     * removed and the set flag is marked true; If the value is null it is set
     * to "", the set flag is marked false, and the element image is set to
     * blank.
     *
     * @param value The value to be stored.
     */
    public void setValue(String value) {

        if (value != null) {

            if (value.length() > 0) {
                this.value = value.trim();
                set = true;
                return;
            }
        }

        this.value = "";
        setImage(IMAGE_BLANK);
        set = false;
    }

    /**
     * Determine whether or not the drop-down list is rendered.
     *
     * @return the status of selectRendered
     */
    public boolean getSelectRendered() {
        return this.selectRendered;
    }

    /**
     * Set the rendered status of the drop-down list.
     *
     * @param selectRendered the new rendered status
     */
    public void setSelectRendered(boolean selectRendered) {
        this.selectRendered = selectRendered;
    }

    /**
     * Determine the rendered status of the inputText.
     *
     * @return the status of inputRendered
     * @see #getSelectRendered()
     */
    public boolean getInputRendered() {
        return !this.selectRendered;
    }

    /**
     * Get the ArrayList of choices for the drop-down list.
     *
     * @return the selectItem ArrayList of choices
     */
    public Collection getSelect() {
        return select;
    }

    /**
     * Populate the drop-down list choices.
     *
     * @param select the plain ArrayList of choices.
     */
    public void setSelect(ArrayList select) {
        this.select = (ArrayList) getListAsSelectItems(select);
    }

    /**
     * Binding used to prevent selectOneMenu from remembering previous choice.
     *
     * @param selectChoice
     */
    public void setSelectChoice(UISelectOne selectChoice) {
        this.selectChoice = selectChoice;
    }

    /**
     * Binding used to prevent selectOneMenu from remembering previous choice.
     *
     * @return selected choice.
     */
    public UISelectOne getSelectChoice() {
        return selectChoice;
    }

    /**
     * Creates an ArrayList of SelectItem's suitable for a drop-down selection
     * component.
     *
     * @param list The list of choices
     * @return selectItems The list of SelectItem choices
     */
    public Collection getListAsSelectItems(ArrayList list) {

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
}