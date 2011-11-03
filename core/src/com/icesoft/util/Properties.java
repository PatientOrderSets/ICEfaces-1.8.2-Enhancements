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
package com.icesoft.util;

import java.io.Serializable;
import java.util.Map;

public class Properties
        extends java.util.Properties
        implements Cloneable, Map, Serializable {
    private Validator keyValidator;
    private Validator valueValidator;

    public Properties() {
        this(null, new PropertyKeyValidator(), new PropertyValueValidator());
    }

    public Properties(final Properties defaults) {
        this(
                defaults, new PropertyKeyValidator(), new PropertyValueValidator());
    }

    public Properties(
            final Properties defaults, final Validator keyValidator,
            final Validator valueValidator) {

        super(defaults);
        setKeyValidator(keyValidator);
        setValueValidator(valueValidator);
    }

    public Properties(
            final Validator keyValidator, final Validator valueValidator) {

        this(null, keyValidator, valueValidator);
    }

    /**
     * <p>
     * Gets the primitive boolean value of the boolean property with the
     * specified <code>name</code> from the specified <code>properties</code>
     * collection.
     * </p>
     * <p>
     * This is a helper method for getting the value of a property contained
     * in the properties collection as a primitive boolean.
     * </p>
     *
     * @param properties the properties collection.
     * @param name       the name of the boolean property.
     * @return the property value as a primitive boolean.
     * @throws IllegalArgumentException if the specified <code>properties<code> is
     *                                  <code>null</code>.
     * @throws PropertyException        if one of the following occurs:
     *                                  <ul>
     *                                  <li>
     *                                  the property could not be found, or
     *                                  </li>
     *                                  <li>
     *                                  the property value could not be returned as a
     *                                  primitive boolean.
     *                                  </li>
     *                                  </ul>
     * @see #getBooleanProperty(java.util.Properties, String, boolean)
     */
    public static boolean getBooleanProperty(
            final java.util.Properties properties, final String name)
            throws IllegalArgumentException, PropertyException {
        if (properties != null) {
            if (properties.containsKey(name)) {
                return asBoolean(properties.get(name));
            } else {
                throw
                        new PropertyException(
                                "property not found for name: " + name);
            }
        } else {
            throw new IllegalArgumentException("properties is null");
        }
    }

    /**
     * <p>
     * Gets the primitive boolean value of the boolean property with the
     * specified <code>name</code> from the specified <code>properties</code>
     * collection. If the properties collection does not contain the desired
     * property the specified <code>defaultValue</code> is returned.
     * </p>
     * <p>
     * This is a helper method for getting the value of a property contained
     * in the properties collection as a primitive boolean.
     * </p>
     *
     * @param properties   the properties collection.
     * @param name         the name of the boolean property.
     * @param defaultValue the default value that is returned if the property could
     *                     not be found.
     * @return the property value as a primitive boolean or the
     *         <code>defaultValue</code> if the property could not be found.
     * @throws PropertyException if the property value could not be returned as a
     *                           primitive boolean.
     * @see #getBooleanProperty(java.util.Properties, String)
     */
    public static boolean getBooleanProperty(
            final java.util.Properties properties, final String name,
            final boolean defaultValue)
            throws PropertyException {
        if (properties != null && properties.containsKey(name)) {
            return asBoolean(properties.get(name));
        } else {
            return defaultValue;
        }
    }

    public boolean getBooleanProperty(final String key)
            throws PropertyException {
        if (containsKey(key)) {
            return getBooleanProperty(this, key);
        } else if (defaults != null && defaults.containsKey(key)) {
            return getBooleanProperty(defaults, key);
        } else {
            throw new PropertyException("property not found for key: " + key);
        }
    }

    public boolean getBooleanProperty(
            final String key, final boolean defaultValue)
            throws PropertyException {
        if (containsKey(key)) {
            return getBooleanProperty(this, key, defaultValue);
        } else if (defaults != null && defaults.containsKey(key)) {
            return getBooleanProperty(defaults, key, defaultValue);
        } else {
            return defaultValue;
        }
    }

    /**
     * <p>
     * Gets the primitive byte value of the byte property with the specified
     * <code>name</code> from the specified <code>properties</code>
     * collection.
     * </p>
     * <p>
     * This is a helper method for getting the value of a property contained
     * in the properties collection as a primitive byte.
     * </p>
     *
     * @param properties the properties collection.
     * @param name       the name of the byte property.
     * @return the property value as a primitive byte.
     * @throws IllegalArgumentException if the specified <code>properties<code> is
     *                                  <code>null</code>.
     * @throws PropertyException        if one of the following occurs:
     *                                  <ul>
     *                                  <li>
     *                                  the property could not be found, or
     *                                  </li>
     *                                  <li>
     *                                  the property value could not be returned as a
     *                                  primitive byte.
     *                                  </li>
     *                                  </ul>
     * @see #getByteProperty(java.util.Properties, String, byte)
     */
    public static byte getByteProperty(
            final java.util.Properties properties, final String name)
            throws IllegalArgumentException, PropertyException {
        if (properties != null) {
            if (properties.containsKey(name)) {
                return asByte(properties.get(name));
            } else {
                throw
                        new PropertyException(
                                "property not found for name: " + name);
            }
        } else {
            throw new IllegalArgumentException("properties is null");
        }
    }

    /**
     * <p>
     * Gets the primitive byte value of the byte property with the specified
     * <code>name</code> from the specified <code>properties</code>
     * collection. If the properties collection does not contain the desired
     * property the specified <code>defaultValue</code> is returned.
     * </p>
     * <p>
     * This is a helper method for getting the value of a property contained
     * in the properties collection as a primitive byte.
     * </p>
     *
     * @param properties   the properties collection.
     * @param name         the name of the byte property.
     * @param defaultValue the default value that is returned if the property could
     *                     not be found.
     * @return the property value as a primitive byte or the
     *         <code>defaultValue</code> if the property could not be found.
     * @throws PropertyException if the property value could not be returned as a
     *                           primitive boolean.
     * @see #getByteProperty(java.util.Properties, String)
     */
    public static byte getByteProperty(
            final java.util.Properties properties, final String name,
            final byte defaultValue)
            throws PropertyException {
        if (properties != null && properties.containsKey(name)) {
            return asByte(properties.get(name));
        } else {
            return defaultValue;
        }
    }

    public byte getByteProperty(final String key)
            throws PropertyException {
        if (containsKey(key)) {
            return getByteProperty(this, key);
        } else if (defaults != null && defaults.containsKey(key)) {
            return getByteProperty(defaults, key);
        } else {
            throw new PropertyException("value is not a byte: null");
        }
    }

    public byte getByteProperty(final String key, final byte defaultValue)
            throws PropertyException {
        if (containsKey(key)) {
            return getByteProperty(this, key, defaultValue);
        } else if (defaults != null && defaults.containsKey(key)) {
            return getByteProperty(defaults, key, defaultValue);
        } else {
            return defaultValue;
        }
    }

    public static double getDoubleProperty(
            final java.util.Properties properties, final String key)
            throws IllegalArgumentException, PropertyException {
        if (properties != null) {
            if (properties.containsKey(key)) {
                return asDouble(properties.get(key));
            } else {
                throw
                        new PropertyException("property not found for key: " + key);
            }
        } else {
            throw new IllegalArgumentException("properties is null");
        }
    }

    public static double getDoubleProperty(
            final java.util.Properties properties, final String key,
            final double defaultValue)
            throws PropertyException {
        if (properties != null && properties.containsKey(key)) {
            return asDouble(properties.get(key));
        } else {
            return defaultValue;
        }
    }

    public double getDoubleProperty(final String key)
            throws PropertyException {
        if (containsKey(key)) {
            return getDoubleProperty(this, key);
        } else if (defaults != null && defaults.containsKey(key)) {
            return getDoubleProperty(defaults, key);
        } else {
            throw new PropertyException("value is not a double: null");
        }
    }

    public double getDoubleProperty(final String key, final double defaultValue)
            throws PropertyException {
        if (containsKey(key)) {
            return getDoubleProperty(this, key, defaultValue);
        } else if (defaults != null && defaults.containsKey(key)) {
            return getDoubleProperty(defaults, key, defaultValue);
        } else {
            return defaultValue;
        }
    }

    public static float getFloatProperty(
            final java.util.Properties properties, final String key)
            throws IllegalArgumentException, PropertyException {
        if (properties != null) {
            if (properties.containsKey(key)) {
                return asFloat(properties.get(key));
            } else {
                throw
                        new PropertyException("property not found for key: " + key);
            }
        } else {
            throw new IllegalArgumentException("properties is null");
        }
    }

    public static float getFloatProperty(
            final java.util.Properties properties, final String key,
            final float defaultValue)
            throws PropertyException {
        if (properties != null && properties.containsKey(key)) {
            return asFloat(properties.get(key));
        } else {
            return defaultValue;
        }
    }

    public float getFloatProperty(final String key)
            throws PropertyException {
        if (containsKey(key)) {
            return getFloatProperty(this, key);
        } else if (defaults != null && defaults.containsKey(key)) {
            return getFloatProperty(defaults, key);
        } else {
            throw new PropertyException("value is not a float: null");
        }
    }

    public float getFloatProperty(final String key, final float defaultValue) {
        if (containsKey(key)) {
            return getFloatProperty(this, key, defaultValue);
        } else if (defaults != null && defaults.containsKey(key)) {
            return getFloatProperty(defaults, key, defaultValue);
        } else {
            return defaultValue;
        }
    }

    public static int getIntProperty(
            final java.util.Properties properties, final String key)
            throws IllegalArgumentException, PropertyException {
        if (properties != null) {
            if (properties.containsKey(key)) {
                return asInt(properties.get(key));
            } else {
                throw
                        new PropertyException("property not found for key: " + key);
            }
        } else {
            throw new IllegalArgumentException("properties is null");
        }
    }

    public static int getIntProperty(
            final java.util.Properties properties, final String key,
            final int defaultValue)
            throws PropertyException {
        if (properties != null && properties.containsKey(key)) {
            return asInt(properties.get(key));
        } else {
            return defaultValue;
        }
    }

    public int getIntProperty(final String key)
            throws PropertyException {
        if (containsKey(key)) {
            return getIntProperty(this, key);
        } else if (defaults != null && defaults.containsKey(key)) {
            return getIntProperty(defaults, key);
        } else {
            throw new PropertyException("value is not an int: null");
        }
    }

    public int getIntProperty(final String key, final int defaultValue)
            throws PropertyException {
        if (containsKey(key)) {
            return getIntProperty(this, key, defaultValue);
        } else if (defaults != null && defaults.containsKey(key)) {
            return getIntProperty(defaults, key, defaultValue);
        } else {
            return defaultValue;
        }
    }

    public Validator getKeyValidator() {
        return keyValidator;
    }

    public static long getLongProperty(
            final java.util.Properties properties, final String key)
            throws IllegalArgumentException, PropertyException {
        if (properties != null) {
            if (properties.containsKey(key)) {
                return asLong(properties.get(key));
            } else {
                throw
                        new PropertyException("property not found for key: " + key);
            }
        } else {
            throw new IllegalArgumentException("properties is null");
        }
    }

    public static long getLongProperty(
            final java.util.Properties properties, final String key,
            final long defaultValue)
            throws PropertyException {
        if (properties != null && properties.containsKey(key)) {
            return asLong(properties.get(key));
        } else {
            return defaultValue;
        }
    }

    public long getLongProperty(final String key)
            throws PropertyException {
        if (containsKey(key)) {
            return getLongProperty(this, key);
        } else if (defaults != null && defaults.containsKey(key)) {
            return getLongProperty(defaults, key);
        } else {
            throw new PropertyException("value is not a long: null");
        }
    }

    public long getLongProperty(final String key, final long defaultValue)
            throws PropertyException {
        if (containsKey(key)) {
            return getLongProperty(this, key, defaultValue);
        } else if (defaults != null && defaults.containsKey(key)) {
            return getLongProperty(defaults, key, defaultValue);
        } else {
            return defaultValue;
        }
    }

    public static Object getObjectProperty(
            final java.util.Properties properties, final String key) {

        return getObjectProperty(properties, key, null);
    }

    public static Object getObjectProperty(
            final java.util.Properties properties, final String key,
            final Object defaultValue) {

        if (properties == null || !properties.containsKey(key)) {
            return defaultValue;
        } else {
            return properties.get(key);
        }
    }

    public Object getObjectProperty(final String key) {
        return getObjectProperty(key, null);
    }

    public Object getObjectProperty(
            final String key, final Object defaultValue) {

        if (containsKey(key)) {
            return getObjectProperty(this, key);
        } else if (defaults != null && defaults.containsKey(key)) {
            return getObjectProperty(defaults, key);
        } else {
            return defaultValue;
        }
    }

    public String getProperty(final String key) {
        return getStringProperty(key);
    }

    public String getProperty(final String key, final String defaultValue) {
        return getStringProperty(key, defaultValue);
    }

    public static short getShortProperty(
            final java.util.Properties properties, final String key)
            throws IllegalArgumentException, PropertyException {
        if (properties != null) {
            if (properties.containsKey(key)) {
                return asShort(properties.get(key));
            } else {
                throw
                        new PropertyException("property not found for key: " + key);
            }
        } else {
            throw new IllegalArgumentException("properties is null");
        }
    }

    public static short getShortProperty(
            final java.util.Properties properties, final String key,
            final short defaultValue)
            throws PropertyException {
        if (properties != null && properties.containsKey(key)) {
            return asShort(properties.get(key));
        } else {
            return defaultValue;
        }
    }

    public short getShortProperty(final String key)
            throws PropertyException {
        if (containsKey(key)) {
            return getShortProperty(this, key);
        } else if (defaults != null && defaults.containsKey(key)) {
            return getShortProperty(defaults, key);
        } else {
            throw new PropertyException("value is not a short: null");
        }
    }

    public short getShortProperty(final String key, final short defaultValue)
            throws PropertyException {
        if (containsKey(key)) {
            return getShortProperty(this, key, defaultValue);
        } else if (defaults != null && defaults.containsKey(key)) {
            return getShortProperty(defaults, key, defaultValue);
        } else {
            return defaultValue;
        }
    }

    public static String getStringProperty(
            final java.util.Properties properties, final String key) {

        return getStringProperty(properties, key, null);
    }

    public static String getStringProperty(
            final java.util.Properties properties, final String key,
            final String defaultValue) {

        if (properties == null || !properties.containsKey(key)) {
            return defaultValue;
        } else {
            return String.valueOf(properties.get(key));
        }
    }

    public String getStringProperty(final String key) {
        return getStringProperty(key, null);
    }

    public String getStringProperty(
            final String key, final String defaultValue) {

        if (containsKey(key)) {
            return getStringProperty(this, key);
        } else if (defaults != null && defaults.containsKey(key)) {
            return getStringProperty(defaults, key);
        } else {
            return defaultValue;
        }
    }

    public Validator getValueValidator() {
        return valueValidator;
    }

    public synchronized Object put(final Object key, final Object value)
            throws PropertyException {
        validateKey(key);
        validateValue(value);
        return super.put(key, value);
    }

    public static Object setBooleanProperty(
            final java.util.Properties properties, final String key,
            final boolean value)
            throws PropertyException {
        if (properties != null) {
            return properties.put(key, new Boolean(value));
        } else {
            return null;
        }
    }

    public synchronized Object setBooleanProperty(
            final String key, final boolean value)
            throws PropertyException {
        return setBooleanProperty(this, key, value);
    }

    public static Object setByteProperty(
            final java.util.Properties properties, final String key,
            final byte value)
            throws PropertyException {
        if (properties != null) {
            return properties.put(key, new Byte(value));
        } else {
            return null;
        }
    }

    public synchronized Object setByteProperty(
            final String key, final byte value)
            throws PropertyException {
        return setByteProperty(this, key, value);
    }

    public static Object setDoubleProperty(
            final java.util.Properties properties, final String key,
            final double value)
            throws PropertyException {
        if (properties != null) {
            return properties.put(key, new Double(value));
        } else {
            return null;
        }
    }

    public synchronized Object setDoubleProperty(
            final String key, final double value)
            throws PropertyException {
        return setDoubleProperty(this, key, value);
    }

    public static Object setFloatProperty(
            final java.util.Properties properties, final String key,
            final float value)
            throws PropertyException {
        if (properties != null) {
            return properties.put(key, new Float(value));
        } else {
            return null;
        }
    }

    public synchronized Object setFloatProperty(
            final String key, final float value)
            throws PropertyException {
        return setFloatProperty(this, key, value);
    }

    public static Object setIntProperty(
            final java.util.Properties properties, final String key,
            final int value)
            throws PropertyException {
        if (properties != null) {
            return properties.put(key, new Integer(value));
        } else {
            return null;
        }
    }

    public synchronized Object setIntProperty(final String key, final int value)
            throws PropertyException {
        return setIntProperty(this, key, value);
    }

    public void setKeyValidator(final Validator keyValidator) {
        this.keyValidator = keyValidator;
    }

    public static Object setLongProperty(
            final java.util.Properties properties, final String key,
            final long value)
            throws PropertyException {
        if (properties != null) {
            return properties.put(key, new Long(value));
        } else {
            return null;
        }
    }

    public synchronized Object setLongProperty(
            final String key, final long value)
            throws PropertyException {
        return setLongProperty(this, key, value);
    }

    public static Object setObjectProperty(
            final java.util.Properties properties, final String key,
            final Object value)
            throws PropertyException {
        if (properties != null) {
            return properties.put(key, value);
        } else {
            return null;
        }
    }

    public synchronized Object setObjectProperty(
            final String key, final Object value)
            throws PropertyException {
        return setObjectProperty(this, key, value);
    }

    public synchronized Object setProperty(final String key, final String value)
            throws PropertyException {
        return setStringProperty(key, value);
    }

    public static Object setShortProperty(
            final java.util.Properties properties, final String key,
            final short value)
            throws PropertyException {
        if (properties != null) {
            return properties.put(key, new Short(value));
        } else {
            return null;
        }
    }

    public synchronized Object setShortProperty(
            final String key, final short value)
            throws PropertyException {
        return setShortProperty(this, key, value);
    }

    public static Object setStringProperty(
            final java.util.Properties properties, final String key,
            final String value)
            throws PropertyException {
        if (properties != null) {
            return properties.put(key, value);
        } else {
            return null;
        }
    }

    public synchronized Object setStringProperty(
            final String key, final String value)
            throws PropertyException {
        return setStringProperty(this, key, value);
    }

    public void setValueValidator(final Validator valueValidator) {
        this.valueValidator = valueValidator;
    }

    private static void checkIfNull(Object value) {
        if (value == null) {
            throw new PropertyException("Cannot convert null values");
        }
    }

    private static boolean asBoolean(final Object value)
            throws PropertyException {
        checkIfNull(value);
        if (value instanceof String) {
            return Boolean.valueOf((String) value).booleanValue();
        } else if (value instanceof Boolean) {
            return ((Boolean) value).booleanValue();
        } else {
            throw new PropertyException("value is not a boolean: " + value);
        }
    }

    private static byte asByte(final Object value)
            throws PropertyException {
        checkIfNull(value);
        if (value instanceof String) {
            try {
                return Byte.valueOf((String) value).byteValue();
            } catch (NumberFormatException exception) {
                throw new PropertyException("value is not a byte: " + value);
            }
        } else if (value instanceof Byte) {
            return ((Byte) value).byteValue();
        } else {
            throw new PropertyException("value is not a byte: " + value);
        }
    }

    private static double asDouble(final Object value)
            throws PropertyException {
        checkIfNull(value);
        if (value instanceof String) {
            try {
                return Double.valueOf((String) value).doubleValue();
            } catch (NumberFormatException exception) {
                throw new PropertyException("value is not a double: " + value);
            }
        } else if (value instanceof Double) {
            return ((Double) value).doubleValue();
        } else if (value instanceof Float) {
            return ((Float) value).doubleValue();
        } else {
            throw new PropertyException("value is not a double: " + value);
        }
    }

    private static float asFloat(final Object value)
            throws PropertyException {
        checkIfNull(value);
        if (value instanceof String) {
            try {
                return Float.valueOf((String) value).floatValue();
            } catch (NumberFormatException exception) {
                throw new PropertyException("value is not a float: " + value);
            }
        } else if (value instanceof Float) {
            return ((Float) value).floatValue();
        } else {
            throw new PropertyException("value is not a float: " + value);
        }
    }

    private static int asInt(final Object value)
            throws PropertyException {
        checkIfNull(value);
        if (value instanceof String) {
            try {
                return Integer.valueOf((String) value).intValue();
            } catch (NumberFormatException exception) {
                throw new PropertyException("value is not an int: " + value);
            }
        } else if (value instanceof Integer) {
            return ((Integer) value).intValue();
        } else if (value instanceof Short) {
            return ((Short) value).intValue();
        } else if (value instanceof Byte) {
            return ((Byte) value).intValue();
        } else {
            throw new PropertyException("value is not an int: " + value);
        }
    }

    private static long asLong(final Object value)
            throws PropertyException {
        checkIfNull(value);
        if (value instanceof String) {
            try {
                return Long.valueOf((String) value).longValue();
            } catch (NumberFormatException exception) {
                throw new PropertyException("value is not a long: " + value);
            }
        } else if (value instanceof Long) {
            return ((Long) value).longValue();
        } else if (value instanceof Integer) {
            return ((Integer) value).longValue();
        } else if (value instanceof Short) {
            return ((Short) value).longValue();
        } else if (value instanceof Byte) {
            return ((Byte) value).longValue();
        } else {
            throw new PropertyException("value is not a long: " + value);
        }
    }

    private static short asShort(final Object value)
            throws PropertyException {
        checkIfNull(value);
        if (value instanceof String) {
            try {
                return Short.valueOf((String) value).shortValue();
            } catch (NumberFormatException exception) {
                throw
                        new PropertyException("value is not a short: " + value);
            }
        } else if (value instanceof Short) {
            return ((Short) value).shortValue();
        } else if (value instanceof Byte) {
            return ((Byte) value).shortValue();
        } else {
            throw new PropertyException("value is not a short: " + value);
        }
    }

    private void validateKey(final Object key)
            throws PropertyException {
        if (keyValidator != null && !keyValidator.isValid(key)) {
            throw new PropertyException("invalid key: " + key);
        }
    }

    private void validateValue(final Object value)
            throws PropertyException {
        if (valueValidator != null && !valueValidator.isValid(value)) {
            throw new PropertyException("invalid value: " + value);
        }
    }

    public static class PropertyKeyValidator
            implements Validator {
        public boolean isValid(final Object object) {
            if (object instanceof String) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static class PropertyValueValidator
            implements Validator {
        public boolean isValid(final Object object) {
            /*
             * Property values can be boolean, byte, short, int, long, float,
             * double, and String.
             */
            if (object instanceof Boolean ||
                    object instanceof Byte ||
                    object instanceof Short ||
                    object instanceof Integer ||
                    object instanceof Long ||
                    object instanceof Float ||
                    object instanceof Double ||
                    object instanceof String) {

                return true;
            } else {
                return false;
            }
        }
    }
}
