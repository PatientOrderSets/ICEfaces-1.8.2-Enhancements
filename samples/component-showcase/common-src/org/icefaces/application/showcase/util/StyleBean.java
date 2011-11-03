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

import org.icefaces.application.showcase.view.bean.NavigationNames;

import javax.faces.model.SelectItem;
import javax.faces.event.ValueChangeEvent;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.io.Serializable;

/**
 * <p>The StyleBean class is the backing bean which manages the demonstrations'
 * active theme.  There are currently two themes supported by the bean; XP and
 * Royale. </p>
 * <p/>
 * <p>The webpages' style attributes are modified by changing link in the header
 * of the HTML document.  The selectInputDate and tree components' styles are
 * changed by changing the location of their image src directories.</p>
 *
 * @since 0.3.0
 */
public class StyleBean implements Serializable {

    // possible theme choices
    private final String RIME = "rime";
    private final String XP = "xp";
    private final String ROYALE = "royale";

    // default theme
    protected String currentStyle = RIME;
    protected String tempStyle = RIME;

    // available style list
    protected ArrayList styleList;

    protected HashMap styleMap;

    /**
     * Creates a new instance of the StyleBean.
     */
    public StyleBean() {
        // initialize the style list
        styleList = new ArrayList();
        styleList.add(new SelectItem(RIME, RIME));
        styleList.add(new SelectItem(XP, XP));
        styleList.add(new SelectItem(ROYALE, ROYALE));

        styleMap = new HashMap(3);
        styleMap.put(RIME, new StylePath(
                "./xmlhttp/css/rime/rime.css",
                "/xmlhttp/css/rime/css-images/"));
        styleMap.put(XP, new StylePath(
                "./xmlhttp/css/xp/xp.css",
                "/xmlhttp/css/xp/css-images/"));
        styleMap.put(ROYALE, new StylePath(
                "./xmlhttp/css/royale/royale.css",
                "/xmlhttp/css/royale/css-images/"));
    }

    /**
     * Gets the current style.
     *
     * @return current style
     */
    public String getCurrentStyle() {
        return currentStyle;
    }

    /**
     * Sets the current style of the application to one of the predetermined
     * themes.
     *
     * @param currentStyle name of new style
     */
    public void setCurrentStyle(String currentStyle) {
        this.tempStyle = currentStyle;
    }

    /**
     * Gets the html needed to insert a valid css link tag.
     *
     * @return the tag information needed for a valid css link tag
     */
    public String getStyle() {
        return "<link rel=\"stylesheet\" type=\"text/css\" href=\"" +
                ((StylePath)styleMap.get(currentStyle)).getCssPath() + "\"></link>";
    }

    /**
     * Gets the image directory to use for the selectinputdate and tree
     * theming.
     *
     * @return image directory used for theming
     */
    public String getImageDirectory() {
        return ((StylePath)styleMap.get(currentStyle)).imageDirPath;
    }

    /**
     * Applies temp style to to the current style and image directory and
     * manually refreshes the icons in the navigation tree. The page will reload
     * based on navigation rules to ensure the theme is applied; this is
     * necessary because of difficulties encountered by updating the stylesheet
     * reference within the <HEAD> of the document.
     *
     * @return the reload navigation attribute
     */
    public void changeStyle(ValueChangeEvent e) throws java.io.IOException{
        tempStyle = (String)e.getNewValue();
        if (!currentStyle.equalsIgnoreCase(tempStyle)) {
            currentStyle = tempStyle;
            FacesContext.getCurrentInstance().getExternalContext().redirect("/component-showcase/index.jsp");
        }
    }

    /**
     * Gets a list of available theme names that can be applied.
     *
     * @return available theme list
     */
    public List getStyleList() {
        return styleList;
    }

    /**
     * Utility class to manage different cssPath and imageDir namd
     */
    public class StylePath implements Serializable{

        private String cssPath;
        private String imageDirPath;

        public StylePath(String cssPath, String imageDirPath) {
            this.cssPath = cssPath;
            this.imageDirPath = imageDirPath;
        }

        public String getCssPath() {
            return cssPath;
        }

        public String getImageDirPath() {
            return imageDirPath;
        }
    }

}
