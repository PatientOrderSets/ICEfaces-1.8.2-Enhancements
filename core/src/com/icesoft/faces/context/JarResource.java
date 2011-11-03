package com.icesoft.faces.context;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class JarResource implements Resource {
    private final Date lastModified = new Date();
    private String path;

    public JarResource(String path) {
        this.path = path;
    }

    public String calculateDigest() {
        return path;
    }

    public Date lastModified() {
        return lastModified;
    }

    public InputStream open() throws IOException {
        return this.getClass().getClassLoader().getResourceAsStream(path);
    }

    public void withOptions(Options options) throws IOException {
        options.setFileName(path);
    }
}
