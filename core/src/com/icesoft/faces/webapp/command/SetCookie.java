package com.icesoft.faces.webapp.command;

import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class SetCookie extends AbstractCommand {
    private final static DateFormat CookieDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss z");

    static {
        CookieDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    private Cookie cookie;

    public SetCookie(Cookie cookie) {
        this.cookie = cookie;
    }

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
        Macro macro = new Macro();
        macro.addCommand(this);
        macro.addCommand(redirect);
        return macro;
    }

    public Command coalesceWithPrevious(Reload reload) {
        Macro macro = new Macro();
        macro.addCommand(this);
        macro.addCommand(reload);
        return macro;
    }

    public Command coalesceWithPrevious(SessionExpired sessionExpired) {
        return sessionExpired;
    }

    public Command coalesceWithPrevious(SetCookie setCookie) {
        if (setCookie.cookie.getName().equals(cookie.getName())) {
            return this;
        } else {
            Macro macro = new Macro();
            macro.addCommand(setCookie);
            macro.addCommand(this);
            return macro;
        }
    }

    public Command coalesceWithPrevious(Pong pong) {
        Macro macro = new Macro();
        macro.addCommand(this);
        macro.addCommand(pong);
        return macro;
    }

    public Command coalesceWithPrevious(NOOP noop) {
        return this;
    }

    public void serializeTo(Writer writer) throws IOException {
        writer.write("<set-cookie>");
        writer.write(cookie.getName());
        writer.write("=");
        writer.write(cookie.getValue());
        writer.write("; ");
        int maxAge = cookie.getMaxAge();
        if (maxAge >= 0) {
            Date expiryDate = new Date(System.currentTimeMillis() + maxAge * 1000l);
            writer.write("expires=");
            writer.write(CookieDateFormat.format(expiryDate));
            writer.write("; ");
        }
        String path = cookie.getPath();
        if (path != null) {
            writer.write("path=");
            writer.write(path);
            writer.write("; ");
        }
        String domain = cookie.getDomain();
        if (domain != null) {
            writer.write("domain=");
            writer.write(domain);
            writer.write("; ");
        }
        if (cookie.getSecure()) {
            writer.write("secure;");
        }
        writer.write("</set-cookie>");
    }
}
