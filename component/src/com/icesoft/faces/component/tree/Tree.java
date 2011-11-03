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

import com.icesoft.faces.component.CSS_DEFAULT;
import com.icesoft.faces.component.ext.taglib.Util;
import com.icesoft.faces.util.CoreUtils;

import javax.faces.application.FacesMessage;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.NamingContainer;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;
import javax.faces.event.PhaseId;
import javax.faces.event.ActionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Tree is a JSF component class that represent an ICEfaces tree.
 * <p/>
 * The tree component displays hierarchical data as a "tree" of branches and
 * leaf nodes. Optionally, the tree may also display navigation controls for the
 * dynamic expansion and collapse of branch nodes. Nodes may also support an
 * action event when clicked that can be used to respond to user click events.
 * <p/>
 * This component extends the JSF UICommand component and implemnents the JSF
 * NamingContainer interface.
 * <p/>
 * By default this component is rendered by the "com.icesoft.faces.View"
 * renderer type.
 *
 * @author Chris Brown
 * @author gmccleary
 * @version 1.1
 */
public class Tree extends UICommand implements NamingContainer {

    // default style classes
    private static final String DEFAULT_CSSIMAGEDIR =
            "/xmlhttp/css/xp/css-images/";

    private static final String DEFAULT_NAV_OPEN_TOP_GIF =
            "tree_nav_top_open.gif";

    private static final String DEFAULT_NAV_CLOSE_TOP_GIF =
            "tree_nav_top_close.gif";

    private static final String DEFAULT_NAV_OPEN_TOP_NO_SIBLINGS_GIF =
            "tree_nav_top_open_no_siblings.gif";

    private static final String DEFAULT_NAV_CLOSE_TOP_NO_SIBLINGS_GIF =
            "tree_nav_top_close_no_siblings.gif";

    private static final String DEFAULT_NAV_OPEN_MIDDLE_GIF =
            "tree_nav_middle_open.gif";

    private static final String DEFAULT_NAV_CLOSE_MIDDLE_GIF =
            "tree_nav_middle_close.gif";

    private static final String DEFAULT_NAV_CLOSE_BOTTOM_GIF =
            "tree_nav_bottom_close.gif";

    private static final String DEFAULT_NAV_OPEN_BOTTOM_GIF =
            "tree_nav_bottom_open.gif";

    private static final String DEFAULT_LINE_MIDDLE_NODE_GIF =
            "tree_line_middle_node.gif";

    private static final String DEFAULT_LINE_VERTICAL_GIF =
            "tree_line_vertical.gif";

    private static final String DEFAULT_LINE_BLANK_GIF = "tree_line_blank.gif";

    private static final String DEFAULT_LINE_BOTTOM_NODE_GIF =
            "tree_line_last_node.gif";

    private static final String DEFAULT_DOCUMENT_GIF = "tree_document.gif";

    private static final String DEFAULT_FOLDER_GIF = "tree_folder_close.gif";

    private static final String DEFAULT_FOLDER_OPEN_GIF =
            "tree_folder_open.gif";

    /**
     * String constant for tree node collapse event.
     */
    public static final String NAVIGATION_EVENT_COLLAPSE = "collapse";

    /**
     * String constant for tree node expand event.
     */
    public static final String NAVIGATION_EVENT_EXPAND = "expand";

    /**
     * String constant for tree node id prefix.
     */
    public static final String ID_PREFIX = "n-";

    // private attributes
    transient private DefaultMutableTreeNode navigatedNode;
    private String navigationEventType;
    // images
    private String imageDir;
    private String navOpenTop;
    private String navOpenTopNoSiblings;
    private String navCloseTopNoSiblings;
    private String navCloseTop;
    private String navCloseMiddle;
    private String navOpenMiddle;
    private String lineMiddleNode;
    private String lineBottomNode;
    private String lineVertical;
    private String lineBlank;
    private String documentImage;
    private String folderImage;
    private String folderOpenImage;
    private String navCloseBottom;
    private String navOpenBottom;
    private String var;
    private String styleClass;
    private String style;
    private String hideRootNode;
    private String hideNavigation;
    private String pathToExpandedNode;
    private Boolean keyboardNavigationEnabled;
    transient private DefaultMutableTreeNode currentNode;
    private String nodePath;
    private String title;

    /**
     * default no args constructor
     */
    public Tree() {

    }

    /**
     * String constant specifying component type
     */
    public static final String COMPONENT_TYPE = "com.icesoft.faces.TreeView";

    /**
     * @return the renderer type of the tree component.
     */
    public String getRendererType() {
        return "com.icesoft.faces.View";
    }

