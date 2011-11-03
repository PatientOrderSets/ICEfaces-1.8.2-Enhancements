
/*
 *  Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 *  "The contents of this file are subject to the Mozilla Public License
 *  Version 1.1 (the "License"); you may not use this file except in
 *  compliance with the License. You may obtain a copy of the License at
 *  http://www.mozilla.org/MPL/
 *
 *  Software distributed under the License is distributed on an "AS IS"
 *  basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 *  License for the specific language governing rights and limitations under
 *  the License.
 *
 *  The Original Code is ICEfaces 1.5 open source software code, released
 *  November 5, 2006. The Initial Developer of the Original Code is ICEsoft
 *  Technologies Canada, Corp. Portions created by ICEsoft are Copyright (C)
 *  2004-2006 ICEsoft Technologies Canada, Corp. All Rights Reserved.
 *
 *  Contributor(s): _____________________.
 *
 *  Alternatively, the contents of this file may be used under the terms of
 *  the GNU Lesser General Public License Version 2.1 or later (the "LGPL"
 *  License), in which case the provisions of the LGPL License are
 *  applicable instead of those above. If you wish to allow use of your
 *  version of this file only under the terms of the LGPL License and not to
 *  allow others to use your version of this file under the MPL, indicate
 *  your decision by deleting the provisions above and replace them with
 *  the notice and other provisions required by the LGPL License. If you do
 *  not delete the provisions above, a recipient may use your version of
 *  this file under either the MPL or the LGPL License."
 *
 */

package com.icesoft.metadata.generators;

import com.icesoft.jsfmeta.util.AbstractGenerator;
import com.icesoft.jsfmeta.util.InternalConfig;
import com.icesoft.metadata.test.DefaultValueTest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import com.sun.rave.jsfmeta.beans.AttributeBean;
import com.sun.rave.jsfmeta.beans.ComponentBean;
import com.sun.rave.jsfmeta.beans.DescriptionBean;
import com.sun.rave.jsfmeta.beans.PropertyBean;
import com.sun.rave.jsfmeta.beans.RendererBean;
import java.util.logging.Level;

/*
 * DescriptorGenerator generate tld file
 *
 *
 */
public class TLDGenerator extends AbstractGenerator {

        
    private TreeMap attributes;
    
    private PrintWriter writer;
    
    private boolean base;
    
    private static PropertyBean binding;
    
    private String descriptor;
    
    private String listeners[];
    
    private String prefix;
    
    private String tagClassPackage;
    
    private String uri;
    
    private String validators[];
    
    private String defaultValuesFileName;
    
    private PrintWriter defaultValuesWriter;    
    
    static {
        binding = new PropertyBean();
        binding.setPropertyName("binding");
        String db_string = "The value binding expression linking this component to a property in a backing bean";
        DescriptionBean dbBean = new DescriptionBean("", db_string);
        binding.addDescription(dbBean);
        binding.setRequired(false);
    }
    
    public TLDGenerator(InternalConfig internalConfig) {
        
        super(internalConfig);
        attributes = new TreeMap();
        writer = null;
        base = false;
        descriptor = internalConfig.getProperty("project.tld.file");
        listeners = null;
        prefix = internalConfig.getProperty("project.taglib.prefix");
        tagClassPackage = internalConfig.getProperty("project.taglib.package");
        uri = internalConfig.getProperty("project.taglib.uri");
        validators = null;
        defaultValuesFileName = internalConfig.getProperty("project.icefaces.defaultValuesFileName");
    }
    
    public boolean getBase() {
        return base;
    }
    
    public void setBase(boolean base) {
        this.base = base;
    }
    
