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

import com.icesoft.faces.util.IteratorEnumeration;

import javax.el.ELContext;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.el.ExpressionEvaluator;
import javax.servlet.jsp.el.VariableResolver;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

/**
 * This is a stubbed out version of the PageContext.  Only the minimum number of
 * members required to support the parser are implemented.
 */
public class StubPageContext extends PageContext {

    private Map attributes = new Hashtable();
    private ExternalContext externalContext;
    private ServletRequest servletRequest;
    private ServletResponse servletResponse;

    public StubPageContext(FacesContext context) {
        this.externalContext = context.getExternalContext();
        this.servletRequest = new StubHttpServletRequest();
        this.servletResponse = new StubHttpServletResponse();
    }

    public void initialize(Servlet servlet, ServletRequest servletRequest, ServletResponse servletResponse, String s, boolean b, int i, boolean b1) throws IOException, IllegalStateException, IllegalArgumentException {
        //do nothing
    }

    /*
    * @see javax.servlet.jsp.JspContext#setAttribute(java.lang.String, java.lang.Object)
    */
    public void setAttribute(String name, Object value) {
        setAttribute(name, value, PAGE_SCOPE);
    }

    /* 
     * @see javax.servlet.jsp.JspContext#setAttribute(java.lang.String, java.lang.Object, int)
     */
    public void setAttribute(String name, Object value, int scope) {
        switch (scope) {
            case PAGE_SCOPE:
                attributes.put(name, value);
                break;
            case REQUEST_SCOPE:
                externalContext.getRequestMap().put(name, value);
                break;
            case SESSION_SCOPE:
                externalContext.getSessionMap().put(name, value);
                break;
            case APPLICATION_SCOPE:
                externalContext.getApplicationMap().put(name, value);
                break;
            default:
                throw new IllegalArgumentException(
                        scope + " scope is not valid");
        }
    }

    /* 
    * @see javax.servlet.jsp.JspContext#getAttribute(java.lang.String)
    */
    public Object getAttribute(String name) {
        return getAttribute(name, PAGE_SCOPE);
    }

    /* 
     * @see javax.servlet.jsp.JspContext#getAttribute(java.lang.String, int)
     */
    public Object getAttribute(String name, int scope) {
        switch (scope) {
            case PAGE_SCOPE:
                return attributes.get(name);
            case REQUEST_SCOPE:
                return externalContext.getRequestMap().get(name);
            case SESSION_SCOPE:
                return externalContext.getSessionMap().get(name);
            case APPLICATION_SCOPE:
                return (externalContext.getApplicationMap().get(name));
            default:
                throw new IllegalArgumentException(
                        scope + " scope is not valid");
        }
    }

    /* 
    * @see javax.servlet.jsp.JspContext#removeAttribute(java.lang.String)
    */
    public void removeAttribute(String name) {
        removeAttribute(name, PAGE_SCOPE);
    }

    /* 
     * @see javax.servlet.jsp.JspContext#removeAttribute(java.lang.String, int)
     */
    public void removeAttribute(String name, int scope) {
        switch (scope) {
            case PAGE_SCOPE:
                attributes.remove(name);
                break;
            case REQUEST_SCOPE:
                externalContext.getRequestMap().remove(name);
                break;
            case SESSION_SCOPE:
                externalContext.getSessionMap().remove(name);
                break;
            case APPLICATION_SCOPE:
                externalContext.getApplicationMap().remove(name);
                break;
            default:
                throw new IllegalArgumentException(
                        scope + " scope is not valid");
        }
    }

    /* 
     * @see javax.servlet.jsp.JspContext#getOut()
     */
    public JspWriter getOut() {
        return new JspWriterImpl(new PrintWriter(System.out));
    }

    /* 
     * @see javax.servlet.jsp.PageContext#getPage()
     */
    public Object getPage() {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.jsp.PageContext#getServletConfig()
     */
    public ServletConfig getServletConfig() {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.jsp.PageContext#getServletContext()
     */
    public ServletContext getServletContext() {
        throw new UnsupportedOperationException();
    }

    public ELContext getELContext() {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.jsp.PageContext#getRequest()
     */
    public ServletRequest getRequest() {
        return servletRequest;
    }

    /* 
     * @see javax.servlet.jsp.PageContext#getResponse()
     */
    public ServletResponse getResponse() {
        return servletResponse;
    }

    /* 
     * @see javax.servlet.jsp.PageContext#getSession()
     */
    public HttpSession getSession() {
        //return null to preserve previous behavior
        return null;
    }

    /* 
    * @see javax.servlet.jsp.PageContext#release()
    */
    public void release() {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.jsp.PageContext#getException()
     */
    public Exception getException() {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.jsp.PageContext#handlePageException(java.lang.Exception)
     */
    public void handlePageException(Exception arg0)
            throws ServletException, IOException {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.jsp.PageContext#forward(java.lang.String)
     */
    public void forward(String arg0) throws ServletException, IOException {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.jsp.PageContext#include(java.lang.String)
     */
    public void include(String arg0) throws ServletException, IOException {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.jsp.PageContext#include(java.lang.String, boolean)
     */
    public void include(String arg0, boolean arg1)
            throws ServletException, IOException {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.jsp.PageContext#handlePageException(java.lang.Throwable)
     */
    public void handlePageException(Throwable arg0)
            throws ServletException, IOException {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.jsp.JspContext#getAttributesScope(java.lang.String)
     */
    public int getAttributesScope(String arg0) {
        throw new UnsupportedOperationException();
    }


    /* 
     * @see javax.servlet.jsp.JspContext#getAttributeNamesInScope(int)
     */
    public Enumeration getAttributeNamesInScope(int scope) {
        switch (scope) {
            case PAGE_SCOPE:
                return new IteratorEnumeration(
                        attributes.keySet().iterator());
            case REQUEST_SCOPE:
                return new IteratorEnumeration(
                        externalContext.getRequestMap().keySet().iterator());
            case SESSION_SCOPE:
                return new IteratorEnumeration(
                        externalContext.getSessionMap().keySet().iterator());
            case APPLICATION_SCOPE:
                return new IteratorEnumeration(
                        externalContext.getApplicationMap()
                                .keySet().iterator());
            default:
                throw new IllegalArgumentException(
                        scope + " scope is not valid");
        }

    }

    /* 
     * @see javax.servlet.jsp.JspContext#getExpressionEvaluator()
     */
    public ExpressionEvaluator getExpressionEvaluator() {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.jsp.JspContext#getVariableResolver()
     */
    public VariableResolver getVariableResolver() {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.jsp.JspContext#findAttribute(java.lang.String)
     */
    public Object findAttribute(String name) {
        Object attribute;

        //check page scope
        attribute = attributes.get(name);
        if (null != attribute) {
            return attribute;
        }

        //check request scope
        attribute = externalContext.getRequestMap().get(name);
        if (null != attribute) {
            return attribute;
        }

        //check session scope
        attribute = externalContext.getSessionMap().get(name);
        if (null != attribute) {
            return attribute;
        }

        //return null or application scope value
        return externalContext.getApplicationMap().get(name);
    }
}
