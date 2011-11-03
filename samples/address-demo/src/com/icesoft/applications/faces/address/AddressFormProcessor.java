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

import com.icesoft.faces.component.ext.HtmlCommandButton;
import com.icesoft.faces.component.ext.HtmlInputText;
import com.icesoft.faces.component.ext.HtmlSelectOneMenu;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UISelectOne;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

/**
 * Handles all of the inter-field form validation and ValueChangeEvents. Stores
 * calculated values for city, state, and zip, and injects them when PhaseSync
 * calls the inject() method
 *
 * @see PhaseSync, FormElementContainer
 */
public class AddressFormProcessor {

    //access to the address database and associated methods (static)
    static {
        dataBean = new MatchAddressDB();
    }

    private static final String NOT_IN_LIST = "";

    //form elements - title, first name, last name, city, state, zip
    private ArrayList componentList;

    //actual city, state, and zip values
    private LinkedFormElement city, state, zip;

    //injector values
    private String newCity, newState, newZip;

    private SubmitButton submit;
    protected static MatchAddressDB dataBean;

    // component bindings
    private HtmlInputText firstNameText = null;
    private HtmlInputText lastNameText = null;
    private HtmlInputText cityText = null;
    private HtmlInputText stateText = null;
    private HtmlInputText zipText = null;

    private UISelectOne selectTitle = null;
    private UISelectOne selectCity = null;
    private UISelectOne selectState = null;
    private UISelectOne selectZip = null;

    private HtmlCommandButton submitButton = null;

    private boolean addressComplete = false;


    public HtmlCommandButton getSubmitButton() {
        return submitButton;
    }

    public void setSubmitButton(HtmlCommandButton submitButton) {
        this.submitButton = submitButton;
    }

    /**
     * Makes references to the FormElement objects.
     *
     * @see FormElementContainer
     */
    public AddressFormProcessor(FormElementContainer elementContainer) {

        //this allows the use of an iterator over all of the form elements
        componentList = elementContainer.getComponentList();

        //the submit button element
        submit = elementContainer.getSubmit();

        //city, state, and zip elements
        city = elementContainer.getCity();
        state = elementContainer.getState();
        zip = elementContainer.getZip();

        //temporary storage for injector values
        newCity = city.getValue();
        newState = state.getValue();
        newZip = zip.getValue();

    }

    /**
     * Clears the temporary values.
     */
    public void reset() {
        newCity = newState = newZip = "";
        submit.setStatus(false);
        addressComplete = false;
        city.reset();
        state.reset();
        zip.reset();
        selectState.setValue("");
        selectCity.setValue("");
        selectZip.setValue("");
        selectTitle.setValue("");
    }

    /**
     * Called by PhaseSync to inject the calculated temporary values (newCity,
     * newState, newZip) into their respective FormElementContainer objects each
     * cycle.
     *
     * @see PhaseSync
     */
    public void inject() {

        //assign the temporary values to the actual city, state, and zip values
        city.setValue(newCity);
        state.setValue(newState);
        zip.setValue(newZip);
    }

    /**
     * Determines whether or not to activate the submit button. This method is
     * called by PhaseSync to determine the state of the submit button each JSF
     * cycle.
     *
     * @see PhaseSync
     */
    public void updateSubmitButton() {

        boolean status = true;

        Iterator i = componentList.iterator();
        FormElement current;

        //one un-set element will make status false
        while (i.hasNext()) {

            current = (FormElement) i.next();
            if (!current.getIsSet()) {
                status = false;
            }
        }
        //all the elements are set, determine if the address is valid
        if (!isAddressValid()) {
            status = false;

            // clear all of the warning flags if the address is valid
        } else {

            // reset the iterator
            i = componentList.iterator();
            while (i.hasNext()) {

                current = (FormElement) i.next();
                current.setImage(FormElement.IMAGE_BLANK);
            }
        }
        submit.setStatus(status);
        if ((status) && !addressComplete) {
            addressComplete = true;
            submitButton.requestFocus();
        }
    }

