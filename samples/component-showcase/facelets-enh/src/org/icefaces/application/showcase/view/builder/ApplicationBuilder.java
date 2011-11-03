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
package org.icefaces.application.showcase.view.builder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.icefaces.application.showcase.util.FacesUtils;
import org.icefaces.application.showcase.view.bean.NavigationTreeNode;
import org.icefaces.application.showcase.view.jaxb.*;
import org.xml.sax.SAXException;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.faces.context.FacesContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

/**
 * <p>This class is responsible for loading the application structure from
 * meta data.  The library acts as a utility for building data models
 * for ui elements such as trees and for id lookups</p>
 * <p>The scope of this bean is Application but for test purposes can be set
 * to request.  Request scope is handy for building up the meta data file as
 * it allows for content addition without having to restart the application.</p>
 *
 * @since 1.7
 */
public class ApplicationBuilder implements Serializable {

    private static final Log logger =
            LogFactory.getLog(ApplicationBuilder.class);

    /**
     * Package Path to JAXB ObjectFactory home.
     */
    public static final String JAXB_FACTORY_PACKAGE =
            "org.icefaces.application.showcase.view.jaxb";

    /**
     * Directory path to schema and meta data resources.
     */
    public static final String META_DATA_RESOURCE_PATH =
            "/WEB-INF/classes/org/icefaces/application/showcase/view/jaxb/resources/";

    /**
     * File name of our application schema definition.
     */
    public static final String SCHEMA_FILE_NAME = "application.xsd";

    /**
     * File name of the application meta data.
     */
    public static final String META_DATA_FILE_NAME = "application_structure.xml";

    // Reference to the application object tree
    private Application application;

    /**
     * Default constructor, unmarshalles the application meta data.
     */
    public ApplicationBuilder() {
        loadMetaData();
    }

    public void loadMetaData() {

        try {
            // create a jab context
            JAXBContext jaxbContext =
                    JAXBContext.newInstance(JAXB_FACTORY_PACKAGE);

            // schema factory for data validation purposes
            SchemaFactory schemaFactory = SchemaFactory.newInstance(
                    XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(
                    new StreamSource(
                            getResourceStream(
                                    META_DATA_RESOURCE_PATH + "/" + SCHEMA_FILE_NAME)));

            // create an unmarshaller and set schema
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            unmarshaller.setSchema(schema);

            // grab and assign the application object graph
            application = (Application)
                    unmarshaller.unmarshal(new StreamSource(
                            getResourceStream(
                                    META_DATA_RESOURCE_PATH + "/" + META_DATA_FILE_NAME )));

        } catch (JAXBException e) {
            logger.error("JAXB Exception during unmarshalling:", e);
            throw new IllegalStateException(
                    "Could not load/unmarshal Application Meta Data: ", e);
        } catch (SAXException e) {
            logger.error("SAX Exception during unmarshalling:", e);
            throw new IllegalStateException(
                    "Could not load/unmarshal Application Meta Data: ", e);
        }

    }

    /**
     * Utility method to get the inputStream of a resource.  This method
     * tries to handle the API differences between the different server
     * implementations.
     *
     * @param path path to get inputStream of
     * @return input stream for the given path
     */
    private InputStream getResourceStream(String path) {

        InputStream sourceStream =
                FacesUtils.getServletContext().getResourceAsStream(path);

        if (sourceStream == null) {
            try {
                // Work around for websphere
                sourceStream = new FileInputStream(new File(
                        FacesUtils.getServletContext().getRealPath(path)));
            } catch (Exception e) {
                logger.error("Error getting resource: " + path, e);
            }
        }

        return sourceStream;
    }

    public Node getDefaultNode() {
        return application.getNavigation()
                .getNavigationDefault().getNavigationNode();
    }

    /**
     * Gets/searches for a node that has the specified id.
     *
     * @param nodeId unique id for a given node
     * @return found Node, otherwise null
     */
    public Node getNode(String nodeId) {
        return nodeSearch(application.getNavigation().getNode(), nodeId);
    }

    /**
     * Recursive, depth first search of node tree.
     *
     * @param nodes  list of  nodes to search
     * @param nodeId node to look for
     * @return found node, otherwise null.
     */
    private Node nodeSearch(List<Node> nodes, String nodeId) {
        for (Node node : nodes) {
            if (node.getId().equals(nodeId)) {
                return node;
            } else if (node.getNode() != null && nodes.size() > 0) {
                Node foundNode = nodeSearch(node.getNode(), nodeId);
                if (foundNode != null) {
                    return foundNode;
                }
            }
        }
        return null;
    }

    /**
     * Gets/searches for a node that has the specified id and returns a fully
     * contstructed DefaultTreeModel for the found nodes children.
     *
     * @param nodeId unique id for a given node
     * @return found Node, otherwise null
     */
    public DefaultTreeModel getChildNodeTree(String nodeId) {
        Node parentNode =
                nodeSearch(application.getNavigation().getNode(), nodeId);
        // if we have a node build up our tree. 
        if (parentNode != null) {
            // create root node, which will be hidden by component attribute
            // settings.
            DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
            NavigationTreeNode userObject = new NavigationTreeNode(rootNode);
            userObject.setText("rootNode");
            userObject.setExpanded(true);
            rootNode.setUserObject(userObject);

            // build a tree based on child nodes of parentNode.
            buildChildNodes(rootNode, parentNode.getNode());
            return new DefaultTreeModel(rootNode);
        }
        return null;
    }

    private void buildChildNodes(
            DefaultMutableTreeNode parentNode, List<Node> childNodes) {

        DefaultMutableTreeNode treeNode;
        NavigationTreeNode navNode;

        for (Node node : childNodes) {
            treeNode = new DefaultMutableTreeNode();
            navNode = new NavigationTreeNode(treeNode);
            navNode.setText(node.getLabel());
            navNode.setNodeId(node.getId());
            navNode.setExpanded(node.isExpanded());
            treeNode.setUserObject(navNode);
            navNode.setLeaf(node.isLeaf());
            parentNode.add(treeNode);

            if (node.getNode() != null && node.getNode().size() > 0) {
                buildChildNodes(treeNode, node.getNode());
            }
        }
    }

    public ContentDescriptor getContextDescriptor(Node node) {
        return node.getContentDescriptor();
    }

    public Example getContextDescriptorExample(Node node) {
        return node.getContentDescriptor().getExample();
    }

    public Documentation getContextDescriptorDocumentation(Node node) {
        return node.getContentDescriptor().getDocumentation();
    }

    public SourceCode getContextDescriptorSourceCode(Node node) {
        return node.getContentDescriptor().getSourceCode();
    }

    /**
     * <p>Gets the application object graph for the application meta data. The
     * graph can be traversed to view the application structure</p>
     *
     * @return application structure graph.
     */
    public Application getApplicationStructure() {
        return application;
    }
}
