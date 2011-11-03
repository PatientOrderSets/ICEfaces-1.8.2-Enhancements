package com.icesoft.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.StringTokenizer;
import java.util.ArrayList;

import javax.faces.event.PhaseListener;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.context.FacesContext;

import org.springframework.webflow.execution.RequestContextHolder;

/**
 * @author ICEsoft Technologies, Inc.
 *
 * Purpose of this class is to localize Seam Introspection code
 * in one place, and get rid of the variables cluttering up a few
 * of the ICEfaces classes 
 *
 *
 */
public class SeamUtilities {

     private static final Log log =
            LogFactory.getLog(SeamUtilities.class);

    // Seam vars

    private static Class seamManagerClass;
    
    private static Class[] seamClassArgs = new Class[0];
    private static Object[] seamInstanceArgs = new Object[0];
    private static Class[] seamGetEncodeMethodArgs = {String.class, String.class};
    private static Object[] seamEncodeMethodArgs = new Object[2];   
    private static Class[] seamGetSwitchConversationStackMethodArgs= {String.class};
    private static Object[] seamSwitchConversationStackMethodArgs = new Object[1];
    

    private static Object[] seamMethodNoArgs = new Object[0];

    private static Method seamConversationIdMethodInstance;
    private static Method seamLongRunningMethodInstance;
    private static Method seamAppendConversationMethodInstance;
    private static Method seamInstanceMethod;
    private static Method seamSwitchCurrentConversationIdInstanceMethod;
    
    // The method for getting the conversationId parameter name
    private static Method seamConversationIdParameterMethod;

    // This is just convenience, to avoid rebuilding the String
    private static String conversationIdParameter;
    private static String conversationParentParameter = "parentConversationId";
    
    // since seam1.3.0Alpha has api changes, detect which version we are using
    private static String seamVersion = "2.0.0.GA";
    
    private static String flowIdParameterName = null;
    private static String SPRING_CLASS_NAME = 
            "org.springframework.webflow.executor.jsf.FlowVariableResolver";
    private static String SPRING_CONTEXT_HOLDER = 
            "org.springframework.webflow.execution.RequestContextHolder";

    private static int springLoaded = 0;

    private static String SPRING_SECURITY_CONTEXT_HOLDER =
            "org.springframework.security.context.SecurityContextHolder";
    private static int springSecurityLoaded = 0;

    static {
        loadSeamEnvironment();
        loadSpringEnvironment();
        loadSpringSecurityEnvironment();
    }
    
    /**
     * Utility method to determine if the Seam classes can be loaded.
     *
     * @return true if Seam classes can be loaded
     */
    public static boolean isSeamEnvironment() {
        return seamManagerClass != null;
    }
    
    /**
     * Utility method to determine if D2DSeamFaceletViewHandler requires
     * SeamExpressionFactory Class
     * @return false if Seam version 1.3.0.ALPHA
     *         false otherwise
     */
    public static boolean requiresSeamExpressionFactory(){
    	return (seamVersion.startsWith("1.2.1"));
    }
    
    /**
     * In order to perform Seam's workspace management, we have to be able
     * to display various LR conversations within the same viewId ICE-2737
     * @param uri
     * @return
     */
    public static void switchToCurrentSeamConversation(String uri){
       if (conversationIdParameter == null) {
            getConversationIdParameterName();
       }
       String reqCid = stripCidFromRequest(uri);
       String ceId = getSeamConversationId();

       if (!reqCid.equals("") && !reqCid.equals(ceId)){
		   try{			   
			  int convId = Integer.parseInt(reqCid);
		      Object seamManagerInstance =
	                    seamInstanceMethod.invoke(null, seamInstanceArgs);
	          if (seamSwitchCurrentConversationIdInstanceMethod != null) {
	            	    seamSwitchConversationStackMethodArgs[0] = reqCid;
	          }
             Boolean updated =  (Boolean)seamSwitchCurrentConversationIdInstanceMethod
                   .invoke(seamManagerInstance, seamSwitchConversationStackMethodArgs);
	          if (log.isDebugEnabled()) {
                  log.debug("updated conversation from cid: " +
                       ceId + ", to: " + reqCid+"  successful="+updated);
              }		     		    		
		   }
		   catch (Exception e) {
		                seamInstanceMethod = null;
		                seamManagerClass = null;
		                log.error("Exception updating Seam's conversation Stack: ", e);
		   }
       }
    }
    
