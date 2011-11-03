package com.icesoft.faces.webapp.http.portlet;

import com.icesoft.faces.env.RequestAttributes;

import javax.portlet.RenderRequest;
import java.util.Enumeration;

public class PortletRequestAttributes implements RequestAttributes {
    private final RenderRequest request;

    public PortletRequestAttributes(RenderRequest request) {
        this.request = request;
    }

    public Object getAttribute(String name) {
        return request.getAttribute(name);
    }

    public Enumeration getAttributeNames() {
        return request.getAttributeNames();
    }

    /*ICE-2990 resulted in ICE-3694 so need to catch useless NPE here*/
    public void removeAttribute(String name) {
    	try{
    		request.removeAttribute(name);
    	}catch(Exception e){    		
    	}
    }

    public void setAttribute(String name, Object value) {
    	try{
    		request.setAttribute(name, value);
    	}catch(Exception e){    		
    	}
    }
}
