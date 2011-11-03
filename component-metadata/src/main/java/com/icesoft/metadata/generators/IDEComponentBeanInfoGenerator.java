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
import com.icesoft.jsfmeta.util.FilterComponentBeanProperties;
import com.icesoft.jsfmeta.util.InternalConfig;
import com.icesoft.jsfmeta.util.JavaSourceWriter;
import com.sun.rave.jsfmeta.beans.*;
import java.io.*;
import java.util.*;

/*
 * IDEComponentBeanInfoGenerator generates IDE specific beaninfo
 *
 */

public class IDEComponentBeanInfoGenerator extends AbstractGenerator {
    
    
    protected static final String BUNDLE_VARIABLE_NAME = "resources";
    
    protected static final String BUNDLE_GET_MESSAGE_START = "resources.getString(";
    
    protected static final String BUNDLE_GET_MESSAGE_END = ")";
    
    private boolean base;
    
    private String baseBI;
    
    private String categoryDescriptors;
    
    private String defaultMarkupSection;
    
    protected HashMap bundleMaps;
    
    private String defaultTaglibPrefix;
    
    private String defaultTaglibURI;
    
    private String implBD;
    
    private String implPD;
    
    protected boolean useComponentResourceBundles;
    
    public IDEComponentBeanInfoGenerator(InternalConfig internalConfig) {
        
        super(internalConfig);
        base = true;
        baseBI = internalConfig.getProperty("project.base.beaninfo");
        categoryDescriptors = internalConfig.getProperty("project.impl.categoryDescriptors");
        defaultMarkupSection = "FORM";
        bundleMaps = new HashMap();
        defaultTaglibPrefix = internalConfig.getProperty("project.taglib.prefix");
        defaultTaglibURI = internalConfig.getProperty("project.taglib.uri");
        implBD = internalConfig.getProperty("project.impl.beanDescriptor");
        implPD = internalConfig.getProperty("project.impl.propertyDescriptor");
        useComponentResourceBundles = true;
    }
    
    
    public boolean getBase() {
        return base;
    }
    
    public void setBase(boolean base) {
        this.base = base;
    }
    
    public String getBaseBI() {
        return baseBI;
    }
    
    public void setBaseBI(String baseBI) {
        this.baseBI = baseBI;
    }
    
    public String getCategoryDescriptors() {
        return categoryDescriptors;
    }
    
    public void setCategoryDescriptors(String categoryDescriptors) {
        this.categoryDescriptors = categoryDescriptors;
    }
    
    public String getDefaultMarkupSection() {
        return defaultMarkupSection;
    }
    
    public void setDefaultMarkupSection(String defaultMarkupSection) {
        this.defaultMarkupSection = defaultMarkupSection;
    }
    
    protected HashMap getBundleMap(String packageName, String languageCode) {
        HashMap packageMap = (HashMap) bundleMaps.get(packageName);
        if (packageMap == null) {
            packageMap = new HashMap();
            bundleMaps.put(packageName, packageMap);
        }
        HashMap map = (HashMap) packageMap.get(languageCode);
        if (map == null) {
            map = new HashMap();
            packageMap.put(languageCode, map);
        }
        return map;
    }
    
    protected HashMap getBundleMaps() {
        return bundleMaps;
    }
    
    protected void putBundleMap(String packageName, String languageCode,
            String key, String value) {
        HashMap map = getBundleMap(packageName, languageCode);
        map.put(key, value);
    }
    
    public String getDefaultTaglibPrefix() {
        return defaultTaglibPrefix;
    }
    
    public void setDefaultTaglibPrefix(String defaultTaglibPrefix) {
        this.defaultTaglibPrefix = defaultTaglibPrefix;
    }
    
    public String getDefaultTaglibURI() {
        return defaultTaglibURI;
    }
    
    public void setDefaultTaglibURI(String defaultTaglibURI) {
        this.defaultTaglibURI = defaultTaglibURI;
    }
    
    public String getImplBD() {
        return implBD;
    }
    
    public void setImplBD(String implBD) {
        this.implBD = implBD;
    }
    
    public String getImplPD() {
        return implPD;
    }
    
    public void setImplPD(String implPD) {
        this.implPD = implPD;
    }
    
    public boolean getUseComponentResourceBundles() {
        return useComponentResourceBundles;
    }
    
    public void setUseComponentResourceBundles(boolean flag) {
        useComponentResourceBundles = flag;
    }
    
    public void generate() throws IOException {
        ComponentBean cbs[] = getConfig().getComponents();
        for (int i = 0; i < cbs.length; i++)
            if (generated(cbs[i].getComponentClass()))
                generate(cbs[i]);
        
        generateComponentResourceBundlesIfNecessary();
    }
    
    protected void generateComponentResourceBundlesIfNecessary()
    throws IOException {
        if (!getUseComponentResourceBundles())
            return;
        for (Iterator packageKeyIterator = getBundleMaps().keySet().iterator(); packageKeyIterator
                .hasNext();) {
            String packageKey = (String) packageKeyIterator.next();
            HashMap byLangMap = (HashMap) getBundleMaps().get(packageKey);
            Iterator langKeyIterator = byLangMap.keySet().iterator();
            while (langKeyIterator.hasNext()) {
                String langKey = (String) langKeyIterator.next();
                HashMap stringsByKey = (HashMap) byLangMap.get(langKey);
                componentResourceBundle(packageKey, langKey, stringsByKey);
            }
        }
    }
    
