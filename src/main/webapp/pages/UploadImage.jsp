<%--
  Created by IntelliJ IDEA.
  User: jhonc
  Date: 22/10/2025
  Time: 15:09
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<form action="${pageContext.request.contextPath}/uploadImagen"
      method="post" enctype="multipart/form-data">
    <input type="file" name="imagen" accept="image/*" required>
    <button type="submit">Subir</button>
</form>


</body>
</html>
