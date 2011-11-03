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

package com.icesoft.faces.component.ext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.model.SelectItem;

import com.icesoft.faces.component.CSS_DEFAULT;
import com.icesoft.faces.component.IceExtended;
import com.icesoft.faces.component.PORTLET_CSS_DEFAULT;
import com.icesoft.faces.component.ext.taglib.Util;
import com.icesoft.faces.context.BridgeFacesContext;
import com.icesoft.faces.context.effects.CurrentStyle;
import com.icesoft.faces.context.effects.Effect;
import com.icesoft.faces.context.effects.JavascriptContext;
import com.icesoft.faces.util.CoreUtils;


/**
 * This is an extension of javax.faces.component.html.HtmlSelectOneMenu, which
 * provides some additional behavior to this component such as: <ul> <li>default
 * classes for enabled and disabled state</li> <li>provides partial submit
 * mechanism</li> <li>changes the component's enabled and rendered state based
 * on the authentication</li> <li>adds effects to the component</li> <ul>
 */
public class HtmlSelectOneMenu
        extends javax.faces.component.html.HtmlSelectOneMenu
        implements IceExtended {

    public static final String COMPONENT_TYPE =
            "com.icesoft.faces.HtmlSelectOneMenu";
    public static final String RENDERER_TYPE = "com.icesoft.faces.Menu";
    private static final boolean DEFAULT_VISIBLE = true;
    private String styleClass = null;
    private Boolean partialSubmit = null;
    private String enabledOnUserRole = null;
    private String renderedOnUserRole = null;
    private Effect effect;
    private Boolean visible = null;
    private Effect onclickeffect;
    private Effect ondblclickeffect;
    private Effect onmousedowneffect;
    private Effect onmouseupeffect;
    private Effect onmousemoveeffect;
    private Effect onmouseovereffect;
    private Effect onmouseouteffect;

    private Effect onkeypresseffect;
    private Effect onkeydowneffect;
    private Effect onkeyupeffect;


    private CurrentStyle currentStyle;

    private Effect onchangeeffect;

    public HtmlSelectOneMenu() {
        super();
        setRendererType(RENDERER_TYPE);
    }

    /**
     * This method is used to communicate a focus request from the application
     * to the client browser.
     */
    public void requestFocus() {
        ((BridgeFacesContext) FacesContext.getCurrentInstance())
                .setFocusId("null");
        JavascriptContext.focus(FacesContext.getCurrentInstance(),
                                this.getClientId(
                                        FacesContext.getCurrentInstance()));
    }

    public SelectItem[] getSelectItems() {
        List selectItems = new ArrayList();
        Iterator children = this.getChildren().iterator();

        while (children.hasNext()) {
            UIComponent child = (UIComponent) children.next();
            if (child instanceof UISelectItem) {
                Object selectItemValue = ((UISelectItem) child).getValue();
                if (selectItemValue != null &&
                    selectItemValue instanceof SelectItem) {
                    selectItems.add(selectItemValue);
                } else {
                    //If user defines only one member, either itemValue or itemLabel
                    //The default implementation throws a null pointer exception.
                    //So here we are identifying, if either itemValue or itemLabel is found,
                    //Assigned its value to the other member
                    assignDataIfNull(child);
                    selectItems.add(
                            new SelectItem(
                                    ((UISelectItem) child).getItemValue(),
                                    ((UISelectItem) child).getItemLabel(),
                                    ((UISelectItem) child).getItemDescription(),
                                    ((UISelectItem) child).isItemDisabled()));
                }
            } else if (child instanceof UISelectItems) {
                Object selectItemsValue = ((UISelectItems) child).getValue();

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
        return (SelectItem[]) selectItems
                .toArray(new SelectItem[selectItems.size()]);
    }

    static void assignDataIfNull(Object selectItem) {
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

    public void setValueBinding(String s, ValueBinding vb) {
        if (s != null && s.indexOf("effect") != -1) {
            // If this is an effect attribute make sure Ice Extras is included
            JavascriptContext.includeLib(JavascriptContext.ICE_EXTRAS,
                                         getFacesContext());
        }
        super.setValueBinding(s, vb);
    }

    /**
     * <p>Set the value of the <code>visible</code> property.</p>
     */
    public void setVisible(boolean visible) {
        this.visible = Boolean.valueOf(visible);
    }

    /**
     * <p>Return the value of the <code>visible</code> property.</p>
     */
    public boolean getVisible() {
        if (visible != null) {
            return visible.booleanValue();
        }
        ValueBinding vb = getValueBinding("visible");
        Boolean boolVal =
                vb != null ? (Boolean) vb.getValue(getFacesContext()) : null;
        return boolVal != null ? boolVal.booleanValue() : DEFAULT_VISIBLE;
    }

    /**
     * <p>Set the value of the <code>effect</code> property.</p>
     */
    public void setEffect(Effect effect) {
        this.effect = effect;
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
    }

    /**
     * <p>Return the value of the <code>effect</code> property.</p>
     */
    public Effect getEffect() {
        if (effect != null) {
            return effect;
        }
        ValueBinding vb = getValueBinding("effect");
        return vb != null ? (Effect) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>visible</code> property.</p>
     */
    public void setPartialSubmit(boolean partialSubmit) {
        this.partialSubmit = Boolean.valueOf(partialSubmit);
    }

    /**
     * <p>Return the value of the <code>partialSubmit</code> property.</p>
     */
    public boolean getPartialSubmit() {
        if (partialSubmit != null) {
            return partialSubmit.booleanValue();
        }
        ValueBinding vb = getValueBinding("partialSubmit");
        Boolean boolVal =
                vb != null ? (Boolean) vb.getValue(getFacesContext()) : null;
        return boolVal != null ? boolVal.booleanValue() :
               Util.isParentPartialSubmit(this);
    }

    /**
     * <p>Set the value of the <code>enabledOnUserRole</code> property.</p>
     */
    public void setEnabledOnUserRole(String enabledOnUserRole) {
        this.enabledOnUserRole = enabledOnUserRole;
    }

    /**
     * <p>Return the value of the <code>enabledOnUserRole</code> property.</p>
     */
    public String getEnabledOnUserRole() {
        if (enabledOnUserRole != null) {
            return enabledOnUserRole;
        }
        ValueBinding vb = getValueBinding("enabledOnUserRole");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>renderedOnUserRole</code> property.</p>
     */
    public void setRenderedOnUserRole(String renderedOnUserRole) {
        this.renderedOnUserRole = renderedOnUserRole;
    }

    /**
     * <p>Return the value of the <code>renderedOnUserRole</code> property.</p>
     */
    public String getRenderedOnUserRole() {
        if (renderedOnUserRole != null) {
            return renderedOnUserRole;
        }
        ValueBinding vb = getValueBinding("renderedOnUserRole");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Return the value of the <code>rendered</code> property.</p>
     */
    public boolean isRendered() {
        if (!Util.isRenderedOnUserRole(this)) {
            return false;
        }
        return super.isRendered();
    }

    /**
     * <p>Set the value of the <code>styleClass</code> property.</p>
     */
    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    /**
     * <p>Return the value of the <code>styleClass</code> property.</p>
     */
    public String getStyleClass() {
        return Util.getQualifiedStyleClass(this, 
                styleClass,
                CSS_DEFAULT.SELECT_ONE_MENU_DEFAULT_STYLE_CLASS,
                "styleClass",
                isDisabled(),
                PORTLET_CSS_DEFAULT.PORTLET_FORM_FIELD);  
    }

    /**
     * <p>Return the value of the <code>disabled</code> property.</p>
     */
    public boolean isDisabled() {
        if (!Util.isEnabledOnUserRole(this)) {
            return true;
        } else {
            return super.isDisabled();
        }
    }


    /**
     * <p>Return the value of the <code>onchangeeffect</code> property.</p>
     */
    public Effect getOnchangeeffect() {
        if (onchangeeffect != null) {
            return onchangeeffect;
        }
        ValueBinding vb = getValueBinding("onchangeeffect");

        return vb != null ? (Effect) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>onchangeeffect</code> property.</p>
     */
    public void setOnchangeeffect(Effect onchangeeffect) {
        this.onchangeeffect = onchangeeffect;
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
    }


    /**
     * <p>Return the value of the <code>currentStyle</code> property.</p>
     */
    public CurrentStyle getCurrentStyle() {
        return currentStyle;
    }

    /**
     * <p>Set the value of the <code>currentStyle</code> property.</p>
     */
    public void setCurrentStyle(CurrentStyle currentStyle) {
        this.currentStyle = currentStyle;
    }






    /**
     * <p>Set the value of the <code>onclickeffect</code> property.</p>
     */
    public void setOnclickeffect(Effect onclickeffect) {
        this.onclickeffect = onclickeffect;
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
    }

    /**
     * <p>Return the value of the <code>ondblclickeffect</code> property.</p>
     */
    public Effect getOndblclickeffect() {
        if (ondblclickeffect != null) {
            return ondblclickeffect;
        }
        ValueBinding vb = getValueBinding("ondblclickeffect");

        return vb != null ? (Effect) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>ondblclickeffect</code> property.</p>
     */
    public void setOndblclickeffect(Effect ondblclickeffect) {
        this.ondblclickeffect = ondblclickeffect;
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
    }

    /**
     * <p>Return the value of the <code>onmousedowneffect</code> property.</p>
     */
    public Effect getOnmousedowneffect() {
        if (onmousedowneffect != null) {
            return onmousedowneffect;
        }
        ValueBinding vb = getValueBinding("onmousedowneffect");

        return vb != null ? (Effect) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>onmousedowneffect</code> property.</p>
     */
    public void setOnmousedowneffect(Effect onmousedowneffect) {
        this.onmousedowneffect = onmousedowneffect;
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
    }

    /**
     * <p>Return the value of the <code>onmouseupeffect</code> property.</p>
     */
    public Effect getOnmouseupeffect() {
        if (onmouseupeffect != null) {
            return onmouseupeffect;
        }
        ValueBinding vb = getValueBinding("onmouseupeffect");

        return vb != null ? (Effect) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>onmouseupeffect</code> property.</p>
     */
    public void setOnmouseupeffect(Effect onmouseupeffect) {
        this.onmouseupeffect = onmouseupeffect;
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
    }

    /**
     * <p>Return the value of the <code>onmousemoveeffect</code> property.</p>
     */
    public Effect getOnmousemoveeffect() {
        if (onmousemoveeffect != null) {
            return onmousemoveeffect;
        }
        ValueBinding vb = getValueBinding("onmousemoveeffect");

        return vb != null ? (Effect) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>onmousemoveeffect</code> property.</p>
     */
    public void setOnmousemoveeffect(Effect onmousemoveeffect) {
        this.onmousemoveeffect = onmousemoveeffect;
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
    }

    /**
     * <p>Return the value of the <code>onmouseovereffect</code> property.</p>
     */
    public Effect getOnmouseovereffect() {
        if (onmouseovereffect != null) {
            return onmouseovereffect;
        }
        ValueBinding vb = getValueBinding("onmouseovereffect");

        return vb != null ? (Effect) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>onmouseovereffect</code> property.</p>
     */
    public void setOnmouseovereffect(Effect onmouseovereffect) {
        this.onmouseovereffect = onmouseovereffect;
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
    }

    /**
     * <p>Return the value of the <code>onmouseouteffect</code> property.</p>
     */
    public Effect getOnmouseouteffect() {
        if (onmouseouteffect != null) {
            return onmouseouteffect;
        }
        ValueBinding vb = getValueBinding("onmouseouteffect");

        return vb != null ? (Effect) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>onmouseouteffect</code> property.</p>
     */
    public void setOnmouseouteffect(Effect onmouseouteffect) {
        this.onmouseouteffect = onmouseouteffect;
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
    }


    /**
     * <p>Return the value of the <code>onkeypresseffect</code> property.</p>
     */
    public Effect getOnkeypresseffect() {
        if (onkeypresseffect != null) {
            return onkeypresseffect;
        }
        ValueBinding vb = getValueBinding("onkeypresseffect");

        return vb != null ? (Effect) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>onkeypresseffect</code> property.</p>
     */
    public void setOnkeypresseffect(Effect onkeypresseffect) {
        this.onkeypresseffect = onkeypresseffect;
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
    }









    /**
     * <p>Return the value of the <code>onkeydowneffect</code> property.</p>
     */
    public Effect getOnkeydowneffect() {
        if (onkeydowneffect != null) {
            return onkeydowneffect;
        }
        ValueBinding vb = getValueBinding("onkeydowneffect");

        return vb != null ? (Effect) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>onkeydowneffect</code> property.</p>
     */
    public void setOnkeydowneffect(Effect onkeydowneffect) {
        this.onkeydowneffect = onkeydowneffect;
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
    }

    /**
     * <p>Return the value of the <code>onkeyupeffect</code> property.</p>
     */
    public Effect getOnkeyupeffect() {
        if (onkeyupeffect != null) {
            return onkeyupeffect;
        }
        ValueBinding vb = getValueBinding("onkeyupeffect");

        return vb != null ? (Effect) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>onkeyupeffect</code> property.</p>
     */
    public void setOnkeyupeffect(Effect onkeyupeffect) {
        this.onkeyupeffect = onkeyupeffect;
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
    }

    /**
     * <p>Return the value of the <code>onkeyupeffect</code> property.</p>
     */
    public Effect getOnclickeffect() {
        if (onclickeffect != null) {
            return onclickeffect;
        }
        ValueBinding vb = getValueBinding("onclickeffect");

        return vb != null ? (Effect) vb.getValue(getFacesContext()) : null;
    }

    private String autocomplete;

    /**
     * <p>Set the value of the <code>autocomplete</code> property.</p>
     */
    public void setAutocomplete(String autocomplete) {
        this.autocomplete = autocomplete;
    }

    /**
     * <p>Return the value of the <code>autocomplete</code> property.</p>
     */
    public String getAutocomplete() {
        if (autocomplete != null) {
            return autocomplete;
        }
        ValueBinding vb = getValueBinding("autocomplete");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }


    /**
     * <p>Gets the state of the instance as a <code>Serializable</code>
     * Object.</p>
     */
    public Object saveState(FacesContext context) {
        Object values[] = new Object[20];
        values[0] = super.saveState(context);
        values[1] = partialSubmit;
        values[2] = enabledOnUserRole;
        values[3] = renderedOnUserRole;
        values[4] = styleClass;
        values[5] = effect;
        values[6] = onclickeffect;
        values[7] = ondblclickeffect;
        values[8] = onmousedowneffect;
        values[9] = onmouseupeffect;
        values[10] = onmousemoveeffect;
        values[11] = onmouseovereffect;
        values[12] = onmouseouteffect;
        values[13] = onkeypresseffect;
        values[14] = onkeydowneffect;
        values[15] = onkeyupeffect;
        values[16] = onchangeeffect;
        values[17] = autocomplete;
        values[18] = currentStyle;
        values[19] = visible;
        return ((Object) (values));
    }

    /**
     * <p>Perform any processing required to restore the state from the entries
     * in the state Object.</p>
     */
    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        partialSubmit = (Boolean) values[1];
        enabledOnUserRole = (String) values[2];
        renderedOnUserRole = (String) values[3];
        styleClass = (String) values[4];
        effect = (Effect) values[5];
        onclickeffect = (Effect) values[6];
        ondblclickeffect = (Effect) values[7];
        onmousedowneffect = (Effect) values[8];
        onmouseupeffect = (Effect) values[9];
        onmousemoveeffect = (Effect) values[10];
        onmouseovereffect = (Effect) values[11];
        onmouseouteffect = (Effect) values[12];
        onkeypresseffect = (Effect) values[13];
        onkeydowneffect = (Effect) values[14];
        onkeyupeffect = (Effect) values[15];
        onchangeeffect = (Effect) values[16];
        autocomplete = (String) values[17];
        currentStyle = (CurrentStyle) values[18];
        visible = (Boolean) values[19];
    }

}


