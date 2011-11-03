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
package com.icesoft.jsfmeta.templates;

import com.icesoft.jsfmeta.util.GeneratorUtil;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import com.icesoft.jsfmeta.MetadataXmlParser;

import com.icesoft.jsfmeta.templates.jsf11.TempGenRTFacesConfig11;
import com.icesoft.jsfmeta.util.ConfigStorage;
import com.icesoft.jsfmeta.util.InternalConfig;
import com.sun.rave.jsfmeta.beans.ComponentBean;
import com.sun.rave.jsfmeta.beans.ConverterBean;
import com.sun.rave.jsfmeta.beans.FacesConfigBean;
import com.sun.rave.jsfmeta.beans.RendererBean;
import com.sun.rave.jsfmeta.beans.ValidatorBean;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.SAXException;

public final class TemplateMetadataGenerator {

    private static Logger logger = Logger.getLogger("com.icesoft.metadata.generators.MetadataGenerator");
    private FacesConfigBean config;
    private List excludes;

    private MetadataXmlParser parser;
    private InternalConfig internalConfig;

    public TemplateMetadataGenerator() {

        parser = new MetadataXmlParser();
        config = new FacesConfigBean();
        excludes = new ArrayList();
    }

    public static void main(String args[]) throws Exception {

        TemplateMetadataGenerator main = new TemplateMetadataGenerator();
        main.loadProps();
        main.execute(args);
    }

    private void parseXML(String[] urlList) {

        for (int i = 0; i < urlList.length; i++) {
            String url = urlList[i];
            try {
                parser.parse(new URL(url), config);
            } catch (MalformedURLException ex) {
                System.out.println("Please check following: url=" + url);
                ex.printStackTrace();
                System.exit(1);
            } catch (IOException ex) {
                System.out.println("Please check following: url=" + url);
                ex.printStackTrace();
                System.exit(1);
            } catch (SAXException ex) {
                System.out.println("Please check following: url=" + url);
                ex.printStackTrace();
                System.exit(1);
            }
        }
    }

    //TODO: filter version from ICEfaces core
    private void loadProps() {

        init();
        String fileName = GeneratorUtil.getWorkingFolder() + "conf/config.properties";
        Properties props = ConfigStorage.getInstance(fileName).loadProperties();
        internalConfig = new InternalConfig(props);
    }

    //TODO: move to catalog
    private void init() {
        try {

            String standard_html_renderkit = "jar:" + GeneratorUtil.getBaseLineFolder("com/sun/faces/standard-html-renderkit.xml");
            String standard_html_renderkit_overlay = "jar:" + GeneratorUtil.getBaseLineFolder("com/sun/rave/jsfmeta/standard-html-renderkit-overlay.xml");
            String standard_html_renderkit_fixup = "jar:" + GeneratorUtil.getBaseLineFolder("com/sun/rave/jsfmeta/standard-html-renderkit-fixups.xml");

            String[] baseUrlList = new String[]{standard_html_renderkit, standard_html_renderkit_overlay, standard_html_renderkit_fixup};
            parseXML(baseUrlList);

            exclude();

            String extRelativePath = GeneratorUtil.getWorkingFolder();
            String component_faces_config = "file:" + extRelativePath + "conf/faces-config-base.xml";
            String extended_faces_config = "file:" + extRelativePath + "conf/extended-faces-config.xml";
            String[] urlList = new String[]{component_faces_config, extended_faces_config};
            parseXML(urlList);
        } catch (MalformedURLException ex) {
            Logger.getLogger(TemplateMetadataGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void execute(String args[]) throws Exception {

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (arg.equals("--templateGenJsf11")) {
                templateGenJsf11();
            } else if (arg.equals("--templateGenJsf12")) {
                //templateGenJsf12();
            } else {
                usage();
                throw new IllegalArgumentException(arg);
            }
        }
    }

    private void templateGenJsf11() throws Exception {
        try {

            TempGenRTFacesConfig11 genFacesConfig = new TempGenRTFacesConfig11();
            genFacesConfig.setInternalConfig(internalConfig);
            genFacesConfig.setFacesConfigBean(config);
            genFacesConfig.genConf();;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    private void exclude() {

        ComponentBean cpb[] = config.getComponents();
        for (int i = 0; i < cpb.length; i++) {
            excludes.add(cpb[i].getComponentClass());
        }

        ConverterBean cvb1[] = config.getConvertersByClass();
        for (int i = 0; i < cvb1.length; i++) {
            excludes.add(cvb1[i].getConverterClass());
        }

        ConverterBean cvb2[] = config.getConvertersById();
        for (int i = 0; i < cvb2.length; i++) {
            excludes.add(cvb2[i].getConverterClass());
        }

        RendererBean rb[] = config.getRenderKit("HTML_BASIC").getRenderers();
        for (int i = 0; i < rb.length; i++) {
            excludes.add(rb[i].getRendererClass());
        }

        ValidatorBean vb[] = config.getValidators();
        for (int i = 0; i < vb.length; i++) {
            excludes.add(vb[i].getValidatorClass());
        }
    }

    //TODO:
    private void usage() {
        String info = "TODO";
        logger.info(info);
    }
}
