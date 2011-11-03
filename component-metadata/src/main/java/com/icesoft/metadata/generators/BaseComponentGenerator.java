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
import com.icesoft.jsfmeta.util.JavaSourceWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.sun.rave.jsfmeta.beans.AttributeBean;
import com.sun.rave.jsfmeta.beans.ComponentBean;
import com.sun.rave.jsfmeta.beans.DescriptionBean;
import com.sun.rave.jsfmeta.beans.PropertyBean;
import com.sun.rave.jsfmeta.beans.RendererBean;

public class BaseComponentGenerator extends AbstractGenerator {
    
    private boolean base;
    
    private boolean override;
    
    public BaseComponentGenerator(InternalConfig internalConfig) {
        
        super(internalConfig);        
        base = true;
        override = false;
    }
        
    public boolean getBase() {
        return base;
    }
    
    public void setBase(boolean base) {
        this.base = base;
    }
    
    public boolean isOverride() {
        return override;
    }
    
    public void setOverride(boolean override) {
        this.override = override;
    }
    
    public void generate() throws IOException {
        
        ComponentBean cbs[] = getConfig().getComponents();
        for (int i = 0; i < cbs.length; i++){
            if (generated(cbs[i].getComponentClass()))
                generate(cbs[i]);
        }
    }
    
    private boolean isAliasFor(PropertyBean[] pbs){
        
        for(int i=0; i< pbs.length; i++){
            
            if(pbs[i].getAliasFor() != null){
                return true;
            }
        }
        
        return false;
    }
    
    private void bindings(ComponentBean cb) throws IOException {
        
        PropertyBean pbs[] = cb.getProperties();
        
        if (!isAliasFor(pbs)){
            return;
        }
        JavaSourceWriter writer = getWriter();
        String propertyName = null;
        String aliasName = null;
        writer.startJavaDoc();
        writer
                .emitJavaDoc("<p>Return the <code>ValueBinding</code> stored for the");
        writer
                .emitJavaDoc("specified name (if any), respecting any property aliases.</p>");
        writer.emitJavaDoc();
        writer.emitJavaDoc("@param name Name of value binding to retrieve");
        writer.endJavaDoc();
        writer.startMethod("getValueBinding", "ValueBinding",
                new String[] { "String" }, new String[] { "name" });
        for (int k = 0; k < pbs.length; k++) {
            aliasName = pbs[k].getAliasFor();
            if (aliasName != null) {
                propertyName = pbs[k].getPropertyName();
                writer.emitExpression("if (name.equals("
                        + writer.toJavaString(propertyName) + ")) {", true);
                writer.indent();
                writer.emitExpression("return super.getValueBinding("
                        + writer.toJavaString(aliasName) + ");", true);
                writer.outdent();
                writer.emitExpression("}", true);
            }
        }
        
        writer.emitExpression("return super.getValueBinding(name);", true);
        writer.endMethod();
        writer.emitNewline();
        writer.startJavaDoc();
        writer
                .emitJavaDoc("<p>Set the <code>ValueBinding</code> stored for the");
        writer.emitJavaDoc("specified name (if any), respecting any property");
        writer.emitJavaDoc("aliases.</p>");
        writer.emitJavaDoc();
        writer.emitJavaDoc("@param name    Name of value binding to set");
        writer
                .emitJavaDoc("@param binding ValueBinding to set, or null to remove");
        writer.endJavaDoc();
        writer.startMethod("setValueBinding", "void", new String[] { "String",
        "ValueBinding" }, new String[] { "name", "binding" });
        for (int j = 0; j < pbs.length; j++) {
            aliasName = pbs[j].getAliasFor();
            if (aliasName != null) {
                propertyName = pbs[j].getPropertyName();
                writer.emitExpression("if (name.equals("
                        + writer.toJavaString(propertyName) + ")) {", true);
                writer.indent();
                writer.emitExpression("super.setValueBinding("
                        + writer.toJavaString(aliasName) + ", binding);", true);
                writer.emitExpression("return;", true);
                writer.outdent();
                writer.emitExpression("}", true);
            }
        }
        
        writer.emitExpression("super.setValueBinding(name, binding);", true);
        writer.endMethod();
        writer.emitNewline();
    }
    
