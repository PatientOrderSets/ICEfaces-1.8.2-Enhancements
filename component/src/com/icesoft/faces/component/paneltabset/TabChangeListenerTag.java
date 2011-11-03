/*
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

package com.icesoft.faces.component.paneltabset;

import com.icesoft.faces.component.util.CustomComponentUtils;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.webapp.UIComponentTag;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Tag to add tab change listeners to a com.icesoft.faces.component.paneltabset.PanelTabSet
 */
public class TabChangeListenerTag extends TagSupport {
    /**
     * The serialVersionUID unique identifier.
     */
    private static final long serialVersionUID = -6903749011638483023L;
    /**
     * The current type.
     */
    private String type = null;

    /**
     * Creates an instance.
     */
    public TabChangeListenerTag() {

    }

    /**
     * Sets the type.
     *
     * @param type
     */
    public void setType(String type) {

        this.type = type;
    }

    /**
     * @return Tag.SKIP_BODY
     * @throws JspException
     */
    public int doStartTag() throws JspException {

        if (type == null) {
            throw new JspException("type attribute not set");
        }

        //Find parent UIComponentTag
        UIComponentTag componentTag =
                UIComponentTag.getParentUIComponentTag(pageContext);
        if (componentTag == null) {
            throw new JspException(
                    "TabChangeListenerTag has no UIComponentTag ancestor");
        }

        if (componentTag.getCreated()) {
            //Component was just created, so we add the Listener
            UIComponent component = componentTag.getComponentInstance();
            if (component instanceof PanelTabSet) {
                String className;
                if (UIComponentTag.isValueReference(type)) {
                    FacesContext facesContext =
                            FacesContext.getCurrentInstance();
                    ValueBinding valueBinding = facesContext.getApplication()
                            .createValueBinding(type);
                    className = (String) valueBinding.getValue(facesContext);
                } else {
                    className = type;
                }
                TabChangeListener listener =
                        (TabChangeListener) CustomComponentUtils
                                .newInstance(className);
                ((PanelTabSet) component).addTabChangeListener(listener);
            } else {
                throw new JspException("Component " + component.getId() +
                                       " is no PanelTabSet");
            }
        }

        return Tag.SKIP_BODY;
    }
}
