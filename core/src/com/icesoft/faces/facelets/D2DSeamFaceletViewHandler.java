package com.icesoft.faces.facelets;

import com.icesoft.util.SeamUtilities;
import com.sun.facelets.compiler.Compiler;
import com.sun.facelets.compiler.SAXCompiler;
import com.sun.facelets.impl.ResourceResolver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.application.ViewHandler;
import javax.faces.context.FacesContext;
import java.util.Locale;

/**
 * @author ICEsoft Technologies, Inc.
 */
public class D2DSeamFaceletViewHandler extends D2DFaceletViewHandler {

    private static final String SEAM_EXPRESSION_FACTORY = "org.jboss.seam.ui.facelet.SeamExpressionFactory";

    // Log instance for this class
    private static Log log = LogFactory.getLog(D2DSeamFaceletViewHandler.class);

    public D2DSeamFaceletViewHandler(ViewHandler delegate) {
        super(delegate);
    }

    protected void faceletInitialize() {
        /*
        * If Seam1.2.1 or before then the SeamExpressionFactory exists in Seam so instantiate
        * the faceletFactory that way; otherwise, just initialize the compiler without any
        * options for Seam1.3.0.
        * Need SeamUtilities class to determine if we require SeamExpressionFactory
        *
        */
        try {
            if (faceletFactory == null) {
                com.sun.facelets.compiler.Compiler c = new SAXCompiler();
                if (SeamUtilities.requiresSeamExpressionFactory()) {
                    c.setFeature(Compiler.EXPRESSION_FACTORY, SEAM_EXPRESSION_FACTORY);
                } //else just initialize the compiler without any extra seam settings
                initializeCompiler(c);
                faceletFactory = createFaceletFactory(c);
            }
        }
        catch (Throwable t) {
            if (log.isDebugEnabled()) {
                log.debug("Failed initializing facelet instance", t);
            }
        }
    }

    /**
     * @see D2DFaceletViewHandler#preChainResourceResolver(com.sun.facelets.impl.ResourceResolver)
     */
    protected ResourceResolver preChainResourceResolver(ResourceResolver after) {
        ResourceResolver seamDebugResourceResolver =
                SeamDebugResourceResolver.build(after);
        if (seamDebugResourceResolver != null)
            return seamDebugResourceResolver;
        else
            return after;
    }

    public Locale calculateLocale(FacesContext facesContext) {
        return delegate.calculateLocale(facesContext);
    }
}
