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

import com.icesoft.util.PropertyException;

import java.util.Enumeration;

/**
 * <p>
 *   The Message interface is the root interface of all messages. Please note
 *   that implementing classes should not be dependent on any specific
 *   implementation of a message service. Implementations should work in
 *   conjunction with the MessageServiceClient but regardless of the
 *   MessageServiceAdapter being used.
 * </p>
 * <p>
 *   To keep the desired abstraction level, this Message interfaces defines that
 *   a message consists only of properties and a body, thus not headers. Headers
 *   are used by message providers and therefore tend to be implementation
 *   specific.
 * </p>
 */
public interface Message {
    /**
     * <p>
     *   The property name for the message type: <code>message_type</code>.
     * </p>
     */
    public static final String MESSAGE_TYPE =
        "message_type";
    /**
     * <p>
     *   The property name for the message lengths:
     *   <code>message_lengths</code>.
     * </p>
     */
    public static final String MESSAGE_LENGTHS =
        "message_lengths";
    /**
     * <p>
     *   The property name for the source's servlet context path:
     *   <code>source_servletContextPath</code>.
     * </p>
     */
    public static final String SOURCE_SERVLET_CONTEXT_PATH =
        "source_servletContextPath";
    /**
     * <p>
     *   The property name for the source's node address:
     *   <code>source_nodeAddress</code>.
     * </p>
     */
    public static final String SOURCE_NODE_ADDRESS =
        "source_nodeAddress";
    /**
     * <p>
     *   The property name for the destination's servlet context path:
     *   <code>destination_servletContextPath</code>.
     * </p>
     */
    public static final String DESTINATION_SERVLET_CONTEXT_PATH =
        "destination_servletContextPath";
    /**
     * <p>
     *   The property name for the destination's node address:
     *   <code>destination_nodeAddress</code>.
     * </p>
     */
    public static final String DESTINATION_NODE_ADDRESS =
        "destination_nodeAddress";

    public void append(final Message message);

    /**
     * <p>
     *   Clears this Message's body.
     * </p>
     * <p>
     *   This Message's properties are not cleared.
     * </p>
     *
     * @see        #clearProperties()
     */
    public void clearBody();

    /**
     * <p>
     *   Clears this Message's properties.
     * </p>
     * <p>
     *   This Message's body is not cleared.
     * </p>
     *
     * @see        #clearBody()
     */
    public void clearProperties();

    /**
     * <p>
     *   Returns the value of the <code>boolean</code> property with the
     *   specified <code>name</code>.
     * </p>
     *
     * @param      name
     *                 the name of the <code>boolean</code> property.
     * @return     the <code>boolean</code> property value.
     * @throws     PropertyException
     *                 if one of the following occurs:
     *                 <ul>
     *                   <li>
     *                     the property could not be found, or
     *                   </li>
     *                   <li>
     *                     the property value is not a <code>boolean</code>.
     *                   </li>
     *                 </ul>
     * @see        #setBooleanProperty(String, boolean)
     */
    public boolean getBooleanProperty(final String name)
    throws PropertyException;

    /**
     * <p>
     *   Returns the value of the <code>byte</code> property with the specified
     *   <code>name</code>.
     * </p>
     *
     * @param      name
     *                 the name of the <code>byte</code> property.
     * @return     the <code>byte</code> property value.
     * @throws     PropertyException
     *                 if one of the following occurs:
     *                 <ul>
     *                   <li>
     *                     the property could not be found, or
     *                   </li>
     *                   <li>
     *                     the property value is not a <code>byte</code>.
     *                   </li>
     *                 </ul>
     * @see        #setByteProperty(String, byte)
     */
    public byte getByteProperty(final String name)
    throws PropertyException;

    /**
     * <p>
     *   Returns the value of the <code>double</code> property with the
     *   specified <code>name</code>.
     * </p>
     *
     * @param      name
     *                 the name of the <code>double</code> property.
     * @return     the <code>double</code> property value.
     * @throws     PropertyException
     *                 if one of the following occurs:
     *                 <ul>
     *                   <li>
     *                     the property could not be found, or
     *                   </li>
     *                   <li>
     *                     the property value is not a <code>double</code>.
     *                   </li>
     *                 </ul>
     * @see        #setDoubleProperty(String, double)
     */
    public double getDoubleProperty(final String name)
    throws PropertyException;

