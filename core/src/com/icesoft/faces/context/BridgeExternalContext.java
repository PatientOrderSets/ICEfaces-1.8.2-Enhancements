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

/*
 * BridgeExternalContext.java
 */

package com.icesoft.faces.context;

import com.icesoft.faces.env.AcegiAuthWrapper;
import com.icesoft.faces.env.Authorization;
import com.icesoft.faces.env.RequestAttributes;
import com.icesoft.faces.env.SpringAuthWrapper;
import com.icesoft.faces.webapp.command.CommandQueue;
import com.icesoft.faces.webapp.command.NOOP;
import com.icesoft.faces.webapp.command.Redirect;
import com.icesoft.faces.webapp.command.SetCookie;
import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.faces.webapp.http.core.DisposeBeans;
import com.icesoft.util.SeamUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.render.ResponseStateManager;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * This class is supposed to provide a generic interface to the
 * environment that we're running in (e.g. servlets, portlets).
 */
public abstract class BridgeExternalContext extends ExternalContext {
    private static final Log Log = LogFactory.getLog(BridgeExternalContext.class);
    protected static Class AuthenticationClass = null;
    protected static Class AcegiAuthenticationClass = null;
    protected static Class SpringAuthenticationClass = null;

    static {
        try {
            AcegiAuthenticationClass = Class.forName("org.acegisecurity.Authentication");
            AuthenticationClass = AcegiAuthenticationClass;
            Log.debug("Acegi Security detected.");
        } catch (Throwable t) {
            Log.debug("Acegi Security not detected.");
        }
        try {
            SpringAuthenticationClass = Class.forName("org.springframework.security.Authentication");
            AuthenticationClass = SpringAuthenticationClass;
            Log.debug("Spring Security detected.");
        } catch (Throwable t) {
            Log.debug("Spring Security not detected.");
        }
    }

    protected static final RequestAttributes NOOPRequestAttributes = new RequestAttributes() {
        public Object getAttribute(String name) {
            return null;
        }

        public Enumeration getAttributeNames() {
            return Collections.enumeration(Collections.EMPTY_LIST);
        }

        public void removeAttribute(String name) {
        }

        public void setAttribute(String name, Object value) {
        }
    };

    //ICE-2990, JBSEAM-3426
    protected RequestAttributes SimpleRequestAttributes = new RequestAttributes() {

        private HashMap seamAttributes = new HashMap();

        public Object getAttribute(String name) {
            return seamAttributes.get(name);
        }

        public Enumeration getAttributeNames() {
            return Collections.enumeration(seamAttributes.keySet());
        }

        public void removeAttribute(String name) {
            seamAttributes.remove(name);
        }

        public void setAttribute(String name, Object value) {
            seamAttributes.put(name, value);
        }
    };

    protected static final Dispatcher CannotDispatchOnXMLHTTPRequest = new Dispatcher() {
        public void dispatch(String path) throws IOException, FacesException {
            throw new IOException("Cannot dispatch on XMLHTTP request.");
        }
    };
    protected final Dispatcher RequestNotAvailable = new Dispatcher() {
        public void dispatch(String path) throws IOException, FacesException {
            //ignore 'send-receive-updates' requests that arrive after a 'dispose' request has been already received
            Log.debug("View has been disposed. Ignoring dangling request.");
            commandQueue.put(new NOOP());
        }
    };
    private static final String SEAM_LIFECYCLE_SHORTCUT = "com.icesoft.faces.shortcutLifecycle";
    public static String PostBackKey = "com.icesoft.faces.postbackkey";

    static {
        //We will place VIEW_STATE_PARAM in the requestMap so that
        //JSF 1.2 doesn't think the request is a postback and skip
        //execution
        try {
            Field field = ResponseStateManager.class.getField("VIEW_STATE_PARAM");
            if (null != field) {
                PostBackKey = (String) field.get(ResponseStateManager.class);
            }
        } catch (Exception e) {
        }
    }

    protected final String viewIdentifier;
    protected final CommandQueue commandQueue;
    protected final Authorization defaultAuthorization;
    protected Authorization detectedAuthorization;
    protected boolean standardScope;
    protected Map applicationMap;
    protected Map sessionMap;
    protected Map requestMap;
    protected Map initParameterMap;
    protected Redirector redirector;
    protected CookieTransporter cookieTransporter;
    protected String requestServletPath;
    protected String requestPathInfo;
    protected Map requestParameterMap;
    protected Map requestParameterValuesMap;
    protected Map requestCookieMap;
    protected Map responseCookieMap;
    protected Map requestHeaderMap;
    protected Map requestHeaderValuesMap;
    protected Configuration configuration;
    protected BridgeFacesContext facesContext;

