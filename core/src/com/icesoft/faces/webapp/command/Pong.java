package com.icesoft.faces.webapp.command;

import java.io.IOException;
import java.io.Writer;

public class Pong extends AbstractCommand {

    public Command coalesceWithNext(Command command) {
        return command.coalesceWithPrevious(this);
    }

    public Command coalesceWithPrevious(Macro macro) {
        macro.addCommand(this);
        return macro;
    }

    public Command coalesceWithPrevious(UpdateElements updateElements) {
        Macro macro = new Macro();
        macro.addCommand(this);
        macro.addCommand(updateElements);
        return macro;
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
        Macro macro = new Macro();
        macro.addCommand(this);
        macro.addCommand(setCookie);
        return macro;
    }

    public Command coalesceWithPrevious(NOOP noop) {
        return this;
    }

    public Command coalesceWithPrevious(Pong pong) {
        return pong;
    }

    public void serializeTo(Writer writer) throws IOException {
        writer.write("<pong/>");
    }
}
