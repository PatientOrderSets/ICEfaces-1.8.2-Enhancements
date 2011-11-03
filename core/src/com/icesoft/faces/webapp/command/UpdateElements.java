package com.icesoft.faces.webapp.command;

import com.icesoft.faces.util.DOMUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

public class UpdateElements extends AbstractCommand {
    private final static Pattern START_CDATA = Pattern.compile("<\\!\\[CDATA\\[");
    private final static Pattern END_CDATA = Pattern.compile("\\]\\]>");
    private Element[] updates;

    public UpdateElements(Element[] updates) {
        this.updates = updates;
    }

    public Command coalesceWithPrevious(UpdateElements updateElementsCommand) {
        ArrayList coallescedUpdates = new ArrayList();
        Element[] previousUpdates = updateElementsCommand.updates;

        for (int i = 0; i < previousUpdates.length; i++) {
            Element previousUpdate = previousUpdates[i];
            boolean overriden = false;
            //test if any of the new updates is replacing the same element
            for (int j = 0; j < updates.length; j++) {
                Element update = updates[j];
                if (update.getAttribute("id").equals(previousUpdate.getAttribute("id"))) {
                    overriden = true; break;
                }
            }
            //drop overriden updates
            if (!overriden) {
                coallescedUpdates.add(previousUpdate);
            }
        }
        coallescedUpdates.addAll(Arrays.asList(updates));

        return new UpdateElements((Element[]) coallescedUpdates.toArray(new Element[coallescedUpdates.size()]));
    }

    public Command coalesceWithNext(Command command) {
        return command.coalesceWithPrevious(this);
    }

    public Command coalesceWithPrevious(Macro macro) {
        macro.addCommand(this);
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
        writer.write("<updates>");
        for (int i = 0; i < updates.length; i++) {
            Element update = updates[i];
            if (update == null) continue;
            writer.write("<update address=\"");
            writer.write(update.getAttribute("id"));
            writer.write("\" tag=\"" + update.getTagName() + "\">");

            NamedNodeMap attributes = update.getAttributes();
            for (int j = 0; j < attributes.getLength(); j++) {
                Attr attribute = (Attr) attributes.item(j);
                writer.write("<attribute name=\"");
                writer.write(attribute.getName());
                String value = attribute.getValue();
                if ("".equals(value)) {
                    writer.write("\"/>");
                } else {
                    writer.write("\"><![CDATA[");
                    writer.write(DOMUtils.escapeAnsi(value));
                    writer.write("]]></attribute>");
                }
            }

            String content = DOMUtils.childrenToString(update);
            if ("".equals(content)) {
                writer.write("<content/>");
            } else {
                writer.write("<content><![CDATA[");
                content = START_CDATA.matcher(content).replaceAll("<!#cdata#");
                content = END_CDATA.matcher(content).replaceAll("##>");
                writer.write(content);
                writer.write("]]></content>");
            }
            writer.write("</update>");
        }
        writer.write("</updates>");
    }
}
