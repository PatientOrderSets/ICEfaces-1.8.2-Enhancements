package com.icesoft.faces.metadata;

import com.sun.faces.util.ReflectionUtils;
import com.sun.rave.jsfmeta.beans.ComponentBean;
import com.sun.rave.jsfmeta.beans.PropertyBean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.faces.component.UIComponent;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.commons.beanutils.BeanUtils;

import sun.reflect.misc.MethodUtil;

public class BeanImpPropertiesTest extends ICECompsTestCase {

    public static Test suite() {
        return new TestSuite(BeanImpPropertiesTest.class);
    }

    public static void main() {
        junit.textui.TestRunner.run(BeanImpPropertiesTest.suite());
    }

    public void testComponentProperties() {

        UIComponent[] oldAllComps = getComponents();
        UIComponent[] components = new UIComponent[oldAllComps.length];
        System.arraycopy(oldAllComps, 0, components, 0, oldAllComps.length);
        TestPropDataProvider testDataProvider = new TestPropDataProvider();
        for (int j = 0; j < components.length; j++) {
            if (!components[j].getClass().getName().startsWith("com.icesoft")) {
                continue;
            }
            ComponentBean componentBean = getComponentBean(components[j]);

            PropertyBean[] pds = componentBean.getProperties();
            String message = null;
            for (int i = 0; i < pds.length; i++) {
                boolean newLine = false;
                String propertyName = null; 
                String methodName = null;               
                try {
                    propertyName = pds[i].getPropertyName();                    

                    if (pds[i].getPropertyClass() == null || pds[i].getPropertyClass().equalsIgnoreCase("String")) {
                        pds[i].setPropertyClass("java.lang.String");
                    }

                    if(propertyName.equalsIgnoreCase("expanded")){
                        continue;
                    }
                    methodName = propertyName.substring(0, 1).toUpperCase()+propertyName.substring(1);
                    message = "Failed under test Component= " + components[j].getClass().getName() + " property name= " + propertyName +", type boolean" + "\n" + "\tset"+methodName + "(..) doesn't take boolean";
                    Object propValue = testDataProvider.getSimpleTestObject(pds[i].getPropertyClass());
                    if(pds[i].getPropertyClass().equals("boolean")){
                        //check if setMethod found
                        Method setMethod = ReflectionUtils.lookupMethod(components[j].getClass(), "set"+methodName, boolean.class);
                        if(setMethod == null){
                            System.out.println(" "+message);
                        }
                        
                    }
                  //set the value
                    BeanUtils.setProperty(components[j], propertyName, propValue);
                } catch (java.lang.IllegalArgumentException ex) {
                    ex.printStackTrace();
                    fail(message);
                    newLine = true;
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                    fail(message);
                    newLine = true;                    
                } catch (InvocationTargetException ex) {
                    ex.printStackTrace();
                    fail(message);
                    newLine = true;                    
                } catch (Exception ex) {
                    ex.printStackTrace();
                    fail(message);
                    newLine = true;                    
                }
                
                
                if(pds[i].getPropertyClass().equals("boolean")){
                    try {
                        //check if isMethod exist
                        Method m = MethodUtil.getMethod(components[j].getClass(), "is"+methodName, new Class[0]);
                        //when found, check the return type shouldn't be Boolean instead boolean. Log the message
                        if (m.getReturnType()== Boolean.class) {
                            message = " Failed under test Component= " + components[j].getClass().getName() + " property name= " + propertyName +  ", type boolean \tis"+methodName + "() found return type "+ m.getReturnType();
                            System.out.println(message);
                            newLine = true;
                        }
                    } catch (NoSuchMethodException nsme) {
                        //log if isMethod was not exist
                        message = " Failed under test Component= " + components[j].getClass().getName() + " property name= " + propertyName +  ", type boolean \tis"+methodName + "() not found";
                        System.out.println(message);
                        newLine = true;
                    }
                }
                
                //for output format and readability
                if (newLine) {
                    System.out.println("\n");
                }
            }
        }
    }
}
