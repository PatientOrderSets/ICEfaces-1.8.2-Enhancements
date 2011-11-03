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

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import javax.faces.component.UIComponent;
import java.util.List;
import java.util.Vector;
import java.util.HashSet;
import java.util.Arrays;
import java.io.Writer;
import java.io.IOException;
import java.io.StringWriter;

public class DOMUtils {
    private static HashSet TAGS_THAT_CAN_CLOSE_SHORT = new HashSet(
            Arrays.asList(new String[] {
                "img", "input", "br", "hr", "meta", 
                "base", "link", "frame", "col", "area"
            }));
    private static HashSet TAGS_THAT_ALLOW_NEWLINE = new HashSet(
            Arrays.asList(new String[] {
                "img", "input", "td" 
            }));


    public static String DocumentTypetoString(String publicID, String systemID,
                                              String root) {
        return "<!DOCTYPE " + root + " PUBLIC \"" + publicID + "\" \"" +
                systemID + "\">";
    }

    public static String nodeToString(Node node) throws IOException {
        StringWriter writer = new StringWriter();
        try {
            printNode(node, writer);
        } finally {
            writer.flush();
            return writer.toString();
        }
    }

    public static String childrenToString(Node node) throws IOException {
        StringWriter writer = new StringWriter();
        try {
            printChildNodes(node, writer);
        } finally {
            writer.flush();
            return writer.toString();
        }
    }

    public static void printChildNodes(Node node, Writer writer) throws IOException {
        NodeList children = node.getChildNodes();
        int l = children.getLength();
        for (int i = 0; i < l; i++) {
            printNode(children.item(i), writer);
        }
    }

    public static void printNode(Node node, Writer writer) throws IOException {
        printNode(node, writer, 0, true, false);
    }

    private static void printNode(
            Node node, Writer writer,
            int depth, boolean allowAddingWhitespace,
            boolean addTrailingNewline) throws IOException {

        switch (node.getNodeType()) {

            case Node.DOCUMENT_NODE:
                //writer.write("<xml version=\"1.0\">\n");
                // recurse on each child
                NodeList nodes = node.getChildNodes();
                if (nodes != null) {
                    for (int i = 0; i < nodes.getLength(); i++) {
                        printNode(nodes.item(i), writer, depth + 1,
                                allowAddingWhitespace, false);
                    }
                }
                break;

            case Node.ELEMENT_NODE:
                String name = node.getNodeName();
                //#2393 removed limited test for <br>
                
                writer.write("<");
                writer.write(name);
                NamedNodeMap attributes = node.getAttributes();
                for (int i = 0; i < attributes.getLength(); i++) {
                    Node current = attributes.item(i);
                    writer.write(" ");
                    writer.write(current.getNodeName());
                    writer.write("=\"");
                    writer.write(escapeAnsi(current.getNodeValue()));
                    writer.write("\"");
                }


                // #2393 allow short closing of certain tags
                if ( !node.hasChildNodes() && xmlShortClosingAllowed(node) ) {
                    writer.write(" />");
                    break;
                }                        

                writer.write(">");
                // recurse on each child
                NodeList children = node.getChildNodes();

                if (children != null) {
                    int childrenLength = children.getLength();
                    for (int i = 0; i < childrenLength; i++) {
                        boolean childAddTrailingNewline = false;
                        if (allowAddingWhitespace) {
                            if ((i + 1) < childrenLength) {
                                Node nextChild = children.item(i + 1);
                                // We don't add the newline if the next tag is a TD,
                                // because when rendering our tabbedPane, if there's
                                // any whitespace between the adjacent TDs, then
                                // Internet Explorer will add vertical spacing
                                // Also same for some other tags to avoid extra space (JIRA ICE-1351)
                                childAddTrailingNewline =
                                        !isWhitespaceText(nextChild) && isNewlineAllowedTag(nextChild);
                            }
                        }
                        printNode(children.item(i), writer, depth + 1,
                                allowAddingWhitespace,
                                childAddTrailingNewline);
                    }
                }

                writer.write("</");
                writer.write(name);
                writer.write(">");
                if (allowAddingWhitespace && addTrailingNewline)
                    writer.write("\n");
                break;

            case Node.TEXT_NODE:
                writer.write(node.getNodeValue());
                break;
        }
    }

