package com.example.gymtrackerweb.servlet;

import com.example.gymtrackerweb.dao.ProgresoEjercicioDAO;
// Asegúrate que la importación del DTO sea la correcta que usa tu DAO
import com.example.gymtrackerweb.dto.EjercicioRutinaView; // <--- ¡CAMBIO IMPORTANTE!
import com.example.gymtrackerweb.model.Cliente;
import com.example.gymtrackerweb.model.enums.DiaSemana;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap; // <--- Import agregado
import java.util.List;
import java.util.Map; // <--- Import agregado

@WebServlet("/progreso-ejercicios")
public class ProgresoEjercicioServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // --- Verificación de sesión (sin cambios) ---
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        Cliente cliente = (Cliente) session.getAttribute("usuario");
        if (cliente == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        String clienteId = cliente.getCi();

        // --- Lógica para obtener ID de rutina o ejercicio (sin cambios) ---
        String rutinaIdParam = request.getParameter("id");
        String ejIdParam = request.getParameter("ejId");
        String nombreRutina = "Rutina";

        // Mapa para agrupar ejercicios por día (usando el DTO correcto)
        Map<DiaSemana, List<EjercicioRutinaView>> ejerciciosPorDia = new LinkedHashMap<>();

        // CASO A: Venimos de la lista de rutinas (tenemos "id")
        if (rutinaIdParam != null && !rutinaIdParam.isEmpty()) {
            int rutinaId;
            try {
                rutinaId = Integer.parseInt(rutinaIdParam);
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de rutina inválido");
                return;
            }

            ProgresoEjercicioDAO progresosDAO = new ProgresoEjercicioDAO();

            List<EjercicioRutinaView> todosLosEjercicios = progresosDAO.listarEjerciciosPorRutinaYDia(clienteId, rutinaId);

            if (todosLosEjercicios != null && !todosLosEjercicios.isEmpty()) {
                nombreRutina = todosLosEjercicios.get(0).getNombreRutina();

                // Agrupamos los ejercicios por día
                for (EjercicioRutinaView ej : todosLosEjercicios) { // <--- ¡CAMBIO IMPORTANTE!
                    DiaSemana dia = ej.getDiaSemana(); // <--- ¡TYPO CORREGIDO!
                    // Validar que el día no sea null (si fromString falló en el DAO)
                    if (dia != null) {
                        ejerciciosPorDia.computeIfAbsent(dia, k -> new ArrayList<>()).add(ej);
                    } else {
                        System.err.println("Advertencia: Ejercicio con ID " + ej.getIdEjercicio() + " tiene dia_semana nulo o inválido en la BD.");
                        // Opcionalmente, agruparlos en una categoría "Sin día asignado"
                        // ejerciciosPorDia.computeIfAbsent(null, k -> new ArrayList<>()).add(ej);
                    }
                }
            }

        }
        // CASO B: Venimos del Dashboard (sin cambios)
        else if (ejIdParam != null && !ejIdParam.isEmpty()) {
            System.out.println("Cargando JSP para detalle de ejercicio (ejId: " + ejIdParam + ")");
        }
        // CASO C: No vino ni "id" ni "ejId" (sin cambios)
        else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de rutina o ejercicio faltante");
            return;
        }

        // --- Pasar atributos al JSP (sin cambios) ---
        request.setAttribute("ejerciciosPorDia", ejerciciosPorDia);
        request.setAttribute("nombreRutina", nombreRutina);

        // Redirigir al JSP (sin cambios)
        RequestDispatcher dispatcher = request.getRequestDispatcher("/pages/cliente/progreso/progresosCliente.jsp");
        dispatcher.forward(request, response);
    }
}