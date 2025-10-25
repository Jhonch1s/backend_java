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

        // --- Verificación de sesión (sin cambios) ---
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        Cliente cliente = (Cliente) session.getAttribute("usuario");
        if (cliente == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        String clienteId = cliente.getCi();

        // --- INICIO DE LA LÓGICA CORREGIDA ---

        // 1. Obtenemos AMBOS posibles parámetros
        String rutinaIdParam = request.getParameter("id");
        String ejIdParam = request.getParameter("ejId"); // Parámetro del dashboard

        // 2. Comprobamos qué caso es

        // CASO A: Venimos de la lista de rutinas (tenemos "id")
        if (rutinaIdParam != null && !rutinaIdParam.isEmpty()) {
            int rutinaId;
            try {
                rutinaId = Integer.parseInt(rutinaIdParam);
            } catch (NumberFormatException e) {
                // El "id" vino, pero es inválido (ej: "abc")
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de rutina inválido");
                return;
            }

            // --- Lógica original (sin cambios) ---
            ProgresoEjercicioDAO progresosDAO = new ProgresoEjercicioDAO();
            List<EjercicioConProgresoView> progreso = progresosDAO.listarEjerciciosConProgreso(clienteId, rutinaId);
            String nombreRutina = null;
            if (progreso != null && !progreso.isEmpty()) {
                nombreRutina = progreso.get(0).getNombreRutina();
            }
            request.setAttribute("ejercicios", progreso);
            request.setAttribute("nombreRutina", nombreRutina != null ? nombreRutina : "Rutina sin nombre");

            System.out.println("Cliente ID: " + clienteId);
            System.out.println("Ejercicios encontrados: " + progreso.size());
            System.out.println("Nombre rutina: " + nombreRutina);

        }
        // CASO B: Venimos del Dashboard (tenemos "ejId")
        else if (ejIdParam != null && !ejIdParam.isEmpty()) {
            // No hacemos nada. No necesitamos cargar "ejercicios" ni "nombreRutina".
            // Solo queremos mostrar el JSP. El JavaScript leerá "ejId" de la URL.
            System.out.println("Cargando JSP para detalle de ejercicio (ejId: " + ejIdParam + ")");
        }
        // CASO C: No vino ni "id" ni "ejId"
        else {
            // Esta es una solicitud inválida
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de rutina o ejercicio faltante");
            return;
        }

        // 3. Redirigir al JSP (¡Ahora se ejecuta para ambos casos A y B!)
        RequestDispatcher dispatcher = request.getRequestDispatcher("/pages/cliente/progreso/progresosCliente.jsp");
        dispatcher.forward(request, response);

        // --- FIN DE LA LÓGICA CORREGIDA ---
    }
}