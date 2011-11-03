package com.icesoft.faces.webapp.http.common;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import javax.servlet.http.Cookie;

public interface Response {

    void addCookie(Cookie cookie);
    
    void setStatus(int code);

    void setHeader(String name, String value);

    void setHeader(String name, String[] values);

    void setHeader(String name, Date value);

    void setHeader(String name, int value);

    void setHeader(String name, long value);

    OutputStream writeBody() throws IOException;

    void writeBodyFrom(InputStream in) throws IOException;
}