    private void beanDescriptor(ComponentBean cb, RendererBean rb)
    throws IOException {
        
        String implBD = this.implBD;
        int period = implBD.lastIndexOf(".");
        if (period >= 0){
            implBD = implBD.substring(period + 1);
        }
        
        String packageName = packageName(cb);
        String simpleClassName = simpleClassName(cb.getComponentClass());
        JavaSourceWriter writer = getWriter();
        DisplayNameBean displayName = cb.getDisplayName("");
        DisplayNameBean displayNames[] = cb.getDisplayNames();
        DescriptionBean description = cb.getDescription("");
        DescriptionBean descriptions[] = cb.getDescriptions();
        writer.startJavaDoc();
        writer.emitJavaDoc("<p>Return the <code>BeanDescriptor</code> for this bean.</p>");
        writer.endJavaDoc();
        writer.startMethod("getBeanDescriptor", "BeanDescriptor", null, null);
        writer.emitNewline();
        writer.emitExpression("if (beanDescriptor != null) {", true);
        writer.indent();
        writer.emitExpression("return beanDescriptor;", true);
        writer.outdent();
        writer.emitExpression("}", true);
        writer.emitNewline();
        if (cb.getCustomizerClass() != null) {
            
            writer.emitExpression("Class clazz = null;", true);
            writer.emitExpression("try {", true);
            writer.indent();
            writer.emitExpression(
                    "clazz = this.getClass().getClassLoader().loadClass(\""
                    + cb.getCustomizerClass() + "\");", true);
            writer.outdent();
            writer.emitExpression("} catch (Exception e) {", true);
            writer.indent();
            writer.emitExpression("throw new IllegalArgumentException(\""
                    + cb.getCustomizerClass() + "\");", true);
            writer.outdent();
            writer.emitExpression("}", true);
            writer.emitExpression("beanDescriptor = new " + implBD
                    + "(beanClass, clazz);", true);
        } else {
            writer.emitExpression("beanDescriptor = new " + implBD
                    + "(beanClass);", true);
        }
        displayName(displayName, displayNames, writer, "beanDescriptor",
                packageName, simpleClassName, null);
        shortDescription(description, descriptions, writer, "beanDescriptor",
                packageName, simpleClassName, null);
        writer.emitExpression("beanDescriptor.setExpert(" + cb.isExpert()
        + ");", true);
        writer.emitExpression("beanDescriptor.setHidden(" + cb.isHidden()
        + ");", true);
        writer.emitExpression("beanDescriptor.setPreferred(" + cb.isPreferred()
        + ");", true);
        writer.emitExpression(
                "beanDescriptor.setValue(Constants.BeanDescriptor.FACET_DESCRIPTORS,getFacetDescriptors());",
                true);
        if (rb.getComponentHelpKey() != null)
            writer.emitExpression(
                    "beanDescriptor.setValue(Constants.BeanDescriptor.HELP_KEY,"
                    + writer.toJavaString(rb.getComponentHelpKey()
                    .trim()) + ");", true);
        String instanceName = rb.getInstanceName();
        
        if (instanceName == null){
            instanceName = simpleInstanceName(cb, rb);
        }
        
        if (instanceName != null){
            writer.emitExpression(
                    "beanDescriptor.setValue(Constants.BeanDescriptor.INSTANCE_NAME,"
                    + writer.toJavaString(instanceName.trim()) + ");",
                    true);
        }
        writer.emitExpression(
                "beanDescriptor.setValue(Constants.BeanDescriptor.IS_CONTAINER,"
                + (rb.isContainer() ? "Boolean.TRUE" : "Boolean.FALSE")
                + ");", true);
        
        String markupSection = rb.getMarkupSection();
        if (markupSection == null){
            markupSection = getDefaultMarkupSection();
        }
        if (markupSection != null){
            writer.emitExpression(
                    "beanDescriptor.setValue(Constants.BeanDescriptor.MARKUP_SECTION,"
                    + writer.toJavaString(markupSection.toLowerCase()
                    .trim()) + ");", true);
        }
        if (rb.getPropertiesHelpKey() != null){
            writer.emitExpression(
                    "beanDescriptor.setValue(Constants.BeanDescriptor.PROPERTIES_HELP_KEY,"
                    + writer.toJavaString(rb.getPropertiesHelpKey()
                    .trim()) + ");", true);
        }
        
        writer
                .emitExpression(
                "beanDescriptor.setValue(Constants.BeanDescriptor.PROPERTY_CATEGORIES,getCategoryDescriptors());",
                true);
        
        if (rb.getResizeConstraints() != null){
            writer
                    .emitExpression(
                    "beanDescriptor.setValue(Constants.BeanDescriptor.RESIZE_CONSTRAINTS,new Integer(Constants.ResizeConstraints."
                    + rb.getResizeConstraints().toUpperCase()
                    .trim() + "));", true);
        }
        String tagName = rb.getTagName();
        if (tagName == null && !cb.isNonVisual()){
            tagName = simpleTagName(cb, rb);
        }
        if (tagName != null){
            writer.emitExpression(
                    "beanDescriptor.setValue(Constants.BeanDescriptor.TAG_NAME,"
                    + writer.toJavaString(tagName.trim()) + ");", true);
        }
        String taglibPrefix = rb.getTaglibPrefix();
        if (taglibPrefix == null){
            taglibPrefix = getDefaultTaglibPrefix();
        }
        if (taglibPrefix != null){
            writer.emitExpression(
                    "beanDescriptor.setValue(Constants.BeanDescriptor.TAGLIB_PREFIX,"
                    + writer.toJavaString(taglibPrefix.trim()) + ");",
                    true);
        }
        String taglibURI = rb.getTaglibURI();
        if (taglibURI == null){
            taglibURI = getDefaultTaglibURI();
        }
        if (taglibURI != null){
            writer.emitExpression(
                    "beanDescriptor.setValue(Constants.BeanDescriptor.TAGLIB_URI,"
                    + writer.toJavaString(taglibURI.trim()) + ");",
                    true);
        }
        if (cb.isNonVisual() || rb.isNonVisual()){
            writer
                    .emitExpression(
                    "beanDescriptor.setValue(Constants.BeanDescriptor.TRAY_COMPONENT,Boolean.TRUE);",
                    true);
        }
        Map namedValues = rb.getNamedValues();
        String key;
        String expression;
        String value;
        for (Iterator keys = namedValues.keySet().iterator(); keys.hasNext(); writer
                .emitExpression("beanDescriptor.setValue("
                + writer.toJavaString(key.trim())
                + ","
                + (expression == null ? writer.toJavaString(value
                .trim()) : expression.trim()) + ");", true)) {
            key = (String) keys.next();
            NamedValueBean nvb = (NamedValueBean) namedValues.get(key);
            expression = nvb.getExpression();
            value = nvb.getValue();
        }
        
        writer.emitNewline();
        if (raveBaseBI()){
            writer.emitExpression("annotateBeanDescriptor(beanDescriptor);",
                    true);
        }
        writer.emitExpression("return beanDescriptor;", true);
        writer.emitNewline();
        writer.endMethod();
        writer.emitNewline();
    }
    
