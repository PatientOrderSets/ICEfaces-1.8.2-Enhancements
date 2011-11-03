package com.icesoft.metadata.test;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class DefaultValueTest {
    String tld;
    private StringBuffer result = new StringBuffer();
    private Map componentMap = new HashMap();
    
    public DefaultValueTest(String tld) {
        this.tld = tld;

        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse (new File(tld));
    
            doc.getDocumentElement ().normalize ();
            NodeList components = doc.getElementsByTagName("component");
       
            for(int s=0; s<components.getLength() ; s++){
                Node component = components.item(s);
                Element componentElement = (Element)component;
                NodeList name = componentElement.getElementsByTagName("name");
                String componentClass = name.item(0).getFirstChild().getNodeValue();
                Map attributeMap = new HashMap();
                componentMap.put(componentClass, attributeMap);
                NodeList attributes = componentElement.getElementsByTagName("attribute");
                for (int i = 0; i < attributes.getLength(); i++ ) {
                    Element attribute = (Element)attributes.item(i);
                    name = attribute.getElementsByTagName("name");
                    String attributeName = name.item(0).getFirstChild().getNodeValue();
                    NodeList defaultValueNode = attribute.getElementsByTagName("default-value");
                    String defaultValue = defaultValueNode.item(0).getFirstChild().getNodeValue();
                    attributeMap.put(attributeName, defaultValue);
                }
            }
        }catch (Exception e) {
                e.printStackTrace ();
        }

        startDocument(tld);
        Iterator componentsIt = componentMap.keySet().iterator();
        while (componentsIt.hasNext()) {
            String clazz = String.valueOf(componentsIt.next());
            Map attMap = (Map)componentMap.get(clazz);
            printClassMembers(clazz, attMap);
        }
        endDocument();
        generateResult(tld);
    }
    
    public void printClassMembers(String clazz, Map attMap) {
        Class c = null;
        Object obj = null;
        try {
            c = Class.forName(clazz);
            obj = c.newInstance() ;
            result.append("<tr><td colspan='2' style='background-color:#999999;'> <h4>"+ clazz + "</h4></td></tr>");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        PropertyDescriptor[] pd = null;

            try {
                pd = Introspector.getBeanInfo(c).
                    getPropertyDescriptors();
            } catch (IntrospectionException e) {
                e.printStackTrace();
            }
            int passed = 0;
            int failed = 0;
            for (int i = 0; i < pd.length; i++) {
                Method readMethod = pd[i].getReadMethod();
                if (readMethod != null) {
                    try {
                        String attName =  pd[i].getName();
                        String value = readMethod.invoke(obj, new Object[0])+"";
                        if (attMap.containsKey(attName)) {
                            String valueInTld = String.valueOf(attMap.get(attName));
                            valueInTld = valueInTld.replaceAll("\"", "");
                            if (attributeValueIsSentinel(value)) {
                               if ("null".equals(valueInTld)) {
                                   passed++;
                                   result.append("<tr style='background-color:#339900;'><td >"+ attName +"</td><td> sentinel value found "+ value +"</td></tr>");
                               } else {
                                   failed++;
                                   result.append("<tr style='background-color:#FF6633;'><td >"+ attName +"</td><td>[Warning] sentinel value found : ["+  value + "], &nbsp;&nbsp;&nbsp;and metadata contains a default value ["+ valueInTld +"], However default-value is not required for sentinel value</td></tr>");
                               }
                            }else if (value.equals(valueInTld)) {
                                passed++;
                                result.append("<tr style='background-color:#339900;'><td >"+ attName +"</td><td>"+ value +"</td></tr>");
                            } else {
                                failed++;
                                result.append("<tr style='background-color:#CC0000;'><td >"+ attName +"</td><td> component returns expected: ["+  value + "], &nbsp;&nbsp;&nbsp;in meta-data found: ["+ valueInTld +"] </td></tr>");
                            }

                        }
                    } catch (Exception e) {}
                }                
            }
            result.append("<tr><td colspan='2'> <b>"+ passed +"</b> passed, and <b>"+ failed+"</b> failed <br/><br/></td></tr>");
    }
 
    private void startDocument(String tld) {
        result.append("<html>\n");
        result.append("<head>");
        result.append("<title>Default values test based on "+ tld.substring(tld.lastIndexOf("\\")+1)  + "</title>");        
        result.append("</head>\n");
        result.append("<body>\n");
        result.append("<table border='1'>\n");
    }
    
    
    private void endDocument() {
        result.append("</table>"); 
        result.append("</body>");         
        result.append("</html>");        
    }

    private void generateResult(String tld) {
      try{
          String resultHtml = tld.replaceAll(".xml", ".html");
          FileWriter fstream = new FileWriter(resultHtml);
          BufferedWriter out = new BufferedWriter(fstream);
          out.write(result.toString());
          out.close();
      }catch (Exception e){
          System.err.println("Error: " + e.getMessage());
      }
    }
    
    private boolean attributeValueIsSentinel(String value) {
        if (value == null) {
            return true;
        }

        if ("null".equals(value)) {
            return true;
        }
        
        if ("".equals(value)) {
            return true;
        }
        
        if ("false".equals(value)) {
            return true;
        }
        
        if (String.valueOf(Integer.MIN_VALUE).equals(value)) {
            return true;
        }

        if (String.valueOf(Long.MIN_VALUE).equals(value)) {
            return true;
        }
        
        if (String.valueOf(Short.MIN_VALUE).equals(value)) {
            return true;
        }

        if (String.valueOf(Float.MIN_VALUE).equals(value)) {
            return true;
        }

        if (String.valueOf(Double.MIN_VALUE).equals(value)) {
            return true;
        }

        if (String.valueOf(Byte.MIN_VALUE).equals(value)) {
            return true;
        }

        if (String.valueOf(Character.MIN_VALUE).equals(value)) {
            return true;
        }
        return false;
    }    
}



