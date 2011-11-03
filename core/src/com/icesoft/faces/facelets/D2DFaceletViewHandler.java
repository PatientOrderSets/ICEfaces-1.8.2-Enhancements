/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * "The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations under
 * the License.
 *
 * The Original Code is ICEfaces 1.5 open source software code, released
 * November 5, 2006. The Initial Developer of the Original Code is ICEsoft
 * Technologies Canada, Corp. Portions created by ICEsoft are Copyright (C)
 * 2004-2006 ICEsoft Technologies Canada, Corp. All Rights Reserved.
 *
 * Contributor(s): _____________________.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"
 * License), in which case the provisions of the LGPL License are
 * applicable instead of those above. If you wish to allow use of your
 * version of this file only under the terms of the LGPL License and not to
 * allow others to use your version of this file under the MPL, indicate
 * your decision by deleting the provisions above and replace them with
 * the notice and other provisions required by the LGPL License. If you do
 * not delete the provisions above, a recipient may use your version of
 * this file under either the MPL or the LGPL License."
 *
 */

package com.icesoft.faces.facelets;

import com.icesoft.faces.application.D2DViewHandler;
import com.icesoft.faces.context.BridgeFacesContext;
import com.sun.facelets.Facelet;
import com.sun.facelets.FaceletFactory;
import com.sun.facelets.compiler.Compiler;
import com.sun.facelets.compiler.SAXCompiler;
import com.sun.facelets.compiler.TagLibraryConfig;
import com.sun.facelets.impl.DefaultFaceletFactory;
import com.sun.facelets.impl.DefaultResourceResolver;
import com.sun.facelets.impl.ResourceResolver;
import com.sun.facelets.tag.TagDecorator;
import com.sun.facelets.tag.TagLibrary;
import com.sun.facelets.tag.jsf.ComponentSupport;
import com.sun.facelets.util.ReflectionUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * <B>D2DViewHandler</B> is the ICEfaces Facelet ViewHandler implementation
 *
 * @see javax.faces.application.ViewHandler
 */
public class D2DFaceletViewHandler extends D2DViewHandler {

    //Facelets parameter constants
    public final static long DEFAULT_REFRESH_PERIOD = 2;
    public final static String PARAM_REFRESH_PERIOD = "facelets.REFRESH_PERIOD";
    public final static String PARAM_SKIP_COMMENTS = "facelets.SKIP_COMMENTS";
    public final static String PARAM_VIEW_MAPPINGS = "facelets.VIEW_MAPPINGS";
    public final static String PARAM_LIBRARIES = "facelets.LIBRARIES";
    public final static String PARAM_DECORATORS = "facelets.DECORATORS";
    public final static String PARAM_RESOURCE_RESOLVER =
            "facelets.RESOURCE_RESOLVER";

    // Log instance for this class
    private static Log log = LogFactory.getLog(D2DFaceletViewHandler.class);

    protected FaceletFactory faceletFactory;

    public D2DFaceletViewHandler() {
    }

    public D2DFaceletViewHandler(ViewHandler delegate) {
        super(delegate);
    }

    protected void faceletInitialize() {
        try {
            if (faceletFactory == null) {
                com.sun.facelets.compiler.Compiler c = new SAXCompiler();
                initializeCompiler(c);
                faceletFactory = createFaceletFactory(c);
            }
        }
        catch (Throwable t) {
            if (log.isErrorEnabled()) {
                log.error("Failed initializing facelet instance", t);
            }
        }
    }


    protected void initializeCompiler(Compiler c) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        ExternalContext ext = ctx.getExternalContext();

        // Use a TagLibrary to create UIXhtmlComponents from all xhtml Tags
        c.addTagLibrary(new UIXhtmlTagLibrary());
        c.addTagDecorator(new UIXhtmlTagDecorator());

        c.addTagDecorator(new JspTagDetector());

        // Load libraries
        String paramLibraries = ext.getInitParameter(PARAM_LIBRARIES);
        if (paramLibraries != null) {
            paramLibraries = paramLibraries.trim();
            String[] paramLibrariesArray = paramLibraries.split(";");
            for (int i = 0; i < paramLibrariesArray.length; i++) {
                try {
                    URL url = ext.getResource(paramLibrariesArray[i]);
                    if (url == null) {
                        throw new FileNotFoundException(paramLibrariesArray[i]);
                    }
                    TagLibrary tagLibrary = TagLibraryConfig.create(url);
                    c.addTagLibrary(tagLibrary);
                    if (log.isDebugEnabled()) {
                        log.debug("Loaded library: " + paramLibrariesArray[i]);
                    }
                }
                catch (IOException e) {
                    if (log.isWarnEnabled()) {
                        log.warn("Problem loading library: " + paramLibrariesArray[i], e);
                    }
                }
            }
        }