    // recursive function keeps recursing unto a form is found
    private UIForm getForm(UIComponent uiComponent) {
        UIComponent form = uiComponent.getParent();
        if (form instanceof UIForm) {
            return (UIForm) form;
        } else {
            return getForm(form);
        }
    }

    /**
     * Calculates form values based on the new city value.
     *
     * @param event the event caused by the form change
     */
    public void cityChanged(ValueChangeEvent event) {

        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage msg;
        UIComponent component = event.getComponent();

        String newValue = (String) event.getNewValue();

        city.setImage(LinkedFormElement.IMAGE_PROGRESS);

        if (newValue == null || newValue.equals("")) {
            newValue = "";
            newCity = newValue;
            return;
        }

        newCity = newValue;

        city.setImage(LinkedFormElement.IMAGE_PROGRESS);

        boolean stateAutoFilled = false;
        newCity = fixCapitalization(newCity);

        //process city against zip
        MatchZip zipDb;

        if ((null != newZip) && (newZip.length() > 0)) {

            //see if we get a valid zip
            zipDb = dataBean.getZip(newZip);

            if (zipDb != null) {

                //city must match zip to be valid
                if (!zipDb.getCity().equals(newCity)) {
                    msg = new FacesMessage("City not found with Zip "
                                           + newZip + ".  Our best guess is "
                                           + zipDb.getCity() + ", " +
                                           zipDb.getState());

                    city.setImage(LinkedFormElement.IMAGE_ALERT);
                    context.addMessage(component.getClientId(context), msg);
                    return;
                } else {
                    //zip and city match
                    if (newCity.length() == 0) {
                        //we can autofill the state
                        newCity = zipDb.getState();
                        city.setImage(LinkedFormElement.IMAGE_BLANK);
                        stateAutoFilled = true;
                    }
                }
            }
        }

        //process city against state
        if (!stateAutoFilled && (null != newState)
            && newState.length() > 0) {
            //see if we get a valid state
            MatchState stateDb = dataBean.getState(newState);

            if (stateDb != null) {
                MatchCity bestGuessCity = stateDb.getClosestCity(newCity);
                if (bestGuessCity.isMatch()) {

                    if (newZip.length() == 0) {
                        //autofill zip if there is only one
                        autoFillZip(bestGuessCity);
                    }
                } else {
                    //city and state don't match
                    msg = new FacesMessage("City not found in "
                                           + newState + ". Our best guess is "
                                           + bestGuessCity.getCity() + ", "
                                           + bestGuessCity.getState());
                    city.setImage(LinkedFormElement.IMAGE_ALERT);
                    context.addMessage(component.getClientId(context), msg);
                    return;
                }
            }
        }

        if ((newZip == null) || (newZip.length() == 0)
                                && (newState == null) || newState.length() == 0)
        {
            //no zip or state, so check for best match
            ArrayList possibleCities = dataBean.getClosestCity(newCity);
            MatchCity possibleCity = (MatchCity) possibleCities.get(0);

            if (possibleCity.isMatch()) {
                //exact city match
                newCity = possibleCity.getCity();

                if (possibleCities.size() == 1) {
                    //only one match, so autofill state and possibly zip
                    newState = possibleCity.getState();

                    //autofill zips
                    autoFillZip(possibleCity);
                } else {
                    //multiple states
                    ArrayList stateSelect =
                            new ArrayList(possibleCities.size() + 1);
                    stateSelect.add(NOT_IN_LIST);
                    for (int i = 0; i < possibleCities.size(); i++) {
                        stateSelect.add((
                                (MatchCity) possibleCities.get(i)) .getState());
                    }
                    //drop-down selection
                    state.setSelect(stateSelect);
                    state.setSelectRendered(true);
                    ((HtmlSelectOneMenu) selectState).requestFocus();
                }
            } else {
                //don't have exact match, so suggest our best guess
                msg = new FacesMessage("City is not valid. Our best guess is "
                                       + possibleCity.getCity() + ", "
                                       + possibleCity.getState());
                city.setImage(LinkedFormElement.IMAGE_ALERT);
                context.addMessage(component.getClientId(context), msg);
            }
        }
    }

