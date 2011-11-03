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

import com.icesoft.faces.async.render.OnDemandRenderer;
import com.icesoft.faces.async.render.RenderManager;

import java.util.*;

/**
 * The ChatRoom class is the hub of the application.  It keeps track of
 * Participants (adding and removing) as well as the message history.
 * It is also responsible for firing server-initiated rendering calls
 * when the state of the application has changed.
 */
public class ChatRoom {

    public static final String ROOM_RENDERER_NAME = "all";
    private OnDemandRenderer roomRenderer;

    private Map participants = Collections.synchronizedMap(new HashMap());
    private LinkedList messages = new LinkedList();

    public ChatRoom() {
    }

    public void setRenderManager(RenderManager renderManager) {
        roomRenderer = renderManager.getOnDemandRenderer(ROOM_RENDERER_NAME);
    }

    public void addParticipant(Participant participant) {
        participants.put(participant.getHandle(),participant);
        roomRenderer.add(participant);
        addMessage(participant, "joined");
    }

    public void removeParticipant(Participant participant) {
        addMessage(participant, "left");
        roomRenderer.remove(participant);
        participants.remove(participant.getHandle());
    }

    public String[] getHandles() {
        return (String[]) participants.keySet().toArray(new String[participants.size()]);
    }

    public int getNumberOfParticipants() {
        return participants.size();
    }

    public List getMessages() {
        return messages;
    }

    public int getNumberOfMessages() {
        return messages.size();
    }

    protected void addMessage(Message message) {
        messages.addFirst(message.getFormattedMessage());
    }

    public void addMessage(Participant participant, String message) {
        if( participant != null && participant.getHandle() != null ){
            addMessage(new Message(participant, message));
            roomRenderer.requestRender();
        }
    }

    public boolean hasParticipant(Participant participant) {
        return participants.containsKey(participant.getHandle());
    }
}
