/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * "The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations under
 * the License.
 *
 * The Original Code is ICEfaces 1.5 open source software code, released
 * November 5, 2006. The Initial Developer of the Original Code is ICEsoft
 * Technologies Canada, Corp. Portions created by ICEsoft are Copyright (C)
 * 2004-2006 ICEsoft Technologies Canada, Corp. All Rights Reserved.
 *
 * Contributor(s): _____________________.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"
 * License), in which case the provisions of the LGPL License are
 * applicable instead of those above. If you wish to allow use of your
 * version of this file only under the terms of the LGPL License and not to
 * allow others to use your version of this file under the MPL, indicate
 * your decision by deleting the provisions above and replace them with
 * the notice and other provisions required by the LGPL License. If you do
 * not delete the provisions above, a recipient may use your version of
 * this file under either the MPL or the LGPL License."
 *
 */

package com.icesoft.faces.component.panelborder;

import com.icesoft.faces.component.util.CustomComponentUtils;
import com.icesoft.faces.component.ExtendedAttributeConstants;
import com.icesoft.faces.context.DOMContext;
import com.icesoft.faces.renderkit.dom_html_basic.DomBasicRenderer;
import com.icesoft.faces.renderkit.dom_html_basic.HTML;
import com.icesoft.faces.renderkit.dom_html_basic.PassThruAttributeRenderer;
import org.w3c.dom.Element;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.List;

/**
 * This PanelBorderRenderer is responsible for rendering PanelBorder
 * components.
 */
public class PanelBorderRenderer extends DomBasicRenderer {
    public static final String DEFAULT_LAYOUT = "default";
    public static final String REVERSE_W_E = "horizontal reverse";
    public static final String REVERSE_N_S = "vertical reverse";
    public static final String CENTER_ONLY = "center only";
    public static final String HIDE_N = "hide north";
    public static final String HIDE_E = "hide east";
    public static final String HIDE_S = "hide south";
    public static final String HIDE_W = "hide west";
    
    private static final String[] PASSTHRU_EXCLUDE =
        new String[] { HTML.STYLE_ATTR };
    private static final String[] PASSTHRU =
        ExtendedAttributeConstants.getAttributes(
            ExtendedAttributeConstants.ICE_PANELBORDER,
            PASSTHRU_EXCLUDE);
    
    /* (non-Javadoc)
     * @see javax.faces.render.Renderer#getRendersChildren()
     */
    public boolean getRendersChildren() {
        return true;
    }

    /* (non-Javadoc)
     * @see javax.faces.render.Renderer#encodeChildren(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
     */
    public void encodeChildren(FacesContext facesContext,
                               UIComponent uiComponent) throws IOException {
        super.encodeChildren(facesContext, uiComponent);
    }

    /* (non-Javadoc)
     * @see com.icesoft.faces.renderkit.dom_html_basic.DomBasicRenderer#encodeEnd(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
     */
    public void encodeEnd(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
        validateParameters(facesContext, uiComponent, PanelBorder.class);

        DOMContext domContext =
                DOMContext.attachDOMContext(facesContext, uiComponent);
        PanelBorder borderLayout = (PanelBorder) uiComponent;
        List layout = borderLayout.getLayoutAsList();
        String clientId = uiComponent.getClientId(facesContext);
        if (!domContext.isInitialized()) {
            Element table = domContext.createRootElement(HTML.TABLE_ELEM);
            setRootElementId(facesContext, table, uiComponent);
            table.setAttribute(HTML.NAME_ATTR, clientId);
        }

        Element table = (Element) domContext.getRootNode();
        PassThruAttributeRenderer
            .renderHtmlAttributes(facesContext, uiComponent, PASSTHRU);
        table.setAttribute(HTML.CLASS_ATTR, borderLayout.getStyleClass());
        String style = borderLayout.getStyle();
        if(style != null && style.length() > 0)
            table.setAttribute(HTML.STYLE_ATTR, style);
        else
            table.removeAttribute(HTML.STYLE_ATTR);

        DOMContext.removeChildren(table);

        renderPanel(facesContext, layout, table, borderLayout, domContext);

        if (borderLayout.getChildCount() > 0) {
            throw new RuntimeException(
                    "PanelBorder must not have children, only facets allowed!");
        }


        domContext.stepOver();
    }

