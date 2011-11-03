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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.webapp.UIComponentBodyTag;
import javax.faces.webapp.UIComponentTag;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.ArrayList;

/**
 * This is the JSFX parser.  It digests a JSFX file into a tag processing tree,
 * and then executes the JSP tag processing lifecycle over that tree to produce
 * the JSF component tree.
 * <p/>
 * A parser is initialized with a list of tags that it processes.  This list of
 * tags comes from a serialized version of a TagToComponentMap, which is passed
 * in as an application initialization parameter.  The parser should be created
 * and initialized by the ViewHandler implementation for the application.  Once
 * it is initialized, the parser can parse a jsf jsp and initialize the
 * component tree for the page.  The parser relies on the apache Digester, and a
 * set of rules created in the ComponentRuleSet class.
 *
 * @author Steve Maryka
 */
public class Parser {
    private static final Log log = LogFactory.getLog(Parser.class);
    private JsfJspDigester digester;

    private int indentIndex;

    public Parser(InputStream fis) throws IOException {
        // Create digester and add rules;
        digester = new JsfJspDigester();
        digester.setNamespaceAware(true);
        digester.setValidating(false);
        digester.setUseContextClassLoader(false);
        digester.setClassLoader(this.getClass().getClassLoader());

        try {
            TagToComponentMap map = TagToComponentMap.loadFrom(fis);
            digester.addRuleSet(new ComponentRuleSet(map, ""));
        } catch (ClassNotFoundException e) {
            throw new IOException(e.getMessage());
        } finally {
            fis.close();
        }
    }

    /**
     * The main parsing logic.  Creates a Digester to parse page into a tag
     * processing tree, and then executes the JSP lifecycle across that tree.
     * The end result is a JSF component rooted with a UIViewRoot component.
     *
     * @param page    The Reader for the page.
     * @param context
     * @throws java.io.IOException      If stream IO fails.
     * @throws org.xml.sax.SAXException If digester encounters invalid XML.
     */
    public void parse(Reader page, FacesContext context)
            throws java.io.IOException, org.xml.sax.SAXException {
        // Need a mock pageContext
        StubPageContext pageContext = new StubPageContext(context);
        Set componentIds = new HashSet();

        //placeholder tag and wire
        XhtmlTag rootTag = new XhtmlTag();
        rootTag.setTagName("ICEtag");

        rootTag.setParent(null);
        TagWire rootWire = new TagWire();
        rootWire.setTag(rootTag);

        TagWire realViewWire = null;
        String viewTagClassName = null;
        synchronized(this)  {
            digester.clear();
            digester.push(rootTag);
            digester.push(rootWire);
            digester.parse(page);
            realViewWire = digester.getViewWire();
            viewTagClassName = digester.getViewTagClassName();
        }

        try {

            // #2551 We have captured the real View Tag (from wherever it was in the tree)
            // now we check to see if it's the first child of this fake root, and
            // if not, make it so.
            // We do this by taking all top level siblings of the fake root, and
            // making them the first children of the real view root, and replacing
            // the original viewRoot with its children in the hiearchy.
            //  This duplicates what the previous code was doing, except the created
            // ViewTag has the attributes set

            Tag viewTag;
            if (null != realViewWire) {
                viewTag = realViewWire.getTag();
                transmogrifyHierarchy(realViewWire, rootWire);
                viewTag.setParent(null);
            } else {
                //fallback case for pages with no view tag
                if (null == viewTagClassName)
                    throw new IllegalStateException(
                            "ICEfaces parser unable to determine JSF implementation ViewTag class.");
                viewTag = (Tag) Class.forName(viewTagClassName).newInstance();
                rootWire.setTag(viewTag);
                realViewWire = rootWire;
                viewTag.setParent(null);
            }

            executeJspLifecycle(realViewWire, pageContext, context, componentIds);

            pageContext.removeAttribute(
                    "javax.faces.webapp.COMPONENT_TAG_STACK",
                    PageContext.REQUEST_SCOPE);
            pageContext.removeAttribute(
                    "javax.faces.webapp.GLOBAL_ID_VIEW",
                    PageContext.REQUEST_SCOPE);
        } catch (Exception e) {
            log.error("Failed to execute JSP lifecycle.", e);
            if (log.isDebugEnabled()) {

                log.debug("Dumping Tag Hierarchy");
                if (realViewWire != null) {
                    displayHierarchy(realViewWire);
                } else {
                    displayHierarchy(rootWire);
                }
            }
            throw new FacesException("Failed to execute JSP lifecycle.", e);
        }
    }


