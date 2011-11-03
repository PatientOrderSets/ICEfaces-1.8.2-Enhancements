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

package com.icesoft.faces.component.tree;

import com.icesoft.faces.component.util.CustomComponentUtils;
import com.icesoft.faces.context.DOMContext;
import com.icesoft.faces.renderkit.dom_html_basic.DomBasicRenderer;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.swing.tree.DefaultMutableTreeNode;
import java.io.IOException;
import java.util.Iterator;


/**
 * TreeNodeRenderer is an ICEfaces D2D renderer for the TreeNode component.
 */
public class TreeNodeRenderer extends DomBasicRenderer {


    /* (non-Javadoc)
     * @see com.icesoft.faces.renderkit.dom_html_basic.DomBasicRenderer#decode(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
     */
    public void decode(FacesContext facesContext, UIComponent uiComponent) {
    }

    /* (non-Javadoc)
     * @see com.icesoft.faces.renderkit.dom_html_basic.DomBasicRenderer#encodeEnd(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
     */
    public void encodeEnd(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {

        DOMContext domContext =
                DOMContext.attachDOMContext(facesContext, uiComponent);

        validateParameters(facesContext, uiComponent, TreeNode.class);
        resetDescendentIds(uiComponent);
        UIComponent iconFacet = ((TreeNode) uiComponent).getIcon();
        UIComponent contentFacet = ((TreeNode) uiComponent).getContent();

        if (iconFacet != null && iconFacet.isRendered()) {
            CustomComponentUtils.renderChild(facesContext, iconFacet);
        }
        domContext.getCursorParent().appendChild(domContext.createTextNode(" "));
        if (contentFacet != null && contentFacet.isRendered()) {
            CustomComponentUtils.renderChild(facesContext, contentFacet);
        }

    }

    /**
     * Reset ids so the client id will be nullified and regenerated when
     * attachDOMContext is called. This is necessary so that each child
     * component of the TreeNode has a unique client id instead of using the
     * cached id. If the cached id is used, the DOMContext from the previous
     * node is reused which is totally incorrect. the same DOMContext and
     *
     * @param uiComponent
     */
    public static void resetDescendentIds(UIComponent uiComponent) {
        uiComponent.setId(uiComponent.getId());
        Iterator it = uiComponent.getFacetsAndChildren();
        while (it.hasNext()) {
            UIComponent next = (UIComponent) it.next();
            resetDescendentIds(next);
        }
    }
}
