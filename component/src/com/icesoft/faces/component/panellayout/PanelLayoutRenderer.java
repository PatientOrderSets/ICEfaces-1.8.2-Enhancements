/*
 * 
 * AbsoluteLayout allow placement of components in absolute positions.
 * A flow layout arranges components in relative alignment.
 */
package com.icesoft.faces.component.panellayout;

import java.io.IOException;
import java.util.Iterator;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

public class PanelLayoutRenderer extends Renderer {

    public void encodeBegin(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {

        ResponseWriter writer = facesContext.getResponseWriter();
        writer.startElement("div", uiComponent);
        
        if(uiComponent instanceof PanelLayout){
            PanelLayout panelLayout = (PanelLayout)uiComponent;
            String modifiedStyle = getLayoutStyle(panelLayout.getStyle(), getLayoutMode(panelLayout.getLayout()));            
            writer.writeAttribute("style", modifiedStyle, "style");
            String clientId = uiComponent.getClientId(facesContext);
            writer.writeAttribute("id", clientId, "id");
            
            String styleClass = panelLayout.getStyleClass();
            if(styleClass != null && styleClass.length() > 0){
                writer.writeAttribute("class", styleClass, "styleClass");
            }
        }                    
    }

    private String getLayoutStyle(String style, int layoutMode){
        
        StringBuffer prefixStyle = new StringBuffer(" ");
        switch(layoutMode){
            case 1: 
                 prefixStyle.append("position:relative;");
                 break;
            case 2:
                 prefixStyle.append("position:absolute;");  
                 break;
        }
        
        if(style != null && style.length() > 0){
            prefixStyle.append(style);
        }
        
        return prefixStyle.toString();
    } 
    
    private int getLayoutMode(String layout){
        
        if(layout.equals(PanelLayout.FLOWLAYOUT)){
            return 1;
        }else if(layout.equals(PanelLayout.ABSOLUATELAYOUT)){
            return 2;
        }
        
        return 2;
    }
    
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {

        if (context == null || component == null) {
            throw new NullPointerException();
        }

        if (!component.isRendered()) return;

        com.icesoft.faces.component.util.CustomComponentUtils.renderChildren(context, component);
    }

    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        writer.endElement("div");
    }

    public boolean getRendersChildren() {
        return true;
    }
}
