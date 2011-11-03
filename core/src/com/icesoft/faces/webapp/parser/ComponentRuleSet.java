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
import org.apache.commons.digester.Rule;
import org.apache.commons.digester.RuleSetBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import javax.servlet.jsp.tagext.Tag;
import java.util.Enumeration;
import java.util.Hashtable;


/**
 * This class provides a ruleset for the Apache Digester, that supports parsing
 * of a JSFX page into a tree of JSP tag processing classes.  This tree is then
 * used in the parser to mimick the JSP lifecycle in order to produce the JSF
 * component tree.
 *
 * @author Steve Maryka
 */
public class ComponentRuleSet extends RuleSetBase {

    /**
     * The tag to tag processing class map.
     */
    private TagToComponentMap map;
    private String viewTagClass;
    private String ruleNamespace;
    private static String TAG_NEST = "*/";
    private static Class setPropertiesRuleClass = null;
    private static boolean isJSF12 = false;

    private static final Log log = LogFactory.getLog(ComponentRuleSet.class);

    static {
        try {
            setPropertiesRuleClass =
                    Class.forName(
                            "org.apache.commons.digester.SetPropertiesRule");
            //Test for JSF 1.2
            Class.forName("javax.faces.webapp.UIComponentELTag");
            setPropertiesRuleClass =
                    Class.forName(
                            "com.icesoft.faces.webapp.parser.ELSetPropertiesRule");
            if (log.isDebugEnabled()) {
                log.debug(
                        "ICEfaces using JSF 1.2 JSP tags.");
            }
            isJSF12 = true;
        } catch (Throwable t) {
            //many different throwables besides ClassNotFoundException
            if (log.isDebugEnabled()) {
                log.debug(
                        "No JSF 1.2 classes found. Running in JSF 1.1 environment");
            }
        }
    }

    /**
     * Constructor
     *
     * @param mp  The map from tags to tag processing classes
     * @param namespaceURI 
     */
    public ComponentRuleSet(TagToComponentMap mp, String namespaceURI) {
        super();
        map = mp;
        ruleNamespace = namespaceURI;
    }

    /**
     * Detect JSF 1.2 for special cases
     *
     * @return true if JSF 1.2 is being used
     */
    public static boolean isJSF12() {
        return isJSF12;
    }

    /**
     * Adds rules for each tag in the map.
     *
     * @param digester The digester to which rules are added.
     */
    public void addRuleInstances(Digester digester) {

        Hashtable table = map.getTagToComponentMap();
        Enumeration keys = table.keys();
        TagRule tagRule = new TagRule();
        XhtmlTagRule xhtmlTagRule = new XhtmlTagRule();
        ViewTagRule viewTagRule = new ViewTagRule();

        digester.setRuleNamespaceURI(ruleNamespace);

        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            String tagType = (String) table.get(key);

            if (!key.equals("view")) {


                // Want to create tag object regardless of type;
                digester.addObjectCreate(TAG_NEST + key, tagType);

                if (tagType.equals(XhtmlTag.class.getName())) {
                    // Rules for Xhtml Tags;
                    digester.addObjectCreate(TAG_NEST + key,
                                             "com.icesoft.faces.webapp.parser.TagWire");
                    digester.addRule(TAG_NEST + key, xhtmlTagRule);
                } else {
                    // Rules for JSF Tags;
                    try {
                        digester.addRule(TAG_NEST + key,
                                         (Rule) setPropertiesRuleClass
                                                 .newInstance());
                    } catch (Exception e) {
                        if (log.isDebugEnabled()) {
                            log.debug(e.getMessage(), e);
                        }
                    }
                    digester.addObjectCreate(TAG_NEST + key,
                                             "com.icesoft.faces.webapp.parser.TagWire");
                    digester.addRule(TAG_NEST + key, tagRule);
                }
            } else {

                // #2551 we do want to create the tag and wire for the viewTag
                digester.addObjectCreate(TAG_NEST + key, tagType);
                try {
                    digester.addRule(TAG_NEST + key,
                                     (Rule) setPropertiesRuleClass
                                             .newInstance());
                } catch (Exception e) {
                    if (log.isDebugEnabled()) {
                        log.debug(e.getMessage(), e);
                    }
                }
                digester.addObjectCreate(TAG_NEST + key,
                                         "com.icesoft.faces.webapp.parser.TagWire");
                digester.addRule(TAG_NEST + key, viewTagRule);

                // Capture the view tag class;
                ((JsfJspDigester) digester).setViewTagClassName(tagType);
                viewTagClass = tagType;
            }
        }
    }


    public String getViewTagClass() {
        return viewTagClass;
    }
}

/**
 * Contains base functionality for all rules.
 */
abstract class BaseRule extends Rule {

    /**
     * Constructor
     */
    public BaseRule() {
        super();
    }

    /**
     * Body processing for the rule.  The body text for this tag gets trimmed
     * up, and an outputTextTag is created to hold the body text.
     *
     * @param namespace Namespace provided by digester.
     * @param name      Name provided by digester.
     * @param text      The body text.
     */
    public void body(String namespace, String name, String text) {

        String bodyText = text.trim();
//        System.out.println("Base Rule handles body: " + name);
        if (bodyText.length() > 0) {
            TagWire wire = (TagWire) digester.peek();
            Tag child = (Tag) digester.peek(1);
//            System.out.println("TagWire: " + wire + " child: " + child);
            IceOutputTextTag bodyTextTag = new IceOutputTextTag();
            TagWire newWire = new TagWire();
            bodyTextTag.setValue(bodyText);
            wireUpTheTag((Tag) bodyTextTag, child, newWire, wire);
        }
    }

