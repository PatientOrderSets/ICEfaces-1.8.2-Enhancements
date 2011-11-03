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

package org.icefaces.application.showcase.view.bean.examples.layoutPanel.panelBorder;

import javax.faces.event.ValueChangeEvent;
import java.io.Serializable;

/**
 * <p>The PanelBorderBean class store boolean visibility values for the five
 * visible areas of the BorderLayout component.  Users can change the visibilty
 * of the different visable areas and see the component update for the
 * respective visibility change. </p>
 * <p>Sizing of the BorderLayout areas is controlled by CSS style sheet rules</p>
 *
 * @since 0.3.0
 */
public class PanelBorderBean implements Serializable {

    // five configurable areas that make up a BorderLayout
    private boolean renderNorth = true;
    private boolean renderSouth = true;
    private boolean renderCenter = true;
    private boolean renderEast = true;
    private boolean renderWest = true;

    /**
     * event handler for the north border.
     *
     * @param event the value change event.
     */
    public void north(ValueChangeEvent event) {
        setRenderNorth(((Boolean) event.getNewValue()).booleanValue());
    }


    /**
     * event handler for the south border.
     *
     * @param event the value change event.
     */
    public void south(ValueChangeEvent event) {
        setRenderSouth(((Boolean) event.getNewValue()).booleanValue());
    }

    /**
     * event handler for the center border.
     *
     * @param event the value change event.
     */
    public void center(ValueChangeEvent event) {
        setRenderCenter(((Boolean) event.getNewValue()).booleanValue());
    }

    /**
     * event handler for the east border.
     *
     * @param event the value change event.
     */
    public void east(ValueChangeEvent event) {
        setRenderEast(((Boolean) event.getNewValue()).booleanValue());
    }

    /**
     * event handler for the west border.
     *
     * @param event the value change event.
     */
    public void west(ValueChangeEvent event) {
        setRenderWest(((Boolean) event.getNewValue()).booleanValue());
    }

    public boolean isRenderCenter() {
        return renderCenter;
    }

    public void setRenderCenter(boolean renderCenter) {
        this.renderCenter = renderCenter;
    }

    public boolean isRenderEast() {
        return renderEast;
    }

    public void setRenderEast(boolean renderEast) {
        this.renderEast = renderEast;
    }

    public boolean isRenderNorth() {
        return renderNorth;
    }

    public void setRenderNorth(boolean renderNorth) {
        this.renderNorth = renderNorth;
    }

    public boolean isRenderSouth() {
        return renderSouth;
    }

    public void setRenderSouth(boolean renderSouth) {
        this.renderSouth = renderSouth;
    }

    public boolean isRenderWest() {
        return renderWest;
    }

    public void setRenderWest(boolean renderWest) {
        this.renderWest = renderWest;
    }
}
