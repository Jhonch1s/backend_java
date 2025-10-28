package com.example.gymtrackerweb.servlet.staff;

import com.example.gymtrackerweb.dao.ClienteDAO;
import com.example.gymtrackerweb.model.Cliente;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@WebServlet(name = "ClienteModificarServlet", urlPatterns = {"/api/clientes/modificar"})
public class ClienteModificarServlet extends HttpServlet {

    private ClienteDAO clienteDAO;

    @Override
    public void init() throws ServletException {
        this.clienteDAO = new ClienteDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Solo POST
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("application/json; charset=UTF-8");
        resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        try (PrintWriter out = resp.getWriter()) {
            out.write("{\"success\":false,\"message\":\"Método no permitido. Use POST.\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());

        // === 1) Leer parámetros ===
        String ciRaw         = trim(req.getParameter("ci"));
        String email         = trim(req.getParameter("email"));
        String nombre        = trim(req.getParameter("nombre"));
        String apellido      = trim(req.getParameter("apellido"));
        String ciudad        = trim(req.getParameter("ciudad"));
        String direccion     = trim(req.getParameter("direccion"));
        String tel           = trim(req.getParameter("telefono"));
        String pais          = trim(req.getParameter("pais"));
        String fechaIngresoS = trim(req.getParameter("fecha_ingreso")); // esperado yyyy-MM-dd

        String ci = normalizeCi(ciRaw);
        if (ci == null || ci.isBlank()) {
            sendError(req, resp, HttpServletResponse.SC_BAD_REQUEST, "La CI es requerida.");
            return;
        }

        // === 2) Parsear fecha ===
        Date fechaIngreso = null;
        if (fechaIngresoS != null && !fechaIngresoS.isBlank()) {
            try {
                LocalDate ld = LocalDate.parse(fechaIngresoS); // yyyy-MM-dd
                fechaIngreso = Date.valueOf(ld);
            } catch (DateTimeParseException ex) {
                sendError(req, resp, HttpServletResponse.SC_BAD_REQUEST, "Formato de fecha inválido (use yyyy-MM-dd).");
                return;
            }
        }

        // === 3) Construir modelo ===
        Cliente c = new Cliente();
        c.setCi(ci);
        c.setEmail(nvl(email));
        c.setNombre(nvl(nombre));
        c.setApellido(nvl(apellido));
        c.setCiudad(nvl(ciudad));
        c.setDireccion(nvl(direccion));
        c.setTel(nvl(tel));
        c.setPais(nvl(pais));
        c.setFechaIngreso(fechaIngreso); // puede ser null

        // === 4) Persistir ===
        try {
            clienteDAO.modificarCliente(c);
        } catch (Exception ex) {
            // Loggear ex si tenés logger
            sendError(req, resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No se pudo actualizar el cliente.");
            return;
        }

        // === 5) Responder según el tipo de request ===
        if (wantsJson(req)) {
            resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
            resp.setContentType("application/json; charset=UTF-8");
            try (PrintWriter out = resp.getWriter()) {
                out.write("{\"success\":true,\"message\":\"Cliente actualizado correctamente.\"}");
            }
        } else {
            // Submit tradicional: redirigir a la vista de modificación (ajusta la ruta si usás otra)
            String ctx = req.getContextPath();
            HttpSession session = req.getSession(true);
            session.setAttribute("flash_ok", "Cliente actualizado correctamente.");
            // Si querés volver con la CI seleccionada:
            resp.sendRedirect(ctx + "/pages/staff/cliente/modificarCliente.jsp?ci=" + ci);
        }
    }

    // ===== Helpers =====

    private boolean wantsJson(HttpServletRequest req) {
        String accept = req.getHeader("Accept");
        String xrw = req.getHeader("X-Requested-With"); // fetch/ajax clients can set this
        return (accept != null && accept.contains("application/json"))
                || (xrw != null && !xrw.isBlank());
    }

    private String trim(String s) {
        return s == null ? null : s.trim();
    }

    /** Normaliza CI quitando puntos, guiones y espacios. */
    private String normalizeCi(String ci) {
        if (ci == null) return null;
        return ci.replaceAll("[.\\-\\s]", "").trim();
    }

    private String nvl(String s) {
        return (s == null) ? "" : s;
    }

    private void sendError(HttpServletRequest req, HttpServletResponse resp, int status, String message) throws IOException {
        if (wantsJson(req)) {
            resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
            resp.setContentType("application/json; charset=UTF-8");
            resp.setStatus(status);
            try (PrintWriter out = resp.getWriter()) {
                out.write("{\"success\":false,\"message\":\"" + esc(message) + "\"}");
            }
        } else {
            // Flash + redirect simple
            String ctx = req.getContextPath();
            HttpSession session = req.getSession(true);
            session.setAttribute("flash_error", message);
            resp.sendRedirect(ctx + "/staff/clientes/modificar");
        }
    }

    private String esc(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (ch < 0x20) {
                        sb.append(String.format("\\u%04x", (int) ch));
                    } else {
                        sb.append(ch);
                    }
            }
        }
        return sb.toString();
    }
}
