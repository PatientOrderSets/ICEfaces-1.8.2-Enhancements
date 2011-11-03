package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.webapp.command.Command;
import com.icesoft.faces.webapp.command.Reload;
import com.icesoft.faces.webapp.http.common.standard.FixedXMLContentHandler;

import java.io.IOException;
import java.io.Writer;

public class ReloadResponse extends FixedXMLContentHandler {
    private final Command reloadCommand;

    public ReloadResponse(String viewIdentifier) {
        reloadCommand = new Reload(viewIdentifier);
    }

    public void writeTo(Writer writer) throws IOException {
        reloadCommand.serializeTo(writer);
    }
}
