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

import javax.servlet.jsp.JspWriter;
import java.io.IOException;

/**
 * This is a stubbed out version of the JspWriter.  Only the mimimum number of
 * members required to support the parser are implemented.
 */
public class StubJspWriter extends JspWriter {

    protected StubJspWriter(int bufferSize, boolean autoFlush) {
        super(bufferSize, autoFlush);
    }

    /* 
     * @see javax.servlet.jsp.JspWriter#getRemaining()
     */
    public int getRemaining() {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.jsp.JspWriter#clear()
     */
    public void clear() throws IOException {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.jsp.JspWriter#clearBuffer()
     */
    public void clearBuffer() throws IOException {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.jsp.JspWriter#close()
     */
    public void close() throws IOException {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.jsp.JspWriter#flush()
     */
    public void flush() throws IOException {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.jsp.JspWriter#newLine()
     */
    public void newLine() throws IOException {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.jsp.JspWriter#println()
     */
    public void println() throws IOException {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.jsp.JspWriter#print(char)
     */
    public void print(char arg0) throws IOException {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.jsp.JspWriter#println(char)
     */
    public void println(char arg0) throws IOException {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.jsp.JspWriter#print(double)
     */
    public void print(double arg0) throws IOException {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.jsp.JspWriter#println(double)
     */
    public void println(double arg0) throws IOException {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.jsp.JspWriter#print(float)
     */
    public void print(float arg0) throws IOException {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.jsp.JspWriter#println(float)
     */
    public void println(float arg0) throws IOException {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.jsp.JspWriter#print(int)
     */
    public void print(int arg0) throws IOException {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.jsp.JspWriter#println(int)
     */
    public void println(int arg0) throws IOException {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.jsp.JspWriter#print(long)
     */
    public void print(long arg0) throws IOException {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.jsp.JspWriter#println(long)
     */
    public void println(long arg0) throws IOException {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.jsp.JspWriter#print(boolean)
     */
    public void print(boolean arg0) throws IOException {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.jsp.JspWriter#println(boolean)
     */
    public void println(boolean arg0) throws IOException {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.jsp.JspWriter#print(char[])
     */
    public void print(char[] arg0) throws IOException {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.jsp.JspWriter#println(char[])
     */
    public void println(char[] arg0) throws IOException {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.jsp.JspWriter#print(java.lang.Object)
     */
    public void print(Object arg0) throws IOException {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.jsp.JspWriter#println(java.lang.Object)
     */
    public void println(Object arg0) throws IOException {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.jsp.JspWriter#print(java.lang.String)
     */
    public void print(String arg0) throws IOException {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see javax.servlet.jsp.JspWriter#println(java.lang.String)
     */
    public void println(String arg0) throws IOException {
        throw new UnsupportedOperationException();
    }

    /* 
     * @see java.io.Writer#write(char[], int, int)
     */
    public void write(char[] arg0, int arg1, int arg2) throws IOException {
        throw new UnsupportedOperationException();
    }
}
