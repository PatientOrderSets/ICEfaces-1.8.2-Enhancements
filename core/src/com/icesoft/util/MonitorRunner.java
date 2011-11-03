package com.icesoft.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class MonitorRunner {
    private static final Log log = LogFactory.getLog(MonitorRunner.class);
    private Collection monitors = new ArrayList();
    private boolean run = true;

    public MonitorRunner(final long interval) {
        try {
            Thread thread = new Thread("Monitor Runner") {
                public void run() {
                    while (run) {
                        try {
                            Thread.sleep(interval);
                            Iterator i = new ArrayList(monitors).iterator();
                            while (i.hasNext()) {
                                Runnable monitor = (Runnable) i.next();
                                try {
                                    monitor.run();
                                } catch (Throwable t) {
                                    log.warn("Failed to run monitor: " + monitor);
                                }
                            }
                        } catch (InterruptedException e) {
                            //do nothing
                        }
                    }
                }
            };
            thread.setDaemon(true);
            thread.start();
        } catch (Exception e)  {
            log.error("Unable to initialize Monitor Runner ", e);
        }
    }

    public void registerMonitor(Runnable monitor) {
        monitors.add(monitor);
    }

    public void unregisterMonitor(Runnable monitor) {
        monitors.remove(monitor);
    }

    public void stop() {
        run = false;
    }
}
