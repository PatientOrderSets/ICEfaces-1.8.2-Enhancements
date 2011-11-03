package com.icesoft.faces.mock.test;

import com.icesoft.faces.mock.test.container.MockTestCase;
import com.icesoft.faces.mock.test.container.MockSerializedView;
import com.icesoft.faces.component.ext.HtmlForm;
import com.icesoft.faces.util.CoreUtils;
import com.sun.faces.application.StateManagerImpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestSuite;


/**
 *
 * @author fye
 */
public class SerializedViewFieldTest extends MockTestCase {
    private static Method SelectItem_isEscape;
    static {
        try {
            SelectItem_isEscape = javax.faces.model.SelectItem.class.getMethod("isEscape", new Class[] {});
        }
        catch(Exception e) { // JSF 1.1
        }
    }

    public static Test suite() {
        return new TestSuite(SerializedViewFieldTest.class);
    }
    
    private boolean normalRules(UIComponent uiComponent, Map resultMap, boolean bVal) {
//System.out.println("normalRules()  uiComponent: " + uiComponent.getClass().getName());
        boolean success = true;
        Field[] stateFields = CompPropsUtils.getStateFieldsForComponent(uiComponent);
        for(int i = 0; i < stateFields.length; i++) {
            Field field = stateFields[i];
//System.out.println("normalRules()    field: " + field.getName());
            Object value = null;
            try {
                value = TestDataProvider.getSimpleTestObject(field.getType(), bVal);
                field.set(uiComponent, value);
            }
            catch(IllegalAccessException e) {
                Logger.getLogger(SerializedViewFieldTest.class.getName()).log(Level.SEVERE, "Problem accessing field", e);
                success = false;
            }
            catch(InstantiationException e) {
                String msg = "For " + uiComponent.getClass().getName() + "." + field.getName() + ", with type: " + field.getType() + ", could not instantiate test value";
                Logger.getLogger(SerializedViewFieldTest.class.getName()).log(Level.SEVERE, msg, e);
                success = false;
            }
            catch(IllegalArgumentException e) {
                String msg = "For " + uiComponent.getClass().getName() + "." + field.getName() + ", with type: " + field.getType() + ", could not set value: " + value;
                Logger.getLogger(SerializedViewFieldTest.class.getName()).log(Level.SEVERE, msg, e);
                success = false;
            }
            resultMap.put(field.getName(), value);
        }
        return success;
    }

    private void setDefaultTestDataProvider(UIComponent[] allComps, UIComponent form, Map expectedMap, boolean bVal) {
        boolean completeSuccess = true;
        for (int i = 0; i < allComps.length; i++) {
            Map classesMap = new HashMap();
            UIComponent uiComponent = allComps[i];
            boolean success = normalRules(uiComponent, classesMap, bVal);
            if (!success)
                completeSuccess = false;
            form.getChildren().add(uiComponent);

            expectedMap.put(uiComponent.getClass().getName(), classesMap);
        }
        if (!completeSuccess)
            fail();
    }
    
    public void testBothSerializedSaveState() {
        runSerializedSaveState(false);
        runSerializedSaveState(true);
    }
    
    private void runSerializedSaveState(boolean bVal) {
        UIViewRoot uiViewRoot = getViewHandler().createView(getFacesContext(), this.getClass().getName() + "_view_id");
        getFacesContext().setViewRoot(uiViewRoot);
        HtmlForm form = new HtmlForm();
        uiViewRoot.getChildren().add(form);

        UIComponent[] oldAllComps = getUIComponents();
        UIComponent[] allComps = new UIComponent[oldAllComps.length];
        System.arraycopy(oldAllComps, 0, allComps, 0, oldAllComps.length);

        Map expectedMap = new HashMap();
        setDefaultTestDataProvider(allComps, form, expectedMap, bVal);
        
        System.out.println("Done populating test data. About to save state");
        
        //save state
        Object state = uiViewRoot.processSaveState(getFacesContext());
        
        System.out.println("Processed saved state. About to capture child");
        
        Class stateManagerClass = null;
        Object stateManager = null;
        try {
            stateManagerClass = Class.forName("com.sun.faces.application.StateManagerImpl");
            stateManager = stateManagerClass.newInstance();
        }
        catch(Exception e) {
            String msg = "Problem loading Sun's JSF 1.2 StateManagerImpl class: " + e.getMessage();
            Logger.getLogger(ExternalizableTest.class.getName()).log(Level.SEVERE, msg, e);
            fail(msg);
        }
        List treeList = new ArrayList();
        invokePrivateMethod("captureChild",
                new Class[]{List.class, Integer.TYPE, UIComponent.class},
                new Object[]{treeList, 0, uiViewRoot},
                stateManagerClass,
                stateManager);

        //tree
        Object[] oldComps = treeList.toArray();
        Object[] comps = new Object[oldComps.length];
        System.arraycopy(oldComps, 0, comps, 0, oldComps.length);

        System.out.println("Captured child. About to serialize");
        
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
            
            System.out.println("Serialized. About to deserialize");
            
            byte[] bytes = bos.toByteArray();
            InputStream in = new ByteArrayInputStream(bytes);
            GZIPInputStream gis = new GZIPInputStream(in);
            ObjectInputStream ois = new ObjectInputStream(gis);

            readView = (MockSerializedView) ois.readObject();
            
            System.out.println("Deserialized. About to restore data tree");
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
                new Object[]{readView.getStructure()}, stateManagerClass,
                stateManager);
        
