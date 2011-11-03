package com.icesoft.faces.webapp.http.core;

/**
 *
 */

import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.context.portlet.PortletExternalContext;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.executor.FlowExecutionResult;

public class FlowExecutionHandlerFactory {

	public static FlowExecutionHandler getInstance(javax.faces.context.ExternalContext facesExternalContext, FlowExecutionResult result, ExternalContext externalContext, FacesContext facesContext) {
		if(externalContext instanceof ServletExternalContext){
			HttpServletRequest servletRequest = (HttpServletRequest) externalContext.getNativeRequest();
			HttpServletResponse servletResponse = (HttpServletResponse) externalContext.getNativeResponse();
			return new ServletFlowExecutionHandler(facesExternalContext, result, (ServletExternalContext)externalContext, servletRequest, servletResponse, facesContext);
		} else {
			PortletRequest portletRequest = (PortletRequest) externalContext.getNativeRequest();
			PortletResponse portletResponse = (PortletResponse) externalContext.getNativeResponse();
			return new PortletFlowExecutionHandler(facesExternalContext, result, (PortletExternalContext)externalContext, portletRequest, portletResponse, facesContext);
		}
	}
}