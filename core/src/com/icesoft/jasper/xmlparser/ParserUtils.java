/*
 * Copyright 1999,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.icesoft.jasper.xmlparser;

import com.icesoft.jasper.JasperException;
import com.icesoft.jasper.compiler.Localizer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;


/**
 * XML parsing utilities for processing web application deployment descriptor
 * and tag library descriptor files.  FIXME - make these use a separate class
 * loader for the parser to be used.
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.10 $ $Date: 2004/03/17 19:23:05 $
 */

public class ParserUtils {

    // Logger
    static Log log = LogFactory.getLog(ParserUtils.class);

    /**
     * An error handler for use when parsing XML documents.
     */
    static ErrorHandler errorHandler = new MyErrorHandler();

    /**
     * An entity resolver for use when parsing XML documents.
     */
    public static EntityResolver entityResolver = new CachedEntityResolver();

    // Turn off for JSP 2.0 until switch over to using xschema.
    public static boolean validating = false;

    // --------------------------------------------------------- Public Methods

    /**
     * Parse the specified XML document, and return a <code>TreeNode</code> that
     * corresponds to the root node of the document tree.
     *
     * @param uri URI of the XML document being parsed
     * @param is  Input stream containing the deployment descriptor
     * @throws JasperException if an input/output error occurs
     * @throws JasperException if a parsing error occurs
     */
    public TreeNode parseXMLDocument(String uri, InputStream is)
            throws JasperException {

        Document document = null;

        // Perform an XML parse of this document, via JAXP
        try {
            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setValidating(validating);
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setEntityResolver(entityResolver);
            builder.setErrorHandler(errorHandler);
            document = builder.parse(is);
        } catch (ParserConfigurationException ex) {
            throw new JasperException
                    (Localizer.getMessage("jsp.error.parse.xml", uri), ex);
        } catch (SAXParseException ex) {
            throw new JasperException
                    (Localizer.getMessage("jsp.error.parse.xml.line",
                                          uri,
                                          Integer.toString(ex.getLineNumber()),
                                          Integer.toString(
                                                  ex.getColumnNumber())),
                     ex);
        } catch (SAXException sx) {
            if (log.isErrorEnabled()) {
                log.error( "XML parsing failed for " + uri + 
                        "SAXException: " +sx.getMessage() );
            }
            throw new JasperException
                    (Localizer.getMessage("jsp.error.parse.xml", uri) + 
                            "SAXException: " +sx.getMessage(), sx);
        } catch (IOException io) {
            throw new JasperException
                    (Localizer.getMessage("jsp.error.parse.xml", uri), io);
        }

        // Convert the resulting document to a graph of TreeNodes
        return (convert(null, document.getDocumentElement()));
    }

    // ------------------------------------------------------ Protected Methods


    /**
     * Create and return a TreeNode that corresponds to the specified Node,
     * including processing all of the attributes and children nodes.
     *
     * @param parent The parent TreeNode (if any) for the new TreeNode
     * @param node   The XML document Node to be converted
     */
    protected TreeNode convert(TreeNode parent, Node node) {

        // Construct a new TreeNode for this node
        TreeNode treeNode = new TreeNode(node.getNodeName(), parent);

        // Convert all attributes of this node
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            int n = attributes.getLength();
            for (int i = 0; i < n; i++) {
                Node attribute = attributes.item(i);
                treeNode.addAttribute(attribute.getNodeName(),
                                      attribute.getNodeValue());
            }
        }

        // Create and attach all children of this node
        NodeList children = node.getChildNodes();
        if (children != null) {
            int n = children.getLength();
            for (int i = 0; i < n; i++) {
                Node child = children.item(i);
                if (child instanceof Comment)
                    continue;
                if (child instanceof Text) {
                    String body = ((Text) child).getData();
                    if (body != null) {
                        body = body.trim();
                        if (body.length() > 0)
                            treeNode.setBody(body);
                    }
                } else {
                    TreeNode treeChild = convert(treeNode, child);
                }
            }
        }

        // Return the completed TreeNode graph
        return (treeNode);
    }
}

// ------------------------------------------------------------ Private Classes

/*
class MyEntityResolver implements EntityResolver {
    public InputSource resolveEntity(String publicId, String systemId)
	throws SAXException
    {
	for (int i=0; i<Constants.CACHED_DTD_PUBLIC_IDS.length; i++) {
	    String cachedDtdPublicId = Constants.CACHED_DTD_PUBLIC_IDS[i];
	    if (cachedDtdPublicId.equals(publicId)) {
		String resourcePath = Constants.CACHED_DTD_RESOURCE_PATHS[i];
		InputStream input =
		    this.getClass().getResourceAsStream(resourcePath);
		if (input == null) {
		    throw new SAXException(
                        Localizer.getMessage("jsp.error.internal.filenotfound",
					     resourcePath));
		}
		InputSource isrc = new InputSource(input);
		return isrc;
	    }
	}
        System.out.println("Resolve entity failed"  + publicId + " "
			   + systemId );
	ParserUtils.log.error(Localizer.getMessage("jsp.error.parse.xml.invalidPublicId",
						   publicId));
        return null;
    }
}
*/

class MyErrorHandler implements ErrorHandler {
    public void warning(SAXParseException ex)
            throws SAXException {
        System.out.println("ParserUtils: warning " + ex);
        // We ignore warnings
    }

    public void error(SAXParseException ex)
            throws SAXException {
        throw ex;
    }

    public void fatalError(SAXParseException ex)
            throws SAXException {
        throw ex;
    }
}