    private void body(ComponentBean cb, RendererBean rb) throws IOException {
        
        String baseBI = this.baseBI;
        int period = baseBI.lastIndexOf(".");
        if (period >= 0){
            baseBI = baseBI.substring(period + 1);
        }
        JavaSourceWriter writer = getWriter();
        String simple = simpleClassName(cb.getComponentClass());
        simple = simple + (getBase() ? "BeanInfoBase" : "BeanInfo");
        writer.startClass(simple, baseBI, null, !getBase(), getBase());
        writer.emitNewline();
        if (getUseComponentResourceBundles()) {
            writer.emitExpression(
                    "protected static ResourceBundle resources = ResourceBundle.getBundle(\""
                    + packageName(cb) + ".Bundle-JSF\""
                    + ", Locale.getDefault(), " + simple
                    + ".class.getClassLoader());", true);
            writer.emitNewline();
        }
        
        constructor(cb, rb);
        instance(cb, rb);
        beanDescriptor(cb, rb);
        categoryDescriptors(cb, rb);
        defaultPropertyIndex(cb, rb);
        facetDescriptors(cb, rb);
        icon(cb, rb);
        loadClass(cb);
        
        FilterComponentBeanProperties filter = new FilterComponentBeanProperties();
        
        String baseComponentType = cb.getBaseComponentType();
        if (baseComponentType != null) {
            ComponentBean bcb = getConfig().getComponent(baseComponentType);
            filter.filter(bcb);
        }
        propertyDescriptors(cb, rb);
        writer.endClass();
    }
    
    private void loadClass(ComponentBean cb) throws IOException {
        JavaSourceWriter writer = getWriter();
        writer.startJavaDoc();
        writer
                .emitJavaDoc("<p>Return a class loaded by name via the class loader that loaded this class.</p>");
        writer.endJavaDoc();
        writer.startMethod("loadClass", "java.lang.Class",
                new String[] { "java.lang.String" }, new String[] { "name" },
                "private");
        writer.emitNewline();
        writer.emitExpression("try {", true);
        writer.indent();
        writer.emitExpression("return Class.forName(name);", true);
        writer.outdent();
        writer.emitExpression("} catch (ClassNotFoundException e) {", true);
        writer.indent();
        writer.emitExpression("throw new RuntimeException(e);", true);
        writer.outdent();
        writer.emitExpression("}", true);
        writer.emitNewline();
        writer.endMethod();
        writer.emitNewline();
    }
    
    private void categoryDescriptors(ComponentBean cb, RendererBean rb)
    throws IOException {
        JavaSourceWriter writer = getWriter();
        writer.startJavaDoc();
        writer
                .emitJavaDoc("<p>Return the <code>CategoryDescriptor</code> array for the property categories of this component.</p>");
        writer.endJavaDoc();
        writer.startMethod("getCategoryDescriptors", "CategoryDescriptor[]",
                null, null, "private");
        writer.emitNewline();
        if (rb.getPropertyCategories() != null) {
            writer.emitExpression(
                    "CategoryDescriptor cds[] = new CategoryDescriptor[] {",
                    true);
            writer.indent();
            String temp = rb.getPropertyCategories().trim();
            do {
                int comma = temp.indexOf(',');
                if (comma < 0)
                    break;
                writer.emitExpression(getCategoryDescriptors() + "."
                        + temp.substring(0, comma).trim().toUpperCase() + ",",
                        true);
                temp = temp.substring(comma + 1);
            } while (true);
            writer.emitExpression(getCategoryDescriptors() + "."
                    + temp.trim().toUpperCase(), true);
            writer.outdent();
            writer.emitExpression("};", true);
            writer.emitExpression("return cds;", true);
        } else {
            writer.emitExpression("return " + getCategoryDescriptors()
            + ".getDefaultCategoryDescriptors();", true);
        }
        writer.emitNewline();
        writer.endMethod();
        writer.emitNewline();
    }
    