        // Load decorators
        String paramDecorators = ext.getInitParameter(PARAM_DECORATORS);
        if (paramDecorators != null) {
            paramDecorators = paramDecorators.trim();
            String[] paramDecoratorsArray = paramDecorators.split(";");
            for (int i = 0; i < paramDecoratorsArray.length; i++) {
                try {
                    Class tagDecoratorClass = ReflectionUtil.forName(paramDecoratorsArray[i]);
                    TagDecorator tagDecorator = (TagDecorator)
                            tagDecoratorClass.newInstance();
                    c.addTagDecorator(tagDecorator);
                    if (log.isDebugEnabled()) {
                        log.debug("Loaded decorator: " +
                                paramDecoratorsArray[i]);
                    }
                }
                catch (Exception e) {
                    if (log.isWarnEnabled()) {
                        log.warn("Problem loading decorator: " +
                                paramDecoratorsArray[i], e);
                    }
                }
            }
        }

        // Load whether to skip comments or not. For our hierarchial
        //  UIComponent tree, we have to throw away most useless text nodes,
        //  so this is a bit redundant getting the parameter. But who knows,
        //  things might change later, so best to preserve this code.
        String paramSkipComments =
                ext.getInitParameter(PARAM_SKIP_COMMENTS);
        // Default is true.  I think this behaviour has changed over time
        //  is stock Facelets builds from 1.0.x to 1.1.x
        if (paramSkipComments != null && paramSkipComments.equals("false")) {
            c.setTrimmingComments(false);
        }

