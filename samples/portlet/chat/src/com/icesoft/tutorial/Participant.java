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

import com.icesoft.faces.async.render.Renderable;
import com.icesoft.faces.component.panelseries.PanelSeries;
import com.icesoft.faces.webapp.xmlhttp.FatalRenderingException;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesState;
import com.icesoft.faces.webapp.xmlhttp.RenderingException;
import com.icesoft.faces.webapp.xmlhttp.TransientRenderingException;
import com.icesoft.faces.context.DisposableBean;
import com.icesoft.tutorial.resources.ResourceUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.event.ActionEvent;

/**
 * The Participant class stores information about an individual participant
 * in the chat room. Since this is a fairly simple example, it also stores
 * information about the state of the current conversation for the the user
 * (e.g. what part of the chat history they are currently viewing).  In a
 * more sophisticated application, this could potentially held in a separate
 * bean.
 *
 * It also implements the Renderable interface so that it can provide the
 * appropriate PersistentFacesState used for server-initiated render calls.
 */
public class Participant implements Renderable, DisposableBean {

    private static Log log = LogFactory.getLog(Participant.class);

    private String handle;
    private ChatRoom chatRoom;
    private String message;
    private PersistentFacesState state;

    private PanelSeries participantList;
    private PanelSeries messageList;

    public Participant() {
        state = PersistentFacesState.getInstance();
    }

    public String getHandle() {
        state = PersistentFacesState.getInstance();
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle.trim();
    }

    public ChatRoom getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    public String getMessage() {
        return "";
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void register(ActionEvent event) {

        if (handle != null && handle.trim().length() > 0) {
            if (!chatRoom.hasParticipant(this)) {
                chatRoom.addParticipant(this);
            } else {
                ResourceUtil.addMessage("alreadyRegistered", handle);
                handle = null;
            }
        } else {
            ResourceUtil.addMessage("badHandle");
        }
    }

    public void sendMessage(ActionEvent event) {
        if (!chatRoom.hasParticipant(this) || message == null || message.trim().length() < 1) {
            return;
        }
        chatRoom.addMessage(this, message);
    }

    public void logout(ActionEvent event) {
        chatRoom.removeParticipant(this);
        handle = null;
    }

    public PersistentFacesState getState() {
        return state;
    }

    public void renderingException(RenderingException renderingException) {
        if (log.isDebugEnabled() &&
                renderingException instanceof TransientRenderingException) {
            log.debug("Transient Rendering exception for " + handle + ":", renderingException);
        } else if (renderingException instanceof FatalRenderingException) {
            if (log.isDebugEnabled()) {
                log.debug("Fatal rendering exception for " + handle + ":", renderingException);
            }
            performCleanup();
        }
    }

    public boolean isRegistered() {
        return chatRoom.hasParticipant(this);
    }

    public void setRegistered(boolean registered) {
        //do nothing when set from outside
    }

    public String toString() {
        return super.toString() + " [" + handle + "]";
    }

    public PanelSeries getMessageList() {
        return messageList;
    }

    public void setMessageList(PanelSeries messageList) {
        this.messageList = messageList;
    }

    public PanelSeries getParticipantList() {
        return participantList;
    }

    public void setParticipantList(PanelSeries participantList) {
        this.participantList = participantList;
    }

    public boolean isPreviousParticipants() {
        return isPrevious(participantList);
    }

    public boolean isPreviousMessages() {
        return isPrevious(messageList);
    }

    private boolean isPrevious(PanelSeries list) {
        return list.getFirst() > 0;
    }

    public boolean isNextParticipants() {
        return isNext(participantList);
    }

    public boolean isNextMessages() {
        return isNext(messageList);
    }

    private boolean isNext(PanelSeries list) {
        return list.getFirst() + list.getRows() < list.getRowCount();
    }

    public void goPreviousParticipants(ActionEvent event) {
        goPrevious(participantList);
    }

    public void goPreviousMessages(ActionEvent event) {
        goPrevious(messageList);
    }

    private void goPrevious(PanelSeries list) {
        int newFirst = list.getFirst() - list.getRows();
        if (newFirst < 0) {
            newFirst = 0;
        }
        list.setFirst(newFirst);
    }

    public void goNextParticipants(ActionEvent event) {
        goNext(participantList);
    }

    public void goNextMessages(ActionEvent event) {
        goNext(messageList);
    }

    private void goNext(PanelSeries list) {
        int newFirst = list.getFirst() + list.getRows();
        if (newFirst > (list.getRowCount() - 1)) {
            newFirst = (list.getRowCount() - 1);
        }
        list.setFirst(newFirst);
    }

    protected boolean performCleanup() {
        try {
            if (chatRoom.hasParticipant(this)) {
        		logout(null);
            }
            return true;
        } catch (Exception failedCleanup) {
            if (log.isErrorEnabled()) {
                log.error("Failed to cleanup a Participant", failedCleanup);
            }
        }
        return false;
    }    
    
    /**
     * Dispose callback called due to a view closing or session
     * invalidation/timeout
     */
	public void dispose() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("Dispose Participant - cleaning up");
        }
        performCleanup();		
	}

}
