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

package com.icesoft.faces.component.selectinputtext;

import com.icesoft.faces.component.ExtendedAttributeConstants;
import com.icesoft.faces.context.DOMContext;
import com.icesoft.faces.context.effects.JavascriptContext;
import com.icesoft.faces.renderkit.dom_html_basic.DomBasicInputRenderer;
import com.icesoft.faces.renderkit.dom_html_basic.HTML;
import com.icesoft.faces.renderkit.dom_html_basic.PassThruAttributeRenderer;
import com.icesoft.faces.util.DOMUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.icesoft.util.pooling.ClientIdPool;

public class SelectInputTextRenderer extends DomBasicInputRenderer {
    private static final String AUTOCOMPLETE_DIV = "_div";
    static final String AUTOCOMPLETE_INDEX = "_idx";
    private static final Log log =
            LogFactory.getLog(SelectInputTextRenderer.class);

    //private static final String[] passThruAttributes = ExtendedAttributeConstants.getAttributes(ExtendedAttributeConstants.ICE_SELECTINPUTTEXT);
    //handle         HTML.ONKEYDOWN_ATTR, HTML.ONKEYUP_ATTR, HTML.ONFOCUS_ATTR, HTML.ONBLUR_ATTR
    private static final String[] passThruAttributes = new String[]{ HTML.ACCESSKEY_ATTR,  HTML.ALT_ATTR,  HTML.DIR_ATTR,  HTML.LANG_ATTR,  HTML.MAXLENGTH_ATTR,  HTML.ONCHANGE_ATTR,  HTML.ONCLICK_ATTR,  HTML.ONDBLCLICK_ATTR,   HTML.ONKEYPRESS_ATTR,   HTML.ONMOUSEDOWN_ATTR,  HTML.ONMOUSEMOVE_ATTR,  HTML.ONMOUSEOUT_ATTR,  HTML.ONMOUSEOVER_ATTR,  HTML.ONMOUSEUP_ATTR,  HTML.ONSELECT_ATTR,  HTML.ROWS_ATTR,  HTML.SIZE_ATTR,  HTML.STYLE_ATTR,  HTML.TABINDEX_ATTR,  HTML.TITLE_ATTR,  HTML.WIDTH_ATTR };                        
           
    public boolean getRendersChildren() {
        return true;
    }

