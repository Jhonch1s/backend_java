package com.example.gymtrackerweb.servlet.staff;

import com.example.gymtrackerweb.dao.DetalleRutinaDAO;
import com.example.gymtrackerweb.model.DetalleRutina;
import com.example.gymtrackerweb.model.enums.DiaSemana;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "ModificarRutinaServlet", urlPatterns = "/admin/modificar-rutina")
public class ModificarRutinaServlet extends HttpServlet {

    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // (Aquí va tu verificación de sesión de Staff)

        String action = request.getParameter("action");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Map<String, Object> jsonResponse = new HashMap<>();

        try {
            DetalleRutinaDAO dao = new DetalleRutinaDAO();

            switch (action) {
                case "agregar":
                    agregarEjercicioCompleto(request, response, dao, jsonResponse);
                    break;

                case "quitar":
                    int idDetalleRutina = Integer.parseInt(request.getParameter("idDetalleRutina"));
                    dao.eliminarDetalle(idDetalleRutina); // Asume ON DELETE CASCADE
                    jsonResponse.put("success", true);
                    break;

                case "modificar":
                    modificarEjercicioCompleto(request, response, dao, jsonResponse);
                    break;

                default:
                    throw new IllegalArgumentException("Acción no válida: " + action);
            }

            response.getWriter().write(gson.toJson(jsonResponse));

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.put("success", false);
            jsonResponse.put("error", e.getMessage());
            response.getWriter().write(gson.toJson(jsonResponse));
            e.printStackTrace();
        }
    }

    private void agregarEjercicioCompleto(HttpServletRequest request, HttpServletResponse response, DetalleRutinaDAO dao, Map<String, Object> jsonResponse) throws Exception {
        int idRutina = Integer.parseInt(request.getParameter("idRutina"));
        int idEjercicio = Integer.parseInt(request.getParameter("idEjercicio"));
        int series = Integer.parseInt(request.getParameter("series"));
        int repeticiones = Integer.parseInt(request.getParameter("repeticiones"));
        String[] dias = request.getParameterValues("dias[]"); // Recibimos el array de días

        // 1. Crear la fila en detalle_rutina
        DetalleRutina nuevoDetalle = new DetalleRutina();
        nuevoDetalle.setId_rutina(idRutina); // Ojo con el nombre del setter
        nuevoDetalle.setId_ejercicio(idEjercicio); // Ojo con el nombre del setter
        nuevoDetalle.setSeries(series);
        nuevoDetalle.setRepeticiones(repeticiones);

        int nuevoIdDetalle = dao.agregarDetalle(nuevoDetalle); // Asume que devuelve el ID

        if (nuevoIdDetalle <= 0) {
            throw new Exception("No se pudo crear el detalle de la rutina.");
        }

        // 2. Asignar los días en detalle_rutina_dia
        if (dias != null && dias.length > 0) {
            for (String diaStr : dias) {
                DiaSemana diaEnum = DiaSemana.fromString(diaStr);
                if (diaEnum != null) {
                    dao.agregarDiaADetalle(nuevoIdDetalle, diaEnum);
                }
            }
        }

        // Devolvemos el objeto completo para que el JS lo "pinte"
        jsonResponse.put("success", true);
        jsonResponse.put("idDetalleRutina", nuevoIdDetalle);
        jsonResponse.put("idEjercicio", idEjercicio);
        jsonResponse.put("series", series);
        jsonResponse.put("repeticiones", repeticiones);
        jsonResponse.put("dias", dias != null ? dias : new String[0]);
    }

    private void modificarEjercicioCompleto(HttpServletRequest request, HttpServletResponse response, DetalleRutinaDAO dao, Map<String, Object> jsonResponse) throws Exception {
        int idDetalleRutina = Integer.parseInt(request.getParameter("idDetalleRutina"));
        int series = Integer.parseInt(request.getParameter("series"));
        int repeticiones = Integer.parseInt(request.getParameter("repeticiones"));
        String[] dias = request.getParameterValues("dias[]");

        DetalleRutina detalleModif = dao.listarDetallesPorId(idDetalleRutina);
        if(detalleModif == null) throw new Exception("Detalle no encontrado");

        detalleModif.setSeries(series);
        detalleModif.setRepeticiones(repeticiones);
        dao.modificarDetalle(detalleModif);

        dao.eliminarDiasDeDetalle(idDetalleRutina);

        if (dias != null && dias.length > 0) {
            for (String diaStr : dias) {
                DiaSemana diaEnum = DiaSemana.fromString(diaStr);
                if (diaEnum != null) {
                    dao.agregarDiaADetalle(idDetalleRutina, diaEnum);
                }
            }
        }

        jsonResponse.put("success", true);
        jsonResponse.put("dias", dias != null ? dias : new String[0]);
    }
}