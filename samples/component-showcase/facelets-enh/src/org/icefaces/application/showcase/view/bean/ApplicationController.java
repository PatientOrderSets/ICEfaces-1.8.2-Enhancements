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

import com.icesoft.faces.component.paneltabset.TabChangeEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.icefaces.application.showcase.util.ContextUtilBean;
import org.icefaces.application.showcase.util.FacesUtils;
import org.icefaces.application.showcase.view.builder.ApplicationBuilder;
import org.icefaces.application.showcase.view.jaxb.*;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.io.Serializable;

/**
 * <p>Application controller is responsible for managing the presentation
 * layer action and actionListener functionality.  This bean is intended
 * to stay in Application scope and act on request and session beans as
 * needed. This class mainly handles site navigation processing via the tree
 * and common Tabset components.  Source and Document iFrame links are
 * also processed by this class.</p>
 * <p/>
 * <p>Each individual component showcase example has its own set of Beans which
 * are issolated from the main application and other examples.  Some examples
 * use a common mock service layer for retrieving Employee Objects.</p>
 *
 * @since 1.7
 */
public class ApplicationController  implements Serializable {

    private static final Log logger =
            LogFactory.getLog(ApplicationController.class);

    /**
     * <p>Tree navigation events are processed by this method when a tree
     * leaf or folder is clicked on by the user.  This method assumes
     * that the navigation node in question passed the request param
     * "nodeId" in the request map.  The "nodeId" is a unique id assigned to
     * the node from the application meta data.  This ide is then used
     * to find the corresponding Node object from the ApplicationBuilder class</p>
     * <p/>
     * <p>A node is marked as selected and the previously selected node is
     * marked as unselected.  The nodes corresponding include may have
     * a tabSet state which will either be loaded or initialized.  This is
     * important to note as it allows the use of only one tabSetComponent
     * for the intire application.</p>
     *
     * @param event jsf action event
     */
    public void navigationEvent(ActionEvent event) {

        // get id of node that was clicked on.
        String nodeId = FacesUtils.getRequestParameter("nodeId");

        // get a reference to the application scoped application builder.
        ApplicationBuilder applicationBuilder =
                (ApplicationBuilder) FacesUtils.getManagedBean(
                        BeanNames.APPLICATION_BUILDER);

        // do a bean lookup by node Id
        Node node = applicationBuilder.getNode(nodeId);

        // not all notes are marked as links and some nodes may be null
        // if the meta data was not correctly validated.
        if (node != null && node.isLink()) {

            // get a reference to the sessionModel data
            ApplicationSessionModel applicationModel =
                    (ApplicationSessionModel) FacesUtils.getManagedBean(
                            BeanNames.APPLICATION_MODEL);
            // reset selected state on old selected node.
            setTreeNodeSelectedState(
                    applicationModel.getCurrentNode().getId(), false);

            // set our new current node.
            applicationModel.setCurrentNode(node);

            // mark the new current node as selected
            setTreeNodeSelectedState(
                    applicationModel.getCurrentNode().getId(), true);

            // update the tabset state for this example
            HashMap<Node, TabState> tabStates = applicationModel.getTabContents();
            TabState tabState = tabStates.get(node);

            // otherwise, create a new state with the default tab index.
            if (tabState == null) {
                tabState = new TabState();
                // set default selected tab index. 
                tabState.setTabIndex(TabState.DEMONSTRATION_TAB_INDEX);
                // set default document and source paths, the first defined
                // document is what we will set as the default.
                tabState.setDescriptionContent(getDefaultDocumentPath(node));
                // set default source paths
                tabState.setSourceContent(getDefaultSourcePath(node));
            }
            // update  tabstate and put it into session scope.
            tabStates.put(node, tabState);
        }
        // otherwise we just toggle the expanded state of the node
        // that was selected, should always be a folder
        else if (node != null && !node.isLink()) {
            // toggle expanded state. 
            toggleTreeNodeExpandedState(node.getId());
        }
    }

    /**
     * <p>The method captures the selected tab state of the currently selected
     * node content.  We only use one Tabset for this application so this
     * method is responsible for making sure that each nodes content state
     * is persisted.</p>
     *
     * @param tabChangeEvent used to set the tab focus.
     * @throws javax.faces.event.AbortProcessingException
     *          An exception that may
     *          be thrown by event listeners to terminate the processing of the current event.
     */
    public void processTabChange(TabChangeEvent tabChangeEvent)
            throws AbortProcessingException {

        // get a reference to our sessionModel class
        ApplicationSessionModel applicationModel =
                (ApplicationSessionModel) FacesUtils.getManagedBean(
                        BeanNames.APPLICATION_MODEL);

        // get current node
        Node currentNode = applicationModel.getCurrentNode();

        // get the tabset state for this example from the current node
        HashMap<Node, TabState> tabStates = applicationModel.getTabContents();
        TabState tabState = tabStates.get(currentNode);

        // update model with selected tab index. 
        tabState.setTabIndex(tabChangeEvent.getNewTabIndex());
    }

