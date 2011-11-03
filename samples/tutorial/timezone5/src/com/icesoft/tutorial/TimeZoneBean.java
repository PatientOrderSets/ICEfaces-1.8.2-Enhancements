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

import com.icesoft.faces.async.render.IntervalRenderer;
import com.icesoft.faces.async.render.RenderManager;
import com.icesoft.faces.async.render.Renderable;
import com.icesoft.faces.context.DisposableBean;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesState;
import com.icesoft.faces.webapp.xmlhttp.RenderingException;
import com.icesoft.faces.webapp.xmlhttp.FatalRenderingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Map;
import java.util.TimeZone;

/**
 * Bean backing the Time Zone application. This bean uses the RenderManager to
 * update state at a specified interval. Also controls time zone information
 * during the session.
 */

public class TimeZoneBean implements Renderable, DisposableBean {
    private static Log log = LogFactory.getLog(TimeZoneBean.class);
    /**
     * The default {@link TimeZone} for this host server.
     */
    private TimeZone serverTimeZone;

    /**
     * {@link DateFormat} used to display the server time.
     */
    private DateFormat serverFormat;

    /**
     * Active {@link TimeZone} displayed at top of UI. Changes when a time zone
     * is selected by pressing one of six commandButtons in UI map.
     */
    private TimeZone selectedTimeZone;

    /**
     * {@link DateFormat} used to display the selected time.
     */
    private DateFormat selectedFormat;

    /**
     * List of all possible {@link TimeZoneWrapper} objects, which must mirror
     * the map and checkbox UIs.
     */
    private ArrayList allTimeZoneList;

    /**
     * List of checked {@link TimeZoneWrapper} objects, which are shown at the
     * bottom table UI. Changes when a time zone is selected or deselected by
     * checking one of six check boxes underneath map UI.
     */
    private ArrayList checkedTimeZoneList;

    /**
     * a table mapping the checkbox id's to the checkbox states
     */
    private Hashtable checkboxStates;

    /**
     * Time interval, in milliseconds, between renders.
     */
    private final int renderInterval = 1000;

    /**
     * The state associated with the current user that can be used for
     * server-initiated render calls.
     */
    private PersistentFacesState state;

    /**
     * A named render group that can be shared by all TimeZoneBeans for
     * server-initiated render calls.  Setting the interval determines the
     * frequency of the render call.
     */
    private IntervalRenderer clock;

    /**
     * Constructor initializes time zones.
     */
    public TimeZoneBean() {
        init();
    }

    /**
     * Initializes this TimeZoneBean's properties.
     */
    private void init() {
        serverTimeZone = TimeZone.getDefault();
        serverFormat = buildDateFormatForTimeZone(serverTimeZone);
        selectedTimeZone = TimeZone.getTimeZone(
                "Etc/GMT+0"); // selected time zone set to UTC as default
        selectedFormat = buildDateFormatForTimeZone(selectedTimeZone);

        // Entries in this list are hardcoded to match entries in
        //  the timezone web file, so no parameters can be changed.
        allTimeZoneList = new ArrayList(7);
        allTimeZoneList
                .add(new TimeZoneWrapper("Pacific/Honolulu", "GMTminus10",
                        hawaiiXCoords, hawaiiYCoords,
                        hawaiiXCoords.length));
        allTimeZoneList
                .add(new TimeZoneWrapper("America/Anchorage", "GMTminus9",
                        alaskaXCoords, alaskaYCoords,
                        alaskaXCoords.length));
        allTimeZoneList
                .add(new TimeZoneWrapper("America/Los_Angeles", "GMTminus8",
                        pacificXCoords, pacificYCoords,
                        pacificXCoords.length));
        allTimeZoneList
                .add(new TimeZoneWrapper("America/Denver", "GMTminus7",
                        mountainXCoords, mountainYCoords,
                        mountainXCoords.length));
        allTimeZoneList
                .add(new TimeZoneWrapper("America/Chicago", "GMTminus6",
                        centralXCoords, centralYCoords,
                        centralXCoords.length));
        allTimeZoneList
                .add(new TimeZoneWrapper("America/New_York", "GMTminus5",
                        easternXCoords, easternYCoords,
                        easternXCoords.length));
        allTimeZoneList
                .add(new TimeZoneWrapper("Canada/Newfoundland", "GMTminus4",
                        nfldXCoords, nfldYCoords,
                        nfldXCoords.length));

        // create main timezone outlines

        checkedTimeZoneList = new ArrayList();

        state = PersistentFacesState.getInstance();
        checkboxStates = new Hashtable(6);
        checkboxStates.put("GMTminus10", "false");
        checkboxStates.put("GMTminus9", "false");
        checkboxStates.put("GMTminus8", "false");
        checkboxStates.put("GMTminus7", "false");
        checkboxStates.put("GMTminus6", "false");
        checkboxStates.put("GMTminus5", "false");
        checkboxStates.put("GMTminus4", "false");
    }

