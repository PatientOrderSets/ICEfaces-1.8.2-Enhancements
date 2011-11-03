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
package org.icefaces.application.showcase.util;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.Serializable;

/**
 * It's used to generate the iframe markup for the Description and Source tab panels.
 * It adds the context path of the application dynamically in front of the src attribute
 * to ensure that the resource can be found when running in both portlet and plain servlet
 * environments.
 *
 * @since 1.6
 */
public class ContextUtilBean {

    private static final String IFRAME_PREFIX = "<iframe src=\"";
    private static final String IFRAME_SUFFIX = "\"class=\"includeIframe\" width=\"100%\"></iframe>";

    private static final String SOURCE_CODE_ULR = "/sourcecodeStream.html?path=";

    //ICE-2967
    private static final String LIFERAY_SCRIPT;

    static{
        StringBuffer buff = new StringBuffer();
        buff.append("<script type='text/javascript'>\n");
        buff.append("if ( (typeof(Liferay) != 'undefined') ) {\n");
        buff.append("    if (Liferay.Columns && !Liferay.Columns._ICE_positionSet) {\n");
        buff.append("        Liferay.Util.actsAsAspect(Liferay.Columns);\n");
        buff.append("        Liferay.Columns.after(\n");
        buff.append("            'add',\n");
        buff.append("            function(portlet) {\n");
        buff.append("                jQuery(portlet).css('position', 'static');\n");
        buff.append("            }\n");
        buff.append("            );\n");
        buff.append("        Liferay.Columns._ICE_positionSet = true;\n");
        buff.append("    }");
        buff.append("}");
        buff.append("</script>\n");
        LIFERAY_SCRIPT = buff.toString();
    }

    public String getContextPath() {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        return ec.getRequestContextPath();
    }

    private static String getContextPathStatic() {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        return ec.getRequestContextPath();
    }

    /**
     * Generates iFrame markup with the specified source as the
     * url to the iFrame content. Does not modify the
     * source path
     *
     * @param source path to some web resource
     * @return iFrame markup to load specified resource.
     */
    public static String generateIFrame(String source) {
        StringBuffer markup = new StringBuffer();
        markup.append(IFRAME_PREFIX);
        markup.append(source);
        markup.append(IFRAME_SUFFIX);
        return markup.toString();
    }

    /**
     * Generates iFrame markup with the specified source as the url to the iFrame content.
     * Modifies the source path by prepending the context path.
     *
     * @param source path to some web resource
     * @return iFrame markup to load specified resource.
     */
    public static String generateIFrameWithContextPath(String source) {
        return generateIFrame(getContextPathStatic() + source);
    }

    /**
     * Generates iFrame markup with the specified source as the url to the iFrame content.
     * Modifies the source path by prepending the source code loading servlet path.
     *
     * @param source path to some web resource
     * @return iFrame markup to load specified resource.
     */
    public static String generateSourceCodeIFrame(String source) {
        return generateIFrameWithContextPath(SOURCE_CODE_ULR + source);
    }

    public String getLiferayScript(){
        return LIFERAY_SCRIPT;
    }
}
