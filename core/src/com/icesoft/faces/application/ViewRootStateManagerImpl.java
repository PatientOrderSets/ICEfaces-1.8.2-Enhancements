package com.icesoft.faces.application;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.application.StateManager;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.component.UIViewRoot;
import javax.faces.render.RenderKitFactory;

import com.icesoft.faces.context.BridgeFacesContext;
import com.icesoft.faces.context.View;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Method;

/**
 * A implementation of StateSaving that stores the ViewRoot into the Session between
 * requests. Fast and performant, the only downside is an inability to persist
 * the state to other nodes in a cluster
 */
public class ViewRootStateManagerImpl extends StateManager {

    protected static Log log = LogFactory.getLog(ViewRootStateManagerImpl.class);

    protected StateManager delegate;
    protected boolean parametersInitialized;
    protected boolean pureDelegation;
    protected Method v2DelegateSaveViewMethod;


    public ViewRootStateManagerImpl(StateManager delegate) {
        if (log.isInfoEnabled()) {
            log.info("ViewRootStateManagerImpl constructed with Delegate: " + delegate);
        }
        this.delegate = delegate;
    }

    /**
     * Override the restoreView method to fetch the ViewRoot from the session
     * and restore it. The UIViewRoot in the session Map is keyed by ICEfaces viewNumber 
     * @param context
     * @param viewId
     * @param renderKitId
     * @return The restored ViewRoot, null if none saved for this ICEfaces viewNumber
     */
    public UIViewRoot restoreView(FacesContext context, String viewId, String renderKitId) {

        initializeParameters( context );
        if ((pureDelegation) || !(context instanceof BridgeFacesContext) ) {
            return delegate.restoreView(context, viewId, renderKitId);
        }

        BridgeFacesContext bfc = (BridgeFacesContext) context;
        String viewNumber = bfc.getViewNumber();
        if (log.isDebugEnabled()) {
            log.debug("RestoreView called for View: " + bfc.getIceFacesId() + ", viewNumber: " + viewNumber );
        } 

        Map sessionMap = bfc.getExternalContext().getSessionMap();
        Map stateMap = (Map) sessionMap.get(  View.ICEFACES_STATE_MAPS );
        if (stateMap == null) {
            stateMap  = new HashMap();
            sessionMap.put( View.ICEFACES_STATE_MAPS, stateMap );
        }

        UIViewRoot root;

        root = (UIViewRoot) stateMap.get( viewNumber );
        if (root == null) {
            log.error("Missing ViewRoot in restoreState, ice.session: " + bfc.getIceFacesId() + ", viewNumber: " + viewNumber );
            return null;
        }
        return root;
    }

    /**
     * Perform first time initialization. Primarily determine if this instance is invoked from
     * another ICEfaces stateSaving instance and if so, enter a pure delegation
     * mode where this istance does nothing but delegate 
     * @param context
     */
     private void initializeParameters(FacesContext context) {
         if (parametersInitialized) {
             return;
         }

         // check to calling sequence to see if this class is stacked with others from ICEsoft.
        StackTraceElement[] ste = (new RuntimeException()).getStackTrace();
        String className;
        boolean external = false;
        for (int i = 0; i < ste.length; i++) {
            className = ste[i].getClassName();
            if (className.equals(this.getClass().getName()) || className.equals(SingleCopyStateManagerImpl.class.getName())) {
                if (external) {
                     if (log.isDebugEnabled()) {
                         log.debug("Pure delegate role taken by ViewRootStateSavingImpl");
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
     * Defer to the current strategy for saving the View
     * @param context
     * @return
     */
    public Object saveView(FacesContext context ) {

        initializeParameters(context);
          if ((pureDelegation) || !(context instanceof BridgeFacesContext) ) {
            // this bit because ICEfaces compiles against 1.1 and saveView is a 1.2 construct
            if (v2DelegateSaveViewMethod != null) {
                try {
                    return v2DelegateSaveViewMethod.invoke( delegate, new Object[] { context } );
                } catch (Exception e)  {
                    log.error("Exception in saveView" , e);
                }
            } else {
                return delegate.saveSerializedView(context);
            }
        } 

        UIViewRoot root = context.getViewRoot();

        BridgeFacesContext bfc = (BridgeFacesContext) context;
        String viewNumber = bfc.getViewNumber();

        Map sessionMap = bfc.getExternalContext().getSessionMap();
        Map stateMap = (Map) sessionMap.get(  View.ICEFACES_STATE_MAPS );
        if (stateMap == null) {
            stateMap  = new HashMap();
            sessionMap.put(View.ICEFACES_STATE_MAPS, stateMap );
        }
        stateMap.put( viewNumber, root );

        StateManager sm = context.getApplication().getStateManager();
        return sm.new SerializedView( viewNumber, null);
    }


    public SerializedView saveSerializedView(FacesContext context) {
        if ((pureDelegation) || !(context instanceof BridgeFacesContext) ) {
            return delegate.saveSerializedView(context);
        }
        return (SerializedView) saveView(context);
    }



    /*
       The following methods come from the eventual ResponseStateManager.
       Standard boilerplate disclaimer. We want to eventually support fully
       the RenderKit implementation.
    */
    final java.lang.String VIEW_STATE_PARAM = "javax.faces.ViewState";

    final char[] STATE_FIELD_START =
            ("<input type=\"hidden\" name=\""
             + VIEW_STATE_PARAM
             + "\" id=\""
             + VIEW_STATE_PARAM
             + "\" value=\"").toCharArray();

    final char[] STATE_FIELD_END =
            "\" />".toCharArray();


    /**
     *
     * @param context
     * @param view
     * @throws java.io.IOException
     */
    public void writeState(FacesContext context, SerializedView view)
    throws IOException {
        initializeParameters(context);
        if ((pureDelegation) || !(context instanceof BridgeFacesContext) ) {
            delegate.writeState(context, view);
        } 

        ResponseWriter writer = context.getResponseWriter();
        writer.write(STATE_FIELD_START);
        writer.write( view.getStructure().toString());
        writer.write(STATE_FIELD_END);
        writeRenderKitIdField(context, writer);
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

    public void restoreComponentState(FacesContext context,
                                      UIViewRoot viewRoot,      
                                      String renderKitId) {
    }

    protected UIViewRoot restoreTreeStructure(FacesContext context,
                                              String viewId,
                                              String renderKitId) {
        return null;
    }

    protected Object getComponentStateToSave(FacesContext context) {
        return null;
    }

    protected Object getTreeStructureToSave(FacesContext context) {
        return null;
    }
}
