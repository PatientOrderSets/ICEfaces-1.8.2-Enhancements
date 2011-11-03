package com.icesoft.faces.metadata;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.commons.beanutils.PropertyUtils;

public class BeanPropertiesTest extends ICECompsTestCase {

    public static Test suite() {
        return new TestSuite(BeanPropertiesTest.class);
    }

    public static void main() {
        junit.textui.TestRunner.run(BeanPropertiesTest.suite());
    }

    /*TODO: test argument signature */
    public void testComponentProperties() {

        UIComponent[] oldAllComps = getComponents();
        UIComponent[] components = new UIComponent[oldAllComps.length];
        System.arraycopy(oldAllComps, 0, components, 0, oldAllComps.length);

        for (int j = 0; j < components.length; j++) {
            try {
                if(!components[j].getClass().getName().startsWith("com.icesoft")){
                    continue;
                }
                Class beanInfoClass = Class.forName(components[j].getClass().getName() + "BeanInfo");
                Object object = beanInfoClass.newInstance();
                if (object instanceof SimpleBeanInfo) {
                    SimpleBeanInfo simpleBeanInfo = (SimpleBeanInfo) object;
                    validateSimpleBeanInfo(simpleBeanInfo, components[j]);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
                fail(e.getMessage());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                fail(e.getMessage());
            } catch (InstantiationException e) {
                e.printStackTrace();
                fail(e.getMessage());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                fail(e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                fail(e.getMessage());
            }
        }
    }

    public void validateSimpleBeanInfo(SimpleBeanInfo simpleBeanInfo, UIComponent uiComponent) {

        PropertyDescriptor[] pds = null;
        try {
            pds = simpleBeanInfo.getPropertyDescriptors();
        } catch (NullPointerException e) {
            e.printStackTrace();
            fail(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        //System.out.println("Component under test="+uiComponent.getClass().getName());
        String[] names = getMethodArray(uiComponent);
        for (int i = 0; i < pds.length; i++) {
            try {
                String propertyName = pds[i].getName();
                String tmp = "\tmethod name=" + pds[i].getReadMethod().getName() + " type" + pds[i].getPropertyType();
                boolean methodIndexBoolean = getMethodIndex(pds[i].getReadMethod().getName(), names) == -1;
                String message = " failed class name= " + uiComponent.getClass().getName() + " method name= " + propertyName + "\n" + tmp;
                assertEquals(" " + message + "", false, methodIndexBoolean);
                String expectedPropertyType = pds[i].getPropertyType().getName();
                String actualType = PropertyUtils.getPropertyType(uiComponent, propertyName).getName();
                assertEquals(message, expectedPropertyType, actualType);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(BeanPropertiesTest.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(BeanPropertiesTest.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchMethodException ex) {
                Logger.getLogger(BeanPropertiesTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private int getMethodIndex(String methodName, String[] names) {

        int index = -1;
        for (int j = 0; j < names.length; j++) {
            if (methodName.equalsIgnoreCase(names[j])) {
                index = j;
                return index;
            }
        }
        return index;
    }

    private String[] getMethodArray(Object object) {

        String[] methodArray = null;
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(object.getClass(), Introspector.IGNORE_ALL_BEANINFO);
            MethodDescriptor[] mds = beanInfo.getMethodDescriptors();
            methodArray = new String[mds.length];
            for (int j = 0; j < mds.length; j++) {
                methodArray[j] = mds[j].getMethod().getName();
            }
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
        return methodArray;
    }
}
