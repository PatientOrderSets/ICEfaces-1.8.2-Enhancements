package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.webapp.command.Command;
import com.icesoft.faces.webapp.http.common.Response;
import com.icesoft.faces.webapp.http.common.ResponseHandler;
import com.icesoft.faces.webapp.http.common.standard.FixedXMLContentHandler;

import java.io.IOException;
import java.io.Writer;

public class SessionExpiredResponse {
    private static final Command SessionExpiredCommand = new com.icesoft.faces.webapp.command.SessionExpired();

    public static final FixedXMLContentHandler Handler = new FixedXMLContentHandler() {
        public void writeTo(Writer writer) throws IOException {
            SessionExpiredCommand.serializeTo(writer);
        }
    };
    
    public static ResponseHandler getRedirectingHandler(final String redirectURI)  {
        ResponseHandler handler = new ResponseHandler() {
            public void respond(Response response) throws Exception {
                response.setStatus(302);
                response.setHeader("Location", redirectURI);
            }
        };
        return handler;
    }
}
