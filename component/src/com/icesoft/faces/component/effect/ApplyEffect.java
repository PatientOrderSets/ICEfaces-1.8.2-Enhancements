package com.icesoft.faces.component.effect;

import com.icesoft.faces.context.effects.JavascriptContext;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.component.UIComponentBase;

public class ApplyEffect extends UIComponentBase {
    public static final String COMPONENET_TYPE = "com.icesoft.faces.ApplyEffect";
    public static final String DEFAULT_RENDERER_TYPE = "com.icesoft.faces.ApplyEffectRenderer";
    public static final String COMPONENT_FAMILY = "com.icesoft.faces.ApplyEffectFamily";
    private Boolean fire;
    private Boolean autoReset;
    private Boolean transitory;
    private Boolean submit;

    private String effectType;
    private String event;
    private String options;
    private String sequence;
    private Integer sequenceNumber;


    public ApplyEffect() {
        super();
        JavascriptContext.includeLib(JavascriptContext.ICE_EXTRAS, FacesContext.getCurrentInstance());
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getRendererType() {
        return DEFAULT_RENDERER_TYPE;
    }

    public Boolean getFire() {
        ValueBinding vb = getValueBinding("fire");
        if (vb != null) {
            return (Boolean) vb.getValue(getFacesContext());
        }
        if (fire != null) {
            return fire;
        }
        return Boolean.FALSE;
    }

    public void setFire(Boolean label) {
        ValueBinding vb = getValueBinding("fire");
        if (vb != null) {
            vb.setValue(getFacesContext(), label);
        } else {
            this.fire = label;
        }
    }

    public Boolean getTransitory() {
        ValueBinding vb = getValueBinding("transitory");
        if (vb != null) {
            return (Boolean) vb.getValue(getFacesContext());
        }
        if (transitory != null) {
            return transitory;
        }
        return Boolean.TRUE;
    }

    public void setTransitory(Boolean transitory) {
        ValueBinding vb = getValueBinding("transitory");
        if (vb != null) {
            vb.setValue(getFacesContext(), transitory);
        } else {
            this.transitory= transitory;
        }
    }

    public Boolean getSubmit() {
        ValueBinding vb = getValueBinding("submit");
        if (vb != null) {
            return (Boolean) vb.getValue(getFacesContext());
        }
        if (submit != null) {
            return submit;
        }
        return Boolean.FALSE;
    }

    public void setSubmit(Boolean submit) {
        ValueBinding vb = getValueBinding("submit");
        if (vb != null) {
            vb.setValue(getFacesContext(), submit);
        } else {
            this.submit= submit;
        }
    }
    public Boolean getAutoReset() {
        ValueBinding vb = getValueBinding("autoReset");
        if (vb != null) {
            return (Boolean) vb.getValue(getFacesContext());
        }
        if (autoReset != null) {
            return autoReset;
        }
        return Boolean.TRUE;
    }

    public void setAutoReset(Boolean autoReset) {
        ValueBinding vb = getValueBinding("autoReset");
        if (vb != null) {
            vb.setValue(getFacesContext(), autoReset);
        } else {
            this.autoReset = autoReset;
        }
    }


     public String getOptions() {
        ValueBinding vb = getValueBinding("options");
        if (vb != null) {
            return (String) vb.getValue(getFacesContext());
        }
        if (options != null) {
            return options;
        }
        return null;
    }

    public void setOptions(String options) {
        ValueBinding vb = getValueBinding("options");
        if (vb != null) {
            vb.setValue(getFacesContext(), options);
        } else {
            this.options = options;
        }
    }


     public String getEvent() {
        ValueBinding vb = getValueBinding("event");
        if (vb != null) {
            return (String) vb.getValue(getFacesContext());
        }
        if (event != null) {
            return event;
        }
        return null;
    }

    public void setEvent(String event) {
        ValueBinding vb = getValueBinding("event");
        if (vb != null) {
            vb.setValue(getFacesContext(), event);
        } else {
            this.event = event;
        }
    }


     public String getEffectType() {
        ValueBinding vb = getValueBinding("effectType");
        if (vb != null) {
            return (String) vb.getValue(getFacesContext());
        }
        if (effectType != null) {
            return effectType;
        }
        return null;
    }

    public void setEffectType(String effectType) {
        ValueBinding vb = getValueBinding("effectType");
        if (vb != null) {
            vb.setValue(getFacesContext(), effectType);
        } else {
            this.effectType = effectType;
        }
    }


     public String getSequence() {
        ValueBinding vb = getValueBinding("sequence");
        if (vb != null) {
            return (String) vb.getValue(getFacesContext());
        }
        if (sequence != null) {
            return sequence;
        }
        return null;
    }

    public void setSequence(String sequence) {
        ValueBinding vb = getValueBinding("sequence");
        if (vb != null) {
            vb.setValue(getFacesContext(), sequence);
        } else {
            this.sequence = sequence;
        }
    }

    private transient Object values[];
    public void restoreState(FacesContext context, Object state) {
            
        values = (Object[])state;
        super.restoreState(context, values[0]);
        fire = (Boolean)values[1];
        autoReset = (Boolean)values[2];
        transitory = (Boolean)values[3];
        submit = (Boolean)values[4];
        effectType = (String)values[5];
        event = (String)values[6];
        options =  (String)values[7];
        sequence = (String)values[8];
        sequenceNumber = (Integer)values[9];
    }

    public Object saveState(FacesContext context) {
       
        if(values == null){
            values = new Object[10];
        }
        values[0] = super.saveState(context);
        values[1] = fire;
        values[2] = autoReset;
        values[3] = transitory;
        values[4] = submit;
        values[5] = effectType;
        values[6] = event;
        values[7] = options;
        values[8] = sequence;
        values[9] = sequenceNumber;
        return values;
    }

    public Integer getSequenceNumber() {
        ValueBinding vb = getValueBinding("sequenceNumber");
        if (vb != null) {
            return (Integer) vb.getValue(getFacesContext());
        }
        if (sequenceNumber != null) {
            return sequenceNumber;
        }
        return new Integer(0);
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        ValueBinding vb = getValueBinding("sequenceNumber");
        if (vb != null) {
            vb.setValue(getFacesContext(), sequenceNumber);
        } else {
            this.sequenceNumber = sequenceNumber;
        }
    }




}
