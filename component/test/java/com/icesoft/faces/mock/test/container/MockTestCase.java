/*
 * limited scope mock objects test 
 */
package com.icesoft.faces.mock.test.container;

import com.icesoft.faces.metadata.ICECompsListHelper;
import com.sun.faces.config.WebConfiguration;
import com.sun.faces.mock.MockApplication;
import com.icesoft.faces.mock.test.container.MockExternalContext;
import com.sun.faces.mock.MockFacesContext;
import com.sun.faces.mock.MockHttpServletRequest;
import com.sun.faces.mock.MockHttpServletResponse;
import com.sun.faces.mock.MockHttpSession;
import com.sun.faces.mock.MockLifecycle;
import com.sun.faces.mock.MockRenderKit;
import com.sun.faces.mock.MockViewHandler;
import com.sun.rave.jsfmeta.beans.RendererBean;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.FactoryFinder;
import javax.faces.application.ApplicationFactory;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import junit.framework.TestCase;

/**
 *
 * @author fye
 */
public class MockTestCase extends TestCase {

    //Mock Container
    private MockApplication application;
    private MockViewHandler viewHandler;
    private MockServletConfig servletConfig;
    private MockExternalContext externalContext;
    private MockFacesContext facesContext;
    private Lifecycle lifecycle;
    private HttpServletRequest httpServletRequest;
    private HttpServletResponse httpServletResponse;
    private MockServletContext servletContext;
    private HttpSession httpSession;

    //TODO
    private Properties properties;

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    protected void defaultMockContainer() {

        //Servlet API 2.4
        servletContext = new MockServletContext();
        servletConfig = new MockServletConfig(servletContext);
        httpSession = new MockHttpSession(servletContext);

        httpServletRequest = new MockHttpServletRequest(httpSession);
        httpServletResponse = new MockHttpServletResponse();

        String appFactoryName = "com.sun.faces.mock.MockApplicationFactory";
        FactoryFinder.setFactory(FactoryFinder.APPLICATION_FACTORY, appFactoryName);

        String renderKitFactoryName = "com.sun.faces.mock.MockRenderKitFactory";
        FactoryFinder.setFactory(FactoryFinder.RENDER_KIT_FACTORY, renderKitFactoryName);

        externalContext = new MockExternalContext(servletContext, httpServletRequest, httpServletResponse);

        Map myRequestParameterMap = new HashMap();
        myRequestParameterMap.put("clientid_inputText","event");
        externalContext.setRequestParameterMap(myRequestParameterMap);
        lifecycle = new MockLifecycle();

        facesContext = new MockFacesContext(externalContext, lifecycle);

        ApplicationFactory applicationFactory = (ApplicationFactory) FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY);
        application = (MockApplication) applicationFactory.getApplication();
        facesContext.setApplication(application);

        //TODO:
        WebConfiguration webConfig = WebConfiguration.getInstance(servletContext);
        viewHandler = new MockViewHandler();
        application.setViewHandler(viewHandler);

        //TODO:
        RenderKitFactory renderKitFactory = (RenderKitFactory)FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
        RenderKit renderkit = new MockRenderKit();
        renderKitFactory.addRenderKit(renderKitFactoryName, renderkit);
        renderKitFactory.addRenderKit(RenderKitFactory.HTML_BASIC_RENDER_KIT, renderkit);
    }

    @Override
    protected void setUp() throws Exception {

        defaultMockContainer();
    }

    @Override
    protected void tearDown() throws Exception {
        application = null;
        viewHandler = null;
        servletConfig = null;
        externalContext = null;
        facesContext = null;
        lifecycle = null;
        httpServletRequest = null;
        httpServletResponse = null;
        servletContext = null;
        httpSession = null;
        properties = null;
    }

    public ViewHandler getViewHandler() {
        return viewHandler;
    }

    protected FacesContext getFacesContext() {
        return facesContext;
    }

    protected Object invokePrivateMethod(String methodName,
            Class[] params,
            Object[] args,
            Class className,
            Object invocationTarget) {
        try {
            Method method = className.getDeclaredMethod(methodName, params);
            method.setAccessible(true);
            return method.invoke(invocationTarget, args);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(MockTestCase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(MockTestCase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(MockTestCase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(MockTestCase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    protected Object getPrivateFieldValue(Object instance, Class className, String fieldName){
        try {            
            Field field = className.getDeclaredField(fieldName);
            field.setAccessible(true);
                   
            return field.get(instance);
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(MockTestCase.class.getName()).log(Level.SEVERE, className.getName()+" "+ex.getMessage(), ex);
        } catch (SecurityException ex) {
            Logger.getLogger(MockTestCase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(MockTestCase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(MockTestCase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    protected void setPrivateFieldValue(Object instance, Class className, String fieldName, Object value){
        try {            
            Field field = className.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(instance, value);
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(MockTestCase.class.getName()).log(Level.SEVERE, className.getName()+" "+ex.getMessage(), ex);
        } catch (SecurityException ex) {
            Logger.getLogger(MockTestCase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(MockTestCase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(MockTestCase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public UIComponent[] getUIComponents(){
        return ICECompsListHelper.getComponents();
    }

    public RendererBean getRendererBean(String componentFamily, String rendererType){
        return ICECompsListHelper.getRenderer(componentFamily, rendererType);
    }
}
