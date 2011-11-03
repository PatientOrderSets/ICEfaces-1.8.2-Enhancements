package com.icesoft.faces.webapp.command;

import java.io.IOException;
import java.io.Writer;

public class SessionExpired extends AbstractCommand {

    public Command coalesceWithNext(Command command) {
        return command.coalesceWithPrevious(this);
    }

    public Command coalesceWithPrevious(Redirect redirect) {
        return redirect;
    }

    public Command coalesceWithPrevious(Reload reload) {
        return this;
    }

    public Command coalesceWithPrevious(Macro macro) {
        return this;
    }

    public Command coalesceWithPrevious(UpdateElements updateElements) {
        return this;
    }

    public Command coalesceWithPrevious(SessionExpired sessionExpired) {
        return this;
    }

    public Command coalesceWithPrevious(SetCookie setCookie) {
        return this;
    }

    public Command coalesceWithPrevious(Pong pong) {
        return this;
    }

    public Command coalesceWithPrevious(NOOP noop) {
        return this;
    }

    public void serializeTo(Writer writer) throws IOException {
        writer.write("<session-expired/>");
    }
}