        // This has to be true, otherwise table or other container
        //   UIComponents will have text children, when they're
        //   expecting only real UIComponents
        c.setTrimmingWhitespace(true);
        c.setTrimmingComments(true);
        c.setTrimmingXmlDeclarations(true);
        c.setTrimmingDoctypeDeclarations(true);
    }

    protected FaceletFactory createFaceletFactory(Compiler c) {
        long refreshPeriod = DEFAULT_REFRESH_PERIOD;
        FacesContext ctx = FacesContext.getCurrentInstance();
        String paramRefreshPeriod = ctx.getExternalContext().getInitParameter(
                PARAM_REFRESH_PERIOD);
        if (paramRefreshPeriod != null && paramRefreshPeriod.length() > 0) {
            try {
                refreshPeriod = Long.parseLong(paramRefreshPeriod);
            }
            catch (NumberFormatException nfe) {
                if (log.isWarnEnabled()) {
                    log.warn("Problem parsing refresh period: " +
                            paramRefreshPeriod, nfe);
                }
            }
        }

        ResourceResolver resourceResolver = null;
        String paramResourceResolver = ctx.getExternalContext().getInitParameter(
                PARAM_RESOURCE_RESOLVER);
        if (paramResourceResolver != null && paramResourceResolver.length() > 0) {
            try {
                Class resourceResolverClass = ReflectionUtil.forName(
                        paramResourceResolver);
                resourceResolver = (ResourceResolver)
                        resourceResolverClass.newInstance();
            }
            catch (Exception e) {
                throw new FacesException("Problem initializing ResourceResolver: " +
                        paramResourceResolver, e);
            }
        }
        if (resourceResolver == null)
            resourceResolver = new DefaultResourceResolver();

        resourceResolver = preChainResourceResolver(resourceResolver);

        return new DefaultFaceletFactory(c, resourceResolver, refreshPeriod);
    }

    /**
     * When D2DFaceletViewHandler is setting up the ResourceResolver for
     * Facelets, it uses this callback to allow for any subclass to
     * define a ResourceResolver of higher precedence, that would have
     * first crack at resolving resources, and then could delegate to
     * the standard mechanism.
     *
     * @param after The standard ResourceResolver that Facelets would ordinarily use
     * @return Either the new pre-chained ResourceResolver if one is being added,
     *         or just the given one if nothing is being chained in
     */
    protected ResourceResolver preChainResourceResolver(ResourceResolver after) {
        return after;
    }

    protected void renderResponse(FacesContext facesContext) throws IOException {
        if (log.isTraceEnabled()) {
            log.trace("renderResponse(FC)");
        }
        BridgeFacesContext context = (BridgeFacesContext) facesContext;
        try {
            ResponseWriter responseWriter = context.createAndSetResponseWriter();

            UIViewRoot viewToRender = context.getViewRoot();
            if (viewToRender.getId() == null) {
                viewToRender.setId(viewToRender.createUniqueId());
            }

            ComponentSupport.removeTransient(viewToRender);

            // grab our FaceletFactory and create a Facelet
            faceletInitialize();
            Facelet f = null;
            FaceletFactory.setInstance(faceletFactory);
            try {
                f = faceletFactory.getFacelet(viewToRender.getViewId());
            } finally {
                FaceletFactory.setInstance(null);
            }

            // Populate UIViewRoot
            f.apply(context, viewToRender);

            verifyUniqueComponentIds(context, viewToRender);

            // Uses D2DViewHandler logging
            tracePrintComponentTree(context);

            responseWriter.startDocument();
            renderResponse(context, viewToRender);
            // make stateSaving changes to DOM before ending document
            invokeStateSaving(context);
            responseWriter.endDocument();
        }
        /*
        catch(FileNotFoundException e) {
            handleFaceletFileNotFoundException(context);
        }
        */
        catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Problem in renderResponse: " + e.getMessage(), e);
            }
            throw new FacesException("Problem in renderResponse: " + e.getMessage(), e);
        }
    }

    /*
    protected void handleFaceletFileNotFoundException(FacesContext context)
            throws FacesException, IOException {
        String actualId = "";
        UIViewRoot viewToRender = context.getViewRoot();
        if( viewToRender != null) {
            String viewId = viewToRender.getViewId();
            String renderedViewId = getRenderedViewId(context, viewId);
            actualId = getActionURL(context, renderedViewId);
        }
        Object respObj = context.getExternalContext().getResponse();
        if (respObj instanceof HttpServletResponse) {
            HttpServletResponse respHttp = (HttpServletResponse) respObj;
            respHttp.sendError(HttpServletResponse.SC_NOT_FOUND, actualId);
            context.responseComplete();
        }
    }
    */

    /**
     * Fetch a string for logging purposes
     * @return  .xhtml, a likely suffix for Facelets. 
     */
    protected String getDefaultSuffix() {
        return ".xhtml";
    }

    protected static void removeTransient(UIComponent c) {
        UIComponent d, e;
        if (c.getChildCount() > 0) {
            for (Iterator itr = c.getChildren().iterator(); itr.hasNext();) {
                d = (UIComponent) itr.next();
                if (d.getFacets().size() > 0) {
                    for (Iterator jtr = d.getFacets().values().iterator(); jtr
                            .hasNext();) {
                        e = (UIComponent) jtr.next();
                        if (e.isTransient()) {
                            jtr.remove();
                        } else {
                            D2DFaceletViewHandler.removeTransient(e);
                        }
                    }
                }
                if (d.isTransient()) {
                    itr.remove();
                } else {
                    D2DFaceletViewHandler.removeTransient(d);
                }
            }
        }
        if (c.getFacets().size() > 0) {
            for (Iterator itr = c.getFacets().values().iterator(); itr
                    .hasNext();) {
                d = (UIComponent) itr.next();
                if (d.isTransient()) {
                    itr.remove();
                } else {
                    D2DFaceletViewHandler.removeTransient(d);
                }
            }
        }
    }

    /**
     * For performance reasons, when there aren't id collisions
     * we want this to be as fast as possible.  When there are
     * collisions, then we'll take some extra time to do a second
     * pass to provide more information
     * It could have all been done in one pass, but that would penalise
     * the typical case, where there are not duplicate ids
     *
     * @param comp UIComponent to recurse down through, searching for
     *             duplicate ids. Should be the UIViewRoot
     */
    protected static void verifyUniqueComponentIds(
            FacesContext context, UIComponent comp) {
        if (!log.isDebugEnabled())
            return;

        HashMap ids = new HashMap(512);
        ArrayList duplicateIds = new ArrayList(256);
        quicklyDetectDuplicateComponentIds(comp, ids, duplicateIds);

        if (!duplicateIds.isEmpty()) {
            HashMap duplicateIds2comps = new HashMap(512);
            compileDuplicateComponentIds(comp, duplicateIds2comps, duplicateIds);
            reportDuplicateComponentIds(context, duplicateIds2comps, duplicateIds);
        }
    }

    /**
     * Do the least amount of work to find if there are any duplicate ids,
     * with the assumption being that we won't typically find any
     * We also mention any null ids, just to be safe
     *
     * @param comp         UIComponent to recurse down through, searching for
     *                     duplicate ids.
     * @param ids          HashMap<String id, String id> allows for detecting
     *                     if an id has already been encountered or not
     * @param duplicateIds ArrayList<String id> duplicate ids encountered
     *                     as we recurse down
     */
    private static void quicklyDetectDuplicateComponentIds(
            UIComponent comp, HashMap ids, ArrayList duplicateIds) {
        String id = comp.getId();
        if (id == null) {
            log.debug("UIComponent has null id: " + comp);
        } else {
            if (ids.containsKey(id)) {
                if (!duplicateIds.contains(id))
                    duplicateIds.add(id);
            } else {
                ids.put(id, id);
            }
        }
        Iterator children = comp.getFacetsAndChildren();
        while (children.hasNext()) {
            UIComponent child = (UIComponent) children.next();
            quicklyDetectDuplicateComponentIds(child, ids, duplicateIds);
        }
    }

    /**
     * Make a list of every UIComponent that has a duplicate id, as found
     * in the duplicateIds parameter.
     *
     * @param comp               UIComponent to recurse down through, searching for
     *                           duplicate ids.
     * @param duplicateIds2comps HashMap< String id, ArrayList<UIComponent> >
     *                           save every UIComponent with one of the
     *                           duplicate ids, so we can list them all
     * @param duplicateIds       ArrayList<String id> duplicate ids encountered
     *                           before
     */
    private static void compileDuplicateComponentIds(
            UIComponent comp, HashMap duplicateIds2comps, ArrayList duplicateIds) {
        String id = comp.getId();
        if (id != null && duplicateIds.contains(id)) {
            ArrayList duplicateComps = (ArrayList) duplicateIds2comps.get(id);
            if (duplicateComps == null) {
                duplicateComps = new ArrayList();
                duplicateIds2comps.put(id, duplicateComps);
            }
            duplicateComps.add(comp);
        }
        Iterator children = comp.getFacetsAndChildren();
        while (children.hasNext()) {
            UIComponent child = (UIComponent) children.next();
            compileDuplicateComponentIds(child, duplicateIds2comps, duplicateIds);
        }
    }

    /**
     * Given a list of duplicate ids, and a mapping to each list of
     * UIComponents sharing each id, log this info so that the user
     * can most easily debug their application.
     *
     * @param duplicateIds2comps HashMap< String id, ArrayList<UIComponent> >
     *                           for each duplicated id, the UIComponents
     *                           sharing that id
     * @param duplicateIds       ArrayList<String id> duplicate ids encountered
     *                           before
     */
    private static void reportDuplicateComponentIds(
            FacesContext context, HashMap duplicateIds2comps, ArrayList duplicateIds) {
        // We don't simply iterate over duplicateIds2comps's keys, since that
        //  sequence is will probably not be very useful, whereas duplicateIds
        //  is sequenced in the order that we encountered the ids in the
        //  component tree, and thus in the source .xhtml file.

        int numDuplicateIds = duplicateIds.size();
        log.debug("There were " + numDuplicateIds + " ids found which are duplicates, meaning that multiple UIComponents share that same id");
        for (int i = 0; i < numDuplicateIds; i++) {
            String id = (String) duplicateIds.get(i);
            ArrayList duplicateComps = (ArrayList) duplicateIds2comps.get(id);
            StringBuffer sb = new StringBuffer(512);
            sb.append("Duplicate id: ");
            sb.append(id);
            sb.append(".  Number of UIComponents sharing that id: ");
            sb.append(Integer.toString(duplicateComps.size()));
            sb.append('.');
            for (int c = 0; c < duplicateComps.size(); c++) {
                UIComponent comp = (UIComponent) duplicateComps.get(c);
                sb.append("\n  clientId: ");
                sb.append(comp.getClientId(context));
                if (comp.isTransient())
                    sb.append(".  TRANSIENT");
                sb.append(".  component: ");
                sb.append(comp.toString());
            }
            log.debug(sb.toString());
        }
    }
}