    private void constructor(ComponentBean cb) throws IOException {
        JavaSourceWriter writer = getWriter();
        String simple = simpleClassName(cb.getComponentClass());
        if (getBase())
            simple = simple + "Base";
        String rendererType = rendererType(cb);
        writer.startJavaDoc();
        writer.emitJavaDoc("<p>Construct a new <code>" + simple
                + "</code>.</p>");
        writer.endJavaDoc();
        writer.startMethod(simple, null, null, null);
        writer.emitExpression("super();", true);
        if (rendererType != null) {
            writer.emitExpression("setRendererType(\"" + rendererType + "\");",
                    true);
            writer.endMethod();
        }
        writer.emitNewline();
    }
    
    private void family(ComponentBean cb) throws IOException {
        JavaSourceWriter writer = getWriter();
        writer.startJavaDoc();
        writer.emitJavaDoc("<p>Return the family for this component.</p>");
        writer.endJavaDoc();
        writer.startMethod("getFamily", "String", null, null);
        writer.emitExpression("return \"" + componentFamily(cb) + "\";", true);
        writer.endMethod();
        writer.emitNewline();
    }
    
    private void footer(ComponentBean cb) throws IOException {
        JavaSourceWriter writer = getWriter();
        writer.endClass();
    }
    
    private void generate(ComponentBean cb) throws IOException {
        if (cb.getComponentClass().startsWith("javax.faces.component."))
            return;
        if (cb.isSuppressed()) {
            return;
        } else {
            File outputFile = outputFile(cb.getComponentClass()
            + (getBase() ? "Base" : ""));
            outputFile.mkdirs();
            outputFile.delete();
            getWriter().setOutputWriter(
                    new BufferedWriter(new FileWriter(outputFile)));
            license(cb);
            header(cb);
            constructor(cb);
            family(cb);
            bindings(cb);
            properties(cb);
            restore(cb);
            save(cb);
            footer(cb);
            getWriter().flush();
            getWriter().close();
            return;
        }
    }
    
    private void header(ComponentBean cb) throws IOException {
        
        ComponentBean bcb = null;
        String baseComponentType = cb.getBaseComponentType();
        if (baseComponentType != null)
            bcb = getConfig().getComponent(baseComponentType);
        RendererBean rb = renderer(cb);
        JavaSourceWriter writer = getWriter();
        String simple = simpleClassName(cb.getComponentClass());
        if (getBase())
            simple = simple + "Base";
        String componentClass = cb.getComponentClass();
        int last = componentClass.lastIndexOf('.');
        if (last >= 0) {
            writer.emitPackage(componentClass.substring(0, last));
            writer.emitNewline();
        }
        writer.emitImport("java.io.IOException");
        writer.emitImport("javax.faces.component.UIComponent");
        writer.emitImport("javax.faces.context.FacesContext");
        writer.emitImport("javax.faces.el.MethodBinding");
        writer.emitImport("javax.faces.el.ValueBinding");
        writer.emitNewline();
        writer.startJavaDoc();
        DescriptionBean db = null;
        if (isOverride() && rb != null){
            db = rb.getDescription("");
        }
        if (db == null){
            db = cb.getDescription("");
        }
        if (db != null) {
            String description = db.getDescription();
            if (description != null && description.length() > 0)
                writer.emitJavaDocMultiLine(description.trim());
        }
        writer.emitJavaDoc("<p>Auto-generated component class.");
        writer.emitJavaDoc("Do <strong>NOT</strong> modify; all changes");
        writer.emitJavaDoc("<strong>will</strong> be lost!</p>");
        writer.endJavaDoc();
        writer.emitNewline();
        if (bcb != null){
            writer.startClass(simple, bcb.getComponentClass(), null, !getBase(),
                    getBase());
        }else{
            writer.startClass(simple, "javax.faces.component.UIComponentBase",
                    null, true, getBase());
        }
        writer.emitNewline();
    }
    
    private void license(ComponentBean componentbean) throws IOException {
        
        JavaSourceWriter writer = getWriter();
        writer.emitLicense();
    }
    
    private void properties(ComponentBean cb) throws IOException {
        PropertyBean pbs[] = cb.getProperties();
        if (pbs == null){
            return;
        }
        for (int i = 0; i < pbs.length; i++){
            if (!pbs[i].isSuppressed()){
                property(cb, pbs[i]);
            }
        }
        
    }
    
