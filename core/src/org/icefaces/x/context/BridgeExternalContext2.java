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
package org.icefaces.x.context;

import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.HashMap;
import java.util.Locale;

import javax.faces.render.RenderKit;
import javax.faces.FactoryFinder;
import javax.faces.context.ExternalContext;
import javax.faces.context.Flash;
import javax.faces.context.PartialViewContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.net.URL;
import java.net.MalformedURLException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.icesoft.faces.context.BridgeExternalContext;
import com.icesoft.faces.webapp.http.common.Configuration;

/* This skeleton class is intended to allow experimentation with JSF 2.0
*/
public class BridgeExternalContext2 extends BridgeExternalContext  {
    private BridgeExternalContext wrapped = null;

    public BridgeExternalContext2(BridgeExternalContext externalContext)  {
        wrapped = externalContext;
    }

    public void addResponseCookie(String name,
                                  String value,
                                  Map<String, Object> properties) {

            wrapped.addResponseCookie(name, value, properties);
            return;
    }

    public void dispatch(String path) throws IOException {
        wrapped.dispatch(path);
    }

    public String encodeActionURL(String url) {
        return wrapped.encodeActionURL(url);
    }
    
    public String encodeNamespace(String name)  {
        return wrapped.encodeNamespace(name);
    }


    public String encodeResourceURL(String url)  {
        return wrapped.encodeResourceURL(url);
    }
    
    public Map<String, Object> getApplicationMap()  {
        return wrapped.getApplicationMap();
    }

    public String getAuthType()  {
        return wrapped.getAuthType();
    }

    public Flash getFlash() {
//        return wrapped.getFlash();
        return com.sun.faces.context.flash.ELFlash.getFlash(this, true);
    }    

    public String getMimeType(String file) {
        return wrapped.getMimeType(file);
    }
    
    public  Object getContext()  {
        return wrapped.getContext();
    }

    public String getContextName() {
        return wrapped.getContextName();
    }

    public  String getInitParameter(String name)  {
        return wrapped.getInitParameter(name);
    }

    public  Map getInitParameterMap()  {
        return wrapped.getInitParameterMap();
    }

    public String getRemoteUser()  {
        return wrapped.getRemoteUser();
    }

    public  Object getRequest()  {
        return wrapped.getRequest();
    }

    public void setRequest(Object request) {
        wrapped.setRequest(request);
        return;
    }

    public String getRequestScheme() {
        return wrapped.getRequestScheme();
    }

    public String getRequestServerName() {
        return wrapped.getRequestServerName();
    }

    public int getRequestServerPort() {
        return wrapped.getRequestServerPort();
    }
    
    public void setRequestCharacterEncoding(String encoding)  {
        wrapped.setRequestCharacterEncoding(encoding);
        return;
    }

    public String getRealPath(String path) {
        return wrapped.getRealPath(path);
    }

    public  String getRequestContextPath()  {
        return wrapped.getRequestContextPath();
    }

    public  Map<String, Object> getRequestCookieMap()  {
        return wrapped.getRequestCookieMap();
    }

    public Map<String, String> getRequestHeaderMap()  {
        return wrapped.getRequestHeaderMap();
    }

    public Map<String, String []> getRequestHeaderValuesMap()  {
        return wrapped.getRequestHeaderValuesMap();
    }

    public Locale getRequestLocale()  {
        return wrapped.getRequestLocale();
    }

    public Iterator<Locale> getRequestLocales()  {
        return wrapped.getRequestLocales();
    }

    public Map<String, Object> getRequestMap()  {
        return wrapped.getRequestMap();
    }

    public Map<String, String> getRequestParameterMap()  {
        return wrapped.getRequestParameterMap();
    }

    public Iterator<String> getRequestParameterNames()  {
        return wrapped.getRequestParameterNames();
    }

    public Map<String, String []> getRequestParameterValuesMap()  {
        return wrapped.getRequestParameterValuesMap();
    }

    public String getRequestPathInfo()  {
        return wrapped.getRequestPathInfo();
    }

    public String getRequestServletPath()  {
        return wrapped.getRequestServletPath();
    }

    public String getRequestCharacterEncoding() {
        return wrapped.getRequestCharacterEncoding();
    }

