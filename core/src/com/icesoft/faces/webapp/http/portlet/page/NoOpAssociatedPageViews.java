package com.icesoft.faces.webapp.http.portlet.page;

import com.icesoft.faces.context.View;

/**
 * The noop version of AssociatedViews has empty implementations of the
 * methods.  It is used in environments where associated views are not
 * an issue or when there is no specific implementation for that
 * environment.
 */
public class NoOpAssociatedPageViews implements AssociatedPageViews {

    public String getPageId() {
        return null;
    }

    public void add(View view) {
    }

    public void disposeAssociatedViews(View view) {
    }
}
