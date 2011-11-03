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

package com.icesoft.faces.component.ext;


import javax.faces.component.UIComponent;
import javax.faces.event.ActionEvent;
import javax.faces.event.FacesListener;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class KeyEvent extends ActionEvent {
    public static final int ESC = 27;
    public static final int TAB = 9;
    public static final int CAPSLOCK = 20;
    public static final int SHIFT = 16;
    public static final int CTRL = 17;
    public static final int START_LEFT = 91;
    public static final int START_RIGHT = 92;
    public static final int CONTEXT_MENU = 93;
    public static final int ALT = 18;
    public static final int SPACE = 32;
    public static final int CARRIAGE_RETURN = 13;
    public static final int LINE_FEED = 10;
    public static final int BACK_SLASH = 220;
    public static final int BACK_SPACE = 8;

    public static final int INSERT = 45;
    public static final int DEL = 46;
    public static final int HOME = 36;
    public static final int END = 35;
    public static final int PAGE_UP = 33;
    public static final int PAGE_DOWN = 34;

    public static final int PRINT_SCREEN = 44;
    public static final int SCR_LK = 145;
    public static final int PAUSE = 19;


    public static final int LEFT_ARROW_KEY = 37;
    public static final int UP_ARROW_KEY = 38;
    public static final int RIGHT_ARROW_KEY = 39;
    public static final int DOWN_ARROW_KEY = 40;


    public static final int F1 = 112;
    public static final int F2 = 113;
    public static final int F3 = 114;
    public static final int F4 = 115;
    public static final int F5 = 116;
    public static final int F6 = 117;
    public static final int F7 = 118;
    public static final int F8 = 119;
    public static final int F9 = 120;
    public static final int F10 = 121;
    public static final int F11 = 122;
    public static final int F12 = 123;
    private Map requestMap;

    public KeyEvent(UIComponent uiComponent, Map requestMap) {
        super(uiComponent);
        this.requestMap = requestMap;
    }

    public boolean isAltKey() {
        return Boolean.valueOf((String) this.requestMap.get("ice.event.alt")).booleanValue();
    }

    public boolean isCtrlKey() {
        return Boolean.valueOf((String) this.requestMap.get("ice.event.ctrl")).booleanValue();
    }

    public boolean isShiftKey() {
        return Boolean.valueOf((String) this.requestMap.get("ice.event.shift")).booleanValue();
    }

    public int getKeyCode() {
        String s = (String) this.requestMap.get("ice.event.keycode");
        if(s == null)return -1;
        return Integer.parseInt(s);
    }

    public String getType() {
        return (String) this.requestMap.get("ice.event.type");
    }

    public String getComponentId() {
        return (String) this.requestMap.get("ice.event.captured");
    }

    public boolean isAppropriateListener(FacesListener arg0) {
        return false;
    }

    public void processListener(FacesListener arg0) {
    }
}
