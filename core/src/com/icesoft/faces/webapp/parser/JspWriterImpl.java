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
import java.io.Writer;

//Stubbed-out class for debugging tags that write to the body

public class JspWriterImpl extends JspWriter {

    private Writer out;

    public JspWriterImpl(Writer out) {
        super(2000, true);
        this.out = out;
    }

    public final void clear() throws IOException {
    }

    public void clearBuffer() throws IOException {
    }

    public void flush() throws IOException {
        if (null != out) {
            out.flush();
        }
    }

    public void close() throws IOException {
        if (null != out) {
            out.close();
        }
        out = null;
    }

    public int getRemaining() {
        return 2000;
    }


    public void write(int c) throws IOException {
        out.write(c);
    }

    public void write(char cbuf[], int off, int len)
            throws IOException {
        out.write(cbuf, off, len);

    }


    public void write(char buf[]) throws IOException {
        write(buf, 0, buf.length);
    }


    public void write(String s, int off, int len) throws IOException {
        out.write(s, off, len);
    }

    public void write(String s) throws IOException {
        write(s, 0, s.length());
    }

    static String lineSeparator = System.getProperty("line.separator");

    public void newLine() throws IOException {
        write(lineSeparator);
    }

    public void print(boolean b) throws IOException {
        write(b ? "true" : "false");
    }

    public void print(char c) throws IOException {
        write(String.valueOf(c));
    }

    public void print(int i) throws IOException {
        write(String.valueOf(i));
    }

    public void print(long l) throws IOException {
        write(String.valueOf(l));
    }

    public void print(float f) throws IOException {
        write(String.valueOf(f));
    }

    public void print(double d) throws IOException {
        write(String.valueOf(d));
    }

    public void print(char s[]) throws IOException {
        write(s);
    }

    public void print(String s) throws IOException {
        if (null == s) {
            s = "null";
        }
        write(s);
    }

    public void print(Object obj) throws IOException {
        write(String.valueOf(obj));
    }

    public void println() throws IOException {
        newLine();
    }

    public void println(boolean x) throws IOException {
        print(x);
        println();
    }

    public void println(char x) throws IOException {
        print(x);
        println();
    }

    public void println(int x) throws IOException {
        print(x);
        println();
    }

    public void println(long x) throws IOException {
        print(x);
        println();
    }

    public void println(float x) throws IOException {
        print(x);
        println();
    }

    public void println(double x) throws IOException {
        print(x);
        println();
    }

    public void println(char x[]) throws IOException {
        print(x);
        println();
    }

    public void println(String x) throws IOException {
        print(x);
        println();
    }

    public void println(Object x) throws IOException {
        print(x);
        println();
    }

}
