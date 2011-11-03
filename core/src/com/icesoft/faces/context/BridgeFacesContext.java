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
package com.icesoft.faces.context;

import com.icesoft.faces.application.ViewHandlerProxy;
import com.icesoft.faces.el.ELContextImpl;
import com.icesoft.faces.env.Authorization;
import com.icesoft.faces.webapp.command.Reload;
import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.core.ResourceDispatcher;
import com.icesoft.faces.webapp.http.core.SessionExpiredException;
import com.icesoft.faces.webapp.http.portlet.PortletExternalContext;
import com.icesoft.faces.webapp.http.servlet.ServletExternalContext;
import com.icesoft.faces.webapp.http.servlet.SessionDispatcher;
import com.icesoft.faces.webapp.parser.ImplementationUtil;
import com.icesoft.jasper.Constants;
import com.sun.xml.fastinfoset.dom.DOMDocumentParser;
import com.sun.xml.fastinfoset.dom.DOMDocumentSerializer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jvnet.fastinfoset.FastInfosetException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.el.ELContext;
import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.application.FacesMessage;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

public class BridgeFacesContext extends FacesContext implements ResourceRegistry {
    private static final Log log = LogFactory.getLog(BridgeFacesContext.class);
    //todo: factor out the page template extension pattern to reuse it MainServlet.java as well (maybe in configuration)
    private static final Pattern PageTemplatePattern = Pattern.compile(".*(\\.iface$|\\.jsf$|\\.faces$|\\.jsp$|\\.jspx$|\\.xhtml$|\\.seam$)");
    private static final Runnable Noop = new Runnable() {
        public void run() {
        }
    };

    private Application application;
    private BridgeExternalContext externalContext;
    private HashMap faceMessages = new HashMap();
    private Map bundles = Collections.EMPTY_MAP;
    private FacesMessage.Severity maxSeverity;
    private boolean renderResponse;
    private boolean responseComplete;
    private ResponseStream responseStream;
    private ResponseWriter responseWriter;
    private DOMSerializer domSerializer;
    private UIViewRoot viewRoot;
    private String sessionID;
    private String viewNumber;
    private View view;
    private Configuration configuration;
    private Collection jsCodeURIs = new ArrayList();
    private Collection cssRuleURIs = new ArrayList();
    private ResourceDispatcher resourceDispatcher;
    private String blockingRequestHandlerContext;
    private ELContext elContext;
    private DocumentStore documentStore;
    private String lastViewID;
    private boolean retainViewRoot;
    private Runnable onRelease = Noop;
    private Runnable clearViewRoot = new Runnable() {
        public void run() {
            onRelease = Noop;
            viewRoot = null;
        }
    };

    private static Constructor jsfUnitConstructor;
    private static String jsfUnitClass;

    static {
        try {
            Class c = Class.forName("org.jboss.jsfunit.context.JSFUnitFacesContext");
            jsfUnitClass = c.getName();
            jsfUnitConstructor = c.getConstructor(new Class[]{javax.faces.context.FacesContext.class});
            log.info("JSFUnit environment detected");
        } catch (Exception e) {
        }
    }

