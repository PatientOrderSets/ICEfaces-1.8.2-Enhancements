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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.servlet.jsp.PageContext;
import java.util.List;


/**
 * For ICEfaces to support JSF-RI, MyFaces, or any other future JSF
 * implementations, it may require some logic specific to the implementation.
 * Obviously this is not a good thing but it may be unavoidable if we need to
 * access something under the hood that isn't available through public API
 * calls.  This class is available to encapsulate implementation specific
 * anomalies.
 */
public class ImplementationUtil {

    /**
     * Logging instance for this class.
     */
    protected static Log log = LogFactory.getLog(ImplementationUtil.class);


    /**
     * Boolean values to track which implementation we are running under.
     */
    private static boolean isRI = false;
    private static boolean isMyFaces = false;
    private static boolean isJSF12 = false;
    private static boolean isJSF2 = false;
    private static boolean jsfStateSaving;
    
    /**
     * Whether stock JSF components' set attributes are tracked.
     * @see #isAttributeTracking
     */
    private static boolean isStockAttributeTracking = false;
    /**
     * Whether 3rd party components' set attributes are tracked.
     * @see #isAttributeTracking
     */
    private static boolean isAnyAttributeTracking = false;

    /**
     * Marker classes whose presence we used to detect which implementation we
     * are running under.
     */
    private static String RI_MARKER =
            "com.sun.faces.application.ApplicationImpl";
    private static String MYFACES_MARKER =
            "org.apache.myfaces.application.ApplicationImpl";
    private static String JSF12_MARKER =
            "javax.faces.webapp.UIComponentELTag";
    private static String JSF2_MARKER =
            "javax.faces.context.PartialViewContext";

    /**
     * In a couple of places, we need to access the component stack from the
     * PageContext and the key used to do this is implemenation dependent.  So
     * here is where we track the keys and provide appropriate value depending
     * on the implementation we are running under.
     */
    private static String RI_COMPONENT_STACK_KEY =
            "javax.faces.webapp.COMPONENT_TAG_STACK";

    private static String MYFACES_COMPONENT_STACK_KEY =
            "javax.faces.webapp.UIComponentTag.COMPONENT_STACK";


    static {
        try {
            Class.forName(RI_MARKER);
            isRI = true;
        } catch (ClassNotFoundException e) {
        }

        //Test for JSF 1.2
        try {
            Class.forName(JSF12_MARKER);
            isJSF12 = true;
        } catch (Throwable t) {
        }

        if (log.isTraceEnabled()) {
            log.trace("JSF-12: " + isJSF12);
        }

        //Test for JSF 2
        try {
            Class.forName(JSF2_MARKER);
            isJSF2 = true;
        } catch (Throwable t) {
        }

        if (log.isTraceEnabled()) {
            log.trace("JSF-2: " + isJSF2);
        }

        try {
            Class.forName(MYFACES_MARKER);
            isMyFaces = true;
            //Disable JSF12 detection in MyFaces environment
            isJSF12 = false;
        } catch (ClassNotFoundException e) {
        }

        if (log.isTraceEnabled()) {
            log.trace("JSF-RI: " + isRI + "  MyFaces: " + isMyFaces);
        }

        try {
            javax.faces.component.html.HtmlOutputText comp =
                new javax.faces.component.html.HtmlOutputText();
            isStockAttributeTracking = isAttributeTracking(comp);
            
            comp = (javax.faces.component.html.HtmlOutputText) Class.forName(
                "com.icesoft.faces.component.ext.HtmlOutputText").newInstance();
            //TODO List seting will happen in our component constructors, not in this test
            //comp.getAttributes().put(
            //    "javax.faces.component.UIComponentBase.attributesThatAreSet",
            //    new java.util.ArrayList(6));
            isAnyAttributeTracking = isAttributeTracking(comp);
        } catch(Throwable t) {
        }
    }

