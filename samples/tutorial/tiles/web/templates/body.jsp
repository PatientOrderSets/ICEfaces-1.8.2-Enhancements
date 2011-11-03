<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles" %>

<html>
    <head> <title><tiles:getAsString name="title"/></title> </head>
    <link rel="stylesheet" type="text/css" href="./icesoft_styles1.css" />
    <body bgcolor="white">
 
      <tiles:insert definition="header" /> 
      <div id="timeZonePanel" >
        <tiles:insert definition="display" /> 
        <tiles:insert definition="map" />  
      </div>

    </body>
</html>
