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

package com.icesoft.faces.component.panelpositioned;

import com.icesoft.faces.context.DOMContext;
import com.icesoft.faces.context.effects.EffectsArguments;
import com.icesoft.faces.context.effects.JavascriptContext;
import com.icesoft.faces.renderkit.dom_html_basic.DomBasicRenderer;
import com.icesoft.faces.renderkit.dom_html_basic.HTML;
import com.icesoft.faces.utils.DnDCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import java.beans.Beans;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

import com.icesoft.util.pooling.ClientIdPool;

/**
 * Renderer for Positioned Panel
 */
public class PanelPositionedRenderer extends DomBasicRenderer {

    private static Log log = LogFactory.getLog(PanelPositionedRenderer.class);

    private static final String INPUT_ID = "colOrder";


    public boolean getRendersChildren() {
        return true;
    }

    public void encodeBegin(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {

        try {
            DOMContext domContext =
                    DOMContext.attachDOMContext(facesContext, uiComponent);
            if (!domContext.isInitialized()) {
                Element root = domContext.createRootElement(HTML.DIV_ELEM);
                domContext.setRootNode(root);
                setRootElementId(facesContext, root, uiComponent);


                String style = ((PanelPositioned) uiComponent).getStyle();
                String styleClass =
                        ((PanelPositioned) uiComponent).getStyleClass();
                if(style != null && style.length() > 0)
                    root.setAttribute(HTML.STYLE_ATTR, style);
                else
                    root.removeAttribute(HTML.STYLE_ATTR);
                if (styleClass != null && styleClass.length() > 0)
                    root.setAttribute(HTML.CLASS_ATTR, styleClass);
                else
                    root.removeAttribute(HTML.CLASS_ATTR);
                Element orderField = domContext.createElement(HTML.INPUT_ELEM);
                String orderFieldId = ClientIdPool.get(
                        getHiddenFieldName(facesContext, uiComponent, INPUT_ID));
                orderField.setAttribute(HTML.ID_ATTR, orderFieldId);
                orderField.setAttribute(HTML.TYPE_ATTR, HTML.INPUT_TYPE_HIDDEN);
                orderField.setAttribute(HTML.NAME_ATTR, orderFieldId);
                orderField.setAttribute(HTML.VALUE_ATTR, "");

                if (isChanged(facesContext)) {
                    // Force the re rendering of the entire component. This is due to a strange quick with positioned
                    // panel. When an element is moved in the same list then it container element moves with it
                    // When the update occurs the elements are replaced but because the containers have moved
                    // then the result looks the same. (But a refresh shows otherwise)
                    Node node = domContext.createTextNode(
                            "<!-- " + (new Random().nextInt(1000)) + "-->");
                    root.appendChild(node);
                }
                root.appendChild(orderField);

            }

            Element root = (Element) domContext.getRootNode();
            if (!Beans.isDesignTime()) {
                DOMContext.removeChildrenByTagName(root, HTML.DIV_ELEM);
            }
        } catch (Exception e) {
            log.error("Encode Begin", e);
        }
    }

    public void encodeEnd(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
        validateParameters(facesContext, uiComponent, null);
        DOMContext domContext =
                DOMContext.getDOMContext(facesContext, uiComponent);
        String id = uiComponent.getClientId(facesContext);
        PanelPositioned panelPositioned = (PanelPositioned) uiComponent;
        String orderFieldId =
                getHiddenFieldName(facesContext, uiComponent, INPUT_ID);
        EffectsArguments ea = new EffectsArguments();
        ea.add("tag", "div");
        String o = panelPositioned.getConstraint();
        if (o == null) {
            ea.add("constraint", false);
        } else {
            ea.add("constraint", panelPositioned.getConstraint());
        }
        ea.add("dropOnEmpty", true);
        ea.add("containment", false);
        o = panelPositioned.getHandle();
        if (o != null) {
            ea.add("handle", o);
        }
        o = panelPositioned.getHoverclass();
        if (o != null) {
            ea.add("hoverclass", o);
        }
        o = panelPositioned.getOverlap();
        if (o != null) {
            ea.add("overlap", o);
        }


        String updateCode =
                "function(){var o = Sortable.options('" + id + "');" +
                "var s = o.serializeValue;" +
                "f = $('" + orderFieldId + "');" +
                "f.value = s;" +
                "}";
        ea.addFunction("onUpdate", updateCode);

        if (!panelPositioned.isDisabled()) {
            String call = "Sortable.create('" + id + "'" + ea.toString();
            JavascriptContext.addJavascriptCall(facesContext, call);
        }
        DOMContext.getDOMContext(facesContext, uiComponent).stepOver();
    }


    public void encodeChildren(FacesContext facesContext,
                               UIComponent uiComponent)
            throws IOException {
        try {
            validateParameters(facesContext, uiComponent, null);
            DOMContext domContext =
                    DOMContext.getDOMContext(facesContext, uiComponent);

            Element root = (Element) domContext.getRootNode();
            PanelPositioned series = (PanelPositioned) uiComponent;
            List seriesList = (List) series.getValueAsList();
            if (seriesList != null) {
                if (log.isTraceEnabled()) {
                    for (int i = 0; i < seriesList.size(); i++) {
                        log.trace("Encode index[" + i + "] value [" +
                                  seriesList.get(i) + "]");
                    }
                }
                Iterator cells = seriesList.iterator();
                int index = 0;
                PanelPositionedModel ppm = PanelPositionedModel
                        .resetInstance(facesContext, uiComponent);
                while (cells.hasNext()) {
                    series.setRowIndex(index);
                    Object cell = cells.next();

                    if (uiComponent.getChildCount() > 0) {
                        Iterator childs;
                        childs = uiComponent.getChildren().iterator();
                        while (childs.hasNext()) {
                            UIComponent nextChild = (UIComponent) childs.next();
                            if (nextChild.isRendered()) {
                                domContext.setCursorParent(root);
                                encodeParentAndChildren(facesContext, nextChild);
                                String childId =
                                        nextChild.getClientId(facesContext);
    
                                ppm.setIndex(childId, index);
                                DnDCache.getInstance(facesContext, false)
                                        .putPositionPanelValue(childId, seriesList,
                                                               index);
                            }
                        }
                    }
                    index++;
                }
                series.setRowIndex(-1);
            }
            // set the cursor here since nothing happens in encodeEnd
            domContext.setCursorParent(root);
        } catch (Exception e) {
            log.error("Encode Children", e);
        }
    }

    public void decode(FacesContext context, UIComponent component) {

        try {
            super.decode(context, component);

            if (component instanceof PanelPositioned) {
                Map requestParameters =
                        context.getExternalContext().getRequestParameterMap();
                PanelPositioned uiSeries = (PanelPositioned) component;
                PanelPositionedModel sortOrder =
                        PanelPositionedModel.getInstance(context, component);

                String baseName =
                        getHiddenFieldName(context, component, INPUT_ID);
                Iterator names = requestParameters.keySet().iterator();
                names = requestParameters.keySet().iterator();

                while (names.hasNext()) {
                    String name = (String) names.next();
                    int lastIndex = -1;
                    if (name.equals(baseName)) {
                        String value = (String) requestParameters.get(name);

                        StringTokenizer st = new StringTokenizer(value, ";");
                        Object o = uiSeries.getValueAsList();
                        List newList = new ArrayList();
                        List oldList = null;

                        if (o instanceof List) {
                            oldList = (List) o;
                        } else {
                            throw new RuntimeException(
                                    "PanelPositioned must have a java.util.List instance set as " +
                                    "its value");
                        }
                        if (st.hasMoreTokens()) { // Don't do a thing if its blank
                            st.nextToken();//Last Token
                            String last = st.nextToken(); //Last ID dragged

                            lastIndex = sortOrder.getIndex(last);

                            String s = st.nextToken(); // Third token is always the keyword 'changed' used to indicate this filed has changed. (Even if its blank now)


                            int currentIndex = 0;

                            while (st.hasMoreTokens()) {
                                String id = st.nextToken();
                               
                                int index = sortOrder.getIndex(id);

                                if (index != -1) {
                                    Object obj = oldList.get(index);
                                    if (log.isTraceEnabled()) {
                                        log.trace("Moving ID[" + id +
                                                  "] Value [" + obj.toString() +
                                                  "] from index [" +
                                                  index + "] to [" +
                                                  currentIndex + "]");
                                    }
                                    newList.add(obj);

                                } else {
                                    // Value is not from this list, check the cache
                                    PanelPositionedValue ppv =
                                            DnDCache.getInstance(context, false)
                                                    .getPositionedPanelValue(
                                                            id);
                                    if (ppv != null) {
                                        List source = ppv.getSourceList();
                                        Object sourceValue =
                                                source.get(ppv.getValueIndex());
                                        if (log.isTraceEnabled()) {
                                            log.trace("Added value [" +
                                                      sourceValue + "]");
                                        }
                                        newList.add(sourceValue);

                                    } else {
                                        throw new RuntimeException(
                                                "Unable to find Value for ID[" +
                                                id + "]");
                                    }


                                }
                                currentIndex++;
                            }
                            int[] eventInfo = getEventInfo(oldList, newList);
                            int event_type = eventInfo[0];
                            int newIndex = eventInfo[1];
                            int oldIndex = eventInfo[2];
                            if(event_type == PanelPositionedEvent.TYPE_MOVE){

                                if(lastIndex != oldIndex){
                                    int a = newIndex;
                                    newIndex = oldIndex;
                                    oldIndex = a;

                                }
                            }
                          
                          


                            if (log.isTraceEnabled()) {
                                for (int i = 0; i < newList.size(); i++) {

                                    log.trace("New Index [" + i + "] Value [" +
                                            newList.get(i) + "]");

                                }
                            }
                            setChanged(context);



                            uiSeries.queueEvent(new PanelPositionedEvent(
                                    component, uiSeries.getListener(),
                                    event_type, newIndex, oldIndex, oldList, newList));

                        }

                    }
                }
            }
        } catch (Exception e) {
            log.error("Decode Error Positioned Panel ", e);
        }
    }

    private void setChanged(FacesContext context) {
        context.getExternalContext().getRequestMap()
                .put(PanelPositionedRenderer.class.getName(), Boolean.TRUE);
    }

    private boolean isChanged(FacesContext context) {
        Boolean b = (Boolean) context.getExternalContext().getRequestMap()
                .get(PanelPositionedRenderer.class.getName());
        if (b != null && b.booleanValue()) {
            return true;
        }
        return false;
    }


    private String getHiddenFieldName
            (FacesContext
                    facesContext, UIComponent
                    uiComponent, String
                    name) {
        UIComponent form = findForm(uiComponent);
        if(form == null){
            throw new NullPointerException("PanelPositioned must be contained withing an <ice:form>");
        }
        String formId = form.getClientId(facesContext);
        String clientId = uiComponent.getClientId(facesContext);
        return formId
               + NamingContainer.SEPARATOR_CHAR
               + UIViewRoot.UNIQUE_ID_PREFIX
               + clientId
               + name;
    }

    private int[] getEventInfo(List l1, List l2){
        int type;
        int newIndex = -1;
        int oldIndex = -1;
        if(l1.size() > l2.size()){
            type = PanelPositionedEvent.TYPE_REMOVE;
        }else if(l1.size() < l2.size()){
            type = PanelPositionedEvent.TYPE_ADD;
            List l = l1;
            l1 = l2;
            l2 = l;
        }else{
            type = PanelPositionedEvent.TYPE_MOVE;
            for(int i = 0; i < l1.size(); i++){
                if(l1.get(i) != l2.get(i)){
                    if(newIndex == -1)newIndex = i;
                    else oldIndex = i;
                }
            }
        }
        if(type != PanelPositionedEvent.TYPE_MOVE){
            for(int i = 0; i < l1.size(); i++){
                // Find the odd one
                if(!l2.contains(l1.get(i))){
                    newIndex = i;
                }
            }
        }
        return new int[]{type, newIndex, oldIndex};
    }


}
