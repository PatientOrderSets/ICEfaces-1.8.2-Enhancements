package com.icesoft.faces.webapp.http.servlet;

import javax.servlet.ServletException;

public class EnvironmentAdaptingException extends ServletException {
    public EnvironmentAdaptingException() {
        super();
    }

    public EnvironmentAdaptingException(final String message) {
        super(message);
    }

    public EnvironmentAdaptingException(final String message, final Throwable rootCause) {
        super(message, rootCause);
    }

    public EnvironmentAdaptingException(final Throwable rootCause) {
        super(rootCause);
    }
}
