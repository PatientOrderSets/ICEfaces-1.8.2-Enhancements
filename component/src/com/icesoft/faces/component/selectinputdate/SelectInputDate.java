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

package com.icesoft.faces.component.selectinputdate;

import com.icesoft.faces.component.CSS_DEFAULT;
import com.icesoft.faces.component.ext.HtmlInputText;
import com.icesoft.faces.component.ext.taglib.Util;
import com.icesoft.faces.context.BridgeFacesContext;
import com.icesoft.faces.context.effects.JavascriptContext;
import com.icesoft.faces.utils.SeriesStateHolder;

import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.DateTimeConverter;
import javax.faces.el.ValueBinding;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * SelectInputDate is a JSF component class that represents an ICEfaces input
 * date selector.
 * <p/>
 * The component extends the ICEfaces extended HTMLPanelGroup.
 * <p/>
 * By default the component is rendered by the "com.icesoft.faces.Calendar"
 * renderer type.
 *
 * @author Greg McCleary
 * @version 1.1
 */
public class SelectInputDate
        extends HtmlInputText
        implements SeriesStateHolder {
    /**
     * The component type.
     */
    public static final String COMPONENT_TYPE =
            "com.icesoft.faces.SelectInputDate";
    /**
     * The component family.
     */
    public static final String COMPONENT_FAMILY = "javax.faces.Input";
    /**
     * The default renderer type.
     */
    private static final String DEFAULT_RENDERER_TYPE =
            "com.icesoft.faces.Calendar";
    /**
     * The default date format for the popup input text child component.
     */
    public static final String DEFAULT_POPUP_DATE_FORMAT = "MM/dd/yyyy";

    // style
    private String style = null;

    private String styleClass = null;

    /**
     * The current renderAsPopup state.
     */
    private Boolean _renderAsPopup = null;
    /**
     * The current directory path of the images used by the component.
     */
    private String _imageDir = null;
    /**
     * The current name of the move next image
     */
    private String _moveNextImage = null;
    /**
     * The current name of the move previous image.
     */
    private String _movePreviousImage = null;
    /**
     * The current name of the open popup image.
     */
    private String _openPopupImage = null;
    /**
     * The current name of the close popup image.
     */
    private String _closePopupImage = null;

    /**
     * The current date format used for the input text child of the component.
     * <p>Only applies when component is used in popup mode.
     */
    private String _popupDateFormat = null;

    /**
     * The current popup state.
     */
    private List showPopup = new ArrayList();
    /**
     * The current navigation event state.
     */
    private boolean navEvent = false;
    /**
     * The current navigation date of the component.
     */
    private Date navDate = null;
    private Date popupDate = null;
    
    // declare default style classes
    /**
     * The default directory where the images used by this component can be
     * found. This directory and its contents are included in the icefaces.jar
     * file.
     */
    private final String DEFAULT_IMAGEDIR = "/xmlhttp/css/xp/css-images/";
    /**
     * The default move next image name.
     */
    private final String DEFAULT_MOVENEXT = "cal_arrow_right.gif";
    /**
     * The default move previous image name.
     */
    private final String DEFAULT_MOVEPREV = "cal_arrow_left.gif";
    /**
     * The default open popup image name.
     */
    private final String DEFAULT_OPENPOPUP = "cal_button.gif";
    /**
     * The default close popup image name.
     */
    private final String DEFAULT_CLOSEPOPUP = "cal_off.gif";

    public final static String CALENDAR_INPUTTEXT = "";
    private Boolean _renderMonthAsDropdown;
    private Boolean _renderYearAsDropdown;
    private String inputTitle = null;
    private static final Pattern timePattern = Pattern.compile("[aHkKhmsS]");
    private Integer submittedHours = null;
    private Integer submittedMinutes = null;
    private String submittedAmPm= null;    
    /**
     * Creates an instance and sets renderer type to "com.icesoft.faces.Calendar".
     */
    public SelectInputDate() {
        setRendererType(DEFAULT_RENDERER_TYPE);
    }

    public void encodeBegin(FacesContext context) throws IOException {
        super.encodeBegin(context);
        buildHighlightMap();
        
    }

    /**
     * <p/>
     * CSS style attribute. </p>
     *
     * @return style
     */

    public String getStyle() {
        if (this.style != null) {
            return this.style;
        }
        ValueBinding _vb = getValueBinding("style");


        if (_vb != null) {
            return (String) _vb.getValue(getFacesContext());
        }
        return null;
    }

    /**
     * <p/>
     * CSS style attribute. </p>
     *
     * @param style
     * @see #getStyle()
     */
    public void setStyle(String style) {
        this.style = style;
    }


    /**
     * Formats the given date using the default date format MM/dd/yyyy.
     *
     * @param date
     * @return the formatted date as a String.
     */
    public String formatDate(Date date) {
        if (date != null) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            String ret = resolveDateTimeConverter(facesContext).getAsString(
                    facesContext, this, date);
            return ret;
        } else {
            return "";
        }
    }

    public String getTextToRender() {
        if (getPopupDate() != null &&
            isRenderAsPopup() &&
            !isEnterKeyPressed(getFacesContext())) {
            return formatDate(getPopupDate());
        }        
        Object submittedValue = getSubmittedValue();
        if (submittedValue != null) {
            if (submittedValue instanceof Date)
                return formatDate((Date) submittedValue);
            else {
                // If the submittedValue had passed validation, then it
                //  would already be null and value would be a Date,
                //  so we're dealing with bogus text that we're outputting
                //  so the user can fix it
                return submittedValue.toString();
            }
        }
        return formatDate((Date) getValue());
    }

    /**
     * To properly function, selectInputDate needs to use the same timezone
     * in the inputText field as well as the calendar, which is accomplished
     * by using a javax.faces.convert.DateTimeConverter, which provides
     * the required Converter behaviours, as we as gives access to its
     * TimeZone object. If developers require a custom Converter, then they
     * must subclass javax.faces.convert.DateTimeConverter.
     *
     * @return DateTimeConverter
     */
    public DateTimeConverter resolveDateTimeConverter(FacesContext context) {
        DateTimeConverter converter = null;
        Converter compConverter = getConverter();
        if (compConverter instanceof DateTimeConverter) {
            converter = (DateTimeConverter) compConverter;
        } else {
            Converter appConverter = context.getApplication().createConverter(
                    java.util.Date.class);
            if (appConverter instanceof DateTimeConverter) {
                converter = (DateTimeConverter) appConverter;
            } else {
                converter = new DateTimeConverter();
            }
        }
      //  System.out.println("ReolveDate");
        // For backwards compatibility, if they specify the popupDateFormat
        //  attribute, then that takes precedence over the DateTimeConverter's
        //  original pattern or other settings relating to its DateFormat
        String pattern = getSpecifiedPopupDateFormat();
        if (pattern != null && pattern.trim().length() > 0)
            converter.setPattern(pattern);
        return converter;
    }

    public TimeZone resolveTimeZone(FacesContext context) {
        DateTimeConverter converter = resolveDateTimeConverter(context);
        TimeZone tz = converter.getTimeZone();
        if (tz == null) { // DateTimeConverter should already do this
            tz = TimeZone.getTimeZone("GMT");
        }
        return tz;
    }

    public Locale resolveLocale(FacesContext context) {
        return context.getViewRoot().getLocale();
    }

    /**
     * Sets the boolean navEvent attribute.
     *
     * @param navEvent a value of true indicates that a navigation event has
     *                 occured.
     */
    public void setNavEvent(boolean navEvent) {
        this.navEvent = navEvent;
    }

    /**
     * A navEvent value of true indicates that a navEvent has occured.
     *
     * @return a value of true if a navigation event caused that render.
     */
    public boolean isNavEvent() {
        return this.navEvent;
    }

    /**
     * Set the date value of the navDate. The navDate is used to render a
     * calendar when the user is navigating from month to month or year to
     * year.
     *
     * @param navDate a Date assigned to the navDate.
     */
    public void setNavDate(Date navDate) {
        this.navDate = navDate;
    }

    /**
     * Get the navDate to render a calendar on a navigation event.
     *
     * @return the navDate as a Date
     */
    public Date getNavDate() {
        return this.navDate;
    }

    /**
     * Setting the showPopup attribute to true will render the SelectInputDate
     * popup calendar.
     *
     * @param showPopup a value of true will cause the popup calendar to be
     *                  rendered
     */
    public void setShowPopup(boolean showPopup) {
        if (showPopup) {
            this.showPopup.add(getClientId(FacesContext.getCurrentInstance()));
        } else {
            this.showPopup
                    .remove(getClientId(FacesContext.getCurrentInstance()));
        }
    }

    /**
     * A showPopup value of true indicates the SelectInputText popup be
     * rendered.
     *
     * @return the current value showPopup
     */
    public boolean isShowPopup() {
        if (showPopup
                .contains(getClientId(FacesContext.getCurrentInstance()))) {
            return true;
        } else {
            return false;
        }
    }

    /* (non-Javadoc)
    * @see javax.faces.component.UIComponent#getFamily()
    */
    public String getFamily() {
        return COMPONENT_FAMILY;
    }


    /**
     * Returns the style class name used for the row containing the month and
     * Year. The style class is defined in an external style sheet.
     *
     * @return the style class name applied to the monthYearRow. If a
     *         monthYearRowClass attribute has not been set the default will be
     *         used.
     */
    public String getMonthYearRowClass() {
        return Util.getQualifiedStyleClass(this,
                CSS_DEFAULT.DEFAULT_YEARMONTHHEADER_CLASS,
                isDisabled());
    }

    public String getMonthYearDropdownClass() {
        return Util.getQualifiedStyleClass(this, CSS_DEFAULT.DEFAULT_MO_YR_DROPDOWN_CLASS, isDisabled());
    }

    public String getTimeClass() {
        return Util.getQualifiedStyleClass(this,
                CSS_DEFAULT.DEFAULT_TIME_CLASS,
                isDisabled());
    }
    
    public String getTimeDropDownClass() {
        return "iceSelOneMnu " + Util.getQualifiedStyleClass(this,
                CSS_DEFAULT.DEFAULT_TIME_DRP_DWN_CLASS,
                isDisabled());
    }
    /**
     * Returns the style class name of the weekRowClass The style class is
     * defined in an external style sheet.
     *
     * @return the style class name applied to the weekRow. If a weekRowClass
     *         attribute has not been set the default will be used.
     */
    public String getWeekRowClass() {
        return Util.getQualifiedStyleClass(this,
                CSS_DEFAULT.DEFAULT_WEEKHEADER_CLASS,
                isDisabled());
    }

    /**
     * @return the style class name used for the input text of the calendar.
     */
    public String getCalendarInputClass() {
        return Util.getQualifiedStyleClass(this,
                CSS_DEFAULT.DEFAULT_CALENDARINPUT_CLASS,
                isDisabled());
    }

    /**
     * Returns the style class name applied to the day cells in the
     * SelectInputDate calendar.
     *
     * @return the style class name that is applied to the SelectInputDate day
     *         cells
     */
    public String getDayCellClass() {
        return Util.getQualifiedStyleClass(this,
                CSS_DEFAULT.DEFAULT_DAYCELL_CLASS,
                isDisabled());
    }

    /**
     * Returns the style class name applied to the previous month or year button in the SelectInputDate calendar.
     *
     * @return the style class name that is applied to the SelectInputDate previous month or year button
     */
    public String getMovePrevClass() {
        return Util.getQualifiedStyleClass(this, CSS_DEFAULT.DEFAULT_CALENDARMOVEPREV_CLASS, isDisabled());
    }

    /**
     * Returns the style class name applied to the next month or year button in the SelectInputDate calendar.
     *
     * @return the style class name that is applied to the SelectInputDate next month or year button
     */
    public String getMoveNextClass() {
        return Util.getQualifiedStyleClass(this, CSS_DEFAULT.DEFAULT_CALENDARMOVENEXT_CLASS, isDisabled());
    }

    /**
     * Returns the style class name applied to the open popup button in the SelectInputDate calendar.
     *
     * @return the style class name that is applied to the SelectInputDate open popup button
     */
    public String getOpenPopupClass() {
        return Util.getQualifiedStyleClass(this, CSS_DEFAULT.DEFAULT_CALENDAROPENPOPUP_CLASS, isDisabled());
    }

    /**
     * Returns the style class name applied to the close popup button in the SelectInputDate calendar.
     *
     * @return the style class name that is applied to the SelectInputDate close popup button
     */
    public String getClosePopupClass() {
        return Util.getQualifiedStyleClass(this, CSS_DEFAULT.DEFAULT_CALENDARCLOSEPOPUP_CLASS, isDisabled());
    }

    /* (non-Javadoc)
     * @see com.icesoft.faces.component.ext.HtmlInputText#setStyleClass(java.lang.String)
     */
    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    /**
     * <p>Return the value of the <code>styleClass</code> property.</p>
     *
     * @return styleClass
     */
    public String getStyleClass() {
        return Util.getQualifiedStyleClass(this,
                styleClass,
                CSS_DEFAULT.DEFAULT_CALENDAR,
                "styleClass", isDisabled());
    }

    /**
     * Returns the currentDayCell style class name.
     *
     * @return style class name used for the current day cell
     */
    public String getCurrentDayCellClass() {
        return Util.getQualifiedStyleClass(this,
                CSS_DEFAULT.DEFAULT_CURRENTDAYCELL_CLASS,
                isDisabled());
    }


    /**
     * @return the value of the renderAsPopup indicator.
     */
    public boolean isRenderAsPopup() {
        if (_renderAsPopup != null) {
            return _renderAsPopup.booleanValue();
        }
        ValueBinding vb = getValueBinding("renderAsPopup");
        Boolean v =
                vb != null ? (Boolean) vb.getValue(getFacesContext()) : null;
        return v != null ? v.booleanValue() : false;
    }

    /**
     * @param b
     */
    public void setRenderAsPopup(boolean b) {
        _renderAsPopup = new Boolean(b);
    }

    public boolean isRenderMonthAsDropdown() {
        if (_renderMonthAsDropdown != null) {
            return _renderMonthAsDropdown.booleanValue();
        }
        ValueBinding vb = getValueBinding("renderMonthAsDropdown");
        Boolean v = vb != null ? (Boolean) vb.getValue(getFacesContext()) : null;
        return v != null ? v.booleanValue() : false;
    }

    public void setRenderMonthAsDropdown(boolean b) {
        _renderMonthAsDropdown = new Boolean(b);
    }

    public boolean isRenderYearAsDropdown() {
        if (_renderYearAsDropdown != null) {
            return _renderYearAsDropdown.booleanValue();
        }
        ValueBinding vb = getValueBinding("renderYearAsDropdown");
        Boolean v = vb != null ? (Boolean) vb.getValue(getFacesContext()) : null;
        return v != null ? v.booleanValue() : false;
    }

    public void setRenderYearAsDropdown(boolean b) {
        _renderYearAsDropdown = new Boolean(b);
    }

    /**
     * Sets the directory where the images used by this component are located.
     *
     * @param imageDir the directory where the images used by this component are
     *                 located
     */
    public void setImageDir(String imageDir) {
        _imageDir = imageDir;
    }

    /**
     * @return the directory name where the images used by this component are
     *         located.
     */
    public String getImageDir() {
        if (_imageDir != null) {
            return _imageDir;
        }

        ValueBinding vb = getValueBinding("imageDir");
        if (vb != null) {
            return (String) vb.getValue(getFacesContext());
        } else {
            return DEFAULT_IMAGEDIR;
        }
    }

    /**
     * @return whether the imageDir attribute has been set.
     */
    public boolean isImageDirSet() {
        return _imageDir != null || getValueBinding("imageDir") != null;
    }

    /**
     * @return the name of the move next image.
     */
    public String getMoveNextImage() {
        return this.DEFAULT_MOVENEXT;
    }

    /**
     * Returns the name of the move previous image.
     *
     * @return DEFAULT_MOVEPREV
     */
    public String getMovePreviousImage() {
        return this.DEFAULT_MOVEPREV;
    }

    /**
     * returns the name of the open popup image.
     *
     * @return DEFAULT_OPENPOPUP
     */
    public String getOpenPopupImage() {
        return this.DEFAULT_OPENPOPUP;
    }

    /**
     * Returns the name of the close popup image.
     *
     * @return DEFAULT_CLOSEPOPUP
     */
    public String getClosePopupImage() {
        return this.DEFAULT_CLOSEPOPUP;
    }

    /**
     * Sets the date format of the input text child component when the component
     * is in popup mode.
     *
     * @param popupDateFormat
     */
    public void setPopupDateFormat(String popupDateFormat) {
        _popupDateFormat = popupDateFormat;
    }

    /**
     * Returns the date format string of the input text child componenet.
     *
     * @return _popupDateFormat
     */
    public String getPopupDateFormat() {
        String popupDateFormat = getSpecifiedPopupDateFormat();
        if (popupDateFormat == null)
            popupDateFormat = DEFAULT_POPUP_DATE_FORMAT;
        return popupDateFormat;
    }

    /**
     * If the popupDateFormat was specified, then return that, but not any
     * default values.
     *
     * @return popupDateFormat
     */
    protected String getSpecifiedPopupDateFormat() {
        if (_popupDateFormat != null) {
            return _popupDateFormat;
        }
        ValueBinding vb = getValueBinding("popupDateFormat");
        if (vb != null) {
            return (String) vb.getValue(getFacesContext());
        }
        return null;
    }

    /* (non-Javadoc)
    * @see javax.faces.component.StateHolder#saveState(javax.faces.context.FacesContext)
    */
    public Object saveState(FacesContext context) {
        Object values[] = new Object[29];
        values[0] = super.saveState(context);
        values[1] = _renderAsPopup;
        values[2] = _popupDateFormat;
        values[3] = _imageDir;
        values[4] = _moveNextImage;
        values[5] = _movePreviousImage;
        values[6] = _openPopupImage;
        values[7] = _closePopupImage;
        values[8] = new Boolean(navEvent);
        values[9] = navDate;
        values[10] = _renderMonthAsDropdown;
        values[11] = _renderYearAsDropdown;
        values[12] = style;
        values[13] = styleClass;
        values[14] = highlightValue;
        values[15] = highlightUnit;
        values[16] = inputTitle;
        values[17] = highlightClass;
        values[18] = highlightDayClass;
        values[19] = highlightMonthClass;
        values[20] = highlightWeekClass;
        values[21] = highlightYearClass;
        values[22] = linkMap;
        values[23] = selectedDayLink;
        values[24] = showPopup;
        values[25] = submittedHours;
        values[26] = submittedMinutes;  
        values[27] = submittedAmPm;         
        values[28] = popupDate;        
        return ((Object) (values));
    }

    /* (non-Javadoc)
     * @see javax.faces.component.StateHolder#restoreState(javax.faces.context.FacesContext, java.lang.Object)
     */
    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        _renderAsPopup = (Boolean) values[1];
        _popupDateFormat = (String) values[2];
        _imageDir = (String) values[3];
        _moveNextImage = (String) values[4];
        _movePreviousImage = (String) values[5];
        _openPopupImage = (String) values[6];
        _closePopupImage = (String) values[7];
        navEvent = ((Boolean) values[8]).booleanValue();
        navDate = (Date) values[9];
        _renderMonthAsDropdown = (Boolean) values[10];
        _renderYearAsDropdown = (Boolean) values[11];
        style = (String)values[12];
        styleClass = (String)values[13];
        highlightValue = (String)values[14];
        highlightUnit = (String)values[15];
        inputTitle = (String)values[16];
        highlightClass = (String)values[17];
        highlightDayClass = (String) values[18];
        highlightMonthClass = (String) values[19];
        highlightWeekClass = (String) values[20];
        highlightYearClass = (String) values[21];
        linkMap = (Map) values[22];
        selectedDayLink = (String) values[23];
        showPopup = (List) values[24];
        submittedHours = (Integer)values[25];
        submittedMinutes = (Integer)values[26];  
        submittedAmPm = (String)values[27];
        popupDate = (Date) values[28];        
    }
    
    public Object saveSeriesState(FacesContext facesContext) {
        Object values[] = new Object[6];
        values[0] = navEvent ? Boolean.TRUE : Boolean.FALSE;
        values[1] = navDate;
        values[2] = submittedHours;
        values[3] = submittedMinutes;
        values[4] = submittedAmPm;
        values[5] = popupDate;
        return values;
    }

    public void restoreSeriesState(FacesContext facesContext, Object state) {
        Object values[] = (Object[]) state;
        navEvent = ((Boolean) values[0]).booleanValue();
        navDate = (Date) values[1];
        submittedHours = (Integer)values[2];
        submittedMinutes = (Integer)values[3];
        submittedAmPm = (String)values[4];
        popupDate = (Date) values[5];
    }

    private Map linkMap = new HashMap();

    /**
     * @return linkMap
     */
    public Map getLinkMap() {
        return linkMap;
    }

    /**
     * @param linkMap
     */
    public void setLinkMap(Map linkMap) {
        this.linkMap = linkMap;
    }

    private String selectedDayLink;

    /**
     * @return selectedDayLink
     */
    public String getSelectedDayLink() {
        return selectedDayLink;
    }

    /**
     * @param selectedDayLink
     */
    public void setSelectedDayLink(String selectedDayLink) {
        this.selectedDayLink = selectedDayLink;
    }

    
    public boolean getPartialSubmit() {
        if (partialSubmit != null) {
            return partialSubmit.booleanValue();
        }
        ValueBinding vb = getValueBinding("partialSubmit");
        Boolean boolVal =
                vb != null ? (Boolean) vb.getValue(getFacesContext()) : null;
        if (boolVal != null) {
            return boolVal.booleanValue();
        }
        if (Util.isParentPartialSubmit(this)) {
            return true;
        }
        return true;
    }

    private String highlightClass;

    /**
     * <p>Set the value of the <code>highlightClass</code> property.</p>
     *
     * @param highlightClass
     */
    public void setHighlightClass(String highlightClass) {
        this.highlightClass = highlightClass;
    }

    /**
     * <p>Return the value of the <code>highlightClass</code> property.</p>
     *
     * @return String highlightClass, if never set returns a blank string not
     *         null
     */
    public String getHighlightClass() {
        if (highlightClass != null) {
            return highlightClass;
        }
        ValueBinding vb = getValueBinding("highlightClass");
        return vb != null ? (String) vb.getValue(getFacesContext()) : "";
    }

    public void requestFocus() {
        if (isRenderAsPopup()) {
            ((BridgeFacesContext) FacesContext.getCurrentInstance())
                    .setFocusId("null");
            JavascriptContext.focus(FacesContext.getCurrentInstance(),
                    this.getClientId(
                            FacesContext.getCurrentInstance()) + CALENDAR_INPUTTEXT);
        } else {
            //log: focus can only be set in popup mode
        }
    }

    private String highlightUnit;

    /**
     * <p>Set the value of the <code>highlightUnit</code> property.</p>
     *
     * @param highlightUnit The highlight class
     */
    public void setHighlightUnit(String highlightUnit) {
        this.highlightUnit = highlightUnit;
    }

    /**
     * <p>Return the value of the <code>highlightUnit</code> property.</p>
     *
     * @return String highlightUnit, if never set returns a blank string not
     *         null
     */
    public String getHighlightUnit() {
        if (highlightUnit != null) {
            return highlightUnit;
        }
        ValueBinding vb = getValueBinding("highlightUnit");
        return vb != null ? (String) vb.getValue(getFacesContext()) : "";
    }

    private String highlightValue;

    /**
     * <p>Set the value of the <code>highlightValue</code> property.</p>
     *
     * @param highlightValue
     */
    public void setHighlightValue(String highlightValue) {
        this.highlightValue = highlightValue;
    }

    /**
     * <p>Return the value of the <code>highlightValue</code> property.</p>
     *
     * @return String highlightValue. if never set returns blank a string not
     *         null
     */
    public String getHighlightValue() {
        if (highlightValue != null) {
            return highlightValue;
        }
        ValueBinding vb = getValueBinding("highlightValue");
        return vb != null ? (String) vb.getValue(getFacesContext()) : "";
    }

    private transient Map hightlightRules = new HashMap();
    private transient Map unitMap = new UnitMap();

    private void buildHighlightMap() {
        validateHighlight();
        resetHighlightClasses(Calendar.YEAR);
    }

    private boolean validateHighlight() {
        hightlightRules.clear();
        String highlightClassArray[] = getHighlightClass().split(":");
        String highlightUnitArray[] = getHighlightUnit().split(":");
        String highlightValueArray[] = getHighlightValue().split(":");
        if ((highlightClassArray.length < 1) ||
                highlightClassArray[0].equals("") ||
                highlightUnitArray[0].equals("") ||
                highlightValueArray[0].equals("")) {
            return false;
        }
        if (!(highlightClassArray.length == highlightUnitArray.length) ||
                !(highlightUnitArray.length == highlightValueArray.length)) {
            System.out.println("\n[SelectInputDate] The following attributes does not have corresponding values:" +
                    "\n-highlightClass \n-highlightUnit \n-highlightValue \n" +
                    "Note: When highlighting required, all above attributes " +
                    "need to be used together and should have corresponding values.\n" +
                    "Each entity can be separated using the : colon, e.g. \n" +
                    "highlightClass=\"weekend: newyear\" \n" +
                    "highlightUnit=\"DAY_OF_WEEK: DAY_OF_YEAR\" \n" +
                    "highlightValue=\"1, 7: 1\" "
            );
            return false;
        }

        for (int i = 0; i < highlightUnitArray.length; i++) {
            try {
                int option = Integer.parseInt(highlightUnitArray[i].trim());
                if (option < 1 || option > 8) {
                    System.out.println("[SelectInputDate:highlightUnit] \"" + highlightUnitArray[i].trim() + "\" " +
                            "s not a valid unit value. Valid values are between 1 to 8");
                    return false;
                }
            } catch (NumberFormatException exception) {
                if (unitMap.containsKey(highlightUnitArray[i].trim())) {
                    highlightUnitArray[i] = String.valueOf(unitMap.get(
                            highlightUnitArray[i].trim()));
                } else {
                    System.out.println("[SelectInputDate:highlightUnit] \"" + highlightUnitArray[i] + "\" is " +
                            "not a valid unit value, String representation " +
                            "of unit must match with java.util.Calendar contants (e.g.)" +
                            "\nYEAR, MONTH, WEEK_OF_YEAR, WEEK_OF_MONTH, DATE, DAY_OF_YEAR, " +
                            "DAY_OF_WEEK and DAY_OF_WEEK_IN_MONTH");
                    return false;
                }
            }
            String[] value = highlightValueArray[i].replaceAll(" ", "").trim().split(",");
            for (int j = 0; j < value.length; j++) {
                hightlightRules.put(highlightUnitArray[i].trim() + "$" + value[j], highlightClassArray[i]);
            }
        }


        return true;
    }

    Map getHightlightRules() {
        return hightlightRules;
    }

    void setHightlightRules(Map hightlightRules) {
        this.hightlightRules = hightlightRules;
    }

    private String highlightYearClass = "";
    private String highlightMonthClass = "";
    private String highlightWeekClass = "";
    private String highlightDayClass = "";

    String getHighlightDayCellClass() {
        StringBuffer sb = new StringBuffer(64);
        if (highlightYearClass != null && highlightYearClass.length() > 0)
            sb.append(highlightYearClass);
        if (highlightMonthClass != null && highlightMonthClass.length() > 0) {
            if (sb.length() > 0)
                sb.append(' ');
            sb.append(highlightMonthClass);
        }
        if (highlightWeekClass != null && highlightWeekClass.length() > 0) {
            if (sb.length() > 0)
                sb.append(' ');
            sb.append(highlightWeekClass);
        }
        if (highlightDayClass != null && highlightDayClass.length() > 0) {
            if (sb.length() > 0)
                sb.append(' ');
            sb.append(highlightDayClass);
        }
        return sb.toString();
    }

    String getHighlightMonthClass() {
        return highlightMonthClass;
    }

    void setHighlightMonthClass(String highlightMonthClass) {
        this.highlightMonthClass = highlightMonthClass;
    }

    String getHighlightYearClass() {
        return highlightYearClass;
    }

    void setHighlightYearClass(String highlightYearClass) {
        this.highlightYearClass = highlightYearClass;
    }

    String getHighlightWeekClass() {
        return highlightWeekClass;
    }

    void addHighlightWeekClass(String highlightWeekClass) {
        if (this.highlightWeekClass == null || this.highlightWeekClass.length() == 0) {
            this.highlightWeekClass = highlightWeekClass;
        }
        else {
            if (this.highlightWeekClass.indexOf(highlightWeekClass) == -1) {
                this.highlightWeekClass = this.highlightWeekClass + " " + highlightWeekClass;
            }
        }
    }

    void addHighlightDayClass(String highlightDayClass) {
        if (this.highlightDayClass == null || this.highlightDayClass.length() == 0) {
            this.highlightDayClass = highlightDayClass;
        }
        else {
            if (this.highlightDayClass.indexOf(highlightDayClass) == -1) {
                this.highlightDayClass = this.highlightDayClass + " " + highlightDayClass;
            }
        }
    }

    void resetHighlightClasses(int level) {
        if (level <= Calendar.MONTH) {
            this.highlightMonthClass = "";
            this.highlightYearClass = "";
            this.highlightDayClass = "";
        }
        this.highlightDayClass = "";
        this.highlightWeekClass = "";
    }

    public String getInputTitle() {
        if (this.inputTitle != null) {
            return this.inputTitle;
        }
        ValueBinding vb = getValueBinding("inputTitle");
        if (vb != null) {
            return (String) vb.getValue(getFacesContext());
        }
        return null;
    }

    public void setInputTitle(String inputTitle) {
        this.inputTitle = inputTitle;
    }

    static class UnitMap extends HashMap {
        public UnitMap() {
            this.put("YEAR", new Integer(Calendar.YEAR));
            this.put("MONTH", new Integer(Calendar.MONTH));
            this.put("WEEK_OF_YEAR", new Integer(Calendar.WEEK_OF_YEAR));
            this.put("WEEK_OF_MONTH", new Integer(Calendar.WEEK_OF_MONTH));
            this.put("DATE", new Integer(Calendar.DATE));
            this.put("DAY_OF_YEAR", new Integer(Calendar.DAY_OF_YEAR));
            this.put("DAY_OF_WEEK", new Integer(Calendar.DAY_OF_WEEK));
            this.put("DAY_OF_WEEK_IN_MONTH", new Integer(Calendar.DAY_OF_WEEK_IN_MONTH));
        }

        public int getConstant(String key) {
            if (!super.containsKey(key)) {
                return 0;
            }
            return Integer.parseInt(super.get(key).toString());
        }
    }
    
    static boolean isTime(DateTimeConverter converter) {
        String pattern = getDateTimeConverterPattern(converter); 
        if (pattern == null)
            return false;
        Matcher timeMatcher = timePattern.matcher(pattern);
        return timeMatcher.find();
    }
    
    boolean isTime(FacesContext context) {
        return isTime(resolveDateTimeConverter(context)); 
    }
    
    static int[] getHours(DateTimeConverter converter) {
        String pattern = getDateTimeConverterPattern(converter);
        int start = 0;
        int end = 23;
        if (pattern.indexOf("H") >= 0) {
            start = 0;
            end = 23;
        } else if (pattern.indexOf("h") >= 0) {
            start = 1;
            end = 12;
        } else if (pattern.indexOf("k") >= 0) {
            start = 1;
            end = 24;
        } else if(pattern.indexOf("K") >= 0) {
            start = 0;
            end = 11;            
        }
        int base = (start==0?1:0);
        int[] hours = new int[end + base];
        for (int i=0; i < hours.length; i ++) {
            hours[i] = i + start;
        }
        return hours;
    }
    
    int[] getHours(FacesContext context) {
        return getHours(resolveDateTimeConverter(context));
    } 
    
    boolean isAmPm(FacesContext context) {
        String pattern = getDateTimeConverterPattern(resolveDateTimeConverter(context));        
        return pattern.indexOf("a") >= 0 ||
               pattern.indexOf("h") >= 0 ||
               pattern.indexOf("k") >= 0;
    }

    /**
     * This method is necesary since DateTimeConverter.getDateFormat(Locale) is private  
     */
    private static String getDateTimeConverterPattern(DateTimeConverter converter) {
        Locale locale = converter.getLocale();
        String pattern = converter.getPattern();
        String type = converter.getType();
        String dateStyle = converter.getDateStyle();
        String timeStyle = converter.getTimeStyle();
        
        DateFormat df;
        if (pattern != null) {
            df = new SimpleDateFormat(pattern, locale);
        } else if (type.equals("both")) {
            df = DateFormat.getDateTimeInstance
                 (getDateTimeConverterStyle(dateStyle), getDateTimeConverterStyle(timeStyle), locale);
        } else if (type.equals("date")) {
            df = DateFormat.getDateInstance(getDateTimeConverterStyle(dateStyle), locale);
        } else if (type.equals("time")) {
            df = DateFormat.getTimeInstance(getDateTimeConverterStyle(timeStyle), locale);
        } else {
            // PENDING(craigmcc) - i18n
            throw new IllegalArgumentException("Invalid type: " + type);
        }
        df.setLenient(false);
        
        // In the underlying code, it is always a SimpleDateFormat
        if (df instanceof SimpleDateFormat) {
            return ((SimpleDateFormat)df).toPattern();
        }
        return "";
    }
    
    /**
     * This method is necesary since DateTimeConverter.getStylet(String) is private  
     */
    private static int getDateTimeConverterStyle(String name) {
        if      ("short".equals(name))  return DateFormat.SHORT;
        else if ("medium".equals(name)) return DateFormat.MEDIUM;
        else if ("long".equals(name))   return DateFormat.LONG;
        else if ("full".equals(name))   return DateFormat.FULL;
        else                            return DateFormat.DEFAULT;
    }
    
    void setHoursSubmittedValue(Object submittedHours) {
        if (submittedHours == null)
            this.submittedHours = null;
        else
            try {
                this.submittedHours = new Integer(submittedHours.toString());
            } catch (NumberFormatException e) {
                this.submittedHours = null;
            }
    }    
    
    Integer getHoursSubmittedValue() {
        return submittedHours;
    }
    
    void setMinutesSubmittedValue(Object submittedMinutes) {
        if (submittedMinutes == null)
            this.submittedMinutes = null;
        else
            try {
                this.submittedMinutes = new Integer(submittedMinutes.toString());
            } catch (NumberFormatException e) {
                this.submittedMinutes = null;
            }                
    }
    
    Integer getMinutesSubmittedValue() {
        return submittedMinutes;
    }
    
    void setAmPmSubmittedValue(Object submittedAmPm) {
        if (submittedAmPm == null)
            this.submittedAmPm = null;
        else        
            this.submittedAmPm = submittedAmPm.toString();
    }
    
    String getAmPmSubmittedValue() {
        return submittedAmPm;
    }
    
    public void validate(FacesContext context) {
        try {
            if (isRenderAsPopup() && !isEnterKeyPressed(context)) {
                if (isShowPopup()) {
                    Object submittedValue = getSubmittedValue();
                    Object newValue = getConvertedValue(context, submittedValue);
                    setPopupDate((Date)newValue);
                    setSubmittedValue(null);
                }
                else {
                    setPopupDate(null);
                }
            }
        } catch (ConverterException ce) {
            //faces message will be handled by the super class
        }
        super.validate(context);
    }    
    
    boolean isEnterKeyPressed(FacesContext facesContext) {
        Map requestParameterMap = facesContext.getExternalContext().getRequestParameterMap();
        String clientId= this.getClientId(facesContext);
        boolean enterKeyPressed = false;
        if (requestParameterMap.get("ice.event.target") != null ) {
            enterKeyPressed =  requestParameterMap.get("ice.event.target")
            .equals(clientId + SelectInputDate.CALENDAR_INPUTTEXT) && 
            ("13".equalsIgnoreCase(String.valueOf
                    (requestParameterMap.get("ice.event.keycode"))) ||
                         "onblur".equalsIgnoreCase(String.valueOf
                                (requestParameterMap.get("ice.event.type"))));
        }
        return enterKeyPressed;
    }

    Date getPopupDate() {
        return popupDate;
    }

    void setPopupDate(Date popupDate) {
        this.popupDate = popupDate;
    }
    
}
