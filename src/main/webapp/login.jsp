

<%


    String nombreUsuario = (String) request.getAttribute("nombreUsuario");

%>

<h1>Bievenido<%=nombreUsuario%></h1>





<form action="users" method="post" >

    <label for="nombre">Nombre</label>
    <input type="text" id="nombre" name="nombre">

    <br>

    <label for="apellido">Apellido</label>
    <input type="text" id="apellido" name="apellido">

    <br>

    <label for="password">Password</label>
    <input type="password" id="password" name="password">

    <button type="submit">Enviar</button>

</form>
