package com.icesoft.faces.component.gmap;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.w3c.dom.Element;

import com.icesoft.faces.context.DOMContext;
import com.icesoft.faces.renderkit.dom_html_basic.DomBasicRenderer;
import com.icesoft.faces.renderkit.dom_html_basic.HTML;

import com.icesoft.util.pooling.ClientIdPool;

public class GMapRenderer extends DomBasicRenderer{
	
    public boolean getRendersChildren() {
        return true;
    }
    
 
	 public void encodeBegin(FacesContext facesContext, UIComponent uiComponent)
     throws IOException {
	        String clientId = uiComponent.getClientId(facesContext);
	        GMap gmap = (GMap) uiComponent;
	        DOMContext domContext =
	                DOMContext.attachDOMContext(facesContext, uiComponent);
	        if (!domContext.isInitialized()) {
	        	Element container = domContext.createRootElement(HTML.TABLE_ELEM);
	        	container.setAttribute(HTML.CLASS_ATTR, gmap.getStyleClass());
	        	if (gmap.getStyle() != null) {
	        		container.setAttribute(HTML.STYLE_ATTR, gmap.getStyle());
	        	}
	        	Element tr = domContext.createElement(HTML.TR_ELEM);
	        	Element td = domContext.createElement(HTML.TD_ELEM);
	        	td.setAttribute(HTML.CLASS_ATTR, gmap.getMapTdStyleClass());
	        	container.appendChild(tr);
	        	tr.appendChild(td);
	        	
	        	Element gmapDiv = domContext.createElement(HTML.DIV_ELEM);
	        	td.appendChild(gmapDiv);
	        	gmapDiv.setAttribute(HTML.ID_ATTR, gmap.getClientId(facesContext));
	        	gmapDiv.setAttribute(HTML.CLASS_ATTR, "gmap");
	        	
	        	addHiddenField(domContext, td, clientId, "hdn");
	        	addHiddenField(domContext, td, clientId, "lat");
	        	addHiddenField(domContext, td, clientId, "lng");
	        	addHiddenField(domContext, td, clientId, "zoom");
	        	addHiddenField(domContext, td, clientId, "type", gmap.getType());	        	
	        	addHiddenField(domContext, td, clientId, "event");

	        }
	        domContext.stepOver();
	 }
	 
	 public void encodeChildren(FacesContext context, UIComponent component)
     throws IOException {
	     if (context == null || component == null) {
	         throw new NullPointerException();
	     }
	     if (component.getChildCount() == 0)return;
	     Iterator kids = component.getChildren().iterator();
	     while (kids.hasNext()) {
		    UIComponent kid = (UIComponent) kids.next();
		    kid.encodeBegin(context);
		    if (kid.getRendersChildren()) {
			kid.encodeChildren(context);
		    }
		    kid.encodeEnd(context);
	     }
	 }
	 
	 private void addHiddenField(DOMContext domContext, 
			 					 Element td, 
			 					 String clientId, 
			 					 String name) {
		 addHiddenField(domContext,td, clientId, name, null);		 
	 }
	 
	 
	 private void addHiddenField(DOMContext domContext, 
			 					 Element td, 
			 					 String clientId, 
			 					 String name,
			 					 String value) {
     	Element hidden = domContext.createElement(HTML.INPUT_ELEM);
     	String hiddenId = ClientIdPool.get(clientId + name);
    	hidden.setAttribute(HTML.ID_ATTR, hiddenId);
    	hidden.setAttribute(HTML.NAME_ATTR, hiddenId);
    	if (value != null) {
    		hidden.setAttribute(HTML.VALUE_ATTR, value);
    	}
    	hidden.setAttribute(HTML.TYPE_ATTR, "hidden");
    	td.appendChild(hidden);		 
	 }

}