    /**
     * <p>Each example has a source code tab which has links to JSPX
     * and Java Beans associated with the example.  The selection of source
     * code is handle by the method {@link #viewSourceEvent(javax.faces.event.ActionEvent)}
     * .  This method takes the selected source code and generates a
     * valid iFrame tag to which is used to load the source code.  The iFrame
     * url points to a Servlet which does the work of sending back the source
     * code in plain text. </p>
     *
     * @return iFrame tag and src which points to currently selected source
     *         code content.  If no source content is selected an iframe with an empty
     *         source link is returned.
     */
    public String getCurrentSource() {

        // get session model data
        ApplicationSessionModel applicationModel =
                (ApplicationSessionModel) FacesUtils.getManagedBean(
                        BeanNames.APPLICATION_MODEL);

        // get the currently selected node
        Node currentNode = applicationModel.getCurrentNode();

        // get the tabset state for this example
        HashMap<Node, TabState> tabStates = applicationModel.getTabContents();
        TabState tabState = tabStates.get(currentNode);

        // if there is associated source content then build and return
        // the iFrame
        if (tabState.getSourceContent() != null) {
            return ContextUtilBean.generateSourceCodeIFrame(
                    tabState.getSourceContent());
        }
        // otherwise just return an empty iFrame.
        return ContextUtilBean.generateIFrame("");
    }

    /**
     * <p>Each example has a documentation tab which has links to documentation
     * and tutorials associated with the example.  The selection of document
     * code is handle by the method {@link #viewIncludeEvent(javax.faces.event.ActionEvent)}
     * .  This method takes the selected document and generates a
     * valid iFrame tag to which is used to load the documentation.  The iFrame
     * url points to a Servlet which does the work of sending back the
     * documentation in plain text. </p>
     *
     * @return iFrame tag and src which points to currently selected
     *         documentation.  If no source content is selected an iframe with
     *         an empty source link is returned.
     */
    public String getCurrentDocumentSource() {

        // get the current model state for the session
        ApplicationSessionModel applicationModel =
                (ApplicationSessionModel) FacesUtils.getManagedBean(
                        BeanNames.APPLICATION_MODEL);

        // get the current node
        Node currentNode = applicationModel.getCurrentNode();

        // get the tabset state for this example
        HashMap<Node, TabState> tabStates = applicationModel.getTabContents();
        TabState tabState = tabStates.get(currentNode);

        // if there is a selected description content that return the
        // iFrame tag needed to load it.
        if (tabState.getDescriptionContent() != null) {
            return ContextUtilBean.generateIFrameWithContextPath(
                    tabState.getDescriptionContent());
        }
        return ContextUtilBean.generateIFrame("");
    }

    /**
     * <p>Loads the source code specified by the request parameters includePath.
     * The parameters includePath is used to generate the full file path
     * needed by the SourceCodeServlet. The path generated by this method
     * is stored in the session TabState object. </p>
     *
     * @param event jsf action event.
     */
    public void viewSourceEvent(ActionEvent event) {
        // get path of include
        String includePath = FacesUtils.getRequestParameter("includePath");

        // set the current node in the model
        ApplicationSessionModel applicationModel =
                (ApplicationSessionModel) FacesUtils.getManagedBean(
                        BeanNames.APPLICATION_MODEL);

        Node currentNode = applicationModel.getCurrentNode();

        // get the tabset state for this example
        HashMap<Node, TabState> tabStates = applicationModel.getTabContents();
        TabState tabState = tabStates.get(currentNode);

        // update tabset state
        tabState.setSourceContent(includePath);

        // put state back in session scope map.
        tabStates.put(currentNode, tabState);

    }

    /**
     * <p>Loads the documentation specified by the request parameters includePath.
     * The parameters includePath is used to generate the full file path
     * needed by the SourceCodeServlet. The path generated by this method
     * is stored in the session TabState object. </p>
     *
     * @param event jsf action event.
     */
    public void viewIncludeEvent(ActionEvent event) {
        // get id of node that was clicked on.
        String includePath = FacesUtils.getRequestParameter("includePath");

        // set the current node in the model
        ApplicationSessionModel applicationModel =
                (ApplicationSessionModel) FacesUtils.getManagedBean(
                        BeanNames.APPLICATION_MODEL);

        Node currentNode = applicationModel.getCurrentNode();

        // get the tabset state for this example
        HashMap<Node, TabState> tabStates = applicationModel.getTabContents();
        TabState tabState = tabStates.get(currentNode);

        // update tabset state
        tabState.setDescriptionContent(includePath);

        // put state back in session scope map.
        tabStates.put(currentNode, tabState);
    }

