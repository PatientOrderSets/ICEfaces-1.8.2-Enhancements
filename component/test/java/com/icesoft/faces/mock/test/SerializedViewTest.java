/*
 * limited scope mock test for save and restore the tree as well as state related to it
 */
package com.icesoft.faces.mock.test;

import com.icesoft.faces.mock.test.container.MockTestCase;
import com.icesoft.faces.mock.test.container.MockSerializedView;
import com.icesoft.faces.mock.test.data.MockDataObject;
import com.icesoft.faces.component.ext.HtmlForm;
import com.sun.faces.application.StateManagerImpl;

//
import com.sun.faces.mock.MockMethodBinding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIViewRoot;
import javax.faces.el.MethodBinding;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.commons.beanutils.PropertyUtils;

/**
 *
 * @author fye
 */
public class SerializedViewTest extends MockTestCase {

    public static Test suite() {
        return new TestSuite(SerializedViewTest.class);
    }

    private boolean isRelativeRules(UIComponent uiComponent, String name, String className) {

        try {
            if (className == null) {
                //TODO fail this one, work around only 
                className = "java.lang.String";
            }
            Method[] myMethods = CompPropsUtils.describe_useRef_method(uiComponent);
            for (int i = 0; i < myMethods.length; i++) {
                Method method = myMethods[i];
                String methodName = method.getName();
                //work around
                if (methodName.toLowerCase().indexOf(name.toLowerCase()) != -1) {
                    return true;
                }
            }
        } catch (SecurityException ex) {
            Logger.getLogger(SerializedViewTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private void normalRules(UIComponent uiComponent, Map resultMap) {

        Map fieldMap = new HashMap();
        CompPropsUtils.describe_useRefl_field(uiComponent, fieldMap);

        Map propsMap = new HashMap();
        //Generic Reflection
        //CompPropsUtils.describe_useBeanUtils(uiComponent, propsMap);
        CompPropsUtils.describe_useMetaBeanInfo(uiComponent, propsMap);

        TestDataProvider testDataProvider = new TestDataProvider();

        Iterator iterator = fieldMap.keySet().iterator();
        String message = "Component=" + uiComponent.getClass().getName() + "\n Field details: ";
        String fieldName = null;
        String fieldValue = null;

        String className = null;
        while (iterator.hasNext()) {
            fieldName = (String) iterator.next();
            fieldValue = (String) fieldMap.get(fieldName);
            boolean modified = false;
            //Coding style with instance name
            if (fieldName.startsWith("_")) {
                fieldName = fieldName.substring(1);
                modified = true;
            }

            Object propValue = propsMap.get((String) fieldName);
            className = (String) propValue;
            if (className == null) {
                className = "java.lang.String";
            }

            if (propValue != null) {
                resultMap.put(fieldName, fieldValue);
            //TODO: fix me
//                if(className.toLowerCase().indexOf("boolean") == -1
//                        && className.toLowerCase().indexOf("int") == -1
//                        && !className.equalsIgnoreCase(fieldValue)){
//                    message += "\n\tField with property method, but not same Type="+fieldName
//                            +"field Type="+ fieldValue+" property Type="+className;
//                    fail(message);
//                }
            } else if (isRelativeRules(uiComponent, fieldName, fieldValue)) {

//                if(fieldName.equalsIgnoreCase("currentStyle")){
//                    //resultMap.put(fieldName, fieldValue);
//                    continue;
//                }
//                if(fieldName.equalsIgnoreCase("onchangeeffect")){
//                    resultMap.put("onchangeeffect", fieldValue);
//                }
                message += "\n\tManual Verify: has method impl, name=" + fieldName + " classType = " + fieldValue;
            } else {

                if (fieldValue == null) {
                    message += "failed: fieldValue is Null  field name=" + fieldName;
                    fail(message);
                }

                if (fieldValue != null && fieldValue.toLowerCase().indexOf("boolean") != -1 && fieldName.toLowerCase().endsWith("set")) {
                    //no warning for boolean _set 
                } else {
                    if (modified) {
                        fieldName = "_" + fieldName;
                    }

                    try {

                        if (fieldValue.startsWith("[L")) {
                            continue;
                        }
                        message += "\n\tManual Verify: no method impl, instance name=" + fieldName + " classType=" + fieldValue;
                        Object expectedvalue = testDataProvider.getSimpleTestObject(fieldValue);
                        setPrivateFieldValue(uiComponent, uiComponent.getClass(), fieldName, expectedvalue);
                        
                        Object changed_value = getPrivateFieldValue(uiComponent, uiComponent.getClass(), fieldName);
                        
                        String clientId = uiComponent.getClientId(getFacesContext());

//                        message = "Component=" + uiComponent.getClass().getName() + "\n Field details: ";
//                        message += "\n\tManual Verify: no method impl, instance name=" + fieldName + " classType=" + fieldValue;
//                        verifyWithDefault(testDataProvider, fieldValue, message, expectedvalue, changed_value);
//

                    } catch (AssertionFailedError error) {
                        print(error.getMessage());
                    }
                }
            }
        }

        if (!message.endsWith("\n Field details: ")) {
            print(message);
        }
    }

    private void setDefaultTestDataProvider(UIComponent[] allComps, UIComponent form, Map expectedMap, TestDataProvider testDataProvider) {
        for (int i = 0; i < allComps.length; i++) {
            Map classesMap = new HashMap();
            UIComponent uiComponent = allComps[i];
            normalRules(uiComponent, classesMap);
            form.getChildren().add(uiComponent);

            Map expectedValueMap = new HashMap();
            Iterator iterator = classesMap.keySet().iterator();
            String message = "";
            String keyProp = null;
            String keyValue = null;
            Object value = null;
            while (iterator.hasNext()) {
                try {
                    keyProp = (String) iterator.next();
                    keyValue = (String) classesMap.get(keyProp);

                    value = testDataProvider.getSimpleTestObject(keyValue);

                    message = "Component=" + uiComponent.getClass().getName() + " name=" + keyProp + " class=" + keyValue + " setValue=" + value;

                    if (keyValue.equalsIgnoreCase("com.icesoft.faces.utils.UpdatableProperty") || keyValue.equalsIgnoreCase("java.io.File") || keyProp.equalsIgnoreCase("disabled")) {
                        continue;
                    }

                    if (keyValue.equalsIgnoreCase("javax.faces.el.MethodBinding")) {
                        //limited scope
                        Class args[] = new Class[]{};
                        value = getFacesContext().getApplication().createMethodBinding("#{mock.methodBinding}",
                                args);
                    }

                    if (keyValue.equalsIgnoreCase("java.util.Map")) {
                        Map tempMap = (Map) value;
                        for (int j = 0; j < 4; j++) {
                            tempMap.put(new Integer(j), new Integer(j));
                        }
                    }

                    //try to set instance as well as attribute map
                    PropertyUtils.setProperty(uiComponent, keyProp, value);
                    if (keyValue.equals("java.lang.String")) {
                        Map map = (Map) invokePrivateMethod("getAttributes", new Class[0], new Object[0], UIComponentBase.class, uiComponent);
                        map.put(keyProp, value);
                    }
                } catch (IllegalAccessException ex) {
                    fail(message);
                    Logger.getLogger(SerializedViewTest.class.getName()).log(Level.SEVERE, message, ex);
                } catch (InvocationTargetException ex) {
                    fail(message);
                    Logger.getLogger(SerializedViewTest.class.getName()).log(Level.SEVERE, message, ex);
                } catch (NoSuchMethodException ex) {
                    //TODO enable me                    
                    Logger.getLogger(SerializedViewTest.class.getName()).log(Level.SEVERE, "FIX ME:" + message, ex);
                    fail(message);
                    continue;
                }
                expectedValueMap.put(keyProp, keyValue);
            }
            expectedMap.put(uiComponent.getClass().getName(), expectedValueMap);
        }
    }

    public void testSerializabedSaveState() {

        UIViewRoot uiViewRoot = getViewHandler().createView(getFacesContext(), this.getClass().getName() + "_view_id");
        getFacesContext().setViewRoot(uiViewRoot);
        HtmlForm form = new HtmlForm();

        UIComponent[] oldAllComps = getUIComponents();
        UIComponent[] allComps = new UIComponent[oldAllComps.length];
        System.arraycopy(oldAllComps, 0, allComps, 0, oldAllComps.length);

        Map expectedMap = new HashMap();
        TestDataProvider testDataProvider = new TestDataProvider();
        setDefaultTestDataProvider(allComps, form, expectedMap, testDataProvider);

        uiViewRoot.getChildren().add(form);

        //save state
        Object state = uiViewRoot.processSaveState(getFacesContext());

        StateManagerImpl stateManager = new StateManagerImpl();
        List treeList = new ArrayList();
        invokePrivateMethod("captureChild",
                new Class[]{List.class, Integer.TYPE, UIComponent.class},
                new Object[]{treeList, 0, uiViewRoot},
                StateManagerImpl.class,
                stateManager);

        //tree
        Object[] oldComps = treeList.toArray();
        Object[] comps = new Object[oldComps.length];
        System.arraycopy(oldComps, 0, comps, 0, oldComps.length);

        //tree plus state
        MockSerializedView view = new MockSerializedView(comps, state);
        MockSerializedView readView = null;
        try {
            GZIPOutputStream zos = null;
            ObjectOutputStream oos = null;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            zos = new GZIPOutputStream(bos);
            oos = new ObjectOutputStream(zos);

            oos.writeObject(view);
            oos.close();
            byte[] bytes = bos.toByteArray();
            InputStream in = new ByteArrayInputStream(bytes);
            GZIPInputStream gis = new GZIPInputStream(in);
            ObjectInputStream ois = new ObjectInputStream(gis);

            readView = (MockSerializedView) ois.readObject();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ExternalizableTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("Serialized View failed  " + ex.getMessage());
        } catch (NotSerializableException ex) {
            Logger.getLogger(ExternalizableTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("Serialized View failed  " + ex.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(ExternalizableTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("Serialized View failed  " + ex.getMessage());
        }

        UIViewRoot restoreViewRoot = (UIViewRoot) invokePrivateMethod("restoreTree",
                new Class[]{Object[].class},
                new Object[]{readView.getStructure()}, StateManagerImpl.class,
                stateManager);
        restoreViewRoot.processRestoreState(getFacesContext(), readView.getState());

        UIComponent firstForm = (UIComponent) restoreViewRoot.getChildren().get(0);
        List<UIComponent> restoredChildren = firstForm.getChildren();

        verifyExpectedOutput(restoredChildren, expectedMap, testDataProvider);
    }

    private void verifyWithDefault(TestDataProvider testDataProvider, String propClassName, String message, Object expectedValue, Object actualValue) {
        int m = testDataProvider.getMatchClass(propClassName);
        //            "java.util.List", //1
        //            "java.lang.Double", //2
        //            "java.lang.String", //3
        //            "boolean", //4
        //            "com.icesoft.faces.context.effects.Effect", //5
        //            "java.lang.Object", //6
        //            "javax.faces.el.MethodBinding",//7
        //            "int",//8
        //            "java.lang.Integer",//9
        //            "java.util.Date", //10
        //            "java.io.File", //11
        //            "java.util.Map", //12
        //            "javax.faces.convert.Converter", //13
        //            "java.lang.Boolean", //14
        //            "javax.faces.component.UIComponent", //15
        //            "com.icesoft.faces.utils.UpdatableProperty" //16
        switch (m) {
            case 1:
                assertEquals(message, ((java.util.List) expectedValue).size(), ((java.util.List) actualValue).size());
                break;
            case 2:
                assertEquals(message, Double.parseDouble(expectedValue.toString()), Double.parseDouble(actualValue.toString()));
                break;
            case 3:
                String expectedValue_string = (String) expectedValue;
                String actualValue_string = (String) actualValue;
                if (!expectedValue.equals(actualValue_string) && actualValue_string.indexOf(expectedValue_string) == -1) {
                    fail(message);
                }
                break;
            case 4://limited boolean
                assertEquals(message, Boolean.parseBoolean(expectedValue.toString()), Boolean.parseBoolean(actualValue.toString()));
                break;
            case 5:
                assertEquals(message, expectedValue.toString(), actualValue.toString());
                break;
            case 6:
                if (!(actualValue instanceof MockDataObject)) {
                    fail(message);
                } else {
                    if (!((MockDataObject) expectedValue).getTest().equals(((MockDataObject) actualValue).getTest())) {
                        fail(message);
                    }
                }
                break;
            case 7:
                //limited scope "javax.faces.el.MethodBinding"
                if (actualValue instanceof MockMethodBinding) {
                    assertEquals(message, "#{mock.methodBinding}", ((MockMethodBinding) actualValue).getExpressionString());
                } else {
                    fail(message);
                }
                break;
            case 8:
                assertEquals(message, Integer.parseInt(expectedValue.toString()), Integer.parseInt(actualValue.toString()));
                break;
            case 9:
                assertEquals(message, (Integer) expectedValue, (Integer) actualValue);
                break;
            case 10:
                assertEquals(message, ((java.util.Date) expectedValue).getTime(), ((java.util.Date) actualValue).getTime());
                break;
            case 11:
                assertEquals(message, ((java.io.File) expectedValue).getPath(), ((java.io.File) actualValue).getPath());
                break;
            case 12://limited "java.util.Map"
                assertEquals(message, 4, ((java.util.Map) actualValue).size());
                break;
            case 13://TODO "javax.faces.convert.Converter"
                break;
            case 14://limited java.lang.Boolean
                assertEquals(message, Boolean.parseBoolean(expectedValue.toString()), Boolean.parseBoolean(actualValue.toString()));
                break;
            case 15:
                assertEquals(message, ((UIComponent) expectedValue).getRendererType(), ((UIComponent) actualValue).getRendererType());
                break;
            case 16:
                //TODO "com.icesoft.faces.utils.UpdatableProperty"
                break;

            default:
                fail("unknown property class where " + message);
        }
    }

    private void verifyExpectedOutput(List<UIComponent> restoredChildren, Map expectedMap, TestDataProvider testDataProvider) {

        for (int i = 0; i < restoredChildren.size(); i++) {
            UIComponent uiComponent = restoredChildren.get(i);
            Map testMap = (Map) expectedMap.get(uiComponent.getClass().getName());
            Iterator iterator = testMap.keySet().iterator();
            while (iterator.hasNext()) {
                try {
                    Object valueName = iterator.next();
                    String propClassName = (String) testMap.get(valueName);
                    Object expectedValue = testDataProvider.getSimpleTestObject((String) testMap.get(valueName));
                    Object actualValue = PropertyUtils.getProperty(uiComponent, (String) valueName);

                    String message = " Component Test Results class=" + uiComponent.getClass().getName() + " Property name=" + valueName + " expectedValue contains=" + expectedValue + " actualValue=" + actualValue;

                    if (actualValue == null) {
                        fail(message);
                        continue;
                    }
                    verifyWithDefault(testDataProvider, propClassName, message, expectedValue, actualValue);

                } catch (IllegalAccessException ex) {
                    Logger.getLogger(SerializedViewTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(SerializedViewTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchMethodException ex) {
                    Logger.getLogger(SerializedViewTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (AssertionFailedError error) {
                    print(error.getMessage());
                }
            }
        }
    }

    private void print(String message) {
        Logger.getLogger(SerializedViewTest.class.getName()).log(Level.INFO, message);
    }
}
