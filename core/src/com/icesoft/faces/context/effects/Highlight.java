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

import com.icesoft.faces.util.CoreUtils;

/**
 * The highlight effect will change the background color of an HTML element and
 * then transition the color back its original state
 */
public class Highlight extends Effect {
    private String startColor;

    /**
     * Default is #ffff99
     */
    public Highlight() {
        setStartColor("#ffff99");
    }

    /**
     * Set the highlight color
     *
     * @param s The RGB color to highlight to. Example: #ff00ff
     */
    public Highlight(String s) {
        setStartColor(s);
    }

    /**
     * Get the starting (highlight) color
     *
     * @return the highlight color
     */
    public String getStartColor() {
        return startColor;
    }

    /**
     * Set the starting (highlight) color
     *
     * @param startColor
     */
    public void setStartColor(String startColor) {
        this.startColor = startColor;
        ea.add("startcolor", startColor);
    }

    /**
     * The Javascript function name
     *
     * @return
     */
    public String getFunctionName() {

        return "new Effect.Highlight";
    }

    public int hashCode() {
        int value = 0;
        char[] ca = startColor.toCharArray();
        for (int i = 1; i < ca.length; i++) {
            value += (int) ca[i] + i;
        }
        return EffectHashCode.HIGHLIGHT * value;
    }

    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof Highlight)) {
            return false;
        }
        Highlight effect = (Highlight) obj;
        if (!CoreUtils.objectsEqual(startColor, effect.startColor)) {
            return false;
        }
        return true;
    }
}
