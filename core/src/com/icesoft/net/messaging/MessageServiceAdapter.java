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

public interface MessageServiceAdapter {
    /**
     * <p>
     *   Adds the specified <code>messageHandler</code> to this
     *   MessageServiceAdapter for the topic with the specified
     *   <code>topicName</code>.
     * </p>
     *
     * @param      messageHandler
     *                 the MessageHandler to be added.
     * @param      topicName
     *                 the topic name of the topic to be associated with the
     *                 MessageHandler.
     * @see        #removeMessageHandler(MessageHandler, String)
     */
    public void addMessageHandler(
        final MessageHandler messageHandler, final String topicName);

    /**
     * <p>
     *   Closes this MessageServiceAdapter's connection to the message service
     *   and disposes of all relevant resources.
     * </p>
     *
     * @throws     MessageServiceException
     *                 if the message service fails to close the connection due
     *                 to some internal error.
     * @see        #stop()
     */
    public void close()
    throws MessageServiceException;

    public MessageServiceClient getMessageServiceClient();

    public MessageServiceConfiguration getMessageServiceConfiguration();

    /**
     * <p>
     *   Gets the topic names of the topics on which this MessageServiceAdapter
     *   is a publisher.
     * </p>
     *
     * @return     the topic names.
     * @see        #getSubscriberTopicNames()
     */
    public String[] getPublisherTopicNames();

    /**
     * <p>
     *   Gets the topic names of the topics to which this MessageServiceAdapter
     *   is a subscriber.
     * </p>
     *
     * @return     the topic names.
     * @see        #getPublisherTopicNames()
     */
    public String[] getSubscriberTopicNames();

    /**
     * <p>
     *   Checks to see if this MessageServiceAdapter is publishing on the topic
     *   with the specified <code>topicName</code>.
     * </p>
     *
     * @param      topicName
     *                 the topic name to be checked.
     * @return     <code>true</code> if publishing on the topic with the
     *             specified <code>topicName</code>, <code>false</code> if not.
     * @see        #isSubscribedTo(String)
     */
    public boolean isPublishingOn(final String topicName);

    /**
     * <p>
     *   Checks to see if this MessageServiceAdapter is subscribed to the topic
     *   with the specified <code>topicName</code>.
     * </p>
     *
     * @param      topicName
     *                 the topic name to be checked.
     * @return     <code>true</code> if subscribed to the topic with the
     *             specified <code>topicName</code>, <code>false</code> if not.
     * @see        #isPublishingOn(String)
     */
    public boolean isSubscribedTo(final String topicName);

    /**
     * <p>
     *   Publishes the specified <code>message</code> to the topic with the
     *   specified <code>topicName</code>.
     * </p>
     * <p>
     *   If this MessageServiceAdapter is not a publisher on the topic with the
     *   <code>topicName</code> yet, it will automatically add itself as a
     *   publisher.
     * </p>
     *
     * @param      message
     *                 the message to be published.
     * @param      topicName
     *                 the topic name of the topic to publish on.
     * @throws     MessageServiceException
     */
    public void publish(final Message message, final String topicName)
    throws MessageServiceException;

    /**
     * <p>
     *   Removes the specified <code>MessageHandler</code> from this
     *   MessageServiceAdapter for the topic with the specified
     *   <code>topicName</code>.
     * </p>
     *
     * @param      messageHandler
     *                 the MessageHandler to be removed.
     * @param      topicName
     *                 the topic name of the topic associated with the
     *                 MessageHandler.
     * @see        #addMessageHandler(MessageHandler, String)
     */
    public void removeMessageHandler(
        final MessageHandler messageHandler, final String topicName);

    public void setMessageServiceClient(
        final MessageServiceClient messageServiceClient);
    
    /**
     * <p>
     *   Starts this MessageServiceAdapter's delivery of incoming messages.
     * </p>
     *
     * @throws     MessageServiceException
     *                 if the message service fails to start message delivery
     *                 due to some internal error.
     * @see        #stop()
     */
    public void start()
    throws MessageServiceException;

    /**
     * <p>
     *   Stops this MessageServiceAdapter's delivery of incoming messages. The
     *   delivery can be restarted using the <code>start()</code> method.
     * </p>
     * <p>
     *   Stopping this MessageServiceAdapter's message delivery has no effect on
     *   its ability to send messages.
     * </p>
     *
     * @throws     MessageServiceException
     *                 if the message service fails to stop message delivery due
     *                 to some internal error.
     * @see        #start()
     * @see        #close()
     */
    public void stop()
    throws MessageServiceException;

    /**
     * <p>
     *   Subscribes this MessageServiceAdapter to the topic with the specified
     *   <code>topicName</code>..
     * </p>
     *
     * @param      topicName
     *                 the topic name of the topic to subscribe to.
     * @param      messageSelector
     *                 only incoming messages with properties matching the
     *                 MessageSelector's expression are delivered.
     * @param      noLocal
     *                 if <code>true</code> inhibits the delivery of incoming
     *                 messages published by this MessageServiceAdapter's own
     *                 connection.
     * @throws     MessageServiceException
     *                 if the message service fails to subscribe due to some
     *                 internal error.
     * @see        #unsubscribe(String)
     */
    public void subscribe(
        final String topicName, final MessageSelector messageSelector,
        final boolean noLocal)
    throws MessageServiceException;

    /**
     * <p>
     *   Unsubscribes this MessageServiceAdapter from the topic with the
     *   specified <code>topicName</code>.
     * </p>
     *
     * @param      topicName
     *                 the topic name of the topic to unsubscribe from.
     * @throws     MessageServiceException
     *                 if the message service fails to unsubscribe due to some
     *                 internal error.
     * @see        #subscribe(String, MessageSelector, boolean)
     */
    public void unsubscribe(final String topicName)
    throws MessageServiceException;
}
