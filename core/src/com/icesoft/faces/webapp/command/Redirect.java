package com.icesoft.faces.webapp.command;

import com.icesoft.faces.util.DOMUtils;

import java.io.IOException;
import java.io.Writer;
import java.net.URI;

public class Redirect extends AbstractCommand {
    private URI uri;

    public Redirect(URI uri) {
        this.uri = uri;
    }

    public Redirect(String uri) {
        this.uri = URI.create(uri);
    }

    public Command coalesceWithNext(Command command) {
        return command.coalesceWithPrevious(this);
    }

    public Command coalesceWithPrevious(Macro macro) {
        macro.addCommand(this);
        return macro;
    }

    public Command coalesceWithPrevious(UpdateElements updateElements) {
        return this;
    }

    public Command coalesceWithPrevious(Redirect redirect) {
        return this;
    }

    public Command coalesceWithPrevious(Reload reload) {
        return this;
    }

    public Command coalesceWithPrevious(SessionExpired sessionExpired) {
        return this;
    }

    public Command coalesceWithPrevious(SetCookie setCookie) {
        Macro macro = new Macro();
        macro.addCommand(this);
        macro.addCommand(setCookie);
        return macro;
    }

    public Command coalesceWithPrevious(Pong pong) {
        return this;
    }

    public Command coalesceWithPrevious(NOOP noop) {
        return this;
    }

    public void serializeTo(Writer writer) throws IOException {
        writer.write("<redirect url=\"" + DOMUtils.escapeAnsi(uri.toString()) + "\"/>");
    }
}
