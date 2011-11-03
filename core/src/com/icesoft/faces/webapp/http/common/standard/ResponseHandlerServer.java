package com.icesoft.faces.webapp.http.common.standard;

import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.ResponseHandler;
import com.icesoft.faces.webapp.http.common.Server;

public class ResponseHandlerServer implements Server {
    private ResponseHandler handler;

    public ResponseHandlerServer(ResponseHandler handler) {
        this.handler = handler;
    }

    public void service(Request request) throws Exception {
        request.respondWith(handler);
    }

    public void shutdown() {
        //do nothing
    }
}
