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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractMessageServiceAdapter
implements MessageServiceAdapter {
    private static final Log LOG =
        LogFactory.getLog(AbstractMessageServiceAdapter.class);

    protected static final String MESSAGING_PROPERTIES =
        "com.icesoft.net.messaging.properties";

    protected MessageServiceClient messageServiceClient;
    protected MessageServiceConfiguration messageServiceConfiguration;
    protected Map topicPublisherMap = new HashMap();
    protected Map topicSubscriberMap = new HashMap();

    protected AbstractMessageServiceAdapter(
        final MessageServiceConfiguration messageServiceConfiguration)
    throws IllegalArgumentException {
        if (messageServiceConfiguration == null) {
            throw
                new IllegalArgumentException(
                    "messageServiceConfiguration is null");
        }
        this.messageServiceConfiguration = messageServiceConfiguration;
    }

    protected AbstractMessageServiceAdapter(final ServletContext servletContext)
    throws IllegalArgumentException {
        if (servletContext == null) {
            throw new IllegalArgumentException("servletContext is null");
        }
    }

    public MessageServiceClient getMessageServiceClient() {
        return messageServiceClient;
    }

    public MessageServiceConfiguration getMessageServiceConfiguration() {
        return messageServiceConfiguration;
    }

    public String[] getPublisherTopicNames() {
        return
            (String[])
                topicPublisherMap.keySet().
                    toArray(new String[topicPublisherMap.size()]);
    }

    public String[] getSubscriberTopicNames() {
        return
            (String[])
                topicSubscriberMap.keySet().
                    toArray(new String[topicSubscriberMap.size()]);
    }

    public boolean isPublishingOn(final String topicName) {
        return
            topicName != null && topicName.trim().length() != 0 &&
            topicPublisherMap.containsKey(topicName);
    }

    public boolean isSubscribedTo(final String topicName) {
        return
            topicName != null && topicName.trim().length() != 0 &&
            topicSubscriberMap.containsKey(topicName);
    }

    public void setMessageServiceClient(
        final MessageServiceClient messageServiceClient) {

        this.messageServiceClient = messageServiceClient;
    }
}
