package com.icesoft.faces.webapp.http.common.standard;

import com.icesoft.faces.webapp.http.common.Response;
import com.icesoft.faces.webapp.http.common.ResponseHandler;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public abstract class FixedSizeContentHandler implements ResponseHandler {
    private String mimeType;
    private String characterSet;

    protected FixedSizeContentHandler(String mimeType, String characterSet) {
        this.mimeType = mimeType;
        this.characterSet = characterSet;
    }

    public abstract void writeTo(Writer writer) throws IOException;

    public void respond(Response response) throws Exception {
        StringWriter writer = new StringWriter();
        writeTo(writer);
        writer.write("\n\n");
        writer.flush();
        byte[] content = writer.getBuffer().toString().getBytes(characterSet);
        response.setHeader("Content-Type", mimeType + "; charset=" + characterSet);
        response.setHeader("Content-Length", content.length);
        response.writeBody().write(content);
    }
}
