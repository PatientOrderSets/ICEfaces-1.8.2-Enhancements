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

package org.icefaces.application.showcase.view.bean.examples.component.buttonsAndLinks;

import org.icefaces.application.showcase.util.MessageBundleLoader;
import org.icefaces.application.showcase.view.bean.BaseBean;

import javax.faces.event.ActionEvent;

/**
 * <p>The ButtonsAndLinksBean class is the backing bean for the buttons and
 * links demonstration. It is used to store the input submitted by buttons and
 * links. It also has action listeners to record which button or link was clicked.</p>
 */
public class ButtonsAndLinksBean extends BaseBean {

    /**
     * Variables to store the button clicked and the input submitted.
     */
    private String clicked;
    private String inputText;

    /**
     * Gets the name of the button clicked.
     *
     * @return name of the button clicked.
     */
    public String getClicked() {
        return MessageBundleLoader.getMessage(clicked);
    }

    /**
     * Gets the value of the input submitted.
     *
     * @return the value of the input submitted.
     */
    public String getInputText() {
        return inputText;
    }

    /**
     * Sets the input text value.
     *
     * @param newValue input text value.
     */
    public void setInputText(String newValue) {
        inputText = newValue;
    }

    /**
     * Listener for the submit button click action.
     *
     * @param e click action event.
     */
    public void submitButtonListener(ActionEvent e) {
        clicked = "bean.buttonsAndLinks.submitButton";
        valueChangeEffect.setFired(false);
    }

    /**
     * Listener for the image button click action.
     *
     * @param e click action event.
     */
    public void imageButtonListener(ActionEvent e) {
        clicked = "bean.buttonsAndLinks.imageButton";
        valueChangeEffect.setFired(false);
    }

    /**
     * Listener for the command link click action.
     *
     * @param e click action event.
     */
    public void commandLinkListener(ActionEvent e) {
        clicked = "bean.buttonsAndLinks.commandLink";
        valueChangeEffect.setFired(false);
    }
}
