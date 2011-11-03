package com.icesoft.faces.component.ext;

import javax.faces.event.ActionEvent;
import javax.faces.event.FacesListener;
import javax.faces.component.UIComponent;

/**
 * @author mcollette
 * @since 1.7
 */
public class RowSelectorActionEvent extends ActionEvent {
    public RowSelectorActionEvent(UIComponent component) {
        super(component);
    }
    
    public boolean isAppropriateListener(FacesListener listener) {
        return false;
    }
    
    public void processListener(FacesListener listener) {
    }
}
