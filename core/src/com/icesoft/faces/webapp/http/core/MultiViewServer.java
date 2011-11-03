package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.context.View;
import com.icesoft.faces.env.Authorization;
import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.Server;
import com.icesoft.faces.webapp.http.servlet.SessionDispatcher;
import com.icesoft.faces.webapp.http.portlet.page.AssociatedPageViews;

import javax.servlet.http.HttpSession;
import java.util.Map;

public class MultiViewServer implements Server {
    private int viewCount = 0;
    private Map views;
    private ViewQueue asynchronouslyUpdatedViews;
    private String sessionID;
    private Configuration configuration;
    private SessionDispatcher.Monitor sessionMonitor;
    private HttpSession session;
    private ResourceDispatcher resourceDispatcher;
    private String blockingRequestHandlerContext;
    private Authorization authorization;
    private AssociatedPageViews associatedPageViews;

    public MultiViewServer(final HttpSession session,
                           final String sessionID,
                           final SessionDispatcher.Monitor sessionMonitor,
                           final Map views,
                           final ViewQueue asynchronouslyUpdatedViews,
                           final Configuration configuration,
                           final ResourceDispatcher resourceDispatcher,
                           final String blockingRequestHandlerContext,
                           final Authorization authorization,
                           AssociatedPageViews associatedPageViews) {
        this.session = session;
        this.sessionID = sessionID;
        this.sessionMonitor = sessionMonitor;
        this.views = views;
        this.asynchronouslyUpdatedViews = asynchronouslyUpdatedViews;
        this.configuration = configuration;
        this.resourceDispatcher = resourceDispatcher;
        this.blockingRequestHandlerContext = blockingRequestHandlerContext;
        this.authorization = authorization;
        this.associatedPageViews = associatedPageViews;
    }

    public void service(Request request) throws Exception {
        //extract viewNumber if this request is from a redirect
        final View view;
        synchronized (views) {
            if (request.containsParameter("rvn")) {
                final String viewIdentifier = request.getParameter("rvn");
                if (isInteger(viewIdentifier) && views.containsKey(viewIdentifier)) {
                    view = (View) views.get(viewIdentifier);
                } else {
                    view = createView();
                }
            } else {
                view = createView();
            }
        }

        try {
            sessionMonitor.touchSession();
            view.servePage(request);
        } finally {
            view.release();
        }
    }

    private View createView() throws Exception {
        String viewNumber = String.valueOf(++viewCount);
        View view = new View(viewNumber, sessionID, session, asynchronouslyUpdatedViews, configuration, sessionMonitor, resourceDispatcher, blockingRequestHandlerContext, authorization);
        associatedPageViews.add(view);
        views.put(viewNumber, view);
        return view;
    }

    public void shutdown() {
    }

    private static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
