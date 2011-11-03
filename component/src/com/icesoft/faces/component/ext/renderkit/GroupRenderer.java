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

package com.icesoft.faces.component.ext.renderkit;

import com.icesoft.faces.component.dragdrop.DndEvent;
import com.icesoft.faces.component.dragdrop.DragEvent;
import com.icesoft.faces.component.dragdrop.DropEvent;
import com.icesoft.faces.component.ext.HtmlPanelGroup;
import com.icesoft.faces.component.menupopup.MenuPopupHelper;
import com.icesoft.faces.component.ExtendedAttributeConstants;
import com.icesoft.faces.component.panelpopup.PanelPopup;
import com.icesoft.faces.component.util.DelimitedProperties;
import com.icesoft.faces.context.DOMContext;
import com.icesoft.faces.context.effects.CurrentStyle;
import com.icesoft.faces.context.effects.DragDrop;
import com.icesoft.faces.context.effects.JavascriptContext;
import com.icesoft.faces.context.effects.LocalEffectEncoder;
import com.icesoft.faces.renderkit.dom_html_basic.HTML;
import com.icesoft.faces.renderkit.dom_html_basic.PassThruAttributeRenderer;
import com.icesoft.faces.util.CoreUtils;
import com.icesoft.faces.utils.DnDCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.icesoft.util.pooling.ClientIdPool;

