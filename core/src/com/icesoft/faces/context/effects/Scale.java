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
 * script.aculo.us scale effect
 *
 * Grow or shrink an element by a specifed percent. (Default 50%)
 */
public class Scale extends Effect {

    private boolean scaleX = true;
    private boolean scaleY = true;
    private boolean scaleContent = true;
    private boolean scaleFromCenter = false;
    private String scaleMode = "box";
    private float scaleFrom = 100.0f;
    private float scaleTo = 50.0f;

    public Scale(float to) {
        this.scaleTo = to;
        ea.add("scaleX", scaleX);
        ea.add("scaleY", scaleY);
        ea.add("scaleContent", scaleContent);
        ea.add("scaleFromCenter", scaleFromCenter);
        ea.add("scaleMode", scaleMode);
        ea.add("scaleFrom", scaleFrom);
        ea.add("scaleTo", scaleTo);
    }

    public boolean isScaleX() {
        return scaleX;
    }

    public void setScaleX(boolean scaleX) {
        this.scaleX = scaleX;
        ea.add("scaleX", scaleX);
    }

    public boolean isScaleY() {
        return scaleY;
    }

    public void setScaleY(boolean scaleY) {
        this.scaleY = scaleY;
        ea.add("scaleY", scaleY);
    }

    public boolean isScaleContent() {
        return scaleContent;
    }

    public void setScaleContent(boolean scaleContent) {
        this.scaleContent = scaleContent;
        ea.add("scaleContent", scaleContent);
    }

    public boolean isScaleFromCenter() {
        return scaleFromCenter;
    }

    public void setScaleFromCenter(boolean scaleFromCenter) {
        this.scaleFromCenter = scaleFromCenter;
        ea.add("scaleFromCenter", scaleFromCenter);
    }

    public String getScaleMode() {
        return scaleMode;
    }

    public void setScaleMode(String scaleMode) {
        this.scaleMode = scaleMode;
        ea.add("scaleMode", scaleMode);
    }

    public float getScaleFrom() {
        return scaleFrom;
    }

    public void setScaleFrom(float scaleFrom) {
        this.scaleFrom = scaleFrom;
        ea.add("scaleFrom", scaleFrom);
    }

    public float getScaleTo() {
        return scaleTo;
    }

    public void setScaleTo(float scaleTo) {
        this.scaleTo = scaleTo;
        ea.add("scaleTo", scaleTo);
    }

    public String getFunctionName() {
        return "new Effect.Scale";
    }

    public String toString(String var, String lastCall) {
        if (isQueued()) {
            ea.add("queue", "front");
        }
        if (isQueueEnd()) {
            ea.add("queue", "end");
        }
        if (!isTransitory()) {
            ea.add("uploadCSS", "true");
        }
        if (lastCall != null) {
            ea.addFunction("iceFinish", "function(){" + lastCall + "}");
        }
        return "new Effect.Scale(" + var + ", " + scaleTo + ea.toString();
    }

    public String toString() {
        return toString("id", null);
    }

    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof Scale)) {
            return false;
        }
        Scale effect = (Scale) obj;
        if (scaleX != effect.scaleX) {
            return false;
        }
        if (scaleY != effect.scaleY) {
            return false;
        }
        if (scaleContent != effect.scaleContent) {
            return false;
        }
        if (scaleFromCenter != effect.scaleFromCenter) {
            return false;
        }
        if (!CoreUtils.objectsEqual(scaleMode, effect.scaleMode)) {
            return false;
        }
        if (scaleFrom != effect.scaleFrom) {
            return false;
        }
        if (scaleTo != effect.scaleTo) {
            return false;
        }
        return true;
    }
}