    /**
     * @return the component type of the tree component.
     */
    public String getComponentType() {
        return COMPONENT_TYPE;
    }

    /*
      * (non-Javadoc)
      *
      * @see javax.faces.component.UIComponent#getFamily()
      */
    public String getFamily() {
        return "com.icesoft.faces.TreeView";
    }

    // accessors & modifiers aka getters and setters

    /**
     * @param currentNode
     */
    public void setCurrentNode(DefaultMutableTreeNode currentNode) {
        this.currentNode = currentNode;
    }

    /**
     * @return DefaultMutableTreeNode currentNode
     */
    public DefaultMutableTreeNode getCurrentNode() {
        return this.currentNode;
    }

    public void encodeBegin(FacesContext context) throws IOException {
            if (!keepSaved(context)) {
                savedChildren = new HashMap();
            }
            super.encodeBegin(context);

    }
    
    private boolean keepSaved(FacesContext context) {

        Iterator clientIds = savedChildren.keySet().iterator();
        while (clientIds.hasNext()) {
            String clientId = (String) clientIds.next();
            Iterator messages = context.getMessages(clientId);
            while (messages.hasNext()) {
                FacesMessage message = (FacesMessage) messages.next();
                if (message.getSeverity().compareTo(FacesMessage.SEVERITY_ERROR)
                    >= 0) {
                    return (true);
                }
            }
        }
        return false;

    }    
    
    /**
     * @param nodePath
     */
    public void setNodePath(String nodePath) {
        FacesContext facesContext = getFacesContext();
        this.nodePath = nodePath;
        // save the state of the last node
        saveChildrenState(facesContext);


        // put the current node on the request map
        this.setCurrentVarToRequestMap(facesContext, getCurrentNode());
    

        // restore the state for current node
        restoreChildrenState(facesContext);


    }

    /**
     * @return String nodePath
     */
    public String getNodePath() {
        return this.nodePath;
    }

    /**
     * @return TreeModel model associated with tree
     */
    public TreeModel getModel() {
        ValueBinding vb = getValueBinding("value");
        return (TreeModel) vb.getValue(getFacesContext());
    }

    /**
     * @return TreeNode template
     * @throws MalformedTreeTagException
     */
    public TreeNode getTreeNodeTemplate() throws MalformedTreeTagException {
        TreeNode template = null;
        int childCount = this.getChildCount();
        if (childCount != 1) {
            throw new MalformedTreeTagException(
                    "The tree tag requires a single treeNode child tag. Found ["
                    + childCount + "] children");
        }
        UIComponent treeNodeTemplate = (UIComponent) getChildren().get(0);
        if (treeNodeTemplate == null) {
            throw new MalformedTreeTagException(
                    "The Tree requires a TreeNode child. None found.");
        }

        if (!(treeNodeTemplate instanceof TreeNode)) {
            throw new MalformedTreeTagException(
                    "The Tree requires a TreeNode child. Found child of type ["
                    + treeNodeTemplate.getClass() + "]");
        }
        return template;
    }

