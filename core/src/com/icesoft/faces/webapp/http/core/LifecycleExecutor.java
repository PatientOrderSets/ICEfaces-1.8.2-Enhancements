package com.icesoft.faces.webapp.http.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;

import com.icesoft.faces.env.SpringWebFlowInstantiationServlet;

import java.util.Map;

public abstract class LifecycleExecutor {
    private static Log log = LogFactory.getLog(LifecycleExecutor.class);

    private static String JSF_EXEC = "JSF Lifecyle Executor";
    private static String SWF_EXEC = "SWF Lifecyle Executor";

    public static LifecycleExecutor getLifecycleExecutor(FacesContext context)  {
        init(context);
        Map appMap = context.getExternalContext().getApplicationMap();
        Object swfExecObj = appMap.get(SWF_EXEC);
        if (null != swfExecObj)  {
            //Spring Web Flow URLs do not typically contain file extensions
            //this is not the correct way to determine whether to delegate
            //these requests
           if (!isExtensionMapped(context))  {
                return (LifecycleExecutor)swfExecObj;
            }
        }
        return (LifecycleExecutor)appMap.get(JSF_EXEC);
    }

    protected LifecycleExecutor getJsfLifecycleExecutor(FacesContext context){
        Map appMap = context.getExternalContext().getApplicationMap();
        return (LifecycleExecutor)appMap.get(JSF_EXEC);
    }

    public abstract void apply(FacesContext facesContext);

    private static void init(FacesContext context)  {
        Map appMap = context.getExternalContext().getApplicationMap();
        Object obj = appMap.get(JSF_EXEC);
        if (null != obj)  {
            return;
        } else {
            appMap.put(JSF_EXEC, new JsfLifecycleExecutor() );
        }

        Object flowExecutor = null;
        try {
            flowExecutor = SpringWebFlowInstantiationServlet.getFlowExecutor();
        } catch (Throwable t)  {
            if (log.isDebugEnabled()) {
                log.debug("SpringWebFlow unavailable ");
            }
        }
        if (null != flowExecutor)  {
            appMap.put(SWF_EXEC, new com.icesoft.faces.webapp.http.core.SwfLifecycleExecutor() );
        }
    }
    
    static boolean isExtensionMapped(FacesContext facesContext)  {
        Object request = facesContext.getExternalContext().getRequest();
        if (request instanceof HttpServletRequest)  {
            String requestURI = ((HttpServletRequest) request).getRequestURI();
            int slashIndex = requestURI.lastIndexOf("/");
            int dotIndex = requestURI.lastIndexOf(".");
            if (slashIndex < dotIndex) {
                return true;
            }
        }

        return false;
    }

}