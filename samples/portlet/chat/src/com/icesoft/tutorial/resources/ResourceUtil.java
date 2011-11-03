package com.icesoft.tutorial.resources;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * The ResourceUtil is used to retrieve localised messages and such from the resource
 * bundle.  It can also add localized FacesMessages to the chat page.
 */
public class ResourceUtil {

    private static final String BUNDLE = "com.icesoft.tutorial.resources.messages";

    public static void addMessage(String messagePatternKey) {
        String[] messageArgs = {};
        addMessage(messagePatternKey,messageArgs);
    }

    public static void addMessage(String messagePatternKey, String message) {
        String[] messageArgs = {message};
        addMessage(messagePatternKey,messageArgs);
    }

    public static void addMessage(String messagePatternKey, String[] messageArgs) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UIViewRoot root = facesContext.getViewRoot();
        Locale locale = root.getLocale();
        String localizedPattern = ResourceUtil.getI18NString(locale,messagePatternKey);
        String localizedMessage = MessageFormat.format(localizedPattern,(Object[])messageArgs);
        facesContext.addMessage(null,new FacesMessage(localizedMessage));
    }

    public static String getI18NString(Locale locale, String key) {

        String text = null;
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE, locale);
            text = bundle.getString(key);
        } catch (Exception e) {
            text = "?UNKNOWN?";
        }
        return text;
    }


}
