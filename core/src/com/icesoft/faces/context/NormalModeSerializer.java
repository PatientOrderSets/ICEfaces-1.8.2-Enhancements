package com.icesoft.faces.context;

import com.icesoft.faces.util.DOMUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public class NormalModeSerializer implements DOMSerializer {
    private final static Log log = LogFactory.getLog(NormalModeSerializer.class);
    private BridgeFacesContext context;
    private Writer writer;

    public NormalModeSerializer(BridgeFacesContext context, Writer writer) {
        this.context = context;
        this.writer = writer;
    }

    public void serialize(Document document) throws IOException {
        try {
            if (context.isContentIncluded()) {
                if (log.isDebugEnabled()) {
                    log.debug("treating request as a fragment");
                }

                Node body = DOMUtils.getChildByNodeName(document.getDocumentElement(), "body");
                if (null != body) {
                    //insert a containing element for bridge anchoring
                    writer.write("<div>\n");
                    DOMUtils.printChildNodes(body, writer);
                    writer.write("</div>\n");
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("treating request as a whole page (not a fragment)");
                }
                Map requestMap = context.getExternalContext().getRequestMap();
                String publicID =
                        (String) requestMap.get(DOMResponseWriter.DOCTYPE_PUBLIC);
                String systemID =
                        (String) requestMap.get(DOMResponseWriter.DOCTYPE_SYSTEM);
                String root =
                        (String) requestMap.get(DOMResponseWriter.DOCTYPE_ROOT);
                String output =
                        (String) requestMap.get(DOMResponseWriter.DOCTYPE_OUTPUT);
                boolean prettyPrinting =
                        Boolean.valueOf((String) requestMap
                                .get(DOMResponseWriter.DOCTYPE_PRETTY_PRINTING))
                                .booleanValue();

                //todo: replace this with a complete new implementation that doesn't rely on xslt but can serialize xml, xhtml, and html.
                if (output == null || ("html".equals(output) && !prettyPrinting)) {
                    if (publicID != null && systemID != null && root != null) {
                        writer.write(DOMUtils.DocumentTypetoString(publicID, systemID,
                                root));
                    }
                    DOMUtils.printNode(document, writer);
                } else {
                    //use a serializer. not as performant.
                    JAXPSerializer serializer = new JAXPSerializer(writer, publicID, systemID);
                    if ("xml".equals(output)) {
                        serializer.outputAsXML();
                    } else {
                        serializer.outputAsHTML();
                    }
                    if (prettyPrinting) {
                        serializer.printPretty();
                    }
                    serializer.serialize(document);
                }
            }

            writer.flush();
        } catch (IOException e) {
            //capture & log Tomcat specific exception
            if (e.getClass().getName().endsWith("ClientAbortException")) {
                log.debug("Browser closed the connection prematurely.");
            } else {
                throw e;
            }
        }
    }
}