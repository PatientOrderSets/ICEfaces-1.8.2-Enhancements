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

package org.icefaces.application.showcase.view.bean.examples.component.expandableTable;


import org.icefaces.application.showcase.util.StyleBean;

import javax.faces.event.ActionEvent;
import java.util.ArrayList;

/**
 * <p>The <code>FilesGroupRecordBean</code> class is responsible for storing
 * view specific information for the filesGroupRecord Bean. Such things as
 * expand/contract images and css attributes are stored in this Bean. Model can
 * be found in the sub class <code>FilesGroupRecord</code>. </p>
 * <p/>
 * <p>This class is also responsible for handling all events that control how
 * the view (jspx) page behaves.</p>
 */
public class FilesGroupRecordBean extends FilesGroupRecord {

    protected static final String DEFAULT_IMAGE_DIR =
            "/xmlhttp/css/rime/css-images/";
    protected static final String SPACER_IMAGE =
            "tree_line_blank.gif";

    // style for column that holds expand/contract image toggle, in the files
    // record row.
    protected String indentStyleClass = "";

    // style for all other columns in the files record row.
    protected String rowStyleClass = "";

    protected StyleBean styleBean;

    // Images used to represent expand/contract, spacer by default
    protected String expandImage;   // + or >
    protected String contractImage; // - or v

    // callback to list which contains all data in the dataTable.  This callback
    // is needed so that a node can be set in the expanded state at construction time.
    protected ArrayList parentInventoryList;

    // indicates if node is in expanded state.
    protected boolean isExpanded;

    /**
     * <p>Creates a new <code>FilesGroupRecordBean</code>.  This constructor
     * should be used when creating FilesGroupRecordBeans which will contain
     * children</p>
     *
     * @param isExpanded true, indicates that the specified node will be
     *                   expanded by default; otherwise, false.
     */
    public FilesGroupRecordBean(String indentStyleClass,
                                String rowStyleClass,
                                StyleBean styleBean,
                                String expandImage,
                                String contractImage,
                                ArrayList parentInventoryList,
                                boolean isExpanded) {

        this.indentStyleClass = indentStyleClass;
        this.rowStyleClass = rowStyleClass;
        this.styleBean = styleBean;
        this.expandImage = expandImage;
        this.contractImage = contractImage;
        this.parentInventoryList = parentInventoryList;
        this.parentInventoryList.add(this);
        this.isExpanded = isExpanded;
        // update the default state of the node.
        if (this.isExpanded) {
            expandNodeAction();
        }
    }

    /**
     * <p>Creates a new <code>FilesGroupRecordBean</code>.  This constructor
     * should be used when creating a FilesGroupRecordBean which will be a child
     * of some other FilesGroupRecordBean.</p>
     * <p/>
     * <p>The created FilesGroupRecordBean has no image states defined.</p>
     *
     * @param indentStyleClass
     * @param rowStyleClass
     */
    public FilesGroupRecordBean(String indentStyleClass,
                                String rowStyleClass) {

        this.indentStyleClass = indentStyleClass;
        this.rowStyleClass = rowStyleClass;
    }

    /**
     * Gets the renderable state of the contract/expand image toggle.
     *
     * @return true if images should be drawn; otherwise, false.
     */
    public boolean isRenderImage() {
        return childFilesRecords != null && childFilesRecords.size() > 0;
    }

    /**
     * Toggles the expanded state of this FilesGroup Record.
     *
     * @param event
     */
    public void toggleSubGroupAction(ActionEvent event) {
        // toggle expanded state
        isExpanded = !isExpanded;

        // add sub elements to list
        if (isExpanded) {
            expandNodeAction();
        }
        // remove items from list
        else {
            contractNodeAction();
        }
    }

    /**
     * Adds a child files record to this files group.
     *
     * @param filesGroupRecord child files record to add to this record.
     */
    public void addChildFilesGroupRecord(
            FilesGroupRecordBean filesGroupRecord) {
        if (this.childFilesRecords != null && filesGroupRecord != null) {
            this.childFilesRecords.add(filesGroupRecord);
            if (isExpanded) {
                // to keep elements in order, remove all
                contractNodeAction();
                // then add them again.
                expandNodeAction();
            }
        }
    }

    /**
     * Removes the specified child files record from this files group.
     *
     * @param filesGroupRecord child files record to remove.
     */
    public void removeChildFilesGroupRecord(
            FilesGroupRecordBean filesGroupRecord) {
        if (this.childFilesRecords != null && filesGroupRecord != null) {
            if (isExpanded) {
                // remove all, make sure we are removing the specified one too.
                contractNodeAction();
            }
            // remove the current node
            this.childFilesRecords.remove(filesGroupRecord);
            // update the list if needed.
            if (isExpanded) {
                // to keep elements in order, remove all
                contractNodeAction();
                // then add them again.
                expandNodeAction();
            }
        }
    }

    /**
     * Utility method to add all child nodes to the parent dataTable list.
     */
    private void expandNodeAction() {
        if (childFilesRecords != null && childFilesRecords.size() > 0) {
            // get index of current node
            int index = parentInventoryList.indexOf(this);

            // add all items in childFilesRecords to the parent list
            parentInventoryList.addAll(index + 1, childFilesRecords);
        }

    }

    /**
     * Utility method to remove all child nodes from the parent dataTable list.
     */
    private void contractNodeAction() {
        if (childFilesRecords != null && childFilesRecords.size() > 0) {
            // remove all items in childFilesRecords from the parent list
            parentInventoryList.removeAll(childFilesRecords);
        }
    }

    /**
     * Gets the style class name used to define the first column of a files
     * record row.  This first column is where a expand/contract image is
     * placed.
     *
     * @return indent style class as defined in css file
     */
    public String getIndentStyleClass() {
        return indentStyleClass;
    }

    /**
     * Gets the style class name used to define all other columns in the files
     * record row, except the first column.
     *
     * @return style class as defined in css file
     */
    public String getRowStyleClass() {
        return rowStyleClass;
    }

    /**
     * Gets the image which will represent either the expanded or contracted
     * state of the <code>FilesGroupRecordBean</code>.
     *
     * @return name of image to draw
     */
    public String getExpandContractImage() {
        if (styleBean != null) {
            String dir = styleBean.getImageDirectory();
            String img = isExpanded ? contractImage : expandImage;
            return dir + img;
        }
        return DEFAULT_IMAGE_DIR + SPACER_IMAGE;
    }
}