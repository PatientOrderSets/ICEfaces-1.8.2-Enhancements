package com.icesoft.faces.webapp.http.common.standard;

import com.icesoft.faces.webapp.http.common.Response;

import java.io.IOException;
import java.io.Writer;

public class NotFoundHandler extends StreamingContentHandler {
    private String message;

    public NotFoundHandler(String message) {
        super("text/plain", "UTF-8");
        this.message = message;
    }

    public void writeTo(Writer writer) throws IOException {
        writer.write(message);
    }

    public void respond(Response response) throws Exception {
        response.setStatus(404);
        super.respond(response);
    }
}