    private void renderPanel(FacesContext facesContext, List facets,
                             Element table, PanelBorder borderLayout,
                             DOMContext domContext) throws IOException {
        String order = borderLayout.getLayout();
        if (order.equalsIgnoreCase(REVERSE_N_S)) {
            renderSouth(facesContext, domContext, borderLayout, facets, table);
            renderEastWestCenter(facesContext, domContext, borderLayout, facets,
                                 table);
            renderNorth(facesContext, domContext, borderLayout, facets, table);

        } else {
            renderNorth(facesContext, domContext, borderLayout, facets, table);
            renderEastWestCenter(facesContext, domContext, borderLayout, facets,
                                 table);
            renderSouth(facesContext, domContext, borderLayout, facets, table);
        }
    }

    private void renderEastWestCenter(FacesContext facesContext,
                                      DOMContext domContext,
                                      PanelBorder borderLayout, List facets,
                                      Element table) throws IOException {
        if (borderLayout.getWest() != null ||
            borderLayout.getCenter() != null ||
            borderLayout.getEast() != null) {
            Element tr = domContext.createElement(HTML.TR_ELEM);
            table.appendChild(tr);
            String order = borderLayout.getLayout();
            if (order.equalsIgnoreCase(REVERSE_W_E)) {
                renderEast(facesContext, domContext, borderLayout, facets, tr);
                renderCenter(facesContext, domContext, borderLayout, facets,
                             tr);
                renderWest(facesContext, domContext, borderLayout, facets, tr);
            } else {
                renderWest(facesContext, domContext, borderLayout, facets, tr);
                renderCenter(facesContext, domContext, borderLayout, facets,
                             tr);
                renderEast(facesContext, domContext, borderLayout, facets, tr);
            }

        }
    }

    private void renderWest(FacesContext facesContext, DOMContext domContext,
                            PanelBorder borderLayout, List facets, Element tr)
            throws IOException {
        if (facets.contains(PanelBorder.WEST_LAYOUT)) {
            renderTableCells(facesContext, borderLayout, tr,
                             PanelBorder.WEST_LAYOUT, domContext);
        }
    }

    private void renderCenter(FacesContext facesContext, DOMContext domContext,
                              PanelBorder borderLayout, List facets, Element tr)
            throws IOException {
        if (facets.contains(PanelBorder.CENTER_LAYOUT)) {
            renderTableCells(facesContext, borderLayout, tr,
                             PanelBorder.CENTER_LAYOUT, domContext);
        }
    }

    private void renderEast(FacesContext facesContext, DOMContext domContext,
                            PanelBorder borderLayout, List facets, Element tr)
            throws IOException {
        if (facets.contains(PanelBorder.EAST_LAYOUT)) {
            renderTableCells(facesContext, borderLayout, tr,
                             PanelBorder.EAST_LAYOUT, domContext);
        }
    }


    private void renderNorth(FacesContext facesContext, DOMContext domContext,
                             PanelBorder borderLayout, List facets,
                             Element table) throws IOException {
        if ((borderLayout.getNorth() != null) &&
            (facets.contains(PanelBorder.NORTH_LAYOUT))) {
            Element tr = domContext.createElement(HTML.TR_ELEM);
            table.appendChild(tr);
            renderTableCells(facesContext, borderLayout, tr,
                             PanelBorder.NORTH_LAYOUT, domContext);
        }
    }

