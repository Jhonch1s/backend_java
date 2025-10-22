package com.example.gymtrackerweb.servlet;

import com.example.gymtrackerweb.db.databaseConection;
import com.example.gymtrackerweb.model.Cliente;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/cliente/stats/exercise/series")
public class EstadisticasSeriesEjercicioServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");

        Cliente usuario = (Cliente) req.getSession().getAttribute("usuario");
        if (usuario == null) {
            resp.setStatus(401);
            resp.getWriter().write("{\"ok\":false,\"error\":\"no-auth\"}");
            return;
        }

        String ej = req.getParameter("ej");
        String r  = req.getParameter("r");
        if (ej == null || !ej.matches("\\d+")) {
            resp.setStatus(400);
            resp.getWriter().write("{\"ok\":false,\"error\":\"bad-req-ej\"}");
            return;
        }
        if (r == null) r = "4w";

        // --- rango temporal
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate from = null, to = today;
        switch (r) {
            case "4w"  -> from = today.minusWeeks(4);
            case "3m"  -> from = today.minusMonths(3);
            case "6m"  -> from = today.minusMonths(6);
            case "12m" -> from = today.minusMonths(12);
            case "all" -> { from = null; to = null; }
            case "custom" -> {
                try {
                    String f = req.getParameter("from");
                    String t = req.getParameter("to");
                    from = (f == null || f.isBlank()) ? null : java.time.LocalDate.parse(f);
                    to   = (t == null || t.isBlank()) ? today : java.time.LocalDate.parse(t);
                } catch (Exception ex) {
                    resp.setStatus(400);
                    resp.getWriter().write("{\"ok\":false,\"error\":\"bad-range\"}");
                    return;
                }
            }
            default -> from = today.minusWeeks(4);
        }

        String ci = usuario.getCi();
        int idEj = Integer.parseInt(ej);

        Connection conn = databaseConection.getInstancia().getConnection();
        try {
            String cond = (from != null ? "AND fecha BETWEEN ? AND ? " : "");

            // e1RM semanal
            String sqlE1 = """
    SELECT 
        YEARWEEK(fecha,3) AS yw,
        DATE_FORMAT(MIN(fecha), '%%x-W%%v') AS lbl,
        AVG(peso_usado * (1 + repeticiones/30.0)) AS e1rm
    FROM progreso_ejercicio
    WHERE id_cliente = ? AND id_ejercicio = ?
      %s
    GROUP BY YEARWEEK(fecha,3)
    ORDER BY yw ASC
""".formatted(cond);

            String sqlV = """
    SELECT 
        YEARWEEK(fecha,3) AS yw,
        DATE_FORMAT(MIN(fecha), '%%x-W%%v') AS lbl,
        SUM(peso_usado * repeticiones) AS vol
    FROM progreso_ejercicio
    WHERE id_cliente = ? AND id_ejercicio = ?
      %s
    GROUP BY YEARWEEK(fecha,3)
    ORDER BY yw ASC
""".formatted(cond);


            // scatter carga–reps
            String sqlS = """
                SELECT repeticiones AS reps, peso_usado AS kg
                FROM progreso_ejercicio
                WHERE id_cliente = ? AND id_ejercicio = ?
                  %s
                ORDER BY fecha ASC, id_progreso ASC
                LIMIT 400
            """.formatted(cond);

            StringBuilder out = new StringBuilder(1024);
            out.append("{\"ok\":true,\"series\":[");

            // e1RM
            try (PreparedStatement ps = conn.prepareStatement(sqlE1)) {
                int i = 1;
                ps.setString(i++, ci);
                ps.setInt(i++, idEj);
                if (from != null) {
                    ps.setDate(i++, java.sql.Date.valueOf(from));
                    ps.setDate(i++, java.sql.Date.valueOf(to));
                }
                try (ResultSet rs = ps.executeQuery()) {
                    boolean first = true;
                    while (rs.next()) {
                        if (!first) out.append(',');
                        first = false;
                        BigDecimal bd = rs.getBigDecimal("e1rm");
                        double e1rm = (bd == null) ? 0.0 : bd.doubleValue();
                        out.append("{\"label\":\"").append(rs.getString("lbl"))
                                .append("\",\"e1rm\":").append(String.format(java.util.Locale.US,"%.2f", e1rm)).append("}");
                    }
                }
            }

            // Volumen
            out.append("],\"volumeWeekly\":[");
            try (PreparedStatement ps = conn.prepareStatement(sqlV)) {
                int i = 1;
                ps.setString(i++, ci);
                ps.setInt(i++, idEj);
                if (from != null) {
                    ps.setDate(i++, java.sql.Date.valueOf(from));
                    ps.setDate(i++, java.sql.Date.valueOf(to));
                }
                try (ResultSet rs = ps.executeQuery()) {
                    boolean first = true;
                    while (rs.next()) {
                        if (!first) out.append(',');
                        first = false;
                        BigDecimal bd = rs.getBigDecimal("vol");
                        double vol = (bd == null) ? 0.0 : bd.doubleValue();
                        out.append("{\"weekLabel\":\"").append(rs.getString("lbl"))
                                .append("\",\"volume\":").append(String.format(java.util.Locale.US,"%.2f", vol)).append("}");
                    }
                }
            }

            // Scatter
            out.append("],\"scatter\":[");
            try (PreparedStatement ps = conn.prepareStatement(sqlS)) {
                int i = 1;
                ps.setString(i++, ci);
                ps.setInt(i++, idEj);
                if (from != null) {
                    ps.setDate(i++, java.sql.Date.valueOf(from));
                    ps.setDate(i++, java.sql.Date.valueOf(to));
                }
                try (ResultSet rs = ps.executeQuery()) {
                    boolean first = true;
                    while (rs.next()) {
                        if (!first) out.append(',');
                        first = false;
                        out.append("{\"reps\":").append(rs.getInt("reps"))
                                .append(",\"kg\":").append(String.format(java.util.Locale.US,"%.2f", rs.getDouble("kg"))).append("}");
                    }
                }
            }

            out.append("]}");
            resp.getWriter().write(out.toString());

        } catch (Exception ex) {
            ex.printStackTrace();
            resp.setStatus(500);
            resp.getWriter().write("{\"ok\":false,\"error\":\"server\"}");
        }
    }
}
