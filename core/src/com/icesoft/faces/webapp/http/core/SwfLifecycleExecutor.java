package com.icesoft.faces.webapp.http.core;


import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.webflow.context.portlet.PortletExternalContext;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.definition.registry.NoSuchFlowDefinitionException;
import org.springframework.webflow.executor.FlowExecutionResult;
import org.springframework.webflow.executor.FlowExecutor;

import com.icesoft.faces.env.SpringWebFlowInstantiationServlet;

public class SwfLifecycleExecutor extends LifecycleExecutor  {

    private org.springframework.webflow.context.ExternalContext selectedExternalContext = null;
    private String flowExecutionKey = null;
    private String flowId = null;
    private boolean isServletContext = true;
    private HttpServletRequest servletRequest = null;
    private HttpServletResponse servletResponse = null;
    private PortletRequest portletRequest = null;
    private PortletResponse portletResponse = null;
    private FlowExecutorUtil flowExecutorUtil = null;


    public void apply(FacesContext facesContext)  {
        //FlowExecutor flowExecutor = (FlowExecutor)SpringWebFlowInstantiationServlet.getFlowExecutor();
        ExternalContext externalContext = facesContext.getExternalContext();

        Object context = externalContext.getContext();

        if (context instanceof ServletContext) {
            setupServletContext(externalContext);
        } else {
            setupPortletContext(externalContext);
        }

        flowExecutorUtil = new FlowExecutorUtil((FlowExecutor) SpringWebFlowInstantiationServlet.getFlowExecutor(), selectedExternalContext);
        FlowExecutionResult result = null;

        try {
            if (null != flowExecutionKey)  {
                result = resumeExecution(flowExecutionKey);
            } else {
                result = launchExecution();
            }
        }
        catch (NoSuchFlowDefinitionException e)  {
            getJsfLifecycleExecutor(facesContext);
//            jsfExecutor.apply(facesContext);
        }

        FlowExecutionHandler flowExecutionHandler = FlowExecutionHandlerFactory.getInstance(externalContext, result, selectedExternalContext, facesContext);
        try {
            flowExecutionHandler.handleFlowExecutionResult();
        }
        catch (IOException e) {
            throw(new FacesException(e));
        }
    }

    protected void setupServletContext(ExternalContext externalContext) {
        isServletContext = true;
        ServletContext servletContext = (ServletContext) externalContext.getContext();
        servletRequest = (HttpServletRequest) externalContext.getRequest();
        servletResponse = (HttpServletResponse) externalContext.getResponse();
        flowExecutionKey = servletRequest.getParameter("org.springframework.webflow.FlowExecutionKey");

        // if the ajax value is null, try the URL parameter from a possible GET.
        if (flowExecutionKey == null) {
            flowExecutionKey = servletRequest.getParameter("execution");
        }

        flowId = firstSegment(servletRequest.getPathInfo());
        selectedExternalContext = new ServletExternalContext(servletContext, servletRequest, servletResponse );
        ((ServletExternalContext)selectedExternalContext).setAjaxRequest(true);
    }

    public String firstSegment(String path)  {
        String path1 = path.substring(1);
        int end = path1.indexOf("/");
        if (-1 == end) {
            end = path1.length();
        }
        path1 = path1.substring(0, end);
        return path1;
    }


    protected void setupPortletContext(ExternalContext externalContext) {
        isServletContext = false;
        PortletContext portletContext = (PortletContext) externalContext.getContext();
        portletRequest = (PortletRequest) externalContext.getRequest();
        portletResponse = (PortletResponse) externalContext.getResponse();


        String[] flowExecutionKeyArr = null;

        //See if a flowExecutionKey is present
        flowExecutionKeyArr = (String[]) externalContext.getRequestParameterValuesMap().get("org.springframework.webflow.FlowExecutionKey");
        if(null != flowExecutionKeyArr){
            flowExecutionKey = flowExecutionKeyArr[0];
        } else {
            //we need to do this, otherwise the old value will be stored
            flowExecutionKey = null;
        }

        /*
           * The flowId is stored in portlet.xml and read by the MainPortlet,
           * who then puts it as a attribute for us to use here.
           */
        flowId = (String) portletRequest.getAttribute("org.springframework.webflow.FlowId");
        selectedExternalContext = new PortletExternalContext(portletContext, portletRequest, portletResponse);
    }

    public FlowExecutionResult resumeExecution(String flowExecutionKey){
        return flowExecutorUtil.resumeExecution(flowExecutionKey);
    }

    public FlowExecutionResult launchExecution(){
        MutableAttributeMap input = setupInputMap();
        return flowExecutorUtil.launchExecution(input, flowId);
    }

    public MutableAttributeMap setupInputMap(){
        MutableAttributeMap input = null;
        if(isServletContext){
            input = flowExecutorUtil.defaultFlowExecutionInputMap(servletRequest);
        } else {
            input = flowExecutorUtil.defaultFlowExecutionInputMapPortlet(portletRequest);
        }
        return input;
    }

}