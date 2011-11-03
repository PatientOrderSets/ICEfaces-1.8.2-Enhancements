/*
 *  Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 *  "The contents of this file are subject to the Mozilla Public License
 *  Version 1.1 (the "License"); you may not use this file except in
 *  compliance with the License. You may obtain a copy of the License at
 *  http://www.mozilla.org/MPL/
 *
 *  Software distributed under the License is distributed on an "AS IS"
 *  basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 *  License for the specific language governing rights and limitations under
 *  the License.
 *
 *  The Original Code is ICEfaces 1.5 open source software code, released
 *  November 5, 2006. The Initial Developer of the Original Code is ICEsoft
 *  Technologies Canada, Corp. Portions created by ICEsoft are Copyright (C)
 *  2004-2006 ICEsoft Technologies Canada, Corp. All Rights Reserved.
 *
 *  Contributor(s): _____________________.
 *
 *  Alternatively, the contents of this file may be used under the terms of
 *  the GNU Lesser General Public License Version 2.1 or later (the "LGPL"
 *  License), in which case the provisions of the LGPL License are
 *  applicable instead of those above. If you wish to allow use of your
 *  version of this file only under the terms of the LGPL License and not to
 *  allow others to use your version of this file under the MPL, indicate
 *  your decision by deleting the provisions above and replace them with
 *  the notice and other provisions required by the LGPL License. If you do
 *  not delete the provisions above, a recipient may use your version of
 *  this file under either the MPL or the LGPL License."
 *
 */

package com.icesoft.jsfmeta.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;


public class JavaSourceWriter extends Writer {


	private int currentIndent = 0;

	private int indentAmount = 4;

	private Writer outputWriter = null;


	public JavaSourceWriter() {

		outputWriter = new PrintWriter(System.out);
	}

	public void setOutputWriter(Writer outputWriter) {
		this.outputWriter = outputWriter;
	}

	public int getCurrentIndent() {
		return currentIndent;
	}

	public void setIndentAmount(int indentAmount) {
		this.indentAmount = indentAmount;
	}

	public void indent() {
		currentIndent += indentAmount;
	}

	public void outdent() {
		currentIndent -= indentAmount;
		if (currentIndent < 0)
			currentIndent = 0;
	}

	private void doIndent() throws IOException {
		for (int i = 0; i < currentIndent; i++){
			outputWriter.write(32);
		}
	}

	public void write(char cbuf[], int off, int len) throws IOException {
		outputWriter.write(cbuf, off, len);
	}

	public void flush() throws IOException {
		outputWriter.flush();
	}

	public void close() throws IOException {
		outputWriter.close();
	}

	public void startSource() throws IOException {
		currentIndent = 0;
	}

	public void endSource() throws IOException {
		currentIndent = 0;
	}

	public void startJavaDoc() throws IOException {
		doIndent();
		outputWriter.write("/**");
		outputWriter.write(10);
	}

	public void emitJavaDoc() throws IOException {
		doIndent();
		outputWriter.write(" *");
		outputWriter.write(10);
	}

	public void emitJavaDoc(String comment) throws IOException {
		doIndent();
		outputWriter.write(" * " + comment);
		emitNewline();
	}

	public void emitJavaDoc(String comment, int width) throws IOException {
		int actualWidth = width - (3 + currentIndent);
		int index = 0;
		char buffer[] = comment.toCharArray();
		int pos = 0;
		while (index < buffer.length) {
			if (pos == 0) {
				doIndent();
				outputWriter.write(" * ");
			}
			if (pos < actualWidth) {
				outputWriter.write(buffer[index++]);
				pos++;
			} else {
				while (index < buffer.length && buffer[index] != ' '){
					outputWriter.write(buffer[index++]);
				}
				while (index < buffer.length && buffer[index] == ' '){
					outputWriter.write(buffer[index++]);
				}
				if (index < buffer.length){
					emitNewline();
				}
				pos = 0;
			}
		}
		emitNewline();
	}

	public void emitJavaDocMultiLine(String comment) throws IOException {
		if (comment == null)
			return;
		boolean start = true;
		for (int i = 0; i < comment.length(); i++) {
			char ch = comment.charAt(i);
			if (ch == '\r')
				continue;
			if (start) {
				outputWriter.write(" * ");
				start = false;
			}
			outputWriter.write(ch);
			if (ch == '\n')
				start = true;
		}

		if (!start){
			//new line
			emitNewline();
		}
	}

	public void endJavaDoc() throws IOException {
		doIndent();
		outputWriter.write(" */");
		emitNewline();
	}

	public void emitNewline() throws IOException {
		outputWriter.write(10);
	}

	public void emitPackage(String name) throws IOException {
		doIndent();
		outputWriter.write("package " + name + ";");
		emitNewline();
	}

