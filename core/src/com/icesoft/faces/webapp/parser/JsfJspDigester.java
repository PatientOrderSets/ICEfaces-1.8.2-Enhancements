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

package com.icesoft.faces.webapp.parser;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rules;
import org.xml.sax.Attributes;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

/**
 * A customized Digester that gives us access to the body text of the containing
 * tags.
 *
 * @author Steve Maryka
 */
public class JsfJspDigester extends Digester {

    Vector loadedNamespaces;
    String viewTagClassName;

    TagWire viewWire;

    /**
     * Constructor.
     */
    public JsfJspDigester() {
        super();
        loadedNamespaces = new Vector(); 
        // the html and core namespaces are dynamically loaded now. 
        // loadedNamespaces.add("http://java.sun.com/jsf/html");
        // loadedNamespaces.add("http://java.sun.com/jsf/core");
    }

    /**
     * Return the <code>Rules</code> implementation object containing our
     * rules collection and associated matching policy.  If none has been
     * established, a default implementation will be created and returned.
     */
    public Rules getRules() {

        if (this.rules == null) {
            this.rules = new RulesBase();
            this.rules.setDigester(this);
        }
        return (this.rules);

    }

    /**
     * This member gets the previous body text and returns it. It also clears
     * out that body text from the parent.
     *
     * @return The parent's body text.
     */
    public String stealParentBodyText() {

        StringBuffer parentBodyText = (StringBuffer) bodyTexts.peek();
        if (parentBodyText == null || parentBodyText.length() == 0) {
            return null;
        }

        String returnString = new String(parentBodyText.toString());
        if (returnString.trim().length() == 0) {
            // Don't want to create whitespace only components;
            returnString = null;
        }

        // Get rid of body text that we just processed;
        parentBodyText.delete(0, parentBodyText.length());

        return returnString;
    }

    public void startPrefixMapping(String prefix, String namespaceURI) {
        ExternalContext context = FacesContext
                .getCurrentInstance().getExternalContext();
        if (loadedNamespaces.contains(namespaceURI)) {
            return;
        }
        try {
            TagToComponentMap tagMap = new TagToComponentMap();
            InputStream tldStream = JspPageToDocument.getTldInputStream(
                    context, namespaceURI);
            if (null == tldStream) {
                if (log.isDebugEnabled()) {
                    log.debug("tldStream null");
                }
                return;
            }
            tagMap.addTags(tldStream);
            ComponentRuleSet rules = new ComponentRuleSet(tagMap, namespaceURI);
            rules.addRuleInstances(this);
            loadedNamespaces.add(namespaceURI);                                        

            if (log.isDebugEnabled()) {
                log.debug(
                        "JsfJspDigester loaded " + prefix + ":" + namespaceURI);
            }
        } catch (IOException e) {
            if (log.isDebugEnabled()) {
                log.debug(e.getMessage(), e);
            }
        }
    }

    /**
     * @deprecated should no longer be needed
     * @param className  name of class
     */
    public void setViewTagClassName(String className) {
        viewTagClassName = className;
    }

     /**
     * @deprecated should no longer be needed
     * @return className  parsed class name
     */
    public String getViewTagClassName() {
        return viewTagClassName;
    }


    
    public TagWire getViewWire() {
        return viewWire;
    }

    /**
     * Save the parsed ViewTag TagWire instance
     * #2551
     * @param viewWire The TagWire referencing the viewTag 
     */
    public void setViewWire(TagWire viewWire) {
        this.viewWire = viewWire;
    }
}
