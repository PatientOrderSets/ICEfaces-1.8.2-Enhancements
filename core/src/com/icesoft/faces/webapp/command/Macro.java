package com.icesoft.faces.webapp.command;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;

class Macro extends AbstractCommand {
    //see http://krijnhoetmer.nl/stuff/javascript/maximum-cookies/
    private static final int MaxNumberOfCookies = 50;
    private Command updateElements;
    private Command pong;
    private Command redirect;
    private Command reload;
    private ArrayList setCookies = new ArrayList();

    public Command coalesceWithNext(Command command) {
        return command.coalesceWithPrevious(this);
    }

    public Command coalesceWithPrevious(UpdateElements updateElements) {
        throw new IllegalStateException("Macro commands are constructed only as result of coalescing ");
    }

    public Command coalesceWithPrevious(Redirect redirect) {
        throw new IllegalStateException("Macro commands are constructed only as result of coalescing ");
    }

    public Command coalesceWithPrevious(Reload reload) {
        throw new IllegalStateException("Macro commands are constructed only as result of coalescing ");
    }

    public Command coalesceWithPrevious(SessionExpired sessionExpired) {
        throw new IllegalStateException("Macro commands are constructed only as result of coalescing ");
    }

    public Command coalesceWithPrevious(Macro macro) {
        throw new IllegalStateException("Macro commands are constructed only as result of coalescing ");
    }

    public Command coalesceWithPrevious(SetCookie setCookie) {
        throw new IllegalStateException("Macro commands are constructed only as result of coalescing ");
    }

    public Command coalesceWithPrevious(Pong pong) {
        throw new IllegalStateException("Macro commands are constructed only as result of coalescing ");
    }

    public Command coalesceWithPrevious(NOOP noop) {
        throw new IllegalStateException("Macro commands are constructed only as result of coalescing ");
    }

    public void addCommand(UpdateElements updateElements) {
        if (redirect == null && reload == null) {
            this.updateElements = this.updateElements == null ? updateElements : this.updateElements.coalesceWithNext(updateElements);
        }
    }

    public void addCommand(Pong pong) {
        this.pong = pong;
    }

    public void addCommand(SetCookie setCookie) {
        if (setCookies.size() > MaxNumberOfCookies) {
            setCookies.remove(0);
        }
        setCookies.add(setCookie);
    }

    public void addCommand(Redirect redirect) {
        this.redirect = redirect;
        this.reload = null;
        this.pong = null;
        this.updateElements = null;
    }

    public void addCommand(Reload reload) {
        if (redirect == null) {
            this.reload = reload;
            this.pong = null;
            this.updateElements = null;
        }
    }

    public void serializeTo(Writer writer) throws IOException {
        writer.write("<macro>");
        if (updateElements != null) updateElements.serializeTo(writer);
        if (pong != null) pong.serializeTo(writer);
        if (redirect != null) redirect.serializeTo(writer);
        if (reload != null) reload.serializeTo(writer);
        Iterator i = setCookies.iterator();
        while (i.hasNext()) {
            ((Command) i.next()).serializeTo(writer);
        }
        writer.write("</macro>");
    }
}
