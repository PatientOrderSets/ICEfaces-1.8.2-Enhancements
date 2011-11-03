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

import java.util.Iterator;
import java.util.TreeMap;

/**
 * State-specific matching
 */
public class MatchState extends MatchBean {

    /**
     * Constructor for MatchState instantiates a new TreeMap.
     */
    public MatchState(String state) {
        this.state = state;
        cityMap = new TreeMap();
    }

    /**
     * Adds the supplied MatchCity to the city map.
     *
     * @param cityDb the MatchCity to add
     * @return the closest matching city
     */
    public MatchCity addCity(MatchCity cityDb) {
        MatchCity existingCity = getCity(cityDb.getCity());
        if (existingCity == null) {
            cityMap.put(cityDb.getCity(), cityDb);
            return cityDb;
        }
        return existingCity;
    }

    /**
     * Takes the city values from the map and puts them in an array.
     *
     * @return the array of city strings
     */
    public String[] getCitiesAsStrings() {

        Iterator itor = cityMap.keySet().iterator();
        String cities[] = new String[cityMap.size()];
        int i = 0;

        while (itor.hasNext()) {
            cities[i++] = (String) itor.next();
        }
        return cities;
    }

    /**
     * Finds the MatchCity object that corresponds to the provided string.
     *
     * @param checkCity the city to check
     * @return the corresponding MatchCity object
     */
    public MatchCity getCity(String checkCity) {
        MatchCity matchingCity = (MatchCity) cityMap.get(checkCity);
        if (matchingCity != null) {
            //exact Match
            matchingCity.setMatch(true);
        }
        return matchingCity;
    }

    /**
     * Finds the closest city to the provided string.
     *
     * @param checkCity the city to check
     * @return the closest matching city
     */
    public MatchCity getClosestCity(String checkCity) {
        checkCity = checkCity.trim();
        AddressFormProcessor.fixCapitalization(checkCity);
        return (MatchCity) getClosestMatch(checkCity, cityMap);
    }

    /**
     * Finds the closest zip to the provided string.
     *
     * @param checkZip the zip to check
     * @return the closest matching MatchZip object
     */
    public MatchZip getClosestZip(String checkZip) {
        checkZip = checkZip.trim();
        Iterator itor = cityMap.keySet().iterator();
        TreeMap zips = new TreeMap();

        while (itor.hasNext()) {
            MatchCity thisCity = (MatchCity) cityMap.get(itor.next());
            MatchZip thisZip = thisCity.getClosestZip(checkZip);
            zips.put(thisZip.getZip(), thisZip);
        }
        return (MatchZip) getClosestMatch(checkZip, zips);
    }
}