    public void titleSelectChanged(ValueChangeEvent event) {
        firstNameText.requestFocus();
    }

    /**
     * Calculates form values based on the new selected city value.
     *
     * @param event the event caused by the form change
     */
    public void citySelectChanged(ValueChangeEvent event) {

        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage msg;
        UIComponent component = event.getComponent();
        String newValue = (String) event.getNewValue();

        city.setImage(LinkedFormElement.IMAGE_PROGRESS);

        if ((newValue == null) || "".equals(newValue)) {
            return;
        }

        if (newValue.equals(NOT_IN_LIST)) {
            //revert to blank text input field
            newCity = "";
        } else {
            newCity = newValue;
        }

        //disable the drop-down list
        city.setSelectRendered(false);

        //process city against zip
        MatchZip zipDb;

        if ((null != newZip) && (newZip.length() > 0)) {
            //see if we get a valid zip
            zipDb = dataBean.getZip(newZip);

            if (zipDb != null) {
                //city must match zip to be valid
                if (!zipDb.getCity().equals(newCity)) {
                    msg = new FacesMessage("City not found with Zip "
                                           + newZip + ".  Our best guess is "
                                           + zipDb.getCity());
                    city.setImage(LinkedFormElement.IMAGE_ALERT);
                    context.addMessage(component.getClientId(context), msg);
                    return;
                }
            }
        } else {
            //have a city and state, so see if we can autofill zip
            MatchState stateDb = dataBean.getState(newState);
            if (stateDb != null) {
                MatchCity cityDb = stateDb.getCity(newCity);
                if (cityDb != null) {
                    autoFillZip(cityDb);
                }
            }
        }
        city.setImage(LinkedFormElement.IMAGE_BLANK);
        city.reset();
    }


    /**
     * Calculates form values based on the new state value.
     *
     * @param event the event caused by the form change
     */
    public void stateChanged(ValueChangeEvent event) {

        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage msg;
        UIComponent component = event.getComponent();
        String newValue = (String) event.getNewValue();

        state.setImage(LinkedFormElement.IMAGE_PROGRESS);

        //check for null pointers
        if (newValue == null) {
            if ((newState == null) || ("".equals(newState))) {
                return;
            } else {
                newState = "";
                return;
            }
        } else {
            if (newValue.equals(newState)) {
                return;
            }
        }

        //set proper capitalization
        newState = newValue.toUpperCase();

        //process state against zip
        boolean cityAutoFilled = false;
        MatchZip zipDb;

        if ((null != newZip) && (newZip.length() > 0)) {
            //see if we get a valid zip
            zipDb = dataBean.getZip(newZip);

            if (zipDb != null) {

                //state must match zip to be valid
                if (!zipDb.getState().equals(newState)) {
                    msg = new FacesMessage("State not found with Zip "
                                           + newZip + ".  Our best guess is "
                                           + zipDb.getState());
                    state.setImage(LinkedFormElement.IMAGE_ALERT);
                    context.addMessage(component.getClientId(context), msg);
                    return;
                } else {
                    if (newCity.length() == 0) {
                        //we can autofill the city
                        newCity = zipDb.getCity();
                        cityAutoFilled = true;
                    }
                }
            }
        }

        //process state against city
        if (!cityAutoFilled && newCity.length() > 0) {
            //see if we get a valid city
            ArrayList cities = dataBean.getCity(newCity);

            if (cities != null) {

                //check each city for closest matching state
                TreeMap states = new TreeMap();
                Iterator itor = cities.iterator();

                while (itor.hasNext()) {
                    MatchCity thisCity = (MatchCity) itor.next();
                    states.put(thisCity.getState(), thisCity);
                }
                MatchCity bestGuessCity =
                        (MatchCity) dataBean.getClosestMatch(newState, states);

                //exact match
                if (bestGuessCity.isMatch()) {

                    if (newZip.length() == 0) {
                        //autofill zips if there's only one of them
                        autoFillZip(bestGuessCity);
                    }
                } else {
                    //state and city don't match
                    msg = new FacesMessage("State not valid for "
                                           + newCity + ". Our best guess is "
                                           + bestGuessCity.getState());
                    state.setImage(LinkedFormElement.IMAGE_ALERT);
                    context.addMessage(component.getClientId(context), msg);
                }
            }
        }

        if (((newZip == null) || (newZip.length() == 0))
            && ((newCity == null) || (newCity.length() == 0))) {
            //no zip or city, so check for best match
            MatchState possibleState = dataBean.getClosestState(newState);

            //exact match
            if (possibleState.isMatch()) {
                newState = possibleState.getState();

                //build a city drop-down selection list
                String cities[] = possibleState.getCitiesAsStrings();
                ArrayList citySelect = new ArrayList(cities.length + 1);
                citySelect.add(NOT_IN_LIST);
                for (int i = 0; i < cities.length; i++) {
                    citySelect.add(cities[i]);
                }
                //activate the city drop-down selection
                city.setSelect(citySelect);
                city.setSelectRendered(true);
                ((HtmlSelectOneMenu) selectCity).requestFocus();
            } else {
                // Don't have exact match so fill in our best guess;
                msg = new FacesMessage("State is not valid. Our best guess is "
                                       + possibleState.getState());
                state.setImage(LinkedFormElement.IMAGE_ALERT);
                context.addMessage(component.getClientId(context), msg);
            }
        }
    }

