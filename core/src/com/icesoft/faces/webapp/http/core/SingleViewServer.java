package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.context.View;
import com.icesoft.faces.env.Authorization;
import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.Server;
import com.icesoft.faces.webapp.http.servlet.SessionDispatcher;

import javax.servlet.http.HttpSession;
import java.util.Map;

public class SingleViewServer implements Server {
    private static final String viewNumber = "1";
    private Map views;
    private String sessionID;
    private ViewQueue allUpdatedViews;
    private Configuration configuration;
    private SessionDispatcher.Monitor sessionMonitor;
    private HttpSession session;
    private ResourceDispatcher resourceDispatcher;
    private String blockingRequestHandlerContext;
    private Authorization authorization;

    public SingleViewServer(final HttpSession session, final String sessionID, final SessionDispatcher.Monitor sessionMonitor, final Map views, final ViewQueue allUpdatedViews, final Configuration configuration, final ResourceDispatcher resourceDispatcher, final String blockingRequestHandlerContext, final Authorization authorization) {
        this.session = session;
        this.sessionID = sessionID;
        this.sessionMonitor = sessionMonitor;
        this.views = views;
        this.allUpdatedViews = allUpdatedViews;
        this.configuration = configuration;
        this.resourceDispatcher = resourceDispatcher;
        this.blockingRequestHandlerContext = blockingRequestHandlerContext;
        this.authorization = authorization;
    }

    //synchronize to avoid concurrent state modifications of the single View
    public void service(Request request) throws Exception {
        //create single view or re-create view if the request is the result of a redirect
        final View view;
        synchronized (views) {
            if (views.containsKey(viewNumber)) {
                view = (View) views.get(viewNumber);
            } else {
                view = new View(viewNumber, sessionID, session, allUpdatedViews, configuration, sessionMonitor, resourceDispatcher, blockingRequestHandlerContext, authorization);
                views.put(viewNumber, view);
            }
        }

        try {
            sessionMonitor.touchSession();
            view.servePage(request);
        } finally {
            view.release();
        }
    }

    public void shutdown() {
    }
}
