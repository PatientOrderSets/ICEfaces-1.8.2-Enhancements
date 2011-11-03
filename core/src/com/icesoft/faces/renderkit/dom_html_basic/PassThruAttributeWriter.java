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

package com.icesoft.faces.renderkit.dom_html_basic;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import com.icesoft.faces.context.effects.CurrentStyle;
import com.icesoft.faces.webapp.parser.ImplementationUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class is used by ReponseWriter based renderers to render html
 * pass thru attributes.
 *
 * @author gmccleary
 */
public class PassThruAttributeWriter {
    
    public static final String[] EMPTY_STRING_ARRAY = {};
    
    private static List passThruAttributeNames = new ArrayList();
    private static List booleanPassThruAttributeNames = new ArrayList();

    static {
        passThruAttributeNames.add("accept");
        passThruAttributeNames.add("accesskey");
        passThruAttributeNames.add("alt");
        passThruAttributeNames.add("bgcolor");
        passThruAttributeNames.add("border");
        passThruAttributeNames.add("cellpadding");
        passThruAttributeNames.add("cellspacing");
        passThruAttributeNames.add("charset");
        passThruAttributeNames.add("cols");
        passThruAttributeNames.add("coords");
        passThruAttributeNames.add("dir");
        passThruAttributeNames.add("enctype");
        passThruAttributeNames.add("frame");
        passThruAttributeNames.add("height");
        passThruAttributeNames.add("hreflang");
        passThruAttributeNames.add("lang");
        passThruAttributeNames.add("longdesc");
        passThruAttributeNames.add("maxlength");
        passThruAttributeNames.add("onblur");
        passThruAttributeNames.add("onchange");
        passThruAttributeNames.add("onclick");
        passThruAttributeNames.add("ondblclick");
        passThruAttributeNames.add("onfocus");
        passThruAttributeNames.add("onkeydown");
        passThruAttributeNames.add("onkeypress");
        passThruAttributeNames.add("onkeyup");
        passThruAttributeNames.add("onload");
        passThruAttributeNames.add("onmousedown");
        passThruAttributeNames.add("onmousemove");
        passThruAttributeNames.add("onmouseout");
        passThruAttributeNames.add("onmouseover");
        passThruAttributeNames.add("onmouseup");
        passThruAttributeNames.add("onreset");
        passThruAttributeNames.add("onselect");
        passThruAttributeNames.add("onsubmit");
        passThruAttributeNames.add("onunload");
        passThruAttributeNames.add("rel");
        passThruAttributeNames.add("rev");
        passThruAttributeNames.add("rows");
        passThruAttributeNames.add("rules");
        passThruAttributeNames.add("shape");
        passThruAttributeNames.add("size");
        passThruAttributeNames.add("style");
        passThruAttributeNames.add("summary");
        passThruAttributeNames.add("tabindex");
        passThruAttributeNames.add("target");
        passThruAttributeNames.add("title");
        passThruAttributeNames.add("usemap");
        passThruAttributeNames.add("width");
        passThruAttributeNames.add("width");
        passThruAttributeNames.add("onclickeffect");
        passThruAttributeNames.add("ondblclickeffect");
        passThruAttributeNames.add("onmousedowneffect");
        passThruAttributeNames.add("onmouseupeffect");
        passThruAttributeNames.add("onmousemoveeffect");
        passThruAttributeNames.add("onmouseovereffect");
        passThruAttributeNames.add("onmouseouteffect");
        passThruAttributeNames.add("onchangeeffect");
        passThruAttributeNames.add("onreseteffect");
        passThruAttributeNames.add("onsubmiteffect");
        passThruAttributeNames.add("onkeypresseffect");
        passThruAttributeNames.add("onkeydowneffect");
        passThruAttributeNames.add("onkeyupeffect");
        passThruAttributeNames.add("autocomplete");

        booleanPassThruAttributeNames.add("disabled");
        booleanPassThruAttributeNames.add("readonly");
        booleanPassThruAttributeNames.add("ismap");
    }

    /**
     * Write pass thru attributes associated with the UIComponent parameter. The
     * excludedAttributes argument is a String array of the names of attributes
     * to omit. Do not render attributes contained in the excludedAttributes
     * argument.
     *
     * @param writer
     * @param uiComponent
     * @param excludedAttributes attributes to exclude
     * @throws IOException
     * @deprecated
     */
    public static void renderAttributes(ResponseWriter writer,
                                        UIComponent uiComponent,
                                        String[] excludedAttributes)
            throws IOException {
        renderNonBooleanAttributes(writer, uiComponent, excludedAttributes);
        renderBooleanAttributes(writer, uiComponent, excludedAttributes);
    }

