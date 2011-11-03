<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE faces-config PUBLIC
  "-//Sun Microsystems, Inc.//DTD JavaServer Faces Config 1.1//EN"
  "http://java.sun.com/dtd/web-facesconfig_1_1.dtd">
<faces-config>
<#list componentBeans as componentBean>
    <component>
        <component-type>${componentBean.componentType}</component-type>
        <component-class>${componentBean.componentClass}</component-class>
    </component>
</#list>
    <render-kit>
        <#list rendererBeans as rendererBean>
            <renderer>
                <component-family>${rendererBean.componentFamily}</component-family>
                <renderer-type>${rendererBean.rendererType}</renderer-type>
                <renderer-class>${rendererBean.rendererClass}</renderer-class>
            </renderer>
        </#list>
    </render-kit>
</faces-config>
