package com.icesoft.faces.component.menupopup;

import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;
import javax.faces.component.UIComponent;
import javax.faces.el.ValueBinding;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>A MenuContextEvent encapsulates the information about which UIComponent
 *  was context-clicked on by the user, in order to display the menuPopup,
 *  and thus, on which UIComponent actions should be taken.</p>
 * <p>The source UIComponent, for this event, has a <i>menuContext</i>
 *  attribute, which is how it specified to receive MenuContextEvents.</p>
 * <p>Unlike most FacesEvents, which are queued up, and sent via MethodBindings,
 *  at the end of whichever phase, MenuContextEvent is set via ValueBindings
 *  during the Apply Request Values phase, while UIData, or other containers,
 *  are iterating over their children, so that bean code can use
 *  MenuContextEvent.getComponent() to access any row specific information it
 *  needs. The Apply Request Values phase is used, so that the MenuContextEvent
 *  data is available to the bean, before the MenuItem actionListener is
 *  called, which is in the Invoke Application phase, or at the end of the
 *  Apply Request Values phase, if <i>immediate="true"</i>.</p>
 * <p>MenuContextEvent provides context for a specific component in the
 *  hierarchy, but there can be several levels for which contextual
 *  information is necessary. If there are nested dataTables, then context
 *  will be needed for each dataTable, as well as the columns. To facilitate
 *  this, each MenuContextEvent is a part of a double-linked-list of
 *  MenuContextEvents, which together provide the complete context. Bean code
 *  can process each MenuContextEvent separately, in its own listener method,
 *  or process the <i>terminal</i> MenuContextEvent, and follow its <i>outer</i>
 *  references, all within a single method.</p>
 * 
 * @author Mark Collette
 */
public class MenuContextEvent extends FacesEvent {
    private static final Log log = LogFactory.getLog(MenuContextEvent.class);
    
    private MenuContextEvent outer;
    private MenuContextEvent inner;
    
    MenuContextEvent(UIComponent src, MenuContextEvent inner) {
        super(src);
        this.inner = inner;
        if(this.inner != null)
            this.inner.outer = this;
    }

    /**
     * The outer MenuContextEvent is the event corresponding to the source
     *  UIComponent that is the nearest <b>parent</b> of this event's source
     *  UIComponent, for which there is a <i>menuContext</i> attribute.
     * @return Outer MenuContextEvent
     */
    public MenuContextEvent getOuter() {
        return this.outer;
    }
    
    /**
     * The inner MenuContextEvent is the event corresponding to the source
     *  UIComponent that is the nearest <b>child</b> of this event's source
     *  UIComponent, for which there is a <i>menuContext</i> attribute.
     * @return Outer MenuContextEvent
     */
    public MenuContextEvent getInner() {
        return this.inner;
    }

    /**
     * A MenuContextEvent is terminal if it is the inner-most event providing
     *  context for which UIComponent was context-clicked on by the user, in
     *  order to display the menuPopup.
     * @return If this MenuContextEvent is terminal
     */
    public boolean isTerminal() {
        return (this.inner == null);
    }
    
    public void process(FacesContext facesContext) {
//System.out.println("MenuContextEvent.process");
        UIComponent comp = getComponent();
//System.out.println("  comp: " + comp);
        if(comp == null)
            return;
//System.out.println("  comp.clientId: " + comp.getClientId(facesContext));
//if(comp instanceof javax.faces.component.UIData)
//  System.out.println("  UIData.rowIndex: " + ((javax.faces.component.UIData)comp).getRowIndex());
        ValueBinding vb = comp.getValueBinding("menuContext");
//System.out.println("  vb: " + vb);
        if(vb == null)
            return;
//System.out.println("  el: " + vb.getExpressionString());
        try {
            vb.setValue(facesContext, this);
//System.out.println("  vb.setValue worked");
        }
        catch(Exception e) {
//System.out.println("  Exception: " + e);
            log.error(e);
        }
    }
    
    /**
     * MenuContextEvent is not enqueued and broadcast, so this is not relevant
     */
    public boolean isAppropriateListener(FacesListener facesListener) {
        return false;
    }

    /**
     * MenuContextEvent is not enqueued and broadcast, so this is not relevant
     */
    public void processListener(FacesListener facesListener) {
    }
}
