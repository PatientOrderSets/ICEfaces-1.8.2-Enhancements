package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.webapp.http.common.FileLocator;
import com.icesoft.faces.webapp.http.common.MimeTypeMatcher;
import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.Response;
import com.icesoft.faces.webapp.http.common.ResponseHandler;
import com.icesoft.faces.webapp.http.common.Server;
import com.icesoft.faces.webapp.http.common.standard.NotFoundHandler;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;

public class FileServer implements Server {
    private FileLocator locator;
    private MimeTypeMatcher mimeTypeMatcher;

    public FileServer(FileLocator locator, MimeTypeMatcher mimeTypeMatcher) {
        this.locator = locator;
        this.mimeTypeMatcher = mimeTypeMatcher;
    }

    public void service(Request request) throws Exception {
        final String path = request.getURI().getPath();
        final File file = locator.locate(path);
        if (file.exists()) {
            request.respondWith(new ResponseHandler() {
                public void respond(Response response) throws Exception {
                    String mimeType = mimeTypeMatcher.mimeTypeFor(path);
                    Date lastModified = new Date(file.lastModified());

                    response.setHeader("Content-Type", mimeType);
                    response.setHeader("Last-Modified", lastModified);
                    response.writeBodyFrom(new FileInputStream(file));
                }
            });
        } else {
            request.respondWith(new NotFoundHandler("Could not find file at " + path));
        }
    }

    public void shutdown() {
    }
}
