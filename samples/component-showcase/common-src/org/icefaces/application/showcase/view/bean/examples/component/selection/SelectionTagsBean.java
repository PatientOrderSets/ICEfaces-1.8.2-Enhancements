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

package org.icefaces.application.showcase.view.bean.examples.component.selection;

import org.icefaces.application.showcase.util.MessageBundleLoader;
import org.icefaces.application.showcase.view.bean.BaseBean;

import javax.faces.event.ValueChangeEvent;
import javax.faces.event.PhaseId;
import javax.faces.model.SelectItem;

/**
 * <p>The SelectionTagsBean Class is the backing bean for the selection
 * components demonstration. It is used to store the options and selected values
 * of the various selection components.</p>
 * <p>For Developers new to JSF, the SelectItem object is a key class for
 * working with ICEfaces standard enhanced components. It can be added
 * as the &lt;f:selectItems /&gt; tags value attribute to generate  list data.
 * JSF takes care of the default value and value binding in general.</p>
 * <p>Two gotcha when using and of the selection tags with JSF 1.1.
 * <li>use boolean primative instead of the Boolean Object</li>
 * <li>selection tags require String object.  If you want to use another
 * object type for list use a Converter.</li>
 * </p>
 *
 * @sinse 0.3.0
 */
public class SelectionTagsBean extends BaseBean {
    // check box example value
    private boolean newUser;
    // selectOneMenu for components
    private String selectedComponent;
    // selectManyMenu cars values
    private String[] selectedCars;
    // radio button example
    private String selectedDrink;
    // checkbox multiselect languanges example
    private String[] selectedLanguages;
    // selectOneListbox example value
    private String selectedCountry;
    // selectManyListbox example value
    private String[] selectedCities;


    /**
     * Value change listen called when the new checkbox checked/unchecked. No
     * actual work is done for this method call but it does show what a
     * ValuesChange method signature should look like.
     *
     * @param event jsf value change event
     */
    public void carChanged(ValueChangeEvent event) {
        valueChangeEffect.setFired(false);
    }

    /**
     * Value change listener for the country change event. Sets up the cities
     * listbox according to the country.
     *
     * @param event value change event
     */
    public void countryChanged(ValueChangeEvent event) {
        if (!event.getPhaseId().equals(PhaseId.INVOKE_APPLICATION)) {
            event.setPhaseId(PhaseId.INVOKE_APPLICATION);
            event.queue();
            return;
        }
        
        // check to see if the country has changed if clear the selected cities
        selectedCities = new String[]{};

        // reset effect
        valueChangeEffect.setFired(false);
    }

    /**
     * Value change listener called when an new item is selected in the list of
     * cities.  No actual work is done for this method but it does show
     * what a ValueChange method signature should look like.
     *
     * @param event JSF value change event
     */
    public void cityChanged(ValueChangeEvent event) {
        valueChangeEffect.setFired(false);
    }

    /**
     * Gets the option items for component types.
     *
     * @return array of component type items
     */
    public SelectItem[] getComponentItems() {
        SelectItem[] componentItems =
            buildSelectItemArray("bean.selection.component.comp", ".value", 1, 3);
        return componentItems;
    }

    /**
     * returns the list of available cars to select
     *
     * @return carlist
     */
    public SelectItem[] getCarListItems() {
        SelectItem[] carItems =
            buildSelectItemArray("bean.selection.cars.car", ".value", 1, 5);
        return carItems;
    }
    
    /**
     * Gets the option items for drinks.
     *
     * @return array of drink items
     */
    public SelectItem[] getDrinkItems() {
        SelectItem[] drinkItems =
            buildSelectItemArray("bean.selection.drink.drink", ".value", 1, 4);
        return drinkItems;
    }

    /**
     * Gets the option items for languages.
     *
     * @return array of language items
     */
    public SelectItem[] getLanguageItems() {
        SelectItem[] languageItems =
            buildSelectItemArray("bean.selection.language.lang", ".value", 1, 5);
        return languageItems;
    }

