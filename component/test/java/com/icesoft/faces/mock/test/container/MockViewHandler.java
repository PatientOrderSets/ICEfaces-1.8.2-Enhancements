/*
 * ViewHandler wrapper
 */
package com.icesoft.faces.mock.test.container;

import java.io.IOException;
import java.util.Locale;
import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.render.RenderKitFactory;

/**
 *
 * @author fye
 */
public class MockViewHandler extends ViewHandler {

    @Override
    public Locale calculateLocale(FacesContext context) {
        System.out.println("TODO caculateLocale");
        return Locale.CANADA;
    }

    @Override
    public String calculateRenderKitId(FacesContext context) {
        return "HTML_BASIC";
        //return RenderKitFactory.HTML_BASIC_RENDER_KIT;
    }

    @Override
    public UIViewRoot createView(FacesContext context, String viewId) {
        System.out.println("createView ");
        UIViewRoot result = new UIViewRoot();
        result.setViewId(viewId);
        //result.setRenderKitId(calculateRenderKitId(context));
        return result;
    }

    @Override
    public String getActionURL(FacesContext context, String viewId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getResourceURL(FacesContext context, String path) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void renderView(FacesContext context, UIViewRoot viewToRender) throws IOException, FacesException {
        System.out.println("renderView context=" + context + " UIViewRoot id=" + viewToRender.getViewId());
    }

    @Override
    public UIViewRoot restoreView(FacesContext context, String viewId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void writeState(FacesContext context) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
