package com.example.gymtrackerweb.servlet.staff;

import com.example.gymtrackerweb.dao.ClienteListadoDAO;
import com.example.gymtrackerweb.db.databaseConection;
import com.example.gymtrackerweb.dto.ClienteListadoDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/staff/cliente/listar")
public class ListarClientesServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(ListarClientesServlet.class.getName());

    private ClienteListadoDAO dao;

    @Override
    public void init() throws ServletException {
        super.init();
        this.dao = new ClienteListadoDAO(); // ← simple y sin provider
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ClienteListadoDAO.ListarClientesParams p = new ClienteListadoDAO.ListarClientesParams();
        var session = req.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            // redirigir al login
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        if (this.dao == null) {
            this.dao = new ClienteListadoDAO();
        }
        try {
            p.q       = trimToNull(req.getParameter("q"));
            p.ciudad  = trimToNull(req.getParameter("ciudad"));
            p.pais    = trimToNull(req.getParameter("pais"));
            p.fiFrom  = parseSqlDate(req.getParameter("fi_from"));
            p.fiTo    = parseSqlDate(req.getParameter("fi_to"));
            p.estado  = Optional.ofNullable(req.getParameter("estado")).orElse("todos");

            p.sort    = Optional.ofNullable(req.getParameter("sort")).orElse("fecha_ingreso");
            p.dir     = Optional.ofNullable(req.getParameter("dir")).orElse("DESC");

            p.page    = clamp(parseIntOr(req.getParameter("page"), 1), 1, Integer.MAX_VALUE);
            p.size    = clamp(parseIntOr(req.getParameter("size"), 20), 1, 100);

            ClienteListadoDAO.PageResult<ClienteListadoDTO> page = dao.listar(p);

            for (ClienteListadoDTO dto : page.items) {
                String msg = dto.getMensajeReenganche();
                if (msg != null && !msg.isBlank()) {
                    dto.setMensajeReenganche(com.example.gymtrackerweb.utils.MembresiaHelper.urlEncode(msg));
                }
            }

            // Si no querés pegar a la BD cada vez, podés cachearlo en ServletContext.
            List<String> ciudades = distinctFromCliente("ciudad");
            List<String> paises   = distinctFromCliente("pais");

            // -------- Atributos para el JSP --------
            req.setAttribute("clientes", page.items);
            req.setAttribute("total", page.total);
            req.setAttribute("page", page.page);
            req.setAttribute("size", page.size);
            req.setAttribute("pages", page.pages);

            // Echo de filtros/orden
            req.setAttribute("q", p.q);
            req.setAttribute("estado", p.estado);
            req.setAttribute("ciudad", p.ciudad);
            req.setAttribute("pais", p.pais);
            req.setAttribute("fi_from", p.fiFrom);
            req.setAttribute("fi_to", p.fiTo);
            req.setAttribute("sort", p.sort);
            req.setAttribute("dir", p.dir);

            req.setAttribute("ciudades", ciudades);
            req.setAttribute("paises", paises);

            // Fecha actual (por si querés “hace X días” en UI)
            req.setAttribute("nowUy", LocalDate.now(java.time.ZoneId.of("America/Montevideo")));

            // -------- Forward --------
            req.getRequestDispatcher("/pages/staff/cliente/mostrarCliente.jsp").forward(req, resp);

        } catch (Exception e) {
            log.log(Level.SEVERE, "Error listando clientes", e);
            req.setAttribute("errorMsg", "Ocurrió un error al listar clientes: " + e.getMessage());
            req.setAttribute("requestId", UUID.randomUUID().toString());
            // Podés llevar a una página de error propia, o al mismo JSP con estado de error
            req.getRequestDispatcher("/pages/staff/cliente/mostrarCliente.jsp").forward(req, resp);
        }
    }

    // ================= Helpers =================

    private static String trimToNull(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    private static int parseIntOr(String raw, int def) {
        try {
            return raw == null ? def : Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }

    private static java.sql.Date parseSqlDate(String raw) {
        if (raw == null || raw.isBlank()) return null;
        try {
            return java.sql.Date.valueOf(raw.trim()); // yyyy-MM-dd
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Devuelve una lista ordenada de valores distintos (no nulos/ni vacíos) de la columna dada en la tabla cliente.
     * Ajustá si tus columnas se llaman distinto.
     */
    private List<String> distinctFromCliente(String column) {
        String col = switch (column) {
            case "ciudad" -> "ciudad";
            case "pais"   -> "pais";
            default       -> throw new IllegalArgumentException("Columna no soportada: " + column);
        };

        String sql = "SELECT DISTINCT " + col + " AS v FROM cliente WHERE " + col + " IS NOT NULL AND " + col + " <> '' ORDER BY v ASC";
        List<String> out = new ArrayList<>();

        Connection cn = databaseConection.getInstancia().getConnection();
        try (
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(rs.getString("v"));
            }
        } catch (SQLException e) {
            log.log(Level.WARNING, "No se pudieron cargar valores distintos de " + column, e);
        }
        return out;
    }
}
