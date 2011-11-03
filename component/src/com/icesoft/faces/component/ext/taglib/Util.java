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
/* Original Copyright:
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

package com.icesoft.faces.component.ext.taglib;

import com.icesoft.faces.component.CSS_DEFAULT;
import com.icesoft.faces.component.IceExtended;
import com.icesoft.faces.component.ext.HtmlInputText;
import com.icesoft.faces.component.ext.renderkit.FormRenderer;
import com.icesoft.faces.context.effects.Effect;
import com.icesoft.faces.context.effects.EffectBuilder;
import com.icesoft.faces.renderkit.dom_html_basic.DomBasicRenderer;
import com.icesoft.faces.util.CoreUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.faces.component.UIViewRoot;

import com.icesoft.util.pooling.CSSNamePool;

public class Util extends Object {
    private static Log log = LogFactory.getLog(Util.class);

    /**
     * Gets the comma separated list of visibility user roles from the given
     * component and checks if current user is in one of these roles.
     *
     * @param component a user role aware component
     * @return true if no user roles are defined for this component or user is
     *         in one of these roles, false otherwise
     */
    public static boolean isRenderedOnUserRole(UIComponent component) {
        if (ignoreUserRoleAttributes())  { return true; }
        String userRole;
        if (component instanceof IceExtended) {
            userRole = ((IceExtended) component).getRenderedOnUserRole();
        } else {
            userRole = (String) component.getAttributes()
                    .get(IceExtended.RENDERED_ON_USER_ROLE_ATTR);
        }

        if (log.isTraceEnabled()) {
	        log.trace("userRole in " + getRoot(component) + " is "+userRole);

        }

        //there is no restriction
        if (userRole == null) {
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

    private static javax.faces.component.UIViewRoot getRoot(UIComponent comp) {
        while(comp != null) {
            if(comp instanceof UIViewRoot)
                return (UIViewRoot) comp;
            comp = comp.getParent();
        }
        return null;
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
        if (ignoreUserRoleAttributes())  { return true; }
        String userRole;
        if (component instanceof IceExtended) {
            userRole = ((IceExtended) component).getEnabledOnUserRole();
        } else {
            userRole = (String) component.getAttributes()
                    .get(IceExtended.ENABLED_ON_USER_ROLE_ATTR);
        }

        // there is no restriction
        if (userRole == null) {
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

    private static boolean ignoreUserRoleTested = false;
    private static boolean ignoreUserRole = false;
    
    public static boolean ignoreUserRoleAttributes()  {
        if (!ignoreUserRoleTested)  {
            String ignoreAttributesParam = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getInitParameter("com.icesoft.faces.ignoreUserRoleAttributes");
            ignoreUserRole = "true".equalsIgnoreCase(ignoreAttributesParam);
            ignoreUserRoleTested = true;
        }
        return ignoreUserRole;
    }

    public static boolean isParentPartialSubmit(UIComponent uiComponent) {
        UIComponent form = DomBasicRenderer.findForm(uiComponent);
        if (form instanceof IceExtended) {
            if (((IceExtended) form).getPartialSubmit()) {
                return true;
            }
        }
        return false;
    }

    /**
     * <p>Return an Iterator over representing the available options for this
     * component, assembled from the set of UISelectItem and/or UISelectItems
     * components that are direct children of this component.  If there are no
     * such children, a zero-length array is returned.</p>
     */

    public static List getSelectItems(FacesContext context,
                                      UIComponent uiComponent) {

        List selectItems = new ArrayList();
        if (uiComponent.getChildCount() == 0) return selectItems;
        Iterator children = uiComponent.getChildren().iterator();
        while (children.hasNext()) {
            UIComponent nextSelectItemChild = (UIComponent) children.next();
            if (nextSelectItemChild instanceof UISelectItem) {
                Object selectItemValue =
                        ((UISelectItem) nextSelectItemChild).getValue();
                if (selectItemValue != null &&
                    selectItemValue instanceof SelectItem) {
                    selectItems.add(selectItemValue);
                } else {
                    selectItems.add(
                            new SelectItem(
                                    ((UISelectItem) nextSelectItemChild).getItemValue(),
                                    ((UISelectItem) nextSelectItemChild).getItemLabel(),
                                    ((UISelectItem) nextSelectItemChild).getItemDescription(),
                                    ((UISelectItem) nextSelectItemChild).isItemDisabled()));
                }
            } else if (nextSelectItemChild instanceof UISelectItems) {
                Object selectItemsValue =
                        ((UISelectItems) nextSelectItemChild).getValue();

                if (selectItemsValue != null) {
                    if (selectItemsValue instanceof SelectItem) {
                        selectItems.add(selectItemsValue);
                    } else if (selectItemsValue instanceof Collection) {
                        Iterator selectItemsIterator =
                                ((Collection) selectItemsValue).iterator();
                        while (selectItemsIterator.hasNext()) {
                            selectItems.add(selectItemsIterator.next());
                        }
                    } else if (selectItemsValue instanceof SelectItem[]) {
                        SelectItem selectItemArray[] =
                                (SelectItem[]) selectItemsValue;
                        for (int i = 0; i < selectItemArray.length; i++) {
                            selectItems.add(selectItemArray[i]);
                        }
                    } else if (selectItemsValue instanceof Map) {
                        Iterator selectItemIterator =
                                ((Map) selectItemsValue).keySet().iterator();
                        while (selectItemIterator.hasNext()) {
                            Object nextKey = selectItemIterator.next();
                            if (nextKey != null) {
                                Object nextValue =
                                        ((Map) selectItemsValue).get(nextKey);
                                if (nextValue != null) {
                                    selectItems.add(
                                            new SelectItem(
                                                    nextValue.toString(),
                                                    nextKey.toString()));
                                }
                            }
                        }
                    }
                }
            }
        }
        return selectItems;
    }


    public static void addEffect(String effect, UIComponent panel) {
        if (effect != null) {
            if (isValueReference(effect)) {
                ValueBinding vb = Util.getValueBinding(effect);
                panel.setValueBinding("effect", vb);
            } else {
                Effect fx = (Effect) panel.getAttributes().get("effect");
                if (fx == null) {
                    fx = EffectBuilder.build(effect);
                    panel.getAttributes().put("effect", fx);
                }
            }
        }
    }

    public static void addVisible(String visible, UIComponent panel) {
        if (visible != null) {
            if (isValueReference(visible)) {
                ValueBinding vb = Util.getValueBinding(visible);
                panel.setValueBinding("visible", vb);
            } else {
                Boolean boolVisible = new Boolean(visible);
                panel.getAttributes().put("visible", boolVisible);

            }
        }
    }

    public static void addLocalEffect(String effect, String name,
                                      UIComponent panel) {

        if (effect != null) {
            if (log.isTraceEnabled()) {
                log.trace("AddLocalEffect. String [" + effect + "] name [" +
                          name + "] class [" + panel.getClass().getName() +
                          "]");
            }
            if (isValueReference(effect)) {
                if (log.isTraceEnabled()) {
                    log.trace("Adding Value Binding");
                }
                ValueBinding vb = Util.getValueBinding(effect);
                panel.setValueBinding(name, vb);
            } else {
                if (log.isTraceEnabled()) {
                    log.trace("Adding Literal String");
                }

                Effect fx = (Effect) panel.getAttributes().get(name);
                if (fx == null) {
                    fx = EffectBuilder.build(effect);
                    panel.getAttributes().put(name, fx);
                }
            }
        } else {
            if (log.isTraceEnabled()) {
                log.trace("AddLocalEffect. Effect is null");
            }
        }
    }

    public static boolean isValueReference(String v) {
        if (v == null) {
            return false;
        }
        if (v.startsWith("#{") && v.endsWith("}")) {
            return true;
        }
        return false;
    }

    public static String stripPx(String s) {
        if (s == null) {
            return null;
        }
        int i = s.indexOf("px");
        if (i != -1) {
            return s.substring(0, i);
        }
        return s;
    }


    /**
     * This method should be used by the component/renderer class to
     * get the qualifiedStyleClass for a class which has been used
     * internally. This would return a qualifiedStyleClass string using the
     * following pattern.
     *      componentDefaultClass + noneAttributeClass
     * @param uiComponent
     * @param noneAttributeClass
     * @return qualifiedStyleClass string
     */
    public static String getQualifiedStyleClass(UIComponent uiComponent,
                                          String noneAttributeClass) {
        return getQualifiedStyleClass(uiComponent,
                                noneAttributeClass,
                                null,
                                null,
                                false,
                                true,
                                "");

    }

    /**
     * This method should be used by the renderer/component class that
     * can be disabled, to get the qualifiedStyleClass for a class which has
     * been used internally. This would return a qualifiedStyleClass string
     * using the following pattern.
     *      defaulStyleClass + noneAttributeClass[-dis]
     * @param uiComponent
     * @param noneAttributeClass
     * @return qualifiedStyleClass string
     */
    public static String getQualifiedStyleClass(UIComponent uiComponent,
                                          String noneAttributeClass,
                                          boolean disabled) {
        return getQualifiedStyleClass(uiComponent,
                                noneAttributeClass,
                                null,
                                null,
                                disabled,
                                true,
                                "");

    }

    /**
     * This method should be used by the renderer/component class to get the
     * qualifiedStyleClass for a "styleClass" or any other class related attribute
     * on the component (e.g.) inputText on inputFile component.
     * This would return a qualifiedStyleClass string using the following pattern.
     *      defaulStyleClass  [userDefinedStyleClass]
     * @param uiComponent
     * @param userDefinedStyleClass
     * @param defaulStyleClass
     * @param classAttributeName
     * @return qualifiedStyleClass string
     */

    public static String getQualifiedStyleClass(UIComponent uiComponent,
                                          String userDefinedStyleClass,
                                          String defaulStyleClass,
                                          String classAttributeName) {
        return getQualifiedStyleClass(uiComponent,
                userDefinedStyleClass,
                defaulStyleClass,
                classAttributeName,
                false,
                false,
                "");

    }

    /**
     * This method should be used by the renderer/component class to get the
     * qualifiedStyleClass for a "styleClass" or any other class related attribute
     * on the component (e.g.) inputText on inputFile component.
     * This would return a qualifiedStyleClass string using the following pattern.
     *      defaulStyleClass  [userDefinedStyleClass]
     * @param uiComponent
     * @param userDefinedStyleClass
     * @param defaulStyleClass
     * @param classAttributeName
     * @return qualifiedStyleClass string
     */

    public static String getQualifiedStyleClass(UIComponent uiComponent,
                                          String userDefinedStyleClass,
                                          String defaulStyleClass,
                                          String classAttributeName,
                                          String portletClass) {
        return getQualifiedStyleClass(uiComponent,
                userDefinedStyleClass,
                defaulStyleClass,
                classAttributeName,
                false,
                false,
                portletClass);

    }

    /**
     * This method should be used by the renderer/component class that can be
     * disabled, to get the qualifiedStyleClass for a "styleClass" or any other
     * class related attribute on the component (e.g.) "inputText" attribute on
     * "inputFile" component. This methods returns a qualifiedStyleClass string
     * using the following pattern.
     *      defaulStyleClass[-dis]  [userDefinedStyleClass[-dis]]
     * @param uiComponent
     * @param userDefinedStyleClass
     * @param defaulStyleClass
     * @param classAttributeName
     * @return qualifiedStyleClass string
     */
    public static String getQualifiedStyleClass(UIComponent uiComponent,
                                          String userDefinedStyleClass,
                                          String defaulStyleClass,
                                          String classAttributeName,
                                          boolean disabled,
                                          String portletClass) {
        return getQualifiedStyleClass(uiComponent,
                userDefinedStyleClass,
                defaulStyleClass,
                classAttributeName,
                disabled,
                false,
                portletClass);

    }
    /**
     * This method should be used by the renderer/component class that can be
     * disabled, to get the qualifiedStyleClass for a "styleClass" or any other
     * class related attribute on the component (e.g.) "inputText" attribute on
     * "inputFile" component. This methods returns a qualifiedStyleClass string
     * using the following pattern.
     *      defaulStyleClass[-dis]  [userDefinedStyleClass[-dis]]
     * @param uiComponent
     * @param userDefinedStyleClass
     * @param defaulStyleClass
     * @param classAttributeName
     * @return qualifiedStyleClass string
     */
    public static String getQualifiedStyleClass(UIComponent uiComponent,
                                          String userDefinedStyleClass,
                                          String defaulStyleClass,
                                          String classAttributeName,
                                          boolean disabled) {
        return getQualifiedStyleClass(uiComponent,
                userDefinedStyleClass,
                defaulStyleClass,
                classAttributeName,
                disabled,
                false,
                "");

    }

      private static String getQualifiedStyleClass(UIComponent uiComponent,
                                          String userDefinedStyleClass,
                                          String defaulStyleClass,
                                          String classAttributeName,
                                          boolean disabled,
                                          boolean isNoneAttributeClass,
                                          String portletClass) {
          if (isNoneAttributeClass) {
              //1- This requested class is used internally by the component and
              //not visible to the developer (or can not be set using an attribute)
              return CSSNamePool.get(appendLocalClassToStyleClass(uiComponent, userDefinedStyleClass));
          }

          String disabledSuffix = disabled ? CSS_DEFAULT.DIS_SUFFIX: "";
          String styleClass = null;

          if ("styleClass".equalsIgnoreCase(classAttributeName)) {
              //2- The following code is responsible to assembled the qualified
              //value for "styleClass" attribute using the following syntax:
              //         defaultStyleClass[-dis] [userDefinedStyleClass[-dis]]
              styleClass = getValue(uiComponent,
                      userDefinedStyleClass,
                      "styleClass");
              styleClass = defaulStyleClass +
              disabledSuffix+ CoreUtils.getPortletStyleClass(portletClass) +
              " " + ((styleClass!=null)? styleClass + disabledSuffix :"");
              return CSSNamePool.get(styleClass.trim());
          } else {
              //3- the following code is to deal with other style class
              //attributes that has been defined on the components (e.g.)
              // "inputTextClass" attribute on "inputFile" component.
              styleClass = String.valueOf(uiComponent.getAttributes()
                      .get("styleClass"));
              String subClass = getValue(uiComponent,
                      userDefinedStyleClass,
                      classAttributeName);

              String newClass = appendLocalClassToStyleClass(uiComponent,
                                      defaulStyleClass);
              if (subClass != null) {
                  newClass += " " + subClass + disabledSuffix;
              }
              return CSSNamePool.get(newClass.trim());
          }
       }

    private static String getValue(UIComponent uiComponent,
                                    String value,
                                    String attributeName) {
        if (value != null) {
            return value;
        }
        ValueBinding vb = uiComponent.getValueBinding(attributeName);
        return vb != null ?
               (String) vb.getValue(FacesContext.getCurrentInstance()) :
               null;
    }

    private static String appendLocalClassToStyleClass(UIComponent uiComponent,
                                        String localClass) {
        String styleClass = String.valueOf(uiComponent.getAttributes()
                .get("styleClass")).trim();
        String[] classes = styleClass.split(" ");
        StringBuffer name = new StringBuffer();
        for (int i =0; i <classes.length; i++) {
            if (classes[i] != null && classes[i].startsWith("portlet-")) { // ICE-2302
                name.append(classes[i]);
            }
            else if (classes[i] != null && classes[i].endsWith(CSS_DEFAULT.DIS_SUFFIX)) {
                name.append(classes[i].replaceAll(CSS_DEFAULT.DIS_SUFFIX,
                        localClass + CSS_DEFAULT.DIS_SUFFIX));
            } else {
                name.append(classes[i]+ localClass);
            }
            name.append(" ");
        }
       return name.toString().trim();
    }

    public static String getClassName(UIComponent uiComponent,
                                      String styleClass,
                                      String styleClassAsString,
                                      String defaultStyleClass) {
        if (styleClass != null) {
            return styleClass;
        }
        ValueBinding vb = uiComponent.getValueBinding(styleClassAsString);
        return vb != null ?
               (String) vb.getValue(FacesContext.getCurrentInstance()) :
               defaultStyleClass;
    }

    public static ValueBinding getValueBinding(String valueRef) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        return facesContext.getApplication().createValueBinding(valueRef);
    }

    public static boolean isEventSource(FacesContext facesContext, UIComponent uiComponent) {
        Object focusId = facesContext.getExternalContext()
                .getRequestParameterMap().get(FormRenderer.getFocusElementId());
        if (focusId != null) {
            if (focusId.toString()
                    .equals(uiComponent.getClientId(facesContext))) {
                ((HtmlInputText) uiComponent).setFocus(true);
            } else {
                ((HtmlInputText) uiComponent).setFocus(false);
            }
        }
        Object componenetId = facesContext.getExternalContext()
                .getRequestParameterMap().get("ice.event.captured");
        if (componenetId != null) {
            if (componenetId.toString()
                    .equals(uiComponent.getClientId(facesContext))) {
                return true;
            } else {

                return false;
            }
        }
        return false;
    }
} // end of class Util
