package com.icesoft.faces.webapp.http.common.standard;

import com.icesoft.faces.webapp.http.common.Response;
import com.icesoft.faces.webapp.http.common.ResponseHandler;

public class OKResponse implements ResponseHandler {
    public static final ResponseHandler Handler = new OKResponse();

    public void respond(Response response) throws Exception {
        response.setStatus(200);
    }
}