    /**
     * Called on a redirect to invoke any Seam redirection code. Seam uses
     * the sendRedirect method to preserve temporary conversations for the
     * duration of redirects. ICEfaces does not call this method, so this
     * method attempts to call the same Seam code introspectively. Seam will
     * encode the <code>conversationId</code> to the end of the argument URI.
     *
     * @param uri the redirect URI to redirect to, before the
     * conversationId is encoded
     * @return the URI, with the conversationId if Seam is detected
     */
    public static String encodeSeamConversationId(String uri, String viewId) {
    	//uri is request string to redirect to
        // If Seam's not loaded, no changes necessary
        if (! isSeamEnvironment() ) {
            return uri;
        }
        String cleanedUrl = uri;
        if (conversationIdParameter == null) {
            getConversationIdParameterName();
        }
       if (log.isTraceEnabled()) {
            log.trace("SeamConversationURLParam: " + conversationIdParameter);
       }
            
        StringTokenizer st = new StringTokenizer( uri, "?&");
        StringBuffer builder = new StringBuffer();

        String token;
        ArrayList tokenList = new ArrayList();

        while(st.hasMoreTokens() ){
            token = st.nextToken();
            if ( (token.indexOf(conversationIdParameter) == -1)  &&
                 (token.indexOf(conversationParentParameter) == -1) &&
                  token.indexOf("rvn") == -1 ) {

                tokenList.add( token );
            }
        }

        for (int pdx = 0; pdx < tokenList.size(); pdx ++ ) {
            token = (String) tokenList.get(pdx);             
            builder.append(token);

            if (pdx == 0 && (tokenList.size() > 1)) {
                builder.append('?');
            } else if (pdx > 0 && (pdx < tokenList.size() -1))  {
                builder.append('&');
            }
        }

        if (builder.length() > 0) {
            cleanedUrl = builder.toString();
           if (log.isTraceEnabled()) {
                log.trace("Cleaned URI: " + builder);
               
           }
        } 
        // The manager instance is a singleton, but it's continuously destroyed
        // after each request and thus must be re-obtained during each redirect.
        try {

            // Get the singleton instance of the Seam Manager each time through
            Object seamManagerInstance =
                    seamInstanceMethod.invoke(null, seamInstanceArgs);
            if (seamAppendConversationMethodInstance != null) {
                seamEncodeMethodArgs[0] = cleanedUrl;
                if (seamEncodeMethodArgs.length == 2) {
                    seamEncodeMethodArgs[1] = viewId;
                }
                
               // This has to do what the Manager.redirect method does.
                cleanedUrl = (String) seamAppendConversationMethodInstance
                        .invoke(seamManagerInstance, seamEncodeMethodArgs);
                
                if (log.isDebugEnabled()) {
                    log.debug("Enabled redirect from: " +
                            uri + ", to: " + cleanedUrl);
                }
            }
        } catch (Exception e) {
            seamInstanceMethod = null;
            seamManagerClass = null;
            log.error("Exception encoding seam conversationId: ", e);
        }
        return cleanedUrl;
    }

    /** 
     * helper class that strips the conversation id from the request to see
     * if we are doing conversation workspace switching.
     * @param uri
     * @return
     */
    private static String stripCidFromRequest(String uri){
    	String returnVal="";
        if (conversationIdParameter == null){ 
            getConversationIdParameterName();
        }
        int index = uri.indexOf(conversationIdParameter);
        int length=conversationIdParameter.length()+1;
        if (index>0 ){
            String substring = uri.substring(index);
           returnVal = uri.substring(index+length);
           /* have to check for other encoding for seam-1.2.1.*/
           int end = returnVal.indexOf('&');
           if (end>0){
        	   returnVal=returnVal.substring(0,end);
           }else returnVal = uri.substring(index+length);
        }
    	return returnVal;
    }
    
