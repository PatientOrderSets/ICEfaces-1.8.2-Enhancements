package com.icesoft.faces.webapp.http.core;

import edu.emory.mathcs.backport.java.util.concurrent.ConcurrentLinkedQueue;

public class ViewQueue extends ConcurrentLinkedQueue {

    private Runnable listener;

    public void onPut(Runnable listener) {
        this.listener = listener;
    }

    public void put(Object object) throws InterruptedException {
        if (!contains(object)) {
            super.add(object);
        }
        listener.run();
    }
}
