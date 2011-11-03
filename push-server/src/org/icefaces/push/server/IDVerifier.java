package org.icefaces.push.server;

import com.icesoft.faces.webapp.http.common.standard.NotFoundHandler;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IDVerifier
extends AbstractHandler
implements Handler {
    private static final Log LOG = LogFactory.getLog(IDVerifier.class);

    private Set iceFacesIdSet;

    public IDVerifier(final Set iceFacesIdSet, final Handler handler) {
        super(handler);
        this.iceFacesIdSet = iceFacesIdSet;
    }

    public void run() {
        if (iceFacesIdSet.isEmpty()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("404 Not Found (ICEfaces ID(s))");
            }
            try {
                request.respondWith(new NotFoundHandler(""));
            } catch (Exception exception) {
                if (LOG.isErrorEnabled()) {
                    LOG.error(
                        "An error occurred " +
                            "while trying to responde with: 404 Not Found!",
                        exception);
                }
            }
        } else {
            handler.handle();
        }
    }
}
