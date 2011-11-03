package com.icesoft.faces.utils;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;

/**
 * This class has been designed, so the custom components can get 
 * facesMessages either from the icefaces' ResourceBundle or an 
 * application's resourceBundle. The location of ice's messages.properties
 * is under com.icesoft.faces.resources package. 
 */

public class MessageUtils {
    private static Log log = LogFactory.getLog(MessageUtils.class);
    private static String DETAIL_SUFFIX = "_detail";
    private static int SUMMARY = 0;
    private static int DETAIL = 1;
    private static String ICE_MESSAGES_BUNDLE = "com.icesoft.faces.resources.messages";
    public static FacesMessage getMessage(FacesContext context, 
            String messageId) {
        return getMessage(context, messageId, null);
    }
    
    public static FacesMessage getMessage(FacesContext facesContext, 
            String messageId, Object params[]) {
        String messageInfo[] = new String[2];
        
        Locale locale = facesContext.getViewRoot().getLocale();
        String bundleName = facesContext.getApplication().getMessageBundle();
        //see if the message has been overridden by the application
        if (bundleName != null) {
            try {
                loadMessageInfo(bundleName, locale, messageId, messageInfo);
            } catch (Exception e)  {
                if(log.isWarnEnabled())
                    log.warn(e + ", using " + ICE_MESSAGES_BUNDLE);
            }
        }
        
        //TODO Use defered evaluation of the parameters, like how
        // JSF 1.2's javax.faces.component.MessageFactory.
        // BindingFacesMessage does. ICE-2290.
        
        //if not overridden then check in Icefaces message bundle.
        if (messageInfo[SUMMARY] == null && messageInfo[DETAIL]== null) {
            loadMessageInfo(ICE_MESSAGES_BUNDLE, locale, messageId, messageInfo);
        }
        if (params != null) {
            MessageFormat format;
            for (int i= 0; i <messageInfo.length; i++) {
                if (messageInfo[i] != null) {
                    format = new MessageFormat(messageInfo[i], locale);
                    messageInfo[i] = format.format(params);
                }
            }
        }
        return new FacesMessage(messageInfo[SUMMARY], messageInfo[DETAIL]);
    }
    
    private static void loadMessageInfo(String bundleName, 
                                Locale locale,
                                String messageId,  
                                String[] messageInfo) {
        ResourceBundle bundle = ResourceBundle.
                    getBundle(bundleName, locale, getClassLoader(bundleName));
        try {
            messageInfo[SUMMARY] = bundle.getString(messageId);
            messageInfo[DETAIL] = bundle.getString(messageId + DETAIL_SUFFIX);
        } catch (MissingResourceException e) {         
        }
    }
    
   
    public static ClassLoader getClassLoader(Object fallback) {
        ClassLoader classLoader = Thread.currentThread()
                                    .getContextClassLoader();
        if (classLoader == null) {
            classLoader = fallback.getClass().getClassLoader();
        }
        return classLoader;
    }
    
    public static String getResource(FacesContext facesContext, String messageId) {
        String ret = null;
        Locale locale = facesContext.getViewRoot().getLocale();
        String bundleName = facesContext.getApplication().getMessageBundle();
        //see if the message has been overridden by the application
        if (bundleName != null) {
            ret = getResource(bundleName, locale, messageId);
        }
        if(ret == null) {
            ret = getResource(ICE_MESSAGES_BUNDLE, locale, messageId);
        }
        return ret;
    }
    
    protected static String getResource(
        String bundleName, Locale locale, String messageId)
    {
        ResourceBundle bundle = ResourceBundle.getBundle(
            bundleName, locale, getClassLoader(bundleName));
        String ret = null;
        try {
            ret = bundle.getString(messageId);
        } catch(Exception e) {}
        return ret;
    }
    
    public static Object getComponentLabel(
        FacesContext context, UIComponent comp)
    {
        Object label = comp.getAttributes().get("label");
        if(nullOrEmptyString(label)) {
            //TODO When doing that MessageFactory stuff, uncomment this. ICE-2290.
            ////ValueBinding vb = comp.getValueBinding("label");
            ////if(vb != null) {
            ////    label = vb;
            ////}
            ////else {
                label = comp.getClientId(context);
            ////}
        }
        return label;
    }
    
    private static boolean nullOrEmptyString(Object ob) {
        return ( (ob == null) ||
                 ((ob instanceof String) && (ob.toString().length() == 0)) );
    }
}
