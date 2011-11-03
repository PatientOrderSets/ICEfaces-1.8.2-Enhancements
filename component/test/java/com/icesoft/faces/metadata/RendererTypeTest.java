package com.icesoft.faces.metadata;

import junit.framework.Test;
import junit.framework.TestSuite;


import com.sun.rave.jsfmeta.beans.ComponentBean;
import com.sun.rave.jsfmeta.beans.RendererBean;
import java.util.HashMap;
import javax.faces.component.UIComponent;

public class RendererTypeTest extends ICECompsTestCase {

    public static Test suite() {
        return new TestSuite(RendererTypeTest.class);
    }

    public static void main() {
        junit.textui.TestRunner.run(RendererTypeTest.suite());
    }

    public void testNamingType() {

        ComponentBean[] oldComponentBeans = getComponentBean();
        ComponentBean[] componentBeans = new ComponentBean[oldComponentBeans.length];
        System.arraycopy(oldComponentBeans, 0, componentBeans, 0, oldComponentBeans.length);

        UIComponent[] oldUIComponentBases = getComponents();
        UIComponent[] uiComponentBases = new UIComponent[oldUIComponentBases.length];
        System.arraycopy(oldUIComponentBases, 0, uiComponentBases, 0, oldUIComponentBases.length);

        HashMap tagNameMap = new HashMap();
        for (int i = 0; i < componentBeans.length; i++) {
            String renderTypeUIComponent = uiComponentBases[i].getRendererType();
            String renderTypeComponentBean = componentBeans[i].getRendererType();

            boolean notSameRenderType = renderTypeUIComponent != null && renderTypeComponentBean != null && !renderTypeUIComponent.trim().equalsIgnoreCase(
                    renderTypeComponentBean.trim());

            String message = "RenderType not the same for Component Class=" + componentBeans[i].getComponentClass() + "\n component renderType=" + renderTypeUIComponent + "\n faces-config declared renderType=" + renderTypeComponentBean + "\n\n";
            assertFalse(message, notSameRenderType);

            RendererBean myRendererBean = ICECompsListHelper.getRenderer(uiComponentBases[i].getFamily(), uiComponentBases[i].getRendererType());

            if (myRendererBean == null || myRendererBean.getRendererType() == null) {
                message = "No Renderer defined for " + uiComponentBases[i].getClass().getName();
                fail(message);
            } 
            if (myRendererBean != null && myRendererBean.getRendererType() != null) {
                boolean notSameType = renderTypeUIComponent != null && !(renderTypeUIComponent.trim().equalsIgnoreCase(myRendererBean.getRendererType()));
                message = "RenderType not the same for Component Class=" + componentBeans[i].getComponentClass() + "\n component renderType=" + renderTypeUIComponent + "\n faces-config declared renderType=" + renderTypeComponentBean +
                        "\n Renderer Bean type=" + myRendererBean.getRendererType() + "\n\n";
                assertFalse(message, notSameType);

                if (!tagNameMap.containsKey(myRendererBean.getTagName())) {
                    tagNameMap.put(myRendererBean.getTagName(), myRendererBean.getTagName());
                } else {
                    message = "Tag name=" + myRendererBean.getTagName() + " already defined, please check component =" + uiComponentBases[i].getClass().getName();
                    fail(message);
                }
            }

            String familyTypeUIComponent = uiComponentBases[i].getFamily();
            String familyTypeComponentBean = componentBeans[i].getComponentFamily();

            boolean notSameRenderTypeTwo = familyTypeUIComponent != null && familyTypeComponentBean != null && !familyTypeUIComponent.trim().equalsIgnoreCase(
                    familyTypeComponentBean.trim());

            String messageTwo = "FamilyType not the same for Component Class=" + componentBeans[i].getComponentClass() + "\n component familyType=" + familyTypeUIComponent + "\n faces-config declared familyType=" + familyTypeComponentBean + "\n\n";
            assertFalse(messageTwo, notSameRenderTypeTwo);
        }

    }
}
