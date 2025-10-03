<%@ page import="java.util.List" %>
<h1>${mensajeBienvenida}</h1>

<%

    List<String> nombres = (List<String>) request.getAttribute("lista");

    HttpSession sesion = (HttpSession) request.getSession();



%>

<body>

<%@include file="WEB-INF/components/header.jsp"%>


<h1>Bievenido <% sesion.getAttribute("logueado"); %>}</h1>


</body>