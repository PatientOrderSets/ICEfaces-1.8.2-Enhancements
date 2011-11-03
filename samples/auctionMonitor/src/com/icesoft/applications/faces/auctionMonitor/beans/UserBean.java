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

package com.icesoft.applications.faces.auctionMonitor.beans;

import com.icesoft.applications.faces.auctionMonitor.ChatState;
import com.icesoft.applications.faces.auctionMonitor.Message;
import com.icesoft.applications.faces.auctionMonitor.MessageLog;
import com.icesoft.faces.async.render.SessionRenderer;
import com.icesoft.faces.context.DisposableBean;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Bean class used to store user information, as well as local information for
 * messages and viewing information
 */
public class UserBean implements DisposableBean {
    private static final String DEFAULT_NICK = "Anonymous";
    private static final String MINIMIZE_IMAGE =
            "./images/button_triangle_close.gif";
    private static final String MAXIMIZE_IMAGE =
            "./images/button_triangle_open.gif";
    private static final int NUM_MESSAGES =
            4; // Number of messages to display at once, default = 4
    private static final int FLOOD_CAP =
            500; // Minimum number of milliseconds between sent messages, default = 500

    private static final String MESSAGE_SEND = "send";

    private static final ChatState chatState = ChatState.getInstance();
    private static Log log = LogFactory.getLog(UserBean.class);

    private String autoLoad = " ";
    private String message = "";
    private String color = ChatState.DEFAULT_COLOR;
    private String nick = DEFAULT_NICK;
    private String buttonImage = MINIMIZE_IMAGE; // path to button image
    private MessageLog messageLog = getMessageLog();
    private Message[] pageLog = new Message[NUM_MESSAGES];
    private long lastMessageTime = System.currentTimeMillis();
    private int position = 0;
    private boolean minimized = true; // true = start minimized (default)
    private boolean inConversation = false; // render flag to setup chat header
    // false = have not handled initialization (default)
    private boolean leaving =
            false; // flag to track whether the user has left or not
    // this is used to stop old messages from being submit
    //  when pressing "Leave Chat"


    public UserBean() {
        SessionRenderer.addCurrentSession("auction");
    }

    public boolean isMinimized() {
        return (minimized);
    }

    public boolean getConversationStatus() {
        return (inConversation);
    }

    public String getColor() {
        return (color);
    }

    public String getMessage() {
        return (message);
    }

    public String getButtonImage() {
        return (buttonImage);
    }

    public String getNick() {
        return (nick);
    }

    public String getNickFormatted() {
        return (nick + ": ");
    }

    public String getAutoLoad() {
        if (this.autoLoad.equals(" ")) {
            this.autoLoad = "UserBean-Loaded";
        }
        return (this.autoLoad);
    }

    public String getChatStatus() {
        int numParticipants = chatState.getNumParticipants();
        if (numParticipants > 1) {
            return ("Chatting as " + nick + " (" + numParticipants +
                    " users online)");
        }
        return ("Chatting as " + nick + " (" + numParticipants +
                " user online)");
    }

    private Message getMessageAt(int index) {
        // Check for a valid index, then return the message at the index
        if ((index < messageLog.size()) && (index >= 0)) {
            return ((Message) messageLog.get(index));
        }
        return (null);
    }

    private MessageLog getMessageLog() {
        // Get the message log from the faces context
        Map applicationMap = FacesContext.getCurrentInstance().
                getExternalContext().getApplicationMap();
        messageLog = (MessageLog) applicationMap.get(LogBean.LOG_PATH);

        // Null messageLog means the LogBean is null, so create it and get the log again
        if (messageLog == null) {
            messageLog = LogBean.getInstance().storeMessageLog();
        }

        return (messageLog);
    }

    public Message[] getPageLog() {
        // Fill the array used in the page data table with messages relevant to current position
        for (int i = 0; i < NUM_MESSAGES; i++) {
            pageLog[i] = getMessageAt(position - (NUM_MESSAGES - i - 1));
        }

        return (pageLog);
    }

    public void setMessage(String pageMessage) {
        if (!leaving) {
            addMessage(pageMessage);
        }
    }

    public void setNick(String nick) {
        // Remove and HTML tags and trim the nick
        nick = cleanTags(nick).trim();

        // Check that the length is between 0 and 20
        if (nick.length() <= 0) {
            nick = DEFAULT_NICK;
        } else if (nick.length() > 20) {
            nick = nick.substring(0, 20);
        }

        // Assign the nick and enter the conversation
        this.nick = nick;
    }

    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Method to add the current message variable to the messageLog
     *
     * @param toAdd
     * @return String "send" to be used by JSF action if needed
     */
    private String addMessage(String toAdd) {
        // Check if the message is null or empty
        if ((toAdd == null) || (toAdd.trim().length() == 0)) {
            message = "";
            return MESSAGE_SEND;
        }

        // Check if the flood cap is exceeded (the user is spamming the channel)
        if ((System.currentTimeMillis() - lastMessageTime) < FLOOD_CAP) {
            lastMessageTime = System.currentTimeMillis();
            message = "";
            return MESSAGE_SEND;
        }

        // Trim length, remove tags and check for URL linkage
        toAdd = trimLength(toAdd);
        toAdd = cleanTags(toAdd);
        toAdd = urlRecognize(toAdd);

        // Add the message, update the log, and reset the last send time
        messageLog.addMessage(nick, toAdd, color);
        lastMessageTime = System.currentTimeMillis();
        message = "";
        updateMessageLog();

        return MESSAGE_SEND;
    }

