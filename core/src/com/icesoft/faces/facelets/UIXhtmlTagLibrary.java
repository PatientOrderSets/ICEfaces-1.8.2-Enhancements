package com.icesoft.faces.facelets;

import com.sun.facelets.tag.TagLibrary;
import com.sun.facelets.tag.TagHandler;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.Tag;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentHandler;
import com.sun.facelets.FaceletHandler;
import com.icesoft.faces.component.UIXhtmlComponent;

import javax.faces.FacesException;
import java.lang.reflect.Method;

/**
 * @author Mark Collette
 * @since 1.6
 */
public class UIXhtmlTagLibrary implements TagLibrary {
    static final String NAMESPACE =
            "http://www.icesoft.com/icefaces/html/internal";
    private static final String[] MATCHING_NAMESPACES = new String[] {
            NAMESPACE,
            "http://www.w3.org/1999/xhtml",
            ""
    };
    
    public UIXhtmlTagLibrary() {
        super();
    }
    
    /**
     * @see TagLibrary#containsNamespace(String)
     */
    public boolean containsNamespace(String ns) {
        boolean matches = false;
        if( ns == null )
            matches = true;
        else {
            for(int i = 0; i < MATCHING_NAMESPACES.length; i++) {
                if( ns.equals(MATCHING_NAMESPACES[i]) ) {
                    matches = true;
                    break;
                }
            }
        }
//System.out.println("UIXhtmlTagLibrary.containsNamespace()  ns: " + ns + "  ==> " + matches);
        return matches;
    }

    /**
     * @see TagLibrary#containsTagHandler(String, String)
     */
    public boolean containsTagHandler(String ns, String localName) {
//System.out.println("UIXhtmlTagLibrary.containsTagHandler()  ns: " + ns + "  localName: " + localName);
        return containsNamespace( ns );
    }

    /**
     * @see TagLibrary#createTagHandler(String, String, TagConfig)
     */
    public TagHandler createTagHandler(
        String ns, String localName, TagConfig tagConfig) throws FacesException
    {
//System.out.println("UIXhtmlTagLibrary.createTagHandler()  ns: " + ns + "  localName: " + localName + "  tagConfig: " + tagConfig);
        ComponentConfig ccfg = new ComponentConfigWrapper(
            tagConfig,
            UIXhtmlComponent.COMPONENT_FAMILY,
            UIXhtmlComponent.RENDERER_TYPE);
        return new UIXhtmlComponentHandler(ccfg);
    }

    /**
     * @see TagLibrary#containsFunction(String, String)
     */
    public boolean containsFunction(String ns, String name) {
        return false;
    }
    
    /**
     * @see TagLibrary#createFunction(String, String)
     */
    public Method createFunction(String ns, String name) {
        return null;
    }
    
    
    private static class ComponentConfigWrapper implements ComponentConfig {
        private final TagConfig wrapped;
        private final String componentType;
        private final String rendererType;

        public ComponentConfigWrapper(
            TagConfig parent, String componentType, String rendererType)
        {
            this.wrapped = parent;
            this.componentType = componentType;
            this.rendererType = rendererType;
        }
        
        public String getComponentType() {
            return componentType;
        }

        public String getRendererType() {
            return rendererType;
        }

        public FaceletHandler getNextHandler() {
            return wrapped.getNextHandler();
        }

        public Tag getTag() {
            return wrapped.getTag();
        }

        public String getTagId() {
            return wrapped.getTagId();
        }
    }
}
