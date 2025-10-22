package com.example.gymtrackerweb.servlet;

import com.example.gymtrackerweb.dao.RutinaClienteDAO;
import com.example.gymtrackerweb.dto.RutinaClienteView;
import com.example.gymtrackerweb.model.Cliente;
import com.example.gymtrackerweb.model.enums.Objetivo; // Asegúrate de importar tu enum
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/rutinas-cliente")
public class RutinasClienteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Verificar sesión activa
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // Obtener el cliente desde la sesión
        Cliente cliente = (Cliente) session.getAttribute("usuario");
        if (cliente == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // Obtener el ID del cliente
        String clienteId = cliente.getCi();

        // Consultar las rutinas del cliente
        RutinaClienteDAO rutinaDAO = new RutinaClienteDAO();
        List<RutinaClienteView> rutinas = rutinaDAO.RutinasDeCliente(clienteId);

        // Pasar la lista al JSP
        request.setAttribute("rutinas", rutinas);

        // Redirigir al JSP que mostrará las rutinas
        RequestDispatcher dispatcher = request.getRequestDispatcher("/pages/cliente/rutinas/rutinasCliente.jsp");
        dispatcher.forward(request, response);
    }
}

