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
package org.icefaces.sample.location;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.event.ValueChangeEvent;

import com.icesoft.faces.context.effects.Effect;
import com.icesoft.faces.context.effects.Highlight;

import java.io.Serializable;

/**
 * <p>The BaseBean is a nice little helper class for common functionality
 * accross the component examples.  The BaseBean or the notion of a base
 * bean is handy in most application as it can provice commonality for logging,
 * init and dispose methods as well as references to Service lookup
 * mechanism. </p>
 *
 * <p>The valueChangeEffect is used by most example beans to highlight
 * changes in backing bean values that are reflected on the client side.</p>
 *
 * @since 1.7
 */
public class BaseBean implements Serializable {
    //the logger for this class
	protected final Log logger = LogFactory.getLog(this.getClass());

    // effect that shows a value binding chance on there server
    protected Effect valueChangeEffect;

    public BaseBean() {
        valueChangeEffect = new Highlight("#fda505");
        valueChangeEffect.setFired(true);
    }

    /**
     * Resets the valueChange effect to fire when the current response
     * is completed.
     *
     * @param event jsf action event
     */
    public void effectChangeListener(ValueChangeEvent event){
        valueChangeEffect.setFired(false);
    }

    /**
	 * Used to initialize the managed bean.
	 */
	protected void init() {

    }

    public Effect getValueChangeEffect() {
        return valueChangeEffect;
    }

    public void setValueChangeEffect(Effect valueChangeEffect) {
        this.valueChangeEffect = valueChangeEffect;
    }
}
