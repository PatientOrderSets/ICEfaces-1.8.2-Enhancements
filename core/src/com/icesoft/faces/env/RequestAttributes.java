package com.icesoft.faces.env;

import java.util.Enumeration;

public interface RequestAttributes {

    Enumeration getAttributeNames();

    Object getAttribute(String name);

    void setAttribute(String name, Object value);

    void removeAttribute(String name);
}
