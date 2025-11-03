package com.example.gymtrackerweb.servlet.staff;

import com.example.gymtrackerweb.dao.MovimientoDAO;
import com.example.gymtrackerweb.dto.MovimientoView;
import com.example.gymtrackerweb.dto.MovimientoViewExtra;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "MovimientosServlet", value = "/staff/movimientos")
public class MovimientosServlet extends HttpServlet {
    private static final int entradas_pagina = 50;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        var session = request.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            // redirigir al login
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        try {
            MovimientoDAO dao = new MovimientoDAO();

            String staffLogin = trimOrNull(request.getParameter("staff"));     // ahora staff es usuarioLogin (String)
            String ciCliente  = trimOrNull(request.getParameter("cliente"));   // CI del cliente (8 dígitos)
            LocalDate desde      = parseDateOrNull(request.getParameter("desde"));
            LocalDate hasta      = parseDateOrNull(request.getParameter("hasta"));

            int page = Math.max(1, parseIntOrDefault(request.getParameter("page"), 1));
            int offset = (page - 1) * entradas_pagina;

            List<MovimientoViewExtra> lista = dao.listarMovimientosFiltrados(
                    staffLogin, ciCliente, desde, hasta, entradas_pagina, offset
            );
            int total = dao.contarMovimientosFiltrados(staffLogin, ciCliente, desde, hasta);
            int totalPages = Math.max(1, (int) Math.ceil(total / (double) entradas_pagina));

            request.setAttribute("listaMovimientos", lista);
            request.setAttribute("page", page);
            request.setAttribute("totalPages", totalPages);
            BigDecimal totalFiltrado = dao.sumarImportesFiltrados(staffLogin, ciCliente, desde, hasta);
            request.setAttribute("totalFiltrado", totalFiltrado);

            request.getRequestDispatcher("/pages/staff/verMovimientos.jsp").forward(request, response);

        } catch (Exception e) {
            response.sendError(500, "Error al listar movimientos: " + e.getMessage());
        }
    }

    private static int parseIntOrDefault(String s, int def) {
        try {
            return (s == null || s.isBlank()) ? def : Integer.parseInt(s);
        } catch (Exception e) {
            return def;
        }
    }

    private static LocalDate parseDateOrNull(String s) {
        try {
            return (s == null || s.isBlank()) ? null : LocalDate.parse(s);
        } catch (Exception e) {
            return null;
        }
    }

    private static String trimOrNull(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }
}