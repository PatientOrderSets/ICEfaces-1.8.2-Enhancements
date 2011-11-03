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

import com.icesoft.util.Properties;

import java.io.InputStream;
import java.io.IOException;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MessageServiceConfigurationProperties
implements MessageServiceConfiguration {
    private static final String PROPERTIES =
        "com.icesoft.net.messaging.properties";
    private static final Log LOG =
        LogFactory.getLog(MessageServiceConfigurationProperties.class);

    protected Properties messageServiceConfigurationProperties =
        new Properties();

    public MessageServiceConfigurationProperties() {
        // do nothing.
    }

    public MessageServiceConfigurationProperties(final InputStream inputStream)
    throws IOException {
        messageServiceConfigurationProperties.load(inputStream);
    }

    public String get(final String name) {
        return messageServiceConfigurationProperties.getProperty(name, null);
    }

    public long getMessageMaxDelay() {
        return
            messageServiceConfigurationProperties.getLongProperty(
                MESSAGE_MAX_DELAY);
    }

    public int getMessageMaxLength() {
        return
            messageServiceConfigurationProperties.getIntProperty(
                MESSAGE_MAX_LENGTH);
    }

    public void set(final String name, final String value) {
        messageServiceConfigurationProperties.setProperty(
            name, value);
    }

    public void setMessageMaxDelay(final long messageMaxDelay) {
        messageServiceConfigurationProperties.setLongProperty(
            MESSAGE_MAX_DELAY, messageMaxDelay);
    }

    public void setMessageMaxLength(final int messageMaxLength) {
        messageServiceConfigurationProperties.setIntProperty(
            MESSAGE_MAX_LENGTH, messageMaxLength);
    }
}
