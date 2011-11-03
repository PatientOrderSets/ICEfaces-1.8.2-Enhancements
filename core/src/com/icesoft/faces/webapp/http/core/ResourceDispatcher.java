package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.context.Resource;
import com.icesoft.faces.context.ResourceLinker;
import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.faces.webapp.http.common.MimeTypeMatcher;
import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.Response;
import com.icesoft.faces.webapp.http.common.ResponseHandler;
import com.icesoft.faces.webapp.http.common.Server;
import com.icesoft.faces.webapp.http.common.standard.CompressingServer;
import com.icesoft.faces.webapp.http.common.standard.PathDispatcherServer;
import com.icesoft.faces.webapp.http.servlet.SessionDispatcher;
import com.icesoft.util.encoding.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;

public class ResourceDispatcher implements Server {
    private static final Log log = LogFactory.getLog(ResourceDispatcher.class);
    private static final ResourceLinker.Handler NOOPHandler = new ResourceLinker.Handler() {
        public void linkWith(ResourceLinker linker) {
            //do nothing!
        }
    };
    private PathDispatcherServer dispatcher = new PathDispatcherServer();
    private Server compressResource;
    private MimeTypeMatcher mimeTypeMatcher;
    private String prefix;
    private ArrayList registered = new ArrayList();
    private SessionDispatcher.Monitor monitor;

    public ResourceDispatcher(String prefix, MimeTypeMatcher mimeTypeMatcher, SessionDispatcher.Monitor monitor, Configuration configuration) {
        this.prefix = prefix;
        this.mimeTypeMatcher = mimeTypeMatcher;
        this.monitor = monitor;
        this.compressResource = new CompressingServer(dispatcher, mimeTypeMatcher, configuration);
    }

    public void service(Request request) throws Exception {
        try {
            compressResource.service(request);
        } catch (IOException e) {
            //capture & log Tomcat specific exception
            if (e.getClass().getName().endsWith("ClientAbortException")) {
                log.debug("Browser closed the connection prematurely for " + request.getURI());
            } else {
                throw e;
            }
        }
    }

    public URI registerResource(Resource resource) {
        return registerResource(resource, NOOPHandler);
    }

