package com.icesoft.faces.component.ext;


import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

public class OutputHead extends javax.faces.component.UIComponentBase{
    private String dir;
    private String lang;
    private String profile;

    public OutputHead() {
        super();
        setRendererType("com.icesoft.faces.OutputHead");
    }
    /**
     * <p>Return the family for this component.</p>
     */
    public String getFamily() {
        return "com.icesoft.faces.OutputHead";
    }

    public String getDir() {
        return (String) getAttribute("dir", dir, null);
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getLang() {
        return (String) getAttribute("lang", lang, null);
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getProfile() {
        return (String) getAttribute("profile", profile, null);
    }

    public void setProfile(String profile) {
        this.profile = profile;
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
                dir,
                lang,
                profile,
        };
    }

    public void restoreState(FacesContext context, Object state) {
        String[] attrNames = {
                "dir",
                "lang",
                "profile",
        };
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        for (int i = 0; i < attrNames.length; i++) {
            getAttributes().put(attrNames[i], values[i + 1]);
        }
    }
}