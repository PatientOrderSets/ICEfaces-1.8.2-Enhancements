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
import com.icesoft.faces.context.effects.JavascriptContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.apache.commons.logging.LogFactory;

import javax.faces.component.*;
import javax.faces.component.html.HtmlSelectManyCheckbox;
import javax.faces.component.html.HtmlSelectManyListbox;
import javax.faces.component.html.HtmlSelectManyMenu;
import javax.faces.component.html.HtmlSelectOneListbox;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.el.ValueBinding;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

public class MenuRenderer extends DomBasicInputRenderer {

    private static Logger log =  Logger.getLogger(MenuRenderer.class.getName());
    private static final String[] selectOneMenuPassThruAttributes = AttributeConstants.getAttributes(AttributeConstants.H_SELECTONEMENU);
    private static final String[] selectManyMenuPassThruAttributes = AttributeConstants.getAttributes(AttributeConstants.H_SELECTMANYMENU);
    private static final String[] selectOneListboxPassThruAttributes = AttributeConstants.getAttributes(AttributeConstants.H_SELECTONELISTBOX);
    private static final String[] selectManyListboxPassThruAttributes = AttributeConstants.getAttributes(AttributeConstants.H_SELECTMANYLISTBOX);
    
    public void decode(FacesContext facesContext, UIComponent uiComponent) {
        validateParameters(facesContext, uiComponent, null);
        if (isStatic(uiComponent)) {
            return;
        }
        String clientId = uiComponent.getClientId(facesContext);
        if (uiComponent instanceof UISelectMany) {
            Map requestParameterValuesMap = facesContext.getExternalContext()
                    .getRequestParameterValuesMap();
            if (requestParameterValuesMap.containsKey(clientId)) {
                String[] decodedValue =
                        (String[]) requestParameterValuesMap.get(clientId);
                setSubmittedValue(uiComponent, decodedValue);
            } else {
                // This represents a deselected control
                setSubmittedValue(uiComponent, new String[0]);
            }
        } else if (uiComponent instanceof UISelectOne) {
            Map requestParameterValuesMap = facesContext.getExternalContext()
                    .getRequestParameterValuesMap();
            String decodedValue = null;
            if ((requestParameterValuesMap != null) && (requestParameterValuesMap.containsKey(clientId))) {
                decodedValue =
                        ((String[]) requestParameterValuesMap.get(clientId))[0];
            } else {
                //none of the option has been selected
                //set it to a blank string, not to null
                decodedValue = "";
            }
            ((UISelectOne) uiComponent).setSubmittedValue(decodedValue);

        }
        return;
    }

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

