package com.icesoft.faces.webapp.http.core;

public class SessionExpiredException extends RuntimeException {
    private static final String Message = "User session has expired or it was invalidated.";

    public SessionExpiredException() {
        super(Message);
    }

    public SessionExpiredException(Throwable throwable) {
        super(Message, throwable);
    }
}
