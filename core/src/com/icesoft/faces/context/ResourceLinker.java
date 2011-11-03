package com.icesoft.faces.context;

public interface ResourceLinker {

    void registerRelativeResource(String path, Resource resource);

    public interface Handler {

        void linkWith(ResourceLinker linker);
    }
}