    public URI registerResource(Resource resource, ResourceLinker.Handler handler) {
        if (handler == null)
            handler = NOOPHandler;
        final FileNameOption options = new FileNameOption();
        try {
            resource.withOptions(options);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String filename = options.getFileName();
        String dispatchFilename, uriFilename;
        if (filename == null || filename.trim().equals("")) {
            dispatchFilename = uriFilename = "";
        } else {
            dispatchFilename = convertToEscapedUnicode(filename);
            try {
                uriFilename = java.net.URLEncoder.encode(filename, "UTF-8").replaceAll("\\+", "%20");
            } catch (UnsupportedEncodingException e) {
                uriFilename = filename;
                e.printStackTrace();
            }
        }
        final String name = prefix + encode(resource) + "/";
        if (!registered.contains(name)) {
            registered.add(name);
            dispatcher.dispatchOn(".*" + name.replaceAll("\\/", "\\/") + dispatchFilename + "$", new ResourceServer(resource));
            if (handler != NOOPHandler) {
                handler.linkWith(new RelativeResourceLinker(name));
            }
        }

        return URI.create(name + uriFilename);
    }

    public void shutdown() {
        compressResource.shutdown();
        registered.clear();
    }

    private class ResourceServer implements Server, ResponseHandler {
        private final Date lastModified = new Date();
        private final ResponseHandler notModified = new ResponseHandler() {
            public void respond(Response response) throws Exception {
                response.setStatus(304);
                response.setHeader("ETag", encode(resource));
                response.setHeader("Date", new Date());
                response.setHeader("Last-Modified", lastModified);
                response.setHeader("Expires", monitor.expiresBy());
            }
        };
        private final Resource resource;

        public ResourceServer(Resource resource) {
            this.resource = resource;
        }

        public void service(Request request) throws Exception {
            try {
                Date modifiedSince = request.getHeaderAsDate("If-Modified-Since");
                if (lastModified.getTime() > modifiedSince.getTime() + 1000) {
                    request.respondWith(this);
                } else {
                    request.respondWith(notModified);
                }
            } catch (Exception e) {
                request.respondWith(this);
            }
        }

        public void respond(final Response response) throws Exception {
            ResourceOptions options = new ResourceOptions();
            resource.withOptions(options);
            if (options.mimeType == null && options.fileName != null) {
                options.mimeType = mimeTypeMatcher.mimeTypeFor(options.fileName);
            }
            response.setHeader("ETag", encode(resource));
            response.setHeader("Cache-Control", "public");
            response.setHeader("Content-Type", options.mimeType);
            response.setHeader("Last-Modified", options.lastModified);
            response.setHeader("Expires", options.expiresBy);
            if (options.attachement && options.contentDispositionFileName != null) {
                response.setHeader("Content-Disposition", "attachment; filename" + options.contentDispositionFileName);
            }
            InputStream inputStream = resource.open();
            if (inputStream == null) {
                throw new IOException("Resource of type " + resource.getClass().getName() + "[digest: " +
                        resource.calculateDigest() + "; mime-type: " + options.mimeType +
                        (options.attachement ? "; attachment: " + options.fileName : "") +
                        "] returned a null input stream.");
            } else {
                response.writeBodyFrom(inputStream);
            }
        }

        public void shutdown() {
        }

        private class ResourceOptions implements ExtendedResourceOptions {
            private Date lastModified = new Date();
            private Date expiresBy = monitor.expiresBy();
            private String mimeType;
            private String fileName;
            private boolean attachement;
            private String contentDispositionFileName;

            public void setMimeType(String type) {
                mimeType = type;
            }

            public void setLastModified(Date date) {
                lastModified = date;
            }

            public void setFileName(String name) {
                fileName = name;
            }

            public void setExpiresBy(Date date) {
                expiresBy = date;
            }

            public void setAsAttachement() {
                attachement = true;
            }
            // ICE-4342
            // Encoded filename in Content-Disposition header; to be used in save file dialog;
            // See http://greenbytes.de/tech/tc2231/
            public void setContentDispositionFileName(String contentDispositionFileName) {
                this.contentDispositionFileName = contentDispositionFileName;
            }
        }
    }

    private static String encode(Resource resource) {
        return Base64.encode(String.valueOf(resource.calculateDigest().hashCode()));
    }

    public static String convertToEscapedUnicode(String s) {
        char[] chars = s.toCharArray();
        String hexStr;
        StringBuffer stringBuffer = new StringBuffer(chars.length * 6);
        String[] leadingZeros = {"0000", "000", "00", "0", ""};
        for (int i = 0; i < chars.length; i++) {
            hexStr = Integer.toHexString(chars[i]).toUpperCase();
            stringBuffer.append("\\u");
            stringBuffer.append(leadingZeros[hexStr.length()]);
//            stringBuffer.append("0000".substring(0, 4 - hexStr.length()));
            stringBuffer.append(hexStr);
        }
        return stringBuffer.toString();
    }

    private class RelativeResourceLinker implements ResourceLinker {
        private final String name;

        public RelativeResourceLinker(String name) {
            this.name = name;
        }

        public void registerRelativeResource(String path, Resource relativeResource) {
            String pathExpression = (name + path).replaceAll("\\/", "\\/").replaceAll("\\.", "\\.");
            dispatcher.dispatchOn(".*" + pathExpression + "$", new ResourceServer(relativeResource));
        }
    }

    private class FileNameOption implements Resource.Options {
        private String fileName;

        public String getFileName() {
            return fileName;
        }

        public void setAsAttachement() {
        }

        public void setExpiresBy(Date date) {
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public void setLastModified(Date date) {
        }

        public void setMimeType(String mimeType) {
        }
    }

    public interface ExtendedResourceOptions extends Resource.Options {
        public void setContentDispositionFileName(String contentDispositionFileName);
    }
}
