package com.icesoft.openajax.beans;

import com.icesoft.faces.async.render.IntervalRenderer;
import com.icesoft.faces.async.render.RenderManager;
import com.icesoft.faces.async.render.Renderable;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesState;
import com.icesoft.faces.webapp.xmlhttp.RenderingException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.TimeZone;

/**
 * Bean backing the Time Zone application. This bean uses the RenderManager to
 * update state at a specified interval. Also controls time zone information
 * during the session.
 */
public class TimeZoneBean implements Renderable
{
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
     * a table mapping the checkbox id's to the checkbox states
     */
    private Hashtable checkboxStates;
    
    /**
     * display the open ajax conformance dialog or not
     */
    private boolean dialogRendered = true;

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
        allTimeZoneList = new ArrayList(6);
        allTimeZoneList.add(new TimeZoneWrapper(
                "Pacific/Honolulu", "GMTminus10", "Cminus10"));
        allTimeZoneList.add(new TimeZoneWrapper(
                "America/Anchorage", "GMTminus9", "Cminus9"));
        allTimeZoneList.add(new TimeZoneWrapper(
                "America/Los_Angeles", "GMTminus8", "Cminus8"));
        allTimeZoneList.add(new TimeZoneWrapper(
                "America/Phoenix", "GMTminus7", "Cminus7"));
        allTimeZoneList.add(new TimeZoneWrapper(
                "America/Chicago", "GMTminus6", "Cminus6"));
        allTimeZoneList.add(new TimeZoneWrapper(
                "America/New_York", "GMTminus5", "Cminus5"));

        checkedTimeZoneList = new ArrayList();

        state = PersistentFacesState.getInstance();

        checkboxStates = new Hashtable(6);
        checkboxStates.put("Cminus10", "false");
        checkboxStates.put("Cminus9", "false");
        checkboxStates.put("Cminus8", "false");
        checkboxStates.put("Cminus7", "false");
        checkboxStates.put("Cminus6", "false");
        checkboxStates.put("Cminus5", "false");
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
        synchronized (TimeZone.class) {
            return displayNameTokenizer(selectedTimeZone.getDisplayName());
        }
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
    
    public boolean isDialogRendered() {
        return dialogRendered;
    }
    
    public void setDialogRendered(boolean dialogRendered) {
        this.dialogRendered = dialogRendered;
    }
    
    public String closeDialog() { dialogRendered = false; return "closeDialog"; }

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
        for (int i = 0; i < allTimeZoneList.size(); i++) {
            TimeZoneWrapper tzw = (TimeZoneWrapper) allTimeZoneList.get(i);
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
     * Callback to inform us that there was an Exception while rendering
     *
     * @param renderingException
     */
    public void renderingException(RenderingException renderingException) {
        if (clock != null) {
            clock.remove(this);
            clock = null;
        }
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
        UIComponent comp = event.getComponent();
        FacesContext context = FacesContext.getCurrentInstance();
        String componentId = comp.getClientId(context);
        TimeZoneWrapper tzw = getTimeZoneWrapperByComponentId(componentId);
        if (tzw != null) {
            selectedTimeZone = TimeZone.getTimeZone(tzw.getId());
            selectedFormat = buildDateFormatForTimeZone(selectedTimeZone);
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
            checkboxStates.put(tzw.getCheckboxId(), checked ? "true" : "false");
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
}
