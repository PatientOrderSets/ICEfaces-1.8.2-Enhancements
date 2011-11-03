package com.icesoft.faces.webapp.http.core;

import java.util.Iterator;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.executor.FlowExecutionResult;
import org.springframework.webflow.executor.FlowExecutor;

/**
 *
 * Helper class used when launching or resuming Spring Web Flows
 *
 * @author Peter Bodskov
 */
public class FlowExecutorUtil {
	private FlowExecutor flowExecutor;
	private ExternalContext externalContext;


	public FlowExecutorUtil(FlowExecutor flowExecutor, ExternalContext externalContext){
		this.flowExecutor = flowExecutor;
		this.externalContext = externalContext;
	}

    public FlowExecutor getFlowExecutor() {
		return flowExecutor;
	}

	public void setFlowExecutor(FlowExecutor flowExecutor) {
		this.flowExecutor = flowExecutor;
	}

	public ExternalContext getExternalContext() {
		return externalContext;
	}

	public void setExternalContext(ExternalContext externalContext) {
		this.externalContext = externalContext;
	}

	protected MutableAttributeMap defaultFlowExecutionInputMap(HttpServletRequest request) {
        LocalAttributeMap inputMap = new LocalAttributeMap();
        Map parameterMap = request.getParameterMap();
        Iterator it = parameterMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String name = (String) entry.getKey();
            String[] values = (String[]) entry.getValue();
            if (values.length == 1) {
                inputMap.put(name, values[0]);
            } else {
                inputMap.put(name, values);
            }
        }
        return inputMap;
    }

    protected MutableAttributeMap defaultFlowExecutionInputMapPortlet(PortletRequest request) {
    	//Could probably be combined with the defaultFlowExecutionInputMap method who would then
    	//need to check what type of response was sent in. This would require the attribute
    	//to be of type Object though!
    	LocalAttributeMap inputMap = new LocalAttributeMap();
    	Map parameterMap = request.getParameterMap();
    	Iterator it = parameterMap.entrySet().iterator();
    	while (it.hasNext()) {
    		Map.Entry entry = (Map.Entry) it.next();
    		String name = (String) entry.getKey();
    		String[] values = (String[]) entry.getValue();
    		if (values.length == 1) {
    			inputMap.put(name, values[0]);
    		} else {
    			inputMap.put(name, values);
    		}
    	}
    	return inputMap;
    }


    protected FlowExecutionResult resumeExecution(String flowExecutionKey) {
		return flowExecutor.resumeExecution(flowExecutionKey, externalContext);
	}

	protected FlowExecutionResult launchExecution(MutableAttributeMap input, String flowId) {
        return flowExecutor.launchExecution(flowId, input, externalContext);
	}

}
