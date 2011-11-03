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

package com.icesoft.faces.application;

import com.icesoft.faces.context.BridgeExternalContext;
import com.icesoft.faces.context.BridgeFacesContext;
import com.icesoft.faces.context.DOMResponseWriter;
import com.icesoft.faces.webapp.http.servlet.ServletExternalContext;
import com.icesoft.faces.webapp.parser.ImplementationUtil;
import com.icesoft.faces.webapp.parser.JspPageToDocument;
import com.icesoft.faces.webapp.parser.Parser;
import com.icesoft.util.SeamUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.StateManager;
import javax.faces.application.ViewHandler;
import javax.faces.component.NamingContainer;
import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKitFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * <B>D2DViewHandler</B> is the ICEfaces ViewHandler implementation
 *
 * @see javax.faces.application.ViewHandler
 */
public class D2DViewHandler extends ViewHandler {
    // Log instance for this class
    protected static Log log = LogFactory.getLog(D2DViewHandler.class);

    static {
        if (log.isInfoEnabled()) {
            log.info(new ProductInfo().toString());//Announce ICEfaces
        }

        //ICE-4704
        if (ImplementationUtil.isJSF2() && log.isWarnEnabled()) {
            log.warn("JSF 2.0 libraries detected. This version of ICEfaces is not supported on JSF 2.0. JSF 1.1 or 1.2 are required.");
        }
    }

    private final static String ACTION_URL_SUFFIX =
            "com.icesoft.faces.actionURLSuffix";
    private final static String RELOAD_INTERVAL =
            "com.icesoft.faces.reloadInterval";

    private final static String LAST_LOADED_KEY = "_lastLoaded";
    private final static String LAST_CHECKED_KEY = "_lastChecked";
    public static final String CHAR_ENCODING = "UTF-8";
    public static final String HTML_CONTENT_TYPE =
            "text/html;charset=" + CHAR_ENCODING;

    public static final String DEFAULT_VIEW_ID = "default";

    private String actionURLSuffix;
    //reloadInterval internally in milliseconds
    protected long reloadInterval;
    protected long reloadIntervalDefault = 10;
    private boolean parametersInitialized = false;

    protected Parser parser;
    protected ViewHandler delegate;

