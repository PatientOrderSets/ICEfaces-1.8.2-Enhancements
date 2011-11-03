package com.icesoft.faces.metadata;



import junit.framework.TestCase;


import com.sun.rave.jsfmeta.beans.ComponentBean;
import com.sun.rave.jsfmeta.beans.RendererBean;
import javax.faces.component.UIComponent;

public class ICECompsTestCase extends TestCase {

    private static ComponentBean[] componentBeans;
    private static UIComponent[] uiComponent;
    private static RendererBean[] rendererBeans;

    protected void setUp() {

        if (componentBeans == null || uiComponent == null) {
         
            componentBeans = ICECompsListHelper.getComponentBeans();
            rendererBeans = ICECompsListHelper.getRendererBean();
            uiComponent = ICECompsListHelper.getComponents();
        }
    }

    public UIComponent[] getComponents() {
        return uiComponent;
    }

    public ComponentBean[] getComponentBean() {
        return componentBeans;
    }

    public RendererBean[] getRendererBean() {
        return rendererBeans;
    }

    public ComponentBean getComponentBean(UIComponent uiComponent){        
        for (int i = 0; i < componentBeans.length; i++) {
            ComponentBean componentBean = componentBeans[i];
            if(uiComponent.getClass().getName().equals(componentBean.getComponentClass())){
                return componentBean;
            }
        }
        return null;
    }
}
