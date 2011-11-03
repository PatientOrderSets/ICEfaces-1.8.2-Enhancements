package com.icesoft.faces.webapp.http.common.standard;

import com.icesoft.faces.webapp.http.common.Response;
import com.icesoft.faces.webapp.http.common.ResponseHandler;

public class EmptyResponse {

    public static final ResponseHandler Handler = new ResponseHandler() {
        public void respond(Response response) throws Exception {
            response.setHeader("Content-Length", 0);
        }
    };
}
