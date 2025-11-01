package com.example.gymtrackerweb.servlet.staff;

import com.example.gymtrackerweb.dao.RutinaDAO;
import com.example.gymtrackerweb.model.Rutina;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/admin/rutina/detalles")
public class CargarDetallesRutinaServlet extends HttpServlet {

    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Map<String, Object> jsonResponse = new HashMap<>();

        String idRutinaStr = request.getParameter("id");
        int idRutina;
        try {
            idRutina = Integer.parseInt(idRutinaStr);
        } catch (NumberFormatException | NullPointerException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.put("error", "ID de rutina inválido.");
            response.getWriter().write(gson.toJson(jsonResponse));
            return;
        }

        RutinaDAO rutinaDAO = new RutinaDAO();

        try {
            Rutina rutina = rutinaDAO.buscarPorId(idRutina);

            if (rutina != null) {
                jsonResponse.put("success", true);
                jsonResponse.put("rutina", rutina);
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                jsonResponse.put("error", "Rutina no encontrada.");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.put("error", "Error al cargar detalles.");
        }

        response.getWriter().write(gson.toJson(jsonResponse));
    }
}