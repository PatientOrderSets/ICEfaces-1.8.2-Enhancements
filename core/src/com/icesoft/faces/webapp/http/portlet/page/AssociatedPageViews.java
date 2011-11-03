package com.icesoft.faces.webapp.http.portlet.page;

import com.icesoft.faces.context.View;

/**
 * In a portlet environment, there can be multiple views on a single page.  It's generally desirable that, when
 * one view is disposed due to navigation or closing a window/tab that all the associated views of that portal
 * page are also disposed.  To do that, we need to track all the views associated with the page. However, since
 * there is no specified way to determine which page a portlet/view is on, the strategy for finding out are
 * specific to the container.
 * <p/>
 * To support this feature in a portal container, you should do the following:
 * <p/>
 * <ul>
 * <li>write a class that extends AssociatedPageViewsImpl (an abstract class that implements this interface)</li>
 * <li>implement the getPageId() method with the logic required</li>
 * <li>activate your implementation by specifying a context parameter that has the fully qualified name of
 * the implemention as the value</li>
 * <p/>
 * </ul>
 * <p/>
 * e.g.
 * <p/>
 * <pre>
 * &lt;context-param&gt;
 *     &lt;param-name&gt;com.icesoft.faces.portlet.associatedPageViewsImpl&lt;/param-name&gt;
 *     &lt;param-value&gt;com.icesoft.faces.webapp.http.portlet.page.JBossAssociatedPageViews&lt;/param-value&gt;
 * &lt;/context-param&gt;
 * </pre>
 * <p/>
 * The ICEfaces core framework will attempt to use the specified implementation to track associated views and, when
 * one of the views on the page is disposed, all associated views will be disposed as well.
 * <p/>
 * Note: This interface and the other members of this package are not currently considered officially supported APIs.
 * They are aimed at providing an extension point for adding portal container specific functionality without adding
 * compile or runtime dependencies on portal specific libraries.
 */
public interface AssociatedPageViews {

    /**
     * Context parameter name to use when specifying an implementation of this interface.
     */
    static final String IMPLEMENTATION_KEY = "portlet.associatedPageViewsImpl";

    /**
     * Class name for the noop implemenation of this interface.
     */
    static final String NOOP_IMPLEMENTATION = "com.icesoft.faces.webapp.http.portlet.page.NoOpAssociatedPageViews";

    /**
     * The implementation of this method will typically use portal implementation specific logic to determine the
     * unique page name/id that the current portlet resides in.
     *
     * @return The unique name or id of the page that the portlet resides in.
     */
    public String getPageId();

    /**
     * Associates the the specified view to the current page. Implementations should rely on the superclass
     * (AssociatedPageViewsImpl) to handle this.
     *
     * @param view The view to associate with the page.
     */
    public void add(View view);

    /**
     * Disposes the specified view as well as all other views associated with the same page. Implementations should
     * rely on the superclass (AssociatedPageViewsImpl) to handle this.
     *
     * @param view The view to dispose.
     */
    public void disposeAssociatedViews(View view);

}
