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
/* Original Copyright
 * Copyright 2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.icesoft.faces.component.ext;

import com.icesoft.faces.component.CSS_DEFAULT;
import com.icesoft.faces.component.IceExtended;
import com.icesoft.faces.component.PORTLET_CSS_DEFAULT;
import com.icesoft.faces.component.ext.taglib.Util;
import com.icesoft.faces.context.BridgeFacesContext;
import com.icesoft.faces.context.effects.CurrentStyle;
import com.icesoft.faces.context.effects.Effect;
import com.icesoft.faces.context.effects.JavascriptContext;
import com.icesoft.faces.util.CoreUtils;
import com.icesoft.faces.renderkit.dom_html_basic.DomBasicRenderer;

import javax.faces.component.ActionSource;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.event.FacesEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;

/**
 * This is an extension of javax.faces.component.html.HtmlInputText, which
 * provides some additional behavior to this component such as: <ul> <li>default
 * classes for enabled and disabled state</li> <li>provides full and partial
 * submit mechanism</li> <li>changes the component's enabled and rendered state
 * based on the authentication</li> <li>adds effects to the component</li>
 * <li>allows to set the action and actionListener for this component</li> <ul>
 */

public class HtmlInputText
        extends javax.faces.component.html.HtmlInputText
        implements IceExtended, ActionSource {

    public static final String COMPONENT_TYPE =
            "com.icesoft.faces.HtmlInputText";
    public static final String RENDERER_TYPE = "com.icesoft.faces.Text";
    private static final boolean DEFAULT_ACTION_KEY_EVENT = false;
    private static final boolean DEFAULT_VISIBLE = true;
    private String styleClass = null;
    protected Boolean partialSubmit = null;
    private String enabledOnUserRole = null;
    private String renderedOnUserRole = null;
    private MethodBinding action = null;
    private MethodBinding actionListener = null;
    private Boolean actionKeyEvent;
    private boolean immediate = false;
    private boolean immediateSet = false;
    private Effect effect;
    private Boolean visible = null;
    protected boolean focus = false;
    private Effect onclickeffect;
    private Effect ondblclickeffect;
    private Effect onmousedowneffect;
    private Effect onmouseupeffect;
    private Effect onmousemoveeffect;
    private Effect onmouseovereffect;
    private Effect onmouseouteffect;
    private Effect onchangeeffect;
    private Effect onkeypresseffect;
    private Effect onkeydowneffect;
    private Effect onkeyupeffect;

    private CurrentStyle currentStyle;

    public HtmlInputText() {
        super();
        setRendererType(RENDERER_TYPE);
    }

    /**
     * @deprecated
     * @return The converted text that the Renderer would output
     */
    public String getText() {
        return DomBasicRenderer.converterGetAsString(
            FacesContext.getCurrentInstance(), this, getValue());
    }

    /**
     * Mimics the behaviour of a user entering some text and submitting it.
     * The difference being that the processing is not kicked off from the
     *  decode phase after a submit, but can be invoked from an actionListener
     *  in a bean, in time for rendering. Just be aware that nothing is
     *  being done in the proper phase.
     * 
     * @deprecated
     * @param value Text String, like that which a user could have typed in
     */
    public void changeText(String value) {
        setSubmittedValue(value);
        FacesContext facesContext = FacesContext.getCurrentInstance();
        validate(facesContext);
        updateModel(facesContext);
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
     * <p>Set the value of the <code>partialSubmit</code> property.</p>
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
                CSS_DEFAULT.INPUT_TEXT_DEFAULT_STYLE_CLASS,
                "styleClass",
                isDisabled(),
                PORTLET_CSS_DEFAULT.PORTLET_FORM_INPUT_FIELD);
    }

    protected String getStyleClassField() {
        return styleClass;
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
     * <p>Set the value of the <code>action</code> property.</p>
     */
    public void setAction(MethodBinding action) {
        this.action = action;
    }

    /**
     * <p>Return the value of the <code>action</code> property.</p>
     */
    public MethodBinding getAction() {
        return action;
    }

    /* (non-Javadoc)
     * @see javax.faces.component.ActionSource#setActionListener(javax.faces.el.MethodBinding)
     */
    public void setActionListener(MethodBinding actionListener) {
        this.actionListener = actionListener;
    }

    /* (non-Javadoc)
     * @see javax.faces.component.ActionSource#getActionListener()
     */
    public MethodBinding getActionListener() {
        return actionListener;
    }

    /* (non-Javadoc)
     * @see javax.faces.component.ActionSource#addActionListener(javax.faces.event.ActionListener)
     */
    public void addActionListener(ActionListener listener) {
        addFacesListener(listener);
    }

    /* (non-Javadoc)
     * @see javax.faces.component.ActionSource#getActionListeners()
     */
    public ActionListener[] getActionListeners() {
        return (ActionListener[]) getFacesListeners(ActionListener.class);
    }

    /* (non-Javadoc)
     * @see javax.faces.component.ActionSource#removeActionListener(javax.faces.event.ActionListener)
     */
    public void removeActionListener(ActionListener listener) {
        removeFacesListener(listener);
    }

    /**
     * <p>Set the value of the <code>actionKeyEvent</code> property.</p>
     */
    public void setActionKeyEvent(boolean actionKeyEvent) {
        this.actionKeyEvent = Boolean.valueOf(actionKeyEvent);
    }

    /**
     * <p>Return the value of the <code>actionKeyEvent</code> property.</p>
     */
    public boolean isActionKeyEvent() {
        if (actionKeyEvent != null) {
            return actionKeyEvent.booleanValue();
        }
        ValueBinding vb = getValueBinding("actionKeyEvent");
        Boolean boolVal =
                vb != null ? (Boolean) vb.getValue(getFacesContext()) : null;
        return boolVal != null ? boolVal.booleanValue() :
               DEFAULT_ACTION_KEY_EVENT;
    }

    /**
     * <p>Return the value of the <code>onclickeffect</code> property.</p>
     */
    public Effect getOnclickeffect() {
        if (onclickeffect != null) {
            return onclickeffect;
        }
        ValueBinding vb = getValueBinding("onclickeffect");

        return vb != null ? (Effect) vb.getValue(getFacesContext()) : null;
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
     * <p>Gets the state of the instance as a <code>Serializable</code>
     * Object.</p>
     */
    public Object saveState(FacesContext context) {
        Object values[] = new Object[29];
        values[0] = super.saveState(context);
        values[1] = partialSubmit;
        values[2] = enabledOnUserRole;
        values[3] = renderedOnUserRole;
        values[4] = styleClass;
        values[5] = saveAttachedState(context, action);
        values[6] = saveAttachedState(context, actionListener);
        values[7] = actionKeyEvent;
        values[8] = immediate ? Boolean.TRUE : Boolean.FALSE;
        values[9] = immediateSet ? Boolean.TRUE : Boolean.FALSE;
        values[10] = effect;
        values[11] = onclickeffect;
        values[12] = ondblclickeffect;
        values[13] = onmousedowneffect;
        values[14] = onmouseupeffect;
        values[15] = onmousemoveeffect;
        values[16] = onmouseovereffect;
        values[17] = onmouseouteffect;
        values[18] = onchangeeffect;
        values[21] = onkeypresseffect;
        values[22] = onkeydowneffect;
        values[23] = onkeyupeffect;
        values[24] = currentStyle;
        values[25] = visible;
        values[26] = autocomplete;
        values[27] = Boolean.valueOf(focus);
        values[28] = getSubmittedValue();
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
        action = (MethodBinding) restoreAttachedState(context, values[5]);
        actionListener =
                (MethodBinding) restoreAttachedState(context, values[6]);
        actionKeyEvent = (Boolean) values[7];
        immediate = ((Boolean) values[8]).booleanValue();
        immediateSet = ((Boolean) values[9]).booleanValue();
        effect = (Effect) values[10];
        onclickeffect = (Effect) values[11];
        ondblclickeffect = (Effect) values[12];
        onmousedowneffect = (Effect) values[13];
        onmouseupeffect = (Effect) values[14];
        onmousemoveeffect = (Effect) values[15];
        onmouseovereffect = (Effect) values[16];
        onmouseouteffect = (Effect) values[17];
        onchangeeffect = (Effect) values[18];
        onkeypresseffect = (Effect) values[21];
        onkeydowneffect = (Effect) values[22];
        onkeyupeffect = (Effect) values[23];
        currentStyle = (CurrentStyle) values[24];
        visible = (Boolean) values[25];
        autocomplete = (String) values[26];
        focus = ((Boolean) values[27]).booleanValue();
        setSubmittedValue(values[28]);        
    }


    /* (non-Javadoc)
     * @see javax.faces.component.UIComponent#broadcast(javax.faces.event.FacesEvent)
     */
    public void broadcast(FacesEvent event) throws AbortProcessingException {
        try {
            super.broadcast(event);
        } catch (IllegalArgumentException e) {
            //MyFaces thinks that a UIInput should bail out here, but
            //it interferes with subclass event processing
        }
        if ((event instanceof ActionEvent)) {
            ActionEvent actionEvent = (ActionEvent) event;
            try {
                MethodBinding actionListenerBinding = getActionListener();
                if (actionListenerBinding != null) {
                    actionListenerBinding.invoke(
                        getFacesContext(), new Object[]{actionEvent});
                }
                // super.broadcast(event) does this itself
                //ActionListener[] actionListeners = getActionListeners();
                //if(actionListeners != null) {
                //    for(int i = 0; i < actionListeners.length; i++) {
                //        actionListeners[i].processAction(actionEvent);
                //    }
                //}
            } catch (EvaluationException e) {
                Throwable cause = e.getCause();
                if (cause != null &&
                    cause instanceof AbortProcessingException) {
                    throw(AbortProcessingException) cause;
                } else {
                    throw e;
                }
            }//try

            // Invoke the default ActionListener
            ActionListener listener =
                    getFacesContext().getApplication().getActionListener();
            if (listener != null) {
                listener.processAction((ActionEvent) event);
            }
        }
    }


    /* (non-Javadoc)
     * @see javax.faces.component.UIComponent#queueEvent(javax.faces.event.FacesEvent)
     */
    public void queueEvent(FacesEvent event) {
        if (event instanceof ActionEvent) {
            if (isImmediate()) {
                event.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);
            } else {
                event.setPhaseId(PhaseId.INVOKE_APPLICATION);
            }
        }
        super.queueEvent(event);
    }


    /**
     * <p>Return the value of the <code>immediate</code> property.</p>
     */
    public boolean isImmediate() {
        if (this.immediateSet) {
            return (this.immediate);
        }
        ValueBinding vb = getValueBinding("immediate");
        if (vb != null) {
            return (Boolean.TRUE.equals(vb.getValue(getFacesContext())));
        } else {
            return (this.immediate);
        }
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
     * <p>Set the value of the <code>immediate</code> property.</p>
     */
    public void setImmediate(boolean immediate) {
        if (immediate != this.immediate) {
            this.immediate = immediate;
        }
        this.immediateSet = true;

    }

    /* (non-Javadoc)
     * @see javax.faces.component.UIComponent#decode(javax.faces.context.FacesContext)
     */
    public void decode(FacesContext context) {
        super.decode(context);
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

    /**
     * <p>Set the value of the <code>focus</code> property.</p>
     */
    public void setFocus(boolean focus) {
        this.focus = focus;
    }

    /**
     * <p>Return the value of the <code>focus</code> property.</p>
     */
    public boolean hasFocus() {
        return focus;
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
}
