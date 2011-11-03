package com.icesoft.faces.component.selectinputtext;

import javax.faces.event.ValueChangeEvent;
import javax.faces.event.FacesListener;
import javax.faces.component.UIComponent;

/**
 * TextChangeEvent is broadcast in the APPLY_REQUEST_VALUES phase via the
 * SelectInputText's textChangeListener MethodBinding, containing the
 * SelectInputText's submittedValue as its new value.
 * 
 * It's purpose is to notify the application that the user has typed in a
 * text fragment into the SelectInputText's text input field, allowing for
 * the application to refine its selection list which will popup.
 * 
 * In the case of converted and validated values, which require a complete
 * input of text, like with a Date, the textChangeListener may call
 * FacesContext.getCurrentInstance().renderResponse() to forstall the
 * doomed validation.
 * 
 * @author Mark Collette
 * @since ICEfaces 1.7
 */
public class TextChangeEvent extends ValueChangeEvent {
    public TextChangeEvent(UIComponent comp, Object oldValue, Object newValue) {
        super(comp, oldValue, newValue);
    }

    public boolean isAppropriateListener(FacesListener facesListener) {
        return false;
    }

    public void processListener(FacesListener facesListener) {
    }
}
