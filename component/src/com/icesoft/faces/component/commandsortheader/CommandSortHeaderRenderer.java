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

package com.icesoft.faces.component.commandsortheader;

import com.icesoft.faces.component.ext.HtmlDataTable;
import com.icesoft.faces.component.ext.renderkit.CommandLinkRenderer;
import com.icesoft.faces.component.ext.taglib.Util;
import com.icesoft.faces.context.DOMContext;
import com.icesoft.faces.renderkit.dom_html_basic.HTML;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.io.IOException;

public class CommandSortHeaderRenderer extends CommandLinkRenderer {
    
    /*
     *  (non-Javadoc)
     * @see javax.faces.render.Renderer#encodeEnd(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
     */
    public void encodeEnd(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
        validateParameters(facesContext, uiComponent, null);
        //Render if user is in given role
        if (Util.isEnabledOnUserRole(uiComponent)) {
            CommandSortHeader sortHeader = (CommandSortHeader) uiComponent;
            HtmlDataTable dataTable = sortHeader.findParentDataTable();

            Node child = null;
            DOMContext domContext =
                    DOMContext.getDOMContext(facesContext, uiComponent);
            Element root = (Element) domContext.getRootNode();
            String headerClass = sortHeader.getStyleClass();
            if (headerClass != null) {
                root.setAttribute(HTML.CLASS_ATTR, headerClass);
            }
            if (sortHeader.getColumnName().equals(dataTable.getSortColumn())) {
                child = root.getFirstChild();
                if (dataTable.isSortAscending()) {
                    headerClass += "Asc";
                } else {
                    headerClass += "Desc";
                }
                if (child != null) {
                    if (child.getNodeType() == 1) { //span
                        child = child.getFirstChild();
                    }
                    String value = child.getNodeValue();
                    Element table = domContext.createElement(HTML.TABLE_ELEM);
                    Element tr = domContext.createElement(HTML.TR_ELEM);
                    table.appendChild(tr);
                    Element textTd = domContext.createElement(HTML.TD_ELEM);
                    textTd.appendChild(domContext.createTextNode(value));
                    Element arrowTd = domContext.createElement(HTML.TD_ELEM);
                    tr.appendChild(textTd);
                    tr.appendChild(arrowTd);
                    Element arrowDiv = domContext.createElement(HTML.DIV_ELEM);
                    arrowDiv.setAttribute(HTML.CLASS_ATTR, headerClass);
                    arrowDiv.setAttribute("valign", "middle");
                    arrowTd.appendChild(arrowDiv);
                    child.setNodeValue("");
                    child.getParentNode().appendChild(table);                   
                }
            }
        }
        super.encodeEnd(facesContext, uiComponent);
    }
}
