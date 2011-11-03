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
import com.icesoft.util.PropertyException;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractMessage
implements Message {
    protected Properties messageProperties =
        new Properties(
            new IdentifierValidator(),
            new Properties.PropertyValueValidator());

    protected AbstractMessage() {
        // do nothing.
    }

    public void clearProperties() {
        messageProperties.clear();
    }

    public boolean getBooleanProperty(final String name)
    throws PropertyException {
        return messageProperties.getBooleanProperty(name);
    }

    public byte getByteProperty(final String name)
    throws PropertyException {
        return messageProperties.getByteProperty(name);
    }

    public double getDoubleProperty(final String name)
    throws PropertyException {
        return messageProperties.getDoubleProperty(name);
    }

    public float getFloatProperty(final String name)
    throws PropertyException {
        return messageProperties.getFloatProperty(name);
    }

    public int getIntProperty(final String name)
    throws PropertyException {
        return messageProperties.getIntProperty(name);
    }

    public long getLongProperty(final String name)
    throws PropertyException {
        return messageProperties.getLongProperty(name);
    }

    public Object getObjectProperty(final String name) {
        return messageProperties.getObjectProperty(name);
    }

    public Enumeration getPropertyNames() {
        return Collections.enumeration(messageProperties.keySet());
    }

    public short getShortProperty(final String name)
    throws PropertyException {
        return messageProperties.getShortProperty(name);
    }

    public String getStringProperty(final String name) {
        return messageProperties.getStringProperty(name);
    }

    public boolean propertyExists(final String name) {
        return messageProperties.containsKey(name);
    }

    public void setBooleanProperty(final String name, final boolean value)
    throws PropertyException {
        messageProperties.setBooleanProperty(name, value);
    }

    public void setByteProperty(final String name, final byte value)
    throws PropertyException {
        messageProperties.setByteProperty(name, value);
    }

    public void setDoubleProperty(final String name, final double value)
    throws PropertyException {
        messageProperties.setDoubleProperty(name, value);
    }

    public void setFloatProperty(final String name, final float value)
    throws PropertyException {
        messageProperties.setFloatProperty(name, value);
    }

    public void setIntProperty(final String name, final int value)
    throws PropertyException {
        messageProperties.setIntProperty(name, value);
    }

    public void setLongProperty(final String name, final long value)
    throws PropertyException {
        messageProperties.setLongProperty(name, value);
    }

    public void setObjectProperty(final String name, final Object value)
    throws PropertyException {
        messageProperties.setObjectProperty(name, value);
    }

    public void setShortProperty(final String name, final short value)
    throws PropertyException {
        messageProperties.setShortProperty(name, value);
    }

    public void setStringProperty(final String name, final String value)
    throws PropertyException {
        messageProperties.setStringProperty(name, value);
    }

    public String toString() {
        StringBuffer _messageString = new StringBuffer();
        Enumeration _propertyNames = getPropertyNames();
        while (_propertyNames.hasMoreElements()) {
            String _propertyName = (String)_propertyNames.nextElement();
            _messageString.
                append(_propertyName).append(": ").
                    append(getObjectProperty(_propertyName)).append("\r\n");
        }
        return _messageString.append("\r\n").toString();
    }
}
