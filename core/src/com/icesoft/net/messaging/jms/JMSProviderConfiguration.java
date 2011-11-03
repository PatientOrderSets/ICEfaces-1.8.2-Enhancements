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
package com.icesoft.net.messaging.jms;

import com.icesoft.net.messaging.MessageServiceConfiguration;

import javax.naming.Context;

public interface JMSProviderConfiguration
extends MessageServiceConfiguration {
    /**
     * <p>
     *   The environment property name for the initial context factory to use:
     *   <code>java.naming.factory.initial</code>.
     * </p>
     */
    public static final String INITIAL_CONTEXT_FACTORY =
        Context.INITIAL_CONTEXT_FACTORY;
    /**
     * <p>
     *   The environment property name for the URL to the service provider to
     *   use: <code>java.naming.provider.url</code>.
     * </p>
     */
    public static final String PROVIDER_URL =
        Context.PROVIDER_URL;
    /**
     * <p>
     *   The environment property name for the topic connection factory name to
     *   use:
     *   <code>com.icesoft.net.messaging.jms.topicConnectionFactoryName</code>.
     * </p>
     */
    public static final String TOPIC_CONNECTION_FACTORY_NAME =
        "com.icesoft.net.messaging.jms.topicConnectionFactoryName";
    /**
     * <p>
     *   The environment property name for the topic name prefix to use when
     *   looking up the topics:
     *   <code>com.icesoft.net.messaging.jms.topicNamePrefix</code>.
     * </p>
     */
    public static final String TOPIC_NAME_PREFIX =
        "com.icesoft.net.messaging.jms.topicNamePrefix";
    /**
     * <p>
     *   The environment property name for the list of package prefixes to use
     *   when loading in URL context factories:
     *   <code>java.naming.factory.url.pkgs</code>.
     * </p>
     */
    public static final String URL_PACKAGE_PREFIXES =
        Context.URL_PKG_PREFIXES;

    /**
     * <p>
     *   Convenience method for getting the property value containing the
     *   initial context factory to use. Invoking this method is the same as
     *   invoking <code>get(INITIAL_CONTEXT_FACTORY)</code>.
     * </p>
     *
     * @return     the property value containing the initial context factory to
     *             use.
     * @see        #setInitialContextFactory(String)
     */
    public String getInitialContextFactory();

    /**
     * <p>
     *   Convenience method for getting the property value containing the URL to
     *   the service provider to use. Invoking this method is the same as
     *   invoking <code>get(PROVIDER_URL)</code>.
     * </p>
     *
     * @return     the property value containing the URL to the service provider
     *             to use.
     * @see        #setProviderURL(String)
     */
    public String getProviderURL();

    /**
     * <p>
     *   Convenience method for getting the property value containing the topic
     *   connection factory name to use. Invoking this method is the same as
     *   invoking <code>get(TOPIC_CONNECTION_FACTORY_NAME)</code>.
     * </p>
     *
     * @return     the property value containing the topic connection factory
     *             name to use.
     * @see        #setTopicConnectionFactoryName(String)
     */
    public String getTopicConnectionFactoryName();

    /**
     * <p>
     *   Convenience method for getting the property value containing the topic
     *   name prefix to use. Invoking this method is the same as invoking
     *   <code>get(TOPIC_NAME_PREFIX)</code>.
     * </p>
     *
     * @return     the property value containing the topic name prefix to use.
     * @see        #setTopicNamePrefix(String)
     */
    public String getTopicNamePrefix();

    /**
     * <p>
     *   Convenience method for getting the property value containing the URL to
     *   the list of package prefixes to use. Invoking this method is the same
     *   as invoking <code>get(URL_PACKAGE_PREFIXES)</code>.
     * </p>
     *
     * @return     the property value containing the URL to the list of package
     *             prefixes to use.
     * @see        #setURLPackagePrefixes(String)
     */
    public String getURLPackagePrefixes();

    /**
     * <p>
     *   Convenience method for setting the property value containing the
     *   initial context factory to use to the specified
     *   <code>initialContextFactory</code>. Invoking this method is the same as
     *   invoking
     *   <code>set(INITIAL_CONTEXT_FACTORY, initialContextFactory)</code>.
     * </p>
     *
     * @param      initialContextFactory
     *                 the new initial context factory to use.
     * @see        #getInitialContextFactory()
     */
    public void setInitialContextFactory(final String initialContextFactory);

    /**
     * <p>
     *   Convenience method for setting the property value containing the URL to
     *   the service provider to use to the specified <code>providerUrl</code>.
     *   Invoking this method is the same as invoking
     *   <code>set(PROVIDER_URL, providerUrl)</code>.
     * </p>
     *
     * @param      providerUrl
     *                 the new URL to the service provider to use.
     * @see        #getProviderURL()
     */
    public void setProviderURL(final String providerUrl);

    /**
     * <p>
     *   Convenience method for setting the property value containing the topic
     *   connection factory name to use to the specified
     *   <code>topicConnectionFactoryName</code>. Invoking this method is the
     *   same as invoking
     *   <code>set(TOPIC_CONNECTION_FACTORY_NAME, topicConnectionFactoryName)</code>.
     * </p>
     *
     * @param      topicConnectionFactoryName
     *                 the new topic connection factory name to use.
     * @see        #getTopicConnectionFactoryName()
     */
    public void setTopicConnectionFactoryName(
        final String topicConnectionFactoryName);

    /**
     * <p>
     *   Convenience method for setting the property value containing the topic
     *   name prefix to use to the specified <code>topicNamePrefix</code>.
     *   Invoking this method is the same as invoking
     *   <code>set(TOPIC_NAME_PREFIX, topicNamePrefix)</code>.
     * </p>
     *
     * @param      topicNamePrefix
     *                 the new topic name prefix to use.
     * @see        #getTopicNamePrefix()
     */
    public void setTopicNamePrefix(final String topicNamePrefix);

    /**
     * <p>
     *   Convenience method for setting the property value containing the URL to
     *   the list of package prefixes to use to the specified
     *   <code>urlPackagePrefixes</code>. Invoking this method is the same as
     *   invoking <code>set(URL_PACKAGE_PREFIXES, urlPackagePrefixes)</code>.
     * </p>
     *
     * @param      urlPackagePrefixes
     *                 the new URL to the list of package prefixes to use.
     * @see        #getURLPackagePrefixes()
     */
    public void setURLPackagePrefixes(final String urlPackagePrefixes);
}
