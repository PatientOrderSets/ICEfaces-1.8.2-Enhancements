package com.icesoft.faces.webapp.http.common;

public class ServerProxy implements Server {
    protected Server server;

    public ServerProxy(Server server) {
        this.server = server;
    }

    public void service(Request request) throws Exception {
        server.service(request);
    }

    public void shutdown() {
        server.shutdown();
    }
}
