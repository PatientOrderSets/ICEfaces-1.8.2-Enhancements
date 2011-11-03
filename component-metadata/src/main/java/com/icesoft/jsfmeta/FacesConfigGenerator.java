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

/*
 * FacesConfigGenerator based on generated class from dtd. 
 * 
 * It merges component's faces-config.xml with custom renderer defined in webui-faces-config.xml
 */

package com.icesoft.jsfmeta;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.icesoft.jsfmeta.util.FacesConfigParserHelper;
import com.sun.rave.jsfmeta.beans.RendererBean;

public class FacesConfigGenerator {
	
	private static final String WORKING_FOLDER;
	private String inputXmlFile;
	private String outputXmlFile;
	private String addonXmlFile;
	
	public FacesConfigGenerator() {

		inputXmlFile = WORKING_FOLDER+"/build/conf/faces-config.xml";	
		outputXmlFile = WORKING_FOLDER+"/build/component/META-INF/faces-config.xml";	
		addonXmlFile = "./src/main/resources/conf/webui-faces-config.xml";
	}
	
	public static void main(String[] args){

		FacesConfigGenerator facesConfigGenerator = new FacesConfigGenerator();
		facesConfigGenerator.generate(args);
	}
	
	private void generate(String[] args){

		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (arg.equals("-input")){
				inputXmlFile = args[++i];
				continue;
			}
			if(arg.equals("-output")){
				outputXmlFile = args[++i];
				continue;
			}		
			if(arg.equals("-addon")){
				addonXmlFile = args[++i];
				continue;
			}
		}
		
		System.out.println("input faces-config file ="+  inputXmlFile);
		System.out.println("output faces-config file ="+ outputXmlFile);
		System.out.println("addon faces-config file =" + addonXmlFile);
			
