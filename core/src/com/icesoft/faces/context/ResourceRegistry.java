package com.icesoft.faces.context;

import java.net.URI;

/**
 * I am a resource registry meant to be used by the component renderers to load
 * Javascript code, CSS rules, and also register any kind of resource the
 * component renderer needs (such as images, movies, flash...).
 */
public interface ResourceRegistry {

    /**
     * Register Javascript code to be served and load the code by inserting a
     * reference to it into the page. The code will be loaded only once, the
     * number of method invocations or number of component instances using this
     * registry are irrelevant.
     *
     * @param resource the Javascript code
     * @return the URI of the resource
     */
    URI loadJavascriptCode(Resource resource);

    /**
     * Register Javascript code to be served and load the code by inserting a
     * reference to it into the page. The code will be loaded only once, the
     * number of method invocations or number of component instances using this
     * registry are irrelevant.
     *
     * @param resource      the main Javascript code
     * @param linkerHandler handler used to specify any other resource relatively
     *                      referenced by the main resource
     * @return the URI of the resource
     */
    URI loadJavascriptCode(Resource resource, ResourceLinker.Handler linkerHandler);

    /**
     * Register CSS rules to be served and load the rules by inserting a
     * reference to them into the page. The rules will be loaded only once, the
     * number of method invocations or number of component instances using this
     * registry are irrelevant.
     *
     * @param resource the css rules
     * @return the URI of the resource
     */
    URI loadCSSRules(Resource resource);

    /**
     * Register CSS rules to be served and load the rules by inserting a
     * reference to them into the page. The rules will be loaded only once, the
     * number of method invocations or number of component instances using this
     * registry are irrelevant.
     *
     * @param resource      the main css rules
     * @param linkerHandler handler used to specify any other resource relatively
     *                      referenced by the main resource (such as '@import' rules)
     * @return the URI of the resource
     */
    URI loadCSSRules(Resource resource, ResourceLinker.Handler linkerHandler);

    /**
     * Register resource to be served. The URI is encoded using
     * {@link javax.faces.application.ViewHandler#getResourceURL(javax.faces.context.FacesContext,String)} so
     * that proper resolution is achieved when template subdirectories or
     * forwards are used.
     *
     * @param resource the resource
     * @return the URI of the resource
     */
    URI registerResource(Resource resource);

    /**
     * Register resource to be served. The URI is encoded using
     * {@link javax.faces.application.ViewHandler#getResourceURL(javax.faces.context.FacesContext,String)} so
     * that proper resolution is achieved when template subdirectories or
     * forwards are used.
     *
     * @param resource      the resource
     * @param linkerHandler handler used to specify any other resource relatively
     *                      referenced by the main resource (such as '@import' rules)
     * @return the URI of the resource
     */
    URI registerResource(Resource resource, ResourceLinker.Handler linkerHandler);

    //deprecated methods -----------------------------------------------------------------------------------------------

    /**
     * Register resource to be served. The URI is encoded using
     * {@link javax.faces.application.ViewHandler#getResourceURL(javax.faces.context.FacesContext,String)} so
     * that proper resolution is achieved when template subdirectories or
     * forwards are used.
     *
     * @param mimeType the mime-type of the resource
     * @param resource the resource
     * @return the URI of the resource
     * @deprecated use {@link ResourceRegistry#registerResource(Resource)} instead
     */
    URI registerResource(String mimeType, Resource resource);

    /**
     * Register resource to be served. The URI is encoded using
     * {@link javax.faces.application.ViewHandler#getResourceURL(javax.faces.context.FacesContext,String)} so
     * that proper resolution is achieved when template subdirectories or
     * forwards are used.
     *
     * @param mimeType      the mime-type of the resource
     * @param resource      the resource
     * @param linkerHandler handler used to specify any other resource relatively
     *                      referenced by the main resource (such as '@import' rules)
     * @return the URI of the resource
     * @deprecated use {@link ResourceRegistry#registerResource(Resource,  ResourceLinker.Handler)} instead
     */
    URI registerResource(String mimeType, Resource resource, ResourceLinker.Handler linkerHandler);

    /**
     * Register resource to be served. The URI is encoded using
     * {@link javax.faces.application.ViewHandler#getResourceURL(javax.faces.context.FacesContext,String)} so
     * that proper resolution is achieved when template subdirectories or
     * forwards are used.
     *
     * @param name     the name of the resource
     * @param resource the resource
     * @return the URI of the resource
     * @deprecated use {@link ResourceRegistry#registerResource(Resource)} instead
     */
    URI registerNamedResource(String name, Resource resource);

    /**
     * Register resource to be served. The URI is encoded using
     * {@link javax.faces.application.ViewHandler#getResourceURL(javax.faces.context.FacesContext,String)} so
     * that proper resolution is achieved when template subdirectories or
     * forwards are used.
     *
     * @param name          the name of the resource
     * @param resource      the resource
     * @param linkerHandler handler used to specify any other resource relatively
     *                      referenced by the main resource (such as '@import' rules)
     * @return the URI of the resource
     * @deprecated use {@link ResourceRegistry#registerResource(Resource,  ResourceLinker.Handler)} instead
     */
    URI registerNamedResource(String name, Resource resource, ResourceLinker.Handler linkerHandler);

    /**
     * Register resource to be served. The URI is *not* encoded by
     * {@link javax.faces.application.ViewHandler#getResourceURL(javax.faces.context.FacesContext,String)}. The returned
     * path starts from the web-application context path which is considered to be the root.
     *
     * @param resource the resource
     * @return the URI of the resource
     */
    URI registerResourceWithRelativePath(Resource resource);

    /**
     * Register resource to be served. The URI is *not* encoded by
     * {@link javax.faces.application.ViewHandler#getResourceURL(javax.faces.context.FacesContext,String)}. The returned
     * path starts from the web-application context path which is considered to be the root.
     *
     * @param resource      the resource
     * @param linkerHandler handler used to specify any other resource relatively
     *                      referenced by the main resource (such as '@import' rules)
     * @return the URI of the resource
     */
    URI registerResourceWithRelativePath(Resource resource, ResourceLinker.Handler linkerHandler);
}