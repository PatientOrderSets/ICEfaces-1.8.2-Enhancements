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

package com.icesoft.faces.component.outputconnectionstatus;

import com.icesoft.faces.context.DOMContext;
import com.icesoft.faces.renderkit.dom_html_basic.DomBasicRenderer;
import com.icesoft.faces.renderkit.dom_html_basic.HTML;
import com.icesoft.faces.util.DOMUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.io.IOException;

import com.icesoft.util.pooling.ClientIdPool;

public class OutputConnectionStatusRenderer extends DomBasicRenderer {

    public void encodeBegin(FacesContext context, UIComponent uiComponent)
            throws IOException {
        validateParameters(context, uiComponent, null);
        OutputConnectionStatus component =
                ((OutputConnectionStatus) uiComponent);
        DOMContext domContext =
                DOMContext.attachDOMContext(context, uiComponent);
        if (!domContext.isInitialized()) {
            String id = uiComponent.getClientId(context);
            Element root = domContext.createRootElement(HTML.DIV_ELEM);
            domContext.setRootNode(root);

            root.setAttribute(HTML.ID_ATTR, id);
            root.setAttribute(HTML.CLASS_ATTR, component.getStyleClass());
            String style = component.getStyle();
            if (style != null && style.length() > 0)
                root.setAttribute(HTML.STYLE_ATTR, style);
            else
                root.removeAttribute(HTML.STYLE_ATTR);

            String idleID = ClientIdPool.get(id + ":connection-idle");
            root.appendChild(getNextNode(domContext,
                    component.getInactiveClass(),
                    component.getInactiveLabel(),
                    idleID, true));
            String workingID = ClientIdPool.get(id + ":connection-working");
            root.appendChild(getNextNode(domContext, component.getActiveClass(),
                    component.getActiveLabel(),
                    workingID, false));
            String troubleID = ClientIdPool.get(id + ":connection-trouble");
            root.appendChild(getNextNode(domContext,
                    component.getCautionClass(),
                    component.getCautionLabel(),
                    troubleID, false));
            String lostID = ClientIdPool.get(id + ":connection-lost");
            root.appendChild(getNextNode(domContext,
                    component.getDisconnectedClass(),
                    component.getDisconnectedLabel(),
                    lostID, false));
            Element script = domContext.createElement(HTML.SCRIPT_ELEM);
            script.setAttribute(HTML.TYPE_ATTR, "text/javascript");
            script.setAttribute(HTML.ID_ATTR, ClientIdPool.get(id + "script"));            
            script.appendChild(domContext.createTextNode(
                    "'" + id + "'.asExtendedElement().findContainerFor('bridge').bridge.attachStatusManager(" +
                            "function(defaultStatusManager) {" +
                            "return new Ice.Status.ComponentStatusManager('" + workingID + "', '" + idleID + "', '" + troubleID + "', '" + lostID + "', defaultStatusManager, " + component.isShowPopupOnDisconnect() + ", " + component.isDisplayHourglassWhenActive() + ");" +
                            "});"
            ));
            root.appendChild(script);
        }

        domContext.stepOver();
    }

    public Element getNextNode(DOMContext domContext, String classString,
                               String label, String id, boolean visible) {
        Element div = (Element) domContext.createElement(HTML.DIV_ELEM);
        div.setAttribute(HTML.ID_ATTR, id);
        div.setAttribute(HTML.CLASS_ATTR, classString);
        if (!visible) {
            div.setAttribute(HTML.STYLE_ATTR, "visibility: hidden;");
        }
        if ( (null == label) || ("".equals(label)) )  {
            return div;
        }
        if (label != null) {
            label = DOMUtils.escapeAnsi(label);
        }
        Text text = (Text) domContext.createTextNode(label);
        div.appendChild(text);
        return div;
    }
}
