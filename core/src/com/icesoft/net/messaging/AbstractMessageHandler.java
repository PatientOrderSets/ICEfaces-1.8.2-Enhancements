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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractMessageHandler
implements MessageHandler {
    private static final Log LOG = LogFactory.getLog(AbstractMessageHandler.class);

    protected final Map callbackMap = new HashMap();

    protected MessageSelector messageSelector;

    protected AbstractMessageHandler() {
        this(null);
    }
                                         
    protected AbstractMessageHandler(final MessageSelector messageSelector) {
        this.messageSelector = messageSelector;
    }

    public void addCallback(final Callback callback) {
        this.addCallback(callback, null);
    }

    public void addCallback(final Callback callback, final MessageSelector messageSelector) {
        if (callback != null) {
            synchronized (callbackMap) {
                callbackMap.put(callback, messageSelector);
            }
        }
    }

    public MessageSelector getMessageSelector() {
        return messageSelector;
    }

    public void removeCallback(final Callback callback) {
        if (callback != null && callbackMap.containsKey(callback)) {
            synchronized (callbackMap) {
                if (callbackMap.containsKey(callback)) {
                    callbackMap.remove(callback);
                }
            }
        }
    }

    public void setMessageSelector(final MessageSelector messageSelector) {
        this.messageSelector = messageSelector;
    }

    protected Callback[] getCallbacks(final Message message) {
        List _callbackList = new ArrayList();
        if (message != null) {
            synchronized (callbackMap) {
                Set _entrySet = callbackMap.entrySet();
                int _size = _entrySet.size();
                Iterator _entries = _entrySet.iterator();
                for (int i = 0; i < _size; i++) {
                    Map.Entry _entry = (Map.Entry)_entries.next();
                    MessageSelector _messageSelector = (MessageSelector)_entry.getValue();
                    if (_messageSelector == null || _messageSelector.matches(message)) {
                        _callbackList.add(_entry.getKey());
                    }
                }
            }
        }
        return (Callback[])_callbackList.toArray(new Callback[_callbackList.size()]);
    }
}
