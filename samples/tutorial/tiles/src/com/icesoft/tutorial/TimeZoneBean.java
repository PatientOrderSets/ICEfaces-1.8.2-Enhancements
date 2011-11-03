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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TimeZone;

/**
 * Bean backing the Time Zone application. This bean controls time zone
 * information during the session.
 */
public class TimeZoneBean {

    /**
     * {@link DateFormat} used to display time.
     */
    private DateFormat currentFormat;
    /**
     * The default {@link TimeZone} for this host server.
     */
    private TimeZone serverTimeZone;
    /**
     * Active {@link TimeZone} displayed at top of UI. Changes when a time zone
     * is selected by pressing one of six commandButtons in UI map.
     */
    private TimeZone selectedTimeZone;
    /**
     * Identifies a user across more than one page request and stores
     * information about that user.
     */

    private ArrayList checkedTimeZoneList;
    /**
     * Constants designating a specific location in a time zone. This level of
     * detail is required to extract daylight time properties.
     */
    private static final String GMT5DAYLIGHTLOCATION = "America/New_York";
    private static final String GMT6DAYLIGHTLOCATION = "America/Chicago";
    private static final String GMT7DAYLIGHTLOCATION = "America/Phoenix";
    private static final String GMT8DAYLIGHTLOCATION = "America/Los_Angeles";
    private static final String GMT9DAYLIGHTLOCATION = "America/Anchorage";
    private static final String GMT10DAYLIGHTLOCATION = "Pacific/Honolulu";

    /**
     * Constructor initializes time zones and starts thread which updates the
     * time.
     */
    public TimeZoneBean() {
        init();
    }

    /**
     * Initializes this TimeZoneBean's properties.
     */
    private void init() {
        currentFormat = new SimpleDateFormat("EEE, HH:mm:ss");
        serverTimeZone = TimeZone.getDefault();
        selectedTimeZone = TimeZone.getTimeZone(
                "Etc/GMT+0"); // selected time zone set to UTC as default
        checkedTimeZoneList = new ArrayList();
    }

    /**
     * Gets selected time zone time. This is the time zone selected by one of
     * six commandButtons from the map in the UI.
     *
     * @return selectedTimeZone time.
     */

    public String getSelectedTime() {
        return getComputedTime(selectedTimeZone);
    }

    /**
     * Gets selected time zone display name.
     *
     * @return selectedTimeZone display name.
     */
    public String getSelectedTimeZoneName() {
        return displayNameTokenizer(selectedTimeZone.getDisplayName());
    }

    /**
     * Gets server time.
     *
     * @return Server time.
     */
    public String getServerTime() {
        long now = System.currentTimeMillis();
        Calendar serverZoneCal = Calendar.getInstance(serverTimeZone);
        serverZoneCal.setTimeInMillis(now);
        return currentFormat.format(serverZoneCal.getTime());
    }

    /**
     * Gets server time zone display name.
     *
     * @return Server time zone display name.
     */
    public String getServerTimeZoneName() {
        return displayNameTokenizer(serverTimeZone.getDisplayName());
    }

    /**
     * Gets ArrayList of <code>CheckedTimeZone</code> objects. This list is
     * populated by selectBooleanCheckbox components in UI.
     *
     * @return ArrayList of CheckedTimeZone objects.
     */
    public ArrayList getCheckedTimeZoneList() {
        return checkedTimeZoneList;
    }

    /**
     * Extracts the first word from a TimeZone displayName.
     *
     * @param displayName A TimeZone displayName.
     * @return String The first word from the TimeZone displayName.
     */
    public String displayNameTokenizer(String displayName) {
        if (displayName == null) {
            displayName = "";
        } else {
            StringTokenizer tokens = new StringTokenizer(displayName, " ");
            if (tokens.hasMoreTokens()) {
                displayName = tokens.nextToken();
            }
        }
        return displayName;
    }

