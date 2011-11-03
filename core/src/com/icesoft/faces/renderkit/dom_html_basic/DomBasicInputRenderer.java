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

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.el.ValueBinding;

public abstract class DomBasicInputRenderer extends DomBasicRenderer {

    /**
     * Set the submittedValue parameter to the UIComponent instance if and only
     * if the UIComponent is a subclass of UIInput
     */
    public void setSubmittedValue(UIComponent uiComponent,
                                  Object submittedValue) {
        if (uiComponent instanceof UIInput) {
            ((UIInput) uiComponent).setSubmittedValue(submittedValue);
        }
    }

    Object getValue(UIComponent uiComponent) {
        Object value = null;
        if (uiComponent instanceof ValueHolder) {
            value = ((ValueHolder) uiComponent).getValue();
        }
        return value;
    }

    /**
     * Return the converted submittedValue. If a converter is registered with
     * the component then use that converter. Otherwise get the default
     * converter corresponding to the type of the value binding. If no converter
     * is found then return the submittedValue unchanged.
     *
     * @param facesContext   the current FacesContext
     * @param uiComponent    the uiComponent whose value will be converted.
     * @param submittedValue the submittedValue to be submitted
     */

    public Object getConvertedValue(FacesContext facesContext, UIComponent
            uiComponent, Object submittedValue) throws ConverterException {

        // get the converter (if any) registered with this component 
        Converter converter = null;
        if (uiComponent instanceof ValueHolder) {
            converter = ((ValueHolder) uiComponent).getConverter();
        }
        // if we didn't find a converter specifically registered with the component
        // then get the default converter for the type of the value binding,
        // if it exists
        ValueBinding valueBinding = uiComponent.getValueBinding("value");
        if (converter == null && valueBinding != null) {
            Class valueBindingClass = valueBinding.getType(facesContext);
            if (valueBindingClass != null) {
                converter = facesContext.getApplication()
                        .createConverter(valueBindingClass);
            }
        }

        if (converter != null) {
            return converter.getAsObject(facesContext, uiComponent,
                                         (String) submittedValue);
        } else if (submittedValue != null) {
            return (String) submittedValue;
        } else {
            return null;
        }
    }
}
