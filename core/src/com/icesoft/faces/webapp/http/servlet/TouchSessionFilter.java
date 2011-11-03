package com.icesoft.faces.webapp.http.servlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class TouchSessionFilter implements Filter {
    private static final Log LOG = LogFactory.getLog(TouchSessionFilter.class);

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            HttpSession session = request.getSession();
            if (session != null) {
                SessionDispatcher.Monitor mon = SessionDispatcher.Monitor.lookupSessionMonitor(session);
                //If the first request is not an ICEfaces request, there will not be a monitor yet
                //so we should check.
                if(mon != null ){
                    mon.touchSession();
                    LOG.debug("Session last access time updated by " + request.getRequestURI() + " request.");
                }
            }
        } finally {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    public void destroy() {
    }
}