    protected BridgeExternalContext() {
        this.viewIdentifier = null;
        this.commandQueue = null;
        this.defaultAuthorization = null;
        this.configuration = null;
        this.facesContext = null;
        this.standardScope = false;
    }

    protected BridgeExternalContext(String viewIdentifier, CommandQueue commandQueue, Configuration configuration, Authorization authorization, BridgeFacesContext context) {
        this.viewIdentifier = viewIdentifier;
        this.commandQueue = commandQueue;
        this.defaultAuthorization = authorization;
        this.configuration = configuration;
        this.facesContext = context;
        // ICE-3549
        this.standardScope = SeamUtilities.isSeamEnvironment() ||
                configuration.getAttributeAsBoolean("standardRequestScope", false);
    }

    public abstract Writer getWriter(String encoding) throws IOException;

    public abstract void switchToNormalMode();

    public abstract void switchToPushMode();

    public abstract void update(HttpServletRequest request, HttpServletResponse response);

    public abstract void updateOnPageLoad(Object request, Object response);

    public abstract void removeSeamAttributes();

    public Configuration getConfiguration() {
        return configuration;
    }

    public boolean isUserInRole(String role) {
        return detectedAuthorization.isUserInRole(role);
    }

    public void addCookie(Cookie cookie) {
        responseCookieMap.put(cookie.getName(), cookie);
        cookieTransporter.send(cookie);
    }

    public void setRequestPathInfo(String viewId) {
        requestPathInfo = viewId;
    }

    public void setRequestServletPath(String viewId) {
        requestServletPath = viewId;
    }

    public Map getApplicationSessionMap() {
        return sessionMap;
    }

    /**
     * This method is not necessary. The application developer can keep track
     * of the added cookies.
     *
     * @deprecated
     */
    public Map getResponseCookieMap() {
        return responseCookieMap;
    }

    //todo: see if we can execute full JSP cycle all the time (not only when page is parsed)
    //todo: this way the bundles are put into the request map every time, so we don't have to carry
    //todo: them between requests
    public Map collectBundles() {
        Map result = new HashMap();
        Set set = requestMap.entrySet();
        //synchronize iteration as described in http://java.sun.com/j2se/1.4.2/docs/api/java/util/Collections.html#synchronizedMap(java.util.Map)
        synchronized (requestMap) {
            Iterator entries = set.iterator();
            while (entries.hasNext()) {
                Map.Entry entry = (Map.Entry) entries.next();
                Object value = entry.getValue();
                if (value != null) {
                    String className = value.getClass().getName();
                    if ((className.indexOf("LoadBundle") > 0) ||  //Sun RI or ICEfaces
                            (className.indexOf("BundleMap") > 0)) {     //MyFaces
                        result.put(entry.getKey(), value);
                    }
                }
            }
        }

        return result;
    }

    public void injectBundles(Map bundles) {
        requestMap.putAll(bundles);
    }

    /**
     * Insert an object into the Parameter map, making JSF think
     * the request is a postback. This should only be called in non-state
     * savings environments
     */
    protected void insertPostbackKey() {
        if (null != PostBackKey) {
            requestParameterMap.put(PostBackKey, "not reload");
            requestParameterValuesMap.put(PostBackKey, new String[]{"not reload"});
        }
    }

    /**
     * Any GET request performed by the browser is a non-faces request to the framework.
     * (JSF-spec chapter 2, introduction). Given this, the framework must create a new
     * viewRoot for the request, even if the viewId has already been visited. (Spec
     * section 2.1.1) <p>
     * <p/>
     * Only during GET's remember, not during partial submits, where the JSF framework must
     * be allowed to attempt to restore the view. There is a great deal of Seam related code
     * that depends on this happening. So put in a token that allows the D2DViewHandler
     * to differentiate between the non-faces request, and the postbacks, for this
     * request, which will allow the ViewHandler to make the right choice, since we keep
     * the view around for all types of requests
     */
    protected void insertNewViewrootToken() {
        if (SeamUtilities.isSeamEnvironment()) setSeamLifecycleShortcut();
    }

    public void release() {
        resetRequestMap();
    }

    /**
     * If in Standard request scope mode, remove all parameters from
     * the Request Map.
     */
    protected void resetRequestMap() {
        if (standardScope) {
            DisposeBeans.in(requestMap);
            if (!requestMap.isEmpty()) requestMap.clear();
        }
    }

    public void dispose() {
        DisposeBeans.in(requestMap);
        requestMap.clear();
    }

