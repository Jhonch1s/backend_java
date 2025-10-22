package com.example.gymtrackerweb.servlet;

import com.example.gymtrackerweb.model.Cliente;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/cliente/detallerutina")
public class DetalleRutinaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Verificar sesión activa
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Obtener el cliente desde la sesión
        Cliente cliente = (Cliente) session.getAttribute("usuario");
        if (cliente == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Obtener la cédula del cliente
        String rutinaIdParam = request.getParameter("id");
        int rutinaId = -1;

        try {
            rutinaId = Integer.parseInt(rutinaIdParam);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de rutina inválido");
            return;
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("/pages/cliente/rutina/detalleRutinaCliente.jsp");
        dispatcher.forward(request, response);
    }
}