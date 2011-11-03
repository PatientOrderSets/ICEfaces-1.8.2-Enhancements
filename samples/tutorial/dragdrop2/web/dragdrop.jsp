<f:view xmlns:f="http://java.sun.com/jsf/core"
        xmlns:h="http://java.sun.com/jsf/html"
        xmlns:ice="http://www.icesoft.com/icefaces/component">
    <html>
    <head>
    </head>
    <body>
    <ice:outputText value="See the Drag and Drop Tutorial in the Advanced topics section of the Developers guide."/>
<ice:form>
  <ice:panelGroup style="z-index:10;width:200px;height:60px;background:#ddd;border:2px solid black; cursor:move;" 
    draggable="true" dragListener="#{dragDropBean.dragPanelListener}" dragMask="dragging,hover_start,hover_end">
                                
      <ice:outputText value="#{dragDropBean.dragPanelMessage}"/>
  </ice:panelGroup>
  
  <ice:panelGroup style="z-index:0;width:250px;height:100px;background:#FFF;border:2px solid black;"
     dropTarget="true" dropValue="One">     
     <ice:outputText value="One"/>
  </ice:panelGroup>
  <ice:panelGroup style="z-index:0;width:250px;height:100px;background:#FFF;border:2px solid black;"
     dropTarget="true" dropValue="Two">     
     <ice:outputText value="Two"/>
  </ice:panelGroup>
</ice:form>
    </body>
    </html>
</f:view>