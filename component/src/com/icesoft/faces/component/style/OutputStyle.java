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

package com.icesoft.faces.component.style;

import javax.faces.component.UIComponentBase;
import javax.faces.el.ValueBinding;
import javax.faces.context.FacesContext;

/**
 * The OutputStyle component will include an additional style sheet for Internet
 * Explorer and Safari browsers. An additional style sheet will also be included
 * when rendered in Sun Studio Creator design time. This allows style classes to
 * be overridden for specific browsers.
 */
public class OutputStyle extends UIComponentBase {

    public static final String COMPONENT_TYPE =
            "com.icesoft.faces.OutputStyleComp";
    public static final String COMPONENT_FAMILY =
            "com.icesoft.faces.OutputStyle";
    public static final String DEFAULT_RENDERER_TYPE =
            "com.icesoft.faces.style.OutputStyleRenderer";

    /**
     * The href value of the link element that is rendered. An additional link
     * ellement is rendered for internet explorer and Safari browsers. An
     * additional style sheet is specifed when in design time in Studio Creator.
     * The IE style sheet must end with '_ie.css', and the Safari style sheet
     * must end with '_safari.css'. Design Time is '_dt.css' For example if the
     * href value is 'style.css' then the IE style sheet needs to be named
     * 'style_ie.css'
     */
    private String href;
    private String userAgent;

    public OutputStyle() {
        super();
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getRendererType() {
        return DEFAULT_RENDERER_TYPE;
    }

    /**
     * Returns the href value of the link element that is rendered.
     */
    public String getHref() {
        if (href != null) {
            return href;
        }
        ValueBinding vb = getValueBinding("href");
        if (vb != null) {
            return (String) vb.getValue(getFacesContext());
        }
        return null;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    /**
     * Sets the href value of the link element that is rendered.
     * <p/>
     * The href value of the link element that is rendered. An additional link
     * element is rendered for internet explorer and Safari browsers. An
     * additional style sheet is specifed when in design time in Studio Creator.
     * The IE style sheet must end with '_ie.css', and the Safari style sheet
     * must end with '_safari.css'. Design Time is '_dt.css' For example if the
     * href value is 'style.css' then the IE style sheet needs to be named
     * 'style_ie.css' </p>
     */
    public void setHref(String href) {
        this.href = href;
    }

    /**
     * <p>Gets the state of the instance as a <code>Serializable</code>
     * Object.</p>
     */
    public Object saveState(FacesContext context) {
        Object values[] = new Object[3];
        values[0] = super.saveState(context);
        values[1] = href;
        values[2] = userAgent;
        return ((Object) (values));
    }

    /**
     * <p>Perform any processing required to restore the state from the entries
     * in the state Object.</p>
     */
    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        href = (String) values[1];
        userAgent = (String) values[2];
    }
}
