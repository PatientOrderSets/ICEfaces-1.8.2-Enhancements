package com.icesoft.faces.component.paneltabset;

import com.icesoft.faces.renderkit.dom_html_basic.GroupRenderer;
import com.icesoft.faces.renderkit.dom_html_basic.HTML;
import com.icesoft.faces.context.DOMContext;
import org.w3c.dom.Element;

/**
 * <p>PanelTabRenderer extends GroupRenderer and is responsible for
 * rendering the PanelTab's child components. The rendering of the
 * actual tab is done by PanelTabSetRenderer.</p>
 */
public class PanelTabRenderer extends GroupRenderer {
    
    protected Element createRootElement(DOMContext domContext) {
        // The SPAN that our superclass renders causes problems in
        //  some browsers, so we render a DIV here, to avoid that
        return domContext.createElement(HTML.DIV_ELEM);
    }
    
    protected void renderStyleAndStyleClass(
        String style, String styleClass, Element root)
    {
        // Do not render out the style or styleClass attributes to root,
        //  since PanelTab's styling is not for it as a container, but
        //  instead for the actual tabs that you click on.
    }
}
