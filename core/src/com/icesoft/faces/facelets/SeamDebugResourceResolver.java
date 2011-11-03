package com.icesoft.faces.facelets;

import java.io.IOException;
import java.net.URL;
import java.lang.reflect.Method;

import javax.faces.context.FacesContext;

import com.sun.facelets.impl.ResourceResolver;
import com.sun.facelets.impl.DefaultResourceResolver;
import com.icesoft.util.SeamUtilities;

/**
 * Intercepts any request for a path like /debug.xxx and renders
 * the Seam debug page using facelets.
 *
 * @author Mark Collette
 */
public class SeamDebugResourceResolver implements ResourceResolver {
    private static final String Init_className =
        "org.jboss.seam.core.Init";
    private static final String SeamDebugPhaseListener_className =
        "org.jboss.seam.debug.jsf.SeamDebugPhaseListener";
    private static Class Init_class;
    private static Method Init_instance_method;
    private static Method Init_isDebug_method;
    private static Class SeamDebugPhaseListener_class;
    private static boolean loaded = false;
    
    public static ResourceResolver build(ResourceResolver delegate) {
        if( delegate == null ) {
            throw new IllegalArgumentException(
                "SeamDebugResourceResolver must have valid delegate ResourceResolver");
        }
        if( !loadSeamDebugClasses() )
            return null;
        return new SeamDebugResourceResolver( delegate );
    }
    
    private static boolean loadSeamDebugClasses() {
        if( !loaded ) {
            try {
                ClassLoader dbgClassLoader =
                    SeamUtilities.getSeamDebugPhaseListenerClassLoader();
                if( dbgClassLoader == null ) {
                    dbgClassLoader =
                        Thread.currentThread().getContextClassLoader();
                }
                Init_class = Class.forName(
                    Init_className, true, dbgClassLoader);
                Init_instance_method = Init_class.getMethod(
                    "instance", new Class[0]);
                Init_isDebug_method = Init_class.getMethod(
                    "isDebug", new Class[0]);
                SeamDebugPhaseListener_class = Class.forName(
                    SeamDebugPhaseListener_className, true, dbgClassLoader);
                loaded = true;
            }
            catch(Exception e) {
//e.printStackTrace();
                // Silently fail, since it's valid to not be in Seam,
                //  or not have the Seam debug JAR available
                Init_class = null;
                Init_instance_method = null;
                Init_isDebug_method = null;
                SeamDebugPhaseListener_class = null;
                loaded = false;
            }
        }
        return loaded;
    }

    private ResourceResolver delegate;

    private SeamDebugResourceResolver(ResourceResolver delegate) {
        this.delegate = delegate;
    }

    public URL resolveUrl(String path) {
//System.out.println("SeamDebugResourceResolver.resolveUrl()  path: " + path);
        if ( path!=null && path.startsWith("/debug.") && Init_instance_isDebug() )
        {
            URL url = SeamDebugPhaseListener_class.getClassLoader().getResource("META-INF/debug.xhtml");
//System.out.println("SeamDebugResourceResolver.resolveUrl()  url: " + url);
            return url;
        }
        return delegate.resolveUrl(path);
    }
    
    private static boolean Init_instance_isDebug() {
        try {
            Object instance = Init_instance_method.invoke(null, new Object[0]);
            Object isDebug = Init_isDebug_method.invoke(instance, new Object[0]);
            return ((Boolean) isDebug).booleanValue();
        }
        catch(Exception e) {
//e.printStackTrace();
            return false;
        }
    }
}
