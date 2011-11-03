package com.icesoft.faces.webapp.http.common.standard;

import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.Server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Pattern;

public class PathDispatcherServer implements Server {
    private List matchers = new ArrayList();
    private List servers = new ArrayList();

    public void service(Request request) throws Exception {
        String path = request.getURI().getPath();
        ListIterator i = new ArrayList(matchers).listIterator();
        while (i.hasNext()) {
            int index = i.nextIndex();
            Pattern pattern = (Pattern) i.next();
            if (pattern.matcher(path).find()) {
                Server server = (Server) servers.get(index);
                server.service(request);
                return;
            }
        }

        request.respondWith(new NotFoundHandler("Could not find resource at " + path));
    }

    public void dispatchOn(String pathExpression, final Server toServer) {
        matchers.add(Pattern.compile(pathExpression));
        servers.add(toServer);
    }

    public void shutdown() {
        Iterator i = servers.iterator();
        while (i.hasNext()) {
            Server server = (Server) i.next();
            server.shutdown();
        }
    }
}