    private void property(ComponentBean cb, PropertyBean pb) throws IOException {
        String name = pb.getPropertyName();
        String type = pb.getPropertyClass();
        if (type == null){
            type = "String";
        } else if (type.startsWith("java.lang.")){
            type = type.substring(10);
        }
        RendererBean rb = renderer(cb);
        AttributeBean ab = null;
        if (rb != null){
            ab = rb.getAttribute(pb.getPropertyName());
        }
        String var = mangle(name);
        JavaSourceWriter writer = getWriter();
        String aliasFor = pb.getAliasFor();
        if (aliasFor != null) {
            PropertyBean pb1 = cb.getProperty(aliasFor);
            if (pb1 == null) {
                for (ComponentBean cb1 = cb; pb1 == null && cb1 != null; pb1 = cb1
                        .getProperty(aliasFor)) {
                    String bct = cb1.getBaseComponentType();
                    if (bct == null)
                        throw new IllegalArgumentException(aliasFor);
                    cb1 = getConfig().getComponent(bct);
                    if (cb1 == null)
                        throw new IllegalArgumentException(bct);
                }
                
                if (pb1 == null)
                    throw new IllegalArgumentException(aliasFor);
            }
            String aliasType = pb1.getPropertyClass();
            
            if (aliasType == null){
                aliasType = "String";
            }else if (aliasType.startsWith("java.lang.")){
                aliasType = aliasType.substring(10);
            }
            writer.emitExpression("// " + name, true);
            String readMethod = null;
            if (!pb.isWriteOnly()) {
                DescriptionBean db = null;
                if (isOverride() && ab != null){
                    db = ab.getDescription("");
                }
                if (db == null){
                    db = pb.getDescription("");
                }
                if (db != null) {
                    String description = db.getDescription();
                    if (description != null && description.length() > 0) {
                        writer.startJavaDoc();
                        description = description.trim();
                        if (description.startsWith("<")){
                            writer.emitJavaDocMultiLine(description);
                        }else{
                            writer.emitJavaDocMultiLine("<p>" + description
                                    + "</p>");
                        }
                        writer.endJavaDoc();
                    }
                }
                String method = pb.getReadMethod();
                if (method == null)
                    if ("boolean".equals(type))
                        method = "is" + capitalize(name);
                    else
                        method = "get" + capitalize(name);
                readMethod = method;
                writer.startMethod(method, type, null, null);
                StringBuffer sb = new StringBuffer("return ");
                if (!type.equals(aliasType)) {
                    sb.append("(");
                    sb.append(type);
                    sb.append(") ");
                }
                String aliasMethod = pb1.getReadMethod();
                if (aliasMethod == null){
                    if ("boolean".equals(aliasType)){
                        aliasMethod = "is" + capitalize(aliasFor);
                    }else{
                        aliasMethod = "get" + capitalize(aliasFor);
                    }
                }
                sb.append(aliasMethod);
                sb.append("();");
                writer.emitExpression(sb.toString(), true);
                writer.endMethod();
                writer.emitNewline();
            }
            if (!pb.isReadOnly()) {
                DescriptionBean db = null;
                if (isOverride() && ab != null)
                    db = ab.getDescription("");
                if (db == null)
                    db = pb.getDescription("");
                if (db != null) {
                    String description = db.getDescription();
                    if (description != null && description.length() > 0) {
                        writer.startJavaDoc();
                        description = description.trim();
                        if (description.startsWith("<"))
                            writer.emitJavaDocMultiLine(description);
                        else
                            writer.emitJavaDocMultiLine("<p>" + description
                                    + "</p>");
                        if (readMethod != null)
                            writer.emitJavaDoc("@see #" + readMethod + "()");
                        writer.endJavaDoc();
                    }
                }
                String method = pb.getWriteMethod();
                if (method == null)
                    method = "set" + capitalize(name);
                writer.startMethod(method, "void", new String[] { type },
                        new String[] { var });
                String aliasMethod = pb1.getWriteMethod();
                if (aliasMethod == null)
                    aliasMethod = "set" + capitalize(aliasFor);
                StringBuffer sb = new StringBuffer(aliasMethod);
                sb.append("(");
                if (!type.equals(aliasType)) {
                    sb.append("(");
                    sb.append(aliasType);
                    sb.append(") ");
                }
                sb.append(var);
                sb.append(");");
                writer.emitExpression(sb.toString(), true);
                writer.endMethod();
                writer.emitNewline();
            }
            return;
        }
        writer.emitExpression("// " + name, true);
        StringBuffer sb = new StringBuffer("private ");
        if ("java.lang.String".equals(type))
            sb.append("String");
        else
            sb.append(type);
        sb.append(" ");
        sb.append(var);
        sb.append(" = ");
        if (primitive(type))
            sb.append((String) defaults.get(type));
        else
            sb.append("null");
        sb.append(";");
        writer.emitExpression(sb.toString(), true);
        if (primitive(type) && pb.isBindable()) {
            sb = new StringBuffer("private boolean ");
            sb.append(var);
            sb.append("_set = false;");
            writer.emitExpression(sb.toString(), true);
        }
        writer.emitNewline();
        String readMethod = null;
        if (!pb.isWriteOnly()) {
            DescriptionBean db = null;
            if (isOverride() && ab != null)
                db = ab.getDescription("");
            if (db == null)
                db = pb.getDescription("");
            if (db != null) {
                String description = db.getDescription();
                if (description != null && description.length() > 0) {
                    writer.startJavaDoc();
                    description = description.trim();
                    if (description.startsWith("<"))
                        writer.emitJavaDocMultiLine(description);
                    else
                        writer.emitJavaDocMultiLine("<p>" + description
                                + "</p>");
                    writer.endJavaDoc();
                }
            }
            String method = pb.getReadMethod();
            if (method == null)
                if ("boolean".equals(type))
                    method = "is" + capitalize(name);
                else
                    method = "get" + capitalize(name);
            readMethod = method;
            writer.startMethod(method, type, null, null);
            if (!pb.isBindable()) {
                writer.emitExpression("return this." + var + ";", true);
            } else {
                if (primitive(type))
                    writer.emitExpression("if (this." + var + "_set) {", true);
                else
                    writer.emitExpression("if (this." + var + " != null) {",
                            true);
                writer.indent();
                writer.emitExpression("return this." + var + ";", true);
                writer.outdent();
                writer.emitExpression("}", true);
                writer.emitExpression("ValueBinding _vb = getValueBinding(\""
                        + name + "\");", true);
                writer.emitExpression("if (_vb != null) {", true);
                writer.indent();
                if (primitive(type)) {
                    writer
                            .emitExpression(
                            "Object _result = _vb.getValue(getFacesContext());",
                            true);
                    writer.emitExpression("if (_result == null) {", true);
                    writer.indent();
                    writer.emitExpression("return " + defaults.get(type) + ";",
                            true);
                    writer.outdent();
                    writer.emitExpression("} else {", true);
                    writer.indent();
                    writer.emitExpression("return ((" + wrappers.get(type)
                    + ") _result)." + unwrappers.get(type) + "();",
                            true);
                    writer.outdent();
                    writer.emitExpression("}", true);
                } else {
                    writer.emitExpression("return (" + type + ") "
                            + "_vb.getValue(getFacesContext());", true);
                }
                writer.outdent();
                writer.emitExpression("}", true);
                sb = new StringBuffer("return ");
                //remove me
                if (pb.getDefaultValue() != null){
                    if(pb.getDefaultValue().endsWith("\"") && pb.getDefaultValue().startsWith("\"")){
                        sb.append(pb.getDefaultValue());
                    }else if (primitive(type)){
                        sb.append((String) defaults.get(type));
                    }else if (pb.getDefaultValue() != null 
                        && pb.getDefaultValue().toLowerCase().trim().length() > 0){
                        sb.append("\""+pb.getDefaultValue()+"\"");
                    }
                }else if (primitive(type)){
                        sb.append((String) defaults.get(type));
                }else{
                    sb.append("null");
                }
                sb.append(";");
                writer.emitExpression(sb.toString(), true);
            }
            writer.endMethod();
            writer.emitNewline();
        }
        if (!pb.isReadOnly()) {
            DescriptionBean db = null;
            if (isOverride() && ab != null)
                db = ab.getDescription("");
            if (db == null)
                db = pb.getDescription("");
            if (db != null) {
                String description = db.getDescription();
                if (description != null && description.length() > 0) {
                    writer.startJavaDoc();
                    description = description.trim();
                    if (description.startsWith("<"))
                        writer.emitJavaDocMultiLine(description);
                    else
                        writer.emitJavaDocMultiLine("<p>" + description
                                + "</p>");
                    if (readMethod != null)
                        writer.emitJavaDoc("@see #" + readMethod + "()");
                    writer.endJavaDoc();
                }
            }
            String method = pb.getWriteMethod();
            if (method == null)
                method = "set" + capitalize(name);
            writer.startMethod(method, "void", new String[] { type },
                    new String[] { var });
            writer.emitExpression("this." + var + " = " + var + ";", true);
            if (primitive(type) && pb.isBindable())
                writer.emitExpression("this." + var + "_set = true;", true);
            writer.endMethod();
        }
        writer.emitNewline();
    }
    
