/*
 * 
 */

package com.icesoft.faces.mock.test.container;

import java.io.IOException;
import javax.faces.application.StateManager;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

/**
 *
 * @author fye
 */
public class MockStateManager extends StateManager{

    @Override
    protected Object getComponentStateToSave(FacesContext context) {
        return super.getComponentStateToSave(context);
    }

    @Override
    protected Object getTreeStructureToSave(FacesContext context) {
        return super.getTreeStructureToSave(context);
    }

    @Override
    public boolean isSavingStateInClient(FacesContext context) {
        return super.isSavingStateInClient(context);
    }

    @Override
    protected void restoreComponentState(FacesContext context, UIViewRoot viewRoot, String renderKitId) {
        super.restoreComponentState(context, viewRoot, renderKitId);
    }

    @Override
    protected UIViewRoot restoreTreeStructure(FacesContext context, String viewId, String renderKitId) {
        return super.restoreTreeStructure(context, viewId, renderKitId);
    }

    @Override
    public SerializedView saveSerializedView(FacesContext context) {
        return super.saveSerializedView(context);
    }

    @Override
    public Object saveView(FacesContext context) {
        return super.saveView(context);
    }

    @Override
    public void writeState(FacesContext context, Object state) throws IOException {
        super.writeState(context, state);
    }

    @Override
    public void writeState(FacesContext context, SerializedView state) throws IOException {
        super.writeState(context, state);
    }

    @Override
    public UIViewRoot restoreView(FacesContext context, String viewId, String renderKitId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
