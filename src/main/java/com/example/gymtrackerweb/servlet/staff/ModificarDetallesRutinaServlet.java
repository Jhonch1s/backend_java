package com.example.gymtrackerweb.servlet.staff;

import com.example.gymtrackerweb.dao.RutinaDAO;
import com.example.gymtrackerweb.model.Rutina;
import com.example.gymtrackerweb.model.enums.Objetivo;
import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/admin/rutina/modificar-detalles")
public class ModificarDetallesRutinaServlet extends HttpServlet {

    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Map<String, Object> jsonResponse = new HashMap<>();

        String idStr = request.getParameter("id");
        String nombre = request.getParameter("nombre");
        String objetivoStr = request.getParameter("objetivo");
        String duracionStr = request.getParameter("duracionSemanas");

        RutinaDAO rutinaDAO = new RutinaDAO();

        try {
            int id = Integer.parseInt(idStr);
            int duracion = 0;
            if (duracionStr != null && !duracionStr.trim().isEmpty()) {
                duracion = Integer.parseInt(duracionStr);
            }

            // Validar existencia y cargar el objeto
            Rutina rutina = rutinaDAO.buscarPorId(id);
            if (rutina == null) {
                throw new Exception("Rutina no encontrada para modificar.");
            }

            // Validar nombre duplicado (excluyendo el propio ID)
            if (rutinaDAO.existePorNombre(nombre) && !nombre.equalsIgnoreCase(rutina.getNombre())) {
                throw new IllegalArgumentException("Ya existe otra rutina con ese nombre.");
            }

            // Modificar el objeto
            rutina.setNombre(nombre.trim());
            rutina.setObjetivo(Objetivo.valueOf(objetivoStr.toUpperCase()));
            rutina.setDuracionSemanas(duracion);

            // Guardar en DB
            rutinaDAO.modificarRutina(rutina); // RutinaDAO.modificarRutina is available

            jsonResponse.put("success", true);
            response.setStatus(HttpServletResponse.SC_OK);

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.put("error", "Datos numéricos inválidos.");
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.put("error", e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.put("error", "Error al modificar la rutina.");
        }

        response.getWriter().write(gson.toJson(jsonResponse));
    }
}