    /**
     * @param pathToSelectedNode
     * @return DefaultMutableTreeNode node at specified path
     */
    public DefaultMutableTreeNode getNodeAtPathsEnd(String pathToSelectedNode) {
        if (pathToSelectedNode.equalsIgnoreCase("root")) {
            return (DefaultMutableTreeNode) getModel().getRoot();
        }
        String[] indices = pathToSelectedNode
                .split(TreeRenderer.PATH_DELIMITER);
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) getModel()
                .getRoot();
        for (int i = 0; i < indices.length; i++) {
            parent = (DefaultMutableTreeNode) parent.getChildAt(Integer
                    .parseInt(indices[i]));
        }
        return parent;
    }

    // image paths

    /**
     * @return String imageDir
     */
    public String getImageDir() {
        if (imageDir != null) {
            return imageDir;
        }
        ValueBinding vb = getValueBinding("imageDir");
        return CoreUtils.resolveResourceURL(getFacesContext(),
                vb != null ? (String) vb.getValue(getFacesContext()) : DEFAULT_CSSIMAGEDIR);
    }

    /**
     * <p>Set the value of the <code>styleClass</code> property.</p>
     *
     * @return String style class property value.
     */
    public String getStyleClass() {
        return Util.getQualifiedStyleClass(this, 
                styleClass, 
                CSS_DEFAULT.TREE_DEFAULT_STYLE_CLASS, 
                "styleClass");
    }

    /**
     * <p>Set the value of the <code>styleClass</code> property.</p>
     *
     * @param styleClass
     */
    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    /**
     * <p>Set the value of the <code>style</code> property.</p>
     *
     * @return String style property value.
     */
    public String getStyle() {
        if (style != null) {
            return style;
        }
        ValueBinding vb = getValueBinding("style");
        return vb != null ? (String) vb.getValue(getFacesContext()) : "";
    }

    /**
     * <p>Set the value of the <code>style</code> property.</p>
     *
     * @param style
     */
    public void setStyle(String style) {
        this.style = style;
    }

    String getTreeRowStyleClass() {
        return Util.getQualifiedStyleClass(this, 
                CSS_DEFAULT.STYLE_TREEROW);        
    }
    /**
     * @param imageProperty
     * @param bindingName
     * @param defaultImage
     * @return String img src
     */
    public String getImage(String imageProperty, String bindingName,
                           String defaultImage) {
        if (imageProperty != null) {
            return imageProperty;
        }
        ValueBinding vb = getValueBinding(bindingName);
        if (vb != null) {
            return (String) vb.getValue(getFacesContext());
        }
        return getImageDir() + defaultImage;

    }

    /**
     * @return String folder img src
     */
    public String getFolderImage() {
        return getImage(folderImage, "folderImage", DEFAULT_FOLDER_GIF);
    }

    /**
     * @return String folder open img src
     */
    public String getFolderOpenImage() {
        return getImage(folderOpenImage, "folderOpenImage",
                        DEFAULT_FOLDER_OPEN_GIF);
    }

    /**
     * @return String document img src
     */
    public String getDocumentImage() {
        return getImage(documentImage, "documentImage", DEFAULT_DOCUMENT_GIF);
    }

    /**
     * @return String line bottom img src
     */
    public String getLineBottomImage() {
        return getImage(lineBottomNode, "lineBottom",
                        DEFAULT_LINE_BOTTOM_NODE_GIF);
    }

    /**
     * @return String line vertical img src
     */
    public String getLineVerticalImage() {
        return getImage(lineVertical, "lineVertical",
                        DEFAULT_LINE_VERTICAL_GIF);
    }

    /**
     * @return String line middle img src
     */
    public String getLineMiddleImage() {
        return getImage(lineMiddleNode, "lineMiddle",
                        DEFAULT_LINE_MIDDLE_NODE_GIF);
    }

    /**
     * @return String navigation close middle img src
     */
    public String getNavCloseMiddleImage() {
        return getImage(navCloseMiddle, "navCloseMiddleImage",
                        DEFAULT_NAV_CLOSE_MIDDLE_GIF);
    }

    /**
     * @return String navigation open middle img src
     */
    public String getNavOpenMiddleImage() {
        return getImage(navOpenMiddle, "navOpenMiddle",
                        DEFAULT_NAV_OPEN_MIDDLE_GIF);
    }

    /**
     * @return String navigation close top img src
     */
    public String getNavCloseTopImage() {
        return getImage(navCloseTop, "navCloseTop", DEFAULT_NAV_CLOSE_TOP_GIF);
    }

    /**
     * @return String navigation open top img src
     */
    public String getNavOpenTopImage() {
        return getImage(navOpenTop, "navOpenTopImage",
                        DEFAULT_NAV_OPEN_TOP_GIF);
    }

    /**
     * @return String navigation open top img src
     */
    public String getNavOpenTopNoSiblingsImage() {
        return getImage(navOpenTopNoSiblings, "navOpenTopNoSiblingsImage",
                        DEFAULT_NAV_OPEN_TOP_NO_SIBLINGS_GIF);
    }

    /**
     * @return String navigation close top img src
     */
    public String getNavCloseTopNoSiblingsImage() {
        return getImage(navCloseTopNoSiblings, "navCloseTopNoSiblingsImage",
                        DEFAULT_NAV_CLOSE_TOP_NO_SIBLINGS_GIF);
    }

    /**
     * @return String navigation close bottom img src
     */
    public String getNavCloseBottomImage() {
        return getImage(navCloseBottom, "navCloseBottomImage",
                        DEFAULT_NAV_CLOSE_BOTTOM_GIF);
    }

    /**
     * @return String navigation open bottom img src
     */
    public String getNavOpenBottomImage() {
        return getImage(navOpenBottom, "navOpenBottomImage",
                        DEFAULT_NAV_OPEN_BOTTOM_GIF);
    }

    /**
     * @return String blank img src
     */
    public String getLineBlankImage() {
        return getImage(lineBlank, "lineBlank", DEFAULT_LINE_BLANK_GIF);
    }

    /**
     * @return String line bottom node
     */
    public String getLineBottomNode() {
        return lineBottomNode;
    }

    /**
     * @param lineBottomNode
     */
    public void setLineBottomNode(String lineBottomNode) {
        this.lineBottomNode = lineBottomNode;
    }

    /**
     * @return String line middle node
     */
    public String getLineMiddleNode() {
        return lineMiddleNode;
    }

    /**
     * @param lineMiddleNode
     */
    public void setLineMiddleNode(String lineMiddleNode) {
        this.lineMiddleNode = lineMiddleNode;
    }

    /**
     * @return String line vertical
     */
    public String getLineVertical() {
        return lineVertical;
    }

    /**
     * @param lineVertical
     */
    public void setLineVertical(String lineVertical) {
        this.lineVertical = lineVertical;
    }

    /**
     * @return String navigation expand middle
     */
    public String getNavExpandedMiddle() {
        return navCloseMiddle;
    }

    /**
     * @param navExpandedMiddle
     */
    public void setNavExpandedMiddle(String navExpandedMiddle) {
        this.navCloseMiddle = navExpandedMiddle;
    }

    /**
     * @return String navigation expanded top
     */
    public String getNavExpandedTop() {
        return navOpenTop;
    }

    /**
     * @param navExpandedTop
     */
    public void setNavExpandedTop(String navExpandedTop) {
        this.navOpenTop = navExpandedTop;
    }

    /**
     * @param imageDir
     */
    public void setImageDir(String imageDir) {
        this.imageDir = imageDir;
    }

    /**
     * @param documentImage
     */
    public void setDocumentImage(String documentImage) {
        this.documentImage = documentImage;
    }

    /**
     * @param folderImage
     */
    public void setFolderImage(String folderImage) {
        this.folderImage = folderImage;
    }

    /**
     * @return String navigation close top
     */
    public String getNavCloseTop() {
        return navCloseTop;
    }

    /**
     * @param navCloseTop
     */
    public void setNavCloseTop(String navCloseTop) {
        this.navCloseTop = navCloseTop;
    }

    /**
     * @return String navigation open top
     */
    public String getNavOpenTop() {
        return navOpenTop;
    }

    /**
     * @param navOpenTop
     */
    public void setNavOpenTop(String navOpenTop) {
        this.navOpenTop = navOpenTop;
    }

    /**
     * @return String navigation open top
     */
    public String getNavOpenTopNoSiblings() {
        return navOpenTopNoSiblings;
    }

    /**
     * @param navOpenTopNoSiblings
     */
    public void setNavOpenTopNoSiblings(String navOpenTopNoSiblings) {
        this.navOpenTopNoSiblings = navOpenTopNoSiblings;
    }

    /**
     * @return String navigation close top
     */
    public String getNavCloseTopNoSiblings() {
        return navCloseTopNoSiblings;
    }

    /**
     * @param navCloseTopNoSiblings
     */
    public void setNavCloseTopNoSiblings(String navCloseTopNoSiblings) {
        this.navCloseTopNoSiblings = navCloseTopNoSiblings;
    }

    /**
     * @param folderOpenImage
     */
    public void setFolderOpenImage(String folderOpenImage) {
        this.folderOpenImage = folderOpenImage;
    }

    /**
     * @return String navigation close bottom
     */
    public String getNavCloseBottom() {
        return navCloseBottom;
    }

    /**
     * @param navCloseBottom
     */
    public void setNavCloseBottom(String navCloseBottom) {
        this.navCloseBottom = navCloseBottom;
    }

    /**
     * @return String navigation close middle
     */
    public String getNavCloseMiddle() {
        return navCloseMiddle;
    }

    /**
     * @param navCloseMiddle
     */
    public void setNavCloseMiddle(String navCloseMiddle) {
        this.navCloseMiddle = navCloseMiddle;
    }

    /**
     * @return String navigation open bottom
     */
    public String getNavOpenBottom() {
        return navOpenBottom;
    }

    /**
     * @param navOpenBottom
     */
    public void setNavOpenBottom(String navOpenBottom) {
        this.navOpenBottom = navOpenBottom;
    }

    /**
     * @return String navigation open middle
     */
    public String getNavOpenMiddle() {
        return navOpenMiddle;
    }

    /**
     * @param navOpenMiddle
     */
    public void setNavOpenMiddle(String navOpenMiddle) {
        this.navOpenMiddle = navOpenMiddle;
    }

    /**
     * @return String var
     */
    public String getVar() {
        return var;
    }

    /**
     * @param var
     */
    public void setVar(String var) {
        this.var = var;
    }

    /**
     * @return String lineBlank
     */
    public String getLineBlank() {
        return lineBlank;
    }

    /**
     * @param lineBlank
     */
    public void setLineBlank(String lineBlank) {
        this.lineBlank = lineBlank;
    }

    /**
     * @return DefaultMutableTreeNode navigatedNode
     */
    public DefaultMutableTreeNode getNavigatedNode() {
        if (pathToExpandedNode != null) {
            return getNodeAtPathsEnd(pathToExpandedNode);
        }
        return navigatedNode;
    }

    /**
     * @param navigatedNode
     */
    public void setNavigatedNode(DefaultMutableTreeNode navigatedNode) {
        pathToExpandedNode = null;
        this.navigatedNode = navigatedNode;
    }

    /**
     * @param string
     */
    public void setNavigationEventType(String string) {
        this.navigationEventType = string;
    }

    /**
     * @return String navigationEventType
     */
    public String getNavigationEventType() {
        return navigationEventType;
    }

    // StateHolder Methods

    /**
     * save the tree component state
     *
     * @param context
     * @return Object values[]
     */
    public Object saveState(FacesContext context) {

        Object values[] = new Object[28];
        values[0] = super.saveState(context);
        values[1] = title;        
        values[2] = navigationEventType;
        values[3] = imageDir;
        values[4] = navOpenTop;
        values[5] = navOpenTopNoSiblings;
        values[6] = navCloseTopNoSiblings;
        values[7] = navCloseTop;
        values[8] = navCloseMiddle;
        values[9] = navOpenMiddle;
        values[10] = lineMiddleNode;
        values[11] = lineBottomNode;
        values[12] = lineVertical;
        values[13] = lineBlank;
        values[14] = documentImage;
        values[15] = folderImage;
        values[16] = folderOpenImage;
        values[17] = navCloseBottom;
        values[18] = navOpenBottom;
        values[19] = var;
        values[20] = styleClass;
        values[21] = style;
        values[22] = hideRootNode;
        values[23] = hideNavigation;
        values[24] = nodePath;
        values[25] = savedChildren;
        values[26] = pathToExpandedNode; 
        values[27] = keyboardNavigationEnabled;
        

        return (values);

    }

    /**
     * restore the tree component state
     *
     * @param context
     * @param state
     */
    public void restoreState(FacesContext context, Object state) {

        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        title = (String) values[1];
        navigationEventType = (String) values[2];
        imageDir = (String) values[3];
        navOpenTop = (String) values[4];
        navOpenTopNoSiblings = (String) values[5];
        navCloseTopNoSiblings = (String) values[6];
        navCloseTop = (String) values[7];
        navCloseMiddle = (String) values[8];
        navOpenMiddle = (String) values[9];
        lineMiddleNode = (String) values[10];
        lineBottomNode = (String) values[11];
        lineVertical = (String) values[12];
        lineBlank = (String) values[13];
        documentImage = (String) values[14];
        folderImage = (String) values[15];
        folderOpenImage = (String) values[16];
        navCloseBottom = (String) values[17];
        navOpenBottom = (String) values[18];
        var = (String) values[19];
        styleClass = (String) values[20];
        style = (String) values[21];
        hideRootNode = (String) values[22];
        hideNavigation = (String) values[23];
        nodePath = (String) values[24];
        savedChildren = (Map) values[25];
        pathToExpandedNode = (String) values[26];
        keyboardNavigationEnabled = (Boolean) values[27];         
    }


    /*
    * (non-Javadoc)
    *
    * @see javax.faces.component.UIComponent#processDecodes(javax.faces.context.FacesContext)
    */
    public void processDecodes(FacesContext context) {
        if (context == null) {
            throw new NullPointerException("context");
        }
        if (!isRendered()) {
            return;
        }
        if (null == savedChildren || !keepSaved(context)) {
            savedChildren = new HashMap();
        }


        this.processTreeNodes((DefaultMutableTreeNode) getModel().getRoot(),
                              PhaseId.APPLY_REQUEST_VALUES, context);

        try {
            this.setNodePath(null);
            this.setCurrentNode(null);
            decode(context);
        } catch (RuntimeException e) {
            context.renderResponse();
            throw e;
        }
    }

    // see superclass for documentation
    /* (non-Javadoc)
     * @see javax.faces.component.UIComponentBase#processValidators(javax.faces.context.FacesContext)
     */
    public void processValidators(FacesContext context) {
        if (context == null) {
            throw new NullPointerException("context");
        }
        if (!isRendered()) {
            return;
        }

        this.processTreeNodes((DefaultMutableTreeNode) getModel().getRoot(),
                              PhaseId.PROCESS_VALIDATIONS, context);

        this.setNodePath(null);
        this.setCurrentNode(null);
    }


    // see superclass for documentation
    /* (non-Javadoc)
     * @see javax.faces.component.UIComponentBase#processUpdates(javax.faces.context.FacesContext)
     */
    public void processUpdates(FacesContext context) {
        if (context == null) {
            throw new NullPointerException("context");
        }
        if (!isRendered()) {
            return;
        }

        this.processTreeNodes((DefaultMutableTreeNode) getModel().getRoot(),
                              PhaseId.UPDATE_MODEL_VALUES, context);

        this.setNodePath(null);
        this.setCurrentNode(null);
    }

    /**
     * Recursively process all TreeNodes starting at the currentNode. TreeNodes
     * will be process according the the phaseId
     *
     * @param currentNode
     * @param phaseId
     * @param context
     */
    private void processTreeNodes(DefaultMutableTreeNode currentNode,
                                  Object phaseId,
                                  FacesContext context) {
        // set currentNode on tree
        this.setCurrentNode(currentNode);

        TreeNode treeNodeTemplate = (TreeNode) this.getChildren().get(0);
        String pathToCurrentNode = TreeRenderer.getPathAsString(currentNode,
                                                                (DefaultMutableTreeNode) getModel()
                                                                        .getRoot());
        treeNodeTemplate.setMutable(currentNode);
        treeNodeTemplate.setId(ID_PREFIX + pathToCurrentNode);
        treeNodeTemplate.setParent(this);

        this.setNodePath(pathToCurrentNode);

        // get TreeNode facets from treeNodeTemplate
        UIComponent iconFacet;
        UIComponent contentFacet;

        iconFacet = treeNodeTemplate.getIcon();
        contentFacet = treeNodeTemplate.getContent();

        // call appropriate phase handler
        if (phaseId == PhaseId.APPLY_REQUEST_VALUES) {
            if (iconFacet != null) {
                iconFacet.processDecodes(context);
            }
            if (contentFacet != null) {
                contentFacet.processDecodes(context);
            }
        } else if (phaseId == PhaseId.PROCESS_VALIDATIONS) {
            if (iconFacet != null) {
                iconFacet.processValidators(context);
            }
            if (contentFacet != null) {
                contentFacet.processValidators(context);
            }

        } else if (phaseId == PhaseId.UPDATE_MODEL_VALUES) {
            if (iconFacet != null) {
                iconFacet.processUpdates(context);
            }
            if (contentFacet != null) {
                contentFacet.processUpdates(context);
            }

        }

        // recurse currentRoot's children
        IceUserObject userObject = (IceUserObject) currentNode.getUserObject();
        if (userObject.isExpanded()) {
            int childCount = currentNode.getChildCount();
            for (int childIndex = 0; childIndex < childCount; childIndex++) {
                DefaultMutableTreeNode nextNode =
                        (DefaultMutableTreeNode) currentNode
                                .getChildAt(childIndex);

                processTreeNodes(nextNode,
                                 phaseId,
                                 context);
            }
        }
    }

    /*
      * (non-Javadoc)
      *
      * @see javax.faces.component.UIComponent#queueEvent(javax.faces.event.FacesEvent)
      */
    public void queueEvent(FacesEvent event) {
        UIComponent eventComponent = event.getComponent();
        UIComponent parentTreeNode = eventComponent.getParent();
        while (parentTreeNode != null &&
               !(parentTreeNode instanceof TreeNode)) {
            parentTreeNode = parentTreeNode.getParent();
        }
        if (parentTreeNode != null) {
            event = new NodeEvent(
                this, event, ((TreeNode) parentTreeNode).getMutable());
        }
        // ICE-1956 UICommand subclasses shouldn't call super.queueEvent
        //  on ActionEvents or else the immediate flag is ignored
        // Shouldn't really be an issue for Tree though, since it tries
        //  to wrap event in NodeEvent, which doesn't extend ActionEvent,
        //  but we might as well still have this code so we're not brittle
        if( (event instanceof ActionEvent) &&
            !this.equals(event.getComponent()) &&
            getParent() != null )
        {
            getParent().queueEvent(event);
        }
        else
            super.queueEvent(event);
    }

    /*
      * (non-Javadoc)
      *
      * @see javax.faces.component.UIComponent#broadcast(javax.faces.event.FacesEvent)
      */
    public void broadcast(FacesEvent event) throws AbortProcessingException {

        if (!(event instanceof NodeEvent)) {
            super.broadcast(event);
            return;
        }

        // Set up the correct context and fire our wrapped event
        NodeEvent wrapperEvent = (NodeEvent) event;
        DefaultMutableTreeNode eventNode = wrapperEvent.getNode();
        this.setCurrentNode(eventNode);
        this.setNodePath(TreeRenderer.getPathAsString(eventNode,
                                                      (DefaultMutableTreeNode) getModel()
                                                              .getRoot()));

        FacesEvent facesEvent = wrapperEvent.getFacesEvent();
        facesEvent.getComponent().broadcast(facesEvent);
    }

    /**
     * @param context
     * @param currentVar
     */
    private void setCurrentVarToRequestMap(FacesContext context,
                                           DefaultMutableTreeNode currentVar) {
        Map requestMap = context.getExternalContext().getRequestMap();
        String varAttribute = getVar();
        if (currentVar != null) {
            requestMap.put(varAttribute, currentVar);
        }
    }

    /**
     * @return String hideNavigation
     */
    public String getHideNavigation() {

        if (hideNavigation != null) {
            return hideNavigation;
        }
        ValueBinding vb = getValueBinding("hideNavigation");
        if (vb != null) {
            return vb.getValue(getFacesContext()).toString();
        }
        return String.valueOf(false);

    }

    /**
     * @param b
     */
    public void setHideNavigation(String b) {
        hideNavigation = b;
    }

    /**
     * @return String hideRootNode
     */
    public String getHideRootNode() {

        if (hideRootNode != null) {
            return hideRootNode;
        }
        ValueBinding vb = getValueBinding("hideRootNode");
        if (vb != null) {
            return vb.getValue(getFacesContext()).toString();
        }
        return String.valueOf(false);
    }

    /**
     * @param b
     */
    public void setHideRootNode(String b) {
        hideRootNode = b;
    }

    public String getTitle() {
        if (title != null) return title;
        ValueBinding vb = getValueBinding("title");
        if (vb == null) return null;
        return (String) vb.getValue(getFacesContext());
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // This class wraps TreeNode events 
    // the TreeNode generates an event and the Tree.queueEvent()
    // wraps the event in a NodeEvent which the Tree.broadcast
    // recieves and passes on to the TreeNode.
    class NodeEvent extends FacesEvent {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        private FacesEvent event = null;

        private DefaultMutableTreeNode node = null;

        /**
         * @param component
         * @param event
         * @param node
         */
        public NodeEvent(UIComponent component, FacesEvent event,
                         DefaultMutableTreeNode node) {
            super(component);
            this.event = event;
            this.node = node;
        }

        /**
         * @return DefaultMutableTreeNode node
         */
        public DefaultMutableTreeNode getNode() {
            return node;
        }

        /**
         * @param node
         */
        public void setNode(DefaultMutableTreeNode node) {
            this.node = node;
        }

        /**
         * @return FacesEvent event
         */
        public FacesEvent getFacesEvent() {
            return (this.event);
        }

        /* (non-Javadoc)
         * @see javax.faces.event.FacesEvent#getPhaseId()
         */
        public PhaseId getPhaseId() {
            return (this.event.getPhaseId());
        }

        /* (non-Javadoc)
         * @see javax.faces.event.FacesEvent#setPhaseId(javax.faces.event.PhaseId)
         */
        public void setPhaseId(PhaseId phaseId) {
            this.event.setPhaseId(phaseId);
        }

        /* (non-Javadoc)
         * @see javax.faces.event.FacesEvent#isAppropriateListener(javax.faces.event.FacesListener)
         */
        public boolean isAppropriateListener(FacesListener listener) {
            return (false);
        }

        /* (non-Javadoc)
         * @see javax.faces.event.FacesEvent#processListener(javax.faces.event.FacesListener)
         */
        public void processListener(FacesListener listener) {
            throw new IllegalStateException();
        }

    }


    /**
     * <p>This map contains <code>ChildState</code> instances for each child
     * component, keyed by the client id of the child.</p>
     */
    protected Map savedChildren = new HashMap();


    /**
     * <p>Restore state information for all child components.</p>
     *
     * @param context
     */
    protected void restoreChildrenState(FacesContext context) {
        if (getChildCount() == 0) return;
        Iterator kids = getChildren().iterator();
        while (kids.hasNext()) {
            UIComponent kid = (UIComponent) kids.next();
            restoreChildState(kid, context);
        }
    }

    /**
     * <p>Restore state information for the given child component.</p>
     *
     * @param component
     * @param context
     */
    protected void restoreChild(UIComponent component, FacesContext context) {
        // Restore state for this component (if it is a EditableValueHolder)
        if (component instanceof EditableValueHolder) {
            EditableValueHolder input = (EditableValueHolder) component;
            String clientId = component.getClientId(context);
            ChildState state = (ChildState) savedChildren.get(clientId);
            if (state == null) {
                state = new ChildState();
            }
            input.setValue(state.getValue());
            input.setValid(state.isValid());
            input.setSubmittedValue(state.getSubmittedValue());
            input.setLocalValueSet(state.isLocalValueSet());
        }
    }

    /**
     * <p>Restore state information for the given child component and its
     * children.</p>
     *
     * @param component
     * @param context
     */
    private void restoreChildState(UIComponent component,
                                   FacesContext context) {

        // Reset the client identifier for this component
        String id = component.getId();
        component.setId(id); // Forces client id to be reset
        // restore state for child component
        restoreChild(component, context);

        // Restore state for children of this component
        Iterator kids = component.getFacetsAndChildren();
        while (kids.hasNext()) {
            restoreChildState((UIComponent) kids.next(), context);
        }
    }

    /**
     * <p>Save state information for all children of this component.</p>
     *
     * @param context
     */
    protected void saveChildrenState(FacesContext context) {
        if (getChildCount() == 0) return;
        Iterator kids = getChildren().iterator();
        while (kids.hasNext()) {
            UIComponent kid = (UIComponent) kids.next();
            saveChildState(kid, context);
        }
    }

    /**
     * <p>Save state information for the given child component.</p>
     *
     * @param component
     * @param context
     */
    protected void saveChild(UIComponent component, FacesContext context) {
        // Save state for this component (if it is a EditableValueHolder)
        if (component instanceof EditableValueHolder) {
            EditableValueHolder input = (EditableValueHolder) component;
            String clientId = component.getClientId(context);
            ChildState state = (ChildState) savedChildren.get(clientId);
            if (state == null) {
                state = new ChildState();
                savedChildren.put(clientId, state);
            }
            state.setValue(input.getLocalValue());
            state.setValid(input.isValid());
            state.setSubmittedValue(input.getSubmittedValue());
            state.setLocalValueSet(input.isLocalValueSet());
        }
    }

    /**
     * <p>Save state information for the given child component and its
     * children.</p>
     *
     * @param component
     * @param context
     */
    protected void saveChildState(UIComponent component, FacesContext context) {
        // Save state for this child
        saveChild(component, context);
        // Save state for children of this component including facets as tree nodes are made up of facets
        Iterator kids = component.getFacetsAndChildren();
        while (kids.hasNext()) {
            saveChildState((UIComponent) kids.next(), context);
        }
    }

    String getPathToExpandedNode() {
        return pathToExpandedNode;
    }

    void setPathToExpandedNode(String pathToExpandedNode) {
        this.pathToExpandedNode = pathToExpandedNode;
    }
    
    public boolean isKeyboardNavigationEnabled() {
        if (keyboardNavigationEnabled != null) {
            return keyboardNavigationEnabled.booleanValue();
        }
        ValueBinding vb = getValueBinding("keyboardNavigationEnabled");
        Boolean boolVal = vb != null ?
                (Boolean) vb.getValue(getFacesContext()) : null;
        return boolVal != null ? boolVal.booleanValue() : true;
    }

    public void setKeyboardNavigationEnabled(boolean keyboardNavigationEnabled) {
        this.keyboardNavigationEnabled = new Boolean(keyboardNavigationEnabled);
    }     
}

//  Private class to represent saved state information for the children of the Tree component

class ChildState implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Object submittedValue;
    private boolean valid = true;
    private Object value;
    private boolean localValueSet;

    Object getSubmittedValue() {
        return (this.submittedValue);
    }

    void setSubmittedValue(Object submittedValue) {
        this.submittedValue = submittedValue;
    }

    boolean isValid() {
        return (this.valid);
    }

    void setValid(boolean valid) {
        this.valid = valid;
    }

    Object getValue() {
        return (this.value);
    }

    /**
     * @param value
     */
    public void setValue(Object value) {
        this.value = value;
    }

    boolean isLocalValueSet() {
        return (this.localValueSet);
    }

    /**
     * @param localValueSet
     */
    public void setLocalValueSet(boolean localValueSet) {
        this.localValueSet = localValueSet;
    }
}


