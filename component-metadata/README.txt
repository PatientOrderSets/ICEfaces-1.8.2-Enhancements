This is the README file for Component Metadata.

Directory of metadata (src/main/resources):
-- conf
-- -- component (extended standard component)
-- -- custom (custom component)
-- -- ice/components (component-type  definition)
-- -- ice/renderers (renderer-type definition)
-- -- ice_cust_properties (custom component properties)
-- -- ice_properties (extended component properties)
-- -- properties (Sun's RI properties, normally comes from baseline component)

IDE needs both grammar and metadata information for JSF components in 
order to provide a design-time experience.  


faces-config-base.xml ( grammar information and a minimal set of metadata for 
components, attributes, properties, facets, and renderers )

The following files extending the metadata in faces-config.xml. (Schema is based on Creator 2 dtd, TODO xsd). 
It is defined with data for generating tag class,  tld file and beainfo specifc to IDE.

extended-faces-config.xml (Netbeans VWP specifc)

Generator Configuration file:
main/resources/conf/config.properties


How to add in a new custom component:

#1 add metadata in conf/custom

style-component.xml

<?xml version="1.0" encoding="UTF-8"?>
<component>
	<description>
		<![CDATA[ Links one or more theme CSS files into the page ]]>
	</description>
	<display-name>Output Style</display-name>
	<component-type>com.icesoft.faces.OutputStyleComp</component-type>

	&ice-cust-style-props; //entity reference in extended-faces-config.xml
	<component-extension>
	    <base-component-type>com.sun.faces.Component</base-component-type> //baseline component extended from using generated component 
		<component-family>com.icesoft.faces.OutputStyle</component-family> // same from faces-config.xml
		<renderer-type>com.icesoft.faces.style.OutputStyleRenderer</renderer-type>  // same from faces-config.xml
	</component-extension>
</component>

style-renderer.xml

<?xml version="1.0" encoding="UTF-8"?>
<renderer>
         <description>
             <![CDATA[ Description in here will be used to generate TLD file ]]>
         </description>
	<component-family>com.icesoft.faces.OutputStyle</component-family>
	<renderer-type>com.icesoft.faces.style.OutputStyleRenderer</renderer-type>	
	<renderer-extension>
		<instance-name>outputStyle</instance-name>
		<is-container>false</is-container> //has children
		<renders-children>false</renders-children> //render children
		<tag-name>outputStyle</tag-name>
		<taglib-prefix>@taglib-prefix@</taglib-prefix>
		<taglib-uri>@taglib-uri@</taglib-uri>
	</renderer-extension>
</renderer>


#2 add properties in  conf/ice_cust_properties

cust-style-props.xml

<property>
        <property-name>actionListener</property-name>
        <property-extension>
                <category>ADVANCED</category>
                <editor-class>
                        &methodBindingEditor;
                </editor-class>
                <is-hidden>true</is-hidden>
                <tag-attribute>false</tag-attribute> //hide property from tag attribute, no tag class and tld generated.
        </property-extension>
</property>

also 
<property>
        <description>
                The dropListener specifies a method on a backing bean that will
                accept DnDEvents. This value must be a method binding
                expression. This method will be called when a droppable is
                dropped or hovered on this panel unless masked.
        </description>
        <property-name>dropListener</property-name>
        <property-class>javax.faces.el.MethodBinding</property-class>
                <property-extension>
                <category>DRAGANDDROP</category>
                <editor-class>
                        &methodBindingEditor;
                </editor-class>
                <is-bindable>true</is-bindable>//indicating whether
    or not value binding expressions may be used to specify the value of
    the surrounding attribute or property
        </property-extension>
</property>

#3 create entity in extended-faces-config.xml

  <!ENTITY ice-cust-style-props SYSTEM "ice_cust_properties/cust-style-props.xml">
  <!ENTITY style-component SYSTEM "custom/style-component.xml">
  <!ENTITY style-renderer SYSTEM "custom/style-renderer.xml">

and

faces-config
&ice-style-component

renderer-kit
&ice-style-renderer

#4 add component and render in faces-config.xml (component-metadata/main/resources/conf/faces-config-base.xml)

<!ENTITY ice-OutputStyle-component SYSTEM "ice/components/OutputStyle-component.xml">
<!ENTITY ice-OutputStyle-renderer SYSTEM "ice/renderers/OutputStyle-renderer.xml">

      ice/renderers/OutputStyle-renderer.xml
      <renderer>
            <component-family>com.icesoft.faces.OutputStyle</component-family>
            <renderer-type>com.icesoft.faces.style.OutputStyleRenderer</renderer-type>
            <renderer-class>com.icesoft.faces.component.style.OutputStyleRenderer</renderer-class>
      </renderer>

    ice/components/OutputStyle-component.xml
    <component>
      <component-type>com.icesoft.faces.OutputStyleComp</component-type>
      <component-class>com.icesoft.faces.component.style.OutputStyle</component-class>
    </component>


#5 run generator target in build.xml. The following class will be generated: tag, baseline component, beaninfo and new tld file

#6 IDE design time related 

extends com/icesoft/faces/component/style/OutputStyleBeanInfoBase.java
in designtime directory:
com/icesoft/faces/component/style/OutputStyleBeanInfo.java

#7 run time related

implement OutputStyle and design time related class ...


Running code generator:

build.xml file related target


Override specific method output etc ...

Modify metadata generator:
metadata/src  ("jsfmeta data generators")


Generated Sources Directory:

component-metadata/target/geneated-sources

Component Baseline
component-metadata/target/geneated-sources/component/main/java

Tag Classes under
component-metadata/target/geneated-sources/taglib/main/java

Tld under
component-metadata/target/geneated-sources/tld/icefaces_component.tld

beaninfo
component-metadata/target/generated-sources/beaninfo/main/java

testbeaninfo
component-metadata/target/generated-sources/testbeaninfo/main/java

(testbeaninfo is generated without dependencies on IDE related classes)


Reference:

Design-Time Metadata for JavaServerTM Faces Components
http://jcp.org/en/jsr/detail?id=276

Schema
http://wiki.java.net/bin/view/Javatools/SunFacesConfigDtd

Component Library Package File Specification
http://wiki.java.net/bin/view/Javatools/ThresherComplibSpec

Custom Component Libraries
http://wiki.java.net/bin/view/Javatools/CustomComponentLibraries

Metadata Proposal
http://www.jsfcentral.com/articles/oracle_metadata_proposal.html