    /**
     * For stock JSF components, all setting of attributes, whether by setter 
     * methods, or by puts on the the attribute map (which can delegate to 
     * setter methods), result in List UIComponent.attributesThatAreSet, aka 
     * UIComponentBase.getAttributes().get(
     *     "javax.faces.component.UIComponentBase.attributesThatAreSet")
     * containing that attribute name, as of JSF RI 1.2_05. This optimisation 
     * is disabled for components not in javax.faces.component.* packages, 
     * even if the component extend the stock ones. Meaning that the stock 
     * attributes become decellerated in third party components. It's possible 
     * to create the attributesThatAreSet List for 3rd party components, and 
     * have it track attribute maps puts for attributes that do not have 
     * setter methods. But setter method tracking is only enabled as of 
     * //TODO// JSF RI 1.2_xx, so we need to ascertain that separately, for ICEfaces 
     * extended and custom component rendering.
     * 
     * @param comp An arbitrary component whose attributes are known 
     * @return If the JSF implementation tracks this component's set attributes
     */
    private static boolean isAttributeTracking(
        javax.faces.component.html.HtmlOutputText comp) {
        boolean tracked = false;
        comp.setTitle("value");
        comp.getAttributes().put("lang", "value");
        comp.getAttributes().put("no_method", "value");
        List attributesThatAreSet = (List) comp.getAttributes().get(
            "javax.faces.component.UIComponentBase.attributesThatAreSet");
//System.out.println(comp.getClass().getName() + " :: attributesThatAreSet: " + attributesThatAreSet);
        if (attributesThatAreSet != null &&
            attributesThatAreSet.contains("title") &&
            attributesThatAreSet.contains("lang") &&
            attributesThatAreSet.contains("no_method")) {
//System.out.println(comp.getClass().getName() + " :: attributesThatAreSet ENABLED");
            tracked = true;
        }
        return tracked;
    }

    /**
     * Identifies if the JSF implementation we are running in is Sun's JSF
     * reference implemenation (RI).
     *
     * @return true if the JSF implementation is Sun's JSF reference
     *         implemenation
     */
    public static boolean isRI() {
        return isRI;
    }

    /**
     * Identifies if the JSF implementation we are running in is Apache's
     * MyFaces implementation.
     *
     * @return true if the JSF implementation is Apache MyFaces.
     */
    public static boolean isMyFaces() {
        return isMyFaces;
    }

    /**
     * Identifies if the JSF implementation we are running in is JSF 1.2
     *
     * @return true if the JSF implementation is JSF 1.2
     */
    public static boolean isJSF12() {
        return isJSF12;
    }

    /**
     * Identifies if the JSF implementation we are running in is JSF 2.x
     *
     * @return true if the JSF implementation is JSF 2.x
     */
    public static boolean isJSF2() {
        return isJSF2;
    }
    
    /**
     * @return Whether stock JSF components' set attributes are tracked.
     * @see #isAttributeTracking
     */
    public static boolean isStockAttributeTracking() {
        return isStockAttributeTracking;
    }
    
    /**
     * @return Whether 3rd party components' set attributes are tracked.
     * @see #isAttributeTracking
     */
    public static boolean isAnyAttributeTracking() {
        return isAnyAttributeTracking;
    }

    /**
     * Returns the key used to get the component stack from the PageContext. The
     * key is a private member of UIComponentTag class but differs between the
     * known JSF implementations.  We detect the correct implementation in this
     * class and provide the proper key.
     *
     * @return String
     */
    public static String getComponentStackKey() {
        String key = null;
        if (isRI) {
            key = RI_COMPONENT_STACK_KEY;
        } else if (isMyFaces) {
            key = MYFACES_COMPONENT_STACK_KEY;
        }

        if (key != null) {
            return key;
        }

        if (log.isFatalEnabled()) {
            log.fatal(
                    "cannot detect JSF implementation so cannot determine component stack key");
        }

        throw new UnknownJSFImplementationException(
                "cannot determine component stack key");
    }

    /**
     * Returns the component tag stack, including checking in the current
     * request as is the strategy of JSF 1.1_02
     * @param pageContext
     * @return list being the component tag stack
     */
    public static List getComponentStack(PageContext pageContext) {
        List list = (List) pageContext.getAttribute(
                getComponentStackKey(), PageContext.REQUEST_SCOPE);
        if (null == list) {
            list = (List) FacesContext.getCurrentInstance()
                    .getExternalContext().getRequestMap().get(
                    getComponentStackKey());
        }
        return list;

    }

    public static void setJSFStateSaving(boolean isJSFStateSaved) {
        jsfStateSaving = isJSFStateSaved;
    }

    public static boolean isJSFStateSaving() {
        return jsfStateSaving;
    }
}

class UnknownJSFImplementationException extends FacesException {

    public UnknownJSFImplementationException() {
    }

    public UnknownJSFImplementationException(Throwable cause) {
        super(cause);
    }

    public UnknownJSFImplementationException(String message) {
        super(message);
    }

    public UnknownJSFImplementationException(String message, Throwable cause) {
        super(message, cause);
    }

}
