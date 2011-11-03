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
import com.icesoft.faces.context.DOMResponseWriter;
import com.icesoft.faces.context.effects.CurrentStyle;
import com.icesoft.faces.webapp.parser.ImplementationUtil;
import com.icesoft.util.SeamUtilities;
import com.icesoft.util.pooling.ClientIdPool;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.NamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FormRenderer extends DomBasicRenderer {

    public static final String COMMAND_LINK_HIDDEN_FIELD =
            "command_link_hidden_field";
    private static final String COMMAND_LINK_HIDDEN_FIELDS_KEY =
            "com.icesoft.faces.FormRequiredHidden";
    private static final Log log = LogFactory.getLog(FormRenderer.class);

    public static final String STATE_SAVING_MARKER = "stateSavingMarker";
    
    private static final String[] passThruAttributes = AttributeConstants.getAttributes(AttributeConstants.H_FORMFORM);
    
    public void decode(FacesContext facesContext, UIComponent uiComponent) {
        validateParameters(facesContext, uiComponent, UIForm.class);
        UIForm uiForm = (UIForm) uiComponent;
        Map requestParameterMap =
                facesContext.getExternalContext().getRequestParameterMap();
        String formClientId = uiForm.getClientId(facesContext);
        if (requestParameterMap.containsKey(formClientId) ||
                uiComponent.getAttributes().containsKey("fileUploaded")) {
            uiForm.setSubmitted(true);
        } else {
            uiForm.setSubmitted(false);
        }
    }

    public void encodeBegin(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
        validateParameters(facesContext, uiComponent, UIForm.class);
        validateNestingForm(uiComponent);
        DOMContext domContext =
                DOMContext.attachDOMContext(facesContext, uiComponent);
        String formClientId = uiComponent.getClientId(facesContext);

        if (!domContext.isInitialized()) {
            Element root = domContext.createElement("form");

            domContext.setRootNode(root);
            root.setAttribute("id", formClientId);
            root.setAttribute("method", "post");
            root.setAttribute("action", "javascript:;");

            String styleClass =
                    (String) uiComponent.getAttributes().get("styleClass");
            if (styleClass != null) {
                root.setAttribute("class", styleClass);
            }
            String acceptcharset =
                    (String) uiComponent.getAttributes().get("acceptcharset");
            if (acceptcharset != null) {
                root.setAttribute("accept-charset", acceptcharset);
            }
            //redirect form submits
            String redirectScript = "$element(document.getElementById('" + formClientId + "')).captureAndRedirectSubmit();";
            Element scriptElement = (Element) root.appendChild(domContext.createElement("script"));
            scriptElement.setAttribute("type", "text/javascript");
            scriptElement.setAttribute("id", ClientIdPool.get(formClientId + "script"));
            scriptElement.appendChild(domContext.createTextNode(redirectScript));
            root.appendChild(scriptElement);

            // this hidden field will be checked in the decode method to
            // determine if this form has been submitted.
            Element formHiddenField = domContext.createElement("input");
            formHiddenField.setAttribute("type", "hidden");
            formHiddenField.setAttribute("name", formClientId);
            formHiddenField.setAttribute("value", formClientId);
            root.appendChild(formHiddenField);

            // Only render the css update field once. Rendering more then one will cause
            // a duplicate ID error
            Element cssUpdateField = domContext.createElement(HTML.INPUT_ELEM);
            cssUpdateField.setAttribute(HTML.TYPE_ATTR, HTML.INPUT_TYPE_HIDDEN);
            cssUpdateField.setAttribute(HTML.NAME_ATTR,
                    CurrentStyle.CSS_UPDATE_FIELD);
            cssUpdateField.setAttribute(HTML.VALUE_ATTR, "");
            root.appendChild(cssUpdateField);
        }

        // This has to occur outside the isInitialized test, as it has to happen
        // all the time, even if the form otherwise has not changed.
        Element root = (Element) domContext.getRootNode();

        String conversationId = SeamUtilities.getSeamConversationId();
        if (conversationId != null) {
            String conversationParamName =
                    SeamUtilities.getConversationIdParameterName();

            Element conversationIDElement =
                    domContext.createElement(HTML.INPUT_ELEM);
            if (log.isDebugEnabled()) {
                log.debug("Embedding Seam Param - name: " + conversationParamName +
                        ", value: " + conversationId);
            }
            conversationIDElement
                    .setAttribute(HTML.TYPE_ATTR, HTML.INPUT_TYPE_HIDDEN);
            conversationIDElement
                    .setAttribute(HTML.NAME_ATTR, conversationParamName);

            conversationIDElement.setAttribute(HTML.VALUE_ATTR, conversationId);
            // # 4581 put id into hidden field for dom diffing.
            conversationIDElement.setAttribute(HTML.ID_ATTR, "cid:" + formClientId );
            root.appendChild(conversationIDElement);

        }

        String flowId = SeamUtilities.getSpringFlowId();
        if (flowId != null) {
            String flowParamName =
                    SeamUtilities.getFlowIdParameterName();

            Element flowIDElement =
                    domContext.createElement(HTML.INPUT_ELEM);
            if (log.isDebugEnabled()) {
                log.debug("Embedding Spring Param - name: " + flowParamName +
                        ", value: " + flowId);
            }
            String flowParamId = formClientId + ":" + flowParamName;
            flowIDElement
                    .setAttribute(HTML.TYPE_ATTR, HTML.INPUT_TYPE_HIDDEN);
            flowIDElement
                    .setAttribute(HTML.NAME_ATTR, flowParamName);
            flowIDElement
                    .setAttribute(HTML.ID_ATTR, flowParamId);

            flowIDElement.setAttribute(HTML.VALUE_ATTR, flowId);
            root.appendChild(flowIDElement);

        }


        //String contextClass = facesContext.getClass().toString();
        //root.setAttribute("context_type", contextClass);

        PassThruAttributeRenderer.renderHtmlAttributes(facesContext, uiComponent, passThruAttributes);
        String autoComplete = (String)uiComponent.getAttributes().get(HTML.AUTOCOMPLETE_ATTR);
        if(autoComplete != null && "off".equalsIgnoreCase(autoComplete)){
            root.setAttribute(HTML.AUTOCOMPLETE_ATTR, "off");
        }
                
        // don't override user-defined value
        String userDefinedValue = root.getAttribute("onsubmit");
        if (userDefinedValue == null || userDefinedValue.equalsIgnoreCase("")) {
            root.setAttribute("onsubmit", "return false;");
        }

        facesContext.getApplication().getViewHandler().writeState(facesContext);
        // Currently we have to put the marker node in the DOM here, because
        // the DOMResponseWriter doesn't have the same View of the DOM as this object does,
        // because this object isn't using the DOMResponseWriter. 
        ResponseWriter writer = facesContext.getResponseWriter();

        if (ImplementationUtil.isJSFStateSaving() && (writer instanceof DOMResponseWriter)) {
            DOMResponseWriter domWriter = (DOMResponseWriter) writer;
            Node n = domContext.createElement("div");

            String id = formClientId +
                        NamingContainer.SEPARATOR_CHAR +STATE_SAVING_MARKER;

            root.appendChild( n );
            ((Element) n).setAttribute( "id", id );
            ((Element) n).setAttribute(HTML.STYLE_ATTR, "width:0px;height:0px;");
            domWriter.trackMarkerNode( n );
        }

        domContext.stepInto(uiComponent);
    }

    public void encodeChildren(FacesContext facesContext,
                               UIComponent uiComponent) {
        validateParameters(facesContext, uiComponent, UIForm.class);
    }

    public void encodeEnd(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
        validateParameters(facesContext, uiComponent, UIForm.class);

        // render needed hidden fields added by CommandLinkRenderer (and perhaps
        // other renderers as well)
        DOMContext domContext =
                DOMContext.getDOMContext(facesContext, uiComponent);
        // set static class variable for support of myfaces command link
        renderCommandLinkHiddenFields(facesContext, uiComponent);
        
        //check if the messages renderer asked to be rendered later,
        //if yes, then re-render it
        if (uiComponent.getAttributes().get("$ice-msgs$") != null)  {
            UIComponent messages = (UIComponent)uiComponent.getAttributes().get("$ice-msgs$");
            messages.encodeBegin(facesContext);
            messages.encodeChildren(facesContext);
            messages.encodeEnd(facesContext);
        }
        
        domContext.stepOver();
    }


    /**
     * @param facesContext
     * @param uiComponent  Render any required hidden fields. There is a list
     *                     (on the request map of the external context) of
     *                     'required hidden fields'. Hidden fields can be
     *                     contributed by the CommandLinkRenderer. Contribution
     *                     is made during rendering of this form's commandLink
     *                     children so we have to wait for the child renderers
     *                     to complete their work before we render the hidden
     *                     fields. Therefore, this method should be called from
     *                     the form's encodeEnd method. We can assume that the
     *                     hidden fields are the last fields in the form because
     *                     they are rendered in the FormRenderer's encodeEnd
     *                     method. Note that the CommandLinkRenderer adds one
     *                     hidden field that indicates the id of the link that
     *                     was clicked to submit the form ( in case there are
     *                     multiple commandLinks on a page) and one hidden field
     *                     for each of its UIParameter children.
     */
    private static void renderCommandLinkHiddenFields(FacesContext facesContext,
                                                      UIComponent uiComponent) {
        Map commandLinkHiddenFields = getCommandLinkFields(facesContext);
        if (commandLinkHiddenFields != null) {
            renderRequiredCommandLinkHiddenFields(uiComponent, facesContext,
                    commandLinkHiddenFields);
            resetCommandLinkFieldsInRequestMap(facesContext);
        }
    }

    /**
     * @param facesContext
     */
    private static void resetCommandLinkFieldsInRequestMap(
            FacesContext facesContext) {
        Map requestMap = facesContext.getExternalContext().getRequestMap();
        requestMap.put(COMMAND_LINK_HIDDEN_FIELDS_KEY, null);
    }

    /**
     * @param uiComponent
     * @param facesContext
     * @param map
     */
    private static void renderRequiredCommandLinkHiddenFields(
            UIComponent uiComponent,
            FacesContext facesContext, Map map) {
        DOMContext domContext =
                DOMContext.getDOMContext(facesContext, uiComponent);
        Element root = (Element) domContext.getRootNode();
        Element hiddenFieldsDiv = domContext.createElement(HTML.DIV_ELEM);
        hiddenFieldsDiv.setAttribute(HTML.ID_ATTR, uiComponent.getClientId(facesContext) + "hdnFldsDiv");
        hiddenFieldsDiv.setAttribute(HTML.STYLE_ATTR, "display:none;");
        root.appendChild(hiddenFieldsDiv);
        
        Iterator commandLinkFields = map.entrySet().iterator();
        while (commandLinkFields.hasNext()) {
            Map.Entry nextField = (Map.Entry) commandLinkFields.next();
            if (COMMAND_LINK_HIDDEN_FIELD.equals(nextField.getValue())) {
                Element next = domContext.createElement("input");
                next.setAttribute("type", "hidden");
                next.setAttribute("name", nextField.getKey().toString());
                hiddenFieldsDiv.appendChild(next);
            }
        }
    }

    /**
     * @param facesContext
     * @param fieldName
     */
    public static void addHiddenField(FacesContext facesContext,
                                      String fieldName) {
        addHiddenField(facesContext, fieldName, COMMAND_LINK_HIDDEN_FIELD);
    }

    /**
     * @param facesContext
     * @param fieldName
     * @param value
     */
    //make this method public when we modify the hidden field rendering
    //to accept arbitrary hidden fields
    private static void addHiddenField(FacesContext facesContext,
                                       String fieldName, String value) {
        Map hiddenFieldMap = getCommandLinkFields(facesContext);
        if (hiddenFieldMap == null) {
            hiddenFieldMap = createCommandLinkFieldsOnRequestMap(facesContext);
        }
        if (!hiddenFieldMap.containsKey(fieldName)) {
            hiddenFieldMap.put(fieldName, value);
        }
    }

    /**
     * @param facesContext
     * @return Map the hiddenFieldMap
     */
    private static Map getCommandLinkFields(FacesContext facesContext) {
        Map requestMap = facesContext.getExternalContext().getRequestMap();
        Map hiddenFieldMap =
                (Map) requestMap.get(COMMAND_LINK_HIDDEN_FIELDS_KEY);
        if (hiddenFieldMap == null) {
            hiddenFieldMap = new HashMap();
            requestMap.put(COMMAND_LINK_HIDDEN_FIELDS_KEY, hiddenFieldMap);
        }
        return hiddenFieldMap;
    }

    /**
     * @param facesContext
     * @return Map hiddenFieldMap
     */
    private static Map createCommandLinkFieldsOnRequestMap(
            FacesContext facesContext) {
        Map requestMap = facesContext.getExternalContext().getRequestMap();
        Map hiddenFieldMap =
                (Map) requestMap.get(COMMAND_LINK_HIDDEN_FIELDS_KEY);
        if (hiddenFieldMap == null) {
            hiddenFieldMap = new HashMap();
            requestMap.put(COMMAND_LINK_HIDDEN_FIELDS_KEY, hiddenFieldMap);
        }
        return hiddenFieldMap;
    }

    private void validateNestingForm(UIComponent uiComponent) throws IOException {
        UIComponent parent = uiComponent.getParent();
        if (parent == null) {
            return;
        }
        if (parent instanceof UIForm) {
            throw new FacesException("Nested form found on the page. The form " +
                    "action element can not be nested");
        }
        validateNestingForm(parent);
    }
}
