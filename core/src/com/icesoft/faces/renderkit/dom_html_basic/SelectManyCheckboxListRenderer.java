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
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.w3c.dom.Node;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UISelectMany;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import java.io.IOException;
import java.util.*;

import com.icesoft.util.pooling.ClientIdPool;

public class SelectManyCheckboxListRenderer extends MenuRenderer {
    private static final String[] selectManyCheckboxPassThruAttributes = AttributeConstants.getAttributes(AttributeConstants.H_SELECTMANYCHECKBOX);
    public void encodeEnd(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {

        validateParameters(facesContext, uiComponent, null);

        String componentName = uiComponent.getClass().getName();
        if (componentName.equals("com.icesoft.faces.component.ext.HtmlRadio") ||
                componentName.equals("com.icesoft.faces.component.ext.HtmlCheckbox")) {
            renderOption(facesContext, uiComponent);
            return;
        }

        int counter = 0;

        boolean renderVertically = false;
        String layout = (String) uiComponent.getAttributes().get("layout");
        if (layout != null && layout.equals("spread")) {
            return;
        }
        if (layout != null) {
            renderVertically =
                    layout.equalsIgnoreCase("pageDirection") ? true : false;
        }

        int border = getBorderSize(uiComponent);

        DOMContext domContext =
                DOMContext.attachDOMContext(facesContext, uiComponent);

        Element rootTR = null;
        // remove all existing table rows from the root table
        if (!domContext.isInitialized()) {
            Element rootNode = createRootNode(domContext);
            setRootElementId(facesContext, rootNode, uiComponent);
            addJavaScript(facesContext, uiComponent, rootNode, new HashSet());            
        }
        Element rootNode = (Element) domContext.getRootNode();
        Element rootTable = getTableElement(domContext);
        String styleClass =
                (String) uiComponent.getAttributes().get("styleClass");
        if (styleClass != null) {
            rootNode.setAttribute("class", styleClass);
            rootTable.setAttribute("class", styleClass);
        }

        rootTable.setAttribute("border", new Integer(border).toString());

        if (!renderVertically) {
            rootTR = domContext.createElement("tr");
            rootTable.appendChild(rootTR);
        }

        Iterator options = getSelectItems(uiComponent);

        //We should call uiComponent.getValue() only once, becase if it binded with the bean,
        //The bean method would be called as many time as this method call.
        Object[] submittedValue = null;
        Object componentValue = null;
        if (options.hasNext()) {
            submittedValue = getSubmittedSelectedValues(uiComponent);
            if (submittedValue == null) {
                componentValue = getCurrentSelectedValues(uiComponent);
            }
        }
        while (options.hasNext()) {
            SelectItem nextSelectItem = (SelectItem) options.next();

            counter++;

            // render a SelectItemGroup in a nested table
            if (nextSelectItem instanceof SelectItemGroup) {

                Element nextTR = domContext.createElement("tr");
                Element nextTD = null;

                if (nextSelectItem.getLabel() != null) {
                    if (renderVertically) {
                        rootTable.appendChild(nextTR);
                    }
                    nextTD = domContext.createElement("td");
                    nextTR.appendChild(nextTD);
                    Text label = domContext.getDocument()
                            .createTextNode(nextSelectItem.getLabel());
                    nextTD.appendChild(label);
                }
                if (renderVertically) {
                    nextTR = domContext.createElement("tr");
                    rootTable.appendChild(nextTR);
                } else {
                    rootTable.appendChild(nextTR);
                }
                nextTD = domContext.createElement("td");
                nextTR.appendChild(nextTD);

                SelectItem[] selectItemsArray =
                        ((SelectItemGroup) nextSelectItem).getSelectItems();
                for (int i = 0; i < selectItemsArray.length; ++i) {
                    renderOption(facesContext, uiComponent, selectItemsArray[i],
                                 renderVertically, rootTable, nextTR, counter,
                                 submittedValue, componentValue);
                }
            } else {
                renderOption(facesContext, uiComponent, nextSelectItem,
                             renderVertically, rootTable, rootTR, counter,
                             submittedValue, componentValue);
            }
        }

        domContext.stepOver();
    }

    private int getBorderSize(UIComponent uiComponent) {
        int border = 0;
        Object borderAttribute = uiComponent.getAttributes().get("border");
        if (borderAttribute instanceof Integer) {
            if (((Integer) borderAttribute).intValue() != Integer.MIN_VALUE) {
                border = ((Integer) borderAttribute).intValue();
            }
        } else {
            try {
                border = Integer.valueOf(borderAttribute.toString()).intValue();
            } catch (NumberFormatException nfe) {
                // couldn't parse it; stick with the default (initial) value of 0
            }
        }
        return border;
    }

    protected void renderOption(FacesContext facesContext,
                                UIComponent uiComponent,
                                SelectItem selectItem, boolean renderVertically,
                                Element rootTable, Element rootTR, int counter,
                                Object[] submittedValue, Object componentValue)
            throws IOException {

        DOMContext domContext =
                DOMContext.getDOMContext(facesContext, uiComponent);
        boolean disabled = false;
        if (uiComponent.getAttributes().get("disabled") != null) {
            if ((uiComponent.getAttributes().get("disabled"))
                    .equals(Boolean.TRUE)) {
                disabled = true;
            }
        }
        if (selectItem.isDisabled()) {
            disabled = true;
        }

        if (renderVertically) {
            rootTR = domContext.createElement("tr");
            rootTable.appendChild(rootTR);
        }
        Element td = domContext.createElement("td");
        rootTR.appendChild(td);
        
        String clientId = uiComponent.getClientId(facesContext);
        String itemId = ClientIdPool.get(clientId + ":_" + counter);
        
        Element inputElement = domContext.createElement("input");
        inputElement
                .setAttribute("name", clientId);
        inputElement.setAttribute("id", itemId);
        td.appendChild(inputElement);
        
        if( selectItem.getLabel() != null ){
        	Element label = domContext.createElement("label");
            label.setAttribute(HTML.FOR_ATTR, itemId);
            Text textNode =
                domContext.getDocument().createTextNode(selectItem.getLabel());
            label.appendChild(textNode);            
            td.appendChild(label);
        }
        
        HashSet excludes = new HashSet();
        String accesskey =
                (String) uiComponent.getAttributes().get("accesskey");
        if (accesskey != null) {
            inputElement.setAttribute("accesskey", accesskey);
            excludes.add("accesskey");
        }

        String formattedOptionValue = formatComponentValue(
                facesContext,
                uiComponent,
                selectItem.getValue());
        inputElement.setAttribute("value", formattedOptionValue);
        inputElement.setAttribute("type", "checkbox");

        if (isValueSelected(facesContext, selectItem, uiComponent,
            submittedValue, componentValue))
        {
            inputElement.setAttribute("checked", Boolean.TRUE.toString());
        }
        if (disabled) {
            inputElement.setAttribute("disabled", "disabled");
        }
        
        boolean readonly = false;
        if (uiComponent.getAttributes().get("readonly") != null) {
            if ((uiComponent.getAttributes().get("readonly"))
                    .equals(Boolean.TRUE)) {
                readonly = true;
            }
        }
        if (readonly) {
            inputElement.setAttribute("readonly", "readonly");
        }
        
        excludes.add("style");
        excludes.add("border");
        String[] excludesStringArray = new String[excludes.size()];
        excludesStringArray = (String[]) excludes.toArray(excludesStringArray);
        PassThruAttributeRenderer.renderHtmlAttributes(
                facesContext, uiComponent,
                inputElement, rootTable,
                selectManyCheckboxPassThruAttributes);

        inputElement.setAttribute("onkeypress", combinedPassThru((String) uiComponent.getAttributes().get("onkeypress"),
                "Ice.util.radioCheckboxEnter(form,this,event);"));
    }

    protected void renderOption(FacesContext facesContext, UIComponent uiComponent) throws IOException {
        validateParameters(facesContext, uiComponent, null);

        UIComponent forComponent = findForComponent(facesContext, uiComponent);
        if (!(forComponent instanceof UISelectMany)) {
            throw new IllegalStateException("Could not find UISelectMany component for checkbox.");
        }
        String layout = (String) forComponent.getAttributes().get("layout");
        if (layout == null || !layout.equals("spread")) {
            return;
        }
        List selectItemList = getSelectItemList(forComponent);
        if (selectItemList.isEmpty()) {
            throw new IllegalStateException("Could not find select items for UISelectMany component.");
        }
        
        Object[] submittedValue = getSubmittedSelectedValues(forComponent);
        Object componentValue = (submittedValue != null) ? null :
            getCurrentSelectedValues(forComponent);

        UISelectMany selectMany = (UISelectMany) forComponent;
        int checkboxIndex = ((Integer) uiComponent.getAttributes().get("index")).intValue();
        if (checkboxIndex < 0) checkboxIndex = 0;
        if (checkboxIndex >= selectItemList.size()) checkboxIndex = selectItemList.size() - 1;
        SelectItem selectItem = (SelectItem) selectItemList.get(checkboxIndex);

        String selectManyClientId = selectMany.getClientId(facesContext);
        String checkboxClientId = ClientIdPool.get(selectManyClientId + ":_" + checkboxIndex);

        String selectItemValue = formatComponentValue(facesContext, selectMany, selectItem.getValue());
        String selectItemLabel = selectItem.getLabel();

        DOMContext domContext = DOMContext.attachDOMContext(facesContext, uiComponent);
        if (domContext.isInitialized()) {
            DOMContext.removeChildren(domContext.getRootNode());
        } else {
            domContext.createRootElement(HTML.SPAN_ELEM);
        }
        Node rootNode = domContext.getRootNode();
        HashSet excludes = new HashSet();

        Element input = domContext.createElement(HTML.INPUT_ELEM);
        input.setAttribute(HTML.TYPE_ATTR, HTML.INPUT_TYPE_CHECKBOX);
        input.setAttribute(HTML.ID_ATTR, checkboxClientId);
        input.setAttribute(HTML.NAME_ATTR, selectManyClientId);
        input.setAttribute(HTML.VALUE_ATTR, selectItemValue);
        if (selectItem.isDisabled()) {
            input.setAttribute(HTML.DISABLED_ATTR, HTML.DISABLED_ATTR);
        }
        if (isValueSelected(facesContext, selectItem, selectMany,
            submittedValue, componentValue))
        {
            input.setAttribute(HTML.CHECKED_ATTR, HTML.CHECKED_ATTR);
        }
        addJavaScript(facesContext, selectMany, input, excludes);

        Element label = domContext.createElement(HTML.LABEL_ATTR);
        label.setAttribute(HTML.FOR_ATTR, checkboxClientId);
        if (selectItemLabel != null) 
        	label.appendChild(domContext.createTextNode(selectItemLabel));

        PassThruAttributeRenderer.renderHtmlAttributes(facesContext, selectMany, input, label, selectManyCheckboxPassThruAttributes);
        PassThruAttributeRenderer.renderBooleanAttributes(
                facesContext,
                uiComponent,
                input,
                PassThruAttributeRenderer.EMPTY_STRING_ARRAY) ;
        input.setAttribute("onkeypress", combinedPassThru((String) selectMany.getAttributes().get("onkeypress"),
                "Ice.util.radioCheckboxEnter(form,this,event);"));
        rootNode.appendChild(input);
        rootNode.appendChild(label);

        domContext.stepOver();
    }

    protected void addJavaScript(FacesContext facesContext,
                                 UIComponent uiComponent, Element root,
                                 Set excludes) {
    }

    protected List getSelectItemList(UIComponent uiComponent) {
        List list = new ArrayList();
        Iterator iter = getSelectItems(uiComponent);
        while (iter.hasNext()) {
            Object o = iter.next();
            if (o instanceof SelectItemGroup) {
                addSelectItemGroupToList((SelectItemGroup) o, list);
                continue;
            }
            list.add(o);
        }
        return list;
    }

    private void addSelectItemGroupToList(SelectItemGroup selectItemGroup, List list) {
        SelectItem[] selectItems = selectItemGroup.getSelectItems();
        if (selectItems == null || selectItems.length == 0) {
            list.add(selectItemGroup);
            return;
        }
        for (int i = 0; i < selectItems.length; i++) {
            SelectItem selectItem = selectItems[i];
            if (selectItem instanceof SelectItemGroup) {
                addSelectItemGroupToList((SelectItemGroup) selectItem, list);
                continue;
            }
            list.add(selectItem);
        }
    }
    
    protected Element getTableElement(DOMContext domContext) {
        return (Element) domContext.getRootNode();
    }
    
    protected Element createRootNode(DOMContext domContext) {
        return (Element) domContext.createRootElement("table");
    }
    
}