		Document document = parse(inputXmlFile);
		visitDocument(document);	
		transform(document, outputXmlFile);
	}
	
	
	static{
		String result = ".";
		try {
			ClassLoader classLoader = Thread.currentThread()
					.getContextClassLoader();
			URL localUrl = classLoader.getResource(".");
                        if(localUrl != null){
                            result = localUrl.getPath() + "./../../";
                        }

		} catch (Exception ex) {
                    ex.printStackTrace();
		}
		WORKING_FOLDER = result;
	}
	
	public void transform(Document document, String outputXmlFile){
		
		TransformerFactory transformerFactory = TransformerFactory
		.newInstance();
		try {
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "-//Sun Microsystems, Inc.//DTD JavaServer Faces Config 1.1//EN");
			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://java.sun.com/dtd/web-facesconfig_1_1.dtd");		
			
			transformer.transform(new DOMSource(document), new StreamResult(
					new FileOutputStream(outputXmlFile)));
			
			FacesConfigParserHelper.validate(outputXmlFile);
			
//			transformer.transform(new DOMSource(document), new StreamResult(
//					System.out));execute
//			System.out.println();

		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Document parse(String filePath) {

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
				.newInstance();
		documentBuilderFactory.setValidating(true);
		documentBuilderFactory.setNamespaceAware(true);

		DocumentBuilder documentBuilder = null;
		Document document = null;

		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		documentBuilder.setErrorHandler(new ErrorHandler() {
			public void error(SAXParseException e) {
				e.printStackTrace();
			}

			public void fatalError(SAXParseException e) throws SAXException {
				e.printStackTrace();
			}

			public void warning(SAXParseException e) {
				e.printStackTrace();
			}
		});

		try {
			document = documentBuilder.parse(new File(filePath));

		} catch (IOException e) {

			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();

		}
		return document;
	}

	/**
	 * Scan through org.w3c.dom.Document document.
	 */
	public void visitDocument(Document document) {

		org.w3c.dom.Element element = document.getDocumentElement();
		if ((element != null) && element.getTagName().equals("faces-config")) {
			visitElement_faces_config(element);
		}
		if ((element != null) && element.getTagName().equals("application")) {
			visitElement_application(element);
		}
		if ((element != null) && element.getTagName().equals("factory")) {
			visitElement_factory(element);
		}
		if ((element != null) && element.getTagName().equals("attribute")) {
			visitElement_attribute(element);
		}
		if ((element != null)
				&& element.getTagName().equals("attribute-extension")) {
			visitElement_attribute_extension(element);
		}
		if ((element != null) && element.getTagName().equals("component")) {
			visitElement_component(element);
		}
		if ((element != null)
				&& element.getTagName().equals("component-extension")) {
			visitElement_component_extension(element);
		}
		if ((element != null) && element.getTagName().equals("facet")) {
			visitElement_facet(element);
		}
		if ((element != null) && element.getTagName().equals("facet-extension")) {
			visitElement_facet_extension(element);
		}
		if ((element != null) && element.getTagName().equals("facet-name")) {
			visitElement_facet_name(element);
		}
		if ((element != null) && element.getTagName().equals("converter")) {
			visitElement_converter(element);
		}
		if ((element != null) && element.getTagName().equals("icon")) {
			visitElement_icon(element);
		}
		if ((element != null) && element.getTagName().equals("lifecycle")) {
			visitElement_lifecycle(element);
		}
		if ((element != null) && element.getTagName().equals("locale-config")) {
			visitElement_locale_config(element);
		}
		if ((element != null) && element.getTagName().equals("managed-bean")) {
			visitElement_managed_bean(element);
		}
		if ((element != null)
				&& element.getTagName().equals("managed-property")) {
			visitElement_managed_property(element);
		}
		if ((element != null) && element.getTagName().equals("map-entry")) {
			visitElement_map_entry(element);
		}
		if ((element != null) && element.getTagName().equals("map-entries")) {
			visitElement_map_entries(element);
		}
		if ((element != null) && element.getTagName().equals("message-bundle")) {
			visitElement_message_bundle(element);
		}
		if ((element != null) && element.getTagName().equals("navigation-case")) {
			visitElement_navigation_case(element);
		}
		if ((element != null) && element.getTagName().equals("navigation-rule")) {
			visitElement_navigation_rule(element);
		}
		if ((element != null) && element.getTagName().equals("property")) {
			visitElement_property(element);
		}
		if ((element != null)
				&& element.getTagName().equals("property-extension")) {
			visitElement_property_extension(element);
		}
		if ((element != null) && element.getTagName().equals("referenced-bean")) {
			visitElement_referenced_bean(element);
		}
		if ((element != null) && element.getTagName().equals("render-kit")) {
			visitElement_render_kit(element);
		}
		if ((element != null) && element.getTagName().equals("renderer")) {
			visitElement_renderer(element);
		}
		if ((element != null)
				&& element.getTagName().equals("renderer-extension")) {
			visitElement_renderer_extension(element);
		}
		if ((element != null) && element.getTagName().equals("validator")) {
			visitElement_validator(element);
		}
		if ((element != null) && element.getTagName().equals("list-entries")) {
			visitElement_list_entries(element);
		}
		if ((element != null) && element.getTagName().equals("action-listener")) {
			visitElement_action_listener(element);
		}
		if ((element != null)
				&& element.getTagName().equals("application-factory")) {
			visitElement_application_factory(element);
		}
		if ((element != null) && element.getTagName().equals("attribute-class")) {
			visitElement_attribute_class(element);
		}
		if ((element != null) && element.getTagName().equals("attribute-name")) {
			visitElement_attribute_name(element);
		}
		if ((element != null) && element.getTagName().equals("component-class")) {
			visitElement_component_class(element);
		}
		if ((element != null)
				&& element.getTagName().equals("component-family")) {
			visitElement_component_family(element);
		}
		if ((element != null) && element.getTagName().equals("component-type")) {
			visitElement_component_type(element);
		}
		if ((element != null) && element.getTagName().equals("converter-class")) {
			visitElement_converter_class(element);
		}
		if ((element != null)
				&& element.getTagName().equals("converter-for-class")) {
			visitElement_converter_for_class(element);
		}
		if ((element != null) && element.getTagName().equals("converter-id")) {
			visitElement_converter_id(element);
		}
		if ((element != null)
				&& element.getTagName().equals("default-render-kit-id")) {
			visitElement_default_render_kit_id(element);
		}
		if ((element != null) && element.getTagName().equals("default-locale")) {
			visitElement_default_locale(element);
		}
		if ((element != null) && element.getTagName().equals("default-value")) {
			visitElement_default_value(element);
		}
		if ((element != null) && element.getTagName().equals("description")) {
			visitElement_description(element);
		}
		if ((element != null) && element.getTagName().equals("display-name")) {
			visitElement_display_name(element);
		}
		if ((element != null)
				&& element.getTagName().equals("faces-context-factory")) {
			visitElement_faces_context_factory(element);
		}
		if ((element != null) && element.getTagName().equals("from-action")) {
			visitElement_from_action(element);
		}
		if ((element != null) && element.getTagName().equals("from-outcome")) {
			visitElement_from_outcome(element);
		}
		if ((element != null) && element.getTagName().equals("from-view-id")) {
			visitElement_from_view_id(element);
		}
		if ((element != null) && element.getTagName().equals("key")) {
			visitElement_key(element);
		}
		if ((element != null) && element.getTagName().equals("key-class")) {
			visitElement_key_class(element);
		}
		if ((element != null) && element.getTagName().equals("large-icon")) {
			visitElement_large_icon(element);
		}
		if ((element != null)
				&& element.getTagName().equals("lifecycle-factory")) {
			visitElement_lifecycle_factory(element);
		}
		if ((element != null)
				&& element.getTagName().equals("managed-bean-class")) {
			visitElement_managed_bean_class(element);
		}
		if ((element != null)
				&& element.getTagName().equals("managed-bean-name")) {
			visitElement_managed_bean_name(element);
		}
		if ((element != null)
				&& element.getTagName().equals("managed-bean-scope")) {
			visitElement_managed_bean_scope(element);
		}
		if ((element != null)
				&& element.getTagName().equals("navigation-handler")) {
			visitElement_navigation_handler(element);
		}
		if ((element != null) && element.getTagName().equals("phase-listener")) {
			visitElement_phase_listener(element);
		}
		if ((element != null) && element.getTagName().equals("redirect")) {
			visitElement_redirect(element);
		}
		if ((element != null) && element.getTagName().equals("suggested-value")) {
			visitElement_suggested_value(element);
		}
		if ((element != null) && element.getTagName().equals("view-handler")) {
			visitElement_view_handler(element);
		}
		if ((element != null) && element.getTagName().equals("state-manager")) {
			visitElement_state_manager(element);
		}
		if ((element != null) && element.getTagName().equals("null-value")) {
			visitElement_null_value(element);
		}
		if ((element != null) && element.getTagName().equals("property-class")) {
			visitElement_property_class(element);
		}
		if ((element != null) && element.getTagName().equals("property-name")) {
			visitElement_property_name(element);
		}
		if ((element != null)
				&& element.getTagName().equals("property-resolver")) {
			visitElement_property_resolver(element);
		}
		if ((element != null)
				&& element.getTagName().equals("referenced-bean-class")) {
			visitElement_referenced_bean_class(element);
		}
		if ((element != null)
				&& element.getTagName().equals("referenced-bean-name")) {
			visitElement_referenced_bean_name(element);
		}
		if ((element != null) && element.getTagName().equals("render-kit-id")) {
			visitElement_render_kit_id(element);
		}
		if ((element != null)
				&& element.getTagName().equals("render-kit-class")) {
			visitElement_render_kit_class(element);
		}
		if ((element != null) && element.getTagName().equals("renderer-class")) {
			visitElement_renderer_class(element);
		}
		if ((element != null)
				&& element.getTagName().equals("render-kit-factory")) {
			visitElement_render_kit_factory(element);
		}
		if ((element != null) && element.getTagName().equals("renderer-type")) {
			visitElement_renderer_type(element);
		}
		if ((element != null) && element.getTagName().equals("small-icon")) {
			visitElement_small_icon(element);
		}
		if ((element != null)
				&& element.getTagName().equals("supported-locale")) {
			visitElement_supported_locale(element);
		}
		if ((element != null) && element.getTagName().equals("to-view-id")) {
			visitElement_to_view_id(element);
		}
		if ((element != null) && element.getTagName().equals("validator-class")) {
			visitElement_validator_class(element);
		}
		if ((element != null) && element.getTagName().equals("validator-id")) {
			visitElement_validator_id(element);
		}
		if ((element != null) && element.getTagName().equals("value")) {
			visitElement_value(element);
		}
		if ((element != null) && element.getTagName().equals("value-class")) {
			visitElement_value_class(element);
		}
		if ((element != null)
				&& element.getTagName().equals("variable-resolver")) {
			visitElement_variable_resolver(element);
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named faces-config.
	 */
	void visitElement_faces_config(org.w3c.dom.Element element) { // <faces-config>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("xmlns")) { // <faces-config
													// xmlns="???">
			// attr.getValue();
			}
			if (attr.getName().equals("id")) { // <faces-config id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				if (nodeElement.getTagName().equals("application")) {
					visitElement_application(nodeElement);
				}
				if (nodeElement.getTagName().equals("factory")) {
					visitElement_factory(nodeElement);
				}
				if (nodeElement.getTagName().equals("component")) {
					visitElement_component(nodeElement);
				}
				if (nodeElement.getTagName().equals("converter")) {
					visitElement_converter(nodeElement);
				}
				if (nodeElement.getTagName().equals("lifecycle")) {
					visitElement_lifecycle(nodeElement);
				}
				if (nodeElement.getTagName().equals("managed-bean")) {
					visitElement_managed_bean(nodeElement);
				}
				if (nodeElement.getTagName().equals("navigation-rule")) {
					visitElement_navigation_rule(nodeElement);
				}
				if (nodeElement.getTagName().equals("referenced-bean")) {
					visitElement_referenced_bean(nodeElement);
				}
				if (nodeElement.getTagName().equals("render-kit")) {
					visitElement_render_kit(nodeElement);
				}
				if (nodeElement.getTagName().equals("validator")) {
					visitElement_validator(nodeElement);
				}
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named application.
	 */
	void visitElement_application(org.w3c.dom.Element element) { // <application>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <application id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				if (nodeElement.getTagName().equals("locale-config")) {
					visitElement_locale_config(nodeElement);
				}
				if (nodeElement.getTagName().equals("message-bundle")) {
					visitElement_message_bundle(nodeElement);
				}
				if (nodeElement.getTagName().equals("action-listener")) {
					visitElement_action_listener(nodeElement);
				}
				if (nodeElement.getTagName().equals("default-render-kit-id")) {
					visitElement_default_render_kit_id(nodeElement);
				}
				if (nodeElement.getTagName().equals("navigation-handler")) {
					visitElement_navigation_handler(nodeElement);
				}
				if (nodeElement.getTagName().equals("view-handler")) {
					visitElement_view_handler(nodeElement);
				}
				if (nodeElement.getTagName().equals("state-manager")) {
					visitElement_state_manager(nodeElement);
				}
				if (nodeElement.getTagName().equals("property-resolver")) {
					visitElement_property_resolver(nodeElement);
				}
				if (nodeElement.getTagName().equals("variable-resolver")) {
					visitElement_variable_resolver(nodeElement);
				}
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named factory.
	 */
	void visitElement_factory(org.w3c.dom.Element element) { // <factory>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <factory id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				if (nodeElement.getTagName().equals("application-factory")) {
					visitElement_application_factory(nodeElement);
				}
				if (nodeElement.getTagName().equals("faces-context-factory")) {
					visitElement_faces_context_factory(nodeElement);
				}
				if (nodeElement.getTagName().equals("lifecycle-factory")) {
					visitElement_lifecycle_factory(nodeElement);
				}
				if (nodeElement.getTagName().equals("render-kit-factory")) {
					visitElement_render_kit_factory(nodeElement);
				}
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named attribute.
	 */
	void visitElement_attribute(org.w3c.dom.Element element) { // <attribute>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <attribute id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				if (nodeElement.getTagName().equals("attribute-extension")) {
					visitElement_attribute_extension(nodeElement);
				}
				if (nodeElement.getTagName().equals("icon")) {
					visitElement_icon(nodeElement);
				}
				if (nodeElement.getTagName().equals("attribute-class")) {
					visitElement_attribute_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("attribute-name")) {
					visitElement_attribute_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("default-value")) {
					visitElement_default_value(nodeElement);
				}
				if (nodeElement.getTagName().equals("description")) {
					visitElement_description(nodeElement);
				}
				if (nodeElement.getTagName().equals("display-name")) {
					visitElement_display_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("suggested-value")) {
					visitElement_suggested_value(nodeElement);
				}
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named attribute-extension.
	 */
	void visitElement_attribute_extension(org.w3c.dom.Element element) { // <attribute-extension>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <attribute-extension
												// id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				if (nodeElement.getTagName().equals("faces-config")) {
					visitElement_faces_config(nodeElement);
				}
				if (nodeElement.getTagName().equals("application")) {
					visitElement_application(nodeElement);
				}
				if (nodeElement.getTagName().equals("factory")) {
					visitElement_factory(nodeElement);
				}
				if (nodeElement.getTagName().equals("attribute")) {
					visitElement_attribute(nodeElement);
				}
				if (nodeElement.getTagName().equals("attribute-extension")) {
					visitElement_attribute_extension(nodeElement);
				}
				if (nodeElement.getTagName().equals("component")) {
					visitElement_component(nodeElement);
				}
				if (nodeElement.getTagName().equals("component-extension")) {
					visitElement_component_extension(nodeElement);
				}
				if (nodeElement.getTagName().equals("facet")) {
					visitElement_facet(nodeElement);
				}
				if (nodeElement.getTagName().equals("facet-extension")) {
					visitElement_facet_extension(nodeElement);
				}
				if (nodeElement.getTagName().equals("facet-name")) {
					visitElement_facet_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("converter")) {
					visitElement_converter(nodeElement);
				}
				if (nodeElement.getTagName().equals("icon")) {
					visitElement_icon(nodeElement);
				}
				if (nodeElement.getTagName().equals("lifecycle")) {
					visitElement_lifecycle(nodeElement);
				}
				if (nodeElement.getTagName().equals("locale-config")) {
					visitElement_locale_config(nodeElement);
				}
				if (nodeElement.getTagName().equals("managed-bean")) {
					visitElement_managed_bean(nodeElement);
				}
				if (nodeElement.getTagName().equals("managed-property")) {
					visitElement_managed_property(nodeElement);
				}
				if (nodeElement.getTagName().equals("map-entry")) {
					visitElement_map_entry(nodeElement);
				}
				if (nodeElement.getTagName().equals("map-entries")) {
					visitElement_map_entries(nodeElement);
				}
				if (nodeElement.getTagName().equals("message-bundle")) {
					visitElement_message_bundle(nodeElement);
				}
				if (nodeElement.getTagName().equals("navigation-case")) {
					visitElement_navigation_case(nodeElement);
				}
				if (nodeElement.getTagName().equals("navigation-rule")) {
					visitElement_navigation_rule(nodeElement);
				}
				if (nodeElement.getTagName().equals("property")) {
					visitElement_property(nodeElement);
				}
				if (nodeElement.getTagName().equals("property-extension")) {
					visitElement_property_extension(nodeElement);
				}
				if (nodeElement.getTagName().equals("referenced-bean")) {
					visitElement_referenced_bean(nodeElement);
				}
				if (nodeElement.getTagName().equals("render-kit")) {
					visitElement_render_kit(nodeElement);
				}
				if (nodeElement.getTagName().equals("renderer")) {
					visitElement_renderer(nodeElement);
				}
				if (nodeElement.getTagName().equals("renderer-extension")) {
					visitElement_renderer_extension(nodeElement);
				}
				if (nodeElement.getTagName().equals("validator")) {
					visitElement_validator(nodeElement);
				}
				if (nodeElement.getTagName().equals("list-entries")) {
					visitElement_list_entries(nodeElement);
				}
				if (nodeElement.getTagName().equals("action-listener")) {
					visitElement_action_listener(nodeElement);
				}
				if (nodeElement.getTagName().equals("application-factory")) {
					visitElement_application_factory(nodeElement);
				}
				if (nodeElement.getTagName().equals("attribute-class")) {
					visitElement_attribute_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("attribute-name")) {
					visitElement_attribute_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("component-class")) {
					visitElement_component_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("component-family")) {
					visitElement_component_family(nodeElement);
				}
				if (nodeElement.getTagName().equals("component-type")) {
					visitElement_component_type(nodeElement);
				}
				if (nodeElement.getTagName().equals("converter-class")) {
					visitElement_converter_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("converter-for-class")) {
					visitElement_converter_for_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("converter-id")) {
					visitElement_converter_id(nodeElement);
				}
				if (nodeElement.getTagName().equals("default-render-kit-id")) {
					visitElement_default_render_kit_id(nodeElement);
				}
				if (nodeElement.getTagName().equals("default-locale")) {
					visitElement_default_locale(nodeElement);
				}
				if (nodeElement.getTagName().equals("default-value")) {
					visitElement_default_value(nodeElement);
				}
				if (nodeElement.getTagName().equals("description")) {
					visitElement_description(nodeElement);
				}
				if (nodeElement.getTagName().equals("display-name")) {
					visitElement_display_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("faces-context-factory")) {
					visitElement_faces_context_factory(nodeElement);
				}
				if (nodeElement.getTagName().equals("from-action")) {
					visitElement_from_action(nodeElement);
				}
				if (nodeElement.getTagName().equals("from-outcome")) {
					visitElement_from_outcome(nodeElement);
				}
				if (nodeElement.getTagName().equals("from-view-id")) {
					visitElement_from_view_id(nodeElement);
				}
				if (nodeElement.getTagName().equals("key")) {
					visitElement_key(nodeElement);
				}
				if (nodeElement.getTagName().equals("key-class")) {
					visitElement_key_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("large-icon")) {
					visitElement_large_icon(nodeElement);
				}
				if (nodeElement.getTagName().equals("lifecycle-factory")) {
					visitElement_lifecycle_factory(nodeElement);
				}
				if (nodeElement.getTagName().equals("managed-bean-class")) {
					visitElement_managed_bean_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("managed-bean-name")) {
					visitElement_managed_bean_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("managed-bean-scope")) {
					visitElement_managed_bean_scope(nodeElement);
				}
				if (nodeElement.getTagName().equals("navigation-handler")) {
					visitElement_navigation_handler(nodeElement);
				}
				if (nodeElement.getTagName().equals("phase-listener")) {
					visitElement_phase_listener(nodeElement);
				}
				if (nodeElement.getTagName().equals("redirect")) {
					visitElement_redirect(nodeElement);
				}
				if (nodeElement.getTagName().equals("suggested-value")) {
					visitElement_suggested_value(nodeElement);
				}
				if (nodeElement.getTagName().equals("view-handler")) {
					visitElement_view_handler(nodeElement);
				}
				if (nodeElement.getTagName().equals("state-manager")) {
					visitElement_state_manager(nodeElement);
				}
				if (nodeElement.getTagName().equals("null-value")) {
					visitElement_null_value(nodeElement);
				}
				if (nodeElement.getTagName().equals("property-class")) {
					visitElement_property_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("property-name")) {
					visitElement_property_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("property-resolver")) {
					visitElement_property_resolver(nodeElement);
				}
				if (nodeElement.getTagName().equals("referenced-bean-class")) {
					visitElement_referenced_bean_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("referenced-bean-name")) {
					visitElement_referenced_bean_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("render-kit-id")) {
					visitElement_render_kit_id(nodeElement);
				}
				if (nodeElement.getTagName().equals("render-kit-class")) {
					visitElement_render_kit_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("renderer-class")) {
					visitElement_renderer_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("render-kit-factory")) {
					visitElement_render_kit_factory(nodeElement);
				}
				if (nodeElement.getTagName().equals("renderer-type")) {
					visitElement_renderer_type(nodeElement);
				}
				if (nodeElement.getTagName().equals("small-icon")) {
					visitElement_small_icon(nodeElement);
				}
				if (nodeElement.getTagName().equals("supported-locale")) {
					visitElement_supported_locale(nodeElement);
				}
				if (nodeElement.getTagName().equals("to-view-id")) {
					visitElement_to_view_id(nodeElement);
				}
				if (nodeElement.getTagName().equals("validator-class")) {
					visitElement_validator_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("validator-id")) {
					visitElement_validator_id(nodeElement);
				}
				if (nodeElement.getTagName().equals("value")) {
					visitElement_value(nodeElement);
				}
				if (nodeElement.getTagName().equals("value-class")) {
					visitElement_value_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("variable-resolver")) {
					visitElement_variable_resolver(nodeElement);
				}
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named component.
	 */
	void visitElement_component(org.w3c.dom.Element element) { // <component>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <component id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				if (nodeElement.getTagName().equals("attribute")) {
					visitElement_attribute(nodeElement);
				}
				if (nodeElement.getTagName().equals("component-extension")) {
					visitElement_component_extension(nodeElement);
				}
				if (nodeElement.getTagName().equals("facet")) {
					visitElement_facet(nodeElement);
				}
				if (nodeElement.getTagName().equals("icon")) {
					visitElement_icon(nodeElement);
				}
				if (nodeElement.getTagName().equals("property")) {
					visitElement_property(nodeElement);
				}
				if (nodeElement.getTagName().equals("component-class")) {
					visitElement_component_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("component-type")) {
					visitElement_component_type(nodeElement);
				}
				if (nodeElement.getTagName().equals("description")) {
					visitElement_description(nodeElement);
				}
				if (nodeElement.getTagName().equals("display-name")) {
					visitElement_display_name(nodeElement);
				}
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named component-extension.
	 */
	void visitElement_component_extension(org.w3c.dom.Element element) { // <component-extension>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <component-extension
												// id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				if (nodeElement.getTagName().equals("faces-config")) {
					visitElement_faces_config(nodeElement);
				}
				if (nodeElement.getTagName().equals("application")) {
					visitElement_application(nodeElement);
				}
				if (nodeElement.getTagName().equals("factory")) {
					visitElement_factory(nodeElement);
				}
				if (nodeElement.getTagName().equals("attribute")) {
					visitElement_attribute(nodeElement);
				}
				if (nodeElement.getTagName().equals("attribute-extension")) {
					visitElement_attribute_extension(nodeElement);
				}
				if (nodeElement.getTagName().equals("component")) {
					visitElement_component(nodeElement);
				}
				if (nodeElement.getTagName().equals("component-extension")) {
					visitElement_component_extension(nodeElement);
				}
				if (nodeElement.getTagName().equals("facet")) {
					visitElement_facet(nodeElement);
				}
				if (nodeElement.getTagName().equals("facet-extension")) {
					visitElement_facet_extension(nodeElement);
				}
				if (nodeElement.getTagName().equals("facet-name")) {
					visitElement_facet_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("converter")) {
					visitElement_converter(nodeElement);
				}
				if (nodeElement.getTagName().equals("icon")) {
					visitElement_icon(nodeElement);
				}
				if (nodeElement.getTagName().equals("lifecycle")) {
					visitElement_lifecycle(nodeElement);
				}
				if (nodeElement.getTagName().equals("locale-config")) {
					visitElement_locale_config(nodeElement);
				}
				if (nodeElement.getTagName().equals("managed-bean")) {
					visitElement_managed_bean(nodeElement);
				}
				if (nodeElement.getTagName().equals("managed-property")) {
					visitElement_managed_property(nodeElement);
				}
				if (nodeElement.getTagName().equals("map-entry")) {
					visitElement_map_entry(nodeElement);
				}
				if (nodeElement.getTagName().equals("map-entries")) {
					visitElement_map_entries(nodeElement);
				}
				if (nodeElement.getTagName().equals("message-bundle")) {
					visitElement_message_bundle(nodeElement);
				}
				if (nodeElement.getTagName().equals("navigation-case")) {
					visitElement_navigation_case(nodeElement);
				}
				if (nodeElement.getTagName().equals("navigation-rule")) {
					visitElement_navigation_rule(nodeElement);
				}
				if (nodeElement.getTagName().equals("property")) {
					visitElement_property(nodeElement);
				}
				if (nodeElement.getTagName().equals("property-extension")) {
					visitElement_property_extension(nodeElement);
				}
				if (nodeElement.getTagName().equals("referenced-bean")) {
					visitElement_referenced_bean(nodeElement);
				}
				if (nodeElement.getTagName().equals("render-kit")) {
					visitElement_render_kit(nodeElement);
				}
				if (nodeElement.getTagName().equals("renderer")) {
					visitElement_renderer(nodeElement);
				}
				if (nodeElement.getTagName().equals("renderer-extension")) {
					visitElement_renderer_extension(nodeElement);
				}
				if (nodeElement.getTagName().equals("validator")) {
					visitElement_validator(nodeElement);
				}
				if (nodeElement.getTagName().equals("list-entries")) {
					visitElement_list_entries(nodeElement);
				}
				if (nodeElement.getTagName().equals("action-listener")) {
					visitElement_action_listener(nodeElement);
				}
				if (nodeElement.getTagName().equals("application-factory")) {
					visitElement_application_factory(nodeElement);
				}
				if (nodeElement.getTagName().equals("attribute-class")) {
					visitElement_attribute_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("attribute-name")) {
					visitElement_attribute_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("component-class")) {
					visitElement_component_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("component-family")) {
					visitElement_component_family(nodeElement);
				}
				if (nodeElement.getTagName().equals("component-type")) {
					visitElement_component_type(nodeElement);
				}
				if (nodeElement.getTagName().equals("converter-class")) {
					visitElement_converter_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("converter-for-class")) {
					visitElement_converter_for_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("converter-id")) {
					visitElement_converter_id(nodeElement);
				}
				if (nodeElement.getTagName().equals("default-render-kit-id")) {
					visitElement_default_render_kit_id(nodeElement);
				}
				if (nodeElement.getTagName().equals("default-locale")) {
					visitElement_default_locale(nodeElement);
				}
				if (nodeElement.getTagName().equals("default-value")) {
					visitElement_default_value(nodeElement);
				}
				if (nodeElement.getTagName().equals("description")) {
					visitElement_description(nodeElement);
				}
				if (nodeElement.getTagName().equals("display-name")) {
					visitElement_display_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("faces-context-factory")) {
					visitElement_faces_context_factory(nodeElement);
				}
				if (nodeElement.getTagName().equals("from-action")) {
					visitElement_from_action(nodeElement);
				}
				if (nodeElement.getTagName().equals("from-outcome")) {
					visitElement_from_outcome(nodeElement);
				}
				if (nodeElement.getTagName().equals("from-view-id")) {
					visitElement_from_view_id(nodeElement);
				}
				if (nodeElement.getTagName().equals("key")) {
					visitElement_key(nodeElement);
				}
				if (nodeElement.getTagName().equals("key-class")) {
					visitElement_key_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("large-icon")) {
					visitElement_large_icon(nodeElement);
				}
				if (nodeElement.getTagName().equals("lifecycle-factory")) {
					visitElement_lifecycle_factory(nodeElement);
				}
				if (nodeElement.getTagName().equals("managed-bean-class")) {
					visitElement_managed_bean_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("managed-bean-name")) {
					visitElement_managed_bean_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("managed-bean-scope")) {
					visitElement_managed_bean_scope(nodeElement);
				}
				if (nodeElement.getTagName().equals("navigation-handler")) {
					visitElement_navigation_handler(nodeElement);
				}
				if (nodeElement.getTagName().equals("phase-listener")) {
					visitElement_phase_listener(nodeElement);
				}
				if (nodeElement.getTagName().equals("redirect")) {
					visitElement_redirect(nodeElement);
				}
				if (nodeElement.getTagName().equals("suggested-value")) {
					visitElement_suggested_value(nodeElement);
				}
				if (nodeElement.getTagName().equals("view-handler")) {
					visitElement_view_handler(nodeElement);
				}
				if (nodeElement.getTagName().equals("state-manager")) {
					visitElement_state_manager(nodeElement);
				}
				if (nodeElement.getTagName().equals("null-value")) {
					visitElement_null_value(nodeElement);
				}
				if (nodeElement.getTagName().equals("property-class")) {
					visitElement_property_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("property-name")) {
					visitElement_property_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("property-resolver")) {
					visitElement_property_resolver(nodeElement);
				}
				if (nodeElement.getTagName().equals("referenced-bean-class")) {
					visitElement_referenced_bean_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("referenced-bean-name")) {
					visitElement_referenced_bean_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("render-kit-id")) {
					visitElement_render_kit_id(nodeElement);
				}
				if (nodeElement.getTagName().equals("render-kit-class")) {
					visitElement_render_kit_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("renderer-class")) {
					visitElement_renderer_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("render-kit-factory")) {
					visitElement_render_kit_factory(nodeElement);
				}
				if (nodeElement.getTagName().equals("renderer-type")) {
					visitElement_renderer_type(nodeElement);
				}
				if (nodeElement.getTagName().equals("small-icon")) {
					visitElement_small_icon(nodeElement);
				}
				if (nodeElement.getTagName().equals("supported-locale")) {
					visitElement_supported_locale(nodeElement);
				}
				if (nodeElement.getTagName().equals("to-view-id")) {
					visitElement_to_view_id(nodeElement);
				}
				if (nodeElement.getTagName().equals("validator-class")) {
					visitElement_validator_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("validator-id")) {
					visitElement_validator_id(nodeElement);
				}
				if (nodeElement.getTagName().equals("value")) {
					visitElement_value(nodeElement);
				}
				if (nodeElement.getTagName().equals("value-class")) {
					visitElement_value_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("variable-resolver")) {
					visitElement_variable_resolver(nodeElement);
				}
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named facet.
	 */
	void visitElement_facet(org.w3c.dom.Element element) { // <facet>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <facet id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				if (nodeElement.getTagName().equals("facet-extension")) {
					visitElement_facet_extension(nodeElement);
				}
				if (nodeElement.getTagName().equals("facet-name")) {
					visitElement_facet_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("icon")) {
					visitElement_icon(nodeElement);
				}
				if (nodeElement.getTagName().equals("description")) {
					visitElement_description(nodeElement);
				}
				if (nodeElement.getTagName().equals("display-name")) {
					visitElement_display_name(nodeElement);
				}
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named facet-extension.
	 */
	void visitElement_facet_extension(org.w3c.dom.Element element) { // <facet-extension>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <facet-extension id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				if (nodeElement.getTagName().equals("faces-config")) {
					visitElement_faces_config(nodeElement);
				}
				if (nodeElement.getTagName().equals("application")) {
					visitElement_application(nodeElement);
				}
				if (nodeElement.getTagName().equals("factory")) {
					visitElement_factory(nodeElement);
				}
				if (nodeElement.getTagName().equals("attribute")) {
					visitElement_attribute(nodeElement);
				}
				if (nodeElement.getTagName().equals("attribute-extension")) {
					visitElement_attribute_extension(nodeElement);
				}
				if (nodeElement.getTagName().equals("component")) {
					visitElement_component(nodeElement);
				}
				if (nodeElement.getTagName().equals("component-extension")) {
					visitElement_component_extension(nodeElement);
				}
				if (nodeElement.getTagName().equals("facet")) {
					visitElement_facet(nodeElement);
				}
				if (nodeElement.getTagName().equals("facet-extension")) {
					visitElement_facet_extension(nodeElement);
				}
				if (nodeElement.getTagName().equals("facet-name")) {
					visitElement_facet_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("converter")) {
					visitElement_converter(nodeElement);
				}
				if (nodeElement.getTagName().equals("icon")) {
					visitElement_icon(nodeElement);
				}
				if (nodeElement.getTagName().equals("lifecycle")) {
					visitElement_lifecycle(nodeElement);
				}
				if (nodeElement.getTagName().equals("locale-config")) {
					visitElement_locale_config(nodeElement);
				}
				if (nodeElement.getTagName().equals("managed-bean")) {
					visitElement_managed_bean(nodeElement);
				}
				if (nodeElement.getTagName().equals("managed-property")) {
					visitElement_managed_property(nodeElement);
				}
				if (nodeElement.getTagName().equals("map-entry")) {
					visitElement_map_entry(nodeElement);
				}
				if (nodeElement.getTagName().equals("map-entries")) {
					visitElement_map_entries(nodeElement);
				}
				if (nodeElement.getTagName().equals("message-bundle")) {
					visitElement_message_bundle(nodeElement);
				}
				if (nodeElement.getTagName().equals("navigation-case")) {
					visitElement_navigation_case(nodeElement);
				}
				if (nodeElement.getTagName().equals("navigation-rule")) {
					visitElement_navigation_rule(nodeElement);
				}
				if (nodeElement.getTagName().equals("property")) {
					visitElement_property(nodeElement);
				}
				if (nodeElement.getTagName().equals("property-extension")) {
					visitElement_property_extension(nodeElement);
				}
				if (nodeElement.getTagName().equals("referenced-bean")) {
					visitElement_referenced_bean(nodeElement);
				}
				if (nodeElement.getTagName().equals("render-kit")) {
					visitElement_render_kit(nodeElement);
				}
				if (nodeElement.getTagName().equals("renderer")) {
					visitElement_renderer(nodeElement);
				}
				if (nodeElement.getTagName().equals("renderer-extension")) {
					visitElement_renderer_extension(nodeElement);
				}
				if (nodeElement.getTagName().equals("validator")) {
					visitElement_validator(nodeElement);
				}
				if (nodeElement.getTagName().equals("list-entries")) {
					visitElement_list_entries(nodeElement);
				}
				if (nodeElement.getTagName().equals("action-listener")) {
					visitElement_action_listener(nodeElement);
				}
				if (nodeElement.getTagName().equals("application-factory")) {
					visitElement_application_factory(nodeElement);
				}
				if (nodeElement.getTagName().equals("attribute-class")) {
					visitElement_attribute_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("attribute-name")) {
					visitElement_attribute_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("component-class")) {
					visitElement_component_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("component-family")) {
					visitElement_component_family(nodeElement);
				}
				if (nodeElement.getTagName().equals("component-type")) {
					visitElement_component_type(nodeElement);
				}
				if (nodeElement.getTagName().equals("converter-class")) {
					visitElement_converter_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("converter-for-class")) {
					visitElement_converter_for_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("converter-id")) {
					visitElement_converter_id(nodeElement);
				}
				if (nodeElement.getTagName().equals("default-render-kit-id")) {
					visitElement_default_render_kit_id(nodeElement);
				}
				if (nodeElement.getTagName().equals("default-locale")) {
					visitElement_default_locale(nodeElement);
				}
				if (nodeElement.getTagName().equals("default-value")) {
					visitElement_default_value(nodeElement);
				}
				if (nodeElement.getTagName().equals("description")) {
					visitElement_description(nodeElement);
				}
				if (nodeElement.getTagName().equals("display-name")) {
					visitElement_display_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("faces-context-factory")) {
					visitElement_faces_context_factory(nodeElement);
				}
				if (nodeElement.getTagName().equals("from-action")) {
					visitElement_from_action(nodeElement);
				}
				if (nodeElement.getTagName().equals("from-outcome")) {
					visitElement_from_outcome(nodeElement);
				}
				if (nodeElement.getTagName().equals("from-view-id")) {
					visitElement_from_view_id(nodeElement);
				}
				if (nodeElement.getTagName().equals("key")) {
					visitElement_key(nodeElement);
				}
				if (nodeElement.getTagName().equals("key-class")) {
					visitElement_key_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("large-icon")) {
					visitElement_large_icon(nodeElement);
				}
				if (nodeElement.getTagName().equals("lifecycle-factory")) {
					visitElement_lifecycle_factory(nodeElement);
				}
				if (nodeElement.getTagName().equals("managed-bean-class")) {
					visitElement_managed_bean_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("managed-bean-name")) {
					visitElement_managed_bean_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("managed-bean-scope")) {
					visitElement_managed_bean_scope(nodeElement);
				}
				if (nodeElement.getTagName().equals("navigation-handler")) {
					visitElement_navigation_handler(nodeElement);
				}
				if (nodeElement.getTagName().equals("phase-listener")) {
					visitElement_phase_listener(nodeElement);
				}
				if (nodeElement.getTagName().equals("redirect")) {
					visitElement_redirect(nodeElement);
				}
				if (nodeElement.getTagName().equals("suggested-value")) {
					visitElement_suggested_value(nodeElement);
				}
				if (nodeElement.getTagName().equals("view-handler")) {
					visitElement_view_handler(nodeElement);
				}
				if (nodeElement.getTagName().equals("state-manager")) {
					visitElement_state_manager(nodeElement);
				}
				if (nodeElement.getTagName().equals("null-value")) {
					visitElement_null_value(nodeElement);
				}
				if (nodeElement.getTagName().equals("property-class")) {
					visitElement_property_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("property-name")) {
					visitElement_property_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("property-resolver")) {
					visitElement_property_resolver(nodeElement);
				}
				if (nodeElement.getTagName().equals("referenced-bean-class")) {
					visitElement_referenced_bean_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("referenced-bean-name")) {
					visitElement_referenced_bean_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("render-kit-id")) {
					visitElement_render_kit_id(nodeElement);
				}
				if (nodeElement.getTagName().equals("render-kit-class")) {
					visitElement_render_kit_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("renderer-class")) {
					visitElement_renderer_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("render-kit-factory")) {
					visitElement_render_kit_factory(nodeElement);
				}
				if (nodeElement.getTagName().equals("renderer-type")) {
					visitElement_renderer_type(nodeElement);
				}
				if (nodeElement.getTagName().equals("small-icon")) {
					visitElement_small_icon(nodeElement);
				}
				if (nodeElement.getTagName().equals("supported-locale")) {
					visitElement_supported_locale(nodeElement);
				}
				if (nodeElement.getTagName().equals("to-view-id")) {
					visitElement_to_view_id(nodeElement);
				}
				if (nodeElement.getTagName().equals("validator-class")) {
					visitElement_validator_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("validator-id")) {
					visitElement_validator_id(nodeElement);
				}
				if (nodeElement.getTagName().equals("value")) {
					visitElement_value(nodeElement);
				}
				if (nodeElement.getTagName().equals("value-class")) {
					visitElement_value_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("variable-resolver")) {
					visitElement_variable_resolver(nodeElement);
				}
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named facet-name.
	 */
	void visitElement_facet_name(org.w3c.dom.Element element) { // <facet-name>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <facet-name id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named converter.
	 */
	void visitElement_converter(org.w3c.dom.Element element) { // <converter>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <converter id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				if (nodeElement.getTagName().equals("attribute")) {
					visitElement_attribute(nodeElement);
				}
				if (nodeElement.getTagName().equals("icon")) {
					visitElement_icon(nodeElement);
				}
				if (nodeElement.getTagName().equals("property")) {
					visitElement_property(nodeElement);
				}
				if (nodeElement.getTagName().equals("converter-class")) {
					visitElement_converter_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("converter-for-class")) {
					visitElement_converter_for_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("converter-id")) {
					visitElement_converter_id(nodeElement);
				}
				if (nodeElement.getTagName().equals("description")) {
					visitElement_description(nodeElement);
				}
				if (nodeElement.getTagName().equals("display-name")) {
					visitElement_display_name(nodeElement);
				}
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named icon.
	 */
	void visitElement_icon(org.w3c.dom.Element element) { // <icon>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("xml:lang")) { // <icon xml:lang="???">
			// attr.getValue();
			}
			if (attr.getName().equals("id")) { // <icon id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				if (nodeElement.getTagName().equals("large-icon")) {
					visitElement_large_icon(nodeElement);
				}
				if (nodeElement.getTagName().equals("small-icon")) {
					visitElement_small_icon(nodeElement);
				}
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named lifecycle.
	 */
	void visitElement_lifecycle(org.w3c.dom.Element element) { // <lifecycle>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <lifecycle id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				if (nodeElement.getTagName().equals("phase-listener")) {
					visitElement_phase_listener(nodeElement);
				}
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named locale-config.
	 */
	void visitElement_locale_config(org.w3c.dom.Element element) { // <locale-config>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <locale-config id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				if (nodeElement.getTagName().equals("default-locale")) {
					visitElement_default_locale(nodeElement);
				}
				if (nodeElement.getTagName().equals("supported-locale")) {
					visitElement_supported_locale(nodeElement);
				}
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named managed-bean.
	 */
	void visitElement_managed_bean(org.w3c.dom.Element element) { // <managed-bean>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <managed-bean id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				if (nodeElement.getTagName().equals("icon")) {
					visitElement_icon(nodeElement);
				}
				if (nodeElement.getTagName().equals("managed-property")) {
					visitElement_managed_property(nodeElement);
				}
				if (nodeElement.getTagName().equals("map-entries")) {
					visitElement_map_entries(nodeElement);
				}
				if (nodeElement.getTagName().equals("list-entries")) {
					visitElement_list_entries(nodeElement);
				}
				if (nodeElement.getTagName().equals("description")) {
					visitElement_description(nodeElement);
				}
				if (nodeElement.getTagName().equals("display-name")) {
					visitElement_display_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("managed-bean-class")) {
					visitElement_managed_bean_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("managed-bean-name")) {
					visitElement_managed_bean_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("managed-bean-scope")) {
					visitElement_managed_bean_scope(nodeElement);
				}
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named managed-property.
	 */
	void visitElement_managed_property(org.w3c.dom.Element element) { // <managed-property>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <managed-property id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				if (nodeElement.getTagName().equals("icon")) {
					visitElement_icon(nodeElement);
				}
				if (nodeElement.getTagName().equals("map-entries")) {
					visitElement_map_entries(nodeElement);
				}
				if (nodeElement.getTagName().equals("list-entries")) {
					visitElement_list_entries(nodeElement);
				}
				if (nodeElement.getTagName().equals("description")) {
					visitElement_description(nodeElement);
				}
				if (nodeElement.getTagName().equals("display-name")) {
					visitElement_display_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("null-value")) {
					visitElement_null_value(nodeElement);
				}
				if (nodeElement.getTagName().equals("property-class")) {
					visitElement_property_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("property-name")) {
					visitElement_property_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("value")) {
					visitElement_value(nodeElement);
				}
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named map-entry.
	 */
	void visitElement_map_entry(org.w3c.dom.Element element) { // <map-entry>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <map-entry id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				if (nodeElement.getTagName().equals("key")) {
					visitElement_key(nodeElement);
				}
				if (nodeElement.getTagName().equals("null-value")) {
					visitElement_null_value(nodeElement);
				}
				if (nodeElement.getTagName().equals("value")) {
					visitElement_value(nodeElement);
				}
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named map-entries.
	 */
	void visitElement_map_entries(org.w3c.dom.Element element) { // <map-entries>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <map-entries id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				if (nodeElement.getTagName().equals("map-entry")) {
					visitElement_map_entry(nodeElement);
				}
				if (nodeElement.getTagName().equals("key-class")) {
					visitElement_key_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("value-class")) {
					visitElement_value_class(nodeElement);
				}
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named message-bundle.
	 */
	void visitElement_message_bundle(org.w3c.dom.Element element) { // <message-bundle>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <message-bundle id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named navigation-case.
	 */
	void visitElement_navigation_case(org.w3c.dom.Element element) { // <navigation-case>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <navigation-case id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				if (nodeElement.getTagName().equals("icon")) {
					visitElement_icon(nodeElement);
				}
				if (nodeElement.getTagName().equals("description")) {
					visitElement_description(nodeElement);
				}
				if (nodeElement.getTagName().equals("display-name")) {
					visitElement_display_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("from-action")) {
					visitElement_from_action(nodeElement);
				}
				if (nodeElement.getTagName().equals("from-outcome")) {
					visitElement_from_outcome(nodeElement);
				}
				if (nodeElement.getTagName().equals("redirect")) {
					visitElement_redirect(nodeElement);
				}
				if (nodeElement.getTagName().equals("to-view-id")) {
					visitElement_to_view_id(nodeElement);
				}
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named navigation-rule.
	 */
	void visitElement_navigation_rule(org.w3c.dom.Element element) { // <navigation-rule>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <navigation-rule id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				if (nodeElement.getTagName().equals("icon")) {
					visitElement_icon(nodeElement);
				}
				if (nodeElement.getTagName().equals("navigation-case")) {
					visitElement_navigation_case(nodeElement);
				}
				if (nodeElement.getTagName().equals("description")) {
					visitElement_description(nodeElement);
				}
				if (nodeElement.getTagName().equals("display-name")) {
					visitElement_display_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("from-view-id")) {
					visitElement_from_view_id(nodeElement);
				}
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named property.
	 */
	void visitElement_property(org.w3c.dom.Element element) { // <property>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <property id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				if (nodeElement.getTagName().equals("icon")) {
					visitElement_icon(nodeElement);
				}
				if (nodeElement.getTagName().equals("property-extension")) {
					visitElement_property_extension(nodeElement);
				}
				if (nodeElement.getTagName().equals("default-value")) {
					visitElement_default_value(nodeElement);
				}
				if (nodeElement.getTagName().equals("description")) {
					visitElement_description(nodeElement);
				}
				if (nodeElement.getTagName().equals("display-name")) {
					visitElement_display_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("suggested-value")) {
					visitElement_suggested_value(nodeElement);
				}
				if (nodeElement.getTagName().equals("property-class")) {
					visitElement_property_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("property-name")) {
					visitElement_property_name(nodeElement);
				}
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named property-extension.
	 */
	void visitElement_property_extension(org.w3c.dom.Element element) { // <property-extension>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <property-extension id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				if (nodeElement.getTagName().equals("faces-config")) {
					visitElement_faces_config(nodeElement);
				}
				if (nodeElement.getTagName().equals("application")) {
					visitElement_application(nodeElement);
				}
				if (nodeElement.getTagName().equals("factory")) {
					visitElement_factory(nodeElement);
				}
				if (nodeElement.getTagName().equals("attribute")) {
					visitElement_attribute(nodeElement);
				}
				if (nodeElement.getTagName().equals("attribute-extension")) {
					visitElement_attribute_extension(nodeElement);
				}
				if (nodeElement.getTagName().equals("component")) {
					visitElement_component(nodeElement);
				}
				if (nodeElement.getTagName().equals("component-extension")) {
					visitElement_component_extension(nodeElement);
				}
				if (nodeElement.getTagName().equals("facet")) {
					visitElement_facet(nodeElement);
				}
				if (nodeElement.getTagName().equals("facet-extension")) {
					visitElement_facet_extension(nodeElement);
				}
				if (nodeElement.getTagName().equals("facet-name")) {
					visitElement_facet_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("converter")) {
					visitElement_converter(nodeElement);
				}
				if (nodeElement.getTagName().equals("icon")) {
					visitElement_icon(nodeElement);
				}
				if (nodeElement.getTagName().equals("lifecycle")) {
					visitElement_lifecycle(nodeElement);
				}
				if (nodeElement.getTagName().equals("locale-config")) {
					visitElement_locale_config(nodeElement);
				}
				if (nodeElement.getTagName().equals("managed-bean")) {
					visitElement_managed_bean(nodeElement);
				}
				if (nodeElement.getTagName().equals("managed-property")) {
					visitElement_managed_property(nodeElement);
				}
				if (nodeElement.getTagName().equals("map-entry")) {
					visitElement_map_entry(nodeElement);
				}
				if (nodeElement.getTagName().equals("map-entries")) {
					visitElement_map_entries(nodeElement);
				}
				if (nodeElement.getTagName().equals("message-bundle")) {
					visitElement_message_bundle(nodeElement);
				}
				if (nodeElement.getTagName().equals("navigation-case")) {
					visitElement_navigation_case(nodeElement);
				}
				if (nodeElement.getTagName().equals("navigation-rule")) {
					visitElement_navigation_rule(nodeElement);
				}
				if (nodeElement.getTagName().equals("property")) {
					visitElement_property(nodeElement);
				}
				if (nodeElement.getTagName().equals("property-extension")) {
					visitElement_property_extension(nodeElement);
				}
				if (nodeElement.getTagName().equals("referenced-bean")) {
					visitElement_referenced_bean(nodeElement);
				}
				if (nodeElement.getTagName().equals("render-kit")) {
					visitElement_render_kit(nodeElement);
				}
				if (nodeElement.getTagName().equals("renderer")) {
					visitElement_renderer(nodeElement);
				}
				if (nodeElement.getTagName().equals("renderer-extension")) {
					visitElement_renderer_extension(nodeElement);
				}
				if (nodeElement.getTagName().equals("validator")) {
					visitElement_validator(nodeElement);
				}
				if (nodeElement.getTagName().equals("list-entries")) {
					visitElement_list_entries(nodeElement);
				}
				if (nodeElement.getTagName().equals("action-listener")) {
					visitElement_action_listener(nodeElement);
				}
				if (nodeElement.getTagName().equals("application-factory")) {
					visitElement_application_factory(nodeElement);
				}
				if (nodeElement.getTagName().equals("attribute-class")) {
					visitElement_attribute_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("attribute-name")) {
					visitElement_attribute_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("component-class")) {
					visitElement_component_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("component-family")) {
					visitElement_component_family(nodeElement);
				}
				if (nodeElement.getTagName().equals("component-type")) {
					visitElement_component_type(nodeElement);
				}
				if (nodeElement.getTagName().equals("converter-class")) {
					visitElement_converter_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("converter-for-class")) {
					visitElement_converter_for_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("converter-id")) {
					visitElement_converter_id(nodeElement);
				}
				if (nodeElement.getTagName().equals("default-render-kit-id")) {
					visitElement_default_render_kit_id(nodeElement);
				}
				if (nodeElement.getTagName().equals("default-locale")) {
					visitElement_default_locale(nodeElement);
				}
				if (nodeElement.getTagName().equals("default-value")) {
					visitElement_default_value(nodeElement);
				}
				if (nodeElement.getTagName().equals("description")) {
					visitElement_description(nodeElement);
				}
				if (nodeElement.getTagName().equals("display-name")) {
					visitElement_display_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("faces-context-factory")) {
					visitElement_faces_context_factory(nodeElement);
				}
				if (nodeElement.getTagName().equals("from-action")) {
					visitElement_from_action(nodeElement);
				}
				if (nodeElement.getTagName().equals("from-outcome")) {
					visitElement_from_outcome(nodeElement);
				}
				if (nodeElement.getTagName().equals("from-view-id")) {
					visitElement_from_view_id(nodeElement);
				}
				if (nodeElement.getTagName().equals("key")) {
					visitElement_key(nodeElement);
				}
				if (nodeElement.getTagName().equals("key-class")) {
					visitElement_key_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("large-icon")) {
					visitElement_large_icon(nodeElement);
				}
				if (nodeElement.getTagName().equals("lifecycle-factory")) {
					visitElement_lifecycle_factory(nodeElement);
				}
				if (nodeElement.getTagName().equals("managed-bean-class")) {
					visitElement_managed_bean_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("managed-bean-name")) {
					visitElement_managed_bean_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("managed-bean-scope")) {
					visitElement_managed_bean_scope(nodeElement);
				}
				if (nodeElement.getTagName().equals("navigation-handler")) {
					visitElement_navigation_handler(nodeElement);
				}
				if (nodeElement.getTagName().equals("phase-listener")) {
					visitElement_phase_listener(nodeElement);
				}
				if (nodeElement.getTagName().equals("redirect")) {
					visitElement_redirect(nodeElement);
				}
				if (nodeElement.getTagName().equals("suggested-value")) {
					visitElement_suggested_value(nodeElement);
				}
				if (nodeElement.getTagName().equals("view-handler")) {
					visitElement_view_handler(nodeElement);
				}
				if (nodeElement.getTagName().equals("state-manager")) {
					visitElement_state_manager(nodeElement);
				}
				if (nodeElement.getTagName().equals("null-value")) {
					visitElement_null_value(nodeElement);
				}
				if (nodeElement.getTagName().equals("property-class")) {
					visitElement_property_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("property-name")) {
					visitElement_property_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("property-resolver")) {
					visitElement_property_resolver(nodeElement);
				}
				if (nodeElement.getTagName().equals("referenced-bean-class")) {
					visitElement_referenced_bean_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("referenced-bean-name")) {
					visitElement_referenced_bean_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("render-kit-id")) {
					visitElement_render_kit_id(nodeElement);
				}
				if (nodeElement.getTagName().equals("render-kit-class")) {
					visitElement_render_kit_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("renderer-class")) {
					visitElement_renderer_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("render-kit-factory")) {
					visitElement_render_kit_factory(nodeElement);
				}
				if (nodeElement.getTagName().equals("renderer-type")) {
					visitElement_renderer_type(nodeElement);
				}
				if (nodeElement.getTagName().equals("small-icon")) {
					visitElement_small_icon(nodeElement);
				}
				if (nodeElement.getTagName().equals("supported-locale")) {
					visitElement_supported_locale(nodeElement);
				}
				if (nodeElement.getTagName().equals("to-view-id")) {
					visitElement_to_view_id(nodeElement);
				}
				if (nodeElement.getTagName().equals("validator-class")) {
					visitElement_validator_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("validator-id")) {
					visitElement_validator_id(nodeElement);
				}
				if (nodeElement.getTagName().equals("value")) {
					visitElement_value(nodeElement);
				}
				if (nodeElement.getTagName().equals("value-class")) {
					visitElement_value_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("variable-resolver")) {
					visitElement_variable_resolver(nodeElement);
				}
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named referenced-bean.
	 */
	void visitElement_referenced_bean(org.w3c.dom.Element element) { // <referenced-bean>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <referenced-bean id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				if (nodeElement.getTagName().equals("icon")) {
					visitElement_icon(nodeElement);
				}
				if (nodeElement.getTagName().equals("description")) {
					visitElement_description(nodeElement);
				}
				if (nodeElement.getTagName().equals("display-name")) {
					visitElement_display_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("referenced-bean-class")) {
					visitElement_referenced_bean_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("referenced-bean-name")) {
					visitElement_referenced_bean_name(nodeElement);
				}
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			}
		}
	}

	private void initDom(Element render_kit_element) {
		
		if(visitedElement_render_kit){
			return;
		}
		
		FacesConfigParserHelper facesConfigParserHelper = new FacesConfigParserHelper(addonXmlFile);		
		RendererBean[] rendererBeans = facesConfigParserHelper.getRendererBeans();

		Document document = render_kit_element.getOwnerDocument();
		for (int i = 0; i < rendererBeans.length; i++) {
			// component element
			Element rdElement = document.createElement("renderer");
			render_kit_element.appendChild(document.createTextNode("\n"));
			render_kit_element.appendChild(rdElement);
			// closing component
			rdElement.appendChild(document.createTextNode("\n\t"));
			
			Element cfElement = document.createElement("component-family");
			cfElement.appendChild(document.createTextNode(rendererBeans[i].getComponentFamily()));
			rdElement.appendChild(cfElement);
			cfElement.appendChild(document.createTextNode("\n\t"));
			
			rdElement.appendChild(document.createTextNode("\n\t"));
			Element rtElement = document.createElement("renderer-type");
			rtElement.appendChild(document.createTextNode(rendererBeans[i].getRendererType()));
			rtElement.appendChild(document.createTextNode("\n\t"));
			rdElement.appendChild(rtElement);
			
			rdElement.appendChild(document.createTextNode("\n\t"));
			Element rcElement = document.createElement("renderer-class");
			rcElement.appendChild(document.createTextNode(rendererBeans[i].getRendererClass()));
			rdElement.appendChild(rcElement);
			rcElement.appendChild(document.createTextNode("\n\t"));
			rdElement.appendChild(document.createTextNode("\n"));
		}
		
		visitedElement_render_kit = true;

	}
	
	private boolean visitedElement_render_kit = false;
	
	/**
	 * Scan through org.w3c.dom.Element named render-kit.
	 */
	void visitElement_render_kit(org.w3c.dom.Element element) { // <render-kit>
	// element.getValue();
		
//add web ui custom renderer 
		initDom(element);
		
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <render-kit id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				if (nodeElement.getTagName().equals("icon")) {
					visitElement_icon(nodeElement);
				}
				if (nodeElement.getTagName().equals("renderer")) {
					visitElement_renderer(nodeElement);
				}
				if (nodeElement.getTagName().equals("description")) {
					visitElement_description(nodeElement);
				}
				if (nodeElement.getTagName().equals("display-name")) {
					visitElement_display_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("render-kit-id")) {
					visitElement_render_kit_id(nodeElement);
				}
				if (nodeElement.getTagName().equals("render-kit-class")) {
					visitElement_render_kit_class(nodeElement);
				}
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named renderer.
	 */
	void visitElement_renderer(org.w3c.dom.Element element) { // <renderer>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <renderer id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				if (nodeElement.getTagName().equals("attribute")) {
					visitElement_attribute(nodeElement);
				}
				if (nodeElement.getTagName().equals("facet")) {
					visitElement_facet(nodeElement);
				}
				if (nodeElement.getTagName().equals("icon")) {
					visitElement_icon(nodeElement);
				}
				if (nodeElement.getTagName().equals("renderer-extension")) {
					visitElement_renderer_extension(nodeElement);
				}
				if (nodeElement.getTagName().equals("component-family")) {
					visitElement_component_family(nodeElement);
				}
				if (nodeElement.getTagName().equals("description")) {
					visitElement_description(nodeElement);
				}
				if (nodeElement.getTagName().equals("display-name")) {
					visitElement_display_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("renderer-class")) {
					visitElement_renderer_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("renderer-type")) {
					visitElement_renderer_type(nodeElement);
				}
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named renderer-extension.
	 */
	void visitElement_renderer_extension(org.w3c.dom.Element element) { // <renderer-extension>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <renderer-extension id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				if (nodeElement.getTagName().equals("faces-config")) {
					visitElement_faces_config(nodeElement);
				}
				if (nodeElement.getTagName().equals("application")) {
					visitElement_application(nodeElement);
				}
				if (nodeElement.getTagName().equals("factory")) {
					visitElement_factory(nodeElement);
				}
				if (nodeElement.getTagName().equals("attribute")) {
					visitElement_attribute(nodeElement);
				}
				if (nodeElement.getTagName().equals("attribute-extension")) {
					visitElement_attribute_extension(nodeElement);
				}
				if (nodeElement.getTagName().equals("component")) {
					visitElement_component(nodeElement);
				}
				if (nodeElement.getTagName().equals("component-extension")) {
					visitElement_component_extension(nodeElement);
				}
				if (nodeElement.getTagName().equals("facet")) {
					visitElement_facet(nodeElement);
				}
				if (nodeElement.getTagName().equals("facet-extension")) {
					visitElement_facet_extension(nodeElement);
				}
				if (nodeElement.getTagName().equals("facet-name")) {
					visitElement_facet_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("converter")) {
					visitElement_converter(nodeElement);
				}
				if (nodeElement.getTagName().equals("icon")) {
					visitElement_icon(nodeElement);
				}
				if (nodeElement.getTagName().equals("lifecycle")) {
					visitElement_lifecycle(nodeElement);
				}
				if (nodeElement.getTagName().equals("locale-config")) {
					visitElement_locale_config(nodeElement);
				}
				if (nodeElement.getTagName().equals("managed-bean")) {
					visitElement_managed_bean(nodeElement);
				}
				if (nodeElement.getTagName().equals("managed-property")) {
					visitElement_managed_property(nodeElement);
				}
				if (nodeElement.getTagName().equals("map-entry")) {
					visitElement_map_entry(nodeElement);
				}
				if (nodeElement.getTagName().equals("map-entries")) {
					visitElement_map_entries(nodeElement);
				}
				if (nodeElement.getTagName().equals("message-bundle")) {
					visitElement_message_bundle(nodeElement);
				}
				if (nodeElement.getTagName().equals("navigation-case")) {
					visitElement_navigation_case(nodeElement);
				}
				if (nodeElement.getTagName().equals("navigation-rule")) {
					visitElement_navigation_rule(nodeElement);
				}
				if (nodeElement.getTagName().equals("property")) {
					visitElement_property(nodeElement);
				}
				if (nodeElement.getTagName().equals("property-extension")) {
					visitElement_property_extension(nodeElement);
				}
				if (nodeElement.getTagName().equals("referenced-bean")) {
					visitElement_referenced_bean(nodeElement);
				}
				if (nodeElement.getTagName().equals("render-kit")) {
					visitElement_render_kit(nodeElement);
				}
				if (nodeElement.getTagName().equals("renderer")) {
					visitElement_renderer(nodeElement);
				}
				if (nodeElement.getTagName().equals("renderer-extension")) {
					visitElement_renderer_extension(nodeElement);
				}
				if (nodeElement.getTagName().equals("validator")) {
					visitElement_validator(nodeElement);
				}
				if (nodeElement.getTagName().equals("list-entries")) {
					visitElement_list_entries(nodeElement);
				}
				if (nodeElement.getTagName().equals("action-listener")) {
					visitElement_action_listener(nodeElement);
				}
				if (nodeElement.getTagName().equals("application-factory")) {
					visitElement_application_factory(nodeElement);
				}
				if (nodeElement.getTagName().equals("attribute-class")) {
					visitElement_attribute_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("attribute-name")) {
					visitElement_attribute_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("component-class")) {
					visitElement_component_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("component-family")) {
					visitElement_component_family(nodeElement);
				}
				if (nodeElement.getTagName().equals("component-type")) {
					visitElement_component_type(nodeElement);
				}
				if (nodeElement.getTagName().equals("converter-class")) {
					visitElement_converter_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("converter-for-class")) {
					visitElement_converter_for_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("converter-id")) {
					visitElement_converter_id(nodeElement);
				}
				if (nodeElement.getTagName().equals("default-render-kit-id")) {
					visitElement_default_render_kit_id(nodeElement);
				}
				if (nodeElement.getTagName().equals("default-locale")) {
					visitElement_default_locale(nodeElement);
				}
				if (nodeElement.getTagName().equals("default-value")) {
					visitElement_default_value(nodeElement);
				}
				if (nodeElement.getTagName().equals("description")) {
					visitElement_description(nodeElement);
				}
				if (nodeElement.getTagName().equals("display-name")) {
					visitElement_display_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("faces-context-factory")) {
					visitElement_faces_context_factory(nodeElement);
				}
				if (nodeElement.getTagName().equals("from-action")) {
					visitElement_from_action(nodeElement);
				}
				if (nodeElement.getTagName().equals("from-outcome")) {
					visitElement_from_outcome(nodeElement);
				}
				if (nodeElement.getTagName().equals("from-view-id")) {
					visitElement_from_view_id(nodeElement);
				}
				if (nodeElement.getTagName().equals("key")) {
					visitElement_key(nodeElement);
				}
				if (nodeElement.getTagName().equals("key-class")) {
					visitElement_key_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("large-icon")) {
					visitElement_large_icon(nodeElement);
				}
				if (nodeElement.getTagName().equals("lifecycle-factory")) {
					visitElement_lifecycle_factory(nodeElement);
				}
				if (nodeElement.getTagName().equals("managed-bean-class")) {
					visitElement_managed_bean_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("managed-bean-name")) {
					visitElement_managed_bean_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("managed-bean-scope")) {
					visitElement_managed_bean_scope(nodeElement);
				}
				if (nodeElement.getTagName().equals("navigation-handler")) {
					visitElement_navigation_handler(nodeElement);
				}
				if (nodeElement.getTagName().equals("phase-listener")) {
					visitElement_phase_listener(nodeElement);
				}
				if (nodeElement.getTagName().equals("redirect")) {
					visitElement_redirect(nodeElement);
				}
				if (nodeElement.getTagName().equals("suggested-value")) {
					visitElement_suggested_value(nodeElement);
				}
				if (nodeElement.getTagName().equals("view-handler")) {
					visitElement_view_handler(nodeElement);
				}
				if (nodeElement.getTagName().equals("state-manager")) {
					visitElement_state_manager(nodeElement);
				}
				if (nodeElement.getTagName().equals("null-value")) {
					visitElement_null_value(nodeElement);
				}
				if (nodeElement.getTagName().equals("property-class")) {
					visitElement_property_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("property-name")) {
					visitElement_property_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("property-resolver")) {
					visitElement_property_resolver(nodeElement);
				}
				if (nodeElement.getTagName().equals("referenced-bean-class")) {
					visitElement_referenced_bean_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("referenced-bean-name")) {
					visitElement_referenced_bean_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("render-kit-id")) {
					visitElement_render_kit_id(nodeElement);
				}
				if (nodeElement.getTagName().equals("render-kit-class")) {
					visitElement_render_kit_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("renderer-class")) {
					visitElement_renderer_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("render-kit-factory")) {
					visitElement_render_kit_factory(nodeElement);
				}
				if (nodeElement.getTagName().equals("renderer-type")) {
					visitElement_renderer_type(nodeElement);
				}
				if (nodeElement.getTagName().equals("small-icon")) {
					visitElement_small_icon(nodeElement);
				}
				if (nodeElement.getTagName().equals("supported-locale")) {
					visitElement_supported_locale(nodeElement);
				}
				if (nodeElement.getTagName().equals("to-view-id")) {
					visitElement_to_view_id(nodeElement);
				}
				if (nodeElement.getTagName().equals("validator-class")) {
					visitElement_validator_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("validator-id")) {
					visitElement_validator_id(nodeElement);
				}
				if (nodeElement.getTagName().equals("value")) {
					visitElement_value(nodeElement);
				}
				if (nodeElement.getTagName().equals("value-class")) {
					visitElement_value_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("variable-resolver")) {
					visitElement_variable_resolver(nodeElement);
				}
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named validator.
	 */
	void visitElement_validator(org.w3c.dom.Element element) { // <validator>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <validator id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				if (nodeElement.getTagName().equals("attribute")) {
					visitElement_attribute(nodeElement);
				}
				if (nodeElement.getTagName().equals("icon")) {
					visitElement_icon(nodeElement);
				}
				if (nodeElement.getTagName().equals("property")) {
					visitElement_property(nodeElement);
				}
				if (nodeElement.getTagName().equals("description")) {
					visitElement_description(nodeElement);
				}
				if (nodeElement.getTagName().equals("display-name")) {
					visitElement_display_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("validator-class")) {
					visitElement_validator_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("validator-id")) {
					visitElement_validator_id(nodeElement);
				}
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named list-entries.
	 */
	void visitElement_list_entries(org.w3c.dom.Element element) { // <list-entries>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <list-entries id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				if (nodeElement.getTagName().equals("null-value")) {
					visitElement_null_value(nodeElement);
				}
				if (nodeElement.getTagName().equals("value")) {
					visitElement_value(nodeElement);
				}
				if (nodeElement.getTagName().equals("value-class")) {
					visitElement_value_class(nodeElement);
				}
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named action-listener.
	 */
	void visitElement_action_listener(org.w3c.dom.Element element) { // <action-listener>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <action-listener id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named application-factory.
	 */
	void visitElement_application_factory(org.w3c.dom.Element element) { // <application-factory>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <application-factory
												// id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named attribute-class.
	 */
	void visitElement_attribute_class(org.w3c.dom.Element element) { // <attribute-class>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <attribute-class id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named attribute-name.
	 */
	void visitElement_attribute_name(org.w3c.dom.Element element) { // <attribute-name>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <attribute-name id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named component-class.
	 */
	void visitElement_component_class(org.w3c.dom.Element element) { // <component-class>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <component-class id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named component-family.
	 */
	void visitElement_component_family(org.w3c.dom.Element element) { // <component-family>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <component-family id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named component-type.
	 */
	void visitElement_component_type(org.w3c.dom.Element element) { // <component-type>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <component-type id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named converter-class.
	 */
	void visitElement_converter_class(org.w3c.dom.Element element) { // <converter-class>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <converter-class id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named converter-for-class.
	 */
	void visitElement_converter_for_class(org.w3c.dom.Element element) { // <converter-for-class>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <converter-for-class
												// id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named converter-id.
	 */
	void visitElement_converter_id(org.w3c.dom.Element element) { // <converter-id>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <converter-id id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named default-render-kit-id.
	 */
	void visitElement_default_render_kit_id(org.w3c.dom.Element element) { // <default-render-kit-id>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <default-render-kit-id
												// id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named default-locale.
	 */
	void visitElement_default_locale(org.w3c.dom.Element element) { // <default-locale>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <default-locale id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named default-value.
	 */
	void visitElement_default_value(org.w3c.dom.Element element) { // <default-value>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <default-value id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named description.
	 */
	void visitElement_description(org.w3c.dom.Element element) { // <description>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("xml:lang")) { // <description
														// xml:lang="???">
			// attr.getValue();
			}
			if (attr.getName().equals("id")) { // <description id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				if (nodeElement.getTagName().equals("faces-config")) {
					visitElement_faces_config(nodeElement);
				}
				if (nodeElement.getTagName().equals("application")) {
					visitElement_application(nodeElement);
				}
				if (nodeElement.getTagName().equals("factory")) {
					visitElement_factory(nodeElement);
				}
				if (nodeElement.getTagName().equals("attribute")) {
					visitElement_attribute(nodeElement);
				}
				if (nodeElement.getTagName().equals("attribute-extension")) {
					visitElement_attribute_extension(nodeElement);
				}
				if (nodeElement.getTagName().equals("component")) {
					visitElement_component(nodeElement);
				}
				if (nodeElement.getTagName().equals("component-extension")) {
					visitElement_component_extension(nodeElement);
				}
				if (nodeElement.getTagName().equals("facet")) {
					visitElement_facet(nodeElement);
				}
				if (nodeElement.getTagName().equals("facet-extension")) {
					visitElement_facet_extension(nodeElement);
				}
				if (nodeElement.getTagName().equals("facet-name")) {
					visitElement_facet_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("converter")) {
					visitElement_converter(nodeElement);
				}
				if (nodeElement.getTagName().equals("icon")) {
					visitElement_icon(nodeElement);
				}
				if (nodeElement.getTagName().equals("lifecycle")) {
					visitElement_lifecycle(nodeElement);
				}
				if (nodeElement.getTagName().equals("locale-config")) {
					visitElement_locale_config(nodeElement);
				}
				if (nodeElement.getTagName().equals("managed-bean")) {
					visitElement_managed_bean(nodeElement);
				}
				if (nodeElement.getTagName().equals("managed-property")) {
					visitElement_managed_property(nodeElement);
				}
				if (nodeElement.getTagName().equals("map-entry")) {
					visitElement_map_entry(nodeElement);
				}
				if (nodeElement.getTagName().equals("map-entries")) {
					visitElement_map_entries(nodeElement);
				}
				if (nodeElement.getTagName().equals("message-bundle")) {
					visitElement_message_bundle(nodeElement);
				}
				if (nodeElement.getTagName().equals("navigation-case")) {
					visitElement_navigation_case(nodeElement);
				}
				if (nodeElement.getTagName().equals("navigation-rule")) {
					visitElement_navigation_rule(nodeElement);
				}
				if (nodeElement.getTagName().equals("property")) {
					visitElement_property(nodeElement);
				}
				if (nodeElement.getTagName().equals("property-extension")) {
					visitElement_property_extension(nodeElement);
				}
				if (nodeElement.getTagName().equals("referenced-bean")) {
					visitElement_referenced_bean(nodeElement);
				}
				if (nodeElement.getTagName().equals("render-kit")) {
					visitElement_render_kit(nodeElement);
				}
				if (nodeElement.getTagName().equals("renderer")) {
					visitElement_renderer(nodeElement);
				}
				if (nodeElement.getTagName().equals("renderer-extension")) {
					visitElement_renderer_extension(nodeElement);
				}
				if (nodeElement.getTagName().equals("validator")) {
					visitElement_validator(nodeElement);
				}
				if (nodeElement.getTagName().equals("list-entries")) {
					visitElement_list_entries(nodeElement);
				}
				if (nodeElement.getTagName().equals("action-listener")) {
					visitElement_action_listener(nodeElement);
				}
				if (nodeElement.getTagName().equals("application-factory")) {
					visitElement_application_factory(nodeElement);
				}
				if (nodeElement.getTagName().equals("attribute-class")) {
					visitElement_attribute_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("attribute-name")) {
					visitElement_attribute_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("component-class")) {
					visitElement_component_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("component-family")) {
					visitElement_component_family(nodeElement);
				}
				if (nodeElement.getTagName().equals("component-type")) {
					visitElement_component_type(nodeElement);
				}
				if (nodeElement.getTagName().equals("converter-class")) {
					visitElement_converter_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("converter-for-class")) {
					visitElement_converter_for_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("converter-id")) {
					visitElement_converter_id(nodeElement);
				}
				if (nodeElement.getTagName().equals("default-render-kit-id")) {
					visitElement_default_render_kit_id(nodeElement);
				}
				if (nodeElement.getTagName().equals("default-locale")) {
					visitElement_default_locale(nodeElement);
				}
				if (nodeElement.getTagName().equals("default-value")) {
					visitElement_default_value(nodeElement);
				}
				if (nodeElement.getTagName().equals("description")) {
					visitElement_description(nodeElement);
				}
				if (nodeElement.getTagName().equals("display-name")) {
					visitElement_display_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("faces-context-factory")) {
					visitElement_faces_context_factory(nodeElement);
				}
				if (nodeElement.getTagName().equals("from-action")) {
					visitElement_from_action(nodeElement);
				}
				if (nodeElement.getTagName().equals("from-outcome")) {
					visitElement_from_outcome(nodeElement);
				}
				if (nodeElement.getTagName().equals("from-view-id")) {
					visitElement_from_view_id(nodeElement);
				}
				if (nodeElement.getTagName().equals("key")) {
					visitElement_key(nodeElement);
				}
				if (nodeElement.getTagName().equals("key-class")) {
					visitElement_key_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("large-icon")) {
					visitElement_large_icon(nodeElement);
				}
				if (nodeElement.getTagName().equals("lifecycle-factory")) {
					visitElement_lifecycle_factory(nodeElement);
				}
				if (nodeElement.getTagName().equals("managed-bean-class")) {
					visitElement_managed_bean_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("managed-bean-name")) {
					visitElement_managed_bean_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("managed-bean-scope")) {
					visitElement_managed_bean_scope(nodeElement);
				}
				if (nodeElement.getTagName().equals("navigation-handler")) {
					visitElement_navigation_handler(nodeElement);
				}
				if (nodeElement.getTagName().equals("phase-listener")) {
					visitElement_phase_listener(nodeElement);
				}
				if (nodeElement.getTagName().equals("redirect")) {
					visitElement_redirect(nodeElement);
				}
				if (nodeElement.getTagName().equals("suggested-value")) {
					visitElement_suggested_value(nodeElement);
				}
				if (nodeElement.getTagName().equals("view-handler")) {
					visitElement_view_handler(nodeElement);
				}
				if (nodeElement.getTagName().equals("state-manager")) {
					visitElement_state_manager(nodeElement);
				}
				if (nodeElement.getTagName().equals("null-value")) {
					visitElement_null_value(nodeElement);
				}
				if (nodeElement.getTagName().equals("property-class")) {
					visitElement_property_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("property-name")) {
					visitElement_property_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("property-resolver")) {
					visitElement_property_resolver(nodeElement);
				}
				if (nodeElement.getTagName().equals("referenced-bean-class")) {
					visitElement_referenced_bean_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("referenced-bean-name")) {
					visitElement_referenced_bean_name(nodeElement);
				}
				if (nodeElement.getTagName().equals("render-kit-id")) {
					visitElement_render_kit_id(nodeElement);
				}
				if (nodeElement.getTagName().equals("render-kit-class")) {
					visitElement_render_kit_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("renderer-class")) {
					visitElement_renderer_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("render-kit-factory")) {
					visitElement_render_kit_factory(nodeElement);
				}
				if (nodeElement.getTagName().equals("renderer-type")) {
					visitElement_renderer_type(nodeElement);
				}
				if (nodeElement.getTagName().equals("small-icon")) {
					visitElement_small_icon(nodeElement);
				}
				if (nodeElement.getTagName().equals("supported-locale")) {
					visitElement_supported_locale(nodeElement);
				}
				if (nodeElement.getTagName().equals("to-view-id")) {
					visitElement_to_view_id(nodeElement);
				}
				if (nodeElement.getTagName().equals("validator-class")) {
					visitElement_validator_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("validator-id")) {
					visitElement_validator_id(nodeElement);
				}
				if (nodeElement.getTagName().equals("value")) {
					visitElement_value(nodeElement);
				}
				if (nodeElement.getTagName().equals("value-class")) {
					visitElement_value_class(nodeElement);
				}
				if (nodeElement.getTagName().equals("variable-resolver")) {
					visitElement_variable_resolver(nodeElement);
				}
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named display-name.
	 */
	void visitElement_display_name(org.w3c.dom.Element element) { // <display-name>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("xml:lang")) { // <display-name
														// xml:lang="???">
			// attr.getValue();
			}
			if (attr.getName().equals("id")) { // <display-name id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named faces-context-factory.
	 */
	void visitElement_faces_context_factory(org.w3c.dom.Element element) { // <faces-context-factory>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <faces-context-factory
												// id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named from-action.
	 */
	void visitElement_from_action(org.w3c.dom.Element element) { // <from-action>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <from-action id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named from-outcome.
	 */
	void visitElement_from_outcome(org.w3c.dom.Element element) { // <from-outcome>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <from-outcome id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named from-view-id.
	 */
	void visitElement_from_view_id(org.w3c.dom.Element element) { // <from-view-id>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <from-view-id id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named key.
	 */
	void visitElement_key(org.w3c.dom.Element element) { // <key>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <key id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named key-class.
	 */
	void visitElement_key_class(org.w3c.dom.Element element) { // <key-class>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <key-class id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named large-icon.
	 */
	void visitElement_large_icon(org.w3c.dom.Element element) { // <large-icon>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <large-icon id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named lifecycle-factory.
	 */
	void visitElement_lifecycle_factory(org.w3c.dom.Element element) { // <lifecycle-factory>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <lifecycle-factory id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named managed-bean-class.
	 */
	void visitElement_managed_bean_class(org.w3c.dom.Element element) { // <managed-bean-class>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <managed-bean-class id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named managed-bean-name.
	 */
	void visitElement_managed_bean_name(org.w3c.dom.Element element) { // <managed-bean-name>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <managed-bean-name id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named managed-bean-scope.
	 */
	void visitElement_managed_bean_scope(org.w3c.dom.Element element) { // <managed-bean-scope>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <managed-bean-scope id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named navigation-handler.
	 */
	void visitElement_navigation_handler(org.w3c.dom.Element element) { // <navigation-handler>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <navigation-handler id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named phase-listener.
	 */
	void visitElement_phase_listener(org.w3c.dom.Element element) { // <phase-listener>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <phase-listener id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named redirect.
	 */
	void visitElement_redirect(org.w3c.dom.Element element) { // <redirect>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <redirect id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named suggested-value.
	 */
	void visitElement_suggested_value(org.w3c.dom.Element element) { // <suggested-value>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <suggested-value id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named view-handler.
	 */
	void visitElement_view_handler(org.w3c.dom.Element element) { // <view-handler>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <view-handler id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named state-manager.
	 */
	void visitElement_state_manager(org.w3c.dom.Element element) { // <state-manager>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <state-manager id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named null-value.
	 */
	void visitElement_null_value(org.w3c.dom.Element element) { // <null-value>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <null-value id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named property-class.
	 */
	void visitElement_property_class(org.w3c.dom.Element element) { // <property-class>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <property-class id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named property-name.
	 */
	void visitElement_property_name(org.w3c.dom.Element element) { // <property-name>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <property-name id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named property-resolver.
	 */
	void visitElement_property_resolver(org.w3c.dom.Element element) { // <property-resolver>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <property-resolver id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named referenced-bean-class.
	 */
	void visitElement_referenced_bean_class(org.w3c.dom.Element element) { // <referenced-bean-class>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <referenced-bean-class
												// id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named referenced-bean-name.
	 */
	void visitElement_referenced_bean_name(org.w3c.dom.Element element) { // <referenced-bean-name>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <referenced-bean-name
												// id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named render-kit-id.
	 */
	void visitElement_render_kit_id(org.w3c.dom.Element element) { // <render-kit-id>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <render-kit-id id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named render-kit-class.
	 */
	void visitElement_render_kit_class(org.w3c.dom.Element element) { // <render-kit-class>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <render-kit-class id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named renderer-class.
	 */
	void visitElement_renderer_class(org.w3c.dom.Element element) { // <renderer-class>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <renderer-class id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named render-kit-factory.
	 */
	void visitElement_render_kit_factory(org.w3c.dom.Element element) { // <render-kit-factory>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <render-kit-factory id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named renderer-type.
	 */
	void visitElement_renderer_type(org.w3c.dom.Element element) { // <renderer-type>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <renderer-type id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named small-icon.
	 */
	void visitElement_small_icon(org.w3c.dom.Element element) { // <small-icon>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <small-icon id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named supported-locale.
	 */
	void visitElement_supported_locale(org.w3c.dom.Element element) { // <supported-locale>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <supported-locale id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named to-view-id.
	 */
	void visitElement_to_view_id(org.w3c.dom.Element element) { // <to-view-id>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <to-view-id id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named validator-class.
	 */
	void visitElement_validator_class(org.w3c.dom.Element element) { // <validator-class>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <validator-class id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named validator-id.
	 */
	void visitElement_validator_id(org.w3c.dom.Element element) { // <validator-id>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <validator-id id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named value.
	 */
	void visitElement_value(org.w3c.dom.Element element) { // <value>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <value id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named value-class.
	 */
	void visitElement_value_class(org.w3c.dom.Element element) { // <value-class>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <value-class id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

	/**
	 * Scan through org.w3c.dom.Element named variable-resolver.
	 */
	void visitElement_variable_resolver(org.w3c.dom.Element element) { // <variable-resolver>
	// element.getValue();
		org.w3c.dom.NamedNodeMap attrs = element.getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			org.w3c.dom.Attr attr = (org.w3c.dom.Attr) attrs.item(i);
			if (attr.getName().equals("id")) { // <variable-resolver id="???">
			// attr.getValue();
			}
		}
		org.w3c.dom.NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			org.w3c.dom.Node node = nodes.item(i);
			switch (node.getNodeType()) {
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				// ((org.w3c.dom.CDATASection)node).getData();
				break;
			case org.w3c.dom.Node.ELEMENT_NODE:
				org.w3c.dom.Element nodeElement = (org.w3c.dom.Element) node;
				break;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				// ((org.w3c.dom.ProcessingInstruction)node).getTarget();
				// ((org.w3c.dom.ProcessingInstruction)node).getData();
				break;
			case org.w3c.dom.Node.TEXT_NODE:
				// ((org.w3c.dom.Text)node).getData();
				break;
			}
		}
	}

}
