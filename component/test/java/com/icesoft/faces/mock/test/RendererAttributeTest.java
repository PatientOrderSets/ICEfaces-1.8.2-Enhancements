/*
 * 
 */
package com.icesoft.faces.mock.test;

import com.icesoft.faces.mock.test.container.MockTestCase;
import com.sun.rave.jsfmeta.beans.RendererBean;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.render.Renderer;
import org.apache.commons.beanutils.PropertyUtils;

/**
 *
 * @author fye
 */
public class RendererAttributeTest extends MockTestCase {

    public void testRenderer() {

//        UIViewRoot uiViewRoot = getViewHandler().createView(getFacesContext(), this.getClass().getName() + "_view_id");
//        getFacesContext().setViewRoot(uiViewRoot);

        //limited scope
       // myRenderer();
    }

    public void myRenderer() {
        try {
            UIComponent[] uiComponent = getUIComponents();
            List<UIComponent> myComps = new ArrayList(uiComponent.length);
            for (int i = 0; i < uiComponent.length; i++) {
                Map propsMap = new HashMap();
                CompPropsUtils.describe_useMetaBeanInfo(uiComponent[i], propsMap);
                if (propsMap.get("readonly") != null) {
                    myComps.add(uiComponent[i]);
                    uiComponent[i].getRendererType();
                }
            }

            String componentFamily = null;
            String rendererType = null;
            String rendererClassName = null;

            UIComponent[] hasDisabledComponent = myComps.toArray(new UIComponent[myComps.size()]);

            for (int i = 0; i < hasDisabledComponent.length; i++) {
                UIComponent tmpComponent = hasDisabledComponent[i];

                try {
                    PropertyUtils.setSimpleProperty(tmpComponent, "disabled", true);
                } catch (IllegalAccessException illegalAccessException) {
                    print(illegalAccessException.getMessage());
                } catch (InvocationTargetException invocationTargetException) {
                    print(invocationTargetException.getMessage());
                } catch (NoSuchMethodException noSuchMethodException) {
                    print(noSuchMethodException.getMessage());
                }

                componentFamily = tmpComponent.getFamily();
                rendererType = tmpComponent.getRendererType();
                RendererBean rendererBean = getRendererBean(componentFamily, rendererType);
                if (rendererBean == null) {
                    continue;
                }
                rendererClassName = rendererBean.getRendererClass();
                Renderer renderer = (Renderer) Class.forName(rendererClassName).newInstance();
                String message = "\n\tRenderer="+rendererClassName+" Component="+ tmpComponent.getClass().getName();
                print(message);

            }



        } catch (ClassNotFoundException ex) {
            Logger.getLogger(RendererAttributeTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(RendererAttributeTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(RendererAttributeTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void print(String message) {
        Logger.getLogger(RendererAttributeTest.class.getName()).log(Level.INFO, message);
    }
}