public class GroupRenderer
        extends com.icesoft.faces.renderkit.dom_html_basic.GroupRenderer {

    protected static final String STATUS = "status";

    protected static final String DROP = "dropID";
    protected static final String HIDDEN_FILED = ":iceDND";    
    private static Log log = LogFactory.getLog(GroupRenderer.class);

    // Basically, everything is excluded
    private static final String[] PASSTHRU_EXCLUDE =
        new String[] { HTML.STYLE_ATTR };
   
    
    private static final String[] PASSTHRU_JS_EVENTS = LocalEffectEncoder.maskEvents(
            ExtendedAttributeConstants.getAttributes(
                ExtendedAttributeConstants.ICE_PANELGROUP));
    private static final String[] passThruAttributes =
            ExtendedAttributeConstants.getAttributes(
                ExtendedAttributeConstants.ICE_PANELGROUP,
                new String[][] {PASSTHRU_EXCLUDE, PASSTHRU_JS_EVENTS});

    public void encodeBegin(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
        try {
            String viewID = facesContext.getViewRoot().getViewId();

            String style = ((HtmlPanelGroup) uiComponent).getStyle();
            String styleClass = ((HtmlPanelGroup) uiComponent).getStyleClass();
            String blockingFlag = (String) facesContext.getExternalContext()
                    .getRequestMap().get("BlockingServlet");

            String dndType = getDndType(uiComponent);
            DOMContext domContext =
                    DOMContext.attachDOMContext(facesContext, uiComponent);

            if (!domContext.isInitialized()) {
                Element rootSpan = domContext.createElement(HTML.DIV_ELEM);
                domContext.setRootNode(rootSpan);
                setRootElementId(facesContext, rootSpan, uiComponent);

                if (dndType != null) {
                    // Drag an drop needs some hidden fields
                    UIComponent form = findForm(uiComponent);
                    String formId = form.getClientId(facesContext);
                    
                    FormRenderer.addHiddenField(facesContext, ClientIdPool.get(formId+HIDDEN_FILED));
 
                }
            }

            Element rootSpan = (Element) domContext.getRootNode();
            if (dndType != null) {
                DnDCache.getInstance(facesContext, true).put(
                        uiComponent.getClientId(facesContext),
                        (HtmlPanelGroup) uiComponent, facesContext);
                String call = addJavascriptCalls(uiComponent, dndType, null, facesContext);
                
                Map rendererJavascriptDraggable =new HashMap();
                rendererJavascriptDraggable.put(HTML.ONMOUSEOUT_ATTR,
                            "Draggable.removeMe(this.id);");
                rendererJavascriptDraggable.put(HTML.ONMOUSEMOVE_ATTR, call);
                LocalEffectEncoder.encode(
                        facesContext, uiComponent, PASSTHRU_JS_EVENTS, 
                                    rendererJavascriptDraggable, rootSpan, null);                
            } else {
                LocalEffectEncoder.encode(
                        facesContext, uiComponent, PASSTHRU_JS_EVENTS, null, rootSpan, null);                
            }


            if (styleClass != null) {
                rootSpan.setAttribute("class", styleClass);
            }
            JavascriptContext.fireEffect(uiComponent, facesContext);
            String extraStyle = null;
            String scrollWidth =
                    (String) uiComponent.getAttributes().get("scrollWidth");
            String scrollHeight =
                    (String) uiComponent.getAttributes().get("scrollHeight");


            if (scrollHeight != null || scrollWidth != null) {
                if (extraStyle == null) {
                    extraStyle = "";
                }
                if (scrollHeight == null) {
                    extraStyle += "width:" + scrollWidth + ";overflow:auto;";
                } else if (scrollWidth == null) {
                    extraStyle += "height:" + scrollHeight + ";overflow:auto;";
                } else {
                    extraStyle += "width:" + scrollWidth + ";height:" +
                                  scrollHeight + ";overflow:auto;";
                }
            }

            CurrentStyle.apply(facesContext, uiComponent, null, extraStyle);
            MenuPopupHelper.renderMenuPopupHandler(facesContext, uiComponent, rootSpan);
            PassThruAttributeRenderer.renderNonBooleanHtmlAttributes(uiComponent, 
                                            rootSpan, passThruAttributes);           
            domContext.stepInto(uiComponent);
            // domContext.stepOver();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected String addJavascriptCalls(UIComponent uiComponent, String dndType,
                                        String handleId,
                                        FacesContext facesContext) {
        String calls = "";

        boolean dragListener =
                uiComponent.getAttributes().get("dragListener") != null;
        boolean dropListener =
                uiComponent.getAttributes().get("dropListener") != null;
        String dragMask = DndEvent.parseMask(
                (String) uiComponent.getAttributes().get("dragMask"));
        String dropMask = DndEvent.parseMask(
                (String) uiComponent.getAttributes().get("dropMask"));
        String dragOptions =
                (String) uiComponent.getAttributes().get("dragOptions");
        String hoverClass =
                (String) uiComponent.getAttributes().get("hoverclass");
        if (!dragListener) {
            if (dragMask == null) {
                dragMask = DndEvent.MASK_ALL_BUT_DROPS;
            }
        }
        if (!dropListener) {
            dropMask = DndEvent.MASK_ALL;
        }
        if ("DRAG".equalsIgnoreCase(dndType)) {

            calls += DragDrop.addDragable(uiComponent.getClientId(facesContext),
                                          handleId, dragOptions, dragMask,
                                          facesContext);

        } else if ("drop".equalsIgnoreCase(dndType)) {
            DragDrop.addDroptarget(
                    uiComponent, null, facesContext,
                    dropMask, hoverClass);
        } else if ("dragdrop".equalsIgnoreCase(dndType)) {

            calls += DragDrop.addDragable(uiComponent.getClientId(facesContext),
                                          handleId, dragOptions, dragMask,
                                          facesContext);
            DragDrop.addDroptarget(
                    uiComponent, null, facesContext,
                    dropMask, hoverClass);
        } else {
            throw new IllegalArgumentException("Value [" + dndType +
                                               "] is not valid for dndType. Please use drag or drop");
        }
        return calls;
    }


    public void encodeEnd(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
        validateParameters(facesContext, uiComponent, null);
        DOMContext domContext =
                DOMContext.getDOMContext(facesContext, uiComponent);
        CoreUtils.addPanelTooltip(facesContext, uiComponent);
        domContext.stepOver();

    }

    protected String appendStyle(String currentStyle, String additionalStyle) {
        String result = "";
        if (!isBlank(currentStyle)) {
            result = currentStyle;
        }
        if (!isBlank(additionalStyle)) {
            result += additionalStyle;
        }
        if (isBlank(result)) {
            return null;
        }
        return result;
    }

    private static boolean isBlank(String s) {
        return !(s != null && s.trim().length() > 0);
    }


    public void decode(FacesContext context, UIComponent component) {
        super.decode(context, component);
        String clientId = component.getClientId(context);
        if (log.isTraceEnabled()) {
            log.trace("GroupRenderer:decode");
        }
        MenuPopupHelper.decodeMenuContext(context, component);
        if (component instanceof PanelPopup) {
            String dndType = getDndType(component);
            if ("dragdrop".equals(dndType) || "DRAG".equals(dndType)) {
                PanelPopup panel = (PanelPopup) component;
                if (panel.getAutoPosition() != null || panel.isAutoCentre()) {
                    panel.setDragged(true);
                }
            }
        }
        if (component instanceof HtmlPanelGroup) {
            HtmlPanelGroup panel = (HtmlPanelGroup) component;
            String dndType = getDndType(component);

            if (panel.getDraggable() != null || panel.getDropTarget() != null) {
         
                Map requestMap = context.getExternalContext().getRequestParameterMap();
                
                UIComponent form = findForm(component);
                String formId = form.getClientId(context);
                String hdnFld = ClientIdPool.get(formId+HIDDEN_FILED);
                if (!requestMap.containsKey(hdnFld)) return;
                String value = String.valueOf(requestMap.get(hdnFld));
                DelimitedProperties delimitedProperties = new DelimitedProperties(value);
                
                String fieldName = clientId + STATUS;
                String status = delimitedProperties.get(fieldName);
                
                if (status == null) {
                    if (log.isTraceEnabled()) {
                        log.trace("Drag Drop Status for ID [" +
                                  panel.getClientId(context) +
                                  "] Field Name [" + fieldName +
                                  "] is null. Returning");
                    }
                    return;
                }
                String targetID = delimitedProperties.get(clientId + DROP);
                
                Object targetDragValue = null;
                Object targetDropValue = null;

                if (targetID != null && targetID.length() > 0) {
                    DnDCache dndCache = DnDCache.getInstance(context, false);
                    if ("drop".equals(dndType)) {
                        targetDragValue = dndCache.getDragValue(targetID);
                        targetDropValue = dndCache.getDropValue(panel.getClientId(context));                        
                    } else {                    
                        targetDragValue = dndCache.getDragValue(panel.getClientId(context));
                        targetDropValue = dndCache.getDropValue(targetID);
                    }
                }

                if (log.isTraceEnabled()) {
                    log.trace("Dnd Event Client ID [" +
                              component.getClientId(context) + "] Target ID [" +
                              targetID + "] Status [" + status + "]");
                }


                if (panel.getDragListener() == null &&
                    panel.getDropListener() == null) {

                    return;
                }
                int type = 0;
                try {
                    type = Integer.parseInt(status);
                } catch (NumberFormatException e) {
                    if (status != null || status.length() != 0)

                    {
                        return;
                    }
                }


                MethodBinding listener = panel.getDragListener();
                if (listener != null) {

                    DragEvent event = new DragEvent(component, type, targetID,
                                                    targetDragValue,
                                                    targetDropValue);
                    panel.queueEvent(event);
                }
                listener = panel.getDropListener();
                if (listener != null) {

                    DropEvent event = new DropEvent(component, type, targetID,
                                                    targetDragValue,
                                                    targetDropValue);
                    panel.queueEvent(event);
                }
            }
        }
    }


    protected Element createHiddenField(DOMContext domContext,
                                        FacesContext facesContext,
                                        UIComponent uiComponent, String name) {
        Element ele = domContext.createElement(HTML.INPUT_ELEM);
        ele.setAttribute(HTML.TYPE_ATTR, "hidden");
        String n = ClientIdPool.get(getHiddenFieldName(facesContext, uiComponent, name));
        ele.setAttribute(HTML.NAME_ATTR, n);
        ele.setAttribute(HTML.ID_ATTR, n);
        ele.setAttribute(HTML.VALUE_ATTR, "");
        return ele;
    }


    protected String getHiddenFieldName(FacesContext facesContext,
                                        UIComponent uiComponent, String name) {
        UIComponent form = findForm(uiComponent);
        String formId = form.getClientId(facesContext);
        String clientId = uiComponent.getClientId(facesContext);
        return formId
               + NamingContainer.SEPARATOR_CHAR
               + UIViewRoot.UNIQUE_ID_PREFIX
               + clientId
               + name;
    }

    protected String getDndType(UIComponent uiComponent) {
        String dndType = null;
        String draggable =
                (String) uiComponent.getAttributes().get("draggable");
        String droppable =
                (String) uiComponent.getAttributes().get("dropTarget");
        if ("true".equalsIgnoreCase(draggable) &&
            "true".equalsIgnoreCase(droppable)) {
            dndType = "dragdrop";
        } else if ("true".equalsIgnoreCase(draggable)) {
            dndType = "DRAG";
        } else if ("true".equalsIgnoreCase(droppable)) {
            dndType = "drop";
        }
        return dndType;
    }

    /**
     * Safri can return mutile values. The first one blank. This was a BIG
     * problem  to solve!
     *
     * @param sa
     * @return
     */
    private String getParamamterValue(String[] sa) {

        // bail if the sa array is null
        if (sa == null) {
            if (log.isTraceEnabled()) {
                log.trace("Null parameter value");
            }
            return null;
        }

        String result = null;
        for (int i = 0; i < sa.length; i++) {
            String s = sa[i];
            if (log.isTraceEnabled()) {
                log.trace("getParameterValue Checking [" + s + "]");
            }
            if (s != null && s.trim().length() > 0) {
                if (log.isTraceEnabled()) {
                    log.trace("getParameterValue result:" + s);
                }
                result = s;
            }
        }
        if (log.isTraceEnabled()) {
            log.trace("Length [" + sa.length + "] Result [" + result + "]");
        }
        return result;
    }


}
