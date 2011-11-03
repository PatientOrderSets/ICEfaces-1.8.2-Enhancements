package com.icesoft.faces.facelets;

import com.sun.facelets.tag.TagDecorator;
import com.sun.facelets.tag.Tag;

/**
 * @author Mark Collette
 * @since 1.6
 */
public class UIXhtmlTagDecorator implements TagDecorator {
    public UIXhtmlTagDecorator() {
        super();
    }

    /**
     * @see TagDecorator#decorate(com.sun.facelets.tag.Tag)
     */
    public Tag decorate(Tag tag) {
//System.out.println("UIXhtmlTagDecorator.decorate()  tag: " + tag + "  tag-ns: " + tag.getNamespace());
        if( tag.getNamespace() == null || tag.getNamespace().length() == 0 ) {
            Tag newTag = new Tag(
                tag.getLocation(),
                UIXhtmlTagLibrary.NAMESPACE,
                tag.getLocalName(),
                tag.getQName(),
                tag.getAttributes() );
//System.out.println("UIXhtmlTagDecorator.decorate()    newTag: " + newTag);
            return newTag;
        }
        return null;
    }
}
