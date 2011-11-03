package com.icesoft.faces.webapp.http.common.standard;

import com.icesoft.faces.webapp.http.common.Response;
import com.icesoft.faces.webapp.http.common.ResponseHandler;

import java.util.Date;

public class NotModifiedHandler implements ResponseHandler {
    private Date expirationDate;

    public NotModifiedHandler(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void respond(Response response) throws Exception {
        response.setStatus(304);
        response.setHeader("Date", new Date());
        response.setHeader("Expires", expirationDate);
        response.setHeader("Content-Length", 0);
    }
}
