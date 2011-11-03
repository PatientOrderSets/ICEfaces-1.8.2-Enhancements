package com.icesoft.faces.component.ext.renderkit;

import com.icesoft.faces.context.effects.LocalEffectEncoder;
import com.icesoft.faces.component.ExtendedAttributeConstants;
import com.icesoft.faces.renderkit.dom_html_basic.PassThruAttributeWriter;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.component.UIComponent;
import java.io.IOException;

public class OutputTextRenderer extends com.icesoft.faces.renderkit.dom_html_basic.OutputTextRenderer {
    // LocalEffectEncoder takes ownership of any passthrough attributes
    private static final String[] jsEvents = LocalEffectEncoder.maskEvents(
        ExtendedAttributeConstants.getAttributes(
            ExtendedAttributeConstants.ICE_OUTPUTTEXT));
    private static final String[] passThruAttributes =
        ExtendedAttributeConstants.getAttributes(
            ExtendedAttributeConstants.ICE_OUTPUTTEXT,
            jsEvents);
    
    protected void renderHtmlAttributes(
        FacesContext facesContext, ResponseWriter writer, UIComponent uiComponent)
        throws IOException {
        PassThruAttributeWriter.renderHtmlAttributes(
            writer, uiComponent, passThruAttributes);
        LocalEffectEncoder.encode(
            facesContext, uiComponent, jsEvents, null, null, writer);
    }
}