    public void setSeamLifecycleShortcut() {
        requestParameterMap.put(SEAM_LIFECYCLE_SHORTCUT, Boolean.TRUE);
    }

    public boolean removeSeamLifecycleShortcut() {
        return requestParameterMap.remove(SEAM_LIFECYCLE_SHORTCUT) != null;
    }

    public boolean isSeamLifecycleShortcut() {
        return requestParameterMap != null && requestParameterMap.containsKey(SEAM_LIFECYCLE_SHORTCUT);
    }

    public Map getApplicationMap() {
        return applicationMap;
    }

    public Map getSessionMap() {
        return sessionMap;
    }

    public Map getRequestMap() {
        return requestMap;
    }

    /**
     * Override the JSF method, but do nothing here. JSF interjects an
     * interweaving response wrapper object that is unnecessary for us.
     *
     * @param response new Response object
     * @since jsf 1.2_06
     */
    public void setResponse(Object response) {
    }

    public String getInitParameter(String name) {
        return (String) initParameterMap.get(name);
    }

    public Map getInitParameterMap() {
        return initParameterMap;
    }

    public void redirect(final String requestURI) throws IOException {
        // Seam ONLY ...first have to decide if we are requesting the same URI as previous
        // since Seam can redirect to same page/view.  With seam workspace management
        // a new page/view can be selected from the current one which
        // means that Seam's ConversationEntry stack must be updated before
        // redirection.  ICE-2737
        if (SeamUtilities.isSeamEnvironment()) {
            SeamUtilities.switchToCurrentSeamConversation(requestURI);
        }
        String uriString = SeamUtilities.encodeSeamConversationId(requestURI, viewIdentifier);
        int index = uriString.lastIndexOf('?');
        final URI uri;
        try {
            /*
             * ICE-3427: URI.create(String) and URI(String) do not encode for
             *           us. However, the multi-argument constructors do!
             *
             * Note: We're not specifically checking to see if the passed
             *       requestURI is absolute or relative. Using the
             *       multi-argument URI(...) constructor and passing the
             *       requestURI as a path regardless of its form might seem
             *       confusing. However, the result is as desired.
             */
            uri =
                    new URI(
                            null,                                              // scheme
                            null,                                            // userInfo
                            null,                                                // host
                            -1,                                                  // port
                            index != -1 ?                                        // path
                                    uriString.substring(0, index) : uriString,
                            index != -1 ?                                       // query
                                    uriString.substring(index + 1) : null,
                            null);                                           // fragment
        } catch (URISyntaxException exception) {
            throw new RuntimeException(exception);
        }
        redirector.redirect(encodeResourceURL(uri.toString()));
        facesContext.resetLastViewID();
        facesContext.responseComplete();
    }

    public Map getRequestParameterMap() {
        return requestParameterMap;
    }

    public Map getRequestParameterValuesMap() {
        return requestParameterValuesMap;
    }

    public Iterator getRequestParameterNames() {
        return requestParameterMap.keySet().iterator();
    }

    public Map getRequestCookieMap() {
        return requestCookieMap;
    }

    protected void recreateParameterAndCookieMaps() {
        requestParameterMap = Collections.synchronizedMap(new HashMap());
        requestParameterValuesMap = Collections.synchronizedMap(new HashMap());
        requestCookieMap = Collections.synchronizedMap(new HashMap());
        responseCookieMap = Collections.synchronizedMap(new HashMap());
        requestHeaderMap = Collections.synchronizedMap(new HashMap());
        requestHeaderValuesMap = Collections.synchronizedMap(new HashMap());
    }

    public interface Redirector {
        void redirect(String uri);
    }

    public interface CookieTransporter {
        void send(Cookie cookie);
    }

    public interface Dispatcher {
        void dispatch(String path) throws IOException, FacesException;
    }


    public class CommandQueueRedirector implements Redirector {
        public void redirect(String uri) {
            commandQueue.put(new Redirect(uri));
        }
    }

    public class CommandQueueCookieTransporter implements CookieTransporter {
        public void send(Cookie cookie) {
            commandQueue.put(new SetCookie(cookie));
        }
    }

    protected Authorization detectAuthorization(final Principal principal) {
        if (AuthenticationClass != null) {
            return SpringAuthenticationClass == null ? AcegiAuthWrapper.getVerifier(principal, sessionMap) : SpringAuthWrapper.getVerifier(principal, sessionMap);
        } else {
            return defaultAuthorization;
        }
    }

    public String getResponseCharacterEncoding() {
        return "utf-8";
    }

    public void setRequestCharacterEncoding(String encoding) {
        //to avoid UnsupportedOperationException with rave ViewHandler
    }
}
