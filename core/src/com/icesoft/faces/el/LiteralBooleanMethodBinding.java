package com.icesoft.faces.el;

import javax.faces.el.MethodBinding;
import javax.faces.el.EvaluationException;
import javax.faces.el.MethodNotFoundException;
import javax.faces.context.FacesContext;
import java.io.Serializable;

/**
 * The EL that Facelets uses allows for a MethodBinding with
 *  "true" or "false" that will resolve to Boolean.TRUE or
 *  Boolean.FALSE.  The EL with JSF1.1-JSP doesn't seem so
 *  forgiving.  So we need this helper class to act as a
 *  MethodBinding, but just return a constant Boolean value.
 * 
 * @author Mark Collette
 * @since 1.6
 */
public class LiteralBooleanMethodBinding
    extends MethodBinding
    implements Serializable
{
    private String svalue;
    private Boolean value;
    
    public LiteralBooleanMethodBinding(String svalue) {
        this.svalue = svalue;
        this.value = resolve(svalue);
    }
    
    public Object invoke(FacesContext facesContext, Object[] objects)
        throws EvaluationException, MethodNotFoundException
    {
        return value;
    }
    
    public Class getType(FacesContext facesContext)
        throws MethodNotFoundException
    {
        return Boolean.class;
    }
    
    public String getExpressionString() {
        return svalue;
    }
    
    private static Boolean resolve(String value) {
        Boolean ret = Boolean.FALSE;
        if( value != null ) {
            try {
                ret = Boolean.valueOf(value);
            }
            catch(Exception e) {} // Leave it as Boolean.FALSE
        }
        return ret;
    }
}
