package com.icesoft.faces.webapp.command;

import java.io.IOException;
import java.io.Writer;

public class NOOP extends AbstractCommand {

    public Command coalesceWithNext(Command command) {
        return command.coalesceWithPrevious(this);
    }

    public Command coalesceWithPrevious(Macro macro) {
        return macro;
    }

    public Command coalesceWithPrevious(UpdateElements updateElements) {
        return updateElements;
    }

    public Command coalesceWithPrevious(Redirect redirect) {
        return redirect;
    }

    public Command coalesceWithPrevious(Reload reload) {
        return reload;
    }

    public Command coalesceWithPrevious(SessionExpired sessionExpired) {
        return sessionExpired;
    }

    public Command coalesceWithPrevious(SetCookie setCookie) {
        return setCookie;
    }

    public Command coalesceWithPrevious(Pong pong) {
        return pong;
    }

    public Command coalesceWithPrevious(NOOP noop) {
        return noop;
    }

    public void serializeTo(Writer writer) throws IOException {
        writer.write("<noop/>");
    }
}
