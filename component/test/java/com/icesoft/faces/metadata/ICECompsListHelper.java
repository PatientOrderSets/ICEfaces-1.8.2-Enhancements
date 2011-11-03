/*
 * Helper
 */
package com.icesoft.faces.metadata;

import com.icesoft.jsfmeta.MetadataXmlParser;
import com.icesoft.jsfmeta.util.GeneratorUtil;
import com.sun.rave.jsfmeta.beans.ComponentBean;
import com.sun.rave.jsfmeta.beans.FacesConfigBean;
import com.sun.rave.jsfmeta.beans.RendererBean;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.render.RenderKitFactory;
import org.xml.sax.SAXException;

/**
 *
 * @author fye
 */
public class ICECompsListHelper {

    private static ComponentBean[] componentBeans;
    private static UIComponentBase[] uiComponentBases;
    private static RendererBean[] rendererBeans;
    private static FacesConfigBean facesConfigBean;
    private static ArrayList<ComponentBean> testComponentBeanList = new ArrayList<ComponentBean>();

    static {
        if (componentBeans == null || uiComponentBases == null) {
            componentBeans = getFacesConfigBean().getComponents();
            for (int i = 0; i < componentBeans.length; i++) {
                ComponentBean componentBean = componentBeans[i];
                if(componentBean.getComponentClass().startsWith("com.icesoft")){
                    testComponentBeanList.add(componentBean);
                }
            }
            componentBeans = testComponentBeanList.toArray(new ComponentBean[testComponentBeanList.size()]);
            rendererBeans = getFacesConfigBean().getRenderKit(
                    RenderKitFactory.HTML_BASIC_RENDER_KIT).getRenderers();
            uiComponentBases = new UIComponentBase[componentBeans.length];

            for (int j = 0; j < componentBeans.length; j++) {
                Object newObject = null;

                try {
                    Class namedClass = Class.forName(componentBeans[j].getComponentClass());
                    newObject = namedClass.newInstance();

                    if (newObject instanceof UIComponentBase) {
                        uiComponentBases[j] = (UIComponentBase) newObject;
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    System.exit(1);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    System.exit(1);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                    System.exit(1);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }

    }

    //TODO: baseline Component com/sun/faces/standard-html-renderkit.xml
    private static FacesConfigBean getFacesConfigBean() {

        if (facesConfigBean == null) {
            facesConfigBean = new FacesConfigBean();
            MetadataXmlParser jsfMetaParser = new MetadataXmlParser();
            try {

                String standard_html_renderkit = "jar:" + GeneratorUtil.getBaseLineFolder("com/sun/faces/standard-html-renderkit.xml");
                String standard_html_renderkit_overlay = "jar:" + GeneratorUtil.getBaseLineFolder("com/sun/rave/jsfmeta/standard-html-renderkit-overlay.xml");
                String standard_html_renderkit_fixup = "jar:" + GeneratorUtil.getBaseLineFolder("com/sun/rave/jsfmeta/standard-html-renderkit-fixups.xml");

                String extRelativePath = GeneratorUtil.getWorkingFolder();
                String extended_faces_config = "file:" + extRelativePath + "../../../component-metadata/src/main/resources/conf/extended-faces-config.xml";
                String component_faces_config = "file:" + extRelativePath + "../../../component-metadata/src/main/resources/conf/faces-config-base.xml";

                String[] files = new String[]{standard_html_renderkit, standard_html_renderkit_overlay, standard_html_renderkit_fixup, extended_faces_config, component_faces_config};
                URL url = null;
                for (int i = 0; i < files.length; i++) {

                    url = new URL(files[i]);
                    jsfMetaParser.parse(url, facesConfigBean);

                }
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                System.exit(1);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            } catch (SAXException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        return facesConfigBean;
    }

    public static UIComponent[] getComponents() {
        return uiComponentBases;
    }

    public static ComponentBean[] getComponentBeans() {
        return componentBeans;
    }

    public static RendererBean[] getRendererBean() {
        return rendererBeans;
    }

    public static RendererBean getRenderer(String componentFamily, String rendererType) {
        return getFacesConfigBean().getRenderKit(RenderKitFactory.HTML_BASIC_RENDER_KIT).getRenderer(componentFamily, rendererType);
    }
}