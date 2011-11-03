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

package com.icesoft.faces.context.effects;

/**
 * The appear effect will transition an HTML element from one opacity  to
 * another. By default if will transition from invisible to 100% opacity.
 */
public class Appear extends Effect {

    private float from = 0.0f;
    private float to = 1.0f;

    /**
     * Default. Transition from invisible to 100%
     */
    public Appear() {
        ea.add("from", from);
        ea.add("to", to);
    }

    /**
     * Transition between two opacities. Max value i 1.0 min value 0.0. 1.0 =
     * 100% 0.0 = 0%
     *
     * @param from
     * @param to
     */
    public Appear(float from, float to) {
        setFrom(from);
        setTo(to);
    }

    /**
     * Get Start opacity
     *
     * @return
     */
    public float getFrom() {
        return from;
    }

    /**
     * Set start opacity
     *
     * @param from
     */
    public void setFrom(float from) {
        this.from = from;
        ea.add("from", from);
    }

    /**
     * Get end opacity
     *
     * @return
     */
    public float getTo() {
        return to;
    }

    /**
     * Set end opacity
     *
     * @param to
     */
    public void setTo(float to) {
        this.to = to;
        ea.add("to", to);
    }

    /**
     * The javascript function call.
     *
     * @return
     */
    public String getFunctionName() {
        return "Effect.Appear";
    }

    public int hashCode() {
        int from = (int) (this.from * 100);
        int to = (int) (this.to * 100);
        return EffectHashCode.APPEAR * from * to;
    }
    
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof Appear)) {
            return false;
        }
        Appear effect = (Appear) obj;
        if (from != effect.from) {
            return false;
        }
        if (to != effect.to) {
            return false;
        }
        return true;
    }
}
