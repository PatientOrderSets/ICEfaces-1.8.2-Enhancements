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

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.digester.Rule;
import org.xml.sax.Attributes;

import java.lang.reflect.Method;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import javax.faces.webapp.UIComponentTag;
import javax.faces.webapp.UIComponentELTag;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import java.util.HashMap;


public class ELSetPropertiesRule extends Rule {

    public void begin(Attributes attributes) throws Exception {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HashMap values = new HashMap();
        Object top = digester.peek();

        for (int i = 0; i < attributes.getLength(); i++) {
            String name = attributes.getLocalName(i);
            if ("".equals(name)) {
                name = attributes.getQName(i);
            }
            String value = attributes.getValue(i);

            //take a guess at the types of the JSF 1.2 tag members
            //we are probably better off doing this reflectively
            if (name != null) {
                values.put(name, value);
                if (("id".equals(name)) ||
                    ("name".equals(name)) ||
                    ("var".equals(name))) {
                    values.put(name, value);
                } else if (top instanceof UIComponentTag) {
                    //must be a JSF 1.1 tag
                    values.put(name, value);
                } else if ("action".equals(name)) {
                    values.put(name,
                               getMethodExpression( facesContext, name, value,
                                                    null ));
                } else if ("validator".equals(name)) {
                    values.put(name, 
                            getMethodExpression( facesContext, name, value,
                                    null ));
                } else if ("actionListener".equals(name)) {
                    values.put(name,
                               getMethodExpression(facesContext, name, value,
                                                   ActionEvent.class));
                } else if ("valueChangeListener".equals(name)) {
                    values.put(name,
                               getMethodExpression(facesContext, name, value,
                                                   ValueChangeEvent.class));
                } else {
                    values.put(name,
                               getValueExpression(facesContext, name, value));
                }
                if (top instanceof javax.faces.webapp.UIComponentELTag) {
                    //special case for 
                    //com.sun.faces.taglib.jsf_core.ParameterTag
                    //and potentially others
                    if ("name".equals(name))  {
                        values.put(name,
                                   getValueExpression(facesContext, name, value));
                    } else if ("locale".equals(name)) {
                        values.put(name,
                                   getValueExpression(facesContext, name, value));
                    } 
                } else {
                    //reflection based code as mentioned above.  More likely
                    //to be correct, but performance may not be as good,
                    //so only applying it in a specific case
                    if ("name".equals(name))  {
                        Method setNameMethod = null;
                        try {
                            setNameMethod = top.getClass().getMethod("setName",
                                                                     new Class[] { ValueExpression.class } );
                        } catch (Exception e)  { }
                        if (null != setNameMethod)  {
                            values.put(name,
                                       getValueExpression(facesContext, name, value));
                        }
                    }

                }

            }
        }


        BeanUtils.populate(top, values);
    }

    private ValueExpression getValueExpression(FacesContext facesContext,
                                               String name, String value)  {

        Class argType = Object.class;
        // For some reason, tab index is typed to be a String, even though
        //  it can only be positive integral values
        if (!name.equals("tabindex")) {
            try {
                if (value.equalsIgnoreCase("true") ||
                    value.equalsIgnoreCase("false") ) {
                    argType = Boolean.class;
                } else if (null != Integer.valueOf(value)) {
                    //attempt to coerce to Integer type for standard JSF components
                    argType = Integer.class;
                }
            } catch (NumberFormatException e) {
            }
        }

        
        ValueExpression valueExpression =
                facesContext.getApplication().getExpressionFactory()
                        .createValueExpression(
                                facesContext.getELContext(),
                                value, argType );

        return valueExpression;
    }

    private MethodExpression getMethodExpression(FacesContext facesContext,
                                                 String name, String value,
                                                 Class argType)  {
        Class[] argTypes = new Class[]{};
        if (null != argType) {
            argTypes = new Class[]{argType};
        }

        MethodExpression methodExpression =
                facesContext.getApplication().getExpressionFactory()
                        .createMethodExpression(
                                                facesContext.getELContext(),
                                                value, String.class,
                                                argTypes);
        return methodExpression;
    }
    
}
