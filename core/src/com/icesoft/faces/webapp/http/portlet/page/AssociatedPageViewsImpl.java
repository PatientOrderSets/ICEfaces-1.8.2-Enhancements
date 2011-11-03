package com.icesoft.faces.webapp.http.portlet.page;

import com.icesoft.faces.context.View;
import com.icesoft.faces.webapp.http.common.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Iterator;
import java.util.Set;

/**
 * This is the default implementation of the AssociatedPageViews interface.  It has logic for handling
 * all aspects of the associated views except for determining the portal page name/id.  Implementations
 * should extend this class and implement the getPageId() method with logic specific to the container.
 */
public abstract class AssociatedPageViewsImpl implements AssociatedPageViews {

    protected static final Log log = LogFactory.getLog(AssociatedPageViews.class);

    private ViewsPageBidiMap bidi = new ViewsPageBidiMap();
    private static Class impl;

    public abstract String getPageId();

    public static AssociatedPageViews getImplementation(Configuration config) {

        //First detect the default implementation to use for all further requests based
        //on an context parameter. If the parameter is not set, default to the noop
        //implementation.
        if (impl == null) {
            String implName = config.getAttribute(IMPLEMENTATION_KEY, NOOP_IMPLEMENTATION);
            try {
                impl = Class.forName(implName);
            } catch (ClassNotFoundException cnfe1) {

                //If an implementation was specified but it can't be loaded, then
                //just use the noop implementation.
                if (log.isWarnEnabled()) {
                    log.warn("could not load " + implName);
                }
                impl = NoOpAssociatedPageViews.class;
            }
            if (log.isInfoEnabled()) {
                log.info("using " + impl.getName());
            }
        }

        try {
            Object inst = impl.newInstance();
            return (AssociatedPageViews) inst;
        } catch (Exception e) {
            if (log.isWarnEnabled()) {
                log.warn("could not create an instance of " + impl.getName(), e);
            }
            return new NoOpAssociatedPageViews();
        }
    }

    public void add(View view) {
        String pageId = getPageId();
        bidi.put(pageId, view);
    }

    public void disposeAssociatedViews(View view) {
        Set associatedViews = bidi.getAssociatedViews(view);
        Iterator views = associatedViews.iterator();
        while (views.hasNext()) {
            View v = (View) views.next();
            v.dispose();
            if (log.isDebugEnabled()) {
                log.debug("disposed " + v.toString());
            }
        }
        bidi.clear();
    }
}

