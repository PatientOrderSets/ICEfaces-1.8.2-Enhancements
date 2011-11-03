package com.icesoft.faces.application;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.application.StateManager;
import javax.faces.context.FacesContext;
import javax.faces.context.ExternalContext;
import javax.faces.context.ResponseWriter;
import javax.faces.component.UIViewRoot;
import javax.faces.component.UIComponent;
import javax.faces.render.RenderKitFactory;
import javax.faces.FacesException;

import com.icesoft.faces.context.BridgeFacesContext;
import com.icesoft.faces.context.View;
import com.icesoft.faces.application.state.Util;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.reflect.Method;

/**
 * A implementation of StateSaving that stores state using the JSF StateManager
 * but only stores one copy per View. The copy is keyed by the View Number, not
 * the ViewID
 */
public class SingleCopyStateManagerImpl extends StateManager {

    protected static Log log = LogFactory.getLog(SingleCopyStateManagerImpl.class);

    protected Map classMap = new ConcurrentHashMap(32);
    protected boolean serialize_server_state;
    protected boolean compress_view_state;

    protected final static String SERIALIZE_SERVER_STATE = "com.sun.faces.serializeServerState";
    protected final static String COMPRESS_VIEW_STATE = "com.sun.faces.compressViewState";

    protected boolean parametersInitialized;
    protected StateManager delegate;

    protected boolean pureDelegation;
    protected Method v2DelegateSaveViewMethod;


    /**
     * Public one argument constructor for setting up the delegation model
     * @param delegate original overrider state saver. 
     */
    public SingleCopyStateManagerImpl(StateManager delegate ) {

        if (log.isInfoEnabled()) {
            log.info("Constructing SingleCopyStateManagerImpl with delegate: " + delegate);
        }
        this.delegate = delegate;
    }

    /**
     *
     * @param context Current facesContext
     */
    private void initializeParameters(FacesContext context) {
        if (parametersInitialized) {
            return;
        }

        ExternalContext ec = context.getExternalContext();
        serialize_server_state = Boolean.getBoolean( ec.getInitParameter(
                SERIALIZE_SERVER_STATE));
        compress_view_state = Boolean.getBoolean( ec.getInitParameter(
                COMPRESS_VIEW_STATE));

        // check to calling sequence to see if this class is stacked with others from ICEsoft.
        StackTraceElement[] ste = (new RuntimeException()).getStackTrace();
        String className;
        boolean external = false; 
        for (int i = 0; i < ste.length; i++) {
            className = ste[i].getClassName();
            if (className.equals(ViewRootStateManagerImpl.class.getName()) || className.equals(this.getClass().getName())) {
                if (external) {
                    if (log.isDebugEnabled()) {
                        log.debug("Pure delegate role taken by SingleCopyStateSavingImpl");
                    } 
                    pureDelegation = true;
                    break;
                }
            } else {
                external = true;
            } 
        }
        try {
            v2DelegateSaveViewMethod = delegate.getClass().getMethod("saveView", new Class[] { FacesContext.class} );
        } catch (Exception e) {
            log.error("Exception finding JSF1.2 saveView method on delegate", e); 
        }
        parametersInitialized = true;
    }


    /**
     * Defer to the current strategy for restoring the view
     * @param context
     * @param viewId
     * @param renderKitId
     * @return
     */
    public UIViewRoot restoreView(FacesContext context, String viewId,
                                  String renderKitId) {
        initializeParameters( context );

        if (pureDelegation || !(context instanceof BridgeFacesContext) ) {
            return delegate.restoreView(context, viewId, renderKitId);
        }

        UIViewRoot viewRoot;
        ExternalContext externalCtx = context.getExternalContext();

        Object [] stateArray;
        Map viewMap = (Map) externalCtx.getSessionMap().get(View.ICEFACES_STATE_MAPS);
        if (viewMap == null) {
            Map sessionMap = externalCtx.getSessionMap();
            viewMap= new HashMap();
            sessionMap.put( View.ICEFACES_STATE_MAPS, viewMap );
        }

        BridgeFacesContext bfc = (BridgeFacesContext) context;
        String viewNumber = bfc.getViewNumber();
        if (log.isDebugEnabled()) {
            log.debug("RestoreView called for View: " + bfc.getIceFacesId() + ", viewNumber: " + viewNumber );
        }
        
        stateArray = (Object[]) viewMap.get(viewNumber);
        if (stateArray == null) {
            log.error("Session Available, but View State does not exist for viewId:"
                      + viewId);
            return null;
        }

        viewRoot = Util.restoreTree( (Object[]) ((Object[])stateArray[0]).clone(), classMap );
        viewRoot.processRestoreState(context, handleRestoreState(stateArray[1]));
        return viewRoot;
    }

