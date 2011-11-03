package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.webapp.http.common.ResponseHandler;
import com.icesoft.faces.webapp.http.common.Server;
import edu.emory.mathcs.backport.java.util.concurrent.Semaphore;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ThreadBlockingAdaptingServlet implements PseudoServlet {
    private static final Log LOG = LogFactory.getLog(ThreadBlockingAdaptingServlet.class);
    private static final int TIMEOUT = 10; // minutes

    private Server server;

    public ThreadBlockingAdaptingServlet(Server server) {
        this.server = server;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ThreadBlockingRequestResponse requestResponse = new ThreadBlockingRequestResponse(request, response);
        server.service(requestResponse);
        requestResponse.blockUntilRespond();
    }

    public void shutdown() {
        server.shutdown();
    }

    private class ThreadBlockingRequestResponse extends ServletRequestResponse {
        private final Semaphore semaphore;

        public ThreadBlockingRequestResponse(HttpServletRequest request, HttpServletResponse response) throws Exception {
            super(request, response);
            semaphore = new Semaphore(1);
            //Acquire semaphore hoping to have it released by a call to respondWith() method.
            semaphore.acquire();
        }

        public void respondWith(final ResponseHandler handler) throws Exception {
            try {
                super.respondWith(handler);
            } finally {
                semaphore.release();
            }
        }

        public void blockUntilRespond() throws InterruptedException {
            //Block thread by trying to acquire the semaphore a second time.
            boolean acquired = semaphore.tryAcquire(TIMEOUT, TimeUnit.MINUTES);
            if (acquired) {
                //Release the semaphore previously acquired.
                semaphore.release();
            } else {
                LOG.error("No response sent to " +
                        "request '" + request.getRequestURI() + "' " +
                        "with ICEfaces ID '" +
                        request.getParameter("ice.session") + "' " +
                        "from " + request.getRemoteAddr() + " " +
                        "in " + TIMEOUT + " minutes.  " +
                        "Unblocking " +
                        "thread '" + Thread.currentThread().getName() + "'.");
                //Release the semaphore; most probably respondWith() method was not invoked.
                semaphore.release();
            }
        }
    }
}
