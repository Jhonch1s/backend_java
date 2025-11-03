package com.example.gymtrackerweb.servlet.staff;

import com.example.gymtrackerweb.dao.RutinaDAO;
import com.example.gymtrackerweb.dto.RutinaAsignadaConIdDTO; // ¡DTO NUEVO!
import com.example.gymtrackerweb.model.Rutina;
import com.example.gymtrackerweb.utils.LocalDateAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/api/rutinas-disponibles")
public class ApiListarRutinasServlet extends HttpServlet {

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();
    private final RutinaDAO rutinaDAO = new RutinaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Map<String, Object> jsonResponse = new HashMap<>();

        try {
            String clienteId = request.getParameter("clienteId");
            if (clienteId == null || clienteId.trim().isEmpty()) {
                throw new IllegalArgumentException("Falta el ID del cliente.");
            }

            List<Rutina> rutinasDisponibles = rutinaDAO.listarRutinas();

            List<RutinaAsignadaConIdDTO> rutinasAsignadas = rutinaDAO.listarRutinasActivasPorCliente(clienteId);

            jsonResponse.put("success", true);
            jsonResponse.put("disponibles", rutinasDisponibles);
            jsonResponse.put("asignadas", rutinasAsignadas);

            response.getWriter().write(gson.toJson(jsonResponse));

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.put("error", e.getMessage());
            response.getWriter().write(gson.toJson(jsonResponse));
        }
    }
}