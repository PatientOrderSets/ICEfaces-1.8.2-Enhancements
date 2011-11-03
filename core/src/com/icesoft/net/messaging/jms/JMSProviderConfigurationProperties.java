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
import com.icesoft.net.messaging.MessageServiceConfigurationProperties;

import java.io.InputStream;
import java.io.IOException;

public class JMSProviderConfigurationProperties
extends MessageServiceConfigurationProperties
implements JMSProviderConfiguration, MessageServiceConfiguration {
    public JMSProviderConfigurationProperties() {
        super();
    }

    public JMSProviderConfigurationProperties(final InputStream inputStream)
    throws IOException {
        super(inputStream);
    }

    public String getInitialContextFactory() {
        return
            messageServiceConfigurationProperties.getProperty(
                INITIAL_CONTEXT_FACTORY, null);
    }

    public String getProviderURL() {
        return
            messageServiceConfigurationProperties.getProperty(
                PROVIDER_URL, null);
    }

    public String getTopicConnectionFactoryName() {
        return
            messageServiceConfigurationProperties.getProperty(
                TOPIC_CONNECTION_FACTORY_NAME, "ConnectionFactory");
    }

    public String getTopicNamePrefix() {
        return
            messageServiceConfigurationProperties.getProperty(
                TOPIC_NAME_PREFIX, null);
    }

    public String getURLPackagePrefixes() {
        return
            messageServiceConfigurationProperties.getProperty(
                URL_PACKAGE_PREFIXES, null);
    }

    public void setInitialContextFactory(final String initialContextFactory) {
        messageServiceConfigurationProperties.setProperty(
            INITIAL_CONTEXT_FACTORY, initialContextFactory);
    }

    public void setProviderURL(final String providerUrl) {
        messageServiceConfigurationProperties.setProperty(
            PROVIDER_URL, providerUrl);
    }

    public void setTopicConnectionFactoryName(
        final String topicConnectionFactoryName) {

        messageServiceConfigurationProperties.setProperty(
            TOPIC_CONNECTION_FACTORY_NAME, topicConnectionFactoryName);
    }

    public void setTopicNamePrefix(final String topicNamePrefix) {
        messageServiceConfigurationProperties.setProperty(
            TOPIC_NAME_PREFIX, topicNamePrefix);
    }

    public void setURLPackagePrefixes(final String urlPackagePrefixes) {
        messageServiceConfigurationProperties.setProperty(
            URL_PACKAGE_PREFIXES, urlPackagePrefixes);
    }
}
