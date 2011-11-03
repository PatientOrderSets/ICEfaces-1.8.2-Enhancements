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

package com.icesoft.faces.renderkit;

import com.icesoft.faces.renderkit.dom_html_basic.TextRenderer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

public class IncludeRenderer extends TextRenderer {

    private static final Log log = LogFactory.getLog(IncludeRenderer.class);

    public void encodeBegin(FacesContext context, UIComponent component)
            throws IOException {
        if (context == null || component == null) {
            throw new NullPointerException(
                    "Null Faces context or component parameter");
        }
        // suppress rendering if "rendered" property on the component is
        // false.
        if (!component.isRendered()) {
            return;
        }

        String page = (String) component.getAttributes().get("page");

        HttpServletRequest request = (HttpServletRequest)
                context.getExternalContext().getRequest();
        URI absoluteURI = null;
        try {
            absoluteURI = new URI(
                    request.getScheme() + "://" +
                            request.getServerName() + ":" +
                            request.getServerPort() +
                            request.getRequestURI());
            URL includedURL = absoluteURI.resolve(page).toURL();
            URLConnection includedConnection = includedURL.openConnection();
            includedConnection
                    .setRequestProperty("Cookie",
                            "JSESSIONID=" + ((HttpSession) context.getExternalContext()
                                    .getSession(false)).getId());
            Reader contentsReader = new InputStreamReader(
                    includedConnection.getInputStream());

            try {
                StringWriter includedContents = new StringWriter();
                char[] buf = new char[2000];
                int len = 0;
                while ((len = contentsReader.read(buf)) > -1) {
                    includedContents.write(buf, 0, len);
                }

                ((UIOutput) component).setValue(includedContents.toString());
            } finally {
                contentsReader.close();
            }
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug(e.getMessage());
            }
        }

        super.encodeBegin(context, component);
    }


}
