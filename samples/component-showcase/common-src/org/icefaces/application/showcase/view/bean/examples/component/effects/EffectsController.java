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
package org.icefaces.application.showcase.view.bean.examples.component.effects;

import org.icefaces.application.showcase.util.FacesUtils;
import org.icefaces.application.showcase.view.bean.BeanNames;

import javax.faces.event.ActionEvent;
import java.io.Serializable;

/**
 * <p>The effects controller is responsible for firing finding and firing
 * an effect associated with an commandLink component.  This bean is
 * in application scope to try and reduce the amount of object creation for
 * the application demos in general</p>
 *
 * @sinse 1.7
 */
public class EffectsController implements Serializable {

    /**
     * <p>Gets an instance of the BeanNames.EFFECTS_MODEL that lives in request
     * scope and sets the current effect based on a request param.  The
     * request param is named "effectKey".  The request param is a valid key
     * to an effect that exists in Map. The respective effect is then set
     * as the current effect in the session model bean. </p>
     *
     * @param event JSF action event.
     */
    public void changeEffectAction(ActionEvent event) {

        // get id of effect action, via param
        String effectKey = FacesUtils.getRequestParameter("effectKey");

        // get the callers session effects data model
        EffectsModel effectsModel =
                (EffectsModel) FacesUtils.getManagedBean(
                        BeanNames.EFFECTS_MODEL);

        // do a look up for the effect
        EffectsModel.EffectWrapper effectWrapper = (EffectsModel.EffectWrapper)
                effectsModel.getEffects().get(effectKey);

        // if found we reset the effect to fire on the soon to occure
        // response. 
        if (effectWrapper != null) {
            effectWrapper.getEffect().setFired(false);
            effectsModel.setCurrentEffecWrapper(effectWrapper);
        }

    }
}
