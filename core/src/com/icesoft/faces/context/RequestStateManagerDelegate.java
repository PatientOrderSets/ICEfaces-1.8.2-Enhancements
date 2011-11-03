package com.icesoft.faces.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.context.FacesContext;
import java.util.Set;
import java.lang.reflect.Method;

//ICE-1900: A class for allowing us to call through to the com.sun.faces.util.RequestStateManager
//utility in the reference implementation classes.  We do it reflectively to avoid runtime
//dependency on the implementation.
public class RequestStateManagerDelegate {
    private static final Log log = LogFactory.getLog(RequestStateManagerDelegate.class);
    private static final String DELEGATE_CLASS_KEY = "com.sun.faces.util.RequestStateManager";
    private static final String PENDING_MESSAGES_KEY = "com.sun.faces.clientIdMessagesNotDisplayed";
    private static boolean detected = false;

    private static Method containsKeyMethod = null;
    private static Method getMethod = null;

    static {
        try {
            Class rsmClass = Class.forName(DELEGATE_CLASS_KEY);
            containsKeyMethod = rsmClass.getMethod("containsKey", new Class[]{FacesContext.class, String.class});
            getMethod = rsmClass.getMethod("get", new Class[]{FacesContext.class, String.class});
            detected = true;
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug(DELEGATE_CLASS_KEY + " was not detected");
            }
            detected = false;
        }
    }

    private RequestStateManagerDelegate(){
    }

    public static void clearMessages(FacesContext fc) {

        if (!detected || fc == null) {
            return;
        }

        if (containsKey(fc)) {
            Set pendingMessageIds = getPendingMessageIds(fc);
            if (pendingMessageIds != null) {
                pendingMessageIds.clear();
            }
        }

    }

    public static void clearMessages(FacesContext fc, String clientID) {

        if (!detected || fc == null || clientID == null) {
            return;
        }

        Set pendingMessageIds = getPendingMessageIds(fc);
        if (pendingMessageIds != null && !pendingMessageIds.isEmpty()) {
            pendingMessageIds.remove(clientID);
        }

    }

    private static boolean containsKey(FacesContext fc) {
        try {
            Object containsKeyResult =
                    containsKeyMethod.invoke(null, new Object[]{fc, PENDING_MESSAGES_KEY});
            return ((Boolean) containsKeyResult).booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private static Set getPendingMessageIds(FacesContext fc) {
        try {
            Object getResult =
                    getMethod.invoke(null, new Object[]{fc, PENDING_MESSAGES_KEY});
            return (Set) getResult;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}