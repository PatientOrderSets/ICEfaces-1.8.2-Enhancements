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

package com.icesoft.jsfmeta;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.digester.Digester;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.rave.jsfmeta.beans.FacesConfigBean;
import com.sun.rave.jsfmeta.rules.FacesConfigRuleSet;
import java.net.URLConnection;

/*
 * Parser for metadata.
 *
 * TODO: 
 *  separate dtd to a catalog class
 */


public class MetadataXmlParser {
    
    private static final String FACES_CONFIG_1_0_ID = "-//Sun Microsystems, Inc.//DTD JavaServer Faces Config 1.0//EN";
    
    private static final String FACES_CONFIG_1_0_DTD = "conf/dtd/web-facesconfig_1_0.dtd";
    
    private static final String FACES_CONFIG_1_1_ID = "-//Sun Microsystems, Inc.//DTD JavaServer Faces Config 1.1//EN";
    
    private static final String FACES_CONFIG_1_1_DTD = "conf/dtd/web-facesconfig_1_1.dtd";
    
    private static final String FACES_CONFIG_1_2_ID = "-//Sun Microsystems, Inc.//DTD JavaServer Faces Config 1.2//EN";
    
    private static final String FACES_CONFIG_1_2_DTD = "conf/dtd/web-facesconfig_1_2.dtd";
    
    private static final String FACES_OVERLAY_1_0_ID = "-//Sun Microsystems, Inc.//DTD JavaServer Faces Overlay 1.0//EN";
    
    private static final String FACES_OVERLAY_1_0_DTD = "conf/dtd/web-facesconfig-overlay_1_0.dtd";
    
    private static final String FACES_OVERLAY_1_1_ID = "-//Sun Microsystems, Inc.//DTD JavaServer Faces Overlay 1.1//EN";
    
    private static final String FACES_OVERLAY_1_1_DTD = "conf/dtd/web-facesconfig-overlay_1_1.dtd";
    
    private static final String FACES_OVERLAY_1_2_ID = "-//Sun Microsystems, Inc.//DTD JavaServer Faces Overlay 1.2//EN";
    
    private static final String FACES_OVERLAY_1_2_DTD = "conf/dtd/web-facesconfig-overlay_1_2.dtd";
    
    private boolean configured;
    
    private Digester digester;
    
    private boolean design;
    
    private boolean generate;
    
    private boolean runtime;
    
    public MetadataXmlParser() {
        
        configured = false;
        digester = new Digester();
        design = true;
        generate = true;
        runtime = false;
        digester.setNamespaceAware(false);
        digester.setUseContextClassLoader(true);
        
        //TODO: need to validating
        digester.setValidating(false);
        URL url = null;
        url = getClass().getClassLoader().getResource(FACES_CONFIG_1_0_DTD);
        if (url != null){
            register(FACES_CONFIG_1_0_ID, url);
        }
        
        url = getClass().getClassLoader().getResource(FACES_CONFIG_1_1_DTD);
        if (url != null){
            register(FACES_CONFIG_1_1_ID, url);
        }
                
        url = getClass().getClassLoader().getResource(FACES_CONFIG_1_2_DTD);
        if (url != null){
            register(FACES_CONFIG_1_2_ID, url);
        }        
        
        url = getClass().getClassLoader().getResource(FACES_OVERLAY_1_0_DTD);
        if (url != null){
            register( FACES_OVERLAY_1_0_ID, url);
        }
        
        url = getClass().getClassLoader().getResource(FACES_OVERLAY_1_1_DTD);
        if (url != null){
            register( FACES_OVERLAY_1_1_ID, url);
        }
        
        url = getClass().getClassLoader().getResource(FACES_OVERLAY_1_2_DTD);
        if (url != null){
            register(FACES_OVERLAY_1_2_ID, url);
        }
    }
    
    public boolean isDesign() {
        return design;
    }
    
    public void setDesign(boolean design) {
        this.design = design;
    }
    
    public boolean isGenerate() {
        return generate;
    }
    
    public void setGenerate(boolean generate) {
        this.generate = generate;
    }
    
    public boolean isRuntime() {
        return runtime;
    }
    
    public void setRuntime(boolean runtime) {
        this.runtime = runtime;
    }
    
    public FacesConfigBean parse(URL url) throws IOException, SAXException {
        return parse(url, new FacesConfigBean());
    }
    
    public FacesConfigBean parse(File file) throws IOException, SAXException {
        return parse(file.toURI().toURL(), new FacesConfigBean());
    }
    
    public FacesConfigBean parse(URL url, FacesConfigBean metadata)throws IOException, SAXException{
        
        configure();
        digester.clear();
        digester.push(metadata);
        InputSource source = null;
        InputStream stream = null;
        FacesConfigBean fcb = null;
        try {
            URLConnection urlConnection = url.openConnection();                                    
            stream = new BufferedInputStream(urlConnection.getInputStream());
            source = new InputSource(url.toString());
            source.setByteStream(stream);
            fcb = (FacesConfigBean) digester.parse(source);
        } catch(SAXException e){
            System.err.println("Please check the syntax for the following file: "+ url.getFile());
            e.printStackTrace();
            System.exit(1);
            
        } catch(IOException e){
            System.err.println("Please check the syntax for the following file: "+ url.getFile());
            e.printStackTrace();
            System.exit(1);
            
        } finally {
        
            if (stream != null)
                try {
                    stream.close();
                } catch (IOException e) {
                    System.err.println("Please check the following file:"+url.getFile());
                    e.printStackTrace();
                    System.exit(1);
                }
            stream = null;
        }
        return fcb;
    }
    
    public void register(String publicId, URL entityURL) {
        digester.register(publicId, entityURL.toString());
    }
    
    private void configure() {
        if (!configured) {
            digester.addRuleSet(new FacesConfigRuleSet(design, generate,
                    runtime));
            configured = true;
        }
    }
    
}
