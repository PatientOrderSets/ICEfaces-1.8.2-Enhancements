package com.icesoft.faces.context;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class StringResource implements Resource {
    private final Date lastModified = new Date();
    private String content;
    private String encoding;

    public StringResource(String content) {
        this(content, "UTF-8");
    }

    public StringResource(String content, String encoding) {
        this.content = content;
        this.encoding = encoding;
    }

    public String calculateDigest() {
        return content;
    }

    public Date lastModified() {
        return lastModified;
    }

    public InputStream open() throws IOException {
        return new ByteArrayInputStream(content.getBytes(encoding));
    }

    public void withOptions(Options options) throws IOException {
        options.setMimeType("text/plain; encoding=" + encoding);
    }
}
