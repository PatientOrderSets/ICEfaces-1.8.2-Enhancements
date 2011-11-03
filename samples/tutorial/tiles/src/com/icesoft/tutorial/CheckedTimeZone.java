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

package com.icesoft.tutorial;

import java.util.TimeZone;

/**
 * Bean holding time zone specific information. Checking a selectBooleanCheckbox
 * in the UI will cause instances of this class to populate the ArrayList in
 * <code>TimeZoneBean</code>. That ArrayList is used to create a DataTable of
 * checked time zones in the UI.
 */
public class CheckedTimeZone {

    /**
     * Time zone name displayed in the UI. DisplayName is associated with the id
     * for this CheckedTimeZone.
     */
    private String dislayName;
    /**
     * {@link TimeZone} id used to identify the time zone. This is the id
     * returned from <code>interpretId</code> in TimeZoneBean.
     * <code>interpretId</code> returns a specific location in a timezone so
     * that daylight time properties can be accessed.
     */
    private String id;
    /**
     * TimeZoneBean used to access dynamic time.
     */
    private TimeZoneBean parent;
    /**
     * Time zone uses daylight savings time.
     */
    private boolean useDaylightTime;
    /**
     * Time zone in daylight savings time.
     */
    private boolean inDaylightTime;

    /**
     * Constructor
     *
     * @param displayName     Time zone name that is displayed in the UI.
     * @param id              {@link TimeZone} id used to identify the time
     *                        zone.
     * @param useDaylightTime Does time zone use daylight time.
     * @param inDaylightTime  Is time zone currently in daylight time.
     * @param parent          TimeZoneBean instance to access dynamic time in
     *                        time zone.
     */
    public CheckedTimeZone(String displayName, String id,
                           boolean useDaylightTime, boolean inDaylightTime,
                           TimeZoneBean parent) {
        this.dislayName = displayName;
        this.id = id;
        this.useDaylightTime = useDaylightTime;
        this.inDaylightTime = inDaylightTime;
        this.parent = parent;
    }

    /**
     * Gets the name of this time zone to be displayed in the UI.
     *
     * @return String
     */
    public String getDisplayName() {
        return dislayName;
    }

    /**
     * Gets the {@link TimeZone} id used to identify this time zone.
     *
     * @return String
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the dynamic time through the <code>getComputedTime</code> method in
     * the <code>TimeZoneBean</code>.
     *
     * @return String
     */
    public String getTime() {
        TimeZone thisTimeZone = TimeZone.getTimeZone(id);
        return parent.getComputedTime(thisTimeZone);

    }

    /**
     * Gets whether or not this time zone uses DayLight time.
     *
     * @return Returns the useDaylightTime.
     */
    public String getUseDaylightTime() {
        if (useDaylightTime) {
            return "Yes";
        } else {
            return "No";
        }
    }

    /**
     * Gets the state of DayLight Time in this time zone.
     *
     * @return Returns the inDaylightTime.
     */
    public String getInDaylightTime() {
        if (inDaylightTime) {
            return "Yes";
        } else {
            return "No";
        }
    }
}
