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

import com.icesoft.faces.component.InvalidComponentTypeException;
import com.icesoft.faces.component.ExtendedAttributeConstants;
import com.icesoft.faces.component.util.CustomComponentUtils;
import com.icesoft.faces.context.DOMContext;
import com.icesoft.faces.renderkit.dom_html_basic.DomBasicRenderer;
import com.icesoft.faces.renderkit.dom_html_basic.HTML;
import com.icesoft.faces.renderkit.dom_html_basic.PassThruAttributeRenderer;
import com.icesoft.faces.util.CoreUtils;

import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import java.io.IOException;
import java.util.Map;

import com.icesoft.util.pooling.ClientIdPool;

/**
 * TreeRenderer is an ICEfaces D2D renderer for the Tree component.
 *
 * @author gmccleary
 * @version 1.1
 */
public class TreeRenderer extends DomBasicRenderer {
    private static final String[] passThruAttributes =
            ExtendedAttributeConstants.getAttributes(ExtendedAttributeConstants.ICE_TREE);

    /**
     *
     */
    public static final String PATH_DELIMITER = "-";

    /**
     * @return true as this component will render its children.
     */
    public boolean getRendersChildren() {
        return true;
    }

    /**
     * @param facesContext
     * @param uiComponent
     */
    public void decode(FacesContext facesContext, UIComponent uiComponent) {
        if (isStatic(uiComponent)) {
            return;
        }

        Tree treeComponent = (Tree) uiComponent;
        String expandNodeKey = CustomComponentUtils
                .getHiddenTreeExpandFieldName(
                        uiComponent.getClientId(facesContext),
                        CustomComponentUtils.getFormName(uiComponent,
                                                         facesContext));
        String pathToExpandedNode = (String) facesContext.getExternalContext()
                .getRequestParameterMap().get(expandNodeKey);
        // handle navigation: if the user clicked on an open/close icon
        // then expand/contract the appropriate node by updating the state
        // of the user object encapsulated by the tree node that was expanded/collapsed
        if (pathToExpandedNode != null &&
            !pathToExpandedNode.equalsIgnoreCase("")) {
            try {
                treeComponent.setPathToExpandedNode(pathToExpandedNode);
                // get the node that was expanded/contracted
                DefaultMutableTreeNode navigatedNode =
                        treeComponent.getNavigatedNode();
                IceUserObject userObject =
                        (IceUserObject) navigatedNode.getUserObject();
                if (userObject != null) {
                    treeComponent.setNavigationEventType(
                            userObject.isExpanded() ?
                            Tree.NAVIGATION_EVENT_COLLAPSE :
                            Tree.NAVIGATION_EVENT_EXPAND);
                    userObject.setExpanded(!userObject.isExpanded());
                }

                ActionEvent actionEvent = new ActionEvent(uiComponent);
                uiComponent.queueEvent(actionEvent);

            } catch (NumberFormatException nfe) {
                throw new InvalidNavigationId(
                        "TreeRenderer.decode() NumberFormatException invalid tree navigation id",
                        nfe);
            }
        }
    }

