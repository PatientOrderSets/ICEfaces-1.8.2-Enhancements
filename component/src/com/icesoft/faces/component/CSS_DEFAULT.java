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

package com.icesoft.faces.component;

/**
 * Created by IntelliJ IDEA. User: rmayhew Date: Apr 21, 2006 Time: 11:31:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class CSS_DEFAULT {
    //------------------------------------------------------------------
    // CSS style naming rules:
    // 1. All styles start with 'ice'
    // 2. Don't end with Class (redundant)
    // 3. Look for existing abbreviations, and reuse them
    // 4. Try to keep new abbreviations to 3 letters
    //------------------------------------------------------------------

    public static final String COMMAND_BTN_DEFAULT_STYLE_CLASS =
            "iceCmdBtn"; // Originally was "iceButton"
    public static final String COMMAND_LINK_DEFAULT_STYLE_CLASS =
            "iceCmdLnk"; // Originally was "iceLink"
    public static final String INPUT_SECRET_DEFAULT_STYLE_CLASS =
            "iceInpSecrt"; // Originally was "iceInputtext"
    public static final String INPUT_TEXT_DEFAULT_STYLE_CLASS =
            "iceInpTxt"; // Originally was "iceInputtext"
    public static final String INPUT_TEXT_AREA_DEFAULT_STYLE_CLASS =
            "iceInpTxtArea"; // Originally was "iceTextarea"
    public static final String OUTPUT_LABEL_DEFAULT_STYLE_CLASS =
            "iceOutLbl"; // Originally was "iceOuputtext"
    public static final String OUTPUT_LINK_DEFAULT_STYLE_CLASS =
            "iceOutLnk"; // Originally was "iceOuputLink"
    public static final String OUTPUT_TEXT_DEFAULT_STYLE_CLASS =
            "iceOutTxt"; // Originally was "iceOuputtext"
    public static final String SELECT_BOOLEAN_CHECKBOX_DEFAULT_STYLE_CLASS =
            "iceSelBoolChkbx";  // Originally was "iceBooleanCheckbox"
    public static final String SELECT_MANY_CHECKBOX_DEFAULT_STYLE_CLASS =
            "iceSelMnyCb"; // Originally was "iceCheckbox"
    public static final String SELECT_MANY_LISTBOX_DEFAULT_STYLE_CLASS =
            "iceSelMnyLb"; // Originally was "iceSelect"
    public static final String SELECT_MANY_MENU_DEFAULT_STYLE_CLASS =
            "iceSelMnyMnu";// Originally was "iceSelect"
    public static final String SELECT_ONE_LISTBOX_DEFAULT_STYLE_CLASS =
            "iceSelOneLb"; // Originally was "iceSelect"
    public static final String SELECT_ONE_MENU_DEFAULT_STYLE_CLASS =
            "iceSelOneMnu"; // Originally was "iceSelect"
    public static final String SELECT_ONE_RADIO_DEFAULT_STYLE_CLASS =
            "iceSelOneRb"; // Originally was "iceRadiobutton"
    public static final String TABLE_STYLE_CLASS =
            "iceDatTbl"; // Originally was "dataTableOutlineClass"
    public static final String TABLE_HEADER_CLASS =
        "Hdr";   // Originally was "headerClass"
    public static final String TABLE_HEADER_CLASS1 =
            "Hdr1";   // Originally was "headerClass"
    public static final String TABLE_HEADER_CLASS2 =
        "Hdr2";   // Originally was "headerClass"
    public static final String TABLE_FOOTER_CLASS =
            "Ftr";   // Originally was "headerClass"
    public static final String TABLE_ROW_CLASS =
        "Row"; // Originally was "rowClasses1"    
    public static final String TABLE_ROW_CLASS1 =
            "Row1"; // Originally was "rowClasses1"
    public static final String TABLE_ROW_CLASS2 =
            "Row2"; // Originally was "rowClasses2"
    public static final String TABLE_COLUMN_GROUP_CLASS = "ColGrp";
        
    public static final String TABLE_COLUMN_CLASS1 =
        "Col1";
    public static final String TABLE_COLUMN_CLASS2 =
        "Col2";    
    public static final String TABLE_COLUMN_CLASS =
            "Col";  // Originally was "columnClasses"
    public static final String TABLE_COLUMN_HEADER_CLASS =
        "ColHdr";  // Originally was "columnClasses"    
    public static final String TABLE_COLUMN_HEADER_CLASS1 =
        "ColHdr1";  // Originally was "columnClasses" 
    public static final String TABLE_COLUMN_HEADER_CLASS2 =
        "ColHdr2";  // Originally was "columnClasses"     
    public static final String TABLE_SCRL_SPR =
        "ScrlSpr";   
    public static final String TABLE_SCRL_HDR_TBL = "ScrlHdrTbl";
    public static final String TABLE_SCRL_BDY_TBL = "ScrlBdyTbl";
    public static final String TABLE_ACTIVE_SORT_COLUMN =
        " iceDatTblActvSrtHdr";
    public static final String PANEL_GROUP_DEFAULT_STYLE_CLASS = "icePnlGrp";
    public static final String PANEL_GRID_DEFAULT_STYLE_CLASS = "icePnlGrd";
    public static final String ROW = "Row";
    public static final String COLUMN = "Col";
    public static final String HEADER = "Hdr";
    public static final String FOOTER = "Ftr";
    public static final String OUTPUT_CONNECTION_STATUS_DEFAULT_STYLE_CLASS =
            "iceOutConStat";  // Originally was "outputConnectionStatusStyleClass"
    public static final String OUTPUT_CONNECTION_STATUS_DEFAULT_INACTIVE_CLASS =
            "Inactv"; // Originally was "outputConnectionStatusInactiveClass"
    public static final String OUTPUT_CONNECTION_STATUS_DEFAULT_ACTIVE_CLASS =
            "Actv"; // Originally was "outputConnectionStatusActiveClass"
    public static final String OUTPUT_CONNECTION_STATUS_DEFAULT_CAUTION_CLASS =
            "Caution"; // Originally was "outputConnectionStatusCautionClass"
    public static final String OUTPUT_CONNECTION_STATUS_DEFAULT_DISCONNECT_CLASS =
            "Disconnect"; // Originally was "outputConnectionStatusDisconnectClass"
    public static final String PANEL_BORDER_DEFAULT = "icePnlBrdr";
    public static final String PANEL_BORDER_DEFAULT_NORTH_CLASS =
            "North"; // Originally was "pageNorth"
    public static final String PANEL_BORDER_DEFAULT_WEST_CLASS =
            "West"; // Originally was "pageWest"
    public static final String PANEL_BORDER_DEFAULT_EAST_CLASS =
            "East"; // Originally was "pageEast"
    public static final String PANEL_BORDER_DEFAULT_CENTER_CLASS =
            "Center"; // Originally was "pageCenter"
    public static final String PANEL_BORDER_DEFAULT_SOUTH_CLASS =
            "South";  // Originally was "pageSouth"
    public static final String COMMAND_SORT_HEADER_STYLE_CLASS = "iceCmdSrtHdr";
    public static final String FORM_STYLE_CLASS = "iceFrm";
    public static final String GRAPHIC_IMAGE_STYLE_CLASS = "iceGphImg";
    public static final String MESSAGE_STYLE_CLASS = "iceMsg";
    public static final String MESSAGES_STYLE_CLASS = "iceMsgs";
    public static final String ERROR_STYLE_CLASS = "Error";
    public static final String FATAL_STYLE_CLASS = "Fatal";
    public static final String INFO_STYLE_CLASS = "Info";
    public static final String WARN_STYLE_CLASS = "Warn";
    public static final String OUTPUT_CHART_DEFAULT_STYLE_CLASS =
            "iceOutChrt";
    public static final String OUTPUT_FORMAT_DEFAULT_STYLE_CLASS =
            "iceOutFrmt";
    //public static final String PANEL_BORDER_DEFAULT_STYLE_CLASS = "Style"; // Originally was "pageStyleClass"

    public static final String TOOLTIP_BASE =
        "icePnlTlTip";
    
    public static final String POPUP_BASE =
            "icePnlPop"; // Originally was "panelPopup"
    public static final String POPUP_DEFAULT_HEADER_CLASS =
            "Hdr"; // Originally was "panelPopupHeader"
    public static final String POPUP_DEFAULT_BODY_CLASS =
            "Body"; // Originally was "panelPopupBody"

    public static final String PANEL_TAB_DEFAULT_STYLECLASS =
            "icePnlTb"; // Originally was "panelTabClass"

    public static final String PANEL_TAB_SET_DEFAULT_TAB_SET =
            "icePnlTbSet";  // Originally was "tabSet"
    public static final String PANEL_TAB_SET_DEFAULT_TABONCLASS =
            "On";// Originally was "TabOnClass"
    public static final String PANEL_TAB_SET_DEFAULT_TABOVERCLASS =
            "Over";// Originally was "tabOverClass"
    public static final String PANEL_TAB_SET_DEFAULT_TABOFFCLASS =
            "Off";// Originally was "TabOffClass"
    public static final String PANEL_TAB_SET_DEFAULT_TABSPACER =
            "Spcr";// Originally was "TabSpacer"
    public static final String PANEL_TAB_SET_DEFAULT_TABPANEL =
            "TbPnl";// Originally was "TabPanel"
    public static final String PANEL_TAB_CONTENTS_CLASS =
        "Cnt";// Originally was "TabPanel"    
    public static final String PANEL_TAB_HEADER_ICON_DEFAULT_CLASS = "HdrIcon";
    public static final String PANEL_TAB_HEADER_LABEL_DEFAULT_CLASS =
            "HdrLbl";
    public static final String PANEL_TAB_SET_DEFAULT_LEFT = "Lft";
    public static final String PANEL_TAB_SET_DEFAULT_RIGHT = "Rt";
    public static final String PANEL_TAB_SET_DEFAULT_MIDDLE = "Mid";
    public static final String PANEL_TAB_SET_DEFAULT_TOP = "Top";
    public static final String PANEL_TAB_SET_DEFAULT_BOTTOM = "Btm";
    public static final String PANEL_SERIES_DEFAULT_CLASS = "icePnlSrs";
    public static final String POSITIONED_PANEL_DEFAULT_CLASS = "icePnlPos";

    public static final String ICE_FILE_UPLOAD_DEFAULT_BUTTON_CLASS =
            "Btn"; // Originally was "Button"
    public static final String ICE_FILE_UPLOAD_DEFAULT_INPUT_TEXT_CLASS =
            "Txt"; // Originally was "Text"
    public static final String ICE_FILE_UPLOAD_BASE_CLASS =
            "iceInpFile";  // Originally was "fileUpload"
    public static final String OUTPUT_PROGRESS_BASE_CLASS =
            "iceOutProg"; // Originally was "outputProgress"
    public static final String OUTPUT_PROGRESS_INDETERMINATE_ACTIVE_CLASS =
            "IndetActv"; // Originally was "IndeterminateActiveClass"
    public static final String OUTPUT_PROGRESS_INDETERMINATE_INACTIVE_CLASS =
            "IndetInactv"; // Originally was "IndeterminateInactiveClass"
    public static final String OUTPUT_PROGRESS_TEXT_STYLE_CLASS =
            "Txt";   // Originally was "Text"
    public static final String OUTPUT_PROGRESS_BG_STYLE_CLASS =
            "Bg"; // Originally was "Background"
    public static final String OUTPUT_PROGRESS_FILL_STYLE_CLASS =
            "Fill";  // Originally was "Fill"
    public static String MENU_BAR_STYLE = "iceMnuBar";
    public static String MENU_BAR_ITEM_STYLE = "Item";
    public static String MENU_BAR_ITEM_LABEL_STYLE = "ItemLabel";
    public static String MENU_BAR_TOP_SUB_MENU_STYLE = "TopSubMenu";
    public static String MENU_BAR_SUB_MENU_STYLE = "SubMenu";
    public static String MENU_BAR_SUB_MENU_INDICATOR_STYLE = "SubMenuInd";
    public static String MENU_BAR_VERTICAL_SUFFIX_STYLE = "Vrt";
    
    public static String MENU_ITEM_STYLE = "iceMnuItm";
    public static String MENU_ITEM_LABEL_STYLE = "Label";
    public static String MENU_ITEM_IMAGE_STYLE = "Image";
    
    public static String MENU_ITEM_SEPARATOR_STYLE = "iceMnuItmSep";
    
    
    public static String SUBMENU_DIVIDER_VERTICAL_STYLE =
            "iceSubMenuDividerVert";  // Originally was "submenuDividerVert"
    public static String SUBMENU_DIVIDER_STYLE =
            "iceSubMenuDivider";  // Originally was "submenuDivider"
    public static String SUBMENU_INDICATOR_STYLE =
            "iceSubMenuInd"; // Originally was "submenuIndicator"
    public static String MENU_POPUP_STYLE = "iceMnuPop";
    public static final String STYLE_TREEROW =
            "Row";// Originally was "treerow"
    public static final String TREE_DEFAULT_STYLE_CLASS = "iceTree";
    /**
     * Calendar Constants. Now based on calendar style name
     * <p/>
     * The default style class name for the row containing the month, year and
     * navigation buttons.
     */
    public final static String DEFAULT_CALENDAR = "iceSelInpDate";

    public final static String DEFAULT_YEARMONTHHEADER_CLASS =
            "MonthYear";  // iceCalMonthYear Originally was "monthYearRowClass"
    public final static String DEFAULT_MO_YR_DROPDOWN_CLASS = "MoYrDropdown";
    public final static String DEFAULT_TIME_CLASS = "Time";
    public final static String DEFAULT_TIME_DRP_DWN_CLASS = "TimeDropDown";    
    /**
     * The default style class name for the row containg the names of the days
     * of the week.
     */
    public final static String DEFAULT_WEEKHEADER_CLASS =
            "Week"; // iceCalWeek Originally was "weekRowClass"
    /**
     * The default style class name for the cell containing the currently
     * selected day.
     */
    public final static String DEFAULT_CURRENTDAYCELL_CLASS =
            "Cur";  // iceCalCur Originally was "currentDayCellClass"
    /**
     * The default style class name for the cells containing the days of the
     * month.
     */
    public final static String DEFAULT_DAYCELL_CLASS =
            "Day"; // iceCalDay Originally was "dayCellClass"
      /**
     * The default style class name for the input text field part of the date
     * picker
     */
    public final static String DEFAULT_CALENDARINPUT_CLASS =
            "Input"; // Did not have an original name
    /**
     * The default style class name for the calendar previous month or year button
     */
    public final static String DEFAULT_CALENDARMOVEPREV_CLASS = "MovePrev";
    /**
     * The default style class name for the calendar next month or year button
     */
    public final static String DEFAULT_CALENDARMOVENEXT_CLASS = "MoveNext";
    /**
     * The default style class name for the popup calendar
     */
    public final static String DEFAULT_CALENDARPOPUP_CLASS = "Popup";
    /**
     * The default style class name for the calendar open popup button
     */
    public final static String DEFAULT_CALENDAROPENPOPUP_CLASS = "OpenPopup";
    /**
     * The default style class name for the calendar close popup button
     */
    public final static String DEFAULT_CALENDARCLOSEPOPUP_CLASS = "ClosePopup";


    public static final String DEFAULT_SELECT_INPUT = "iceSelInpTxt";
    /**
     * The default style calss name for this components input text element.
     */
    public static final String DEFAULT_SELECT_INPUT_TEXT_CLASS =
            "Txt";  // Originally was "autoCompleteInputTextClass"
    /**
     * the default style class name for this components list element.
     */
    public static final String DEFAULT_SELECT_INPUT_LIST_CLASS =
            "List";   // Originally was "autoCompleteListClass"
    /**
     * The default style class name for this components list row elements.
     */
    public static final String DEFAULT_SELECT_INPUT_ROW_CLASS =
            "Row";// Originally was "autoCompleteRowClass"
    /**
     * The default style class name for this components list selected row
     * element.
     */
    public static final String DEFAULT_SELECT_INPUT_SELECTED_ROW_CLASS =
            "SelRow"; // Originally was "autoCompleteSelectedRowClass"

    // DataPaginator
    public static final String DATA_PAGINATOR_BASE = "iceDatPgr";
    public static final String DATA_PAGINATOR_SCROLL_BUTTON_CELL_CLASS =
            "ScrBtn"; // Originally was "scrollButtonCellClass"
    public static final String OUTLINE_CLASS =
            "ScrOut"; // Originally was "dataScrollerOutlineClass"
    public static final String PAGINATOR_ACTIVE_COLUMN_CLASS =
            "ScrCol"; // Originally was "paginatorActiveColumnClass"
    public static final String PAGINATOR_COLUMN_CLASS =
            "Col"; // Originally was "paginatorColumnClass"
    public static final String PAGINATOR_TABLE_CLASS =
            "Tbl"; // Originally was "paginatorTableClass"

    public static final String PANEL_STACK_BASE = "icePnlStk";
    public static final String PANEL_STACK_ROW = "Row";
    public static final String PANEL_STACK_COL = "Col";
    // position constants

    // Row Selection Constants
    public static final String ROW_SELECTION_BASE = "iceRowSel";
    public static final String ROW_SELECTION_SELECTED = "Selected";
    public static final String ROW_SELECTION_MOUSE_OVER = "MouseOver";
    public static final String ROW_SELECTION_SELECTED_MOUSE_OVER = "SelectedMouseOver";
    
    // PanelCollapsible constants
    public static final String PANEL_COLLAPSIBLE_DEFAULT_STYLE_CLASS =
            "icePnlClpsbl";
    public static final String PANEL_COLLAPSIBLE_HEADER =
            "Hdr";
    public static final String PANEL_COLLAPSIBLE_CONTENT =
            "Cnt";
    public static final String PANEL_COLLAPSIBLE_CONTAINER =
            "Cont";
    public static final String PANEL_COLLAPSIBLE_STATE_COLLAPSED =
            "Colpsd";
    public static final String DIS_SUFFIX =
            "-dis";
    public static final String GMAP =
        "iceGmp";    
    public static final String GMAP_MAP_TD =
        "MapTd";
    public static final String GMAP_TXT_TD =
        "TxtTd";
    
    public static final String INPUT_RICH_TEXT = "iceInpRchTxt";
    
    public static final String PANEL_DIVIDER_BASE = "icePnlDvr";
    public static final String PANEL_DIVIDER_HOR_BASE = "icePnlDvrHor";
    public static final String PANEL_DIVIDER_FIRST_PANE = "Fst";
    public static final String PANEL_DIVIDER_SECOND_PANE = "Snd";
    public static final String PANEL_DIVIDER_SPLITTER = "Spt"; 
    public static final String PANEL_DIVIDER_CONTAINER = "Cnt";    
    
    public static final String PANEL_CONFIRMATION_BASE = "icePnlCnf";
    public static final String PANEL_CONFIRMATION_HEADER = "Hdr";
    public static final String PANEL_CONFIRMATION_BODY = "Body";
    public static final String PANEL_CONFIRMATION_BUTTONS = "Btns";
    
    public static final String DATAEXPORTER_DEFAULT_STYLE_CLASS = "iceDatExp";
    
}