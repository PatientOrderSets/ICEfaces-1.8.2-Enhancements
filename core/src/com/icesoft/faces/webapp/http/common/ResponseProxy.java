package com.icesoft.faces.webapp.http.common;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import javax.servlet.http.Cookie;

public class ResponseProxy implements Response {
    protected Response response;

    public ResponseProxy(Response response) {
        this.response = response;
    }

    public void addCookie(final Cookie cookie) {
        response.addCookie(cookie);
    }

    public void setStatus(int code) {
        response.setStatus(code);
    }

    public void setHeader(String name, String value) {
        response.setHeader(name, value);
    }

    public void setHeader(String name, String[] values) {
        response.setHeader(name, values);
    }

    public void setHeader(String name, Date value) {
        response.setHeader(name, value);
    }

    public void setHeader(String name, int value) {
        response.setHeader(name, value);
    }

    public void setHeader(String name, long value) {
        response.setHeader(name, value);
    }

    public OutputStream writeBody() throws IOException {
        return response.writeBody();
    }

    public void writeBodyFrom(InputStream in) throws IOException {
        response.writeBodyFrom(in);
    }
}