    /**
     * <p>
     *   Returns the value of the <code>float</code> property with the specified
     *   <code>name</code>.
     * </p>
     *
     * @param      name
     *                 the name of the <code>float</code> property.
     * @return     the <code>float</code> property value.
     * @throws     PropertyException
     *                 if one of the following occurs:
     *                 <ul>
     *                   <li>
     *                     the property could not be found, or
     *                   </li>
     *                   <li>
     *                     the property value is not a <code>float</code>.
     *                   </li>
     *                 </ul>
     * @see        #setFloatProperty(String, float)
     */
    public float getFloatProperty(final String name)
    throws PropertyException;

    /**
     * <p>
     *   Returns the value of the <code>int</code> property with the specified
     *   <code>name</code>.
     * </p>
     *
     * @param      name
     *                 the name of the <code>int</code> property.
     * @return     the <code>int</code> property value.
     * @throws     PropertyException
     *                 if one of the following occurs:
     *                 <ul>
     *                   <li>
     *                     the property could not be found, or
     *                   </li>
     *                   <li>
     *                     the property value is not an <code>int</code>.
     *                   </li>
     *                 </ul>
     * @see        #setIntProperty(String, int)
     */
    public int getIntProperty(final String name)
    throws PropertyException;

    public int getLength();

    /**
     * <p>
     *   Returns the value of the <code>long</code> property with the specified
     *   <code>name</code>.
     * </p>
     *
     * @param      name
     *                 the name of the <code>long</code> property.
     * @return     the <code>long</code> property value.
     * @throws     PropertyException
     *                 if one of the following occurs:
     *                 <ul>
     *                   <li>
     *                     the property could not be found, or
     *                   </li>
     *                   <li>
     *                     the property value is not a <code>long</code>.
     *                   </li>
     *                 </ul>
     * @see        #setLongProperty(String, long)
     */
    public long getLongProperty(final String name)
    throws PropertyException;

    /**
     * <p>
     *   Returns the value of the <code>Object</code> property with the
     *   specified <code>name</code>.
     * </p>
     * <p>
     *   This method can be used to return, in objectified format, an object
     *   that has been stored as a property in the message with the equivalent
     *   <code>setObjectProperty()</code> method call, or its equivalent
     *   primitive <code>set<i>Type</i>Property()</code> method.
     * </p>
     *
     * @param      name
     *                 the name of the <code>Object</code> property.
     * @return     the <code>Object</code> property value, in objectified format
     *             (for example, if the property was set as an <code>int</code>,
     *             an <code>Integer</code> is returned); if there is no property
     *             with the specified <code>name</code>, a <code>null</code>
     *             value is returned.
     * @see        #setObjectProperty(String, Object)
     */
    public Object getObjectProperty(final String name);

    /**
     * <p>
     *   Returns an <code>Enumeration</code> of all the property names.
     * </p>
     *
     * @return     an enumeration of all the property names.
     */
    public Enumeration getPropertyNames();

    /**
     * <p>
     *   Returns the value of the <code>short</code> property with the specified
     *   <code>name</code>.
     * </p>
     *
     * @param      name
     *                 the name of the <code>short</code> property.
     * @return     the <code>short</code> property value.
     * @throws     PropertyException
     *                 if one of the following occurs:
     *                 <ul>
     *                   <li>
     *                     the property could not be found, or
     *                   </li>
     *                   <li>
     *                     the property value is not a <code>short</code>.
     *                   </li>
     *                 </ul>
     * @see        #setShortProperty(String, short)
     */
    public short getShortProperty(final String name)
    throws PropertyException;

    /**
     * <p>
     *   Returns the value of the <code>String</code> property with the
     *   specified <code>name</code>.
     * </p>
     *
     * @param      name
     *                 the name of the <code>String</code> property.
     * @return     the <code>String</code> property value; if there is no
     *             property with the specified <code>name</code>, a
     *             <code>null</code> value is returned.
     * @see        #setStringProperty(String, String)
     */
    public String getStringProperty(final String name);

    /**
     * <p>
     *   Indicates whether a property value exists for the specified
     *   <code>name</code>.
     * </p>
     *
     * @param      name
     *                 the name of the property to test.
     * @return     <code>true</code> if the property exists, <code>false</code>
     *             if not.
     */
    public boolean propertyExists(final String name);

    /**
     * <p>
     *   Sets a <code>boolean</code> property with the specified
     *   <code>name</code> and <code>value</code> into this Message.
     * </p>
     *
     * @param      name
     *                 the name of the <code>boolean</code> property.
     * @param      value
     *                 the <code>boolean</code> property value to set.
     * @throws     PropertyException
     *                 if the specified <code>name</code> or <code>value</code>
     *                 is invalid.
     * @see        #getBooleanProperty(String)
     */
    public void setBooleanProperty(final String name, final boolean value)
    throws PropertyException;

