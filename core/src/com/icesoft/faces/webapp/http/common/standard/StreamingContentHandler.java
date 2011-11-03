package com.icesoft.faces.webapp.http.common.standard;

import com.icesoft.faces.webapp.http.common.Response;
import com.icesoft.faces.webapp.http.common.ResponseHandler;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

public abstract class StreamingContentHandler implements ResponseHandler {
    private String mimeType;
    private String characterSet;

    protected StreamingContentHandler(String mimeType, String characterSet) {
        this.mimeType = mimeType;
        this.characterSet = characterSet;
    }

    public abstract void writeTo(Writer writer) throws IOException;

    public void respond(Response response) throws Exception {
        response.setHeader("Content-Type", mimeType + "; charset=" + characterSet);
        OutputStreamWriter writer = new OutputStreamWriter(response.writeBody(), characterSet);
        writeTo(writer);
        writer.flush();
    }
}