    /**
     * ToDo: Render the icefaces onfocus handler to the root element. This
     * should be restricted to input type elements and commandlinks.
     *
     * @param writer
     * @throws IOException 
     */
    public static void renderOnFocus(ResponseWriter writer)
            throws IOException {
        writer.writeAttribute("onfocus", "setFocus(this.id);", "onfocus");
    }

    /**
     * ToDo: Render the icefaces onblur handler to the root element. This should
     * be restricted to input type elements and commandlinks.
     *
     * @param writer
     * @throws IOException 
     */
    public static void renderOnBlur(ResponseWriter writer)
            throws IOException {
        writer.writeAttribute("onfocus", "setFocus('');", "onfocus");
    }

    public static void renderBooleanAttributes(
            ResponseWriter writer, UIComponent uiComponent,
            String[] excludedAttributes)
            throws IOException {

        if (writer == null) {
            throw new FacesException("Null pointer exception");
        }
        if (uiComponent == null) {
            throw new FacesException("Null pointer exception");
        }

        List excludedAttributesList = null;
        if (excludedAttributes != null && excludedAttributes.length > 0)
            excludedAttributesList = Arrays.asList(excludedAttributes);

        Object nextPassThruAttributeName;
        Object nextPassThruAttributeValue = null;
        Iterator passThruNameIterator =
                booleanPassThruAttributeNames.iterator();
        boolean primitiveAttributeValue;

        while (passThruNameIterator.hasNext()) {
            nextPassThruAttributeName = (passThruNameIterator.next());
            if (excludedAttributesList != null) {
                if (excludedAttributesList.contains(nextPassThruAttributeName))
                    continue;
            }
            nextPassThruAttributeValue = uiComponent.getAttributes().get(
                    nextPassThruAttributeName);
            if (nextPassThruAttributeValue != null) {
                if (nextPassThruAttributeValue instanceof Boolean) {
                    primitiveAttributeValue = ((Boolean)
                            nextPassThruAttributeValue).booleanValue();
                } else {
                    if (!(nextPassThruAttributeValue instanceof String)) {
                        nextPassThruAttributeValue =
                                nextPassThruAttributeValue.toString();
                    }
                    primitiveAttributeValue = (new Boolean((String)
                            nextPassThruAttributeValue)).booleanValue();
                }
                if (primitiveAttributeValue) {
                    writer.writeAttribute(nextPassThruAttributeName.toString(),
                                          nextPassThruAttributeValue,
                                          nextPassThruAttributeName.toString());
                }

            }
        }
    }

    private static void renderNonBooleanAttributes(
            ResponseWriter writer, UIComponent uiComponent,
            String[] excludedAttributes)

            throws IOException {
        if (writer == null) {
            throw new FacesException("Null pointer exception");
        }

        if (uiComponent == null) {
            throw new FacesException("Component instance is null");
        }

        List excludedAttributesList = null;
        if (excludedAttributes != null && excludedAttributes.length > 0)
            excludedAttributesList = Arrays.asList(excludedAttributes);

        Object nextPassThruAttributeName = null;
        Object nextPassThruAttributeValue = null;
        Iterator passThruNameIterator = passThruAttributeNames.iterator();
        while (passThruNameIterator.hasNext()) {
            nextPassThruAttributeName = (passThruNameIterator.next());
            if (excludedAttributesList != null) {
                if (excludedAttributesList.contains(nextPassThruAttributeName))
                    continue;
            }
            nextPassThruAttributeValue =
                    uiComponent.getAttributes().get(nextPassThruAttributeName);
            // Only render non-null attributes.
            // Some components have attribute values
            // set to the Wrapper classes' minimum value - don't render 
            // an attribute with this sentinel value.
            if (nextPassThruAttributeValue != null &&
                !attributeValueIsSentinel(nextPassThruAttributeValue)) {
                writer.writeAttribute(
                        nextPassThruAttributeName.toString(),
                        nextPassThruAttributeValue,
                        nextPassThruAttributeValue.toString());
            }
        }
    }

    /**
     * Determine whether any of the attributes defined for the UIComponent
     * instance are pass thru attributes.
     *
     * @param uiComponent
     * @return true if the UIComponent parameter has one or more attributes
     *         defined that are pass thru attributes
     */
    public static boolean passThruAttributeExists(UIComponent uiComponent) {
        if (uiComponent == null) {
            return false;
        }
        Map componentAttributes = uiComponent.getAttributes();
        if (componentAttributes.size() <= 0) {
            return false;
        }
        if (componentAttributesIncludePassThruAttribute(componentAttributes,
                                                        passThruAttributeNames))
        {
            return true;
        }
        if (componentAttributesIncludePassThruAttribute(componentAttributes,
                                                        booleanPassThruAttributeNames))
        {
            return true;
        }
        return false;
    }

