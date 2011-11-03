/*
 * attribute constants and extended constants test
 */
package com.icesoft.faces.metadata;

import com.icesoft.faces.component.AttributeConstants;
import com.icesoft.faces.component.ExtendedAttributeConstants;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author fye
 */
public class AttributeConstantsTest extends TestCase {

    private static final String[] baseHtmlComps = new String[]{
        "H_COMMANDBUTTON",
        "H_COMMANDLINK",
        "H_DATATABLE",
        "H_FORMFORM",
        "H_GRAPHICIMAGE",
        "H_INPUTHIDDEN",
        "H_INPUTSECRET",
        "H_INPUTTEXT",
        "H_INPUTTEXTAREA",
        "H_MESSAGEMESSAGE",
        "H_MESSAGESMESSAGES",
        "H_OUTPUTFORMAT",
        "H_OUTPUTLABEL",
        "H_OUTPUTLINK",
        "H_OUTPUTTEXT",
        "H_PANELGRID",
        "H_PANELGROUP",
        "H_SELECTBOOLEANCHECKBOX",
        "H_SELECTMANYCHECKBOX",
        "H_SELECTMANYLISTBOX",
        "H_SELECTMANYMENU",
        "H_SELECTONELISTBOX",
        "H_SELECTONEMENU",
        "H_SELECTONERADIO"
    };
    private static final String RI_ATTRIBUTES_HELPER = "com.icesoft.faces.component.AttributeConstants";
    private static final String ICE_ATTRIBUTES_HELPER = "com.icesoft.faces.component.ExtendedAttributeConstants";

    public static Test suite() {
        return new TestSuite(AttributeConstantsTest.class);
    }

    public static void main() {
        junit.textui.TestRunner.run(BeanPropertiesTest.suite());
    }

    public void testAttributes() {

        String compName = null;
        String iceCompName = null;
        String h_attributes[] = null;
        String ice_attributes[] = null;
        for (int i = 0; i < baseHtmlComps.length; i++) {
            compName = baseHtmlComps[i];
            h_attributes = AttributeConstants.getAttributes(getCompIndex(RI_ATTRIBUTES_HELPER, compName));
            iceCompName = baseHtmlComps[i].replaceFirst("H_", "ICE_");
            if (iceCompName.equalsIgnoreCase("ICE_FORMFORM")) {
                iceCompName = "ICE_FORM";
            } else if (iceCompName.equalsIgnoreCase("ICE_MESSAGEMESSAGE")) {
                iceCompName = "ICE_MESSAGE";
            } else if (iceCompName.equalsIgnoreCase("ICE_MESSAGESMESSAGES")) {
                iceCompName = "ICE_MESSAGES";
            }
            ice_attributes = ExtendedAttributeConstants.getAttributes(getCompIndex(ICE_ATTRIBUTES_HELPER, iceCompName));
            
            //TODO enable the following case
            String message = "ICE Component=" + iceCompName + " defined more html attribute=";
            compareHtmlAttributesWithRI(h_attributes, ice_attributes, message, Boolean.FALSE);
            //the following case should pass
            message = "ICE Component=" + iceCompName + " does not have html attribute=";
            compareHtmlAttributesWithRI(ice_attributes, h_attributes, message, Boolean.TRUE);
        }
    }

    private int getCompIndex(String className, String fieldName) {

        int index = -1;
        try {
            Class testClass = Class.forName(className);
            Object object = testClass.newInstance();
            index = testClass.getDeclaredField(fieldName).getInt(object);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AttributeConstantsTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(AttributeConstantsTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(AttributeConstantsTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(AttributeConstantsTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return index;
    }

    private void compareHtmlAttributesWithRI(String[] first_attributes, String[] second_attributes, String message, boolean failed) {

        HashMap hashMap = new HashMap();
        for (int i = 0; i < first_attributes.length; i++) {
            hashMap.put(first_attributes[i], first_attributes[i]);
        }

        for (int i = 0; i < second_attributes.length; i++) {
            Object value = hashMap.get(second_attributes[i]);
            if (value == null) {
                Logger.getLogger(AttributeConstantsTest.class.getName()).log(Level.INFO, message + second_attributes[i]);
                if(failed){
                    fail(message);
                }
            }
        }
    }

}
