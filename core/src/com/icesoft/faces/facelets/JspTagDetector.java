package com.icesoft.faces.facelets;

import com.sun.facelets.tag.TagDecorator;
import com.sun.facelets.tag.Tag;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * When people are converting from JSP to Facelets, they can
 *  forget that they can't use JSP tags, and get confused as
 *  to why things aren't working. This will give detect JSP
 *  tags and log a debug message.
 * 
 * @author Mark Collette
 * @since 1.6
 */
public class JspTagDetector implements TagDecorator {
    private static Log log = LogFactory.getLog(TagDecorator.class);
    
    public JspTagDetector() {
        super();
    }
    
    /**
     * @see TagDecorator#decorate(com.sun.facelets.tag.Tag)
     */
    public Tag decorate(Tag tag) {
        if( log.isDebugEnabled() ) {
            if( tag.getNamespace() != null &&
                tag.getNamespace().startsWith("http://java.sun.com/JSP/") )
            {
                log.debug(
                    "A JSP tag has been detected in your Facelets page. "+
                    "Facelets is an alternative to JSP, so it is not valid " +
                    "to use JSP tags or directives here. The JSP tag:\n" + tag);
            }
        }
        return null;
    }
}