    private static boolean attributeValueIsSentinel(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean) {
            if (((Boolean) value).booleanValue() == false) {
                return true;
            }
            return false;
        }
        if (value instanceof Number) {
            if (value instanceof Integer) {
                if (((Integer) value).intValue() == Integer.MIN_VALUE) {
                    return true;
                }
                return false;
            }
            if (value instanceof Long) {
                if (((Long) value).longValue() == Long.MIN_VALUE) {
                    return true;
                }
                return false;
            }
            if (value instanceof Short) {
                if (((Short) value).shortValue() == Short.MIN_VALUE) {
                    return true;
                }
                return false;
            }
            if (value instanceof Float) {
                if (((Float) value).floatValue() == Float.MIN_VALUE) {
                    return true;
                }
                return false;
            }
            if (value instanceof Double) {
                if (((Double) value).doubleValue() == Double.MIN_VALUE) {
                    return true;
                }
                return false;
            }
            if (value instanceof Byte) {
                if (((Byte) value).byteValue() == Byte.MIN_VALUE) {
                    return true;
                }
                return false;
            }
        }
        if (value instanceof Character) {
            if (((Character) value).charValue() == Character.MIN_VALUE) {
                return true;
            }
            return false;
        }
        return false;
    }

    private static boolean componentAttributesIncludePassThruAttribute(
            Map componentAttributes, List passThru) {
        Object componentAttributeKey;
        Object componentAttributeValue;
        Iterator attributeKeys = componentAttributes.keySet().iterator();
        while (attributeKeys.hasNext()) {
            componentAttributeKey = attributeKeys.next();
            if (passThru.contains(componentAttributeKey)) {
                componentAttributeValue =
                        componentAttributes.get(componentAttributeKey);
                if ((componentAttributeValue != null) &&
                    (componentAttributeValue != "")) {
                    return true;
                }
            }
        }
        return false;
    }

    static final List getpassThruAttributeNames() {
        return passThruAttributeNames;
    }
    
    /**
     * Write pass thru attributes associated with the UIComponent parameter. 
     *
     * @param writer
     * @param uiComponent
     * @throws IOException
     */
    public static void renderHtmlAttributes(ResponseWriter writer,
                                        UIComponent uiComponent,
                                        String[] htmlAttributes)
            throws IOException {
        if (writer == null) {
            throw new FacesException("Null pointer exception");
        }

        if (uiComponent == null) {
            throw new FacesException("Component instance is null");
        }

        // For now, we just support accelerating h: component rendering
        boolean stockAttribTracking =
            ImplementationUtil.isStockAttributeTracking();
        boolean attribTracking =
            stockAttribTracking &&
            uiComponent.getClass().getName().startsWith("javax.faces.component.");
        List attributesThatAreSet = (!attribTracking) ? null :
            (List) uiComponent.getAttributes().get(
                "javax.faces.component.UIComponentBase.attributesThatAreSet");
        
        if (!attribTracking ||
            (attributesThatAreSet != null &&
             attributesThatAreSet.size() > 0)) {

            String nextPassThruAttributeName = null;
            Object nextPassThruAttributeValue = null;

            for (int i = 0; i < htmlAttributes.length; i++) {
                nextPassThruAttributeName = htmlAttributes[i];
                if (attribTracking &&
                    (attributesThatAreSet == null ||
                     !attributesThatAreSet.contains(nextPassThruAttributeName))) {
                    continue;
                }
                nextPassThruAttributeValue =
                    uiComponent.getAttributes().get(nextPassThruAttributeName);
                // Only render non-null attributes.
                // Some components have integer attribute values
                // set to the Wrapper classes' minimum value - don't render
                // an attribute with this sentinel value.
                if (nextPassThruAttributeValue != null &&
                    !valueIsIntegerSentinelValue(nextPassThruAttributeValue)) {
                    writer.writeAttribute(
                        nextPassThruAttributeName,
                        nextPassThruAttributeValue,
                        nextPassThruAttributeValue.toString());
                }
            }
        }
        //this call maintains the css related changes made by the effects on the client
        //especially the "visible" attribute.
        CurrentStyle.apply(FacesContext.getCurrentInstance(), uiComponent, writer); 
    }
    
    private static boolean valueIsIntegerSentinelValue(Object value) {

        if (value instanceof String) {
            return false;
        }else if (value instanceof Number) {
            if (value instanceof Integer) {
                if (((Integer) value).intValue() == Integer.MIN_VALUE) {
                    return true;
                }
                return false;
            }
        }
        return false;
    }

}
