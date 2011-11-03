package com.icesoft.faces.component.paneldivider;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.w3c.dom.Element;

import com.icesoft.faces.component.CSS_DEFAULT;
import com.icesoft.faces.component.ExtendedAttributeConstants;
import com.icesoft.faces.component.ext.renderkit.FormRenderer;
import com.icesoft.faces.component.util.CustomComponentUtils;
import com.icesoft.faces.context.DOMContext;
import com.icesoft.faces.context.effects.JavascriptContext;
import com.icesoft.faces.renderkit.dom_html_basic.DomBasicRenderer;
import com.icesoft.faces.renderkit.dom_html_basic.HTML;

import com.icesoft.util.pooling.ClientIdPool;

public class PanelDividerRenderer extends DomBasicRenderer{
    // Basically, everything is excluded
    private static final String[] PASSTHRU_EXCLUDE =
        new String[] { HTML.STYLE_ATTR };
    private static final String[] PASSTHRU =
        ExtendedAttributeConstants.getAttributes(
            ExtendedAttributeConstants.ICE_PANELDIVIDER,
            PASSTHRU_EXCLUDE);
    
    public void encodeBegin(FacesContext facesContext, UIComponent uiComponent)
    throws IOException {
        validateParameters(facesContext, uiComponent, PanelDivider.class);
        PanelDivider panelDivider = (PanelDivider) uiComponent;
        DOMContext domContext =
                DOMContext.attachDOMContext(facesContext, uiComponent);
        if (!domContext.isInitialized()) {
            Element container = domContext.createRootElement(HTML.DIV_ELEM);
            setRootElementId(facesContext, container, uiComponent);
        }

        Element root = (Element) domContext.getRootNode();        
        Element container = (Element)domContext.createElement(HTML.DIV_ELEM);
        root.appendChild(container);
        container.setAttribute(HTML.CLASS_ATTR, panelDivider.getContainerClass());
        String style = panelDivider.getStyle();
        if (style != null) {
            root.setAttribute(HTML.STYLE_ATTR, style);
        }
        Element dividerContainer = (Element) domContext.createElement(HTML.DIV_ELEM);
        root.setAttribute(HTML.CLASS_ATTR, panelDivider.getStyleClass());
        dividerContainer.setAttribute(HTML.CLASS_ATTR, panelDivider.getSplitterClass());
        if(panelDivider.isHorizontal()) {
            dividerContainer.setAttribute(HTML.ONMOUSEDOWN_ATTR, "new Ice.PanelDivider(event, true);");
        } else {
            dividerContainer.setAttribute(HTML.ONMOUSEDOWN_ATTR, "new Ice.PanelDivider(event);");      
        }
        
        String clientId = uiComponent.getClientId(facesContext);
        dividerContainer.appendChild(domContext.createTextNode("&nbsp;"));
        renderPane(facesContext, uiComponent, true);
        container.appendChild(dividerContainer);
        dividerContainer.setAttribute(HTML.ID_ATTR, ClientIdPool.get(clientId + "Divider"));
        renderPane(facesContext, uiComponent, false);
        addHiddenField(domContext, root, clientId, PanelDivider.FIRST_PANL_STYLE, panelDivider.getPanePosition(true));
        addHiddenField(domContext, root, clientId, PanelDivider.SECOND_PANL_STYLE, panelDivider.getPanePosition(false));
        addHiddenField(domContext, root, clientId, PanelDivider.IN_PERCENT, "");
        domContext.stepOver();
        JavascriptContext.addJavascriptCall(facesContext, "Ice.PanelDivider.onLoad('" +
                clientId + "Divider', " + panelDivider.isHorizontal() + ");");
    }
    
    
    void renderPane(FacesContext facesContext,
                    UIComponent uiComponent,
                    boolean isFrist) throws IOException{
        DOMContext domContext = DOMContext.getDOMContext(facesContext, uiComponent);
        PanelDivider panelDivider = (PanelDivider) uiComponent;
        String clientId = uiComponent.getClientId(facesContext);
        Element pane = (Element) domContext.createElement(HTML.DIV_ELEM);
        UIComponent facet= null;
        if (isFrist) {
            facet = panelDivider.getFirstFacet();
            pane.setAttribute(HTML.ID_ATTR, ClientIdPool.get(clientId+= "First"));
            pane.setAttribute(HTML.CLASS_ATTR, panelDivider.getFirstPaneClass());
        } else {
            facet = panelDivider.getSecondFacet();
            pane.setAttribute(HTML.ID_ATTR, ClientIdPool.get(clientId+= "Second"));    
            pane.setAttribute(HTML.CLASS_ATTR, panelDivider.getSecondPaneClass());  
        }
        pane.setAttribute(HTML.STYLE_ATTR, panelDivider.getPanePosition(isFrist));
        if (facet == null ) {
            //log message facet required
            return;
        }
        Element container = (Element) domContext.getRootNode().getFirstChild();  
        container.appendChild(pane);
        domContext.setCursorParent(pane);
        CustomComponentUtils.renderChild(facesContext, facet);
    }
    
    private void addHiddenField(DOMContext domContext, 
            Element root, 
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
            root.appendChild(hidden);      
    }
}
