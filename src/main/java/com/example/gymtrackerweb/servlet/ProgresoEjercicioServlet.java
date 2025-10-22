package com.example.gymtrackerweb.servlet;

import com.example.gymtrackerweb.dao.ProgresoEjercicioDAO;
import com.example.gymtrackerweb.dto.EjercicioConProgresoView;
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

@WebServlet("/progreso-ejercicios")
public class ProgresoEjercicioServlet extends HttpServlet {

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

        // Obtener la cédula del cliente
        String clienteId = cliente.getCi();
        String rutinaIdParam = request.getParameter("id");
        int rutinaId = -1;

        try {
            rutinaId = Integer.parseInt(rutinaIdParam);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de rutina inválido");
            return;
        }

        // Consultar los ejercicios con progreso
        ProgresoEjercicioDAO progresosDAO = new ProgresoEjercicioDAO();
        List<EjercicioConProgresoView> progreso = progresosDAO.listarEjerciciosConProgreso(clienteId, rutinaId);

        // Extraer el nombre de la rutina si hay ejercicios
        String nombreRutina = null;
        if (progreso != null && !progreso.isEmpty()) {
            nombreRutina = progreso.get(0).getNombreRutina();
        }

        // Pasar atributos al JSP
        request.setAttribute("ejercicios", progreso);
        request.setAttribute("nombreRutina", nombreRutina != null ? nombreRutina : "Rutina sin nombre");

        // Redirigir al JSP
        RequestDispatcher dispatcher = request.getRequestDispatcher("/pages/cliente/progreso/progresosCliente.jsp");
        dispatcher.forward(request, response);

        // Logging opcional
        System.out.println("Cliente ID: " + clienteId);
        System.out.println("Ejercicios encontrados: " + progreso.size());
        System.out.println("Nombre rutina: " + nombreRutina);
    }
}