    /**
     * Gets server time.
     *
     * @return Server time.
     */
    public String getServerTime() {
        return formatCurrentTime(serverFormat);
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
     * Gets selected time zone time. This is the time zone selected by one of
     * six commandButtons from the map in the UI.
     *
     * @return selectedTimeZone time.
     */
    public String getSelectedTime() {
        return formatCurrentTime(selectedFormat);
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
     * Gets ArrayList of currently active <code>TimeZoneWrapper</code> objects.
     * This list is populated by selectBooleanCheckbox components in UI.
     *
     * @return ArrayList of TimeZoneWrapper objects.
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
    public static String displayNameTokenizer(String displayName) {
        if (displayName == null) {
            displayName = "";
        } else {
            int firstSpace = displayName.indexOf(' ');
            if (firstSpace != -1) {
                displayName = displayName.substring(0, firstSpace);
            }
        }
        return displayName;
    }

    public static DateFormat buildDateFormatForTimeZone(TimeZone timeZone) {
        SimpleDateFormat currentFormat = new SimpleDateFormat("EEE, HH:mm:ss");
        Calendar currentZoneCal = Calendar.getInstance(timeZone);
        currentFormat.setCalendar(currentZoneCal);
        currentFormat.setTimeZone(timeZone);
        return currentFormat;
    }

    public static String formatCurrentTime(DateFormat dateFormat) {
        Calendar cal = dateFormat.getCalendar();
        cal.setTimeInMillis(System.currentTimeMillis());
        return dateFormat.format(cal.getTime());
    }

    /**
     * Each TimeZoneWrapper has one or more ids of components in the UI that
     * correspond to its time zone.  By this, if an event comes from a component
     * in the web page, then this will return the relevant TimeZoneWrapper.
     *
     * @param componentId Id of component in UI
     * @return TimeZoneWrapper
     */
    private TimeZoneWrapper getTimeZoneWrapperByComponentId(
            String componentId) {
        TimeZoneWrapper tzw;
        for (int i = 0; i < allTimeZoneList.size(); i++) {
            tzw = (TimeZoneWrapper) allTimeZoneList.get(i);
            if (tzw.isRelevantComponentId(componentId)) {
                return tzw;
            }
        }
        return null;
    }

    /**
     * Used to create, setup, and start an IntervalRenderer from the passed
     * renderManager This is used in conjunction with faces-config.xml to allow
     * the same single render manager to be set in all TimeZoneBeans
     *
     * @param renderManager RenderManager to get the IntervalRenderer from
     */
    public void setRenderManager(RenderManager renderManager) {
        clock = renderManager.getIntervalRenderer("clock");
        clock.setInterval(renderInterval);
        clock.add(this);
        clock.requestRender();
    }

    /**
     * Gets RenderManager
     *
     * @return RenderManager null
     */
    public RenderManager getRenderManager() {
        return null;
    }

    //
    // Renderable interface
    //

    /**
     * Gets the current instance of PersistentFacesState
     *
     * @return PersistentFacesState state
     */
    public PersistentFacesState getState() {
        return state;
    }

    /**
     * Callback to inform us that there was an Exception while rendering.
     * Continue from a transientRenderingException but not
     * from a FatalRenderingException
     *
     * @param renderingException render exception passed in frome framework.
     */
    public void renderingException(RenderingException renderingException) {

        if (log.isDebugEnabled()) {
            log.debug("Rendering exception: ", renderingException);
        }

        if (renderingException instanceof FatalRenderingException)  {
            performCleanup();
        }
    }

    /**
     * Used to properly shut-off the ticking clock.
     *
     * @return true if properly shut-off, false if not.
     */
    protected boolean performCleanup() {
        try {
            if (clock != null) {
                clock.remove(this);
                // whether or not this is necessary depends on how 'shutdown'
                // you want an empty renderer. If it's emptied often, the cost
                // of shutdown+startup is too great
                if (clock.isEmpty() ) {
                    clock.dispose();
                }
                clock = null;
            }
            return true;
        } catch (Exception failedCleanup) {
            if (log.isErrorEnabled()) {
                log.error("Failed to cleanup a clock bean", failedCleanup);
            }

        }
        return false;
    }

    /**
     * Disposes a view either due to a window closing
     * or a timeout.
     */
    public void dispose() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("Dispose TimeZoneBean for a user - cleaning up");
        }
        performCleanup();
    }

