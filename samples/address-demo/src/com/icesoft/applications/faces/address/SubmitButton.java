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
package com.icesoft.applications.faces.address;

/**
 * Form submit button that activates based on the form content. It requires all
 * of the FormElements in the componentList to be set. Moreover, the city,
 * state, and zip must constitute a valid address in the database. Only when
 * these requirements are met will the SubmitButton activate. These conditions
 * are checked during every JSF lifecycle, via PhaseSync.
 *
 * @see PhaseSync
 */
public class SubmitButton {

    //status
    private boolean status;

    //button images
    public static final String IMAGE_ENABLED =
            "./images/complete-address-button.gif";
    public static final String IMAGE_DISABLED =
            "./images/complete-address-button-dis.gif";


    /**
     * Constructor for SubmitButton defaults the set value to false.
     */
    public SubmitButton() {
        status = false;
    }

    /**
     * Determine whether the submit button is activated.
     *
     * @return the status of the submit button
     */
    public boolean getStatus() {
        return status;
    }

    /**
     * Set the status of the submit button.
     *
     * @param status the new status of the submit button
     * @see PhaseSync
     */
    public void setStatus(boolean status) {
        this.status = status;
    }

    /**
     * Determine whether the submit button is disabled.
     *
     * @return the disabled status of the submit button
     * @see #getStatus()
     */
    public boolean isDisabled() {
        return !status;
    }

    /**
     * Get the image for the submit button.
     *
     * @return the image for the submit button
     */
    public String getImageButton() {

        if (status) return IMAGE_ENABLED;
        else return IMAGE_DISABLED;
    }
}