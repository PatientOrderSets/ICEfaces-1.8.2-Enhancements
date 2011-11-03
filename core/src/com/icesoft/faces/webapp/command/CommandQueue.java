package com.icesoft.faces.webapp.command;

public interface CommandQueue {

    void put(Command command);

    Command take();
}