    /**
     * <p>Gets the ContextDescriptor object associated with the currently
     * selected node.  The ContextDescriptor class contains example,
     * documentation and source code information derived from the schema
     * data.</p>
     *
     * @return ContentDescriptor associated withthe selected node.
     */
    public ContentDescriptor getCurrentContextDescriptor() {
        // get reference to model data
        ApplicationSessionModel applicationModel =
                (ApplicationSessionModel) FacesUtils.getManagedBean(
                        BeanNames.APPLICATION_MODEL);
        // get reference to application builder
        ApplicationBuilder applicationBuilder =
                (ApplicationBuilder) FacesUtils.getManagedBean(
                        BeanNames.APPLICATION_BUILDER);

        Node currentNode = applicationModel.getCurrentNode();

        // return ContextDescriptor associated with this node.
        return applicationBuilder.getContextDescriptor(currentNode);
    }

    /**
     * Utility method to assign the selected state of a tree node.
     *
     * @param nodeId nodeId of the node to set the selected state on.
     * @param value  desired selection state of node.
     */
    private void setTreeNodeSelectedState(String nodeId, boolean value) {
        DefaultMutableTreeNode defaultNode = findTreeNode(nodeId);
        if (defaultNode != null) {
            NavigationTreeNode node =
                    (NavigationTreeNode) defaultNode.getUserObject();
            node.setSelected(value);
        }
    }

    /**
     * Utility method to toggle the selected state of the specified node.
     *
     * @param nodeId nodeId of the the node to toggle the expanded state of.
     */
    private void toggleTreeNodeExpandedState(String nodeId) {
        DefaultMutableTreeNode defaultNode = findTreeNode(nodeId);
        if (defaultNode != null) {
            NavigationTreeNode node = (NavigationTreeNode) defaultNode.getUserObject();
            node.setExpanded(!node.isExpanded());
        }
    }

    /**
     * Utility method to find a tree node by its ID.
     *
     * @param nodeId node Id of node to to find in tree.
     * @return node specified by ID or null of no node of that ID is found.
     */
    private DefaultMutableTreeNode findTreeNode(String nodeId) {
        ApplicationSessionModel applicationModel =
                (ApplicationSessionModel) FacesUtils.getManagedBean(
                        BeanNames.APPLICATION_MODEL);
        Collection<DefaultTreeModel> trees =
                applicationModel.getNavigationTrees().values();

        DefaultMutableTreeNode node;
        DefaultMutableTreeNode rootNode;
        // search all trees defined by meta data using a depthFirst search.
        for (DefaultTreeModel treeModel : trees) {
            rootNode = (DefaultMutableTreeNode) treeModel.getRoot();
            Enumeration nodes = rootNode.depthFirstEnumeration();
            while (nodes.hasMoreElements()) {
                node = (DefaultMutableTreeNode) nodes.nextElement();
                NavigationTreeNode tmp = (NavigationTreeNode) node.getUserObject();
                if (tmp.getNodeId() != null && tmp.getNodeId().equals(nodeId)) {
                    return node;
                }
            }
        }
        return null;
    }

    /**
     * Uitlity method to find the first available document reference that
     * can be used at the default content.
     *
     * @param node node to search child documentation resources for a the
     *             first available documentation link.
     * @return first available documentation link or an empty String if none
     *         are found.
     */
    private String getDefaultDocumentPath(Node node) {
        // return first instance of a document link
        if (node.getContentDescriptor().getDocumentation().getDocuments() != null){
            List<ReferenceType> references = node.getContentDescriptor()
                    .getDocumentation().getDocuments().getResourceReference();
            if (references != null && references.get(0) != null &&
                    references.get(0).getResourceRef() != null) {
                return references.get(0).getResourceRef().getPath();
            }
        }
        if (node.getContentDescriptor().getDocumentation().getTlds() != null){
            // check tld's encase there are documents for this component.
            List<ReferenceType> references = node.getContentDescriptor()
                    .getDocumentation().getTlds().getResourceReference();
            if (references != null && references.get(0) != null &&
                    references.get(0).getResourceRef() != null) {
                return references.get(0).getResourceRef().getPath();
            }
            // currently no check for default tutorial, as they are external links.
        }
        // if none found we just return an empty string and no content will
        // be loaded by default.
        return "";

    }

    /**
     * Uitlity method to find the first available source reference that
     * can be used at the default content.
     *
     * @param node node to search child documentation resources for a the
     *             first available source link.
     * @return first available source link or an empty String if none
     *         are found.
     */
    private String getDefaultSourcePath(Node node) {

        // return first instance of a bean link
        if (node.getContentDescriptor().getSourceCode().getBeans() != null){
            List<ReferenceType> references = node.getContentDescriptor()
                    .getSourceCode().getBeans().getResourceReference();
            if (references != null && references.get(0) != null &&
                    references.get(0).getResourceRef() != null) {
                return references.get(0).getResourceRef().getPath();
            }
        }
        if (node.getContentDescriptor().getSourceCode().getJspxPages() != null){
            // check JSPX code encase there are no beans for this component example
            List<ReferenceType> references = node.getContentDescriptor()
                    .getSourceCode().getJspxPages().getResourceReference();
            if (references != null && references.get(0) != null &&
                    references.get(0).getResourceRef() != null) {
                return references.get(0).getResourceRef().getPath();
            }
        }
        // if none found we just return an empty string and no content will
        // be loaded by default.
        return "";

    }
}
