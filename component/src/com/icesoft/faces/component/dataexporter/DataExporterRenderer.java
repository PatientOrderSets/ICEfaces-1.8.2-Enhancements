package com.icesoft.faces.component.dataexporter;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;

import com.icesoft.faces.renderkit.dom_html_basic.BaseRenderer;
import com.icesoft.faces.renderkit.dom_html_basic.DomBasicRenderer;
import com.icesoft.faces.renderkit.dom_html_basic.HTML;
import com.icesoft.util.pooling.ClientIdPool;

public class DataExporterRenderer extends BaseRenderer {

	public void decode(FacesContext facesContext, UIComponent uiComponent) {
        Map requestMap =
            facesContext.getExternalContext().getRequestParameterMap();
        String clientId = uiComponent.getClientId(facesContext);
        if (requestMap.containsKey("ice.event.captured")) {
            if (clientId.equals(requestMap.get("ice.event.captured"))) {
                uiComponent.queueEvent(new ActionEvent(uiComponent));
            }
	    }
	}
	
	public void encodeBegin(FacesContext facesContext, UIComponent uiComponent)
	throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();
        String clientId = uiComponent.getClientId(facesContext);
        DataExporter dataExporter = (DataExporter)uiComponent;
        String label = dataExporter.getLabel();
        String image = dataExporter.getImage();
        boolean renderLabelAsButton = dataExporter.isRenderLabelAsButton();
        boolean linkRequired = false;
        String type = dataExporter.getType();
        if ( (type != null &&  !"".equals(type)) ||
                dataExporter.getOutputTypeHandler() != null) {
            linkRequired = true;
        }
        writer.startElement(HTML.DIV_ELEM, uiComponent);
        writer.writeAttribute(HTML.ID_ATTR, ClientIdPool.get(clientId + "container"), HTML.ID_ATTR);   
        String style = dataExporter.getStyle();
        if (style != null) {
            writer.writeAttribute(HTML.STYLE_ATTR, style, HTML.STYLE_ATTR);
        }
        writer.writeAttribute(HTML.CLASS_ATTR, dataExporter.getStyleClass(), HTML.CLASS_ATTR);     
        
        if (linkRequired) {
            if (renderLabelAsButton && image == null) {
                writer.startElement(HTML.INPUT_ELEM, uiComponent);
                writer.writeAttribute(HTML.ID_ATTR, clientId, HTML.ID_ATTR);        
                writer.writeAttribute(HTML.TYPE_ATTR, HTML.BUTTON_ELEM, HTML.TYPE_ATTR);
                writer.writeAttribute(HTML.ONCLICK_ATTR, "return "+ DomBasicRenderer.ICESUBMITPARTIAL, HTML.ONCLICK_ATTR);
                writer.writeAttribute(HTML.VALUE_ATTR, label, HTML.VALUE_ATTR);                
                writer.endElement(HTML.INPUT_ELEM);
            } else {
                writer.startElement(HTML.ANCHOR_ELEM, uiComponent);
                writer.writeAttribute(HTML.ID_ATTR, clientId, HTML.ID_ATTR);        
                writer.writeAttribute(HTML.HREF_ATTR, "javascript:;", HTML.HREF_ATTR);
                writer.writeAttribute(HTML.ONCLICK_ATTR, "var form=formOf(this); return "+ DomBasicRenderer.ICESUBMITPARTIAL, HTML.ONCLICK_ATTR);        
                if (image !=null) {
                    writer.startElement(HTML.IMG_ELEM, uiComponent);
                    writer.writeAttribute(HTML.SRC_ATTR, facesContext.
                            getApplication().getViewHandler()
                            .getResourceURL(facesContext, image), HTML.SRC_ATTR);  
                    writer.writeAttribute(HTML.TITLE_ATTR, label, HTML.TITLE_ATTR);
                    writer.writeAttribute(HTML.ALT_ATTR, label, HTML.ALT_ATTR);                
                    writer.endElement(HTML.IMG_ELEM);
                } else {
                    writer.write(label);
                }
                writer.endElement(HTML.ANCHOR_ELEM);
            }
        }
        writer.endElement(HTML.DIV_ELEM);
	}
}
