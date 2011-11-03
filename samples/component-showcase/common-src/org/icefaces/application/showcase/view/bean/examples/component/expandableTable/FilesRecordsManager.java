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

import org.icefaces.application.showcase.util.MessageBundleLoader;
import org.icefaces.application.showcase.util.StyleBean;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.io.Serializable;

/**
 * <p>The <code>FilesRecordsManager</code> class is responsible for constructing
 * the list of <code>FilesGroupRecordBean</code> beans which will be bound to a
 * ice:dataTable JSF component.  This construction process is currently static
 * but could easily be configured to work with Hibernate or DAO implementation.
 * </p>
 * <p/>
 * <p>Large data sets could be handled by adding an ice:dataPaginator.
 * Alternatively the dataTable could also be hidden and the dataTable could be
 * added to scrollable ice:panelGroup. </p>
 * <p/>
 * <p>All default style allocation can be changed in this class.  The
 * application /lib/sytle.css file can be edited to change the style properties
 * of the table.</p>
 */
public class FilesRecordsManager implements Serializable {

    private ArrayList inventoryGroupItemBeans;

    private boolean isInit;

    // css style related constants
    public static final String GROUP_INDENT_STYLE_CLASS = MessageBundleLoader.getMessage("bean.expandTable.GROUP_INDENT_STYLE_CLASS");
    public static final String GROUP_ROW_STYLE_CLASS = MessageBundleLoader.getMessage("bean.expandTable.GROUP_ROW_STYLE_CLASS");
    public static final String CHILD_INDENT_STYLE_CLASS = MessageBundleLoader.getMessage("bean.expandTable.CHILD_INDENT_STYLE_CLASS");
    public static final String CHILD_ROW_STYLE_CLASS = MessageBundleLoader.getMessage("bean.expandTable.CHILD_ROW_STYLE_CLASS");
    // toggle for expand contract
    public static final String CONTRACT_IMAGE = MessageBundleLoader.getMessage("bean.expandTable.CONTRACT_IMAGE");
    public static final String EXPAND_IMAGE = MessageBundleLoader.getMessage("bean.expandTable.EXPAND_IMAGE");

    public FilesRecordsManager() {
        init();
    }