    public void encodeBegin(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
        validateParameters(facesContext, uiComponent, null);
        if (log.isTraceEnabled()) {
            log.trace("encodeBegin");
        }
        SelectInputText component = (SelectInputText) uiComponent;
        DOMContext domContext =
                DOMContext.attachDOMContext(facesContext, uiComponent);
        String clientId = uiComponent.getClientId(facesContext);
        String divId = ClientIdPool.get(clientId + AUTOCOMPLETE_DIV);
        String call = " new Ice.Autocompleter('" + clientId + "','" + divId +
                      "', " + component.getOptions() + " ,'" + component.getRowClass() + "','" +
                      component.getSelectedRowClass() + "');";

        if (!domContext.isInitialized()) {
            Element root = domContext.createRootElement(HTML.DIV_ELEM);
            Element input = domContext.createElement(HTML.INPUT_ELEM);
            input.setAttribute(HTML.TYPE_ATTR, HTML.INPUT_TYPE_TEXT);
            setRootElementId(facesContext, input, uiComponent);
            root.appendChild(input);
            input.setAttribute(HTML.NAME_ATTR, clientId);
            input.setAttribute(HTML.CLASS_ATTR, component.getInputTextClass());
            
            String mousedownScript = (String)uiComponent.getAttributes().get(HTML.ONMOUSEDOWN_ATTR);
            input.setAttribute(HTML.ONMOUSEDOWN_ATTR, combinedPassThru(mousedownScript, "this.focus();"));

            String inputStyle = component.getWidthAsStyle();
            if(inputStyle != null && inputStyle.length() > 0)
                input.setAttribute(HTML.STYLE_ATTR, inputStyle);
            else
                input.removeAttribute(HTML.STYLE_ATTR);
            input.setAttribute("autocomplete", "off");
            Element div = domContext.createElement(HTML.DIV_ELEM);
            String listClass =  component.getListClass();

            div.setAttribute(HTML.ID_ATTR, divId);
            if(listClass == null){
                div.setAttribute(HTML.STYLE_ATTR,
                             "display:none;border:1px solid black;background-color:white;z-index:500;");
            }else{
                div.setAttribute(HTML.CLASS_ATTR, listClass);
            }
            root.appendChild(div);
            
            Element index = domContext.createElement(HTML.INPUT_ELEM);
            index.setAttribute(HTML.TYPE_ATTR, HTML.INPUT_TYPE_HIDDEN);
            String indexId = ClientIdPool.get(clientId + AUTOCOMPLETE_INDEX);
            index.setAttribute(HTML.NAME_ATTR, indexId);
            root.appendChild(index);
            
            String rootStyle = component.getStyle();
            if(rootStyle != null && rootStyle.length() > 0)
                root.setAttribute(HTML.STYLE_ATTR, rootStyle);
            else
                root.removeAttribute(HTML.STYLE_ATTR);
            root.setAttribute(HTML.CLASS_ATTR, component.getStyleClass());
            
          //  Element script = domContext.createElement(HTML.SCRIPT_ELEM);
          //  script.setAttribute(HTML.SCRIPT_LANGUAGE_ATTR,
                              //  HTML.SCRIPT_LANGUAGE_JAVASCRIPT);
       //     String scriptCode = "window.onLoad(function(){" + call + "});";
         //   Node node = domContext.createTextNode(scriptCode);
          //  script.appendChild(node);
          //  root.appendChild(script);
            if (log.isDebugEnabled()) {
                log.debug(
                        "SelectInputText:encodeBegin():component created with the following id : " +
                        clientId);
            }
            PassThruAttributeRenderer.renderHtmlAttributes(facesContext, uiComponent, passThruAttributes);
            PassThruAttributeRenderer.renderBooleanAttributes(facesContext, 
                    uiComponent, input, PassThruAttributeRenderer.EMPTY_STRING_ARRAY);            
        }
//        Set excludes = new HashSet();
//        excludes.add(HTML.ONKEYDOWN_ATTR);
//        excludes.add(HTML.ONKEYUP_ATTR);
//        excludes.add(HTML.ONFOCUS_ATTR);
//        excludes.add(HTML.ONBLUR_ATTR);

        if (!component.isDisabled() && !component.isReadonly()) {
            JavascriptContext.addJavascriptCall(facesContext, call);
        }
    }

    public void encodeChildren(FacesContext facesContext,
                               UIComponent uiComponent)
            throws IOException {
        DOMContext domContext =
                DOMContext.getDOMContext(facesContext, uiComponent);
        SelectInputText component = (SelectInputText) uiComponent;
        Element input = (Element) domContext.getRootNode().getFirstChild();

        String combinedValue = "setFocus(this.id);";
        Object appValue = uiComponent.getAttributes().get("onfocus");
        if (appValue != null)
            combinedValue += appValue.toString();
        input.setAttribute("onfocus", combinedValue);

        combinedValue = "setFocus('');";
        appValue = uiComponent.getAttributes().get("onblur");
        if (appValue != null)
            combinedValue += appValue.toString();
        input.setAttribute("onblur", combinedValue);

        appValue = uiComponent.getAttributes().get("onchange");
        if (appValue != null)
            input.setAttribute("onchange", appValue.toString());
        // this would prevent, when first valueChangeListener fires with null value
//System.out.println("SelectInputTextRenderer.encodeChildren()  clientId: " + uiComponent.getClientId(facesContext));
        String value = getValue(facesContext, uiComponent);
//System.out.println("SelectInputTextRenderer.encodeChildren()  value: " + value);
        if (value != null) {
            input.setAttribute(HTML.VALUE_ATTR, value);
//System.out.println("SelectInputTextRenderer.encodeChildren()  changed: " + component.hasChanged());
            if (component.hasChanged()) {
                if (log.isDebugEnabled()) {
                    log.debug(
                            "SelectInputText:encodeChildren(): component's value have been changed, start populating list : ");
                }
                populateList(facesContext, component);
                component.setChangedComponentId(null);
            }
        }
//        renderAttribute(uiComponent, input, HTML.DISABLED_ATTR,
//                        HTML.DISABLED_ATTR);
//        renderAttribute(uiComponent, input, HTML.READONLY_ATTR,
//                        HTML.READONLY_ATTR);
        domContext.stepOver();
    }


