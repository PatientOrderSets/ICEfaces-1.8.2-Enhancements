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
package org.icefaces.x.context;

import com.icesoft.faces.context.BridgeFacesContext;
import com.icesoft.faces.context.BridgeExternalContext;
import com.icesoft.faces.context.View;

import com.icesoft.faces.env.Authorization;
import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.core.ResourceDispatcher;
import com.icesoft.faces.webapp.http.servlet.SessionDispatcher;

import javax.faces.render.RenderKit;
import javax.faces.FactoryFinder;
import javax.faces.context.ExceptionHandlerFactory;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExternalContext;
import javax.faces.context.PartialViewContext;
import java.util.Map;
import java.util.HashMap;

/* This skeleton class is intended to allow experimentation with JSF 2.0
*/
public class BridgeFacesContext2 extends BridgeFacesContext  {
    public BridgeFacesContext2(final Request request, final String viewIdentifier, final String sessionID, final View view, final Configuration configuration, final ResourceDispatcher resourceDispatcher, final SessionDispatcher.Monitor sessionMonitor, final String blockingRequestHandlerContext, final Authorization authorization) throws Exception {
        super(request, viewIdentifier, sessionID, view, configuration, resourceDispatcher, sessionMonitor, blockingRequestHandlerContext, authorization);
    }
    
        javax.faces.event.PhaseId phaseId = null;

        public void setCurrentPhaseId(javax.faces.event.PhaseId currentPhaseId) {
            phaseId = currentPhaseId;
        }

        public javax.faces.event.PhaseId getCurrentPhaseId() {
            return phaseId;
        }

    private ExceptionHandler exceptionHandler = null;
    public ExceptionHandler getExceptionHandler() {
        if (null == exceptionHandler)  {
            ExceptionHandlerFactory exceptionHandlerFactory = (ExceptionHandlerFactory)
              FactoryFinder.getFactory(FactoryFinder.EXCEPTION_HANDLER_FACTORY);
            exceptionHandler = exceptionHandlerFactory.getExceptionHandler();
        }
        return exceptionHandler;
    }


    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }
    
    ExternalContext externalContext = null;
    public ExternalContext getExternalContext()  {
        if (null == externalContext)  {
            externalContext = new BridgeExternalContext2((BridgeExternalContext) super.getExternalContext());
        }
        return externalContext;
    }

    PartialViewContext partialViewContext  = null;

    public PartialViewContext getPartialViewContext() {
        if (null == partialViewContext)  {
            partialViewContext = new com.sun.faces.context.PartialViewContextImpl(this);
        }

        return partialViewContext;
    }

    private static final String POST_BACK_MARKER =
          BridgeFacesContext.class.getName() + "_POST_BACK";
          
    public boolean isPostback() {
        Boolean postback = (Boolean) this.getAttributes().get(POST_BACK_MARKER);
        if (postback == null) {
            postback = Boolean.FALSE;
            RenderKit rk = this.getRenderKit();
            if (rk != null) {
                postback = Boolean.valueOf(rk.getResponseStateManager().isPostback(this));
            } else {
                // ViewRoot hasn't been set yet, so calculate the RK
//                ViewHandler vh = this.getApplication().getViewHandler();
//                String rkId = vh.calculateRenderKitId(this);
//                postback = RenderKitUtils.getResponseStateManager(this, rkId)
//                      .isPostback(this);
            }
            
            this.getAttributes().put(POST_BACK_MARKER, postback);
        }
        BridgeExternalContext externalContext = (BridgeExternalContext) getExternalContext();
        String postbackKey = (String) externalContext.getRequestParameterMap().get(externalContext.PostBackKey);
        postback = Boolean.valueOf(postbackKey != null);
        return postback.booleanValue();
    }
    
    Map attributes = null;

    public Map getAttributes() {

        if (attributes == null) {
            attributes = new HashMap();
        }
        return attributes;

    }

}
