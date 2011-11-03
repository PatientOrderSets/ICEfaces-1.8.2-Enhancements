package com.icesoft.faces.context;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.Arrays;

public class ByteArrayResource implements Resource, Serializable {
    private final Date lastModified;
    private byte[] content;

    public ByteArrayResource(byte[] content) {
        this.content = content;
        this.lastModified = new Date();
    }

    public String calculateDigest() {
        return String.valueOf(content);
    }

    public Date lastModified() {
        return lastModified;
    }

    public InputStream open() throws IOException {
        return new ByteArrayInputStream(content);
    }

    public void withOptions(Options options) throws IOException {
        //no options
    }
    
    public boolean equals(Object obj) {
        if (!(obj instanceof ByteArrayResource)) {
            return false;
        }
        ByteArrayResource bar = (ByteArrayResource) obj;
        if (!lastModified.equals(bar.lastModified))
            return false;
        if (!Arrays.equals(content, bar.content)) {
            return false;
        }
        return true;
    }
}
