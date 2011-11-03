/*
 * MetadataTest.java
 *
 * Created on June 13, 2007, 11:59 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.icesoft.jsfmeta;

import com.sun.rave.jsfmeta.beans.ComponentBean;
import com.sun.rave.jsfmeta.beans.FacesConfigBean;
import com.sun.rave.jsfmeta.beans.PropertyBean;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.xml.sax.SAXException;

/**
 *
 * @author fye
 */
public class MetadataTest extends TestCase{
    
    private String METADATA_XML = "extended-faces-config.xml";
    private FacesConfigBean facesConfigBean;
    
    public static Test suite() {
        return new TestSuite(MetadataTest.class);
    }
    
    public static void main() {
        junit.textui.TestRunner.run(MetadataTest.suite());
    }
    
    protected void setUp(){
        
        File confFile = getConfDir();
        boolean isConfFile = confFile.isDirectory();
        if(!isConfFile){
            System.out.println("no conf directory in the build directory: "+ confFile);
            if( !confFile.mkdirs() )
                System.out.println("conf directory could not be created");
        }
        MetadataXmlParser jsfMetaParser = new MetadataXmlParser();
        String filePath = confFile.getPath() + File.separatorChar
                + METADATA_XML;
        try {
            facesConfigBean = jsfMetaParser.parse(new File(filePath));
            
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void testMetadata(){
        
        ComponentBean[] componentBeans = facesConfigBean.getComponents();
        for(int i=0; i<componentBeans.length; i++){
            PropertyBean[] propertyBeans = componentBeans[i].getProperties();
            for(int j=0; j<propertyBeans.length; j++){
                if(propertyBeans[j].getPropertyClass() != null && propertyBeans[j].getPropertyClass().trim().equalsIgnoreCase("boolean")){                    
                    if(propertyBeans[j].getEditorClass() != null){
                        boolean flag = propertyBeans[j].getEditorClass().trim().length() > 0;
                        assertFalse("\ncomponent class="+componentBeans[i].getComponentClass()+
                                "\ncomponent type="+componentBeans[i].getComponentType()+" has boolean property named="+propertyBeans[j].getPropertyName()
                        +" \nusing wrong editor="+ propertyBeans[j].getEditorClass(), flag);
                    }
                }
            };
        }
    }
    
    private File getConfDir() {
        
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL url = classLoader.getResource(".");
        
        File buildDir = new File( convertFileUrlToPath(url) );
        
        if(!buildDir.isDirectory()){
            System.out.println("test build directory does not exist: "+buildDir);
            System.exit(1);
        }
        
        File confFile = new File(buildDir.getParent() + File.separatorChar
                + "classes" + File.separator + "conf");
        
        return confFile;
    }
    /**
     * Kind of hack-ish attempt at solving problem that if the directory,
     *  where we're building the component-metadata in,  has special
     *  characters in its path, like spaces, then the URL to it will be
     *  escaped, which will be interpretted as a different directory,
     *  unless we unescape it.
     */
    private static String convertFileUrlToPath(URL url) {
        
        String path = url.getPath();
        if( url.toExternalForm().startsWith("file:") ) {
            StringBuffer sb = new StringBuffer( path.length() );
            int pathLength = path.length();
            for(int i = 0; i < pathLength;) {
                char c = path.charAt(i);
                if( c == '%' ) {
                    if( (i+1) < pathLength && isHexDigit(path.charAt(i+1)) ) {
                        int increment = 2;
                        if( (i+2) < pathLength && isHexDigit(path.charAt(i+2)) )
                            increment++;
                        try {
                            char unescaped = (char) Integer.parseInt(
                                    path.substring(i+1, i+increment), 16);
                            
                            sb.append( unescaped );
                            i += increment;
                            continue;
                        } catch(NumberFormatException nfe) {
                            // Not a valid hex escape, so just fall through,
                            //  and append it to the path
                        }
                    }
                }
                sb.append( c );
                i++;
            }
            path = sb.toString();
        }
        return path;
    }
    
    private static boolean isHexDigit(char c) {
        return ( (c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') );
    }
}