        renderSelect(facesContext, uiComponent);
        JavascriptContext.fireEffect(uiComponent, facesContext);
    }

    public Object getConvertedValue(FacesContext facesContext,
                                    UIComponent uiComponent,
                                    Object newSubmittedValue)
            throws ConverterException {
        if (uiComponent instanceof UISelectOne) {
            if (newSubmittedValue == null || "".equals(newSubmittedValue)) {
                return null;
            } else {
                return super.getConvertedValue(facesContext,
                                               (UISelectOne) uiComponent,
                                               newSubmittedValue);
            }
        } else {
            return convertSelectValue(facesContext,
                                      ((UISelectMany) uiComponent),
                                      (String[]) newSubmittedValue);
        }
    }

    public Object convertSelectValue(FacesContext facesContext,
                                     UIComponent uiComponent,
                                     Object newSubmittedValue)
            throws ConverterException {
        if (uiComponent instanceof UISelectOne) {
            if (newSubmittedValue == null || "".equals(newSubmittedValue)) {
                return null;
            } else {
                return super.getConvertedValue(facesContext,
                                               (UISelectOne) uiComponent,
                                               newSubmittedValue);
            }
        } else {
            return convertSelectValue(facesContext,
                                      ((UISelectMany) uiComponent),
                                      (String[]) newSubmittedValue);
        }
    }

    public Object convertSelectValue(FacesContext facesContext,
                                     UISelectMany uiSelectMany,
                                     String[] newSubmittedValues)
            throws ConverterException {

        ValueBinding valueBinding = uiSelectMany.getValueBinding("value");
        if (valueBinding == null) {
            Class componentType = (new Object[1]).getClass().getComponentType();
            return convertArray(facesContext, uiSelectMany, componentType,
                                newSubmittedValues);
        }
        Class valueBindingClass = valueBinding.getType(facesContext);
        if (valueBindingClass == null) {
            throw new ConverterException("Inconvertible type in value binding");
        }
        if (List.class.isAssignableFrom(valueBindingClass)) {
            Converter converter = uiSelectMany.getConverter();
            if (converter == null) {
                // Determine if there is a default converter for the class
                converter = getConverterForClass(valueBindingClass);
            }
            
            ArrayList submittedValuesAsList = new ArrayList(
                    newSubmittedValues.length);
            for (int index = 0; index < newSubmittedValues.length; index++) {
                Object convertedValue = newSubmittedValues[index];
                if (converter != null) {
                    convertedValue = converter.getAsObject(
                        facesContext, uiSelectMany, newSubmittedValues[index]);
                }
                submittedValuesAsList.add(convertedValue);
            }
            return submittedValuesAsList;
        }
        if (valueBindingClass.isArray()) {
            Class componentType = valueBindingClass.getComponentType();
            return convertArray(facesContext, uiSelectMany, componentType,
                                newSubmittedValues);
        }
        throw new ConverterException(
                "Non-list and Non-array values are inconvertible");
    }

    protected Object convertArray(FacesContext facesContext,
                                  UISelectMany uiSelectMany,
                                  Class componentType,
                                  String[] newSubmittedValues)
            throws ConverterException {

        // component type of String means no conversion is necessary
        if (componentType.equals(String.class)) {
            return newSubmittedValues;
        }

        // if newSubmittedValue is null return zero-length array
        if (newSubmittedValues == null) {
            return Array.newInstance(componentType, 0);
        }

        // create the array with specified component length
        int numberOfValues = newSubmittedValues.length;
        Object convertedValues = Array.newInstance(componentType,
                                                   numberOfValues);

        // Determine if a converter is explicitly registered with the component
        Converter converter = uiSelectMany.getConverter();
        if (converter == null) {
            // Determine if there is a default converter for the class
            converter = getConverterForClass(componentType);
        }
        if (converter == null) {
            // we don't need to convert base Object types
            if (componentType.equals(Object.class)) {
                return newSubmittedValues;
            } else {
                throw new ConverterException("Converter is null");
            }
        }

        for (int index = 0; index < numberOfValues; index++) {

            // convert the next element
            Object nextConvertedElement = converter.getAsObject(facesContext,
                                                                uiSelectMany,
                                                                newSubmittedValues[index]);

            if (!componentType.isPrimitive()) {
                Array.set(convertedValues, index, nextConvertedElement);
            } else if (componentType.equals(Boolean.TYPE)) {

                Array.setBoolean(convertedValues, index,
                                 ((Boolean) nextConvertedElement).booleanValue());

            } else if (componentType.equals(Integer.TYPE)) {

                Array.setInt(convertedValues, index,
                             ((Integer) nextConvertedElement).intValue());

            } else if (componentType.equals(Long.TYPE)) {

                Array.setLong(convertedValues, index,
                              ((Long) nextConvertedElement).longValue());

            } else if (componentType.equals(Short.TYPE)) {

                Array.setShort(convertedValues, index,
                               ((Short) nextConvertedElement).shortValue());

            } else if (componentType.equals(Byte.TYPE)) {

                Array.setByte(convertedValues, index,
                              ((Byte) nextConvertedElement).byteValue());

            } else if (componentType.equals(Float.TYPE)) {

                Array.setFloat(convertedValues, index,
                               ((Float) nextConvertedElement).floatValue());

            } else if (componentType.equals(Double.TYPE)) {

                Array.setDouble(convertedValues, index,
                                ((Double) nextConvertedElement).doubleValue());

            } else if (componentType.equals(Character.TYPE)) {

                Array.setChar(convertedValues, index,
                              ((Character) nextConvertedElement).charValue());

            }
        }
        return convertedValues;
    }

    protected void renderOption(FacesContext facesContext,
                                UIComponent uiComponent,
                                SelectItem selectItem, Element optionGroup,
                                Object[] submittedValues, Object selectedValues)
            throws IOException {

        DOMContext domContext =
                DOMContext.getDOMContext(facesContext, uiComponent);

        Element select = (Element) domContext.getRootNode();
        Element option = domContext.createElement("option");

        if (optionGroup == null) {
            select.appendChild(option);
        } else {
            optionGroup.appendChild(option);
        }

        String valueString = formatComponentValue(facesContext, uiComponent,
                                                  selectItem.getValue());
        option.setAttribute("value", valueString);

        boolean selected = isValueSelected(facesContext, selectItem, uiComponent,
                submittedValues, selectedValues);
        if (uiComponent instanceof HtmlSelectOneMenu) {
            if (submittedValues == null && selectedValues== null && 
                    (selectItem.getValue() == "" || selectItem.getValue() == null))
                selected = true;
        }
        
        if (selected) {
            option.setAttribute("selected", "selected");
        }
        if (selectItem.isDisabled()) {
            option.setAttribute("disabled", "disabled");
        }

        Document doc = domContext.getDocument();
        String label = selectItem.getLabel();
        Text labelNode = doc.createTextNode(label == null ? valueString : label);
        option.appendChild(labelNode);
    }

    protected boolean isValueSelected(
        FacesContext facesContext,
        SelectItem selectItem,
        UIComponent uiComponent,
        Object[] submittedValues,
        Object selectedValues)
    {
        if (submittedValues != null) {
            String valueString = formatComponentValue(facesContext, uiComponent, selectItem.getValue());
            return isSelected(valueString, submittedValues);
        }
        return isSelected(selectItem.getValue(), selectedValues, facesContext, uiComponent);
    }

    void renderSelect(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
        HashSet excludes = new HashSet();
        // setup
        DOMContext domContext =
                DOMContext.attachDOMContext(facesContext, uiComponent);
        if (!domContext.isInitialized()) {
            Element root = domContext.createElement("select");
            domContext.setRootNode(root);
            setRootElementId(facesContext, root, uiComponent);
            root.setAttribute("name", uiComponent.getClientId(facesContext));
            // render styleClass attribute if present.
            String styleClass = null;
            if (null != (styleClass = (String) uiComponent.getAttributes().get(
                    "styleClass"))) {
                root.setAttribute("class", styleClass);
            }
            if (!getMultipleText(uiComponent).equals("")) {
                root.setAttribute("multiple", "multiple");
            }
        }
        Element root = (Element) domContext.getRootNode();

        // Determine how many option(s) we need to render, and update
        // the component's "size" attribute accordingly; The "size"
        // attribute will be rendered as one of the "pass thru" attributes
        int itemCount = countSelectOptionsRecursive(facesContext, uiComponent);
        // If "size" is *not* set explicitly, we have to default it correctly
        Object size = uiComponent.getAttributes().get("size");
        if ((null == size)
            || (  ( size instanceof Integer) &&
                (  ((Integer) size).intValue() == Integer.MIN_VALUE ||
                 ((Integer) size).intValue() == 0) ) ) {
            renderSizeAttribute(root, itemCount);
            excludes.add("size");
        } else {
            renderSizeAttribute(root, Integer.valueOf(size.toString()).intValue());
        }

        Object currentValue = null;
        if (null ==
            (currentValue = ((UIInput) uiComponent).getSubmittedValue())) {
            currentValue = "";
        }

        addJavaScript(facesContext, uiComponent, root, currentValue.toString(),
                      excludes);

        if (uiComponent instanceof HtmlSelectOneMenu) {
            PassThruAttributeRenderer.renderHtmlAttributes(facesContext, uiComponent,selectOneMenuPassThruAttributes);
        } else if (uiComponent instanceof HtmlSelectManyMenu) {
            PassThruAttributeRenderer.renderHtmlAttributes(facesContext, uiComponent,selectManyMenuPassThruAttributes);
        } else if (uiComponent instanceof HtmlSelectOneListbox) {
            PassThruAttributeRenderer.renderHtmlAttributes(facesContext, uiComponent,selectOneListboxPassThruAttributes);
        } else if (uiComponent instanceof HtmlSelectManyListbox) { 
            PassThruAttributeRenderer.renderHtmlAttributes(facesContext, uiComponent,selectManyListboxPassThruAttributes);
        }
        String[] attributes = new String[]{HTML.DISABLED_ATTR, HTML.READONLY_ATTR};
        Object attribute;
        for (int i = 0; i < attributes.length; i++) {
            attribute = uiComponent.getAttributes().get(attributes[i]);
            if (attribute instanceof Boolean && ((Boolean) attribute).booleanValue()) {
                root.setAttribute(attributes[i], attributes[i]);
            }
        }
        excludes.clear();

        domContext.stepInto(uiComponent);
        renderOptions(facesContext, uiComponent);
        domContext.stepOver();
    }

    public String getEventType(UIComponent uiComponent) {
        if (uiComponent instanceof javax.faces.component.html.HtmlSelectOneListbox)
        {
            return "onchange";
        } else
        if (uiComponent instanceof javax.faces.component.html.HtmlSelectOneMenu)
        {
            return "onchange";
        } else
        if (uiComponent instanceof javax.faces.component.html.HtmlSelectManyListbox)
        {
            return "onchange";
        } else
        if (uiComponent instanceof javax.faces.component.html.HtmlSelectManyMenu)
        {
            return "onchange";
        } else if (uiComponent instanceof HtmlSelectManyCheckbox) {
            return "onclick";
        }
        return "";
    }

    int countSelectOptionsRecursive(FacesContext facesContext,
                                    UIComponent uiComponent) {
        int counter = 0;
        Iterator selectItems = getSelectItems(uiComponent);
        while (selectItems.hasNext()) {
            counter++;
            SelectItem nextSelectItem = (SelectItem) selectItems.next();
            if (nextSelectItem instanceof SelectItemGroup) {
                counter += ((SelectItemGroup) nextSelectItem)
                        .getSelectItems().length;
            }
        }
        return counter;
    }

    void renderOptions(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {

        DOMContext domContext =
                DOMContext.getDOMContext(facesContext, uiComponent);

        Element rootSelectElement = (Element) domContext.getRootNode();
        DOMContext.removeChildrenByTagName(rootSelectElement, "option");
        DOMContext.removeChildrenByTagName(rootSelectElement, "optgroup");

        Iterator selectItems = getSelectItems(uiComponent);
        Object[] submittedValues = null;
        Object selectedValues = null;
        if (selectItems.hasNext()) {
            submittedValues = getSubmittedSelectedValues(uiComponent);
            if (submittedValues == null) {
                selectedValues = getCurrentSelectedValues(uiComponent);
            }
        }
        while (selectItems.hasNext()) {
            SelectItem nextSelectItem = (SelectItem) selectItems.next();
            if (nextSelectItem instanceof SelectItemGroup) {
                Element optGroup = domContext.createElement("optgroup");
                rootSelectElement.appendChild(optGroup);
                optGroup.setAttribute("label", nextSelectItem.getLabel());
                domContext.setCursorParent(optGroup);
                SelectItem[] selectItemsArray =
                        ((SelectItemGroup) nextSelectItem).getSelectItems();
                for (int i = 0; i < selectItemsArray.length; ++i) {
                    renderOption(facesContext, uiComponent, selectItemsArray[i],
                                 optGroup, submittedValues, selectedValues);
                }
            } else {
                renderOption(facesContext, uiComponent, nextSelectItem, null,
                             submittedValues, selectedValues);
            }
        }
    }

    boolean isSelected(Object sentinel, Object selectedValues, FacesContext facesContext, UIComponent uiComponent) {
        boolean isSelected = false;
         if (selectedValues == null || sentinel == null) {
            return isSelected;
        }
        String formattedSelectedValue;
        String formattedSentinel = formatComponentValue(facesContext, uiComponent, sentinel);
        int length = Array.getLength(selectedValues);
        for (int index = 0; index < length; index++) {
            Object nextSelectedValue = Array.get(selectedValues, index);
            formattedSelectedValue = formatComponentValue(facesContext, uiComponent, nextSelectedValue);
            if (nextSelectedValue == null && sentinel == null) {
                isSelected = true;
                break;
            } else if (nextSelectedValue != null && nextSelectedValue.equals(sentinel)) {
                isSelected = true;
                break;
            }else if (sentinel instanceof String) {
            	if (isConversionMatched(sentinel.toString(), nextSelectedValue)) {
	            	isSelected = true;
	            	break;
            	}
                if (formattedSelectedValue.equals(sentinel)) {
                    isSelected = true;
                    break;
                }
            } else if (formattedSelectedValue != null && formattedSelectedValue.equals(formattedSentinel)) {
                isSelected = true;
                break;
            }
        }
        return isSelected;
    }

    boolean isSelected(Object sentinelValue, Object[] selectedValues) {
        boolean valueIsSelected = false;
        if (selectedValues != null) {
            Iterator selectedValuesIterator =
                    Arrays.asList(selectedValues).iterator();
            while (selectedValuesIterator.hasNext()) {
                if (selectedValuesIterator.next().equals(sentinelValue)) {
                    valueIsSelected = true;
                    break;
                }
            }
        }
        return valueIsSelected;
    }

    /**
     * Render "1" as the value of the size attribute
     *
     * @param targetElement
     * @param size
     * @throws IOException
     */
    protected void renderSizeAttribute(Element targetElement, int size)
            throws IOException {
        targetElement.setAttribute("size", "1");
    }

    String getSelectedTextString() {
        return " selected";
    }

    // To derive a selectOne type component from this, override
    // these methods.
    public String getMultipleText(UIComponent component) {
        if (component instanceof UISelectMany) {
            return " multiple ";
        }
        return "";
    }

    Object[] getSubmittedSelectedValues(UIComponent uiComponent) {
        if (uiComponent instanceof UISelectMany) {
            UISelectMany uiSelectMany = (UISelectMany) uiComponent;
            return (Object[]) uiSelectMany.getSubmittedValue();
        }
        if (uiComponent instanceof UISelectOne) {
            UISelectOne uiSelectOne = (UISelectOne) uiComponent;
            Object submittedValue = uiSelectOne.getSubmittedValue();
            if (submittedValue != null) {
                return new Object[]{submittedValue};
            }
        }
        return null;
    }


    Object getCurrentSelectedValues(UIComponent uiComponent) {
        Object currentSelectedValues = null;
        if (uiComponent instanceof UISelectMany) {
            UISelectMany uiSelectMany = (UISelectMany) uiComponent;
            currentSelectedValues = uiSelectMany.getValue();
            if (currentSelectedValues instanceof List) {
                return ((List) currentSelectedValues).toArray();
            }
            return currentSelectedValues;
        }
        if (uiComponent instanceof UISelectOne) {
            UISelectOne uiSelectOne = (UISelectOne) uiComponent;
            currentSelectedValues = uiSelectOne.getValue();
            if (currentSelectedValues != null) {
                return new Object[]{currentSelectedValues};
            }
        }
        return null;
    }

    protected Iterator getSelectItems(UIComponent uiComponent) {

        List selectItems = new ArrayList();
        if (uiComponent.getChildCount() == 0) return selectItems.iterator();
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
                    //If user defines only one member, either itemValue or itemLabel
                    //The default implementation throws a null pointer exception.
                    //So here we are identifying, if either itemValue or itemLabel is found,
                    //Assigned its value to the other member
                    assignDataIfNull(nextSelectItemChild);
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
                    } else if (selectItemsValue instanceof String[]) {
                        String stringItemArray[] = (String[]) selectItemsValue;
                        for (int i = 0; i < stringItemArray.length; i++) {
                            selectItems.add(new SelectItem(stringItemArray[i]));
                        }
                    }
                }
            }
        }
        return selectItems.iterator();
    }

    private void assignDataIfNull(Object selectItem) {
        UISelectItem uiSelectItem = (UISelectItem) selectItem;
        if (uiSelectItem.getItemValue() == null) {
            if (uiSelectItem.getItemLabel() != null) {
                uiSelectItem.setItemValue(uiSelectItem.getItemLabel());
            }
        }
        if (uiSelectItem.getItemLabel() == null) {
            if (uiSelectItem.getItemValue() != null) {
                uiSelectItem
                        .setItemLabel(uiSelectItem.getItemValue().toString());
            }
        }
    }

    protected void addJavaScript(FacesContext facesContext,
                                 UIComponent uiComponent, Element root,
                                 String currentValue,
                                 Set excludes) {
    }
    
    private boolean isConversionMatched(String sentinel, Object selectedValue){
    	boolean match = false;
        if (sentinel.length() == 0){
            if (selectedValue == null) {
                match = true;
            }
        }
        else if (selectedValue instanceof Long){
    		if (selectedValue.equals(Long.valueOf(sentinel))) {
    			match = true;
    		}
    	} else if (selectedValue instanceof Byte) {
    		if (selectedValue.equals(Byte.valueOf(sentinel))) {
    			match = true;
    		}
    	} else if (selectedValue instanceof Integer) {
    		if (selectedValue.equals(Integer.valueOf(sentinel))) {
    			match = true;
    		}    		
    	} else if (selectedValue instanceof Short) {
    		if (selectedValue.equals(Short.valueOf(sentinel))) {
    			match = true;
    		}
    	} else if (selectedValue instanceof Double) {
    		if (selectedValue.equals(Double.valueOf(sentinel))) {
    			match = true;
    		}
    	} else if (selectedValue instanceof Float) {
    		if (selectedValue.equals(Float.valueOf(sentinel))) {
    			match = true;
    		}
    	} else if (selectedValue instanceof Boolean) {
    		if (selectedValue.equals(Boolean.valueOf(sentinel))) {
    			match = true;
    		}
    	}
    	return match;
    }
}
