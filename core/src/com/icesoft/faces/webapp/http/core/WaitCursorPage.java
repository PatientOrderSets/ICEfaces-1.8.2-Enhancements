package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.Response;
import com.icesoft.faces.webapp.http.common.ResponseHandler;
import com.icesoft.faces.webapp.http.common.Server;

import java.io.PrintStream;

public class WaitCursorPage implements Server, ResponseHandler {

    public void service(Request request) throws Exception {
        request.respondWith(this);
    }

    public void respond(Response response) throws Exception {
        response.setHeader("Content-Type", "text/html");
        new PrintStream(response.writeBody()).println("<html><head><title>This page intentionally left blank</title></head><body style=\"background-color: white; cursor: wait;\"></body></html>");
    }

    public void shutdown() {
    }
}