    private void restore(ComponentBean cb) throws IOException {
        PropertyBean pbs[] = cb.getProperties();
        if (pbs == null)
            pbs = new PropertyBean[0];
        JavaSourceWriter writer = getWriter();
        writer.startJavaDoc();
        writer.emitJavaDoc("<p>Restore the state of this component.</p>");
        writer.endJavaDoc();
        writer.startMethod("restoreState", "void", new String[] {
            "FacesContext", "Object" },
                new String[] { "_context", "_state" });
        writer.emitExpression("Object _values[] = (Object[]) _state;", true);
        writer
                .emitExpression("super.restoreState(_context, _values[0]);",
                true);
        int index = 1;
        for (int i = 0; i < pbs.length; i++) {
            if (pbs[i].getAliasFor() != null)
                continue;
            String name = pbs[i].getPropertyName();
            String type = pbs[i].getPropertyClass();
            if (type == null)
                type = "String";
            else if (type.startsWith("java.lang."))
                type = type.substring(10);
            String var = mangle(name);
            if (primitive(type)) {
                writer.emitExpression("this." + var + " = (("
                        + wrappers.get(type) + ") _values[" + index++ + "])."
                        + unwrappers.get(type) + "();", true);
                if (pbs[i].isBindable())
                    writer.emitExpression("this." + var
                            + "_set = ((Boolean) _values[" + index++
                            + "]).booleanValue();", true);
                continue;
            }
            if (type.equals("javax.faces.el.MethodBinding"))
                writer.emitExpression("this." + var + " = (" + type + ") "
                        + "restoreAttachedState(_context, _values[" + index++
                        + "]);", true);
            else
                writer.emitExpression("this." + var + " = (" + type
                        + ") _values[" + index++ + "];", true);
        }
        
        writer.endMethod();
        writer.emitNewline();
    }
    