    /**
     * Retrieve the current Seam conversationId (if any) by introspection from
     * the SeamManager. The seam Conversation must be a long running
     * conversation, otherwise it isn't useful to encode it in the form. Long
     * running conversations are started by Seam components at various parts of
     * the application lifecycle, and their Id is necessary during a partial
     * submit to continue the thread of the conversation. 
     *
     * @return The current conversation id, or null if not a seam environment,
     *         or there is no current long running conversation.
     */
    public static String getSeamConversationId() {
        if ( !isSeamEnvironment()) {
            return null;
        }       
        String returnVal = null;
        try {
            // The manager instance is a singleton, but it's continuously
            // destroyed after each request and thus must be re-obtained.
            Object seamManagerInstance =
                    seamInstanceMethod.invoke(null, seamMethodNoArgs);

            if (seamConversationIdMethodInstance != null) {

                String conversationId =
                        (String) seamConversationIdMethodInstance.invoke(
                                seamManagerInstance, seamMethodNoArgs);
                
                Boolean is = (Boolean) seamLongRunningMethodInstance
                        .invoke(seamManagerInstance,
                                seamMethodNoArgs);
                
                if (is.booleanValue()) {
                    returnVal = conversationId;
                }
           }           
        } catch (Exception e) {
            seamInstanceMethod = null;
            seamManagerClass = null;
            log.error("Exception determining Seam ConversationId: ", e);
        }
        return returnVal;
    }


    /**
     * Attempt to load the classes from the Seam jars. The methods I
     * can locate and load here, but the values (for example, the
     * conversationIdParameter name which is not mutable) have to be
     * retrieved from a Manager instance when the Manager object is
     * available, and that is only during a valid EventContext. 
     */
    private static void loadSeamEnvironment() {
        try {
            // load classes
            seamManagerClass = Class.forName("org.jboss.seam.core.Manager");
            // load method instances
            seamInstanceMethod = seamManagerClass.getMethod("instance",
                                                            seamClassArgs);

            // for D2DSeamFaceletViewHandler need to know version 
            // to load compiler for facelet creation. See ICE-2405
           Class seamELResolver = null;
           try {
         	   seamELResolver = Class.forName("org.jboss.seam.jsf.SeamELResolver");
         	   if (seamELResolver !=null)seamVersion="1.2.1.GA";
            } catch (Exception e){
            	seamVersion="not1.2.1.GA";
            } 
        	if (log.isDebugEnabled())log.debug("\t ->>> Using seamVersion="+seamVersion);
            
            try {
                seamAppendConversationMethodInstance =
                        seamManagerClass.getMethod("encodeConversationId",
                                                   seamGetEncodeMethodArgs);
                seamSwitchCurrentConversationIdInstanceMethod = 
                        seamManagerClass.getMethod("switchConversation",
                        		seamGetSwitchConversationStackMethodArgs);
                	    
            } catch (NoSuchMethodException e)  {
                /* revert our reflectively discovered Seam method
                   to the Seam 1.2.0 API  
                */
                seamGetEncodeMethodArgs = new Class[] {String.class};
                seamEncodeMethodArgs = new Object[1];
                seamAppendConversationMethodInstance =
                        seamManagerClass.getMethod("encodeConversationId",
                                                   seamGetEncodeMethodArgs);
            }
            seamConversationIdMethodInstance =
                    seamManagerClass.getMethod("getCurrentConversationId",
                                               seamClassArgs);
            
            seamLongRunningMethodInstance =
                    seamManagerClass.getMethod("isLongRunningConversation",
                                               seamClassArgs);

            seamConversationIdParameterMethod =
                    seamManagerClass.getMethod("getConversationIdParameter",
                                               seamClassArgs);

        } catch (ClassNotFoundException cnf) {
//            log.info ("Seam environment not detected ");
        } catch (Exception e) {
            seamInstanceMethod = null;
            seamManagerClass = null;
            log.info("Exception loading seam environment: ", e);
        }
        
        if (seamManagerClass != null) {
            log.info("Seam environment detected ");
        }
    }


