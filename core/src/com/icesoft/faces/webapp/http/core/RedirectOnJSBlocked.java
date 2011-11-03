package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.Response;
import com.icesoft.faces.webapp.http.common.ResponseHandler;
import com.icesoft.faces.webapp.http.common.Server;

import java.io.PrintStream;

public class RedirectOnJSBlocked implements Server {
    private ResponseHandler handler;

    public RedirectOnJSBlocked(Configuration configuration) {
        try {
            final String redirectURI = configuration.getAttribute("javascriptBlockedRedirectURI");
            handler = new ResponseHandler() {
                public void respond(Response response) {
                    response.setStatus(307);
                    response.setHeader("Location", redirectURI);
                }
            };
        } catch (Exception e) {
            handler = new ResponseHandler() {
                public void respond(Response response) throws Exception {
                    response.setStatus(403);
                    PrintStream stream = new PrintStream(response.writeBody());
                    stream.println("Javascript is blocked. ICEfaces cannot run.");
                }
            };
        }
    }

    public void service(final Request request) throws Exception {
        request.respondWith(handler);
    }

    public void shutdown() {
    }
}
