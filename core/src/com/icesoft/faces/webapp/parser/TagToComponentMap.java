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

import com.icesoft.jasper.xmlparser.ParserUtils;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;

import java.io.*;
import java.util.Hashtable;

/**
 * This class provides a map from TLD tag names to the tag processing class
 * associated with the tag.  The map is used by the parser to establish a
 * ruleset for the digester to use when parsing a JSFX page.
 *
 * @author Steve Maryka
 */
public class TagToComponentMap implements Serializable {

    public static final String XHTML_COMPONENT_TYPE =
            "com.icesoft.faces.XhtmlComponent";
    public static final String XHTML_COMPONENT_CLASS =
            "com.icesoft.faces.component.UIXhtmlComponent";
    private static ClassLoader loader =
            TagToComponentMap.class.getClassLoader();
    private static final Log log = LogFactory.getLog(TagToComponentMap.class);

    private Hashtable tagToComponentMap = new Hashtable();
    private Writer faceletsTaglibXmlWriter;

    private void setFaceletsTaglibXmlWriter(Writer writer) {
        faceletsTaglibXmlWriter = writer;
    }

    /**
     * Build the map from a serialized source.
     *
     * @param fis Input stream for the serialized data.
     * @return The map
     * @throws IOException
     * @throws ClassNotFoundException
     */
    static TagToComponentMap loadFrom(InputStream fis)
            throws IOException, ClassNotFoundException {
        try {
            ObjectInputStream ois = new ObjectInputStream(fis);
            return (TagToComponentMap) ois.readObject();
        } catch (IOException e) {
            log.error("Error building map from TLD tag names", e);
            throw e;
        } catch (ClassNotFoundException e) {
            log.error("Error building map from TLD tag names", e);
            throw e;
        }
        catch (Exception e) {
            return new TagToComponentMap();
        }
    }

    /**
     * Getter for TagToComponentMap
     *
     * @return The tag to tag processing class map.
     */
    public Hashtable getTagToComponentMap() {
        return tagToComponentMap;
    }

    /**
     * Takes a TLD file, parses it and build up map from tag name to tag
     * processing class.
     *
     * @param tldInput The TLD to process
     * @throws IOException If digester barfs.
     */
    public void addTags(InputStream tldInput) throws IOException {

        /*
          Use the digester to parse input file looking for <tag> entries, extract the <name>
          and extract the <tag-class> and build hash table for looking up component
          classes based on a tag name.
        */
        Digester digester = new Digester();
        digester.setNamespaceAware(true);
        digester.setValidating(false);
        digester.setEntityResolver(ParserUtils.entityResolver);
        digester.setUseContextClassLoader(false);

        /* Need to set the class loader to work.  Not sure why.
           May need to change when we move behind servlet container or Tomcat */
        digester.setClassLoader(loader);

        // This rule creates an element we can use to populate the map;
        digester.addObjectCreate("*/tag",
                "com.icesoft.faces.webapp.parser.TagToTagClassElement");
        digester.addObjectCreate("*/uri", "java.lang.StringBuffer");

        // This rule pushes everything into the hash table;
        NameRule nRule =
                new NameRule(tagToComponentMap, faceletsTaglibXmlWriter);
        digester.addRule("*/tag/tag-class", nRule);
        digester.addRule("*/tag/tagclass", nRule);
        digester.addRule("*/uri", nRule);

        // These rules scoop the values from <name> and <tag-class> elements;
        digester.addCallMethod("*/tag/name", "setTagName", 0);
        digester.addCallMethod("*/tag/tag-class", "setTagClass", 0);
        digester.addCallMethod("*/tag/tagclass", "setTagClass", 0);
        digester.addCallMethod("*/uri", "append", 0);

        try {
            digester.parse(tldInput);
        } catch (Throwable e) {
            IOException ioe = new IOException("Can't parse tld " + tldInput.toString());
            ioe.initCause(e);
            throw ioe;
        } finally {
            tldInput.close();
        }
    }