    /**
     * Seam 1.0.1 uses an element <code>'conversationId'</code> as the
     * parameter name, whereas Seam 1.1 has it as a configurable item. This method
     * will call the Manager instance to retrieve the current parameter name
     * defining containing the conversation ID. This method must only be called
     *  when the EventContext is valid (and thus the Manager
     * instance is retrievable). The parameter is configurable on a
     * per application basis, so it wont change at runtime.
     * 
     * <p>
     * Calling this method also fills in the conversationIdParameter,
     * the conversationIsLongRunningParameter, and the conversationParentIdParameter
     * fields, as they are all configurable, and used in the encoding conversation
     * id method
     * 
     * @return the appropriate parameter name for the application
     */
    public static String getConversationIdParameterName() {
        if (!isSeamEnvironment()) {
            return null;
        }
        if (conversationIdParameter != null ) {
            return conversationIdParameter;
        } 

        String returnVal = null;
        try {

            Object seamManagerInstance =
                    seamInstanceMethod.invoke(null, seamMethodNoArgs);

            // The method may not be available on all versions of Manager
            if (seamConversationIdParameterMethod != null) {

                returnVal = (String) seamConversationIdParameterMethod.
                        invoke(seamManagerInstance, seamMethodNoArgs);
                conversationIdParameter = returnVal;
            }


        } catch (Exception e) {
            log.error("Exception fetching conversationId Parameter name: ", e);

        }
        return returnVal;
    }

    /**
     * ICE-1084 : We have to turn off Seam's PhaseListener that makes
     *  it's debug page appear, so that our SeamDebugResourceResolver
     *  can do its work.
     * 
     * @param lifecycle The Lifecycle maintains the list of PhaseListeners
     */
    public static void removeSeamDebugPhaseListener(Lifecycle lifecycle) {
        PhaseListener[] phaseListeners = lifecycle.getPhaseListeners();
//  System.out.println("*** SeamUtilities.removeSeamDebugPhaseListener()");
// System.out.println("***   phaseListeners: " + phaseListeners.length);
        for(int i = 0; i < phaseListeners.length; i++) {
// System.out.println("***     phaseListeners["+i+"]: " + phaseListeners[i]);
            if( phaseListeners[i].getClass().getName().equals(
                "org.jboss.seam.debug.jsf.SeamDebugPhaseListener") )
            {
                lifecycle.removePhaseListener(phaseListeners[i]);
//System.out.println("***       REMOVED: " + phaseListeners[i]);
                seamDebugPhaseListenerClassLoader = phaseListeners[i].getClass().getClassLoader();
//System.out.println("******      SeamDebugPhaseListener.class.getClassLoader(): " + phaseListeners[i].getClass().getClassLoader());
            }
        }
    }
    
    public static ClassLoader getSeamDebugPhaseListenerClassLoader() {
        return seamDebugPhaseListenerClassLoader;
    }
    
    private static ClassLoader seamDebugPhaseListenerClassLoader;
    

    
    /**
     * Perform any needed Spring initialization.
     */
    private static void loadSpringEnvironment() {
        Class springClass = null;
        try {
            springClass = Class.forName(SPRING_CLASS_NAME);
        } catch (Throwable t)  {
            if (log.isDebugEnabled()) {
                log.debug("Spring webflow 1.x not detected: " + t);
            }
        }
        if (null != springClass) {
            springLoaded = 1;
            if (log.isDebugEnabled()) {
                log.debug("Spring webflow detected: " + springClass);
            }
        }

        springClass = null;
        try {
            springClass = Class.forName(SPRING_CONTEXT_HOLDER);
        } catch (Throwable t)  {
            if (log.isDebugEnabled()) {
                log.debug("Spring webflow 2.x not detected: " + t);
            }
        }
        if (null != springClass) {
            springLoaded = 2;
            if (log.isDebugEnabled()) {
                log.debug("Spring webflow detected: " + springClass);
            }
        }

    }

