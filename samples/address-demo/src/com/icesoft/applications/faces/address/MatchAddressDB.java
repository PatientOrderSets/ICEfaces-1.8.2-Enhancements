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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

/**
 * MatchAddressDB is the bridge between the database and the MatchBean objects.
 */
public class MatchAddressDB extends MatchBean {

    private static Log log = LogFactory.getLog(MatchAddressDB.class);

    //database entry mapping
    private TreeMap stateMap;
    private TreeMap zipMap;

    /**
     * Initializes the TreeMaps and decodes the XML file to map objects to
     * them.
     */
    public MatchAddressDB() {

        //maps for the address match objects
        stateMap = new TreeMap();
        zipMap = new TreeMap();

        //read the database from the XML file and map it
        loadXDB();
    }

    /**
     * Decodes the stored XML wrapper object representation of the address
     * database and maps the entries as MatchCity, MatchState, and MatchZip
     * objects.
     *
     * @see XAddressDataWrapper
     */
    private void loadXDB() {

        //each of these contains a city, state, and zip value
        XAddressDataWrapper xData = null;

        //for decoding the wrapper objects
        XMLDecoder xDecode = null;

        try {
            //load andunzip the xml file
            GZIPInputStream in = new GZIPInputStream(
                    MatchAddressDB.class.getResourceAsStream("address.xml.gz"));

            //xml decoding mechanism
            xDecode = new XMLDecoder(new BufferedInputStream(in));
            xData = (XAddressDataWrapper) xDecode.readObject();

        } catch (Exception e) {
            if (log.isDebugEnabled())
                log.debug("Database not found.");
        }
        
        if(xDecode == null)
            return;

        //loop through every entry in the xml file
        MatchBean city;
        MatchState state;
        MatchZip zip;

        while (xData != null) {

            //create zip, city and state objects
            zip = (MatchZip) zipMap.get((xData.getZip()));

            //new zip
            if (zip == null) {
                zip = new MatchZip(xData.getZip(), xData.getCity(),
                                   xData.getState());
                zipMap.put((xData.getZip()), zip);

                city = new MatchCity(xData.getCity(), xData.getState());

                state = (MatchState) stateMap.get((xData.getState()));

                //new state
                if (state == null) {
                    state = new MatchState(xData.getState());
                    stateMap.put((xData.getState()), state);

                }
                city = state.addCity((MatchCity) city);
                ((MatchCity) city).addZip(zip);
            }
            //get the next encoded object
            try {
                xData = (XAddressDataWrapper) xDecode.readObject();
            }
            //end of file
            catch (ArrayIndexOutOfBoundsException e) {
                if (log.isDebugEnabled())
                    log.debug("Reached end of XML file.");
                return;
            }

        }
        //close the XML decoder
        xDecode.close();
    }

    /**
     * Determines whether a given state is in the state map
     *
     * @param checkState the state to check
     * @return state object with modified match flag
     */
    public MatchState getState(String checkState) {
        MatchState state = (MatchState) stateMap.get(checkState);
        if (state != null) {
            state.setMatch(true);
        }
        return state;
    }

    /**
     * Finds the closest matching state to the given string
     *
     * @param checkState the state to check
     * @return the closest state that can be found
     */
    public MatchState getClosestState(String checkState) {
        checkState = checkState.trim().toUpperCase();
        return (MatchState) getClosestMatch(checkState, stateMap);
    }

    /**
     * Retrieves all of the state values from the state map.
     *
     * @return array of Strings of states
     */
    public String[] getAllStates() {

        String states[] = new String[stateMap.size()];
        Iterator itor = stateMap.keySet().iterator();
        int i = 0;

        while (itor.hasNext()) {
            states[i++] = (String) itor.next();
        }
        return states;
    }

    /**
     * Determines whether a given zip is in the zip map
     *
     * @param checkZip the zip to check
     * @return zip object with modified match flag
     */
    public MatchZip getZip(String checkZip) {

        MatchZip zip = (MatchZip) zipMap.get(checkZip);
        if (zip != null) {
            zip.setMatch(true);
        }
        return zip;
    }

    /**
     * Finds the closest matching zip to the given string
     *
     * @param checkZip the zip to check
     * @return the closest zip that can be found
     */
    public MatchZip getClosestZip(String checkZip) {

        checkZip = checkZip.trim();
        return (MatchZip) getClosestMatch(checkZip, zipMap);
    }

    /**
     * Finds all cities that match the provided string exactly.
     *
     * @param checkCity the city to check
     * @return list of cities
     */
    public ArrayList getCity(String checkCity) {

        Iterator states = stateMap.values().iterator();
        ArrayList cities = new ArrayList();

        //check every object in the state map
        while (states.hasNext()) {
            MatchState state = (MatchState) states.next();
            MatchCity city = state.getCity(checkCity);

            //add each matching city to the list
            if (city != null) {
                city.setMatch(true);
                cities.add(city);
            }
        }
        if (cities.size() == 0) {
            return null;
        } else {
            return cities;
        }
    }

    /**
     * Finds the closest cities to the provided city.
     *
     * @param checkCity the city to check
     * @return list of cities
     */
    public ArrayList getClosestCity(String checkCity) {

        String myCheckCity = AddressFormProcessor.fixCapitalization(checkCity);
        ArrayList cities = getCity(myCheckCity);

        if (cities != null) {
            //at least one exact match, so exit
            return cities;
        }

        //get closest city in each state
        Iterator itor = stateMap.values().iterator();
        TreeMap closest = new TreeMap();
        MatchCity city;

        while (itor.hasNext()) {
            MatchState state = (MatchState) itor.next();
            city = state.getClosestCity(myCheckCity);
            closest.put((city.getCity()), city);
        }

        //get closest of the closest cities
        city = (MatchCity) getClosestMatch(myCheckCity, closest);

        //must return all cities with this name
        cities = getCity(city.getCity());
        itor = cities.iterator();

        while (itor.hasNext()) {
            ((MatchCity) itor.next()).setMatch(false);
        }
        return cities;
    }
}