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
package org.icefaces.application.showcase.view.bean.examples.component.selectInputText;

import com.icesoft.faces.component.selectinputtext.SelectInputText;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.icefaces.application.showcase.view.bean.BaseBean;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import java.util.List;

/**
 * <p>The SelectInputTextController class is responsible handling the auto
 * complete lookup and selection</p>
 *
 * @since 1.7
 */
public class SelectInputTextController extends BaseBean {

    private final Log log = LogFactory.getLog(this.getClass());

    // city dictionary
    private CityDictionary cityDictionary;

    // list of possible city matches for a given city dictionary lookup
    private List cityMatchPossibilities;

    // number of city possibilities to show
    private static int cityListLength = 15;

    // value associatd with first selectInput Component
    private String selectedCityValue1 = "";
    // value associatd with first selectInput Component
    private String selectedCityValue2 = "";

    // selected city information, assigned when user uses mouse or enter key
    // to select a city.
    private City selectedCity;

    public SelectInputTextController() {
        selectedCity = new City();
    }

    /**
     * <p>Called by the selectInputText component at set intervals.  By using
     * the change event we can can get the newly typed work and do a look up in
     * the city dictionary.  The list of possible cities calculatd from the city
     * dictionary is assigned back to the component for display.</p>
     * <p>If the component selected a value then we find the respective city
     * information for dispaly purposes.
     *
     * @param event jsf value change event.
     */
    public void selectInputValueChanged(ValueChangeEvent event) {

        if (event.getComponent() instanceof SelectInputText) {

            // get the number of displayable records from the component
            SelectInputText autoComplete =
                    (SelectInputText) event.getComponent();
            // get the new value typed by component user.
            String newWord = (String) event.getNewValue();

            cityMatchPossibilities =
                    cityDictionary.generateCityMatches(newWord, cityListLength);

            // if there is a selected item then find the city object of the
            // same name
            if (autoComplete.getSelectedItem() != null) {
                selectedCity = (City) autoComplete.getSelectedItem().getValue();
                // fire effect to draw attention
                valueChangeEffect.setFired(false);
            }
            // if there was no selection we still want to see if a proper
            // city was typed and update our selectedCity instance.
            else{
                City tmp = getFindCityMatch(newWord);
                if (tmp != null){
                    selectedCity = tmp;
                     // fire effect to draw attention
                    valueChangeEffect.setFired(false);
                }
            }

        }
    }

    /**
     * Utility method for finding detailed city information fromn the list of
     * possibile city names.
     *
     * @param cityName city name to do city search on.
     * @return found city object if any, null otherwise.
     */
    private City getFindCityMatch(String cityName) {
        if (cityMatchPossibilities != null) {
            SelectItem city;
            for(int i = 0, max = cityMatchPossibilities.size(); i < max; i++){
                city = (SelectItem)cityMatchPossibilities.get(i);
                if (city.getLabel().compareToIgnoreCase(cityName) == 0) {
                    return (City) city.getValue();
                }
            }
        }
        return null;
    }

    public void setCityDictionary(CityDictionary cityDictionary) {
        this.cityDictionary = cityDictionary;
    }

    public List getCityMatchPossibilities() {
        return cityMatchPossibilities;
    }

    public City getSelectedCity() {
        return selectedCity;
    }

    public void setSelectedCity(City selectedCity) {
        this.selectedCity = selectedCity;
    }

    public String getSelectedCityValue1() {
        return selectedCityValue1;
    }

    public void setSelectedCityValue1(String selectedCityValue1) {
        this.selectedCityValue1 = selectedCityValue1;
    }

    public String getSelectedCityValue2() {
        return selectedCityValue2;
    }

    public void setSelectedCityValue2(String selectedCityValue2) {
        this.selectedCityValue2 = selectedCityValue2;
    }

    public int getCityListLength() {
        return cityListLength;
    }
}
