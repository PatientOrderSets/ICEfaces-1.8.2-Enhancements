package com.icesoft.faces.component.panelcollapsible;

import java.io.IOException;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.el.EvaluationException;
import javax.faces.event.ActionEvent;
import javax.faces.event.FacesEvent;

import com.icesoft.faces.component.CSS_DEFAULT;
import com.icesoft.faces.component.ext.taglib.Util;

public class PanelCollapsible extends UICommand {
    public static final String COMPONENET_TYPE = "com.icesoft.faces.PanelCollapsible";
    public static final String DEFAULT_RENDERER_TYPE = "com.icesoft.faces.PanelCollapsibleRenderer";
    public static final String COMPONENT_FAMILY = "com.icesoft.faces.PanelCollapsible";
    private String style;
    private String styleClass;
    private boolean disabled = false;
    private boolean disabledSet = false;
    private String enabledOnUserRole = null;
    private String renderedOnUserRole = null;
    private Boolean toggleOnClick = null; 

    public PanelCollapsible(){
        setRendererType(DEFAULT_RENDERER_TYPE);
    }
    
    public String getFamily() {
        return COMPONENT_FAMILY;
    }


    public void decode(FacesContext context) {
    	super.decode(context);
    	Map map = context.getExternalContext().getRequestParameterMap();
    	String clientId = getClientId(context)+"Expanded";
    	if (map.containsKey(clientId) && !map.get(clientId).toString().equals("")) {
            getAttributes().put(getMatureClientId()+"changedByDecode", "true");
    		boolean exp = Boolean.valueOf(map.get(clientId).toString()).booleanValue();
    		exp = !exp;
    		setExpanded(exp);
    		queueEvent(new ActionEvent(this));
    	}
    }

    public void encodeBegin(FacesContext context) throws IOException {
        setId(getId());
        super.encodeBegin(context);
        if (getAttributes().get(getClientId(getFacesContext()))== null){
        	setExpanded(isExpanded());
        }
    }
    
    public boolean isExpanded() {
        ValueBinding vb = getValueBinding("expanded");
        if (vb != null) {
            Boolean exp = (Boolean) vb.getValue(getFacesContext()); 
            if (exp != null) {
                return exp.booleanValue();
            }
        }
        Object value = getAttributes().get(getMatureClientId());
        if (value != null) {
        	return ((Boolean)value).booleanValue();
        }
        return false;
    }

    public void setToggleOnClick(boolean toggleOnClick) {
    	this.toggleOnClick = Boolean.valueOf(toggleOnClick);
    }
    
    public boolean isToggleOnClick() {
        if (toggleOnClick != null) {
            return toggleOnClick.booleanValue();
        }
        ValueBinding vb = getValueBinding("toggleOnClick");
        if (vb != null) {
            return ((Boolean) vb.getValue(getFacesContext())).booleanValue();
        }
        return true;
    }
    public void setExpanded(boolean expanded) {
    	//expanded is a core state of this component,
    	//so to make it work with any UIData type of component
    	//the state need to be put inside the map belongs to
    	//this component instance.
  
        getAttributes().put(getMatureClientId(), Boolean.valueOf(expanded));

    }
    
    public String getStyle() {
        if (style != null) {
            return style;
        }
        ValueBinding vb = getValueBinding("style");
        if (vb != null) {
            return (String) vb.getValue(getFacesContext());
        }
        return null;
    }
    
    public void setStyle(String style) {
        this.style = style;
    }

    public String getStyleClass() {
        return Util.getQualifiedStyleClass(this, 
                getCollapsedStyle(styleClass),
                getCollapsedStyle(CSS_DEFAULT.PANEL_COLLAPSIBLE_DEFAULT_STYLE_CLASS),
                "styleClass",
                isDisabled());
    }

    public String getHeaderClass() {
        return Util.getQualifiedStyleClass(this, 
                CSS_DEFAULT.PANEL_COLLAPSIBLE_HEADER, 
                isDisabled());
    }
    
    public String getContentClass() {
        return Util.getQualifiedStyleClass(this, 
                CSS_DEFAULT.PANEL_COLLAPSIBLE_CONTENT, 
                isDisabled());        
    }
    
