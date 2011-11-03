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

package com.icesoft.faces.facelets;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagAttributeException;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagException;
import com.sun.facelets.tag.TagHandler;
import com.sun.facelets.tag.jsf.ComponentSupport;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author Mark Collette
 * @since 1.1
 */
public class TabChangeListenerHandler extends TagHandler {
    private static Class panelTabSetClass;
    private static Class tabChangeListenerClass;
    private static Method addTabChangeListenerMethod;
    private static boolean triedGettingICEfacesComponentClasses;

    private static synchronized boolean isICEfacesComponentClassesPresent() {
        if( !triedGettingICEfacesComponentClasses ) {
            try {
                panelTabSetClass = Class.forName(
                        "com.icesoft.faces.component.paneltabset.PanelTabSet");
                tabChangeListenerClass = Class.forName(
                        "com.icesoft.faces.component.paneltabset.TabChangeListener");
                addTabChangeListenerMethod = panelTabSetClass.getMethod(
                        "addTabChangeListener", new Class[]{tabChangeListenerClass});
            }
            catch (Exception e) {
            }
            triedGettingICEfacesComponentClasses = true;
        }
        return (panelTabSetClass           != null &&
                tabChangeListenerClass     != null &&
                addTabChangeListenerMethod != null);
    }


    private final TagAttribute typeTagAttribute;

    /**
     * @param config
     */
    public TabChangeListenerHandler(TagConfig config) {
        super(config);

        typeTagAttribute = getRequiredAttribute("type");
    }

    /**
     * Threadsafe Method for controlling evaluation of its child tags,
     * represented by "nextHandler"
     */
    public void apply(FaceletContext ctx, UIComponent parent)
            throws IOException, FacesException, ELException {
        if( !isICEfacesComponentClassesPresent() ) {
            throw new TagException(
                    tag,
                    "ICEfaces components classes not found, can not use this tag");
        }
        if( parent == null ) {
            throw new TagException(tag, "Parent UIComponent was null");
        }
        if( !panelTabSetClass.isAssignableFrom(parent.getClass()) ) {
            throw new TagException(
                    tag,
                    "Parent UIComponent must be a "+
                    "com.icesoft.faces.component.paneltabset.PanelTabSet");
        }
        if( !typeTagAttribute.isLiteral() ) {
            throw new TagAttributeException(
                    tag, typeTagAttribute,
                    "The class, as given by tabChangeListener tag's type " +
                    "attribute, must be literal, and not a value expression: " +
                    typeTagAttribute.getValue());
        }
        String listenerClassName = typeTagAttribute.getValue(ctx);
        try {
            Class listenerClass = Class.forName( listenerClassName );
            if( !tabChangeListenerClass.isAssignableFrom(listenerClass) ) {
                throw new TagAttributeException(
                        tag, typeTagAttribute,
                        "The class, as given by tabChangeListener tag's type " +
                        "attribute, must implement TabChangeListener: " +
                        listenerClassName);
            }

            if( ComponentSupport.isNew(parent) ) {
                // TabChangeListener instance
                Object listenerObject = listenerClass.newInstance();
                addTabChangeListenerMethod.invoke(
                        parent, new Object[]{listenerObject} );
            }
        }
        catch(Exception e) {
            throw new TagAttributeException(
                    tag, typeTagAttribute,
                    "Could not either find, or instantiate, or add as a " +
                    "listener, the class described by tabChangeListener " +
                    "tag's type attribute: " + listenerClassName, e);
        }

        //TODO Use nextHandler? ice:tabChangeListener can't have children
    }
}
