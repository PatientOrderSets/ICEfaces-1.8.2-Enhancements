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

package com.icesoft.faces.renderkit.dom_html_basic;

import com.icesoft.faces.component.PORTLET_CSS_DEFAULT;
import com.icesoft.faces.context.DOMContext;
import com.icesoft.faces.context.effects.CurrentStyle;
import com.icesoft.faces.context.effects.JavascriptContext;
import com.icesoft.faces.util.CoreUtils;
import com.icesoft.faces.util.Debug;
import com.icesoft.util.pooling.ClientIdPool;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.w3c.dom.Element;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.NamingContainer;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIInput;
import javax.faces.component.UIMessage;
import javax.faces.component.UIParameter;
import javax.faces.component.ValueHolder;
import javax.faces.component.html.HtmlMessage;
import javax.faces.component.html.HtmlMessages;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.render.Renderer;
import java.beans.Beans;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class DomBasicRenderer extends Renderer {
    private static final Log log = LogFactory.getLog(DomBasicRenderer.class);
    public static final String ATTRIBUTES_THAT_ARE_SET_KEY =
        "javax.faces.component.UIComponentBase.attributesThatAreSet";

    // iceSubmitPartial
    public final static String ICESUBMITPARTIAL =
            "iceSubmitPartial(form, this, event);";
    // iceSubmit
    public final static String ICESUBMIT = "iceSubmit(form,this,event);";

    // component family constants for UIForm and WebUIForm
    public static final String WEB_UIFORM = "com.sun.rave.web.ui.Form";
    public static final String UIFORM = "javax.faces.form";
    public static final String WEB_UIJSFFORM = "com.sun.webui.jsf.Form";

    public void decode(FacesContext facesContext, UIComponent uiComponent) {
        CurrentStyle.decode(facesContext, uiComponent);
        validateParameters(facesContext, uiComponent, null);
        // only need to decode input components
        if (!(uiComponent instanceof UIInput)) {
            return;
        }
        // only need to decode enabled, writable components
        if (isStatic(uiComponent)) {
            return;
        }
        // extract component value from the request map
        String clientId = uiComponent.getClientId(facesContext);
        Debug.assertTrue(clientId != null,
                         "Client id is not defined for decoding");
        Map requestMap =
                facesContext.getExternalContext().getRequestParameterMap();
        if (requestMap.containsKey(clientId)) {
            String decodedValue = (String) requestMap.get(clientId);
            setSubmittedValue(uiComponent, decodedValue);
        }
    }

    /**
     * This method should be overridden by renderers for components who subclass
     * UIInput
     *
     * @param uiComponent
     * @param value
     */
    public void setSubmittedValue(UIComponent uiComponent, Object value) {
    }

    /**
     * Delegate rendering to the renderEnd(..) method after validating
     * parameters and before maintaining the cursor position. The renderEnd
     * method should be overridden by subclasses of this class so that the
     * common infrastructure of parameter validation and cursor maintenance are
     * provided here.
     */
    public void encodeEnd(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
        validateParameters(facesContext, uiComponent, null);
        CoreUtils.recoverFacesMessages(facesContext, uiComponent);
        renderEnd(facesContext, uiComponent,
                  getValue(facesContext, uiComponent));
        DOMContext domContext =
                DOMContext.getDOMContext(facesContext, uiComponent);

        domContext.stepOver();
        JavascriptContext.fireEffect(uiComponent, facesContext);
    }

    /**
     * Get the submitted value from the UIComponent argument. If the UIComponent
     * is not an instance of UIInput, or its <code>getSubmittedValue()</code>
     * method returns null or a non-String value, then an attempt is made to
     * obtain the value from the UIComponent's renderer. Conversion is performed
     * on a value obtained from the renderer.
     *
     * @param facesContext
     * @param uiComponent
     * @return String the submitted value
     */
    public String getValue(FacesContext facesContext, UIComponent uiComponent) {
        // for input components, get the submitted value
        if (uiComponent instanceof UIInput) {
            Object submittedValue = ((UIInput) uiComponent).getSubmittedValue();
            if (submittedValue != null && submittedValue instanceof String) {
                return (String) submittedValue;
            }
        }
        return formatComponentValue(facesContext, uiComponent,
                                    getValue(uiComponent));
    }

    Object getValue(UIComponent uiComponent) {
        return null;
    }


    /**
     * The common infrastructure of parameter validation and cursor management
     * will be provided by the encodeEnd method and rendering is delegated to
     * this method. Renderers should override this method instead of encodeEnd
     * to provide rendering at the time of execution of the encodeEnd method.
     *
     * @param facesContext
     * @param uiComponent
     * @param currentValue
     * @throws IOException
     */
    protected void renderEnd(FacesContext facesContext,
                             UIComponent uiComponent, String currentValue)
            throws IOException {
    }

    /**
     * If the parameter UIComponent instance is a ValueHolder, return the
     * currentValue parameter. If there is a converter registered with the
     * component then use the converter to obtain a String value.
     *
     * @param facesContext
     * @param uiComponent
     * @param currentValue
     * @return
     * @throws ConverterException
     */
    String formatComponentValue(FacesContext facesContext,
                                UIComponent uiComponent, Object currentValue)
            throws ConverterException {
        return converterGetAsString(facesContext, uiComponent, currentValue);
    }
    
    public static String converterGetAsString(FacesContext facesContext,
                                              UIComponent uiComponent,
                                              Object currentValue)
    {
        if (!(uiComponent instanceof ValueHolder)) {
            if (currentValue != null) {
                return currentValue.toString();
            } else {
                return null;
            }
        }

        // look to see whether there is a converter registered with the component
        Converter converter = ((ValueHolder) uiComponent).getConverter();

        // if there was no converter registered with the component then 
        // look for the default converter for the Class of the currentValue
        if (converter == null) {
            if(currentValue == null) {
                return "";
            } else if (currentValue instanceof String) {
                return (String) currentValue;
            } 
            
            converter = getConverterForClass(currentValue.getClass());

            if (converter == null) {
                return currentValue.toString();
            }
        }
        
        String ret = converter.getAsString(facesContext, uiComponent, currentValue);
//System.out.println("DomBasicRenderer.converterGetAsString()  currentValue: " + currentValue);        
//System.out.println("DomBasicRenderer.converterGetAsString()  ret         : " + ret);        
//System.out.println("DomBasicRenderer.converterGetAsString()  converter   : " + converter);
//        if(converter instanceof javax.faces.convert.DateTimeConverter)
//            System.out.println("DomBasicRenderer.converterGetAsString()  timeZone: " + ((javax.faces.convert.DateTimeConverter)converter).getTimeZone());
        return ret;
    }
    
    /**
     * Find the UIComponent whose id is given by the for attribute of the
     * UIComponent parameter.
     *
     * @param facesContext
     * @param uiComponent
     * @return the UIComponent associated with the component id indicated by the
     *         value of the for attribute of the UIComponent parameter.
     */
    public static UIComponent findForComponent(FacesContext facesContext,
                                               UIComponent uiComponent) {

        String forComponentId = null;
        if (uiComponent instanceof UIMessage) {
            forComponentId = ((UIMessage) uiComponent).getFor();
        } else {
            forComponentId =
                    (String) uiComponent.getAttributes().get(HTML.FOR_ATTR);
        }

        if (forComponentId == null) {
            return null;
        }
        if (forComponentId.length() == 0) {
            return null;
        }

        // Look for the 'for' component in the nearest parental naming container 
        // of the UIComponent (there's actually a bit more to this search - see
        // the docs for the findComponent method
        UIComponent forComponent = uiComponent.findComponent(forComponentId);

        // Since the nearest naming container may be nested, search the 
        // next-to-nearest parental naming container in a recursive fashion, 
        // until we get to the view root
        if (forComponent == null) {
            UIComponent nextParent = uiComponent;
            while (true) {
                nextParent = nextParent.getParent();
                // avoid extra searching by going up to the next NamingContainer
                // (see the docs for findComponent for an information that will
                // justify this approach)
                while (nextParent != null &&
                       !(nextParent instanceof NamingContainer)) {
                    nextParent = nextParent.getParent();
                }
                if (nextParent == null) {
                    break;
                } else {
                    forComponent = nextParent.findComponent(forComponentId);
                }
                if (forComponent != null) {
                    break;
                }
            }
        }

        // There is one other situation to cover: if the 'for' component 
        // is not situated inside a NamingContainer then the algorithm above
        // will not have found it. We need, in this case, to search for the 
        // component from the view root downwards
        if (forComponent == null) {
            forComponent = searchDownwardsForChildComponentWithId(
                    facesContext.getViewRoot(),
                    forComponentId);
        }
        return forComponent;
    }

    /**
     * Retrieve the array of excluded attributes. This array should be
     * constructed in the renderer class and then passed in to the
     * PassThruAttributeRenderer.
     *
     * @return a String array of excluded attributes.
     */
    public static String[] getExcludesArray(Set excludes) {
        String[] excludesArray = new String[excludes.size()];
        excludes.toArray(excludesArray);
        return excludesArray;
    }

    private static UIComponent searchDownwardsForChildComponentWithId(
            UIComponent parent,
            String searchChildId) {
        UIComponent foundChild = null;
        if (parent.getChildCount() == 0)return foundChild;
        Iterator children = parent.getChildren().iterator();
        UIComponent nextChild = null;
        while (children.hasNext()) {
            nextChild = (UIComponent) children.next();
            if (nextChild instanceof NamingContainer) {
                foundChild = nextChild.findComponent(searchChildId);
            }
            if (foundChild == null) {
                searchDownwardsForChildComponentWithId(nextChild,
                                                       searchChildId);
            }
            if (foundChild != null) {
                break;
            }
        }
        return foundChild;
    }

    /**
     * Recursively render the parent UIComponent instance and its children.
     *
     * @param facesContext
     * @param parent
     * @throws IOException
     */
    public static void encodeParentAndChildren(FacesContext facesContext,
                                               UIComponent parent)
            throws IOException {
        parent.encodeBegin(facesContext);
        if (parent.getRendersChildren()) {
            parent.encodeChildren(facesContext);
        } else {
            if (parent.getChildCount() > 0) {
                Iterator children = parent.getChildren().iterator();
                while (children.hasNext()) {
                    UIComponent nextChild = (UIComponent) children.next();
                    if (nextChild.isRendered()) {
                        encodeParentAndChildren(facesContext, nextChild);
                    }
                }
            }
        }
        parent.encodeEnd(facesContext);
    }


    protected static UIComponent getFacetByName(UIComponent uiComponent,
                                                String name) {
        UIComponent facet = uiComponent.getFacet(name);
        if (facet == null) {
            return null;
        }
        if (!facet.isRendered()) {
            return null;
        }
        return facet;
    }

    static boolean idNotNull(UIComponent uiComponent) {
        return (uiComponent.getId() != null);
    }

    /**
     * Set the id of the root element of the DOMContext associated with the
     * UIComponent parameter.
     *
     * @param facesContext
     * @param rootElement
     * @param uiComponent
     */
    public static void setRootElementId(FacesContext facesContext,
                                        Element rootElement,
                                        UIComponent uiComponent) {
        if (idNotNull(uiComponent)) {
            rootElement
                    .setAttribute("id", uiComponent.getClientId(facesContext));
        }
    }

    /**
     * <p/>
     * Sets a non-null, non-empty-string, UIComponent property to the
     * corresponding DOM Element
     * <p/>
     *
     * @param uiComponent         the source of the attribute value
     * @param targetElement       the DOM Element that will receive the
     *                            attribute
     * @param attrNameInComponent the property name in the UIComponent object
     * @param attrNameInDom       the attribute name in the DOM Element
     */
    public static void renderAttribute(UIComponent uiComponent,
                                       Element targetElement,
                                       String attrNameInComponent,
                                       String attrNameInDom) {
        Object attrValue = uiComponent.getAttributes().get(attrNameInComponent);
        if (attrValue != null && !attrValue.equals("")) {
            if (attrValue.toString().equalsIgnoreCase("true") ||
                attrValue.toString().equalsIgnoreCase("false")) {
                boolean trueValue =
                        new Boolean(attrValue.toString()).booleanValue();
                if (!trueValue) {
                    targetElement.removeAttribute(attrNameInDom.toString());
                    return;
                }
            }
            targetElement.setAttribute(attrNameInDom.toString(),
                                       attrValue.toString());
        }
    }


    /**
     * Due to the behaviour of the UIParameter class, the names in the
     * name-value pairs of the Map returned by this method are guaranteed to be
     * Strings
     *
     * @param uiComponent
     * @return Map the parameterMap
     */
    static Map getParameterMap(UIComponent uiComponent) {
        Map parameterMap = new HashMap();
        if (uiComponent.getChildCount() > 0) {
            Iterator children = uiComponent.getChildren().iterator();
            while (children.hasNext()) {
                UIComponent nextChild = (UIComponent) children.next();
                if (nextChild instanceof UIParameter) {
                    UIParameter uiParam = (UIParameter) nextChild;
                    parameterMap.put(uiParam.getName(), uiParam.getValue());
                }
            }
        }
        return parameterMap;
    }

    /**
     * Validates that the facesContext is not null, the uiComponent is not null,
     * and that uiComponent is assignment-compatible with the
     * validComponentType. Pass a null parameter for validComponentType to avoid
     * any type checking.
     *
     * @param facesContext
     * @param uiComponent
     * @param validComponentType
     * @throws NullPointerException if either of the facesContext or the
     *                              uiComponent parameters are null or
     *                              if a parent form is not 
     *                              found when the given UIComponent
     *                              is a UIInput or UICommand,
     *                              IllegalArgumentException if the
     *                              validComponentType is not null and the
     *                              uiComponent is not assignable to the given
     *                              type.                               
     */
    public static void validateParameters(FacesContext facesContext,
                                   UIComponent uiComponent,
                                   Class validComponentType) {

        if (facesContext == null) {
            throw new NullPointerException(
                    "Invalid Parameter - FacesContext instance must not be null");
        }
        if (uiComponent == null) {
            throw new NullPointerException(
                    "Invalid Parameter - UIComponent instance must not be null");
        }
        if (!Beans.isDesignTime() && validComponentType != null &&
            !(validComponentType.isInstance(uiComponent))) {
            throw new IllegalArgumentException(
                    "Invalid Parameter - UIComponent class should be ["
                    + validComponentType +
                    "] but it is an instance of ["
                    + uiComponent.getClass() + "]");
        }

        if (log.isDebugEnabled()) {
            if ((uiComponent instanceof UIInput) || (uiComponent instanceof UICommand)) {
                if (findForm(uiComponent) == null) {
                    log.debug("Missing Form - the UIComponent of type [" 
                            + uiComponent.getClass() + "] requires a containing form.");
                }
            }
        }
        
    }

    /**
     * A component is static if it is disabled or readonly.
     *
     * @param uiComponent
     * @return true if the component is disabled or readonly
     */
    public static boolean isStatic(UIComponent uiComponent) {
        // the algorithm here is to return true as soon as we get affirmation that
        // the component is static
        boolean isStatic = false;
        Object disabled = uiComponent.getAttributes().get("disabled");
        Object readonly = uiComponent.getAttributes().get("readonly");
        if (disabled != null) {
            if (disabled instanceof Boolean) {
                isStatic = ((Boolean) disabled).booleanValue();
            } else if (disabled instanceof String) {
                isStatic = ((String) disabled).equalsIgnoreCase("true");
            }
        }
        if (isStatic) {
            return isStatic;
        }
        if (readonly != null) {
            if (readonly instanceof Boolean) {
                return ((Boolean) readonly).booleanValue();
            }
            if (readonly instanceof String) {
                return ((String) readonly).equalsIgnoreCase("true");
            }
        }
        return isStatic;
    }

    /**
     * <p/>
     * Given a UIComponent instance, recursively examine the heirarchy of parent
     * UIComponents until the first NamingContainer is found. </p>
     *
     * @param uiComponent
     * @return the nearest parent NamingContainer or null if none exist.
     */
    public static UIComponent findNamingContainer(UIComponent uiComponent) {
        UIComponent parent = uiComponent.getParent();
        while (parent != null) {
            if (parent instanceof NamingContainer) {
                break;
            }
            parent = parent.getParent();
        }
        return parent;
    }

    /**
     * <p/>
     * Given a UIComponent instance, recursively examine the heirarchy of parent
     * NamingContainers until a Form is found. </p>
     *
     * @param uiComponent the UIComponent instance
     * @return form as the UIComponent instance
     */
    public static UIComponent findForm(UIComponent uiComponent) {
        UIComponent parent = uiComponent.getParent();
        while (parent != null && !(parent instanceof UIForm)) {
            parent = findNamingContainer(parent);
        }
        UIComponent form = null;
        // check family 
        if (parent != null &&
            (parent.getFamily().equalsIgnoreCase(WEB_UIFORM) ||
             parent.getFamily().equalsIgnoreCase(UIFORM) ||
             parent.getFamily().equalsIgnoreCase(WEB_UIJSFFORM))) {
            form = (UIComponent) parent;
        }

        if (form == null && Beans.isDesignTime()) {
            form = uiComponent.getParent();
        }
        return form;
    }

    /**
     * This method fabricates the clientId of a component. It should be used
     * only when the clientId of the uiComponent is required in advance of the
     * component existing. The uiComponentId may be provided by, for example, a
     * label element with a 'for' attribute defined. The for attribute will be
     * the id of the component that will eventually be created.
     * <p/>
     * Determine the id of the nearest parental naming container and prepend it
     * to the id of the component's id.
     *
     * @param uiComponent
     * @param facesContext
     * @param uiComponentId
     * @return
     */
    String fabricateClientId(UIComponent uiComponent,
                             FacesContext facesContext, String uiComponentId) {
        UIComponent parentNamingContainer = findNamingContainer(uiComponent);
        String parentNamingContainerClientId = null;
        if (parentNamingContainer == null) {
            return uiComponentId;
        } else {
            parentNamingContainerClientId =
                    parentNamingContainer.getClientId(facesContext);
        }
        return parentNamingContainerClientId
               + NamingContainer.SEPARATOR_CHAR
               + uiComponentId;
    }

    protected String[] getColumnStyleClasses(UIComponent uiComponent) {
        return getStyleClasses(uiComponent, "columnClasses");
    }

    /**
     * This method, given a component, will return an array of the component's
     * row classes.
     *
     * @param uiComponent
     * @return a String array of row classes defined in a tag attribute or
     *         defined by default, depending on the component. Can be a
     *         zero-length array
     */
    public String[] getRowStyleClasses(UIComponent uiComponent) {
        return getStyleClasses(uiComponent, "rowClasses");
    }

    public String[] getStyleClasses(UIComponent uiComponent,
                                     String styleClassAttributeName) {
        String allStyleClasses = (String) uiComponent.getAttributes()
                .get(styleClassAttributeName);
        if (allStyleClasses == null) {
            return (new String[0]);
        }

        String separator = ",";
        if (allStyleClasses.indexOf(separator) <= 0) {
            separator = " ";
        }
        String[] styleClassesArray = allStyleClasses.trim().split(separator);
        int numberOfStyles = styleClassesArray.length;
        for (int i = 0; i < numberOfStyles; i++) {
            styleClassesArray[i] = styleClassesArray[i].trim();
        }
        return styleClassesArray;
    }

    /**
     * Get the style and style class associated with the severity of the
     * FacesMessage
     *
     * @param uiComponent
     * @param facesMessage
     * @return
     */
    static String[] getStyleAndStyleClass(UIComponent uiComponent,
                                          FacesMessage facesMessage) {
        // obtain the severity style and severity style class
        String severityStyle = null;
        String severityStyleClass = null;
        String baseStyle = null;
        Severity messageSeverity = facesMessage.getSeverity();
        if (messageSeverity == FacesMessage.SEVERITY_INFO) {
            severityStyle =
                    (String) uiComponent.getAttributes().get("infoStyle");
            if (uiComponent instanceof HtmlMessage) {
                severityStyleClass = ((HtmlMessage) uiComponent).getInfoClass();
                baseStyle = "iceMsg";
            } else if (uiComponent instanceof HtmlMessages) {
                severityStyleClass =
                        ((HtmlMessages) uiComponent).getInfoClass();
                baseStyle = "iceMsgs";                
            }
            if (uiComponent.getRendererType().startsWith("com.icesoft.faces.Message")){
            	severityStyleClass = CoreUtils.addPortletStyleClassToQualifiedClass
            							(severityStyleClass, 
            							baseStyle + "Info", 
            							PORTLET_CSS_DEFAULT.PORTLET_MSG_INFO);
            }
        } else if (messageSeverity == FacesMessage.SEVERITY_WARN) {
            severityStyle =
                    (String) uiComponent.getAttributes().get("warnStyle");
            if (uiComponent instanceof HtmlMessage) {
                severityStyleClass = ((HtmlMessage) uiComponent).getWarnClass();
                baseStyle = "iceMsg";                
            } else if (uiComponent instanceof HtmlMessages) {
                severityStyleClass =
                        ((HtmlMessages) uiComponent).getWarnClass();
                baseStyle = "iceMsgs";                
            }
            if (uiComponent.getRendererType().startsWith("com.icesoft.faces.Message")){
            	severityStyleClass = CoreUtils.addPortletStyleClassToQualifiedClass
            							(severityStyleClass, 
            							baseStyle + "Warn", 
            							PORTLET_CSS_DEFAULT.PORTLET_MSG_ALERT);
            }
        } else if (messageSeverity == FacesMessage.SEVERITY_ERROR) {
            severityStyle =
                    (String) uiComponent.getAttributes().get("errorStyle");
            if (uiComponent instanceof HtmlMessage) {
                severityStyleClass =
                        ((HtmlMessage) uiComponent).getErrorClass();
                baseStyle = "iceMsg";
            } else if (uiComponent instanceof HtmlMessages) {
                severityStyleClass =
                        ((HtmlMessages) uiComponent).getErrorClass();
                baseStyle = "iceMsgs";
            }
            if (uiComponent.getRendererType().startsWith("com.icesoft.faces.Message")){
            	severityStyleClass = CoreUtils.addPortletStyleClassToQualifiedClass
            							(severityStyleClass, 
            							baseStyle + "Error", 
            							PORTLET_CSS_DEFAULT.PORTLET_MSG_ERROR);
            }

        } else if (messageSeverity == FacesMessage.SEVERITY_FATAL) {
            severityStyle =
                    (String) uiComponent.getAttributes().get("fatalStyle");
            if (uiComponent instanceof HtmlMessage) {
                severityStyleClass =
                        ((HtmlMessage) uiComponent).getFatalClass();
            } else if (uiComponent instanceof HtmlMessages) {
                severityStyleClass =
                        ((HtmlMessages) uiComponent).getFatalClass();
            }
        }

        String style = null;
        if (severityStyle != null) {
            style = severityStyle;
        } else {
            style = (String) uiComponent.getAttributes().get("style");
        }
        String styleClass = null;
        if (severityStyleClass != null) {
            styleClass = severityStyleClass;
        } else {
            if (uiComponent instanceof HtmlMessage) {
                styleClass = ((HtmlMessage) uiComponent).getStyleClass();
            } else if (uiComponent instanceof HtmlMessages) {
                styleClass = ((HtmlMessages) uiComponent).getStyleClass();
            }
        }
        return new String[]{style, styleClass};
    }

    /**
     * @param facesMessage
     * @return
     */
    String[] getSummaryAndDetail(FacesMessage facesMessage) {
        String summary = facesMessage.getSummary();
        if (summary == null) {
            summary = "";
        }
        String detail = facesMessage.getDetail();
        if (detail == null) {
            detail = "";
        }
        return new String[]{summary, detail};
    }

    /**
     * @param uiComponent
     * @return
     */
    boolean getToolTipAttribute(UIComponent uiComponent) {
        boolean tooltip = false;
        Object tooltipAttribute = uiComponent.getAttributes().get("tooltip");
        if (tooltipAttribute instanceof Boolean
            && ((Boolean) tooltipAttribute).booleanValue()) {
            tooltip = true;
        }
        return tooltip;
    }

    /**
     * @param converterClass
     * @return
     */
    static Converter getConverterForClass(Class converterClass) {
        if (converterClass == null) {
            return null;
        }
        try {
            FacesContext ctx = FacesContext.getCurrentInstance();
            Application application = ctx.getApplication();
            return (application.createConverter(converterClass));
        } catch (Exception e) {
            return (null);
        }
    }

    public static String getResourceURL(FacesContext context, String path) {
        return context.getApplication().getViewHandler()
                .getResourceURL(context, path);
    }

    /**
     * This is a utility method for concatenating two Strings, where passThru
     *  is typically null or an empty String, and renderer is usually non-null,
     *  but can in theory be null, and we want to minimise needless new String
     *  creation.
     * 
     * @param passThru The passthru attribute from the component
     * @param renderer The Javascript that the Renderer needs to output
     * @return A String concatenation of passThru + renderer
     */
    public static String combinedPassThru(String passThru, String renderer) {
        int passThruLen = (passThru == null) ? 0 : passThru.length();
        int rendererLen = (renderer == null) ? 0 : renderer.length();
        if(passThruLen == 0 && rendererLen == 0)
            return null;
        if(passThruLen == 0)
            return renderer;
        if(rendererLen == 0)
            return passThru;
        return passThru + renderer;
    }     
    
    public String convertClientId(FacesContext context, String clientId) {
        return ClientIdPool.get(clientId);    
    }
}