    //
    // Implicit interfaces as defined by the callbacks in the web files
    //

    /**
     * Listens to client input from commandButtons in the UI map and sets the
     * selected time zone.
     *
     * @param event ActionEvent.
     */
    public void listen(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map requestParams =
                context.getExternalContext().getRequestParameterMap();
        // get mouse coordinate of user click
        int x = Integer.parseInt((String) requestParams.get("ice.event.x"));
        int y = Integer.parseInt((String) requestParams.get("ice.event.y"));
        x -= icefacesXOffset;
        y -= icefacesYOffset;
        // compare mouse coordinate to know timzone polygons.
        TimeZoneWrapper tzw;
        for (int i = 0; i < allTimeZoneList.size(); i++) {
            if (((TimeZoneWrapper) allTimeZoneList.get(i)).getMapPolygon()
                    .contains(x, y)) {
                tzw = (TimeZoneWrapper) allTimeZoneList.get(i);
                selectedTimeZone = TimeZone.getTimeZone(tzw.getId());
                selectedFormat = buildDateFormatForTimeZone(selectedTimeZone);
            }
        }
    }

    /**
     * Adds or removes a <code>TimeZoneWrapper</code> to
     * <code>checkedTimeZoneList</code> when a selectBooleanCheckbox
     * ValueChangeEvent is fired from the UI.
     *
     * @param event ValueChangeEvent.
     */
    public void timeZoneChanged(ValueChangeEvent event) {
        UIComponent comp = event.getComponent();
        FacesContext context = FacesContext.getCurrentInstance();
        String componentId = comp.getClientId(context);
        TimeZoneWrapper tzw = getTimeZoneWrapperByComponentId(componentId);
        if (tzw != null) {
            boolean checked = ((Boolean) event.getNewValue()).booleanValue();
            // If checkbox is checked, then add tzw to checkedTimeZoneList
            if (checked) {
                if (!checkedTimeZoneList.contains(tzw)) {
                    checkedTimeZoneList.add(tzw);
                }
            }
            // Otherwise, if checkbox is unchecked, then remove tzw from checkedTimeZoneList
            else {
                checkedTimeZoneList.remove(tzw);
            }
            checkboxStates.put(tzw.getMapCommandButtonId(), checked ? "true" : "false");
        }
    }

    /**
     * Gets the table of checkbox states.
     *
     * @return a table mapping the checkbox id's to the checkbox states
     */
    public Hashtable getCheckboxStates() {
        return checkboxStates;
    }

    /**
     * Sets the table of checkbox states.
     *
     * @param checkboxStates a table mapping the checkbox id's to the checkbox
     *                       states
     */
    public void setCheckboxStates(Hashtable checkboxStates) {
        this.checkboxStates = checkboxStates;
    }

    // ICEfaces image map integration needs offset values to calculate the
    // correct coordinate values.
    private static int icefacesXOffset = 193;
    private static int icefacesYOffset = 236;
    // Create primary polygon objects for continental country outlines
    private static int[] hawaiiXCoords = {0, 29, 54, 58, 58, 61, 61, 0};
    private static int[] hawaiiYCoords =
            {186, 194, 208, 215, 223, 243, 254, 254};

    private static int[] alaskaXCoords =
            {117, 118, 125, 132, 135, 138, 141, 146, 147, 157, 164, 165, 162,
                    156, 144, 120, 75, 72, 60, 45, 1, 0, 0, 14};
    private static int[] alaskaYCoords =
            {0, 4, 5, 12, 12, 8, 7, 14, 14, 28, 31, 37, 38, 41, 41, 16, 16, 25,
                    35, 38, 55, 55, 1, 0};

    private static int[] pacificXCoords =
            {176, 176, 187, 187, 181, 185, 191, 192, 207, 207, 214, 214,
                    218, 222, 222, 221, 221, 222, 224, 230, 229, 225,
                    222, 219, 220, 218, 214, 214, 219, 107, 219, 232,
                    231, 230, 228, 228, 229, 228, 226, 226, 229, 231,
                    238, 233, 226, 217, 205, 198, 195, 197, 194, 187,
                    188, 189, 190, 186, 169, 152, 145, 158, 164, 164,
                    155, 141, 136, 134, 132, 125, 118, 118};
    private static int[] pacificYCoords =
            {0, 3, 3, 7, 7, 20, 19, 25, 32, 43, 47, 50, 54, 59, 64, 68, 67,
                    71, 71, 80, 86, 90, 92, 89, 90, 93, 95, 97, 106,
                    107, 112, 112, 137, 139, 140, 148, 149, 157, 158,
                    162, 163, 171, 179, 179, 171, 154, 148, 138, 133,
                    130, 130, 118, 114, 103, 88, 77, 61, 54, 41, 41,
                    37, 32, 25, 7, 9, 11, 11, 4, 4, 0};

