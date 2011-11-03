package com.icesoft.faces.webapp.http.common.standard;

import com.icesoft.faces.webapp.http.common.Response;

public abstract class FixedXMLContentHandler extends FixedSizeContentHandler {

    protected FixedXMLContentHandler() {
        super("text/xml", "UTF-8");
    }

    public void respond(Response response) throws Exception {
        response.setHeader("Cache-Control", new String[]{"no-cache", "no-store", "must-revalidate"});//HTTP 1.1
        response.setHeader("Pragma", "no-cache");//HTTP 1.0
        response.setHeader("Expires", 0);//prevents proxy caching
        super.respond(response);
    }
}