    /**
     * <p>
     *   Sets a <code>byte</code> property with the specified <code>name</code>
     *   and <code>value</code> into this Message.
     * </p>
     *
     * @param      name
     *                 the name of the <code>byte</code> property.
     * @param      value
     *                 the <code>byte</code> property value to set.
     * @throws     PropertyException
     *                 if the specified <code>name</code> or <code>value</code>
     *                 is invalid.
     * @see        #getByteProperty(String)
     */
    public void setByteProperty(final String name, final byte value)
    throws PropertyException;

    /**
     * <p>
     *   Sets a <code>double</code> property with the specified
     *   <code>name</code> and <code>value</code> into this Message.
     * </p>
     *
     * @param      name
     *                 the name of the <code>double</code> property.
     * @param      value
     *                 the <code>double</code> property value to set.
     * @throws     PropertyException
     *                 if the specified <code>name</code> or <code>value</code>
     *                 is invalid.
     * @see        #getDoubleProperty(String)
     */
    public void setDoubleProperty(final String name, final double value)
    throws PropertyException;

    /**
     * <p>
     *   Sets a <code>float</code> property with the specified <code>name</code>
     *   and <code>value</code> into this Message.
     * </p>
     *
     * @param      name
     *                 the name of the <code>float</code> property.
     * @param      value
     *                 the <code>float</code> property value to set.
     * @throws     PropertyException
     *                 if the specified <code>name</code> or <code>value</code>
     *                 is invalid.
     * @see        #getFloatProperty(String)
     */
    public void setFloatProperty(final String name, final float value)
    throws PropertyException;

    /**
     * <p>
     *   Sets an <code>int</code> property with the specified <code>name</code>
     *   and <code>value</code> into this Message.
     * </p>
     *
     * @param      name
     *                 the name of the <code>int</code> property.
     * @param      value
     *                 the <code>int</code> property value to set.
     * @throws     PropertyException
     *                 if the specified <code>name</code> or <code>value</code>
     *                 is invalid.
     * @see        #getIntProperty(String)
     */
    public void setIntProperty(final String name, final int value)
    throws PropertyException;

    /**
     * <p>
     *   Sets a <code>long</code> property with the specified <code>name</code>
     *   and <code>value</code> into this Message.
     * </p>
     *
     * @param      name
     *                 the name of the <code>long</code> property.
     * @param      value
     *                 the <code>long</code> property value to set.
     * @throws     PropertyException
     *                 if the specified <code>name</code> or <code>value</code>
     *                 is invalid.
     * @see        #getLongProperty(String)
     */
    public void setLongProperty(final String name, final long value)
    throws PropertyException;

    /**
     * <p>
     *   Sets an <code>Object</code> property with the specified
     *   <code>name</code> and <code>value</code> into this Message.
     * </p>
     * <p>
     *   Note that this method works only for the objectified primitive object
     *   types (<code>Boolean</code>, <code>Byte</code>, <code>Double</code>,
     *   <code>Float</code>, <code>Integer</code>, <code>Long</code>, and
     *   <code>Short</code>) and <code>String</code> objects.
     * </p>
     *
     * @param      name
     *                 the name of the <code>String</code> property.
     * @param      value
     *                 the <code>String</code> property value to set.
     * @throws     PropertyException
     *                 if the specified <code>name</code> or <code>value</code>
     *                 is invalid.
     * @see        #getBooleanProperty(String)
     */
    public void setObjectProperty(final String name, final Object value)
    throws PropertyException;

    /**
     * <p>
     *   Sets a <code>short</code> property with the specified <code>name</code>
     *   and <code>value</code> into this Message.
     * </p>
     *
     * @param      name
     *                 the name of the <code>short</code> property.
     * @param      value
     *                 the <code>short</code> property value to set.
     * @throws     PropertyException
     *                 if the specified <code>name</code> or <code>value</code>
     *                 is invalid.
     * @see        #getShortProperty(String)
     */
    public void setShortProperty(final String name, final short value)
    throws PropertyException;

    /**
     * <p>
     *   Sets a <code>String</code> property with the specified
     *   <code>name</code> and <code>value</code> into this Message.
     * </p>
     *
     * @param      name
     *                 the name of the <code>String</code> property.
     * @param      value
     *                 the <code>String</code> property value to set.
     * @throws     PropertyException
     *                 if the specified <code>name</code> or <code>value</code>
     *                 is invalid.
     * @see        #getBooleanProperty(String)
     */
    public void setStringProperty(final String name, final String value)
    throws PropertyException;
}
