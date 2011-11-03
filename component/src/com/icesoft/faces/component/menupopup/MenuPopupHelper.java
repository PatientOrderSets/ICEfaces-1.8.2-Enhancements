package com.icesoft.faces.component.menupopup;

import java.util.Map;

import org.w3c.dom.Element;

import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;

import com.icesoft.faces.application.D2DViewHandler;
import com.icesoft.faces.component.DisplayEvent;
import com.icesoft.faces.component.ext.HtmlPanelGroup;
import com.icesoft.faces.context.effects.JavascriptContext;
import com.icesoft.faces.renderkit.dom_html_basic.HTML;

/**
 * @author Mark Collette
 */
public class MenuPopupHelper {
    public static void renderMenuPopupHandler(
        FacesContext facesContext, UIComponent comp, Element elem)
    {
        StringBuffer handler = new StringBuffer(256);

        UIComponent menuPopup = findMenuPopup(comp);
        if(menuPopup != null) {
            String menuPopupClientId = menuPopup.getClientId(facesContext);
            String originatorClientId = comp.getClientId(facesContext);
//System.out.println("MenuPopupHelper.renderMenuPopupHandler()  menuPopupClientId: " + menuPopupClientId);
            handler.append("Ice.Menu.contextMenuPopup(event, '");
            handler.append(menuPopupClientId);
            handler.append("_sub', '");
            handler.append(originatorClientId);            
            handler.append("');");            
            handler.append("return false;");
        }
        
        if(handler.length() > 0) {
//System.out.println("MenuPopupHelper.renderMenuPopupHandler()  handler: " + handler.toString());
            elem.setAttribute(HTML.ONCONTEXTMENU_ATTR, handler.toString());
        }
        else
            elem.removeAttribute(HTML.ONCONTEXTMENU_ATTR);
        // oncontextmenu="Ice.Menu.contextMenuPopup('iceform:icepnltabset:outDesc');Ice.Menu.contextMenuPopup(event, 'iceform:icepnltabset:menuP_sub');return false;"
    }
    
    public static void decodeMenuContext(FacesContext facesContext, UIComponent comp) {
        
        processDisplayListener(facesContext, comp);
        
//System.out.println("MenuPopupHelper.decodeMenuContext()  for: " + comp.getClientId(facesContext));
        String requestMenuContext = (String) facesContext.getExternalContext().
            getRequestParameterMap().get("ice.menuContext");
        if(requestMenuContext == null || requestMenuContext.length() == 0)
            return;
//System.out.println("MenuPopupHelper.decodeMenuContext()    requestMenuContext: " + requestMenuContext);
        String originatorClientId = comp.getClientId(facesContext);
//System.out.println("MenuPopupHelper.decodeMenuContext()    originatorClientId: " + originatorClientId);
        if(!requestMenuContext.equals(originatorClientId))
            return;
//System.out.println("MenuPopupHelper.decodeMenuContext()    *** MATCH");
        
        UIComponent menuPopup = findMenuPopup(comp);
        if(menuPopup != null) {
            Object contextValue = comp.getAttributes().get("contextValue");
//System.out.println("MenuPopupHelper.decodeMenuContext()    contextValue: " + contextValue);
            menuPopup.getAttributes().put("contextTarget", comp);
            if(contextValue == null)
                menuPopup.getAttributes().remove("contextValue");
            else
                menuPopup.getAttributes().put("contextValue", contextValue);
        }
    }
    
    private static void processDisplayListener(FacesContext facesContext, UIComponent target) {
        Map requestMap =
            facesContext.getExternalContext().getRequestParameterMap();
         UIComponent menuPopupComponent = findMenuPopup(target);
         if(menuPopupComponent != null) {

            String clientId = menuPopupComponent.getClientId(facesContext);
            String displayListenerId = clientId + "_sub" + MenuPopup.DISPLAY_LISTENER_ID;
            if (requestMap.containsKey(displayListenerId) && 
                    requestMap.get("ice.event.captured").equals(displayListenerId) ) {
                String displayListenerValue = (String) requestMap.get(displayListenerId);
                if (displayListenerValue != null) {
                    String xy[] = displayListenerValue.split(",");
                    if (xy.length < 3) return;
                    if (!target.getClientId(facesContext).equals(xy[3].trim()) ) return;
                    JavascriptContext.addJavascriptCall(facesContext, "Ice.Menu.showIt('"+
                                xy[0]+"', '"+ xy[1] +"', '"+ xy[2]+ "', '"+ xy[3] +"');");
                    Object contextValue = ((HtmlPanelGroup)target).getContextValue();
                    target.queueEvent(new DisplayEvent(menuPopupComponent,
                            target,
                            contextValue,
                            true
                            ));
                }
            }    
         }
         
    }
    
    private static UIComponent findMenuPopup(UIComponent comp) {
        String id = (String) comp.getAttributes().get("menuPopup");
        if(id != null && id.trim().length() > 0) {
//System.out.println("MenuPopupHelper.findMenuPopup()  menuPopup: " + id);
            UIComponent menuPopup = D2DViewHandler.findComponent(id, comp);
            if(menuPopup == null) {
                //TODO Suggest potentials
                throw new IllegalArgumentException(
                    "Could not find the MenuPopup UIComponent referenced by " +
                    "attribute menuPopup=\""+id+"\" in UIComponent of type: " +
                    comp.getClass().getName() + " with id: \""+comp.getId()+"\"");
            }
            return menuPopup;
        }
        return null;
    }
}
