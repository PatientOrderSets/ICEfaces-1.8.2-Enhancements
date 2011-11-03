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

/**
 * <p>The TabState class stores tabSet related state data.  The
 * {@link org.icefaces.application.showcase.view.bean.ApplicationSessionModel}
 * class contains a lazily loaded hashMap which contains the a one to
 * one mapping of Node objects to TabSate objects.  When a user clicks on
 * a navigation node the TabState object is either created or loaded and tabSet
 * component reflects what ever state was defined in the TabState object. </p>
 *
 * @sinse 1.7
 */
public class TabState {

    public static final int DEMONSTRATION_TAB_INDEX = 0;
    public static final int DOCUMENT_TAB_INDEX = 1;
    public static final int SOURCE_TAB_INDEX = 2;

    // currently selected tab index
    private int tabIndex;
    
    // relative path to currently selected documentation, if any. 
    private String descriptionContent;

    // relative path to currently selected source code, if any.
    private String sourceContent;

    public int getTabIndex() {
        return tabIndex;
    }

    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
    }

    public String getDescriptionContent() {
        return descriptionContent;
    }

    public void setDescriptionContent(String descriptionContent) {
        this.descriptionContent = descriptionContent;
    }

    public String getSourceContent() {
        return sourceContent;
    }

    public void setSourceContent(String sourceContent) {
        this.sourceContent = sourceContent;
    }
}