    private String getCollapsedStyle(String style) {
        if (!isExpanded() && style != null) {
            style += CSS_DEFAULT.PANEL_COLLAPSIBLE_STATE_COLLAPSED;
        }
        return style;
    }
    
    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }
    
    /**
     * @param disabled
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
        this.disabledSet = true;
    }

    /**
     * @return the value of disabled
     */
    public boolean isDisabled() {
        if (!Util.isEnabledOnUserRole(this)) {
            return true;
        }
        if (disabledSet) {
            return disabled;
        }
        ValueBinding vb = getValueBinding("disabled");
        Boolean v =
                vb != null ? (Boolean) vb.getValue(getFacesContext()) : null;
        return v != null ? v.booleanValue() : false;
    }
    
    /**
     * <p>Set the value of the <code>enabledOnUserRole</code> property.</p>
     */
    public void setEnabledOnUserRole(String enabledOnUserRole) {
        this.enabledOnUserRole = enabledOnUserRole;
    }

    /**
     * <p>Return the value of the <code>enabledOnUserRole</code> property.</p>
     */
    public String getEnabledOnUserRole() {
        if (enabledOnUserRole != null) {
            return enabledOnUserRole;
        }
        ValueBinding vb = getValueBinding("enabledOnUserRole");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>renderedOnUserRole</code> property.</p>
     */
    public void setRenderedOnUserRole(String renderedOnUserRole) {
        this.renderedOnUserRole = renderedOnUserRole;
    }

    /**
     * <p>Return the value of the <code>renderedOnUserRole</code> property.</p>
     */
    public String getRenderedOnUserRole() {
        if (renderedOnUserRole != null) {
            return renderedOnUserRole;
        }
        ValueBinding vb = getValueBinding("renderedOnUserRole");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Return the value of the <code>rendered</code> property.</p>
     */
    public boolean isRendered() {
        if (!Util.isRenderedOnUserRole(this)) {
            return false;
        }
        return super.isRendered();
    }
    /* (non-Javadoc)
     * @see javax.faces.component.UIComponent#processDecodes(javax.faces.context.FacesContext)
     */
     public void processDecodes(javax.faces.context.FacesContext context) {
  	   if (isExpanded()) {
  		   super.processDecodes(context);
  	   } else {//now process the children of the header only
  		 UIComponent headerFacet = getFacet("header");
         if(headerFacet != null){
         	headerFacet.processDecodes(context);
         }
         decode(context);
  	   }
     }

     /* (non-Javadoc)
     * @see javax.faces.component.UIComponent#processValidators(javax.faces.context.FacesContext)
     */
     public void processValidators(FacesContext context) {
  	   if (isExpanded()) {
  		   super.processValidators(context);
  	   } else {//now process the children of the header only
    		 UIComponent headerFacet = getFacet("header");
             if(headerFacet != null){
             	headerFacet.processValidators(context);
             }
       }
     }

     /* (non-Javadoc)
      * @see javax.faces.component.UIComponent#processUpdates(javax.faces.context.FacesContext)
      */
     public void processUpdates(FacesContext context) {
    	if (isExpanded()) {
    		super.processUpdates(context);
    	}  else {//now process the children of the header only
     		 UIComponent headerFacet = getFacet("header");
             if(headerFacet != null){
             	headerFacet.processUpdates(context);
             }
      	}
        ValueBinding vb = getValueBinding("expanded");
        //Let bean to know that the component's expanded state 
        //has been changed by the decode method. 
        if (vb != null && getAttributes().get(getMatureClientId()+"changedByDecode") != null) {
        	getAttributes().remove(getMatureClientId()+"changedByDecode");
            try {
            	vb.setValue(context, getAttributes().get(getClientId(getFacesContext())));
            	return;
            } catch (EvaluationException e) {
                String messageStr = e.getMessage();
                FacesMessage message = null;
                if (null == messageStr) {
                    messageStr = "Evaluation error";
                }
                message = new FacesMessage(messageStr);
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                context.addMessage(getClientId(context), message);
            } catch (Exception e) {
                String messageStr = e.getMessage();
                FacesMessage message = null;
                if (null == messageStr) {
                    messageStr = "Evaluation error";
                }
               message = new FacesMessage(messageStr);
               message.setSeverity(FacesMessage.SEVERITY_ERROR);
               context.addMessage(getClientId(context), message);
          
            }
        }
    }
    
    public void queueEvent(FacesEvent e) {
        // ICE-1956 UICommand subclasses shouldn't call super.queueEvent
        //  on ActionEvents or else the immediate flag is ignored
        if( (e instanceof ActionEvent) && !this.equals(e.getComponent()) && getParent() != null) {
            getParent().queueEvent(e);
        }
        else {
            super.queueEvent(e);
        }
    }
    

    public Object saveState(FacesContext context) {
        Object[] state = new Object[8];
        state[0] = super.saveState(context);
        state[1] = style;
        state[2] = styleClass;
        state[3] = Boolean.valueOf(disabled);
        state[4] = enabledOnUserRole;
        state[5] = renderedOnUserRole;
        state[6] = toggleOnClick;
        state[7] = Boolean.valueOf(disabledSet);
        
        return state;
    }

    public void restoreState(FacesContext context, Object stateIn) {
        Object[] state = (Object[]) stateIn;
        super.restoreState(context, state[0]);
        style = (String)state[1];
        styleClass = (String)state[2];
        disabled = ((Boolean)state[3]).booleanValue();
        enabledOnUserRole = (String)state[4];
        renderedOnUserRole = (String)state[5];
        toggleOnClick = (Boolean)state[6];        
        disabledSet = ((Boolean) state[7]).booleanValue();
    }
    
    //At the time of the creation of the component both JSP and facelets
    //returns incomplete client-id. So we want to keep the initial state of the 
    //component and later we want to replace it with the client-id so when this 
    //component is inside the dataTable it work for each row properly.
    private String getMatureClientId() {
        String clientId = getClientId(getFacesContext());
        if (clientId != null && clientId.indexOf(':') >= 0) {
            if (getAttributes().containsKey("expandedState")) {
                getAttributes().put(clientId, getAttributes().get("expandedState"));
                getAttributes().remove("expandedState");
            }
            return clientId;
        }
        return "expandedState";
    }
    
    public boolean hasInitiatedSubmit(FacesContext context) {
        Map map = context.getExternalContext().getRequestParameterMap();
        String clientId = getClientId(context)+"Expanded";
        if (map.containsKey(clientId) && !map.get(clientId).toString().equals("")) {
            return true;
        }
        return false;
    }
}