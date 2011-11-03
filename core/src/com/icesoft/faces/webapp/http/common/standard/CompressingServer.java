package com.icesoft.faces.webapp.http.common.standard;

import com.icesoft.faces.webapp.http.common.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public class CompressingServer implements Server {
    private Server server;
    private MimeTypeMatcher mimeTypeMatcher;
    private boolean compressResources;
    private List noCompressForMimeTypes;

    public CompressingServer(Server server, MimeTypeMatcher mimeTypeMatcher, Configuration configuration) {
        this.server = server;
        this.mimeTypeMatcher = mimeTypeMatcher;
        this.compressResources = configuration.getAttributeAsBoolean("compressResources", true);
        this.noCompressForMimeTypes = Arrays.asList(configuration.getAttribute("compressResourcesExclusions",
                "image/gif image/png image/jpeg image/tiff " +
                        "application/pdf application/zip application/x-compress application/x-gzip application/java-archive " +
                        "video/x-sgi-movie audio/x-mpeg video/mp4 video/mpeg"
        ).split(" "));
    }

    public void service(Request request) throws Exception {
        if (compressResources) {
            String mimeType = mimeTypeMatcher.mimeTypeFor(request.getURI().getPath());
            if (noCompressForMimeTypes.contains(mimeType)) {
                server.service(request);
            } else {
                String acceptEncodingHeader = request.getHeader("Accept-Encoding");
                if (acceptEncodingHeader != null && (acceptEncodingHeader.indexOf("gzip") >= 0 || acceptEncodingHeader.indexOf("compress") >= 0)) {
                    server.service(new CompressingRequest(request));
                } else {
                    server.service(request);
                }
            }
        } else {
            server.service(request);
        }
    }

    public void shutdown() {
        server.shutdown();
    }

    private class CompressingRequest extends RequestProxy {
        public CompressingRequest(Request request) {
            super(request);
        }

        public void respondWith(final ResponseHandler handler) throws Exception {
            request.respondWith(new ResponseHandler() {
                public void respond(Response response) throws Exception {
                    CompressingResponse compressingResponse = new CompressingResponse(response);
                    handler.respond(compressingResponse);
                    compressingResponse.finishCompression();
                }
            });
        }
    }

    private class CompressingResponse extends ResponseProxy {
        private GZIPOutputStream output;

        public CompressingResponse(Response response) {
            super(response);
            response.setHeader("Content-Encoding", "gzip");
        }

        public OutputStream writeBody() throws IOException {
            return output = new GZIPOutputStream(response.writeBody());
        }

        public void writeBodyFrom(InputStream in) throws IOException {
            try {
                copy(in, writeBody());
            } finally {
                in.close();
            }
        }

        public void finishCompression() throws IOException {
            if (output != null) {
                output.finish();
            }
        }
    }

    private static void copy(InputStream input, OutputStream output) throws IOException {
        byte[] buf = new byte[4096];
        int len = 0;
        while ((len = input.read(buf)) > -1) output.write(buf, 0, len);
    }
}
