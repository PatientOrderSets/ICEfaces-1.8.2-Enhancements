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

package com.icesoft.metadata.generators;

import com.icesoft.jsfmeta.util.GeneratorUtil;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import com.icesoft.jsfmeta.MetadataXmlParser;
import com.icesoft.jsfmeta.util.ConfigStorage;
import com.icesoft.jsfmeta.util.InternalConfig;
import com.sun.rave.jsfmeta.beans.ComponentBean;
import com.sun.rave.jsfmeta.beans.ConverterBean;
import com.sun.rave.jsfmeta.beans.FacesConfigBean;
import com.sun.rave.jsfmeta.beans.RendererBean;
import com.sun.rave.jsfmeta.beans.ValidatorBean;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.SAXException;

public final class MetadataGenerator {
    
    private static Logger logger = Logger.getLogger("com.icesoft.metadata.generators.MetadataGenerator");
    
    private FacesConfigBean config;
    
    private List excludes;
    
    private List includes;
    
    private List listeners;
    
    private MetadataXmlParser parser;
    
    private List validators;
    
    private InternalConfig internalConfig;
    
    public MetadataGenerator() {
        
        parser = new MetadataXmlParser();
        config = new FacesConfigBean();
        excludes = new ArrayList();
        includes = new ArrayList();
        listeners = new ArrayList();
        validators = new ArrayList();
    }
    
    
    public static void main(String args[]) throws Exception {
        
        MetadataGenerator main = new MetadataGenerator();
        main.loadProps();
        main.execute(args);
    }
    
    private void parseXML(String[] urlList){
        
        for(int i=0; i< urlList.length; i++){
            String url = urlList[i];
            try {
                parser.parse(new URL(url), config);
            } catch (MalformedURLException ex) {
                System.out.println("Please check following: url="+url);
                ex.printStackTrace();
                System.exit(1);
            } catch (IOException ex) {
                System.out.println("Please check following: url="+url);
                ex.printStackTrace();
                System.exit(1);
            } catch (SAXException ex) {
                System.out.println("Please check following: url="+url);
                ex.printStackTrace();
                System.exit(1);
            }
        }
    }
    
    //TODO: filter version from ICEfaces core
    private void loadProps(){
        
        init();
        String fileName = GeneratorUtil.getWorkingFolder()+"conf/config.properties";
        Properties props = ConfigStorage.getInstance(fileName).loadProperties();
        internalConfig = new InternalConfig(props);
    }
    
    //TODO: move to catalog
    private void init(){
        try {

            String standard_html_renderkit = "jar:" + GeneratorUtil.getBaseLineFolder("com/sun/faces/standard-html-renderkit.xml");
            String standard_html_renderkit_overlay = "jar:" + GeneratorUtil.getBaseLineFolder("com/sun/rave/jsfmeta/standard-html-renderkit-overlay.xml");
            String standard_html_renderkit_fixup = "jar:" + GeneratorUtil.getBaseLineFolder("com/sun/rave/jsfmeta/standard-html-renderkit-fixups.xml");

            String[] baseUrlList = new String[]{standard_html_renderkit, standard_html_renderkit_overlay, standard_html_renderkit_fixup};
            parseXML(baseUrlList);

            exclude();

            String component_faces_config = "file:" + GeneratorUtil.getWorkingFolder() + "conf/faces-config-base.xml";
            String extended_faces_config = "file:" + GeneratorUtil.getWorkingFolder() + "conf/extended-faces-config.xml";
            String[] urlList = new String[]{component_faces_config, extended_faces_config};
            parseXML(urlList);
        } catch (MalformedURLException ex) {
            Logger.getLogger(MetadataGenerator.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
        
    }
    
    
    private void execute(String args[]) throws Exception {
        
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            
            if (arg.equals("--cpBeanInfoBase")) {
                componentBeanInfo();
                continue;
            }
            if (arg.equals("--cpTestBeanInfo")) {
                componentTestBeanInfo();
                continue;
            }
            if (arg.equals("--cpClassBase")) {
                component();
                continue;
            }
            if (arg.equals("--tlClass")) {
                tagLibrary();
                continue;
            }
            if (arg.equals("--cpCreatorBeanInfoBase")) {
                componentBeanInfo();
                continue;
            }
            if (arg.equals("--tlDescriptor")) {
                descriptor();
            } else {
                usage();
                throw new IllegalArgumentException(arg);
            }
        }
    }
    
    private void tagLibrary() throws Exception {
        try {
            TagLibraryGenerator generator = new TagLibraryGenerator(internalConfig);
            generator.setDest(GeneratorUtil.getDestFolder(GeneratorUtil.getWorkingFolder()+"../generated-sources/taglib/main/java"));
            generator.setConfig(config);
            generator.generate();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
    
    private void component() throws Exception {
        
        try {
            BaseComponentGenerator generator = new BaseComponentGenerator(internalConfig);
            generator.setDest(GeneratorUtil.getDestFolder(GeneratorUtil.getWorkingFolder()+"../generated-sources/component/main/java"));
            generator.setConfig(config);
            generator.generate();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
    
        
    private void componentCreatorBeanInfo() throws Exception {
                       
        try {
            IDEComponentBeanInfoGenerator generator = new IDEComponentBeanInfoGenerator(internalConfig);
            generator.setDest(GeneratorUtil.getDestFolder(GeneratorUtil.getWorkingFolder()+"../generated-sources/beaninfo/main/java"));
            generator.setConfig(config);
            generator.generate();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
    
    private void componentBeanInfo() throws Exception {
        
        try {
            IDEComponentBeanInfoGenerator generator = new IDEComponentBeanInfoGenerator(internalConfig);
            generator.setDest(GeneratorUtil.getDestFolder(GeneratorUtil.getWorkingFolder()+"../generated-sources/beaninfo/main/java"));
            generator.setConfig(config);
            generator.generate();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
    
    private void componentTestBeanInfo() throws Exception {
        try {
            ComponentTestBeanInfoGenerator generator = new ComponentTestBeanInfoGenerator(internalConfig);
            generator.setDest(GeneratorUtil.getDestFolder(GeneratorUtil.getWorkingFolder()+"../generated-sources/testbeaninfo/main/java"));
            generator.setConfig(config);
            generator.generate();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
    
    private void descriptor() throws Exception {
        try {
            TLDGenerator generator = new TLDGenerator(internalConfig);
            generator.setDest(GeneratorUtil.getDestFolder(GeneratorUtil.getWorkingFolder()+"../generated-sources/tld"));
            generator.setConfig(config);
            generator.setVerbose(true);
            generator.generate();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
    
    
    private void exclude() {
        
        ComponentBean cpb[] = config.getComponents();
        for (int i = 0; i < cpb.length; i++)
            excludes.add(cpb[i].getComponentClass());
        
        ConverterBean cvb1[] = config.getConvertersByClass();
        for (int i = 0; i < cvb1.length; i++)
            excludes.add(cvb1[i].getConverterClass());
        
        ConverterBean cvb2[] = config.getConvertersById();
        for (int i = 0; i < cvb2.length; i++)
            excludes.add(cvb2[i].getConverterClass());
        
        RendererBean rb[] = config.getRenderKit("HTML_BASIC").getRenderers();
        for (int i = 0; i < rb.length; i++){
            excludes.add(rb[i].getRendererClass());
        }
        
        ValidatorBean vb[] = config.getValidators();
        for (int i = 0; i < vb.length; i++){
            excludes.add(vb[i].getValidatorClass());
        }
    }
    
    //TODO:
    private void usage() {
        String info = "TODO";
        logger.info(info);
    }
    
}
