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
 * faces-Config.xml Parser Helper
 *
 */

package com.icesoft.jsfmeta.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.faces.render.RenderKitFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.icesoft.jsfmeta.MetadataXmlParser;
import com.sun.rave.jsfmeta.beans.FacesConfigBean;
import com.sun.rave.jsfmeta.beans.RenderKitBean;
import com.sun.rave.jsfmeta.beans.RendererBean;

public class FacesConfigParserHelper {
    
    private String fileName;
    
    public FacesConfigParserHelper(String file){
        fileName = file;
    }
    
    public static void main(String[] args){
        String tmp = "./src/main/resources/conf/webui-faces-config.xml";
        FacesConfigParserHelper helper = new FacesConfigParserHelper(tmp);
        helper.getRendererBeans();
        
    }
    
    public static void validate(String filePath) {
        
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                .newInstance();
        documentBuilderFactory.setValidating(true);
        
        DocumentBuilder documentBuilder = null;
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
            documentBuilder.parse(new File(filePath));
        } catch (IOException e) {
            
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
            
        }
    }
    
    

    public RendererBean[] getRendererBeans() {
        
        RendererBean[] rd = null;
        MetadataXmlParser metadataParser = new MetadataXmlParser();
        metadataParser.setDesign(false);
        
        try {
            
            File file = new File(fileName);
            FacesConfigBean facesConfigBean = metadataParser.parse(file);
            RenderKitBean renderKitBean = facesConfigBean.getRenderKit(RenderKitFactory.HTML_BASIC_RENDER_KIT);
            rd = renderKitBean.getRenderers();
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        
        return rd;
    }
    
}