    public BridgeFacesContext(final Request request, final String viewIdentifier, final String sessionID, final View view, final Configuration configuration, final ResourceDispatcher resourceDispatcher, final SessionDispatcher.Monitor sessionMonitor, final String blockingRequestHandlerContext, final Authorization authorization) throws Exception {
        setCurrentInstance(this);
        request.detectEnvironment(new Request.Environment() {
            public void servlet(Object request, Object response) {
                externalContext = new ServletExternalContext(viewIdentifier, request, response, view, configuration, sessionMonitor, authorization, BridgeFacesContext.this);
            }

            public void portlet(Object request, Object response, Object portletConfig) {
                externalContext = new PortletExternalContext(viewIdentifier, request, response, view, configuration, sessionMonitor, portletConfig, authorization, BridgeFacesContext.this);
            }
        });

        String keepViewRoot = externalContext.getInitParameter("com.icesoft.faces.retainViewRoot");
        if (keepViewRoot != null) {
            retainViewRoot = Boolean.valueOf(keepViewRoot).booleanValue();
        }
        this.viewNumber = viewIdentifier;
        this.sessionID = sessionID;
        this.view = view;
        this.configuration = configuration;
        this.application = ((ApplicationFactory) FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY)).getApplication();
        this.resourceDispatcher = resourceDispatcher;
        this.blockingRequestHandlerContext = blockingRequestHandlerContext;
        this.documentStore = configuration.getAttributeAsBoolean("compressDOM", false) ? new FastInfosetDocumentStore() : (DocumentStore) new ReferenceDocumentStore();
        this.switchToNormalMode();
    }

    public void setCurrentInstance() {
        setCurrentInstance(this);
    }

    public static boolean isThreadLocalNull() {
        return getCurrentInstance() == null;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public Iterator getClientIdsWithMessages() {
        return faceMessages.keySet().iterator();
    }

    public ExternalContext getExternalContext() {
        return this.externalContext;
    }

    public void setExternalContext(ExternalContext externalContext) {
        //do nothing
    }

    public ELContext getELContext() {
        if (null != elContext) {
            return elContext;
        }
        elContext = new ELContextImpl(application);
        elContext.putContext(FacesContext.class, this);
        UIViewRoot root = getViewRoot();
        if (null != root) {
            elContext.setLocale(root.getLocale());
        }

        return elContext;
    }

    public FacesMessage.Severity getMaximumSeverity() {
        return maxSeverity;
    }

    /**
     * gets all FacesMessages whether or not associatted with clientId.
     *
     * @return list of FacesMessages
     */
    public Iterator getMessages() {

        //ICE-1900
        RequestStateManagerDelegate.clearMessages(this);

        // Jira #1358 The hashmap contains vectors of FacesMessages, not FacesMessages
        // See following method.
        ArrayList buffer = new ArrayList();
        if (faceMessages.values() == null) return Collections.EMPTY_LIST.iterator();
        Iterator i = faceMessages.values().iterator();
        while (i.hasNext()) {
            buffer.addAll((Vector) i.next());
        }
        return buffer.iterator();
    }

    /**
     * returns list of FacesMessages associated with a clientId. If client id is
     * null, then return all FacesMessages which are not assocaited wih any
     * clientId
     *
     * @param clientId
     * @return list of FacesMessages
     */
    public Iterator getMessages(String clientId) {

        //ICE-1900
        RequestStateManagerDelegate.clearMessages(this, clientId);

        Object obj = faceMessages.get(clientId);
        if (obj == null) {
            if (log.isTraceEnabled()) {
                log.trace(clientId + " has no FacesMessages");
            }
            return Collections.EMPTY_LIST.iterator();
        }

        return ((Vector) obj).iterator();
    }

    RenderKit renderKit = null;
    String renderKitId = null;

    public RenderKit getRenderKit() {
        UIViewRoot viewRoot = getViewRoot();
        if (null == viewRoot) {
            return (null);
        }
        String rootKitId = viewRoot.getRenderKitId();
        if (null == rootKitId) {
            return (null);
        }

        if (!rootKitId.equals(renderKitId)) {
            renderKitId = rootKitId;
            RenderKitFactory renderKitFactory = (RenderKitFactory)
                    FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
            renderKit = renderKitFactory.getRenderKit(this, renderKitId);
        }
        return (renderKit);
    }

    public boolean getRenderResponse() {
        return this.renderResponse;
    }

    public boolean getResponseComplete() {
        return this.responseComplete;
    }

    public ResponseStream getResponseStream() {
        return this.responseStream;
    }

    public void setResponseStream(ResponseStream responseStream) {
        this.responseStream = responseStream;
    }

    public ResponseWriter getResponseWriter() {
        return responseWriter;
    }

    public void setResponseWriter(ResponseWriter responseWriter) {
        this.responseWriter = responseWriter;
    }

    public ResponseWriter createAndSetResponseWriter() throws IOException {
        return responseWriter = new DOMResponseWriter(this, domSerializer, configuration, jsCodeURIs, cssRuleURIs, blockingRequestHandlerContext);
    }

    public void switchToNormalMode() throws Exception {
        externalContext.switchToNormalMode();
        domSerializer = new SaveCurrentDocument(new NormalModeSerializer(this, externalContext.getWriter("UTF-8")));
    }

    public void switchToPushMode() throws Exception {
        //collect bundles put by Tag components when the page is parsed
        bundles = externalContext.collectBundles();
        externalContext.switchToPushMode();
        domSerializer = new SaveCurrentDocument(new PushModeSerializer(documentStore, view, this, viewNumber));
    }

    public UIViewRoot getViewRoot() {
        if (externalContext.isSeamLifecycleShortcut()) {
            //ViewRoot and attributes being cached interferes with PAGE scope
            return null;
        }

        return this.viewRoot;
    }

    public void setViewRoot(UIViewRoot viewRoot) {
        // #3424. Allow viewRoot to be set to null. On page reload, this object
        // is reused, but viewRoot must be nullable 
        this.viewRoot = viewRoot;
        if (viewRoot != null) {
            lastViewID = viewRoot.getViewId();
            final String path = viewRoot.getViewId();
            //Likely an error condition, but allows JSF 2.0 to run
            if (null == path) {
                return;
            }
            if (PageTemplatePattern.matcher(path).matches()) {
                //pointing this FacesContext to the new view
                responseWriter = null;
            } else {
                view.put(new Reload(viewNumber));
                application.setViewHandler(new SwitchViewHandler(application.getViewHandler(), path));
            }
        }
    }

    public String getIceFacesId() {
        return sessionID;
    }

    /**
     * Return the unique identifier associated with each browser window
     * associated with a single user.
     */
    public String getViewNumber() {
        return viewNumber;
    }

    /**
     * Return the id of the Element that currently has focus in the browser.
     *
     * @return String
     */
    public String getFocusId() {
        Map map = externalContext.getRequestParameterMap();
        return (String) (map.containsKey("ice.focus") ? map.get("ice.focus") : "");
    }

    /**
     * Sets the id of the Element that should get focus in the browser.
     */
    public void setFocusId(String focusId) {
        externalContext.getRequestParameterMap().put("ice.focus", focusId);
    }

    /**
     * add a FacesMessage to the set of message associated with the clientId, if
     * clientId is not null.
     *
     * @param clientId
     * @param message
     */
    public void addMessage(String clientId, FacesMessage message) {
        if (message == null) {
            throw new IllegalArgumentException("Message is null");
        }
        if (faceMessages.containsKey(clientId)) {
            ((Vector) faceMessages.get(clientId)).addElement(message);
        } else {
            Vector vector = new Vector();
            vector.add(message);
            faceMessages.put(clientId, vector);
        }
        if (maxSeverity == null ||
                message.getSeverity().getOrdinal() > maxSeverity.getOrdinal()) {
            maxSeverity = message.getSeverity();
        }
    }

    public void renderResponse() {
        this.renderResponse = true;
    }

    public void responseComplete() {
        this.responseComplete = true;
    }

    /**
     * The release() found in FacesContextImpl is more comprehensive: since they
     * blow away the context instance after a response, they null/false out much
     * more than we do. We chose to keep the context instance around across
     * requests so we need to keep some of our state intact.
     */
    public void release() {
        // If JSFUnit environment don't release
        if (doJSFUnitCheck()) return;

        faceMessages.clear();
        maxSeverity = null;
        renderResponse = false;
        responseComplete = false;
        renderKit = null;
        renderKitId = null;

        // if we're doing state management, we always clear the viewRoot between requests.
        if (ImplementationUtil.isJSFStateSaving()) {
            this.viewRoot = null;
        }

        //Spring Web Flow 2 releases the FacesContext in between lifecycle
        //phases
        if (com.icesoft.util.SeamUtilities.isSpring2Environment()) {
            if (!retainViewRoot) {
                this.viewRoot = null;
            }
        } else {
            //clear the request map except when we have SWF2
            externalContext.release();
        }

        onRelease.run();
        // #2807 release thread locals
        setCurrentInstance(null);
    }

    public void dispose() {
        externalContext.dispose();
    }

    public void applyBrowserDOMChanges() throws IOException {
        Document document = documentStore.load();
        if (document == null) return;
        Map parameters = externalContext.getRequestParameterValuesMap();

        NodeList formElements = document.getElementsByTagName("form");
        int formElementsLength = formElements.getLength();
        for (int i = 0; i < formElementsLength; i++) {
            Element formElement = (Element) formElements.item(i);
            String id = formElement.getAttribute("id");
            //String formClientId = ((String[]) parameters.get(id))[0];
            //if (formClientId != null && formClientId.equals(id)) {
            if (parameters.containsKey(id)) {
                applyBrowserFormChanges(parameters, document, formElement);
                break; // This assumes only one form is submitted
            }
        }
        documentStore.cache(document);
    }

    protected void applyBrowserFormChanges(Map parameters, Document document, Element form) {
        NodeList inputElements = form.getElementsByTagName("input");
        int inputElementsLength = inputElements.getLength();
        for (int i = 0; i < inputElementsLength; i++) {
            Element inputElement = (Element) inputElements.item(i);
            String id = inputElement.getAttribute("id");
            if (!"".equals(id)) {
                String name = null;
                if (parameters.containsKey(id)) {
                    String value = ((String[]) parameters.get(id))[0];
                    //empty string is implied (default) when 'value' attribute is missing
                    if (!"".equals(value)) {
                        if (inputElement.hasAttribute("value")) {
                            inputElement.setAttribute("value", value);
                        } else if (inputElement.getAttribute("type").equals("checkbox")) {
                            inputElement.setAttribute("checked", "checked");
                        }
                    } else {
                        inputElement.setAttribute("value", "");
                    }
                } else if (!"".equals(name = inputElement.getAttribute("name")) && parameters.containsKey(name)) {
                    String type = inputElement.getAttribute("type");
                    if (type != null && type.equals("checkbox") || type.equals("radio")) {
                        String currValue = inputElement.getAttribute("value");
                        if (!"".equals(currValue)) {
                            boolean found = false;
                            // For multiple checkboxes, values can have length > 1,
                            // but for multiple radios, values would have at most length=1
                            String[] values = (String[]) parameters.get(name);
                            if (values != null) {
                                for (int v = 0; v < values.length; v++) {
                                    if (currValue.equals(values[v])) {
                                        found = true;
                                        break;
                                    }
                                }
                            }
                            if (found) {
                                // For some reason, our multiple checkbox 
                                // components use checked="true", while
                                // our single checkbox components use
                                // checked="checked". The latter complying
                                // with the HTML specification.
                                // Also, radios use checked="checked"
                                if (type.equals("checkbox")) {
                                    inputElement.setAttribute("checked", "true");
                                } else if (type.equals("radio")) {
                                    inputElement.setAttribute("checked", "checked");
                                }
                            } else {
                                inputElement.removeAttribute("checked");
                            }
                        }
                    }
                } else {
                    if (inputElement.getAttribute("type").equals("checkbox")) {
                        ////inputElement.setAttribute("checked", "");
                        inputElement.removeAttribute("checked");
                    }
                }
            }
        }

        NodeList textareaElements = form.getElementsByTagName("textarea");
        int textareaElementsLength = textareaElements.getLength();
        for (int i = 0; i < textareaElementsLength; i++) {
            Element textareaElement = (Element) textareaElements.item(i);
            String id = textareaElement.getAttribute("id");
            if (!"".equals(id) && parameters.containsKey(id)) {
                String value = ((String[]) parameters.get(id))[0];
                Node firstChild = textareaElement.getFirstChild();
                if (null != firstChild) {
                    //set value on the Text node
                    firstChild.setNodeValue(value);
                } else {
                    //DOM brought back from compression may have no
                    //child for empty TextArea
                    if (value != null && value.length() > 0) {
                        textareaElement.appendChild(document.createTextNode(value));
                    }
                }
            }
        }

        NodeList selectElements = form.getElementsByTagName("select");
        int selectElementsLength = selectElements.getLength();
        for (int i = 0; i < selectElementsLength; i++) {
            Element selectElement = (Element) selectElements.item(i);
            String id = selectElement.getAttribute("id");
            if (!"".equals(id) && parameters.containsKey(id)) {
                List values = Arrays.asList((String[]) parameters.get(id));

                NodeList optionElements =
                        selectElement.getElementsByTagName("option");
                int optionElementsLength = optionElements.getLength();
                for (int j = 0; j < optionElementsLength; j++) {
                    Element optionElement = (Element) optionElements.item(j);
                    if (values.contains(optionElement.getAttribute("value"))) {
                        optionElement.setAttribute("selected", "selected");
                    } else {
                        optionElement.removeAttribute("selected");
                    }
                }
            }
        }
    }

    public URI loadJavascriptCode(final Resource resource) {
        String uri = resourceDispatcher.registerResource(resource).toString();
        if (!jsCodeURIs.contains(uri)) {
            jsCodeURIs.add(uri);
        }
        return resolve(uri);
    }

    public URI loadJavascriptCode(Resource resource, ResourceLinker.Handler linkerHandler) {
        String uri = resourceDispatcher.registerResource(resource, linkerHandler).toString();
        if (!jsCodeURIs.contains(uri)) {
            jsCodeURIs.add(uri);
        }
        return resolve(uri);
    }

    public URI loadCSSRules(Resource resource) {
        String uri = resourceDispatcher.registerResource(resource).toString();
        if (!cssRuleURIs.contains(uri)) {
            cssRuleURIs.add(uri);
        }
        return resolve(uri);
    }


    public URI loadCSSRules(Resource resource, ResourceLinker.Handler linkerHandler) {
        String uri = resourceDispatcher.registerResource(resource, linkerHandler).toString();
        if (!cssRuleURIs.contains(uri)) {
            cssRuleURIs.add(uri);
        }
        return resolve(uri);
    }

    public URI registerResource(Resource resource) {
        return registerResource(resource, null);
    }

    public URI registerResource(Resource resource, ResourceLinker.Handler linkerHandler) {
        try {
            return resolve(registerResourceWithRelativePath(resource, linkerHandler).toString());
        } catch (NullPointerException e) {
            log.warn("Cannot register a null resource");
            return null;
        }
    }

    public URI registerResourceWithRelativePath(Resource resource) {
        return registerResourceWithRelativePath(resource, null);
    }

    public URI registerResourceWithRelativePath(Resource resource, ResourceLinker.Handler linkerHandler) {
        return resourceDispatcher.registerResource(resource, linkerHandler);
    }

    //adapting deprecated methods to current API

    public URI registerResource(final String mimeType, final Resource resource) {
        return registerResource(new ProxyResource(resource) {
            public void withOptions(Options options) throws IOException {
                options.setMimeType(mimeType);
            }
        });
    }

    public URI registerResource(final String mimeType, final Resource resource, ResourceLinker.Handler linkerHandler) {
        return registerResource(new ProxyResource(resource) {
            public void withOptions(Options options) throws IOException {
                options.setMimeType(mimeType);
            }
        }, linkerHandler);
    }

    public URI registerNamedResource(final String name, Resource resource) {
        return registerResource(new ProxyResource(resource) {
            public void withOptions(Options options) throws IOException {
                options.setFileName(name);
            }
        });
    }

    public URI registerNamedResource(final String name, Resource resource, ResourceLinker.Handler linkerHandler) {
        return registerResource(new ProxyResource(resource) {
            public void withOptions(Options options) throws IOException {
                options.setFileName(name);
            }
        }, linkerHandler);
    }

    private URI resolve(String uri) {
        return URI.create(application.getViewHandler().getResourceURL(this, uri));
    }

    /**
     * Check the delegation chain for a BridgeFacesContext wrapped by another
     *
     * @param facesContext return BridgeFacesContext (if found) or input FacesContext
     */
    public static FacesContext unwrap(FacesContext facesContext) {
        if (facesContext instanceof BridgeFacesContext) {
            return facesContext;
        }
        FacesContext result = facesContext;
        try {
            Method delegateMethod = facesContext.getClass()
                    .getDeclaredMethod("getDelegate", new Class[]{});
            delegateMethod.setAccessible(true);
            Object delegate = delegateMethod
                    .invoke(facesContext, (Object[]) null);
            if (delegate instanceof BridgeFacesContext) {
                result = (FacesContext) delegate;
                // #3668 fetch messages from Spring
                Iterator clientIdIterator = facesContext.getClientIdsWithMessages();
                FacesMessage message;
                String clientId;
                while (clientIdIterator.hasNext()) {
                    clientId = (String) clientIdIterator.next();
                    Iterator facesMessagesForClientId = facesContext.getMessages(clientId);
                    while (facesMessagesForClientId.hasNext()) {
                        message = (FacesMessage) facesMessagesForClientId.next();
                        result.addMessage(clientId, message);
                    }
                }
                Iterator facesMessagesWithNoId = facesContext.getMessages(null);
                while (facesMessagesWithNoId.hasNext()) {
                    message = (FacesMessage) facesMessagesWithNoId.next();
                    result.addMessage("", message);
                }
                if (log.isDebugEnabled()) {
                    log.debug("BridgeFacesContext delegate of " + facesContext);
                }
            }
        } catch (Exception e) {
        }

        return result;
    }

    public void processPostback(Request request) throws Exception {
        request.detectEnvironment(new Request.Environment() {
            public void servlet(Object request, Object response) {
                externalContext.update((HttpServletRequest) request, (HttpServletResponse) response);
                //#2139 Don't insert a false postback key if state saving is configured,
                // as this will overwrite the real state saving key
                if (!ImplementationUtil.isJSFStateSaving()) {
                    externalContext.insertPostbackKey();
                }
            }

            public void portlet(Object request, Object response, Object config) {
                //this call cannot arrive from a Portlet
            }
        });
    }

    public void reload(Request request) throws Exception {
        request.detectEnvironment(new Request.Environment() {
            public void servlet(Object request, Object response) {
                externalContext.updateOnPageLoad(request, response);
            }

            public void portlet(Object request, Object response, Object config) {
                externalContext.updateOnPageLoad(request, response);
            }
        });
        //fake a forwarded request to restore the last rendered view
        externalContext.getRequestMap().put("javax.servlet.include.path_info", lastViewID);
    }

    public void injectBundles() {
        externalContext.injectBundles(bundles);
    }

    private class DispatchingViewHandler extends ViewHandlerProxy {
        private final String path;

        public DispatchingViewHandler(ViewHandler originalHandler, String path) {
            super(originalHandler);
            this.path = path;
        }

        public void renderView(FacesContext context, UIViewRoot viewToRender) throws IOException, FacesException {
            application.setViewHandler(handler);
            externalContext.dispatch(path);
        }
    }

    private class SwitchViewHandler extends ViewHandlerProxy {
        private final String path;

        public SwitchViewHandler(ViewHandler originalHandler, String path) {
            super(originalHandler);
            this.path = path;
        }

        public void renderView(FacesContext context, UIViewRoot viewToRender) throws IOException, FacesException {
            application.setViewHandler(new DispatchingViewHandler(handler, path));
            handler.renderView(context, viewToRender);
        }
    }


    public boolean isContentIncluded() {
        //TODO - assuming we can handle portlets just like includes, then
        //we can probably reduce the attributes that we check for.  We need
        //to be specific about when to use request URI and when to use servlet
        //path.
        Map requestMap = externalContext.getRequestMap();
        String frag = (String) requestMap.get(Constants.INC_REQUEST_URI);
        if (log.isDebugEnabled()) {
            log.debug(Constants.INC_REQUEST_URI + " = " + frag);
        }
        if (frag != null) {
            return true;
        }

        frag = (String) requestMap.get(Constants.INC_SERVLET_PATH);
        if (log.isDebugEnabled()) {
            log.debug(Constants.INC_SERVLET_PATH + " = " + frag);
        }
        if (frag != null) {
            return true;
        }

        //This type of check should no longer be required.  If we need
        //to put a portlet specific attribute back in, then we should
        //define our own.
        frag = (String) requestMap.get("com.sun.faces.portlet.INIT");
        if (frag != null) {
            return true;
        }

        return false;
    }

    private static class ProxyResource implements Resource {
        private final Resource resource;

        public ProxyResource(Resource resource) {
            this.resource = resource;
        }

        public String calculateDigest() {
            return resource.calculateDigest();
        }

        public InputStream open() throws IOException {
            return resource.open();
        }

        public Date lastModified() {
            return resource.lastModified();
        }

        public void withOptions(Options options) throws IOException {
            resource.withOptions(options);
        }
    }

    public static interface DocumentStore {
        void save(Document document) throws IOException;

        /* Keep the Document in a more efficient form for
           fast loading 
        */
        void cache(Document document) throws IOException;

        Document load() throws IOException;
    }

    void resetLastViewID() {
        lastViewID = null;
        //reset viewRoot only when running with JSF1.1
        if (!ImplementationUtil.isJSFStateSaving()) {
            //reset the viewRoot only at the end of lifecycle
            onRelease = clearViewRoot;
        }
    }

    private static class ReferenceDocumentStore implements DocumentStore {
        private Document document;

        public void save(Document document) {
            this.document = document;
        }

        public void cache(Document document) {
            this.document = document;
        }

        public Document load() {
            return document;
        }
    }

    private static class FastInfosetDocumentStore implements DocumentStore {
        private byte[] data = new byte[0];
        private Document document = null;

        public void save(Document document) throws IOException {
            DOMDocumentSerializer serializer = new DOMDocumentSerializer();
            ByteArrayOutputStream out = new ByteArrayOutputStream(10000);
            serializer.setOutputStream(out);
            serializer.serialize(document);
            data = out.toByteArray();
            this.document = null;
        }

        public void cache(Document document) {
            this.document = document;
        }

        public Document load() throws IOException {
            if (null != document) {
                return document;
            }
            if (data.length == 0) return null;

            Document document = DOMResponseWriter.DOCUMENT_BUILDER.newDocument();
            try {
                DOMDocumentParser parser = new DOMDocumentParser();
                ByteArrayInputStream in = new ByteArrayInputStream(data);
                parser.parse(document, in);
            } catch (FastInfosetException e) {
                throw new IOException(e.getMessage());
            }

            return document;
        }
    }

    private class SaveCurrentDocument implements DOMSerializer {
        private final DOMSerializer serializer;

        public SaveCurrentDocument(DOMSerializer serializer) {
            this.serializer = serializer;
        }

        public void serialize(Document document) throws IOException {
            serializer.serialize(document);
            documentStore.save(document);
        }
    }

    /**
     * Check if JSFUnit classes are available. If so, instantiate a wrapping
     * JSFUnitFacesContext passing <code>this</code> and stuff the result into the session where
     * appropriate.
     *
     * @return true if this is a JSFUnit environment.
     */
    private boolean doJSFUnitCheck() {
        // if we can instantiate an instance of JSFUnitFacesContext, do so, put it in the session,
        // and don't release.
        try {
            if (jsfUnitConstructor != null) {
                externalContext.getSessionMap().put(jsfUnitClass + ".sessionkey",
                        jsfUnitConstructor.newInstance(new Object[]{this}));
                return true;
            }
        } catch (SessionExpiredException ite) {
            // Can happen after test is run, and release is called because
            // JSFUnit is cleaning up.
        } catch (Exception e) {
            log.error("Exception instantiating JSFUnitFacesContext", e);
        }
        return false;
    }
}
