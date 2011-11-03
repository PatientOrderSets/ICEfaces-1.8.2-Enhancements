package com.icesoft.faces.component.inputfile;

import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;
import javax.faces.event.PhaseId;
import javax.faces.component.UIComponent;

/**
 * @author Mark Collette
 * @since 1.8
 */
public class InputFileSetFileEvent extends FacesEvent {
    public InputFileSetFileEvent(UIComponent component) {
        super(component);
        this.setPhaseId(PhaseId.UPDATE_MODEL_VALUES);
    }
    
    public boolean isAppropriateListener(FacesListener listener) {
        return false;
    }
    
    public void processListener(FacesListener listener) {
    }
}
