package org.icefaces.application.showcase.view.bean;

import org.icefaces.application.showcase.view.builder.ApplicationBuilder;
import org.icefaces.application.showcase.util.FacesUtils;

import java.util.HashMap;

/**
 * <p>This bean builds and stores the state of the collapsible panel used by main
 * navigation menu for grouping component types.  With a little HashMap
 * trickery we load the collapsible panel expanded state lazily.  This helps
 * keep the memory requirements of the application done.</p>
 * <p/>
 * <p>The application meta data defines the navigation trees with
 * node elements that have unique ids.  The navigation.jspx file renders all
 * first level Node elements as panelCollapsible components.  The default
 * expanded/contracted state is stored in the meta data and any session specific
 * changes are stored in this class.  </p>
 *
 * @since 1.7
 */
public class NavigationPanelCollapsibleFactory extends HashMap<String, Boolean> {

    /**
     * <p>Gets panelCollapsible expanded state associated with this user session
     * given the parentNodeId which must be a valid parent node id.  The nodes
     * are as defined in the application meta data. </p>
     * <p>Once the tree is build it is added to the HashMap and the cached
     * version is loaded on the next request.</p>
     *
     * @param nodeId nodeId of the meta data node in which we want to
     *                     build panelCollapsible panel with.
     * @return tree true if the panelCollapsible is expanded, otherewise; false.
     */
    public Boolean get(Object nodeId) {

        // check to see if the given tree model has been constructed.
        Boolean panelColapsibleState = super.get(nodeId);

        // if not we build it and add it to our hash.
        if (panelColapsibleState == null) {

            // uses application builder to get us a Node for the given node id.
            ApplicationBuilder applicationBuilder =
                    (ApplicationBuilder) FacesUtils.getManagedBean(
                            BeanNames.APPLICATION_BUILDER);

            panelColapsibleState =
                    applicationBuilder.getNode((String) nodeId).isExpanded();

            // cach the result for future reference
            this.put((String) nodeId, panelColapsibleState);
        }

        // finally return our node
        return panelColapsibleState;
    }
}
