package com.icesoft.faces.mock.test.data;

import javax.faces.el.MethodBinding;
import javax.faces.el.EvaluationException;
import javax.faces.el.MethodNotFoundException;
import javax.faces.context.FacesContext;
import javax.faces.component.StateHolder;
import java.io.Serializable;

public class MockMethodBinding
    extends MethodBinding implements StateHolder, Serializable {
    
    private String expression;
    private boolean transientFlag;
    
    public MockMethodBinding() {
        super();
        transientFlag = false;
    }
    
    public MockMethodBinding(String expression) {
        super();
        this.expression = expression;
    }
    
    public Object invoke(FacesContext facesContext, Object[] objects) throws EvaluationException, MethodNotFoundException {
        return null;
    }

    public Class getType(FacesContext facesContext) throws MethodNotFoundException {
        return Object.class;
    }
    
    public java.lang.String getExpressionString() {
        return expression;
    }
    
    public boolean equals(Object obj) {
        if (!(obj instanceof MockMethodBinding)) {
            return false;
        }
        MockMethodBinding mmb = (MockMethodBinding) obj;
        if (expression == null && mmb.expression == null) {
            return true;
        }
        if (expression == null && mmb.expression != null) {
            return false;
        }
        if (expression != null && mmb.expression == null) {
            return false;
        }
        return expression.equals(mmb.expression);
    }
    
    public int hashCode() {
        if (expression == null) {
            return 0;
        }
        return expression.hashCode();
    }
    
    public String toString() {
        return super.toString() + "{expression: "+expression+"}";
    }
    
    public boolean isTransient() {
        return transientFlag;
    }
    
    public void setTransient(boolean t) {
        transientFlag = t;
    }
    
    public Object saveState(FacesContext context){
        Object result = null;
        if (!transientFlag) {
            result = expression;
        }
        return result;
    }

    public void restoreState(FacesContext context, Object state) {
        if (null == state) {
            return;
        }
        expression = (String) state;
    }
}
