package com.icesoft.faces.component.outputresource;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.w3c.dom.Element;

import com.icesoft.faces.context.DOMContext;
import com.icesoft.faces.renderkit.dom_html_basic.DomBasicInputRenderer;
import com.icesoft.faces.renderkit.dom_html_basic.HTML;
import com.icesoft.faces.renderkit.dom_html_basic.PassThruAttributeRenderer;

public class OutputResourceRenderer extends DomBasicInputRenderer {

	protected static final String CONTAINER_DIV_SUFFIX = "_cont";

	public void encodeBegin(FacesContext facesContext, UIComponent uiComponent)
			throws IOException {

		String clientId = uiComponent.getClientId(facesContext);
		OutputResource outputResource = (OutputResource) uiComponent;
		if( outputResource.getResource() != null ){
			DOMContext domContext = DOMContext.attachDOMContext(facesContext,
					uiComponent);
			if (!domContext.isInitialized()) {
				domContext.createRootElement(HTML.DIV_ELEM);
			}

			Element root = (Element) domContext.getRootNode();
			root.setAttribute(HTML.ID_ATTR, uiComponent.getClientId(facesContext)
					+ CONTAINER_DIV_SUFFIX);
			domContext.setCursorParent(root);
			
			String style = outputResource.getStyle();
			String styleClass = outputResource.getStyleClass();
			
			Element resource = null;
		        		
			if( OutputResource.TYPE_BUTTON.equals(outputResource.getType())){
				resource = domContext.createElement(HTML.INPUT_ELEM);
				resource.setAttribute(HTML.TYPE_ATTR, "button");
				resource.setAttribute(HTML.VALUE_ATTR, outputResource.getLabel());
				resource.setAttribute(HTML.ONCLICK_ATTR, "window.open('" + outputResource.getPath() + "');");
			}
			else{
				resource = domContext.createElement(HTML.ANCHOR_ELEM);
				resource.setAttribute(HTML.HREF_ATTR, outputResource.getPath());
                PassThruAttributeRenderer.renderNonBooleanHtmlAttributes(uiComponent, resource, new String[]{"target"});

				if( outputResource.getImage() != null ){
					Element img = domContext.createElement(HTML.IMG_ELEM);
					String image = outputResource.getImage();
					if (image != null) {
    					img.setAttribute(HTML.SRC_ATTR, facesContext.
    					        getApplication().getViewHandler()
    	                        .getResourceURL(facesContext, image));
					}
					resource.appendChild(img);
					img.setAttribute(HTML.ALT_ATTR, outputResource.getLabel());
				}
				else{
					resource.appendChild(domContext.createTextNode(outputResource
							.getLabel()));
				}
				
			}
			resource.setAttribute(HTML.ID_ATTR, clientId);
			root.appendChild(resource);
			if( style != null ){
				resource.setAttribute(HTML.STYLE_ATTR, style);
			}
			if (styleClass != null) {
				resource.setAttribute("class", styleClass);
	        }
			
			domContext.stepOver();
		}
		
	}

}
