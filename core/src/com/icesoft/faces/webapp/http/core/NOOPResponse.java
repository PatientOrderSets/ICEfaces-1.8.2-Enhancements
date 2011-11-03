package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.webapp.http.common.ResponseHandler;
import com.icesoft.faces.webapp.http.common.standard.FixedXMLContentHandler;

import java.io.IOException;
import java.io.Writer;

public class NOOPResponse {
    private static final com.icesoft.faces.webapp.command.NOOP NOOPCommand = new com.icesoft.faces.webapp.command.NOOP();

    public static final ResponseHandler Handler = new FixedXMLContentHandler() {
        public void writeTo(Writer writer) throws IOException {
            NOOPCommand.serializeTo(writer);
        }
    };
}
