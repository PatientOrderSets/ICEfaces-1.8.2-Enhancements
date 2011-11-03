<html>
<head>
    <link rel="stylesheet" type="text/css" href="./icesoft_styles1.css" />
</head>
<body>
<h3>jsp:include Example</h3>
This example shows JSP inclusion.<br>
This JSP was served on <%= new java.util.Date() %>.<br>
The ICEfaces content below is incrementally and asynchronously updated.<br>
<br>
<jsp:include page="/timezone.iface" />
</body>
</html>
