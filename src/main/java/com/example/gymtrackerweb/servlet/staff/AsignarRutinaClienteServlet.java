package com.example.gymtrackerweb.servlet.staff;

import com.example.gymtrackerweb.dao.RutinaDAO;
import com.example.gymtrackerweb.db.databaseConection;
import com.example.gymtrackerweb.utils.LocalDateAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/admin/guardar-asignacion-rutina")
public class AsignarRutinaClienteServlet extends HttpServlet {

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();
    private final RutinaDAO rutinaDAO = new RutinaDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Connection conn = null;

        String clienteId = request.getParameter("clienteId");
        String asignacionesARemoverParam = request.getParameter("asignacionesIdsARemover");
        String rutinasAAgregarParam = request.getParameter("rutinasIdsAAgregar");
        String fechaAsignacionParam = request.getParameter("fechaAsignacion");

        boolean hayNuevas = rutinasAAgregarParam != null && !rutinasAAgregarParam.trim().isEmpty();
        boolean hayParaQuitar = asignacionesARemoverParam != null && !asignacionesARemoverParam.trim().isEmpty();

        try {
            if (clienteId == null || clienteId.trim().isEmpty()) {
                throw new IllegalArgumentException("Falta el ID del cliente.");
            }

            LocalDate fechaAsignacion = null;
            if (hayNuevas) {
                if (fechaAsignacionParam == null || fechaAsignacionParam.trim().isEmpty()) {
                    throw new IllegalArgumentException("Debes seleccionar una fecha para las nuevas rutinas.");
                }
                fechaAsignacion = LocalDate.parse(fechaAsignacionParam);
            }

            conn = databaseConection.getInstancia().getConnection();
            conn.setAutoCommit(false); // 1. INICIAMOS TRANSACCIÓN

            int remocionesCount = 0;
            int adicionesCount = 0;

            // --- 2. PROCESAR REMOCIONES ---
            if (hayParaQuitar) {
                String[] idsArray = asignacionesARemoverParam.split(",");
                for (String idStr : idsArray) {
                    try {
                        int asignacionId = Integer.parseInt(idStr.trim());
                        rutinaDAO.eliminarAsignacion(conn, asignacionId);
                        remocionesCount++;
                    } catch (NumberFormatException e) {
                        System.err.println("ID de asignación inválido: " + idStr);
                    }
                }
            }

            if (hayNuevas) {
                String[] idsArray = rutinasAAgregarParam.split(",");
                for (String idStr : idsArray) {
                    try {
                        int rutinaId = Integer.parseInt(idStr.trim());
                        rutinaDAO.asignarNuevaRutinaCliente(conn, clienteId, rutinaId, fechaAsignacion);
                        adicionesCount++;
                    } catch (NumberFormatException e) {
                        System.err.println("ID de rutina inválido: " + idStr);
                    }
                }
            }

            conn.commit();

            if (remocionesCount == 0 && adicionesCount == 0) {
                throw new Exception("No se procesó ningún cambio válido.");
            }

            response.getWriter().write(gson.toJson(Map.of("success", true)));

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error en rollback: " + ex.getMessage());
                }
            }
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(Map.of("success", false, "error", e.getMessage())));
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}