    /**
     * Method to trim the user message after it exceeds a maximum length The
     * length cap is 150 characters
     *
     * @param trim message to trim
     * @return String the trimmed message
     */
    private String trimLength(String trim) {
        if (trim.length() > 150) {
            trim = (trim.substring(0, 150) + "...");
        }

        return trim;
    }

    /**
     * Method to remove any tags the user may have put in the passed string An
     * example of a troublesome tag would be a closing div tag as a nickname
     *
     * @param clean to clean of tags
     * @return String the cleaned and trimmed result
     */
    private String cleanTags(String clean) {
        clean = clean.replaceAll("<", "&lt;");
        clean = clean.replaceAll(">", "&gt;");
        clean = clean.replaceAll("&", "&amp;");

        return (clean);
    }

    /**
     * Method to recognize and URLs anywhere in the message and pad them with an
     * href link tag
     *
     * @param pad to check and pad
     * @return String padded
     */
    private String urlRecognize(String pad) {
        int index = pad.indexOf("http://");

        // Quick check to see if pad could possibly be a URL
        if (index == -1) {
            return (pad);
        }

        // Variables for possible text before / after the URL
        String before, after = "";

        // Try to cast pad as a URL
        URL checker;
        try {
            // Get the index of the first space after the URL
            int spaceIndex = pad.indexOf(" ", index);

            before = pad.substring(0, index);
            if (spaceIndex != -1) {
                after = pad.substring(spaceIndex, pad.length());
            }
            if (spaceIndex != -1) {
                pad = pad.substring(index, spaceIndex);
            } else {
                pad = pad.substring(index);
            }
            checker = new URL(pad);
        } catch (MalformedURLException mue) {
            return pad;
        }

        // Pad is a valid URL, so add html link tags and return
        pad = checker.toString();
        pad = ("<A href=\"" + pad + "\" target=\"_blank\">" + pad + "</A>");
        pad = before + pad + after;
        return (pad);
    }

    /**
     * Method to return the bottom position of the log
     *
     * @return int bottom of log (size-1)
     */
    private int bottom() {
        if (messageLog != null) {
            return (messageLog.size() - 1);
        }
        return 0;
    }

    /**
     * Method to handle initialization of entering the conversation Add this
     * UserBean to the ChatState Send a join conversation message
     *
     * @param event jsf action event.
     */
    public void enterConversation(ActionEvent event) {
        chatState.addUserChild(this);
        buttonImage = MAXIMIZE_IMAGE;
        minimized = false;
        position = bottom();
        inConversation = true;
        leaving = false;
    }

    /**
     * Method to remove this user from the conversation and reset their status
     * flag
     * @param event jsf action event.
     */
    public void leaveConversation(ActionEvent event) {
        leaving = true;
        inConversation = false;
        buttonImage = MINIMIZE_IMAGE;
        minimized = true;
        chatState.removeUserChild(this);
    }

    /**
     * Method to move the user's view upwards by a single line, by setting each
     * message to the one above it in the log
     *
     * @return String "moveUpMinor" to be used by JSF action if needed
     */
    public String moveUpMinor() {
        if ((position - 1) - (NUM_MESSAGES - 1) >= 0) {
            position--;
        }

        return ("moveUpMinor");
    }

    /**
     * Method to move the user's view downwards by a single line, by setting
     * each message to the one below it in the log
     *
     * @return String "moveDownMinor" to be used by JSF action if needed
     */
    public String moveDownMinor() {
        if (messageLog.size() > (position + 1)) {
            position++;
        }

        return ("moveDownMinor");
    }

    /**
     * Method to move the user's view to the top of the log (if necessary)
     *
     * @return String "moveToTop" to be used by JSF action if needed
     */
    public String moveToTop() {
        if (position >= NUM_MESSAGES) {
            position = (NUM_MESSAGES - 1);
        }

        return ("moveToTop");
    }

    /**
     * Method to move the user's view to the bottom of the log
     *
     * @return String "moveToBottom" to be used by JSF action if needed
     */
    public String moveToBottom() {
        position = bottom();

        return ("moveToBottom");
    }

    /**
     * Method to switch the minimized / maximized state of the chat
     *
     * @return String "switchMinimized" to be used by JSF action if needed
     */
    public String switchMinimized() {
        minimized = !minimized;

        // Change the image based on the new status, and reRender the page
        if (minimized) {
            buttonImage = MINIMIZE_IMAGE;
        } else {
            buttonImage = MAXIMIZE_IMAGE;
            position = bottom();
        }

        return ("switchMinimized");
    }

    /**
     * Method to tell the chat state to update everyone in the chat This method
     * is normally called when a message is added to the log
     */
    private void updateMessageLog() {
        //move all users to chat list bottom
        chatState.updateAll();
        SessionRenderer.render("auction");
    }


    
    /**
     * View has been disposed either by window closing or a session timeout.
     */
    public void dispose() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("UserBean Dispose called - cleaning up");
        }
        // remove the user.
        if (chatState != null){
            chatState.removeUserChild(this);
        }
    }
    
}
