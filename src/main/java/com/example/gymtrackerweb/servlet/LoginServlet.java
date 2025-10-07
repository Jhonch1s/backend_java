package com.example.gymtrackerweb.servlet;
import java.io.*;

import com.example.gymtrackerweb.dao.StaffDAO;
import com.example.gymtrackerweb.model.Staff;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "LoginServlet", value = "/login") //Esto me dice como accedo al login
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/pages/login.jsp").forward(request, response);
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        //Obtengo el valor que viene del id de los forms
        String usuario = request.getParameter("usuario");
        String contrasena = request.getParameter("contrasenia");

        //creamos el objeto Staff con esos datos
        Staff staff = new Staff();
        staff.setUsuarioLogin(usuario);
        staff.setContrasenia(contrasena);
        try{
            StaffDAO staffDAO = new StaffDAO();
            Staff s = staffDAO.iniciarSesion(staff); //pasamos el objeto a dao ahora
            if (s != null) {
                // Si el usuario es valido se guarda la sesion
                HttpSession sesion = request.getSession();
                sesion.setAttribute("usuario", s);
                response.sendRedirect(request.getContextPath() + "/pages/dashboard.jsp"); //si las credenciales son correctas me manda al dashboard
            } else {
                // Usuario o contraseña incorrectos
                request.setAttribute("error", "Usuario o contraseña incorrectos.");
                request.getRequestDispatcher("/pages/login.jsp").forward(request, response); //si no son incorrectas me mantiene en el login
            }
        }catch (Exception e){
            e.printStackTrace();
            request.setAttribute("error", "Error interno al iniciar sesion.");
            request.getRequestDispatcher("/pages/login.jsp").forward(request, response);
        }
    }
}