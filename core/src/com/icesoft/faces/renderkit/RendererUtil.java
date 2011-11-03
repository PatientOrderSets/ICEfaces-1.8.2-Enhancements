package com.icesoft.faces.renderkit;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.ResponseWriter;

import com.icesoft.faces.renderkit.dom_html_basic.PassThruAttributeRenderer;
import com.icesoft.faces.renderkit.dom_html_basic.PassThruAttributeWriter;

public class RendererUtil {

    public static String SUPPORTED_PASSTHRU_ATT = "supportedPassThru";
    public static void renderPassThruAttributes(ResponseWriter writer, 
            UIComponent uiComponent,
            String[] excludeArray) throws IOException{
        String[] attributes = (String[]) uiComponent.getAttributes().get(SUPPORTED_PASSTHRU_ATT);
        if (attributes == null) {
            PassThruAttributeWriter.renderAttributes(writer, uiComponent, excludeArray);
            return;
        }
        List excludeArrayList = Arrays.asList(excludeArray);
        for (int i=0; i < attributes.length; i++) {
            if (excludeArrayList.contains(attributes[i])) continue;
            Object value = null;
            if ((value = uiComponent.getAttributes().get(attributes[i])) != null &&
                    !PassThruAttributeRenderer.attributeValueIsSentinel(value)) {
                writer.writeAttribute(attributes[i], value, null);
            }
        }
        //Boolean attributes
        boolean result;
        for (int i=0; i < PassThruAttributeRenderer.booleanPassThruAttributeNames.length; i++) {
            if (excludeArrayList.
                    contains(PassThruAttributeRenderer.booleanPassThruAttributeNames[i])) continue;
            Object value = null;
            if ((value = uiComponent.getAttributes().
                  get(PassThruAttributeRenderer.booleanPassThruAttributeNames[i])) != null) {
                if (value instanceof Boolean) {
                    result = ((Boolean)value).booleanValue();
                } else {
                    if (!(value instanceof String)) {
                        value = value.toString();
                    }
                    result = (new Boolean ((String)value).booleanValue());
                }
                if (result) {
                    writer.writeAttribute(PassThruAttributeRenderer.booleanPassThruAttributeNames[i], 
                            PassThruAttributeRenderer.booleanPassThruAttributeNames[i], null);
                    result = false;
                }
            }
        }        
        
    } 
}
