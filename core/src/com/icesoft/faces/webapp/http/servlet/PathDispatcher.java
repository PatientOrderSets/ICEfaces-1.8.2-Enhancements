package com.icesoft.faces.webapp.http.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Pattern;

public class PathDispatcher implements PseudoServlet {
    private List matchers = new ArrayList();
    private List servlets = new ArrayList();

    public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = request.getRequestURI();
        ListIterator i = matchers.listIterator();
        while (i.hasNext()) {
            int index = i.nextIndex();
            Pattern pattern = (Pattern) i.next();
            if (pattern.matcher(path).find()) {
                PseudoServlet server = (PseudoServlet) servlets.get(index);
                server.service(request, response);
                return;
            }
        }

        response.sendError(404, "Could not find resource at " + path);
    }

    public void dispatchOn(String pathExpression, PseudoServlet toServlet) {
        matchers.add(Pattern.compile(pathExpression));
        servlets.add(toServlet);
    }

    public void shutdown() {
        Iterator i = servlets.iterator();
        while (i.hasNext()) {
            PseudoServlet servlet = (PseudoServlet) i.next();
            servlet.shutdown();
        }
    }
}