    /**
     * Because Tag processing classes themselves don't support a tree structure,
     * a wiring class is used to hold the tree together.  This member wires up
     * the tree appropriately.
     *
     * @param child      The child Tag
     * @param parent     The parent Tag
     * @param childWire  The child Wire
     * @param parentWire The parent Wire
     */
    protected void wireUpTheTag(Tag child, Tag parent,
                                TagWire childWire, TagWire parentWire) {

        child.setParent(parent);
        childWire.setTag(child);
        parentWire.addChild(childWire);
    }

    /**
     * This function peeks into the digester to see if there is body text
     * assiciated with the parent.  If there is, an Output Text tag is created
     * to hold that body text.
     *
     * @param parent     The parent Tag.
     * @param parentWire The parent Wire.
     */
    protected void dealWithPreceedingBodyText(Tag parent, TagWire parentWire) {
        /* This function peeks into the digester to see if there is body text
           associated with the parent.  If there is, an OutputText component is
           created to hold that body text */

        // In this version we create an IceOutputTextTag and wire it in;

        String bodyText = ((JsfJspDigester) digester).stealParentBodyText();
        if (bodyText != null) {
            IceOutputTextTag bodyTextTag = new IceOutputTextTag();
            TagWire wire = new TagWire();
            bodyTextTag.setValue(bodyText);
            wireUpTheTag((Tag) bodyTextTag, parent, wire, parentWire);
        }
    }
}

/**
 * This class provides the rule for processing JSF tags.  The tag attributes are
 * saved and the tag is wired into the tag processing tree.
 */
final class TagRule extends BaseRule {

    /**
     * Constructor.
     */
    public TagRule() {
        super();
    }

    /**
     * The begin processing for the rule.  Saves attributes, deals with parent
     * body text, and wires up tag processing tree.
     *
     * @param attributes Attributes for the Tag.
     * @throws Exception No exception is thrown.
     */
    public void begin(Attributes attributes) throws Exception {

        // Get all the important bits off the digester stack;
        TagWire wire = (TagWire) digester.peek();
        Tag child = (Tag) digester.peek(1);
        TagWire parentWire = (TagWire) digester.peek(2);
        Tag parent = (Tag) digester.peek(3);

        Attributes cloned = clone(attributes);
        wire.setAttributes(cloned);

        // Deal with preceeding body text;
        dealWithPreceedingBodyText(parent, parentWire);

        // Wire up the tree;
        wireUpTheTag(child, parent, wire, parentWire);
    }

    /**
     * Create clone of attributes.
     *
     * @param attributes Attributes to clone.
     * @return Cloned attributes.
     */
    private Attributes clone(Attributes attributes) {
        Attributes clone = new AttributesImpl(attributes);
        for (int i = 0; i < clone.getLength(); i++) {
            String name = attributes.getQName(i);
            String value = attributes.getValue(name);
            ((AttributesImpl) clone).setLocalName(i, name);
            ((AttributesImpl) clone).setValue(i, value);
        }
        return clone;
    }
}

/**
 * A rule for processing Xhtml Tags.  Need to save the tag name for future
 * processing.
 */
final class XhtmlTagRule extends BaseRule {

    /**
     * Constructor.
     */
    public XhtmlTagRule() {
        super();
    }

    /**
     * Begin processing for the rule.  Saves attributes, saves tag name, deals
     * with parent's preceeding body text, and wires up the tag processing
     * tree.
     *
     * @param attributes Attributes for the tag.
     * @throws Exception No exception is thrown.
     */
    public void begin(Attributes attributes) throws Exception {

        // Get all the important bits off the digester stack;
        TagWire wire = (TagWire) digester.peek();
        XhtmlTag child = (XhtmlTag) digester.peek(1);
        TagWire parentWire = (TagWire) digester.peek(2);
        Tag parent = (Tag) digester.peek(3);

        // Save attributes;
        child.setAttributes((Attributes) (new AttributesImpl(attributes)));

        // Save the tag;
        child.setTagName(new String(digester.getCurrentElementName()));
                                
        // Deal with preceeding body text;
        dealWithPreceedingBodyText(parent, parentWire);

        // Wire up the tree;
        wireUpTheTag((Tag) child, parent, wire, parentWire);
    }
}

/**
 * A rule for processing the View Tag.  Need to save the viewTag
 */
final class ViewTagRule extends BaseRule {

    /**
     * Constructor.
     */
    public ViewTagRule() {
        super();
    }

    /**
     * Begin processing for the rule.  Saves attributes, saves tag name, deals
     * with parent's preceeding body text, and wires up the tag processing
     * tree.
     *
     * @param attributes Attributes for the tag.
     * @throws Exception No exception is thrown.
     */
    public void begin(Attributes attributes) throws Exception {

        // Get all the important bits off the digester stack;
        TagWire wire = (TagWire) digester.peek();
        Tag child = (Tag) digester.peek(1);

        JsfJspDigester jsdig = (JsfJspDigester) digester;
        jsdig.setViewWire ( wire );
        
        TagWire parentWire = (TagWire) digester.peek(2);
        Tag parent = (Tag) digester.peek(3);

        // the attributes haven't been set up yet? !
        Attributes cloned = clone(attributes);
        wire.setAttributes(cloned);
       
//        dealWithPreceedingBodyText(parent, parentWire, true);

        // Wire up the tree;
        wireUpTheTag(child, parent, wire, parentWire);
    }

    private Attributes clone(Attributes attributes) {
        Attributes clone = new AttributesImpl(attributes);
        for (int i = 0; i < clone.getLength(); i++) {
            String name = attributes.getQName(i);
            String value = attributes.getValue(name);
            ((AttributesImpl) clone).setLocalName(i, name);
            ((AttributesImpl) clone).setValue(i, value);
        }
        return clone;
    }
}