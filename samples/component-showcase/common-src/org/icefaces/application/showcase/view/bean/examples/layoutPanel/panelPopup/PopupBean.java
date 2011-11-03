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

package org.icefaces.application.showcase.view.bean.examples.layoutPanel.panelPopup;

import org.icefaces.application.showcase.util.MessageBundleLoader;

import javax.faces.event.ActionEvent;
import java.io.Serializable;

/**
 * <p>The PopupBean class is the backing bean that manages the Popup Panel
 * state.</p>
 * <p>This includes the modal and draggable user configurable message, as well
 * as the rendered and visibility state.</p>
 */
public class PopupBean implements Serializable {
    // user entered messages for both dialogs
    private String draggableMessage = MessageBundleLoader.getMessage("page.panelPopup.defaultDraggableMessage");
    private String modalMessage = MessageBundleLoader.getMessage("page.panelPopup.defaultModalMessage");
    // render flags for both dialogs
    private boolean draggableRendered = false;
    private boolean modalRendered = false;
    // if we should use the auto centre attribute on the draggable dialog
    private boolean autoCentre = false;

    public String getDraggableMessage() {
        return draggableMessage;
    }

    public void setDraggableMessage(String draggableMessage) {
        this.draggableMessage = draggableMessage;
    }

    public String getModalMessage() {
        return modalMessage;
    }

    public void setModalMessage(String modalMessage) {
        this.modalMessage = modalMessage;
    }

    public boolean isDraggableRendered() {
        return draggableRendered;
    }

    public void setDraggableRendered(boolean draggableRendered) {
        this.draggableRendered = draggableRendered;
    }

    public boolean getModalRendered() {
        return modalRendered;
    }

    public void setModalRendered(boolean modalRendered) {
        this.modalRendered = modalRendered;
    }
    
    public boolean getAutoCentre() {
        return autoCentre;
    }

    public void setAutoCentre(boolean autoCentre) {
        this.autoCentre = autoCentre;
    }

    public String getDetermineDraggableButtonText() {
        return MessageBundleLoader.getMessage("page.panelPopup.show."
                + draggableRendered);
    }

    public String getDetermineModalButtonText() {
        return MessageBundleLoader.getMessage("page.panelPopup.show."
                + modalRendered);
    }

    public void toggleDraggable(ActionEvent event) {
        draggableRendered = !draggableRendered;
    }

    public void toggleModal(ActionEvent event) {
        modalRendered = !modalRendered;
    }
}
