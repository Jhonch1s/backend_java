package com.example.gymtrackerweb.servlet.staff;

import com.example.gymtrackerweb.dao.RutinaDAO;
import com.example.gymtrackerweb.model.Rutina;
import com.example.gymtrackerweb.model.enums.Objetivo;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/admin/crear-rutina")
public class CrearRutinaServlet extends HttpServlet {

    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Map<String, Object> jsonResponse = new HashMap<>();

        HttpSession session = request.getSession(false);
//        if (session == null || session.getAttribute("usuarioStaff") == null) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            jsonResponse.put("success", false);
//            jsonResponse.put("error", "Sesión no válida.");
//            response.getWriter().write(gson.toJson(jsonResponse));
//            return;
//        }

        String nombre = request.getParameter("nombre");
        String objetivoStr = request.getParameter("objetivo");
        String duracionStr = request.getParameter("duracionSemanas");

        RutinaDAO rutinaDAO = new RutinaDAO();

        try {
            if (rutinaDAO.existePorNombre(nombre)) {
                throw new IllegalArgumentException("Ya existe una rutina con ese nombre.");
            }

            Rutina nuevaRutina = new Rutina();
            nuevaRutina.setNombre(nombre.trim());
            nuevaRutina.setObjetivo(Objetivo.valueOf(objetivoStr.toUpperCase()));

            int duracion = 0;
            if (duracionStr != null && !duracionStr.trim().isEmpty()) {
                duracion = Integer.parseInt(duracionStr);
            }
            nuevaRutina.setDuracionSemanas(duracion);

            int nuevoId = rutinaDAO.agregarRutina(nuevaRutina);

            if (nuevoId > 0) {
                jsonResponse.put("success", true);
                jsonResponse.put("nuevaRutinaId", nuevoId);
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                throw new Exception("Error desconocido al insertar la rutina en la base de datos.");
            }

        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.put("success", false);
            jsonResponse.put("error", e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.put("success", false);
            jsonResponse.put("error", "Error interno al guardar la rutina.");
            System.err.println("Error en CrearRutinaServlet: " + e.getMessage());
        }

        response.getWriter().write(gson.toJson(jsonResponse));
    }
}