    public String getRequestContentType() {
        return wrapped.getRequestContentType();
    }

    public int getRequestContentLength() {
        return wrapped.getRequestContentLength();
    }

    public String getResponseCharacterEncoding() {
        return wrapped.getResponseCharacterEncoding();
    }
    
    public String getResponseContentType() {
        return wrapped.getResponseContentType();
    }

    public  URL getResource(String path) throws MalformedURLException  {
        return wrapped.getResource(path);
    }

    public  InputStream getResourceAsStream(String path)  {
        return wrapped.getResourceAsStream( path);
    }

    public  Set<String> getResourcePaths(String path)  {
        return wrapped.getResourcePaths( path);
    }

    public  Object getResponse()  {
        return wrapped.getResponse();
    }

    public void setResponse(Object response) {
        wrapped.setResponse(response);
        return;
    }

    public OutputStream getResponseOutputStream() throws IOException {
        return wrapped.getResponseOutputStream();
    }

    public Writer getResponseOutputWriter() throws IOException {
        return wrapped.getResponseOutputWriter();
    }
    
    public void setResponseCharacterEncoding(String encoding) {
        wrapped.setResponseCharacterEncoding(encoding);
        return;
    }

    public void setResponseContentType(String contentType) {
        wrapped.setResponseContentType(contentType);
        return;
    }

    public  Object getSession(boolean create)  {
        return wrapped.getSession( create);
    }

    public  Map<String, Object> getSessionMap()  {
        return wrapped.getSessionMap();
    }

    public  Principal getUserPrincipal()  {
        return wrapped.getUserPrincipal();
    }

    public void invalidateSession() {
        wrapped.invalidateSession();
        return;
    }

    public boolean isUserInRole(String role)  {
        return wrapped.isUserInRole( role);
    }

    public void log(String message)  {
        wrapped.log( message);
    }

    public void log(String message, Throwable exception)  {
        wrapped.log( message,  exception);
    }

    public  void redirect(String url)	throws IOException  {
        wrapped.redirect( url);
    }

    public void setResponseHeader(String name, String value) {
        wrapped.setResponseHeader(name, value);
    }

    public void addResponseHeader(String name, String value) {
        wrapped.addResponseHeader(name, value);
    }

    public void setResponseBufferSize(int size) {
        wrapped.setResponseBufferSize(size);
    }


    public int getResponseBufferSize() {
        return wrapped.getResponseBufferSize();
    }

    public boolean isResponseCommitted() {
        return wrapped.isResponseCommitted();
    }

    public void responseReset() {
        wrapped.responseReset();
    }


    public void responseSendError(int statusCode, String message) throws IOException {
        wrapped.responseSendError(statusCode, message);
    }


    public void setResponseStatus(int statusCode) {
        wrapped.setResponseStatus(statusCode);
    }


    public void responseFlushBuffer() throws IOException {
        wrapped.responseFlushBuffer();
    }


    public void setResponseContentLength(int length) {
        wrapped.setResponseContentLength(length);
    }


    public String encodeBookmarkableURL(String baseUrl,
                                        Map<String, List<String>> parameters) {
        return wrapped.encodeBookmarkableURL(baseUrl, parameters);
    }

    public String encodeRedirectURL(String baseUrl,
                                    Map<String,List<String>> parameters) {
        return wrapped.encodeRedirectURL(baseUrl, parameters);
    }

    public String encodePartialActionURL(String url) {
        return wrapped.encodePartialActionURL(url);
    }

    public void removeSeamAttributes()  {
        wrapped.removeSeamAttributes();
    }
    
    public void updateOnPageLoad(Object obj1, Object obj2)  {
        wrapped.updateOnPageLoad(obj1, obj2);
    }
    
    public void update(HttpServletRequest request, HttpServletResponse response)  {
        wrapped.update(request, response);
    }

    public void switchToPushMode()  {
        wrapped.switchToPushMode();
    }

    public void switchToNormalMode()  {
        wrapped.switchToNormalMode();
    }

    public Writer getWriter(String str) throws IOException {
        return wrapped.getWriter(str);
    }

    public String getRequestURI()  {
        return wrapped.getRequestURI();
    }

    public Configuration getConfiguration() {
        return wrapped.getConfiguration();
    }
    
}