    public void populateList(FacesContext facesContext, SelectInputText component)
            throws IOException {
            if (log.isTraceEnabled()) {
                log.trace("populateList");
            }
            component.populateItemList();
            Iterator matchs = component.getItemList();
            int rows = component.getRows();
            int rowCounter = 0;             
            if (component.getSelectFacet() != null) {
                if (log.isDebugEnabled()) {
                    log.debug(
                            "SelectInputText:populateList(): \"selectInputText\" facet found, generate generic html for list");
                }
                UIComponent facet = component.getSelectFacet();
                DOMContext domContext =
                        DOMContext.getDOMContext(facesContext, component);

                Element listDiv = domContext.createElement(HTML.DIV_ELEM);
                Map requestMap =
                        facesContext.getExternalContext().getRequestMap();
                //set index to 0, so child components can get client id from autoComplete component
                component.setIndex(0);
                while (matchs.hasNext() && (rowCounter++ < rows || rows == 0)) {
                    Element div = domContext.createElement(HTML.DIV_ELEM);
                    SelectItem item = (SelectItem) matchs.next();
                    requestMap.put(component.getListVar(), item.getValue());
                    listDiv.appendChild(div);
                    // When HTML is display we still need a selected value. Hidding the value in a hidden span
                    // accomplishes this.
                    Element spanToDisplay =
                            domContext.createElement(HTML.SPAN_ELEM);
                    spanToDisplay.setAttribute(HTML.CLASS_ATTR, "informal");
                    div.appendChild(spanToDisplay);
                    domContext.setCursorParent(spanToDisplay);
                    encodeParentAndChildren(facesContext, facet);
                    Element spanToSelect =
                            domContext.createElement(HTML.SPAN_ELEM);
                    spanToSelect.setAttribute(HTML.STYLE_ATTR,
                                              "visibility:hidden;display:none;");
                    String itemLabel = item.getLabel();
                    if(itemLabel == null) {
                        itemLabel = converterGetAsString(
                            facesContext, component, item.getValue());
                    }
                    Text label = domContext.createTextNode(DOMUtils.escapeAnsi(itemLabel));
                    spanToSelect.appendChild(label);
                    div.appendChild(spanToSelect);
                    component.resetId(facet);
                }
                component.setIndex(-1);

                String nodeValue =
                        DOMUtils.nodeToString(listDiv).replaceAll("\n", "");
                String call = "Autocompleter.Finder.find('" +
                              component.getClientId(facesContext) +
                              "').updateNOW('" + nodeValue + "');";
                JavascriptContext.addJavascriptCall(facesContext, call);

            } else {
                if (log.isDebugEnabled()) {
                    log.debug(
                            "SelectInputText:populateList(): \"selectItem(s)\" found, generate plain-text for list");
                }
                if (matchs.hasNext()) {
                    StringBuffer sb = new StringBuffer("<div>");
                    SelectItem item = null;
                    while (matchs.hasNext() && (rowCounter++ < rows || rows == 0)) {
                        item = (SelectItem) matchs.next();
                        String itemLabel = item.getLabel();
                        if(itemLabel == null) {
                            itemLabel = converterGetAsString(
                                facesContext, component, item.getValue());
                        }
                        sb.append("<div>").append(DOMUtils.escapeAnsi(itemLabel))
                                .append("</div>");
                    }
                    sb.append("</div>");
                    String call = "Autocompleter.Finder.find('" +
                                  component.getClientId(facesContext) +
                                  "').updateNOW('" + sb.toString() + "');";
                    JavascriptContext.addJavascriptCall(facesContext, call);
                }
            }
    }
}
