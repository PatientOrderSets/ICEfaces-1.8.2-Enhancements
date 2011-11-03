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

import com.icesoft.faces.component.AttributeConstants;
import com.icesoft.faces.context.DOMContext;
import com.icesoft.faces.util.DOMUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;

public class OutputMessageRenderer extends DomBasicRenderer {

    private static final String[] passThruAttributes = AttributeConstants.getAttributes(AttributeConstants.H_OUTPUTFORMAT);
 
    public void encodeBegin(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
        validateParameters(facesContext, uiComponent, null);
    }

    public void encodeChildren(FacesContext facesContext,
                               UIComponent uiComponent) {
        validateParameters(facesContext, uiComponent, null);
    }

    public void encodeEnd(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
        validateParameters(facesContext, uiComponent, null);

        // determine whether a span is required
        boolean spanIsRequired = false;
        String style = (String) uiComponent.getAttributes().get("style");
        String styleClass =
                (String) uiComponent.getAttributes().get("styleClass");
        if (style != null
            || styleClass != null
            || idNotNull(uiComponent)) {
            spanIsRequired = true;
        }

        // get a list of the UIParameter child components
        ArrayList uiParameterChildren = new ArrayList();
        if (uiComponent.getChildCount() > 0) {
            Iterator allChildren = uiComponent.getChildren().iterator();
            while (allChildren.hasNext()) {
                UIComponent nextChild = (UIComponent) allChildren.next();
                if (nextChild instanceof UIParameter) {
                    uiParameterChildren.add(((UIParameter) nextChild).getValue());
                }
            }
        }

        // get the component value and convert to a string for later use
        String uiComponentValue = null;
        if (uiComponent instanceof ValueHolder) {
            Object uiComponentValueObject = null;
            if ((uiComponentValueObject =
                    ((ValueHolder) uiComponent).getValue()) != null) {
                if (uiComponentValueObject instanceof String) {
                    uiComponentValue = (String) uiComponentValueObject;
                } else {
                    uiComponentValue = uiComponentValueObject.toString();
                }
            }
        }

        // if there is one or more parameters, format the parameters using 
        // the uiComponentValue as the pattern
        int numberOfParameters = uiParameterChildren.size();
        if (numberOfParameters > 0) {
            Object[] parameters = uiParameterChildren.toArray();
            uiComponentValue =
                    MessageFormat.format(uiComponentValue, parameters);
        }

        // escape
        boolean escape = DOMUtils.escapeIsRequired(uiComponent);
        if (escape && uiComponentValue != null) {
            uiComponentValue = DOMUtils.escapeAnsi(uiComponentValue);
        }

        DOMContext domContext =
                DOMContext.attachDOMContext(facesContext, uiComponent);

        if (!domContext.isInitialized()) {
            // create the text message
            Document document = domContext.getDocument();
            Text textNode = (Text) document.createTextNode(uiComponentValue);
            // create a parent span, if required, otherwise set the root node
            // to the text node
            if (spanIsRequired) {
                Element rootSpan = domContext.createElement(HTML.SPAN_ELEM);
                domContext.setRootNode(rootSpan);
                if (styleClass != null) {
                    rootSpan.setAttribute("class", styleClass);
                }
                PassThruAttributeRenderer
                        .renderHtmlAttributes(facesContext, uiComponent, passThruAttributes);
                rootSpan.appendChild(textNode);
            } else {
                domContext.setRootNode(textNode);
            }
        } else {
            // the root node is either a text node or an element with a single 
            // text node child; find the text node and update its value
            Node rootSpanOrText = domContext.getRootNode();
            if (rootSpanOrText instanceof Text) {
                ((Text) rootSpanOrText).setData(uiComponentValue);
            } else {
                Node textNode = null;
                if ((textNode = rootSpanOrText.getFirstChild()) instanceof Text)
                {
                    ((Text) textNode).setData(uiComponentValue);
                }
            }
        }
        domContext.stepOver();
    }
} 
