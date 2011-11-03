package com.icesoft.faces.application;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.Locale;

public class ViewHandlerProxy extends ViewHandler {
    protected ViewHandler handler;

    public ViewHandlerProxy(ViewHandler handler) {
        this.handler = handler;
    }

    public Locale calculateLocale(FacesContext context) {
        return handler.calculateLocale(context);
    }

    public String calculateRenderKitId(FacesContext context) {
        return handler.calculateRenderKitId(context);
    }

    public UIViewRoot createView(FacesContext context, String viewId) {
        return handler.createView(context, viewId);
    }

    public String getActionURL(FacesContext context, String viewId) {
        return handler.getActionURL(context, viewId);
    }

    public String getResourceURL(FacesContext context, String path) {
        return handler.getResourceURL(context, path);
    }

    public void renderView(FacesContext context, UIViewRoot viewToRender) throws IOException, FacesException {
        handler.renderView(context, viewToRender);
    }

    public UIViewRoot restoreView(FacesContext context, String viewId) {
        return handler.restoreView(context, viewId);
    }

    public void writeState(FacesContext context) throws IOException {
        handler.writeState(context);
    }
}
