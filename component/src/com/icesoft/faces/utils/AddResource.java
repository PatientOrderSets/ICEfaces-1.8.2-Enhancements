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

package com.icesoft.faces.utils;

import com.icesoft.faces.context.DOMContext;
import com.icesoft.faces.renderkit.dom_html_basic.HTML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import java.util.Iterator;
import java.util.List;


/**
 * This utility class uses DOM methods to render javascript and css resources to
 * the page.
 *
 * @author gmccleary
 */
public final class AddResource {

    private static final int TEXT_NODE_TYPE = 3;
    private static final String LINK_ELM = "link";

    private AddResource() {

    }

    // Methods to Add Javascript and CSS resources

    /**
     * Adds the given javascript to the given node
     *
     * @param parentNode
     * @param resourceDirectory
     * @param scriptFileName
     * @param domContext
     */
    public static void addJavaScriptToNode(Node parentNode,
                                           String resourceDirectory,
                                           String scriptFileName,
                                           DOMContext domContext) {
        // scan the given node for script elements
        Element jsElement = null;
        List scripts = DOMContext.findChildrenWithNodeName((Element) parentNode,
                                                           HTML.SCRIPT_ELEM);
        Iterator scriptIterator = scripts.iterator();

        while (scriptIterator.hasNext()) {
            Element next = (Element) scriptIterator.next();
            if (next.getAttribute("src")
                    .equalsIgnoreCase(resourceDirectory + scriptFileName)) {
                jsElement = next;
            }
        }
        // if js script already exists, then don't add it
        if (jsElement == null) {
            jsElement =
                    domContext.getDocument().createElement(HTML.SCRIPT_ELEM);
            jsElement.setAttribute(HTML.SRC_ATTR,
                                   resourceDirectory + scriptFileName);
            jsElement.setAttribute("language", "javascript");

            parentNode.appendChild(jsElement);
        }
    }

    /**
     * Adds the given javascript resource to the head node
     *
     * @param resourceDirectory
     * @param scriptFileName
     * @param domContext
     */
    public static void addJavaScriptToHead(String resourceDirectory,
                                           String scriptFileName,
                                           DOMContext domContext) {
        // extract the head node from the document
        Document test = domContext.getDocument();
        NodeList heads = test.getElementsByTagName("head");
        Node headNode = null;
        if (heads.getLength() > 0) {
            headNode = heads.item(0);
        } else { // no head exists , create one
            Element head = domContext.createElement("head");
            test.appendChild(head);
            headNode = (Node) head;
        }
        addJavaScriptToNode(headNode, resourceDirectory, scriptFileName,
                            domContext);
    }

    /**
     * Adds the given stylesheet resource to the head
     *
     * @param resourceDirectory
     * @param cssFileName
     * @param domContext
     */
    public static void addStyleSheetToHead(String resourceDirectory,
                                           String cssFileName,
                                           DOMContext domContext) {

        Document test = domContext.getDocument();

        NodeList heads = test.getElementsByTagName("head");

        Node headNode = heads.item(0);

        // if stylesheet link already exists, then don't add it
        Element styleElement = null;
        List links = DOMContext
                .findChildrenWithNodeName((Element) headNode, LINK_ELM);
        Iterator linkIterator = links.iterator();

        while (linkIterator.hasNext()) {
            Element next = (Element) linkIterator.next();
            if (next.getAttribute("href")
                    .equalsIgnoreCase(resourceDirectory + cssFileName)) {
                styleElement = next;
            }
        }

        if (styleElement == null) {
            styleElement = domContext.getDocument().createElement(LINK_ELM);
            styleElement.setAttribute(HTML.HREF_ATTR,
                                      resourceDirectory + cssFileName);
            styleElement.setAttribute("type", "text/css");
            styleElement.setAttribute("rel", "stylesheet");

            headNode.appendChild(styleElement);
        }
    }

    /**
     * Adds the given inline style to the head
     *
     * @param inlineStyle
     * @param domContext
     */
    public static void addInlineStyleToHead(String inlineStyle,
                                            DOMContext domContext) {
        // extract the head from the document element
        Document test = domContext.getDocument();
        NodeList heads = test.getElementsByTagName("head");
        Node headNode = heads.item(0);

        // extract style elements from head
        Element styleElement = null;
        List styles = DOMContext
                .findChildrenWithNodeName((Element) headNode, HTML.STYLE_ELEM);
        Iterator styleIterator = styles.iterator();

        while (styleIterator.hasNext()) {
            Element next = (Element) styleIterator.next();
            if (next.hasChildNodes()) {
                if (next.getFirstChild().getNodeType() == TEXT_NODE_TYPE) {
                    Text styleText = (Text) next.getFirstChild();
                    if (styleText.getNodeValue()
                            .equalsIgnoreCase(inlineStyle)) {
                        styleElement = next;
                    }
                }
            }
        }
        // if inline style already exists, then don't add it
        if (styleElement == null) {
            styleElement =
                    domContext.getDocument().createElement(HTML.STYLE_ELEM);
            styleElement.setAttribute("type", "text/css");
            styleElement.setAttribute("rel", "stylesheet");
            Text inlineText = domContext.createTextNode(inlineStyle);
            styleElement.appendChild(inlineText);
            headNode.appendChild(styleElement);
        }
    }

}
