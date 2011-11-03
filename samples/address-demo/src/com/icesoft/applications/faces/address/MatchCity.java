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
 * City-specific matching.
 */
public class MatchCity extends MatchBean {

    public MatchCity(String city) {
        this.city = city;
        zipMap = new TreeMap();
    }

    public MatchCity(String city, String state) {
        this.city = city;
        this.state = state;
        zipMap = new TreeMap();
    }

    /**
     * Adds the supplied MatchZip to the zip map.
     *
     * @param zipDb the MatchZip to add
     */
    public MatchZip addZip(MatchZip zipDb) {
        MatchZip existingZip = getZip(zipDb.getZip());

        if (existingZip == null) {
            zipMap.put(zipDb.getZip(), zipDb);
            return zipDb;
        } else {
            return existingZip;
        }
    }

    /**
     * Takes the zip values from the map and puts them in an array.
     *
     * @return the array of zip strings
     */
    public String[] getZipsAsStrings() {

        Iterator itor = zipMap.keySet().iterator();
        String zips[] = new String[zipMap.size()];
        int i = 0;

        while (itor.hasNext()) {
            zips[i++] = (String) itor.next();
        }
        return zips;
    }

    /**
     * Determines whether the provided zip matches one in the database.
     *
     * @param checkZip the provided zip
     * @return the status of the existance of the zip
     */
    public boolean hasZip(String checkZip) {
        return getClosestZip(checkZip).isMatch();
    }

    /**
     * Check whether the zip matches in the database and modifies the match
     * flag.
     *
     * @param checkZip the zip to lookup
     * @return the MatchZip object of the provided zip string
     */
    public MatchZip getZip(String checkZip) {

        MatchZip matchingZip = (MatchZip) zipMap.get(checkZip);

        if (matchingZip != null) {
            //exact match
            matchingZip.setMatch(true);
        }
        return matchingZip;
    }

    /**
     * Finds the closest zip to the provided string.
     *
     * @param checkZip the zip to check
     * @return the MatchZip object of the closest matching zip
     */
    public MatchZip getClosestZip(String checkZip) {
        checkZip = checkZip.trim();
        return (MatchZip) getClosestMatch(checkZip, zipMap);
    }
}