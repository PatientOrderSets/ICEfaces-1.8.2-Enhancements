package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.webapp.http.common.standard.EmptyResponse;
import com.icesoft.faces.webapp.http.common.standard.ResponseHandlerServer;
import com.icesoft.faces.webapp.http.core.SessionExpiredResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SessionVerifier implements PseudoServlet {
    private static final Log log = LogFactory.getLog(SessionVerifier.class);
    private static final PseudoServlet SessionExpiredServlet = new BasicAdaptingServlet(new ResponseHandlerServer(SessionExpiredResponse.Handler));
    private static final PseudoServlet EmptyResponseServlet = new BasicAdaptingServlet(new ResponseHandlerServer(EmptyResponse.Handler));
    private PseudoServlet servlet;
    private boolean xmlResponse;

    public SessionVerifier(PseudoServlet servlet, boolean xmlResponse) {
        this.servlet = servlet;
        this.xmlResponse = xmlResponse;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (request.isRequestedSessionIdValid()) {
            servlet.service(request, response);
        } else {
            if (xmlResponse) {
                SessionExpiredServlet.service(request, response);
            } else {
                log.debug("Request for " + request.getRequestURI() + " belongs to an expired session. Dropping connection...");
                EmptyResponseServlet.service(request, response);
            }
        }
    }

    public void shutdown() {
        servlet.shutdown();
    }
}
