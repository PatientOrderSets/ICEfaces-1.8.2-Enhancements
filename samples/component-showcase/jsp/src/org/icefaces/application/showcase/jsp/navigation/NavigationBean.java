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

package org.icefaces.application.showcase.jsp.navigation;

import javax.faces.event.ActionEvent;
import javax.faces.context.FacesContext;
import java.util.Map;

/**
 * <p>The NavigationBean class is responsible for storing the state of the two
 * panel stacks which display dynamic content.  </p>
 *
 * @since 0.3.0
 */
public class NavigationBean {

    // selected page content bean.
    private String selectedPanel = "splashPanel";

    /**
     * Gets the currently selected content panel.
     *
     * @return currently selected content panel.
     */
    public String getSelectedPanel() {
        return selectedPanel;
    }

    /**
     * Sets the selected panel to the specified panel.
     *
     * @param selectedPanel a none null page content bean.
     */
    public void setSelectedPanel(String selectedPanel) {
        this.selectedPanel = selectedPanel;
    }

    /**
     * Action Listener for the changes the selected panel stack in the
     * jsp version of component showcase.
     *
     * @param event JSF Action Event.
     */
    public void navigationStackChange(ActionEvent event){

        // get from the context panel stack item to show as well
        // as the title associated with the link.
        FacesContext context = FacesContext.getCurrentInstance();
        Map map = context.getExternalContext().getRequestParameterMap();
        selectedPanel = (String) map.get("stackName");
    }

}