    /**
     * Calculates form values based on the new selected state value.
     *
     * @param event the event caused by the form change
     */
    public void stateSelectChanged(ValueChangeEvent event) {

        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage msg;
        UIComponent component = event.getComponent();
        String newValue = (String) event.getNewValue();

        state.setImage(LinkedFormElement.IMAGE_PROGRESS);

        if ((newValue == null) || "".equals(newValue)) {
            return;
        }

        if (newValue.equals(NOT_IN_LIST)) {
            //revert to blank text input field
            newState = "";
        } else {
            newState = newValue;
        }
        state.setSelectRendered(false);

        //process state against zip
        MatchZip zipDb;

        if ((null != newZip) && (newZip.length() > 0)) {
            //see if we get a valid zip
            zipDb = dataBean.getZip(newZip);

            if (zipDb != null) {

                //state must match zip to be valid
                if (!zipDb.getState().equals(newState)) {
                    msg = new FacesMessage("State not found with Zip "
                                           + newZip + ".  Our best guess is "
                                           + zipDb.getState());
                    state.setImage(LinkedFormElement.IMAGE_ALERT);
                    context.addMessage(component.getClientId(context), msg);
                }
            }
        } else {
            //have city and state, so see if we can autofill zip
            MatchState stateDb = dataBean.getState(newState);
            if (stateDb != null) {
                MatchCity cityDb = stateDb.getCity(newCity);
                if (cityDb != null) {
                    autoFillZip(cityDb);
                }
            }
        }

        state.reset();
    }

