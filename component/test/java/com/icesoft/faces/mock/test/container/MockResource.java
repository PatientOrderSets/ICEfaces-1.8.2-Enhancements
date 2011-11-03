/*
 * Mock
 */

package com.icesoft.faces.mock.test.container;

import com.icesoft.faces.context.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 *
 * @author fye
 */
public class MockResource implements Resource {

    public String calculateDigest() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public InputStream open() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Date lastModified() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void withOptions(Options options) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
