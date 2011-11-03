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

package com.icesoft.faces.util;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.faces.component.UIComponent;
import javax.faces.context.ResponseWriter;
import java.io.IOException;

public class HalterDump {
    private UIComponent component;
    private ResponseWriter writer;
    private boolean halt = false;
    private boolean resume = true;
    private Node rootNode;
    private Node halter;
    private Node resumer;

    public HalterDump(ResponseWriter writer, UIComponent component, Node root) {
        this.component = component;
        this.writer = writer;
        this.rootNode = root;
    }


    public void streamWrite(Node halter) throws IOException {
        this.halter = halter;
        halt = false;
        dumpNode(rootNode);
    }

    public void dumpNode(Node node) throws IOException {
        if (halt) {
            return;
        }
        if (node.equals(halter)) {
            halt = true;
            resumer = halter;
            halter = null;
        }

        if (resume) {
            startNode(node);
        }

        if (halt) {
            resume = false;
            return;
        }

        NodeList children = node.getChildNodes();
        int length = children.getLength();
        for (int i = 0; i < length; i++) {
            Node nextChildNode = children.item(i);
            dumpNode(nextChildNode);
        }

        if (node.equals(resumer)) {
            resume = true;
            halt = false;
        }

        if (halt) {
            resume = false;
            return;
        }

        if (resume) {
            endNode(node);
        }

    }

    private void startNode(Node node) throws IOException {
        switch (node.getNodeType()) {

            case Node.DOCUMENT_NODE:
                //nothing
                break;

            case Node.ELEMENT_NODE:
                String name = node.getNodeName();

                //XHTML tag should be lower case 
                writer.startElement(node.getNodeName().toLowerCase(),
                                    component);
                NamedNodeMap attributes = node.getAttributes();

                for (int i = 0; i < attributes.getLength(); i++) {
                    Node current = attributes.item(i);
                    //todo: boolean type as well
                    writer.writeAttribute(current.getNodeName(),
                                          current.getNodeValue(),
                                          current.getNodeName());
                }
                break;

            case Node.TEXT_NODE:
                writer.writeText(node.getNodeValue(), "text");
                break;
        }
    }

    private void endNode(Node node) throws IOException {
        switch (node.getNodeType()) {

            case Node.DOCUMENT_NODE:
                //nothing
                break;

            case Node.ELEMENT_NODE:
                String name = node.getNodeName();

                writer.endElement(node.getNodeName().toLowerCase());
                break;

            case Node.TEXT_NODE:
                break;
        }
    }

}
