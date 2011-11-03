package com.icesoft.faces.component.inputfile;

import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;
import javax.faces.event.PhaseId;
import javax.faces.component.UIComponent;

/**
 * @author Mark Collette
 * @since 1.7.1
 */
public class InputFileProgressEvent extends FacesEvent {
    public InputFileProgressEvent(UIComponent component) {
        super(component);
        this.setPhaseId(PhaseId.INVOKE_APPLICATION);
    }
    
    public boolean isAppropriateListener(FacesListener listener) {
        return false;
    }
    
    public void processListener(FacesListener listener) {
    }
}

