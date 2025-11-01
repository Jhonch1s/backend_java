package com.example.gymtrackerweb.servlet.staff;

import com.example.gymtrackerweb.dao.RutinaDAO;
import com.example.gymtrackerweb.model.Rutina; // Asumo que tienes model.Rutina
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/api/rutinas-disponibles")
public class ApiListarRutinasServlet extends HttpServlet {

    private final Gson gson = new Gson();
    private final RutinaDAO rutinaDAO = new RutinaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            List<Rutina> rutinas = rutinaDAO.listarRutinas();

            List<RutinaSimpleDTO> rutinasSimples = rutinas.stream()
                    .map(r -> new RutinaSimpleDTO(r.getId(), r.getNombre()))
                    .collect(Collectors.toList());

            response.getWriter().write(gson.toJson(rutinasSimples));

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(java.util.Map.of("error", "Error al cargar rutinas")));
        }
    }

    private static class RutinaSimpleDTO {
        int id;
        String nombre;
        public RutinaSimpleDTO(int id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }
    }
}
