package com.example.gymtrackerweb.servlet.staff.rutina;

import com.example.gymtrackerweb.dao.RutinaDAO;
import com.example.gymtrackerweb.model.Rutina;
import com.example.gymtrackerweb.model.enums.Objetivo;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// Un solo servlet para todas las acciones CRUD de la plantilla
@WebServlet("/admin/rutina-crud")
public class RutinaCrudServlet extends HttpServlet {

    private final Gson gson = new Gson();
    private final RutinaDAO rutinaDAO = new RutinaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Map<String, Object> jsonResponse = new HashMap<>();

        try {
            int idRutina = Integer.parseInt(request.getParameter("id"));
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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Map<String, Object> jsonResponse = new HashMap<>();

        // El parámetro "action" decide qué hacer
        String action = request.getParameter("action");

        try {
            switch (action) {
                case "crear":
                    crearRutina(request, jsonResponse);
                    break;
                case "modificar":
                    modificarRutina(request, jsonResponse);
                    break;
                case "eliminar":
                    eliminarRutina(request, jsonResponse);
                    break;
                default:
                    throw new IllegalArgumentException("Acción no válida.");
            }
            response.setStatus(HttpServletResponse.SC_OK);

        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.put("error", e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.put("error", "Error interno: " + e.getMessage());
        }

        response.getWriter().write(gson.toJson(jsonResponse));
    }

    // Lógica copiada de CrearRutinaServlet
    private void crearRutina(HttpServletRequest request, Map<String, Object> jsonResponse) throws Exception {
        String nombre = request.getParameter("nombre");
        if (rutinaDAO.existePorNombre(nombre)) {
            throw new IllegalArgumentException("Ya existe una rutina con ese nombre.");
        }

        Rutina nuevaRutina = new Rutina();
        nuevaRutina.setNombre(nombre.trim());
        nuevaRutina.setObjetivo(Objetivo.valueOf(request.getParameter("objetivo").toUpperCase()));

        int duracion = 0;
        String duracionStr = request.getParameter("duracionSemanas");
        if (duracionStr != null && !duracionStr.trim().isEmpty()) {
            duracion = Integer.parseInt(duracionStr);
        }
        nuevaRutina.setDuracionSemanas(duracion);

        int nuevoId = rutinaDAO.agregarRutina(nuevaRutina);
        if (nuevoId <= 0) {
            throw new Exception("Error desconocido al insertar la rutina.");
        }

        jsonResponse.put("success", true);
        jsonResponse.put("nuevaRutinaId", nuevoId);
    }

    // Lógica copiada de ModificarDetallesRutinaServlet
    private void modificarRutina(HttpServletRequest request, Map<String, Object> jsonResponse) throws Exception {
        int id = Integer.parseInt(request.getParameter("id"));
        String nombre = request.getParameter("nombre");

        Rutina rutina = rutinaDAO.buscarPorId(id);
        if (rutina == null) {
            throw new Exception("Rutina no encontrada para modificar.");
        }

        if (rutinaDAO.existePorNombre(nombre) && !nombre.equalsIgnoreCase(rutina.getNombre())) {
            throw new IllegalArgumentException("Ya existe otra rutina con ese nombre.");
        }

        rutina.setNombre(nombre.trim());
        rutina.setObjetivo(Objetivo.valueOf(request.getParameter("objetivo").toUpperCase()));

        int duracion = 0;
        String duracionStr = request.getParameter("duracionSemanas");
        if (duracionStr != null && !duracionStr.trim().isEmpty()) {
            duracion = Integer.parseInt(duracionStr);
        }
        rutina.setDuracionSemanas(duracion);

        rutinaDAO.modificarRutina(rutina);
        jsonResponse.put("success", true);
    }

    // Lógica copiada de EliminarRutinaServlet
    private void eliminarRutina(HttpServletRequest request, Map<String, Object> jsonResponse) throws Exception {
        int id = Integer.parseInt(request.getParameter("id"));
        boolean eliminado = rutinaDAO.eliminarRutina(id);

        if (!eliminado) {
            throw new Exception("No se pudo eliminar la rutina.");
        }
        jsonResponse.put("success", true);
    }
}