    private void renderSouth(FacesContext facesContext, DOMContext domContext,
                             PanelBorder borderLayout, List facets,
                             Element table) throws IOException {
        if ((borderLayout.getSouth() != null) &&
            (facets.contains(PanelBorder.SOUTH_LAYOUT))) {
            Element tr = domContext.createElement(HTML.TR_ELEM);
            table.appendChild(tr);
            renderTableCells(facesContext, borderLayout, tr,
                             PanelBorder.SOUTH_LAYOUT, domContext);
        }
    }

    private Element getTD(FacesContext facesContext, PanelBorder borderLayout,
                          DOMContext domContext, Element tr)
            throws IOException {
        Element td = domContext.createElement(HTML.TD_ELEM);
        tr.appendChild(td);
        domContext.setCursorParent(td);
        return td;
    }

    private void renderTableCells(FacesContext facesContext,
                                  PanelBorder borderLayout, Element tr,
                                  String facet, DOMContext domContext)
            throws IOException {
        UIComponent north = borderLayout.getNorth();
        UIComponent west = borderLayout.getWest();
        UIComponent east = borderLayout.getEast();
        UIComponent center = borderLayout.getCenter();
        UIComponent south = borderLayout.getSouth();

        if (facet.equals(PanelBorder.NORTH_LAYOUT) && north != null) {
            // Determine how wide the north component should be
            // Based on if the west, east, and center are found
            int width = 0;

            if (west != null) {
                width++;
            }
            if (east != null) {
                width++;
            }
            if (center != null) {
                width++;
            }

            renderTableCell(facesContext, north,
                            getTD(facesContext, borderLayout, domContext, tr),
                            (width == 0) ? 1 : width,
                            borderLayout.getNorthClass(), null, borderLayout);
        }

        if (facet.equals(PanelBorder.WEST_LAYOUT) && west != null) {
            renderTableCell(facesContext, west,
                            getTD(facesContext, borderLayout, domContext, tr),
                            1,
                            borderLayout.getWestClass(), null, borderLayout);
        }

        if (facet.equals(PanelBorder.EAST_LAYOUT) && east != null) {
            renderTableCell(facesContext, east,
                            getTD(facesContext, borderLayout, domContext, tr),
                            1,
                            borderLayout.getEastClass(), null, borderLayout);
        }

        if (facet.equals(PanelBorder.CENTER_LAYOUT) && center != null) {
            renderTableCell(facesContext, center,
                            getTD(facesContext, borderLayout, domContext, tr),
                            1,
                            borderLayout.getCenterClass(), null, borderLayout);
        }

        if (facet.equals(PanelBorder.SOUTH_LAYOUT) && south != null) {
            // Determine how wide the south component should be
            // Based on if the west, east, and center are found            
            int width = 0;

            if (west != null) {
                width++;
            }
            if (east != null) {
                width++;
            }
            if (center != null) {
                width++;
            }

            renderTableCell(facesContext, south,
                            getTD(facesContext, borderLayout, domContext, tr),
                            (width == 0) ? 1 : width,
                            borderLayout.getSouthClass(), null, borderLayout);
        }
    }

    private void renderTableCell(FacesContext facesContext,
                                 UIComponent component,
                                 Element td, int colspan, String styleClass,
                                 String style, PanelBorder panelBorder)
            throws IOException {
        if (colspan > 0) {
            td.setAttribute(HTML.COLSPAN_ATTR, Integer.toString(colspan));
        }
        else {
            td.removeAttribute(HTML.COLSPAN_ATTR);
        }
        if (styleClass != null && styleClass.length() > 0) {
            td.setAttribute(HTML.CLASS_ATTR, styleClass);
        }
        else {
            td.removeAttribute(HTML.CLASS_ATTR);
        }
        if (style != null && style.length() > 0) {
            td.setAttribute(HTML.STYLE_ATTR, style);
        }
        else {
            td.removeAttribute(HTML.STYLE_ATTR);
        }

        DOMContext domContext =
                DOMContext.getDOMContext(facesContext, panelBorder);
        CustomComponentUtils.renderChild(facesContext, component);
    }
}