package org.example.apiweb;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


@WebServlet(name = "userServlet", value = "/users")
public class UsersServlet extends HttpServlet {
    private String message;

    public void init() {
        message = "Hello World!";
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        try {

            String accion = request.getParameter("accion");

            if(accion.equals("bienvenida")){
                request.getRequestDispatcher("bienvenida.jsp").forward(request, response);

            } else {



        List<String> nombres = new ArrayList<>();

        nombres.add("Juan");
        nombres.add("Manuel");
        nombres.add("Pancho");
        nombres.add("Pepito");

        request.setAttribute("lista", nombres);
        request.setAttribute("mensajeBienvenida", "Hola alumno con el correo: ");

        request.getRequestDispatcher("usuarios.jsp").forward(request, response);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{

        try {



        String nombre = request.getParameter("nombre");
        String apellido = request.getParameter("apellido");
        String password = request.getParameter("password");


        // guardar el usuario en la base de datos

        // mostrar mensaje de bienvenida


        request.setAttribute("exito", true);
        request.setAttribute("nombreUsuario", nombre.toUpperCase());

        request.getSession().setAttribute("logueado", nombre);

        request.getRequestDispatcher("registro.jsp?exito=true").forward(request, response);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void destroy() {
    }
}