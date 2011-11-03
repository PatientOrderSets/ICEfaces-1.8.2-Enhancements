package com.icesoft.faces.component.seteventphase;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.FacesEvent;
import javax.faces.event.PhaseId;
import javax.faces.FacesException;
import java.util.Map;
import java.util.HashMap;

/**
 * @author mcollette
 * @since 1.8
 */
public class SetEventPhase extends UIComponentBase {
    public static final String COMPONENT_FAMILY = "com.icesoft.faces.SetEventPhase";
    private static Map phaseName2PhaseId = new HashMap();
    static {
        phaseName2PhaseId.put(
            "ANY",                  PhaseId.ANY_PHASE);
        phaseName2PhaseId.put(
            "APPLY_REQUEST_VALUES", PhaseId.APPLY_REQUEST_VALUES);
        phaseName2PhaseId.put(
            "PROCESS_VALIDATIONS",  PhaseId.PROCESS_VALIDATIONS);
        phaseName2PhaseId.put(
            "UPDATE_MODEL_VALUES",  PhaseId.UPDATE_MODEL_VALUES);
        phaseName2PhaseId.put(
            "INVOKE_APPLICATION",   PhaseId.INVOKE_APPLICATION);
    }
    
    private String events;
    private String phase;
    private Boolean disabled;
    
    public SetEventPhase() {
    }
    
    public String getFamily() {
        return COMPONENT_FAMILY;
    }
    
    public void queueEvent(FacesEvent event) {
        if (!isDisabled() && eventMatchingType(event)) {
            changePhaseId(event);
        }
        super.queueEvent(event);
    }
    
    protected boolean eventMatchingType(FacesEvent event) {
        String events = getEvents();
        if (events == null || events.length() == 0) {
            return false;
        }
        String[] specifiedEvents = events.split("\\s");
        if (specifiedEvents.length > 0) {
            for(int i = 0; i < specifiedEvents.length; i++) {
                Class specifiedClass = null;
                try {
                    specifiedClass = Class.forName(specifiedEvents[i]);
                }
                catch(Exception directName) {
                    if (specifiedEvents[i].indexOf(".") < 0) {
                        try {
                            specifiedClass = Class.forName(
                                "javax.faces.event." + specifiedEvents[i]);
                        }
                        catch(Exception shortName) {
                            // If both the given class name and our attempt at
                            // adding a package didn't resolve to an actual
                            // class, complain using the given class name
                            throw new FacesException(
                                "Could not resolve event class type: " +
                                    specifiedEvents[i], directName);
                        }
                    }
                }
                if (specifiedClass == null) {
                    continue;
                }
                if (specifiedClass.isInstance(event)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    protected void changePhaseId(FacesEvent event) {
        String phase = getPhase();
        if (phase == null || phase.trim().length() == 0) {
            return;
        }
        PhaseId phaseId = (PhaseId) phaseName2PhaseId.get(phase);
        if (phaseId == null) {
            throw new FacesException("Could not resolve phase: " + phase);
        }
        event.setPhaseId(phaseId);
    }
    
    /**
     * <p>Set the value of the <code>events</code> property.</p>
     */
    public void setEvents(String events) {
        this.events = events;
    }

    /**
     * <p>Return the value of the <code>events</code> property.</p>
     */
    public String getEvents() {
        if (events != null) {
            return events;
        }
        ValueBinding vb = getValueBinding("events");
        return vb != null ? vb.getValue(getFacesContext()).toString() : null;
    }
    
    /**
     * <p>Set the value of the <code>phase</code> property.</p>
     */
    public void setPhase(String phase) {
        this.phase = phase;
    }

    /**
     * <p>Return the value of the <code>phase</code> property.</p>
     */
    public String getPhase() {
        if (phase != null) {
            return phase;
        }
        ValueBinding vb = getValueBinding("phase");
        return vb != null ? vb.getValue(getFacesContext()).toString() : null;
    }

    /**
     * <p>Set the value of the <code>disabled</code> property.</p>
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled ? Boolean.TRUE : Boolean.FALSE;
    }
    
    /**
     * <p>Return the value of the <code>disabled</code> property.</p>
     */
    public boolean isDisabled() {
        if (null != disabled) {
            return disabled.booleanValue();
        }
        ValueBinding vb = getValueBinding("disabled");
        if (vb != null) {
            Boolean val = (Boolean) vb.getValue(FacesContext.getCurrentInstance());
            if (val != null) {
                return val.booleanValue();
            }
        }
        return false;
    }
    
    /**
     * <p>Gets the state of the instance as a <code>Serializable</code>
     * Object.</p>
     */
    public Object saveState(FacesContext context) {
        Object values[] = new Object[4];
        values[0] = super.saveState(context);
        values[1] = events;
        values[2] = phase;
        values[3] = disabled;
        return ((Object) (values));
    }

    /**
     * <p>Perform any processing required to restore the state from the entries
     * in the state Object.</p>
     */
    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        events = (String) values[1];
        phase = (String) values[2];
        disabled = (Boolean) values[3];
    }
}
