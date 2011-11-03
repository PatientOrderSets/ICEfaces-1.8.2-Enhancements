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

import org.icefaces.application.showcase.view.jaxb.Node;
import org.icefaces.application.showcase.util.FacesUtils;

import java.util.HashMap;
import java.io.Serializable;

/**
 * <p>The purpose of the bean is to store user session data for the navigation
 * and content presentation layer of the application.  The intention is
 * to keep this class as lean as possible and thus only persist between
 * requests the absolute minimum amount of data. </p>
 * <p/>
 * <p>To help minimize the amount of data we keep in session scop lazy
 * initilization is used for the navigationTreeFactory and tabContents
 * data models.</p>
 *
 * @since 1.7
 */
public class ApplicationSessionModel implements Serializable {

    // current node, and thus content the user is viewing
    private Node currentNode;

    // navigation tree model states
    private NavigationTreeFactory navigationTreeFactory;

    // navigation panel collapsible expanded state
    private NavigationPanelCollapsibleFactory navigationPanelCollapsible;

    // map of tab content visited by users, lazy loaded
    private HashMap<Node, TabState> tabContents;

    /**
     * Creates a new instance of this class, instantiating the
     * navigationTreeFactory and tabContents data models.
     */
    public ApplicationSessionModel() {
        navigationTreeFactory = new NavigationTreeFactory();
        navigationPanelCollapsible = new NavigationPanelCollapsibleFactory();
        tabContents = new HashMap<Node, TabState>();
    }

    /**
     * Gets the selected tabSet index for the currently selected node.
     *
     * @return selected tabset for the currently selected node.
     */
    public int getSelectedTabIndex(){
        // get the tabset state for this example
        TabState tabState = tabContents.get(currentNode);
        return tabState.getTabIndex();
    }

    /**
     * Sets the selected tab index for the currently selected node tabState
     * model.
     *
     * @param tabIndex tabIndex currently  selected in a tabSet component.
     */
    public void setSelectedTabIndex(int tabIndex){
        TabState tabState = tabContents.get(currentNode);
        tabState.setTabIndex(tabIndex);
    }

    /**
     * Gets the currently select node.
     * @return selected node
     */
    public Node getCurrentNode() {
        return currentNode;
    }

    /**
     * Sets the currently selected node.
     * @param currentNode node to be set as the currently selected node.
     */
    public void setCurrentNode(Node currentNode) {
        this.currentNode = currentNode;
    }

    /**
     * Gets the navigation tree factory
     * @return  navigation tree factory
     */
    public NavigationTreeFactory getNavigationTrees() {
        return navigationTreeFactory;
    }

    /**
     * Gets the navigation panelCollapsible factory
     * @return navigation panelCollapsible factory
     */
    public NavigationPanelCollapsibleFactory getNavigationPanelCollapsible() {
        return navigationPanelCollapsible;
    }

    /**
     * Gets the tabContents data model.
     * @return tab contents data model.
     */
    public HashMap<Node, TabState> getTabContents() {
        return tabContents;
    }

    /**
     * Gets the TabState model for the currently selected
     * navigation node. .
     * @return TabState model for the selected Node. 
     */
    public TabState getSelectedTabState(){
        return tabContents.get(currentNode);
    }

}
