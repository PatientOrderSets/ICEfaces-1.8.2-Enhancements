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

package com.icesoft.faces.webapp.parser;

import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.webapp.UIComponentTag;
import javax.servlet.jsp.tagext.Tag;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

public class IceOutputTextTag extends UIComponentTag {
    private static Set noSpanTagSet = new HashSet(Arrays.asList(new String[]{"script", "style", "title"}));
    private String value;

    public String getRendererType() {
        return "javax.faces.Text";
    }

    public String getComponentType() {
        return "javax.faces.Output";
    }

    protected void setProperties(UIComponent comp) {
        super.setProperties(comp);
        UIOutput component = (UIOutput) comp;
        component.setValue(value);
        component.setRendererType("com.icesoft.faces.PlainText");

        // don't escape plain text
        component.getAttributes().put("escape", "false");

        Tag parentTag = getParent();
        if (parentTag instanceof XhtmlTag && noSpanTagSet.contains(((XhtmlTag) parentTag).getTagName().toLowerCase())) {
            component.getAttributes().put("nospan", Boolean.TRUE);
        }
    }

    /**
     * Value setter.
     *
     * @param val value of the output.
     */
    public void setValue(String val) {
        value = val;
    }

    /**
     * Value getter.
     *
     * @return value
     */
    public String getValue() {
        return value;
    }
}