    /**
     * Calculates form values based on the new zip value.
     *
     * @param event the event caused by the form change
     */
    public void zipChanged(ValueChangeEvent event) {

        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage msg;
        UIComponent component = event.getComponent();
        String newValue = (String) event.getNewValue();

        zip.setImage(LinkedFormElement.IMAGE_PROGRESS);

        if (newValue == null) {
            if ((newZip == null) || ("".equals(newZip))) {
                return;
            } else {
                newZip = "";
                return;
            }
        } else {
            if (newValue.equals(newZip)) {
                return;
            }
            if (newValue.length() < 1) {
                //if zip is blank do nothing
                newZip = "";
                return;
            }
        }

        newValue = newValue.toUpperCase();
        newZip = newValue;

        //process zip against state
        boolean cityAutoFilled = false;
        MatchState stateDb;

        if (newState.length() > 0) {
            //see if we get a valid state
            stateDb = dataBean.getState(newState);

            if (stateDb != null) {
                //zip must match state to be valid
                MatchZip possibleZip = stateDb.getClosestZip(newValue);

                if (!possibleZip.isMatch()) {
                    //zip doesn't match state
                    msg = new FacesMessage("Zip not valid in  "
                                           + newState + ".  Our best guess is "
                                           + possibleZip.getZip() + " ("
                                           + possibleZip.getCity() + ")");
                    zip.setImage(LinkedFormElement.IMAGE_ALERT);
                    context.addMessage(component.getClientId(context), msg);
                    return;
                } else {
                    //state and zip match
                    if (newCity.length() == 0) {
                        //we can auto fill the city;
                        newCity = possibleZip.getCity();
                        cityAutoFilled = true;
                    }
                }
            }
        }

        //process zip against city
        if (!cityAutoFilled && newCity.length() > 0) {
            //see if we get a valid city
            ArrayList cities = dataBean.getCity(newCity);
            if (cities != null) {

                //check each city for closest matching zip
                TreeMap zips = new TreeMap();

                Iterator itor = cities.iterator();

                while (itor.hasNext()) {
                    MatchCity thisCity = (MatchCity) itor.next();
                    MatchZip thisZip = thisCity.getClosestZip(newValue);
                    zips.put(thisZip.getZip(), thisZip);
                }

                MatchZip bestGuessZip =
                        (MatchZip) dataBean.getClosestMatch(newValue, zips);

                if (bestGuessZip.isMatch()) {
                    //zip and city match
                    newZip = newValue;

                    if (newState.length() == 0) {
                        //autofill states if it is blank;
                        newState = bestGuessZip.getState();
                    }
                } else {
                    //zip and city don't match
                    msg = new FacesMessage("Zip not valid for "
                                           + newCity + ". Our best guess is "
                                           + bestGuessZip.getZip());

                    zip.setImage(LinkedFormElement.IMAGE_ALERT);
                    context.addMessage(component.getClientId(context), msg);
                    return;
                }
            }
        }

        if (newState.length() == 0 && newCity.length() == 0) {
            //no state or city, so check for best match
            MatchZip possibleZip = dataBean.getClosestZip(newValue);

            if (possibleZip.isMatch()) {
                //exact zip match, so set zip, state, and city
                newZip = possibleZip.getZip();
                newState = possibleZip.getState();
                newCity = possibleZip.getCity();

            } else {
                //don't have exact match so fill in our best guess
                msg = new FacesMessage(
                        "Zip is not valid. Our best guess is "
                        + possibleZip.getZip() + " ("
                        + possibleZip.getCity() + ", "
                        + possibleZip.getState() + ")");

                zip.setImage(LinkedFormElement.IMAGE_ALERT);
                context.addMessage(component.getClientId(context), msg);
            }
        }
    }


    /**
     * Calculates form values based on the new selected zip value.
     *
     * @param event the event caused by the form change
     */
    public void zipSelectChanged(ValueChangeEvent event) {

        String newValue = (String) event.getNewValue();
        zip.setImage(LinkedFormElement.IMAGE_PROGRESS);

        if ((newValue == null) || "".equals(newValue)) {
            newZip = "";
            return;
        }

        if (newValue.equals(NOT_IN_LIST)) {
            //revert to blank text input
            newZip = "";
        } else {
            newZip = newValue;
        }
        if (!NOT_IN_LIST.equals(newValue)) {
            zip.setSelectRendered(false);
        }

        zip.setImage(LinkedFormElement.IMAGE_BLANK);
        zip.reset();
    }

