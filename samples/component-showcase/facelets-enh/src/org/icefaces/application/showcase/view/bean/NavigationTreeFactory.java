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
package org.icefaces.application.showcase.view.bean;

import org.icefaces.application.showcase.util.FacesUtils;
import org.icefaces.application.showcase.view.builder.ApplicationBuilder;

import javax.swing.tree.DefaultTreeModel;
import java.util.HashMap;

/**
 * <p>The navigation builds the DefaultTreeModels used by the tree component.
 * The application ues serveral DefaultTreeModels in the main navigation
 * menu.  With a little HashMap trickery we load the trees as they are
 * needed (lazily).  There is very little effort in doing this and it will
 * save a little bit of memory. </p>
 * <p/>
 * <p>The application meta data defines the navigation trees with
 * node elements that have unique ids.  Any one of the node elements can be used
 * to build a valid DefaultTreeModel.  The tree will be composed of the root
 * node defined by the parentid and all child nodes will be added to the
 * DefaultTreeModel. </p>
 *
 * @since 1.7
 */
public class NavigationTreeFactory extends HashMap<String, DefaultTreeModel> {

    /**
     * <p>Gets child tree model associated with this user session given the
     * parentNodeId which must be a valid parent node id.  The nodes
     * are as defined in the application meta data. </p>
     * <p>Once the tree is build it is added to the HashMap and the cached
     * version is loaded on the next request.</p>
     *
     * @param parentNodeId nodeId of the meta data node in which we want to
     *                     build a tree on.
     * @return tree model for the given node id's child elements.  Can be passed
     *         directing into a tree componetns value attribute.
     */
    public DefaultTreeModel get(Object parentNodeId) {

        // check to see if the given tree model has been constructed.
        DefaultTreeModel childTreeModel = super.get(parentNodeId);

        // if not we build it and add it to our hash.
        if (childTreeModel == null) {

            // uses application builder to get us a tree for the given node id.
            ApplicationBuilder applicationBuilder =
                    (ApplicationBuilder) FacesUtils.getManagedBean(
                            BeanNames.APPLICATION_BUILDER);

            childTreeModel =
                    applicationBuilder.getChildNodeTree((String) parentNodeId);

            // cach the result for future reference
            this.put((String) parentNodeId, childTreeModel);
        }

        // finally return our node
        return childTreeModel;
    }
}
