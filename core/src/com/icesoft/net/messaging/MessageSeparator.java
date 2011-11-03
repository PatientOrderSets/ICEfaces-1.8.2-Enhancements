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

import java.util.Enumeration;
import java.util.StringTokenizer;

/**
 * <p>
 *   The MessageSeparator class is a wrapper class that wraps around a
 *   MessageHandler. Its responsibility is to separate concatenated incoming
 *   messages before passing on the individual messages to the underlying
 *   MessageHandler.
 * </p>
 * <p>
 *   When adding a MessageHandler to a MessageServiceClient it automatically
 *   wraps a MessageSeparator around it before passing on the MessageSeparator
 *   to the underlying MessageServiceAdapter.
 * </p>
 *
 * @see        MessageHandler
 * @see        MessageServiceClient
 * @see        MessageServiceAdapter
 */
public class MessageSeparator
implements MessageHandler {
    private MessageHandler messageHandler;

    public MessageSeparator(final MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public void addCallback(final Callback callback) {
        // do nothing...
    }

    public void addCallback(final Callback callback, MessageSelector messageSelector) {
        // do nothing...
    }

    public MessageSelector getMessageSelector() {
        return messageHandler.getMessageSelector();
    }

    public void handle(final Message message) {
        if (message instanceof TextMessage &&
            message.propertyExists(Message.MESSAGE_LENGTHS)) {

            TextMessage _textMessage = (TextMessage)message;
            StringTokenizer _lengths =
                new StringTokenizer(
                    _textMessage.getStringProperty(
                        Message.MESSAGE_LENGTHS), ",");
            int _beginIndex;
            int _endIndex = 0;
            while (_lengths.hasMoreTokens()) {
                _beginIndex = _endIndex;
                _endIndex =
                    _beginIndex + Integer.parseInt(_lengths.nextToken());
                messageHandler.handle(
                    subMessage(_textMessage, _beginIndex, _endIndex));
            }
        } else {
            messageHandler.handle(message);
        }
    }

    public void removeCallback(final Callback callback) {
        // do nothing...
    }

    public void setMessageSelector(final MessageSelector messageSelector) {
        messageHandler.setMessageSelector(messageSelector);
    }

    public String toString() {
        return messageHandler.toString();
    }

    private static TextMessage subMessage(
        final TextMessage textMessage, final int beginIndex,
        final int endIndex) {

        TextMessage _textMessage =
            new TextMessage(
                textMessage.getText().substring(beginIndex, endIndex));
        Enumeration _propertyNames = textMessage.getPropertyNames();
        while (_propertyNames.hasMoreElements()) {
            String _propertyName = (String)_propertyNames.nextElement();
            if (!_propertyName.equalsIgnoreCase(Message.MESSAGE_LENGTHS)) {
                _textMessage.setObjectProperty(
                    _propertyName,
                    textMessage.getObjectProperty(_propertyName));
            }
        }
        return _textMessage;
    }
}