        System.out.println("Restored data tree. About to restore component state");
        
        restoreViewRoot.processRestoreState(getFacesContext(), readView.getState());
        
        System.out.println("Restored component state. About to verify");
        
        UIComponent firstForm = (UIComponent) restoreViewRoot.getChildren().get(0);
        List<UIComponent> restoredChildren = firstForm.getChildren();
        
        verifyExpectedOutput(restoredChildren, expectedMap);
        
        System.out.println("Verified.");
    }

    private void verifyExpectedOutput(List<UIComponent> restoredChildren, Map expectedMap) {
        boolean failed = false;
        for (int i = 0; i < restoredChildren.size(); i++) {
            try {
                UIComponent uiComponent = restoredChildren.get(i);
                Map testMap = (Map) expectedMap.get(uiComponent.getClass().getName());

                Field[] stateFields = CompPropsUtils.getStateFieldsForComponent(uiComponent);
                for(int j = 0; j < stateFields.length; j++) {
                    Field field = stateFields[j];
                    Object restoredValue = field.get(uiComponent);
                    Object storedValue = testMap.get(field.getName());
                    String msg = compareValues(field.getType(), storedValue, restoredValue);
                    if (msg != null) {
                        Logger.getLogger(SerializedViewFieldTest.class.getName()).log(Level.SEVERE, "MISMATCH for " + uiComponent.getClass().getName() + "." + field.getName() + "\n\t" + msg);
                        failed = true;
                    }
                }
            }
            catch(IllegalAccessException e) {
                Logger.getLogger(SerializedViewFieldTest.class.getName()).log(Level.SEVERE, "Problem accessing field", e);
                failed = true;
            }
        }
        if (failed) {
            fail("Verification failed");
        }
    }
    
    private String compareValues(Class clazz, Object storedValue, Object restoredValue) {
        if (storedValue == null && restoredValue == null) {
            return null;
        }
        if (storedValue != null && restoredValue == null) {
            return "Restored value is null, while stored value was not: " + storedValue;
        }
        if (storedValue == null && restoredValue != null) {
            return "Stored value was null, but restored value is not: " + restoredValue;
        }
        if (clazz.isArray()) {
            int storedLength = Array.getLength(storedValue);
            int restoredLength = Array.getLength(restoredValue);
            if (storedLength != restoredLength) {
                return "Stored array had length of " + storedLength + ", but restored array has length of " + restoredLength;
            }
            for(int i = 0; i < storedLength; i++) {
                Object sv = Array.get(storedValue, i);
                Object rv = Array.get(restoredValue, i);
                String msg = compareValues(clazz.getComponentType(), sv, rv);
                if (msg != null) {
                    return "["+i+"]" + (msg.startsWith("[") ? "" : " :: ") + msg;
                }
            }
            return null;
        }
        if (javax.faces.model.SelectItem.class.equals(clazz)) {
            javax.faces.model.SelectItem sv = (javax.faces.model.SelectItem) storedValue;
            javax.faces.model.SelectItem rv = (javax.faces.model.SelectItem) restoredValue;
            Object svf, rvf;
            StringBuffer sb = null;
            String msg = null;
            if (null != (msg = compareValues(java.lang.String.class, svf = sv.getValue(), rvf = rv.getValue()))) {
                if (sb == null) sb = new StringBuffer(128);
                sb.append("Stored SelectItem.value was " + svf + ", but restored SelectItem.value is " + rvf);
            }
            if (null != (msg = compareValues(java.lang.String.class, svf = sv.getLabel(), rvf = rv.getLabel()))) {
                if (sb == null) sb = new StringBuffer(128);
                sb.append("Stored SelectItem.label was " + svf + ", but restored SelectItem.value is " + rvf);
            }
            if (null != (msg = compareValues(java.lang.String.class, svf = sv.getDescription(), rvf = rv.getDescription()))) {
                if (sb == null) sb = new StringBuffer(128);
                sb.append("Stored SelectItem.description was " + svf + ", but restored SelectItem.description is " + rvf);
            }
            if (sv.isDisabled() != rv.isDisabled()) {
                if (sb == null) sb = new StringBuffer(128);
                sb.append("Stored SelectItem.disabled was " + sv.isDisabled() + ", but restored SelectItem.description is " + rv.isDisabled());
            }
            if (SelectItem_isEscape != null) {
                try {
                    svf = SelectItem_isEscape.invoke(sv, new Object[0]);
                    rvf = SelectItem_isEscape.invoke(rv, new Object[0]);
                    if (null != (msg = compareValues(java.lang.Boolean.class, svf, rvf))) {
                        if (sb == null) sb = new StringBuffer(128);
                        sb.append("Stored SelectItem.escape was " + svf + ", but restored SelectItem.escape is " + rvf);
                    }
                }
                catch(Exception e) {
                    return "Problem comparing SelectItem.escape: " + e.getMessage();
                }
            }
            return null;
        }
        if (java.lang.Throwable.class.equals(clazz)) {
            if (!CoreUtils.throwablesEqual((Throwable)storedValue, (Throwable)restoredValue)) {
                return "Stored Throwable was " + storedValue + ", but restored Throwable is " + restoredValue;
            }
            return null;
        }
        if (!storedValue.equals(restoredValue)) {
            return "Stored value was " + storedValue + ", but restored value is " + restoredValue;
        }
        return null;
    }
    
    private void print(String message) {
        Logger.getLogger(SerializedViewFieldTest.class.getName()).log(Level.INFO, message);
    }
}
