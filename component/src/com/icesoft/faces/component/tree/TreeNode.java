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

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * TreeNode is a JSF component class that represents an ICEfaces tree node.
 * <p>The treeNode provides the template that be applied in rendering each node
 * in the backing data model. The treeNode supports two facets: the icon facet
 * and the content facet. The icon facet is intended to contain a graphic image
 * that will serve as the icon for the node it represents. This image can be
 * customized for each node, or default icons for leaf nodes, expand branch
 * nodes, and contracted branch nodes will be used. The content facet can
 * contain any collection of components. For each node in the tree's backing
 * data model, the child components of the two facets will be rendered with
 * state retrieved from the data model as configured in the JSP by the
 * application developer.
 * <p/>
 * This component extends the JSF UIComponentBase and implements the JSF
 * NamingContainer interface.
 * <p/>
 * By default this component is rendered by the "com.icesoft.faces.View"
 * renderer type.
 *
 * @author Chris Brown
 * @version beta 1.0
 */
public class TreeNode extends UIComponentBase implements NamingContainer {

    /**
     * Default no args constructor
     */
    public TreeNode() {
    }

    /**
     * @param node
     * @param trunk
     */
    public TreeNode(DefaultMutableTreeNode node, Tree trunk) {
        this.mutable = node;
        this.tree = trunk;
    }

    private transient DefaultMutableTreeNode mutable;
    private transient Tree tree;
    /**
     * The name for the content facet of the TreeNode
     */
    public static final String FACET_CONTENT = "content";
    /**
     * The name for the icon facet of the TreeNode
     */
    public static final String FACET_ICON = "icon";

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
        return "com.icesoft.faces.TreeNode";
    }

    /**
     * @return default mutable tree node
     */
    public DefaultMutableTreeNode getMutable() {
        return mutable;
    }

    /**
     * @param mutable
     */
    public void setMutable(DefaultMutableTreeNode mutable) {
        this.mutable = mutable;
    }

    /**
     * @return parent tree
     */
    public Tree getTree() {
        UIComponent component = getParent();
        while (component != null && !(component instanceof Tree)) {
            component = component.getParent();
        }
        return (Tree) component;
    }

    /**
     * @param tree
     */
    public void setTree(Tree tree) {
        this.tree = tree;
    }

    /**
     * @return content
     */
    public UIComponent getContent() {
        return (UIComponent) getFacet(FACET_CONTENT);
    }

    /**
     * @return icon
     */
    public UIComponent getIcon() {
        return (UIComponent) getFacet(FACET_ICON);
    }
}
