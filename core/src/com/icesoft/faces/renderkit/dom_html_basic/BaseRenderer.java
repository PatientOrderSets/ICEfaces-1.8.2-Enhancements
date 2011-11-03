package com.icesoft.faces.renderkit.dom_html_basic;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.el.ValueBinding;
import javax.faces.render.Renderer;

import com.icesoft.faces.context.effects.CurrentStyle;
import com.icesoft.faces.context.effects.JavascriptContext;
import com.icesoft.faces.util.CoreUtils;

import com.icesoft.util.pooling.ClientIdPool;

public class BaseRenderer extends Renderer{
    public void decode(FacesContext facesContext, UIComponent uiComponent) {
        CurrentStyle.decode(facesContext, uiComponent);
        super.decode(facesContext, uiComponent);
    }
    
    public void encodeBegin(FacesContext facesContext, UIComponent uiComponent)
    throws IOException {
        super.encodeBegin(facesContext, uiComponent);
        
    }
    
    public void encodeChildren(FacesContext facesContext, UIComponent uiComponent)
    throws IOException {
        super.encodeChildren(facesContext, uiComponent);
        
    }
    
    public void encodeEnd(FacesContext facesContext, UIComponent uiComponent)
    throws IOException {
        super.encodeEnd(facesContext, uiComponent);    
        CoreUtils.recoverFacesMessages(facesContext, uiComponent);
        JavascriptContext.fireEffect(uiComponent, facesContext);
    }
    
    public String getResourceURL(FacesContext context, String path) {
        return DomBasicRenderer.getResourceURL(context, path);
    }    
    
    //This method should not be removed, it will be called by the UIInput class
    //for all input type of components.
    public Object getConvertedValue(FacesContext facesContext, UIComponent
            uiComponent, Object submittedValue) throws ConverterException {

        // get the converter (if any) registered with this component 
        Converter converter = null;
        if (uiComponent instanceof ValueHolder) {
            converter = ((ValueHolder) uiComponent).getConverter();
        }
        // if we didn't find a converter specifically registered with the component
        // then get the default converter for the type of the value binding,
        // if it exists
        ValueBinding valueBinding = uiComponent.getValueBinding("value");
        if (converter == null && valueBinding != null) {
            Class valueBindingClass = valueBinding.getType(facesContext);
            if (valueBindingClass != null) {
                converter = facesContext.getApplication()
                        .createConverter(valueBindingClass);
            }
        }

        if (converter != null) {
            return converter.getAsObject(facesContext, uiComponent,
                                         (String) submittedValue);
        } else if (submittedValue != null) {
            return (String) submittedValue;
        } else {
            return null;
        }
    }    
    
    public String convertClientId(FacesContext context, String clientId) {
        return ClientIdPool.get(clientId);    
    }   
}
