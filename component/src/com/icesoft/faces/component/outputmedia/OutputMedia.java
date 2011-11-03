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
package com.icesoft.faces.component.outputmedia;

import com.icesoft.faces.component.ext.taglib.Util;

import javax.faces.component.UIComponentBase;
import javax.faces.el.ValueBinding;
import javax.faces.context.FacesContext;

public class OutputMedia extends UIComponentBase {
    private String codebase;
    private String mimeType;
    private String player;
    private String renderedOnUserRole;
    private String source;
    private String standbyText;
    private String style;
    private String styleClass;

    public OutputMedia() {
        setRendererType("com.icesoft.faces.OutputMedia");
    }

    public String getFamily() {
        return "com.icesoft.faces.OutputMedia";
    }

    public String getSource() {
        return (String) getAttribute("source", source, null);
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getStyle() {
        return (String) getAttribute("style", style, null);
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getStyleClass() {
        return (String) getAttribute("styleClass", styleClass, null);
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public String getStandbyText() {
        return (String) getAttribute("standbyText", standbyText, null);
    }

    public void setStandbyText(String standbyText) {
        this.standbyText = standbyText;
    }

    public String getCodebase() {
        return (String) getAttribute("codebase", codebase, null);
    }

    public void setCodebase(String codebase) {
        this.codebase = codebase;
    }

    public String getMimeType() {
        return (String) getAttribute("mimeType", mimeType, null);
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getPlayer() {
        return (String) getAttribute("player", player, null);
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getRenderedOnUserRole() {
        return (String) getAttribute("renderedOnUserRole", renderedOnUserRole, null);
    }

    public void setRenderedOnUserRole(String renderedOnUserRole) {
        this.renderedOnUserRole = renderedOnUserRole;
    }

    private Object getAttribute(String name, Object localValue, Object defaultValue) {
        if (localValue != null) return localValue;
        ValueBinding vb = getValueBinding(name);
        if (vb == null) return defaultValue;
        Object value = vb.getValue(getFacesContext());
        if (value == null) return defaultValue;
        return value;
    }

    public boolean isRendered() {
        return super.isRendered() && Util.isRenderedOnUserRole(this);
    }

    public Object saveState(FacesContext context) {
        return new Object[]{
                super.saveState(context),
                codebase,
                mimeType,
                player,
                renderedOnUserRole,
                source,
                standbyText,
                style,
                styleClass,
        };
    }

    public void restoreState(FacesContext context, Object state) {
        String[] attrNames = {
                "codebase",
                "mimeType",
                "player",
                "renderedOnUserRole",
                "source",
                "standbyText",
                "style",
                "styleClass",
        };
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        for (int i = 0; i < attrNames.length; i++) {
            getAttributes().put(attrNames[i], values[i + 1]);
        }
    }
}
