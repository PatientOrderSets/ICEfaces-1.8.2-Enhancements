package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.context.View;

import javax.servlet.http.HttpSession;

public class InterceptingServletSession extends ProxyHttpSession {
    private final SessionDispatcher.Monitor sessionMonitor;
    private final View view;

    public InterceptingServletSession(HttpSession session, SessionDispatcher.Monitor sessionMonitor, View view) {
        super(session);
        this.sessionMonitor = sessionMonitor;
        this.view = view;
    }

    public void invalidate() {
        //invalidate session right away!
        //see ICE-2731 -- delaying session invalidation doesn't work since JBoss+Catalina resuses session objects and
        //IDs which causes a lot of confusion in applications that have logout processes (invalidate session and
        //immediately initiate new session)
        view.onRelease(new Runnable() {
            public void run() {
                sessionMonitor.shutdown();
            }
        });
    }
}