    public D2DViewHandler() {
        try {
            InputStream inputStream = this.getClass().getResourceAsStream("serializedTagToComponentMapFull.ser");
            parser = new Parser(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        catch (Throwable te) {
            //this allows us to use facelets & jsf1.2 impl on J2EE AS  see ICE-2356
            log.info("ICEfaces JSP parser disabled. This is a Facelets-only configuration.");
        }
    }

    public D2DViewHandler(ViewHandler delegate) {
        this();
        this.delegate = delegate;
    }

    public void initView(FacesContext facesContext) {
        //TODO implement ExternalContext.setRequestCharacterEncoding
        //so that this method can use the default implementation on
        //JSF 1.2
        //super.initView(facesContext);
    }

    // Render the components
    public void renderView(FacesContext context, UIViewRoot viewToRender)
            throws IOException, FacesException {
        initializeParameters(context);

        if (SeamUtilities.isSpringEnvironment()) {
            context = BridgeFacesContext.unwrap(context);
            if (context instanceof BridgeFacesContext) {
                ((BridgeFacesContext) context).setCurrentInstance();
            }
        }

        if (delegateView(context)) {
            delegate.renderView(context, viewToRender);
            return;
        }

        if (log.isTraceEnabled()) {
            log.trace("renderView(FC,UIVR)  BEFORE  renderResponse  " +
                    "viewToRender.getViewId(): " + viewToRender.getViewId());
        }
        renderResponse(context);
    }


    /**
     * Create a new ViewRoot
     *
     * @param context FacesContext
     * @param viewId  ViewId identifying the root
     * @return A new viewRoot
     */
    public UIViewRoot createView(FacesContext context, String viewId) {
        initializeParameters(context);

        if (delegateView(context)) {
            return delegate.createView(context, viewId);
        }
        // # 2141 consume this parameter under JSF too, (no guarantee restore
        // view is ever called )
        if (SeamUtilities.isSeamEnvironment()) {
            ((BridgeExternalContext) context.getExternalContext()).removeSeamLifecycleShortcut();
        }

        // #3422 Needs to be a public class instantiable by JSF
        UIViewRoot root = new SettableLocaleViewRoot();
        String renderKitId = null;
        UIViewRoot oldRoot = context.getViewRoot();
        if (oldRoot != null) {
            renderKitId = oldRoot.getRenderKitId();
        }
        if (renderKitId == null) {
            // For some reason the RI goes back to the top level ViewHandler
            // Something about decorated implementations
            renderKitId = context.getApplication().getViewHandler().calculateRenderKitId(context);
        }
        root.setRenderKitId(renderKitId);
        root.setViewId(getRenderedViewId(context, null == viewId ? "default" : viewId));

        return root;
    }

    /**
     * Restore the view if possible. This method can return null if
     * no ViewRoot is available. The <code>LifeCycle</code> will call
     * createView in this case.
     *
     * @param context FacesContext
     * @param viewId  ViewId identifying the view to restore
     * @return UIViewRoot instance if found, null if none yet created,
     *         or if trying to model Seam JSF behaviour.
     */
    public UIViewRoot restoreView(FacesContext context, String viewId) {
        this.initializeParameters(context);
        if (SeamUtilities.isSpringEnvironment()) {
            context = BridgeFacesContext.unwrap(context);
            if (context instanceof BridgeFacesContext) {
                ((BridgeFacesContext) context).setCurrentInstance();
            }
        }

        if (delegateView(context)) {
            return delegate.restoreView(context, viewId);
        }
        //MyFaces expects path to match current view
        ExternalContext externalContext = context.getExternalContext();
        if (externalContext instanceof ServletExternalContext) {
            ServletExternalContext servletExternalContext =
                    (ServletExternalContext) externalContext;
            servletExternalContext.setRequestServletPath(viewId);

            if (null != externalContext.getRequestPathInfo()) {
                //it's not null, so must be valid to keep in synch for MyFaces
                servletExternalContext.setRequestPathInfo(viewId);
            }

            if (SeamUtilities.isSeamEnvironment()) {
                if (servletExternalContext.removeSeamLifecycleShortcut()) {
                    if (log.isTraceEnabled()) {
                        log.trace("Seam Keyword shortcut found, new ViewRoot");
                    }
                    return null;
                } else {
                    if (log.isTraceEnabled()) {
                        log.trace("No Seam Keyword shortcut found");
                    }
                }
            }
        }


        if (ImplementationUtil.isJSFStateSaving()) {

            String renderKitId =
                    calculateRenderKitId(context);
            long start = System.currentTimeMillis();

            Application a = context.getApplication();
            StateManager sm = a.getStateManager();
            UIViewRoot viewRoot = sm.restoreView(context, viewId, renderKitId);

            if (log.isDebugEnabled()) {
                log.debug("\n Restored ViewRoot from state management: " + viewRoot + " in " + (System.currentTimeMillis() - start) / 1000f);
            }
            return viewRoot;
        } else {

            UIViewRoot currentRoot = context.getViewRoot();
            // For spring webflow
            if (SeamUtilities.isSpringEnvironment()) {
                return currentRoot;
            }
            if (null != currentRoot &&
                    getRenderedViewId(context, viewId)
                            .equals(getRenderedViewId(context,
                                    currentRoot.getViewId()))) {
                return currentRoot;
            } else {
                return null;
            }
        }
    }

    public String getActionURL(FacesContext context, String viewId) {
        //Maybe should always use delegate
        // Temporary solution for ICE-30ot1mot1ponaa
        // , to use our 1.6.x check for .iface
        boolean ifaceSuffix =
                ((viewId != null) && viewId.endsWith(".iface"));
        if (delegateView(context) && !ifaceSuffix) {
            return delegate.getActionURL(context, viewId);
        }

        if (viewId.indexOf("://") >= 0) {
            return viewId;
        }

        if (viewId.charAt(0) != '/') {
            throw new IllegalArgumentException(
                    "viewId " + viewId + "does not begin with '/'");
        }

        //remove extension and replace with parametrized suffix
        if (null != actionURLSuffix) {
            viewId =
                    viewId.substring(0, viewId.lastIndexOf(".")) +
                            actionURLSuffix;
        }

        return context.getExternalContext().getRequestContextPath() + viewId;
    }

    public String getResourceURL(FacesContext context, String path) {
        ExternalContext extContext = context.getExternalContext();
        if (path.startsWith("/")) {
            return (extContext.getRequestContextPath() + path);
        } else {
            return path;
        }
    }

    protected long getTimeAttribute(UIComponent root, String key) {
        Long timeLong = (Long) root.getAttributes().get(key);
        long time = (null == timeLong) ? 0 :
                timeLong.longValue();
        return time;
    }

    protected void renderResponse(FacesContext facesContext) throws IOException {
        BridgeFacesContext context = (BridgeFacesContext) facesContext;
        UIViewRoot root = context.getViewRoot();
        String viewId = root.getViewId();

        if (log.isTraceEnabled()) {
            log.trace("Rendering " + root + " with " +
                    root.getChildCount() + " children");
        }

        ResponseWriter responseWriter = context.createAndSetResponseWriter();

        boolean reloadView = false;
        URLConnection viewConnection = null;
        URL viewURL = null;

        if ((root.getChildCount() == 0) || (reloadInterval > -1)) {
            // We have not parsed the page yet;
            // Need an input stream for the page;
            if (viewId.startsWith("/faces")) {
                viewId = viewId.substring(6);
            }
            if (viewId.endsWith(".jpg") || viewId.endsWith(".gif") ||
                    viewId.endsWith(".png")) {
                context.getExternalContext().dispatch(viewId);
                return;
            }
            try {
                viewURL = context.getExternalContext().getResource(viewId);
                if (null == viewURL) {
                    if (viewId.endsWith(".faces")) {
                        viewId = truncate(".faces", viewId);
                    } else if (viewId.endsWith(".jsf")) {
                        viewId = truncate(".jsf", viewId);
                    } else if (viewId.endsWith(".iface")) {
                        viewId = truncate(".iface", viewId);
                    } else if (viewId.endsWith(".jsp")) {
                        //MyFaces thinks everything is a .jsp
                        viewId = truncate(".jsp", viewId);
                    }

                    viewId = viewId + ".jspx";
                    viewURL = context.getExternalContext().getResource(viewId);
                }

                if (null == viewURL) {
                    if (viewId.endsWith(".jspx")) {
                        viewId = truncate(".jspx", viewId) +
                                ".jsp";
                    }
                    viewURL = context.getExternalContext().getResource(viewId);
                }
                root.setViewId(viewId);


                long currentTime = System.currentTimeMillis();
                long lastLoaded = getTimeAttribute(root, LAST_LOADED_KEY);
                long lastChecked = getTimeAttribute(root, LAST_CHECKED_KEY);
                long lastModified = 0;

                //newly instantiated viewRoot will have lastChecked of 0
                //and lastLoaded of 0
                if (currentTime > lastChecked + reloadInterval) {
                    viewConnection = viewURL.openConnection();
                    lastModified = viewConnection.getLastModified();
                    root.getAttributes().put(LAST_CHECKED_KEY,
                            new Long(currentTime));
                    if (lastModified > lastLoaded) {
                        reloadView = true;
                        if (log.isDebugEnabled()) {
                            log.debug("View is modified, reloading " +
                                    String.valueOf(viewURL));
                        }
                    }
                }

            } catch (Exception e) {
                throw new FacesException("Can't find stream for " + viewId, e);
            }
        }

        if (reloadView) {
            Reader viewInput = null;

            try {
                viewInput = new InputStreamReader(
                        viewConnection.getInputStream(), CHAR_ENCODING);
                if (viewId.endsWith(".jsp")) {
                    if (log.isDebugEnabled()) {
                        log.debug("JspPageToDocument transforming JSP page " +
                                String.valueOf(viewURL));
                    }
                    viewInput = JspPageToDocument.transform(viewInput);
                } else if (viewId.endsWith(".jspx")) {
                    if (log.isDebugEnabled()) {
                        log.debug("JspPageToDocument preprocessing JSP doc " +
                                String.valueOf(viewURL));
                    }
                    viewInput =
                            JspPageToDocument.preprocessJspDocument(viewInput);
                }
            } catch (Throwable e) {
                throw new FacesException("Can't read stream for " + viewId, e);
            }

            // Parse the page;
            try {
                //TODO: pass viewInput as an InputStream in order to give to the XML parser a chance to
                //TODO: read the encoding type declared in the xml processing instruction (<?xml version="1.0" charset="..."?>)
                if (log.isDebugEnabled()) {
                    log.debug("Parsing " +
                            String.valueOf(viewURL));
                }
                parser.parse(viewInput, context);
                root.getAttributes().put(LAST_LOADED_KEY,
                        new Long(System.currentTimeMillis()));


            } catch (Throwable e) {
                throw new FacesException("Can't parse stream for " + viewId +
                        " " + e.getMessage(), e);
            }

            if (ImplementationUtil.isJSF12()) {
                if (log.isDebugEnabled()) {
                    log.debug("Rendering outside ViewTag for JSF 1.2");
                }
                //JSF 1.2 ViewTag does not invoke rendering
                responseWriter.startDocument();

                renderResponse(context, root);
                // make state saving changes to DOM before ending document
                invokeStateSaving(context);

                responseWriter.endDocument();
                tracePrintComponentTree(context);
            }

        } else {
            responseWriter.startDocument();

            renderResponse(context, root);
            // make state saving changes to DOM before ending document
            invokeStateSaving(context);

            responseWriter.endDocument();
            tracePrintComponentTree(context);
        }

    }

    protected void renderResponse(FacesContext context, UIComponent component)
            throws IOException {

        if (!component.isRendered()) {
            return;
        }

        // UIViewRoot.encodeBegin(FacesContext) resets its counter for
        //   createUniqueId(), which we don't want, or else we get
        //   duplicate ids
        boolean isUIViewRoot = component instanceof UIViewRoot;
        if (!isUIViewRoot)
            component.encodeBegin(context);

        if (component.getRendersChildren()) {
            component.encodeChildren(context);
        } else {
            if (component.getChildCount() > 0) {
                Iterator kids = component.getChildren().iterator();
                while (kids.hasNext()) {
                    renderResponse(context, (UIComponent) kids.next());
                }
            }
        }

        if (!isUIViewRoot)
            component.encodeEnd(context);

        //Workaround so that MyFaces UIData will apply values to
        //child components even if the tree is not restored via StateManager
        if (component instanceof javax.faces.component.UIData) {
            StateHolder stateHolder = (StateHolder) component;
            stateHolder.restoreState(context, stateHolder.saveState(context));
        }
    }

    protected void tracePrintComponentTree(FacesContext context) {
        tracePrintComponentTree(context, context.getViewRoot());
    }

    protected void tracePrintComponentTree(
            FacesContext context, UIComponent component) {
        if (log.isTraceEnabled()) {
            StringBuffer sb = new StringBuffer(4096);
            sb.append("tracePrintComponentTree() vvvvvv\n");
            java.util.Set nonICEfacesRenderers = new java.util.HashSet();
            tracePrintComponentTree(context, component, 0, sb, null, nonICEfacesRenderers);
            log.trace(sb.toString());
            if (!nonICEfacesRenderers.isEmpty()) {
                log.trace("Non-ICEfaces Renderers: " + nonICEfacesRenderers.size());
                Iterator otherRenderers = nonICEfacesRenderers.iterator();
                while (otherRenderers.hasNext())
                    log.trace("             Renderer: " + otherRenderers.next());
            }
            log.trace("tracePrintComponentTree() ^^^^^^");
        }
    }

    private void tracePrintComponentTree(
            FacesContext context, UIComponent component,
            int levels, StringBuffer sb, String facetName,
            java.util.Set nonICEfacesRenderers) {
        if (component == null) {
            sb.append("null\n");
            return;
        }
        final String PREFIX_WHITESPACE = "  ";
        StringBuffer prefix = new StringBuffer(64);
        for (int i = 0; i < levels; i++)
            prefix.append(PREFIX_WHITESPACE);
        prefix.append("<");
        String compStr = component.toString();

        StringBuffer open = new StringBuffer(512);
        open.append(prefix);
        open.append(compStr);
        Map facetsMap = component.getFacets();
        boolean hasKids = component.getChildCount() > 0;
        boolean hasFacets = (facetsMap != null) && (facetsMap.size() > 0);
        boolean hasUnderlings = hasKids | hasFacets;
        if (!hasUnderlings)
            open.append("/");
        open.append(">");
        if (facetName != null) {
            open.append(" facetName: ");
            open.append(facetName);
        }
        open.append(" id: ");
        open.append(component.getId());
        if (component.getParent() != null) {
            open.append(" clientId: ");
            open.append(component.getClientId(context));
        }
        String rendererType = component.getRendererType();
        if (rendererType != null) {
            open.append(" renderer: ");
            Object renderer = context.getRenderKit().getRenderer(
                    component.getFamily(), rendererType);
            if (renderer == null)
                open.append("null");
            else {
                String rendererClassName = renderer.getClass().getName();
                if (!rendererClassName.startsWith("com.icesoft"))
                    nonICEfacesRenderers.add(rendererClassName);
                open.append(rendererClassName);
            }
        }
        if (hasKids) {
            open.append(" kids: ");
            open.append(Integer.toString(component.getChildCount()));
        }
        if (hasFacets) {
            open.append(" facets: ");
            open.append(Integer.toString(facetsMap.size()));
        }
        if (component.isTransient())
            open.append(" TRANSIENT ");
        sb.append(open.toString());
        sb.append('\n');

        if (hasUnderlings) {
            if (hasFacets) {
                Object[] facetKeys = facetsMap.keySet().toArray();
                Arrays.sort(facetKeys);
                for (int i = 0; i < facetKeys.length; i++) {
                    tracePrintComponentTree(
                            context, (UIComponent) facetsMap.get(facetKeys[i]),
                            levels + 1, sb, facetKeys[i].toString(),
                            nonICEfacesRenderers);
                }
            }
            if (hasKids) {
                if (component.getChildCount() > 0) {
                    Iterator kids = component.getChildren().iterator();
                    while (kids.hasNext()) {
                        tracePrintComponentTree(
                                context, (UIComponent) kids.next(), levels + 1,
                                sb, null, nonICEfacesRenderers);
                    }
                }
            }

            StringBuffer close = new StringBuffer(512);
            close.append(prefix);
            close.append("/");
            close.append(compStr);
            close.append(">");
            sb.append(close);
            sb.append('\n');
        }
    }

    /**
     * This method is called from the Form renderer during the encodeBegin method.
     * Its intent was to write the component tree and state into the response. Either
     * the entire state is written if client side state saving is configured, or
     * a token is written if server side saving is configured.
     * <p/>
     * <p/>
     * Because the way the FormRenderer doesn't currently use the DOMResponseWriter
     * the view from the DOMResponseWriter is not up to date with what the
     * FormRenderer is currently doing to the DOM, so this method will do nothing
     *
     * @param context
     * @throws IOException
     */
    public void writeState(FacesContext context) throws IOException {

        if (delegateView(context)) {
            delegate.writeState(context);
        }
    }

    /**
     * This method invokes state saving on the stateManager. It also instructs
     * the DOMResponseWriter to save the DOM nodes written during writeState()
     * a method on the stateManager for the purposes of copying them to marker
     * nodes later.
     *
     * @param context FacesContext
     */
    protected void invokeStateSaving(FacesContext context) {

        if (!ImplementationUtil.isJSFStateSaving()) {
            return;
        }

        Application a = context.getApplication();
        StateManager sm = a.getStateManager();

        long start = System.currentTimeMillis();

        StateManager.SerializedView sv = sm.saveSerializedView(context);
        if (log.isDebugEnabled()) {
            log.debug("Serialized state saved in: " + (System.currentTimeMillis() - start) / 1000f + "seconds");
        }

        // Tell the DOMResponseWriter to capture the nodes created by JSF
        ResponseWriter writer = context.getResponseWriter();
        if (writer != null && (writer instanceof DOMResponseWriter)) {
            ((DOMResponseWriter) writer).setSaveNextNode(true);
        }

        try {
            // get JSF to write state (captured by DOMResponseWriter)
            start = System.currentTimeMillis();
            sm.writeState(context, sv);
            if (log.isDebugEnabled()) {
                log.debug("Serialized state written in: " + (System.currentTimeMillis() - start) / 1e9f + "seconds");
            }
        } catch (IOException ioe) {
            log.error("IOException saving state: ", ioe);
        } finally {

            // turn off state saving node capture
            if (writer != null && (writer instanceof DOMResponseWriter)) {
                ((DOMResponseWriter) writer).setSaveNextNode(false);
                ((DOMResponseWriter) writer).copyStateNodesToMarkers();
            }
        }
    }


    public Locale calculateLocale(FacesContext context) {
        Application application = context.getApplication();
        Iterator acceptedLocales = context.getExternalContext().getRequestLocales();
        while (acceptedLocales.hasNext()) {
            Locale acceptedLocale = (Locale) acceptedLocales.next();
            Iterator supportedLocales = application.getSupportedLocales();
            while (supportedLocales.hasNext()) {
                Locale supportedLocale = (Locale) supportedLocales.next();
                if (acceptedLocale.equals(supportedLocale)) {
                    return supportedLocale;
                }
            }
            supportedLocales = application.getSupportedLocales();
            while (supportedLocales.hasNext()) {
                Locale supportedLocale = (Locale) supportedLocales.next();
                if (acceptedLocale.getLanguage().equals(supportedLocale.getLanguage()) &&
                        supportedLocale.getCountry().length() == 0) {
                    return supportedLocale;
                }
            }
        }
        // no match is found.
        Locale defaultLocale = application.getDefaultLocale();
        return defaultLocale == null ? Locale.getDefault() : defaultLocale;
    }

    public String calculateRenderKitId(FacesContext context) {
        if (delegateView(context)) {
            return delegate.calculateRenderKitId(context);
        }

        Map requestParamMap = context.getExternalContext().getRequestParameterMap();
        String renderKitId = (String) requestParamMap.get("javax.faces.RenderKitId");
        // The key difference is checking for non-null but empty strings
        if (renderKitId == null || renderKitId.trim().length() == 0) {
            renderKitId = context.getApplication().getDefaultRenderKitId();
            if (renderKitId == null) {
                renderKitId = RenderKitFactory.HTML_BASIC_RENDER_KIT;
            }
        }
        return renderKitId;
    }

    public static boolean isValueReference(String value) {
        if ((value.indexOf("#{") != -1) &&
                (value.indexOf("#{") < value.indexOf('}'))) {
            return true;
        }
        return false;
    }

    /**
     * A dumber version (that can't find child components of the UIData
     * component) of this method resides in UIComponentBase. The same is true
     * for the private findComoponent in UIComponentBase - it is duplicated
     * here.
     *
     * @param clientId
     * @param base
     */
    public static UIComponent findComponent(String clientId, UIComponent base) {
//System.out.println("    findComponent()  clientId: " + clientId + "  base: " + base);
        // Set base, the parent component whose children are searched, to be the
        // nearest parent that is either 1) the view root if the id expression
        // is absolute (i.e. starts with the delimiter) or 2) the nearest parent
        // NamingContainer if the expression is relative (doesn't start with
        // the delimiter)
        String delimeter = String.valueOf(NamingContainer.SEPARATOR_CHAR);
        int count = getNumberOfLeadingNamingContainerSeparators(clientId);
//System.out.println("      count: " + count);
        if (count == 1) {
            // Absolute searches start at the root of the tree
            while (base.getParent() != null) {
                base = base.getParent();
            }
            // Treat remainder of the expression as relative
            clientId = clientId.substring(delimeter.length());
        } else if (count == 0) {
            // Relative expressions start at the closest NamingContainer or root
            while (base.getParent() != null) {
                if (base instanceof NamingContainer) {
                    break;
                }
                base = base.getParent();
            }
        } else if (count > 1) {
            // Relative expressions start at the closest NamingContainer or root
            int numNamingContainersUp = count - 1;
//System.out.println("      numNamingContainersUp: " + numNamingContainersUp);
            while (base.getParent() != null) {
                if (base instanceof NamingContainer) {
                    numNamingContainersUp--;
//System.out.println("      NamingContainer["+numNamingContainersUp+"]: " + base);
                    if (numNamingContainersUp == 0)
                        break;
                }
                base = base.getParent();
            }
            clientId = clientId.substring(delimeter.length() * count);
//System.out.println("      clientId: " + clientId);
        }
        // Evaluate the search expression (now guaranteed to be relative)
        String id = null;
        UIComponent result = null;
        while (clientId.length() > 0) {
            int separator = clientId.indexOf(NamingContainer.SEPARATOR_CHAR);
            if (base instanceof UIData) {
                if (separator >= 0) {
                    clientId = clientId.substring(separator + 1);
                }
                separator = clientId.indexOf(NamingContainer.SEPARATOR_CHAR);
            }
            if (separator >= 0) {
                id = clientId.substring(0, separator);
                clientId = clientId.substring(separator + 1);
            } else {
                id = clientId;
                clientId = "";
            }
            result = findComponent(base, id);
            if ((result == null) || (clientId.length() == 0)) {
                break; // Missing intermediate node or this is the last node
            }
            if (result instanceof NamingContainer) {
                result = findComponent(clientId, result);
                break;
            }
        }

        return result;
    }

    // Allow multiple leading NamingContainer separator chars to allow for
    //  findComponent() to search upwards, relatively, as described by:
    //  http://myfaces.apache.org/trinidad/trinidad-api/apidocs/org/apache/myfaces/trinidad/component/core/nav/CoreSingleStepButtonBar.html#getPartialTriggers()
    private static int getNumberOfLeadingNamingContainerSeparators(
            String clientId) {
        int count = 0;
        String delimeter = String.valueOf(NamingContainer.SEPARATOR_CHAR);
        for (int index = 0; clientId.indexOf(delimeter, index) == index; index += delimeter.length())
            count++;
        return count;
    }

    private static String truncate(String remove, String input) {
        return input.substring(0, input.length() - remove.length());
    }

    //Determine whether handling of the view should be delegated to
    //the delegate ViewHandler
    private boolean delegateView(FacesContext context) {

        return !(context instanceof BridgeFacesContext);
    }

    private void initializeParameters(FacesContext context) {
        if (parametersInitialized) {
            return;
        }

        ExternalContext ec = context.getExternalContext();
        String reloadIntervalParameter = ec.getInitParameter(RELOAD_INTERVAL);
        String stateManagementServerSide = ec.getInitParameter(StateManager.STATE_SAVING_METHOD_PARAM_NAME);

        // #3980 This enables state saving to work in jsf1.1 environment with the default settings
        // (since state saving is always on now)
        ImplementationUtil.setJSFStateSaving((ImplementationUtil.isJSF12() || ImplementationUtil.isJSF2()));

        actionURLSuffix = ec.getInitParameter(ACTION_URL_SUFFIX);
        try {
            reloadInterval = Long.parseLong(reloadIntervalParameter) * 1000;
        } catch (NumberFormatException e) {
            reloadInterval = reloadIntervalDefault * 1000;
        }

        boolean server = (stateManagementServerSide == null) ||
                stateManagementServerSide.toLowerCase().equals(StateManager.STATE_SAVING_METHOD_SERVER);
        if (!server) {
            log.fatal("Client side state saving is not supported with ICEfaces");
            throw new UnsupportedOperationException("Client side state saving is not supported with ICEfaces");
        }

        if (!ImplementationUtil.isJSFStateSaving()) {
            log.debug("JSF State Management not configured");
        } else {
            log.debug("JSF State Management enabled - server side state saving");
        }
        parametersInitialized = true;
    }

    /**
     * Obtain the viewId. This method removes the suffix from the action
     * in whatever form it came from the servlet, and appends the default suffix
     * parameter instead
     *
     * @param context  current FacesContext
     * @param actionId current view action id.
     * @return The viewId with the proper suffix
     */
    protected String getRenderedViewId(FacesContext context, String actionId) {
        ExternalContext extCtx = context.getExternalContext();
        String viewId = actionId;
        if (extCtx.getRequestPathInfo() == null) {
            String viewSuffix = context.getExternalContext().getInitParameter(
                    ViewHandler.DEFAULT_SUFFIX_PARAM_NAME);
            // Log a debug message to help users with this parameter left undefined
            if (viewSuffix == null) {
                if (log.isDebugEnabled()) {
                    log.debug(
                            "The " + ViewHandler.DEFAULT_SUFFIX_PARAM_NAME +
                                    " context parameter is not set in web.xml. " +
                                    "Please define the filename extension used for " +
                                    "your source JSF pages. Example:\n" +
                                    "<context-param>\n" +
                                    " <param-name>javax.faces.DEFAULT_SUFFIX</param-name>\n" +
                                    " <param-value>" +
                                    getDefaultSuffix() + "</param-value>\n" +
                                    "</context-param>");
                }
                viewSuffix = ViewHandler.DEFAULT_SUFFIX;
            }

            int lastPeriod = actionId.lastIndexOf('.');
            if (lastPeriod < 0) {
                viewId = actionId + viewSuffix;
            } else {
                viewId = actionId.substring(0, lastPeriod) + viewSuffix;
            }
        }
        return viewId;
    }

    /**
     * Allow subclasses to override the suffix for logging purposes
     *
     * @return Default suffix for this technology type
     */
    protected String getDefaultSuffix() {
        return ViewHandler.DEFAULT_SUFFIX;
    }


    private static UIComponent findComponent(UIComponent uiComponent,
                                             String componentId) {
        UIComponent component = null;
        UIComponent child = null;

        if (componentId.equals(uiComponent.getId())) {
            return uiComponent;
        }
        Iterator children = uiComponent.getFacetsAndChildren();
        while (children.hasNext() && (component == null)) {
            child = (UIComponent) children.next();
            if (!(child instanceof NamingContainer)) {
                component = findComponent(child, componentId);
                if (component != null) {
                    break;
                }
            } else if (componentId.endsWith(child.getId())) {
                component = child;
                break;
            }
        }
        return component;
    }

    /**
     * A version of findComponent() that attempts to locate a component by id (not clientId)
     * and searches into NamingContainers. If there are more than one component with the
     * provided id, the first one found will be returned
     *
     * @param uiComponent the base component to search from
     * @param componentId the id to search for
     */

    public static UIComponent findComponentInView(UIComponent uiComponent,
                                                  String componentId) {
        UIComponent component = null;
        UIComponent child = null;

        if (componentId.equals(uiComponent.getId())) {
            return uiComponent;
        }
        Iterator children = uiComponent.getFacetsAndChildren();
        while (children.hasNext() && (component == null)) {
            child = (UIComponent) children.next();
            component = findComponentInView(child, componentId);
            if (component != null) {
                break;
            }
            if (componentId.endsWith(child.getId())) {
                component = child;
                break;
            }
        }
        return component;
    }
}
