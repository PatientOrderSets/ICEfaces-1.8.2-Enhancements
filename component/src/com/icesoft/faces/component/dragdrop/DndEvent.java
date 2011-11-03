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

package com.icesoft.faces.component.dragdrop;

import javax.faces.component.UIComponent;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;
import javax.faces.event.PhaseId;
import java.util.StringTokenizer;


/**
 * A DnDEvent is passed to Drag and Drop Event listeners. It contains all the
 * information on the type of event, and the components involved.
 */
public class DndEvent extends FacesEvent {

    private int eventType;


    private Object targetDropValue;
    private Object targetDragValue;
    private String targetClientId;

    /**
     * Event type for when a Draggable panel is starting to drag
     */
    public static final int DRAG_START = 1;
    public static final int DRAGGING = 1;
    /**
     * Event type for when a Draggable panel has been dropped, but not on a drop
     * target.
     */
    public static final int DRAG_CANCEL = 2;
    /**
     * Event type for when a Draggable panel has been dropped on a drop target.
     * The dndComponent will be set to the drop target for this event
     */
    public static final int DROPPED = 3;
    /**
     * Event type for when a Draggable panel is being hovered over a drop
     * target. The dndComponent will be set to the drop target for this event
     */
    public static final int HOVER_START = 4;
    /**
     * Event type for when a Drgabble panel is no longer hovering over a drop
     * target.
     */
    public static final int HOVER_END = 5;

    private static String[] names = {"none", "dragging", "drag_cancel",
                                     "dropped", "hover_start", "hover_end",
                                     "pointerDraw"};

    /**
     * Mask to cover all DnD Events.
     */
    public static final String MASK_ALL = "1,2,3,4,5";

    public static final String MASK_ALL_BUT_DROPS = "1,4,5";


    /**
     * DnDEvent This constructor is called by Drag and Drop components in the
     * Decode method.
     *
     * @param uiComponent
     * @param eventType
     * @param targetClentId
     * @param targetDragValue
     * @param targetDropValue
     */
    public DndEvent(UIComponent uiComponent, int eventType,
                    String targetClentId, Object targetDragValue,
                    Object targetDropValue) {
        super(uiComponent);
        setPhaseId(PhaseId.APPLY_REQUEST_VALUES);
        this.eventType = eventType;
        this.targetClientId = targetClentId;
        this.targetDragValue = targetDragValue;
        this.targetDropValue = targetDropValue;


    }

    /**
     * False
     *
     * @param facesListener
     * @return boolean
     */
    public boolean isAppropriateListener(FacesListener facesListener) {
        return false;
    }

    /**
     * Not implemented
     *
     * @param facesListener
     */
    public void processListener(FacesListener facesListener) {
    }

    /**
     * Event type for the Event. Possible values are DRAG_START, DRAG_CANCEL,
     * DROPPED, HOVER_START HOVER_END
     *
     * @return int eventType
     */
    public int getEventType() {
        return eventType;
    }


    /**
     * The drop value assigned to the target PanelGroup Null for DRAG_START,
     * DRAG_CANCEL, and HOVER_END events
     *
     * @return Object targetDropValue
     */
    public Object getTargetDropValue() {
        return targetDropValue;
    }

    /**
     * The drag value assigned to the target PanelGroup Null for DRAG_START,
     * DRAG_CANCEL, and HOVER_END events
     *
     * @return Object targetDragValue
     */
    public Object getTargetDragValue() {
        return targetDragValue;
    }

    /**
     * The clientId of the target Panel Group. Null for DRAG_START, DRAG_CANCEL,
     * and HOVER_END events
     *
     * @return String targetClientId
     */
    public String getTargetClientId() {
        return targetClientId;
    }

    public static String getEventName(int i) {
        return names[i];
    }

    /**
     * Parse a mask value to make its valid, Used in rendering.
     *
     * @param mask
     * @return String mask
     */
    public static String parseMask(String mask) {
        if (mask == null) {
            return null;
        }
        StringTokenizer st = new StringTokenizer(mask, ",");
        StringBuffer sb = new StringBuffer();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            boolean f = false;
            for (int i = 1; i < names.length; i++) {
                token = token.trim();
                if (token.length() > 0) {
                    if (token.equalsIgnoreCase(names[i])) {
                        sb.append(i);
                        f = true;
                    }
                }
            }
            if (!f) {
                String message = "Mask value [" + token + "] in mask [" + mask +
                                 "] is not valid. Valid values are [";
                for (int ie = 1; ie < names.length; ie++) {
                    message += names[ie];
                    int next = ie + 1;
                    if (next < names.length) {
                        message += ", ";
                    }
                    message += "]";
                }
                throw new IllegalArgumentException(message);
            }
            if (st.hasMoreTokens()) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
}
