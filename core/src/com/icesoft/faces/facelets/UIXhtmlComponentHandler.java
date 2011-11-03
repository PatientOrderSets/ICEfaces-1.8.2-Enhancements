package com.icesoft.faces.facelets;

import com.sun.facelets.tag.jsf.ComponentHandler;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.el.LegacyValueBinding;
import com.icesoft.faces.component.UIXhtmlComponent;

import javax.faces.component.UIComponent;
import javax.faces.el.ValueBinding;
import javax.el.ValueExpression;

/**
 * @author Mark Collette
 * @since 1.6
 */
public class UIXhtmlComponentHandler extends ComponentHandler {
    public UIXhtmlComponentHandler(ComponentConfig componentConfig) {
        super(componentConfig);
    }

    /**
     * @see ComponentHandler#createComponent(FaceletContext)
     */
    protected UIComponent createComponent(FaceletContext ctx) {
//System.out.println("UIXhtmlComponentHandler.createComponent()  tag: " + tag + "  tagId: " + tagId);
        UIXhtmlComponent current = new UIXhtmlComponent();
        current.setCreatedByFacelets();
        current.setTag(tag.getQName());
        TagAttribute[] attribs = tag.getAttributes().getAll();
        for(int i = 0; i < attribs.length; i++) {
            String qName = attribs[i].getQName();
            if( attribs[i].isLiteral() ) {
                String value = attribs[i].getValue();
                current.addStandardAttribute(qName, value);
            }
            else {
                ValueExpression ve =
                        attribs[i].getValueExpression(ctx, Object.class);
                ValueBinding vb = new LegacyValueBinding(ve);
                current.addValueBindingAttribute(qName, vb);
            }
        }
        return current;
    }
    
    protected void setAttributes(FaceletContext ctx, Object instance) {
        // Do nothing here, since UIXhtmlComponent sets up its properties
        //  differently than other UIComponents
    }


    /*
    protected UIComponent applyToCurrent(FaceletContext ctx, UIComponent wrapped)
            throws IOException, FacesException {
        UIXhtmlComponent current = new UIXhtmlComponent();
        current.setCreatedByFacelets();
        current.setTag(tag.getQName());
        String id = null;
        TagAttribute[] attribs = tag.getAttributes().getAll();
        for(int i = 0; i < attribs.length; i++) {
            String qName = attribs[i].getQName();
            if( attribs[i].isLiteral() ) {
                String value = attribs[i].getValue();
                if( qName.equals("id") )
                    id = value;
                current.addStandardAttribute(qName, value);
            }
            else {
                // We don't always include the EL jars, so our
                //  code has to use reflection to refer to
                //  javax.el.ValueExpression
                Object value =
                        attribs[i].getValueExpression(ctx, Object.class);
                current.addELValueExpression(qName, value);
            }
        }
        // com.sun.facelets.tag.jsf.ComponentHandler.apply(-)
        //   assigns ids in a similar fashion
        if( id != null ) {
            current.setId( ctx.generateUniqueId(id) );
        }
        else {
            UIViewRoot root =
                    ComponentSupport.getViewRoot( ctx, wrapped );
            if( root != null ) {
                current.setId( root.createUniqueId() );
            }
        }
        return current;
    }
    */
}