    /**
     * Main method for when this class is run to build the serialized data from
     * a set of TLDS.
     *
     * @param args The runtime arguements.
     */
    public static void main(String args[]) {

        /* arg[0] is "new" to create serialzed data or 'old' to read serialized data
           arg[1] is filename for serialized data;
           arg[2...] are tld's to process */

        FileInputStream tldFile = null;

        TagToComponentMap map = new TagToComponentMap();

        if (args[0].equals("new")) {
            // Build new component map from tlds and serialize it;

            for (int i = 2; i < args.length; i++) {
                try {
                    tldFile = new FileInputStream(args[i]);
                    map.addTags((InputStream) tldFile);
                }
                catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }

            try {
                FileOutputStream fos = new FileOutputStream(args[1]);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(map);
                oos.flush();
                oos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (args[0].equals("old")) {
            // Build component from serialized data;
            try {
                FileInputStream fis = new FileInputStream(args[1]);
                ObjectInputStream ois = new ObjectInputStream(fis);
                map = (TagToComponentMap) ois.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (args[0].equals("facelets")) {
            // Build new component map from tld, and use that to
            //  generate a Facelets taglib.xml
            // args[0] is command
            // args[1] is output taglib.xml
            // args[2] is input tld

            try {
                FileWriter faceletsTaglibXmlWriter = new FileWriter(args[1]);
                String preamble =
                        "<?xml version=\"1.0\"?>\n" +
                                "<!DOCTYPE facelet-taglib PUBLIC\n" +
                                "  \"-//Sun Microsystems, Inc.//DTD Facelet Taglib 1.0//EN\"\n" +
                                "  \"http://java.sun.com/dtd/web-facesconfig_1_0.dtd\">\n\n" +
                                "<facelet-taglib>\n";
                String trailer =
                        "</facelet-taglib>\n";
                faceletsTaglibXmlWriter.write(preamble);

                map.setFaceletsTaglibXmlWriter(faceletsTaglibXmlWriter);
                tldFile = new FileInputStream(args[2]);
                map.addTags((InputStream) tldFile);

                faceletsTaglibXmlWriter.write(trailer);
                faceletsTaglibXmlWriter.flush();
                faceletsTaglibXmlWriter.close();
            }
            catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}


/**
 * A digest rule for reading tags and creating map elements.
 */
final class NameRule extends Rule {
    // A class for adding tag name to current element;

    private Hashtable componentMap;
    private Writer faceletsTaglibXmlWriter;
    private String currentNamespace;

    private static final Log log = LogFactory.getLog(NameRule.class);

    /**
     * Constructor.
     *
     * @param map    The map being created.
     * @param writer
     */
    public NameRule(Hashtable map, Writer writer) {
        super();
        componentMap = map;
        faceletsTaglibXmlWriter = writer;
        currentNamespace = null;
    }

    /**
     * Do nothing in begin.
     *
     * @param attributes The tag attributes
     * @throws Exception No exception thrown.
     * @deprecated
     */
    public void begin(Attributes attributes) throws Exception {

    }

    /**
     * Puts the element into the map.
     *
     * @param namespace Not used
     * @param name      Not used
     */
    public void end(String namespace, String name) {
        if (name.equals("uri")) {
            if (faceletsTaglibXmlWriter != null) {
                try {
                    String ns = digester.peek().toString();
                    boolean namespaceChanged =
                            (ns != null && ns.length() > 0) &&
                                    (currentNamespace == null ||
                                            !currentNamespace.equals(ns));
                    if (namespaceChanged) {
                        currentNamespace = ns;
                        String nsOutput =
                                "	<namespace>" + currentNamespace +
                                        "</namespace>\n";
                        faceletsTaglibXmlWriter.write(nsOutput);
                        System.out.print(nsOutput);
                    }
                }
                catch (Exception e) {
                    System.out.println(
                            "Problem writing namespace to Facelets taglib.xml.  Exception: " +
                                    e);
                }
            }
            return;
        }

        TagToTagClassElement elem = (TagToTagClassElement) digester.peek();

        /* Don't want to duplicate tag entries.  Need JSF tags to be first though */
        if (componentMap.get(elem.getTagName()) != null) {
            if (log.isDebugEnabled()) {
                log.debug("Duplicate Tag " + elem.getTagName() +
                        " not processed");
            }
            return;
        }

        componentMap.put(elem.getTagName(), elem.getTagClass());
        if (log.isDebugEnabled()) {
            log.debug(
                    "Adding " + elem.getTagName() + ": " + elem.getTagClass());
        }

        if (faceletsTaglibXmlWriter != null) {
            try {
                String tagName = elem.getTagName();
                String tagClassStr = elem.getTagClass();
                if (tagName != null && tagClassStr != null &&
                        tagClassStr.indexOf("com.icesoft") >= 0) {
                    // We have to have special cases for any tags that
                    //  are not UIComponents, but are instead simply tags
                    // Map from JSP tag TabChangeListenerTag to
                    //  Facelets TabChangeListenerHandler
                    if (tagName.equals("tabChangeListener")) {
                        StringBuffer sb = new StringBuffer(256);
                        sb.append("\t<tag>\n\t\t<tag-name>");
                        sb.append(tagName);
                        sb.append("</tag-name>\n\t\t<handler-class>");
                        sb.append(
                                "com.icesoft.faces.facelets.TabChangeListenerHandler");
                        sb.append("</handler-class>\n\t</tag>\n");
                        faceletsTaglibXmlWriter.write(sb.toString());
                        System.out.print(sb.toString());
                    } else {
                        Class tagClass = Class.forName(tagClassStr);
                        Object tagObj = tagClass.newInstance();
                        java.lang.reflect.Method getComponentTypeMeth =
                                tagClass.getMethod("getComponentType",
                                        new Class[]{});
                        String componentType =
                                (String) getComponentTypeMeth.invoke(
                                        tagObj, new Object[]{});
                        java.lang.reflect.Method getRendererTypeMeth =
                                tagClass.getMethod("getRendererType",
                                        new Class[]{});
                        String rendererType =
                                (String) getRendererTypeMeth.invoke(
                                        tagObj, new Object[]{});

                        StringBuffer sb = new StringBuffer(256);
                        sb.append("\t<tag>\n\t\t<tag-name>");
                        sb.append(tagName);
                        sb.append(
                                "</tag-name>\n\t\t<component>\n\t\t\t<component-type>");
                        sb.append(componentType);
                        sb.append("</component-type>\n");
                        if (rendererType != null) {
                            sb.append("\t\t\t<renderer-type>");
                            sb.append(rendererType);
                            sb.append("</renderer-type>\n");
                        }
                        sb.append("\t\t\t<handler-class>com.icesoft.faces.component.facelets.IceComponentHandler</handler-class>\n");
                        sb.append("\t\t</component>\n\t</tag>\n");
                        faceletsTaglibXmlWriter.write(sb.toString());
                        System.out.print(sb.toString());
                    }
                }
            }
            catch (Exception e) {
                System.out.println(
                        "Problem writing tag to Facelets taglib.xml.  Tag name: " +
                                elem.getTagName() +
                                ", Tag class: " + elem.getTagClass() +
                                ", Exception: " + e);
            }
        }
    }
}
