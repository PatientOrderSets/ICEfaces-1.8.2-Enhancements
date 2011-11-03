package com.icesoft.faces.webapp.http.common.standard;

import com.icesoft.faces.webapp.http.common.ResponseHandler;
import com.icesoft.faces.webapp.http.common.Response;

public class NoCacheContentHandler implements ResponseHandler {
    private String mimeType;
    private String characterSet;

    public NoCacheContentHandler(String mimeType, String characterSet) {
        this.mimeType = mimeType;
        this.characterSet = characterSet;
    }

    public void respond(Response response) throws Exception {
        response.setHeader("Cache-Control", new String[]{"no-cache", "no-store", "must-revalidate"});//HTTP 1.1
        response.setHeader("Pragma", "no-cache");//HTTP 1.0
        response.setHeader("Expires", 0);//prevents proxy caching
        response.setHeader("Content-Type", mimeType + "; charset=" + characterSet);
    }
}