    private void save(ComponentBean cb) throws IOException {
        PropertyBean pbs[] = cb.getProperties();
        if (pbs == null)
            pbs = new PropertyBean[0];
        JavaSourceWriter writer = getWriter();
        writer.startJavaDoc();
        writer.emitJavaDoc("<p>Save the state of this component.</p>");
        writer.endJavaDoc();
        writer.startMethod("saveState", "Object",
                new String[] { "FacesContext" }, new String[] { "_context" });
        int n = pbs.length + 1;
        for (int i = 0; i < pbs.length; i++) {
            if (pbs[i].getAliasFor() != null) {
                n--;
                continue;
            }
            if (primitive(pbs[i].getPropertyClass()) && pbs[i].isBindable())
                n++;
        }
        
        writer
                .emitExpression("Object _values[] = new Object[" + n + "];",
                true);
        writer.emitExpression("_values[0] = super.saveState(_context);", true);
        int index = 1;
        for (int i = 0; i < pbs.length; i++) {
            if (pbs[i].getAliasFor() != null)
                continue;
            String name = pbs[i].getPropertyName();
            String type = pbs[i].getPropertyClass();
            if (type == null)
                type = "String";
            else if (type.startsWith("java.lang."))
                type = type.substring(10);
            String var = mangle(name);
            if (primitive(type)) {
                if (type.equals("boolean"))
                    writer.emitExpression("_values[" + index++ + "] = "
                            + "this." + var
                            + " ? Boolean.TRUE : Boolean.FALSE;", true);
                else
                    writer.emitExpression("_values[" + index++ + "] = new "
                            + wrappers.get(type) + "(this." + var + ");", true);
                if (pbs[i].isBindable())
                    writer.emitExpression("_values[" + index++ + "] = "
                            + "this." + var
                            + "_set ? Boolean.TRUE : Boolean.FALSE;", true);
                continue;
            }
            if (type.equals("javax.faces.el.MethodBinding"))
                writer
                    .emitExpression("_values[" + index++
                        + "] = saveAttachedState(_context, " + var
                        + ");", true);
            else
                writer.emitExpression("_values[" + index++ + "] = this." + var
                        + ";", true);
        }
        
        writer.emitExpression("return _values;", true);
        writer.endMethod();
        writer.emitNewline();
    }
    
}