    /**
     * Perform any needed Spring Security initialization.
     */
    private static void loadSpringSecurityEnvironment() {
        Class springSecurityContextHolderClass = null;
        try {
            springSecurityContextHolderClass = Class.forName(SPRING_SECURITY_CONTEXT_HOLDER);
        } catch(Throwable t) {
            if(log.isDebugEnabled()) {
                log.debug("Spring Security not detected: " + t);
            }
        }
        if(null != springSecurityContextHolderClass) {
            springSecurityLoaded = 1;
            if(log.isDebugEnabled()) {
                log.debug("Spring Security detected: " + springSecurityContextHolderClass);
            }
        }        
    }

    /**
     * Utility method to determine if Spring WebFlow is active.
     *
     * @return true if Spring WebFlow is enabled
     */
    public static boolean isSpringEnvironment() {
        return (springLoaded > 0);
    }

    /**
     * Utility method to determine if Spring WebFlow 1.x is active.
     *
     * @return true if Spring WebFlow 1.x is enabled
     */
    public static boolean isSpring1Environment() {
        return (springLoaded == 1);
    }

    /**
     * Utility method to determine if Spring WebFlow 2.x is active.
     *
     * @return true if Spring WebFlow 2.x is enabled
     */
    public static boolean isSpring2Environment() {
        return (springLoaded == 2);
    }

    public static boolean isSpringSecurityEnvironment() {
        if(springSecurityLoaded == 0) {
            return false;
        }

        //classes are available, now look for an actual initialized security context
        Object authentication = null;

        try {
            final Class springSecurityContextHolderClass = Class.forName(SPRING_SECURITY_CONTEXT_HOLDER);
            final Method getContext = springSecurityContextHolderClass.getMethod("getContext", new Class[0]);
            final Object securityContext = getContext.invoke(null, new Object[0]);	
 
            final Method getAuthentication = securityContext.getClass().getMethod("getAuthentication", new Class[0]);
            authentication = getAuthentication.invoke(securityContext, new Object[0]);
        } catch(Throwable t) {
            if(log.isDebugEnabled()) {
                log.debug("Found Spring Security, but unable to retrieve authentication from context: " + t);
            }
        }
        return authentication != null;
    }

    /**
     * Retrieve the current Spring flowId (if any).
     *
     * @return The current Spring flowId.
     */
    public static String getSpringFlowId() {
        if ( !isSpringEnvironment()) {
            return null;
        }
        FacesContext facesContext = FacesContext.getCurrentInstance();
        //obtain key by evaluating expression with Spring VariableResolver
        Object value = null;
        if (1 == springLoaded)  {
            value = facesContext.getApplication()
                    .createValueBinding("#{flowExecutionKey}")
                    .getValue(facesContext);
            if (null != value)  {
                //Spring Web Flow 1.x confirmed
                flowIdParameterName = "_flowExecutionKey";
            }
        } else if (2 == springLoaded) {
            value = RequestContextHolder.getRequestContext()
                    .getFlowExecutionContext().getKey().toString();
            if (null != value)  {
                //Spring Web Flow 2.x confirmed
                flowIdParameterName = 
                        "org.springframework.webflow.FlowExecutionKey";
            }
        }

        if (null == value) {
            return null;
        }
        
        return value.toString();
    }
    /**
     * Return the parameter name for the Spring Flow Id
     *
     * @return the appropriate parameter name for the application
     */
    public static String getFlowIdParameterName() {
        return flowIdParameterName;
    }

}
