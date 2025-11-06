package com.example.gymtrackerweb.servlet.staff.rutina;

import com.example.gymtrackerweb.dao.RutinaDAO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder; // Importar GsonBuilder
import com.example.gymtrackerweb.utils.LocalDateAdapter; // Importar tu adapter
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate; // Importar LocalDate
import java.util.Map;

@WebServlet("/admin/asignar-cliente-rutina")
public class AsignarClienteServlet extends HttpServlet {

    private final RutinaDAO rutinaDAO = new RutinaDAO();

    // Asegúrate de registrar el adapter de LocalDate
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        try {
            if ("agregar".equals(action)) {

                String clienteId = request.getParameter("clienteId");
                int rutinaId = Integer.parseInt(request.getParameter("rutinaId"));
                LocalDate fecha = LocalDate.parse(request.getParameter("fechaAsignacion")); // Leer la fecha

                if (clienteId == null || clienteId.trim().isEmpty()) {
                    throw new IllegalArgumentException("El ID del cliente no puede estar vacío.");
                }

                rutinaDAO.asignarNuevaRutinaCliente(clienteId, rutinaId, fecha);
            } else {
                throw new IllegalArgumentException("Acción no válida.");
            }

            response.getWriter().write(gson.toJson(Map.of("success", true)));

        } catch (Exception e) {
            // --- ¡MANEJO DE ERROR CORREGIDO! ---
            e.printStackTrace(); // Importante para debug
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(Map.of("success", false, "error", e.getMessage())));
        }
    }
}