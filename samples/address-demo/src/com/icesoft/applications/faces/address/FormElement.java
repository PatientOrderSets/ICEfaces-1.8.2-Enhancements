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
 * FormElement is the superclass for all form elements, excepting the reset
 * button. FormElement maintains the value of the field and the status image
 * (blank, alert, or progress). For a 'plain' form element (first name, last
 * name), a custom converter is used to fix the capitalization.
 *
 * @see WordCapitalizationConverter
 */
public class FormElement {

    //basic state information for the component
    protected String value, image;
    protected boolean set;

    //status images
    public static final String IMAGE_ALERT = "status_alert.gif";
    public static final String IMAGE_BLANK = "status_blank.gif";
    public static final String IMAGE_PROGRESS = "status_blank.gif";


    /**
     * FormElement constructor sets the default value to "" instead of null.
     */
    public FormElement() {
        setValue("");
    }

    /**
     * Resets the element to an empty string.
     */
    public void reset() {
        setValue("");
    }

    /**
     * Gets the element value.
     *
     * @return the element value
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Sets the element value. If the value is non-null the set flag is marked
     * true; If the value is null it is set to "", the set flag is marked false,
     * and the element image is set to blank.
     *
     * @param value The value to be stored.
     */
    public void setValue(String value) {

        if (value != null) {

            if (value.length() > 0) {
                this.value = value.trim();
                set = true;
                return;
            }
        }

        this.value = "";
        setImage(IMAGE_BLANK);
        set = false;
    }

    /**
     * Determine whether the element is set or not.
     *
     * @return the element's set status
     * @see #setValue(String)
     */
    public boolean getIsSet() {
        return this.set;
    }

    /**
     * Get the element's status image. These will display next to the element on
     * the form page.
     *
     * @return the element's image.
     */
    public String getImage() {
        return this.image;
    }

    /**
     * Set the element's status image.
     *
     * @param image the status image to set
     */
    public void setImage(String image) {
        this.image = image;
    }
}
