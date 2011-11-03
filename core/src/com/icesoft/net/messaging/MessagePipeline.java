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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 *   The MessagePipeline class is responsible for holding messages until one of
 *   the following conditions is met: the delay interval has been reached or the
 *   combined length of the containing messages' bodies exceeded the set limit.
 *   When one of these conditions is met the containing messages are send as one
 *   message using the configured MessageServiceAdapter.
 * </p>
 * <p>
 *   The conditions can be set using the following:
 *   <ul>
 *     <li>
 *       <code>MessageServiceConfiguration.setMessageMaxDelay(long)</code>
 *     </li>
 *     <li>
 *       <code>MessageServiceConfiguration.setMessageMaxLength(int)</code>
 *     </li>
 *     <li>
 *     </li>
 *   </ul>
 * </p>
 * <p>
 *   The MessagePipeline is intended to improve I/O throughput. As soon as a
 *   message comes in, it will not be send right away. Giving the chance to
 *   concatenate following messages into one bigger message that is being send.
 *   Please note that the maximum delay interval should not be set too high as
 *   it will affect application responsiveness. On the other hand the maximum
 *   length is merely intended to find the sweet spot for the maximum message
 *   length.
 * </p>
 *
 * @see        MessageServiceConfiguration
 * @see        Message
 * @see        MessageServiceClient
 * @see        MessageServiceAdapter
 */
public class MessagePipeline {
    private static final Log LOG = LogFactory.getLog(MessagePipeline.class);

    private MessageServiceClient messageServiceClient;
    private String topicName;
    private Message message;
    private PublishTask publishTask;

    private final Object messageLock = new Object();

    public MessagePipeline(
        final MessageServiceClient messageServiceClient,
        final String topicName) {

        this.messageServiceClient = messageServiceClient;
        this.topicName = topicName;
    }

    /**
     * <p>
     *   Gets the topic name of the topic this MessagePipeline is configured
     *   with.
     * </p>
     *
     * @return     the topic name.
     */
    public String getTopicName() {
        return topicName;
    }

    /**
     * <p>
     *   Enqueues the specified <code>message</code> into this MessagePipeline.
     *   If this MessagePipeline is empty and thus the <code>message</code> is
     *   the first Message, a PublishTask is scheduled that will kick off the
     *   actual publishment of the resulting concatenated Message. As other
     *   Messages come in they will be concatenated to the Messages already in
     *   this MessagePipeline. As soon as one of the conditions is met, the
     *   concatenated Message is send.
     * </p>
     *
     * @param      message
     *                 the Message to be enqueued.
     * @see        PublishTask
     */
    public void enqueue(final Message message) {
        synchronized (messageLock) {
            if (this.message == null) {
                this.message = message;
                publishTask = new PublishTask(this);
                if (this.message.getLength() >=
                        messageServiceClient.getMessageServiceConfiguration().
                            getMessageMaxLength()) {

                    messageServiceClient.schedule(publishTask, 0);
                } else {
                    messageServiceClient.schedule(
                        publishTask,
                        messageServiceClient.getMessageServiceConfiguration().
                            getMessageMaxDelay());
                }
            } else {
                this.message.append(message);
                if (this.message.getLength() >=
                        messageServiceClient.getMessageServiceConfiguration().
                            getMessageMaxLength()) {

                    publishTask.cancel();
                    publishTask = new PublishTask(this);
                    messageServiceClient.schedule(publishTask, 0);
                }
            }
        }
    }

    void publish() {                  
        synchronized (messageLock) {
            try {
                messageServiceClient.getMessageServiceAdapter().
                    publish(message, topicName);
                publishTask.cancel();
                publishTask = null;
                message = null;
            } catch (MessageServiceException exception) {
                LOG.error("", exception);
                if (messageServiceClient.getAdministrator().reconnectNow()) {
                    publish();
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(
                            exception.getMessage() + "\r\n" +
                                "Unable to publish message:\r\n\r\n" + message);
                    }
                    publishTask.cancel();
                    publishTask = null;
                    message = null;
                }
            }
        }
    }
}
