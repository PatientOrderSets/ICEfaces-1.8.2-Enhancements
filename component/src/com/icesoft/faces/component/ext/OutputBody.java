package com.icesoft.faces.component.ext;


import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.icesoft.faces.application.D2DViewHandler;
import com.icesoft.faces.context.effects.JavascriptContext;

public class OutputBody extends javax.faces.component.UIComponentBase{
    private String alink;
    private String background;
    private String bgcolor;
    private String link;
    private String style;
    private String styleClass;
    private String text;
    private String vlink;
    private String focus;
    transient private String previousFocus;
    
    public OutputBody() {
        super();
        setRendererType("com.icesoft.faces.OutputBody");
    }
    /**
     * <p>Return the family for this component.</p>
     */
    public String getFamily() {
        return "com.icesoft.faces.OutputBody";
    }

    public String getAlink() {
        return (String) getAttribute("alink", alink, null);
    }

    public void setAlink(String alink) {
        this.alink = alink;
    }

    public String getBackground() {
        return (String) getAttribute("background", background, null);
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getBgcolor() {
        return (String) getAttribute("bgcolor", bgcolor, null);
    }

    public void setBgcolor(String bgcolor) {
        this.bgcolor = bgcolor;
    }

    public String getLink() {
        return (String) getAttribute("link", link, null);
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getStyle() {
        return (String) getAttribute("style", style, null);
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getStyleClass() {
        return (String) getAttribute("styleClass", styleClass, null);
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public String getText() {
        return (String) getAttribute("text", text, null);
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getVlink() {
        return (String) getAttribute("vlink", vlink, null);
    }

    public void setVlink(String vlink) {
        this.vlink = vlink;
    }
    
    public String getFocus(){
        return (String)getAttribute("focus",focus,null);
    }
    
    public void setFocus(String focus){
        this.focus = focus;
    }

    private Object getAttribute(String name, Object localValue, Object defaultValue) {
        if (localValue != null) return localValue;
        ValueBinding vb = getValueBinding(name);
        if (vb == null) return defaultValue;
        Object value = vb.getValue(getFacesContext());
        if (value == null) return defaultValue;
        return value;
    }
    
    public Object saveState(FacesContext context) {
        return new Object[]{
                super.saveState(context),
                alink,
                background,
                bgcolor,
                link,
                style,
                styleClass,
                text,
                vlink,
                focus
        };
    }

    public void restoreState(FacesContext context, Object state) {
        String[] attrNames = {
                "alink",
                "background",
                "bgcolor",
                "link",
                "style",
                "styleClass",
                "text",
                "vlink",
                "focus"
        };
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        for (int i = 0; i < attrNames.length; i++) {
            getAttributes().put(attrNames[i], values[i + 1]);
        }
    }
    
    public void encodeEnd(FacesContext context) throws IOException {
        super.encodeEnd(context);
        String focus = getFocus();
        if( focus != null ){
            setFocus(focus, context);
        } 
    }
 
    public void setFocus(String focus, FacesContext fc){
        UIComponent target = null;
        if( focus.indexOf(':') > -1){
            applyFocus(fc,focus);
          
        } else {
            target = D2DViewHandler.findComponentInView(this,focus);
            if( target != null){
                applyFocus(fc,target.getClientId(fc));            }            
        }
    }
    
    void applyFocus(FacesContext facesContext, String focus) {
        if (!focus.equals(previousFocus)){
            JavascriptContext.applicationFocus(facesContext,focus);
            previousFocus = focus;
        }
    }
}

