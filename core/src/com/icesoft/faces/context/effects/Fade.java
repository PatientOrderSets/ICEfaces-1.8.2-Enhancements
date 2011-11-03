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
 * The Fade effect will transition an HTML element from one opacity  to another.
 * By default it will start at 100% and transition to invisible.
 */
public class Fade extends Effect {
    private float from = 1.0f;
    private float to = 0.0f;

    /**
     * Default. from 1.0 to 0.0
     */
    public Fade() {
        ea.add("from", from);
        ea.add("to", to);
    }

    /**
     * @param from Starting opacity
     * @param to   end opacity
     */
    public Fade(float from, float to) {
        setFrom(from);
        setTo(to);
    }

    /**
     * Get the starting opacity
     *
     * @return
     */
    public float getFrom() {
        return from;
    }

    /**
     * Set the starting opacity
     *
     * @param from
     */
    public void setFrom(float from) {
        this.from = from;
        ea.add("from", from);
    }

    /**
     * Get the ending opacity
     *
     * @return
     */
    public float getTo() {
        return to;
    }

    /**
     * Set the ending opacity
     *
     * @param to
     */
    public void setTo(float to) {
        this.to = to;
        ea.add("to", to);
    }

    /**
     * Get the Javascript function name
     *
     * @return
     */
    public String getFunctionName() {
        return "Effect.Fade";
    }

    public int hashCode() {
        int from = (int) (this.from * 100);
        int to = (int) (this.to * 100);
        return EffectHashCode.FADE * from * to;
    }

    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof Fade)) {
            return false;
        }
        Fade effect = (Fade) obj;
        if (from != effect.from) {
            return false;
        }
        if (to != effect.to) {
            return false;
        }
        return true;
    }
}
