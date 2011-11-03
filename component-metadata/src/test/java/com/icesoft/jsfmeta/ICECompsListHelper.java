/*
 * Helper
 */
package com.icesoft.jsfmeta;

import com.icesoft.jsfmeta.MetadataXmlParser;
import com.icesoft.jsfmeta.util.GeneratorUtil;
import com.sun.rave.jsfmeta.beans.ComponentBean;
import com.sun.rave.jsfmeta.beans.FacesConfigBean;
import com.sun.rave.jsfmeta.beans.RendererBean;
import java.io.IOException;
import java.net.URL;
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


    static {
        if (componentBeans == null || uiComponentBases == null) {
            componentBeans = getFacesConfigBean().getComponents();
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
            MetadataXmlParser jsfMetaParser = new MetadataXmlParser();
            try {
                String extRelativePath = GeneratorUtil.getWorkingFolder();
                String component_faces_config = "file:" + extRelativePath + "conf/faces-config-base.xml";
                URL url = new URL(component_faces_config);
                facesConfigBean = jsfMetaParser.parse(url);
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