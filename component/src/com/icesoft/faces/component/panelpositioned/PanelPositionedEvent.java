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

package com.icesoft.faces.component.panelpositioned;

import javax.faces.component.UIComponent;
import javax.faces.el.MethodBinding;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;
import javax.faces.event.PhaseId;

import com.icesoft.faces.component.panelseries.UISeries;

import java.util.ArrayList;
import java.util.List;

/**
 * Event fired by positioned panel events
 */
public class PanelPositionedEvent extends FacesEvent{

    private MethodBinding listener;
    private int index;
    private int oldIndex = -1;
    private int type;
    private List oldList;
    private List newList;


    public static int TYPE_ADD = 1;
    public static int TYPE_REMOVE = 2;
    public static int TYPE_MOVE = 3;
    private PhaseId phaseId = PhaseId.UPDATE_MODEL_VALUES;

    public PanelPositionedEvent(UIComponent uiComponent, MethodBinding listener,
                                int eventType, int index, int oldIndex, List oldList, List newList) {
        super(uiComponent);
        this.listener = listener;
        this.type = eventType;
        this.index = index;
        this.oldIndex = oldIndex;
        this.oldList = oldList;
        this.newList = newList;

        
    }


    public PhaseId getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(PhaseId phaseId) {
        this.phaseId = phaseId;
    }



    public boolean isAppropriateListener(FacesListener facesListener) {
        return false;
    }

    public void processListener(FacesListener facesListener) {
       
    }

    public void process(){
        try {
            if (((UISeries)this.source).getValue() instanceof List) {
               oldList.clear();
               oldList.addAll(newList);               
           } else if (((UISeries)this.source).getValue() instanceof Object[]) {
               Object[] newVal = (Object[])((UISeries)this.source).getValue();
               for (int i = 0; i < newVal.length; i++) {
                   newVal[i] = newList.get(i);
               }
               ((PanelPositioned)this.source).setArrayValue(newVal);
           }

        } catch (Exception e) {}
    }

    public MethodBinding getListener() {
        return listener;
    }

    public void setListener(MethodBinding listener) {
        this.listener = listener;
    }

    /**
     * Index added, removed or changed
     *
     * @return int index
     */
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * The other index when the event type is MOVE. Otherwise its -1.
     *
     * @return int oldIndex
     */
    public int getOldIndex() {
        return oldIndex;
    }

    public void setOldIndex(int oldIndex) {
        this.oldIndex = oldIndex;
    }

    /**
     * Type of event cna be Added, Removed or changed
     *
     * @return int type
     */
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
