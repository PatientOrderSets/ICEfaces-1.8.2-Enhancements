package com.icesoft.faces.renderkit.dom_html_basic;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import java.util.Map;

/**
 * @author mcollette
 * @since 1.8.1
 */
public class BaseInputRenderer extends BaseRenderer {
    public void decode(FacesContext facesContext, UIComponent uiComponent) {
        super.decode(facesContext, uiComponent);
        //readonly or disabled components are not required to submit the value
        if(DomBasicRenderer.isStatic(uiComponent)) {
            return;
        }
        String clientId = uiComponent.getClientId(facesContext);
        Map requestMap =
                facesContext.getExternalContext().getRequestParameterMap();
        if (requestMap.containsKey(clientId)) {
            String decodedValue = (String) requestMap.get(clientId);
            ((UIInput)uiComponent).setSubmittedValue(decodedValue);
        }
    }
    
    public String getValue(FacesContext facesContext, UIComponent uiComponent) {
        // for input components, get the submitted value
        if (uiComponent instanceof UIInput) {
            Object submittedValue = ((UIInput) uiComponent).getSubmittedValue();
            if (submittedValue != null && submittedValue instanceof String) {
                return (String) submittedValue;
            }
        }
        return DomBasicInputRenderer.converterGetAsString(facesContext, 
                uiComponent, ((UIInput) uiComponent).getValue());
    }
}
