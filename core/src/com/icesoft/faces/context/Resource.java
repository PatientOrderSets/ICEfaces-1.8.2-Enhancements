package com.icesoft.faces.context;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * Resource represents a handle to a read-only stream of bytes.
 */
public interface Resource {

    /**
     * Calculate a digest that uniquely identifies the content of the resource.
     *
     * @return the digest
     */
    String calculateDigest();

    /**
     * Open reading stream.
     *
     * @return the stream
     */
    InputStream open() throws IOException;

    /**
     * Return timestamp when resource was last updated or created.
     *
     * @return the timestamp
     * @deprecated use {@link Resource.Options#setLastModified} instead
     */
    Date lastModified();

    /**
     * Set additional options for resource downloading.
     *
     * @param options
     * @throws IOException
     */
    void withOptions(Options options) throws IOException;

    /**
     * Callback for setting optional download information.
     */
    interface Options {

        void setMimeType(String mimeType);

        void setLastModified(Date date);

        void setFileName(String fileName);

        void setExpiresBy(Date date);

        void setAsAttachement();
    }
}
