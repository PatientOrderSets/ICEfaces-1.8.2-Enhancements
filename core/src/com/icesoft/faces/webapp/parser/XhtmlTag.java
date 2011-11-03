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

package com.icesoft.faces.webapp.parser;

import com.icesoft.faces.component.UIXhtmlComponent;
import org.xml.sax.Attributes;

import javax.faces.component.UIComponent;
import javax.faces.webapp.UIComponentTag;
import javax.faces.el.ValueBinding;

import com.icesoft.util.pooling.ELPool;

/**
 * This class contains the tag processing logic for all XHTML tags.
 */
public class XhtmlTag extends UIComponentTag {
    private String tagName;
    private Attributes attributes;

    /**
     * RendererType getter.
     *
     * @return Renderer class name
     */
    public String getRendererType() {
        return "com.icesoft.faces.Xhtml";
    }

    /**
     * ComponentType getter.
     *
     * @return XHTML component type
     */
    public String getComponentType() {
        return TagToComponentMap.XHTML_COMPONENT_TYPE;
    }


    protected void setProperties(UIComponent comp) {
        super.setProperties(comp);
        UIXhtmlComponent component = (UIXhtmlComponent) comp;
        component.setTag(getTagName());
        Attributes attr = getAttributes();
        if(attr != null) {
            for(int i = 0; i < attr.getLength(); i++) {
                String value = (String) attr.getValue(i);
                if (isValueReference(value)) {
                    ValueBinding vb = getFacesContext().getApplication().createValueBinding(ELPool.get(value));
                    component.addValueBindingAttribute(attr.getQName(i), vb);
                } else {
                    component.addStandardAttribute(attr.getQName(i), value);
                }
            }
        }
    }

    /**
     * TagName setter.
     *
     * @param tag tag name.
     */
    public void setTagName(String tag) {
        tagName = tag;
    }

    /**
     * TagName getter.
     *
     * @return tag name
     */
    public String getTagName() {
        return tagName;
    }

    /**
     * Attributes setter.
     *
     * @param attrib Atrributes.
     */
    public void setAttributes(Attributes attrib) {
        attributes = attrib;
    }

    /**
     * Attributes getter.
     *
     * @return Attributes
     */
    public Attributes getAttributes() {
        return attributes;
    }
}
