package com.icesoft.faces.component;

/**
 * All style class names are extracted from JSR-168 spec 
 *
 */
public class PORTLET_CSS_DEFAULT {
						//PLT.C.6 Menus
	public static final String PORTLET_MENU = "portlet-menu"; // (menuBar)
    public static final String PORTLET_MENU_ITEM = "portlet-menu-item"; // (root level menuItem) 
    public static final String PORTLET_MENU_ITEM_HOVER = "portlet-menu-item-hover"; // (root level menuItem:hover)
    public static final String PORTLET_MENU_CASCADE_ITEM = "portlet-menu-cascade-item"; // (submenu menuItem)

    					//PLT.C.5 Forms
    public static final String PORTLET_FORM_LABEL = "portlet-form-label"; //       (outputText, outputLabel)
    public static final String PORTLET_FORM_INPUT_FIELD = "portlet-form-input-field"; // (inputText, inputTextArea, inputSecret)
    public static final String PORTLET_FORM_BUTTON = "portlet-form-button"; //     (commandButton)
    public static final String PORTLET_FORM_FIELD = "portlet-form-field"; // (selectBooleanCheckbox,selectManyCheckbox,selectManyListbox, selectManyMenu, selectOneListbox, selectOneMenu, selectOneRadio )

    		       		//PLT.C.4 Sections (Table)	
    public static final String PORTLET_SECTION_HEADER =  "portlet-section-header"; //   (thead)
    public static final String PORTLET_SECTION_BODY = "portlet-section-body"; //     (Normal text in a table cell (TD))
    public static final String PORTLET_SECTION_ALTERNATE = "portlet-section-alternate"; // (text in every other row in the cell (alternate tr))
    public static final String PORTLET_SECTION_FOOTER = "portlet-section-footer"; //   (tfoot)   

    					//PLT.C.3 Messages (ice:message/ice:messages)
    public static final String PORTLET_MSG_ERROR = "portlet-msg-error"; //	errorClass     
    public static final String PORTLET_MSG_INFO = "portlet-msg-info"; //	infoClass      
    public static final String PORTLET_MSG_ALERT = "portlet-msg-alert"; //	warnClass      

}