    /**
     * 
     * @param context
     * @return
     */
    public Object saveView(FacesContext context) {

        if (pureDelegation || !(context instanceof BridgeFacesContext) ) {
            if (v2DelegateSaveViewMethod != null) {
                // this bit because ICEfaces compiles against 1.1 and saveView is a 1.2 construct
                try {
                    return v2DelegateSaveViewMethod.invoke( delegate, new Object[] { context } );
                } catch (Exception e)  {
                    log.error("Exception in saveView" , e);
                }
            } else {
                return delegate.saveSerializedView(context);
            }
        }

        SerializedView result;

        UIViewRoot viewRoot = context.getViewRoot();
        if (viewRoot.isTransient()) {
            return null;
        }

        // honor the requirement to check for id uniqueness
        checkIdUniqueness(context, viewRoot, new HashSet(viewRoot.getChildCount() << 1));

        if (log.isDebugEnabled()) {
            log.debug("Begin creating serialized view for "
                      + viewRoot.getViewId());
        }
        Object state = viewRoot.processSaveState(context);
        Util.CaptureArray tree = new Util.CaptureArray();
        Util.captureChildNode(tree, 0, viewRoot);
        Object objTree = tree.toArray();

        if (log.isDebugEnabled()) {
            log.debug("End creating serialized view " + viewRoot.getViewId());
        }

        BridgeFacesContext bfc = (BridgeFacesContext) context;
        String viewNumber = bfc.getViewNumber();

        Map sessionMap = bfc.getExternalContext().getSessionMap();

        Map stateMap = (Map) sessionMap.get( View.ICEFACES_STATE_MAPS );
        if (stateMap == null) {
            stateMap  = new HashMap();
            sessionMap.put( View.ICEFACES_STATE_MAPS, stateMap );
        }

        StateManager sm = context.getApplication().getStateManager();
        result = sm.new SerializedView( viewNumber, null );
        stateMap.put( viewNumber, new Object[] { objTree, handleSaveState(state) } );
        return result;
    }


    public SerializedView saveSerializedView(FacesContext context) {
        initializeParameters( context );
        if (pureDelegation || !(context instanceof BridgeFacesContext) ) {
            return delegate.saveSerializedView(context);
        }
        return (SerializedView) saveView(context);
    }


    /*
        The following methods come from the eventual ResponseStateManager.
        Standard boilerplate disclaimer. We want to eventually support fully
        the RenderKit implementation.
     */


    /**
     * This method is an implementation from the ResponseStateManager, and should
     * be put there when we have our RenderKit implementation defined. This just
     * happens to have the same signature as the method from StateManager so we'll
     * leave it at that.
     *
     * @param context
     * @param view
     * @throws java.io.IOException
     */
    public void writeState(FacesContext context, SerializedView view)
            throws IOException {

        delegate.writeState( context, view);
    }



    /**
     * Write the renderKit id to the stream as well.
     * @param context
     * @param writer
     * @throws java.io.IOException
     */
    public static void writeRenderKitIdField(FacesContext context,
                                             ResponseWriter writer)
            throws IOException {
        String result = context.getApplication().getDefaultRenderKitId();
        if (result != null &&
            !RenderKitFactory.HTML_BASIC_RENDER_KIT.equals(result)) {
            writer.startElement("input", context.getViewRoot());
            writer.writeAttribute("type", "hidden", "type");
            writer.writeAttribute("name",
                                  "javax.faces.RenderKitId",
                                  "name");
            writer.writeAttribute("value",
                                  result,
                                  "value");
            writer.endElement("input");
        }
    }


    protected void checkIdUniqueness(FacesContext context,
                                     UIComponent component,
                                     Set componentIds)
            throws IllegalStateException {

        // deal with children/facets that are marked transient.
        Iterator i = component.getFacetsAndChildren();
        while (i.hasNext()) {

            UIComponent kid = (UIComponent) i.next();
            // check for id uniqueness
            String id = kid.getClientId(context);
            if (componentIds.add(id)) {
                checkIdUniqueness(context, kid, componentIds);
            } else {
                String message = "duplicate component id error:" + id;
                if (log.isErrorEnabled()) {
                    log.error(message);
                }
                throw new IllegalStateException(message);
            }
        }
    }

    /**
     * @param state the state as it was stored in the session
     * @return an object that can be passed to <code>UIViewRoot.processRestoreState</code>.
     */
    private Object handleRestoreState(Object state) {

        if (serialize_server_state) {
            ByteArrayInputStream bais = new ByteArrayInputStream((byte[]) state);
            ObjectInputStream ois = null;
            try {

                ois = ( (compress_view_state) ?
                        new ObjectInputStream( ( new GZIPInputStream(bais, 1024))) :
                        new ObjectInputStream(  bais ) );

                return ois.readObject();
            } catch (Exception e) {
                throw new FacesException(e);
            } finally {
                if (ois != null) {
                    try {
                        ois.close();
                    } catch (IOException ignored) { }
                }
            }
        } else {
            return state;
        }
    }

    /**
     * 
     * @param state the object returned from <code>UIView.processSaveState</code>
     */
    private Object handleSaveState(Object state) {

        if (serialize_server_state) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
            ObjectOutputStream oas = null;
            try {
                oas = ( (compress_view_state) ?
                        new ObjectOutputStream( ( new GZIPOutputStream(baos, 1024))) :
                        new ObjectOutputStream(  baos ) );
                oas.writeObject(state);
                oas.flush();
            } catch (Exception e) {
                throw new FacesException(e);
            } finally {
                if (oas != null) {
                    try {
                        oas.close();
                    } catch (IOException ignored) { }
                }
            }
            return baos.toByteArray();
        } else {
            return state;
        }
    }

    // override the abstract 1.1 implementation
    public void restoreComponentState(FacesContext context,
                                      UIViewRoot viewRoot,
                                      String renderKitId) {
    }

    // override the abstract 1.1 implementation
    protected UIViewRoot restoreTreeStructure(FacesContext context,
                                              String viewId,
                                              String renderKitId) {
        return null;
    }

    // override the abstract 1.1 implementation
    protected Object getComponentStateToSave(FacesContext context) {
        return null;
    }

    // override the abstract 1.1 implementation
    protected Object getTreeStructureToSave(FacesContext context) {
        return null;
    }
}
