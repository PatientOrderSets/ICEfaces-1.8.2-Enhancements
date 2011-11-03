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

package com.icesoft.faces.env;

import java.security.Principal;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

/**
 * This is an abstract class that contains fields and method implementations
 * that are common to any request type class that is environmentally dependent.
 * Currently this includes both servlets (ServletEnvironmentRequest) and
 * portlets (PortletEnvironmentRenderRequest).
 */
public abstract class CommonEnvironmentRequest {
    protected String authType;
    protected String remoteUser;
    protected Principal userPrincipal;
    protected String requestedSessionId;
    protected boolean requestedSessionIdValid;
    protected String scheme;
    protected String serverName;
    protected int serverPort;
    protected boolean secure;
    protected String contextPath;
    protected Map attributes;
    protected Map parameters;

    public String getAuthType() {
        return authType;
    }

    public String getContextPath() {
        return contextPath;
    }

    public String getRemoteUser() {
        return remoteUser;
    }

    public Principal getUserPrincipal() {
        return userPrincipal;
    }

    public String getRequestedSessionId() {
        return requestedSessionId;
    }

    public boolean isRequestedSessionIdValid() {
        return requestedSessionIdValid;
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public Enumeration getAttributeNames() {
        return Collections.enumeration(attributes.keySet());
    }

    public String getParameter(String name) {
        Object value = parameters.get(name);
        if (value instanceof String[]) {
            return ((String[]) value)[0];
        } else {
            return (String) value;
        }
    }

    public Enumeration getParameterNames() {
        return Collections.enumeration(parameters.keySet());
    }

    public String[] getParameterValues(String name) {
        return (String[]) parameters.get(name);
    }

    public Map getParameterMap() {
        return parameters;
    }

    public String getScheme() {
        return scheme;
    }

    public String getServerName() {
        return serverName;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setAttribute(String name, Object value) {
        if (null == value) {
            attributes.remove(name);
        } else {
            attributes.put(name, value);
        }
    }

    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    public Locale getLocale() {
        Enumeration locales = getLocales();
        return locales.hasMoreElements() ? (Locale) locales.nextElement() : Locale.getDefault();
    }

    public abstract Enumeration getLocales();

    public boolean isSecure() {
        return secure;
    }
}