    private void init() {

        // check if manager has been initiated
        if (isInit) {
            return;
        }
        isInit = true;

        // initiate the list
        if (inventoryGroupItemBeans != null) {
            inventoryGroupItemBeans.clear();
        } else {
            inventoryGroupItemBeans = new ArrayList(10);
        }

        Application application =
                FacesContext.getCurrentInstance().getApplication();
        StyleBean styleBean =
                ((StyleBean) application.createValueBinding("#{styleBean}").
                        getValue(FacesContext.getCurrentInstance()));

        /**
         * Build the array list group items.  Currently static but could be easily
         * bound to a database.
         */

        // Project Budget's group
        FilesGroupRecordBean filesRecordGroup =
                new FilesGroupRecordBean(GROUP_INDENT_STYLE_CLASS,
                        GROUP_ROW_STYLE_CLASS,
                        styleBean,
                        EXPAND_IMAGE, CONTRACT_IMAGE,
                        inventoryGroupItemBeans, false);
        filesRecordGroup.setDescription(MessageBundleLoader.getMessage("bean.expandTable.parentGroup1.description"));
        filesRecordGroup.setModified(MessageBundleLoader.getMessage("bean.expandTable.parentGroup1.modified"));
        filesRecordGroup.setCreated(MessageBundleLoader.getMessage("bean.expandTable.parentGroup1.created"));
        filesRecordGroup.setSize(MessageBundleLoader.getMessage("bean.expandTable.parentGroup1.size"));
        filesRecordGroup.setKind(MessageBundleLoader.getMessage("bean.expandTable.parentGroup1.kind"));
        filesRecordGroup.setVersion(MessageBundleLoader.getMessage("bean.expandTable.parentGroup1.version"));
        // add Project budget's children
        FilesGroupRecordBean childFilesGroup =
                new FilesGroupRecordBean(CHILD_INDENT_STYLE_CLASS,
                        CHILD_ROW_STYLE_CLASS);
        childFilesGroup.setDescription(MessageBundleLoader.getMessage("bean.expandTable.parentGroup1.childGroup1.description"));
        childFilesGroup.setModified(MessageBundleLoader.getMessage("bean.expandTable.parentGroup1.childGroup1.modified"));
        childFilesGroup.setCreated(MessageBundleLoader.getMessage("bean.expandTable.parentGroup1.childGroup1.created"));
        childFilesGroup.setSize(MessageBundleLoader.getMessage("bean.expandTable.parentGroup1.childGroup1.size"));
        childFilesGroup.setKind(MessageBundleLoader.getMessage("bean.expandTable.parentGroup1.childGroup1.kind"));
        childFilesGroup.setVersion(MessageBundleLoader.getMessage("bean.expandTable.parentGroup1.childGroup1.version"));
        filesRecordGroup.addChildFilesGroupRecord(childFilesGroup);
        childFilesGroup =
                new FilesGroupRecordBean(CHILD_INDENT_STYLE_CLASS,
                        CHILD_ROW_STYLE_CLASS);
        childFilesGroup.setDescription(MessageBundleLoader.getMessage("bean.expandTable.parentGroup1.childGroup2.description"));
        childFilesGroup.setModified(MessageBundleLoader.getMessage("bean.expandTable.parentGroup1.childGroup2.modified"));
        childFilesGroup.setCreated(MessageBundleLoader.getMessage("bean.expandTable.parentGroup1.childGroup2.created"));
        childFilesGroup.setSize(MessageBundleLoader.getMessage("bean.expandTable.parentGroup1.childGroup2.size"));
        childFilesGroup.setKind(MessageBundleLoader.getMessage("bean.expandTable.parentGroup1.childGroup2.kind"));
        childFilesGroup.setVersion(MessageBundleLoader.getMessage("bean.expandTable.parentGroup1.childGroup2.version"));
        filesRecordGroup.addChildFilesGroupRecord(childFilesGroup);

        // Project Development's group
        filesRecordGroup =
                new FilesGroupRecordBean(GROUP_INDENT_STYLE_CLASS,
                        GROUP_ROW_STYLE_CLASS,
                        styleBean,
                        EXPAND_IMAGE, CONTRACT_IMAGE,
                        inventoryGroupItemBeans, true);
        filesRecordGroup.setDescription(MessageBundleLoader.getMessage("bean.expandTable.parentGroup2.description"));
        filesRecordGroup.setModified(MessageBundleLoader.getMessage("bean.expandTable.parentGroup2.modified"));
        filesRecordGroup.setCreated(MessageBundleLoader.getMessage("bean.expandTable.parentGroup2.created"));
        filesRecordGroup.setSize(MessageBundleLoader.getMessage("bean.expandTable.parentGroup2.size"));
        filesRecordGroup.setKind(MessageBundleLoader.getMessage("bean.expandTable.parentGroup2.kind"));
        filesRecordGroup.setVersion(MessageBundleLoader.getMessage("bean.expandTable.parentGroup2.version"));
        // add Project Development's children
        childFilesGroup =
                new FilesGroupRecordBean(CHILD_INDENT_STYLE_CLASS,
                        CHILD_ROW_STYLE_CLASS);
        childFilesGroup.setDescription(MessageBundleLoader.getMessage("bean.expandTable.parentGroup2.childGroup1.description"));
        childFilesGroup.setModified(MessageBundleLoader.getMessage("bean.expandTable.parentGroup2.childGroup1.modified"));
        childFilesGroup.setCreated(MessageBundleLoader.getMessage("bean.expandTable.parentGroup2.childGroup1.created"));
        childFilesGroup.setSize(MessageBundleLoader.getMessage("bean.expandTable.parentGroup2.childGroup1.size"));
        childFilesGroup.setKind(MessageBundleLoader.getMessage("bean.expandTable.parentGroup2.childGroup1.kind"));
        childFilesGroup.setVersion(MessageBundleLoader.getMessage("bean.expandTable.parentGroup2.childGroup1.version"));
        filesRecordGroup.addChildFilesGroupRecord(childFilesGroup);
        childFilesGroup =
                new FilesGroupRecordBean(CHILD_INDENT_STYLE_CLASS,
                        CHILD_ROW_STYLE_CLASS);
        childFilesGroup.setDescription(MessageBundleLoader.getMessage("bean.expandTable.parentGroup2.childGroup2.description"));
        childFilesGroup.setModified(MessageBundleLoader.getMessage("bean.expandTable.parentGroup2.childGroup2.modified"));
        childFilesGroup.setCreated(MessageBundleLoader.getMessage("bean.expandTable.parentGroup2.childGroup2.created"));
        childFilesGroup.setSize(MessageBundleLoader.getMessage("bean.expandTable.parentGroup2.childGroup2.size"));
        childFilesGroup.setKind(MessageBundleLoader.getMessage("bean.expandTable.parentGroup2.childGroup2.kind"));
        childFilesGroup.setVersion(MessageBundleLoader.getMessage("bean.expandTable.parentGroup2.childGroup2.version"));
        filesRecordGroup.addChildFilesGroupRecord(childFilesGroup);
        childFilesGroup =
                new FilesGroupRecordBean(CHILD_INDENT_STYLE_CLASS,
                        CHILD_ROW_STYLE_CLASS);
        childFilesGroup.setDescription(MessageBundleLoader.getMessage("bean.expandTable.parentGroup2.childGroup3.description"));
        childFilesGroup.setModified(MessageBundleLoader.getMessage("bean.expandTable.parentGroup2.childGroup3.modified"));
        childFilesGroup.setCreated(MessageBundleLoader.getMessage("bean.expandTable.parentGroup2.childGroup3.created"));
        childFilesGroup.setSize(MessageBundleLoader.getMessage("bean.expandTable.parentGroup2.childGroup3.size"));
        childFilesGroup.setKind(MessageBundleLoader.getMessage("bean.expandTable.parentGroup2.childGroup3.kind"));
        childFilesGroup.setVersion(MessageBundleLoader.getMessage("bean.expandTable.parentGroup2.childGroup3.version"));
        filesRecordGroup.addChildFilesGroupRecord(childFilesGroup);

        // Training Course's group
        filesRecordGroup =
                new FilesGroupRecordBean(GROUP_INDENT_STYLE_CLASS,
                        GROUP_ROW_STYLE_CLASS,
                        styleBean,
                        EXPAND_IMAGE, CONTRACT_IMAGE,
                        inventoryGroupItemBeans, false);
        filesRecordGroup.setDescription(MessageBundleLoader.getMessage("bean.expandTable.parentGroup3.description"));
        filesRecordGroup.setModified(MessageBundleLoader.getMessage("bean.expandTable.parentGroup3.modified"));
        filesRecordGroup.setCreated(MessageBundleLoader.getMessage("bean.expandTable.parentGroup3.created"));
        filesRecordGroup.setSize(MessageBundleLoader.getMessage("bean.expandTable.parentGroup3.size"));
        filesRecordGroup.setKind(MessageBundleLoader.getMessage("bean.expandTable.parentGroup3.kind"));
        filesRecordGroup.setVersion(MessageBundleLoader.getMessage("bean.expandTable.parentGroup3.version"));
        // add Training Course's children
        childFilesGroup =
                new FilesGroupRecordBean(CHILD_INDENT_STYLE_CLASS,
                        CHILD_ROW_STYLE_CLASS);
        childFilesGroup.setDescription(MessageBundleLoader.getMessage("bean.expandTable.parentGroup3.childGroup1.description"));
        childFilesGroup.setModified(MessageBundleLoader.getMessage("bean.expandTable.parentGroup3.childGroup1.modified"));
        childFilesGroup.setCreated(MessageBundleLoader.getMessage("bean.expandTable.parentGroup3.childGroup1.created"));
        childFilesGroup.setSize(MessageBundleLoader.getMessage("bean.expandTable.parentGroup3.childGroup1.size"));
        childFilesGroup.setKind(MessageBundleLoader.getMessage("bean.expandTable.parentGroup3.childGroup1.kind"));
        childFilesGroup.setVersion(MessageBundleLoader.getMessage("bean.expandTable.parentGroup3.childGroup1.version"));
        filesRecordGroup.addChildFilesGroupRecord(childFilesGroup);

    }

    /**
     * Cleans up the resources used by this class.  This method could be called
     * when a session destroyed event is called.
     */
    public void dispose() {
        isInit = false;
        // clean up the array list
        if (inventoryGroupItemBeans != null) {
            FilesGroupRecordBean tmp;
            ArrayList tmpList;
            for (int i = 0; i < inventoryGroupItemBeans.size(); i++) {
                tmp = (FilesGroupRecordBean) inventoryGroupItemBeans.get(i);
                tmpList = tmp.getChildFilesRecords();
                if (tmpList != null) {
                    tmpList.clear();
                }
            }
            inventoryGroupItemBeans.clear();
        }
    }

    /**
     * Gets the list of FilesGroupRecordBean which will be used by the
     * ice:dataTable component.
     *
     * @return array list of parent FilesGroupRecordBeans
     */
    public ArrayList getFilesGroupRecordBeans() {
        return inventoryGroupItemBeans;
    }
}