    private static boolean isWhitespaceText(Node node) {
        if (node.getNodeType() == Node.TEXT_NODE) {
            String val = node.getNodeValue();
            // Treat an empty string like whitespace
            for (int i = val.length() - 1; i >= 0; i--) {
                if (!Character.isWhitespace(val.charAt(i)))
                    return false;
            }
            return true;
        }
        return false;
    }

    private static boolean isNewlineAllowedTag(Node node) {
        short nodeType = node.getNodeType();
        String nodeName = node.getNodeName().toLowerCase();
        return !(nodeType == Node.ELEMENT_NODE && 
                TAGS_THAT_ALLOW_NEWLINE.contains(nodeName) );
    }

    private static boolean isTD(Node node) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            String name = node.getNodeName();
            if (name != null && name.equalsIgnoreCase("td"))
                return true;
        }
        return false;
    }

    /**
     * Check if short closing form is allowed. Short closing is of the form
     * <code> <xxx /></code> 
     * @param node Node
     * @return true if allowed
     */
    private static boolean xmlShortClosingAllowed(Node node) {
        short nodeType = node.getNodeType();
        String nodeName = node.getNodeName().toLowerCase();
        return (nodeType == Node.ELEMENT_NODE &&
                TAGS_THAT_CAN_CLOSE_SHORT.contains(nodeName));
    }

    /* Return the first child of the given nodeName under the given node.
     * @param node node to search under
     * @param name nodeName to search for
     */
    public static Node getChildByNodeName(Node node, String name) {
        NodeList children = node.getChildNodes();
        Node child;
        int l = children.getLength();
        for (int i = 0; i < l; i++) {
            child = children.item(i);
            if (child.getNodeName().equalsIgnoreCase(name)) {
                return child;
            }
        }
        return null;
    }

    /**
     * Determine the set of top-level nodes in newDOM that are different from
     * the corresponding nodes in oldDOM.
     *
     * @param oldDOM original dom Document
     * @param newDOM changed dom Document
     * @return array of top-level nodes in newDOM that differ from oldDOM
     */
    public static Node[] domDiff(Document oldDOM, Document newDOM) {
        List nodeDiffs = new Vector();
        compareNodes(nodeDiffs, oldDOM.getDocumentElement(),
                newDOM.getDocumentElement());
        return ((Node[]) nodeDiffs.toArray(new Node[0]));
    }


    /**
     * Nodes are equivalent if they have the same names, attributes, and
     * children
     *
     * @param oldNode
     * @param newNode
     * @return true if oldNode and newNode are equivalent
     */
    public static boolean compareNodes(List nodeDiffs,
                                       Node oldNode, Node newNode) {
        if (!oldNode.getNodeName().equals(newNode.getNodeName())) {
            //parent node needs to fix this
            nodeDiffs.add(newNode.getParentNode());
            return false;
        }
        if (!compareIDs(oldNode, newNode)) {
            //parent node needs to fix this
            nodeDiffs.add(newNode.getParentNode());
            return false;
        }
        if (!compareAttributes(oldNode, newNode)) {
            nodeDiffs.add(newNode);
            return false;
        }
        if (!compareStrings(oldNode.getNodeValue(),
                newNode.getNodeValue())) {
            //might not have an id
            nodeDiffs.add(newNode);
            return false;
        }

        NodeList oldChildNodes = oldNode.getChildNodes();
        NodeList newChildNodes = newNode.getChildNodes();

        int oldChildLength = oldChildNodes.getLength();
        int newChildLength = newChildNodes.getLength();

        if (oldChildLength != newChildLength) {
            nodeDiffs.add(newNode);
            return false;
        }

        boolean allChildrenMatch = true;
        for (int i = 0; i < newChildLength; i++) {
            if (!compareNodes(nodeDiffs, oldChildNodes.item(i),
                    newChildNodes.item(i))) {
                allChildrenMatch = false;
            }
        }

        return allChildrenMatch;
    }

    private static boolean compareStrings(String oldString, String newString) {
        if ((null == oldString) && (null == newString)) {
            return true;
        }
        try {
            return (oldString.equals(newString));
        } catch (NullPointerException e) {
        }
        return false;
    }

    /**
     * @param oldNode
     * @param newNode
     * @return true if Nodes have the same IDs
     */
    public static boolean compareIDs(Node oldNode, Node newNode) {
        if (!(oldNode instanceof Element) &&
                !(newNode instanceof Element)) {
            //both do not have an ID
            return true;
        }
        try {
            return ((Element) oldNode).getAttribute("id").equals(
                    ((Element) newNode).getAttribute("id"));
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * @param oldNode
     * @param newNode
     * @return true if Nodes have the same attributes
     */
    public static boolean compareAttributes(Node oldNode, Node newNode) {
        boolean oldHasAttributes = oldNode.hasAttributes();
        boolean newHasAttributes = newNode.hasAttributes();

        if (!oldHasAttributes && !newHasAttributes) {
            return true;
        }
        if (oldHasAttributes != newHasAttributes) {
            return false;
        }

        NamedNodeMap oldMap = oldNode.getAttributes();
        NamedNodeMap newMap = newNode.getAttributes();

        int oldLength = oldMap.getLength();
        int newLength = newMap.getLength();

        if (oldLength != newLength) {
            return false;
        }

        Node newAttribute = null;
        Node oldAttribute = null;
        for (int i = 0; i < newLength; i++) {
            newAttribute = newMap.item(i);
            oldAttribute = oldMap.getNamedItem(newAttribute.getNodeName());
            if (null == oldAttribute) {
                return false;
            }
            if (!(String.valueOf(oldAttribute.getNodeValue()).equals(
                    String.valueOf(newAttribute.getNodeValue())))) {
                return false;
            }
        }

        return true;

    }

    public static Element ascendToNodeWithID(Node node) {
        while (null != node) {
            if (node instanceof Element) {
                String id = ((Element) node).getAttribute("id");
                if ((null != id) && (!"".equals(id))) {
                    return (Element) node;
                }
            }
            node = node.getParentNode();
        }
        //it's going to be null at this point
        return (Element) node;
    }

    /**
     * Escaping is required unless the escape attribute is present and is
     * "false"
     *
     * @param uiComponent
     * @return
     */
    public static boolean escapeIsRequired(UIComponent uiComponent) {
        Object escapeAttribute = uiComponent.getAttributes().get("escape");
        if (escapeAttribute != null) {
            if (escapeAttribute instanceof String) {
                return Boolean.valueOf((String) escapeAttribute).booleanValue();
            } else if (escapeAttribute instanceof Boolean) {
                return ((Boolean) escapeAttribute).booleanValue();
            }
        }
        return true; //default
    }

    public static String escapeAnsi(String text) {
        if (null == text) {
            return "";
        }
        char[] chars = text.toCharArray();
        StringBuffer buffer = new StringBuffer(chars.length);
        for (int index = 0; index < chars.length; index++) {
            char ch = chars[index];
            //see: http://www.w3schools.com/tags/ref_ascii.asp
            if (ch <= 31) {
                if (ch == '\t' || ch == '\n' || ch == '\r') {
                    buffer.append(ch);
                }
                //skip any other control character
            } else if (ch == 127) {
                //skip 'escape' character
            } else if (ch == '>') {
                buffer.append("&gt;");
            } else if (ch == '<') {
                buffer.append("&lt;");
            } else if (ch == '&') {
                buffer.append("&amp;");
            } else if (ch == '\'') {
                buffer.append("&#39;");
            } else if (ch == '"') {
                buffer.append("&quot;");
            } else if (ch >= 0xA0 && ch <= 0xff) {
                buffer.append("&").append(escapeAnsi(ch)).append(";");
            } else if (ch == 0x20AC) {//special case for euro symbol
                buffer.append("&euro;");
            } else {
                buffer.append(ch);
            }
        }

        return buffer.toString();
    }

    /**
     * @param character
     * @return
     */
    private static String escapeAnsi(char character) {
        int indexOfEscapedCharacter = character - 0xA0;
        return ansiCharacters[indexOfEscapedCharacter];
    }

    /**
     * from http://www.w3.org/TR/REC-html40/sgml/entities.html
     * Portions Copyright International Organization for Standardization 1986
     * Permission to copy in any form is granted for use with
     * conforming SGML systems and applications as defined in
     * ISO 8879, provided this notice is included in all copies.
     */
    private static String[] ansiCharacters = new String[]{
            "nbsp"
            /* "&#160;" -- no-break space = non-breaking space, U+00A0 ISOnum -->*/,
            "iexcl"  /* "&#161;" -- inverted exclamation mark, U+00A1 ISOnum */,
            "cent"   /* "&#162;" -- cent sign, U+00A2 ISOnum */,
            "pound"  /* "&#163;" -- pound sign, U+00A3 ISOnum */,
            "curren" /* "&#164;" -- currency sign, U+00A4 ISOnum */,
            "yen"    /* "&#165;" -- yen sign = yuan sign, U+00A5 ISOnum */,
            "brvbar"
            /* "&#166;" -- broken bar = broken vertical bar, U+00A6 ISOnum */,
            "sect"   /* "&#167;" -- section sign, U+00A7 ISOnum */,
            "uml"
            /* "&#168;" -- diaeresis = spacing diaeresis, U+00A8 ISOdia */,
            "copy"   /* "&#169;" -- copyright sign, U+00A9 ISOnum */,
            "ordf"
            /* "&#170;" -- feminine ordinal indicator, U+00AA ISOnum */,
            "laquo"
            /* "&#171;" -- left-pointing double angle quotation mark = left pointing guillemet, U+00AB ISOnum */,
            "not"    /* "&#172;" -- not sign, U+00AC ISOnum */,
            "shy"
            /* "&#173;" -- soft hyphen = discretionary hyphen, U+00AD ISOnum */,
            "reg"
            /* "&#174;" -- registered sign = registered trade mark sign, U+00AE ISOnum */,
            "macr"
            /* "&#175;" -- macron = spacing macron = overline = APL overbar, U+00AF ISOdia */,
            "deg"    /* "&#176;" -- degree sign, U+00B0 ISOnum */,
            "plusmn"
            /* "&#177;" -- plus-minus sign = plus-or-minus sign, U+00B1 ISOnum */,
            "sup2"
            /* "&#178;" -- superscript two = superscript digit two = squared, U+00B2 ISOnum */,
            "sup3"
            /* "&#179;" -- superscript three = superscript digit three = cubed, U+00B3 ISOnum */,
            "acute"
            /* "&#180;" -- acute accent = spacing acute, U+00B4 ISOdia */,
            "micro"  /* "&#181;" -- micro sign, U+00B5 ISOnum */,
            "para"
            /* "&#182;" -- pilcrow sign = paragraph sign, U+00B6 ISOnum */,
            "middot"
            /* "&#183;" -- middle dot = Georgian comma = Greek middle dot, U+00B7 ISOnum */,
            "cedil"  /* "&#184;" -- cedilla = spacing cedilla, U+00B8 ISOdia */,
            "sup1"
            /* "&#185;" -- superscript one = superscript digit one, U+00B9 ISOnum */,
            "ordm"
            /* "&#186;" -- masculine ordinal indicator, U+00BA ISOnum */,
            "raquo"
            /* "&#187;" -- right-pointing double angle quotation mark = right pointing guillemet, U+00BB ISOnum */,
            "frac14"
            /* "&#188;" -- vulgar fraction one quarter = fraction one quarter, U+00BC ISOnum --> */,
            "frac12"
            /* "&#189;" -- vulgar fraction one half = fraction one half, U+00BD ISOnum */,
            "frac34"
            /* "&#190;" -- vulgar fraction three quarters = fraction three quarters, U+00BE ISOnum */,
            "iquest"
            /* "&#191;" -- inverted question mark = turned question mark, U+00BF ISOnum */,
            "Agrave"
            /* "&#192;" -- latin capital letter A with grave = latin capital letter A grave, U+00C0 ISOlat1 */,
            "Aacute"
            /* "&#193;" -- latin capital letter A with acute, U+00C1 ISOlat1 */,
            "Acirc"
            /* "&#194;" -- latin capital letter A with circumflex, U+00C2 ISOlat1 */,
            "Atilde"
            /* "&#195;" -- latin capital letter A with tilde, U+00C3 ISOlat1 */,
            "Auml"
            /* "&#196;" -- latin capital letter A with diaeresis, U+00C4 ISOlat1 */,
            "Aring"
            /* "&#197;" -- latin capital letter A with ring above = latin capital letter A ring, U+00C5 ISOlat1 --> */,
            "AElig"
            /* "&#198;" -- latin capital letter AE = latin capital ligature AE, U+00C6 ISOlat1 --> */,
            "Ccedil"
            /* "&#199;" -- latin capital letter C with cedilla, U+00C7 ISOlat1 */,
            "Egrave"
            /* "&#200;" -- latin capital letter E with grave, U+00C8 ISOlat1 */,
            "Eacute"
            /* "&#201;" -- latin capital letter E with acute, U+00C9 ISOlat1 */,
            "Ecirc"
            /* "&#202;" -- latin capital letter E with circumflex, U+00CA ISOlat1 */,
            "Euml"
            /* "&#203;" -- latin capital letter E with diaeresis, U+00CB ISOlat1 */,
            "Igrave"
            /* "&#204;" -- latin capital letter I with grave, U+00CC ISOlat1 */,
            "Iacute"
            /* "&#205;" -- latin capital letter I with acute, U+00CD ISOlat1 */,
            "Icirc"
            /* "&#206;" -- latin capital letter I with circumflex, U+00CE ISOlat1 */,
            "Iuml"
            /* "&#207;" -- latin capital letter I with diaeresis, U+00CF ISOlat1 */,
            "ETH"    /* "&#208;" -- latin capital letter ETH, U+00D0 ISOlat1 */,
            "Ntilde"
            /* "&#209;" -- latin capital letter N with tilde, U+00D1 ISOlat1 */,
            "Ograve"
            /* "&#210;" -- latin capital letter O with grave, U+00D2 ISOlat1 */,
            "Oacute"
            /* "&#211;" -- latin capital letter O with acute, U+00D3 ISOlat1 */,
            "Ocirc"
            /* "&#212;" -- latin capital letter O with circumflex, U+00D4 ISOlat1 */,
            "Otilde"
            /* "&#213;" -- latin capital letter O with tilde, U+00D5 ISOlat1 */,
            "Ouml"
            /* "&#214;" -- latin capital letter O with diaeresis, U+00D6 ISOlat1 */,
            "times"  /* "&#215;" -- multiplication sign, U+00D7 ISOnum */,
            "Oslash"
            /* "&#216;" -- latin capital letter O with stroke = latin capital letter O slash, U+00D8 ISOlat1 */,
            "Ugrave"
            /* "&#217;" -- latin capital letter U with grave, U+00D9 ISOlat1 */,
            "Uacute"
            /* "&#218;" -- latin capital letter U with acute, U+00DA ISOlat1 */,
            "Ucirc"
            /* "&#219;" -- latin capital letter U with circumflex, U+00DB ISOlat1 */,
            "Uuml"
            /* "&#220;" -- latin capital letter U with diaeresis, U+00DC ISOlat1 */,
            "Yacute"
            /* "&#221;" -- latin capital letter Y with acute, U+00DD ISOlat1 */,
            "THORN"
            /* "&#222;" -- latin capital letter THORN, U+00DE ISOlat1 */,
            "szlig"
            /* "&#223;" -- latin small letter sharp s = ess-zed, U+00DF ISOlat1 */,
            "agrave"
            /* "&#224;" -- latin small letter a with grave = latin small letter a grave, U+00E0 ISOlat1 */,
            "aacute"
            /* "&#225;" -- latin small letter a with acute, U+00E1 ISOlat1 */,
            "acirc"
            /* "&#226;" -- latin small letter a with circumflex, U+00E2 ISOlat1 */,
            "atilde"
            /* "&#227;" -- latin small letter a with tilde, U+00E3 ISOlat1 */,
            "auml"
            /* "&#228;" -- latin small letter a with diaeresis, U+00E4 ISOlat1 */,
            "aring"
            /* "&#229;" -- latin small letter a with ring above = latin small letter a ring, U+00E5 ISOlat1 */,
            "aelig"
            /* "&#230;" -- latin small letter ae = latin small ligature ae, U+00E6 ISOlat1 */,
            "ccedil"
            /* "&#231;" -- latin small letter c with cedilla, U+00E7 ISOlat1 */,
            "egrave"
            /* "&#232;" -- latin small letter e with grave, U+00E8 ISOlat1 */,
            "eacute"
            /* "&#233;" -- latin small letter e with acute, U+00E9 ISOlat1 */,
            "ecirc"
            /* "&#234;" -- latin small letter e with circumflex, U+00EA ISOlat1 */,
            "euml"
            /* "&#235;" -- latin small letter e with diaeresis, U+00EB ISOlat1 */,
            "igrave"
            /* "&#236;" -- latin small letter i with grave, U+00EC ISOlat1 */,
            "iacute"
            /* "&#237;" -- latin small letter i with acute, U+00ED ISOlat1 */,
            "icirc"
            /* "&#238;" -- latin small letter i with circumflex, U+00EE ISOlat1 */,
            "iuml"
            /* "&#239;" -- latin small letter i with diaeresis, U+00EF ISOlat1 */,
            "eth"    /* "&#240;" -- latin small letter eth, U+00F0 ISOlat1 */,
            "ntilde"
            /* "&#241;" -- latin small letter n with tilde, U+00F1 ISOlat1 */,
            "ograve"
            /* "&#242;" -- latin small letter o with grave, U+00F2 ISOlat1 */,
            "oacute"
            /* "&#243;" -- latin small letter o with acute, U+00F3 ISOlat1 */,
            "ocirc"
            /* "&#244;" -- latin small letter o with circumflex, U+00F4 ISOlat1 */,
            "otilde"
            /* "&#245;" -- latin small letter o with tilde, U+00F5 ISOlat1 */,
            "ouml"
            /* "&#246;" -- latin small letter o with diaeresis, U+00F6 ISOlat1 */,
            "divide" /* "&#247;" -- division sign, U+00F7 ISOnum */,
            "oslash"
            /* "&#248;" -- latin small letter o with stroke, = latin small letter o slash, U+00F8 ISOlat1 */,
            "ugrave"
            /* "&#249;" -- latin small letter u with grave, U+00F9 ISOlat1 */,
            "uacute"
            /* "&#250;" -- latin small letter u with acute, U+00FA ISOlat1 */,
            "ucirc"
            /* "&#251;" -- latin small letter u with circumflex, U+00FB ISOlat1 */,
            "uuml"
            /* "&#252;" -- latin small letter u with diaeresis, U+00FC ISOlat1 */,
            "yacute"
            /* "&#253;" -- latin small letter y with acute, U+00FD ISOlat1 */,
            "thorn"  /* "&#254;" -- latin small letter thorn,U+00FE ISOlat1 */,
            "yuml"
            /* "&#255;" -- latin small letter y with diaeresis, U+00FF ISOlat1 */,
    };

    public static String toDebugString(Node node) {
        short type = node.getNodeType();
        switch (type) {
            case Node.ATTRIBUTE_NODE: {
                Attr attr = (Attr) node;
                return "attribute[name: " + attr.getName() + "; value: " + attr.getValue() + "]";
            }
            case Node.ELEMENT_NODE: {
                Element element = (Element) node;
                StringBuffer buffer = new StringBuffer();
                buffer.append("element[tag: ");
                buffer.append(element.getTagName());
                buffer.append("; attributes: ");
                NamedNodeMap attributes = element.getAttributes();
                for (int i = 0; i < attributes.getLength(); i++) {
                    Attr attr = (Attr) attributes.item(i);
                    buffer.append(attr.getName());
                    buffer.append("=");
                    buffer.append(attr.getValue());
                    buffer.append(' ');
                }
                buffer.append(']');

                return buffer.toString();
            }
            case Node.CDATA_SECTION_NODE: {
                CDATASection cdataSection = (CDATASection) node;
                return "cdata[" + cdataSection.getData() + "]";
            }
            case Node.TEXT_NODE: {
                Text text = (Text) node;
                return "text[" + text.getData() + "]";
            }
            case Node.COMMENT_NODE: {
                Comment comment = (Comment) node;
                return "comment[" + comment.getData() + "]";
            }
            case Node.ENTITY_NODE: {
                Entity entity = (Entity) node;
                return "entity[public: " + entity.getPublicId() + "; system: " + entity.getSystemId() + "]";
            }
            default: {
                return node.getNodeName();
            }
        }
    }
}
