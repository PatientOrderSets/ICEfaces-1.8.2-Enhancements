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
 */
package com.icesoft.net.messaging;

/**
 * <p>
 *   The MessageHandler interface defines message handlers that can be added to
 *   the MessageServiceClient. The MessageServiceClient will pass on any Message
 *   that matches the MessageSelector to the MessageHandler in order for the
 *   Message to be handled.
 * </p>
 * <p>
 *   When the MessageHandler is added to a MessageServiceClient and delivery of
 *   incoming messages is started, the MessageSelector contained in this
 *   MessageHandler can be changed on-the-fly and is thus immediately effective
 *   for the next incoming message.
 * </p>
 *
 * @see        MessageServiceClient
 * @see        MessageSelector
 * @see        Message
 */
public interface MessageHandler {
    void addCallback(Callback callback);
    
    void addCallback(Callback callback, MessageSelector messageSelector);

    /**
     * <p>
     *   Gets the MessageSelector of this MessageHandler.
     * </p>
     *
     * @return     the MessageSelector
     * @see        #setMessageSelector(MessageSelector)
     */
    MessageSelector getMessageSelector();

    /**
     * <p>
     *   Handles the specified <code>message</code>.
     * </p>
     *
     * @param      message
     *                 the message to be handled.
     */
    void handle(Message message);

    void removeCallback(Callback callback);

    /**
     * <p>
     *   Sets the MessageSelector of this MessageHandler to the specified
     *   <code>messageSelector</code>.
     * </p>
     *
     * @param      messageSelector
     *                 the new MessageSelector.
     * @see        #getMessageSelector()
     */
    void setMessageSelector(MessageSelector messageSelector);

    public static interface Callback { /* marker interface */ }
}
