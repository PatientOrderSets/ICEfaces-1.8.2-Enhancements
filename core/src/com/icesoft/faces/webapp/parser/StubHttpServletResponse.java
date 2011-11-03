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

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;


/**
 * This is a stubbed out version of the HttpServletResponse class.  Only the
 * mimimum number of members required by the parser are implemented.
 *
 * @author Adnan Durrani
 */
public class StubHttpServletResponse implements HttpServletResponse {


    /* 
     * @see javax.servlet.ServletResponse#setLocale(java.util.Locale)
     */
    public void setLocale(Locale local) {
    }

    /* 
     * @see javax.servlet.ServletResponse#getLocale()
     */
    public Locale getLocale() {
        throw new UnsupportedOperationException();
    }

    /* 
    * @see javax.servlet.ServletResponse#setContentType(java.lang.String)
    */
    public void setContentType(String contentType) {
        throw new UnsupportedOperationException();
    }

    /* 
    * @see javax.servlet.ServletResponse#getContentType()
    */
    public String getContentType() {
        throw new UnsupportedOperationException();
    }

    /* 
    * @see javax.servlet.ServletResponse#setCharacterEncoding(java.lang.String)
    */
    public void setCharacterEncoding(String characterEncoding) {
        throw new UnsupportedOperationException();
    }

    /* 
    * @see javax.servlet.ServletResponse#getCharacterEncoding()
    */
    public String getCharacterEncoding() {
        throw new UnsupportedOperationException();
    }

    /* 
    * @see javax.servlet.http.HttpServletResponse#encodeUrl(java.lang.String)
    */
    public String encodeUrl(String url) {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.http.HttpServletResponse#encodeURL(java.lang.String)
     */
    public String encodeURL(String url) {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.http.HttpServletResponse#encodeRedirectUrl(java.lang.String)
     */
    public String encodeRedirectUrl(String redirectedUrl) {
        throw new UnsupportedOperationException();
    }


    /* 
     * @see javax.servlet.http.HttpServletResponse#encodeRedirectURL(java.lang.String)
     */
    public String encodeRedirectURL(String redirectedUrl) {
        throw new UnsupportedOperationException();
    }

    /* 
    * @see javax.servlet.http.HttpServletResponse#addCookie(javax.servlet.http.Cookie)
    */
    public void addCookie(Cookie arg0) {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.http.HttpServletResponse#containsHeader(java.lang.String)
     */
    public boolean containsHeader(String arg0) {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.http.HttpServletResponse#sendError(int, java.lang.String)
     */
    public void sendError(int arg0, String arg1) throws IOException {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.http.HttpServletResponse#sendError(int)
     */
    public void sendError(int arg0) throws IOException {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.http.HttpServletResponse#sendRedirect(java.lang.String)
     */
    public void sendRedirect(String arg0) throws IOException {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.http.HttpServletResponse#setDateHeader(java.lang.String, long)
     */
    public void setDateHeader(String arg0, long arg1) {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.http.HttpServletResponse#addDateHeader(java.lang.String, long)
     */
    public void addDateHeader(String arg0, long arg1) {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.http.HttpServletResponse#setHeader(java.lang.String, java.lang.String)
     */
    public void setHeader(String arg0, String arg1) {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.http.HttpServletResponse#addHeader(java.lang.String, java.lang.String)
     */
    public void addHeader(String arg0, String arg1) {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.http.HttpServletResponse#setIntHeader(java.lang.String, int)
     */
    public void setIntHeader(String arg0, int arg1) {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.http.HttpServletResponse#addIntHeader(java.lang.String, int)
     */
    public void addIntHeader(String arg0, int arg1) {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.http.HttpServletResponse#setStatus(int)
     */
    public void setStatus(int arg0) {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.http.HttpServletResponse#setStatus(int, java.lang.String)
     */
    public void setStatus(int arg0, String arg1) {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.ServletResponse#getOutputStream()
     */
    public ServletOutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.ServletResponse#getWriter()
     */
    public PrintWriter getWriter() throws IOException {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.ServletResponse#setContentLength(int)
     */
    public void setContentLength(int arg0) {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.ServletResponse#setBufferSize(int)
     */
    public void setBufferSize(int arg0) {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.ServletResponse#getBufferSize()
     */
    public int getBufferSize() {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.ServletResponse#flushBuffer()
     */
    public void flushBuffer() throws IOException {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.ServletResponse#resetBuffer()
     */
    public void resetBuffer() {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.ServletResponse#isCommitted()
     */
    public boolean isCommitted() {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.ServletResponse#reset()
     */
    public void reset() {
        throw new UnsupportedOperationException();
    }
}