    /**
     * Gets the option items for countries.
     *
     * @return array of country items
     */
    public SelectItem[] getCountryItems() {
        SelectItem[] countryItems =
            buildSelectItemArray("bean.selection.country", ".value", 1, 5);
        return countryItems;
    }

    /**
     * Gets the option items of cities.
     *
     * @return array of city items
     */
    public SelectItem[] getCityItems() {
        SelectItem[] cityItems = null;
        if (selectedCountry != null && selectedCountry.length() > 0) {
            SelectItem[] countryItems = getCountryItems();            
            for(int i = 0; i < countryItems.length; i++) {
                if (selectedCountry.equals(countryItems[i].getValue())) {
                    cityItems = buildSelectItemArray(
                        "bean.selection.country"+Integer.toString(i+1)+".city",
                        ".value", 1, 5);
                    break;
                }
            }
        }
        return cityItems;
    }

    /**
     * Gets the newUser property.
     *
     * @return true or false
     */
    public boolean isNewUser() {
        return newUser;
    }

    /**
     * Sets the newUser property.
     *
     * @param newValue true of false
     */
    public void setNewUser(boolean newValue) {
        newUser = newValue;
    }

    /**
     * Gets the selected component.
     *
     * @return the selected component
     */
    public String getSelectedComponent() {
        return selectedComponent;
    }
    
    public void setSelectedComponent(String selectedComponent) {
        this.selectedComponent = selectedComponent;
    }

    /**
     * Gets the array of selected cars.
     *
     * @return the array of selected cars
     */
    public String[] getSelectedCars() {
        return selectedCars;
    }
    
    public void setSelectedCars(String[] selectedCars) {
        this.selectedCars = selectedCars;
    }

    /**
     * Gets the selected drink.
     *
     * @return the selected drink
     */
    public String getSelectedDrink() {
        return selectedDrink;
    }
    
    public void setSelectedDrink(String selectedDrink) {
        this.selectedDrink = selectedDrink;
    }

    /**
     * Gets the selected languages.
     *
     * @return the array of selected languages
     */
    public String[] getSelectedLanguages() {
        return selectedLanguages;
    }

    public void setSelectedLanguages(String[] selectedLanguages) {
        this.selectedLanguages = selectedLanguages;
    }
    
    /**
     * Gets the selected country.
     *
     * @return the selected country
     */
    public String getSelectedCountry() {
        return selectedCountry;
    }
    
    public void setSelectedCountry(String selectedCountry) {
        this.selectedCountry = selectedCountry;
    }

    /**
     * Gets the selected cities.
     *
     * @return array of selected cities
     */
    public String[] getSelectedCities() {
        return selectedCities;
    }

    public void setSelectedCities(String[] selectedCities) {
        this.selectedCities = selectedCities;
    }
    
    /**
     * Returns selectedComponent, translated
     *
     * @return selected component type.
     */
    public String getSelectedComponentString() {
        return convertToString(
            (selectedComponent == null) ? null :
            new String[]{selectedComponent});
    }
    
    /**
     * Returns the selectedCities array a comma seperated list
     *
     * @return comma seperated list of selected cities.
     */
    public String getSelectedCarsStrings() {
        return convertToString(selectedCars);
    }
    
    /**
     * Returns selectedDrink, translated
     *
     * @return selected drink.
     */
    public String getSelectedDrinkString() {
        return convertToString(
            (selectedDrink == null) ? null :
            new String[]{selectedDrink});
    }

    /**
     * Returns the selectedLangues array a comma seperated list
     *
     * @return comma seperated list of selected languages.
     */
    public String getSelectedLanguagesStrings() {
        return convertToString(selectedLanguages);
    }

    /**
     * Returns selectedCountry, translated
     *
     * @return selected country.
     */
    public String getSelectedCountryString() {
        return convertToString(
            (selectedCountry == null) ? null :
            new String[]{selectedCountry});
    }
    
    /**
     * Returns the selectedCities array a comma seperated list
     *
     * @return comma seperated list of selected cities.
     */
    public String getSelectedCitiesStrings() {
        return convertToString(selectedCities);
    }
}