    /**
     * This member mimicks the JSP tag processing lifecyle across the tag
     * processing tree to produce the JSF component tree.
     *
     * @param wire         The tag's wire
     * @param pageContext  The page context
     * @param facesContext The faces context
     * @param componentIds
     * @throws JspException
     */
    public void executeJspLifecycle(TagWire wire, PageContext pageContext,
                                    FacesContext facesContext, Set componentIds)
            throws JspException {
        // Start of lifecycle;
        boolean processingViewTag = false;
        Tag tag = wire.getTag();
        tag.setPageContext(pageContext);
        // Start tag processing;
        tag.doStartTag();

        UIComponent myComponent = null; // reference will be used after doEndTag
        if (tag instanceof UIComponentTag) {
            UIComponentTag compTag = (UIComponentTag) tag;
            myComponent = compTag.getComponentInstance();

            if (myComponent != null) {
                if (myComponent instanceof UIViewRoot) {
                    myComponent.setId("_view");
                    processingViewTag = true;
                }

                String componentId = myComponent.getClientId(facesContext);
                if (componentIds.contains(componentId))
                    throw new IllegalStateException(
                            "Duplicate component ID : " + componentId);
                componentIds.add(componentId);
            }
        }

        // Now process tag children;
        Iterator children = wire.getChildren().iterator();
        while (children.hasNext()) {
            TagWire childWire = (TagWire) children.next();
            executeJspLifecycle(childWire, pageContext, facesContext,
                    componentIds);
        }
        //Do tag body processing. This is not full-fledged body processing. It only calls the doAfterBody() member
        if (!processingViewTag && tag instanceof UIComponentBodyTag) {
            UIComponentBodyTag bodyTag = (UIComponentBodyTag) tag;
            if (bodyTag.getBodyContent() == null) {
                //body content of custom tag should not be null, so create one in here to satisfy the
                //checking in jsf 1.0 impl.
                JspWriter jspWriter =
                        new JspWriterImpl(new PrintWriter(System.out));
                BodyContentImpl imp = new BodyContentImpl(jspWriter);
                bodyTag.setBodyContent(imp);
            }
            bodyTag.doAfterBody();
        }
        // Do end tag processing;
        tag.doEndTag();
        
        // ICE-3443: to save memory, trim the COMPONENT_IDS list capacity
        // to its actual size
        if (null != myComponent) {
            Map attributes = myComponent.getAttributes();
            ArrayList idsList = (ArrayList) attributes.get("javax.faces.webapp.COMPONENT_IDS");
            if (null != idsList) {
                idsList.trimToSize();
            }
        }
    }


    /**
     * This method will redo the topology of the parsed nodes. Previously, the
     * viewTag was not parsed, and all attributes were lost. Now, the tag is parsed
     * but it is no longer guaranteed to be the first child of the fake root node.
     * This method fixes the hierarchy to make it match the previous result
     * The changes required are to move the viewTag to be
     * the first and only child of the fake root Tag, and to replace the viewTag
     * in the hierarchy with its children, preserving order
     *
     * @param viewWire TagWire pointing to real parsed viewTag
     * @param rootWire tagWire pointing to fake Root Tag.
     */
    private void transmogrifyHierarchy(TagWire viewWire, TagWire rootWire) {

        Tag viewTag = viewWire.getTag();
        List rootChildren = rootWire.getChildren();

        // It should be impossible for the fake root to have no rootChildren. ?
        if ((rootChildren.size() == 0) ||
                (!((TagWire) rootChildren.get(0)).getTag().equals(viewTag))) {

            // The tag that originally had the viewTag as a child.
            TagWire viewTagParent = findViewTagInHierarchy(viewWire, rootWire);
            if (log.isDebugEnabled()) {
                log.debug("Replacing ViewTag in midst of hierarchy");
            }
            if (viewTagParent != null) {

                // Put all the viewTag children into where the viewTag was, respecting order
                viewTagParent.replaceTagWireWithChildren(viewWire);

                // reparent all viewTag children to new parent
                List viewTagParentChildren = viewTagParent.getChildren();
                for (int idx = 0; idx < viewTagParentChildren.size(); idx++) {
                    TagWire r = (TagWire) viewTagParentChildren.get(idx);
                    r.getTag().setParent(viewTagParent.getTag());
                }

                // now replace all children of viewTag with those of the fake root
                //do not add viewWire as its own child
                rootChildren.remove(viewWire);
                viewWire.getChildren().clear();
                viewWire.getChildren().addAll(rootChildren);

                // reparent
                for (int idx = 0; idx < rootChildren.size(); idx++) {
                    TagWire r = (TagWire) rootChildren.get(idx);
                    r.getTag().setParent(viewTag);
                }
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("ViewTag is already at top of hierarchy");
            }
        }
    }

    /**
     * Recursive method to locate the original parent of a given tag
     *
     * @param toFind The tagWire to find
     * @param parent The TagWire to look in
     * @return The TagWire parent containing the TagWire
     */
    private TagWire findViewTagInHierarchy(TagWire toFind, TagWire parent) {

        List children = parent.getChildren();
        if (children.contains(toFind)) {
            return parent;
        }
        // recurse through children of 'parent'
        TagWire child;
        for (int idx = 0; idx < children.size(); idx++) {
            child = findViewTagInHierarchy(toFind,
                    (TagWire) children.get(idx));
            if (child != null) {
                return child;
            }
        }
        return null;
    }

    /**
     * In event of parse exception, dump out the contents of the tagwire
     * tree. This is not very performant, but it doesn't need to be. Try to
     * give the author some idea as to why the exception. #ICE-2716
     *
     * @param wire The TagWire at the Root
     */
    private void displayHierarchy(TagWire wire) {

        char[] pad = new char[indentIndex];
        Arrays.fill(pad, ' ');
        log.debug(new String(pad) + "Tag: " + wire.getTag());

        indentIndex += 2;
        List children = wire.getChildren();
        for (int idx = 0; idx < children.size(); idx++) {
            displayHierarchy((TagWire) children.get(idx));
        }
        indentIndex -= 2;
    }
}
