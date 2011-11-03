package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.context.DisposableBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

public class DisposeBeans {
    private static final Log log = LogFactory.getLog(DisposeBeans.class);

    public static void in(ServletContext context) {
        Enumeration enumeration = context.getAttributeNames();
        while (enumeration.hasMoreElements()) {
            dispose(context.getAttribute((String) enumeration.nextElement()));
        }
    }

    public static void in(HttpSession session) {
        Enumeration enumeration = session.getAttributeNames();
        while (enumeration.hasMoreElements()) {
            dispose(session.getAttribute((String) enumeration.nextElement()));
        }
    }

    public static void in(Map map) {
        Iterator iterator = new ArrayList(map.values()).iterator();
        while (iterator.hasNext()) {
            Object object = iterator.next();
            dispose(object);
        }
    }

    private static void dispose(Object object) {
        if (object instanceof DisposableBean) {
            try {
                ((DisposableBean) object).dispose();
            } catch (Exception e) {
                log.error("Failed to properly dispose " + object + " bean.", e);
            }
        }
    }
}
