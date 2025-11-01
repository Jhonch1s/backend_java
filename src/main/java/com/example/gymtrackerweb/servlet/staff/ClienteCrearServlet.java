package com.example.gymtrackerweb.servlet.staff;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.RequestDispatcher;

import java.io.IOException;


@WebServlet(name = "ClienteModificarPageServlet", urlPatterns = {"/staff/clientes/crear"})
public class ClienteCrearServlet extends HttpServlet {

    private static final String JSP_PATH = "/pages/staff/cliente/crearCliente.jsp";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        var session = req.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            // redirigir al login
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        RequestDispatcher rd = req.getRequestDispatcher(JSP_PATH);
        rd.forward(req, resp);
    }

}

