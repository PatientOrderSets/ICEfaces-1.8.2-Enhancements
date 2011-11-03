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
 *
 */
/* Original copyright
 * Copyright 2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.icesoft.faces.component.util;

import com.icesoft.faces.renderkit.dom_html_basic.FormRenderer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.FacesException;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIViewRoot;
import javax.faces.component.ValueHolder;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.el.PropertyNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class CustomComponentUtils {

    private static final String HIDDEN_COMMAND_INPUTS_SET_ATTR
            = FormRenderer.class.getName() + ".HIDDEN_COMMAND_INPUTS_SET";
    public static final String HIDDEN_COMMANDLINK_FIELD_NAME = "_link_hidden_";
    public static final String FOOTER_CLASS_ATTR = "footerClass";
    public static final String HEADER_CLASS_ATTR = "headerClass";
    public static final String[] EMPTY_STRING_ARRAY = new String[0];
    public static final String EMPTY_STRING = new String();
    private static final String HIDDEN_TREE_NAV_FIELD_NAME = "_idtn";
    private static final String HIDDEN_TREE_ACTION_FIELD_NAME = "_idta";
    private static final Log log =
            LogFactory.getLog(CustomComponentUtils.class);

    public static void restoreAncestorState(UIComponent uiComponent) {
        uiComponent.setId(uiComponent.getId());
        if ((uiComponent = uiComponent.getParent()) != null) {
            restoreAncestorState(uiComponent);
        }
    }

    public static void restoreDescendentState(UIComponent uiComponent) {
        uiComponent.setId(uiComponent.getId());
        Iterator it = uiComponent.getFacetsAndChildren();
        while (it.hasNext()) {
            UIComponent next = (UIComponent) it.next();
            restoreDescendentState(next);
        }
    }


    public static String getStringValue(FacesContext facesContext,
                                        UIComponent component) {
        try {
            if (!(component instanceof ValueHolder)) {
                throw new IllegalArgumentException("Component : " +
                                                   getPathToComponent(
                                                           component) +
                                                                      "is not a ValueHolder");
            }

            if (component instanceof EditableValueHolder) {
                Object submittedValue =
                        ((EditableValueHolder) component).getSubmittedValue();
                if (submittedValue != null) {
                    if (submittedValue instanceof String) {
                        return (String) submittedValue;
                    } else {
                        throw new IllegalArgumentException(
                                "Expected submitted value of type String for component : "
                                + getPathToComponent(component));
                    }
                }
            }

            Object value = ((ValueHolder) component).getValue();

            Converter converter = ((ValueHolder) component).getConverter();
            if (converter == null && value != null) {
                if (value instanceof String) {
                    return (String) value;
                }

                try {
                    converter = facesContext.getApplication()
                            .createConverter(value.getClass());
                }
                catch (FacesException e) {

                    log.error("No converter for class " +
                              value.getClass().getName() +
                              " found (component id=" + component.getId() +
                              ").", e);
                    // converter stays null
                }
            }

            if (converter == null) {
                if (value == null) {
                    return "";
                } else {
                    return value.toString();
                }
            } else {
                return converter.getAsString(facesContext, component, value);
            }
        }
        catch (PropertyNotFoundException ex) {
            log.error("Property not found - called by component : " +
                      getPathToComponent(component), ex);
            throw ex;
        }
    }


    public static String getPathToComponent(UIComponent component) {
        StringBuffer buf = new StringBuffer();

        if (component == null) {
            buf.append("{Component-Path : ");
            buf.append("[null]}");
            return buf.toString();
        }

        getPathToComponent(component, buf);

        buf.insert(0, "{Component-Path : ");
        buf.append("}");

        return buf.toString();
    }

    private static void getPathToComponent(UIComponent component,
                                           StringBuffer buf) {
        if (component == null) {
            return;
        }

        StringBuffer intBuf = new StringBuffer();

        intBuf.append("[Class: ");
        intBuf.append(component.getClass().getName());
        if (component instanceof UIViewRoot) {
            intBuf.append(",ViewId: ");
            intBuf.append(((UIViewRoot) component).getViewId());
        } else {
            intBuf.append(",Id: ");
            intBuf.append(component.getId());
        }
        intBuf.append("]");

        //intBuf.toString for non-1.4 JDK 1.5 compiler output
        buf.insert(0, intBuf.toString());

        if (component != null) {
            getPathToComponent(component.getParent(), buf);
        }
    }


    public static void addHiddenCommandParameter(UIComponent form,
                                                 String paramName) {
        Set set =
                (Set) form.getAttributes().get(HIDDEN_COMMAND_INPUTS_SET_ATTR);
        if (set == null) {
            set = new HashSet();
            form.getAttributes().put(HIDDEN_COMMAND_INPUTS_SET_ATTR, set);
        }
        set.add(paramName);
    }

    public static String getHiddenCommandLinkFieldName(String formName) {
        return formName + NamingContainer.SEPARATOR_CHAR
               + HIDDEN_COMMANDLINK_FIELD_NAME;
    }

    public static String getHiddenTreeExpandFieldName(String componentId,
                                                      String formName) {
        return formName + NamingContainer.SEPARATOR_CHAR + componentId
               + HIDDEN_TREE_NAV_FIELD_NAME;
    }

    public static String getHiddenTreeActionFieldName(String componentId,
                                                      String formName) {
        return formName + NamingContainer.SEPARATOR_CHAR + componentId
               + HIDDEN_TREE_ACTION_FIELD_NAME;
    }


    public static String getFormName(UIComponent component,
                                     FacesContext context) {
        //Find form
        UIComponent parent = component.getParent();
        while (parent != null && !(parent instanceof UIForm)) {
            parent = parent.getParent();
        }

        if (parent != null) {
            //link is nested inside a form
            return ((UIForm) parent).getClientId(context);
        }
        return "this";
    }

    /**
     * Split a string into an array of strings arround a character separator.
     * This  function will be efficient for short strings, for longer strings,
     * another approach may be better
     *
     * @param str       the string to be split
     * @param separator the separator character
     * @return array of string subparts
     */
    public static String[] splitShortString(String str, char separator) {
        int len;
        if (str == null || (len = str.length()) == 0) {
            return EMPTY_STRING_ARRAY;
        }

        int lastTokenIndex = 0;

        // Step 1: how many substrings?
        //      We exchange double scan time for less memory allocation
        for (int pos = str.indexOf(separator);
             pos >= 0; pos = str.indexOf(separator, pos + 1)) {
            lastTokenIndex++;
        }

        // Step 2: allocate exact size array
        String[] list = new String[lastTokenIndex + 1];

        int oldPos = 0;

        // Step 3: retrieve substrings
        for (
                int pos = str.indexOf(separator), i = 0; pos >= 0;
                pos = str.indexOf(separator, (oldPos = (pos + 1)))) {
            list[i++] = substring(str, oldPos, pos);
        }

        list[lastTokenIndex] = substring(str, oldPos, len);

        return list;
    }

    public static String substring(String str, int begin, int end) {
        if (begin == end) {
            return "";
        }

        return str.substring(begin, end);
    }

    public static String[] trim(String[] strings) {
        if (strings == null) {
            return null;
        }

        for (int i = 0, len = strings.length; i < len; i++) {
            strings[i] = strings[i].trim();
        }

        return strings;
    }

    public static void renderChildren(FacesContext facesContext,
                                      UIComponent component)
            throws IOException {
        if (component.getChildCount() > 0) {
            for (Iterator it = component.getChildren().iterator();
                 it.hasNext();) {
                UIComponent child = (UIComponent) it.next();
                renderChild(facesContext, child);
            }
        }
    }


    public static void renderChild(FacesContext facesContext, UIComponent child)
            throws IOException {
        if (!child.isRendered()) {
            return;
        }

        child.encodeBegin(facesContext);
        if (child.getRendersChildren()) {
            child.encodeChildren(facesContext);
        } else {
            renderChildren(facesContext, child);
        }
        child.encodeEnd(facesContext);
    }

    /**
     * Gets the comma separated list of visibility user roles from the given
     * component and checks if current user is in one of these roles.
     *
     * @param component a user role aware component
     * @return true if no user roles are defined for this component or user is
     *         in one of these roles, false otherwise
     */
    public static boolean isVisibleOnUserRole(UIComponent component) {
        String userRole;
        if (component instanceof UserRoleAware) {
            userRole = ((UserRoleAware) component).getVisibleOnUserRole();
        } else {
            userRole = (String) component.getAttributes()
                    .get(UserRoleAware.VISIBLE_ON_USER_ROLE_ATTR);
        }

        if (userRole == null) {
            // no restriction
            return true;
        }

        FacesContext facesContext = FacesContext.getCurrentInstance();
        StringTokenizer st = new StringTokenizer(userRole, ",");
        while (st.hasMoreTokens()) {
            if (facesContext.getExternalContext()
                    .isUserInRole(st.nextToken().trim())) {
                return true;
            }
        }
        return false;
    }


    /**
     * Gets the comma separated list of enabling user roles from the given
     * component and checks if current user is in one of these roles.
     *
     * @param component a user role aware component
     * @return true if no user roles are defined for this component or user is
     *         in one of these roles, false otherwise
     */
    public static boolean isEnabledOnUserRole(UIComponent component) {
        String userRole;
        if (component instanceof UserRoleAware) {
            userRole = ((UserRoleAware) component).getEnabledOnUserRole();
        } else {
            userRole = (String) component.getAttributes()
                    .get(UserRoleAware.ENABLED_ON_USER_ROLE_ATTR);
        }

        if (userRole == null) {
            // no restriction
            return true;
        }

        FacesContext facesContext = FacesContext.getCurrentInstance();
        StringTokenizer st = new StringTokenizer(userRole, ",");
        while (st.hasMoreTokens()) {
            if (facesContext.getExternalContext()
                    .isUserInRole(st.nextToken().trim())) {
                return true;
            }
        }
        return false;
    }


    public static Object newInstance(Class clazz)
            throws FacesException {
        try {
            return clazz.newInstance();
        }
        catch (NoClassDefFoundError e) {
            throw new FacesException(e);
        }
        catch (InstantiationException e) {
            throw new FacesException(e);
        }
        catch (IllegalAccessException e) {
            throw new FacesException(e);
        }
    }

    /**
     * Same as {@link #classForName(String)}, but throws a RuntimeException
     * (FacesException) instead of a ClassNotFoundException.
     *
     * @return the corresponding Class
     * @throws NullPointerException if type is null
     * @throws FacesException       if class not found
     */
    public static Class simpleClassForName(String type) {
        try {
            return classForName(type);
        }
        catch (ClassNotFoundException e) {
            throw new FacesException(e);
        }
    }

    public static Object newInstance(String type)
            throws FacesException {
        if (type == null) {
            return null;
        }
        return newInstance(simpleClassForName(type));
    }

    /**
     * Tries a Class.forName with the context class loader of the current thread
     * first and automatically falls back to the ClassUtils class loader (i.e.
     * the loader of the myfaces.jar lib) if necessary.
     *
     * @param type fully qualified name of a non-primitive non-array class
     * @return the corresponding Class
     * @throws NullPointerException   if type is null
     * @throws ClassNotFoundException
     */
    public static Class classForName(String type)
            throws ClassNotFoundException {
        if (type == null) {
            throw new NullPointerException("type");
        }
        try {
            // Try WebApp ClassLoader first
            return Class.forName(type,
                                 false, // do not initialize for faster startup
                                 Thread.currentThread().getContextClassLoader());
        }
        catch (ClassNotFoundException ignore) {
            // fallback: Try ClassLoader for ClassUtils (i.e. the myfaces.jar lib)
            return Class.forName(type,
                                 false, // do not initialize for faster startup
                                 CustomComponentUtils.class.getClassLoader());
        }
    }

    public static boolean isDisabledOrReadOnly(UIComponent component) {
        return isTrue(component.getAttributes().get("disabled")) ||
               isTrue(component.getAttributes().get("readOnly"));
    }

    private static boolean isTrue(Object obj) {
        if (!(obj instanceof Boolean)) {
            return false;
        }

        return ((Boolean) obj).booleanValue();
    }

    public static void decodeUIInput(FacesContext facesContext,
                                     UIComponent component,
                                     String clientId) {
        if (!(component instanceof EditableValueHolder)) {
            throw new IllegalArgumentException("Component "
                                               + component
                    .getClientId(facesContext)
                                               +
                                               " is not an EditableValueHolder");
        }
        Map paramMap = facesContext.getExternalContext()
                .getRequestParameterMap();
        if (paramMap.containsKey(clientId)) {
            //request parameter found, set submittedValue
            ((EditableValueHolder) component).setSubmittedValue(paramMap
                    .get(clientId));
        } else {
            //request parameter not found, nothing to decode - set submitted value to empty
            //if the component has not been disabled
            if (!isDisabledOrReadOnly(component)) {
                ((EditableValueHolder) component)
                        .setSubmittedValue(EMPTY_STRING);
            }
        }
    }
    
    public static void decodeUIInput(FacesContext facesContext,
                                     UIComponent component) {
        decodeUIInput(facesContext, component, component.getClientId(facesContext));
    }
    
    public static Date getDateValue(UIComponent component) {
        if (!(component instanceof ValueHolder)) {
            throw new IllegalArgumentException("Component : " +
                                               getPathToComponent(component) +
                                               "is not a ValueHolder");
        }

        if (component instanceof EditableValueHolder) {
            Object submittedValue =
                    ((EditableValueHolder) component).getSubmittedValue();
            if (submittedValue != null) {
                if (submittedValue instanceof Date) {
                    return (Date) submittedValue;
                }
                // Else, some text was typed into the inputText, which didn't
                //  validate. So, we should fall through to still use value
            }
        }

        Object value = ((ValueHolder) component).getValue();

        if (value == null || value instanceof Date) {
            return (Date) value;
        } else {
            throw new IllegalArgumentException(
                    "Expected value of type Date for component : "
                    + getPathToComponent(component));
        }
    }

    public static void copyHtmlInputTextAttributes(HtmlInputText src,
                                                   HtmlInputText dest) {
        dest.setId(src.getId());
        dest.setImmediate(src.isImmediate());
        dest.setTransient(src.isTransient());
        dest.setAccesskey(src.getAccesskey());
        dest.setAlt(src.getAlt());
        dest.setConverter(src.getConverter());
        dest.setDir(src.getDir());
        dest.setDisabled(src.isDisabled());
        dest.setLang(src.getLang());
        dest.setLocalValueSet(src.isLocalValueSet());
        dest.setMaxlength(src.getMaxlength());
        dest.setOnblur(src.getOnblur());
        dest.setOnchange(src.getOnchange());
        dest.setOnclick(src.getOnclick());
        dest.setOndblclick(src.getOndblclick());
        dest.setOnfocus(src.getOnfocus());
        dest.setOnkeydown(src.getOnkeydown());
        dest.setOnkeypress(src.getOnkeypress());
        dest.setOnkeyup(src.getOnkeyup());
        dest.setOnmousedown(src.getOnmousedown());
        dest.setOnmousemove(src.getOnmousemove());
        dest.setOnmouseout(src.getOnmouseout());
        dest.setOnmouseover(src.getOnmouseover());
        dest.setOnmouseup(src.getOnmouseup());
        dest.setOnselect(src.getOnselect());
        dest.setReadonly(src.isReadonly());
        dest.setRendered(src.isRendered());
        dest.setRequired(src.isRequired());
        dest.setSize(src.getSize());
        dest.setStyle(src.getStyle());
        dest.setStyleClass(src.getStyleClass());
        dest.setTabindex(src.getTabindex());
        dest.setTitle(src.getTitle());
        dest.setValidator(src.getValidator());
    }

    public static String setPropertyValue(String css, String propName, String value, boolean add) {
        String[] properties = css.split(";");
        boolean found = false;
        StringBuffer newCss = new StringBuffer();
        for (int i=0; i < properties.length; i++) {
            String[] property = properties[i].split(":");
            if (property.length == 2) {
                if (property[0].equalsIgnoreCase(propName)) {
                    if (add) {
                        found = true;
                        newCss.append(propName + ":"+ value + ";");
                    }
                } else {
                    newCss.append(property[0] + ":" + property[1] + ";");
                }
            }
        }
        if (add && !found) {
            newCss.append(propName + ":" + value + ";");
        }
        return newCss.toString();
    }
    
    public static String getCssPropertyValue(String css, String propName) {
        String[] properties = css.split(";");
        for (int i=0; i < properties.length; i++) {
            String[] property = properties[i].split(":");
            if (property.length == 2) {
                if (property[0].equalsIgnoreCase(propName)) {
                    return property[1];
                }
            }
        }
        return null;
    }
    
    //ensures that Ancestor's rendered property is true
    public static boolean isAncestorRendered(UIComponent component) {
        if (component == null) return true;
        if (!component.isRendered()) return false;
        if (component instanceof UIViewRoot) return true;
        return isAncestorRendered(component.getParent());   
    }
}
