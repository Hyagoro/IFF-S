<%@ page pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8" />
        <title>Test</title>
    </head>
 
    <body>
        <p>Test IFF</p>
       	<% 
            String attribut = (String) request.getAttribute("test");
            out.println( attribut );
         %>
    </body>
</html>