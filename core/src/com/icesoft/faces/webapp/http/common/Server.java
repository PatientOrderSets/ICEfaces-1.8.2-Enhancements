package com.icesoft.faces.webapp.http.common;

public interface Server {

    void service(Request request) throws Exception;

    void shutdown();
}
