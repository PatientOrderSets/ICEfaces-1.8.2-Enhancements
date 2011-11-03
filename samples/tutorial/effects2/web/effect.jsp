<f:view xmlns:f="http://java.sun.com/jsf/core"
        xmlns:h="http://java.sun.com/jsf/html"
        xmlns:ice="http://www.icesoft.com/icefaces/component">
    <html>
    <head>
    </head>
    <body>
    
<ice:form>
<ice:outputText value="See the Effects Tutorial in the Advanced topics section of the Developers guide."/>
  <ice:commandButton value="Invoke" action="#{effectBean.invokeEffect}"/>
  <ice:outputText value="Effect Test" onmouseovereffect="#{effectBean.textEffect}"/><br/>
  <ice:outputText value="To invoke the effects:"/>  
     <ol>
        <li><ice:outputText value="Click the Invoke button."/></li>
        <li><ice:outputText value="Mouse over the 'Effect Test' text to see the pulsate effect."/></li>
        <li><ice:outputText value="To see a different effect click the Invoke button."/></li>
        <li><ice:outputText value="Mouse over the 'Effect Test' text to see the highlight effect."/></li>
	<li><ice:outputText value="Click the Invoke button to toggle between the two effects."/></li>
    </ol>
</ice:form>
    </body>
    </html>
</f:view>