    protected void componentResourceBundle(String packageName, String lang,
            HashMap map) throws IOException {
        if (map.size() == 0)
            return;
        String filename = packageName;
        if (filename.length() > 0) {
            filename = filename.replace('.', File.separatorChar);
            filename = filename + File.separatorChar;
        }
        filename = filename + "Bundle-JSF";
        if (lang.length() > 0)
            filename = filename + "_" + lang;
        filename = filename + ".properties";
        File file = new File(getDest(), filename);
        file.mkdirs();
        file.delete();
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(
                file)));
        char hexDigit[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'A', 'B', 'C', 'D', 'E', 'F' };
        String specialSaveChars = "=: \t\r\n\f#!";
        Object keys[] = map.keySet().toArray();
        Arrays.sort(keys);
        String lastKeyGroup = null;
        for (int i = 0; i < keys.length; i++) {
            String key = (String) keys[i];
            String value = (String) map.get(key);
            value = trimWhitespace(value);
            int index = key.indexOf('_');
            if (index != -1) {
                String keyGroup = key.substring(0, index);
                if (lastKeyGroup == null || !lastKeyGroup.equals(keyGroup)) {
                    if (lastKeyGroup != null)
                        writer.write(10);
                    writer.write("# ");
                    writer.write(keyGroup);
                    writer.write(10);
                    lastKeyGroup = keyGroup;
                }
            }
            writer.write(key);
            writer.write(61);
            int len = value.length();
            for (int x = 0; x < len; x++) {
                char aChar = value.charAt(x);
                switch (aChar) {
                    case 32: // ' '
                        writer.write(32);
                        break;
                        
                    case 92: // '\\'
                        writer.write(92);
                        writer.write(92);
                        break;
                        
                    case 9: // '\t'
                        writer.write(92);
                        writer.write(116);
                        break;
                        
                    case 10: // '\n'
                        writer.write(92);
                        writer.write(110);
                        break;
                        
                    case 13: // '\r'
                        writer.write(92);
                        writer.write(114);
                        break;
                        
                    case 12: // '\f'
                        writer.write(92);
                        writer.write(102);
                        break;
                        
                    default:
                        if (aChar < ' ' || aChar > '~') {
                            writer.write(92);
                            writer.write(117);
                            writer.write(hexDigit[aChar >> 12 & 0xf]);
                            writer.write(hexDigit[aChar >> 8 & 0xf]);
                            writer.write(hexDigit[aChar >> 4 & 0xf]);
                            writer.write(hexDigit[aChar & 0xf]);
                            break;
                        }
                        if ("=: \t\r\n\f#!".indexOf(aChar) != -1)
                            writer.write(92);
                        writer.write(aChar);
                        break;
                }
            }
            
            writer.write(10);
        }
        
        writer.flush();
        writer.close();
    }
    
    private void constructor(ComponentBean cb, RendererBean rb)
    throws IOException {
        JavaSourceWriter writer = getWriter();
        String simple = simpleClassName(cb.getComponentClass());
        writer.startJavaDoc();
        writer.emitJavaDoc("<p>Construct a new <code>" + simpleClassName(cb)
        + "</code>.</p>");
        writer.endJavaDoc();
        writer.startMethod(simpleClassName(cb), null, null, null);
        writer.emitNewline();
        writer.emitExpression("beanClass = " + simple + ".class;", true);
        if (rb.getDefaultPropertyName() != null)
            writer.emitExpression("defaultPropertyName = "
                    + JavaSourceWriter
                    .toJavaString(rb.getDefaultPropertyName()) + ";",
                    true);
        writer.emitExpression("iconFileName_C16 = \"" + simple + "_C16\";",
                true);
        writer.emitExpression("iconFileName_C32 = \"" + simple + "_C32\";",
                true);
        writer.emitExpression("iconFileName_M16 = \"" + simple + "_M16\";",
                true);
        writer.emitExpression("iconFileName_M32 = \"" + simple + "_M32\";",
                true);
        writer.emitNewline();
        writer.endMethod();
        writer.emitNewline();
    }
    
    private void defaultPropertyIndex(ComponentBean cb, RendererBean rb)
    throws IOException {
        if (raveBaseBI()) {
            return;
        } else {
            JavaSourceWriter writer = getWriter();
            writer.startJavaDoc();
            writer
                    .emitJavaDoc("<p>Return the index of the default property, or");
            writer.emitJavaDoc("-1 if there is no default property.</p>");
            writer.endJavaDoc();
            writer.startMethod("getDefaultPropertyIndex", "int", null, null);
            writer.emitNewline();
            writer.emitExpression("if (defaultPropertyIndex > -2) {", true);
            writer.indent();
            writer.emitExpression("return defaultPropertyIndex;", true);
            writer.outdent();
            writer.emitExpression("} else {", true);
            writer.indent();
            writer.emitExpression("if (defaultPropertyName == null) {", true);
            writer.indent();
            writer.emitExpression("defaultPropertyIndex = -1;", true);
            writer.outdent();
            writer.emitExpression("} else {", true);
            writer.indent();
            writer
                    .emitExpression(
                    "PropertyDescriptor pd[] = getPropertyDescriptors();",
                    true);
            writer
                    .emitExpression("for (int i = 0; i < pd.length; i++) {",
                    true);
            writer.indent();
            writer.emitExpression(
                    "if (defaultPropertyName.equals(pd[i].getName())) {", true);
            writer.indent();
            writer.emitExpression("defaultPropertyIndex = i;", true);
            writer.emitExpression("break;", true);
            writer.outdent();
            writer.emitExpression("}", true);
            writer.outdent();
            writer.emitExpression("}", true);
            writer.outdent();
            writer.emitExpression("}", true);
            writer.outdent();
            writer.emitExpression("}", true);
            writer.emitExpression("return defaultPropertyIndex;", true);
            writer.endMethod();
            writer.emitNewline();
            return;
        }
    }
    
    protected void displayName(DisplayNameBean displayName,
            DisplayNameBean displayNames[], JavaSourceWriter writer,
            String variableName, String packageName, String simpleClassName,
            String propertyName) throws IOException {
        String key = simpleClassName;
        if (propertyName != null)
            key = key + "_" + propertyName;
        key = key + "_DisplayName";
        if (displayName != null || getUseComponentResourceBundles())
            writer.emitExpression(variableName + ".setDisplayName(", false);
        String displayNameString = displayName != null ? displayName
                .getDisplayName().trim() : "";
        if (getUseComponentResourceBundles()) {
            writer.emitExpressionPart("resources.getString(");
            putBundleMap(packageName, "", key, displayNameString);
            writer.emitJavaString(key);
            writer.emitExpressionPart(")");
        } else if (displayName != null)
            writer.emitJavaString(displayNameString);
        if (displayName != null || getUseComponentResourceBundles()) {
            writer.emitExpressionPart(");");
            writer.emitNewline();
        }
        for (int i = 0; i < displayNames.length; i++) {
            String lang = displayNames[i].getLang();
            if ("".equals(lang))
                continue;
            displayNameString = displayNames[i].getDisplayName().trim();
            if (getUseComponentResourceBundles())
                putBundleMap(packageName, lang, key, displayNameString);
            else
                writer.emitExpression(variableName + ".setValue("
                        + writer.toJavaString("displayName." + lang) + ","
                        + writer.toJavaString(displayNameString) + ");", true);
        }
        
    }
    
    private void facetDescriptor(ComponentBean cb, RendererBean rb, FacetBean fb)
    throws IOException {
        JavaSourceWriter writer = getWriter();
        DisplayNameBean displayName = fb.getDisplayName("");
        DisplayNameBean displayNames[] = fb.getDisplayNames();
        DescriptionBean description = fb.getDescription("");
        DescriptionBean descriptions[] = fb.getDescriptions();
        String name = fb.getFacetName();
        StringBuffer fsb = new StringBuffer("FacetDescriptor facet_");
        fsb.append(name);
        fsb.append(" = new FacetDescriptor(");
        fsb.append(writer.toJavaString(name));
        fsb.append(");");
        writer.emitExpression(fsb.toString(), true);
        String packageName = packageName(cb);
        String simpleClassName = simpleClassName(cb.getComponentClass());
        displayName(displayName, displayNames, writer, "facet_" + name,
                packageName, simpleClassName, name);
        shortDescription(description, descriptions, writer, "facet_" + name,
                packageName, simpleClassName, name);
        Map namedValues = fb.getNamedValues();
        String key;
        String expression;
        String value;
        for (Iterator keys = namedValues.keySet().iterator(); keys.hasNext(); genericNamedValue(
                key, value, expression, writer, "facet_" + name, packageName,
                simpleClassName, name)) {
            key = (String) keys.next();
            NamedValueBean nvb = (NamedValueBean) namedValues.get(key);
            expression = nvb.getExpression();
            value = nvb.getValue();
        }
        
        writer.emitNewline();
    }
    
    private void facetDescriptors(ComponentBean cb, RendererBean rb)
    throws IOException {
        JavaSourceWriter writer = getWriter();
        TreeMap map = new TreeMap();
        writer.startJavaDoc();
        writer.emitJavaDoc("<p>The cached facet descriptors.</p>");
        writer.endJavaDoc();
        writer.emitExpression("protected FacetDescriptor[] facetDescriptors;",
                true);
        writer.emitNewline();
        writer.startJavaDoc();
        writer
                .emitJavaDoc("<p>Return the <code>FacetDescriptor</code>s for this bean.</p>");
        writer.endJavaDoc();
        writer.startMethod("getFacetDescriptors", "FacetDescriptor[]", null,
                null);
        writer.emitNewline();
        writer.emitExpression("if (facetDescriptors != null) {", true);
        writer.indent();
        writer.emitExpression("return facetDescriptors;", true);
        writer.outdent();
        writer.emitExpression("}", true);
        FacetBean facets[] = facets(cb, rb);
        if (facets.length > 0) {
            writer.emitExpression("try {", true);
            writer.emitNewline();
            writer.indent();
        }
        for (int i = 0; i < facets.length; i++) {
            FacetBean facet = facets[i];
            facetDescriptor(cb, rb, facet);
            map.put(facet.getFacetName(), facet);
        }
        
        writer.emitExpression("facetDescriptors = new FacetDescriptor[] {",
                true);
        writer.indent();
        String facetName;
        for (Iterator facetNames = map.keySet().iterator(); facetNames
                .hasNext(); writer.emitExpression("facet_" + facetName + ",",
                true))
            facetName = (String) facetNames.next();
        
        writer.outdent();
        writer.emitExpression("};", true);
        writer.emitExpression("return facetDescriptors;", true);
        writer.emitNewline();
        if (facets.length > 0) {
            writer.outdent();
            writer.emitExpression("} catch (RuntimeException e) {", true);
            writer.indent();
            writer.emitExpression("System.out.println(e.getMessage());", true);
            writer.emitExpression("e.printStackTrace(System.out);", true);
            writer.emitExpression("throw e;", true);
            writer.outdent();
            writer.emitExpression("}", true);
            writer.emitNewline();
        }
        writer.endMethod();
        writer.emitNewline();
    }
    
    private void generate(ComponentBean cb) throws IOException {

        if(cb.getComponentClass().startsWith("javax.faces.component.")){
            return;
        }
        if (cb.isSuppressed())
            return;
        RendererBean rb = renderer(cb);
        if (rb == null)
            rb = new RendererBean();
        File outputFile = outputFile(cb.getComponentClass()
        + (getBase() ? "BeanInfoBase" : "BeanInfo"));
        outputFile.mkdirs();
        outputFile.delete();
        try{
            getWriter().setOutputWriter(
                    new BufferedWriter(new FileWriter(outputFile)));
        }catch(Exception e){
            e.printStackTrace();
        }
        license(cb, rb);
        header(cb, rb);
        body(cb, rb);
        getWriter().flush();
        getWriter().close();
    }
    
    private void header(ComponentBean cb, RendererBean rb) throws IOException {
        JavaSourceWriter writer = getWriter();
        String packageName = packageName(cb);
        if (packageName.length() > 0) {
            writer.emitPackage(packageName);
            writer.emitNewline();
        }
        writer.emitImport("java.awt.Image");
        writer.emitImport("java.beans.BeanDescriptor");
        writer.emitImport("java.beans.BeanInfo");
        writer.emitImport("java.beans.IntrospectionException");
        writer.emitImport("java.beans.PropertyDescriptor");
        writer.emitImport("java.util.Locale");
        writer.emitImport("java.util.ResourceBundle");
        writer.emitNewline();
        writer.emitImport("com.sun.rave.designtime.CategoryDescriptor");
        writer.emitImport("com.sun.rave.designtime.Constants");
        writer.emitImport("com.sun.rave.designtime.faces.FacetDescriptor");
        writer.emitImport("com.sun.rave.designtime.markup.AttributeDescriptor");
        writer.emitNewline();
        writer.emitImport(baseBI);
        if (!"java.beans.BeanDescriptor".equals(implBD))
            writer.emitImport(implBD);
        if (!"java.beans.PropertyDescriptor".equals(implPD))
            writer.emitImport(implPD);
        writer.emitNewline();
        writer.startJavaDoc();
        writer.emitJavaDoc("<p>Auto-generated design time metadata class.");
        writer.emitJavaDoc("Do <strong>NOT</strong> modify; all changes");
        writer.emitJavaDoc("<strong>will</strong> be lost!</p>");
        writer.endJavaDoc();
        writer.emitNewline();
    }
    
    protected void genericNamedValue(String valueName, String value,
            String expression, JavaSourceWriter writer, String variableName,
            String packageName, String simpleClassName, String propertyName)
            throws IOException {
        boolean inBundle = false;
        if (getUseComponentResourceBundles() && value != null
                && expression == null && "instructions".equals(valueName))
            inBundle = true;
        writer.emitExpression(variableName, false);
        valueName = valueName.trim();
        writer.emitExpressionPart(".setValue(");
        writer.emitJavaString(valueName);
        writer.emitExpressionPart(", ");
        if (inBundle) {
            String key = simpleClassName;
            if (propertyName != null)
                key = key + "_" + propertyName;
            key = key + "_" + valueName;
            putBundleMap(packageName, "", key, value);
            writer.emitExpressionPart("resources.getString(");
            writer.emitJavaString(key);
            writer.emitExpressionPart(")");
        } else if (expression == null)
            writer.emitJavaString(value.trim());
        else
            writer.emitExpressionPart(expression.trim());
        writer.emitExpressionPart(");");
        writer.emitNewline();
    }
    
    private void icon(ComponentBean cb, RendererBean rb) throws IOException {
        if (raveBaseBI()) {
            return;
        } else {
            JavaSourceWriter writer = getWriter();
            writer.startJavaDoc();
            writer.emitJavaDoc("<p>Return the specified image (if any)");
            writer.emitJavaDoc("for this component class.</p>");
            writer.endJavaDoc();
            writer.startMethod("getIcon", "Image", new String[] { "int" },
                    new String[] { "kind" });
            writer.emitNewline();
            writer.emitExpression("String name;", true);
            writer.emitExpression("switch (kind) {", true);
            writer.indent();
            writer.emitExpression("case ICON_COLOR_16x16:", true);
            writer.indent();
            writer.emitExpression("name = iconFileName_C16;", true);
            writer.emitExpression("break;", true);
            writer.outdent();
            writer.emitExpression("case ICON_COLOR_32x32:", true);
            writer.indent();
            writer.emitExpression("name = iconFileName_C32;", true);
            writer.emitExpression("break;", true);
            writer.outdent();
            writer.emitExpression("case ICON_MONO_16x16:", true);
            writer.indent();
            writer.emitExpression("name = iconFileName_M16;", true);
            writer.emitExpression("break;", true);
            writer.outdent();
            writer.emitExpression("case ICON_MONO_32x32:", true);
            writer.indent();
            writer.emitExpression("name = iconFileName_M32;", true);
            writer.emitExpression("break;", true);
            writer.outdent();
            writer.emitExpression("default:", true);
            writer.indent();
            writer.emitExpression("name = null;", true);
            writer.emitExpression("break;", true);
            writer.outdent();
            writer.outdent();
            writer.emitExpression("}", true);
            writer.emitExpression("if (name == null) {", true);
            writer.indent();
            writer.emitExpression("return null;", true);
            writer.outdent();
            writer.emitExpression("}", true);
            writer.emitNewline();
            writer.emitExpression("Image image = loadImage(name + \".png\");",
                    true);
            writer.emitExpression("if (image == null) {", true);
            writer.indent();
            writer.emitExpression("image = loadImage(name + \".gif\");", true);
            writer.outdent();
            writer.emitExpression("}", true);
            writer.emitExpression("return image;", true);
            writer.emitNewline();
            writer.endMethod();
            writer.emitNewline();
            return;
        }
    }
    
    private void instance(ComponentBean cb, RendererBean rb) throws IOException {
        if (raveBaseBI()) {
            return;
        } else {
            JavaSourceWriter writer = getWriter();
            writer.startJavaDoc();
            writer
                    .emitJavaDoc("<p>The bean class that this BeanInfo represents.");
            writer.endJavaDoc();
            writer.emitExpression("protected Class beanClass;", true);
            writer.emitNewline();
            writer.startJavaDoc();
            writer.emitJavaDoc("<p>The cached BeanDescriptor.</p>");
            writer.endJavaDoc();
            writer.emitExpression("protected BeanDescriptor beanDescriptor;",
                    true);
            writer.emitNewline();
            writer.startJavaDoc();
            writer.emitJavaDoc("<p>The index of the default property.</p>");
            writer.endJavaDoc();
            writer.emitExpression("protected int defaultPropertyIndex = -2;",
                    true);
            writer.emitNewline();
            writer.startJavaDoc();
            writer.emitJavaDoc("<p>The name of the default property.</p>");
            writer.endJavaDoc();
            writer
                    .emitExpression("protected String defaultPropertyName;",
                    true);
            writer.emitNewline();
            writer.startJavaDoc();
            writer.emitJavaDoc("<p>The 16x16 color icon.</p>");
            writer.endJavaDoc();
            writer.emitExpression("protected String iconFileName_C16;", true);
            writer.emitNewline();
            writer.startJavaDoc();
            writer.emitJavaDoc("<p>The 32x32 color icon.</p>");
            writer.endJavaDoc();
            writer.emitExpression("protected String iconFileName_C32;", true);
            writer.emitNewline();
            writer.startJavaDoc();
            writer.emitJavaDoc("<p>The 16x16 monochrome icon.</p>");
            writer.endJavaDoc();
            writer.emitExpression("protected String iconFileName_M16;", true);
            writer.emitNewline();
            writer.startJavaDoc();
            writer.emitJavaDoc("<p>The 32x32 monochrome icon.</p>");
            writer.endJavaDoc();
            writer.emitExpression("protected String iconFileName_M32;", true);
            writer.emitNewline();
            writer.startJavaDoc();
            writer.emitJavaDoc("<p>The cached property descriptors.</p>");
            writer.endJavaDoc();
            writer.emitExpression(
                    "protected PropertyDescriptor[] propDescriptors;", true);
            writer.emitNewline();
            return;
        }
    }
    
    private void license(ComponentBean componentbean, RendererBean rendererbean)
    throws IOException {
    }
    
    private String packageName(ComponentBean cb) {
        String componentClass = cb.getComponentClass();
        int last = componentClass.lastIndexOf('.');
        if (last >= 0)
            return componentClass.substring(0, last);
        else
            return "";
    }
    
    private void propertyDescriptor(ComponentBean cb, RendererBean rb,
            PropertyBean pb, ComponentBean componentBeanBeingGenerated)
            throws IOException {
        String implPD = this.implPD;
        int period = implPD.lastIndexOf(".");
        if (period >= 0)
            implPD = implPD.substring(period + 1);
        JavaSourceWriter writer = getWriter();
        DisplayNameBean displayName = pb.getDisplayName("");
        DisplayNameBean displayNames[] = pb.getDisplayNames();
        DescriptionBean description = pb.getShortDescription("");
        if (description == null)
            description = pb.getDescription("");
        DescriptionBean descriptions[] = pb.getShortDescriptions();
        if (descriptions == null)
            descriptions = pb.getDescriptions();
        String name = pb.getPropertyName();
        String readMethod = pb.getReadMethod();
        if (pb.isWriteOnly())
            readMethod = null;
        else if (readMethod == null)
            if ("boolean".equals(pb.getPropertyClass()))
                readMethod = "is" + Character.toUpperCase(name.charAt(0))
                + name.substring(1);
            else
                readMethod = "get" + Character.toUpperCase(name.charAt(0))
                + name.substring(1);
        String writeMethod = pb.getWriteMethod();
        if (pb.isReadOnly())
            writeMethod = null;
        else if (writeMethod == null)
            writeMethod = "set" + Character.toUpperCase(name.charAt(0))
            + name.substring(1);
        StringBuffer psb = new StringBuffer("PropertyDescriptor prop_");
        psb.append(name);
        psb.append(" = new ");
        psb.append(implPD);
        psb.append("(");
        psb.append(writer.toJavaString(name));
        psb.append(",beanClass,");
        psb.append(readMethod != null ? writer.toJavaString(readMethod)
        : "null");
        psb.append(",");
        psb.append(writeMethod != null ? writer.toJavaString(writeMethod)
        : "null");
        psb.append(");");
        writer.emitExpression(psb.toString(), true);
        String packageName = packageName(componentBeanBeingGenerated);
        String simpleClassName = simpleClassName(componentBeanBeingGenerated
                .getComponentClass());
        displayName(displayName, displayNames, writer, "prop_" + name,
                packageName, simpleClassName, name);
        shortDescription(description, descriptions, writer, "prop_" + name,
                packageName, simpleClassName, name);
        if (pb.getEditorClass() != null)
            writer.emitExpression("prop_" + name + ".setPropertyEditorClass("
                    + "loadClass(\"" + pb.getEditorClass().trim() + "\"));",
                    true);
        writer.emitExpression("prop_" + name + ".setExpert(" + pb.isExpert()
        + ");", true);
        writer.emitExpression("prop_" + name + ".setHidden(" + pb.isHidden()
        + ");", true);
        writer.emitExpression("prop_" + name + ".setPreferred("
                + pb.isPreferred() + ");", true);
        if (pb.isTagAttribute()) {
            StringBuffer asb = new StringBuffer(
                    "attrib = new AttributeDescriptor(");
            String attributeName = pb.getAttributeAlias();
            if (attributeName == null)
                attributeName = name;
            asb.append(writer.toJavaString(attributeName));
            asb.append(",");
            asb.append(pb.isRequired());
            asb.append(",");
            if (pb.getDefaultValue() != null)
                asb.append(writer.toJavaString(pb.getDefaultValue()));
            else
                asb.append("null");
            asb.append(",");
            asb.append(pb.isBindable());
            asb.append(");");
            writer.emitExpression(asb.toString(), true);
            writer.emitExpression("prop_" + name + ".setValue("
                    + "Constants.PropertyDescriptor.ATTRIBUTE_DESCRIPTOR,"
                    + "attrib);", true);
        }
        String category = pb.getCategory();
        if (category == null)
            category = "GENERAL";
        writer.emitExpression("prop_" + name + ".setValue("
                + "Constants.PropertyDescriptor.CATEGORY,"
                + getCategoryDescriptors() + "."
                + category.toUpperCase().trim() + ");", true);
        if (pb.getHelpKey() != null)
            writer.emitExpression("prop_" + name + ".setValue("
                    + "Constants.PropertyDescriptor.HELP_KEY,"
                    + writer.toJavaString(pb.getHelpKey().trim()) + ");", true);
        Map namedValues = pb.getNamedValues();
        String key;
        String expression;
        String value;
        for (Iterator keys = namedValues.keySet().iterator(); keys.hasNext(); genericNamedValue(
                key, value, expression, writer, "prop_" + name, packageName,
                simpleClassName, name)) {
            key = (String) keys.next();
            NamedValueBean nvb = (NamedValueBean) namedValues.get(key);
            expression = nvb.getExpression();
            value = nvb.getValue();
        }
        
        writer.emitNewline();
    }
    
    private void propertyDescriptors(ComponentBean cb, RendererBean rb)
    throws IOException {
        JavaSourceWriter writer = getWriter();
        TreeMap map = new TreeMap();
        writer.startJavaDoc();
        writer
                .emitJavaDoc("<p>Return the <code>PropertyDescriptor</code>s for this bean.</p>");
        writer.endJavaDoc();
        writer.startMethod("getPropertyDescriptors", "PropertyDescriptor[]",
                null, null);
        writer.emitNewline();
        writer.emitExpression("if (propDescriptors != null) {", true);
        writer.indent();
        writer.emitExpression("return propDescriptors;", true);
        writer.outdent();
        writer.emitExpression("}", true);
        writer.emitExpression("AttributeDescriptor attrib = null;", true);
        writer.emitNewline();
        writer.emitExpression("try {", true);
        writer.emitNewline();
        writer.indent();
        propertyDescriptors(cb, rb, ((Map) (map)), cb);
        writer.emitExpression("propDescriptors = new PropertyDescriptor[] {",
                true);
        writer.indent();
        String prop;
        for (Iterator props = map.keySet().iterator(); props.hasNext(); writer
                .emitExpression("prop_" + prop + ",", true))
            prop = (String) props.next();
        
        writer.outdent();
        writer.emitExpression("};", true);
        if (raveBaseBI())
            writer.emitExpression(
                    "annotatePropertyDescriptors(propDescriptors);", true);
        writer.emitExpression("return propDescriptors;", true);
        writer.emitNewline();
        writer.outdent();
        writer.emitExpression("} catch (IntrospectionException e) {", true);
        writer.indent();
        writer.emitExpression("e.printStackTrace();", true);
        writer.emitExpression("return null;", true);
        writer.outdent();
        writer.emitExpression("}", true);
        writer.emitNewline();
        writer.endMethod();
        writer.emitNewline();
    }
    
    private void propertyDescriptors(ComponentBean cb, RendererBean rb,
            Map map, ComponentBean componentBeanBeingGenerated)
            throws IOException {
        PropertyBean pbs[] = cb.getProperties();
        for (int i = 0; i < pbs.length; i++) {
            if (map.containsKey(pbs[i].getPropertyName())
            || "valid".equals(pbs[i].getPropertyName()))
                continue;
            PropertyBean pb = merge(pbs[i], rb.getAttribute(pbs[i]
                    .getPropertyName()));
            if (!pb.isSuppressed()) {
                propertyDescriptor(cb, rb, pb, componentBeanBeingGenerated);
                map.put(pb.getPropertyName(), null);
            }
        }
        
        String baseComponentType = cb.getBaseComponentType();
        if (baseComponentType != null) {
            ComponentBean bcb = getConfig().getComponent(baseComponentType);
            propertyDescriptors(bcb, rb, map, componentBeanBeingGenerated);
        }
    }
    
    protected boolean raveBaseBI() {
        return "com.sun.jsfcl.std.HtmlBeanInfoBase".equals(baseBI);
    }
    
    protected void shortDescription(DescriptionBean description,
            DescriptionBean descriptions[], JavaSourceWriter writer,
            String variableName, String packageName, String simpleClassName,
            String propertyName) throws IOException {
        String key = simpleClassName;
        if (propertyName != null)
            key = key + "_" + propertyName;
        key = key + "_Description";
        if (description != null || getUseComponentResourceBundles()) {
            writer.emitExpression(variableName, false);
            writer.emitExpressionPart(".setShortDescription(");
        }
        String descriptionString = description != null ? description
                .getDescription().trim() : "";
        if (getUseComponentResourceBundles()) {
            putBundleMap(packageName, "", key, descriptionString);
            writer.emitExpressionPart("resources.getString(");
            writer.emitJavaString(key);
            writer.emitExpressionPart(")");
        } else if (description != null)
            writer.emitJavaString(descriptionString);
        if (description != null || getUseComponentResourceBundles()) {
            writer.emitExpressionPart(");");
            writer.emitNewline();
        }
        for (int i = 0; i < descriptions.length; i++) {
            String lang = descriptions[i].getLang();
            if ("".equals(lang))
                continue;
            descriptionString = descriptions[i].getDescription();
            if (getUseComponentResourceBundles())
                putBundleMap(packageName, lang, key, descriptionString);
            else
                writer.emitExpression(variableName + ".setValue("
                        + writer.toJavaString("description." + lang) + ","
                        + writer.toJavaString(descriptionString) + ");", true);
        }
        
    }
    
    private String simpleClassName(ComponentBean cb) {
        return simpleClassName(cb.getComponentClass())
        + (getBase() ? "BeanInfoBase" : "BeanInfo");
    }
    
    private String simpleInstanceName(ComponentBean cb, RendererBean rb) {
        String cname = cb.getComponentType();
        int period = cname.lastIndexOf('.');
        if (period >= 0)
            cname = cname.substring(period + 1);
        String rname = rb.getRendererType();
        if (rname == null){
            rname = "";
        }
        period = rname.lastIndexOf('.');
        if (period >= 0)
            rname = rname.substring(period + 1);
        if (cname.equalsIgnoreCase(rname) || rname.length() < 1){
            return Character.toLowerCase(cname.charAt(0)) + cname.substring(1);
        }else{
            return Character.toLowerCase(cname.charAt(0)) + cname.substring(1)
            + Character.toUpperCase(rname.charAt(0))
            + rname.substring(1);
        }
    }
    
    private String simpleTagName(ComponentBean cb, RendererBean rb) {
        String cname = cb.getComponentType();
        int period = cname.lastIndexOf('.');
        if (period >= 0)
            cname = cname.substring(period + 1);
        String rname = rb.getRendererType();
        if (rname == null)
            rname = "";
        period = rname.lastIndexOf('.');
        if (period >= 0)
            rname = rname.substring(period + 1);
        if (cname.equalsIgnoreCase(rname) || rname.length() < 1){
            return Character.toLowerCase(cname.charAt(0)) + cname.substring(1);
        }else{
            return Character.toLowerCase(cname.charAt(0)) + cname.substring(1)
            + Character.toUpperCase(rname.charAt(0))
            + rname.substring(1);
        }
    }
    
    protected String trimWhitespace(String string) {
        
        StringBuffer buffer = new StringBuffer(string.length());
        boolean eatingWhite = true;
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (eatingWhite) {
                if (Character.isWhitespace(c))
                    continue;
                eatingWhite = false;
            }
            if (Character.isWhitespace(c)) {
                buffer.append(" ");
                eatingWhite = true;
            } else {
                buffer.append(c);
            }
        }
        
        return buffer.toString();
    }
    
}
