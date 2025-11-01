package com.example.gymtrackerweb.servlet.staff;

import com.example.gymtrackerweb.dao.RutinaDAO;
import com.example.gymtrackerweb.dto.GuardarRutinaPayloadDTO; // Lo creamos más abajo
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/admin/guardar-rutina-completa")
public class GuardarRutinaServlet extends HttpServlet {

    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            GuardarRutinaPayloadDTO payload = gson.fromJson(request.getReader(), GuardarRutinaPayloadDTO.class);
            RutinaDAO rutinaDAO = new RutinaDAO();
            rutinaDAO.guardarRutinaCompleta(payload);
            response.getWriter().write(gson.toJson(Map.of("success", true)));
        } catch (Exception e) {
            e.printStackTrace(); // Log del error
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(Map.of("error", "Error al guardar la rutina: " + e.getMessage())));
        }
    }
}