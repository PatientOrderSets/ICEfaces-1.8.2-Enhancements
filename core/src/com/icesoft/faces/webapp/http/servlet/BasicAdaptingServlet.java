package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.webapp.http.common.Server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BasicAdaptingServlet implements PseudoServlet {
    private Server server;

    public BasicAdaptingServlet(Server server) {
        this.server = server;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
        server.service(new ServletRequestResponse(request, response));
    }

    public void shutdown() {
        server.shutdown();
    }
}