    /**
     * @param facesContext
     * @param uiComponent
     * @throws IOException
     */
    public void encodeBegin(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {

        // get domContext first, to check if we are stream writing
        DOMContext domContext =
                DOMContext.attachDOMContext(facesContext, uiComponent);

        // setup
        validateParameters(facesContext, uiComponent, Tree.class);

        Tree treeComponent = (Tree) uiComponent;
        TreeModel treeModel = (TreeModel) uiComponent
                .getValueBinding("value").getValue(facesContext);

        if (treeComponent.getChildCount() != 1) {
            throw new MalformedTreeTagException(
                    "The tree tag requires a single child treeNode tag. Found [" +
                    treeComponent.getChildCount() + "]");
        }

        if (treeModel == null) {
            return;
        }

        // set up form fields

        // encode the root DIV
        if (!domContext.isInitialized()) {
            Element rootDOMNode = domContext.createRootElement(HTML.DIV_ELEM);
            rootDOMNode.setAttribute(HTML.ID_ATTR,
                                     uiComponent.getClientId(facesContext));
        }
        // get the root node
        Element rootDomNode = (Element) domContext.getRootNode();
        // Apply  default styleClass and style to the root node.
        rootDomNode
                .setAttribute(HTML.CLASS_ATTR, treeComponent.getStyleClass());
        String style = treeComponent.getStyle();
        if(style != null && style.length() > 0)
            rootDomNode.setAttribute(HTML.STYLE_ATTR, style);
        else
            rootDomNode.removeAttribute(HTML.STYLE_ATTR);

        // clean up, and remove nodes
        DOMContext.removeChildren(rootDomNode);
        PassThruAttributeRenderer.renderHtmlAttributes(facesContext, uiComponent, passThruAttributes);
        if (treeComponent.isKeyboardNavigationEnabled()) {
            String keyboardSupport = "return Ice.treeNavigator.handleFocus(event, this);";
            String userKeyDown = (String) uiComponent.getAttributes().get(HTML.ONKEYDOWN_ATTR);
            String keydown = DomBasicRenderer.combinedPassThru(userKeyDown, keyboardSupport);
            rootDomNode.setAttribute(HTML.ONKEYDOWN_ATTR, keydown);
            String userClick = (String) uiComponent.getAttributes().get(HTML.ONCLICK_ATTR);
            String click = DomBasicRenderer.combinedPassThru(userClick, keyboardSupport);        
            rootDomNode.setAttribute(HTML.ONCLICK_ATTR, click);     
        }
        domContext.stepInto(uiComponent);

    }

    private boolean isHideRootNode(Tree treeComponent) {
        String hideRootNodeAttribute =
                (String) treeComponent.getAttributes().get("hideRootNode");
        return hideRootNodeAttribute != null &&
               hideRootNodeAttribute.equalsIgnoreCase("true");
    }

    private boolean isHideNavigation(Tree treeComponent) {
        String hideNavAttr =
                (String) treeComponent.getAttributes().get("hideNavigation");
        return hideNavAttr != null && hideNavAttr.equalsIgnoreCase("true");
    }

    /**
     * @param facesContext
     * @param uiComponent
     * @throws IOException
     */
    public void encodeChildren(FacesContext facesContext,
                               UIComponent uiComponent)
            throws IOException {

        // get domContext first to check if we are stream writing
        DOMContext domContext =
                DOMContext.getDOMContext(facesContext, uiComponent);

        TreeModel treeModel = (TreeModel) uiComponent
                .getValueBinding("value").getValue(facesContext);
        DefaultMutableTreeNode treeComponentRootNode =
                (DefaultMutableTreeNode) treeModel.getRoot();
        Element rootNode = (Element) domContext.getRootNode();
        com.icesoft.faces.component.tree.TreeNode treeNode =
                (TreeNode) (uiComponent).getChildren().get(0);
        boolean hideRootNode;
        if (uiComponent instanceof Tree) {
            hideRootNode = isHideRootNode((Tree) uiComponent);
        } else {
            throw new InvalidComponentTypeException("Expecting a Tree");
        }

        encodeParentAndChildNodes(facesContext, (Tree) uiComponent,
                                  (DefaultMutableTreeNode) treeModel.getRoot(),
                                  hideRootNode, rootNode, treeComponentRootNode,
                                  treeNode);

    }

    /**
     * @param facesContext
     * @param treeComponent
     * @param current
     * @param hideCurrentNode
     * @param parentDOMNode
     * @param treeComponentRootNode
     * @param treeNode
     */
    public void encodeParentAndChildNodes(FacesContext facesContext,
                                          Tree treeComponent,
                                          DefaultMutableTreeNode current,
                                          boolean hideCurrentNode,
                                          Element parentDOMNode,
                                          DefaultMutableTreeNode treeComponentRootNode,
                                          TreeNode treeNode) {

        DOMContext domContext =
                DOMContext.getDOMContext(facesContext, treeComponent);

        Element treeNodeDiv = domContext.createElement(HTML.DIV_ELEM);
        if (!hideCurrentNode) {
            // put the next node on the request map
            Map requestMap = facesContext.getExternalContext().getRequestMap();
            String varAttribute = treeComponent.getVar();
            requestMap.put(varAttribute, current);
            if (!treeNode.isRendered()) {
                return;
            }
            domContext.setCursorParent(parentDOMNode);

            treeNodeDiv.setAttribute(HTML.NAME_ATTR, "nd");
            treeNodeDiv
                    .setAttribute(HTML.CLASS_ATTR, treeComponent.getTreeRowStyleClass());
            parentDOMNode.appendChild(treeNodeDiv);
            domContext.setCursorParent(treeNodeDiv);
            encodeNode(facesContext, treeComponent, current, treeNodeDiv,
                       domContext, treeComponentRootNode, treeNode,
                       parentDOMNode);

        } else {
            // root node is hidden 
            // put the next node on the request map
            Map requestMap = facesContext.getExternalContext().getRequestMap();
            String varAttribute = treeComponent.getVar();
            requestMap.put(varAttribute, current);
            if (!treeNode.isRendered()) {
                return;
            }
            domContext.setCursorParent(parentDOMNode);

            treeNodeDiv.setAttribute(HTML.NAME_ATTR, "nd");
            treeNodeDiv
                    .setAttribute(HTML.CLASS_ATTR, treeComponent.getTreeRowStyleClass());
            // treeNodeDiv id is assigned here when roo node is hidden
            treeNodeDiv.setAttribute(HTML.ID_ATTR, ClientIdPool.get(treeComponent
                    .getClientId(facesContext) + "-d-rt"));
            parentDOMNode.appendChild(treeNodeDiv);
            domContext.setCursorParent(treeNodeDiv);
       }
        // iterate child nodes
        int childCount = current.getChildCount();
        if (childCount > 0 &&
            ((IceUserObject) current.getUserObject()).isExpanded()) {
            // render CHILD div
            Element childDiv = domContext.createElement(HTML.DIV_ELEM);
            childDiv.setAttribute(HTML.NAME_ATTR, "c");
            childDiv.setAttribute(HTML.ID_ATTR, ClientIdPool.get(treeNodeDiv
                    .getAttribute(HTML.ID_ATTR) + "-c"));

            treeNodeDiv.appendChild(childDiv);
            // recurse children
            DefaultMutableTreeNode next;
            for (int i = 0; i < childCount; i++) {
                next = (DefaultMutableTreeNode) current.getChildAt(i);
                encodeParentAndChildNodes(facesContext, treeComponent, next,
                                          false, childDiv,
                                          treeComponentRootNode, treeNode);
            }
        }
    }

    /**
     * @param facesContext
     * @param treeComponent
     * @param currentNode
     * @param treeNodeDiv
     * @param domContext
     * @param treeComponentRootNode
     * @param treeNode
     * @param parentDOMNode
     */
    public void encodeNode(FacesContext facesContext,
                           Tree treeComponent,
                           DefaultMutableTreeNode currentNode,
                           Element treeNodeDiv,
                           DOMContext domContext,
                           DefaultMutableTreeNode treeComponentRootNode,
                           TreeNode treeNode,
                           Element parentDOMNode) {

        treeComponent.setNodePath(null);
        treeComponent.setCurrentNode(currentNode);
        String pathToCurrentRoot =
                getPathAsString(currentNode, treeComponentRootNode);
        boolean hideRootNode = isHideRootNode(treeComponent);
        boolean hideNavigation = isHideNavigation(treeComponent);

        String pathToCurrentNode = TreeRenderer.getPathAsString(currentNode,
                (DefaultMutableTreeNode) treeComponent.getModel()
                        .getRoot());
        
        treeNode.setMutable(currentNode);
        treeNode.setId(Tree.ID_PREFIX + pathToCurrentRoot);
        treeNode.setParent(treeComponent);
        treeComponent.setNodePath(pathToCurrentNode);

        // efficiency and simplicity
        IceUserObject userObject = (IceUserObject) currentNode.getUserObject();
        if (currentNode.getLevel() == 0 && treeComponent.getTitle() != null) {
            treeNodeDiv.setAttribute(HTML.TITLE_ATTR, treeComponent.getTitle());
        } else {
            treeNodeDiv.setAttribute(HTML.TITLE_ATTR, userObject.getTooltip());
        }
        // a branch node is a node that is not a leaf
        boolean isBranchNode = !userObject.isLeaf();
        boolean isExpanded = userObject.isExpanded();
        boolean isLastChild =
                treeComponentRootNode.getLastLeaf() == currentNode;
        boolean isCollapsedAndFinalBranch = false;
        if (isBranchNode && !isExpanded) {
            isCollapsedAndFinalBranch = isCollapsedAndFinalBranch(currentNode);
        }
        int level = currentNode.getLevel();
        UIComponent myForm = findForm(treeComponent);
        String formId = myForm.getClientId(facesContext);

        // if the root node was not rendered, then don't render the first
        // column of lines extending down from the position where the root
        // node would have been
        if (hideRootNode) {
            level--;
        }
        for (int i = 0; i < level; i++) {
            //render the vertical line or blank
            Element verticalLine = domContext.createElement(HTML.IMG_ELEM);
            verticalLine.setAttribute(HTML.ALT_ATTR, "");

            // use the level to check parentNodes
            // to determine if we render a line or a blank
            DefaultMutableTreeNode parentNode = null;
            if ((i > 0) || (hideRootNode)) {
                // start at 1 because we are making a call to getParent first
                int j = 1;
                parentNode = (DefaultMutableTreeNode) currentNode.getParent();
                while (j < (level - i)) {
                    parentNode =
                            (DefaultMutableTreeNode) parentNode.getParent();
                    j++;
                }
            }

            boolean renderBlank = false;

            if ((null != parentNode) && (parentNode.getNextSibling() == null)) {
                renderBlank = true;
            }

            if (renderBlank) {
                verticalLine.setAttribute(HTML.SRC_ATTR,
                                          treeComponent.getLineBlankImage());
            } else if ((i == 0) && (!hideRootNode)) {
                verticalLine.setAttribute(HTML.SRC_ATTR,
                                          treeComponent.getLineBlankImage());
            } else if (isLastChild || isCollapsedAndFinalBranch) {
                verticalLine.setAttribute(HTML.SRC_ATTR,
                                          treeComponent.getLineBottomImage());
            } else {
                verticalLine.setAttribute(HTML.SRC_ATTR,
                                          treeComponent.getLineVerticalImage());
            }

            verticalLine.setAttribute(HTML.BORDER_ATTR, "0");
            Text space = domContext.createTextNode(" ");
            treeNodeDiv.appendChild(space);
            treeNodeDiv.appendChild(verticalLine);
            space = domContext.createTextNode(" ");
            treeNodeDiv.appendChild(space);
        }

        if (isBranchNode && !hideNavigation) {
            Element navAnchor = domContext.createElement(HTML.ANCHOR_ELEM);
            navAnchor.setAttribute(HTML.HREF_ATTR, "javascript:;");
            navAnchor.setAttribute(HTML.ID_ATTR, ClientIdPool.get(
                                   treeComponent.getClientId(facesContext) + ":" + pathToCurrentRoot));
            navAnchor.setAttribute(HTML.ONFOCUS_ATTR, "setFocus(this.id);");
            navAnchor.setAttribute(HTML.ONBLUR_ATTR, "setFocus('');");
            String hiddenFieldName =
                    CustomComponentUtils.getHiddenTreeExpandFieldName(
                            treeComponent.getClientId(facesContext),
                            CustomComponentUtils.getFormName(treeComponent,
                                                             facesContext));

            String onclickString =
                    "document.forms['" + formId + "']['" + hiddenFieldName +
                    "'].value="
                    + "'" + pathToCurrentRoot + "';"
                    + "iceSubmitPartial("
                    + " document.forms['" + formId + "'],"
                    + " this,event); "
                    + "return false;";

            navAnchor.setAttribute(HTML.ONCLICK_ATTR, onclickString);
            treeNodeDiv.appendChild(navAnchor);
            treeNodeDiv.appendChild(domContext.createTextNode(" "));
            // icon
            Element iconImage = domContext.createElement(HTML.IMG_ELEM);
            // Determine whether to render the top, middle, or bottom image:
            // Encode the top image with no lines extending if this node is the
            // root of the tree, has children, and is not expanded.
            // Also render the top image with no lines extending if the root
            // node is hidden and this node is the only child of the root and
            // this node is not expanded.
            // Encode the top image if this is the root of the entire tree
            // or if the root is hidden and this is its first child
            // Encode the bottom image if this node has children,
            // is the last child of its parent, and is not expanded
            // Encode the middle image by default
            // *** cleanup replaced line images with blanks ***
            if (currentNode.isRoot() ||
                hideRootNode
                && currentNode.getNextSibling() == null
                && currentNode == treeComponentRootNode.getFirstChild()
                    ) {
                iconImage.setAttribute(HTML.SRC_ATTR, isExpanded ?
                        treeComponent.getNavCloseTopNoSiblingsImage() : treeComponent.getNavOpenTopNoSiblingsImage());
            } else if (
                       hideRootNode &&
                       treeComponentRootNode.getFirstChild() == currentNode) {
                if (isExpanded) {
                    iconImage.setAttribute(HTML.SRC_ATTR,
                                           treeComponent.getNavCloseTopImage());
                } else {
                    iconImage.setAttribute(HTML.SRC_ATTR,
                                           treeComponent.getNavOpenTopImage());
                }
            } else if (currentNode.getNextSibling() == null) {
                if (isExpanded) {
                    iconImage.setAttribute(HTML.SRC_ATTR,
                                           treeComponent.getNavCloseBottomImage());
                } else {
                    iconImage.setAttribute(HTML.SRC_ATTR,
                                           treeComponent.getNavOpenBottomImage());
                }
            } else {
                if (isExpanded) {
                    iconImage.setAttribute(HTML.SRC_ATTR,
                                           treeComponent.getNavCloseMiddleImage());
                } else {
                    iconImage.setAttribute(HTML.SRC_ATTR,
                                           treeComponent.getNavOpenMiddleImage());
                }
            }
            iconImage.setAttribute(HTML.BORDER_ATTR, "0");
            iconImage.setAttribute(HTML.ALT_ATTR, "");

            navAnchor.appendChild(iconImage);

        } else { // this is a leaf node
            Element lineImage = domContext.createElement(HTML.IMG_ELEM);
            Text space = domContext.createTextNode(" ");
            treeNodeDiv.appendChild(space);
            
            treeNodeDiv.appendChild(lineImage);
            lineImage.setAttribute(HTML.BORDER_ATTR, "0");
            lineImage.setAttribute(HTML.ALT_ATTR, "");

            if (currentNode.getNextSibling() == null) {
                // use lineBottomNode image
                lineImage.setAttribute(HTML.SRC_ATTR,
                                       treeComponent.getLineBottomImage());
            } else {
                // use lineMiddleNode image
                lineImage.setAttribute(HTML.SRC_ATTR,
                                       treeComponent.getLineMiddleImage());
            }
            treeNodeDiv.appendChild(domContext.createTextNode(" "));
        }

        String pathToNode = TreeRenderer.getPathAsString(currentNode,
                                                         (DefaultMutableTreeNode) treeComponent
                                                                 .getModel()
                                                                 .getRoot());
        treeNodeDiv.setAttribute(HTML.ID_ATTR, ClientIdPool.get(treeComponent
                .getClientId(facesContext) + "-d-" + pathToNode));

        try {
            encodeParentAndChildren(facesContext, treeNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        treeNodeDiv.normalize();
    }

    /**
     * @param facesContext
     * @param uiComponent
     * @throws IOException
     */
    public void encodeEnd(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
        // TODO refactor
        // the adding of this hidden div ensures that Firefox loads all the
        // (default) images for the tree so when a tree node is clicked and the
        // first instance of an image is encountered for display we don't have to
        // wait for it to be downloaded. The visual disruption that the downloading
        // causes is only visible on firefox. Firefox lays out the page first
        // without the image, and the text in the tree node appears farther left
        // than it will be after the image is downloaded.
        // Then, after the image is downloaded,
        // the page is layed-out again and the text moves to the right
        // to accomodate the image that has finally arrived.
        validateParameters(facesContext, uiComponent, Tree.class);

        DOMContext domContext =
                DOMContext.getDOMContext(facesContext, uiComponent);

        Element rootNode = (Element) domContext.getRootNode();

        Element imageLoaderDiv = domContext.createElement(HTML.DIV_ELEM);
        imageLoaderDiv.setAttribute(HTML.ID_ATTR, "imageCache");
        imageLoaderDiv.setAttribute(HTML.STYLE_ATTR, "display:none;");
        rootNode.appendChild(imageLoaderDiv);

        Element tree_document = domContext.createElement(HTML.IMG_ELEM);
        Element tree_line_blank = domContext.createElement(HTML.IMG_ELEM);
        Element tree_line_vertical = domContext.createElement(HTML.IMG_ELEM);
        Element tree_nav_middle_close = domContext.createElement(HTML.IMG_ELEM);
        Element tree_nav_top_open = domContext.createElement(HTML.IMG_ELEM);
        Element tree_folder_close = domContext.createElement(HTML.IMG_ELEM);
        Element tree_line_last_node = domContext.createElement(HTML.IMG_ELEM);
        Element tree_nav_bottom_close = domContext.createElement(HTML.IMG_ELEM);
        Element tree_nav_middle_open = domContext.createElement(HTML.IMG_ELEM);
        Element tree_nav_top_open_no_siblings =
                domContext.createElement(HTML.IMG_ELEM);
        Element tree_folder_open = domContext.createElement(HTML.IMG_ELEM);
        Element tree_line_middle_node = domContext.createElement(HTML.IMG_ELEM);
        Element tree_nav_bottom_open = domContext.createElement(HTML.IMG_ELEM);
        Element tree_nav_top_close = domContext.createElement(HTML.IMG_ELEM);
        String appBase = CoreUtils.resolveResourceURL(facesContext, "/xmlhttp/css/xp/css-images/");

        tree_document.setAttribute(HTML.SRC_ATTR, appBase + "tree_document.gif");
        tree_line_blank.setAttribute(HTML.SRC_ATTR, appBase + "tree_line_blank.gif");
        tree_line_vertical.setAttribute(HTML.SRC_ATTR, appBase + "tree_line_vertical.gif");
        tree_nav_middle_close.setAttribute(HTML.SRC_ATTR, appBase + "tree_nav_middle_close.gif");
        tree_nav_top_open.setAttribute(HTML.SRC_ATTR, appBase + "tree_nav_top_open.gif");
        tree_folder_close.setAttribute(HTML.SRC_ATTR, appBase + "tree_folder_close.gif");
        tree_line_last_node.setAttribute(HTML.SRC_ATTR, appBase + "tree_line_last_node.gif");
        tree_nav_bottom_close.setAttribute(HTML.SRC_ATTR, appBase +
                                                          "tree_nav_bottom_close.gif");
        tree_nav_middle_open.setAttribute(HTML.SRC_ATTR, appBase +
                                                         "tree_nav_middle_open.gif");
        tree_nav_top_open_no_siblings.setAttribute(HTML.SRC_ATTR, appBase +
                                                                  "tree_nav_top_open_no_siblings.gif");
        tree_folder_open.setAttribute(HTML.SRC_ATTR, appBase +
                                                     "tree_folder_open.gif");
        tree_line_middle_node.setAttribute(HTML.SRC_ATTR, appBase +
                                                          "tree_line_middle_node.gif");
        tree_nav_bottom_open.setAttribute(HTML.SRC_ATTR, appBase +
                                                         "tree_nav_bottom_open.gif");
        tree_nav_top_close.setAttribute(HTML.SRC_ATTR, appBase +
                                                       "tree_nav_top_close.gif");
        

        imageLoaderDiv.appendChild(tree_document);
        imageLoaderDiv.appendChild(tree_line_blank);
        imageLoaderDiv.appendChild(tree_line_vertical);
        imageLoaderDiv.appendChild(tree_nav_middle_close);
        imageLoaderDiv.appendChild(tree_nav_top_open);
        imageLoaderDiv.appendChild(tree_folder_close);
        imageLoaderDiv.appendChild(tree_line_last_node);
        imageLoaderDiv.appendChild(tree_nav_bottom_close);
        imageLoaderDiv.appendChild(tree_nav_middle_open);
        imageLoaderDiv.appendChild(tree_nav_top_open_no_siblings);
        imageLoaderDiv.appendChild(tree_folder_open);
        imageLoaderDiv.appendChild(tree_line_middle_node);
        imageLoaderDiv.appendChild(tree_nav_bottom_open);
        imageLoaderDiv.appendChild(tree_nav_top_close);

        Element hiddenTreeExpand = domContext.createElement(HTML.INPUT_ELEM);
        hiddenTreeExpand.setAttribute(HTML.TYPE_ATTR, "hidden");
        hiddenTreeExpand.setAttribute(HTML.NAME_ATTR, CustomComponentUtils.getHiddenTreeExpandFieldName(
                uiComponent.getClientId(
                        facesContext),
                CustomComponentUtils.getFormName(
                        uiComponent,
                        facesContext)));
        Element hiddenTreeAction = domContext.createElement(HTML.INPUT_ELEM);
        hiddenTreeAction.setAttribute(HTML.TYPE_ATTR, "hidden");        
        hiddenTreeAction.setAttribute(HTML.NAME_ATTR, CustomComponentUtils.getHiddenTreeActionFieldName(
                uiComponent.getClientId(
                        facesContext),
                CustomComponentUtils.getFormName(
                        uiComponent,
                        facesContext)));
        rootNode.appendChild(hiddenTreeAction);
        rootNode.appendChild(hiddenTreeExpand);
        domContext.stepOver();
    }

    private boolean isCollapsedAndFinalBranch(DefaultMutableTreeNode branch) {
        javax.swing.tree.TreeNode[] path = branch.getPath();
        for (int i = 0; i + 1 < path.length; i++) {
            DefaultMutableTreeNode nextParent =
                    (DefaultMutableTreeNode) path[i];
            DefaultMutableTreeNode nextChild =
                    (DefaultMutableTreeNode) path[i + 1];
            if (!(nextParent.getLastChild() == nextChild)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param currentRoot
     * @param treeComponentRootNode
     * @return the path to the given currentRoot
     */
    public static String getPathAsString(DefaultMutableTreeNode currentRoot,
                                         DefaultMutableTreeNode treeComponentRootNode) {
        if (currentRoot == treeComponentRootNode) {
            // special case since there is no path to the root node
            return "root";
        }
        StringBuffer convertedPath = new StringBuffer();
        javax.swing.tree.TreeNode[] path = currentRoot.getPath();
        int pathLength = path.length;
        javax.swing.tree.TreeNode parent = treeComponentRootNode;
        for (int i = 1; i < pathLength; i++) {
            javax.swing.tree.TreeNode nextNodeInPath = path[i];
            int indexOfNextNodeInPath = parent.getIndex(nextNodeInPath);
            convertedPath.append(indexOfNextNodeInPath);
            if (i + 1 < pathLength) {
                convertedPath.append(PATH_DELIMITER);
            }
            parent = nextNodeInPath;
        }
        return convertedPath.toString();
    }
}
