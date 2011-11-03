package com.icesoft.faces.component;

import javax.faces.component.UIComponent;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;

public class DisplayEvent extends FacesEvent{

    private static final long serialVersionUID = 0L;
    UIComponent target = null;
    Object contextValue = null;
    boolean visible = false;
    
    public DisplayEvent(UIComponent component) {
        super(component);
    }

    public DisplayEvent(UIComponent component, UIComponent target) {
        super(component);
        this.target = target;
    }
    
    public DisplayEvent(UIComponent component, UIComponent target, Object contextValue) {
        super(component);
        this.target = target;
        this.contextValue = contextValue;
    }

    public DisplayEvent(UIComponent component, 
                        UIComponent target, 
                        Object contextValue,
                        boolean visible) {
        super(component);
        this.target = target;
        this.contextValue = contextValue;
        this.visible = visible;
    }
    
    public UIComponent getTarget() {
        return target;
    }

    public Object getContextValue() {
        return contextValue;
    }

    public boolean isVisible() {
        return visible;
    }
    
    public boolean isAppropriateListener(FacesListener listener) {
        // TODO Auto-generated method stub
        return false;
    }


    public void processListener(FacesListener listener) {
        // TODO Auto-generated method stub
    }
}
