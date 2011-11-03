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

package com.icesoft.faces.component.menubar;

import javax.faces.el.ValueBinding;
import javax.faces.el.MethodBinding;
import javax.faces.event.ActionListener;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.util.List;

/**
 * MenuItems is the JSF component class that represents a heirarchy of ICEfaces
 * MenuItems. <p>This is the submenu component to use if you want to supply a
 * (potentially) dynamic heirarchy of menuItems.
 * <p/>
 * MenuItems extends the ICEfaces MenuItemBase component.
 * <p/>
 * By default this component is rendered by the "com.icesoft.faces.View"
 * renderer type.
 *
 * @author Chris Brown
 * @author gmccleary
 * @version 1.1
 */
public class MenuItems extends MenuItemBase {

    private String value;

    /* (non-Javadoc)
    * @see javax.faces.component.UIComponent#getRendererType()
    */
    public String getRendererType() {
        return "com.icesoft.faces.View";
    }

    /* (non-Javadoc)
    * @see javax.faces.component.UIComponent#getFamily()
    */
    public String getFamily() {
        return "com.icesoft.faces.MenuNodes";
    }

    /* (non-Javadoc)
     * @see javax.faces.component.UICommand#getValue()
     */
    public Object getValue() {

        if (value != null) {
            return value;
        }
        ValueBinding vb = getValueBinding("value");
        if (vb != null) {

            return (List) vb.getValue(getFacesContext());
        }
        return null;
    }

    public void setValue(Object value){
        this.value = (String)value;
    }

    /**
     * @param value
     */
    public void setValue(String value) {

        this.value = value;
    }
    
    public List prepareChildren() {
        List children = (List) getValue();
        if(children != null && children.size() > 0) {
            // extract the actionListener and action methodBindings from the MenuItems
            // then attach them to the child MenuItem objects
            ActionListener[] als = getActionListeners();
            MethodBinding almb = getActionListener();
            MethodBinding amb = getAction();
            setParentsRecursive(this, children, als, almb, amb);
        }
        return children;
    }
    
    private void setParentsRecursive(UIComponent parent, List children,
                                     ActionListener[] als,
                                     MethodBinding almb, MethodBinding amb) {
        for (int i = 0; i < children.size(); i++) {
            UIComponent nextChild = (UIComponent) children.get(i);
            if( !(nextChild instanceof MenuItemBase) ) {
                continue;
            }
            nextChild.setParent(parent);

            // here's where we attach the action and actionlistener methodBindings to the MenuItem
            MenuItemBase nextChildMenuItemBase = (MenuItemBase) nextChild;
            if (null != als) {
                for(int j = 0; j < als.length; j++) {
                    nextChildMenuItemBase.removeActionListener(als[j]);
                    nextChildMenuItemBase.addActionListener(als[j]);
                }
            }
            if (null != almb) {
                nextChildMenuItemBase.setActionListener(almb);
            }
            if (null != amb) {
                nextChildMenuItemBase.setAction(amb);
            }
            
            if (nextChild.getChildCount() > 0) {
                List grandChildren = nextChild.getChildren();
                setParentsRecursive(nextChild, grandChildren, als, almb, amb);
            }
        }
    }
    
    public void processDecodes(FacesContext context) {
        if (context == null) {
            throw new NullPointerException("context");
        }
        if (!isRendered()) {
            return;
        }
        
        List list = prepareChildren();
        if(list != null) {
            for (int j = 0; j < list.size(); j++) {
                MenuItem item = (MenuItem) list.get(j);
                item.processDecodes(context);
            }
        }
        
        super.processDecodes(context);
    }

    public Object saveState(FacesContext context) {
        Object values[] = new Object[2];
        values[0] = super.saveState(context);
        values[1] = value;
        return values;
    }

    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        value = (String) values[1];
    }
}