    public String getDescriptor() {
        return descriptor;
    }
    
    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }
    
    public String[] getListeners() {
        return listeners;
    }
    
    public void setListeners(String listeners[]) {
        this.listeners = listeners;
    }
    
    public String getPrefix() {
        return prefix;
    }
    
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    
    public String getTagClassPackage() {
        return tagClassPackage;
    }
    
    public void setTagClassPackage(String tagClassPackage) {
        this.tagClassPackage = tagClassPackage;
    }
    
    public String getURI() {
        return uri;
    }
    
    public void setURI(String uri) {
        this.uri = uri;
    }
    
    public String[] getValidators() {
        return validators;
    }
    
    public void setValidators(String validators[]) {
        this.validators = validators;
    }
    
    private String getDefaultValuesFileName() {
        return defaultValuesFileName;
    }
    
    public void generate() throws IOException {
        
        File outputFile = new File(getDest(), getDescriptor());
        File defaultValuesFile = null;
        
        if (getDefaultValuesFileName() != null) {
            defaultValuesFile = new File(getDest(), getDefaultValuesFileName());
            defaultValuesFile.mkdirs();
            defaultValuesFile.delete();
            defaultValuesWriter = new PrintWriter(new BufferedWriter(new FileWriter(defaultValuesFile)));
            defaultValuesWriter.println("<components>");
        }
        outputFile.mkdirs();
        outputFile.delete();
        writer = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));
        logger.log(Level.FINEST, "Generate " + outputFile.getAbsoluteFile());
        license();
        header();
        validators();
        listeners();
        components();
        jspTagOnly();
        
        footer();
        writer.flush();
        writer.close();
        
        if (getDefaultValuesFileName() != null) {
            defaultValuesWriter.println("</components>");            
            defaultValuesWriter.flush();
            defaultValuesWriter.close();
            new DefaultValueTest(defaultValuesFile.getAbsolutePath());
        }
        
    }
    
    private void attribute(ComponentBean cb, RendererBean rb, PropertyBean pb)
    throws IOException {
        if ("com.icesoft.faces.component.menubar.MenuBar".equals(cb.getComponentClass()) 
                && (pb.getPropertyName().equals("action") ||
                        pb.getPropertyName().equals("actionListener"))) {
            return;
        }
        else if ("com.icesoft.faces.component.menupopup.MenuPopup".equals(cb.getComponentClass()) 
                && (pb.getPropertyName().equals("action") ||
                        pb.getPropertyName().equals("actionListener"))) {
            return;
        }
        StringBuffer sb = new StringBuffer();
        sb.append("    <attribute>\n");
        sb.append("      <name>" + pb.getPropertyName() + "</name>\n");
        sb.append("      <required>" + pb.isRequired() + "</required>\n");
        if (getDefaultValuesFileName() != null) {
            StringBuffer dvsb = new StringBuffer();
            dvsb.append("      <attribute>\n");             
            dvsb.append("      <name>" + pb.getPropertyName() + "</name>\n"); 
            dvsb.append("      <default-value>" + pb.getDefaultValue() + "</default-value>\n"); 
            dvsb.append("      </attribute>\n");
            defaultValuesWriter.write(dvsb.toString());
        }
        sb.append("      <rtexprvalue>false</rtexprvalue>\n");
        if (isVerbose()) {
            AttributeBean ab = rb.getAttribute(pb.getPropertyName());
            DescriptionBean db = null;
            if (ab != null && !cb.getComponentType().startsWith("com.icesoft.faces"))
                db = ab.getDescription("");
            else
                db = pb.getDescription("");
            if (db != null) {
                sb.append("      <description><![CDATA[\n");
                sb.append("        " + db.getDescription() + "\n");
                sb.append("      ]]></description>\n");
            }
        }
        sb.append("    </attribute>\n\n");
        attributes.put(pb.getPropertyName(), sb.toString());
    }
    
    private void attributes(ComponentBean cb, RendererBean rb)
    throws IOException {
        attribute(cb, rb, binding);
        attributes(cb, rb, ((Set) (new HashSet())));
    }
    
    private void attributes(ComponentBean cb, RendererBean rb, Set set)
    throws IOException {
        PropertyBean pbs[] = cb.getProperties();
        if (pbs == null){
            pbs = new PropertyBean[0];
        }
        for (int i = 0; i < pbs.length; i++) {
            if (set.contains(pbs[i].getPropertyName()))
                continue;
            set.add(pbs[i].getPropertyName());
            PropertyBean pb = merge(pbs[i], rb.getAttribute(pbs[i]
                    .getPropertyName()));
            if (!pb.isSuppressed() && pb.isTagAttribute()){
                attribute(cb, rb, pb);
            }
        }
        
        String baseComponentType = cb.getBaseComponentType();
        if (baseComponentType != null) {
            ComponentBean bcb = getConfig().getComponent(baseComponentType);
            if (bcb != null){
                attributes(bcb, rb, set);
            }
        }
    }
    
    private void component(ComponentBean cb) throws IOException {
        if (cb.isSuppressed())
            return;
        if(cb.getComponentClass().startsWith("javax.faces.component.")){
            return;
        }
        RendererBean rb = renderer(cb);
        
        if (logger.isLoggable(Level.FINEST)){
            logger.log(Level.FINEST, "component bean class ="+cb.getClass().getName()+
                    "RendererBean comp family="+cb.getComponentFamily()+
                    "componentBean rendertype"+cb.getRendererType());
        }
        
        if (rb == null){
            rb = new RendererBean();
        }
        if (rb.getTagName() == null){
            return;
        }
        writer.println("  <tag>");
        writer.println();
        writer.println("    <name>" + rb.getTagName() + "</name>");
        writer.println("    <tag-class>" + tagClass(cb) + "</tag-class>");
        writer.println("    <body-content>JSP</body-content>");
        if (isVerbose()) {
            DescriptionBean db = null;
            if (rb != null)
                db = rb.getDescription("");
            else
                db = cb.getDescription("");
            if (db != null) {
                writer.println("    <description><![CDATA[");
                writer.println("      " + db.getDescription());
                writer.println("    ]]></description>");
            }
        }
        writer.println();
        if (getDefaultValuesFileName() != null) {
            defaultValuesWriter.println("  <component>");
            defaultValuesWriter.println("      <name>" + cb.getComponentClass() + "</name>"); 
        }        
        attributes(cb, rb);
        if (getDefaultValuesFileName() != null) {
            defaultValuesWriter.println("  </component>");
        }        
        String name;
        for (Iterator names = attributes.keySet().iterator(); names.hasNext(); writer
                .print((String) attributes.get(name)))
            name = (String) names.next();
        
        attributes.clear();
        writer.println("  </tag>");
        writer.println();
        writer.println();
    }
    
    private void components() throws IOException {
        ComponentBean cbs[] = getConfig().getComponents();
        for (int i = 0; i < cbs.length; i++) {
            logger.log(Level.FINEST,  "component = "+ cbs[i].getRendererType());
            if (generated(cbs[i].getComponentClass())) {
                
                if (logger.isLoggable(Level.FINEST)) {
                    logger.log(Level.FINEST, "component class="+cbs[i].getComponentClass());
                }
                component(cbs[i]);
            }
        }
        
    }
    
        /*
         * TODO: move to metadata. jsp tag only
         */
    private void jspTagOnly() {
        
        writer.println("  <tag>");
        writer.println("    <name>tabChangeListener</name>");
        writer
                .println("    <tag-class>com.icesoft.faces.component.paneltabset.TabChangeListenerTag</tag-class>");
        writer.println("    <body-content>empty</body-content>");
        writer.println("    <attribute>");
        writer.println("      <name>type</name>");
        writer.println("      <required>true</required>");
        writer.println("      <rtexprvalue>false</rtexprvalue>");
        writer
                .println("      <description>the name of the class that will be added to the HtmlPanelTabbedPane component as a TabChangeListener</description>");
        writer.println("    </attribute>");
        writer.println("  </tag>");
        writer.println("");
    }
    
    private void footer() throws IOException {
        writer.println("</taglib>");
    }
    
    private void header() throws IOException {
        writer.println("<?xml version=\"1.0\"?>");
        writer.println("<!DOCTYPE taglib PUBLIC");
        writer
                .println("  \"-//Sun Microsystems, Inc.//DTD JSP Tag Library 1.2//EN\"");
        writer
                .println("  \"http://java.sun.com/dtd//web-jsptaglibrary_1_2.dtd\">");
        writer.println();
        writer.println("<taglib>");
        writer.println();
        writer.println();
        
        // TODO: pick up version
        writer.println("  <tlib-version>" + "1.8.1" + "</tlib-version>");
        writer.println("  <jsp-version>1.2</jsp-version>");
        writer.println("  <short-name>" + getPrefix() + "</short-name>");        
        writer.println("  <uri>" + getURI() + "</uri>");        
        writer.println("  <display-name>" + "ICEfaces Component Suite" + "</display-name>");
        writer.println();
    }
    
    private void license() throws IOException {
    }
    
    private void listeners() throws IOException {
        if (listeners == null)
            return;
        for (int i = 0; i < listeners.length; i++) {
            writer.println("  <listener>");
            writer.println("    <listener-class>" + listeners[i]
                    + "</listener-class>");
            writer.println("  </listener>");
            writer.println();
        }
        
    }
    
    private String stripHtmlName(String s, String word){
        
        String tmp = "";
        if(!s.startsWith(word) || s.equalsIgnoreCase(word)){
            tmp = s;
        }else{
            tmp = s.substring(word.length());
        }
        return tmp;
    }
    
    private String tagClass(ComponentBean cb) {
        
        String componentClass = cb.getComponentClass();
        if (tagClassPackage != null && componentClass.indexOf(".ext.") >0){
            
            return tagClassPackage + "."
                    + stripHtmlName(simpleClassName(cb.getComponentClass()), "Html") + "Tag";
            
        }else{
            return componentClass + "Tag";
        }
    }
    
    private void validators() throws IOException {
        if (validators == null)
            return;
        for (int i = 0; i < validators.length; i++) {
            writer.println("  <validator>");
            writer.println("    <validator-class>" + validators[i]
                    + "</validator-class>");
            writer.println("  </validator>");
            writer.println();
        }
        
    }
    
    

}