    /**
     * Calculates the current time in a specified time zone.
     *
     * @param zone The specified time zone.
     * @return Time in the specified time zone.
     */
    protected String getComputedTime(TimeZone zone) {
        String tmpTime = "No Time available";
        long now = System.currentTimeMillis();

        Calendar currentZoneCal = Calendar.getInstance(zone);
        currentZoneCal.setTimeInMillis(now);
        int shift = -1 * serverTimeZone.getRawOffset();

        long calcMillis = zone.getRawOffset() + shift + now;
        Calendar cal = Calendar.getInstance(zone);
        cal.setTimeInMillis(calcMillis);
        tmpTime = currentFormat.format(cal.getTime());

        return tmpTime;
    }

    /**
     * Extracts a {@link TimeZone} id from a component id, then selects a more
     * specific id within that time zone. The specific locations used in this
     * sample are saved in constants and were chosen based on the largest
     * American city in the time zone.
     *
     * @param temp Component id containing time zone id.
     * @return {@link TimeZone} id.
     */
    private String interpretID(String temp, String minus, String plus) {
        String zoneId =
                "Etc/GMT+0"; // fallback is GMT time zone if no zoneid found.
        if (temp.indexOf(minus) > 0) {
            int transIndex = Integer.parseInt(
                    temp.substring(temp.lastIndexOf(minus) + minus.length()));
            zoneId = "Etc/GMT+" + transIndex;
        } else {
            if (temp.indexOf(plus) > 0) {
                int transIndex = Integer.parseInt(
                        temp.substring(temp.lastIndexOf(plus) + plus.length()));
                zoneId = "Etc/GMT-" + transIndex;
            }
        }
        // Choosing a specific {@link TimeZone} id within the larger {@link TimeZone}.
        if (zoneId.endsWith("5")) {
            zoneId = GMT5DAYLIGHTLOCATION;
        } else if (zoneId.endsWith("6")) {
            zoneId = GMT6DAYLIGHTLOCATION;
        } else if (zoneId.endsWith("7")) {
            zoneId = GMT7DAYLIGHTLOCATION;
        } else if (zoneId.endsWith("8")) {
            zoneId = GMT8DAYLIGHTLOCATION;
        } else if (zoneId.endsWith("9")) {
            zoneId = GMT9DAYLIGHTLOCATION;
        } else if (zoneId.endsWith("10")) {
            zoneId = GMT10DAYLIGHTLOCATION;
        }
        return zoneId;
    }

    /**
     * Listens to client input from commandButtons in the UI map and sets the
     * selected time zone.
     *
     * @param e ActionEvent.
     */
    public void listen(ActionEvent e) {
        FacesContext context = FacesContext.getCurrentInstance();
        String temp = e.getComponent().getClientId(context);
        String zoneId = interpretID(temp, "GMTplus", "GMTminus");
        selectedTimeZone = TimeZone.getTimeZone(zoneId);
    }

    /**
     * Adds or removes a <code>CheckedTimeZone</code> to
     * <code>checkedTimeZoneList</code> when a selectBooleanCheckbox
     * ValueChangeEvent is fired from the UI.
     *
     * @param event ValueChangeEvent.
     */
    public void timeZoneChanged(ValueChangeEvent event) {
        UIComponent comp = event.getComponent();
        FacesContext context = FacesContext.getCurrentInstance();
        String zoneId =
                interpretID(comp.getClientId(context), "Cplus", "Cminus");
        TimeZone timeZone = TimeZone.getTimeZone(zoneId);
        //Calendar is required to obtain a Date object to pass into inDaylightTime method.
        Calendar cal = Calendar.getInstance(timeZone);

        boolean newVal = ((Boolean) event.getNewValue()).booleanValue();
        Iterator timeZones = checkedTimeZoneList.iterator();
        while (timeZones.hasNext()) {
            if (((CheckedTimeZone) timeZones.next()).getId()
                    .equals(timeZone.getID())) {
                timeZones.remove();
            }
        }
        if (newVal) {
            CheckedTimeZone checkedTimeZone =
                    new CheckedTimeZone(
                            displayNameTokenizer(timeZone.getDisplayName()),
                            zoneId,
                            timeZone.useDaylightTime(),
                            timeZone.inDaylightTime(cal.getTime()),
                            this);
            checkedTimeZoneList.add(checkedTimeZone);
        }
    }
}