    private static int[] mountainXCoords =
            {177, 287, 287, 268, 268, 258, 259, 249, 249, 254, 254,
                    250, 253, 250, 253, 254, 250, 250, 277, 277, 284,
                    289, 288, 290, 290, 285, 286, 281, 281, 272, 270,
                    265, 258, 256, 256, 264, 263, 268, 269, 275, 276,
                    272, 244, 216, 217, 231, 218, 226, 232, 239, 230,
                    228, 230, 230, 228, 229, 231, 233, 220, 220, 215,
                    215, 221, 220, 223, 225, 231, 231, 225, 222, 222,
                    215, 215, 208, 208, 193, 192, 185, 182, 182, 189,
                    189};
    private static int[] mountainYCoords =
            {0, 0, 8, 8, 45, 45, 41, 40, 48, 48, 52, 52, 63, 63, 63, 69, 69,
                    75, 75, 80, 81, 86, 98, 101, 110, 116, 137, 138,
                    160, 160, 164, 161, 161, 165, 181, 190, 194, 198,
                    201, 204, 210, 214, 233, 231, 216, 208, 176, 174,
                    179, 179, 163, 158, 153, 147, 144, 140, 139, 109,
                    110, 106, 104, 98, 93, 89, 91, 93, 88, 84, 77, 70,
                    61, 53, 48, 44, 41, 30, 24, 16, 17, 8, 7, 2};

    private static int[] centralXCoords =
            {288, 317, 314, 314, 321, 325, 330, 340, 336, 336, 338, 346,
                    348, 349, 350, 351, 347, 347, 357, 356, 358, 357,
                    352, 378, 380, 381, 291, 291, 269, 277, 276, 269,
                    264, 264, 257, 257, 260, 267, 270, 273, 283, 282,
                    287, 286, 292, 291, 289, 290, 284, 277, 277, 252,
                    252, 255, 255, 252, 255, 255, 251, 251, 256, 256,
                    270, 270, 289};
    private static final int[] centralYCoords =
            {0, 0, 9, 15, 15, 26, 25, 30, 35, 74, 86, 89, 94, 111, 113, 115,
                    118, 129, 137, 145, 155, 170, 210, 242, 243, 252,
                    252, 241, 217, 210, 203, 198, 193, 189, 179, 164,
                    162, 163, 165, 163, 162, 139, 138, 116, 109, 98,
                    97, 85, 78, 78, 72, 73, 69, 69, 62, 54, 52, 47, 46,
                    41, 43, 47, 47, 9, 9};

    private static int[] easternXCoords =
            {388, 417, 446, 446, 449, 447, 448, 447, 450, 449, 442, 438,
                    431, 437, 437, 446, 447, 449, 449, 450, 451, 441,
                    429, 430, 433, 433, 435, 424, 419, 415, 415, 463,
                    464, 382, 381, 353, 360, 357, 359, 348, 348, 353,
                    349, 348, 340, 337, 337, 341, 373};
    private static int[] easternYCoords =
            {0, 0, 5, 10, 16, 19, 21, 28, 36, 41, 40, 38, 44, 52, 57, 57, 51,
                    50, 54, 66, 74, 78, 81, 83, 84, 91, 96, 209, 215,
                    216, 226, 242, 255, 253, 241, 208, 154, 144, 136,
                    127, 118, 114, 110, 92, 84, 78, 35, 29, 9};

    private static int[] nfldXCoords =
            {448, 465, 465, 415, 416, 418, 434, 434, 434, 432, 433, 436, 452,
                    452, 450, 453, 450, 447, 447, 444, 440, 440, 434, 440, 443, 450,
                    453, 450, 451, 448, 450, 450, 448};
    private static int[] nfldYCoords =
            {0, 0, 242, 242, 226, 217, 209, 95, 85, 83, 80, 82, 75, 56, 54, 51,
                    50, 52, 56, 54, 55, 50, 45, 40, 42, 42, 37, 29, 23, 20, 17, 14,
                    14};
}