    /**
     * Generates a list of zip codes for the provided city and either fills in
     * the zip field (one zip) or populates and enables the zip drop-down menu
     * (multiple zips).
     *
     * @param city the city of which to check for zips
     */
    private void autoFillZip(MatchCity city) {

        String myZips[] = city.getZipsAsStrings();


        if (myZips.length == 1) {
            //only one zip
            newZip = myZips[0];

            //disable drop-down menu
            zip.setSelectRendered(false);
        } else {
            //multiple zips
            ArrayList zipSelect = new ArrayList(myZips.length + 1);
            zipSelect.add(NOT_IN_LIST);
            for (int i = 0; i < myZips.length; i++) {
                zipSelect.add(myZips[i]);

            }
            //enable drop-down menu
            zip.setSelect(zipSelect);
            zip.setSelectRendered(true);
            ((HtmlSelectOneMenu) selectZip).requestFocus();
        }
    }

    /**
     * Determines whether the city, state, and zip values describe an entry in
     * the database.
     *
     * @return whether or not the address is valid
     * @see SubmitButton, PhaseSync
     */
    public boolean isAddressValid() {

        MatchZip zipDb = dataBean.getZip(zip.getValue());

        return (zipDb != null && zipDb.getCity().equals(city.getValue()) &&
                zipDb.getState().equals(state.getValue()));
    }

    /**
     * Removes extra whitespace and capitalizes the first letter of each word in
     * the provided string.
     *
     * @param inString
     * @return the string with proper capitalization
     */
    public static String fixCapitalization(String inString) {

        StringBuffer str = new StringBuffer(inString.trim().toLowerCase());

        //empty string
        if (str.length() == 0) {
            return str.toString();
        }
        Character nextChar;
        int i = 0;
        nextChar = new Character(str.charAt(i));

        while (i < str.length()) {
            //capitalize the first character
            str.setCharAt(i++, Character.toUpperCase(nextChar.charValue()));

            if (i == str.length()) {
                return str.toString();
            }

            //look for whitespace
            nextChar = new Character(str.charAt(i));
            while (i < str.length() - 2
                   && !Character.isWhitespace(nextChar.charValue())) {
                nextChar = new Character(str.charAt(++i));
            }

            if (!Character.isWhitespace(nextChar.charValue())) {
                //not whitespace, we must be at end of string
                return str.toString();
            }

            //remove all but first whitespace
            nextChar = new Character(str.charAt(++i));
            while (i < str.length()
                   && Character.isWhitespace(nextChar.charValue())) {
                str.deleteCharAt(i);
                nextChar = new Character(str.charAt(i));
            }
        }
        return str.toString();
    }


    public UISelectOne getSelectState() {
        return selectState;
    }


    public void setSelectState(UISelectOne selectState) {
        this.selectState = selectState;
    }

    public HtmlInputText getCityText() {
        return cityText;
    }

    public void setCityText(HtmlInputText cityText) {
        this.cityText = cityText;
    }

    public HtmlInputText getFirstNameText() {
        return firstNameText;
    }

    public void setFirstNameText(HtmlInputText firstNameText) {
        this.firstNameText = firstNameText;
    }

    public HtmlInputText getLastNameText() {
        return lastNameText;
    }

    public void setLastNameText(HtmlInputText lastNameText) {
        this.lastNameText = lastNameText;
    }

    public UISelectOne getSelectCity() {
        return selectCity;
    }

    public void setSelectCity(UISelectOne selectCity) {
        this.selectCity = selectCity;
    }

    public UISelectOne getSelectTitle() {
        return selectTitle;
    }

    public void setSelectTitle(UISelectOne selectTitle) {
        this.selectTitle = selectTitle;
    }

    public UISelectOne getSelectZip() {
        return selectZip;
    }

    public void setSelectZip(UISelectOne selectZip) {
        this.selectZip = selectZip;
    }

    public HtmlInputText getStateText() {
        return stateText;
    }

    public void setStateText(HtmlInputText stateText) {
        this.stateText = stateText;
    }

    public HtmlInputText getZipText() {
        return zipText;
    }

    public void setZipText(HtmlInputText zipText) {
        this.zipText = zipText;
    }

}