        //TODO: pick up from file
	public void emitLicense() throws IOException{
            
		String LICENSE_STRING = "/*\n" +
            " * Version: MPL 1.1/GPL 2.0/LGPL 2.1\n" +
            " *\n" +
            " * \"The contents of this file are subject to the Mozilla Public License\n" +
            " * Version 1.1 (the \"License\"); you may not use this file except in\n" +
            " * compliance with the License. You may obtain a copy of the License at\n" +
            " * http://www.mozilla.org/MPL/\n" +
            " *\n" +
            " * Software distributed under the License is distributed on an \"AS IS\"\n" +
            " * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the\n" +
            " * License for the specific language governing rights and limitations under\n" +
            " * the License.\n" +
            " *\n" +
            " * The Original Code is ICEfaces 1.5 open source software code, released\n" +
            " * November 5, 2006. The Initial Developer of the Original Code is ICEsoft\n" +
            " * Technologies Canada, Corp. Portions created by ICEsoft are Copyright (C)\n" +
            " * 2004-2006 ICEsoft Technologies Canada, Corp. All Rights Reserved.\n" +
            " *\n" +
            " * Contributor(s): _____________________.\n" +
            " *\n" +
            " * Alternatively, the contents of this file may be used under the terms of\n" +
            " * the GNU Lesser General Public License Version 2.1 or later (the \"LGPL\"\n" +
            " * License), in which case the provisions of the LGPL License are\n" +
            " * applicable instead of those above. If you wish to allow use of your\n" +
            " * version of this file only under the terms of the LGPL License and not to\n" +
            " * allow others to use your version of this file under the MPL, indicate\n" +
            " * your decision by deleting the provisions above and replace them with\n" +
            " * the notice and other provisions required by the LGPL License. If you do\n" +
            " * not delete the provisions above, a recipient may use your version of\n" +
            " * this file under either the MPL or the LGPL License.\"\n" +
            " *\n" +
            " */";

		outputWriter.write(LICENSE_STRING);
		emitNewline();
	}

	public void emitImport(String name) throws IOException {
		doIndent();
		outputWriter.write("import " + name + ";");
		emitNewline();
	}

	public void startClass(String name, String superClass, String interfaces[])
			throws IOException {
		startClass(name, superClass, interfaces, true, false);
	}

	public void startClass(String name, String superClass, String interfaces[],
			boolean _public, boolean _abstract) throws IOException {
		doIndent();
		if (_public)
			outputWriter.write("public ");
		if (_abstract)
			outputWriter.write("abstract ");
		outputWriter.write("class " + name);
		if (superClass != null)
			outputWriter.write(" extends " + superClass);
		if (interfaces != null) {
			outputWriter.write(" implements");
			for (int i = 0; i < interfaces.length; i++) {
				if (i > 0)
					outputWriter.write(44);
				outputWriter.write(" " + interfaces[i]);
			}

		}
		outputWriter.write(" {");
		emitNewline();
		indent();
	}

	public void endClass() throws IOException {
		outdent();
		doIndent();
		outputWriter.write("}");
		emitNewline();
	}

	public void startMethod(String name, String retType, String parmTypes[],
			String parmNames[]) throws IOException {
		startMethod(name, retType, parmTypes, parmNames, "public");
	}

	public void startMethod(String name, String retType, String parmTypes[],
			String parmNames[], String scope) throws IOException {
		doIndent();
		if (scope != null)
			outputWriter.write(scope);
		if (retType != null)
			outputWriter.write(" " + retType);
		outputWriter.write(" " + name + "(");
		if (parmTypes != null) {
			if (parmTypes.length != parmNames.length)
				throw new IOException("Oops");
			for (int i = 0; i < parmTypes.length; i++) {
				if (i > 0)
					outputWriter.write(44);
				outputWriter.write(parmTypes[i] + " " + parmNames[i]);
			}

		}
		outputWriter.write(") {");
		emitNewline();
		indent();
	}

	public void endMethod() throws IOException {
		outdent();
		doIndent();
		outputWriter.write("}");
		emitNewline();
	}

	public void emitField(String type, String name, String value)
			throws IOException {
		doIndent();
		outputWriter.write("private " + type + " " + name);
		if (value != null){
			outputWriter.write(" = \"" + value + "\"");
		}
		outputWriter.write(";");
		emitNewline();
	}

	public void emitStaticField(boolean isFinal, String type, String name,
			String expression) throws IOException {
		doIndent();
		outputWriter.write("protected static ");
		if (isFinal){
			outputWriter.write("final ");
		}
		outputWriter.write(type);
		outputWriter.write(32);
		outputWriter.write(name);
		if (expression != null){
			outputWriter.write(" = " + expression + "");
		}
		outputWriter.write(";");
		emitNewline();
	}

	public void emitExpression(String expr, boolean newline) throws IOException {
		doIndent();
		outputWriter.write(expr);
		if (newline){
			emitNewline();
		}
	}

	public void emitExpressionPart(String expr) throws IOException {
		outputWriter.write(expr);
	}

	public void emitCommentLine() throws IOException {
		doIndent();
		outputWriter.write("//");
		emitNewline();
	}

	public void emitCommentLine(String comment) throws IOException {
		doIndent();
		outputWriter.write("// " + comment);
		emitNewline();
	}

	public void emitJavaString(String string) throws IOException {
		outputWriter.write("\"");
		boolean eatingWhite = false;
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			if (eatingWhite) {
				if (Character.isWhitespace(c))
					continue;
				eatingWhite = false;
			}
			if (c == '"') {
				outputWriter.write("\\\"");
				continue;
			}
			if (c == '\n') {
				outputWriter.write(" ");
				eatingWhite = true;
			} else {
				outputWriter.write(c);
			}
		}

		outputWriter.write("\"");
	}

	public static String toJavaString(String s) {
		StringBuffer sb = new StringBuffer(s.length() + 10);
		sb.append("\"");
		boolean eatingWhite = false;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (eatingWhite) {
				if (Character.isWhitespace(c))
					continue;
				eatingWhite = false;
			}
			if (c == '"') {
				sb.append("\\\"");
				continue;
			}
			if (c == '\n') {
				sb.append(" ");
				eatingWhite = true;
			} else {
				sb.append(c);
			}
		}

		sb.append("\"");
		return sb.toString();
	}

}

