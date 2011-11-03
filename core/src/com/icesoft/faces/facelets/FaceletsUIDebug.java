package com.icesoft.faces.facelets;

import com.icesoft.faces.webapp.http.common.Request;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * With stock Facelets, the debug page request is intercepted in the 
 * FaceletViewHandler, which is probably not as efficient of a place 
 * to handle things as possible, since this mechanism has no need for  
 * a JSF lifecycle. So, with ICEfaces, we intercept the request from 
 * our servlet, completely outside of JSF.
 * 
 * @author mcollette
 * @since 1.8.1
 */
public class FaceletsUIDebug {
    public static boolean handleRequest(HttpSession session, Request request)
            throws Exception {
        final String KEY = "facelets.ui.DebugOutput";
        if (request.containsParameter(KEY)) {
            String id = request.getParameter(KEY);
            if (id != null && id.length() > 0) {
                if (session != null) {
                    Map debugs = (Map) session.getAttribute(KEY);
                    if (debugs != null) {
                        Object faceletsDebug = debugs.get(id);
                        if (faceletsDebug != null) {
                            request.respondWith( new com.icesoft.faces.webapp.http.common.standard.StringContentHandler("text/html", "UTF-8", faceletsDebug.toString()) );
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
