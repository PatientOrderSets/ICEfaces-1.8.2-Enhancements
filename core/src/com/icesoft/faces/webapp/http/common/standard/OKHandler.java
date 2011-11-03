package com.icesoft.faces.webapp.http.common.standard;

import com.icesoft.faces.webapp.http.common.Response;
import com.icesoft.faces.webapp.http.common.ResponseHandler;

public class OKHandler implements ResponseHandler {
    public static final ResponseHandler HANDLER = new OKHandler();

    public void respond(Response response) throws Exception {
        response.setStatus(200);
    }
}
