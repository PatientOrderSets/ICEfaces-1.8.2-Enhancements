package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.Response;
import com.icesoft.faces.webapp.http.common.ResponseHandler;
import com.icesoft.faces.webapp.http.common.Server;
import com.icesoft.faces.webapp.http.common.standard.NotFoundHandler;

import java.io.InputStream;

public class ServeJSCode implements Server {
    private static final String Package = "com/icesoft/faces/webapp/xmlhttp/";
    private ClassLoader loader;

    public ServeJSCode() {
        loader = this.getClass().getClassLoader();
    }

    public void service(Request request) throws Exception {
        String path = request.getURI().getPath();
        String file = path.substring(path.lastIndexOf("/") + 1, path.length());
        final InputStream in = loader.getResourceAsStream(Package + file);

        if (in == null) {
            request.respondWith(new NotFoundHandler("Cannot find JS file for " + path));
        } else {
            request.respondWith(new ResponseHandler() {
                public void respond(Response response) throws Exception {
                    response.setHeader("Content-Type", "text/javascript");
                    response.writeBodyFrom(in);
                }
            });
        }
    }

    public void shutdown() {
    }
}
