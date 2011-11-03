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

package com.icesoft.faces.component.menubar;

import javax.swing.tree.DefaultMutableTreeNode;

public class IceMenuObject {

    protected DefaultMutableTreeNode wrapper;
    protected String text;
    protected boolean expanded;
    protected String tooltip;
    protected String action;

    // icon fields
    protected String leafIcon;
    protected String branchExpandedIcon;
    protected String branchContractedIcon;
    protected String icon;

    //constructors
    public IceMenuObject(DefaultMutableTreeNode wrapper) {
        this.wrapper = wrapper;
    }

    public String action() {
        return action;
    }


    // getters/setters
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFamily() {
        return null;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean isExpanded) {
        this.expanded = isExpanded;
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltipString) {
        this.tooltip = tooltipString;
    }

    public String toString() {
        return text;
    }

    public String getLeafIcon() {
        return leafIcon;
    }

    public void setLeafIcon(String leafIcon) {
        this.leafIcon = leafIcon;
    }

    public String getBranchContractedIcon() {
        return branchContractedIcon;
    }

    public void setBranchContractedIcon(String branchContractedIcon) {
        this.branchContractedIcon = branchContractedIcon;
    }

    public String getBranchExpandedIcon() {
        return branchExpandedIcon;
    }

    public void setBranchExpandedIcon(String branchExpandedIcon) {
        this.branchExpandedIcon = branchExpandedIcon;
    }

    /**
     * return the appropriate icon based on this node's expanded/collapsed state
     * and the presence of children
     *
     * @return String application-relative path to the image file
     */
    public String getIcon() {
        if (wrapper.getChildCount() <= 0) {
            if (leafIcon != null) {
                return leafIcon;
            }
        } else if (isExpanded()) {
            if (branchExpandedIcon != null) {
                return branchExpandedIcon;
            }
        } else {
            if (branchContractedIcon != null) {
                return branchContractedIcon;
            }
        }
        return icon;
    }

    public DefaultMutableTreeNode getWrapper() {
        return wrapper;
    }

    /**
     * Set the DefaultMutableTreeNode instance that wraps this instance
     *
     * @param wrapper
     */
    public void setWrapper(DefaultMutableTreeNode wrapper) {
        this.wrapper = wrapper;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}