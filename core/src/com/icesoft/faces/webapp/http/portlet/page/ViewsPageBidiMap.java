package com.icesoft.faces.webapp.http.portlet.page;

import com.icesoft.faces.context.View;

import java.util.*;

/**
 * This is a specific utility class for maintaining a bidirection relationship
 * between views and the pages that they reside on. This optimizes our ability
 * to get all the views for a particular page when we want to dispose of them.
 */
class ViewsPageBidiMap {

    private Map viewsOnPage = new HashMap();
    private Map pageForView = new WeakHashMap();

    public ViewsPageBidiMap() {
    }

    public void put(String pageId, View view) {
        Object viewMapObj = viewsOnPage.get(pageId);
        if (viewMapObj == null) {
            WeakHashMap viewMap = new WeakHashMap();
            viewMap.put(view, null);
            viewsOnPage.put(pageId, viewMap);
        } else {
            WeakHashMap viewMap = (WeakHashMap) viewMapObj;
            viewMap.put(view, null);
        }
        pageForView.put(view, pageId);
    }

    public Set getAssociatedViews(View view) {
        Object pageId = pageForView.get(view);
        if (pageId == null) {
            return Collections.EMPTY_SET;
        }

        Object associatedViewsObj = viewsOnPage.get(pageId);
        if (associatedViewsObj == null) {
            return Collections.EMPTY_SET;
        }

        WeakHashMap associatedViews = (WeakHashMap) associatedViewsObj;
        return associatedViews.keySet();
    }

    public void clear() {
        viewsOnPage.clear();
        pageForView.clear();
    }
}
