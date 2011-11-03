/*
 *
 *
 */
package com.icesoft.faces.mock.test;

import com.icesoft.faces.mock.test.container.MockTestCase;
import com.icesoft.faces.component.ext.HtmlForm;
import com.icesoft.faces.component.ext.HtmlInputText;
import com.sun.faces.application.StateManagerImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 *
 * @author fye
 */
public class InputTextSaveStateTest extends MockTestCase {

    public static Test suite() {
        return new TestSuite(InputTextSaveStateTest.class);
    }

    public void testSaveState() {

        UIViewRoot uiViewRoot = getViewHandler().createView(getFacesContext(), this.getClass().getName()+"_view_id");
        getFacesContext().setViewRoot(uiViewRoot);

        HtmlForm form = new HtmlForm();
        form.setPartialSubmit(true);
        HtmlInputText inputText = new HtmlInputText();
        inputText.setFocus(Boolean.TRUE);
        inputText.setDisabled(Boolean.TRUE);

        form.getChildren().add(inputText);
        uiViewRoot.getChildren().add(form);

        //save state
        Object state = uiViewRoot.processSaveState(getFacesContext());

        StateManagerImpl stateManager = new StateManagerImpl();
        //MockStateManager stateManager = (MockStateManager)getFacesContext().getApplication().getStateManager();
        List treeList = new ArrayList();
        invokePrivateMethod("captureChild",
                new Class[]{List.class, Integer.TYPE, UIComponent.class},
                new Object[]{treeList, 0, uiViewRoot},
                StateManagerImpl.class,
                stateManager);

        //tree
        Object[] comps = treeList.toArray();

        UIViewRoot restoreViewRoot = (UIViewRoot) invokePrivateMethod("restoreTree",
                new Class[]{Object[].class},
                new Object[]{comps}, StateManagerImpl.class,
                stateManager);
        restoreViewRoot.processRestoreState(getFacesContext(), state);

        UIComponent firstForm = (UIComponent) restoreViewRoot.getChildren().get(0);
        UIComponent first = (UIComponent) firstForm.getChildren().get(0);
        myBooleanAttribute(first, "focus", Boolean.TRUE);
        myBooleanAttribute(first, "disabled", Boolean.TRUE);

    }
    
    private void myBooleanAttribute(UIComponent first, String attributeName, boolean expectedValue) {

        Object value = first.getAttributes().get(attributeName);
        if (value != null) {
            Boolean booleanValue = (Boolean) value;
            String message = " component=" + first + " property " + attributeName + " value=" + booleanValue.toString()+" expected value="+ expectedValue;
            Logger.getLogger(InputTextSaveStateTest.class.getName()).log(Level.INFO, message);
        } else {
            String message = " component=" + first + " property " + attributeName + "=null";
            Logger.getLogger(InputTextSaveStateTest.class.getName()).log(Level.INFO, message);
        }
    }
}
