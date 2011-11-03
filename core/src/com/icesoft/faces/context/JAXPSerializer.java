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

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.Writer;
import java.util.Properties;

public class JAXPSerializer implements DOMSerializer {
    private static final TransformerFactory transformerFactory =
            TransformerFactory.newInstance();
    private static Transformer PrettyPrintingTransformer;
    private static Transformer NormalPrintingTransformer;

    static {
        try {
            PrettyPrintingTransformer = transformerFactory
                    .newTransformer(new StreamSource(
                            JAXPSerializer.class.getResourceAsStream(
                                    "pretty-printing.xslt")));
            NormalPrintingTransformer = transformerFactory
                    .newTransformer(new StreamSource(
                            JAXPSerializer.class.getResourceAsStream(
                                    "normal-printing.xslt")));
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private Result result;
    private Properties properties = new Properties();
    private Transformer transformer = NormalPrintingTransformer;

    public JAXPSerializer(Writer writer) {
        this.result = new StreamResult(writer);
    }

    public JAXPSerializer(Writer writer, String publicId, String systemId) {
        this.result = new StreamResult(writer);
        properties.setProperty(OutputKeys.DOCTYPE_PUBLIC, publicId);
        properties.setProperty(OutputKeys.DOCTYPE_SYSTEM, systemId);
        properties.setProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
    }

    public void serialize(Document document) throws IOException {
        try {
            transformer.setOutputProperties(properties);
            transformer.transform(new DOMSource(document), result);
        } catch (TransformerException e) {
            throw new IOException(e.getMessage());
        }
    }

    public void serialize(Node node) throws IOException {
        try {
            transformer.setOutputProperties(properties);
            transformer.transform(new DOMSource(node), result);
        } catch (TransformerException e) {
            throw new IOException(e.getMessage());
        }
    }

    public void printPretty() {
        transformer = PrettyPrintingTransformer;
    }

    public void outputAsHTML() {
        properties.setProperty(OutputKeys.METHOD, "html");
    }

    public void outputAsXML() {
        properties.setProperty(OutputKeys.METHOD, "xml");
    }

    public void ommitXMLDeclaration() {
        properties.setProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    }
}
