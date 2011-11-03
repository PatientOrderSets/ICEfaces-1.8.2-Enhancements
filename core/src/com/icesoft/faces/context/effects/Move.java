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
 * Move an HTML element to a new position. Moves can be absolute or relative.
 * Relative moves an element from it current position, absolute moves it from
 * its begging position. (Here the HTML initially  rendered it)
 */
public class Move extends Effect {
    private int x;
    private int y;
    private String mode;


    public Move(){}
    /**
     * Move an element to a new position
     *
     * @param x    or left location
     * @param y    or top location
     * @param mode can be relative or absolute
     */
    public Move(int x, int y, String mode) {
        setX(x);
        setY(y);
        setMode(mode);
    }

    /**
     * Move an element to a new position. Mode is relative
     *
     * @param x or left location
     * @param y or top location
     */
    public Move(int x, int y) {
        setX(x);
        setY(y);
        setMode("relative");
    }

    /**
     * Get the X or left end position
     *
     * @return
     */
    public int getX() {
        return x;
    }

    /**
     * Set the X or left end position
     *
     * @param x
     */
    public void setX(int x) {
        this.x = x;
        ea.add("x", x);
    }

    /**
     * Get the Y or top position
     *
     * @return
     */
    public int getY() {
        return y;
    }

    /**
     * Set the Y or top position
     *
     * @param y
     */
    public void setY(int y) {
        this.y = y;
        ea.add("y", y);
    }

    /**
     * Get the mode of the move (absolute or relative)
     *
     * @return
     */
    public String getMode() {
        return mode;
    }

    /**
     * Set the mode (absolute or realitve)
     *
     * @param mode
     */
    public void setMode(String mode) {
        this.mode = mode;
        ea.add("mode", mode);
    }

    /**
     * The javascript function name
     *
     * @return
     */
    public String getFunctionName() {
        return "new Effect.Move";
    }

    public int hasCode() {
        return EffectHashCode.MOVE * (x * 1) * (y * 2) +
               ("relative".equals(mode) ? 1 : 2);
    }
    
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof Move)) {
            return false;
        }
        Move effect = (Move) obj;
        if (x != effect.x) {
            return false;
        }
        if (y != effect.y) {
            return false;
        }
        if (!CoreUtils.objectsEqual(mode, effect.mode)) {
            return false;
        }
        return true;
    }
}
