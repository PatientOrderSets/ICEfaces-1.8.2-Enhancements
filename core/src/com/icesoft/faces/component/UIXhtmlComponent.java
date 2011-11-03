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

package com.icesoft.faces.component;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.icesoft.util.pooling.XhtmlPool;

public class UIXhtmlComponent extends UIComponentBase {
    public static final String COMPONENT_FAMILY =
            "com.icesoft.faces.XhtmlComponent";
    public static final String RENDERER_TYPE =
            "com.icesoft.domXhtml";
    
    private static final Log log = LogFactory.getLog(UIXhtmlComponent.class);

    private String tag;
    private Map standardAttributes;
    private Map valueBindingAttributes;
    private boolean createdByFacelets = false;

    public UIXhtmlComponent() {
        setRendererType( RENDERER_TYPE );
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getTag() {
        return tag;
    }

    public Map getTagAttributes() {
        Map allAttributes = new HashMap();
        
        // Straight text attributes
        if (standardAttributes != null) {
            Iterator attributeIterator = standardAttributes.entrySet().iterator();
            while (attributeIterator.hasNext()) {
                Map.Entry attribute = (Map.Entry) attributeIterator.next();
                allAttributes.put(
                    attribute.getKey().toString(),
                    attribute.getValue().toString());
            }
        }

        // EL expression attributes
        if (valueBindingAttributes != null) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            Iterator vbAttributeIterator =
                valueBindingAttributes.entrySet().iterator();
            while (vbAttributeIterator.hasNext()) {
                Map.Entry attribute =
                    (Map.Entry) vbAttributeIterator.next();
                Object name = attribute.getKey();
                ValueBinding vb = (ValueBinding) attribute.getValue();
                try {
                    if (vb != null) {
                        Object evaluatedValue = vb.getValue(facesContext);
                        if (evaluatedValue != null) {
                            allAttributes.put(
                                name.toString(),
                                evaluatedValue.toString());
                        }
                    }
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return allAttributes;
    }

    public boolean isCreatedByFacelets() {
        return createdByFacelets;
    }

    public void setTag(String tag) {
        this.tag = (String) XhtmlPool.get(tag);
    }

    public void addStandardAttribute(String key, Object value) {
        if (standardAttributes == null)
            standardAttributes = new HashMap();
        standardAttributes.put(key, XhtmlPool.get(value));
    }

    public void addValueBindingAttribute(String key, ValueBinding vb) {
        if (valueBindingAttributes == null)
            valueBindingAttributes = new HashMap();
        valueBindingAttributes.put(key, vb);
    }

    public void setCreatedByFacelets() {
        createdByFacelets = true;
    }

    public String toString() {
        return this.getClass() + "@" + this.hashCode() + ":tag=[" +
               this.getTag() + "]";
    }
    
    //We may want to consider a transient implementation of this component
    /*
    public boolean isTransient()  {
        return true;
    }
   */

    public Object saveState(FacesContext facesContext)  {
        Object[] values = new Object[4];
        values[0] = super.saveState(facesContext);
        values[1] = tag;
        values[2] = standardAttributes;
        values[3] = valueBindingAttributes;
        return ((Object) values);
    }
    
    public void restoreState(FacesContext facesContext, Object state)  {
        Object values[] = (Object[]) state;
        super.restoreState(facesContext, values[0]);
        tag = (String) values[1];
        standardAttributes = (Map) values[2];
        valueBindingAttributes = (Map) values[3];
    }
}
