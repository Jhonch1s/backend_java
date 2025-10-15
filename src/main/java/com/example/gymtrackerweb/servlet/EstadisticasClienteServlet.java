package com.example.gymtrackerweb.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/cliente/estadisticas")
public class EstadisticasClienteServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        var session = req.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            // redirigí a login o devolvé 401 según tu flujo
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        resp.setContentType("text/html;charset=UTF-8");
        req.getRequestDispatcher("/pages/cliente/progreso/estadisticaProgresoCliente.jsp")
                .forward(req, resp);
    }
}