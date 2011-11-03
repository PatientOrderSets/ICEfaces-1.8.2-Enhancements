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
 * Determines the closest match in the database. Superclass for all of the
 * Matching classes.
 */
public class Matchable {

    private boolean match = true;

    /**
     * Determine whether or not there is a match.
     *
     * @return the status of match
     */
    public boolean isMatch() {
        return match;
    }

    /**
     * Inidcate whether or not there is a match.
     *
     * @param match
     */
    public void setMatch(boolean match) {
        this.match = match;
    }

    /**
     * Matchable compares a string against a TreeMap of strings to determine the
     * closest match.
     *
     * @param checkMatch The string to compare against
     * @param matchSet   The TreeMap of strings.
     * @return Matchable
     */
    public Matchable getClosestMatch(String checkMatch, TreeMap matchSet) {

        Matchable matchable = (Matchable) matchSet.get(checkMatch);
        if (matchable != null) {
            //exact match
            matchable.setMatch(true);
            return matchable;
        } else {
            //search keys for closest match
            Iterator itor = matchSet.keySet().iterator();
            String prevVal = null;
            String nextVal = null;
            int thisCompare;
            int lastCompare;

            while (itor.hasNext()) {
                nextVal = (String) (itor.next());
                thisCompare = checkMatch.compareTo(nextVal);
                if (thisCompare < 0) {
                    if (prevVal == null) {
                        //first element is closest
                        matchable = (Matchable) matchSet.get(nextVal);
                    } else {
                        //decide if prev or next is closest
                        //need to compare characters in order to decide
                        byte nextByte[] = nextVal.getBytes();
                        byte prevByte[] = prevVal.getBytes();
                        byte matchByte[] = checkMatch.getBytes();
                        lastCompare = 0;
                        int nextDiff, prevDiff;
                        try {
                            //inside try block in case we blow off the end of the string
                            nextDiff = nextByte[lastCompare]
                                       - matchByte[lastCompare];
                            prevDiff = matchByte[lastCompare]
                                       - prevByte[lastCompare];
                            while (nextDiff == prevDiff) {
                                lastCompare++;
                                nextDiff = nextByte[lastCompare]
                                           - matchByte[lastCompare];
                                prevDiff = matchByte[lastCompare]
                                           - prevByte[lastCompare];
                            }
                            if (nextDiff > prevDiff) {
                                //next is farther, so previous is closer
                                matchable = (Matchable) matchSet
                                        .get(prevVal);
                            } else {
                                //next must be closer
                                matchable = (Matchable) matchSet
                                        .get(nextVal);
                            }
                            matchable.setMatch(false);
                            return matchable;

                        } catch (Exception e) {
                            //blew off the end of one of the strings
                            if (lastCompare >= matchByte.length) {
                                //blew off matching string so pick shortest
                                if (prevByte.length <= nextByte.length) {
                                    matchable = (Matchable) matchSet
                                            .get(prevVal);
                                } else {
                                    matchable = (Matchable) matchSet
                                            .get(nextVal);
                                }
                            } else if (lastCompare >= nextByte.length) {
                                //blew off nextDiff, so pick previous
                                matchable = (Matchable) matchSet
                                        .get(prevVal);
                            } else {
                                matchable = (Matchable) matchSet
                                        .get(nextVal);
                            }
                        }
                    }
                    matchable.setMatch(false);
                    return matchable;
                }
                prevVal = nextVal;
            }
            //last result is closest
            matchable = (Matchable) matchSet.get(nextVal);
            matchable.setMatch(false);
            return matchable;
        }
    }
}