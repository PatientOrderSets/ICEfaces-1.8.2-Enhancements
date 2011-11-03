package com.icesoft.openajax.beans;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Bean holding time zone specific information.
 * Checking a selectBooleanCheckbox in the UI will cause instances of this class to populate the checkedTimeZoneList ArrayList in <code>TimeZoneBean</code>.
 * That ArrayList is used to create a DataTable of checked time zones in the UI.
 */
public class TimeZoneWrapper
{
    /* Variables */
    /**
     * {@link TimeZone} id used to identify the time zone. This id can be passed
     * to <code>TimeZone.getTimeZone(String)</code>, to get the appropriate
     * {@link TimeZone} object.
     */
    private String id;

    /**
     * The component id of the commandButton, in the map UI, corresponding to
     * this time zone.
     */
    private String mapCommandButtonId;

    /**
     * The component id of the selectBooleanCheckbox, under the map UI,
     * corresponding to this time zone.
     */
    private String checkboxId;


    /**
     * A cached {@link DateFormat} used to describe what the time is for this
     * {@link TimeZone}
     */
    private DateFormat dateFormat;

    /* Constructors */
    /**
     * @param id      id used to identify the time zone.
     * @param mapId   map button component id in web page
     * @param checkId checkbox component id in web page
     */
    public TimeZoneWrapper(String id, String mapId, String checkId) {
        this.id = id;
        this.mapCommandButtonId = mapId;
        this.checkboxId = checkId;
        this.dateFormat = TimeZoneBean.buildDateFormatForTimeZone(
                TimeZone.getTimeZone(id));
    }

    /* Getters */
    /**
     * Gets the name of this time zone to be displayed in the UI.
     *
     * @return String
     */
    public String getDisplayName() {
        String displayName = null;
        TimeZone timeZone = TimeZone.getTimeZone(id);
        synchronized (TimeZone.class) {
            displayName = TimeZoneBean.displayNameTokenizer(
                    timeZone.getDisplayName());
        }
        return displayName;
    }

    /**
     * Gets the {@link TimeZone} id used to identify this time zone in the Java
     * code.
     *
     * @return String
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the dynamic time through the <code>formatCurrentTime</code> method
     * in the <code>TimeZoneBean</code>.
     *
     * @return String
     */
    public String getTime() {
        return TimeZoneBean.formatCurrentTime(dateFormat);
    }

    /**
     * Gets whether or not this time zone uses DayLight time.
     *
     * @return Returns the useDaylightTime.
     */
    public String getUseDaylightTime() {
        TimeZone timeZone = TimeZone.getTimeZone(id);
        if (timeZone.useDaylightTime()) {
            return "Yes";
        }

        return "No";
    }

    /**
     * Gets the state of DayLight Time in this time zone.
     *
     * @return Returns the inDaylightTime.
     */
    public String getInDaylightTime() {
        TimeZone timeZone = TimeZone.getTimeZone(id);
        Calendar cal = Calendar.getInstance(timeZone);
        if (timeZone.inDaylightTime(cal.getTime())) {
            return "Yes";
        }

        return "No";
    }

    /**
     * Gets the {@link TimeZone} location used to identify this time zone.
     *
     * @return String
     */
    public String getLocation() {
        return id;
    }


    /**
     * Ascertains whether mapCommandButtonId or checkboxId are a part of
     * componentId. componentId might be a fully qualified id, with a prefix
     * corresponding to container component(s).
     *
     * @param componentId Id of some component that may be related to this time
     *                    zone
     */
    public boolean isRelevantComponentId(String componentId) {
        boolean relevant = (componentId.endsWith(mapCommandButtonId) ||
                            componentId.endsWith(checkboxId));
        return relevant;
    }

    /**
     * Gets the component id of the commandButton, in the map UI, corresponding
     * to this time zone.
     */
    public String getMapCommandButtonId() {
        return mapCommandButtonId;
    }

    /**
     * Gets the component id of the selectBooleanCheckbox, under the map UI,
     * corresponding to this time zone.
     */
    public String getCheckboxId() {
        return checkboxId;
    }
}
