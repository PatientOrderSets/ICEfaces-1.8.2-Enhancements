package com.icesoft.faces.context;

import org.w3c.dom.Document;

import java.io.IOException;

public interface DOMSerializer {

    void serialize(Document document) throws IOException;
}
