package com.icesoft.faces.webapp.command;

import java.io.IOException;
import java.io.Writer;

public interface Command {

    Command coalesceWithNext(Command command);

    Command coalesceWithPrevious(Macro macro);

    Command coalesceWithPrevious(UpdateElements updateElements);

    Command coalesceWithPrevious(Redirect redirect);

    Command coalesceWithPrevious(Reload reload);

    Command coalesceWithPrevious(SessionExpired sessionExpired);

    Command coalesceWithPrevious(SetCookie setCookie);

    Command coalesceWithPrevious(Pong pong);

    Command coalesceWithPrevious(NOOP noop);

    void serializeTo(Writer writer) throws IOException;
}
