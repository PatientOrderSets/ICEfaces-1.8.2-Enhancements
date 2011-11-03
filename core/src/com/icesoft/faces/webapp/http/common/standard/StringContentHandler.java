package com.icesoft.faces.webapp.http.common.standard;

import com.icesoft.faces.webapp.http.common.Response;
import com.icesoft.faces.webapp.http.common.ResponseHandler;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class StringContentHandler extends StreamingContentHandler {
    protected String toWrite;
    
    public StringContentHandler(String mimeType, String characterSet, String toWrite) {
        super(mimeType, characterSet);
        this.toWrite = toWrite;
    }

    public void writeTo(Writer writer) throws IOException {
        if (toWrite != null) {
            writer.write(toWrite);
        }
    }
}
